package com.github.alexmodguy.alexscaves.server.level.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

public class WhalefallFeatureConfiguration implements FeatureConfiguration {

    public static final Codec<WhalefallFeatureConfiguration> CODEC = RecordCodecBuilder.create((configurationInstance) -> {
        return configurationInstance.group(ResourceLocation.CODEC.listOf().fieldOf("head_structures").forGetter((p_159830_) -> {
                    return p_159830_.headStructures;
                }),ResourceLocation.CODEC.listOf().fieldOf("body_structures").forGetter((p_159830_) -> {
                    return p_159830_.bodyStructures;
                }),ResourceLocation.CODEC.listOf().fieldOf("tail_structures").forGetter((p_159830_) -> {
                    return p_159830_.tailStructures;
                })
                ).apply(configurationInstance, WhalefallFeatureConfiguration::new);
    });
    public final List<ResourceLocation> headStructures;
    public final List<ResourceLocation> bodyStructures;
    public final List<ResourceLocation> tailStructures;

    public WhalefallFeatureConfiguration(List<ResourceLocation> headStructures, List<ResourceLocation> bodyStructures, List<ResourceLocation> tailStructures) {
        if (headStructures.isEmpty() || bodyStructures.isEmpty() || tailStructures.isEmpty()) {
            throw new IllegalArgumentException("structure lists need at least one entry");
        } else {
            this.headStructures = headStructures;
            this.bodyStructures = bodyStructures;
            this.tailStructures = tailStructures;
        }
    }
}