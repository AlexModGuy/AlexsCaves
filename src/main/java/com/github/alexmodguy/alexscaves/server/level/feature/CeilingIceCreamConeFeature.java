package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.AmbersolBlock;
import com.github.alexmodguy.alexscaves.server.block.IceCreamBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CeilingIceCreamConeFeature extends Feature<NoneFeatureConfiguration> {

    public CeilingIceCreamConeFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos blockpos;
        WorldGenLevel worldgenlevel = context.level();
        RandomSource randomsource = context.random();
        int iceCreamType = randomsource.nextInt(3);
        Block iceCreamBlock = iceCreamType == 1 ? ACBlockRegistry.CHOCOLATE_ICE_CREAM.get() : iceCreamType == 2 ? ACBlockRegistry.SWEETBERRY_ICE_CREAM.get() : ACBlockRegistry.VANILLA_ICE_CREAM.get();
        for (blockpos = context.origin(); blockpos.getY() >= worldgenlevel.getMaxBuildHeight() - 3; blockpos = blockpos.above()) {
            if (!worldgenlevel.isEmptyBlock(blockpos.above())) {
                break;
            }
        }
        if (blockpos.getY() >= worldgenlevel.getMaxBuildHeight() - 3) {
            return false;
        } else {
            BlockState stateAbove = worldgenlevel.getBlockState(blockpos.above());
            if(!stateAbove.is(ACBlockRegistry.CAKE_LAYER.get()) && !stateAbove.is(ACBlockRegistry.BLOCK_OF_CHOCOLATE.get())){
                return false;
            }
            int coneWidthRadius = 3 + randomsource.nextInt(3);
            int coneLength = coneWidthRadius + 8 + randomsource.nextInt(8);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int y = 5; y > -coneLength; y--) {
                boolean stairFlag = false;
                int shrinkIncrement = coneWidthRadius <= 1 ? 2 : coneLength / 3;
                if (((y - 1) % shrinkIncrement == 0 || coneLength - y < 2) && y < 0 || y == -1) {
                    stairFlag = true;
                }
                if(y == -1){
                    coneWidthRadius--;
                }
                for (int x = -coneWidthRadius; x <= coneWidthRadius; x++) {
                    for (int z = -coneWidthRadius; z <= coneWidthRadius; z++) {
                        pos.set(blockpos.getX() + x, blockpos.getY() + y, blockpos.getZ() + z);
                        double distXZ = pos.distToLowCornerSqr(blockpos.getX(), y > 1 ? blockpos.getY() : pos.getY(), blockpos.getZ());
                        double circleFat = coneWidthRadius * coneWidthRadius;
                        if (distXZ < circleFat) {
                            if (canReplace(worldgenlevel.getBlockState(pos))) {
                                boolean placeFlag = false;
                                if (y >= 0) {
                                    if(y != 5 || randomsource.nextBoolean()){
                                        worldgenlevel.setBlock(pos, iceCreamBlock.defaultBlockState().setValue(IceCreamBlock.TYPE, 2), 3);
                                    }
                                    placeFlag = true;
                                } else if(Math.sqrt(distXZ) >= coneWidthRadius - 1.0D){
                                    placeFlag = true;
                                    if(stairFlag && coneWidthRadius > 1){
                                        StairsShape shape = StairsShape.STRAIGHT;
                                        float angle = 180 - (float) (Mth.atan2(pos.getX() - blockpos.getX(), pos.getZ() - blockpos.getZ()) * (double) (180F / (float) Math.PI));
                                        Direction facing = Direction.fromYRot(angle);
                                        if(Mth.degreesDifferenceAbs(angle % 90, 45) < 10){
                                            shape = StairsShape.OUTER_LEFT;
                                        }
                                        worldgenlevel.setBlock(pos, ACBlockRegistry.WAFER_COOKIE_STAIRS.get().defaultBlockState().setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, shape).setValue(StairBlock.FACING, facing), 3);
                                    }else{
                                        worldgenlevel.setBlock(pos, ACBlockRegistry.WAFER_COOKIE_BLOCK.get().defaultBlockState(), 3);
                                    }
                                }

                                BlockPos above = pos.above();
                                if (placeFlag && worldgenlevel.getBlockState(above).is(iceCreamBlock)) {
                                    worldgenlevel.setBlock(above, iceCreamBlock.defaultBlockState().setValue(IceCreamBlock.TYPE, y >= 0 ? 0 : 1), 3);
                                }
                            }
                        }
                    }
                }
                if(stairFlag){
                    coneWidthRadius--;
                }
            }
            return true;
        }
    }

    private static boolean canReplace(BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }

}