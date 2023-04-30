package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.List;

public class CaveInfoItem extends Item {

    private boolean hideCaveId;

    public CaveInfoItem(Properties properties, boolean hideCaveId) {
        super(properties);
        this.hideCaveId = hideCaveId;
    }

    public static int getBiomeColorOf(Level level, ItemStack stack) {
        if (stack.getItem() instanceof CaveInfoItem) {
            ResourceKey<Biome> biomeResourceKey = getCaveBiome(stack);
            if(biomeResourceKey != null){
                int color = ACBiomeRegistry.getBiomeTabletColor(biomeResourceKey);
                if (color == -1) {
                    if (level != null) {
                        Registry<Biome> registry = level.registryAccess().registry(Registries.BIOME).orElse(null);
                        if (registry != null && registry.getHolder(biomeResourceKey).isPresent()) {
                            return registry.getHolder(biomeResourceKey).get().value().getFoliageColor();
                        }
                    }
                    return 0;
                } else {
                    return color;
                }
            }
        }
        return -1;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        return InteractionResultHolder.pass(itemstack);
    }

    public static ItemStack create(Item item, ResourceKey<Biome> biomeResourceKey) {
        ItemStack map = new ItemStack(item);
        CompoundTag tag = new CompoundTag();
        tag.putString("CaveBiome", biomeResourceKey.location().toString());
        map.setTag(tag);
        return map;
    }


    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        ResourceKey<Biome> biomeResourceKey = getCaveBiome(stack);
        if (biomeResourceKey != null && !this.hideCaveId) {
            String biomeName = "biome." + biomeResourceKey.location().toString().replace(":", ".");
            tooltip.add(Component.translatable(biomeName).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public static ResourceKey<Biome> getCaveBiome(ItemStack stack) {
        if (stack.getTag() != null) {
            String s = stack.getTag().getString("CaveBiome");
            return s == null ? null : ResourceKey.create(Registries.BIOME, new ResourceLocation(s));
        }
        return null;
    }
}
