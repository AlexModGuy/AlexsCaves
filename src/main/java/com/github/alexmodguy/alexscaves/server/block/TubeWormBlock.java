package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TubeWormBlock extends Block implements SimpleWaterloggedBlock {
    private static final VoxelShape STRAIGHT_SHAPE = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_TURN_NORTH = Block.box(4, 0, 0, 12, 8, 12);
    private static final VoxelShape SHAPE_TURN_EAST = Block.box(4, 0, 4, 16, 8, 12);
    private static final VoxelShape SHAPE_TURN_SOUTH = Block.box(4, 0, 4, 12, 8, 16);
    private static final VoxelShape SHAPE_TURN_WEST = Block.box(0, 0, 4, 12, 8, 12);
    private static final VoxelShape SHAPE_ELBOW_NORTH = ACMath.buildShape(SHAPE_TURN_NORTH, STRAIGHT_SHAPE);
    private static final VoxelShape SHAPE_ELBOW_EAST = ACMath.buildShape(SHAPE_TURN_EAST, STRAIGHT_SHAPE);
    private static final VoxelShape SHAPE_ELBOW_SOUTH = ACMath.buildShape(SHAPE_TURN_SOUTH, STRAIGHT_SHAPE);
    private static final VoxelShape SHAPE_ELBOW_WEST = ACMath.buildShape(SHAPE_TURN_WEST, STRAIGHT_SHAPE);

    public static final EnumProperty<TubeShape> TUBE_TYPE = EnumProperty.create("type", TubeShape.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public TubeWormBlock() {
        super(BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).requiresCorrectToolForDrops().strength(2F).sound(ACSoundTypes.TUBE_WORM));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(TUBE_TYPE, TubeShape.STRAIGHT));
    }

    public static boolean canSupportWormAt(Level level, BlockState state, BlockPos blockPos) {
        if (state.is(ACBlockRegistry.TUBE_WORM.get()) && state.getValue(TUBE_TYPE) != TubeShape.TURN) {
            BlockState aboveState = level.getBlockState(blockPos.above());
            return aboveState.getFluidState().is(FluidTags.WATER) && !aboveState.isFaceSturdy(level, blockPos.above(), Direction.DOWN, SupportType.CENTER);
        }
        return false;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(TUBE_TYPE) == TubeShape.STRAIGHT) {
            return STRAIGHT_SHAPE;
        } else if (state.getValue(TUBE_TYPE) == TubeShape.TURN) {
            switch (state.getValue(FACING)) {
                case NORTH:
                    return SHAPE_TURN_NORTH;
                case EAST:
                    return SHAPE_TURN_EAST;
                case SOUTH:
                    return SHAPE_TURN_SOUTH;
                case WEST:
                    return SHAPE_TURN_WEST;
            }
        } else {
            switch (state.getValue(FACING)) {
                case NORTH:
                    return SHAPE_ELBOW_NORTH;
                case EAST:
                    return SHAPE_ELBOW_EAST;
                case SOUTH:
                    return SHAPE_ELBOW_SOUTH;
                case WEST:
                    return SHAPE_ELBOW_WEST;
            }
        }
        return STRAIGHT_SHAPE;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        if (!state.canSurvive(levelAccessor, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        BlockState prior = state;
        BlockState above = levelAccessor.getBlockState(blockPos.above());
        if (state1.is(this) && direction != null && state.getValue(TUBE_TYPE) == TubeShape.STRAIGHT && !above.is(this)) {
            if (direction.getAxis().isHorizontal()) {
                BlockState below = levelAccessor.getBlockState(blockPos.below());
                if (below.isFaceSturdy(levelAccessor, blockPos.below(), Direction.UP, SupportType.CENTER)) {
                    prior = prior.setValue(TUBE_TYPE, TubeShape.TURN).setValue(FACING, direction);
                } else {
                    prior = prior.setValue(TUBE_TYPE, TubeShape.ELBOW).setValue(FACING, direction);
                }
            } else if (direction == Direction.DOWN) {
                prior = prior.setValue(TUBE_TYPE, TubeShape.STRAIGHT);
            }
        }
        return prior;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        Direction direction = context.getClickedFace();
        BlockPos blockpos = context.getClickedPos().relative(direction.getOpposite());
        BlockState neighbor = levelaccessor.getBlockState(blockpos);
        BlockState aboveNeighbor = levelaccessor.getBlockState(blockpos.above());
        if (neighbor.is(this)) {
            TubeShape tubeShape = neighbor.getValue(TUBE_TYPE);
            if (tubeShape == TubeShape.STRAIGHT && aboveNeighbor.is(this)) {
                return null;
            }
            if (tubeShape == TubeShape.ELBOW && direction != Direction.UP) {
                return null;
            }
            if (tubeShape == TubeShape.TURN && direction != neighbor.getValue(FACING)) {
                return null;
            }
        }
        BlockState tube = this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(levelaccessor.getFluidState(context.getClickedPos()).getType() == Fluids.WATER));
        if (direction.getAxis().isHorizontal()) {
            tube = tube.setValue(TUBE_TYPE, TubeShape.ELBOW).setValue(FACING, direction.getOpposite());
        }
        return tube;
    }


    public boolean canSurvive(BlockState state, LevelReader levelAccessor, BlockPos blockPos) {
        BlockState belowState = levelAccessor.getBlockState(blockPos.below());
        if (belowState.isFaceSturdy(levelAccessor, blockPos.below(), Direction.UP, SupportType.CENTER)) {
            return true;
        } else if (state.getValue(TUBE_TYPE) == TubeShape.ELBOW) {
            BlockPos offset = blockPos.relative(state.getValue(FACING));
            BlockState offsetState = levelAccessor.getBlockState(offset);
            return offsetState.isFaceSturdy(levelAccessor, offset, state.getValue(FACING).getOpposite(), SupportType.CENTER) || offsetState.is(this);
        }
        return false;
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TUBE_TYPE, WATERLOGGED);
    }

    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(3) == 0 && canSupportWormAt(level, state, pos)) {
            if (AlexsCaves.PROXY.checkIfParticleAt(ACParticleRegistry.TUBE_WORM.get(), pos)) {
                Vec3 center = Vec3.upFromBottomCenterOf(pos, 0.5F);
                level.addParticle(ACParticleRegistry.TUBE_WORM.get(), center.x, center.y, center.z, 0, 0, 0);
            }
        }
    }

    public enum TubeShape implements StringRepresentable {
        STRAIGHT("straight"),
        TURN("turn"),
        ELBOW("elbow");

        private final String name;

        TubeShape(String name) {
            this.name = name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }

}
