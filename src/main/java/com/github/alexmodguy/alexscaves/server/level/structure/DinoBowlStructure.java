package com.github.alexmodguy.alexscaves.server.level.structure;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public class DinoBowlStructure extends AbstractCaveGenerationStructure {

    private static final int BOWL_WIDTH_RADIUS = 100;
    private static final int BOWL_HEIGHT_RADIUS = 55;

    private static final int BOWL_Y_CENTER = -6;

    public static final Codec<DinoBowlStructure> CODEC = simpleCodec((settings) -> new DinoBowlStructure(settings));
    public DinoBowlStructure(StructureSettings settings) {
        super(settings, ACBiomeRegistry.PRIMORDIAL_CAVES);
    }

    @Override
    protected StructurePiece createPiece(BlockPos offset, BlockPos center, int heightBlocks, int widthBlocks) {
        return new DinoBowlStructurePiece(offset, center, heightBlocks, widthBlocks);
    }

    @Override
    public int getGenerateYHeight() {
        return BOWL_Y_CENTER;
    }

    @Override
    public int getWidthRadius() {
        return BOWL_WIDTH_RADIUS;
    }

    @Override
    public int getHeightRadius() {
        return BOWL_HEIGHT_RADIUS;
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.DINO_BOWL.get();
    }
}
