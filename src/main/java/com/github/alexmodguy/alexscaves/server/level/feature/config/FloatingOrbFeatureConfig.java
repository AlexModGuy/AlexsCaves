package com.github.alexmodguy.alexscaves.server.level.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class FloatingOrbFeatureConfig implements FeatureConfiguration {
   public static final Codec<FloatingOrbFeatureConfig> CODEC = RecordCodecBuilder.create((config) -> {
      return config.group(BlockStateProvider.CODEC.fieldOf("orb_made_of").forGetter((otherConfig) -> {
         return otherConfig.orbBlock;
      }), Codec.INT.fieldOf("min_radius").forGetter((otherConfig) -> {
         return otherConfig.minRadius;
      }), Codec.INT.fieldOf("max_radius").forGetter((otherConfig) -> {
         return otherConfig.maxRadius;
      })).apply(config, FloatingOrbFeatureConfig::new);
   });
   public final BlockStateProvider orbBlock;
   public final int minRadius;
   public final int maxRadius;

   public FloatingOrbFeatureConfig(BlockStateProvider orbBlock, int minRadius, int maxRadius) {
      this.orbBlock = orbBlock;
      this.minRadius = minRadius;
      this.maxRadius = maxRadius;
   }
}