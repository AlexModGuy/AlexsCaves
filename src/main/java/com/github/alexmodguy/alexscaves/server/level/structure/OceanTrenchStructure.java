package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.OceanTrenchStructurePiece;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanTrenchStructure extends AbstractCaveGenerationStructure {

    private static final int BOWL_WIDTH_RADIUS = 150;

    public static final Codec<OceanTrenchStructure> CODEC = simpleCodec((settings) -> new OceanTrenchStructure(settings));

    public OceanTrenchStructure(StructureSettings settings) {
        super(settings, ACBiomeRegistry.ABYSSAL_CHASM);
    }

    @Override
    protected StructurePiece createPiece(BlockPos offset, BlockPos center, int heightBlocks, int widthBlocks, RandomState randomState) {
        return new OceanTrenchStructurePiece(offset, center, heightBlocks, widthBlocks);
    }

    @Override
    public void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        int i = context.chunkPos().getMinBlockX();
        int j = context.chunkPos().getMinBlockZ();
        BlockPos center = new BlockPos(i, getGenerateYHeight(context.random(), i, j), j);
        int heightRad = getHeightRadius(context.random(), context.chunkGenerator().getSeaLevel());
        int widthRad = getWidthRadius(context.random());
        int biomeUp = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.UP, center, heightRad) + getYExpandUp();
        int biomeDown = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.DOWN, center, heightRad) + getYExpandDown();
        BlockPos ground = center.below(biomeDown - 2);
        int biomeEast = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.EAST, ground, widthRad) + 32;
        int biomeWest = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.WEST, ground, widthRad) + 32;
        int biomeNorth = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.NORTH, ground, widthRad) + 32;
        int biomeSouth = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.SOUTH, ground, widthRad) + 32;
        int widthBlocks = (biomeEast + biomeWest + biomeNorth + biomeSouth) / 4;
        int heightBlocks = (biomeUp + biomeDown) / 2;
        int widthChunks = (int) Math.ceil((widthBlocks + 16) / 16F / 2F) + 3;
        for (int chunkX = -widthChunks; chunkX <= widthChunks; chunkX++) {
            for (int chunkZ = -widthChunks; chunkZ <= widthChunks; chunkZ++) {
                StructurePiece piece = createPiece(center.offset(new BlockPos(chunkX * 16, 0, chunkZ * 16)), center, heightBlocks, widthBlocks, context.randomState());
                builder.addPiece(piece);
            }
        }
    }

    @Override
    public int getGenerateYHeight(WorldgenRandom random, int x, int y) {
        return -16;
    }

    @Override
    public int getWidthRadius(WorldgenRandom random) {
        return BOWL_WIDTH_RADIUS;
    }

    @Override
    public int getHeightRadius(WorldgenRandom random, int seaLevel) {
        return 64;
    }

    @Override
    protected int getYExpandUp() {
        return 16;
    }

    @Override
    protected int getYExpandDown() {
        return 16;
    }

    protected int getHeightOverride(int heightIn) {
        return Integer.MAX_VALUE;
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.OCEAN_TRENCH.get();
    }
}
