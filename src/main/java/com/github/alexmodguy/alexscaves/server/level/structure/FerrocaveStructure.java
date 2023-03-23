package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.AcidPitStructurePiece;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.FerrocaveStructurePiece;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class FerrocaveStructure extends AbstractCaveGenerationStructure {

    public static final Codec<FerrocaveStructure> CODEC = simpleCodec((settings) -> new FerrocaveStructure(settings));
    public FerrocaveStructure(StructureSettings settings) {
        super(settings, ACBiomeRegistry.MAGNETIC_CAVES);
    }

    @Override
    protected StructurePiece createPiece(BlockPos offset, BlockPos center, int heightBlocks, int widthBlocks) {
        return new FerrocaveStructurePiece(offset, center, heightBlocks, widthBlocks);
    }

    @Override
    public int getGenerateYHeight(WorldgenRandom random, int x, int y) {
        return random.nextInt(40) - 20;
    }

    @Override
    public int getWidthRadius(WorldgenRandom random) {
        return 45 + random.nextInt(25);
    }

    @Override
    public int getHeightRadius(WorldgenRandom random, int seaLevel) {
        return 80 + random.nextInt(25);
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.FERROCAVE.get();
    }
}
