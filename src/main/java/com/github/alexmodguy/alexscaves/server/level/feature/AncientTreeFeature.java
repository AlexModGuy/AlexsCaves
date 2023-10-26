package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.TreeStarBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AncientTreeFeature extends Feature<NoneFeatureConfiguration> {

    public AncientTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos treeBottom = context.origin();
        int height = 3 + randomsource.nextInt(4);

        if (!checkCanTreePlace(level, treeBottom, height)) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != Direction.Axis.Y) {
                int rootHeight = randomsource.nextInt(2);
                for (int i = 0; i < rootHeight; i++) {
                    level.setBlock(treeBottom.above(i).relative(direction), Blocks.JUNGLE_LOG.defaultBlockState(), 3);
                }
                BlockPos canopyPos = treeBottom.above(height);
                int branchOut = 1 + randomsource.nextInt(2);
                int branchUp = randomsource.nextInt(1) + 1;
                for (int i = 1; i <= branchOut; i++) {
                    level.setBlock(canopyPos.relative(direction, i), Blocks.JUNGLE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction.getAxis()), 3);
                }
                for (int i = 1; i <= branchUp + 1; i++) {
                    level.setBlock(canopyPos.relative(direction, branchOut + 1).above(i), Blocks.JUNGLE_LOG.defaultBlockState(), 3);
                }
                drawLeafOrb(level, canopyPos.relative(direction, branchOut).above(branchUp + 2), randomsource, ACBlockRegistry.ANCIENT_LEAVES.get().defaultBlockState(), 2 + randomsource.nextInt(2), 4 + randomsource.nextInt(2), 2 + randomsource.nextInt(2));
            }
        }
        for (int i = 0; i <= height; i++) {
            BlockPos trunkPos = treeBottom.above(i);
            level.setBlock(trunkPos, Blocks.JUNGLE_LOG.defaultBlockState(), 3);
        }

        return true;
    }


    private boolean checkCanTreePlace(WorldGenLevel level, BlockPos treeBottom, int height) {
        BlockState below = level.getBlockState(treeBottom.below());
        if (!below.is(BlockTags.DIRT)) {
            return false;
        }
        for (int i = 0; i < height; i++) {
            if (!canReplace(level.getBlockState(treeBottom.above(i)))) {
                return false;
            }
        }
        BlockPos treeTop = treeBottom.above(height);
        for (BlockPos checkLeaf : BlockPos.betweenClosed(treeTop.offset(-3, -1, -3), treeTop.offset(3, 3, 3))) {
            if (!canReplace(level.getBlockState(checkLeaf))) {
                return false;
            }
        }
        return true;
    }


    private static void drawLeafOrb(WorldGenLevel level, BlockPos center, RandomSource random, BlockState blockState, int radiusX, int radiusY, int radiusZ) {
        double equalRadius = (radiusX + radiusY + radiusZ) / 3.0D;
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY / 3; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    BlockPos fill = center.offset(x, y, z);
                    if (fill.distToLowCornerSqr(center.getX(), center.getY(), center.getZ()) <= equalRadius * equalRadius - random.nextFloat() * 7) {
                        if (canReplace(level.getBlockState(fill))) {
                            level.setBlock(fill, blockState, 3);
                            if (random.nextInt(5) == 0) {
                                Direction dir = Direction.getRandom(random);
                                BlockPos starPos = fill.relative(dir);
                                if (level.getBlockState(starPos).isAir()) {
                                    level.setBlock(starPos, ACBlockRegistry.TREE_STAR.get().defaultBlockState().setValue(TreeStarBlock.FACING, dir), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean canReplace(BlockState state) {
        return (state.isAir() || state.canBeReplaced() || state.is(ACBlockRegistry.ANCIENT_LEAVES.get()) || state.is(ACBlockRegistry.TREE_STAR.get())) && !state.is(ACTagRegistry.UNMOVEABLE) && state.getFluidState().isEmpty();
    }


}
