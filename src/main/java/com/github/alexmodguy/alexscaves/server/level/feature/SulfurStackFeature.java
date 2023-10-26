package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.SulfurBudBlock;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
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

public class SulfurStackFeature extends Feature<NoneFeatureConfiguration> {

    public SulfurStackFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos below = context.origin();
        if (!level.getBlockState(below.below()).is(ACBlockRegistry.RADROCK.get())) {
            return false;
        }
        int centerHeight = 3 + randomsource.nextInt(3);
        generatePillar(level, below, randomsource, centerHeight);
        for (int i = 0; i < 2 + randomsource.nextInt(2); i++) {
            BlockPos offset = below.offset(randomsource.nextInt(4) - 2, -randomsource.nextInt(2), randomsource.nextInt(4) - 2);
            int dist = (int) Math.ceil(offset.distManhattan(below));
            generatePillar(level, offset, randomsource, centerHeight - dist + randomsource.nextInt(2));
        }
        for (int i = 0; i < 4 + randomsource.nextInt(6); i++) {
            BlockPos offset = below.offset(randomsource.nextInt(6) - 3, 4, randomsource.nextInt(6) - 3);
            while (level.isEmptyBlock(offset) && offset.getY() > level.getMinBuildHeight()) {
                offset = offset.below();
            }
            if (level.getBlockState(offset).isFaceSturdy(level, offset, Direction.UP) && level.isEmptyBlock(offset.above())) {
                placeRandomCrystal(level, offset.above(), randomsource);
            }
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return (state.isAir() || state.canBeReplaced()) && !state.is(ACTagRegistry.UNMOVEABLE);
    }

    private static void generatePillar(WorldGenLevel level, BlockPos pos, RandomSource randomSource, int height) {
        BlockPos begin = pos.relative(Direction.DOWN, 3);
        BlockPos stopPillarAt = pos.relative(Direction.UP, height);
        while (!begin.equals(stopPillarAt)) {
            begin = begin.relative(Direction.UP);
            if (canReplace(level.getBlockState(begin))) {
                level.setBlock(begin, ACBlockRegistry.SULFUR.get().defaultBlockState(), 3);
            }
        }
        if (canReplace(level.getBlockState(stopPillarAt.above())) && !(level.getBlockState(stopPillarAt).getBlock() instanceof SulfurBudBlock)) {
            placeRandomCrystal(level, stopPillarAt.above(), randomSource);
        }
    }

    private static void placeRandomCrystal(WorldGenLevel level, BlockPos placeAt, RandomSource randomSource) {
        BlockState crystal = ACBlockRegistry.SULFUR_CLUSTER.get().defaultBlockState();
        switch (randomSource.nextInt(3)) {
            case 0:
                crystal = ACBlockRegistry.SULFUR_BUD_SMALL.get().defaultBlockState();
                break;
            case 1:
                crystal = ACBlockRegistry.SULFUR_BUD_MEDIUM.get().defaultBlockState();
                break;
            case 2:
                crystal = ACBlockRegistry.SULFUR_BUD_LARGE.get().defaultBlockState();
                break;
        }
        if (crystal.getBlock() instanceof SulfurBudBlock) {
            if (level.getFluidState(placeAt).is(Fluids.WATER)) {
                crystal = crystal.setValue(SulfurBudBlock.LIQUID_LOGGED, 1);
            } else if (level.getFluidState(placeAt).getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get()) {
                crystal = crystal.setValue(SulfurBudBlock.LIQUID_LOGGED, 2);
            }
        }
        level.setBlock(placeAt, crystal, 3);
        BlockPos drip = placeAt.above();
        while (level.isEmptyBlock(drip) && drip.getY() < level.getMaxBuildHeight()) {
            drip = drip.above();
        }
        if (level.getFluidState(drip).isEmpty()) {
            level.setBlock(drip, ACBlockRegistry.ACIDIC_RADROCK.get().defaultBlockState(), 3);
        }
    }
}
