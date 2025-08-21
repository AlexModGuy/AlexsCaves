package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.UndergroundCabinProcessor;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.ArrayList;
import java.util.List;

public class UndergroundCabinStructurePiece extends UndergroundTemplateStructurePiece {

    private ResourceKey<Biome> pickedBiome = null;

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
        pickedBiome = pickBiome(randomSource);
        this.placeSettings.clearProcessors().addProcessor(new UndergroundCabinProcessor());
        super.postProcess(worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, pos);
    }

    private ResourceKey<Biome> pickBiome(RandomSource randomSource) {
        List<ResourceKey<Biome>> biomeList = new ArrayList<>(ACBiomeRegistry.ALEXS_CAVES_BIOMES);
        biomeList.removeIf(BiomeGenerationConfig::isBiomeDisabledCompletely);
        return biomeList.isEmpty() ? null : Util.getRandom(biomeList, randomSource);
    }

    protected void handleDataMarker(String string, BlockPos pos, ServerLevelAccessor accessor, RandomSource random, BoundingBox box) {
        accessor.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 0);
        switch (string) {
            case "loot_chest":
                ResourceLocation chestLoot = pickedBiome == null ? BuiltInLootTables.SIMPLE_DUNGEON : ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/underground_cabin_" + pickedBiome.location().getPath());
                RandomizableContainerBlockEntity.setLootTable(accessor, random, pos.below(), chestLoot);
                break;
        }
    }

}
