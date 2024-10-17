package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACLootTableRegistry;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LicowitchTowerStructurePiece extends UndergroundTemplateStructurePiece {

    public LicowitchTowerStructurePiece(StructureTemplateManager manager, ResourceLocation resourceLocation, BlockPos pos, Rotation rotation) {
        super(ACStructurePieceRegistry.LICOWITCH_TOWER.get(), 0, manager, resourceLocation, resourceLocation.toString(), makeSettings(rotation), pos);
    }

    public LicowitchTowerStructurePiece(StructureTemplateManager manager, CompoundTag tag) {
        super(ACStructurePieceRegistry.LICOWITCH_TOWER.get(), tag, manager, (x) -> {
            return makeSettings(Rotation.valueOf(tag.getString("Rotation")));
        });
    }

    public LicowitchTowerStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(context.structureTemplateManager(), tag);
    }

    private static StructurePlaceSettings makeSettings(Rotation rotation) {
        return (new StructurePlaceSettings()).setRotation(rotation).setIgnoreEntities(true).setKeepLiquids(false);
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString("Rotation", this.placeSettings.getRotation().name());
    }

    @Override
    public int minimumDepthBeneathSurface() {
        return 35;
    }

    @Override
    protected void handleDataMarker(String string, BlockPos pos, ServerLevelAccessor accessor, RandomSource random, BoundingBox box) {
        if (string.equals("loot_chest")) {
            RandomizableContainerBlockEntity.setLootTable(accessor, random, pos.below(), ACLootTableRegistry.LICOWITCH_TOWER_CHEST);
        } else if (string.equals("secret_loot_chest")) {
            RandomizableContainerBlockEntity.setLootTable(accessor, random, pos.below(), ACLootTableRegistry.SECRET_LICOWITCH_TOWER_CHEST);
        }
        accessor.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 0);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos structurePos) {
        super.postProcess(level, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, structurePos);
        BoundingBox box = new BoundingBox(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.minY() + 1, this.boundingBox.maxZ());
        Set<BlockPos> supportsNeededBelow = Sets.newHashSet();
        BlockPos.betweenClosedStream(box).forEach((pos) -> {
            if (!level.getBlockState(pos).isAir()) {
                if (!level.getFluidState(pos.below()).isEmpty() || !level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below())) {
                    supportsNeededBelow.add(pos.immutable());
                }
            }
        });
        BlockPos.MutableBlockPos grounded = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos groundedInBox = new BlockPos.MutableBlockPos();
        for (BlockPos pos : supportsNeededBelow) {
            grounded.set(pos.getX(), pos.getY() - 1, pos.getZ());
            groundedInBox.set(pos.getX(), boundingBox.minY() + 1, pos.getZ());
            if(boundingBox.isInside(groundedInBox)){
                int yDown = 0;
                int maxYDown = 3 + randomSource.nextInt(3);
                while ((!level.getBlockState(grounded).getFluidState().isEmpty() || !level.getBlockState(grounded).isCollisionShapeFullBlock(level, grounded)) && grounded.getY() > level.getMinBuildHeight() && yDown < maxYDown) {
                    level.setBlock(grounded, yDown < maxYDown * 0.4F ? ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState() : ACBlockRegistry.CAKE_LAYER.get().defaultBlockState(), 3);
                    grounded.move(0, -1, 0);
                    yDown++;
                }
            }
        }
    }

    @Override
    public boolean placeInHighestCave() {
        return true;
    }

    @Override
    public boolean discardIfNotOnGround() {
        return true;
    }

    @Override
    public boolean sinkByEdges() {
        return false;
    }

    @Override
    protected BlockPos getCaveHeight(BlockPos currentStructureCenter, WorldGenLevel level, RandomSource randomSource) {
        return currentStructureCenter.atY((int) CakeCaveStructurePiece.calculatePlateauHeight(currentStructureCenter.getX(), currentStructureCenter.getZ(), 7, true));
    }
}
