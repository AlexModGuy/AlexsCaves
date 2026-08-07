package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;

public class ACPotPatternRegistry {

    public static final DeferredRegister<DecoratedPotPattern> DEF_REG = DeferredRegister.create(Registries.DECORATED_POT_PATTERN, AlexsCaves.MODID);

    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> DINOSAUR = register("dinosaur_pottery_pattern");
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> FOOTPRINT = register("footprint_pottery_pattern");
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> GUARDIAN = register("guardian_pottery_pattern");
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> HERO = register("hero_pottery_pattern");

    private static DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> register(String name) {
        return DEF_REG.register(name, () -> new DecoratedPotPattern(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name)));
    }

    @Nullable
    public static ResourceKey<DecoratedPotPattern> getPatternForSherd(Item item) {
        if (item == ACItemRegistry.DINOSAUR_POTTERY_SHERD.get()) {
            return DINOSAUR.getKey();
        } else if (item == ACItemRegistry.FOOTPRINT_POTTERY_SHERD.get()) {
            return FOOTPRINT.getKey();
        } else if (item == ACItemRegistry.GUARDIAN_POTTERY_SHERD.get()) {
            return GUARDIAN.getKey();
        } else if (item == ACItemRegistry.HERO_POTTERY_SHERD.get()) {
            return HERO.getKey();
        }
        return null;
    }
}
