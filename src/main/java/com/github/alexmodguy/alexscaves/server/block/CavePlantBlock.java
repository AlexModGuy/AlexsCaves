package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CavePlantBlock extends BushBlock {
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D);
    protected static final VoxelShape SHAPE_TALL = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 7.0D, 13.0D);
    private final boolean taller;

    public CavePlantBlock(Properties props, boolean taller) {
        super(props);
        this.taller = taller;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
        return taller ? SHAPE_TALL : SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter getter, BlockPos pos) {
        return blockState.isFaceSturdy(getter, pos, Direction.UP, SupportType.FULL);
    }
}
