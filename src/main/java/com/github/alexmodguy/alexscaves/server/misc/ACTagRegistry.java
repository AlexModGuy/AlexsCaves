package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

public class ACTagRegistry {

    public static final TagKey<Block> MAGNETIC_ATTACHABLES = registerBlockTag("magnetic_attachables");
    public static final TagKey<Block> MAGNETIC_BLOCKS = registerBlockTag("ferromagnetic_blocks");
    public static final TagKey<Block> UNMOVEABLE = registerBlockTag("unmovable");
    public static final TagKey<Block> MAGNETIC_CAVES_BASE_BLOCKS = registerBlockTag("magnetic_caves_base_blocks");
    public static final TagKey<Block> MAGNET_REMOVES_LAST = registerBlockTag("magnet_removes_last");
    public static final TagKey<Block> TESLA_BULB_BASE_BLOCKS = registerBlockTag("tesla_bulb_base_blocks");
    public static final TagKey<Block> MAGNETRON_WEAPONS = registerBlockTag("magnetron_weapons");
    public static final TagKey<Block> RESISTS_MAGNETRON_BODY_BUILDING = registerBlockTag("resists_magnetron_body_building");
    public static final TagKey<Block> SCAFFOLDING = registerBlockTag("scaffolding");
    public static final TagKey<Block> DINOSAURS_SPAWNABLE_ON = registerBlockTag("dinosaurs_spawnable_on");
    public static final TagKey<Block> TURNS_INTO_CAVE_PAINTINGS = registerBlockTag("turns_into_cave_paintings");
    public static final TagKey<Block> STOPS_DINOSAUR_EGGS = registerBlockTag("stops_dinosaur_eggs");
    public static final TagKey<Block> GROTTOCERATOPS_FOOD_BLOCKS = registerBlockTag("grottoceratops_food_blocks");
    public static final TagKey<Block> RELICHEIRUS_NIBBLES = registerBlockTag("relicheirus_nibbles");
    public static final TagKey<Block> RELICHEIRUS_KNOCKABLE_LEAVES = registerBlockTag("relicheirus_knockable_leaves");
    public static final TagKey<Block> RELICHEIRUS_KNOCKABLE_LOGS = registerBlockTag("relicheirus_knockable_logs");
    public static final TagKey<Block> COOKS_MEAT_BLOCKS = registerBlockTag("cooks_meat_blocks");
    public static final TagKey<Block> REGENERATES_AFTER_PRIMORDIAL_BOSS_FIGHT = registerBlockTag("regenerates_after_primordial_boss_fight");
    public static final TagKey<Block> LUXTRUCTOSAURUS_BREAKS = registerBlockTag("luxtructosaurus_breaks");
    public static final TagKey<Block> VOLCANO_BLOCKS = registerBlockTag("volcano_blocks");
    public static final TagKey<Block> NUKE_PROOF = registerBlockTag("nuke_proof");
    public static final TagKey<Block> RAYCAT_SLEEPS_ON = registerBlockTag("raycat_sleeps_on");
    public static final TagKey<Block> REMOTE_DETONATOR_ACTIVATES = registerBlockTag("remote_detonator_activates");
    public static final TagKey<Block> TRENCH_GENERATION_IGNORES = registerBlockTag("trench_generation_ignores");
    public static final TagKey<Block> TUBE_WORM_AVOIDS = registerBlockTag("tube_worm_avoids");
    public static final TagKey<Block> GROWS_MUSSELS = registerBlockTag("grows_mussels");
    public static final TagKey<Block> WHALEFALL_IGNORES = registerBlockTag("whalefall_ignores");
    public static final TagKey<Block> SUBMARINE_ASSEMBLY_BLOCKS = registerBlockTag("submarine_assembly_blocks");
    public static final TagKey<Block> UNDERZEALOT_LIGHT_SOURCES = registerBlockTag("underzealot_light_sources");
    public static final TagKey<Block> GLOOMOTH_LIGHT_SOURCES = registerBlockTag("gloomoth_light_sources");
    public static final TagKey<Item> MAGNETIC_ITEMS = registerItemTag("ferromagnetic_items");
    public static final TagKey<Item> GALENA_GAUNTLET_CRYSTALLIZATION_ITEMS = registerItemTag("galena_gauntlet_crystallization_items");
    public static final TagKey<Item> TELETOR_SPAWNS_WITH = registerItemTag("teletor_spawns_with");
    public static final TagKey<Item> VALLUMRAPTOR_STEALS = registerItemTag("vallumraptor_steals");
    public static final TagKey<Item> RAW_MEATS = registerItemTag("raw_meats");
    public static final TagKey<Item> NUCLEAR_FURNACE_BARRELS = registerItemTag("nuclear_furnace_barrels");
    public static final TagKey<Item> NUCLEAR_FURNACE_RODS = registerItemTag("nuclear_furnace_rods");
    public static final TagKey<Item> SEA_PIG_DIGESTS = registerItemTag("sea_pig_digests");
    public static final TagKey<Item> DEEP_ONE_BARTERS = registerItemTag("deep_one_barters");
    public static final TagKey<Item> RESTRICTED_BIOME_LOCATORS = registerItemTag("restricted_biome_locators");
    public static final TagKey<EntityType<?>> MAGNETIC_ENTITIES = registerEntityTag("ferromagnetic_entities");
    public static final TagKey<EntityType<?>> NOTOR_IGNORES = registerEntityTag("notor_ignores");
    public static final TagKey<EntityType<?>> DINOSAURS = registerEntityTag("dinosaurs");
    public static final TagKey<EntityType<?>> SUBTERRANODON_FLEES = registerEntityTag("subterranodon_flees");
    public static final TagKey<EntityType<?>> VALLUMRAPTOR_TARGETS = registerEntityTag("vallumraptor_targets");
    public static final TagKey<EntityType<?>> RESISTS_TREMORSAURUS_ROAR = registerEntityTag("resists_tremorsaurus_roar");
    public static final TagKey<EntityType<?>> AMBER_MONOLITH_SKIPS = registerEntityTag("amber_monolith_skips");
    public static final TagKey<EntityType<?>> RESISTS_ACID = registerEntityTag("resists_acid");
    public static final TagKey<EntityType<?>> RESISTS_RADIATION = registerEntityTag("resists_radiation");
    public static final TagKey<EntityType<?>> SEAFLOOR_DENIZENS = registerEntityTag("seafloor_denizens");
    public static final TagKey<EntityType<?>> GLOWING_ENTITIES = registerEntityTag("glowing_entities");
    public static final TagKey<Biome> HAS_NO_UNDERGROUND_CABINS = registerBiomeTag("has_no_underground_cabins");
    public static final TagKey<Biome> CAVE_MAP_BORDER_ON = registerBiomeTag("cave_map_border_on");
    public static final TagKey<Biome> TRENCH_IGNORES_STONE_IN = registerBiomeTag("trench_ignores_stone_in");
    public static final TagKey<Biome> OVERRIDE_ALL_VANILLA_MUSIC_IN = registerBiomeTag("override_all_vanilla_music_in");
    public static final TagKey<Biome> OVERRIDE_UNDERWATER_AMBIENCE_IN = registerBiomeTag("override_underwater_ambience_in");
    public static final TagKey<Biome> ONLY_AMBIENT_LOOP_UNDERWATER = registerBiomeTag("only_ambient_loop_underwater");
    public static final TagKey<Biome> HAS_NO_ANCIENT_CITIES_IN = registerBiomeTag("has_no_ancient_cities_in");
    public static final TagKey<EntityType<?>> RESISTS_BUBBLED = registerEntityTag("resists_bubbled");
    public static final TagKey<Block> DRAIN_BREAKS = registerBlockTag("drain_breaks");
    public static final TagKey<Block> CORRODENT_BLOCKS_DIGGING = registerBlockTag("corrodent_blocks_digging");
    public static final TagKey<EntityType<?>> WEAK_TO_ACID = registerEntityTag("weak_to_acid");
    public static final TagKey<EntityType<?>> WEAK_TO_FORSAKEN_SONIC_ATTACK = registerEntityTag("weak_to_forsaken_sonic_attack");
    public static final TagKey<EntityType<?>> FORSAKEN_IGNORES = registerEntityTag("forsaken_ignores");
    public static final TagKey<EntityType<?>> MOTH_DUST_ENRAGES = registerEntityTag("moth_dust_enrages");
    public static final TagKey<EntityType<?>> RESISTS_TOTEM_OF_POSSESSION = registerEntityTag("resists_totem_of_possession");
    public static final TagKey<Structure> ON_UNDERGROUND_CABIN_MAPS = registerStructureTag("on_underground_cabin_maps");
    public static final TagKey<DamageType> DEEP_ONE_IGNORES = registerDamageTypeTag("deep_one_ignores");
    public static final TagKey<Fluid> DOES_NOT_FLOW_INTO_WATERLOGGABLE_BLOCKS = registerFluidTag("does_not_flow_into_waterloggable_blocks");

    private static TagKey<EntityType<?>> registerEntityTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(AlexsCaves.MODID, name));
    }

    private static TagKey<Item> registerItemTag(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(AlexsCaves.MODID, name));
    }

    private static TagKey<Block> registerBlockTag(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(AlexsCaves.MODID, name));
    }

    private static TagKey<Biome> registerBiomeTag(String name) {
        return TagKey.create(Registries.BIOME, new ResourceLocation(AlexsCaves.MODID, name));
    }

    private static TagKey<Structure> registerStructureTag(String name) {
        return TagKey.create(Registries.STRUCTURE, new ResourceLocation(AlexsCaves.MODID, name));
    }

    private static TagKey<DamageType> registerDamageTypeTag(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(AlexsCaves.MODID, name));
    }

    private static TagKey<Fluid> registerFluidTag(String name) {
        return TagKey.create(Registries.FLUID, new ResourceLocation(AlexsCaves.MODID, name));
    }
}
