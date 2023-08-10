package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface KeybindUsingArmor {
    void onKeyPacket(Entity keyPresser, ItemStack itemStack, int keyType);
}
