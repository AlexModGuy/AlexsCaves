package com.github.alexmodguy.alexscaves.server.recipe;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class NuclearFurnaceRecipe extends AbstractCookingRecipe {
    public NuclearFurnaceRecipe(ResourceLocation name, String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(ACRecipeRegistry.NUCLEAR_FURNACE_TYPE.get(), name, group, category, ingredient, result, experience, cookingTime);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ACBlockRegistry.NUCLEAR_FURNACE.get());
    }

    public RecipeSerializer<?> getSerializer() {
        return ACRecipeRegistry.NUCLEAR_FURNACE.get();
    }
}
