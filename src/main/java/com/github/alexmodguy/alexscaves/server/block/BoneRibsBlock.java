package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BoneRibsBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty UNDER = BooleanProperty.create("under");

    public final Map<BlockState, VoxelShape> shapeMap = new HashMap<>();
    private static final VoxelShape SHAPE_TOP = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape SHAPE_NORTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_WEST = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_EAST = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BoneRibsBlock() {
        super(Properties.of().mapColor(MapColor.SAND).requiresCorrectToolForDrops().strength(2.0F).sound(SoundType.BONE_BLOCK).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(UNDER, false).setValue(FACING, Direction.NORTH));
    }

    protected VoxelShape getRibsShape(BlockState state){
        if(shapeMap.containsKey(state)){
            return shapeMap.get(state);
        }else{
            VoxelShape merge = state.getValue(UNDER) ? SHAPE_BOTTOM : SHAPE_TOP;
            switch (state.getValue(FACING)){
                case NORTH:
                    merge = Shapes.join(merge, SHAPE_NORTH, BooleanOp.OR);
                    break;
                case EAST:
                    merge = Shapes.join(merge, SHAPE_EAST, BooleanOp.OR);
                    break;
                case SOUTH:
                    merge = Shapes.join(merge, SHAPE_SOUTH, BooleanOp.OR);
                    break;
                case WEST:
                    merge = Shapes.join(merge, SHAPE_WEST, BooleanOp.OR);
                    break;
            }
            shapeMap.put(state, merge);
            return merge;
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getRibsShape(state);
    }

    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos blockPos) {
        return 1.0F;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Vec3 vec3 = context.getClickLocation().subtract(Vec3.atLowerCornerOf(context.getClickedPos()));
        BlockState placedOn = levelaccessor.getBlockState(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
        Direction facing = context.getClickedFace().getAxis().isHorizontal() ? context.getClickedFace() : context.getHorizontalDirection().getOpposite();
        boolean under = context.getClickedFace() == Direction.DOWN || vec3.y < 0.5F;
        if(placedOn.is(this) && context.getClickedFace().getAxis().isVertical()){
            facing = placedOn.getValue(FACING);
            under = !placedOn.getValue(UNDER);
        }
        return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER)).setValue(FACING, facing).setValue(UNDER, under);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(WATERLOGGED, UNDER, FACING);
    }

    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }
}
