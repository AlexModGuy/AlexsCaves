package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.world.item.CreativeModeTab;

public interface CustomTabBehavior {
    void fillItemCategory(CreativeModeTab.Output contents);
}
