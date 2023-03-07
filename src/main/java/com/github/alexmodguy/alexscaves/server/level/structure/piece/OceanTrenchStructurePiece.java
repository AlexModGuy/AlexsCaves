package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class OceanTrenchStructurePiece extends AbstractCaveGenerationStructurePiece {

    private BlockState water = Fluids.WATER.defaultFluidState().createLegacyBlock();

    public OceanTrenchStructurePiece(BlockPos chunkCorner, BlockPos holeCenter, int bowlHeight, int bowlRadius) {
        super(ACStructurePieceRegistry.OCEAN_TRENCH.get(), chunkCorner, holeCenter, bowlHeight, bowlRadius, -64, 320);
    }

    public OceanTrenchStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.OCEAN_TRENCH.get(), tag);
    }

    public OceanTrenchStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        int cornerX = this.chunkCorner.getX();
        int cornerY = this.chunkCorner.getY();
        int cornerZ = this.chunkCorner.getZ();
        boolean flag = false;
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveCliff = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveBelow = new BlockPos.MutableBlockPos();
        carve.set(cornerX, cornerY, cornerZ);
        carveCliff.set(cornerX, cornerY, cornerZ);
        carveBelow.set(cornerX, cornerY, cornerZ);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                MutableBoolean doFloor = new MutableBoolean(false);
                int carveX = cornerX + x;
                int carveZ = cornerZ + z;
                int priorHeight = getSeafloorHeight(level, carveX, carveZ);
                float seaFloorExtra = (1.0F + ACMath.sampleNoise2D(carveX - 800, carveZ - 212, 20)) * 5;
                int minY = (int) (level.getMinBuildHeight() + 2 + seaFloorExtra);
                carveCliff.set(carveX, holeCenter.getY(), carveZ);
                for (int y = priorHeight + 3; y >= minY; y--) {
                    carve.set(carveX, Mth.clamp(y, minY, level.getMaxBuildHeight()), carveZ);
                    if (shouldDig(level, carve, chunkGen.getSeaLevel(), priorHeight)) {
                        if (isSeaMountBlocking(carve)) {
                            BlockState prior = checkedGetBlock(level, carve);
                            if (!prior.is(Blocks.BEDROCK)) {
                                checkedSetBlock(level, carve, Blocks.TUFF.defaultBlockState());
                            }
                        } else {
                            flag = true;
                            carveBelow.set(carve.getX(), carve.getY() - 1, carve.getZ());
                            doFloor.setTrue();
                            surroundWater(level, carve, priorHeight);
                        }
                    } else if (carve.getY() < chunkGen.getSeaLevel() && carveCliff.distToLowCornerSqr(holeCenter.getX(), carveCliff.getY(), holeCenter.getZ()) < getRadiusSq(carveCliff) + 25 && (level.isEmptyBlock(carve) && level.getFluidState(carve).isEmpty() || level.getFluidState(carve).is(FluidTags.LAVA))) {
                        surroundTrenchBorder(level, carve, priorHeight);
                    }
                }
                if (doFloor.isTrue()) {
                    surroundTrenchBorder(level, carveBelow, priorHeight);
                    decorateFloor(level, random, carveBelow, chunkGen.getSeaLevel());
                }
            }
            if (flag) {
                replaceBiomes(level, ACBiomeRegistry.ABYSSAL_CHASM, 16);
            }
        }
    }

    private int getSeafloorHeight(WorldGenLevel level, int x, int z) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(x, level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z), z);
        boolean inFrozenOcean = level.getBiome(mutableBlockPos).is(ACTagRegistry.TRENCH_IGNORES_STONE_IN);
        while (ignoreHeight(level, inFrozenOcean, checkedGetBlock(level, mutableBlockPos), mutableBlockPos) && mutableBlockPos.getY() >= -64) {
            mutableBlockPos.move(0, -1, 0);
        }
        return mutableBlockPos.getY();
    }

    private boolean ignoreHeight(WorldGenLevel level, boolean inFrozenOcean, BlockState blockState, BlockPos.MutableBlockPos mutableBlockPos) {
        return blockState.isAir() || blockState.is(ACTagRegistry.TRENCH_GENERATION_IGNORES) || !blockState.getFluidState().isEmpty() || inFrozenOcean && blockState.is(BlockTags.OVERWORLD_CARVER_REPLACEABLES) && mutableBlockPos.getY() > level.getSeaLevel() - 2;
    }

    private void surroundTrenchBorder(WorldGenLevel level, BlockPos carve, int priorHeight) {
        BlockState prior = checkedGetBlock(level, carve);
        if (!prior.is(Blocks.BEDROCK) && !prior.is(ACBlockRegistry.MUCK.get()) && !isSeaMountBlocking(carve)) {
            int dist = priorHeight - carve.getY();
            BlockState toSet;
            int layerOffset = level.getRandom().nextInt(2);
            if (dist <= 5 + layerOffset) {
                toSet = carve.getY() < 0 ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState();
            } else if (dist <= 12 + layerOffset) {
                toSet = Blocks.DEEPSLATE.defaultBlockState();
            } else {
                toSet = ACBlockRegistry.ABYSSMARINE.get().defaultBlockState();
                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                mutableBlockPos.set(carve);
                for (int i = 0; i <= 2 + layerOffset; i++) {
                    mutableBlockPos.move(0, 1, 0);
                    if (level.getFluidState(mutableBlockPos).is(FluidTags.WATER)) {
                        toSet = ACBlockRegistry.MUCK.get().defaultBlockState();
                        break;
                    }
                }
            }
            checkedSetBlock(level, carve, toSet);
        }

    }

    private double getRadiusSq(BlockPos.MutableBlockPos carve) {
        float widthSimplexNoise1 = ACMath.sampleNoise3D(carve.getX(), carve.getY(), carve.getZ(), radius) - 0.65F;
        float widthSimplexNoise2 = ACMath.sampleNoise3D(carve.getX() + 120, carve.getY(), carve.getZ() - 120, radius / 2F) - 0.65F;
        return ACMath.smin((radius + widthSimplexNoise1 * widthSimplexNoise2 * radius) * radius * 0.9F, radius * radius * 0.8F, 0.1F);
    }

    private boolean shouldDig(WorldGenLevel level, BlockPos.MutableBlockPos carve, int seaLevel, int priorHeight) {
        double yDist = calcYDist(level, carve, seaLevel, priorHeight);
        double distToCenter = carve.distToLowCornerSqr(this.holeCenter.getX(), carve.getY() - 1, this.holeCenter.getZ());
        double targetRadius = yDist * getRadiusSq(carve);
        return distToCenter <= targetRadius;
    }

    private boolean isSeaMountBlocking(BlockPos carve) {
        int bottomedY = carve.getY() + 64;
        float heightTarget = 20 + ACMath.sampleNoise3D(carve.getX() - 440, 0, carve.getZ() + 412, 30) * 10 + ACMath.sampleNoise3D(carve.getX() - 110, 0, carve.getZ() + 110, 10) * 3;
        float heightScale = (heightTarget - bottomedY) / (heightTarget + 15);
        float sample = ACMath.sampleNoise3D(carve.getX(), 0, carve.getZ(), 50) + ACMath.sampleNoise3D(carve.getX() - 440, 0, carve.getZ() + 412, 11) * 0.2F + ACMath.sampleNoise3D(carve.getX() - 100, 0, carve.getZ() - 400, 100) * 0.3F - 0.1F;
        return sample >= 0.4F * Math.max(0, 1 - heightScale);
    }


    private void surroundWater(WorldGenLevel level, BlockPos.MutableBlockPos center, int priorHeight) {
        checkedSetBlock(level, center, water);
        BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.values()) {
            offset.set(center);
            offset.move(dir);
            if (shouldDig(level, offset, level.getSeaLevel(), priorHeight)) {
                checkedSetBlock(level, offset, water);
            } else if (!level.isEmptyBlock(offset) && !level.getFluidState(offset).is(FluidTags.WATER)) {
                if (dir == Direction.DOWN) {
                    for (int y = offset.getY(); y >= level.getMinBuildHeight(); y--) {
                        surroundTrenchBorder(level, offset.atY(y), priorHeight);
                    }
                } else {
                    surroundTrenchBorder(level, offset, priorHeight);
                }
            }
        }
    }

    private double calcYDist(WorldGenLevel level, BlockPos.MutableBlockPos carve, int seaLevel, int priorHeight) {
        int j = -64 - carve.getY();
        if (carve.getY() >= seaLevel - 1 || priorHeight >= seaLevel || j > 0) {
            return 0;
        } else if (destroyWithCarve(level, carve)) {
            return 1.0;
        } else {
            float belowSeaBy = ACMath.smin((seaLevel - priorHeight) / 30F, 1.0F, 0.2F);
            float bedrockCloseness = ACMath.smin(Math.abs(j) / 50F - 0.1F, 1.0F, 0.2F);
            float df1 = ACMath.sampleNoise3D(carve.getX(), 0, carve.getZ(), 100) * 0.6F;
            float df2 = ACMath.sampleNoise3D(carve.getX() - 450, 0, carve.getZ() + 450, 300) * 0.25F;
            return ACMath.smin(belowSeaBy * (bedrockCloseness - df1) - df2, 0.9F, 0.2F) - df2;
        }
    }

    public boolean destroyWithCarve(WorldGenLevel level, BlockPos.MutableBlockPos carve) {
        BlockState state = checkedGetBlock(level, carve);
        return state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN);
    }

    private void decorateFloor(WorldGenLevel level, RandomSource rand, BlockPos.MutableBlockPos muckAt, int seaLevel) {
        if (!isSeaMountBlocking(muckAt) && muckAt.getY() < seaLevel - 32) {
            checkedSetBlock(level, muckAt, ACBlockRegistry.MUCK.get().defaultBlockState());
            for (int i = 0; i < 1 + rand.nextInt(2); i++) {
                muckAt.move(0, -1, 0);
                if (checkedGetBlock(level, muckAt).is(ACTagRegistry.UNMOVEABLE)) {
                    break;
                }
                checkedSetBlock(level, muckAt, ACBlockRegistry.MUCK.get().defaultBlockState());
            }
        }
    }
}