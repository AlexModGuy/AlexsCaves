package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.TeslaBulbBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TeslaBulbBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final VoxelShape SHAPE_DOWN = Block.box(3, 1, 3, 11, 15, 11);
    public static final VoxelShape SHAPE_UP = Block.box(3, 1, 3, 11, 15, 11);

    public TeslaBulbBlock() {
        super(Properties.of().mapColor(DyeColor.WHITE).strength(3.0F, 10.0F).sound(SoundType.GLASS).lightLevel((i) -> 15).emissiveRendering((state, level, pos) -> true));
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(DOWN, Boolean.valueOf(false)));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, ACBlockEntityRegistry.TESLA_BULB.get(), TeslaBulbBlockEntity::tick);
    }

    @javax.annotation.Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean down = context.getClickedFace() == Direction.DOWN;
        return this.defaultBlockState().setValue(DOWN, down).setValue(WATERLOGGED, Boolean.valueOf(context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER));
    }

    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult hitResult, Projectile projectile) {
        BlockEntity blockEntity = level.getBlockEntity(hitResult.getBlockPos());
        if (blockEntity instanceof TeslaBulbBlockEntity teslaBulb) {
            teslaBulb.explode();
        }
    }

    public void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof TeslaBulbBlockEntity teslaBulb) {
            teslaBulb.explode();
        }
    }

    @Deprecated
    public boolean canSurvive(BlockState state, LevelReader levelAccessor, BlockPos blockPos) {
        BlockState above = levelAccessor.getBlockState(blockPos.above());
        BlockState below = levelAccessor.getBlockState(blockPos.below());
        if (state.getValue(DOWN)) {
            return above.isFaceSturdy(levelAccessor, blockPos.above(), Direction.UP) || GalenaSpireBlock.isGalenaSpireConnectable(above, true);
        } else {
            return below.isFaceSturdy(levelAccessor, blockPos.below(), Direction.DOWN) || GalenaSpireBlock.isGalenaSpireConnectable(below, false);
        }
    }


    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return state.canSurvive(levelAccessor, blockPos) ? super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1) : Blocks.AIR.defaultBlockState();
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return state.getValue(DOWN) ? SHAPE_DOWN : SHAPE_UP;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeslaBulbBlockEntity(pos, state);
    }


    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(DOWN, WATERLOGGED);
    }
}