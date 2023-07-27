package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class ACAdvancementTriggerRegistry {

    public static final ACAdvancementTrigger KILL_MOB_WITH_GALENA_GAUNTLET = new ACAdvancementTrigger(new ResourceLocation(AlexsCaves.MODID, "kill_mob_with_galena_gauntlet"));
    public static final ACAdvancementTrigger FINISHED_QUARRY = new ACAdvancementTrigger(new ResourceLocation(AlexsCaves.MODID, "finished_quarry"));
    public static final ACAdvancementTrigger ENTER_ACID_WITH_ARMOR = new ACAdvancementTrigger(new ResourceLocation(AlexsCaves.MODID, "enter_acid_with_armor"));
    public static final ACAdvancementTrigger ACID_CREATE_RUST = new ACAdvancementTrigger(new ResourceLocation(AlexsCaves.MODID, "acid_create_rust"));
    public static final ACAdvancementTrigger REMOTE_DETONATION = new ACAdvancementTrigger(new ResourceLocation(AlexsCaves.MODID, "remote_detonation"));
    public static final ACAdvancementTrigger STALKED_BY_DEEP_ONE = new ACAdvancementTrigger(new ResourceLocation(AlexsCaves.MODID, "stalked_by_deep_one"));
    public static final ACAdvancementTrigger DEEP_ONE_TRADE = new ACAdvancementTrigger(new ResourceLocation(AlexsCaves.MODID, "deep_one_trade"));
    public static final ACAdvancementTrigger DEEP_ONE_NEUTRAL = new ACAdvancementTrigger(new ResourceLocation(AlexsCaves.MODID, "deep_one_neutral"));
    public static final ACAdvancementTrigger DEEP_ONE_HELPFUL = new ACAdvancementTrigger(new ResourceLocation(AlexsCaves.MODID, "deep_one_helpful"));

    public static void setup() {
        CriteriaTriggers.register(KILL_MOB_WITH_GALENA_GAUNTLET);
        CriteriaTriggers.register(FINISHED_QUARRY);
        CriteriaTriggers.register(ENTER_ACID_WITH_ARMOR);
        CriteriaTriggers.register(ACID_CREATE_RUST);
        CriteriaTriggers.register(REMOTE_DETONATION);
        CriteriaTriggers.register(STALKED_BY_DEEP_ONE);
        CriteriaTriggers.register(DEEP_ONE_TRADE);
        CriteriaTriggers.register(DEEP_ONE_NEUTRAL);
        CriteriaTriggers.register(DEEP_ONE_HELPFUL);
    }
}
