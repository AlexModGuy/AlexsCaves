package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.ForlornBridgeStructurePiece;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public class ForlornBridgeStructure extends Structure {

    public static int BRIDGE_SECTION_LENGTH = 6;
    public static int BRIDGE_SECTION_WIDTH = 4;

    public static final Codec<ForlornBridgeStructure> CODEC = simpleCodec((settings) -> new ForlornBridgeStructure(settings));

    protected ForlornBridgeStructure(StructureSettings settings) {
        super(settings);
    }

    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        return atYCaveBiomePoint(context, Heightmap.Types.OCEAN_FLOOR_WG, (builder) -> {
            this.generatePieces(builder, context);
        });
    }

    protected Optional<Structure.GenerationStub> atYCaveBiomePoint(Structure.GenerationContext context, Heightmap.Types heightMap, Consumer<StructurePiecesBuilder> builderConsumer) {
        ChunkPos chunkpos = context.chunkPos();
        int i = chunkpos.getMiddleBlockX();
        int j = chunkpos.getMiddleBlockZ();
        int k = ForlornCanyonStructure.BOWL_Y_CENTER;
        return Optional.of(new Structure.GenerationStub(new BlockPos(i, k, j), builderConsumer));
    }

    public void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        int i = context.chunkPos().getMinBlockX();
        int j = context.chunkPos().getMinBlockZ();
        int k = context.chunkGenerator().getSeaLevel();
        BlockPos xzCoords = new BlockPos(i, ForlornCanyonStructure.BOWL_Y_CENTER, j);
        int biomeUp = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.UP, xzCoords, 32);
        int biomeDown = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.DOWN, xzCoords, 32);
        BlockPos center = xzCoords.below(biomeDown).above(worldgenrandom.nextInt(Math.max(biomeUp, 10)));
        Direction bridgeDirection = Util.getRandom(ACMath.HORIZONTAL_DIRECTIONS, worldgenrandom);
        int biomeForwards = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), bridgeDirection, center, 32 + worldgenrandom.nextInt(6) * 16);
        int biomeBackwards = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), bridgeDirection.getOpposite(), center, 32 + worldgenrandom.nextInt(6) * 16);
        int maxSections = (int) Math.ceil((biomeBackwards + biomeForwards) / BRIDGE_SECTION_LENGTH);
        for(int section = 0; section <= maxSections; section++){
            BlockPos at = center.relative(bridgeDirection, section * BRIDGE_SECTION_LENGTH - BRIDGE_SECTION_LENGTH/2 - biomeBackwards);
            builder.addPiece(new ForlornBridgeStructurePiece(at, section, maxSections, bridgeDirection));
        }
    }
    private static Holder<Biome> getBiomeHolder(BiomeSource biomeSource, RandomState randomState, BlockPos pos){
        return biomeSource.getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()), randomState.sampler());
    }

    protected int biomeContinuesInDirectionFor(BiomeSource biomeSource, RandomState randomState, Direction direction, BlockPos start, int cutoff){
        int i = 0;
        while(i < cutoff){
            BlockPos check = start.relative(direction, i);
            Holder<Biome> biomeHolder = getBiomeHolder(biomeSource, randomState, check);
            if(!biomeHolder.is(ACBiomeRegistry.FORLORN_HOLLOWS)){
                break;
            }
            i += 16;
        }
        return Math.min(i, cutoff);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.FORLORN_BRIDGE.get();
    }
}

