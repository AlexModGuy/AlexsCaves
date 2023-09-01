package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CaveBookItem extends Item {
    public CaveBookItem() {
        super(new Item.Properties().stacksTo(1));
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemStackIn = playerIn.getItemInHand(handIn);
        if (playerIn instanceof ServerPlayer) {
            ServerPlayer serverplayerentity = (ServerPlayer) playerIn;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStackIn);
            serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
        }
        playerIn.swing(handIn);
        if (worldIn.isClientSide) {
            AlexsCaves.PROXY.openBookGUI(itemStackIn);
        }
        return new InteractionResultHolder(InteractionResult.PASS, itemStackIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.alexscaves.cave_book.desc").withStyle(ChatFormatting.GRAY));
    }
}
