package com.github.alexmodguy.alexscaves.server.block.grower;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class LicorootGrower extends AbstractTreeGrower {

    public static final ResourceKey<ConfiguredFeature<?, ?>> LICOROOT_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "licoroot_tree"));

    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean b) {
        return LICOROOT_TREE;
    }
}
