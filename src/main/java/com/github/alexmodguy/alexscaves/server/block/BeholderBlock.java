package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.BeholderBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BeholderBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {


    private static final VoxelShape SHAPE = ACMath.buildShape(Block.box(6, 0, 6, 10, 4, 10), Block.box(3, 2, 3, 13, 16, 13));

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected BeholderBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_RED).noCollission().strength(1F, 12.0F).sound(ACSoundTypes.BEHOLDER));
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, Direction.UP, SupportType.CENTER);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER));
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

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, ACBlockEntityRegistry.BEHOLDER.get(), BeholderBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BeholderBlockEntity(pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(WATERLOGGED);
    }
}