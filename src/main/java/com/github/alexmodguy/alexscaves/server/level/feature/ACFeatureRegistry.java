package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.feature.config.*;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACFeatureRegistry {
    public static final DeferredRegister<Feature<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.FEATURES, AlexsCaves.MODID);

    public static final RegistryObject<Feature<GalenaHexagonFeatureConfiguration>> GALENA_HEXAGON = DEF_REG.register("galena_hexagon", () -> new GalenaHexagonFeature(GalenaHexagonFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<MagneticNodeFeatureConfiguration>> MAGNETIC_NODE = DEF_REG.register("magnetic_node", () -> new MagneticNodeFeature(MagneticNodeFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<MagneticRuinsFeatureConfiguration>> MAGNETIC_RUINS = DEF_REG.register("magnetic_ruins", () -> new MagneticRuinsFeature(MagneticRuinsFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<FloatingOrbFeatureConfig>> FLOATING_ORB = DEF_REG.register("floating_orb", () -> new FloatingOrbFeature(FloatingOrbFeatureConfig.CODEC));
    public static final RegistryObject<Feature<CoveredBlockBlobConfiguration>> COVERED_BLOCK_BLOB = DEF_REG.register("covered_block_blob", () -> new CoveredBlockBlobFeature(CoveredBlockBlobConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> AMBERSOL = DEF_REG.register("ambersol", () -> new AmbersolFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PEWEN_TREE = DEF_REG.register("pewen_tree", () -> new PewenTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ANCIENT_TREE = DEF_REG.register("ancient_tree", () -> new AncientTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GIANT_ANCIENT_TREE = DEF_REG.register("giant_ancient_tree", () -> new GiantAncientTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CYCAD = DEF_REG.register("cycad", () -> new CycadFeature(NoneFeatureConfiguration.CODEC));

    //TODO - will need to use these for the underground cabins...

    public static final class ACConfiguredFeatureRegistry {
        public static final DeferredRegister<ConfiguredFeature<?, ?>> DEF_REG = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, AlexsCaves.MODID);


    }

    public static final class ACPlacedFeatureRegistry{
        public static final DeferredRegister<PlacedFeature> DEF_REG = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, AlexsCaves.MODID);

    }
}
