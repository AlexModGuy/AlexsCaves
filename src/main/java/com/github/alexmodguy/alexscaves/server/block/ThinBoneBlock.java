package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ThinBoneBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty OFFSET = IntegerProperty.create("offset", 0, 2);
    public final Map<BlockState, VoxelShape> shapeMap = new HashMap<>();
    public static final VoxelShape SHAPE_X = Block.box(0, 6, 6, 16, 10, 10);
    public static final VoxelShape SHAPE_Y = Block.box(6, 0, 6, 10, 16, 10);
    public static final VoxelShape SHAPE_Z = Block.box(6, 6, 0, 10, 10, 16);

    public ThinBoneBlock() {
        super(Properties.of().mapColor(MapColor.SAND).requiresCorrectToolForDrops().strength(2.0F).sound(SoundType.BONE_BLOCK).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(OFFSET, 1).setValue(AXIS, Direction.Axis.Y));
    }

    protected VoxelShape getThinBoneShape(BlockState state) {
        if (shapeMap.containsKey(state)) {
            return shapeMap.get(state);
        } else {
            VoxelShape merge;
            switch (state.getValue(AXIS)) {
                case X:
                    merge = SHAPE_X;
                    break;
                case Y:
                    merge = SHAPE_Y;
                    break;
                case Z:
                    merge = SHAPE_Z;
                    break;
                default:
                    merge = SHAPE_Z;
                    break;
            }
            int offset;
            if (state.getValue(AXIS) == Direction.Axis.Y || state.getValue(OFFSET) == 1) {
                offset = 0;
            } else {
                offset = state.getValue(OFFSET) == 0 ? -6 : 6;
                merge = merge.move(0, offset / 16F, 0);
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
        return getThinBoneShape(state);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        int offset = 0;
        if (context.getClickedFace().getAxis().isHorizontal()) {
            Vec3 vec3 = context.getClickLocation().subtract(Vec3.atLowerCornerOf(context.getClickedPos()));
            if (vec3.y < 0.33F) {
                offset = 0;
            } else if (vec3.y < 0.66F) {
                offset = 1;
            } else {
                offset = 2;
            }
        }
        return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER)).setValue(AXIS, context.getClickedFace().getAxis()).setValue(OFFSET, offset);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(WATERLOGGED, AXIS, OFFSET);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }

}
