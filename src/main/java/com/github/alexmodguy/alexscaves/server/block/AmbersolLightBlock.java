package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Predicate;

public class AmbersolLightBlock extends Block {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public AmbersolLightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos blockPos) {
        return true;
    }

    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos blockPos) {
        return 1.0F;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    public RenderShape getRenderShape(BlockState p_153693_) {
        return RenderShape.INVISIBLE;
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockPos getTopOfColumn(BlockPos current, LevelReader levelReader, Predicate<BlockState> predicate) {
        while (current.getY() < levelReader.getMaxBuildHeight() && predicate.test(levelReader.getBlockState(current))) {
            current = current.above();
        }
        return current;
    }

    public BlockPos getTopOfColumnLight(BlockPos current, LevelReader levelReader) {
        while (current.getY() < levelReader.getMaxBuildHeight() && testSkylight(levelReader, levelReader.getBlockState(current), current)) {
            current = current.above();
        }
        return current;
    }

    public static boolean testSkylight(LevelReader levelReader, BlockState blockState, BlockPos current) {
        return blockState.propagatesSkylightDown(levelReader, current);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos top = getTopOfColumnLight(pos, level);
        return level.getBlockState(top).is(ACBlockRegistry.AMBERSOL.get());
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        if (levelAccessor.getBlockState(blockPos.below()).getBlock() != this) {
            BlockPos top = getTopOfColumn(blockPos, levelAccessor, state2 -> !state2.is(ACBlockRegistry.AMBERSOL.get()));
            levelAccessor.scheduleTick(new BlockPos(top), ACBlockRegistry.AMBERSOL.get(), 3);
        }
        if (!state.canSurvive(levelAccessor, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

}
