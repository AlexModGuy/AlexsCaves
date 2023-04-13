package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.SlabType;

public class AbyssmarineSlabBlock extends SlabBlock implements ActivatedByAltar {

    public AbyssmarineSlabBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(DISTANCE, Integer.valueOf(MAX_DISTANCE)).setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(ACTIVE, Boolean.valueOf(false)));
    }

    public void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource randomSource) {
        super.tick(state, serverLevel, pos, randomSource);
        serverLevel.setBlock(pos, updateDistance(state, serverLevel, pos), 3);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        BlockState newState = super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
        int i = ActivatedByAltar.getDistanceAt(state1) + 1;
        if (i != 1 || newState.getValue(DISTANCE) != i) {
            levelAccessor.scheduleTick(blockPos, this, 2);
        }
        return newState;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateDistance(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, ACTIVE, TYPE, WATERLOGGED);
    }
}
