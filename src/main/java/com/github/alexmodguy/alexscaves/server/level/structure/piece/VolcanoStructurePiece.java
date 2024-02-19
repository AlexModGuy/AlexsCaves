package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.PrimalMagmaBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class VolcanoStructurePiece extends StructurePiece {

    private BlockPos center;
    private BlockPos chunkCorner;
    private int volcanoRadius;
    private int volcanoHeight;

    public VolcanoStructurePiece(BlockPos center, BlockPos chunkCorner, int volcanoRadius, int volcanoHeight) {
        super(ACStructurePieceRegistry.VOLCANO.get(), 0, createBoundingBox(chunkCorner));
        this.center = center;
        this.chunkCorner = chunkCorner;
        this.volcanoRadius = volcanoRadius;
        this.volcanoHeight = volcanoHeight;
    }

    public VolcanoStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.VOLCANO.get(), tag);
        this.center = new BlockPos(tag.getInt("CPX"), tag.getInt("CPY"), tag.getInt("CPZ"));
        this.chunkCorner = new BlockPos(tag.getInt("TPX"), tag.getInt("TPY"), tag.getInt("TPZ"));
        this.volcanoRadius = tag.getInt("Radius");
        this.volcanoHeight = tag.getInt("Height");
    }

    public VolcanoStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("CPX", this.center.getX());
        tag.putInt("CPY", this.center.getY());
        tag.putInt("CPZ", this.center.getZ());
        tag.putInt("TPX", this.chunkCorner.getX());
        tag.putInt("TPY", this.chunkCorner.getY());
        tag.putInt("TPZ", this.chunkCorner.getZ());
        tag.putInt("Radius", this.volcanoRadius);
        tag.putInt("Height", this.volcanoHeight);
    }

    private static BoundingBox createBoundingBox(BlockPos chunkCorner) {
        ChunkPos chunkPos = new ChunkPos(chunkCorner);
        return new BoundingBox(chunkPos.getMinBlockX(), -64, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), 256, chunkPos.getMaxBlockZ());
    }

    public void checkedSetBlock(WorldGenLevel level, BlockPos position, BlockState state) {
        if (this.getBoundingBox().isInside(position)) {
            level.setBlock(position, state, 128);
        }
    }

    public BlockState checkedGetBlock(WorldGenLevel level, BlockPos position) {
        if (this.getBoundingBox().isInside(position)) {
            return level.getBlockState(position);
        } else {
            return Blocks.VOID_AIR.defaultBlockState();
        }
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        int cornerX = this.chunkCorner.getX();
        int cornerY = this.chunkCorner.getY();
        int cornerZ = this.chunkCorner.getZ();
        int generateCoreHeight = (int) Math.floor(this.volcanoHeight * 0.35);
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                carve.set(cornerX + x, cornerY, cornerZ + z);
                double dist = Math.sqrt(carve.distToLowCornerSqr(center.getX(), carve.getY(), center.getZ())) / volcanoRadius;
                if(dist < 1.0F){
                    double invDist = 1F - dist;
                    double volcanoCurve = calcVolcanoCurve(invDist);
                    int lavaSink = volcanoCurve < 1.0F && dist < 0.15F ? 2 : 0;
                    double heightAt = volcanoHeight * volcanoCurve;
                    int generateHeight = (int) (this.center.getY() + heightAt - lavaSink);
                    while (carve.getY() < generateHeight) {
                        BlockState checkedBlockstate = checkedGetBlock(level, carve);
                        if (checkedBlockstate.is(Blocks.BEDROCK)) {
                            break;
                        }
                        int beneathAmount = generateHeight - carve.getY();
                        int aboveAmount = carve.getY() - center.getY();
                        float calderaAmount = (float) Math.sin(invDist * Math.PI);
                        if (beneathAmount == generateCoreHeight && carve.getX() == center.getX() && carve.getZ() == center.getZ()) {
                            checkedSetBlock(level, carve, ACBlockRegistry.VOLCANIC_CORE.get().defaultBlockState());
                        } else if (beneathAmount > 3 + calderaAmount * 20 && aboveAmount > 3 + calderaAmount * 10) {
                            checkedSetBlock(level, carve, Blocks.LAVA.defaultBlockState());
                        } else if ((lavaSink > 0 || beneathAmount > 6 && aboveAmount > 3)) {
                            checkedSetBlock(level, carve, ACBlockRegistry.PRIMAL_MAGMA.get().defaultBlockState().setValue(PrimalMagmaBlock.ACTIVE, true).setValue(PrimalMagmaBlock.PERMANENT, true));
                        } else {
                            float magmaVeinNoise = ACMath.sampleNoise3D(carve.getX() - 19000, carve.getY() * 0.5F + 120, carve.getZ() + 19000, 20);
                            if (volcanoCurve < 0.035F) {
                                if (random.nextFloat() * 0.13F < volcanoCurve) {
                                    checkedSetBlock(level, carve, Blocks.SMOOTH_BASALT.defaultBlockState());
                                } else {
                                    checkedSetBlock(level, carve, ACBlockRegistry.LIMESTONE.get().defaultBlockState());
                                }
                            } else if (magmaVeinNoise < 0.075F && magmaVeinNoise > -0.075F) {
                                checkedSetBlock(level, carve, ACBlockRegistry.PRIMAL_MAGMA.get().defaultBlockState());
                            } else {
                                checkedSetBlock(level, carve, ACBlockRegistry.FLOOD_BASALT.get().defaultBlockState());
                            }
                        }
                        carve.move(0, 1, 0);
                    }
                    carve.set(carve.getX(), cornerY - 1, carve.getZ());
                    int j = 0;
                    while((volcanoReplacesBeneath(checkedGetBlock(level, carve)) || j < 3) && carve.getY() > level.getMinBuildHeight()){
                        checkedSetBlock(level, carve, ACBlockRegistry.LIMESTONE.get().defaultBlockState());
                        carve.move(0, -1, 0);
                        j++;
                    }
                }
            }
        }
    }

    private boolean volcanoReplacesBeneath(BlockState state) {
        return state.is(Blocks.AIR) || state.is(BlockTags.DIRT);
    }

    private double calcVolcanoCurve(double dist) {
        double off = 2 * dist - 1;
        double d0 = 0.5F * (0.85F * Math.sin(off * Mth.PI) + Math.pow(off, 3) + 0.5F);
        return dist > 0.85F ? d0 : Math.max(d0, dist * dist);
    }
}
