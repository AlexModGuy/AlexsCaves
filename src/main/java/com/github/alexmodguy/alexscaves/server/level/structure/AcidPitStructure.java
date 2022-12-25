package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.AcidPitStructurePiece;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.DinoBowlStructurePiece;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class AcidPitStructure extends AbstractCaveGenerationStructure {

    private static final int BOWL_WIDTH_RADIUS = 80;
    private static final int BOWL_HEIGHT_RADIUS = 45;

    public static final Codec<AcidPitStructure> CODEC = simpleCodec((settings) -> new AcidPitStructure(settings));
    public AcidPitStructure(StructureSettings settings) {
        super(settings, ACBiomeRegistry.TOXIC_CAVES);
    }

    @Override
    protected StructurePiece createPiece(BlockPos offset, BlockPos center, int heightBlocks, int widthBlocks) {
        return new AcidPitStructurePiece(offset, center, heightBlocks, widthBlocks);
    }

    @Override
    public int getGenerateYHeight(WorldgenRandom random) {
        return random.nextInt(10) - 10;
    }

    @Override
    public int getWidthRadius(WorldgenRandom random) {
        return BOWL_WIDTH_RADIUS;
    }

    @Override
    public int getHeightRadius(WorldgenRandom random) {
        return BOWL_HEIGHT_RADIUS;
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.ACID_PIT.get();
    }
}
