package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.WorldEventMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
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

    default void updateDistanceShape(Level accessor, BlockState state, BlockPos pos) {
        int i = getDistanceAt(state) + 1;
        if (i != 1 || state.getValue(DISTANCE) != i) {
            accessor.scheduleTick(pos, (Block) this, 1);
        }
    }

    default BlockState updateDistance(BlockState state, Level level, BlockPos blockPos) {
        int i = MAX_DISTANCE;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            blockpos$mutableblockpos.setWithOffset(blockPos, direction);
            i = Math.min(i, getDistanceAt(level.getBlockState(blockpos$mutableblockpos)) + 1);
            if (i == 1) {
                break;
            }
        }
        boolean prevActive = state.getValue(ACTIVE);
        int prevDist = state.getValue(DISTANCE);
        BlockState state1 = state.setValue(DISTANCE, Integer.valueOf(i));
        if(i <= 1 && !prevActive){
            AlexsCaves.sendMSGToAll(new WorldEventMessage(1, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        }
        if(prevDist <= 1 && prevActive){
            AlexsCaves.sendMSGToAll(new WorldEventMessage(2, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        }
        return activeDistance(i) ? state1.setValue(ACTIVE, true) : state1.setValue(ACTIVE, false);
    }


}
