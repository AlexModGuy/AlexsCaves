package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.UndergroundCabinProcessor;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.List;

public class UndergroundCabinStructurePiece extends TemplateStructurePiece {

    private ResourceKey<Biome> pickedBiome = ACBiomeRegistry.MAGNETIC_CAVES;

    public UndergroundCabinStructurePiece(StructureTemplateManager manager, ResourceLocation resourceLocation, BlockPos pos, Rotation rotation) {
        super(ACStructurePieceRegistry.UNDERGROUND_CABIN.get(), 0, manager, resourceLocation, resourceLocation.toString(), makeSettings(rotation), pos);
    }

    public UndergroundCabinStructurePiece(StructureTemplateManager manager, CompoundTag tag) {
        super(ACStructurePieceRegistry.UNDERGROUND_CABIN.get(), tag, manager, (x) -> {
            return makeSettings(Rotation.valueOf(tag.getString("Rotation")));
        });
    }

    public UndergroundCabinStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(context.structureTemplateManager(), tag);
    }

    private static StructurePlaceSettings makeSettings(Rotation rotation) {
        return (new StructurePlaceSettings()).setRotation(rotation).setIgnoreEntities(true).setKeepLiquids(false);
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString("Rotation", this.placeSettings.getRotation().name());
    }

    public void postProcess(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
        pickedBiome = Util.getRandom(ACBiomeRegistry.ALEXS_CAVES_BIOMES, randomSource);
        this.placeSettings.clearProcessors().addProcessor(new UndergroundCabinProcessor());
        BlockPos blockpos = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.placeSettings.getRotation(), BlockPos.ZERO).offset(this.templatePosition);
        BlockPos cavePos = getCaveHeight(blockpos, worldGenLevel, randomSource);
        this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, worldGenLevel, cavePos), this.templatePosition.getZ());
        super.postProcess(worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, pos);
    }

    private BlockPos getCaveHeight(BlockPos blockPos, BlockGetter level, RandomSource randomSource) {
        List<BlockPos> genPos = new ArrayList<>();
        int j = 0;
        BlockPos.MutableBlockPos chunkCenter = new BlockPos.MutableBlockPos(blockPos.getX(), level.getMinBuildHeight() + 3, blockPos.getZ());
        while (chunkCenter.getY() < blockPos.getY()) {
            BlockState currentState = level.getBlockState(chunkCenter);
            chunkCenter.move(0, 1, 0);
            BlockState nextState = level.getBlockState(chunkCenter);
            if (!canReplace(currentState, j) && canReplace(nextState, j + 1)) {
                genPos.add(chunkCenter.immutable());
            }
            j++;
        }

        if (genPos.isEmpty()) {
            return blockPos;
        }
        return genPos.size() <= 1 ? genPos.get(0) : genPos.get(randomSource.nextInt(genPos.size() - 1));
    }

    private int getHeight(BlockPos blockPos, BlockGetter level, BlockPos pos) {
        int i = blockPos.getY();
        int j = 512;
        int k = i - 1;
        int l = 0;

        for (BlockPos blockpos : BlockPos.betweenClosed(blockPos, pos)) {
            int i1 = blockpos.getX();
            int j1 = blockpos.getZ();
            int k1 = blockPos.getY() - 1;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(i1, k1, j1);
            BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);

            for (FluidState fluidstate = level.getFluidState(blockpos$mutableblockpos); (blockstate.isAir() || fluidstate.is(FluidTags.WATER) || blockstate.is(BlockTags.ICE)) && k1 > level.getMinBuildHeight() + 1; fluidstate = level.getFluidState(blockpos$mutableblockpos)) {
                --k1;
                blockpos$mutableblockpos.set(i1, k1, j1);
                blockstate = level.getBlockState(blockpos$mutableblockpos);
            }

            j = Math.min(j, k1);
            if (k1 < k - 2) {
                ++l;
            }
        }

        int l1 = Math.abs(blockPos.getX() - pos.getX());
        if (k - j > 2 && l > l1 - 2) {
            i = j + 1;
        }

        return i;
    }

    protected boolean canReplace(BlockState state, int already) {
        return state.isAir() || state.canBeReplaced();
    }

    protected void handleDataMarker(String string, BlockPos pos, ServerLevelAccessor accessor, RandomSource random, BoundingBox box) {
        accessor.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 0);
        switch (string) {
            case "loot_chest":
                RandomizableContainerBlockEntity.setLootTable(accessor, random, pos.below(), new ResourceLocation("alexscaves:chests/underground_cabin_" + pickedBiome.location().getPath()));
                break;
        }
    }

}
