package com.github.alexmodguy.alexscaves.compat.jei;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class SpelunkeryTableRecipe {
    private ResourceKey<Biome> biomeResourceKey;

    public SpelunkeryTableRecipe(ResourceKey<Biome> biomeResourceKey) {
        this.biomeResourceKey = biomeResourceKey;
    }

    public ResourceKey<Biome> getBiomeResourceKey() {
        return biomeResourceKey;
    }
}
