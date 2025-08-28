package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.citadel.server.world.ExpandedBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ACBiomeRegistry {
    public static final ResourceKey<Biome> MAGNETIC_CAVES = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "magnetic_caves"));
    public static final ResourceKey<Biome> PRIMORDIAL_CAVES = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "primordial_caves"));
    public static final ResourceKey<Biome> TOXIC_CAVES = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "toxic_caves"));
    public static final ResourceKey<Biome> ABYSSAL_CHASM = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "abyssal_chasm"));
    public static final ResourceKey<Biome> FORLORN_HOLLOWS = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "forlorn_hollows"));
    public static final ResourceKey<Biome> CANDY_CAVITY = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "candy_cavity"));

    public static final List<ResourceKey<Biome>> ALEXS_CAVES_BIOMES = List.of(MAGNETIC_CAVES, PRIMORDIAL_CAVES, TOXIC_CAVES, ABYSSAL_CHASM, FORLORN_HOLLOWS, CANDY_CAVITY);
    private static final Vec3 DEFAULT_LIGHT_COLOR = new Vec3(1, 1, 1);
    private static final Vec3 TOXIC_CAVES_LIGHT_COLOR = new Vec3(0.5, 1.5, 0.5);
    private static final Vec3 ABYSSAL_CHASM_LIGHT_COLOR = new Vec3(0.5, 0.5, 1);
    private static final Vec3 FORLORN_HOLLOWS_LIGHT_COLOR = new Vec3(0.35, 0.32, 0.3);
    private static final Vec3 CANDY_CAVITY_LIGHT_COLOR = new Vec3(1.1, 0.9, 1.0);

    public static void init() {
        ExpandedBiomes.addExpandedBiome(MAGNETIC_CAVES, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(PRIMORDIAL_CAVES, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(TOXIC_CAVES, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(ABYSSAL_CHASM, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(FORLORN_HOLLOWS, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(CANDY_CAVITY, LevelStem.OVERWORLD);
    }

    public static float getBiomeAmbientLight(Holder<Biome> value) {
        if (value.is(PRIMORDIAL_CAVES)) {
            return 0.125F;
        }else if (value.is(TOXIC_CAVES)) {
            return 0.01F;
        }else if (value.is(CANDY_CAVITY)) {
            return 0.125F;
        }
        return 0.0F;
    }

    public static float getBiomeFogNearness(Holder<Biome> value) {
        if (value.is(PRIMORDIAL_CAVES)) {
            return 0.5F;
        }
        if (value.is(TOXIC_CAVES)) {
            return -0.15F;
        }
        if (value.is(ABYSSAL_CHASM)) {
            return -0.3F;
        }
        if (value.is(FORLORN_HOLLOWS)) {
            return -0.2F;
        }
        if (value.is(CANDY_CAVITY)) {
            return 0.75F;
        }
        return 1.0F;
    }

    public static float getBiomeWaterFogFarness(Holder<Biome> value) {
        if (value.is(ABYSSAL_CHASM)) {
            return 0.5F;
        }
        return 1.0F;
    }

    public static float getBiomeSkyOverride(Holder<Biome> value) {
        if (value.is(MAGNETIC_CAVES) || value.is(PRIMORDIAL_CAVES) || value.is(TOXIC_CAVES) || value.is(ABYSSAL_CHASM) || value.is(FORLORN_HOLLOWS) || value.is(CANDY_CAVITY)) {
            return 1.0F;
        }
        return 0.0F;
    }

    public static Vec3 getBiomeLightColorOverride(Holder<Biome> value) {
        if (value.is(TOXIC_CAVES)) {
            return TOXIC_CAVES_LIGHT_COLOR;
        }
        if (value.is(ABYSSAL_CHASM)) {
            return ABYSSAL_CHASM_LIGHT_COLOR;
        }
        if (value.is(FORLORN_HOLLOWS)) {
            return FORLORN_HOLLOWS_LIGHT_COLOR;
        }
        if(value.is(CANDY_CAVITY)){
            return CANDY_CAVITY_LIGHT_COLOR;
        }
        return DEFAULT_LIGHT_COLOR;
    }

    public static int getBiomeTabletColor(ResourceKey<Biome> value) {
        if (value.equals(MAGNETIC_CAVES)) {
            return 0X392447;
        }
        if (value.equals(PRIMORDIAL_CAVES)) {
            return 0XFCBA00;
        }
        if (value.equals(TOXIC_CAVES)) {
            return 0X6ACA04;
        }
        if (value.equals(ABYSSAL_CHASM)) {
            return 0X1919AC;
        }
        if (value.equals(FORLORN_HOLLOWS)) {
            return 0X705632;
        }
        if(value.equals(CANDY_CAVITY)){
            return 0XFF5BC0;
        }
        return -1;
    }

    public static float calculateBiomeSkyOverride(Entity player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return ACBiomeRegistry.getBiomeSkyOverride(player.level().getBiome(player.blockPosition()));
        } else {
            return BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeSkyOverride);
        }
    }
}
