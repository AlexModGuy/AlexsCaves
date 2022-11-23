package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.PewenBranchBlock;
import com.github.alexmodguy.alexscaves.server.level.feature.config.MagneticRuinsFeatureConfiguration;
import com.github.alexmodguy.alexscaves.server.misc.ACLootTableRegistry;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;

public class PewenTreeFeature extends Feature<NoneFeatureConfiguration> {

    public PewenTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos treeBottom = context.origin().above();
        int height = 10 + randomsource.nextInt(10);
        int penumbraLvls = 3 + randomsource.nextInt(2);
        if(!checkCanTreePlace(level, treeBottom, height, penumbraLvls)){
            return false;
        }
        int j = height - penumbraLvls;
        for(int i = 0; i <= height; i++) {
            BlockPos trunkPos = treeBottom.above(i);
            level.setBlock(trunkPos, i == height ? ACBlockRegistry.PEWEN_WOOD.get().defaultBlockState() : ACBlockRegistry.PEWEN_LOG.get().defaultBlockState(), 4);
            if(i > j){
                buildPenumbra(level, trunkPos, i - j, penumbraLvls, randomsource);
            }else if(i > j - 1){
                buildPenumbra(level, trunkPos, 1, 1, randomsource);
            }else if(randomsource.nextInt(5) == 0){
                int branchRot = randomsource.nextInt(7);
                BlockPos offset = trunkPos.subtract(PewenBranchBlock.getOffsetConnectToPos(branchRot));
                if(canReplace(level.getBlockState(offset))){
                    level.setBlock(offset, ACBlockRegistry.PEWEN_BRANCH.get().defaultBlockState().setValue(PewenBranchBlock.ROTATION, branchRot).setValue(PewenBranchBlock.PINES, true), 4);
                }
            }
        }
        return true;
    }

    private void buildPenumbra(WorldGenLevel level, BlockPos trunkPos, int currentIndex, int maxIndex, RandomSource randomSource) {
        int branchWidth = maxIndex - currentIndex + 1;
        for(int i = 0; i < 8; i++){
            int randBranchLength = branchWidth - randomSource.nextInt(1);
            int actualLength = i % 2 != 0 && randBranchLength > 1 ? randBranchLength - 1 : randBranchLength;
            for(int j = 1; j <= actualLength; j++){
                BlockPos offset = trunkPos.subtract(PewenBranchBlock.getOffsetConnectToPos(i).multiply(j));
                if(canReplace(level.getBlockState(offset))){
                    level.setBlock(offset, ACBlockRegistry.PEWEN_BRANCH.get().defaultBlockState().setValue(PewenBranchBlock.ROTATION, i).setValue(PewenBranchBlock.PINES, j == actualLength), 4);
                }
            }
        }
    }

    private boolean checkCanTreePlace(WorldGenLevel level, BlockPos treeBottom, int height, int penumbraLvls) {
        BlockState below = level.getBlockState(treeBottom.below());
        if(!below.is(BlockTags.DIRT)){
            return false;
        }
        for(int i = 0; i < height; i++){
            if(!canReplace(level.getBlockState(treeBottom.above(i)))){
                return false;
            }
        }
        BlockPos treeTop = treeBottom.above(height - penumbraLvls);
        for(BlockPos checkLeaf : BlockPos.betweenClosed(treeTop.offset(-2, -1, -2), treeTop.offset(2, penumbraLvls - 1, 2))){
            if(!canReplace(level.getBlockState(checkLeaf))){
                return false;
            }
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return (state.isAir() || state.getMaterial().isReplaceable() || state.is(ACBlockRegistry.PEWEN_BRANCH.get())) && state.getFluidState().isEmpty();
    }


}
