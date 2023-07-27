package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexthe666.citadel.item.BlockItemWithSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

public class BlockItemWithSupplierLore extends BlockItemWithSupplier {

    private final RegistryObject<Block> block;

    public BlockItemWithSupplierLore(RegistryObject<Block> blockSupplier, Properties props) {
        super(blockSupplier, props);
        this.block = blockSupplier;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        String blockName = block.getId().getNamespace() + "." + block.getId().getPath();
        tooltip.add(Component.translatable("block." + blockName + ".desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
