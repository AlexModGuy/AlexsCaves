package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallCandyCaneBlock extends Block {

    public static final IntegerProperty COUNT = IntegerProperty.create("amount", 1, 4);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 14.0D, 13.0D);

    public SmallCandyCaneBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).noOcclusion().noCollission().pushReaction(PushReaction.DESTROY).instabreak().sound(ACSoundTypes.HARD_CANDY).dynamicShape().offsetType(BlockBehaviour.OffsetType.XZ));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(COUNT, Integer.valueOf(1)));
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        Direction direction = rotation.rotate(state.getValue(FACING));
        return direction.getAxis() == Direction.Axis.Y ? state : state.setValue(FACING, direction);
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState state1 = level.getBlockState(blockpos);
        return state1.isFaceSturdy(level, blockpos, Direction.UP);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        return !state.canSurvive(levelAccessor, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && blockState.getValue(COUNT) < 4 ? true : super.canBeReplaced(blockState, context);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(getter, pos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        return blockstate.is(this) ? blockstate.setValue(COUNT, Integer.valueOf(Math.min(4, blockstate.getValue(COUNT) + 1))) : this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, COUNT);
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return state.getFluidState().isEmpty();
    }

    public boolean isPathfindable(BlockState blockState, BlockGetter getter, BlockPos blockPos, PathComputationType computationType) {
        return computationType == PathComputationType.AIR && !this.hasCollision ? true : super.isPathfindable(blockState, getter, blockPos, computationType);
    }
}
