package com.github.alexmodguy.alexscaves.server.level.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * Validates feature placement positions to prevent unwanted generation in AlexsCaves biomes.
 */
public class FeaturePositionValidator {

    /**
     * Check if the feature placement is in the specified biome (at ocean floor - 30 blocks depth).
     * Returns true on error to conservatively prevent generation.
     */
    public static boolean isBiome(FeaturePlaceContext context, ResourceKey<Biome> biomeResourceKey) {
        try {
            BlockPos origin = context.origin();
            int oceanFloorY = context.level().getHeight(Heightmap.Types.OCEAN_FLOOR, origin.getX(), origin.getZ());
            int checkY = Math.max(context.level().getMinBuildHeight(), oceanFloorY - 30);
            return context.level().getBiome(origin.atY(checkY)).is(biomeResourceKey);
        } catch (Exception e) {
            return true;
        }
    }
}
