package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.TubeWormBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TubeWormFeature extends Feature<NoneFeatureConfiguration> {

    public TubeWormFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos ventBottom = new BlockPos.MutableBlockPos();
        ventBottom.set(context.origin());
        while (!level.getBlockState(ventBottom).getFluidState().isEmpty() && ventBottom.getY() > level.getMinBuildHeight()) {
            ventBottom.move(0, -1, 0);
        }
        if (ventBottom.getY() < level.getSeaLevel() - 30 && level.getBlockState(ventBottom.below()).equals(Blocks.TUFF.defaultBlockState())) {
            for (int i = 0; i < 4 + randomsource.nextInt(4); i++) {
                BlockPos wormAt = ventBottom.immutable().offset(randomsource.nextInt(10) - 5, randomsource.nextInt(4), randomsource.nextInt(10) - 5);
                Direction wormAttachDirection = Direction.DOWN;
                Direction randomDirection = Direction.from2DDataValue(2 + randomsource.nextInt(3));
                BlockPos wormAttachedToPos;
                BlockPos randomPos = wormAt.relative(randomDirection);
                BlockState randomState = level.getBlockState(randomPos);
                if (randomState.isFaceSturdy(level, randomPos, randomDirection.getOpposite())) {
                    wormAttachDirection = randomDirection;
                    wormAttachedToPos = randomPos;
                } else {
                    while (level.getBlockState(wormAt).isFaceSturdy(level, wormAt.above(), Direction.DOWN) && wormAt.getY() < level.getMaxBuildHeight()) {
                        wormAt = wormAt.above();
                    }
                    while (!level.getBlockState(wormAt).getFluidState().isEmpty() && wormAt.getY() > level.getMinBuildHeight()) {
                        wormAt = wormAt.below();
                    }
                    wormAttachedToPos = wormAt.below();
                }
                if (level.getBlockState(wormAt).is(ACTagRegistry.TUBE_WORM_AVOIDS) || level.getBlockState(wormAt).is(ACBlockRegistry.TUBE_WORM.get())) {
                    continue;
                }
                int maxSegments = 4 + randomsource.nextInt(12);
                growWorm(level, wormAttachedToPos, wormAttachDirection, randomsource, maxSegments);
            }
            return true;
        }
        return false;
    }

    private void growWorm(WorldGenLevel level, BlockPos wormAttachedToPos, Direction wormAttachDirection, RandomSource randomsource, int maxSegments) {
        if (wormAttachedToPos.getY() > level.getSeaLevel() - 30) {
            return;
        }
        int placedWorms = 0;
        BlockPos.MutableBlockPos prevWorm = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos worm = new BlockPos.MutableBlockPos();
        prevWorm.set(wormAttachedToPos);
        worm.set(wormAttachedToPos.relative(wormAttachDirection.getOpposite()));
        BlockState defaultWormState = ACBlockRegistry.TUBE_WORM.get().defaultBlockState().setValue(TubeWormBlock.WATERLOGGED, true);
        boolean canBranch = false;
        while (placedWorms < maxSegments) {
            BlockState wormState = defaultWormState;
            prevWorm.set(worm);
            if (worm.getY() > level.getSeaLevel() - 30) {
                return;
            }
            if (canBranch) {
                if (randomsource.nextBoolean()) {
                    Direction randomDirection = Direction.from2DDataValue(2 + randomsource.nextInt(3));
                    worm.move(randomDirection.getStepX(), randomDirection.getStepY(), randomDirection.getStepZ());
                    if (!level.getFluidState(worm).isEmpty()) {
                        if(!level.getBlockState(worm).is(Blocks.WATER) || !level.getBlockState(worm).canBeReplaced()){
                            return;
                        }
                        if(level.getBlockState(prevWorm).canBeReplaced() || level.getBlockState(prevWorm).is(ACBlockRegistry.TUBE_WORM.get())){
                            level.setBlock(prevWorm, wormState.setValue(TubeWormBlock.TUBE_TYPE, TubeWormBlock.TubeShape.TURN).setValue(TubeWormBlock.FACING, randomDirection), 3);
                        }
                        wormState = wormState.setValue(TubeWormBlock.TUBE_TYPE, TubeWormBlock.TubeShape.ELBOW).setValue(TubeWormBlock.FACING, randomDirection.getOpposite());
                    } else {
                        worm.set(prevWorm);
                        worm.move(0, 1, 0);
                    }
                }
                canBranch = false;
            } else {
                worm.move(0, 1, 0);
                canBranch = placedWorms > 1;
            }
            if (level.getFluidState(worm).isEmpty()) {
                break;
            } else if(level.getBlockState(worm).canBeReplaced()){
                level.setBlock(worm, wormState, 3);
            }

            placedWorms++;
        }
        BlockPos fixAttachementPos = wormAttachedToPos.relative(wormAttachDirection.getOpposite());
        if (wormAttachDirection.getAxis().isHorizontal() && (level.getBlockState(fixAttachementPos).canBeReplaced() || level.getBlockState(fixAttachementPos).is(ACBlockRegistry.TUBE_WORM.get()))) {
            level.setBlock(fixAttachementPos, defaultWormState.setValue(TubeWormBlock.TUBE_TYPE, TubeWormBlock.TubeShape.ELBOW).setValue(TubeWormBlock.FACING, wormAttachDirection), 3);
        }
    }
}
