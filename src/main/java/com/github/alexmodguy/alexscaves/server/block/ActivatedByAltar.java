package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface ActivatedByAltar {
    int MAX_DISTANCE = 15;

    BooleanProperty ACTIVE = BooleanProperty.create("active");
    IntegerProperty DISTANCE = IntegerProperty.create("distance", 1, MAX_DISTANCE);


    static int getDistanceAt(BlockState blockState) {
        if (blockState.is(ACBlockRegistry.ABYSSAL_ALTAR.get()) && blockState.getValue(AbyssalAltarBlock.ACTIVE)) {
            return 0;
        } else {
            return blockState.getBlock() instanceof ActivatedByAltar ? blockState.getValue(DISTANCE) : MAX_DISTANCE;
        }
    }

    default boolean activeDistance(BlockState state) {
        return state.getValue(DISTANCE) < MAX_DISTANCE;
    }

    default boolean activeDistance(int distance) {
        return distance < MAX_DISTANCE;
    }

    default void updateDistanceShape(LevelAccessor accessor, BlockState state, BlockPos pos) {
        int i = getDistanceAt(state) + 1;
        if (i != 1 || state.getValue(DISTANCE) != i) {
            accessor.scheduleTick(pos, (Block) this, 1);
        }
    }

    default BlockState updateDistance(BlockState state, LevelAccessor levelAccessor, BlockPos blockPos) {
        int i = MAX_DISTANCE;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            blockpos$mutableblockpos.setWithOffset(blockPos, direction);
            i = Math.min(i, getDistanceAt(levelAccessor.getBlockState(blockpos$mutableblockpos)) + 1);
            if (i == 1) {
                break;
            }
        }
        BlockState state1 = state.setValue(DISTANCE, Integer.valueOf(i));
        return activeDistance(i) ? state1.setValue(ACTIVE, true) : state1.setValue(ACTIVE, false);
    }


}
