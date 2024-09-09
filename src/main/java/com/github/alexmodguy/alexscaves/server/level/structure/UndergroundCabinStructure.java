package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.UndergroundCabinStructurePiece;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
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

public class UndergroundCabinStructure extends Structure {

    public static final Codec<UndergroundCabinStructure> CODEC = simpleCodec((settings) -> new UndergroundCabinStructure(settings));

    private static final ResourceLocation[] CABIN_NBT = new ResourceLocation[]{
            new ResourceLocation(AlexsCaves.MODID, "underground_cabin_0"),
            new ResourceLocation(AlexsCaves.MODID, "underground_cabin_1"),
            new ResourceLocation(AlexsCaves.MODID, "underground_cabin_2"),
            new ResourceLocation(AlexsCaves.MODID, "underground_cabin_3"),
            new ResourceLocation(AlexsCaves.MODID, "underground_cabin_4"),
            new ResourceLocation(AlexsCaves.MODID, "underground_cabin_5"),
            new ResourceLocation(AlexsCaves.MODID, "underground_cabin_6")
    };

    public UndergroundCabinStructure(StructureSettings settings) {
        super(settings);
    }

    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        LevelHeightAccessor levelHeight = context.heightAccessor();
        int y = context.chunkGenerator().getBaseHeight(context.chunkPos().getMinBlockX(), context.chunkPos().getMinBlockZ(), Heightmap.Types.OCEAN_FLOOR_WG, levelHeight, context.randomState()) - 20;
        int maxHeight = y - 14 - context.random().nextInt(15);
        BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), maxHeight, context.chunkPos().getMinBlockZ());
        if(context.biomeSource().getNoiseBiome(blockpos.getX(), blockpos.getY(), blockpos.getZ(), context.randomState().sampler()).is(ACTagRegistry.HAS_NO_UNDERGROUND_CABINS)){
            return Optional.empty();
        }
        ResourceLocation res = Util.getRandom(CABIN_NBT, context.random());
        return Optional.of(new GenerationStub(blockpos, (piecesBuilder -> piecesBuilder.addPiece(new UndergroundCabinStructurePiece(context.structureTemplateManager(), res, blockpos, rotation)))));
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.UNDERGROUND_CABIN.get();
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }


}
