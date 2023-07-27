package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.item.PrimordialArmorItem;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Shadow
    public abstract void eat(int nutrition, float saturation);

    @Inject(
            method = {"Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V"},
            cancellable = true,
            remap = false, //FORGE METHOD
            at = @At(value = "HEAD")
    )
    public void ac_eat(Item item, ItemStack stack, LivingEntity entity, CallbackInfo ci) {
        if (entity != null && stack.is(ACTagRegistry.RAW_MEATS)) {
            int extraShanksFromArmor = PrimordialArmorItem.getExtraSaturationFromArmor(entity);
            if (extraShanksFromArmor != 0) {
                ci.cancel();
                if (item.isEdible()) {
                    FoodProperties foodproperties = stack.getFoodProperties(entity);
                    this.eat(foodproperties.getNutrition() + extraShanksFromArmor, foodproperties.getSaturationModifier() + (extraShanksFromArmor * 0.125F));
                }
            }
        }
    }

}
