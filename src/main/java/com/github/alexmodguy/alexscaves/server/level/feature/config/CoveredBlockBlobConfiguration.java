package com.github.alexmodguy.alexscaves.server.level.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class CoveredBlockBlobConfiguration implements FeatureConfiguration {
    public static final Codec<CoveredBlockBlobConfiguration> CODEC = RecordCodecBuilder.create((config) -> {
        return config.group(BlockStateProvider.CODEC.fieldOf("block").forGetter((otherConfig) -> {
            return otherConfig.block;
        }), BlockStateProvider.CODEC.fieldOf("cover").forGetter((otherConfig) -> {
            return otherConfig.coverBlock;
        })).apply(config, CoveredBlockBlobConfiguration::new);
    });
    public final BlockStateProvider block;
    public final BlockStateProvider coverBlock;

    public CoveredBlockBlobConfiguration(BlockStateProvider block, BlockStateProvider coverBlock) {
        this.block = block;
        this.coverBlock = coverBlock;
    }
}