package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;

public class GalenaPillarBlock extends RotatedPillarBlock {

    public static final IntegerProperty SHAPE = IntegerProperty.create("shape", 0, 3);

    public GalenaPillarBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(3.5F, 10.0F).sound(SoundType.DEEPSLATE));
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(SHAPE, 3));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        Direction.Axis axis = context.getClickedFace().getAxis();
        return this.defaultBlockState().setValue(AXIS, axis).setValue(SHAPE, getShapeInt(levelaccessor, context.getClickedPos(), axis));
    }

    public int getShapeInt(LevelAccessor levelAccessor, BlockPos pos, Direction.Axis axis) {
        Direction belowDir = Direction.UP;
        Direction aboveDir = Direction.UP;
        switch (axis) {
            case X:
                belowDir = Direction.WEST;
                aboveDir = Direction.EAST;
                break;
            case Y:
                belowDir = Direction.DOWN;
                aboveDir = Direction.UP;
                break;
            case Z:
                belowDir = Direction.SOUTH;
                aboveDir = Direction.NORTH;
                break;
        }
        BlockState aboveState = levelAccessor.getBlockState(pos.relative(aboveDir));
        BlockState belowState = levelAccessor.getBlockState(pos.relative(belowDir));
        boolean above = aboveState.is(this) && aboveState.getValue(AXIS) == axis;
        boolean below = belowState.is(this) && belowState.getValue(AXIS) == axis;
        if (above && below) {
            return 3;
        }
        if (!above && !below) {
            return 0;
        }
        return above ? 1 : 2;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        return state.setValue(SHAPE, getShapeInt(levelAccessor, blockPos, state.getValue(AXIS)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(SHAPE, AXIS);
    }
}
