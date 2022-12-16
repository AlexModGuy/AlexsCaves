package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CustomTabBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.RegistryObject;

public class ACCreativeTab {

    public static final ResourceLocation TAB = new ResourceLocation("alexscaves:alexscaves");

    private static ItemStack makeIcon() {
        return new ItemStack(ACBlockRegistry.AMBERSOL.get());
    }

    public static void registerTab(CreativeModeTabEvent.Register event){
        event.registerCreativeModeTab(TAB, builder -> builder.title(Component.translatable("itemGroup.alexscaves")).icon(ACCreativeTab::makeIcon).displayItems((flags, output, isOp) -> {
            for(RegistryObject<Item> item : ACItemRegistry.DEF_REG.getEntries()){
                if(item.get() instanceof CustomTabBehavior customTabBehavior){
                    customTabBehavior.fillItemCategory(output);
                }else{
                    output.accept(item.get());
                }
            }
        }));

    }

}
