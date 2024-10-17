package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.*;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CandyCaneFeature extends Feature<NoneFeatureConfiguration> {

    public CandyCaneFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos genAt = context.origin();
        if (!level.getBlockState(genAt).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get())) {
            return false;
        }
        genAt = genAt.above();
        Direction randomDir = Util.getRandom(ACMath.HORIZONTAL_DIRECTIONS, randomsource);
        int height = 3 + randomsource.nextInt(3);
        if(hasClearance(level, genAt, randomDir, height)){
            for(int i = 0; i < height; i++){
                level.setBlock(genAt.above(i), ACBlockRegistry.CANDY_CANE_POLE.get().defaultBlockState(), 3);
            }
            level.setBlock(genAt.above(height), ACBlockRegistry.CANDY_CANE_POLE.get().defaultBlockState().setValue(CandyCanePoleBlock.getPropertyByDirection(randomDir), true), 3);
            level.setBlock(genAt.above(height).relative(randomDir), ACBlockRegistry.CANDY_CANE_POLE.get().defaultBlockState().setValue(CandyCanePoleBlock.getPropertyByDirection(randomDir.getOpposite()), true), 3);
            for (int i = 0; i < 6 + randomsource.nextInt(7); i++) {
                BlockPos offset = genAt.offset(randomsource.nextInt(8) - 4, 2, randomsource.nextInt(8) - 4);
                while (level.isEmptyBlock(offset) && offset.getY() > level.getMinBuildHeight()) {
                    offset = offset.below();
                }
                if (level.getBlockState(offset).isFaceSturdy(level, offset, Direction.UP) && level.getBlockState(offset).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get()) && level.isEmptyBlock(offset.above())) {
                    int count = Mth.clamp(1 + randomsource.nextInt(4), 1, 4);
                    BlockState randomState = ACBlockRegistry.CANDY_CANE.get().defaultBlockState().setValue(SmallCandyCaneBlock.FACING, Util.getRandom(ACMath.HORIZONTAL_DIRECTIONS, randomsource)).setValue(SmallCandyCaneBlock.COUNT, count);
                    level.setBlock(offset.above(), randomState, 3);
                }
            }
            return true;
        }
        return false;
    }

    private boolean hasClearance(WorldGenLevel level, BlockPos pos, Direction direction, int height){
        int i = 0;
        while(i <= height){
            if(!level.getBlockState(pos.relative(direction, i).above(height)).canBeReplaced()){
                return false;
            }
            i++;
        }
        return level.getBlockState(pos.relative(direction, height - 1).above(height)).canBeReplaced() && level.getBlockState(pos.relative(direction, height).above(height)).canBeReplaced();
    }
}
