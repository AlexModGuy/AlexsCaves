package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fluids.FluidType;

import java.util.List;
import java.util.Queue;

public class DrainBlock extends AbstractGlassBlock {
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    private static final int MAXIMUM_BLOCKS_DRAINED = 64;
    public static final int MAX_FLUID_SPREAD = 10;
    private static final Direction[] DRAIN_DIRECTIONS = new Direction[]{Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH};
    private static final Direction[] FIND_WATER_DIRECTIONS = new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH};
    private static final Direction[] FILL_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH};
    private static final int DRAIN_TIME = 20;

    public DrainBlock() {
        super(Properties.of().mapColor(MapColor.METAL).pushReaction(PushReaction.IGNORE).noOcclusion().requiresCorrectToolForDrops().strength(5F, 15.0F).sound(SoundType.METAL));
        this.registerDefaultState(this.defaultBlockState().setValue(OPEN, Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(OPEN);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(OPEN, Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isClientSide) {
            this.updateState(state, worldIn, pos, blockIn);
        }
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        if (!worldIn.isClientSide) {
            this.updateState(state, worldIn, pos, state.getBlock());

            if(state.getValue(OPEN)){
                drainLogic(state, worldIn, pos);
            }
        }
    }

    public void drainLogic(BlockState state, Level worldIn, BlockPos pos){
        BlockPos above = pos.above();
        if (!worldIn.getFluidState(above).isEmpty() && (worldIn.isEmptyBlock(pos.below()) || !worldIn.getFluidState(pos.below()).isEmpty())) {
            BlockPos.MutableBlockPos highestWaterMutable = new BlockPos.MutableBlockPos();
            highestWaterMutable.set(pos);
            highestWaterMutable.move(0, 1, 0);
            while (!worldIn.getFluidState(highestWaterMutable).isEmpty() && highestWaterMutable.getY() < worldIn.getMaxBuildHeight()) {
                highestWaterMutable.move(0, 1, 0);
            }
            highestWaterMutable.move(0, -1, 0);
            BlockPos highestWater = findHighestWater(worldIn, highestWaterMutable.immutable());
            FluidState copyState = worldIn.getFluidState(highestWater);
            BlockState fluidBlockCopyState = worldIn.getBlockState(highestWater);
            if (!copyState.isEmpty()) {
                int count = removeWaterBreadthFirstSearch(worldIn, highestWater);
                BlockPos.MutableBlockPos lowestAir = new BlockPos.MutableBlockPos();
                lowestAir.set(pos);
                lowestAir.move(0, -1, 0);
                while ((!worldIn.getFluidState(lowestAir).isEmpty() || worldIn.isEmptyBlock(lowestAir)) && lowestAir.getY() > worldIn.getMinBuildHeight()) {
                    lowestAir.move(0, -1, 0);
                }
                lowestAir.move(0, 1, 0);
                BlockPos lowest = lowestAir.immutable();
                BlockState fullBlock = fluidBlockCopyState.getBlock().defaultBlockState();
                for (int i = 0; i < count; i++) {
                    List<BlockPos> ignoredPoses = Lists.newArrayList();
                    BlockPos setPos = getFirstEmptyNeighborPosition(worldIn, lowest, copyState.getFluidType(), 0, ignoredPoses);
                    if (setPos == null) {
                        lowest = lowest.above();
                        if (lowest.getY() >= pos.getY()) {
                            break;
                        }
                        i--;
                    } else {
                        worldIn.setBlockAndUpdate(setPos, fullBlock);
                    }
                }
            }
            worldIn.scheduleTick(pos, this, DRAIN_TIME);
        }
    }

    public void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
        boolean flag = state.getValue(OPEN);
        boolean flag1 = worldIn.hasNeighborSignal(pos);
        if (flag1 != flag) {
            worldIn.setBlock(pos, state.setValue(OPEN, Boolean.valueOf(flag1)), 3);
            worldIn.updateNeighborsAt(pos.below(), this);
            worldIn.scheduleTick(pos, this, DRAIN_TIME);
        }
    }

    private BlockPos findHighestWater(Level level, BlockPos pos) {
        BlockPos highest = pos;
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(pos, 0));
        int i = 0;
        int maxDist = 5;
        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            if (blockpos.getY() > highest.getY()) {
                highest = blockpos;
            }
            int j = tuple.getB();
            for (Direction direction : FIND_WATER_DIRECTIONS) {
                BlockPos blockpos1 = blockpos.relative(direction);
                BlockState blockstate = level.getBlockState(blockpos1);
                if (!blockstate.getFluidState().isEmpty() && !(blockstate.getBlock() instanceof SimpleWaterloggedBlock)) {
                    ++i;
                    if (j < maxDist) {
                        queue.add(new Tuple<>(blockpos1, j + 1));
                    }
                }
            }
            if (i > 10024) {
                break;
            }
        }
        return highest;
    }

    private BlockPos getFirstEmptyNeighborPosition(Level level, BlockPos pos, FluidType ourType, int tries, List<BlockPos> ignoredPoses) {
        if (tries < 20 && !ignoredPoses.contains(pos)) {
            ignoredPoses.add(pos);
            if (canMergeWith(level, pos)) {
                return pos;
            }
            for (Direction direction : FILL_DIRECTIONS) {
                BlockPos pos1 = pos.relative(direction);
                if (canMergeWith(level, pos1)) {
                    return pos1;
                } else if (level.getFluidState(pos1).getFluidType() == ourType) {
                    BlockPos pos2 = getFirstEmptyNeighborPosition(level, pos1, ourType, tries + 1, ignoredPoses);
                    if (pos2 != null) {
                        return pos2;
                    }
                }
                ignoredPoses.add(pos1);
            }
        }
        return null;
    }

    private boolean canMergeWith(Level level, BlockPos pos) {
        return level.getBlockState(pos).isAir() && level.getFluidState(pos).isEmpty() || !level.getFluidState(pos).isEmpty() && !level.getFluidState(pos).isSource();
    }

    private int removeWaterBreadthFirstSearch(Level level, BlockPos pos) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(pos, 0));
        int i = 0;
        int fullBlocks = 0;
        FluidState lastFluidState = null;
        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            BlockState state = level.getBlockState(blockpos);
            int j = tuple.getB();
            if (!state.getFluidState().isEmpty()) {
                fullBlocks++;
                level.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
            }
            for (Direction direction : DRAIN_DIRECTIONS) {
                BlockPos blockpos1 = blockpos.relative(direction);
                BlockState blockstate = level.getBlockState(blockpos1);
                FluidState fluidstate = level.getFluidState(blockpos1);
                if (lastFluidState != null && !fluidstate.isEmpty() && lastFluidState.getFluidType() != fluidstate.getFluidType()) {
                    continue;
                }
                if(blockstate.getBlock() instanceof SimpleWaterloggedBlock){
                    if(!fluidstate.isEmpty()){
                        lastFluidState = fluidstate;
                    }
                    ++i;
                    fullBlocks++;
                    level.setBlockAndUpdate(blockpos1, blockstate.setValue(BlockStateProperties.WATERLOGGED, false));
                    if (j < MAX_FLUID_SPREAD) {
                        queue.add(new Tuple<>(blockpos1, j + 1));
                    }
                }else if (blockstate.getBlock() instanceof BucketPickup && !((BucketPickup) blockstate.getBlock()).pickupBlock(level, blockpos1, blockstate).isEmpty()) {
                    if(!fluidstate.isEmpty()){
                        lastFluidState = fluidstate;
                    }
                    ++i;
                    fullBlocks++;
                    level.setBlockAndUpdate(blockpos1, Blocks.AIR.defaultBlockState());
                    if (j < MAX_FLUID_SPREAD) {
                        queue.add(new Tuple<>(blockpos1, j + 1));
                    }
                } else if (blockstate.getBlock() instanceof LiquidBlock) {
                    if(!fluidstate.isEmpty()){
                        lastFluidState = fluidstate;
                    }
                    level.setBlockAndUpdate(blockpos1, Blocks.AIR.defaultBlockState());
                    ++i;
                    if (blockstate.getFluidState().isSource()) {
                        fullBlocks++;
                    }
                    if (j < MAX_FLUID_SPREAD) {
                        queue.add(new Tuple<>(blockpos1, j + 1));
                    }
                } else if (blockstate.is(ACTagRegistry.DRAIN_BREAKS)) {
                    if(!fluidstate.isEmpty()){
                        lastFluidState = fluidstate;
                    }
                    BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(blockpos1) : null;
                    dropResources(blockstate, level, blockpos1, blockentity);
                    if (blockstate.getFluidState().isSource()) {
                        fullBlocks++;
                    }
                    level.setBlockAndUpdate(blockpos1, Blocks.AIR.defaultBlockState());
                    ++i;
                    if (j < MAX_FLUID_SPREAD) {
                        queue.add(new Tuple<>(blockpos1, j + 1));
                    }
                }
            }
            if (i > MAXIMUM_BLOCKS_DRAINED) {
                break;
            }
        }
        return fullBlocks;
    }


    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

}
