package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.MusselBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class VolcanoBoulderFeature  extends Feature<NoneFeatureConfiguration> {

    public VolcanoBoulderFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos blockpos = context.origin();
        WorldGenLevel worldgenlevel = context.level();
        RandomSource randomsource = context.random();

        for (; blockpos.getY() > worldgenlevel.getMinBuildHeight() + 1; blockpos = blockpos.below()) {
            if (!worldgenlevel.isEmptyBlock(blockpos.below())) {
                BlockState blockstate = worldgenlevel.getBlockState(blockpos.below());
                if (blockstate.is(ACBlockRegistry.FLOOD_BASALT.get())) {
                    break;
                }
            }
        }

        if (blockpos.getY() <= worldgenlevel.getMinBuildHeight() + 1 || blockpos.getY() > worldgenlevel.getMinBuildHeight() + 40) {
            return false;
        } else {
            blockpos = blockpos.above(1 + randomsource.nextInt(1));
            for (int l = 0; l < 3; ++l) {
                int i = randomsource.nextInt(3);
                int j = randomsource.nextInt(3);
                int k = randomsource.nextInt(3);
                float f = (float) (i + j + k) * 0.333F + 0.5F;
                double radius = (double) (f * f);
                for (BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-i, -j, -k), blockpos.offset(i, j, k))) {
                    if (blockpos1.distSqr(blockpos) <= radius && !worldgenlevel.getBlockState(blockpos1).is(ACTagRegistry.UNMOVEABLE)) {
                        worldgenlevel.setBlock(blockpos1, Blocks.SMOOTH_BASALT.defaultBlockState(), 2);
                    }
                }
                blockpos = blockpos.offset(-2 + randomsource.nextInt(4), -randomsource.nextInt(2), -2 + randomsource.nextInt(4));
            }

            return true;
        }
    }
}