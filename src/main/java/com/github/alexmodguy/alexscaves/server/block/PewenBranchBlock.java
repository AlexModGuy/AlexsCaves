package com.github.alexmodguy.alexscaves.server.block;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PewenBranchBlock extends Block implements SimpleWaterloggedBlock {

    public static final Map<Integer, VoxelShape> SHAPES_BY_ROTATION = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, Block.box(6, 2, 0, 10, 6, 16));
        map.put(2, Block.box(0, 2, 6, 16, 6, 10));
        map.put(4, Block.box(6, 2, 0, 10, 6, 16));
        map.put(6, Block.box(0, 2, 6, 16, 6, 10));
        map.put(1, Block.box(0, 2, 0, 16, 6, 16));
        map.put(3, Block.box(0, 2, 0, 16, 6, 16));
        map.put(5, Block.box(0, 2, 0, 16, 6, 16));
        map.put(7, Block.box(0, 2, 0, 16, 6, 16));
    });


    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty PINES = BooleanProperty.create("pines");
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);

    public PewenBranchBlock() {
        super(BlockBehaviour.Properties.of().mapColor(DyeColor.GREEN).strength(1.0F).sound(ACSoundTypes.PEWEN_BRANCH).randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(ROTATION, Integer.valueOf(0)).setValue(PINES, true));
    }

    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return state.getValue(PINES) ? SoundType.GRASS : super.getSoundType(state, level, pos, entity);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPES_BY_ROTATION.getOrDefault(state.getValue(ROTATION), Shapes.empty());
    }

    public void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource randomSource) {
        this.tick(state, serverLevel, pos, randomSource);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        int rot = Mth.floor((double) (context.getRotation() * 8.0F / 360.0F) + 0.5D) & 7;
        BlockPos checkPos = blockpos.offset(getOffsetConnectToPos(rot));
        int loops = 0;
        while (!isGoodBase(levelaccessor.getBlockState(checkPos), levelaccessor, checkPos) && loops < 7) {
            rot++;
            if (rot > 7) {
                rot = 0;
            }
            checkPos = blockpos.offset(getOffsetConnectToPos(rot));
            loops++;
        }
        return this.defaultBlockState().setValue(ROTATION, Integer.valueOf(rot)).setValue(PINES, hasPines(rot, levelaccessor, blockpos)).setValue(WATERLOGGED, Boolean.valueOf(levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER));
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, Integer.valueOf(rotation.rotate(state.getValue(ROTATION), 8)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, Integer.valueOf(mirror.mirror(state.getValue(ROTATION), 8)));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.offset(getOffsetConnectToPos(state.getValue(ROTATION)));
        BlockState state1 = level.getBlockState(blockpos);
        return isGoodBase(state1, level, blockpos);
    }

    public boolean isGoodBase(BlockState state, LevelReader level, BlockPos pos) {
        return state.getBlock() == this || state.isCollisionShapeFullBlock(level, pos);
    }

    public boolean hasPines(int rot, LevelReader levelReader, BlockPos pos) {
        BlockPos checkAt = pos.subtract(getOffsetConnectToPos(rot));
        return levelReader.getBlockState(checkAt).getBlock() != this;
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (!state.canSurvive(level, pos)) {
            for (BlockPos tick : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 1, 1))) {
                level.scheduleTick(tick, this, 1);
            }
            level.destroyBlock(pos, true);
        }

    }

    public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos pos, PathComputationType type) {
        return false;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (!state.canSurvive(levelAccessor, blockPos)) {
            levelAccessor.scheduleTick(blockPos, this, 1);
        }
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        boolean shouldHavePines = hasPines(state.getValue(ROTATION), levelAccessor, blockPos);
        if (shouldHavePines != state.getValue(PINES)) {
            return state.setValue(PINES, shouldHavePines);
        }
        return super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, WATERLOGGED, PINES);
    }

    @Override
    public float getMaxHorizontalOffset() {
        return 0.0F;
    }

    @Override
    public float getMaxVerticalOffset() {
        return 0.75F;
    }

    public static Vec3i getOffsetConnectToPos(int rotationValue) {
        switch (rotationValue) {
            case 0: //North
                return new Vec3i(0, 0, 1);
            case 1: //North East
                return new Vec3i(-1, 0, 1);
            case 2: //East
                return new Vec3i(-1, 0, 0);
            case 3: //South East
                return new Vec3i(-1, 0, -1);
            case 4: //South
                return new Vec3i(0, 0, -1);
            case 5: //South West
                return new Vec3i(1, 0, -1);
            case 6: //West
                return new Vec3i(1, 0, 0);
            case 7: //North West
                return new Vec3i(1, 0, 1);
        }
        return Vec3i.ZERO;
    }
}
