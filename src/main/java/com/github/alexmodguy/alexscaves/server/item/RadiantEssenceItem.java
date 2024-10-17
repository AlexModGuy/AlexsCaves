package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RadiantEssenceItem extends Item {

    public RadiantEssenceItem() {
        super(new Item.Properties().rarity(ACItemRegistry.RARITY_RAINBOW));
    }

    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean b) {
        if (!level.isClientSide) {
            if (wasThrownByLicowitch(itemStack) && entity.tickCount % 10 == 0) {
                itemStack.shrink(1);
                if(entity instanceof LivingEntity living){
                    living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 3000));
                }
            }
        }
    }

    public static boolean wasThrownByLicowitch(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.getBoolean("ThrownByLicowitch");
    }

}
