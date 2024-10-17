package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.SweetishFishEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class SweetishFishBucketItem extends ModFishBucketItem{

    private final GummyColors color;

    public SweetishFishBucketItem(GummyColors color) {
        super(ACEntityRegistry.SWEETISH_FISH, ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
        this.color = color;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.alexscaves.sweetish_fish_bucket.desc_" + color.name().toLowerCase()).withStyle(ChatFormatting.GRAY));
    }

    protected void addExtraAttributes(Entity entity, ItemStack stack) {
        if(entity instanceof SweetishFishEntity sweetishFish){
            sweetishFish.setGummyColor(color);
        }
    }
}
