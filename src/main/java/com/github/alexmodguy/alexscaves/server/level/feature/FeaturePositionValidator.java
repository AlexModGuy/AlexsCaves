package com.github.alexmodguy.alexscaves.server.level.feature;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class FeaturePositionValidator {

    public static boolean isBiome(FeaturePlaceContext context, ResourceKey<Biome> biomeResourceKey) {
        int j = context.level().getHeight(Heightmap.Types.OCEAN_FLOOR, context.origin().getX(), context.origin().getZ());
        return context.level().getBiome(context.origin().atY(Math.min(context.level().getMinBuildHeight(), j - 30))).is(biomeResourceKey);
    }
}
