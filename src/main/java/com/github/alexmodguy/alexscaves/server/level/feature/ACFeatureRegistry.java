package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.feature.config.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACFeatureRegistry {
    public static final DeferredRegister<Feature<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.FEATURES, AlexsCaves.MODID);

    public static final RegistryObject<Feature<GalenaHexagonFeatureConfiguration>> GALENA_HEXAGON = DEF_REG.register("galena_hexagon", () -> new GalenaHexagonFeature(GalenaHexagonFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<MagneticNodeFeatureConfiguration>> MAGNETIC_NODE = DEF_REG.register("magnetic_node", () -> new MagneticNodeFeature(MagneticNodeFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<UndergroundRuinsFeatureConfiguration>> UNDERGROUND_RUINS = DEF_REG.register("underground_ruins", () -> new UndergroundRuinsFeature(UndergroundRuinsFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<FloatingOrbFeatureConfig>> FLOATING_ORB = DEF_REG.register("floating_orb", () -> new FloatingOrbFeature(FloatingOrbFeatureConfig.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> TESLA_BULB = DEF_REG.register("tesla_bulb", () -> new TeslaBulbFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<CoveredBlockBlobConfiguration>> COVERED_BLOCK_BLOB = DEF_REG.register("covered_block_blob", () -> new CoveredBlockBlobFeature(CoveredBlockBlobConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> AMBERSOL = DEF_REG.register("ambersol", () -> new AmbersolFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PEWEN_TREE = DEF_REG.register("pewen_tree", () -> new PewenTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ANCIENT_TREE = DEF_REG.register("ancient_tree", () -> new AncientTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GIANT_ANCIENT_TREE = DEF_REG.register("giant_ancient_tree", () -> new GiantAncientTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CYCAD = DEF_REG.register("cycad", () -> new CycadFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> AMBER_MONOLITH = DEF_REG.register("amber_monolith", () -> new AmberMonolithFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SUBTERRANODON_ROOST = DEF_REG.register("subterranodon_roost", () -> new SubterranodonRoostFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> VOLCANO_BOULDER = DEF_REG.register("volcano_boulder", () -> new VolcanoBoulderFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ACID_VENT = DEF_REG.register("acid_vent", () -> new AcidVentFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SULFUR_STACK = DEF_REG.register("sulfur_stack", () -> new SulfurStackFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> NUCLEAR_SIREN = DEF_REG.register("nuclear_siren", () -> new NuclearSirenFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<FillBiomeAboveConfiguration>> FILL_BIOME_ABOVE = DEF_REG.register("fill_biome_above", () -> new FillBiomeAboveFeature(FillBiomeAboveConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> FILL_IN_BUBBLES_WITH_WATER = DEF_REG.register("fill_in_bubbles_with_water", () -> new FillInBubblesWithWaterFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BLACK_VENT = DEF_REG.register("black_vent", () -> new BlackVentFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> TUBE_WORM = DEF_REG.register("tube_worm", () -> new TubeWormFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<WhalefallFeatureConfiguration>> WHALEFALL = DEF_REG.register("whalefall", () -> new WhalefallFeature(WhalefallFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PING_PONG_SPONGE = DEF_REG.register("ping_pong_sponge", () -> new PingPongSpongeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<AbyssalFloraFeatureConfiguration>> ABYSSAL_FLORA = DEF_REG.register("abyssal_flora", () -> new AbyssalFloraFeature(AbyssalFloraFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ABYSSAL_BOULDER = DEF_REG.register("abyssal_boulder", () -> new AbyssalBoulderFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MUSSEL = DEF_REG.register("mussel", () -> new MusselFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<UndergroundRuinsFeatureConfiguration>> DEEP_ONE_RUINS = DEF_REG.register("deep_one_ruins", () -> new DeepOnesRuinsFeature(UndergroundRuinsFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PEERING_COPROLITH = DEF_REG.register("peering_coprolith", () -> new PeeringCoprolithFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> THORNWOOD_TREE = DEF_REG.register("thornwood_tree", () -> new ThornwoodTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> THORNWOOD_TREE_WITH_BRANCHES = DEF_REG.register("thornwood_tree_with_branches", () -> new ThornwoodTreeWithBranchesFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> THORNWOOD_ROOTS = DEF_REG.register("thornwood_roots", () -> new ThornwoodRootsFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GUANO_PILE = DEF_REG.register("guano_pile", () -> new GuanoPileFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<UndergroundRuinsFeatureConfiguration>> FORLORN_RUINS = DEF_REG.register("forlorn_ruins", () -> new ForlornRuinsFeature(UndergroundRuinsFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PEPPERMINT_PILE = DEF_REG.register("peppermint_patch", () -> new PeppermintPileFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ENCRUSTED_PEPPERMINT = DEF_REG.register("encrusted_peppermint", () -> new EncrustedPeppermintFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> LICOROOT_TREE = DEF_REG.register("licoroot_tree", () -> new LicorootTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> LICOROOT_TREE_WITH_SPROUTS = DEF_REG.register("licoroot_tree_with_sprouts", () -> new LicorootTreeWithSproutsFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SPILLED_ICE_CREAM_CONE = DEF_REG.register("spilled_ice_cream_cone", () -> new SpilledIceCreamConeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<IceCreamScoopFeatureConfiguration>> ICE_CREAM_SCOOP = DEF_REG.register("ice_cream_scoop", () -> new IceCreamScoopFeature(IceCreamScoopFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CEILING_ICE_CREAM_CONE = DEF_REG.register("ceiling_ice_cream_cone", () -> new CeilingIceCreamConeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SWEET_PUFF = DEF_REG.register("sweet_puff", () -> new SweetPuffFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SPRINKLES_PILE = DEF_REG.register("sprinkles_pile", () -> new SprinklesPileFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CANDY_CANE = DEF_REG.register("candy_cane", () -> new CandyCaneFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SUNDROP_PATCH = DEF_REG.register("sundrop_patch", () -> new SundropPatchFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<LollipopFeatureConfiguration>> LOLLIPOP = DEF_REG.register("lollipop", () -> new LollipopFeature(LollipopFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> COOKIE_SHELF = DEF_REG.register("cookie_shelf", () -> new CookieShelfFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<UndergroundRuinsFeatureConfiguration>> CANDY_RUINS = DEF_REG.register("candy_ruins", () -> new CandyRuinsFeature(UndergroundRuinsFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GOBSTOPPER_GEODE = DEF_REG.register("gobstopper_geode", () -> new GobstopperGeodeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CEILING_FROSTMINT = DEF_REG.register("ceiling_frostmint", () -> new CeilingFrostmintFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> FLOATING_GUMMY_RING = DEF_REG.register("floating_gummy_ring", () -> new FloatingGummyRingFeature(NoneFeatureConfiguration.CODEC));

}
