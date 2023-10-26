package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.PingPongSpongeBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PingPongSpongeFeature extends Feature<NoneFeatureConfiguration> {

    public PingPongSpongeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos trenchBottom = new BlockPos.MutableBlockPos();
        trenchBottom.set(context.origin());
        while (!level.getBlockState(trenchBottom).getFluidState().isEmpty() && trenchBottom.getY() > level.getMinBuildHeight()) {
            trenchBottom.move(0, -1, 0);
        }
        if (!level.getBlockState(trenchBottom.below()).is(ACBlockRegistry.MUCK.get()) || context.origin().getY() - trenchBottom.getY() < 15) {
            return false;
        }
        int height = (int) Math.ceil(randomsource.nextFloat() * 3.5F);
        BlockPos genAt = trenchBottom.immutable();
        for (int i = 0; i <= height; i++) {
            BlockPos trunk = genAt.above(i);
            if (canReplace(level.getBlockState(trunk))) {
                level.setBlock(trunk, ACBlockRegistry.PING_PONG_SPONGE.get().defaultBlockState().setValue(PingPongSpongeBlock.TOP, i == height), 2);
            }
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return !state.is(ACTagRegistry.UNMOVEABLE) && state.getFluidState().is(FluidTags.WATER);
    }
}
