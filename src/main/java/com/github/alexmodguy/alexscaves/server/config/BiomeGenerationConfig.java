package com.github.alexmodguy.alexscaves.server.config;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class BiomeGenerationConfig {
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();

    private static final String OVERWORLD = "minecraft:overworld";

    private static final BiomeGenerationNoiseCondition MAGNETIC_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(400).alexscavesRarityOffset(0).continentalness(0.6F, 1F).depth(0.2F, 1F).build();
    private static final BiomeGenerationNoiseCondition PRIMORDIAL_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(450).alexscavesRarityOffset(1).continentalness(0.4F, 1F).depth(0.15F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition TOXIC_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(650).alexscavesRarityOffset(2).continentalness(0.5F, 1F).depth(0.3F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition ABYSSAL_CHASM_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(400).alexscavesRarityOffset(3).continentalness(-0.95F, -0.65F).temperature(-1.0F, 0.5F).depth(0.2F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition FORLORN_HOLLOWS_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(650).alexscavesRarityOffset(4).continentalness(0.6F, 1F).depth(0.3F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition CANDY_CAVITY_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(500).alexscavesRarityOffset(5).continentalness(0.5F, 1F).depth(0.15F, 1.5F).build();
    public static final LinkedHashMap<ResourceKey<Biome>, BiomeGenerationNoiseCondition> BIOMES = new LinkedHashMap<>();

    public static void reloadConfig() {
        BIOMES.put(ACBiomeRegistry.MAGNETIC_CAVES, getConfigData("magnetic_caves", MAGNETIC_CAVES_CONDITION));
        BIOMES.put(ACBiomeRegistry.PRIMORDIAL_CAVES, getConfigData("primordial_caves", PRIMORDIAL_CAVES_CONDITION));
        BIOMES.put(ACBiomeRegistry.TOXIC_CAVES, getConfigData("toxic_caves", TOXIC_CAVES_CONDITION));
        BIOMES.put(ACBiomeRegistry.ABYSSAL_CHASM, getConfigData("abyssal_chasm", ABYSSAL_CHASM_CONDITION));
        BIOMES.put(ACBiomeRegistry.FORLORN_HOLLOWS, getConfigData("forlorn_hollows", FORLORN_HOLLOWS_CONDITION));
        BIOMES.put(ACBiomeRegistry.CANDY_CAVITY, getConfigData("candy_cavity", CANDY_CAVITY_CONDITION));
    }

    @Nullable
    @Deprecated(forRemoval = true, since="1.21")
    public static ResourceKey<Biome> getBiomeForEvent(EventReplaceBiome event) {
        VoronoiGenerator.VoronoiInfo voronoiInfo = ACBiomeRarity.getRareBiomeInfoForQuad(event.getWorldSeed(), event.getX(), event.getZ());
        if(voronoiInfo != null){
            int foundRarityOffset = ACBiomeRarity.getRareBiomeOffsetId(voronoiInfo);
            for (Map.Entry<ResourceKey<Biome>, BiomeGenerationNoiseCondition> condition : BIOMES.entrySet()) {
                if (foundRarityOffset == condition.getValue().getRarityOffset() && condition.getValue().test(event, voronoiInfo)) {
                    return condition.getKey();
                }
            }
        }
        return null;
    }

    public static int getBiomeCount() {
        return BIOMES.size();
    }

    public static boolean isBiomeDisabledCompletely(ResourceKey<Biome> biome){
        BiomeGenerationNoiseCondition noiseCondition = BIOMES.get(biome);
        return noiseCondition != null && noiseCondition.isDisabledCompletely();
    }

    private static <T> T getOrCreateConfigFile(File configDir, String configName, T defaults, Type type, Predicate<T> isInvalid) {
        File configFile = new File(configDir, configName + ".json");
        if (!configFile.exists()) {
            try {
                FileUtils.write(configFile, GSON.toJson(defaults));
            } catch (IOException e) {
                Citadel.LOGGER.error("Biome Generation Config: Could not write " + configFile, e);
            }
        }
        try {
            T found = GSON.fromJson(FileUtils.readFileToString(configFile), type);
            if (isInvalid.test(found)) {
                Citadel.LOGGER.warn("Old Biome Generation Config format found for " + configName + ", replacing with new one.");
                try {
                    FileUtils.write(configFile, GSON.toJson(defaults));
                } catch (IOException e) {
                    Citadel.LOGGER.error("Biome Generation Config: Could not write " + configFile, e);
                }
            } else {
                return found;
            }
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
        }.getType(), BiomeGenerationNoiseCondition::isInvalid);
        return configData;
    }
}
