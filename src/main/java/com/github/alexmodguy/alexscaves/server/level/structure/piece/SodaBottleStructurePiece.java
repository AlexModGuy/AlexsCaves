package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.level.structure.processor.SodaBottleProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class SodaBottleStructurePiece extends UndergroundTemplateStructurePiece {

    public SodaBottleStructurePiece(StructureTemplateManager manager, ResourceLocation resourceLocation, BlockPos pos, Rotation rotation) {
        super(ACStructurePieceRegistry.SODA_BOTTLE.get(), 0, manager, resourceLocation, resourceLocation.toString(), makeSettings(rotation), pos);
    }

    public SodaBottleStructurePiece(StructureTemplateManager manager, CompoundTag tag) {
        super(ACStructurePieceRegistry.SODA_BOTTLE.get(), tag, manager, (x) -> {
            return makeSettings(Rotation.valueOf(tag.getString("Rotation")));
        });
    }

    public SodaBottleStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(context.structureTemplateManager(), tag);
    }

    private static StructurePlaceSettings makeSettings(Rotation rotation) {
        return (new StructurePlaceSettings()).setRotation(rotation).setIgnoreEntities(true).setKeepLiquids(false).addProcessor(SodaBottleProcessor.INSTANCE);
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
        accessor.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 0);
    }
}
