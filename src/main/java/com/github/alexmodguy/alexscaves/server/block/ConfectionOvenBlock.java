package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ConfectionOvenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.blockentity.SirenLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ConfectionOvenBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final IntegerProperty COOKSTATE = IntegerProperty.create("cookstate", 0, 2);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public ConfectionOvenBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(1.5F).lightLevel(blockState -> blockState.getValue(COOKSTATE) == 1 ? 10 : 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(COOKSTATE, 0));
    }

    public static Vec3 getDispensePosition(BlockPos coords, Direction dir) {
        double d0 = coords.getX() + 0.5D + 0.7D * (double) dir.getStepX();
        double d1 = coords.getY() + 0.15D + 0.7D * (double) dir.getStepY();
        double d2 = coords.getZ() + 0.5D + 0.7D * (double) dir.getStepZ();
        return new Vec3(d0, d1, d2);
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

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(POWERED, Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COOKSTATE, POWERED);
    }

    public void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
        boolean flag = state.getValue(POWERED);
        boolean flag1 = worldIn.hasNeighborSignal(pos);
        if (flag1 != flag) {
            worldIn.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(flag1)), 3);
            worldIn.updateNeighborsAt(pos.below(), this);
        }
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, ACBlockEntityRegistry.CONFECTION_OVEN.get(), ConfectionOvenBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConfectionOvenBlockEntity(pos, state);
    }

}
