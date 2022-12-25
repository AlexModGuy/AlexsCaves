package com.github.alexmodguy.alexscaves.server.level.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractCaveGenerationStructure extends Structure {

    private final ResourceKey<Biome> matchingBiome;

    protected AbstractCaveGenerationStructure(StructureSettings settings, ResourceKey<Biome> matchingBiome) {
        super(settings);
        this.matchingBiome = matchingBiome;
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
        int k = getGenerateYHeight(context.random());
        return Optional.of(new Structure.GenerationStub(new BlockPos(i, k, j), builderConsumer));
    }

    private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        int i = context.chunkPos().getMinBlockX();
        int j = context.chunkPos().getMinBlockZ();
        int k = context.chunkGenerator().getSeaLevel();
        BlockPos center = new BlockPos(i, getGenerateYHeight(context.random()), j);
        int heightRad = getHeightRadius(context.random());
        int widthRad = getWidthRadius(context.random());
        int biomeUp = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.UP, center, heightRad) - 5;
        int biomeDown = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.DOWN, center, heightRad) - 5;
        BlockPos ground = center.below(biomeDown - 2);
        int biomeEast = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.EAST, ground, widthRad);
        int biomeWest = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.WEST, ground, widthRad);
        int biomeNorth = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.NORTH, ground, widthRad);
        int biomeSouth = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.SOUTH, ground, widthRad);
        int widthBlocks = (biomeEast + biomeWest + biomeNorth + biomeSouth) / 4;
        int heightBlocks = (biomeUp + biomeDown) / 2;
        int widthChunks = (int)Math.ceil((widthBlocks + 16) / 16F / 2F) + 2;
        int heightChunks = (int)Math.ceil((heightBlocks + 16) / 16F / 2F);
        for(int chunkX = -widthChunks; chunkX <= widthChunks; chunkX++){
            for(int chunkZ = -widthChunks; chunkZ <= widthChunks; chunkZ++){
                for(int chunkY = -heightChunks; chunkY <= heightChunks; chunkY++){
                    StructurePiece piece = createPiece(center.offset(new BlockPos(chunkX * 16, chunkY * 16, chunkZ * 16)), center, heightBlocks, widthBlocks);
                    builder.addPiece(piece);
                }
            }
        }
    }

    protected abstract StructurePiece createPiece(BlockPos offset, BlockPos center, int heightBlocks, int widthBlocks);

    private static Holder<Biome> getBiomeHolder(BiomeSource biomeSource, RandomState randomState, BlockPos pos){
        return biomeSource.getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()), randomState.sampler());
    }

    private int biomeContinuesInDirectionFor(BiomeSource biomeSource, RandomState randomState, Direction direction, BlockPos start, int cutoff){
        int i = 0;
        while(i < cutoff){
            BlockPos check = start.relative(direction, i);
            Holder<Biome> biomeHolder = getBiomeHolder(biomeSource, randomState, check);
            if(!biomeHolder.is(matchingBiome)){
                break;
            }
            i += 16;
        }
        return Math.min(i, cutoff);
    }

    public abstract int getGenerateYHeight(WorldgenRandom random);
    public abstract int getWidthRadius(WorldgenRandom random);
    public abstract int getHeightRadius(WorldgenRandom random);

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.RAW_GENERATION;
    }
}
