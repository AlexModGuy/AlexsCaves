package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

class FallingBlockWithColor extends FallingBlock {

    private int dustColor;

    public FallingBlockWithColor(BlockBehaviour.Properties properties, int dustColor) {
        super(properties);
        this.dustColor = dustColor;
    }

    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return dustColor;
    }

}