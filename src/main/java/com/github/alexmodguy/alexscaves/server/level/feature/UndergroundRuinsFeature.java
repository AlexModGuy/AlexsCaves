package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.config.UndergroundRuinsFeatureConfiguration;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;

public class UndergroundRuinsFeature extends Feature<UndergroundRuinsFeatureConfiguration> {

    public UndergroundRuinsFeature(Codec<UndergroundRuinsFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<UndergroundRuinsFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos chunkCenter = context.origin().atY(level.getMinBuildHeight() + 3);
        List<BlockPos> genPos = new ArrayList<>();
        int surface = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, chunkCenter.getX(), chunkCenter.getZ()) - 5;
        int j = 0;
        while (chunkCenter.getY() < surface) {
            BlockPos next = chunkCenter.above();
            BlockState currentState = level.getBlockState(chunkCenter);
            BlockState nextState = level.getBlockState(next);
            if (!canReplace(currentState, j) && canReplace(nextState, j + 1)) {
                genPos.add(chunkCenter);
            }
            j++;
            chunkCenter = next;
        }

        if (genPos.isEmpty()) {
            return false;
        }
        BlockPos blockpos = genPos.size() <= 1 ? genPos.get(0) : genPos.get(randomsource.nextInt(genPos.size() - 1));
        if (!canGenerateAt(level, blockpos)) {
            return false;
        }
        Rotation rotation = Rotation.getRandom(randomsource);
        UndergroundRuinsFeatureConfiguration config = context.config();
        int i = randomsource.nextInt(config.structures.size());
        StructureTemplateManager structuretemplatemanager = level.getLevel().getServer().getStructureManager();
        StructureTemplate structuretemplate = structuretemplatemanager.getOrCreate(config.structures.get(i));
        ChunkPos chunkpos = new ChunkPos(blockpos);
        BoundingBox boundingbox = new BoundingBox(chunkpos.getMinBlockX() - 16, level.getMinBuildHeight(), chunkpos.getMinBlockZ() - 16, chunkpos.getMaxBlockX() + 16, level.getMaxBuildHeight(), chunkpos.getMaxBlockZ() + 16);
        StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setRotation(rotation).setBoundingBox(boundingbox).setRandom(randomsource);
        structureplacesettings = modifyPlacementSettings(structureplacesettings);
        Vec3i vec3i = structuretemplate.getSize(rotation);
        BlockPos blockpos1 = blockpos.offset(-vec3i.getX() / 2, 0, -vec3i.getZ() / 2);
        int replaceDown = 0;
        while (skipsOver(level.getBlockState(blockpos1), replaceDown) && blockpos1.getY() < level.getMinBuildHeight()) {
            blockpos1 = blockpos1.below();
            replaceDown++;
        }
        blockpos1 = blockpos1.below(calculateSinkBy(level, blockpos1, structuretemplate, config.sinkBy));
        BlockPos blockpos2 = structuretemplate.getZeroPositionWithTransform(blockpos1, Mirror.NONE, rotation);
        if (structuretemplate.placeInWorld(level, blockpos2, blockpos2, structureplacesettings, randomsource, 18)) {
            for (StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : StructureTemplate.processBlockInfos(level, blockpos2, blockpos2, structureplacesettings, getDataMarkers(structuretemplate, blockpos2, rotation, false))) {
                String marker = structuretemplate$structureblockinfo.nbt().getString("metadata");
                if (marker.equals("loot_chest")) {
                    level.setBlock(structuretemplate$structureblockinfo.pos(), Blocks.CAVE_AIR.defaultBlockState(), 3);
                    RandomizableContainerBlockEntity.setLootTable(level, randomsource, structuretemplate$structureblockinfo.pos().below(), context.config().chestLoot);
                } else {
                    processMarker(marker, level, structuretemplate$structureblockinfo.pos(), randomsource);
                }
            }
            processBoundingBox(level, structuretemplate.getBoundingBox(structureplacesettings, blockpos2), randomsource);
        }
        return true;
    }

    protected int calculateSinkBy(WorldGenLevel level, BlockPos blockpos1, StructureTemplate structuretemplate, int sinkByIn) {
        return sinkByIn;
    }

    public void processBoundingBox(WorldGenLevel level, BoundingBox boundingBox, RandomSource randomsource) {
    }

    public StructurePlaceSettings modifyPlacementSettings(StructurePlaceSettings structureplacesettings) {
        return structureplacesettings;
    }

    public void processMarker(String marker, WorldGenLevel level, BlockPos pos, RandomSource randomsource) {
    }

    protected boolean canGenerateAt(WorldGenLevel level, BlockPos blockpos) {
        return true;
    }

    protected boolean canReplace(BlockState state, int already) {
        return (state.isAir() || state.canBeReplaced()) && (state.getFluidState().getFluidType() != ACFluidRegistry.ACID_FLUID_TYPE.get() || already < 3);
    }

    protected boolean skipsOver(BlockState state, int already) {
        return canReplace(state, already);
    }

    private static List<StructureTemplate.StructureBlockInfo> getDataMarkers(StructureTemplate structuretemplate, BlockPos p_227326_, Rotation p_227327_, boolean p_227328_) {
        List<StructureTemplate.StructureBlockInfo> list = structuretemplate.filterBlocks(p_227326_, (new StructurePlaceSettings()).setRotation(p_227327_), Blocks.STRUCTURE_BLOCK, p_227328_);
        List<StructureTemplate.StructureBlockInfo> list1 = Lists.newArrayList();

        for (StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : list) {
            if (structuretemplate$structureblockinfo.nbt() != null) {
                StructureMode structuremode = StructureMode.valueOf(structuretemplate$structureblockinfo.nbt().getString("mode"));
                if (structuremode == StructureMode.DATA) {
                    list1.add(structuretemplate$structureblockinfo);
                }
            }
        }

        return list1;
    }

}
