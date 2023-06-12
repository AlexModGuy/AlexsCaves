package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.CopperValveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class CopperValveBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty TURNED = BooleanProperty.create("turned");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE_UP = buildShape(
            Block.box(7, 0, 7, 9, 11, 9),
            Block.box(1, 9, 1, 15, 11, 15)
    );

    private static final VoxelShape SHAPE_DOWN = buildShape(
            Block.box(7, 4, 7, 9, 16, 9),
            Block.box(1, 5, 1, 15, 7, 15)
    );

    private static final VoxelShape SHAPE_NORTH = buildShape(
            Block.box(7, 7, 4, 9, 9, 16),
            Block.box(1, 1, 5, 15, 15, 7)
    );

    private static final VoxelShape SHAPE_SOUTH = buildShape(
            Block.box(7, 7, 0, 9, 9, 11),
            Block.box(1, 1, 9, 15, 15, 11)
    );

    private static final VoxelShape SHAPE_EAST = buildShape(
            Block.box(0, 7, 7, 11, 9, 9),
            Block.box(9, 1, 1, 11, 15, 15)
    );

    private static final VoxelShape SHAPE_WEST = buildShape(
            Block.box(4, 7, 7, 16, 9, 9),
            Block.box(5, 1, 1, 7, 15, 15)
    );


    protected CopperValveBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3F, 12.0F).sound(SoundType.COPPER));
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(TURNED, Boolean.valueOf(false)));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, direction);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(FACING, TURNED, WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(TURNED, Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos()))).setValue(WATERLOGGED, Boolean.valueOf(context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER));
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState blockState, boolean b) {
        if (!b && !state.is(blockState.getBlock())) {
            level.updateNeighborsAt(blockPos, state.getBlock());
            level.updateNeighborsAt(blockPos.relative(state.getValue(CopperValveBlock.FACING).getOpposite()), state.getBlock());
            super.onRemove(state, level, blockPos, blockState, b);
        }
    }


    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case DOWN:
                return SHAPE_DOWN;
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
            default:
                return SHAPE_UP;
        }
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, ACBlockEntityRegistry.COPPER_VALVE.get(), CopperValveBlockEntity::tick);
    }


    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof CopperValveBlockEntity copperValve && !player.isShiftKeyDown()) {
            if(state.getValue(TURNED)){
                copperValve.moveDown(false);
            }else{
                copperValve.moveDown(!copperValve.isMovingDown());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CopperValveBlockEntity(pos, state);
    }

    private static VoxelShape buildShape(VoxelShape... from){
        return Stream.of(from).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    public int getSignal(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return state.getValue(TURNED) ? 15 : 0;
    }

    public int getDirectSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
        return state.getValue(TURNED) && state.getValue(FACING) == direction ? 15 : 0;
    }

    public boolean isSignalSource(BlockState p_51114_) {
        return true;
    }

}
