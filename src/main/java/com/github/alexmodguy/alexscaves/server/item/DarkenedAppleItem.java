package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DarkenedAppleItem extends Item {

    public DarkenedAppleItem() {
        super(new Properties().food(ACFoods.DARKENED_APPLE).rarity(ACItemRegistry.RARITY_DEMONIC));
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        MobEffectInstance mobEffectInstance = livingEntity.getEffect(ACEffectRegistry.DARKNESS_INCARNATE.get());
        FoodProperties foodProperties = this.getFoodProperties(stack, livingEntity);
        if (mobEffectInstance != null && foodProperties != null) {
            int newDuration = mobEffectInstance.getDuration() + 600;
            MobEffectInstance newEffect = new MobEffectInstance(ACEffectRegistry.DARKNESS_INCARNATE.get(), newDuration, mobEffectInstance.getAmplifier());
            livingEntity.forceAddEffect(newEffect, null);
            ACAdvancementTriggerRegistry.EAT_DARKENED_APPLE.triggerForEntity(livingEntity);
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }
}
