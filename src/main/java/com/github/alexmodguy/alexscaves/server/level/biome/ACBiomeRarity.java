package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class ACBiomeRarity {
    private static long lastTestedSeed = 0;
    private static final List<Integer> BIOME_OCTAVES = ImmutableList.of(0);
    private static final PerlinSimplexNoise NOISE_X = new PerlinSimplexNoise(new XoroshiroRandomSource(1234L), BIOME_OCTAVES);
    private static final PerlinSimplexNoise NOISE_Z = new PerlinSimplexNoise(new XoroshiroRandomSource(4321L), BIOME_OCTAVES);
    private static volatile BiomeConfig config = new BiomeConfig(1.0, 0.0, 0.0);

    record BiomeConfig(double offsetAmount, double biomeSize, double separationDistance) {}

    private static final ThreadLocal<ColumnCache> COLUMN_CACHE = ThreadLocal.withInitial(ColumnCache::new);

    private static final class ColumnCache {
        final VoronoiGenerator generator = new VoronoiGenerator(42L);
        final long[] seeds = new long[64], widths = new long[64];
        final int[] xs = new int[64], zs = new int[64];
        final BiomeConfig[] configs = new BiomeConfig[64];
        final boolean[] populated = new boolean[64];
        final VoronoiGenerator.VoronoiInfo[] values = new VoronoiGenerator.VoronoiInfo[64];
    }

    /**
     * Called after config reset
     */
    public static void init() {
        double offsetAmount = AlexsCaves.COMMON_CONFIG.caveBiomeSpacingRandomness.get();
        double biomeSize = AlexsCaves.COMMON_CONFIG.caveBiomeMeanWidth.get() * 0.25D;
        double separationDistance = biomeSize + AlexsCaves.COMMON_CONFIG.caveBiomeMeanSeparation.get() * 0.25D;
        config = new BiomeConfig(offsetAmount, biomeSize, separationDistance);
    }

    /**
     * Does the heavy lifting of finding if x and z quads should contain a rare biome.
     *
     * @param worldSeed seed of the world for Voroni generation
     * @param x         xQuad being tested
     * @param z         zQuad being tested
     * @return the voroni info if a rare biome is present
     */
    @Nullable
    public static VoronoiGenerator.VoronoiInfo getRareBiomeInfoForQuad(long worldSeed, int x, int z) {
        BiomeConfig snapshot = config;
        double widthRandomness = AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get();
        return getRareBiomeInfoForQuad(worldSeed, x, z, snapshot, widthRandomness);
    }

    @Nullable
    static VoronoiGenerator.VoronoiInfo getRareBiomeInfoForQuad(long worldSeed, int x, int z,
                                                              BiomeConfig snapshot, double widthRandomness) {
        ColumnCache cache = COLUMN_CACHE.get();
        int index = ((x & 7) << 3) | (z & 7);
        long width = Double.doubleToRawLongBits(widthRandomness);
        if (cache.populated[index] && cache.xs[index] == x && cache.zs[index] == z
                && cache.seeds[index] == worldSeed && cache.widths[index] == width
                && cache.configs[index] == snapshot) {
            return cache.values[index];
        }
        cache.generator.setOffsetAmount(snapshot.offsetAmount());
        VoronoiGenerator.VoronoiInfo value = sampleRareBiomeInfo(cache.generator, worldSeed, x, z, snapshot, widthRandomness);
        cache.xs[index] = x;
        cache.zs[index] = z;
        cache.seeds[index] = worldSeed;
        cache.widths[index] = width;
        cache.configs[index] = snapshot;
        cache.values[index] = value;
        cache.populated[index] = true;
        return value;
    }

    @Nullable
    private static VoronoiGenerator.VoronoiInfo sampleRareBiomeInfo(VoronoiGenerator generator, long worldSeed, int x, int z,
                                                                  BiomeConfig snapshot, double widthRandomness) {
        //start of code to initialize noise for world
        generator.setSeed(worldSeed);
        double sampleX = x / snapshot.separationDistance();
        double sampleZ = z / snapshot.separationDistance();
        double positionOffsetX = widthRandomness * NOISE_X.getValue(sampleX, sampleZ, false);
        double positionOffsetZ = widthRandomness * NOISE_Z.getValue(sampleX, sampleZ, false);
        VoronoiGenerator.VoronoiInfo info = generator.get2(sampleX + positionOffsetX, sampleZ + positionOffsetZ);
        if (info.distance() < (snapshot.biomeSize() / snapshot.separationDistance())) {
            return info;
        } else {
            return null;
        }
    }

    /**
     * gets the center of the biome pertaining to the voronoiInfo. Result is in quad coordinates.
     *
     * @param voronoiInfo the info of the sampled biome
     * @return a coordinate
     */
    @Nullable
    public static Vec3 getRareBiomeCenter(VoronoiGenerator.VoronoiInfo voronoiInfo) {
        return voronoiInfo.cellPos().scale(config.separationDistance());
    }

    /**
     * gets the 'rarityOffset' for voroniInfo, which is essentially an internal biome ID.
     *
     * @param voronoiInfo the info of the sampled biome
     * @return the rarityOffset of the info
     */
    @Nullable
    public static int getRareBiomeOffsetId(VoronoiGenerator.VoronoiInfo voronoiInfo) {
        return (int) (((voronoiInfo.hash() + 1D) * 0.5D) * (double) BiomeGenerationConfig.getBiomeCount());
    }

    public static boolean isQuartInRareBiome(long worldSeed, int x, int z) {
        return ACBiomeRarity.getRareBiomeInfoForQuad(worldSeed, x, z) != null;
    }
}
