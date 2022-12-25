package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public abstract class AbstractCaveGenerationStructurePiece extends StructurePiece {
    protected final BlockPos chunkCorner;
    protected final BlockPos holeCenter;
    protected final int height;
    protected final int radius;

    public AbstractCaveGenerationStructurePiece(StructurePieceType pieceType, BlockPos chunkCorner, BlockPos holeCenter, int height, int radius) {
        super(pieceType, 0, createBoundingBox(chunkCorner));
        this.chunkCorner = chunkCorner;
        this.holeCenter = holeCenter;
        this.height = height;
        this.radius = radius;
    }

    public AbstractCaveGenerationStructurePiece(StructurePieceType pieceType, CompoundTag tag) {
        super(pieceType, tag);
        this.chunkCorner = new BlockPos(tag.getInt("TPX"), tag.getInt("TPY"), tag.getInt("TPZ"));
        this.holeCenter = new BlockPos(tag.getInt("HCX"), tag.getInt("HCY"), tag.getInt("HCZ"));
        this.height = tag.getInt("Height");
        this.radius = tag.getInt("Radius");
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
        tag.putInt("Height", this.height);
        tag.putInt("Radius", this.radius);
    }

    public void replaceBiomes(WorldGenLevel level, ResourceKey<Biome> with, int belowLevel) {
        Holder<Biome> biomeHolder = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(with);
        ChunkAccess chunkAccess = level.getChunk(this.chunkCorner);
        int stopY = level.getSeaLevel() - belowLevel;
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

    public void checkedSetBlock(WorldGenLevel level, BlockPos position, BlockState state) {
        if (this.getBoundingBox().isInside(position)) {
            level.setBlock(position, state, Block.UPDATE_SUPPRESS_LIGHT);
        }
    }

    public BlockState checkedGetBlock(WorldGenLevel level, BlockPos position) {
        if (this.getBoundingBox().isInside(position)) {
            return level.getBlockState(position);
        } else {
            return Blocks.VOID_AIR.defaultBlockState();
        }
    }

    public void addChildren(StructurePiece piece, StructurePieceAccessor accessor, RandomSource random) {

    }
}
