package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.AbyssalRuinsStructurePiece;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class AbyssalRuinsStructure extends Structure {

    public static final Codec<AbyssalRuinsStructure> CODEC = simpleCodec((settings) -> new AbyssalRuinsStructure(settings));

    private static final ResourceLocation[] RUINS_NBT = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "abyssal_ruins_0"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "abyssal_ruins_1"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "abyssal_ruins_2"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "abyssal_ruins_3"),
    };

    public AbyssalRuinsStructure(StructureSettings settings) {
        super(settings);
    }

    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        LevelHeightAccessor levelHeight = context.heightAccessor();
        int y = context.chunkGenerator().getBaseHeight(context.chunkPos().getMinBlockX(), context.chunkPos().getMinBlockZ(), Heightmap.Types.OCEAN_FLOOR_WG, levelHeight, context.randomState());
        if (y > context.chunkGenerator().getSeaLevel()) {
            return Optional.empty();
        }
        BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), context.chunkGenerator().getMinY() + 15, context.chunkPos().getMinBlockZ());
        ResourceLocation res = Util.getRandom(RUINS_NBT, context.random());
        return Optional.of(new GenerationStub(blockpos, (piecesBuilder -> piecesBuilder.addPiece(new AbyssalRuinsStructurePiece(context.structureTemplateManager(), res, blockpos, rotation)))));
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.ABYSSAL_RUINS.get();
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }


}
