package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.ThornwoodBranchBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class ThornwoodTreeFeature extends Feature<NoneFeatureConfiguration> {

    public ThornwoodTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos treeGround = context.origin();
        int centerAboveGround = randomsource.nextInt(5);
        int height = centerAboveGround + 4 + randomsource.nextInt(5);
        if (!checkCanTreePlace(level, treeGround, height)) {
            return false;
        }
        BlockPos centerPos = treeGround.above(centerAboveGround);
        if (centerAboveGround > 0) {
            int rootCount = 0;
            for (Direction direction : ACMath.HORIZONTAL_DIRECTIONS) {
                if (rootCount <= 3 + randomsource.nextInt(1)) {
                    generateRoot(level, centerPos.relative(direction), 0.25F, randomsource, direction, centerAboveGround + 1 + randomsource.nextInt(6));
                    rootCount++;
                }
            }
        }
        BlockPos.MutableBlockPos trunkPos = new BlockPos.MutableBlockPos();
        int i = 0;
        trunkPos.set(centerPos);
        trunkPos.move(0, -1, 0);
        int tallPart = height - centerAboveGround;
        while (i < tallPart) {
            i++;
            trunkPos.move(0, 1, 0);
            if (randomsource.nextInt(5) == 0) {
                level.setBlock(trunkPos, ACBlockRegistry.THORNWOOD_WOOD.get().defaultBlockState(), 3);
                trunkPos.move(Util.getRandom(ACMath.HORIZONTAL_DIRECTIONS, randomsource));
                level.setBlock(trunkPos, ACBlockRegistry.THORNWOOD_WOOD.get().defaultBlockState(), 3);
            } else {
                level.setBlock(trunkPos, i == tallPart ? ACBlockRegistry.THORNWOOD_WOOD.get().defaultBlockState() : ACBlockRegistry.THORNWOOD_LOG.get().defaultBlockState(), 3);
            }
            decorateLog(level, trunkPos, randomsource, true);
        }
        BlockPos canopy = trunkPos.immutable();
        BlockPos.MutableBlockPos canopyLogPos = new BlockPos.MutableBlockPos();
        for (Direction direction : ACMath.HORIZONTAL_DIRECTIONS) {
            canopyLogPos.set(canopy);
            int canopyLength = 1 + randomsource.nextInt(3);
            for (int j = 1; j <= canopyLength; j++) {
                boolean upFlag = false;
                canopyLogPos.move(direction);
                if (randomsource.nextInt(2) != 0) {
                    upFlag = true;
                    level.setBlock(canopyLogPos, ACBlockRegistry.THORNWOOD_WOOD.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction.getAxis()), 3);
                    canopyLogPos.move(0, 1, 0);
                }
                if (j == canopyLength && level.getBlockState(canopyLogPos).canBeReplaced()) {
                    level.setBlock(canopyLogPos, ACBlockRegistry.THORNWOOD_BRANCH.get().defaultBlockState().setValue(ThornwoodBranchBlock.FACING, upFlag ? Direction.UP : direction).setValue(ThornwoodBranchBlock.WATERLOGGED, level.getFluidState(canopyLogPos).is(Fluids.WATER)), 3);
                } else {
                    Block wood = j == canopyLength - 1 || upFlag ? ACBlockRegistry.THORNWOOD_WOOD.get() : ACBlockRegistry.THORNWOOD_LOG.get();
                    level.setBlock(canopyLogPos, wood.defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction.getAxis()), 3);
                    decorateLog(level, trunkPos, randomsource, true);
                }

            }
        }
        return true;
    }

    private boolean checkCanTreePlace(WorldGenLevel level, BlockPos treeBottom, int height) {
        BlockState below = level.getBlockState(treeBottom.below());
        if (!below.is(BlockTags.DIRT) && !below.is(ACBlockRegistry.GUANOSTONE.get()) && !below.is(ACBlockRegistry.COPROLITH.get()) && !below.is(Blocks.PACKED_MUD)) {
            return false;
        }
        for (int i = 0; i < height; i++) {
            if (!canReplace(level.getBlockState(treeBottom.above(i)))) {
                return false;
            }
        }
        BlockPos treeTop = treeBottom.above(height).immutable();
        for (BlockPos checkLeaf : BlockPos.betweenClosed(treeTop.offset(-2, -1, -2), treeTop.offset(2, 1, 2))) {
            if (!canReplace(level.getBlockState(checkLeaf))) {
                return false;
            }
        }
        return true;
    }

    protected static void decorateLog(WorldGenLevel level, BlockPos from, RandomSource random, boolean logBranches) {
        if (random.nextFloat() < 0.65F) {
            Direction ranDir = Util.getRandom(Direction.values(), random);
            BlockPos branchPos = from.immutable().relative(ranDir);
            if (level.getBlockState(branchPos).canBeReplaced()) {
                if (logBranches && random.nextFloat() < 0.4F) {
                    int bigBranchLength = 1 + random.nextInt(1);
                    for (int i = 0; i < bigBranchLength; i++) {
                        Block wood = i == bigBranchLength - 1 ? ACBlockRegistry.THORNWOOD_WOOD.get() : ACBlockRegistry.THORNWOOD_LOG.get();
                        level.setBlock(branchPos, wood.defaultBlockState().setValue(RotatedPillarBlock.AXIS, ranDir.getAxis()), 3);
                        branchPos = branchPos.relative(ranDir);
                    }
                }
                level.setBlock(branchPos, ACBlockRegistry.THORNWOOD_BRANCH.get().defaultBlockState().setValue(ThornwoodBranchBlock.FACING, ranDir).setValue(ThornwoodBranchBlock.WATERLOGGED, level.getFluidState(branchPos).is(Fluids.WATER)), 3);
            }
        }
    }

    public static void generateRoot(WorldGenLevel level, BlockPos from, float bendChance, RandomSource random, Direction direction, int length) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        at.set(from);
        int i = 0;
        while (i < length) {
            if (level.getBlockState(at).is(ACTagRegistry.UNMOVEABLE)) {
                return;
            }
            if (random.nextFloat() < bendChance) {
                if (!level.getBlockState(at).is(ACBlockRegistry.THORNWOOD_WOOD.get())) {
                    level.setBlock(at, ACBlockRegistry.THORNWOOD_WOOD.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction.getAxis()), 3);
                }
                at.move(0, -1, 0);
                at.move(direction);
                level.setBlock(at, ACBlockRegistry.THORNWOOD_WOOD.get().defaultBlockState(), 3);
            } else {
                at.move(0, -1, 0);
                level.setBlock(at, i == 0 ? ACBlockRegistry.THORNWOOD_WOOD.get().defaultBlockState() : ACBlockRegistry.THORNWOOD_LOG.get().defaultBlockState(), 3);
            }
            decorateLog(level, at, random, false);
            i++;
        }
        BlockPos rootPos = at.immutable().below();
        if (level.getBlockState(rootPos).canBeReplaced()) {
            level.setBlock(rootPos, ACBlockRegistry.THORNWOOD_BRANCH.get().defaultBlockState().setValue(ThornwoodBranchBlock.FACING, Direction.DOWN).setValue(ThornwoodBranchBlock.WATERLOGGED, level.getFluidState(rootPos).is(Fluids.WATER)), 3);
        }
    }

    private static boolean canReplace(BlockState state) {
        return (state.isAir() || state.canBeReplaced() || state.is(ACBlockRegistry.THORNWOOD_BRANCH.get()) || state.is(BlockTags.DIRT) || state.is(Blocks.PACKED_MUD)) && !state.is(ACTagRegistry.UNMOVEABLE);
    }
}
