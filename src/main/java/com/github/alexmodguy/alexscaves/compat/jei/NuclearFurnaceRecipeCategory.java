package com.github.alexmodguy.alexscaves.compat.jei;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class NuclearFurnaceRecipeCategory implements IRecipeCategory<AbstractCookingRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public NuclearFurnaceRecipeCategory(IGuiHelper guiHelper) {

        background = new NuclearFurnaceDrawable();
        icon = guiHelper.createDrawableItemStack(new ItemStack(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get()));
    }

    @Override
    public RecipeType<AbstractCookingRecipe> getRecipeType() {
        return AlexsCavesPlugin.NUCLEAR_FURNACE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("alexscaves.container.nuclear_furnace_blasting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, AbstractCookingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 62, 38).addIngredients(Ingredient.of(ACTagRegistry.NUCLEAR_FURNACE_RODS));
        builder.addSlot(RecipeIngredientRole.INPUT, 32, 38).addIngredients(Ingredient.of(ACTagRegistry.NUCLEAR_FURNACE_BARRELS));
        builder.addSlot(RecipeIngredientRole.INPUT, 62, 2).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 32, 2).addItemStack(new ItemStack(ACBlockRegistry.WASTE_DRUM.get()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 122, 20).addItemStack(getResultItem(recipe));
    }

    @Override
    public void draw(AbstractCookingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        drawExperience(recipe, guiGraphics, 0);
        drawCookTime(recipe, guiGraphics, 50);
    }

    protected void drawExperience(AbstractCookingRecipe recipe, GuiGraphics guiGraphics, int y) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(experienceString);
            guiGraphics.drawString(fontRenderer, experienceString, getWidth() - stringWidth, y, 0xFF808080, false);
        }
    }

    protected void drawCookTime(AbstractCookingRecipe recipe, GuiGraphics guiGraphics, int y) {
        int cookTime = (int) Math.ceil(recipe.getCookingTime() * NuclearFurnaceBlockEntity.getSpeedReduction());
        if (cookTime > 0) {
            int cookTimeSeconds = cookTime / 20;
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(timeString);
            guiGraphics.drawString(fontRenderer, timeString, getWidth() - stringWidth, y, 0xFF808080, false);
        }
    }



    @Override
    public boolean isHandled(AbstractCookingRecipe recipe) {
        return true;
    }

    public static ItemStack getResultItem(Recipe<?> recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        }
        RegistryAccess registryAccess = level.registryAccess();
        return recipe.getResultItem(registryAccess);
    }

}

