package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSimplexNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DinoBowlStructurePiece extends StructurePiece {
    private final BlockPos chunkCorner;
    private final BlockPos holeCenter;
    private final int bowlHeight;
    private final int bowlRadius;

    public DinoBowlStructurePiece(BlockPos chunkCorner, BlockPos holeCenter, int bowlHeight, int bowlRadius) {
        super(ACStructurePieceRegistry.DINO_BOWL.get(), 0, createBoundingBox(chunkCorner));
        this.chunkCorner = chunkCorner;
        this.holeCenter = holeCenter;
        this.bowlHeight = bowlHeight;
        this.bowlRadius = bowlRadius;
    }

    public DinoBowlStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.DINO_BOWL.get(), tag);
        this.chunkCorner = new BlockPos(tag.getInt("TPX"), tag.getInt("TPY"), tag.getInt("TPZ"));
        this.holeCenter = new BlockPos(tag.getInt("HCX"), tag.getInt("HCY"), tag.getInt("HCZ"));
        this.bowlHeight = tag.getInt("Height");
        this.bowlRadius = tag.getInt("Radius");
    }

    public DinoBowlStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    private static BoundingBox createBoundingBox(BlockPos origin) {
        ChunkPos chunkPos = new ChunkPos(origin);
        return new BoundingBox(chunkPos.getMinBlockX(), origin.getY() - 2, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), origin.getY() + 16, chunkPos.getMaxBlockZ());
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("TPX", this.chunkCorner.getX());
        tag.putInt("TPY", this.chunkCorner.getY());
        tag.putInt("TPZ", this.chunkCorner.getZ());
        tag.putInt("HCX", this.holeCenter.getX());
        tag.putInt("HCY", this.holeCenter.getY());
        tag.putInt("HCZ", this.holeCenter.getZ());
        tag.putInt("Height", this.bowlHeight);
        tag.putInt("Radius", this.bowlRadius);
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
                    float widthSimplexNoise1 = sampleNoise3D(carve.getX(), carve.getY(), carve.getZ(), bowlRadius) - 0.5F;
                    float widthSimplexNoise2 = sampleNoise3D(carve.getX() + 120, carve.getY(), carve.getZ() - 120, bowlRadius / 2F) - 0.5F;
                    double yDist = ACMath.smin(0.8F - Math.abs(this.holeCenter.getY() - carve.getY()) / (float) bowlHeight, 0.8F, 0.2F);
                    double distToCenter = carve.distToLowCornerSqr(this.holeCenter.getX(), carve.getY() - 1, this.holeCenter.getZ());
                    double targetRadius = yDist * (bowlRadius + widthSimplexNoise1 * widthSimplexNoise2 * bowlRadius) * bowlRadius;
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
            replaceBiomes(level);
        }
    }

    private void replaceBiomes(WorldGenLevel level) {
        Holder<Biome> biomeHolder = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(ACBiomeRegistry.PRIMORDIAL_CAVES);
        ChunkAccess chunkAccess = level.getChunk(this.chunkCorner);
        int stopY = level.getSeaLevel() - 32;
        if (chunkAccess != null) {
            LevelChunkSection section = chunkAccess.getSection(chunkAccess.getSectionIndex(this.chunkCorner.getY()));
            PalettedContainer<Holder<Biome>> container = section.getBiomes().recreate();
            if (section.bottomBlockY() < stopY) {
                for (int biomeX = 0; biomeX < 4; ++biomeX) {
                    for (int biomeY = 0; biomeY < 4; ++biomeY) {
                        for (int biomeZ = 0; biomeZ < 4; ++biomeZ) {
                            container.getAndSetUnchecked(biomeX, biomeY, biomeZ, biomeHolder);
                        }
                    }
                }
            }
            section.biomes = container;
        }
    }

    private boolean isPillarBlocking(BlockPos.MutableBlockPos carve, double yDist) {
        float sample = sampleNoise3D(carve.getX(), 0, carve.getZ(), 60) + sampleNoise3D(carve.getX() - 440, 0, carve.getZ() + 412, 22) * 0.2F + sampleNoise3D(carve.getX() - 100, carve.getY(), carve.getZ() - 400, 100) * 0.1F - 0.4F;
        float f = (float) (ACMath.smin((float) yDist / 0.8F, 1, 0.2F) + 1F);
        return sample >= 0.25F * f && sample <= ACMath.smin(1, (float) yDist / 0.8F + 0.25F, 0.1F) * f;
    }

    private float sampleNoise3D(int x, int y, int z, float simplexSampleRate) {
        return (float) ((ACSimplexNoise.noise((x + simplexSampleRate) / simplexSampleRate, (y + simplexSampleRate) / simplexSampleRate, (z + simplexSampleRate) / simplexSampleRate)));
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

    private void checkedSetBlock(WorldGenLevel level, BlockPos position, BlockState state) {
        if (this.getBoundingBox().isInside(position)) {
            level.setBlock(position, state, Block.UPDATE_SUPPRESS_LIGHT);
        }
    }

    private BlockState checkedGetBlock(WorldGenLevel level, BlockPos position) {
        if (this.getBoundingBox().isInside(position)) {
            return level.getBlockState(position);
        } else {
            return Blocks.VOID_AIR.defaultBlockState();
        }
    }

    private boolean checkedIsGenBiome(WorldGenLevel level, BlockPos position) {
        return true;

    }

    public void addChildren(StructurePiece piece, StructurePieceAccessor accessor, RandomSource random) {

    }
}