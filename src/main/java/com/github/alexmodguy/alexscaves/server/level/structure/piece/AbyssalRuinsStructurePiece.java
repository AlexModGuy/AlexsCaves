package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACLootTableRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class AbyssalRuinsStructurePiece extends TemplateStructurePiece {

    public AbyssalRuinsStructurePiece(StructureTemplateManager manager, ResourceLocation resourceLocation, BlockPos pos, Rotation rotation) {
        super(ACStructurePieceRegistry.ABYSSAL_RUINS.get(), 0, manager, resourceLocation, resourceLocation.toString(), makeSettings(rotation), pos);
    }

    public AbyssalRuinsStructurePiece(StructureTemplateManager manager, CompoundTag tag) {
        super(ACStructurePieceRegistry.ABYSSAL_RUINS.get(), tag, manager, (x) -> {
            return makeSettings(Rotation.valueOf(tag.getString("Rotation")));
        });
    }

    public AbyssalRuinsStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(context.structureTemplateManager(), tag);
    }

    private static StructurePlaceSettings makeSettings(Rotation rotation) {
        return (new StructurePlaceSettings()).setRotation(rotation).setMirror(Mirror.NONE).setKeepLiquids(false);
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString("Rotation", this.placeSettings.getRotation().name());
    }

    public void postProcess(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
        int i = worldGenLevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
        this.templatePosition = new BlockPos(this.templatePosition.getX(), i, this.templatePosition.getZ());
        BlockPos blockpos = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.placeSettings.getRotation(), BlockPos.ZERO).offset(this.templatePosition);
        this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, worldGenLevel, blockpos), this.templatePosition.getZ());
        if(templatePosition.getY() > chunkGenerator.getSeaLevel() - 20){
            templatePosition = templatePosition.atY(-128);
        }
        super.postProcess(worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, pos);
    }

    private int getHeight(BlockPos blockPos, BlockGetter level, BlockPos pos) {
        int i = blockPos.getY();
        int j = 512;
        int k = i - 1;
        int l = 0;

        for(BlockPos blockpos : BlockPos.betweenClosed(blockPos, pos)) {
            int i1 = blockpos.getX();
            int j1 = blockpos.getZ();
            int k1 = blockPos.getY() - 1;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(i1, k1, j1);
            BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);

            for(FluidState fluidstate = level.getFluidState(blockpos$mutableblockpos); (blockstate.isAir() || fluidstate.is(FluidTags.WATER) || blockstate.is(BlockTags.ICE)) && k1 > level.getMinBuildHeight() + 1; fluidstate = level.getFluidState(blockpos$mutableblockpos)) {
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

    protected void handleDataMarker(String string, BlockPos pos, ServerLevelAccessor accessor, RandomSource random, BoundingBox box) {
        accessor.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 0);
        switch (string) {
            case "loot_chest":
                RandomizableContainerBlockEntity.setLootTable(accessor, random, pos.below(), ACLootTableRegistry.ABYSSAL_RUINS_CHEST);
                break;
            case "submarine":
                spawnSubmarine(accessor, pos, false);
                break;
            case "submarine_damaged":
                if(random.nextFloat() > 0.5F){
                    spawnSubmarine(accessor, pos, true);
                }
                break;
        }
    }

    private void spawnSubmarine(ServerLevelAccessor level, BlockPos pos, boolean totaled) {
        SubmarineEntity submarine = ACEntityRegistry.SUBMARINE.get().create(level.getLevel());
        while(level.getBlockState(pos).getFluidState().isEmpty() && !level.isEmptyBlock(pos) && pos.getY() < level.getMaxBuildHeight()){
            pos = pos.above();
        }
        Vec3 vec31 = Vec3.atCenterOf(pos);
        submarine.setYRot(level.getRandom().nextFloat() * 360);
        submarine.setPos(vec31.x, vec31.y, vec31.z);
        while (!level.isUnobstructed(submarine)){
            submarine.setPos(submarine.position().add(new Vec3(0, 1, 0)));
        }
        if(totaled){
            submarine.setDamageLevel(4);
            submarine.setOxidizationLevel(3);
        }else{
            submarine.setOxidizationLevel(2);
        }
        level.addFreshEntity(submarine);
    }
}
