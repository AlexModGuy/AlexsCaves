package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ACStructurePieceRegistry {
    public static final DeferredRegister<StructurePieceType> DEF_REG = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, AlexsCaves.MODID);

    public static final RegistryObject<StructurePieceType> DINO_BOWL = DEF_REG.register("dino_bowl", () -> DinoBowlStructurePiece::new);

}
