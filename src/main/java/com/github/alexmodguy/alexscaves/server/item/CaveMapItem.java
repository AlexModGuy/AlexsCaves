package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CaveMapItem extends Item implements UpdatesStackTags {

    private static final double CHECK_REGEN_TICKS = 1800;
    private static final double TRIGGER_REGEN_DIST = 200;
    public static final int MAP_SCALE = 7;

    public CaveMapItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
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

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        if(!level.isClientSide() && held && !isLoading(stack) && isFilled(stack) && (entity.tickCount + entity.getId()) % CHECK_REGEN_TICKS == 0 && entity instanceof Player){
            BlockPos biomePos = getBiomeBlockPos(stack);
            double xD = biomePos.getX() - entity.blockPosition().getX();
            double zD = biomePos.getZ() - entity.blockPosition().getZ();
            if(Mth.sqrt((float) (xD * xD + zD * zD)) < TRIGGER_REGEN_DIST){
                ResourceKey<Biome> biomeResourceKey = getBiomeTarget(stack);
                Holder<Biome> currentBiome = level.getBiome(biomePos);
                if(biomeResourceKey == null || !currentBiome.is(biomeResourceKey)){
                    ACWorldData acWorldData = ACWorldData.get(level);
                    if (acWorldData != null) {
                        UUID uuid;
                        CompoundTag tag = stack.getOrCreateTag();
                        if (!tag.contains("MapUUID")) {
                            uuid = UUID.randomUUID();
                            tag.putUUID("MapUUID", uuid);
                            AlexsCaves.sendMSGToAll(new UpdateItemTagMessage(entity.getId(), stack));
                        }else{
                            uuid = tag.getUUID("MapUUID");
                        }
                        String currentBiomeName = currentBiome.unwrapKey().isPresent() ? currentBiome.unwrapKey().get().location().toString() : "NULL";
                        String wantedBiomeName = biomeResourceKey == null ? "NULL" : biomeResourceKey.location().toString();
                        AlexsCaves.LOGGER.info("regenerating cave biome map, incorrect biome {} found at {} {} {}, should be {}", currentBiomeName, biomePos.getX(), biomePos.getY(), biomePos.getZ(), wantedBiomeName);
                        acWorldData.fillOutCaveMap(uuid, stack, (ServerLevel) level, entity.getRootVehicle().blockPosition(), (Player)entity);
                    }
                }
            }
        }
    }

    public static int[] createBiomeArray(ItemStack stack) {
        if (stack.getTag() != null) {
            ListTag listTag = stack.getTag().getList("MapBiomeList", 10);
            Map<Byte, Integer> integerByteMap = new HashMap<>();
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag innerTag = listTag.getCompound(i);
                integerByteMap.put(innerTag.getByte("BiomeHash"), innerTag.getInt("BiomeID"));
            }
            byte[] byteArray = stack.getTag().getByteArray("MapBiomes");
            int[] intArray = new int[128 * 128];
            int j = Math.min(intArray.length, byteArray.length);
            if(j > 0){
                for(int i = 0; i < j; i++){
                    intArray[i] = integerByteMap.get(byteArray[i]);
                }
            }
            return intArray;
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
            return s == null ? null : ResourceKey.create(Registries.BIOME, ResourceLocation.parse(s));
        }
        return null;
    }
}
