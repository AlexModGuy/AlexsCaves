package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.CakeCaveStructurePiece;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.ForlornCanyonStructurePiece;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public class CakeCaveStructure extends AbstractCaveGenerationStructure {
    public static final Codec<CakeCaveStructure> CODEC = simpleCodec((settings) -> new CakeCaveStructure(settings));

    public CakeCaveStructure(StructureSettings settings) {
        super(settings, ACBiomeRegistry.CANDY_CAVITY);
    }

    @Override
    protected StructurePiece createPiece(BlockPos offset, BlockPos center, int heightBlocks, int widthBlocks, RandomState randomState) {
        return new CakeCaveStructurePiece(offset, center, heightBlocks, widthBlocks);
    }

    @Override
    public int getGenerateYHeight(WorldgenRandom random, int x, int y) {
        return -48;
    }

    @Override
    public int getWidthRadius(WorldgenRandom random) {
        return 100;
    }

    @Override
    public int getHeightRadius(WorldgenRandom random, int seaLevel) {
        return 105;
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.CAKE_CAVE.get();
    }
}
