package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.world.food.FoodProperties;

public class ACFoods {
    public static final FoodProperties PINE_NUTS = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.175F).build();
    public static final FoodProperties DINOSAUR_NUGGETS = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().fast().build();

}
