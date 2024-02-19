package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class GalenaGauntletItem extends Item {
    public GalenaGauntletItem() {
        super(new Item.Properties().stacksTo(1).durability(400).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        ItemStack otherHand = interactionHand == InteractionHand.MAIN_HAND ? player.getItemInHand(InteractionHand.OFF_HAND) : player.getItemInHand(InteractionHand.MAIN_HAND);
        boolean crystallization = itemstack.getEnchantmentLevel(ACEnchantmentRegistry.CRYSTALLIZATION.get()) > 0;
        if (otherHand.is(crystallization ? ACTagRegistry.GALENA_GAUNTLET_CRYSTALLIZATION_ITEMS : ACTagRegistry.MAGNETIC_ITEMS)) {
            if (!player.isCreative()) {
                itemstack.hurtAndBreak(1, player, (player1) -> {
                    player1.broadcastBreakEvent(player1.getUsedItemHand());
                });
            }
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    public boolean isValidRepairItem(ItemStack item, ItemStack repairItem) {
        return repairItem.is(ACBlockRegistry.PACKED_GALENA.get().asItem()) || super.isValidRepairItem(item, repairItem);
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity player, int useTimeLeft) {
        super.releaseUsing(stack, level, player, useTimeLeft);
        if(player instanceof Player realPlayer){
            realPlayer.getCooldowns().addCooldown(this, 5);

        }
        AlexsCaves.PROXY.clearSoundCacheFor(player);
        player.playSound(ACSoundRegistry.GALENA_GAUNTLET_STOP.get());
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int timeUsing) {
        super.onUseTick(level, living, stack, timeUsing);
        InteractionHand otherHand = InteractionHand.MAIN_HAND;
        if (living.getItemInHand(InteractionHand.OFF_HAND) == stack) {
            otherHand = InteractionHand.MAIN_HAND;
        }
        if (living.getItemInHand(InteractionHand.MAIN_HAND) == stack) {
            otherHand = InteractionHand.OFF_HAND;
        }
        AlexsCaves.PROXY.playWorldSound(living, (byte) 11);
        ItemStack otherStack = living.getItemInHand(otherHand);
        boolean otherMagneticWeaponsInUse = false;
        boolean crystallization = stack.getEnchantmentLevel(ACEnchantmentRegistry.CRYSTALLIZATION.get()) > 0;
        if (otherStack.is(crystallization ? ACTagRegistry.GALENA_GAUNTLET_CRYSTALLIZATION_ITEMS : ACTagRegistry.MAGNETIC_ITEMS)) {
            for(MagneticWeaponEntity magneticWeapon : level.getEntitiesOfClass(MagneticWeaponEntity.class, living.getBoundingBox().inflate(64, 64, 64))){
                Entity controller = magneticWeapon.getController();
                if(controller != null && controller.is(living)){
                    otherMagneticWeaponsInUse = true;
                    break;
                }
            }
            if(!otherMagneticWeaponsInUse) {
                ItemStack copy = otherStack.copy();
                otherStack.setCount(0);
                MagneticWeaponEntity magneticWeapon = ACEntityRegistry.MAGNETIC_WEAPON.get().create(level);
                magneticWeapon.setItemStack(copy);
                magneticWeapon.setPos(living.position().add(0, 1, 0));
                magneticWeapon.setControllerUUID(living.getUUID());
                level.addFreshEntity(magneticWeapon);
            }
        } else if (!otherStack.isEmpty()) {
            living.stopUsingItem();
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        boolean using = entity instanceof LivingEntity living && living.getUseItem().equals(stack);
        if (level.isClientSide) {
            int useTime = getUseTime(stack);
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.getInt("PrevUseTime") != tag.getInt("UseTime")) {
                tag.putInt("PrevUseTime", getUseTime(stack));
            }
            if (using && useTime < 5.0F) {
                setUseTime(stack, useTime + 1);
            }
            if (!using && useTime > 0.0F) {
                setUseTime(stack, useTime - 1);
            }
        }
    }

    public static void setUseTime(ItemStack stack, int useTime) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("PrevUseTime", getUseTime(stack));
        tag.putInt("UseTime", useTime);
    }

    public static int getUseTime(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null ? compoundtag.getInt("UseTime") : 0;
    }

    public static float getLerpedUseTime(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getTag();
        float prev = compoundtag != null ? (float) compoundtag.getInt("PrevUseTime") : 0F;
        float current = compoundtag != null ? (float) compoundtag.getInt("UseTime") : 0F;
        return prev + f * (current - prev);
    }

    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.OFFHAND;
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(ACItemRegistry.GALENA_GAUNTLET.get()) || !newStack.is(ACItemRegistry.GALENA_GAUNTLET.get());
    }
}
