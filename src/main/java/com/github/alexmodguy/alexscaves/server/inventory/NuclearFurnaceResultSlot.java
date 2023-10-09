package com.github.alexmodguy.alexscaves.server.inventory;

import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;

public class NuclearFurnaceResultSlot extends FurnaceResultSlot {
    private Player player;
    private int removeCountNuclear;

    public NuclearFurnaceResultSlot(Player player, Container furnaceContainer, int slotId, int x, int y) {
        super(player, furnaceContainer, slotId, x, y);
        this.player = player;
    }


    public ItemStack remove(int count) {
        if (this.hasItem()) {
            this.removeCountNuclear += Math.min(count, this.getItem().getCount());
        }

        return super.remove(count);
    }

    protected void onQuickCraft(ItemStack itemStack, int i) {
        this.removeCountNuclear += i;
        this.checkTakeAchievements(itemStack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack itemStack) {
        itemStack.onCraftedBy(this.player.level(), this.player, this.removeCountNuclear);
        Player player = this.player;
        if (player instanceof ServerPlayer serverplayer) {
            Container container = this.container;
            if (container instanceof NuclearFurnaceBlockEntity nuclearFurnaceBlockEntity) {
                nuclearFurnaceBlockEntity.awardUsedRecipesAndPopExperience(serverplayer);
            }
        }

        this.removeCountNuclear = 0;
        net.minecraftforge.event.ForgeEventFactory.firePlayerSmeltedEvent(this.player, itemStack);
    }

}
