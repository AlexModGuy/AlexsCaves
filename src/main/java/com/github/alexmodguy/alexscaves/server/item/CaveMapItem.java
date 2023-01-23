package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.level.map.CaveBiomeFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

public class CaveMapItem extends Item {

    public static int MAP_SCALE = 10;
    public CaveMapItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(!isLoading(itemstack) && !isFilled(itemstack)) {
            itemstack.getOrCreateTag().putBoolean("Loading", true);
            if(!level.isClientSide){
                CaveBiomeFinder.fillOutCaveMap(itemstack, (ServerLevel) level, player.blockPosition());
            }
        }
        return InteractionResultHolder.success(itemstack);
    }

    public static ItemStack createMap(ResourceKey<Biome> biomeResourceKey){
        ItemStack map = new ItemStack(ACItemRegistry.CAVE_MAP.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("BiomeTargetResourceKey", biomeResourceKey.location().toString());
        map.setTag(tag);
        return map;
    }

    public static boolean isLoading(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().getBoolean("Loading");
    }

    public static boolean isFilled(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().getBoolean("Filled");
    }

    public static BlockPos getBiomeBlockPos(ItemStack stack){
        if(stack.getTag() != null){
            return new BlockPos(stack.getTag().getInt("BiomeX"), stack.getTag().getInt("BiomeY"), stack.getTag().getInt("BiomeZ"));
        }
        return BlockPos.ZERO;
    }

    public static int[] getBiomes(ItemStack stack){
        if(stack.getTag() != null){
            return stack.getTag().getIntArray("MapBiomes");
        }
        return new int[0];
    }

    public static long getSeed(ItemStack stack){
        if(stack.getTag() != null){
            return stack.getTag().getLong("RandomSeed");
        }
        return 0;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        ResourceKey<Biome> biomeResourceKey = getBiomeTarget(stack);
        if(biomeResourceKey != null){
            String biomeName = "biome." + biomeResourceKey.location().toString().replace(":", ".");
            tooltip.add(Component.translatable(biomeName).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public static ResourceKey<Biome> getBiomeTarget(ItemStack stack){
        if(stack.getTag() != null){
            String s = stack.getTag().getString("BiomeTargetResourceKey");
            return s == null ? null : ResourceKey.create(Registries.BIOME, new ResourceLocation(s));
        }
        return null;
    }
}
