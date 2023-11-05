package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.block.MultipleDinosaurEggsBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SubterranodonRoostFeature extends Feature<NoneFeatureConfiguration> {

    public SubterranodonRoostFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource randomSource = context.random();
        Direction direction = getCliffDirection(level, pos, randomSource);
        if (direction != null) {
            int centerLength = 2 + randomSource.nextInt(1);
            int maxLeft = 2 + randomSource.nextInt(5);
            int maxRight = 2 + randomSource.nextInt(5);
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos mutableCopy = new BlockPos.MutableBlockPos();
            mutableBlockPos.set(pos);
            mutableBlockPos.move(direction.getCounterClockWise(), maxRight);
            float radius = (maxLeft + maxRight) / 2F;
            for (int i = -maxRight; i < maxLeft; i++) {
                float dist = Math.abs(i) / radius;
                mutableBlockPos.move(direction.getClockWise());
                mutableCopy.set(mutableBlockPos);
                int length = (int) Math.round(centerLength * Math.sqrt(1 - dist));
                for (int j = 0; j < length + 1; j++) {
                    fillCliff(level, mutableBlockPos, direction, length, j == 0, randomSource);
                    mutableBlockPos.set(mutableCopy);
                    mutableBlockPos.move(0, -j, 0);
                    mutableBlockPos.move(direction.getOpposite(), j);
                }
                mutableBlockPos.set(mutableCopy);
            }
            return true;
        }
        return false;
    }

    private void fillCliff(WorldGenLevel level, BlockPos.MutableBlockPos cliff, Direction direction, float length, boolean decorate, RandomSource randomSource) {
        int distBack = 0;
        int maxBack = (int) (Math.ceil(length) + 1);
        while (level.getBlockState(cliff).isAir() && distBack < maxBack) {
            distBack++;
            cliff.move(direction.getOpposite());
        }
        BlockState behind = level.getBlockState(cliff);

        BlockState set = behind.is(Blocks.SANDSTONE) ? Blocks.SANDSTONE.defaultBlockState() : ACBlockRegistry.LIMESTONE.get().defaultBlockState();
        cliff.move(direction);
        for (int i = 0; i < Math.ceil(length); i++) {
            if (level.getBlockState(cliff).isAir()) {
                if (decorate && randomSource.nextInt(3) == 0 && i == 0) {
                    level.setBlock(cliff, set, 3);
                    level.setBlock(cliff.above(), ACBlockRegistry.FERN_THATCH.get().defaultBlockState(), 3);
                } else if (decorate && randomSource.nextInt(7) == 0) {
                    BlockPos immutable = cliff.immutable();
                    level.setBlock(immutable.below(), set, 3);
                    for (Direction direction1 : ACMath.HORIZONTAL_DIRECTIONS) {
                        BlockPos corner = immutable.relative(direction1);
                        level.setBlock(corner, ACBlockRegistry.FERN_THATCH.get().defaultBlockState(), 3);
                        level.setBlock(corner.below(), set, 3);
                    }
                    level.setBlock(immutable, ACBlockRegistry.FERN_THATCH.get().defaultBlockState(), 3);
                    level.setBlock(immutable.above(), ACBlockRegistry.SUBTERRANODON_EGG.get().defaultBlockState().setValue(MultipleDinosaurEggsBlock.EGGS, 1 + randomSource.nextInt(3)).setValue(DinosaurEggBlock.NEEDS_PLAYER, true), 3);
                    Vec3 spawnMobAt = Vec3.atCenterOf(immutable.relative(direction).above());
                    SubterranodonEntity subterranodon = ACEntityRegistry.SUBTERRANODON.get().create(level.getLevel());
                    subterranodon.setPos(spawnMobAt);
                    subterranodon.restrictTo(immutable.above(), 20 + randomSource.nextInt(20));
                    if (!level.collidesWithSuffocatingBlock(subterranodon, subterranodon.getBoundingBox())) {
                        level.addFreshEntity(subterranodon);
                    }
                } else {
                    level.setBlock(cliff, set, 3);
                    if (decorate && randomSource.nextInt(5) == 0) {
                        level.setBlock(cliff.above(), Blocks.MOSS_CARPET.defaultBlockState(), 3);
                    }
                }
            }
            cliff.move(direction);
        }
    }

    @Nullable
    private Direction getCliffDirection(WorldGenLevel level, BlockPos pos, RandomSource randomSource) {
        if (!level.getBlockState(pos.below()).isAir() || !level.getBlockState(pos.above()).isAir()) {
            return null;
        }
        List<Direction> possiblities = new ArrayList<>();
        for (Direction possible : ACMath.HORIZONTAL_DIRECTIONS) {
            BlockPos check = pos.relative(possible);
            if (isSameChunk(pos, check)) {
                BlockState cliffState = level.getBlockState(check);
                if(cliffState.isFaceSturdy(level, check, possible.getOpposite()) && !cliffState.is(ACTagRegistry.VOLCANO_BLOCKS)){
                    possiblities.add(possible.getOpposite());
                }
            }
        }
        return selectDirection(possiblities, randomSource);
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

