package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MusselBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty MUSSELS = IntegerProperty.create("mussels", 1, 5);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE_UP = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);
    private static final VoxelShape SHAPE_DOWN = Block.box(1.0D, 12.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    private static final VoxelShape SHAPE_WEST = Block.box(12.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    private static final VoxelShape SHAPE_EAST = Block.box(0.0D, 1.0D, 1.0D, 4.0D, 15.0D, 15.0D);
    private static final VoxelShape SHAPE_NORTH = Block.box(1.0D, 1.0D, 12.0D, 15.0D, 15.0D, 16.0D);
    private static final VoxelShape SHAPE_SOUTH = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 4.0D);


    public MusselBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(1F, 1.0F).sound(SoundType.BASALT).noOcclusion().noCollission().dynamicShape().randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(MUSSELS, Integer.valueOf(1)).setValue(WATERLOGGED, Boolean.valueOf(true)));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, direction);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(levelAccessor, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case UP:
                return SHAPE_UP;
            case DOWN:
                return SHAPE_DOWN;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
        }
        return SHAPE_UP;

    }

    public void randomTick(BlockState state, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        Direction direction = state.getValue(FACING);
        BlockPos connectedToPos = blockPos.relative(direction.getOpposite());
        BlockState connectedState = serverLevel.getBlockState(connectedToPos);
        int mussels = state.getValue(MUSSELS);
        if (randomSource.nextInt(20) == 0 && connectedState.is(ACTagRegistry.GROWS_MUSSELS)) {
            if (mussels >= 5) {
                BlockPos randomOffsetPos = connectedToPos.offset(randomSource.nextInt(6) - 3, randomSource.nextInt(6) - 3, randomSource.nextInt(6) - 3);
                BlockState randomOffsetState = serverLevel.getBlockState(randomOffsetPos);
                if (randomOffsetState.is(Blocks.WATER) || randomOffsetState.is(this) && randomOffsetState.getValue(WATERLOGGED)) {
                    List<Direction> possiblities = new ArrayList<>();
                    for (Direction possible : Direction.values()) {
                        BlockPos check = randomOffsetPos.relative(possible);
                        if (serverLevel.getBlockState(check).isFaceSturdy(serverLevel, check, possible.getOpposite())) {
                            possiblities.add(possible.getOpposite());
                        }
                    }
                    Direction chosen = null;
                    if (!possiblities.isEmpty()) {
                        if (possiblities.size() <= 1) {
                            chosen = possiblities.get(0);
                        } else {
                            chosen = possiblities.get(randomSource.nextInt(possiblities.size() - 1));
                        }
                    }
                    if (chosen != null) {
                        int taxicab = Mth.clamp(6 - (int) (Math.ceil(randomOffsetPos.distToLowCornerSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()))), 1, 5);
                        int currentMussels = randomOffsetState.is(this) ? randomOffsetState.getValue(MUSSELS) : 0;
                        int setMussels = Math.max(currentMussels, taxicab);
                        int musselCountOf = randomOffsetState.is(this) ? Math.min(randomOffsetState.getValue(MUSSELS) + 1, setMussels) : 1;
                        Direction musselDirectionOf = randomOffsetState.is(this) ? randomOffsetState.getValue(FACING) : chosen;
                        serverLevel.setBlockAndUpdate(randomOffsetPos, ACBlockRegistry.MUSSEL.get().defaultBlockState().setValue(MusselBlock.FACING, musselDirectionOf).setValue(MusselBlock.WATERLOGGED, true).setValue(MusselBlock.MUSSELS, musselCountOf));
                    }
                }
            } else {
                serverLevel.setBlockAndUpdate(blockPos, state.setValue(MUSSELS, mussels + 1));
            }
        }
    }

    protected void removeOneMussel(Level worldIn, BlockPos pos, BlockState state) {
        int i = state.getValue(MUSSELS);
        if (i <= 1) {
            worldIn.destroyBlock(pos, false);
        } else {
            worldIn.setBlock(pos, state.setValue(MUSSELS, Integer.valueOf(i - 1)), 2);
            worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
            worldIn.levelEvent(2001, pos, Block.getId(state));
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.is(this)) {
            return blockstate.setValue(MUSSELS, Integer.valueOf(Math.min(5, blockstate.getValue(MUSSELS) + 1)));
        } else {
            LevelAccessor levelaccessor = context.getLevel();
            BlockPos blockpos = context.getClickedPos();
            return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER)).setValue(FACING, context.getClickedFace());
        }
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && state.getValue(MUSSELS) < 5 ? true : super.canBeReplaced(state, context);
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
        blockStateBuilder.add(WATERLOGGED, FACING, MUSSELS);
    }

    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }


    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        this.removeOneMussel(worldIn, pos, state);
    }
}
