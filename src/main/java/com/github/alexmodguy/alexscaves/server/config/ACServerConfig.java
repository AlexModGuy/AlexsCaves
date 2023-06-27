package com.github.alexmodguy.alexscaves.server.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ACServerConfig {

    public final ForgeConfigSpec.DoubleValue biomeRarityScale;
    public final ForgeConfigSpec.DoubleValue biomeRarityElevation;
    public final ForgeConfigSpec.IntValue nucleeperFuseTime;
    public final ForgeConfigSpec.DoubleValue magneticTabletLootChance;
    public final ForgeConfigSpec.DoubleValue primordialTabletLootChance;
    public final ForgeConfigSpec.DoubleValue toxicTabletLootChance;
    public final ForgeConfigSpec.DoubleValue abyssalTabletLootChance;
    public final ForgeConfigSpec.DoubleValue forlornTabletLootChance;
    public ACServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("generation");
        biomeRarityScale = builder.comment("the value used to scale the noise function that determines the areas rare cave biomes can spawn in. A higher number means more spread out and larger rare cave biomes.").translation("biome_rarity_scale").defineInRange("biome_rarity_scale", 10.0D, 1D, Double.MAX_VALUE);
        biomeRarityElevation = builder.comment("the second used to raise the noise function by itself to the power of this number. It determines how rare and expansive the biomes are. A higher number means smaller cave biomes.").translation("biome_rarity_scale").defineInRange("biome_rarity_elevation", 1.0D, 0.1D, 10.0D);
        builder.pop();
        builder.push("mob-behavior");
        nucleeperFuseTime = builder.comment("How long (in game ticks) it takes for a nucleeper to explode.").translation("nucleeper_fuse_time").defineInRange("nucleeper_fuse_time", 300, 20, Integer.MAX_VALUE);
        builder.pop();
        builder.push("vanilla-changes");
        magneticTabletLootChance = builder.comment("percent chance of bastion having a cave tablet for magnetic caves in it's loot table:").translation("magnetic_tablet_loot_chance").defineInRange("magnetic_tablet_loot_chance", 0.45D, 0.0, 1.0D);
        primordialTabletLootChance = builder.comment("percent chance of suspicious sand having a cave tablet for primordial caves in it's loot table:").translation("primordial_tablet_loot_chance").defineInRange("primordial_tablet_loot_chance", 0.15D, 0.0, 1.0D);
        toxicTabletLootChance = builder.comment("percent chance of jungle temple having a cave tablet for toxic caves in it's loot table:").translation("toxic_tablet_loot_chance").defineInRange("toxic_tablet_loot_chance", 0.5D, 0.0, 1.0D);
        abyssalTabletLootChance = builder.comment("percent chance of underwater ruins having a cave tablet for abyssal chasm in it's loot table:").translation("abyssal_tablet_loot_chance").defineInRange("abyssal_tablet_loot_chance", 0.4D, 0.0, 1.0D);
        forlornTabletLootChance = builder.comment("percent chance of mansion having a cave tablet for forlorn hollows in it's loot table:").translation("forlorn_tablet_loot_chance").defineInRange("forlorn_tablet_loot_chance", 0.75D, 0.0, 1.0D);
        builder.pop();
    }
}
