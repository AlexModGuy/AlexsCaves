package com.github.alexmodguy.alexscaves.server.level.map;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.message.UpdateCaveBiomeMapTagMessage;
import com.google.common.base.Stopwatch;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CaveBiomeMapWorldWorker implements WorldWorkerManager.IWorker {
    private final Stopwatch stopwatch;
    private ItemStack map;
    private BlockPos center;
    private Player player;
    private ServerLevel serverLevel;
    private ResourceKey<Biome> biomeResourceKey;
    private UUID taskUUID;
    private Direction sampleDirection = Direction.UP;
    private BlockPos lastSampledPos = null;
    private boolean complete;
    private int samples = 0;
    private static final int SAMPLE_INCREMENT = AlexsCaves.COMMON_CONFIG.caveMapSearchWidth.get();
    private int width = 0;
    private int nextWidth = SAMPLE_INCREMENT;
    private BlockPos.MutableBlockPos nextPos = new BlockPos.MutableBlockPos();

    public CaveBiomeMapWorldWorker(ItemStack map, ServerLevel serverLevel, BlockPos center, Player player, UUID taskUUID) {
        this.map = map;
        this.serverLevel = serverLevel;
        this.center = center;
        ResourceKey<Biome> from = CaveMapItem.getBiomeTarget(map);
        this.biomeResourceKey = from == null ? ACBiomeRegistry.MAGNETIC_CAVES : from;
        this.player = player;
        this.taskUUID = taskUUID;
        this.stopwatch = Stopwatch.createStarted(Util.TICKER);
        this.nextPos.set(center);
    }

    @Override
    public boolean hasWork() {
        return !complete && samples < AlexsCaves.COMMON_CONFIG.caveMapSearchAttempts.get();
    }

    @Override
    public boolean doWork() {
        BlockPos pos = null;
        if (hasWork()) {
            samples++;
            int y = player.getBlockY();
            ServerChunkCache cache = serverLevel.getChunkSource();
            BiomeSource source = cache.getGenerator().getBiomeSource();
            Climate.Sampler sampler = cache.randomState().sampler();
            final int height = 64;

            if (sampleDirection != Direction.UP) {
                nextPos.move(sampleDirection, SAMPLE_INCREMENT);
            }

            int[] searchedHeights = Mth.outFromOrigin(y, serverLevel.getMinBuildHeight() + 1, serverLevel.getMaxBuildHeight(), height).toArray();

            int nextBlockX = nextPos.getX();
            int nextBlockZ = nextPos.getZ();
            int quartX = QuartPos.fromBlock(nextBlockX);
            int quartZ = QuartPos.fromBlock(nextBlockZ);

            for (int blockY : searchedHeights) {
                int quartY = QuartPos.fromBlock(blockY);
                Biome biome = source.getNoiseBiome(quartX, quartY, quartZ, sampler).get();
                if (verifyBiomeRespectRegistry(serverLevel, biome, biomeResourceKey)) {
                    pos = new BlockPos(nextBlockX, blockY, nextBlockZ);
                }
            }

            width += SAMPLE_INCREMENT;
            if (width >= nextWidth) {
                if (sampleDirection == Direction.UP) {
                    sampleDirection = Direction.NORTH;
                } else {
                    nextWidth += SAMPLE_INCREMENT;
                    sampleDirection = sampleDirection.getClockWise();
                }
                width = 0;
            }
            lastSampledPos = nextPos.immutable();
            if(pos == null){
                return true;
            }
        }
        if(!complete){
            onWorkComplete(pos);
            complete = true;
        }
        return false;
    }

    private void onWorkComplete(@Nullable BlockPos biomeCorner) {
        CompoundTag tag = map.getOrCreateTag();
        if (biomeCorner != null) {
            BlockPos centered = getCenterOfBiome(biomeCorner);
            fillOutMapColors(centered, tag);
            tag.putInt("BiomeX", centered.getX());
            tag.putInt("BiomeY", centered.getY());
            tag.putInt("BiomeZ", centered.getZ());
            tag.putLong("RandomSeed", serverLevel.getRandom().nextLong());
            tag.putBoolean("Filled", true);
            AlexsCaves.LOGGER.info("Found {} at {} {} {} in {}s", biomeResourceKey.location(), centered.getX(), centered.getY(), centered.getZ(), stopwatch.elapsed().toSeconds());
        } else {
            int distance = 0;
            if (lastSampledPos != null) {
                distance = (int) Math.sqrt(center.distSqr(lastSampledPos));
            }
            player.sendSystemMessage(Component.translatable("item.alexscaves.cave_map.error", distance).withStyle(ChatFormatting.RED));
            AlexsCaves.LOGGER.info("Could not find {} after {}s", biomeResourceKey.location(), stopwatch.elapsed().toSeconds());
        }
        tag.putBoolean("Loading", false);
        tag.remove("MapUUID");
        map.setTag(tag);
        AlexsCaves.sendMSGToAll(new UpdateCaveBiomeMapTagMessage(player.getId(), getTaskUUID(), tag));
    }

    private BlockPos findBiome() {

        return null;
    }

    private static boolean verifyBiomeRespectRegistry(Level level, Biome biome, ResourceKey<Biome> matches) {
        Optional<Registry<Biome>> biomeRegistry = level.registryAccess().registry(ForgeRegistries.Keys.BIOMES);
        if (biomeRegistry.isPresent()) {
            Optional<ResourceKey<Biome>> resourceKey = biomeRegistry.get().getResourceKey(biome);
            return resourceKey.isPresent() && resourceKey.get().equals(matches);
        } else {
            return false;
        }
    }

    private BlockPos getCenterOfBiome(BlockPos biomeCorner) {
        int biomeNorth = 0;
        int biomeSouth = 0;
        int biomeEast = 0;
        int biomeWest = 0;
        BlockPos yCentered;
        if (mapBiomeBeneathSurfaceOnly()) {
            int iterations = 0;
            yCentered = biomeCorner;
            while (iterations < 256 && serverLevel.getBiome(yCentered).is(biomeResourceKey)) {
                iterations++;
                yCentered = yCentered.above();
            }
            yCentered = yCentered.below(10);
        } else {
            int biomeUp = 0;
            int biomeDown = 0;
            while (biomeUp < 32 && serverLevel.getBiome(biomeCorner.above(biomeUp)).is(biomeResourceKey)) {
                biomeUp += 8;
            }
            while (biomeDown < 64 && serverLevel.getBiome(biomeCorner.below(biomeDown)).is(biomeResourceKey)) {
                biomeDown += 8;
            }
            yCentered = biomeCorner.atY((int) (Math.floor(biomeUp * 0.25F)) - biomeDown);
        }
        while (biomeNorth < 800 && serverLevel.getBiome(yCentered.north(biomeNorth)).is(biomeResourceKey)) {
            biomeNorth += 8;
        }
        while (biomeSouth < 800 && serverLevel.getBiome(yCentered.south(biomeSouth)).is(biomeResourceKey)) {
            biomeSouth += 8;
        }
        while (biomeEast < 800 && serverLevel.getBiome(yCentered.east(biomeEast)).is(biomeResourceKey)) {
            biomeEast += 8;
        }
        while (biomeWest < 800 && serverLevel.getBiome(yCentered.west(biomeWest)).is(biomeResourceKey)) {
            biomeWest += 8;
        }
        return yCentered.offset(biomeEast - biomeWest, 0, biomeSouth - biomeNorth);
    }


    private void fillOutMapColors(BlockPos first, CompoundTag tag) {
        Registry<Biome> registry = serverLevel.registryAccess().registry(Registries.BIOME).orElse(null);
        byte[] arr = new byte[128 * 128];
        Map<Integer, Byte> biomeMap = new HashMap<>();
        if (registry != null) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            int scale = CaveMapItem.MAP_SCALE;
            int j = first.getX();
            int k = first.getZ();
            int l = j / scale - 64;
            int i1 = k / scale - 64;
            for (int j1 = 0; j1 < 128; ++j1) {
                for (int k1 = 0; k1 < 128; ++k1) {
                    Holder<Biome> holder1 = serverLevel.getBiome(mutableBlockPos.set((l + k1) * scale, first.getY(), (i1 + j1) * scale));
                    if (mapBiomeBeneathSurfaceOnly()) {
                        holder1 = serverLevel.getBiome(mutableBlockPos.setY(first.getY() - 5));
                    } else {
                        for (int yUpFromBottom = serverLevel.getMinBuildHeight() + 1; yUpFromBottom < serverLevel.getMaxBuildHeight(); yUpFromBottom += 32) {
                            holder1 = serverLevel.getBiome(mutableBlockPos.setY(yUpFromBottom));
                            if (holder1.is(biomeResourceKey)) {
                                break;
                            }
                        }

                    }
                    int id = registry.getId(holder1.value());
                    byte biomeHash;
                    if (biomeMap.containsKey(id)) {
                        biomeHash = biomeMap.get(id);
                    } else {
                        biomeHash = (byte) biomeMap.size();
                        biomeMap.put(id, biomeHash);
                    }
                    arr[j1 * 128 + k1] = biomeHash;
                }
            }
        }
        ListTag listTag = new ListTag();
        for (Map.Entry<Integer, Byte> entry : biomeMap.entrySet()) {
            CompoundTag biomeEntryTag = new CompoundTag();
            biomeEntryTag.putInt("BiomeID", entry.getKey());
            biomeEntryTag.putInt("BiomeHash", entry.getValue());
            listTag.add(biomeEntryTag);
        }
        tag.put("MapBiomeList", listTag);
        tag.putByteArray("MapBiomes", arr);
    }

    private boolean mapBiomeBeneathSurfaceOnly() {
        return biomeResourceKey.equals(ACBiomeRegistry.ABYSSAL_CHASM);
    }

    public UUID getTaskUUID() {
        return taskUUID;
    }
}
