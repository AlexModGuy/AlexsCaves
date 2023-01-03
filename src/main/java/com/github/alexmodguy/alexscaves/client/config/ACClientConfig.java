package com.github.alexmodguy.alexscaves.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ACClientConfig {

    public final ForgeConfigSpec.BooleanValue screenShaking;
    public final ForgeConfigSpec.BooleanValue nuclearBombFlash;
    public final ForgeConfigSpec.BooleanValue biomeAmbientLight;
    public final ForgeConfigSpec.BooleanValue biomeAmbientLightColoring;
    public final ForgeConfigSpec.BooleanValue biomeSkyOverrides;
    public final ForgeConfigSpec.BooleanValue ambersolShines;
    public ACClientConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("visuals");
        screenShaking = builder.comment("whether to shake the screen from tremorsaurus stomping, nuclear explosions, etc.").translation("screen_shaking").define("screen_shaking", true);
        nuclearBombFlash = builder.comment("whether to make the screen flash white during nuclear explosions.").translation("nuclear_bomb_flash").define("nuclear_bomb_flash", true);
        biomeAmbientLight = builder.comment("true if some biomes, such as primordial caves, have ambient light that makes the biome easier to see in.").translation("biome_ambient_light").define("biome_ambient_light", true);
        biomeAmbientLightColoring = builder.comment("true if some biomes, such as toxic caves, apply a color to ambient light. May conflict with shaders.").translation("biome_ambient_light_coloring").define("biome_ambient_light_coloring", true);
        biomeSkyOverrides = builder.comment("true if some biomes, such as primordial caves, have an always well-lit sky when in them. May conflict with shaders.").translation("biome_sky_overrides").define("biome_sky_overrides", true);
        ambersolShines = builder.comment("true if ambersol block renders with rays of light emerging from it.").translation("ambersol_shines").define("ambersol_shines", true);
        builder.pop();

    }
}
