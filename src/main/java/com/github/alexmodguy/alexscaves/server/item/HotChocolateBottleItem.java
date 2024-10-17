package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HotChocolateBottleItem extends DrinkableBottledItem {

    public HotChocolateBottleItem() {
        super(new Item.Properties().stacksTo(16).food(ACFoods.HOT_CHOCOLATE_BOTTLE));
    }

    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack prev = super.finishUsingItem(itemStack, level, livingEntity);
        if (livingEntity instanceof ServerPlayer player) {
            player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        }
        return prev;
    }
}
