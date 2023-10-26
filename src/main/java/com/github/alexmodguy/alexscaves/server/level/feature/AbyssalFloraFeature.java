package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.level.feature.config.AbyssalFloraFeatureConfiguration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class AbyssalFloraFeature extends Feature<AbyssalFloraFeatureConfiguration> {

    public AbyssalFloraFeature(Codec<AbyssalFloraFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<AbyssalFloraFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos trenchBottom = new BlockPos.MutableBlockPos();
        trenchBottom.set(context.origin());
        while (!level.getBlockState(trenchBottom).getFluidState().isEmpty() && trenchBottom.getY() > level.getMinBuildHeight()) {
            trenchBottom.move(0, -1, 0);
        }
        if (context.origin().getY() - trenchBottom.getY() < 15) {
            return false;
        }
        BlockPos above = trenchBottom.above();
        if (canReplace(level.getBlockState(above))) {
            level.setBlock(above, context.config().floraBlock.getState(randomsource, above), 2);
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return state.getFluidState().is(FluidTags.WATER) && state.liquid();
    }
}
