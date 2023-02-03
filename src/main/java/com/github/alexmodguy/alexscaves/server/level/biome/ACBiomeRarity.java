package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACBiomeRarity {
    private static long lastTestedSeed = 0;
    private static final List<Integer> BIOME_OCTAVES = ImmutableList.of(-1, 0);
    private static final Map<Integer, PerlinSimplexNoise> SIMPLEX_NOISE_HASH_MAP = new HashMap<>();

    public static boolean testBiomeRarity(long worldSeed, int rarityOffset, int x, int z, float min, float max) {
        if(lastTestedSeed != worldSeed){
            lastTestedSeed = worldSeed;
            SIMPLEX_NOISE_HASH_MAP.clear();
        }
        if(!SIMPLEX_NOISE_HASH_MAP.containsKey(rarityOffset)){
            SIMPLEX_NOISE_HASH_MAP.put(rarityOffset, new PerlinSimplexNoise(new LegacyRandomSource(32L * rarityOffset + worldSeed), BIOME_OCTAVES));
        }
        PerlinSimplexNoise noise = SIMPLEX_NOISE_HASH_MAP.get(rarityOffset);
        if(noise == null){
            return false;
        }else{
            double simplexScale =  Math.max(AlexsCaves.COMMON_CONFIG.biomeRarityScale.get(), 1);
            double simplexElevation = Mth.clamp(AlexsCaves.COMMON_CONFIG.biomeRarityElevation.get(), 0.1D, 3D);
            double simplex = noise.getValue(x / simplexScale,  z / simplexScale, false);
            double elevatedSimplex = Math.min(Math.pow(simplex, simplexElevation), 1);
            return elevatedSimplex >= min && elevatedSimplex <= max;
        }
    }
}
