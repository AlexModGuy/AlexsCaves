package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HolocoderItem extends Item {
    public HolocoderItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity.isAlive()) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putUUID("BoundEntityUUID", entity.getUUID());
            CompoundTag entityTag = entity.serializeNBT();
            entityTag.putString("id", ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString());
            tag.put("BoundEntityTag", entityTag);
            ItemStack stackReplacement = new ItemStack(this);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            stackReplacement.setTag(tag);
            player.swing(hand);
            if (!player.addItem(stackReplacement)) {
                ItemEntity itementity = player.drop(stackReplacement, false);
                if (itementity != null) {
                    itementity.setNoPickUpDelay();
                    itementity.setThrower(player.getUUID());
                }
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getTag() != null) {
            Tag entity = stack.getTag().get("BoundEntityTag");
            if (entity instanceof CompoundTag) {
                Optional<EntityType<?>> optional = EntityType.by((CompoundTag) entity);
                if (optional.isPresent()) {
                    Component untranslated = optional.get().getDescription().copy().withStyle(ChatFormatting.GRAY);
                    tooltip.add(untranslated);
                }
            }
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public static UUID getBoundEntityUUID(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("BoundEntityUUID")) {
            return stack.getTag().getUUID("BoundEntityUUID");
        } else {
            return null;
        }
    }

    public static boolean isBound(ItemStack stack) {
        return getBoundEntityUUID(stack) != null;
    }
}
