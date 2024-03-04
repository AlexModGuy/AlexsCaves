package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RebarBlock extends Block implements BucketPickup, LiquidBlockContainer {

    public static final BooleanProperty CONNECT_X = BooleanProperty.create("connect_x");
    public static final BooleanProperty CONNECT_Y = BooleanProperty.create("connect_y");
    public static final BooleanProperty CONNECT_Z = BooleanProperty.create("connect_z");

    public final Map<BlockState, VoxelShape> shapeMap = new HashMap<>();

    public static final VoxelShape CENTER_SHAPE = Block.box(7, 7, 7, 9, 9, 9);
    public static final VoxelShape X_SHAPE = Block.box(0, 7, 7, 16, 9, 9);
    public static final VoxelShape Y_SHAPE = Block.box(7, 0, 7, 9, 16, 9);
    public static final VoxelShape Z_SHAPE = Block.box(7, 7, 0, 9, 9, 16);
    public static final IntegerProperty LIQUID_LOGGED = IntegerProperty.create("liquid_logged", 0, 2);

    public RebarBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).sound(ACSoundTypes.SCRAP_METAL));
        this.registerDefaultState(this.stateDefinition.any().setValue(CONNECT_X, false).setValue(CONNECT_Y, true).setValue(CONNECT_Z, false).setValue(LIQUID_LOGGED, 0));
    }

    protected VoxelShape getRebarShape(BlockState state) {
        if (shapeMap.containsKey(state)) {
            return shapeMap.get(state);
        } else {
            VoxelShape merge = CENTER_SHAPE;
            if (state.getValue(CONNECT_X)) {
                merge = Shapes.join(merge, X_SHAPE, BooleanOp.OR);
            }
            if (state.getValue(CONNECT_Y)) {
                merge = Shapes.join(merge, Y_SHAPE, BooleanOp.OR);
            }
            if (state.getValue(CONNECT_Z)) {
                merge = Shapes.join(merge, Z_SHAPE, BooleanOp.OR);
            }
            shapeMap.put(state, merge);
            return merge;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return getRebarShape(state);
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        BlockState desired = getDesiredRebarState(state, context.getClickedFace().getAxis());
        return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && !(state.getValue(CONNECT_X) == desired.getValue(CONNECT_X) && state.getValue(CONNECT_Y) == desired.getValue(CONNECT_Y) && state.getValue(CONNECT_Z) == desired.getValue(CONNECT_Z)) || super.canBeReplaced(state, context);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        int fluid = getLiquidType(context.getLevel().getFluidState(context.getClickedPos()));
        return getDesiredRebarState(blockstate, context.getClickedFace().getAxis()).setValue(LIQUID_LOGGED, fluid);
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState blockState, boolean forced) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }

    }

    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos1) {
        int liquidType = blockState.getValue(LIQUID_LOGGED);
        if (liquidType == 1) {
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        } else if (liquidType == 2) {
            levelAccessor.scheduleTick(pos, ACFluidRegistry.ACID_FLUID_SOURCE.get(), ACFluidRegistry.ACID_FLUID_SOURCE.get().getTickDelay(levelAccessor));
        }
        if (!levelAccessor.isClientSide()) {
            levelAccessor.scheduleTick(pos, this, 1);
        }
        return blockState;
    }

    public FluidState getFluidState(BlockState state) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        return liquidType == 1 ? Fluids.WATER.getSource(false) : liquidType == 2 ? ACFluidRegistry.ACID_FLUID_SOURCE.get().getSource(false) : super.getFluidState(state);
    }

    public boolean canPlaceLiquid(BlockGetter getter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return fluid == Fluids.WATER || fluid.getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get();
    }

    public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos pos, BlockState blockState, FluidState fluidState) {
        int liquidType = blockState.getValue(LIQUID_LOGGED);
        if (liquidType == 0) {
            if (!levelAccessor.isClientSide()) {
                if (fluidState.getType() == Fluids.WATER) {
                    levelAccessor.setBlock(pos, blockState.setValue(LIQUID_LOGGED, 1), 3);
                } else if (fluidState.getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get()) {
                    BlockState state = blockState;
                    if (blockState.getBlock() == ACBlockRegistry.METAL_REBAR.get()) {
                        levelAccessor.levelEvent(1501, pos, 0);
                        state = ACBlockRegistry.RUSTY_REBAR.get().defaultBlockState().setValue(CONNECT_X, blockState.getValue(CONNECT_X)).setValue(CONNECT_Y, blockState.getValue(CONNECT_Y)).setValue(CONNECT_Z, blockState.getValue(CONNECT_Z));
                    }
                    levelAccessor.setBlock(pos, state.setValue(LIQUID_LOGGED, 2), 3);
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
            return new ItemStack(liquidType == 1 ? Items.WATER_BUCKET : ACItemRegistry.ACID_BUCKET.get());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    private int getLiquidType(FluidState fluidState) {
        if (fluidState.getType() == Fluids.WATER) {
            return 1;
        } else if (fluidState.getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get() && fluidState.isSource()) {
            return 2;
        }
        return 0;
    }

    public BlockState getDesiredRebarState(BlockState blockstate, Direction.Axis clickedAxis) {
        boolean xAxis = false;
        boolean yAxis = false;
        boolean zAxis = false;
        if (blockstate.is(this)) {
            xAxis = blockstate.getValue(CONNECT_X);
            yAxis = blockstate.getValue(CONNECT_Y);
            zAxis = blockstate.getValue(CONNECT_Z);
        }
        if (clickedAxis == Direction.Axis.X) {
            xAxis = true;
        } else if (clickedAxis == Direction.Axis.Y) {
            yAxis = true;
        } else if (clickedAxis == Direction.Axis.Z) {
            zAxis = true;
        }
        return this.defaultBlockState().setValue(CONNECT_X, xAxis).setValue(CONNECT_Y, yAxis).setValue(CONNECT_Z, zAxis);
    }

    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.playerDestroy(level, player, blockPos, blockState, blockEntity, stack);
        Vec3 findDirOf = player.getEyePosition().subtract(Vec3.atCenterOf(blockPos));
        BlockState set = blockState;
        if (blockState.getValue(CONNECT_X)) {
            set = set.setValue(CONNECT_X, false);
        } else if (blockState.getValue(CONNECT_Y)) {
            set = set.setValue(CONNECT_Y, false);
        } else if (blockState.getValue(CONNECT_Z)) {
            set = set.setValue(CONNECT_Z, false);
        }
        if(!set.getValue(CONNECT_X) && !set.getValue(CONNECT_Y) && !set.getValue(CONNECT_Z)){
            set = Blocks.AIR.defaultBlockState();
        }
        level.setBlock(blockPos, set, 2);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        BlockState def = this.defaultBlockState().setValue(CONNECT_X, false).setValue(CONNECT_Z, false);
        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            if (state.getValue(CONNECT_X)) {
                def = def.setValue(CONNECT_Z, true);
            }
            if (state.getValue(CONNECT_Z)) {
                def = def.setValue(CONNECT_X, true);
            }
        }
        return def;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONNECT_X, CONNECT_Y, CONNECT_Z, LIQUID_LOGGED);
    }

}
