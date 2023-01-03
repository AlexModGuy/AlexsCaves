package com.github.alexmodguy.alexscaves.server.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ACServerConfig {

    public final ForgeConfigSpec.DoubleValue biomeRarityScale;
    public ACServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("generation");
        biomeRarityScale = builder.comment("the value used to scale the noise function that determines the areas rare cave biomes can spawn in. A higher number means more spread out and larger rare cave biomes.").translation("biome_rarity_scale").defineInRange("biome_rarity_scale", 200.0D, 1D, Double.MAX_VALUE);
        builder.pop();
    }
}
