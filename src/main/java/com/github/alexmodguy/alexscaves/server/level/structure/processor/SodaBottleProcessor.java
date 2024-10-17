package com.github.alexmodguy.alexscaves.server.level.structure.processor;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class SodaBottleProcessor extends StructureProcessor {

    public static final SodaBottleProcessor INSTANCE = new SodaBottleProcessor();

    public static final Codec<SodaBottleProcessor> CODEC = Codec.unit(() -> {
        return SodaBottleProcessor.INSTANCE;
    });

    public SodaBottleProcessor() {
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPosUnused, BlockPos pos, StructureTemplate.StructureBlockInfo relativeInfo, StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings) {
        BlockState in = info.state();
        if(in.is(Blocks.PURPLE_CONCRETE)){
            return new StructureTemplate.StructureBlockInfo(info.pos(), ACBlockRegistry.PURPLE_SODA.get().defaultBlockState(), info.nbt());
        }else if(in.is(Blocks.ORANGE_CONCRETE)){
            return new StructureTemplate.StructureBlockInfo(info.pos(), ACBlockRegistry.PURPLE_SODA.get().defaultBlockState(), info.nbt());
        }
        return info;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ACStructureProcessorRegistry.UNDERGROUND_CABIN.get();
    }
}
