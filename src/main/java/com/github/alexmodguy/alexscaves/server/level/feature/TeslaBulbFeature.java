package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.GalenaSpireBlock;
import com.github.alexmodguy.alexscaves.server.block.TeslaBulbBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class TeslaBulbFeature extends Feature<NoneFeatureConfiguration> {

    public TeslaBulbFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        boolean ceiling = randomsource.nextBoolean();
        BlockPos.MutableBlockPos generateAt = new BlockPos.MutableBlockPos();
        generateAt.set(context.origin());
        if (!level.getBlockState(generateAt).getFluidState().is(Fluids.WATER) && !level.isEmptyBlock(generateAt)) {
            return false;
        }
        if (ceiling) {
            while ((level.getBlockState(generateAt).getFluidState().is(Fluids.WATER) || !level.getBlockState(generateAt).isFaceSturdy(level, generateAt, Direction.DOWN)) && generateAt.getY() < level.getMaxBuildHeight()) {
                generateAt.move(0, 1, 0);
            }
        } else {
            while ((level.getBlockState(generateAt).getFluidState().is(Fluids.WATER) || !level.getBlockState(generateAt).isFaceSturdy(level, generateAt, Direction.UP)) && generateAt.getY() > level.getMinBuildHeight()) {
                generateAt.move(0, -1, 0);
            }
        }
        if (!level.getBlockState(generateAt).is(ACTagRegistry.TESLA_BULB_BASE_BLOCKS)) {
            return false;
        }
        BlockPos below = generateAt.immutable();
        int centerHeight = 3 + randomsource.nextInt(3);
        generatePillar(level, below, randomsource, centerHeight, ceiling, randomsource.nextFloat() < 0.25F);
        for (int i = 0; i < 4 + randomsource.nextInt(4); i++) {
            BlockPos offset = below.offset(randomsource.nextInt(8) - 4, ceiling ? -3 : 3, randomsource.nextInt(8) - 4);
            if (ceiling) {
                while (!level.getBlockState(offset).isFaceSturdy(level, offset, Direction.DOWN) && offset.getY() < level.getMaxBuildHeight()) {
                    offset = offset.above();
                }
            } else {
                while (!level.getBlockState(offset).isFaceSturdy(level, offset, Direction.UP) && offset.getY() > level.getMinBuildHeight()) {
                    offset = offset.below();
                }
            }
            if (!level.getBlockState(offset.relative(ceiling ? Direction.UP : Direction.DOWN)).is(ACTagRegistry.TESLA_BULB_BASE_BLOCKS) || offset.getX() == below.getX() && offset.getZ() == below.getZ()) {
                continue;
            }
            int dist = (int) Math.ceil(offset.distManhattan(below) * 0.2F);
            generatePillar(level, offset, randomsource, Math.min(centerHeight - dist, 1) + randomsource.nextInt(2), ceiling, false);
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return (state.isAir() || state.canBeReplaced()) && !state.is(ACTagRegistry.UNMOVEABLE);
    }

    private static void generatePillar(WorldGenLevel level, BlockPos pos, RandomSource randomSource, int height, boolean ceiling, boolean tesla) {
        BlockPos begin = pos.relative(ceiling ? Direction.UP : Direction.DOWN, 3);
        BlockState spireState = ACBlockRegistry.GALENA_SPIRE.get().defaultBlockState().setValue(GalenaSpireBlock.DOWN, ceiling);
        int spireCount = 0;
        int j = 0;
        while (spireCount <= height && j < 25) {
            j++;
            int shape = 0;
            if (spireCount > height - 1) {
                shape = 3;
            } else if (spireCount > height - 2) {
                shape = 2;
            } else if (spireCount >= 1) {
                shape = 1;
            }
            begin = ceiling ? begin.below() : begin.above();
            BlockState prevState = level.getBlockState(begin);
            if (prevState.is(ACBlockRegistry.GALENA_SPIRE.get()) || !prevState.getFluidState().isEmpty() && !prevState.getFluidState().is(Fluids.WATER)) {
                break;
            }
            if (canReplace(prevState)) {
                if (shape == 3 && tesla) {
                    level.setBlock(begin, ACBlockRegistry.TESLA_BULB.get().defaultBlockState().setValue(TeslaBulbBlock.DOWN, ceiling).setValue(TeslaBulbBlock.WATERLOGGED, level.getFluidState(begin).is(Fluids.WATER)), 3);
                    break;
                } else {
                    level.setBlock(begin, spireState.setValue(GalenaSpireBlock.SHAPE, shape).setValue(GalenaSpireBlock.WATERLOGGED, level.getFluidState(begin).is(Fluids.WATER)), 3);
                }
                spireCount++;
            }
        }
    }
}
