package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
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
import java.util.UUID;

public class CaveMapItem extends Item implements UpdatesStackTags {

    public static int MAP_SCALE = 7;

    public CaveMapItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!isLoading(itemstack) && !isFilled(itemstack)) {
            if (!level.isClientSide) {
                CompoundTag tag = itemstack.getOrCreateTag();
                UUID uuid;
                if (!tag.contains("MapUUID")) {
                    uuid = UUID.randomUUID();
                    tag.putUUID("MapUUID", uuid);
                    AlexsCaves.sendMSGToAll(new UpdateItemTagMessage(player.getId(), itemstack));
                }else{
                    uuid = tag.getUUID("MapUUID");
                }
                tag.putBoolean("Loading", true);
                itemstack.setTag(tag);
                ACWorldData acWorldData = ACWorldData.get(level);
                if (acWorldData != null) {
                    acWorldData.fillOutCaveMap(uuid, itemstack, (ServerLevel) level, player.getRootVehicle().blockPosition(), player);
                }
            }
            return InteractionResultHolder.success(itemstack);
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public static ItemStack createMap(ResourceKey<Biome> biomeResourceKey) {
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

    public static BlockPos getBiomeBlockPos(ItemStack stack) {
        if (stack.getTag() != null) {
            return new BlockPos(stack.getTag().getInt("BiomeX"), stack.getTag().getInt("BiomeY"), stack.getTag().getInt("BiomeZ"));
        }
        return BlockPos.ZERO;
    }

    public static int[] getBiomes(ItemStack stack) {
        if (stack.getTag() != null) {
            return stack.getTag().getIntArray("MapBiomes");
        }
        return new int[0];
    }

    public static long getSeed(ItemStack stack) {
        if (stack.getTag() != null) {
            return stack.getTag().getLong("RandomSeed");
        }
        return 0;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        ResourceKey<Biome> biomeResourceKey = getBiomeTarget(stack);
        if (biomeResourceKey != null) {
            String biomeName = "biome." + biomeResourceKey.location().toString().replace(":", ".");
            tooltip.add(Component.translatable(biomeName).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public static ResourceKey<Biome> getBiomeTarget(ItemStack stack) {
        if (stack.getTag() != null) {
            String s = stack.getTag().getString("BiomeTargetResourceKey");
            return s == null ? null : ResourceKey.create(Registries.BIOME, new ResourceLocation(s));
        }
        return null;
    }
}
