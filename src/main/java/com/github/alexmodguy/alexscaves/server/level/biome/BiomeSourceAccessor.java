package com.github.alexmodguy.alexscaves.server.level.biome;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Set;

public interface BiomeSourceAccessor {
    void setResourceKeyMap(Map<ResourceKey<Biome>, Holder<Biome>> var1);

    Map<ResourceKey<Biome>, Holder<Biome>> getResourceKeyMap();

    void expandBiomesWith(Set<Holder<Biome>> var1);
}
