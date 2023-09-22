package com.github.alexmodguy.alexscaves.server.level.map;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.message.UpdateCaveBiomeMapTagMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.UUID;

public class FilloutCaveBiomeMap implements Runnable {

    private ItemStack map;
    private BlockPos center;
    private Player player;
    private ServerLevel serverLevel;
    private ResourceKey<Biome> biomeResourceKey;

    private UUID taskUUID;
    private int legIndex;
    private int leg;
    private int legX;
    private int legZ;

    public FilloutCaveBiomeMap(ItemStack map, ServerLevel serverLevel, BlockPos center, Player player, UUID taskUUID) {
        this.map = map;
        this.serverLevel = serverLevel;
        this.center = center;
        ResourceKey<Biome> from = CaveMapItem.getBiomeTarget(map);
        this.biomeResourceKey = from == null ? ACBiomeRegistry.MAGNETIC_CAVES : from;
        this.player = player;
        this.taskUUID = taskUUID;
    }

    @Override
    public void run() {
        int dist = AlexsCaves.COMMON_CONFIG.caveMapSearchDistance.get();
        //Pair<BlockPos, Holder<Biome>> pair = serverLevel.findClosestBiome3d((biomeHolder -> biomeHolder.is(biomeResourceKey)), center, dist, 64, 128);
        BlockPos biomeCorner = findBiome();
        CompoundTag tag = map.getOrCreateTag();
        if (biomeCorner != null) {
            BlockPos centered = getCenterOfBiome(biomeCorner);
            tag.putIntArray("MapBiomes", fillOutMapColors(centered));
            tag.putInt("BiomeX", centered.getX());
            tag.putInt("BiomeY", centered.getY());
            tag.putInt("BiomeZ", centered.getZ());
            tag.putLong("RandomSeed", serverLevel.getRandom().nextLong());
            tag.putBoolean("Filled", true);
            AlexsCaves.LOGGER.info("Found {} at {} {} {}", biomeResourceKey.location(), centered.getX(), centered.getY(), centered.getZ());
        } else {
            player.sendSystemMessage(Component.translatable("item.alexscaves.cave_map.error").withStyle(ChatFormatting.RED));
        }
        tag.putBoolean("Loading", false);
        tag.remove("MapUUID");
        map.setTag(tag);
        AlexsCaves.sendMSGToAll(new UpdateCaveBiomeMapTagMessage(player.getId(), getTaskUUID(), tag));
    }

    private BlockPos findBiome() {
        int y = player.getBlockY();
        ServerChunkCache cache = serverLevel.getChunkSource();
        BiomeSource source = cache.getGenerator().getBiomeSource();
        Climate.Sampler sampler = cache.randomState().sampler();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {

            final int height = 64;
            BlockPos nextPos = nextPos();
            if (nextPos == null) {
                return null;
            }
            int[] searchedHeights = Mth.outFromOrigin(y, serverLevel.getMinBuildHeight() + 1, serverLevel.getMaxBuildHeight(), height).toArray();

            int nextBlockX = nextPos.getX();
            int nextBlockZ = nextPos.getZ();
            int quartX = QuartPos.fromBlock(nextBlockX);
            int quartZ = QuartPos.fromBlock(nextBlockZ);

            for (int blockY : searchedHeights) {
                int quartY = QuartPos.fromBlock(blockY);
                Holder<Biome> holder = source.getNoiseBiome(quartX, quartY, quartZ, sampler);
                if (holder.is(biomeResourceKey)) {
                    return new BlockPos(nextBlockX, blockY, nextBlockZ);
                }
            }
        }
        return null;
    }

    protected BlockPos nextPos() {
        final int step = 32;

        int sourceX = center.getX();
        int sourceZ = center.getZ();


        BlockPos cursor = new BlockPos(legX, 0, legZ).relative(ACMath.HORIZONTAL_DIRECTIONS[(leg + 4) % 4]);

        int newX = cursor.getX();
        int newZ = cursor.getZ();

        int legSize = leg / 2 + 1;
        int maxLegs = 4 * Math.floorDiv(AlexsCaves.COMMON_CONFIG.caveMapSearchDistance.get(), step);

        if (legIndex >= legSize) {
            if (leg > maxLegs)
                return null;

            leg++;
            legIndex = 0;
        }

        legIndex++;

        legX = newX;
        legZ = newZ;

        int retX = sourceX + newX * step;
        int retZ = sourceZ + newZ * step;

        return new BlockPos(retX, 0, retZ);
    }


    private BlockPos getCenterOfBiome(BlockPos biomeCorner) {
        int biomeNorth = 0;
        int biomeSouth = 0;
        int biomeEast = 0;
        int biomeWest = 0;
        int biomeUp = 0;
        int biomeDown = 0;
        while (biomeUp < 32 && serverLevel.getBiome(biomeCorner.above(biomeUp)).is(biomeResourceKey)) {
            biomeUp += 8;
        }
        while (biomeDown < 64 && serverLevel.getBiome(biomeCorner.below(biomeDown)).is(biomeResourceKey)) {
            biomeDown += 8;
        }
        BlockPos yCentered = biomeCorner.atY((int) (Math.floor(biomeUp * 0.25F)) - biomeDown);
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


    private int[] fillOutMapColors(BlockPos first) {
        Registry<Biome> registry = serverLevel.registryAccess().registry(Registries.BIOME).orElse(null);
        int[] arr = new int[128 * 128];
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
                    for (int yUpFromBottom = serverLevel.getMinBuildHeight() + 1; yUpFromBottom < serverLevel.getMaxBuildHeight(); yUpFromBottom += 32) {
                        holder1 = serverLevel.getBiome(mutableBlockPos.setY(yUpFromBottom));
                        if (holder1.is(biomeResourceKey)) {
                            break;
                        }
                    }
                    arr[j1 * 128 + k1] = registry.getId(holder1.value());
                }
            }
        }
        return arr;
    }

    public UUID getTaskUUID() {
        return taskUUID;
    }
}
