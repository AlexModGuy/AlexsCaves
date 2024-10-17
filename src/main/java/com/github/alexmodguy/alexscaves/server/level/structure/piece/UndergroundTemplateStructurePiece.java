package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public abstract class UndergroundTemplateStructurePiece extends TemplateStructurePiece {

    private boolean placedInCaveFlag;

    private BlockPos centeredPos;

    public UndergroundTemplateStructurePiece(StructurePieceType structurePieceType, int i, StructureTemplateManager structureTemplateManager, ResourceLocation resourceLocation, String id, StructurePlaceSettings settings, BlockPos blockPos) {
        super(structurePieceType, i, structureTemplateManager, resourceLocation, id, settings, blockPos);
    }

    public UndergroundTemplateStructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag, StructureTemplateManager structureTemplateManager, Function<ResourceLocation, StructurePlaceSettings> function) {
        super(structurePieceType, compoundTag, structureTemplateManager, function);
        this.placedInCaveFlag = compoundTag.getBoolean("PlacedInCave");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putBoolean("PlacedInCave", this.placedInCaveFlag);
    }

    @Override
    public void postProcess(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
        if (!this.placedInCaveFlag) {
            int originalY = this.templatePosition.getY();
            BlockPos structureCenter = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() / 2, 0, this.template.getSize().getZ() / 2), Mirror.NONE, this.placeSettings.getRotation(), BlockPos.ZERO).offset(templatePosition);
            BlockPos cavePos = getCaveHeight(structureCenter, worldGenLevel, randomSource);
            this.templatePosition = new BlockPos(this.templatePosition.getX(), cavePos.getY(), this.templatePosition.getZ());
            //second run to fix floaters
            if(sinkByEdges()){
                BlockPos structureEdge = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.placeSettings.getRotation(), BlockPos.ZERO).offset(this.templatePosition);
                this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, worldGenLevel, structureEdge), this.templatePosition.getZ());
            }
            if (templatePosition.getY() > chunkGenerator.getSeaLevel() - minimumDepthBeneathSurface()) {
                templatePosition = templatePosition.atY(-256);
            }
            this.templatePosition = this.templatePosition.below(moveDownBy());
            this.boundingBox.move(0, this.templatePosition.getY() - originalY, 0);
            this.placedInCaveFlag = true;
            this.centeredPos = structureCenter.atY(templatePosition.getY());
        }
        if (templatePosition.getY() >= worldGenLevel.getMinBuildHeight()) {
            super.postProcess(worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, pos);
        }
    }

    private int getHeight(BlockPos leftMostCornerPos, BlockGetter level, BlockPos rightMostCornerPos) {
        int j = 512;

        for (BlockPos blockpos : BlockPos.betweenClosed(leftMostCornerPos, rightMostCornerPos)) {
            int x = blockpos.getX();
            int y = leftMostCornerPos.getY() - 1;
            int z = blockpos.getZ();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);
            BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);

            for (FluidState fluidstate = level.getFluidState(blockpos$mutableblockpos); (blockstate.isAir() || fluidstate.is(FluidTags.WATER) || blockstate.is(BlockTags.ICE)) && y > level.getMinBuildHeight() + 1; fluidstate = level.getFluidState(blockpos$mutableblockpos)) {
                --y;
                blockpos$mutableblockpos.set(x, y, z);
                blockstate = level.getBlockState(blockpos$mutableblockpos);
            }
            j = Math.min(j, y);
        }
        return j;
    }

    protected BlockPos getCaveHeight(BlockPos currentStructureCenter, WorldGenLevel level, RandomSource randomSource) {
        List<BlockPos> possibilities = new ArrayList<>();
        int j = 0;
        int seaLevel = Math.max(level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, currentStructureCenter.getX(), currentStructureCenter.getZ()), 63);
        BlockPos.MutableBlockPos chunkCenter = new BlockPos.MutableBlockPos(currentStructureCenter.getX(), level.getMinBuildHeight() + 3, currentStructureCenter.getZ());
        while (chunkCenter.getY() < seaLevel - minimumDepthBeneathSurface()) {
            BlockState currentState = level.getBlockState(chunkCenter);
            chunkCenter.move(0, 1, 0);
            BlockState nextState = level.getBlockState(chunkCenter);
            if (!canReplace(currentState) && canReplace(nextState) && canGenerateOn(currentState)) {
                possibilities.add(chunkCenter.immutable().below());
            }
            j++;
        }
        if (possibilities.isEmpty()) {
            return discardIfNotOnGround() ? currentStructureCenter.atY(-256) : currentStructureCenter;
        } else if(placeInHighestCave() && possibilities.size() > 1){
            possibilities.sort(Comparator.comparingInt(Vec3i::getY).reversed());
            return possibilities.get(0);
        }else{
            return possibilities.size() <= 1 ? possibilities.get(0) : possibilities.get(randomSource.nextInt(possibilities.size() - 1));
        }
    }

    public boolean placeInHighestCave() {
        return false;
    }

    protected boolean canReplace(BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }

    public boolean canGenerateOn(BlockState state) {
        return true;
    }

    public boolean sinkByEdges() {
        return true;
    }

    public boolean discardIfNotOnGround() {
        return false;
    }

    public int minimumDepthBeneathSurface() {
        return 15;
    }

    public int moveDownBy() {
        return 0;
    }

    public BlockPos getCenteredPos(){
        return centeredPos;
    }
}
