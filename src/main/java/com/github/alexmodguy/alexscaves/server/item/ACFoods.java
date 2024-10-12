package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ACFoods {
    public static final FoodProperties TRILOCARIS_TAIL = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.3F).meat().build();
    public static final FoodProperties TRILOCARIS_TAIL_COOKED = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.5F).meat().build();
    public static final FoodProperties PINE_NUTS = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.175F).build();
    public static final FoodProperties DINOSAUR_NUGGETS = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().fast().build();
    public static final FoodProperties SERENE_SALAD = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.35F).build();
    public static final FoodProperties SEETHING_STEW = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.6F).effect(() -> new MobEffectInstance(ACEffectRegistry.RAGE.get(), 2200), 1.0F).build();
    public static final FoodProperties PRIMORDIAL_SOUP = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.6F).effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 800), 1.0F).build();
    public static final FoodProperties RADGILL = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.2F).effect(() -> new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 2000), 1.0F).build();
    public static final FoodProperties RADGILL_COOKED = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.3F).effect(() -> new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 1000), 0.1F).build();
    public static final FoodProperties SPELUNKIE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).fast().build();
    public static final FoodProperties SLAM = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.5F).meat().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400), 1.0F).build();
    public static final FoodProperties SOYLENT_GREEN = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.35F).alwaysEat().meat().build();
    public static final FoodProperties LANTERNFISH = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.175F).fast().build();
    public static final FoodProperties LANTERNFISH_COOKED = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.3F).fast().build();
    public static final FoodProperties TRIPODFISH = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.2F).build();
    public static final FoodProperties TRIPODFISH_COOKED = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.34F).build();
    public static final FoodProperties SEA_PIG = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.2F).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 1200), 0.7F).build();
    public static final FoodProperties MUSSEL_COOKED = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).fast().build();
    public static final FoodProperties DEEP_SEA_SUSHI_ROLL = (new FoodProperties.Builder()).nutrition(7).saturationMod(0.4F).build();
    public static final FoodProperties STINKY_FISH = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.1F).effect(() -> new MobEffectInstance(ACEffectRegistry.STUNNED.get(), 100), 1.0F).build();
    public static final FoodProperties VESPER_WING = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.2F).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 1200), 1.0F).build();
    public static final FoodProperties VESPER_SOUP = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.3F).alwaysEat().effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 2400), 1.0F).build();
    public static final FoodProperties DARKENED_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.35F).alwaysEat().build();

}
