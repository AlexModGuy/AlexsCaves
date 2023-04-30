package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.citadel.server.world.ExpandedBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.Vec3;

public class ACBiomeRegistry {
    public static final ResourceKey<Biome> MAGNETIC_CAVES = ResourceKey.create(Registries.BIOME, new ResourceLocation(AlexsCaves.MODID, "magnetic_caves"));
    public static final ResourceKey<Biome> PRIMORDIAL_CAVES = ResourceKey.create(Registries.BIOME, new ResourceLocation(AlexsCaves.MODID, "primordial_caves"));
    public static final ResourceKey<Biome> TOXIC_CAVES = ResourceKey.create(Registries.BIOME, new ResourceLocation(AlexsCaves.MODID, "toxic_caves"));
    public static final ResourceKey<Biome> ABYSSAL_CHASM = ResourceKey.create(Registries.BIOME, new ResourceLocation(AlexsCaves.MODID, "abyssal_chasm"));
    private static final Vec3 ONE = new Vec3(1, 1, 1);

    public static void init() {
        ExpandedBiomes.addExpandedBiome(MAGNETIC_CAVES, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(PRIMORDIAL_CAVES, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(TOXIC_CAVES, LevelStem.OVERWORLD);
        ExpandedBiomes.addExpandedBiome(ABYSSAL_CHASM, LevelStem.OVERWORLD);
    }

    public static float getBiomeAmbientLight(Holder<Biome> value) {
        if (value.is(PRIMORDIAL_CAVES)) {
            return 0.125F;
        }
        if (value.is(TOXIC_CAVES)) {
            return 0.01F;
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
            return -0.2F;
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
        if (value.is(PRIMORDIAL_CAVES)) {
            return 1.0F;
        }
        if (value.is(TOXIC_CAVES)) {
            return 1.0F;
        }
        if (value.is(ABYSSAL_CHASM)) {
            return 1.0F;
        }
        return 0.0F;
    }

    public static Vec3 getBiomeLightColorOverride(Holder<Biome> value) {
        if (value.is(TOXIC_CAVES)) {
            return new Vec3(0.5, 1.5, 0.5);
        }
        if (value.is(ABYSSAL_CHASM)) {
            return new Vec3(0.5, 0.5, 1);
        }
        return ONE;
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
        return -1;
    }
}
