package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import com.github.alexthe666.citadel.server.world.ExpandedBiomes;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACBiomeRegistry {

    public static final ResourceKey<Biome> MAGNETIC_CAVES = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(AlexsCaves.MODID, "magnetic_caves"));
    public static final ResourceKey<Biome> PRIMORDIAL_CAVES = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(AlexsCaves.MODID, "primordial_caves"));

    public static void init() {
        ExpandedBiomes.addExpandedBiome(MAGNETIC_CAVES, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(PRIMORDIAL_CAVES, LevelStem.OVERWORLD);
    }

    public static ResourceKey<Biome> getBiomeForEvent(EventReplaceBiome event) {
        if (event.testHumidity(-0.2F, 0.5F) && event.testContinentalness(-0.35F, 1F) && event.testTemperature(0.0F, 0.8F) && event.testDepth(0.2F, 1F)) {
            return PRIMORDIAL_CAVES;
        }
        if (event.testWeirdness(0.5F, 1F) && event.testErosion(-0.5F, 0.5F) && event.testContinentalness(-0.15F, 0.75F) && event.testDepth(0.2F, 1F)) {
            return MAGNETIC_CAVES;
        }
        return null;
    }

    public static float getBiomeAmbientLight(Holder<Biome> value) {
        if(value.is(PRIMORDIAL_CAVES)){
            return 0.125F;
        }
        return 0.0F;
    }

    public static float getBiomeSkyOverride(Holder<Biome> value) {
        if(value.is(PRIMORDIAL_CAVES)){
            return 1.0F;
        }
        return 0.0F;
    }
}
