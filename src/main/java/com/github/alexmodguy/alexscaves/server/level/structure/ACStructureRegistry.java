package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ACStructureRegistry {

    public static final DeferredRegister<StructureType<?>> DEF_REG = DeferredRegister.create(Registries.STRUCTURE_TYPE, AlexsCaves.MODID);

    public static final RegistryObject<StructureType<UndergroundCabinStructure>> UNDERGROUND_CABIN = DEF_REG.register("underground_cabin", () -> () -> UndergroundCabinStructure.CODEC);
    public static final RegistryObject<StructureType<FerrocaveStructure>> FERROCAVE = DEF_REG.register("ferrocave", () -> () -> FerrocaveStructure.CODEC);
    public static final RegistryObject<StructureType<VolcanoStructure>> VOLCANO = DEF_REG.register("volcano", () -> () -> VolcanoStructure.CODEC);
    public static final RegistryObject<StructureType<DinoBowlStructure>> DINO_BOWL = DEF_REG.register("dino_bowl", () -> () -> DinoBowlStructure.CODEC);
    public static final RegistryObject<StructureType<AcidPitStructure>> ACID_PIT = DEF_REG.register("acid_pit", () -> () -> AcidPitStructure.CODEC);
    public static final RegistryObject<StructureType<OceanTrenchStructure>> OCEAN_TRENCH = DEF_REG.register("ocean_trench", () -> () -> OceanTrenchStructure.CODEC);
    public static final RegistryObject<StructureType<AbyssalRuinsStructure>> ABYSSAL_RUINS = DEF_REG.register("abyssal_ruins", () -> () -> AbyssalRuinsStructure.CODEC);
    public static final RegistryObject<StructureType<ForlornCanyonStructure>> FORLORN_CANYON = DEF_REG.register("forlorn_canyon", () -> () -> ForlornCanyonStructure.CODEC);
    public static final RegistryObject<StructureType<ForlornBridgeStructure>> FORLORN_BRIDGE = DEF_REG.register("forlorn_bridge", () -> () -> ForlornBridgeStructure.CODEC);
}
