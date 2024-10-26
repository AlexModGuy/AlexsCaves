package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;

public class GummyRingBlock extends DirectionalBlock implements BucketPickup, LiquidBlockContainer {
    public static final IntegerProperty LIQUID_LOGGED = IntegerProperty.create("liquid_logged", 0, 2);
    public static final BooleanProperty FLOATING = BooleanProperty.create("floating");
    private static final VoxelShape SHAPE_UP = Shapes.join(
            box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            box(5.0D, 0.0D, 5.0D, 11.0D, 8.0D, 11.0D), BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_UP_FLOATING = Shapes.join(
            box(0.0D, -4.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            box(5.0D, -4.0D, 5.0D, 11.0D, 4.0D, 11.0D), BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_DOWN = Shapes.join(
            box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            box(5.0D, 8.0D, 5.0D, 11.0D, 8.0D, 11.0D), BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_NORTH = Shapes.join(
            box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            box(5.0D, 5.0D, 8.0D, 11.0D, 11.0D, 16.0D), BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_SOUTH = Shapes.join(
            box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D),
            box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 8.0D), BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_EAST = Shapes.join(
            box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D),
            box(0.0D, 5.0D, 5.0D, 8.0D, 11.0D, 11.0D), BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_WEST = Shapes.join(
            box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            box(8.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D), BooleanOp.ONLY_FIRST);

    public GummyRingBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_RED).strength(2.0F).sound(ACSoundTypes.SQUISHY_CANDY).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(LIQUID_LOGGED, 0).setValue(FLOATING, Boolean.valueOf(false)).setValue(FACING, Direction.UP));
    }


    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState blockState, boolean forced) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        if (liquidType == 1) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        } else if (liquidType == 2) {
            levelAccessor.scheduleTick(blockPos, ACFluidRegistry.ACID_FLUID_SOURCE.get(), ACFluidRegistry.ACID_FLUID_SOURCE.get().getTickDelay(levelAccessor));
        }
        boolean flag = liquidType == 0 && state.getValue(FACING) == Direction.UP && !levelAccessor.getFluidState(blockPos.below()).isEmpty();
        return state.setValue(FLOATING, flag);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)){
            case DOWN:
                return SHAPE_DOWN;
            case NORTH:
                return SHAPE_NORTH;
            case EAST:
                return SHAPE_EAST;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            default:
                return state.getValue(FLOATING) ? SHAPE_UP_FLOATING : SHAPE_UP;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        int fluid = getLiquidType(context.getLevel().getFluidState(context.getClickedPos()));
        Direction facing = context.getClickedFace();
        boolean flag = false;
        if(facing == Direction.UP){
            flag = !levelaccessor.getFluidState(blockpos.below()).isEmpty() && fluid == 0;
        }
        return this.defaultBlockState().setValue(LIQUID_LOGGED, fluid).setValue(FACING, facing).setValue(FLOATING, flag);
    }

    private int getLiquidType(FluidState fluidState) {
        if (fluidState.getType() == Fluids.WATER) {
            return 1;
        } else if (fluidState.getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get() && fluidState.isSource()) {
            return 2;
        }
        return 0;
    }


    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public FluidState getFluidState(BlockState state) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        return liquidType == 1 ? Fluids.WATER.getSource(false) : liquidType == 2 ? ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE.get().getSource(false) : super.getFluidState(state);
    }

    public boolean canPlaceLiquid(BlockGetter getter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return fluid == Fluids.WATER || fluid.getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get();
    }

    public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos pos, BlockState blockState, FluidState fluidState) {
        int liquidType = blockState.getValue(LIQUID_LOGGED);
        if (liquidType == 0) {
            if (!levelAccessor.isClientSide()) {
                if (fluidState.getType() == Fluids.WATER) {
                    levelAccessor.setBlock(pos, blockState.setValue(LIQUID_LOGGED, 1), 3);
                } else if (fluidState.getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get()) {
                    levelAccessor.setBlock(pos, blockState.setValue(LIQUID_LOGGED, 2), 3);
                }
                levelAccessor.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(levelAccessor));
            }
            return true;
        } else {
            return false;
        }
    }

    public ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState state) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        levelAccessor.setBlock(blockPos, state.setValue(LIQUID_LOGGED, 0), 3);
        if (liquidType > 0) {
            return new ItemStack(liquidType == 1 ? Items.WATER_BUCKET : ACItemRegistry.PURPLE_SODA_BUCKET.get());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(LIQUID_LOGGED, FLOATING, FACING);
    }

    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }
}
