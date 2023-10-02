package com.github.alexmodguy.alexscaves.server.inventory;

import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class NuclearFurnaceMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;
    protected final Level level;

    public NuclearFurnaceMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(5), new SimpleContainerData(5));
    }

    public NuclearFurnaceMenu(int id, Inventory inventory, Container furnaceContainer, ContainerData dataAccess) {
        super(ACMenuRegistry.NUCLEAR_FURNACE_MENU.get(), id);
        this.container = furnaceContainer;
        this.data = dataAccess;
        this.level = inventory.player.level();
        this.addSlot(new Slot(furnaceContainer, 0, 67, 17));
        this.addSlot(new FuelSlot(this, furnaceContainer, 1, 67, 53, false));
        this.addSlot(new FuelSlot(this, furnaceContainer, 2, 37, 53, true));
        this.addSlot(new NuclearFurnaceResultSlot(inventory.player, furnaceContainer, 3, 127, 35));
        this.addSlot(new NuclearFurnaceResultSlot(inventory.player, furnaceContainer, 4, 37, 17));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
        this.addDataSlots(dataAccess);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotIndex == 3 || slotIndex == 4) {
                if (!this.moveItemStackTo(itemstack1, 5, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (slotIndex != 2 && slotIndex != 1 && slotIndex != 0) {
                if (this.canSmelt(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isBarrel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 4 && slotIndex < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 30 && slotIndex < 39 && !this.moveItemStackTo(itemstack1, 5, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 5, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    private boolean canSmelt(ItemStack stack) {
        return this.level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(stack), this.level).isPresent();
    }

    private boolean isFuel(ItemStack stack) {
        return stack.is(ACTagRegistry.NUCLEAR_FURNACE_RODS);
    }

    private boolean isBarrel(ItemStack stack) {
        return stack.is(ACTagRegistry.NUCLEAR_FURNACE_BARRELS);
    }

    public float getWasteScale() {
        int i = this.data.get(0);
        return i / (float)NuclearFurnaceBlockEntity.MAX_WASTE;
    }

    public float getBarrelScale() {
        int i = this.data.get(1);
        return i / (float)NuclearFurnaceBlockEntity.MAX_BARRELING_TIME;
    }

    public float getFissionScale() {
        int i = this.data.get(2);
        return i / (float)NuclearFurnaceBlockEntity.MAX_FISSION_TIME;
    }

    public float getCookScale() {
        int i = this.data.get(3);
        int j = this.data.get(4);
        return i / (float)j;
    }

    private class FuelSlot extends Slot {
        private final NuclearFurnaceMenu menu;
        private final boolean barrel;

        public FuelSlot(NuclearFurnaceMenu nuclearFurnaceMenu, Container container, int slot, int x, int y, boolean barrel) {
            super(container, slot, x, y);
            this.menu = nuclearFurnaceMenu;
            this.barrel = barrel;
        }

        public boolean mayPlace(ItemStack stack) {
            return barrel ? this.menu.isBarrel(stack) : this.menu.isFuel(stack);
        }
    }
}
