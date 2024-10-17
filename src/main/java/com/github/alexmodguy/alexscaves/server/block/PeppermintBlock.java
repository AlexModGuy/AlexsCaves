package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class PeppermintBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private final VoxelShape shapeUp;
    private final VoxelShape shapeDown;
    private final VoxelShape shapeEast;
    private final VoxelShape shapeWest;
    private final VoxelShape shapeSouth;
    private final VoxelShape shapeNorth;

    public PeppermintBlock(double distFromEdge, double height) {
        super(Properties.of().mapColor(MapColor.SAND).requiresCorrectToolForDrops().strength(2.0F).sound(ACSoundTypes.HARD_CANDY).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(FACING, Direction.UP));
        shapeUp = Block.box(distFromEdge, 0.0D, distFromEdge, 16.0D - distFromEdge, height, 16.0D - distFromEdge);
        shapeDown = Block.box(distFromEdge, 16.0D - height, distFromEdge, 16.0D - distFromEdge, 16.0D, 16.0D - distFromEdge);
        shapeEast = Block.box(0.0D, distFromEdge, distFromEdge, height, 16.0D - distFromEdge, 16.0D - distFromEdge);
        shapeWest = Block.box(16.0D - height, distFromEdge, distFromEdge, 16.0D, 16.0D - distFromEdge, 16.0D - distFromEdge);
        shapeSouth = Block.box(distFromEdge, distFromEdge, 0.0D, 16.0D - distFromEdge, 16.0D - distFromEdge, height);
        shapeNorth = Block.box(distFromEdge, distFromEdge, 16.0D - height, 16.0D - distFromEdge, 16.0D - distFromEdge, 16.0D);
    }


    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case UP:
                return shapeUp;
            case DOWN:
                return shapeDown;
            case EAST:
                return shapeEast;
            case WEST:
                return shapeWest;
            case NORTH:
                return shapeNorth;
            case SOUTH:
                return shapeSouth;
        }
        return shapeUp;

    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER)).setValue(FACING, context.getClickedFace());
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(WATERLOGGED, FACING);
    }

    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }
}
