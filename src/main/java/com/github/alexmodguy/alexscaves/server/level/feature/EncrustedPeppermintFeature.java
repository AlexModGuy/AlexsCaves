package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.MusselBlock;
import com.github.alexmodguy.alexscaves.server.block.PeppermintBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class EncrustedPeppermintFeature extends Feature<NoneFeatureConfiguration> {

    public EncrustedPeppermintFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource randomSource = context.random();
        return tryPlacePeppermint(level, pos, randomSource);
    }

    private boolean tryPlacePeppermint(WorldGenLevel level, BlockPos pos, RandomSource randomSource) {
        if (level.getBlockState(pos).isAir()) {
            List<Direction> possiblities = new ArrayList<>();
            for (Direction possible : Direction.values()) {
                BlockPos check = pos.relative(possible);
                if (isSameChunk(pos, check) && level.getBlockState(check).is(ACBlockRegistry.CAKE_LAYER.get())) {
                    possiblities.add(possible.getOpposite());
                }
            }
            Direction direction = selectDirection(possiblities, randomSource);
            if (direction != null) {
                Block peppermint = randomSource.nextFloat() > 0.7F ? ACBlockRegistry.LARGE_PEPPERMINT.get() : ACBlockRegistry.SMALL_PEPPERMINT.get();
                level.setBlock(pos, peppermint.defaultBlockState().setValue(PeppermintBlock.FACING, direction), 3);
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
