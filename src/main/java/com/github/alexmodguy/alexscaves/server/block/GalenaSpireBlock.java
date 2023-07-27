package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class GalenaSpireBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final IntegerProperty SHAPE = IntegerProperty.create("shape", 0, 3);
    public static final VoxelShape SHAPE_0 = Block.box(2, 0, 2, 14, 16, 14);
    public static final VoxelShape SHAPE_1 = Block.box(4, 0, 4, 12, 16, 12);
    public static final VoxelShape SHAPE_2 = Block.box(4, 0, 4, 12, 16, 12);
    public static final VoxelShape SHAPE_3_TOP = Block.box(6, 9, 6, 10, 16, 10);
    public static final VoxelShape SHAPE_3_BOTTOM = Block.box(6, 0, 6, 10, 9, 10);

    public GalenaSpireBlock() {
        super(BlockBehaviour.Properties.of().mapColor(DyeColor.PURPLE).strength(1.5F).sound(SoundType.DEEPSLATE));
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(DOWN, Boolean.valueOf(false)).setValue(SHAPE, 3));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(getter, pos);
        VoxelShape shape = SHAPE_0;
        switch (state.getValue(SHAPE)) {
            case 0:
                shape = SHAPE_0;
                break;
            case 1:
                shape = SHAPE_1;
                break;
            case 2:
                shape = SHAPE_2;
                break;
            case 3:
                shape = state.getValue(DOWN) ? SHAPE_3_TOP : SHAPE_3_BOTTOM;
                break;
        }
        return shape.move(vec3.x, vec3.y, vec3.z);
    }


    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        boolean down = context.getClickedFace() == Direction.DOWN;
        BlockPos blockpos = context.getClickedPos();
        BlockState above = levelaccessor.getBlockState(blockpos.above());
        BlockState below = levelaccessor.getBlockState(blockpos.below());
        return this.defaultBlockState().setValue(DOWN, down).setValue(SHAPE, down ? getShapeInt(below, above, down) : getShapeInt(above, below, down)).setValue(WATERLOGGED, Boolean.valueOf(levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER));
    }

    public int getShapeInt(BlockState above, BlockState below, boolean down) {
        if (above.getBlock() == ACBlockRegistry.TESLA_BULB.get()) {
            return 2;
        }
        if (!isGalenaSpireConnectable(above, down)) {
            return 3;
        } else {
            int aboveShape = above.getValue(SHAPE);
            if (aboveShape <= 1) {
                boolean connectedUnder;
                if (down) {
                    connectedUnder = above.getBlock() != ACBlockRegistry.GALENA_SPIRE.get();
                } else {
                    connectedUnder = below.getBlock() != ACBlockRegistry.GALENA_SPIRE.get();
                }
                return connectedUnder ? 0 : 1;
            } else {
                return aboveShape - 1;
            }
        }
    }

    @Deprecated
    public boolean canSurvive(BlockState state, LevelReader levelAccessor, BlockPos blockPos) {
        BlockState above = levelAccessor.getBlockState(blockPos.above());
        BlockState below = levelAccessor.getBlockState(blockPos.below());
        if (state.getValue(DOWN)) {
            return above.isFaceSturdy(levelAccessor, blockPos.above(), Direction.UP) || isGalenaSpireConnectable(above, true);
        } else {
            return below.isFaceSturdy(levelAccessor, blockPos.below(), Direction.DOWN) || isGalenaSpireConnectable(below, false);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        BlockState prev = state.canSurvive(levelAccessor, blockPos) ? super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1) : Blocks.AIR.defaultBlockState();
        if (prev.getBlock() == this) {
            BlockState above = levelAccessor.getBlockState(blockPos.above());
            BlockState below = levelAccessor.getBlockState(blockPos.below());
            boolean down = prev.getValue(DOWN);
            int shapeInt = down ? getShapeInt(below, above, down) : getShapeInt(above, below, down);
            prev = prev.setValue(SHAPE, shapeInt);
        }
        return prev;
    }

    public static boolean isGalenaSpireConnectable(BlockState state, boolean down) {
        return state.getBlock() == ACBlockRegistry.GALENA_SPIRE.get() && state.getValue(DOWN) == down;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(SHAPE, DOWN, WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
