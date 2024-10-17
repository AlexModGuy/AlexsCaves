package com.github.alexmodguy.alexscaves.server.level.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class IceCreamScoopFeatureConfiguration implements FeatureConfiguration {

    public static final Codec<IceCreamScoopFeatureConfiguration> CODEC = RecordCodecBuilder.create((config) -> {
        return config.group(BlockStateProvider.CODEC.fieldOf("ice_cream").forGetter((otherConfig) -> {
            return otherConfig.iceCreamBlock;
        })).apply(config, IceCreamScoopFeatureConfiguration::new);
    });
    public final BlockStateProvider iceCreamBlock;

    public IceCreamScoopFeatureConfiguration(BlockStateProvider iceCreamBlock) {
        this.iceCreamBlock = iceCreamBlock;
    }
}