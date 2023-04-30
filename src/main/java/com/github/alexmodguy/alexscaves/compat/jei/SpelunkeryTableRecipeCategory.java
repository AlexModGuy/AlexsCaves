package com.github.alexmodguy.alexscaves.compat.jei;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CaveInfoItem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class SpelunkeryTableRecipeCategory implements IRecipeCategory<SpelunkeryTableRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public SpelunkeryTableRecipeCategory(IGuiHelper guiHelper) {
        background = new SpelunkeryTableDrawable();
        icon = guiHelper.createDrawableItemStack(new ItemStack(ACBlockRegistry.SPELUNKERY_TABLE.get()));
    }

    @Override
    public RecipeType<SpelunkeryTableRecipe> getRecipeType() {
        return AlexsCavesPlugin.SPELUNKERY_TABLE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("alexscaves.container.spelunkery_table_translation");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SpelunkeryTableRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 14, 6).addIngredients(Ingredient.of(CaveInfoItem.create(ACItemRegistry.CAVE_TABLET.get(), recipe.getBiomeResourceKey())));
        builder.addSlot(RecipeIngredientRole.INPUT, 34, 6).addIngredients(Ingredient.of(Items.PAPER));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 106, 6).addItemStack(CaveInfoItem.create(ACItemRegistry.CAVE_CODEX.get(), recipe.getBiomeResourceKey()));
    }

    @Override
    public boolean isHandled(SpelunkeryTableRecipe recipe) {
        return true;
    }
}

