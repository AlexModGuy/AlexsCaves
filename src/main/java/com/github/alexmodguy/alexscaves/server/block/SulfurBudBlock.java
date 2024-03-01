package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SulfurBudBlock extends Block implements SimpleWaterloggedBlock {
    public static final IntegerProperty LIQUID_LOGGED = IntegerProperty.create("liquid_logged", 0, 2);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private final Map<Direction, VoxelShape> shapeMap;

    public SulfurBudBlock(int pixWidth, int pixHeight) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(1F, 2.0F).sound(ACSoundTypes.SULFUR).randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(LIQUID_LOGGED, 0).setValue(FACING, Direction.UP));
        shapeMap = buildShapeMap(pixWidth, pixHeight);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, direction);
    }

    public void randomTick(BlockState currentState, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(3) == 0 && !currentState.is(ACBlockRegistry.SULFUR_CLUSTER.get())) {
            BlockPos acidAbove = blockPos.above();
            while (level.getBlockState(acidAbove).isAir() && acidAbove.getY() < level.getMaxBuildHeight()) {
                acidAbove = acidAbove.above();
            }
            BlockState acidState = level.getBlockState(acidAbove);
            Block block = null;
            if (acidState.is(ACBlockRegistry.ACIDIC_RADROCK.get()) || currentState.getValue(LIQUID_LOGGED) == 2) {
                if (currentState.is(ACBlockRegistry.SULFUR_BUD_SMALL.get())) {
                    block = ACBlockRegistry.SULFUR_BUD_MEDIUM.get();
                } else if (currentState.is(ACBlockRegistry.SULFUR_BUD_MEDIUM.get())) {
                    block = ACBlockRegistry.SULFUR_BUD_LARGE.get();
                } else if (currentState.is(ACBlockRegistry.SULFUR_BUD_LARGE.get())) {
                    block = ACBlockRegistry.SULFUR_CLUSTER.get();
                }
            }
            if (block != null) {
                BlockState blockstate1 = block.defaultBlockState().setValue(FACING, currentState.getValue(FACING)).setValue(LIQUID_LOGGED, currentState.getValue(LIQUID_LOGGED));
                level.setBlockAndUpdate(blockPos, blockstate1);
            }
        }
    }

    public static Map<Direction, VoxelShape> buildShapeMap(int pixWidth, int pixHeight) {
        Map<Direction, VoxelShape> map = new HashMap<>();
        map.put(Direction.UP, Block.box(8 - pixWidth / 2, 0.0D, 8 - pixWidth / 2, 8 + pixWidth / 2, pixHeight, 8 + pixWidth / 2));
        map.put(Direction.DOWN, Block.box(8 - pixWidth / 2, 16 - pixHeight, 8 - pixWidth / 2, 8 + pixWidth / 2, 16, 8 + pixWidth / 2));
        map.put(Direction.NORTH, Block.box(8 - pixWidth / 2, 8 - pixWidth / 2, 0, 8 + pixWidth / 2, 8 + pixWidth / 2, pixHeight));
        map.put(Direction.SOUTH, Block.box(8 - pixWidth / 2, 8 - pixWidth / 2, 16 - pixHeight, 8 + pixWidth / 2, 8 + pixWidth / 2, 16));
        map.put(Direction.EAST, Block.box(0, 8 - pixWidth / 2, 8 - pixWidth / 2, pixHeight, 8 + pixWidth / 2, 8 + pixWidth / 2));
        map.put(Direction.WEST, Block.box(16 - pixHeight, 8 - pixWidth / 2, 8 - pixWidth / 2, 16, 8 + pixWidth / 2, 8 + pixWidth / 2));
        return map;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        if (liquidType == 1) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        } else if (liquidType == 2) {
            levelAccessor.scheduleTick(blockPos, ACFluidRegistry.ACID_FLUID_SOURCE.get(), ACFluidRegistry.ACID_FLUID_SOURCE.get().getTickDelay(levelAccessor));
        }
        if (!levelAccessor.isClientSide()) {
            levelAccessor.scheduleTick(blockPos, this, 1);
        }

        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(levelAccessor, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeMap.get(state.getValue(FACING));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return this.defaultBlockState().setValue(LIQUID_LOGGED, getLiquidType(levelaccessor.getFluidState(blockpos))).setValue(FACING, context.getClickedFace());
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
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


    public FluidState getFluidState(BlockState state) {
        int liquidType = state.getValue(LIQUID_LOGGED);
        return liquidType == 1 ? Fluids.WATER.getSource(false) : liquidType == 2 ? ACFluidRegistry.ACID_FLUID_SOURCE.get().getSource(false) : super.getFluidState(state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(LIQUID_LOGGED, FACING);
    }

    public static int getLiquidType(FluidState fluidState) {
        if (fluidState.getType() == Fluids.WATER) {
            return 1;
        } else if (fluidState.getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get() && fluidState.isSource()) {
            return 2;
        }
        return 0;
    }

    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {

    }
}
