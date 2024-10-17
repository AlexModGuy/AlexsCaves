package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.structure.LicowitchTowerStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ACStructurePieceRegistry {
    public static final DeferredRegister<StructurePieceType> DEF_REG = DeferredRegister.create(Registries.STRUCTURE_PIECE, AlexsCaves.MODID);

    public static final RegistryObject<StructurePieceType> UNDERGROUND_CABIN = DEF_REG.register("underground_cabin", () -> UndergroundCabinStructurePiece::new);
    public static final RegistryObject<StructurePieceType> FERROCAVE = DEF_REG.register("ferrocave", () -> FerrocaveStructurePiece::new);
    public static final RegistryObject<StructurePieceType> DINO_BOWL = DEF_REG.register("dino_bowl", () -> DinoBowlStructurePiece::new);
    public static final RegistryObject<StructurePieceType> VOLCANO = DEF_REG.register("volcano", () -> VolcanoStructurePiece::new);
    public static final RegistryObject<StructurePieceType> ACID_PIT = DEF_REG.register("acid_pit", () -> AcidPitStructurePiece::new);
    public static final RegistryObject<StructurePieceType> OCEAN_TRENCH = DEF_REG.register("ocean_trench", () -> OceanTrenchStructurePiece::new);
    public static final RegistryObject<StructurePieceType> ABYSSAL_RUINS = DEF_REG.register("abyssal_ruins", () -> AbyssalRuinsStructurePiece::new);
    public static final RegistryObject<StructurePieceType> FORLORN_CANYON = DEF_REG.register("forlorn_canyon", () -> ForlornCanyonStructurePiece::new);
    public static final RegistryObject<StructurePieceType> FORLORN_BRIDGE = DEF_REG.register("forlorn_bridge", () -> ForlornBridgeStructurePiece::new);
    public static final RegistryObject<StructurePieceType> CAKE_CAVE = DEF_REG.register("cake_cave", () -> CakeCaveStructurePiece::new);
    public static final RegistryObject<StructurePieceType> SODA_BOTTLE = DEF_REG.register("soda_bottle", () -> SodaBottleStructurePiece::new);
    public static final RegistryObject<StructurePieceType> DONUT_ARCH = DEF_REG.register("donut_arch", () -> DonutArchStructurePiece::new);
    public static final RegistryObject<StructurePieceType> LICOWITCH_TOWER = DEF_REG.register("licowitch_tower", () -> LicowitchTowerStructurePiece::new);
    public static final RegistryObject<StructurePieceType> GINGERBREAD_HOUSE = DEF_REG.register("gingerbread_house", () -> GingerbreadHousePiece::new);
    public static final RegistryObject<StructurePieceType> GINGERBREAD_ROAD = DEF_REG.register("gingerbread_road", () -> GingerbreadRoadPiece::new);


}
