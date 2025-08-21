package com.github.alexmodguy.alexscaves.compat.jei;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.gui.SpelunkeryTableScreen;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JeiPlugin
public class AlexsCavesPlugin implements IModPlugin {
    public static final ResourceLocation MOD = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, AlexsCaves.MODID);
    public static final RecipeType<SpelunkeryTableRecipe> SPELUNKERY_TABLE_RECIPE_TYPE = RecipeType.create(AlexsCaves.MODID, "spelunkery_table", SpelunkeryTableRecipe.class);
    public static final RecipeType<AbstractCookingRecipe> NUCLEAR_FURNACE_RECIPE_TYPE = RecipeType.create(AlexsCaves.MODID, "nuclear_furnace", AbstractCookingRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return MOD;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new SpelunkeryTableRecipeCategory(guiHelper));
        registration.addRecipeCategories(new NuclearFurnaceRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SPELUNKERY_TABLE_RECIPE_TYPE, ACRecipeMaker.createSpelunkeryTableRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, ACRecipeMaker.createCaveMapRecipes());
        if(Minecraft.getInstance().level != null){
            List<AbstractCookingRecipe> abstractCookingRecipeList = new ArrayList<>();
            abstractCookingRecipeList.addAll(Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(NuclearFurnaceBlockEntity.getRecipeType()));
            registration.addRecipes(NUCLEAR_FURNACE_RECIPE_TYPE, abstractCookingRecipeList);
        }
    }

    @Override
    public void registerRuntime(IRuntimeRegistration registration) {
        if(Minecraft.getInstance().level != null){
            Optional<? extends Recipe> alexMealRecipe = Minecraft.getInstance().level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "alex_meal"));
            if(alexMealRecipe.isPresent() && alexMealRecipe.get() instanceof CraftingRecipe craftingRecipe){
                registration.getRecipeManager().hideRecipes(RecipeTypes.CRAFTING, List.of(craftingRecipe));
            }
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(SpelunkeryTableScreen.class, new SpelunkeryTableJEIGuiHandler());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ACBlockRegistry.SPELUNKERY_TABLE.get()), SPELUNKERY_TABLE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.get()), NUCLEAR_FURNACE_RECIPE_TYPE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ACItemRegistry.CAVE_TABLET.get(), CaveTabletSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(ACItemRegistry.CAVE_CODEX.get(), CaveTabletSubtypeInterpreter.INSTANCE);
    }
}
