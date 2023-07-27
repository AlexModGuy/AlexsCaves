package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ACFoods {
    public static final FoodProperties TRILOCARIS_TAIL = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.3F).meat().build();
    public static final FoodProperties TRILOCARIS_TAIL_COOKED = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.5F).meat().build();
    public static final FoodProperties PINE_NUTS = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.175F).build();
    public static final FoodProperties DINOSAUR_NUGGETS = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().fast().build();

    public static final FoodProperties SERENE_SALAD = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.35F).build();
    public static final FoodProperties SEETHING_STEW = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.6F).effect(() -> new MobEffectInstance(ACEffectRegistry.RAGE.get(), 2200), 1.0F).build();
    public static final FoodProperties PRIMORDIAL_SOUP = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.6F).effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 800), 1.0F).build();

    public static final FoodProperties RADGILL = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.2F).effect(() -> new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 2000), 1.0F).build();
    public static final FoodProperties RADGILL_COOKED = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).effect(() -> new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 1000), 0.1F).build();
    public static final FoodProperties SPELUNKIE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).fast().build();
    public static final FoodProperties SLAM = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.5F).meat().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400), 1.0F).build();
    public static final FoodProperties SOYLENT_GREEN = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.35F).meat().build();

}
