package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.GiantSweetberryBlock;
import com.github.alexmodguy.alexscaves.server.block.IceCreamBlock;
import com.github.alexmodguy.alexscaves.server.level.feature.config.IceCreamScoopFeatureConfiguration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class IceCreamScoopFeature extends Feature<IceCreamScoopFeatureConfiguration> {

    public IceCreamScoopFeature(Codec<IceCreamScoopFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<IceCreamScoopFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos genAt = context.origin();
        BlockState belowState = level.getBlockState(genAt);
        BlockState ourIceCream = context.config().iceCreamBlock.getState(randomsource, genAt);
        int pileHeight = 6 + randomsource.nextInt(3);
        int pileWidth = 2 + randomsource.nextInt(3);
        if(belowState.getBlock() instanceof IceCreamBlock){
            pileHeight -= 2;
            pileWidth -= randomsource.nextInt(1);
        }else if (!belowState.is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get()) || belowState.is(ourIceCream.getBlock())) {
            return false;
        }
        long xzSeed = Mth.getSeed(genAt.getX(), 0, genAt.getZ());
        int xOffset = (int) Mth.clamp(((double)((float)(xzSeed & 15L) / 15.0F) - 0.5D) * 6, (double)(-3), (double)3);
        int zOffset = (int) Mth.clamp(((double)((float)(xzSeed >> 8 & 15L) / 15.0F) - 0.5D) * 6, (double)(-3), (double)3);
        genAt = genAt.offset(xOffset, 2, zOffset);
        double circleFat = Math.pow(pileWidth, 2.5D);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -pileWidth; x <= pileWidth; x++) {
            for (int z = -pileWidth; z <= pileWidth; z++) {
                int yAdd = pileHeight;
                int minFall = -3 - randomsource.nextInt(5);
                pos.set(genAt.getX() + x, genAt.getY() + yAdd, genAt.getZ() + z);
                BlockState lastState = null;
                while(yAdd > 0 || level.getBlockState(pos.below()).canBeReplaced() && pos.getY() > level.getMinBuildHeight() && yAdd > minFall){
                    pos.set(genAt.getX() + x, genAt.getY() + yAdd, genAt.getZ() + z);
                    double dist = yAdd >= 0 ? pos.distToLowCornerSqr(genAt.getX(), genAt.getY(), genAt.getZ()) : pos.distToLowCornerSqr(genAt.getX(), pos.getY(), genAt.getZ());
                    BlockState replacingState = level.getBlockState(pos);
                    if((replacingState.canBeReplaced() || replacingState.is(ACBlockRegistry.GIANT_SWEETBERRY.get())) && dist < circleFat){
                        level.setBlock(pos, lastState = context.config().iceCreamBlock.getState(randomsource, pos), 3);
                    }
                    yAdd--;
                }
                BlockState bottomState = level.getBlockState(pos);
                if(bottomState.getBlock() instanceof IceCreamBlock){
                    int bottomType = 0;
                    if(lastState != null){
                        BlockState beneathState = level.getBlockState(pos.below());
                        if(beneathState.is(lastState.getBlock())){
                            bottomType = 0;
                        }else if(beneathState.isFaceSturdy(level, pos.below(), Direction.UP)){
                            bottomType = 1;
                        }else{
                            bottomType = 2;
                        }
                    }
                    level.setBlock(pos, bottomState.setValue(IceCreamBlock.TYPE, bottomType), 3);
                }
            }
        }
        if(randomsource.nextFloat() < 0.5F){
            pos.set(genAt.getX(), genAt.getY() - 6, genAt.getZ());
            while (level.getBlockState(pos).getBlock() instanceof IceCreamBlock && pos.getY() < level.getMaxBuildHeight() || pos.getY() < genAt.getY()){
                boolean iceCreamOn = level.getBlockState(pos).getBlock() instanceof IceCreamBlock;
                pos.move(0, 1, 0);
                if(iceCreamOn && !(level.getBlockState(pos).getBlock() instanceof IceCreamBlock)){
                    break;
                }
            }
            level.setBlock(pos, ACBlockRegistry.GIANT_SWEETBERRY.get().defaultBlockState().setValue(GiantSweetberryBlock.ROTATION, randomsource.nextInt(7)), 3);
        }
        return true;
    }
}

