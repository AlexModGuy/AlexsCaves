package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.GeothermalVentBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AcidVentFeature extends Feature<NoneFeatureConfiguration> {

    public AcidVentFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos ventBottom = context.origin();
        if (level.getBlockState(ventBottom.below()).equals(Blocks.MUD.defaultBlockState())) {
            drawVent(level, ventBottom, randomsource);
            for (int i = 0; i < 1 + randomsource.nextInt(2); i++) {
                drawVent(level, ventBottom.offset(randomsource.nextInt(8) - 4, 0, randomsource.nextInt(8) - 4), randomsource);
            }
        }
        return false;
    }

    private static void drawVent(WorldGenLevel level, BlockPos ventBottom, RandomSource randomsource) {
        int height = randomsource.nextInt(4) + 2;
        int acidCount = 0;
        while ((!level.getFluidState(ventBottom).isEmpty() || !level.getBlockState(ventBottom).isAir()) && ventBottom.getY() < level.getMaxBuildHeight() - height) {
            ventBottom = ventBottom.above();
            acidCount++;
            if (acidCount >= 3) {
                return;
            }
        }
        if (!hasClearance(level, ventBottom, height)) {
            return;
        }
        ventBottom = ventBottom.below();
        drawOrb(level, ventBottom, randomsource, 1 + randomsource.nextInt(1), 2 + randomsource.nextInt(3), 1 + randomsource.nextInt(1));
        level.setBlock(ventBottom.north(), Blocks.TUFF.defaultBlockState(), 2);
        level.setBlock(ventBottom.south(), Blocks.TUFF.defaultBlockState(), 2);
        level.setBlock(ventBottom.east(), Blocks.TUFF.defaultBlockState(), 2);
        level.setBlock(ventBottom.west(), Blocks.TUFF.defaultBlockState(), 2);
        level.setBlock(ventBottom.below(), Blocks.TUFF.defaultBlockState(), 2);
        level.setBlock(ventBottom, ACBlockRegistry.ACID.get().defaultBlockState(), 2);
        int middleStart = Math.max(1, height / 3);
        int middleTop = middleStart * 2;
        for (int i = 1; i <= height; i++) {
            BlockState vent;
            if (i <= middleStart) {
                vent = ACBlockRegistry.GEOTHERMAL_VENT.get().defaultBlockState().setValue(GeothermalVentBlock.SMOKE_TYPE, 3).setValue(GeothermalVentBlock.SPAWNING_PARTICLES, i == height);
            } else if (i > middleTop) {
                vent = ACBlockRegistry.GEOTHERMAL_VENT_THIN.get().defaultBlockState().setValue(GeothermalVentBlock.SMOKE_TYPE, 3).setValue(GeothermalVentBlock.SPAWNING_PARTICLES, i == height);
            } else {
                vent = ACBlockRegistry.GEOTHERMAL_VENT_MEDIUM.get().defaultBlockState().setValue(GeothermalVentBlock.SMOKE_TYPE, 3).setValue(GeothermalVentBlock.SPAWNING_PARTICLES, i == height);
            }
            level.setBlock(ventBottom.above(i), vent, 2);
        }
        level.setBlock(ventBottom.above(height + 1), Blocks.CAVE_AIR.defaultBlockState(), 2);
    }

    private static boolean hasClearance(WorldGenLevel level, BlockPos ventBottom, int height) {
        int i = 0;
        while (i < height) {
            if (!level.isEmptyBlock(ventBottom.above(1 + i))) {
                return false;
            }
            i++;
        }
        return true;
    }

    private static void drawOrb(WorldGenLevel level, BlockPos center, RandomSource random, int radiusX, int radiusY, int radiusZ) {
        double equalRadius = (radiusX + radiusY + radiusZ) / 3.0D;
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    BlockPos fill = center.offset(x, y, z);
                    if (fill.distToLowCornerSqr(center.getX(), center.getY(), center.getZ()) <= equalRadius * equalRadius + random.nextFloat() * 2) {
                        if (canReplace(level.getBlockState(fill)) && fill.getY() <= center.getY()) {
                            level.setBlock(fill, Blocks.TUFF.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
    }


    private static boolean canReplace(BlockState state) {
        return !state.is(ACTagRegistry.UNMOVEABLE) && !state.isAir();
    }
}
