package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.DinoBowlStructurePiece;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class DinoBowlStructure extends AbstractCaveGenerationStructure {

    private static final int BOWL_WIDTH_RADIUS = 100;
    private static final int BOWL_HEIGHT_RADIUS = 80;

    public static final int BOWL_Y_CENTER = -1;

    public static final Codec<DinoBowlStructure> CODEC = simpleCodec((settings) -> new DinoBowlStructure(settings));

    public DinoBowlStructure(StructureSettings settings) {
        super(settings, ACBiomeRegistry.PRIMORDIAL_CAVES);
    }

    @Override
    protected StructurePiece createPiece(BlockPos offset, BlockPos center, int heightBlocks, int widthBlocks, RandomState randomState) {
        return new DinoBowlStructurePiece(offset, center, heightBlocks, widthBlocks);
    }

    @Override
    public int getGenerateYHeight(WorldgenRandom random, int x, int y) {
        return BOWL_Y_CENTER;
    }

    @Override
    public int getWidthRadius(WorldgenRandom random) {
        return BOWL_WIDTH_RADIUS;
    }

    @Override
    public int getHeightRadius(WorldgenRandom random, int seaLevel) {
        return BOWL_HEIGHT_RADIUS;
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.DINO_BOWL.get();
    }
}
