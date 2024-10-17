package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.PeppermintBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PeppermintPileFeature extends Feature<NoneFeatureConfiguration> {

    public PeppermintPileFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos below = context.origin();
        if (!level.getBlockState(below.below()).isSolid()) {
            return false;
        }
        BlockPos.MutableBlockPos pillar = new BlockPos.MutableBlockPos();
        pillar.set(below);
        for (int i = 0; i < 4 + randomsource.nextInt(6); i++) {
            BlockPos offset = below.offset(randomsource.nextInt(6) - 3, 1, randomsource.nextInt(6) - 3);
            while (level.isEmptyBlock(offset) && offset.getY() > level.getMinBuildHeight()) {
                offset = offset.below();
            }
            if (level.getBlockState(offset).isFaceSturdy(level, offset, Direction.UP) && level.getBlockState(offset).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get()) && level.isEmptyBlock(offset.above())) {
                BlockState randomState;
                float f = randomsource.nextFloat();
                if (f < 0.1F) {
                    randomState = ACBlockRegistry.LICOROOT_SPROUT.get().defaultBlockState();
                } else if (f < 0.6F) {
                    randomState = ACBlockRegistry.SMALL_PEPPERMINT.get().defaultBlockState().setValue(PeppermintBlock.FACING, Direction.UP);
                } else {
                    randomState = ACBlockRegistry.LARGE_PEPPERMINT.get().defaultBlockState().setValue(PeppermintBlock.FACING, Direction.UP);
                }
                level.setBlock(offset.above(), randomState, 3);

            }
        }
        return true;
    }
}
