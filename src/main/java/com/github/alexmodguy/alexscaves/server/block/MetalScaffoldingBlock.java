package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class MetalScaffoldingBlock extends Block implements BucketPickup, LiquidBlockContainer {
    private static final VoxelShape STABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape BELOW_BLOCK = Shapes.block().move(0.0D, -1.0D, 0.0D);
    public static final int STABILITY_MAX_DISTANCE = 12;
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, STABILITY_MAX_DISTANCE);
    public static final IntegerProperty LIQUID_LOGGED = IntegerProperty.create("liquid_logged", 0, 2);
    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

    public MetalScaffoldingBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().noOcclusion().strength(5F, 15.0F).sound(ACSoundTypes.METAL_SCAFFOLDING));
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, Integer.valueOf(STABILITY_MAX_DISTANCE)).setValue(LIQUID_LOGGED, 0).setValue(BOTTOM, Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, LIQUID_LOGGED, BOTTOM);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return Shapes.block();
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return isScaffoldingItem(context.getItemInHand());
    }

    public boolean isScaffoldingItem(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock().defaultBlockState().is(ACTagRegistry.SCAFFOLDING);
        }
        return false;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        int i = getDistance(level, blockpos);
        int fluid = getLiquidType(level.getFluidState(blockpos));
        BlockState defaultState = fluid == 2 ? ACBlockRegistry.RUSTY_SCAFFOLDING.get().defaultBlockState() : defaultBlockState();
        return defaultState.setValue(LIQUID_LOGGED, fluid).setValue(DISTANCE, Integer.valueOf(i)).setValue(BOTTOM, Boolean.valueOf(this.isBottom(level, blockpos, i)));
    }

    private int getLiquidType(FluidState fluidState) {
        if (fluidState.getType() == Fluids.WATER) {
            return 1;
        } else if (fluidState.getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get() && fluidState.isSource()) {
            return 2;
        }
        return 0;
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

    public void tick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        int i = getDistance(level, pos);
        BlockState blockstate = blockState.setValue(DISTANCE, Integer.valueOf(i)).setValue(BOTTOM, Boolean.valueOf(this.isBottom(level, pos, i)));
        if (blockstate.getValue(DISTANCE) == STABILITY_MAX_DISTANCE) {
            if (blockState.getValue(DISTANCE) == STABILITY_MAX_DISTANCE) {
                FallingBlockEntity.fall(level, pos, blockstate);
            } else {
                level.destroyBlock(pos, true);
            }
        } else if (blockState != blockstate) {
            level.setBlock(pos, blockstate, 3);
        }

    }

    public boolean canSurvive(BlockState p_56040_, LevelReader p_56041_, BlockPos p_56042_) {
        return getDistance(p_56041_, p_56042_) < STABILITY_MAX_DISTANCE;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (context.isAbove(Shapes.block(), pos, true) && !context.isDescending()) {
            return STABLE_SHAPE;
        } else {
            return state.getValue(DISTANCE) != 0 && state.getValue(BOTTOM) && context.isAbove(BELOW_BLOCK, pos, true) ? UNSTABLE_SHAPE_BOTTOM : Shapes.empty();
        }
    }

    public FluidState getFluidState(BlockState state) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        return liquidType == 1 ? Fluids.WATER.getSource(false) : liquidType == 2 ? ACFluidRegistry.ACID_FLUID_SOURCE.get().getSource(false) : super.getFluidState(state);
    }

    private boolean isBottom(BlockGetter getter, BlockPos pos, int dist) {
        return dist > 0 && !getter.getBlockState(pos.below()).is(this);
    }

    public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    public static int getDistance(BlockGetter getter, BlockPos pos) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable().move(Direction.DOWN);
        BlockState blockstate = getter.getBlockState(blockpos$mutableblockpos);
        int i = STABILITY_MAX_DISTANCE;
        if (blockstate.is(Blocks.SCAFFOLDING)) {
            i = blockstate.getValue(ScaffoldingBlock.DISTANCE);
        } else if (blockstate.getBlock() instanceof MetalScaffoldingBlock) {
            i = blockstate.getValue(MetalScaffoldingBlock.DISTANCE);
        } else if (blockstate.is(ACTagRegistry.SCAFFOLDING) || blockstate.isFaceSturdy(getter, blockpos$mutableblockpos, Direction.UP)) {
            return 0;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockstate1 = getter.getBlockState(blockpos$mutableblockpos.setWithOffset(pos, direction));
            if (blockstate1.is(ACTagRegistry.SCAFFOLDING)) {
                if(blockstate1.getBlock() instanceof MetalScaffoldingBlock){
                    i = Math.min(i, blockstate1.getValue(DISTANCE) + 1);
                }else if(blockstate1.getBlock() instanceof ScaffoldingBlock){
                    i = Math.min(i, blockstate1.getValue(ScaffoldingBlock.DISTANCE) + 1);
                }
                if (i == 1) {
                    break;
                }
            }
        }

        return i;
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
                    if (blockState.getBlock() == ACBlockRegistry.METAL_SCAFFOLDING.get()) {
                        levelAccessor.levelEvent(1501, pos, 0);
                        state = ACBlockRegistry.RUSTY_SCAFFOLDING.get().defaultBlockState().setValue(DISTANCE, blockState.getValue(DISTANCE));
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
        if (liquidType > 0) {
            levelAccessor.setBlock(blockPos, state.setValue(LIQUID_LOGGED, 0), 3);
            if (!state.canSurvive(levelAccessor, blockPos)) {
                levelAccessor.destroyBlock(blockPos, true);
            }
            return new ItemStack(liquidType == 1 ? Items.WATER_BUCKET : ACItemRegistry.ACID_BUCKET.get());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }


    static {
        VoxelShape voxelshape = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape voxelshape1 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
        VoxelShape voxelshape2 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
        VoxelShape voxelshape3 = Block.box(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
        VoxelShape voxelshape4 = Block.box(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
        STABLE_SHAPE = Shapes.or(voxelshape, voxelshape1, voxelshape2, voxelshape3, voxelshape4);
        VoxelShape voxelshape5 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 16.0D);
        VoxelShape voxelshape6 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape7 = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape8 = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D);
        UNSTABLE_SHAPE = Shapes.or(UNSTABLE_SHAPE_BOTTOM, STABLE_SHAPE, voxelshape6, voxelshape5, voxelshape8, voxelshape7);
    }
}