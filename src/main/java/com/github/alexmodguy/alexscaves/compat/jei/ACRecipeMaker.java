package com.github.alexmodguy.alexscaves.compat.jei;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CaveInfoItem;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class ACRecipeMaker {

    public static List<CraftingRecipe> createCaveMapRecipes() {
        String group = "jei.cave_map";
        List<CraftingRecipe> recipes = new ArrayList<>();
        for (ResourceKey<Biome> biome : ACBiomeRegistry.ALEXS_CAVES_BIOMES) {
            ItemStack scroll = CaveInfoItem.create(ACItemRegistry.CAVE_CODEX.get(), biome);
            ItemStack map = CaveMapItem.createMap(biome);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "jei.cave_map_" + biome.location().getPath());
            Ingredient paper = Ingredient.of(Items.PAPER);
            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
                    paper, paper, paper,
                    paper, Ingredient.of(scroll), paper,
                    paper, paper, paper
            );
            recipes.add(new ShapedRecipe(id, group, CraftingBookCategory.MISC, 3, 3, inputs, map));
        }
        return recipes;
    }

    public static List<SpelunkeryTableRecipe> createSpelunkeryTableRecipes() {
        List<SpelunkeryTableRecipe> recipes = new ArrayList<>();
        ACBiomeRegistry.ALEXS_CAVES_BIOMES.forEach(biomeResourceKey -> recipes.add(new SpelunkeryTableRecipe(biomeResourceKey)));
        return recipes;
    }

}
