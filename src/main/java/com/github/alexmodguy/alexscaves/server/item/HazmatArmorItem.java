package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
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

public class HazmatArmorItem extends ArmorItem {

    public HazmatArmorItem(ArmorMaterial armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties());
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (stack.is(ACItemRegistry.HAZMAT_MASK.get()) && Math.cos(player.tickCount * 0.05F) >= 0.9F) {
            Vec3 eyes = player.getEyePosition();
            if (level.random.nextBoolean()) {
                Vec3 leftOffset = new Vec3(0.25F, -0.3F, 0.25F).xRot((float) Math.toRadians(-player.getXRot())).yRot((float) Math.toRadians(-player.getYHeadRot()));
                level.addParticle(ACParticleRegistry.HAZMAT_BREATHE.get(), eyes.x + leftOffset.x, eyes.y + leftOffset.y, eyes.z + leftOffset.z, (level.random.nextFloat() - 0.5F) * 0.1F, (level.random.nextFloat() - 0.5F) * 0.1F, (level.random.nextFloat() - 0.5F) * 0.1F);
            }
            if (level.random.nextBoolean()) {
                Vec3 rightOffset = new Vec3(-0.25F, -0.3F, 0.25F).xRot((float) Math.toRadians(-player.getXRot())).yRot((float) Math.toRadians(-player.getYHeadRot()));
                level.addParticle(ACParticleRegistry.HAZMAT_BREATHE.get(), eyes.x + rightOffset.x, eyes.y + rightOffset.y, eyes.z + rightOffset.z, (level.random.nextFloat() - 0.5F) * 0.1F, (level.random.nextFloat() - 0.5F) * 0.1F, (level.random.nextFloat() - 0.5F) * 0.1F);
            }
        }
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (slot == EquipmentSlot.LEGS) {
            return AlexsCaves.MODID + ":textures/armor/hazmat_suit_1.png";
        } else {
            return AlexsCaves.MODID + ":textures/armor/hazmat_suit_0.png";
        }
    }

    public static int getWornAmount(LivingEntity entity) {
        int i = 0;
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ACTagRegistry.HAZMAT_PROT.get())) {
            i++;
        }
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(ACTagRegistry.HAZMAT_PROT.get())) {
            i++;
        }
        if (entity.getItemBySlot(EquipmentSlot.LEGS).is(ACTagRegistry.HAZMAT_PROT.get())) {
            i++;
        }
        if (entity.getItemBySlot(EquipmentSlot.FEET).is(ACTagRegistry.HAZMAT_PROT.get())) {
            i++;
        }
        return i;
    }
}
