package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.PeppermintBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CookieShelfFeature  extends Feature<NoneFeatureConfiguration> {

    public CookieShelfFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource randomSource = context.random();
        boolean placedFirst = tryPlaceCookie(level, pos, randomSource);
        if(placedFirst && randomSource.nextBoolean()){
            tryPlaceCookie(level, pos.above(2 + randomSource.nextInt(3)), randomSource);
        }
        if(placedFirst && randomSource.nextBoolean()){
            tryPlaceCookie(level, pos.below(2 + randomSource.nextInt(3)), randomSource);
        }
        return placedFirst;
    }

    private boolean tryPlaceCookie(WorldGenLevel level, BlockPos pos, RandomSource randomSource) {
        if (level.getBlockState(pos).isAir()) {
            List<Direction> possiblities = new ArrayList<>();
            for (Direction possible : ACMath.HORIZONTAL_DIRECTIONS) {
                BlockPos check = pos.relative(possible);
                BlockState checkState = level.getBlockState(check);
                if (isSameChunk(pos, check) && (checkState.is(ACBlockRegistry.CAKE_LAYER.get()) || checkState.is(ACBlockRegistry.BLOCK_OF_CHOCOLATE.get()))) {
                    possiblities.add(possible.getOpposite());
                }
            }
            Direction direction = selectDirection(possiblities, randomSource);
            if (direction != null) {
                int cookieRadius = 4 + randomSource.nextInt(3);
                BlockPos offsetBy = pos.relative(direction.getOpposite(), cookieRadius / 3);
                double cookieRadiusSq = Math.pow(cookieRadius, 1.5F);
                for (BlockPos blockpos1 : BlockPos.betweenClosed(offsetBy.offset(-cookieRadius, 0, -cookieRadius), offsetBy.offset(cookieRadius, 0, cookieRadius))) {
                    if (blockpos1.distSqr(offsetBy) <= cookieRadiusSq && !level.getBlockState(blockpos1).is(ACTagRegistry.UNMOVEABLE) && Math.abs(blockpos1.getX() - offsetBy.getX()) != cookieRadius && Math.abs(blockpos1.getZ() - offsetBy.getZ()) != cookieRadius) {
                        level.setBlock(blockpos1, ACBlockRegistry.COOKIE_BLOCK.get().defaultBlockState(), 3);
                    }
                }
                return true;
            }
        }
        return false;
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

