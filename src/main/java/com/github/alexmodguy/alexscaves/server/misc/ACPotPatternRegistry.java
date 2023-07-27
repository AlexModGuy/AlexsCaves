package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ACPotPatternRegistry {

    public static final DeferredRegister<String> DEF_REG = DeferredRegister.create(Registries.DECORATED_POT_PATTERNS, AlexsCaves.MODID);

    public static final RegistryObject<String> DINOSAUR = DEF_REG.register("dinosaur_pottery_pattern", () -> AlexsCaves.MODID + ":dinosaur_pottery_pattern");
    public static final RegistryObject<String> FOOTPRINT = DEF_REG.register("footprint_pottery_pattern", () -> AlexsCaves.MODID + ":footprint_pottery_pattern");

    public static void expandVanillaDefinitions() {
        ImmutableMap.Builder<Item, ResourceKey<String>> itemsToPot = new ImmutableMap.Builder<>();
        itemsToPot.putAll(DecoratedPotPatterns.ITEM_TO_POT_TEXTURE);
        itemsToPot.put(ACItemRegistry.DINOSAUR_POTTERY_SHERD.get(), DINOSAUR.getKey());
        itemsToPot.put(ACItemRegistry.FOOTPRINT_POTTERY_SHERD.get(), FOOTPRINT.getKey());
        DecoratedPotPatterns.ITEM_TO_POT_TEXTURE = itemsToPot.build();
    }
}
