package com.github.alexmodguy.alexscaves.server.item.tooltip;

import com.github.alexmodguy.alexscaves.server.item.SackOfSatingItem;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class SackOfSatingTooltip implements TooltipComponent {

    private final ItemStack itemStack;

    public SackOfSatingTooltip(ItemStack stack) {
        this.itemStack = stack;
    }

    public int getHungerValue() {
        return SackOfSatingItem.getHunger(itemStack);
    }
}
