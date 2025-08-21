package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class ACAdvancementTriggerRegistry {

    public static final ACAdvancementTrigger KILL_MOB_WITH_GALENA_GAUNTLET = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "kill_mob_with_galena_gauntlet"));
    public static final ACAdvancementTrigger FINISHED_QUARRY = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "finished_quarry"));
    public static final ACAdvancementTrigger DINOSAURS_MINECART = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "dinosaurs_minecart"));
    public static final ACAdvancementTrigger CAVE_PAINTING = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "cave_painting"));
    public static final ACAdvancementTrigger MYSTERY_CAVE_PAINTING = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "mystery_cave_painting"));
    public static final ACAdvancementTrigger SUMMON_LUXTRUCTOSAURUS = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "summon_luxtructosaurus"));
    public static final ACAdvancementTrigger ATLATITAN_STOMP = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "atlatitan_stomp"));
    public static final ACAdvancementTrigger ENTER_ACID_WITH_ARMOR = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "enter_acid_with_armor"));
    public static final ACAdvancementTrigger ACID_CREATE_RUST = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "acid_create_rust"));
    public static final ACAdvancementTrigger REMOTE_DETONATION = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "remote_detonation"));
    public static final ACAdvancementTrigger STOP_NUCLEAR_FURNACE_MELTDOWN = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "stop_nuclear_furnace_meltdown"));
    public static final ACAdvancementTrigger HATCH_TREMORZILLA_EGG = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "hatch_tremorzilla_egg"));
    public static final ACAdvancementTrigger TREMORZILLA_KILL_BEAM = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "tremorzilla_kill_beam"));
    public static final ACAdvancementTrigger STALKED_BY_DEEP_ONE = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "stalked_by_deep_one"));
    public static final ACAdvancementTrigger DEEP_ONE_TRADE = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "deep_one_trade"));
    public static final ACAdvancementTrigger DEEP_ONE_NEUTRAL = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "deep_one_neutral"));
    public static final ACAdvancementTrigger DEEP_ONE_HELPFUL = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "deep_one_helpful"));
    public static final ACAdvancementTrigger UNDERZEALOT_SACRIFICE = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "underzealot_sacrifice"));
    public static final ACAdvancementTrigger BEHOLDER_FAR_AWAY = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "beholder_far_away"));
    public static final ACAdvancementTrigger EAT_DARKENED_APPLE = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "eat_darkened_apple"));
    public static final ACAdvancementTrigger FROSTMINT_EXPLOSION = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "frostmint_explosion"));
    public static final ACAdvancementTrigger CONVERT_BIOME = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "convert_biome"));
    public static final ACAdvancementTrigger CONVERT_NETHER_BIOME = new ACAdvancementTrigger(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "convert_nether_biome"));

    public static void setup() {
        CriteriaTriggers.register(KILL_MOB_WITH_GALENA_GAUNTLET);
        CriteriaTriggers.register(DINOSAURS_MINECART);
        CriteriaTriggers.register(CAVE_PAINTING);
        CriteriaTriggers.register(MYSTERY_CAVE_PAINTING);
        CriteriaTriggers.register(SUMMON_LUXTRUCTOSAURUS);
        CriteriaTriggers.register(ATLATITAN_STOMP);
        CriteriaTriggers.register(FINISHED_QUARRY);
        CriteriaTriggers.register(ENTER_ACID_WITH_ARMOR);
        CriteriaTriggers.register(ACID_CREATE_RUST);
        CriteriaTriggers.register(REMOTE_DETONATION);
        CriteriaTriggers.register(STOP_NUCLEAR_FURNACE_MELTDOWN);
        CriteriaTriggers.register(HATCH_TREMORZILLA_EGG);
        CriteriaTriggers.register(TREMORZILLA_KILL_BEAM);
        CriteriaTriggers.register(STALKED_BY_DEEP_ONE);
        CriteriaTriggers.register(DEEP_ONE_TRADE);
        CriteriaTriggers.register(DEEP_ONE_NEUTRAL);
        CriteriaTriggers.register(DEEP_ONE_HELPFUL);
        CriteriaTriggers.register(UNDERZEALOT_SACRIFICE);
        CriteriaTriggers.register(BEHOLDER_FAR_AWAY);
        CriteriaTriggers.register(EAT_DARKENED_APPLE);
        CriteriaTriggers.register(FROSTMINT_EXPLOSION);
        CriteriaTriggers.register(CONVERT_BIOME);
        CriteriaTriggers.register(CONVERT_NETHER_BIOME);
    }
}
