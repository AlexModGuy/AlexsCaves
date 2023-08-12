package com.github.alexmodguy.alexscaves.server.config;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import com.google.common.reflect.TypeToken;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BiomeGenerationConfig {
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    private static final BiomeGenerationNoiseCondition MAGNETIC_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .distanceFromSpawn(400).alexscavesRarityOffset(0).continentalness(0.2F, 1F).depth(0.2F, 1F).build();
    private static final BiomeGenerationNoiseCondition PRIMORDIAL_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .distanceFromSpawn(450).alexscavesRarityOffset(1).continentalness(0.0F, 1F).depth(0.15F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition TOXIC_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .distanceFromSpawn(650).alexscavesRarityOffset(2).continentalness(0.1F, 1F).depth(0.3F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition ABYSSAL_CHASM_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .distanceFromSpawn(400).alexscavesRarityOffset(3).continentalness(-1.0F, -0.2F).temperature(-1.0F, 0.55F).depth(0.2F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition FORLORN_HOLLOWS_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .distanceFromSpawn(650).alexscavesRarityOffset(4).continentalness(0.1F, 1F).depth(0.3F, 1.5F).build();
    private static Map<ResourceKey<Biome>, BiomeGenerationNoiseCondition> biomes = new HashMap<>();

    public static void reloadConfig() {
        biomes.put(ACBiomeRegistry.MAGNETIC_CAVES, getConfigData("magnetic_caves", MAGNETIC_CAVES_CONDITION));
        biomes.put(ACBiomeRegistry.PRIMORDIAL_CAVES, getConfigData("primordial_caves", PRIMORDIAL_CAVES_CONDITION));
        biomes.put(ACBiomeRegistry.TOXIC_CAVES, getConfigData("toxic_caves", TOXIC_CAVES_CONDITION));
        biomes.put(ACBiomeRegistry.ABYSSAL_CHASM, getConfigData("abyssal_chasm", ABYSSAL_CHASM_CONDITION));
        biomes.put(ACBiomeRegistry.FORLORN_HOLLOWS, getConfigData("forlorn_hollows", FORLORN_HOLLOWS_CONDITION));
    }

    @Nullable
    public static ResourceKey<Biome> getBiomeForEvent(EventReplaceBiome event) {
        for (Map.Entry<ResourceKey<Biome>, BiomeGenerationNoiseCondition> condition : biomes.entrySet()) {
            if (condition.getValue().test(event)) {
                return condition.getKey();
            }
        }
        return null;
    }

    public static int getBiomeCount() {
        return biomes.size();
    }

    private static <T> T getOrCreateConfigFile(File configDir, String configName, T defaults, Type type) {
        File configFile = new File(configDir, configName + ".json");
        if (!configFile.exists()) {
            try {
                FileUtils.write(configFile, GSON.toJson(defaults));
            } catch (IOException e) {
                Citadel.LOGGER.error("Biome Generation Config: Could not write " + configFile, e);
            }
        }
        try {
            return GSON.fromJson(FileUtils.readFileToString(configFile), type);
        } catch (Exception e) {
            Citadel.LOGGER.error("Biome Generation Config: Could not load " + configFile, e);
        }

        return defaults;
    }

    private static File getConfigDirectory() {
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path jsonPath = Paths.get(configPath.toAbsolutePath().toString(), "alexscaves_biome_generation");
        return jsonPath.toFile();
    }

    private static BiomeGenerationNoiseCondition getConfigData(String fileName, BiomeGenerationNoiseCondition defaultConfigData) {
        BiomeGenerationNoiseCondition configData = getOrCreateConfigFile(getConfigDirectory(), fileName, defaultConfigData, new TypeToken<BiomeGenerationNoiseCondition>() {
        }.getType());
        return configData;
    }
}
