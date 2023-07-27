package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface LaysEggs {

    boolean hasEgg();

    void setHasEgg(boolean hasEgg);

    BlockState createEggBlockState();

    default void onLayEggTick(BlockPos belowEgg, int time) {
    }

}
