package com.github.alexmodguy.alexscaves.server.level.structure.processor;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.BaleenBoneBlock;
import com.github.alexmodguy.alexscaves.server.block.ThinBoneBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import javax.annotation.Nullable;

public class WhalefallProcessor extends StructureProcessor {

    public static final Codec<WhalefallProcessor> CODEC = Codec.unit(() -> {
        return WhalefallProcessor.INSTANCE;
    });
    public static final WhalefallProcessor INSTANCE = new WhalefallProcessor();

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPosUnused, BlockPos pos, StructureTemplate.StructureBlockInfo relativeInfo, StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings) {
        RandomSource randomsource = settings.getRandom(info.pos);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(info.pos.getX(), info.pos.getY(), info.pos.getZ());
        while(sinkThrough(levelReader.getBlockState(mutableBlockPos)) && mutableBlockPos.getY() > levelReader.getMinBuildHeight()){
            mutableBlockPos.move(0, -1, 0);
        }
        int i = mutableBlockPos.getY();
        int j = relativeInfo.pos.getY() + 1;
        BlockState in = info.state;
        if(in.is(ACBlockRegistry.BALEEN_BONE.get()) && randomsource.nextFloat() < 0.2){
            Direction.Axis axis = in.getValue(BaleenBoneBlock.X) ? Direction.Axis.X : Direction.Axis.Z;
            in = ACBlockRegistry.THIN_BONE.get().defaultBlockState().setValue(ThinBoneBlock.AXIS, axis).setValue(ThinBoneBlock.OFFSET, 0);
        }
        return new StructureTemplate.StructureBlockInfo(new BlockPos(info.pos.getX(), i + j, info.pos.getZ()), in, info.nbt);
    }

    private boolean sinkThrough(BlockState blockState) {
        return !blockState.getFluidState().isEmpty() || blockState.is(ACTagRegistry.WHALEFALL_IGNORES) || blockState.isAir();
    }

    protected StructureProcessorType<?> getType() {
        return ACStructureProcessorRegistry.WHALEFALL.get();
    }
}
