package com.github.alexmodguy.alexscaves.server.level.surface;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class ACSurfaceRules {

    public static void setup() {
        SurfaceRulesManager.registerOverworldSurfaceRule(SurfaceRules.isBiome(ACBiomeRegistry.MAGNETIC_CAVES), createMagneticCavesRules());
        SurfaceRulesManager.registerOverworldSurfaceRule(SurfaceRules.isBiome(ACBiomeRegistry.PRIMORDIAL_CAVES), createPrimordialCavesRules());
        SurfaceRulesManager.registerOverworldSurfaceRule(SurfaceRules.isBiome(ACBiomeRegistry.TOXIC_CAVES), createToxicCavesRules());
        SurfaceRulesManager.registerOverworldSurfaceRule(SurfaceRules.isBiome(ACBiomeRegistry.ABYSSAL_CHASM), createAbyssalChasmRules());
        SurfaceRulesManager.registerOverworldSurfaceRule(SurfaceRules.isBiome(ACBiomeRegistry.FORLORN_HOLLOWS), createForlornHollowsRules());
    }

    public static SurfaceRules.RuleSource createMagneticCavesRules() {
        SurfaceRules.RuleSource galena = SurfaceRules.state(ACBlockRegistry.GALENA.get().defaultBlockState());
        SurfaceRules.RuleSource scarlet = SurfaceRules.state(ACBlockRegistry.ENERGIZED_GALENA_SCARLET.get().defaultBlockState());
        SurfaceRules.RuleSource azure = SurfaceRules.state(ACBlockRegistry.ENERGIZED_GALENA_AZURE.get().defaultBlockState());
        SurfaceRules.RuleSource neutral = SurfaceRules.state(ACBlockRegistry.ENERGIZED_GALENA_NEUTRAL.get().defaultBlockState());
        SurfaceRules.ConditionSource azureCondition = ACSurfaceRuleConditionRegistry.simplexCondition(-0.025F, 0.025F, 90, 1F, 0);
        SurfaceRules.ConditionSource scarletCondition = ACSurfaceRuleConditionRegistry.simplexCondition(-0.025F, 0.025F, 90, 1F, 1);
        return SurfaceRules.sequence(bedrock(), SurfaceRules.ifTrue(azureCondition, SurfaceRules.ifTrue(scarletCondition, neutral)), SurfaceRules.ifTrue(scarletCondition, scarlet), SurfaceRules.ifTrue(azureCondition, azure), galena);
    }

    public static SurfaceRules.RuleSource createPrimordialCavesRules() {
        SurfaceRules.RuleSource limestone = SurfaceRules.state(ACBlockRegistry.LIMESTONE.get().defaultBlockState());
        SurfaceRules.RuleSource grass = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
        SurfaceRules.RuleSource dirt = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
        SurfaceRules.RuleSource packedMud = SurfaceRules.state(Blocks.PACKED_MUD.defaultBlockState());
        SurfaceRules.RuleSource dirtOrPackedMud = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.12D, 0.2D), packedMud), dirt);
        SurfaceRules.ConditionSource isUnderwater = SurfaceRules.waterBlockCheck(0, 0);
        SurfaceRules.RuleSource grassWaterChecked = SurfaceRules.sequence(SurfaceRules.ifTrue(isUnderwater, grass), dirtOrPackedMud);
        SurfaceRules.RuleSource floorRules = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grassWaterChecked), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, dirtOrPackedMud));
        return SurfaceRules.sequence(bedrock(), floorRules, createSandstoneBands(15, 1, 20), limestone);
    }

    public static SurfaceRules.RuleSource createToxicCavesRules() {
        SurfaceRules.RuleSource radrock = SurfaceRules.state(ACBlockRegistry.RADROCK.get().defaultBlockState());
        return SurfaceRules.sequence(bedrock(), radrock);
    }

    public static SurfaceRules.RuleSource createAbyssalChasmRules() {
        SurfaceRules.RuleSource abyssmarine = SurfaceRules.state(ACBlockRegistry.ABYSSMARINE.get().defaultBlockState());
        SurfaceRules.RuleSource deepslate = SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState());
        SurfaceRules.RuleSource stone = SurfaceRules.state(Blocks.STONE.defaultBlockState());
        SurfaceRules.ConditionSource normalDeepslateCondition = SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8));
        SurfaceRules.RuleSource stoneOrDeepslate = SurfaceRules.sequence(SurfaceRules.ifTrue(normalDeepslateCondition, deepslate), stone);
        return SurfaceRules.sequence(bedrock(), SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, stoneOrDeepslate), SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), deepslate), abyssmarine);
    }

    public static SurfaceRules.RuleSource createForlornHollowsRules() {
        SurfaceRules.RuleSource mud = SurfaceRules.state(Blocks.PACKED_MUD.defaultBlockState());
        SurfaceRules.RuleSource guanostone = SurfaceRules.state(ACBlockRegistry.GUANOSTONE.get().defaultBlockState());
        SurfaceRules.RuleSource corpolith = SurfaceRules.state(ACBlockRegistry.COPROLITH.get().defaultBlockState());
        SurfaceRules.ConditionSource corpolithCondition = ACSurfaceRuleConditionRegistry.simplexCondition(-0.2F, 0.4F, 40, 6F, 3);
        return SurfaceRules.sequence(bedrock(), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, mud), SurfaceRules.ifTrue(corpolithCondition, corpolith), guanostone);
    }

    private static SurfaceRules.RuleSource bedrock() {
        SurfaceRules.RuleSource bedrock = SurfaceRules.state(Blocks.BEDROCK.defaultBlockState());
        SurfaceRules.ConditionSource bedrockCondition = SurfaceRules.verticalGradient("bedrock", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5));
        return SurfaceRules.ifTrue(bedrockCondition, bedrock);
    }

    private static SurfaceRules.RuleSource createSandstoneBands(int layers, int layerThickness, int layerDistance) {
        SurfaceRules.RuleSource sandstone = SurfaceRules.state(Blocks.SANDSTONE.defaultBlockState());
        SurfaceRules.RuleSource[] ruleSources = new SurfaceRules.RuleSource[layers];
        for (int i = 1; i <= layers; i++) {
            int yDown = i * layerDistance;
            int extra = i % 3 == 0 ? 1 : 0;
            SurfaceRules.ConditionSource layer1 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62 - yDown), -1);
            SurfaceRules.ConditionSource layer2 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62 + extra + layerThickness - yDown), 0);
            ruleSources[i - 1] = SurfaceRules.ifTrue(layer1, SurfaceRules.ifTrue(SurfaceRules.not(layer2), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.7D, 0.8D), sandstone)));
        }
        return SurfaceRules.sequence(ruleSources);
    }

}
