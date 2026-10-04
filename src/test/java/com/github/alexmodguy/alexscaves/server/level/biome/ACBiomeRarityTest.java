package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class ACBiomeRarityTest {
    private static final class ReferenceSampler {
        final PerlinSimplexNoise noiseX = new PerlinSimplexNoise(new XoroshiroRandomSource(1234L), List.of(0));
        final PerlinSimplexNoise noiseZ = new PerlinSimplexNoise(new XoroshiroRandomSource(4321L), List.of(0));

        VoronoiGenerator.VoronoiInfo sample(long seed, int x, int z, ACBiomeRarity.BiomeConfig config, double width) {
            // Original uncached calculation, with a fresh generator for this sample.
            VoronoiGenerator generator = new VoronoiGenerator(seed);
            generator.setOffsetAmount(config.offsetAmount());
            double sampleX = x / config.separationDistance();
            double sampleZ = z / config.separationDistance();
            var result = generator.get2(sampleX + width * noiseX.getValue(sampleX, sampleZ, false),
                sampleZ + width * noiseZ.getValue(sampleX, sampleZ, false));
            return result.distance() < config.biomeSize() / config.separationDistance() ? result : null;
        }
    }

    @Test
    public void invalidatesAcrossSeedsWidthsConfigSnapshotsAndCollidingColumns() {
        ReferenceSampler reference = new ReferenceSampler();
        ACBiomeRarity.BiomeConfig[] configs = {
            new ACBiomeRarity.BiomeConfig(1, 128, 256),
            new ACBiomeRarity.BiomeConfig(0.2, 64, 192),
            new ACBiomeRarity.BiomeConfig(1, 0, 256)
        };
        for (int pass = 0; pass < 3; pass++) {
            for (var config : configs) {
                for (long seed : new long[]{0, 42, -19734568639L}) {
                    for (double width : new double[]{0.0, -0.0, 0.5, 1.0}) {
                        for (int x = -64; x <= 64; x += 8) {
                            int z = x * 3 + 5;
                            var expected = reference.sample(seed, x, z, config, width);
                            var actual = ACBiomeRarity.getRareBiomeInfoForQuad(seed, x, z, config, width);
                            assertEquals(expected, actual);
                            assertSame(actual, ACBiomeRarity.getRareBiomeInfoForQuad(seed, x, z, config, width));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void distinctButEqualSnapshotsInvalidateTheCachedObject() {
        var first = new ACBiomeRarity.BiomeConfig(1, 1000, 1000);
        var second = new ACBiomeRarity.BiomeConfig(1, 1000, 1000);
        var a = ACBiomeRarity.getRareBiomeInfoForQuad(42, 0, 0, first, 0.5);
        var b = ACBiomeRarity.getRareBiomeInfoForQuad(42, 0, 0, second, 0.5);
        assertNotNull(a);
        assertEquals(a, b);
        assertNotSame(a, b);
    }

    @Test(timeout = 30000)
    public void simultaneousWorldSeedsMatchIsolatedUncachedSampling() throws Exception {
        var executor = Executors.newFixedThreadPool(4);
        var barrier = new CyclicBarrier(4);
        List<Future<?>> tasks = new ArrayList<>();
        try {
            for (int thread = 0; thread < 4; thread++) {
                long seed = thread * 9876543217L;
                tasks.add(executor.submit(() -> {
                    ReferenceSampler reference = new ReferenceSampler();
                    var configs = new ACBiomeRarity.BiomeConfig[]{
                        new ACBiomeRarity.BiomeConfig(1, 128, 256),
                        new ACBiomeRarity.BiomeConfig(0.3, 256, 512)
                    };
                    for (int i = 0; i < 1000; i++) {
                        barrier.await();
                        int x = i % 17 - 8;
                        int z = i % 13 - 6;
                        var config = configs[(i / 13) & 1];
                        double width = (i % 3) * 0.5;
                        assertEquals(reference.sample(seed, x, z, config, width),
                            ACBiomeRarity.getRareBiomeInfoForQuad(seed, x, z, config, width));
                    }
                    return null;
                }));
            }
            for (Future<?> task : tasks)
                task.get();
        } finally {
            executor.shutdownNow();
        }
    }
}
