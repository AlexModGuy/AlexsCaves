package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.CycadBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CycadFeature extends Feature<NoneFeatureConfiguration> {

    public CycadFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos treeBottom = context.origin();
        if (!level.getBlockState(treeBottom.below()).is(BlockTags.DIRT)) {
            return false;
        }
        int height = 1 + (int) Math.ceil(randomsource.nextFloat() * 2.5F);
        for (int i = 0; i <= height; i++) {
            BlockPos trunk = treeBottom.above(i);
            if (canReplace(level.getBlockState(trunk))) {
                level.setBlock(trunk, ACBlockRegistry.CYCAD.get().defaultBlockState().setValue(CycadBlock.TOP, i == height), 2);
            }
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return (state.isAir() || state.canBeReplaced()) && !state.is(ACTagRegistry.UNMOVEABLE) && state.getFluidState().isEmpty();
    }
}
