package com.github.alexmodguy.alexscaves.server.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ACServerConfig {

    public final ForgeConfigSpec.DoubleValue caveBiomeMeanWidth;
    public final ForgeConfigSpec.IntValue caveBiomeMeanSeparation;
    public final ForgeConfigSpec.DoubleValue caveBiomeSpacingRandomness;
    public final ForgeConfigSpec.IntValue nucleeperFuseTime;
    public final ForgeConfigSpec.BooleanValue watcherPossession;
    public final ForgeConfigSpec.IntValue amberMonolithMeanTime;
    public final ForgeConfigSpec.IntValue caveMapSearchDistance;
    public final ForgeConfigSpec.BooleanValue totemOfPossessionPlayers;
    public final ForgeConfigSpec.DoubleValue magneticTabletLootChance;
    public final ForgeConfigSpec.DoubleValue primordialTabletLootChance;
    public final ForgeConfigSpec.DoubleValue toxicTabletLootChance;
    public final ForgeConfigSpec.DoubleValue abyssalTabletLootChance;
    public final ForgeConfigSpec.DoubleValue forlornTabletLootChance;

    public ACServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("generation");
        caveBiomeMeanWidth = builder.comment("Average radius (in blocks) of an Alex's Caves cave biome.").translation("cave_biome_mean_width").defineInRange("cave_biome_mean_width", 235D, 10.0D, Double.MAX_VALUE);
        caveBiomeMeanSeparation = builder.comment("Average separation (in blocks) between each Alex's Caves cave biome.").translation("cave_biome_mean_separation").defineInRange("cave_biome_mean_separation", 4000, 50, Integer.MAX_VALUE);
        caveBiomeSpacingRandomness = builder.comment("Average spacing in between Alex's Caves cave biomes. 0 = all biomes nearly perfectly equidistant. 1 = biomes completely randomly spread out, sometimes next to eachother.").translation("cave_biome_spacing_randomness").defineInRange("cave_biome_spacing_randomness", 0.2D, 0, 1D);
        builder.pop();
        builder.push("mob-behavior");
        nucleeperFuseTime = builder.comment("How long (in game ticks) it takes for a nucleeper to explode.").translation("nucleeper_fuse_time").defineInRange("nucleeper_fuse_time", 300, 20, Integer.MAX_VALUE);
        watcherPossession = builder.comment("Whether the Watcher can take control of the camera.").translation("watcher_possession").define("watcher_possession", true);
        builder.pop();
        builder.push("block-behavior");
        amberMonolithMeanTime = builder.comment("How long (in game ticks) it usually takes for an amber monolith to spawn an animal.").translation("amber_monolith_mean_time").defineInRange("amber_monolith_mean_time", 32000, 1000, Integer.MAX_VALUE);
        builder.pop();
        builder.push("item-behavior");
        caveMapSearchDistance = builder.comment("How far away for cave biomes the Cave Map will search for.").translation("cave_map_search_distance").defineInRange("cave_map_search_distance", 9200, 6400, Integer.MAX_VALUE);
        totemOfPossessionPlayers = builder.comment("Whether the Totem of Possession can be applied to players.").translation("totem_of_possession_works_on_players").define("totem_of_possession_works_on_players", true);
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
