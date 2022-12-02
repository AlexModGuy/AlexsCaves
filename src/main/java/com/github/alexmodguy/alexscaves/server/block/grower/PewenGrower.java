package com.github.alexmodguy.alexscaves.server.block.grower;

import com.github.alexmodguy.alexscaves.server.level.feature.ACConfiguredFeatureRegistry;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class PewenGrower extends AbstractTreeGrower {

    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean b) {
        return ACConfiguredFeatureRegistry.PEWEN_TREE_FROM_SAPLING.getHolder().get();
    }
}
