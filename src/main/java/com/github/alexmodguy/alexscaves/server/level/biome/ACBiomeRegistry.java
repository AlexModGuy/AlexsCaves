package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import com.github.alexthe666.citadel.server.world.ExpandedBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

public class ACBiomeRegistry {

    public static final ResourceKey<Biome> MAGNETIC_CAVES = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(AlexsCaves.MODID, "magnetic_caves"));

    public static void init() {
        ExpandedBiomes.addExpandedBiome(MAGNETIC_CAVES, LevelStem.OVERWORLD);
    }

    public static ResourceKey<Biome> getBiomeForEvent(EventReplaceBiome event) {
        if (event.testWeirdness(0.5F, 1F) && event.testErosion(-0.5F, 0.5F) && event.testContinentalness(-0.15F, 0.75F) && event.testDepth(0.2F, 1F)) {
            return MAGNETIC_CAVES;
        }
        return null;
    }

    private static boolean within(float noiseValue, float min, float max) {
        return noiseValue >= min && noiseValue <= max;
    }
}
