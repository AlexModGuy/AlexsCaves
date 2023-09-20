package com.github.alexmodguy.alexscaves.server.level.biome;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;

public class BiomeSampler {

    private static final double[] GAUSSIAN_SAMPLE_KERNEL = new double[]{0.0D, 1.0D, 4.0D, 6.0D, 4.0D, 1.0D, 0.0D};

    public static Vec3 sampleBiomesVec3(LevelReader level, Vec3 pos, Vec3Fetcher vec3Fetcher) {
        return sampleBiomesVec3Quart(level, pos.subtract(2.0D, 2.0D, 2.0D).scale(0.25D), vec3Fetcher);
    }

    public static float sampleBiomesFloat(LevelReader level, Vec3 pos, FloatFetcher floatFetcher) {
        return sampleBiomesFloatQuart(level, pos.subtract(2.0D, 2.0D, 2.0D).scale(0.25D), floatFetcher);
    }

    public static Vec3 sampleBiomesVec3Quart(LevelReader level, Vec3 quartPos, Vec3Fetcher vec3Fetcher){
        int i = Mth.floor(quartPos.x());
        int j = Mth.floor(quartPos.y());
        int k = Mth.floor(quartPos.z());
        double d0 = quartPos.x() - (double)i;
        double d1 = quartPos.y() - (double)j;
        double d2 = quartPos.z() - (double)k;
        double d3 = 0.0D;
        Vec3 vec3 = Vec3.ZERO;
        BiomeManager biomeManager = level.getBiomeManager();

        for(int l = 0; l < 6; ++l) {
            double d4 = Mth.lerp(d0, GAUSSIAN_SAMPLE_KERNEL[l + 1], GAUSSIAN_SAMPLE_KERNEL[l]);
            int i1 = i - 2 + l;

            for(int j1 = 0; j1 < 6; ++j1) {
                double d5 = Mth.lerp(d1, GAUSSIAN_SAMPLE_KERNEL[j1 + 1], GAUSSIAN_SAMPLE_KERNEL[j1]);
                int k1 = j - 2 + j1;

                for(int l1 = 0; l1 < 6; ++l1) {
                    double d6 = Mth.lerp(d2, GAUSSIAN_SAMPLE_KERNEL[l1 + 1], GAUSSIAN_SAMPLE_KERNEL[l1]);
                    int i2 = k - 2 + l1;
                    double d7 = d4 * d5 * d6;
                    d3 += d7;
                    vec3 = vec3.add(vec3Fetcher.fetch(biomeManager.getNoiseBiomeAtQuart(i1, k1, i2)).scale(d7));
                }
            }
        }

        return vec3.scale(1.0D / d3);
    }

    public static float sampleBiomesFloatQuart(LevelReader level, Vec3 quartPos, FloatFetcher floatFetcher){
        int i = Mth.floor(quartPos.x());
        int j = Mth.floor(quartPos.y());
        int k = Mth.floor(quartPos.z());
        double d0 = quartPos.x() - (double)i;
        double d1 = quartPos.y() - (double)j;
        double d2 = quartPos.z() - (double)k;
        double d3 = 0.0D;
        float f = 0;
        BiomeManager biomeManager = level.getBiomeManager();

        for(int l = 0; l < 6; ++l) {
            double d4 = Mth.lerp(d0, GAUSSIAN_SAMPLE_KERNEL[l + 1], GAUSSIAN_SAMPLE_KERNEL[l]);
            int i1 = i - 2 + l;

            for(int j1 = 0; j1 < 6; ++j1) {
                double d5 = Mth.lerp(d1, GAUSSIAN_SAMPLE_KERNEL[j1 + 1], GAUSSIAN_SAMPLE_KERNEL[j1]);
                int k1 = j - 2 + j1;

                for(int l1 = 0; l1 < 6; ++l1) {
                    double d6 = Mth.lerp(d2, GAUSSIAN_SAMPLE_KERNEL[l1 + 1], GAUSSIAN_SAMPLE_KERNEL[l1]);
                    int i2 = k - 2 + l1;
                    double d7 = d4 * d5 * d6;
                    d3 += d7;
                    f +=  floatFetcher.fetch(biomeManager.getNoiseBiomeAtQuart(i1, k1, i2)) * d7;
                }
            }
        }

        return f / (float) d3;
    }

    @FunctionalInterface
    public interface Vec3Fetcher {
        Vec3 fetch(Holder<Biome> biomeHolder);
    }

    @FunctionalInterface
    public interface FloatFetcher {
        float fetch(Holder<Biome> biomeHolder);
    }
}
