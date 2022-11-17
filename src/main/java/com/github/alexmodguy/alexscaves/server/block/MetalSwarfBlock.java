package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

class MetalSwarfBlock extends FallingBlock {

    public MetalSwarfBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return 0X404253;
    }
}