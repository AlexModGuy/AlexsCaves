package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSimplexNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DinoBowlStructurePiece extends AbstractCaveGenerationStructurePiece {

    public DinoBowlStructurePiece(BlockPos chunkCorner, BlockPos holeCenter, int bowlHeight, int bowlRadius) {
        super(ACStructurePieceRegistry.DINO_BOWL.get(), chunkCorner, holeCenter, bowlHeight, bowlRadius);
    }

    public DinoBowlStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.DINO_BOWL.get(), tag);
    }

    public DinoBowlStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        int cornerX = this.chunkCorner.getX();
        int cornerY = this.chunkCorner.getY();
        int cornerZ = this.chunkCorner.getZ();
        boolean flag = false;
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveAbove = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveBelow = new BlockPos.MutableBlockPos();
        carve.set(cornerX, cornerY, cornerZ);
        carveAbove.set(cornerX, cornerY, cornerZ);
        carveBelow.set(cornerX, cornerY, cornerZ);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                MutableBoolean doFloor = new MutableBoolean(false);
                for (int y = 15; y >= 0; y--) {
                    carve.set(cornerX + x, Mth.clamp(cornerY + y, level.getMinBuildHeight(), level.getMaxBuildHeight()), cornerZ + z);
                    carveAbove.set(carve.getX(), carve.getY() + 1, carve.getZ());
                    float widthSimplexNoise1 = ACMath.sampleNoise3D(carve.getX(), carve.getY(), carve.getZ(), radius) - 0.5F;
                    float widthSimplexNoise2 = ACMath.sampleNoise3D(carve.getX() + 120, carve.getY(), carve.getZ() - 120, radius / 2F) - 0.5F;
                    double yDist = ACMath.smin(0.8F - Math.abs(this.holeCenter.getY() - carve.getY()) / (float) height, 0.8F, 0.2F);
                    double distToCenter = carve.distToLowCornerSqr(this.holeCenter.getX(), carve.getY() - 1, this.holeCenter.getZ());
                    double targetRadius = yDist * (radius + widthSimplexNoise1 * widthSimplexNoise2 * radius) * radius;
                    if (distToCenter <= targetRadius) {
                        double edgy = targetRadius - distToCenter;
                        if (edgy <= 16 && !checkedGetBlock(level, carve).getFluidState().isEmpty()) {
                            checkedSetBlock(level, carve, Blocks.SANDSTONE.defaultBlockState());
                            carveBelow.set(carve.getX(), carve.getY() - 1, carve.getZ());
                            doFloor.setTrue();
                        } else {
                            flag = true;
                            if (isPillarBlocking(carve, yDist)) {
                                if (!checkedGetBlock(level, carve).getFluidState().isEmpty()) {
                                    checkedSetBlock(level, carve, ACBlockRegistry.LIMESTONE.get().defaultBlockState());
                                }
                            } else {
                                if (!checkedGetBlock(level, carveAbove).getFluidState().isEmpty()) {
                                    checkedSetBlock(level, carveAbove, Blocks.SANDSTONE.defaultBlockState());
                                }
                                checkedSetBlock(level, carve, Blocks.CAVE_AIR.defaultBlockState());
                                carveBelow.set(carve.getX(), carve.getY() - 1, carve.getZ());
                                doFloor.setTrue();
                            }
                        }

                    }
                }
                if (doFloor.isTrue() && !checkedGetBlock(level, carveBelow).isAir()) {
                    decorateFloor(level, random, carveBelow.immutable());
                    doFloor.setFalse();
                }
            }
        }
        if (flag){
            replaceBiomes(level, ACBiomeRegistry.PRIMORDIAL_CAVES, 32);
        }
    }

    private boolean isPillarBlocking(BlockPos.MutableBlockPos carve, double yDist) {
        float sample = ACMath.sampleNoise3D(carve.getX(), 0, carve.getZ(), 60) + ACMath.sampleNoise3D(carve.getX() - 440, 0, carve.getZ() + 412, 22) * 0.2F + ACMath.sampleNoise3D(carve.getX() - 100, carve.getY(), carve.getZ() - 400, 100) * 0.1F - 0.4F;
        float f = (float) (ACMath.smin((float) yDist / 0.8F, 1, 0.2F) + 1F);
        return sample >= 0.25F * f && sample <= ACMath.smin(1, (float) yDist / 0.8F + 0.25F, 0.1F) * f;
    }


    private void decorateFloor(WorldGenLevel level, RandomSource rand, BlockPos carveBelow) {
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState dirt = Blocks.DIRT.defaultBlockState();
        checkedSetBlock(level, carveBelow, grass);
        for (int i = 0; i < 1 + rand.nextInt(2); i++) {
            carveBelow = carveBelow.below();
            checkedSetBlock(level, carveBelow, dirt);
        }
    }
}