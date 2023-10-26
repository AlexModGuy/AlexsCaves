package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.NeodymiumNodeBlock;
import com.github.alexmodguy.alexscaves.server.block.NeodymiumPillarBlock;
import com.github.alexmodguy.alexscaves.server.level.feature.config.MagneticNodeFeatureConfiguration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class MagneticNodeFeature extends Feature<MagneticNodeFeatureConfiguration> {

    public MagneticNodeFeature(Codec<MagneticNodeFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<MagneticNodeFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource randomSource = context.random();
        if (canReplace(level.getBlockState(pos))) {
            List<Direction> possiblities = new ArrayList<>();
            for (Direction possible : Direction.values()) {
                BlockPos check = pos.relative(possible);
                if (isSameChunk(pos, check) && level.getBlockState(check).isFaceSturdy(level, check, possible.getOpposite())) {
                    possiblities.add(possible.getOpposite());
                }
            }
            Direction direction = selectDirection(possiblities, randomSource);
            if (direction != null) {
                int centerHeight = 3 + randomSource.nextInt(3);
                generatePillar(level, pos, context.config().pillarBlock, context.config().nodeBlock, direction, randomSource, centerHeight);
                int pillarSpread = 2;
                Vec3i inverseVec = new Vec3i(1, 1, 1).subtract(new Vec3i(Math.abs(direction.getStepX()), Math.abs(direction.getStepY()), Math.abs(direction.getStepZ())));
                for (int pillar = 0; pillar < 2 + randomSource.nextInt(3); pillar++) {
                    BlockPos genAt = pos.offset((randomSource.nextInt(pillarSpread * 2) - pillarSpread) * inverseVec.getX(), (randomSource.nextInt(pillarSpread * 2) - pillarSpread) * inverseVec.getY(), (randomSource.nextInt(pillarSpread * 2) - pillarSpread) * inverseVec.getZ());
                    if (genAt.distManhattan(pos) > 0) {
                        int pillarHeight = (int) Math.max(centerHeight - genAt.distManhattan(pos) - randomSource.nextInt(2), 1) + 2;
                        generatePillar(level, genAt, context.config().pillarBlock, context.config().nodeBlock, direction, randomSource, pillarHeight);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean canReplace(BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }

    private static void generatePillar(WorldGenLevel level, BlockPos pos, BlockStateProvider pillarState, BlockStateProvider nodeState, Direction facing, RandomSource randomSource, int height) {
        BlockPos begin = pos;
        int j = 0;
        while (j < 5 && canReplace(level.getBlockState(begin))) {
            j++;
            begin = begin.relative(facing.getOpposite());
        }
        BlockPos stopPillarAt = pos.relative(facing, height);
        while (!begin.equals(stopPillarAt)) {
            begin = begin.relative(facing);
            if (canReplace(level.getBlockState(begin))) {
                placeBlock(level, begin, pillarState, facing, randomSource, false);
            }
            if (randomSource.nextInt(3) == 0) {
                Direction randomOffset = Direction.getRandom(randomSource);
                if (randomOffset.getAxis() != facing.getAxis()) {
                    if (canReplace(level.getBlockState(begin.relative(randomOffset)))) {
                        BlockPos randomPos = begin.relative(randomOffset);
                        placeBlock(level, randomPos, nodeState, randomOffset, randomSource, level.getFluidState(randomPos).is(Fluids.WATER));
                    }
                }
            }
        }
        placeBlock(level, begin, pillarState, facing, randomSource, true);
        BlockPos relative = begin.relative(facing);
        if (canReplace(level.getBlockState(relative))) {
            placeBlock(level, relative, nodeState, facing, randomSource, level.getFluidState(relative).is(Fluids.WATER));
        }
    }

    private static void placeBlock(WorldGenLevel level, BlockPos pos, BlockStateProvider blockState, Direction facing, RandomSource randomSource, boolean flag) {
        BlockState state = blockState.getState(randomSource, pos);
        if (state.getBlock() instanceof NeodymiumNodeBlock) {
            state = state.setValue(NeodymiumNodeBlock.FACING, facing).setValue(NeodymiumNodeBlock.WATERLOGGED, flag);

        } else if (state.getBlock() instanceof NeodymiumPillarBlock) {
            state = state.setValue(NeodymiumPillarBlock.FACING, facing).setValue(NeodymiumPillarBlock.TOP, flag);
        }
        level.setBlock(pos, state, 3);

    }

    private boolean isSameChunk(BlockPos pos, BlockPos check) {
        return SectionPos.blockToSectionCoord(pos.getX()) == SectionPos.blockToSectionCoord(check.getX()) && SectionPos.blockToSectionCoord(pos.getZ()) == SectionPos.blockToSectionCoord(check.getZ());
    }

    private static Direction selectDirection(List<Direction> directionList, RandomSource randomSource) {
        if (directionList.size() <= 0) {
            return null;
        } else if (directionList.size() <= 1) {
            return directionList.get(0);
        } else {
            return directionList.get(randomSource.nextInt(directionList.size() - 1));
        }
    }
}
