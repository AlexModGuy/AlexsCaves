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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GiantAncientTreeFeature extends Feature<NoneFeatureConfiguration> {

    public GiantAncientTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos treeBottom = context.origin();
        int maxHeight = 20 + randomsource.nextInt(10);
        int trunkThickness = 3;
        if (!checkCanTreePlace(level, treeBottom, maxHeight)) {
            return false;
        }
        BlockPos trunkRoot = treeBottom.below(2);
        int lastLeavesY = 0;
        for (int height = 0; height <= maxHeight; height++) {
            trunkRoot = trunkRoot.above();
            double radShrink = (1F - 0.8 * Math.pow(height / (float) maxHeight, 2));
            double leavesShrink = (1F - 0.45F * (height / (float) maxHeight));
            for (int width = -(int) Math.floor(trunkThickness / 2F); width < (int) Math.ceil(trunkThickness / 2F); width++) {
                for (int length = -(int) Math.floor(trunkThickness / 2F); length < (int) Math.ceil(trunkThickness / 2F); length++) {
                    BlockPos logPos = trunkRoot.offset(width, 0, length);
                    if (trunkRoot.distToLowCornerSqr(logPos.getX(), trunkRoot.getY(), logPos.getZ()) <= (trunkThickness * trunkThickness / 4D) * radShrink && canReplace(level.getBlockState(logPos))) {
                        level.setBlock(logPos, Blocks.JUNGLE_LOG.defaultBlockState(), 3);
                    }
                }
            }
            if (height == maxHeight || height > 3 && (height - lastLeavesY > 4 + randomsource.nextInt(2) || randomsource.nextInt(5) == 0)) {
                lastLeavesY = height;
                drawLeafOrb(level, trunkRoot.offset((int) ((randomsource.nextInt(2) - 1) * radShrink), 0, (int) ((randomsource.nextInt(2) - 1) * radShrink)), randomsource, ACBlockRegistry.ANCIENT_LEAVES.get().defaultBlockState(), (int) Math.ceil(4 * leavesShrink) + randomsource.nextInt(2), 1 + randomsource.nextInt(2), (int) Math.ceil(4 * leavesShrink) + randomsource.nextInt(2));
            }
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
                    if (fill.distToLowCornerSqr(center.getX(), center.getY(), center.getZ()) <= equalRadius * equalRadius - random.nextFloat() * 2) {
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
        return (state.isAir() || state.canBeReplaced() || state.is(ACBlockRegistry.ANCIENT_LEAVES.get()) || state.is(ACBlockRegistry.TREE_STAR.get())) && state.getFluidState().isEmpty() && !state.is(ACTagRegistry.UNMOVEABLE);
    }


}
