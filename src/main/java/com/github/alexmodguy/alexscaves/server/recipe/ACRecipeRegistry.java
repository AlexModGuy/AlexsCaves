package com.github.alexmodguy.alexscaves.server.recipe;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ACRecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> DEF_REG = DeferredRegister.create(Registries.RECIPE_SERIALIZER, AlexsCaves.MODID);

    public static final RegistryObject<RecipeSerializer<?>> CAVE_MAP = DEF_REG.register("cave_map", () -> new SimpleCraftingRecipeSerializer<>(RecipeCaveMap::new));
}
