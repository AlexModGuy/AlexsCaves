package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ACTagRegistry {

    public static final TagKey<Block> MAGNETIC_ATTACHABLES = registerBlockTag("magnetic_attachables");
    public static final TagKey<Block> MAGNETIC_BLOCKS = registerBlockTag("ferromagnetic_blocks");
    public static final TagKey<Block> UNMOVEABLE = registerBlockTag("unmovable");

    public static final TagKey<Block> MAGNETIC_CAVES_BASE_BLOCKS = registerBlockTag("magnetic_caves_base_blocks");
    public static final TagKey<Block> MAGNETRON_WEAPONS = registerBlockTag("magnetron_weapons");
    public static final TagKey<Block> PRIMORDIAL_CAVES_BASE_BLOCKS = registerBlockTag("primordial_caves_base_blocks");

    public static final TagKey<Block> PRIMORDIAL_CAVES_CARVER_PRESERVES = registerBlockTag("primordial_caves_carver_preserves");
    public static final TagKey<Block> DINOSAURS_SPAWNABLE_ON = registerBlockTag("dinosaurs_spawnable_on");

    public static final TagKey<Block> GROTTOCERATOPS_FOOD_BLOCKS = registerBlockTag("grottoceratops_food_blocks");
    public static final TagKey<Block> RELICHEIRUS_NIBBLES = registerBlockTag("relicheirus_nibbles");
    public static final TagKey<Block> RELICHEIRUS_KNOCKABLE_LEAVES = registerBlockTag("relicheirus_knockable_leaves");
    public static final TagKey<Block> RELICHEIRUS_KNOCKABLE_LOGS = registerBlockTag("relicheirus_knockable_logs");
    public static final TagKey<Item> MAGNETIC_ITEMS = registerItemTag("ferromagnetic_items");
    public static final TagKey<Item> TELETOR_SPAWNS_WITH = registerItemTag("teletor_spawns_with");

    public static final TagKey<Item> VALLUMRAPTOR_STEALS = registerItemTag("vallumraptor_steals");
    public static final TagKey<EntityType<?>> MAGNETIC_ENTITIES = registerEntityTag("ferromagnetic_entities");

    public static final TagKey<EntityType<?>> SUBTERRANODON_FLEES = registerEntityTag("subterranodon_flees");

    public static final TagKey<EntityType<?>> RESISTS_TREMORSAURUS_ROAR = registerEntityTag("resists_tremorsaurus_roar");

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
}
