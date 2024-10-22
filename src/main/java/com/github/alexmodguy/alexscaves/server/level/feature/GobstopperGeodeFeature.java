package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class GobstopperGeodeFeature extends Feature<NoneFeatureConfiguration> {

    private static final Block[] GRADIENT_BLOCKS = new Block[]{
            ACBlockRegistry.RED_ROCK_CANDY.get(),
            ACBlockRegistry.PURPLE_ROCK_CANDY.get(),
            ACBlockRegistry.CYAN_ROCK_CANDY.get(),
            ACBlockRegistry.LIME_ROCK_CANDY.get(),
            ACBlockRegistry.YELLOW_ROCK_CANDY.get(),
            ACBlockRegistry.ORANGE_ROCK_CANDY.get(),
    };

    public GobstopperGeodeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource randomSource = context.random();
        if (!level.getBlockState(pos).isAir()) {
            boolean eatenQuadX = randomSource.nextBoolean();
            boolean eatenQuadY = pos.getY() < -10;
            boolean eatenQuadZ = randomSource.nextBoolean();
            int width = 7;
            BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos();
            for(int x = -width; x <= width; x++){
                for(int y = -width; y <= width; y++){
                    for(int z = -width; z <= width; z++){
                        pos1.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        float sprinkleNoise = Math.abs(ACMath.sampleNoise3D(pos1.getX() + 231, pos1.getY() + 221, pos1.getZ() + 3211, 3.0F));
                        int exteriorShellBy = (int)Mth.absMax(x, Mth.absMax(y, z));
                        int taxicab = pos1.distManhattan(pos);
                        if((eatenQuadX ? x > 0 : x < 0) && (eatenQuadY ? y > 0 : y < 0) && (eatenQuadZ ? z > 0 : z < 0) || level.getBlockState(pos1).is(Blocks.BEDROCK)){
                            continue;
                        }else if(exteriorShellBy < width){
                            int gradientBy = (int)((exteriorShellBy / (float)width) * GRADIENT_BLOCKS.length);
                            BlockState gradientState = GRADIENT_BLOCKS[Mth.clamp(gradientBy, 0, GRADIENT_BLOCKS.length - 1)].defaultBlockState();
                            level.setBlock(pos1, gradientState, 3);
                        }else if(randomSource.nextFloat() < 0.95F && sprinkleNoise < 0.9 && taxicab < width * 3 - 1 && !(Math.abs(x) == Math.abs(y) && Math.abs(x) == width || Math.abs(x) == Math.abs(z) && Math.abs(x) == width || Math.abs(y) == Math.abs(z) && Math.abs(y) == width)){
                            BlockState exteriorState = ACBlockRegistry.WHITE_ROCK_CANDY.get().defaultBlockState();
                            if(randomSource.nextFloat() < 0.035F){
                                float color = randomSource.nextFloat();
                                if(color < 0.33F){
                                    exteriorState = ACBlockRegistry.LIGHT_BLUE_ROCK_CANDY.get().defaultBlockState();
                                }else if(color < 0.66F){
                                    exteriorState = ACBlockRegistry.PINK_ROCK_CANDY.get().defaultBlockState();
                                }else{
                                    exteriorState = ACBlockRegistry.YELLOW_ROCK_CANDY.get().defaultBlockState();
                                }
                            }
                            level.setBlock(pos1, exteriorState, 3);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}

