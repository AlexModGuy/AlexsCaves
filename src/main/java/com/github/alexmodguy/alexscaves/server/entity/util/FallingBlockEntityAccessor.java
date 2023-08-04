package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.world.level.block.state.BlockState;

public interface FallingBlockEntityAccessor {

    boolean hasFallBlocking();

    void setFallBlockingTime();

    void setBlockState(BlockState state);
}
