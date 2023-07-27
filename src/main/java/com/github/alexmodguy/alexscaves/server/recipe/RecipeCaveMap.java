package com.github.alexmodguy.alexscaves.server.recipe;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CaveInfoItem;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.github.alexthe666.citadel.recipe.SpecialRecipeInGuideBook;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.biome.Biome;

public class RecipeCaveMap extends ShapedRecipe implements SpecialRecipeInGuideBook {
    public RecipeCaveMap(ResourceLocation name, CraftingBookCategory category) {
        super(name, "", category, 3, 3, NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(ACItemRegistry.CAVE_CODEX.get()), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER)), new ItemStack(ACItemRegistry.CAVE_MAP.get()));
    }

    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack scroll = ItemStack.EMPTY;
        for (int i = 0; i <= container.getContainerSize(); ++i) {
            if (!container.getItem(i).isEmpty() && container.getItem(i).is(ACItemRegistry.CAVE_CODEX.get())) {
                if (scroll.isEmpty()) {
                    scroll = container.getItem(i);
                }
            }
        }
        ResourceKey<Biome> key = CaveInfoItem.getCaveBiome(scroll);
        if (key != null) {
            return CaveMapItem.createMap(key);
        }
        return ItemStack.EMPTY;
    }

    public RecipeSerializer<?> getSerializer() {
        return ACRecipeRegistry.CAVE_MAP.get();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    public boolean isSpecial() {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getDisplayIngredients() {
        return getIngredients();
    }

    @Override
    public ItemStack getDisplayResultFor(NonNullList<ItemStack> nonNullList) {
        ItemStack scroll = ItemStack.EMPTY;
        for (int i = 0; i <= nonNullList.size(); ++i) {
            if (!nonNullList.get(i).isEmpty() && nonNullList.get(i).is(ACItemRegistry.CAVE_CODEX.get())) {
                if (scroll.isEmpty()) {
                    scroll = nonNullList.get(i);
                }
            }
        }
        ResourceKey<Biome> key = CaveInfoItem.getCaveBiome(scroll);
        if (key != null) {
            return CaveMapItem.createMap(key);
        }
        return ItemStack.EMPTY;
    }
}

