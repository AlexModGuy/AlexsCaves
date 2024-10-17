package com.github.alexmodguy.alexscaves.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ACClientConfig {

    public final ForgeConfigSpec.BooleanValue caveMapsVisibleInThirdPerson;
    public final ForgeConfigSpec.BooleanValue screenShaking;
    public final ForgeConfigSpec.BooleanValue emissiveBlockModels;
    public final ForgeConfigSpec.BooleanValue nuclearBombFlash;
    public final ForgeConfigSpec.BooleanValue biomeAmbientLight;
    public final ForgeConfigSpec.BooleanValue biomeAmbientLightColoring;
    public final ForgeConfigSpec.BooleanValue biomeSkyOverrides;
    public final ForgeConfigSpec.BooleanValue biomeSkyFogOverrides;
    public final ForgeConfigSpec.BooleanValue biomeWaterFogOverrides;
    public final ForgeConfigSpec.BooleanValue ambersolShines;
    public final ForgeConfigSpec.BooleanValue radiationGlowEffect;
    public final ForgeConfigSpec.BooleanValue sugarRushSaturationEffect;
    public final ForgeConfigSpec.IntValue subterranodonIndicatorX;
    public final ForgeConfigSpec.IntValue subterranodonIndicatorY;
    public final ForgeConfigSpec.BooleanValue nuclearBombMufflesSounds;

    public ACClientConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("visuals");
        caveMapsVisibleInThirdPerson = builder.comment("whether to cave maps are visible when held by players from the third-person perspective.").translation("cave_maps_visible_in_third_person").define("cave_maps_visible_in_third_person", true);
        screenShaking = builder.comment("whether to shake the screen from tremorsaurus stomping, nuclear explosions, etc.").translation("screen_shaking").define("screen_shaking", true);
        emissiveBlockModels = builder.comment("true if some block models, like uranium ore or abyssmarine bricks render as fullbright. May increase load time, no gameplay performance impact.").translation("emissive_block_models").define("emissive_block_models", true);
        nuclearBombFlash = builder.comment("whether to make the screen flash white during nuclear explosions.").translation("nuclear_bomb_flash").define("nuclear_bomb_flash", true);
        biomeAmbientLight = builder.comment("true if some biomes, such as primordial caves, have ambient light that makes the biome easier to see in.").translation("biome_ambient_light").define("biome_ambient_light", true);
        biomeAmbientLightColoring = builder.comment("true if some biomes, such as toxic caves, apply a color to ambient light. May conflict with shaders.").translation("biome_ambient_light_coloring").define("biome_ambient_light_coloring", true);
        biomeSkyOverrides = builder.comment("true if some biomes, such as primordial caves, have an always well-lit sky when in them. May conflict with shaders.").translation("biome_sky_overrides").define("biome_sky_overrides", true);
        biomeSkyFogOverrides = builder.comment("true if some biomes, such as toxic caves, have an thicker fog to them. May conflict with shaders.").translation("biome_sky_fog_overrides").define("biome_sky_fog_overrides", true);
        biomeWaterFogOverrides = builder.comment("true if some biomes, such as abyssal chasm, have an thicker water fog to them. May conflict with shaders.").translation("biome_water_fog_overrides").define("biome_sky_fog_overrides", true);
        ambersolShines = builder.comment("true if ambersol block renders with rays of light emerging from it.").translation("ambersol_shines").define("ambersol_shines", true);
        radiationGlowEffect = builder.comment("true if irradiated effect makes mobs glow. May conflict with shaders.").translation("radiation_glow_effect").define("radiation_glow_effect", true);
        subterranodonIndicatorX = builder.comment("determines how far to the left the subterranodon flight indicator renders on the screen when mounted. Negative numbers will render it on the right. ").translation("subterranodon_indicator_x").defineInRange("subterranodon_indicator_x", 22, -12000, 12000);
        subterranodonIndicatorY = builder.comment("determines how far from bottom the subterranodon flight indicator renders on the screen when mounted.").translation("subterranodon_indicator_y").defineInRange("subterranodon_indicator_y", 6, -12000, 12000);
        sugarRushSaturationEffect = builder.comment("true if sugar rush makes the world more saturated. May conflict with shaders.").translation("sugar_rush_saturation_effect").define("sugar_rush_saturation_effect", true);
        builder.pop();
        builder.push("audio");
        nuclearBombMufflesSounds = builder.comment("whether nuclear explosions briefly muffle other sounds.").translation("nuclear_bomb_muffles_sounds").define("nuclear_bomb_muffles_sounds", true);
        builder.pop();

    }
}
