package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ACCreativeTab extends CreativeModeTab {

    public static final ACCreativeTab INSTANCE = new ACCreativeTab();

    private ACCreativeTab() {
        super(AlexsCaves.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ACBlockRegistry.GALENA.get());
    }

    @OnlyIn(Dist.CLIENT)
    public void fillItemList(NonNullList<ItemStack> items) {
        super.fillItemList(items);
    }
}
