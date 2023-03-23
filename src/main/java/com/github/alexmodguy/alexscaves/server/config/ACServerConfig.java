package com.github.alexmodguy.alexscaves.server.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ACServerConfig {

    public final ForgeConfigSpec.DoubleValue biomeRarityScale;
    public final ForgeConfigSpec.DoubleValue biomeRarityElevation;
    public final ForgeConfigSpec.IntValue nucleeperFuseTime;
    public ACServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("generation");
        biomeRarityScale = builder.comment("the value used to scale the noise function that determines the areas rare cave biomes can spawn in. A higher number means more spread out and larger rare cave biomes.").translation("biome_rarity_scale").defineInRange("biome_rarity_scale", 10.0D, 1D, Double.MAX_VALUE);
        biomeRarityElevation = builder.comment("the second used to raise the noise function by itself to the power of this number. It determines how rare and expansive the biomes are. A higher number means smaller cave biomes.").translation("biome_rarity_scale").defineInRange("biome_rarity_elevation", 1.0D, 0.1D, 3.0D);
        builder.pop();
        builder.push("mob-behavior");
        nucleeperFuseTime = builder.comment("How long (in game ticks) it takes for a nucleeper to explode.").translation("nucleeper_fuse_time").defineInRange("nucleeper_fuse_time", 300, 20, Integer.MAX_VALUE);
        builder.pop();
    }
}
