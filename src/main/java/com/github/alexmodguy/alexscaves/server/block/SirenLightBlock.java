package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.SirenLightBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

import java.util.function.ToIntFunction;

public class SirenLightBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final VoxelShape SHAPE_UP = ACMath.buildShape(
            Block.box(4, 2, 4, 12, 10, 12),
            Block.box(3, 0, 3, 13, 2, 13),
            Block.box(6, 2, 6, 10, 8, 10)
    );

    private static final VoxelShape SHAPE_DOWN = ACMath.buildShape(
            Block.box(4, 6, 4, 12, 14, 12),
            Block.box(3, 14, 3, 13, 16, 13)
    );

    private static final VoxelShape SHAPE_NORTH = ACMath.buildShape(
            Block.box(4, 4, 6, 12, 12, 14),
            Block.box(3, 3, 14, 13, 13, 16)
    );

    private static final VoxelShape SHAPE_SOUTH = ACMath.buildShape(
            Block.box(4, 4, 2, 12, 12, 10),
            Block.box(3, 3, 0, 13, 13, 2)
    );

    private static final VoxelShape SHAPE_EAST = ACMath.buildShape(
            Block.box(2, 4, 4, 10, 12, 12),
            Block.box(0, 3, 3, 2, 13, 13)
    );

    private static final VoxelShape SHAPE_WEST = ACMath.buildShape(
            Block.box(6, 4, 4, 14, 12, 12),
            Block.box(14, 3, 3, 16, 13, 13)
    );


    protected SirenLightBlock() {
        super(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(1F, 12.0F).sound(SoundType.GLASS).noOcclusion().dynamicShape().lightLevel(poweredBlockEmission(10)));
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(POWERED, Boolean.valueOf(false)));
    }

    private static ToIntFunction<BlockState> poweredBlockEmission(int light) {
        return (state) -> {
            return state.getValue(BlockStateProperties.POWERED) ? light : 0;
        };
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
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, ACBlockEntityRegistry.SIREN_LIGHT.get(), SirenLightBlockEntity::tick);
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
        if (worldIn.getBlockEntity(pos) instanceof SirenLightBlockEntity sirenLightBlock && !player.isShiftKeyDown() && heldItem.getItem() instanceof DyeItem dyeItem) {
            DyeColor dyeColor = dyeItem.getDyeColor();
            player.playSound(SoundEvents.DYE_USE);
            sirenLightBlock.setColor(dyeColor.getTextColor());
            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SirenLightBlockEntity(pos, state);
    }

}
