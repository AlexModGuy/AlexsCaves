package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.message.ArmorKeyMessage;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;

public class DarknessArmorItem extends ArmorItem implements CustomArmorPostRender, KeybindUsingArmor {

    private static final int MAX_CHARGE = 1000;

    public DarknessArmorItem(ArmorMaterial armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties());
    }

    private static boolean canChargeUp(LivingEntity entity, boolean creative){
        return (!DarknessIncarnateEffect.isInLight(entity, 11) || creative && entity instanceof Player player && player.isCreative()) && entity.getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.HOOD_OF_DARKNESS.get())  && !entity.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get());
    }

    public static boolean canChargeUp(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        return tag == null || tag.getBoolean("CanCharge");
    }

    public static boolean hasMeter(Player player){
        return player.getItemBySlot(EquipmentSlot.CHEST).is(ACItemRegistry.CLOAK_OF_DARKNESS.get()) && player.getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.HOOD_OF_DARKNESS.get()) && !player.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get());
    }

    public static float getMeterProgress(ItemStack cloak) {
        CompoundTag tag = cloak.getTag();
        if(tag == null){
            return 0.0F;
        }else{
            return tag.getInt("CloakCharge") / (float)MAX_CHARGE;
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if(stack.is(ACItemRegistry.CLOAK_OF_DARKNESS.get())){
            if (!level.isClientSide) {
                CompoundTag tag = stack.getOrCreateTag();
                int charge = tag.getInt("CloakCharge");
                boolean flag = false;
                if(charge < MAX_CHARGE && canChargeUp(stack)){
                    charge += 10;
                    tag.putInt("CloakCharge", charge);
                    flag = true;
                }
                if(flag){
                    AlexsCaves.sendNonLocal(new UpdateItemTagMessage(player.getId(), stack), (ServerPlayer) player);
                }
            }
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        if(stack.is(ACItemRegistry.CLOAK_OF_DARKNESS.get()) && entity instanceof LivingEntity living) {
            if(living.getItemBySlot(EquipmentSlot.CHEST) == stack){
                CompoundTag tag = stack.getOrCreateTag();
                if(!level.isClientSide) {
                    long lastLightTimestamp = tag.getLong("LastLightTimestamp");
                    if(lastLightTimestamp <= 0 || level.getGameTime() - lastLightTimestamp > 10) {
                        tag.putLong("LastLightTimestamp", level.getGameTime());
                        tag.putBoolean("CanCharge", canChargeUp(living, true));
                    }
                }else if(AlexsCaves.PROXY.getClientSidePlayer() == entity && getMeterProgress(stack) >= 1.0F && AlexsCaves.PROXY.isKeyDown(2)){
                    AlexsCaves.sendMSGToServer(new ArmorKeyMessage(EquipmentSlot.CHEST.ordinal(), living.getId(), 2));
                    onKeyPacket(living, stack, 2);
                }
            }
        }
    }

    public void onKeyPacket(Entity wearer, ItemStack itemStack, int key){
        if(wearer instanceof LivingEntity living && canChargeUp(living, false)){
            itemStack.getOrCreateTag().putInt("CloakCharge", 0);
            living.addEffect(new MobEffectInstance(ACEffectRegistry.DARKNESS_INCARNATE.get(), 200, 0, false, false, false));
        }else if(wearer instanceof Player player && !wearer.level().isClientSide){
            player.displayClientMessage(Component.translatable("item.alexscaves.cloak_of_darkness.requires_darkness"), true);
        }
    }


    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return AlexsCaves.MODID + ":textures/armor/darkness_armor.png";
    }

    @Override
    public boolean stopDefaultRendering() {
        return true;
    }
}
