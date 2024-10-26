package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class RadiantEssenceItem extends Item {

    public RadiantEssenceItem() {
        super(new Item.Properties().rarity(ACItemRegistry.RARITY_RAINBOW));
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(this == ACItemRegistry.LICOWITCH_RADIANT_ESSENCE.get()){
            tooltip.add(Component.translatable("item.alexscaves.licowitch_radiant_essence.desc").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
