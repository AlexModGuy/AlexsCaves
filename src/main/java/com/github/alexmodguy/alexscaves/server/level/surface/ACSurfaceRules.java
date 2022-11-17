package com.github.alexmodguy.alexscaves.server.level.surface;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ACSurfaceRules {

    public static SurfaceRules.RuleSource createMagneticCavesRules() {
        SurfaceRules.RuleSource galena = SurfaceRules.state(ACBlockRegistry.GALENA.get().defaultBlockState());
        SurfaceRules.RuleSource obsidian = SurfaceRules.state(Blocks.OBSIDIAN.defaultBlockState());
        return SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.05D, 0.05D), obsidian), galena);
    }

    public static void init() {
        SurfaceRulesManager.registerOverworldSurfaceRule(SurfaceRules.isBiome(ACBiomeRegistry.MAGNETIC_CAVES), createMagneticCavesRules());
    }
}
