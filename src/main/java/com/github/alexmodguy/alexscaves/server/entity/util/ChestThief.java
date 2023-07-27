package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface ChestThief {

    default boolean isLootable(Container inventory) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (shouldLootItem(inventory.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    boolean shouldLootItem(ItemStack stack);

    default void afterSteal(BlockPos stealPos) {
    }

    default void startOpeningChest() {
    }

    default boolean didOpeningChest() {
        return true;
    }

}
