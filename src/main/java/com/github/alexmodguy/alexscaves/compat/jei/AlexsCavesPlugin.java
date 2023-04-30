package com.github.alexmodguy.alexscaves.compat.jei;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.gui.SpelunkeryTableScreen;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class AlexsCavesPlugin implements IModPlugin {
    public static final ResourceLocation MOD = new ResourceLocation(AlexsCaves.MODID, AlexsCaves.MODID);
    public static final RecipeType<SpelunkeryTableRecipe> SPELUNKERY_TABLE_RECIPE_TYPE = RecipeType.create(AlexsCaves.MODID, "spelunkery_table", SpelunkeryTableRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return MOD;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new SpelunkeryTableRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SPELUNKERY_TABLE_RECIPE_TYPE, ACRecipeMaker.createSpelunkeryTableRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, ACRecipeMaker.createCaveMapRecipes());

    }
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(SpelunkeryTableScreen.class, new SpelunkeryTableJEIGuiHandler());
    }
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ACBlockRegistry.SPELUNKERY_TABLE.get()), SPELUNKERY_TABLE_RECIPE_TYPE);
    }
}
