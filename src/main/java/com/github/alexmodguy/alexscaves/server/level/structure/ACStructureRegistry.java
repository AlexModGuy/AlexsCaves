package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ACStructureRegistry {

    public static final DeferredRegister<StructureType<?>> DEF_REG = DeferredRegister.create(Registries.STRUCTURE_TYPE, AlexsCaves.MODID);

    public static final RegistryObject<StructureType<DinoBowlStructure>> DINO_BOWL = DEF_REG.register("dino_bowl", () -> () -> DinoBowlStructure.CODEC);
    public static final RegistryObject<StructureType<AcidPitStructure>> ACID_PIT = DEF_REG.register("acid_pit", () -> () -> AcidPitStructure.CODEC);
}
