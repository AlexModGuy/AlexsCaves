package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class CycadBlock extends BushBlock {
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    public static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 21, 12);
    public static final VoxelShape SHAPE_TOP = buildShape(
            Block.box(4, 0, 4, 12, 8, 12),
            Block.box(5, 8, 5, 11, 16, 11),
            Block.box(0, 8, 0, 16, 10, 16)
    );

    public CycadBlock() {
        super(Properties.of(Material.WOOD, MaterialColor.METAL).requiresCorrectToolForDrops().dynamicShape().strength(1F, 2.0F).sound(SoundType.WOOD).offsetType(OffsetType.XZ));
        this.registerDefaultState(this.defaultBlockState().setValue(TOP, Boolean.valueOf(true)));
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos blockPos) {
        return true;
    }

    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(getter, pos);
        VoxelShape shape = state.getValue(TOP) ? SHAPE_TOP : SHAPE;
        return shape.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public long getSeed(BlockState blockState, BlockPos pos) {
        return Mth.getSeed(pos.getX(), 0, pos.getZ());
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return super.mayPlaceOn(state, level, pos) || state.getBlock() == this;
    }

    @Override
    public float getMaxHorizontalOffset() {
        return 0.2F;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        BlockState prev = super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
        if (prev.getBlock() == this) {
            if (levelAccessor.getBlockState(blockPos.above()).getBlock() == this) {
                prev = prev.setValue(TOP, false);
            } else {
                prev = prev.setValue(TOP, true);
            }
        }
        return prev;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState above = levelaccessor.getBlockState(blockpos.above());
        return this.defaultBlockState().setValue(TOP, above.getBlock() != this);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(TOP);
    }

    private static VoxelShape buildShape(VoxelShape... from) {
        return Stream.of(from).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos pos, PathComputationType type) {
        return false;
    }
}
