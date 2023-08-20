package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import java.util.List;

public class ACBiomeRarity {
    private static long lastTestedSeed = 0;
    private static PerlinSimplexNoise noiseX;
    private static PerlinSimplexNoise noiseZ;
    private static VoronoiGenerator voronoiGenerator;

    private static final List<Integer> BIOME_OCTAVES = ImmutableList.of(0);

    // ran for every chunk x z
    public static boolean testBiomeRarity(long worldSeed, int rarityOffset, int x, int z) {
        //start of code to initialize noise for world
        if (lastTestedSeed != worldSeed || voronoiGenerator == null || noiseX == null || noiseZ == null) {
            lastTestedSeed = worldSeed;
            noiseX = new PerlinSimplexNoise(new LegacyRandomSource(1234L + worldSeed), BIOME_OCTAVES);
            noiseZ = new PerlinSimplexNoise(new LegacyRandomSource(4321L + worldSeed), BIOME_OCTAVES);
            voronoiGenerator = new VoronoiGenerator(worldSeed);
            voronoiGenerator.setOffsetAmount(AlexsCaves.COMMON_CONFIG.caveBiomeSpacingRandomness.get());
        }
        //in blocks
        double biomeSize = AlexsCaves.COMMON_CONFIG.caveBiomeMeanWidth.get() * 0.25D;
        double seperationDistance = biomeSize + AlexsCaves.COMMON_CONFIG.caveBiomeMeanSeparation.get() * 0.25D;
        double sampleX = x / seperationDistance;
        double sampleZ = z / seperationDistance;
        double positionOffsetX = AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get() * noiseX.getValue(sampleX, sampleZ, false);
        double positionOffsetZ = AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get() * noiseZ.getValue(sampleX, sampleZ, false);
        VoronoiGenerator.VoronoiInfo info = voronoiGenerator.get2(sampleX + positionOffsetX, sampleZ + positionOffsetZ);

        if (info.distance() < (biomeSize / seperationDistance)) {
            double scaledHash = ((info.hash() + 1D) * 0.5D) * (double) BiomeGenerationConfig.getBiomeCount();
            return (int) scaledHash == rarityOffset;
        }
        return false;
    }

}
