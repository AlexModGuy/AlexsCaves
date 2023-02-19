package com.github.alexmodguy.alexscaves.server.level.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

public class AbyssalFloraFeatureConfiguration implements FeatureConfiguration {

    public static final Codec<AbyssalFloraFeatureConfiguration> CODEC = RecordCodecBuilder.create((config) -> {
        return config.group(BlockStateProvider.CODEC.fieldOf("flora").forGetter((otherConfig) -> {
            return otherConfig.floraBlock;
        })).apply(config, AbyssalFloraFeatureConfiguration::new);
    });
    public final BlockStateProvider floraBlock;

    public AbyssalFloraFeatureConfiguration(BlockStateProvider floraBlock) {
        this.floraBlock = floraBlock;
    }
}