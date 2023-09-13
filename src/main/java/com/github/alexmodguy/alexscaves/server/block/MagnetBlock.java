package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.MagnetBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MagnetBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final boolean azure;

    private static final VoxelShape SHAPE_UP = ACMath.buildShape(
            Block.box(0, 6, 5, 6, 16, 11),
            Block.box(0, 0, 5, 16, 6, 11),
            Block.box(10, 6, 5, 16, 16, 11)
    );

    private static final VoxelShape SHAPE_DOWN = ACMath.buildShape(
            Block.box(0, 0, 5, 6, 10, 11),
            Block.box(0, 10, 5, 16, 16, 11),
            Block.box(10, 0, 5, 16, 10, 11)
    );

    private static final VoxelShape SHAPE_NORTH = ACMath.buildShape(
            Block.box(0, 5, 0, 6, 11, 10),
            Block.box(0, 5, 10, 16, 11, 16),
            Block.box(10, 5, 0, 16, 11, 10)
    );

    private static final VoxelShape SHAPE_SOUTH = ACMath.buildShape(
            Block.box(10, 5, 6, 16, 11, 16),
            Block.box(0, 5, 0, 16, 11, 6),
            Block.box(0, 5, 6, 6, 11, 16)
    );

    private static final VoxelShape SHAPE_EAST = ACMath.buildShape(
            Block.box(6, 5, 0, 16, 11, 6),
            Block.box(0, 5, 0, 6, 11, 16),
            Block.box(6, 5, 10, 16, 11, 16)
    );

    private static final VoxelShape SHAPE_WEST = ACMath.buildShape(
            Block.box(0, 5, 10, 10, 11, 16),
            Block.box(10, 5, 0, 16, 11, 16),
            Block.box(0, 5, 0, 10, 11, 6)
    );

    protected MagnetBlock(boolean azure) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(4F, 12.0F).sound(ACSoundTypes.NEODYMIUM).noOcclusion().dynamicShape().lightLevel((i) -> 3).emissiveRendering((state, level, pos) -> true));
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(POWERED, Boolean.valueOf(false)));
        this.azure = azure;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(FACING, POWERED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(POWERED, Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
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
        return RenderShape.MODEL;
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, ACBlockEntityRegistry.MAGNET.get(), MagnetBlockEntity::tick);
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isClientSide) {
            this.updateState(state, worldIn, pos, blockIn);
        }
    }

    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        if (!worldIn.isClientSide) {
            this.updateState(state, worldIn, pos, state.getBlock());
        }
    }

    public void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
        boolean flag = state.getValue(POWERED);
        boolean flag1 = worldIn.hasNeighborSignal(pos);

        if (flag1 != flag) {
            worldIn.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(flag1)), 3);
            worldIn.updateNeighborsAt(pos.below(), this);
        }
    }


    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        if (worldIn.getBlockEntity(pos) instanceof MagnetBlockEntity magnet && !player.isShiftKeyDown()) {
            if (magnet.canAddRange() && magnet.isExtenderItem(heldItem)) {
                magnet.increaseRange(1);
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                player.swing(handIn);
                return InteractionResult.SUCCESS;
            } else if (magnet.canRemoveRange() && magnet.isRetracterItem(heldItem)) {
                magnet.increaseRange(-1);
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                player.swing(handIn);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof MagnetBlockEntity magnetBlockEntity && newState.getBlock() != state.getBlock()) {
            magnetBlockEntity.dropIngots(this.azure);
            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MagnetBlockEntity(pos, state);
    }

}
