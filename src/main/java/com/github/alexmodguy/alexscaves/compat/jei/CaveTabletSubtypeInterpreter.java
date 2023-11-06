package com.github.alexmodguy.alexscaves.compat.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CaveTabletSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final CaveTabletSubtypeInterpreter INSTANCE = new CaveTabletSubtypeInterpreter();

    private CaveTabletSubtypeInterpreter() {

    }

    @Override
    public String apply(ItemStack itemStack, UidContext context) {
        CompoundTag tag = itemStack.getTag();
        if(tag != null && tag.contains("CaveBiome")){
            return tag.getString("CaveBiome");
        }
        return "";
    }
}
