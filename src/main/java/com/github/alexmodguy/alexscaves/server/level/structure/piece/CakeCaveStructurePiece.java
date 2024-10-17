package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSimplexNoise;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class CakeCaveStructurePiece extends AbstractCaveGenerationStructurePiece {

    private static final Block[] ROAD_BLOCKS = new Block[]{
            ACBlockRegistry.RED_ROCK_CANDY.get(),
            ACBlockRegistry.PURPLE_ROCK_CANDY.get(),
            ACBlockRegistry.YELLOW_ROCK_CANDY.get(),
            ACBlockRegistry.LIGHT_BLUE_ROCK_CANDY.get(),
            ACBlockRegistry.ORANGE_ROCK_CANDY.get(),
            ACBlockRegistry.LIME_ROCK_CANDY.get(),
    };
    private VoronoiGenerator voronoiGenerator;
    private static final float SCALE_RIVER_NOISE_BY = 1000;
    private static final float RIVER_WIDTH_SQ = 1000;
    private static final float RIVER_BANK_WIDTH_SQ = 3000;

    public CakeCaveStructurePiece(BlockPos chunkCorner, BlockPos holeCenter, int bowlHeight, int bowlRadius) {
        super(ACStructurePieceRegistry.CAKE_CAVE.get(), chunkCorner, holeCenter, bowlHeight, bowlRadius);
    }

    public CakeCaveStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.CAKE_CAVE.get(), tag);
    }

    public CakeCaveStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        if (voronoiGenerator == null) {
            voronoiGenerator = new VoronoiGenerator(level.getSeed());
            voronoiGenerator.setOffsetAmount(1.0F);
            voronoiGenerator.setDistanceType(VoronoiGenerator.DistanceType.euclidean);
        }
        int cornerX = this.chunkCorner.getX();
        int cornerY = this.chunkCorner.getY();
        int cornerZ = this.chunkCorner.getZ();
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveAbove = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveBelow = new BlockPos.MutableBlockPos();
        carve.set(cornerX, cornerY, cornerZ);
        carveAbove.set(cornerX, cornerY, cornerZ);
        carveBelow.set(cornerX, cornerY, cornerZ);
        boolean flag = false;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                MutableBoolean doFloor = new MutableBoolean(false);
                for (int y = 15; y >= 0; y--) {
                    carve.set(cornerX + x, Mth.clamp(cornerY + y, level.getMinBuildHeight(), level.getMaxBuildHeight()), cornerZ + z);
                    carveAbove.set(carve.getX(), carve.getY() + 1, carve.getZ());
                    if (inCircle(carve) && !checkedGetBlock(level, carve).is(Blocks.BEDROCK)) {
                        checkedSetBlock(level, carve, Blocks.CAVE_AIR.defaultBlockState());
                        flag = true;
                        surroundCornerOfLiquid(level, carve);
                        carveBelow.set(carve.getX(), carve.getY() - 1, carve.getZ());
                        doFloor.setTrue();
                    }
                }
                if (doFloor.isTrue()) {
                    BlockState floor = checkedGetBlock(level, carveBelow);
                    if (!floor.isAir()) {
                        decorateFloor(level, random, carveBelow.immutable());
                    }
                    doFloor.setFalse();
                }
            }
        }
        if (flag) {
            replaceBiomes(level, ACBiomeRegistry.CANDY_CAVITY, 32);
        }
    }

    private void surroundCornerOfLiquid(WorldGenLevel level, BlockPos.MutableBlockPos center) {
        BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.values()) {
            offset.set(center);
            offset.move(dir);
            BlockState state = checkedGetBlock(level, offset);
            if (!state.getFluidState().isEmpty()){
                if(state.getFluidState().getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get()){
                    double riveriness = getRiveriness(offset);
                    if(riveriness > RIVER_WIDTH_SQ / 2){
                        checkedSetBlock(level, offset, ACBlockRegistry.CAKE_LAYER.get().defaultBlockState());
                    }
                }else{
                    checkedSetBlock(level, offset, ACBlockRegistry.CAKE_LAYER.get().defaultBlockState());
                }
            }
        }
    }

    private boolean generateRiverAt(WorldGenLevel level, double riveriness, BlockPos blockPos) {
        double plateauHeight1 = calculatePlateauHeight(blockPos.getX(), blockPos.getZ(), 0, false);
        if (riveriness < RIVER_WIDTH_SQ) {
            int riverDepth = 3 + (int)((1F - Math.sqrt(riveriness / RIVER_WIDTH_SQ)) * 8) + (int)(1.75F * ACMath.sampleNoise2D(blockPos.getX() + 123, blockPos.getZ() + 120, 10.0F));
            BlockPos riverPos = blockPos.atY((int) plateauHeight1 + 15);
            while (riverPos.getY() > blockPos.getY() - riverDepth) {
                riverPos = riverPos.below();
                if (riverPos.getY() < (int) plateauHeight1) {
                    checkedSetBlock(level, riverPos, ACBlockRegistry.PURPLE_SODA.get().defaultBlockState());
                    level.scheduleTick(riverPos, ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE.get(), ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE.get().getTickDelay(level));
                } else {
                    checkedSetBlock(level, riverPos, Blocks.CAVE_AIR.defaultBlockState());
                }
            }
            checkedSetBlock(level, riverPos.below(), ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState());
            return true;
        }else if(riveriness < RIVER_BANK_WIDTH_SQ){
            for(int i = 0; i < 2 + level.getRandom().nextInt(2); i++){
                checkedSetBlock(level, blockPos.below(i), ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState());
            }
            return true;
        }
        return false;
    }

    private double getRiveriness(BlockPos blockPos){
        int riverNoiseX = blockPos.getX() + (int)(350.0F * ACMath.sampleNoise2D(blockPos.getX() + 1200, blockPos.getZ() - 1200, 150.0F));
        int riverNoiseZ = blockPos.getZ() + (int)(350.0F * ACMath.sampleNoise2D(blockPos.getX() - 1200, blockPos.getZ() + 1200, 150.0F));
        float riverNoise = Math.abs(ACMath.sampleNoise2D(riverNoiseX + 68200, riverNoiseZ - 2248, 1000.0F));
        return (float) Math.pow(riverNoise * SCALE_RIVER_NOISE_BY, 2F);
    }

    private void decorateFloor(WorldGenLevel level, RandomSource rand, BlockPos blockPos) {
        int underDownFor = 1 + rand.nextInt(2);
        double roadRadius = 0.001D;
        float roadNoise = Math.abs(ACMath.sampleNoise2D(blockPos.getX() + 1200, blockPos.getZ() + 10222, 100.0F));
        float roadNoiseSq = (float) Math.pow(roadNoise, 3.0F);
        double riveriness = getRiveriness(blockPos);
        boolean isRiverAt = generateRiverAt(level, riveriness, blockPos);
        boolean roadFlag = false;
        BlockState topBlock = ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get().defaultBlockState();
        BlockState underBlock = ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState();
        if (roadNoiseSq < roadRadius) {
            float voronoiSampleSize = 0.025F;
            VoronoiGenerator.VoronoiInfo info2 = voronoiGenerator.get2(blockPos.getX() * voronoiSampleSize, blockPos.getZ() * voronoiSampleSize);
            double normalizedDistance = info2.distance() * ROAD_BLOCKS.length;
            double border = normalizedDistance % 1.0F;
            boolean edge = roadNoiseSq >= roadRadius - 0.00075D;
            if (isRiverAt) {
                topBlock = edge ? ACBlockRegistry.GINGERBREAD_BLOCK.get().defaultBlockState() : ACBlockRegistry.GINGERBREAD_BRICKS.get().defaultBlockState();
                underDownFor = 0;
            } else if (border > 0.45F && border < 0.65F || edge) {
                topBlock = ACBlockRegistry.WHITE_ROCK_CANDY.get().defaultBlockState();
                underDownFor = 0;
            } else {
                int index = (int) Mth.clamp(Math.round(normalizedDistance), 0, ROAD_BLOCKS.length - 1);
                topBlock = ROAD_BLOCKS[index].defaultBlockState();
                underDownFor = 1;
            }
            underBlock = ACBlockRegistry.WHITE_ROCK_CANDY.get().defaultBlockState();
            roadFlag = true;
        }
        if (!isRiverAt || roadFlag) {
            checkedSetBlock(level, blockPos, topBlock);
        }
        if (!isRiverAt) {
            for (int i = 0; i < underDownFor; i++) {
                blockPos = blockPos.below();
                checkedSetBlock(level, blockPos, underBlock);
            }
        }
    }

    public static double calculatePlateauHeight(int x, int z, int curvedTop, boolean terrainNoise) {
        int plateauSize = 48;
        float plateauNoise = (0.5F + ACMath.sampleNoise2D(x, z, 100)) * 0.5F;
        double slightTerrainNoise = terrainNoise ? ACMath.sampleNoise2D(x + 9000, z - 9000, 150) + ACMath.sampleNoise2D(x + 18000, z + 9000, 60) : 0;
        return -48 + ACMath.canyonStep(Mth.sqrt(plateauNoise), 5) * plateauSize + ACMath.canyonStep(Mth.sqrt(plateauNoise), 10) * curvedTop + slightTerrainNoise;
    }

    private boolean inCircle(BlockPos carve) {
        double plateauHeight = calculatePlateauHeight(carve.getX(), carve.getZ(), 7, true);
        double distToCenterXZ = carve.distToLowCornerSqr(this.holeCenter.getX(), carve.getY(), this.holeCenter.getZ());
        if (carve.getY() < (int) plateauHeight) {
            return false;
        }
        double ceilingNoise = 1.0F + (1.0F + ACMath.sampleNoise2D(carve.getX() + 9000, carve.getZ() - 9000, 120)) * 10 + (1.0F + ACMath.sampleNoise2D(carve.getX() + 3000, carve.getZ() + 2000, 40)) * 4;
        double wallNoise = 0.9F + ACMath.sampleNoise2D(carve.getX() + 9000, carve.getZ() - 9000, 120) * 0.1F;
        double celingHeightScaled = this.height * 0.85F - ceilingNoise;
        float yDome = (float) Math.pow(Math.abs(this.holeCenter.getY() - carve.getY()) / (float) height, 4);
        double yDist = ACMath.smin(1F - yDome, 1.0F, 0.2F);
        return distToCenterXZ < yDist * (radius * wallNoise) * radius && carve.getY() < this.holeCenter.getY() + celingHeightScaled;
    }
}
