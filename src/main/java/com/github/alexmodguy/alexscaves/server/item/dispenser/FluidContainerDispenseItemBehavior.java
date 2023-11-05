package com.github.alexmodguy.alexscaves.server.item.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.BlockHitResult;

public class FluidContainerDispenseItemBehavior extends DefaultDispenseItemBehavior {

    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

    public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)itemStack.getItem();
        BlockPos blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
        Level level = blockSource.getLevel();
        if (dispensiblecontaineritem.emptyContents((Player)null, level, blockpos, (BlockHitResult)null, itemStack)) {
            dispensiblecontaineritem.checkExtraContent((Player)null, level, itemStack, blockpos);
            return new ItemStack(Items.BUCKET);
        } else {
            return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
        }
    }
}
