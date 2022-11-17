package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.level.feature.config.MagneticRuinsFeatureConfiguration;
import com.github.alexmodguy.alexscaves.server.misc.ACLootTableRegistry;
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

public class MagneticRuinsFeature extends Feature<MagneticRuinsFeatureConfiguration> {

    public MagneticRuinsFeature(Codec<MagneticRuinsFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<MagneticRuinsFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos chunkCenter = context.origin().atY(level.getMinBuildHeight() + 3);
        List<BlockPos> genPos = new ArrayList<>();
        int surface = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, chunkCenter.getX(), chunkCenter.getZ()) - 5;
        while (chunkCenter.getY() < surface) {
            BlockPos next = chunkCenter.above();
            BlockState currentState = level.getBlockState(chunkCenter);
            BlockState nextState = level.getBlockState(next);
            if (!canReplace(currentState) && canReplace(nextState)) {
                genPos.add(chunkCenter);
            }
            chunkCenter = next;
        }

        if(genPos.isEmpty()){
            return false;
        }
        BlockPos blockpos = genPos.size() <= 1 ? genPos.get(0) : genPos.get(randomsource.nextInt(genPos.size() - 1));

        Rotation rotation = Rotation.getRandom(randomsource);
        MagneticRuinsFeatureConfiguration config = context.config();
        int i = randomsource.nextInt(config.structures.size());
        StructureTemplateManager structuretemplatemanager = level.getLevel().getServer().getStructureManager();
        StructureTemplate structuretemplate = structuretemplatemanager.getOrCreate(config.structures.get(i));
        ChunkPos chunkpos = new ChunkPos(blockpos);
        BoundingBox boundingbox = new BoundingBox(chunkpos.getMinBlockX() - 16, level.getMinBuildHeight(), chunkpos.getMinBlockZ() - 16, chunkpos.getMaxBlockX() + 16, level.getMaxBuildHeight(), chunkpos.getMaxBlockZ() + 16);
        StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setRotation(rotation).setBoundingBox(boundingbox).setRandom(randomsource);
        Vec3i vec3i = structuretemplate.getSize(rotation);
        BlockPos blockpos1 = blockpos.offset(-vec3i.getX() / 2, 0, -vec3i.getZ() / 2);
        while(canReplace(level.getBlockState(blockpos1)) && blockpos1.getY() < level.getMinBuildHeight()){
            blockpos1 = blockpos1.below();
        }
        BlockPos blockpos2 = structuretemplate.getZeroPositionWithTransform(blockpos1, Mirror.NONE, rotation);
        if (structuretemplate.placeInWorld(level, blockpos2, blockpos2, structureplacesettings, randomsource, 4)) {
            for (StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : StructureTemplate.processBlockInfos(level, blockpos2, blockpos2, structureplacesettings, getDataMarkers(structuretemplate, blockpos2, rotation, false))) {
                String marker = structuretemplate$structureblockinfo.nbt.getString("metadata");
                if (marker.equals("magnetic_ruins_loot_chest")) {
                    level.setBlock(structuretemplate$structureblockinfo.pos, Blocks.CAVE_AIR.defaultBlockState(), 4);
                    RandomizableContainerBlockEntity.setLootTable(level, randomsource, structuretemplate$structureblockinfo.pos.below(), ACLootTableRegistry.MAGNETIC_RUINS_CHEST);

                }
            }
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return state.isAir() || state.getMaterial().isReplaceable();
    }

    private static List<StructureTemplate.StructureBlockInfo> getDataMarkers(StructureTemplate structuretemplate, BlockPos p_227326_, Rotation p_227327_, boolean p_227328_) {
        List<StructureTemplate.StructureBlockInfo> list = structuretemplate.filterBlocks(p_227326_, (new StructurePlaceSettings()).setRotation(p_227327_), Blocks.STRUCTURE_BLOCK, p_227328_);
        List<StructureTemplate.StructureBlockInfo> list1 = Lists.newArrayList();

        for (StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : list) {
            if (structuretemplate$structureblockinfo.nbt != null) {
                StructureMode structuremode = StructureMode.valueOf(structuretemplate$structureblockinfo.nbt.getString("mode"));
                if (structuremode == StructureMode.DATA) {
                    list1.add(structuretemplate$structureblockinfo);
                }
            }
        }

        return list1;
    }
}
