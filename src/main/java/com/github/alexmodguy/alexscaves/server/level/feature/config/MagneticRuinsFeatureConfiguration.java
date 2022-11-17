package com.github.alexmodguy.alexscaves.server.level.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

public class MagneticRuinsFeatureConfiguration implements FeatureConfiguration {

        public static final Codec<MagneticRuinsFeatureConfiguration> CODEC = RecordCodecBuilder.create((p_159816_) -> {
            return p_159816_.group(ResourceLocation.CODEC.listOf().fieldOf("structures").forGetter((p_159830_) -> {
                return p_159830_.structures;
            })).apply(p_159816_, MagneticRuinsFeatureConfiguration::new);
        });
        public final List<ResourceLocation> structures;

        public MagneticRuinsFeatureConfiguration(List<ResourceLocation> structures) {
            if (structures.isEmpty()) {
                throw new IllegalArgumentException("structure lists need at least one entry");
            } else {
                this.structures = structures;
            }
        }
    }