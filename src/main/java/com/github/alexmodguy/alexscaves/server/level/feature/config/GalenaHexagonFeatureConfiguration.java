package com.github.alexmodguy.alexscaves.server.level.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class GalenaHexagonFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<GalenaHexagonFeatureConfiguration> CODEC = RecordCodecBuilder.create((config) -> {
      return config.group(BlockStateProvider.CODEC.fieldOf("hexagon_made_of").forGetter((otherConfig) -> {
         return otherConfig.hexBlock;
      }), Codec.BOOL.fieldOf("ceiling").forGetter((otherConfig) -> {
         return otherConfig.ceiling;
      })).apply(config, GalenaHexagonFeatureConfiguration::new);
   });
   public final BlockStateProvider hexBlock;
   public final boolean ceiling;

   public GalenaHexagonFeatureConfiguration(BlockStateProvider hexBlock, boolean ceiling) {
      this.hexBlock = hexBlock;
      this.ceiling = ceiling;
   }
}