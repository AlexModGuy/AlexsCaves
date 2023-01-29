package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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