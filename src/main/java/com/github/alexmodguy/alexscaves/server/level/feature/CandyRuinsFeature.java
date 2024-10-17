package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.config.UndergroundRuinsFeatureConfiguration;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Set;

public class CandyRuinsFeature extends UndergroundRuinsFeature {

    public CandyRuinsFeature(Codec<UndergroundRuinsFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean canGenerateAt(WorldGenLevel level, BlockPos blockpos) {
        return level.getBlockState(blockpos).is(ACBlockRegistry.BLOCK_OF_CHOCOLATE.get()) || level.getBlockState(blockpos).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get());
    }

    protected int calculateSinkBy(WorldGenLevel level, BlockPos blockpos1, StructureTemplate structuretemplate, int sinkByIn) {
        return 1 + level.getRandom().nextInt(sinkByIn + 1);
    }
}
