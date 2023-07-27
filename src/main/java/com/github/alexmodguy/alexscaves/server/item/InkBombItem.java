package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.entity.item.InkBombEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InkBombItem extends Item {

    private final boolean glowing;

    public InkBombItem(Item.Properties properties, boolean glowing) {
        super(properties);
        this.glowing = glowing;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, (level.getRandom().nextFloat() * 0.7F + 0.25F) * 0.5F);
        if (!level.isClientSide) {
            InkBombEntity bomb = new InkBombEntity(level, player);
            bomb.setItem(itemstack);
            bomb.setGlowingBomb(this.glowing);
            bomb.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.65F, 0.9F);
            level.addFreshEntity(bomb);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}