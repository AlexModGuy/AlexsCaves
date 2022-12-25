package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import com.github.alexthe666.citadel.server.world.ExpandedBiomeSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;

public class ACBiomePlacer {

    public static final int AC_BIOME_SEPERATION = 1200;

    public static ResourceKey<Biome> getBiomeForEvent(EventReplaceBiome event) {
        if (isFarEnoughFromSpawnAndNeighbors(event, 300) && event.testErosion(-1.0F, 0.0F) && event.testHumidity(-0.5F, 0.1F) && event.testWeirdness(-0.05F, 1F) && event.testContinentalness(-0.15F, 0.75F) && event.testDepth(0.2F, 1F)) {
            return ACBiomeRegistry.MAGNETIC_CAVES;
        }
        if (isFarEnoughFromSpawnAndNeighbors(event, 500) && event.testHumidity(0.05F, 0.4F) && event.testContinentalness(0.2F, 1F) && event.testTemperature(0.1F, 0.5F) && event.testDepth(0.1F, 1F)) {
            return ACBiomeRegistry.PRIMORDIAL_CAVES;
        }
        if (isFarEnoughFromSpawnAndNeighbors(event, 600) && event.testErosion(-0.5F, 0.5F) && event.testContinentalness(0.4F, 1.1F) && event.testTemperature(0.5F, 1.5F) && event.testDepth(0.2F, 1F)) {
            return ACBiomeRegistry.TOXIC_CAVES;
        }
        return null;
    }

    public static boolean isFarEnoughFromSpawnAndNeighbors(EventReplaceBiome event, double dist){
        if(event.getX() * event.getX() + event.getZ() * event.getZ() <= dist * dist){
            return false;
        }
        return true;
    }
}
