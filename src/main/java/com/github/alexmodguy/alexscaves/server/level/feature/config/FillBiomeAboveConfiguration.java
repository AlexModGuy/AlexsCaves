package com.github.alexmodguy.alexscaves.server.level.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class FillBiomeAboveConfiguration implements FeatureConfiguration {
    public static final Codec<FillBiomeAboveConfiguration> CODEC = RecordCodecBuilder.create((config) -> {
        return config.group(Biome.CODEC.fieldOf("replacing").forGetter((otherConfig) -> {
            return otherConfig.replacing;
        }), Biome.CODEC.fieldOf("new_biome").forGetter((otherConfig) -> {
            return otherConfig.newBiome;
        }), Codec.INT.fieldOf("y_above_sea_level").forGetter((otherConfig) -> {
            return otherConfig.yAboveSeaLevel;
        })).apply(config, FillBiomeAboveConfiguration::new);
    });
    public final Holder<Biome> replacing;
    public final Holder<Biome> newBiome;
    public final int yAboveSeaLevel;

    public FillBiomeAboveConfiguration(Holder<Biome> replacing, Holder<Biome> newBiome, int yAboveSeaLevel) {
        this.replacing = replacing;
        this.newBiome = newBiome;
        this.yAboveSeaLevel = yAboveSeaLevel;
    }
}