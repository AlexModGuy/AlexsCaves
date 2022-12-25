package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import com.github.alexthe666.citadel.server.world.ExpandedBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.HashMap;

public class ACBiomeRegistry {
    public static final ResourceKey<Biome> MAGNETIC_CAVES = ResourceKey.create(Registries.BIOME, new ResourceLocation(AlexsCaves.MODID, "magnetic_caves"));
    public static final ResourceKey<Biome> PRIMORDIAL_CAVES = ResourceKey.create(Registries.BIOME, new ResourceLocation(AlexsCaves.MODID, "primordial_caves"));
    public static final ResourceKey<Biome> TOXIC_CAVES = ResourceKey.create(Registries.BIOME, new ResourceLocation(AlexsCaves.MODID, "toxic_caves"));

    private static final Vec3 ONE = new Vec3(1, 1, 1);

    public static void init() {
        ExpandedBiomes.addExpandedBiome(MAGNETIC_CAVES, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(PRIMORDIAL_CAVES, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(TOXIC_CAVES, LevelStem.OVERWORLD);
    }
    public static float getBiomeAmbientLight(Holder<Biome> value) {
        if(value.is(PRIMORDIAL_CAVES)){
            return 0.125F;
        }
        if(value.is(TOXIC_CAVES)){
            return 0.01F;
        }
        return 0.0F;
    }

    public static float getBiomeFogNearness(Holder<Biome> value) {
        if(value.is(PRIMORDIAL_CAVES)){
            return 0.5F;
        }
        if(value.is(TOXIC_CAVES)){
            return -0.15F;
        }
        return 1.0F;
    }

    public static float getBiomeSkyOverride(Holder<Biome> value) {
        if(value.is(PRIMORDIAL_CAVES)){
            return 1.0F;
        }
        if(value.is(TOXIC_CAVES)){
            return 1.0F;
        }
        return 0.0F;
    }

    public static Vec3 getBiomeLightColorOverride(Holder<Biome> value) {
        if(value.is(TOXIC_CAVES)){
            return new Vec3(0.5, 1.5, 0.5);
        }
        return ONE;
    }
}
