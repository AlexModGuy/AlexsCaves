package com.github.alexmodguy.alexscaves.server.level.feature;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ACConfiguredFeatureRegistry {

    public static final DeferredRegister<ConfiguredFeature<?, ?>> DEF_REG = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, AlexsCaves.MODID);

    public static final RegistryObject<ConfiguredFeature<?, ?>> PEWEN_TREE_FROM_SAPLING = DEF_REG.register("pewen_tree_from_sapling", () -> new ConfiguredFeature<>(ACFeatureRegistry.PEWEN_TREE.get(), NoneFeatureConfiguration.INSTANCE));

}
