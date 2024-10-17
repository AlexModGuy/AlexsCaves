package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.IceCreamBlock;
import com.github.alexmodguy.alexscaves.server.block.PeppermintBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SpilledIceCreamConeFeature extends Feature<NoneFeatureConfiguration> {

    public SpilledIceCreamConeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos genAt = context.origin();
        if (!level.getBlockState(genAt.below()).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get())) {
            return false;
        }
        int shapeType = 0;
        int iceCreamType = randomsource.nextInt(3);
        Direction randomDir = Util.getRandom(ACMath.HORIZONTAL_DIRECTIONS, randomsource);
        Block block = iceCreamType == 2 ? ACBlockRegistry.SWEETBERRY_ICE_CREAM.get() : iceCreamType == 1 ? ACBlockRegistry.CHOCOLATE_ICE_CREAM.get() : ACBlockRegistry.VANILLA_ICE_CREAM.get();
        if(randomsource.nextFloat() < 0.33F && hasClearance(level, genAt, randomDir, 2, false)){
            shapeType = 1;
        }else if(randomsource.nextFloat() < 0.66F && hasClearance(level, genAt, randomDir, 3, true)){
            shapeType = 2;
        }
        level.setBlock(genAt, block.defaultBlockState().setValue(IceCreamBlock.TYPE, 1), 3);
        switch (shapeType){
            case 0:
                level.setBlock(genAt.above(), ACBlockRegistry.WAFER_COOKIE_BLOCK.get().defaultBlockState(), 3);
                level.setBlock(genAt.above(2), ACBlockRegistry.WAFER_COOKIE_WALL.get().defaultBlockState(), 3);
                break;
            case 1:
                level.setBlock(genAt.relative(randomDir), ACBlockRegistry.WAFER_COOKIE_BLOCK.get().defaultBlockState(), 3);
                level.setBlock(genAt.relative(randomDir, 2), ACBlockRegistry.WAFER_COOKIE_STAIRS.get().defaultBlockState().setValue(StairBlock.FACING, randomDir.getOpposite()), 3);
                break;
            case 2:
                level.setBlock(genAt.relative(randomDir), ACBlockRegistry.WAFER_COOKIE_STAIRS.get().defaultBlockState().setValue(StairBlock.FACING, randomDir.getOpposite()).setValue(StairBlock.HALF, Half.TOP), 3);
                level.setBlock(genAt.relative(randomDir).above(), ACBlockRegistry.WAFER_COOKIE_STAIRS.get().defaultBlockState().setValue(StairBlock.FACING, randomDir), 3);
                level.setBlock(genAt.relative(randomDir, 2).above(), ACBlockRegistry.WAFER_COOKIE_STAIRS.get().defaultBlockState().setValue(StairBlock.FACING, randomDir.getOpposite()).setValue(StairBlock.HALF, Half.TOP), 3);
                break;
        }
        return true;
    }

    private boolean hasClearance(WorldGenLevel level, BlockPos pos, Direction direction, int amount, boolean stepUp){
        boolean clear = true;
        int i = 1;
        while(i <= amount){
            if(!level.getBlockState(pos.relative(direction, i).above(stepUp ? i / 2 : 0)).canBeReplaced()){
                clear = false;
                break;
            }
            i++;
        }
        return clear;
    }
}
