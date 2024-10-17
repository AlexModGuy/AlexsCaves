package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.DonutArchStructurePiece;
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

public class DonutArchStructure extends Structure {
    public static final Codec<DonutArchStructure> CODEC = simpleCodec((settings) -> new DonutArchStructure(settings));

    public static final int DONUT_SECTION_WIDTH = 16;

    protected DonutArchStructure(StructureSettings settings) {
        super(settings);
    }

    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return atYCaveBiomePoint(context, Heightmap.Types.OCEAN_FLOOR_WG, (builder) -> {
            this.generatePieces(builder, context);
        });
    }

    protected Optional<GenerationStub> atYCaveBiomePoint(GenerationContext context, Heightmap.Types heightMap, Consumer<StructurePiecesBuilder> builderConsumer) {
        ChunkPos chunkpos = context.chunkPos();
        int i = chunkpos.getMiddleBlockX();
        int j = chunkpos.getMiddleBlockZ();
        int k = -30;
        return Optional.of(new GenerationStub(new BlockPos(i, k, j), builderConsumer));
    }

    public void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        int i = context.chunkPos().getMinBlockX();
        int j = context.chunkPos().getMinBlockZ();
        int k = context.chunkGenerator().getSeaLevel();
        BlockPos xzCoords = new BlockPos(i, -30, j);
        int biomeUp = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.UP, xzCoords, 25);
        int biomeDown = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), Direction.DOWN, xzCoords, 16);
        BlockPos center = xzCoords.below(biomeDown).above(5 + worldgenrandom.nextInt(Math.max(biomeUp, 10)));
        Direction donutFacing = Util.getRandom(ACMath.HORIZONTAL_DIRECTIONS, worldgenrandom);
        int donutRadius = 32;
        int biomeLeft = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), donutFacing.getClockWise(), center, donutRadius);
        int biomeRight = biomeContinuesInDirectionFor(context.biomeSource(), context.randomState(), donutFacing.getCounterClockWise(), center, donutRadius);
        int widthChunks = (int) Math.ceil(biomeRight + biomeLeft) / 2 / DONUT_SECTION_WIDTH;
        int frostingType = worldgenrandom.nextInt(3);
        for (int chunkXZ = -widthChunks; chunkXZ <= widthChunks; chunkXZ++) {
            for (int chunkY = -widthChunks; chunkY <= widthChunks; chunkY++) {
                BlockPos at = center.relative(donutFacing, chunkXZ * DONUT_SECTION_WIDTH).above(chunkY * DONUT_SECTION_WIDTH);
                builder.addPiece(new DonutArchStructurePiece(center, at, donutFacing, widthChunks * DONUT_SECTION_WIDTH, frostingType));
            }
        }
    }

    private static Holder<Biome> getBiomeHolder(BiomeSource biomeSource, RandomState randomState, BlockPos pos) {
        return biomeSource.getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()), randomState.sampler());
    }

    protected int biomeContinuesInDirectionFor(BiomeSource biomeSource, RandomState randomState, Direction direction, BlockPos start, int cutoff) {
        int i = 0;
        while (i < cutoff) {
            BlockPos check = start.relative(direction, i);
            Holder<Biome> biomeHolder = getBiomeHolder(biomeSource, randomState, check);
            if (!biomeHolder.is(ACBiomeRegistry.CANDY_CAVITY)) {
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
        return ACStructureRegistry.DONUT_ARCH.get();
    }
}

