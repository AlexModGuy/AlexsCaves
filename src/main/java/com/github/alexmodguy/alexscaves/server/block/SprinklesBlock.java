package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SprinklesBlock extends Block {

    public static final IntegerProperty AMOUNT = IntegerProperty.create("amount", 1, 4);
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public SprinklesBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).noOcclusion().noCollission().pushReaction(PushReaction.DESTROY).instabreak().sound(ACSoundTypes.HARD_CANDY).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(AMOUNT, Integer.valueOf(1)));
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
        return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && blockState.getValue(AMOUNT) < 4 ? true : super.canBeReplaced(blockState, context);
    }
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        return blockstate.is(this) ? blockstate.setValue(AMOUNT, Integer.valueOf(Math.min(4, blockstate.getValue(AMOUNT) + 1))) : this.defaultBlockState();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(AMOUNT);
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return state.getFluidState().isEmpty();
    }

    public boolean isPathfindable(BlockState blockState, BlockGetter getter, BlockPos blockPos, PathComputationType computationType) {
        return computationType == PathComputationType.AIR && !this.hasCollision ? true : super.isPathfindable(blockState, getter, blockPos, computationType);
    }
}
