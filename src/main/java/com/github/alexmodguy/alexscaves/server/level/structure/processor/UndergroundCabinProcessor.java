package com.github.alexmodguy.alexscaves.server.level.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class UndergroundCabinProcessor extends StructureProcessor {

    private static final UndergroundCabinProcessor INSTANCE = new UndergroundCabinProcessor();

    public static final Codec<UndergroundCabinProcessor> CODEC = Codec.unit(() -> {
        return UndergroundCabinProcessor.INSTANCE;
    });

    public UndergroundCabinProcessor() {
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPosUnused, BlockPos pos, StructureTemplate.StructureBlockInfo relativeInfo, StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings) {
        RandomSource randomsource = settings.getRandom(info.pos());
        BlockState in = info.state();
        if(in.is(Blocks.COBBLESTONE) && randomsource.nextFloat() < 0.2){
            if(randomsource.nextFloat() > 0.3F){
                return null;
            }else{
                return new StructureTemplate.StructureBlockInfo(info.pos(), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), info.nbt());
            }
        }else if(in.is(BlockTags.LOGS) || in.is(BlockTags.PLANKS)){
            float above = relativeInfo.pos().getY() / 7F;
            if(above * randomsource.nextFloat() > 0.2F){
                return null;
            }
        }
        return info;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ACStructureProcessorRegistry.UNDERGROUND_CABIN.get();
    }
}
