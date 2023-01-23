package com.github.alexmodguy.alexscaves.server.level.map;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;

public class FilloutCaveBiomeMap implements Runnable {

    private ItemStack map;
    private BlockPos center;
    private ServerLevel serverLevel;
    private ResourceKey<Biome> biomeResourceKey;

    public FilloutCaveBiomeMap(ItemStack map, ServerLevel serverLevel, BlockPos center) {
        this.map = map;
        this.serverLevel = serverLevel;
        this.center = center;
        ResourceKey<Biome> from = CaveMapItem.getBiomeTarget(map);
        this.biomeResourceKey = from == null ? ACBiomeRegistry.MAGNETIC_CAVES : from;
    }

    @Override
    public void run() {
        double distScale = 6400 / 250D;
        int dist = (int) Math.ceil(distScale * AlexsCaves.COMMON_CONFIG.biomeRarityScale.get());
        Pair<BlockPos, Holder<Biome>> pair = serverLevel.findClosestBiome3d((biomeHolder -> biomeHolder.is(biomeResourceKey)), center, dist, 64, 128);
        CompoundTag tag = map.getOrCreateTag();
        tag.putBoolean("Loading", false);
        if (pair != null) {
            BlockPos biomeCorner = pair.getFirst();
            BlockPos centered = findBiomeCenter(biomeCorner);
            tag.putIntArray("MapBiomes", fillOutMapColors(centered));
            tag.putInt("BiomeX", centered.getX());
            tag.putInt("BiomeY", centered.getY());
            tag.putInt("BiomeZ", centered.getZ());
            tag.putLong("RandomSeed", serverLevel.getRandom().nextLong());
            tag.putBoolean("Filled", true);
            System.out.println(centered);
        }
        map.setTag(tag);
    }

    private BlockPos findBiomeCenter(BlockPos biomeCorner) {
        int biomeNorth = 0;
        int biomeSouth = 0;
        int biomeEast = 0;
        int biomeWest = 0;
        int biomeUp = 0;
        int biomeDown = 0;
        while(biomeNorth < 800 && serverLevel.getBiome(biomeCorner.north(biomeNorth)).is(biomeResourceKey)){
            biomeNorth += 8;
        }
        while(biomeSouth < 800 && serverLevel.getBiome(biomeCorner.south(biomeSouth)).is(biomeResourceKey)){
            biomeSouth += 8;
        }
        while(biomeEast < 800 && serverLevel.getBiome(biomeCorner.east(biomeEast)).is(biomeResourceKey)){
            biomeEast += 8;
        }
        while(biomeWest < 800 && serverLevel.getBiome(biomeCorner.west(biomeWest)).is(biomeResourceKey)){
            biomeWest += 8;
        }
        while(biomeUp < 32 && serverLevel.getBiome(biomeCorner.above(biomeUp)).is(biomeResourceKey)){
            biomeUp += 8;
        }
        while(biomeDown < 64 && serverLevel.getBiome(biomeCorner.below(biomeDown)).is(biomeResourceKey)){
            biomeDown += 8;
        }
        return biomeCorner.north(biomeNorth).east(biomeEast).south(biomeSouth).west(biomeWest).above(biomeUp).below(biomeDown);

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
                    Holder<Biome> holder2 = serverLevel.getBiome(mutableBlockPos.setY(serverLevel.getMaxBuildHeight() - 1));
                    arr[j1 * 128 + k1] = registry.getId(holder1.is(biomeResourceKey) ? holder1.value() : holder2.value());
                }
            }
        }
        return arr;
    }

}
