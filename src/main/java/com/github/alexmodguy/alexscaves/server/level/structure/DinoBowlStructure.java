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
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public class DinoBowlStructure extends Structure {

    private static final int BOWL_WIDTH_RADIUS = 100;
    private static final int BOWL_HEIGHT_RADIUS = 55;

    private static final int BOWL_Y_CENTER = -5;

    public static final Codec<DinoBowlStructure> CODEC = simpleCodec((settings) -> new DinoBowlStructure(settings));
    public DinoBowlStructure(StructureSettings settings) {
        super(settings);
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        return atYCaveBiomePoint(context, Heightmap.Types.OCEAN_FLOOR_WG, (builder) -> {
            this.generatePieces(builder, context);
        });
    }

    protected static Optional<Structure.GenerationStub> atYCaveBiomePoint(Structure.GenerationContext context, Heightmap.Types heightMap, Consumer<StructurePiecesBuilder> builderConsumer) {
        ChunkPos chunkpos = context.chunkPos();
        int i = chunkpos.getMiddleBlockX();
        int j = chunkpos.getMiddleBlockZ();
        int k = BOWL_Y_CENTER;
        return Optional.of(new Structure.GenerationStub(new BlockPos(i, k, j), builderConsumer));
    }

    private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        int i = context.chunkPos().getMinBlockX();
        int j = context.chunkPos().getMinBlockZ();
        int k = context.chunkGenerator().getSeaLevel();
        BlockPos center = new BlockPos(i, BOWL_Y_CENTER, j);
        int biomeEast = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.EAST, center, BOWL_WIDTH_RADIUS);
        int biomeWest = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.WEST, center, BOWL_WIDTH_RADIUS);
        int biomeNorth = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.NORTH, center, BOWL_WIDTH_RADIUS);
        int biomeSouth = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.SOUTH, center, BOWL_WIDTH_RADIUS);
        int biomeUp = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.UP, center, BOWL_HEIGHT_RADIUS) - 5;
        int biomeDown = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.DOWN, center, BOWL_HEIGHT_RADIUS);
        int widthBlocks = (biomeEast + biomeWest + biomeNorth + biomeSouth) / 4;
        int heightBlocks = (biomeUp + biomeDown) / 2;
        int widthChunks = (int)Math.ceil((widthBlocks + 16) / 16F / 2F) + 2;
        int heightChunks = (int)Math.ceil((heightBlocks + 16) / 16F / 2F);
        for(int chunkX = -widthChunks; chunkX <= widthChunks; chunkX++){
            for(int chunkZ = -widthChunks; chunkZ <= widthChunks; chunkZ++){
                for(int chunkY = -heightChunks; chunkY <= heightChunks; chunkY++){
                    DinoBowlStructurePiece piece = new DinoBowlStructurePiece(center.offset(new BlockPos(chunkX * 16, chunkY * 16, chunkZ * 16)), center, heightBlocks, widthBlocks);
                    builder.addPiece(piece);
                }
            }
        }
    }

    private static Holder<Biome> getBiomeHolder(BiomeSource biomeSource, RandomState randomState, BlockPos pos){
        return biomeSource.getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()), randomState.sampler());
    }


    private static int biomeContinuesInDirectionFor(BiomeSource biomeSource, RandomState randomState, Direction direction, BlockPos start, int cutoff){
        int i = 0;
        while(i < cutoff){
            BlockPos check = start.relative(direction, i);
            Holder<Biome> biomeHolder = getBiomeHolder(biomeSource, randomState, check);
            if(!biomeHolder.is(ACBiomeRegistry.PRIMORDIAL_CAVES)){
               break;
            }
            i += 16;
        }
        return Math.min(i, cutoff);
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.DINO_BOWL.get();
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.RAW_GENERATION;
    }
}
