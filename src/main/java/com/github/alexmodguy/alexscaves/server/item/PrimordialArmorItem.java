package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;

public class PrimordialArmorItem extends ArmorItem {

    public PrimordialArmorItem(ArmorMaterial armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties());
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if(slot == EquipmentSlot.LEGS){
            return AlexsCaves.MODID + ":textures/armor/primordial_armor_1.png";
        }else{
            return AlexsCaves.MODID + ":textures/armor/primordial_armor_0.png";
        }
    }

    public static int getExtraSaturationFromArmor(LivingEntity entity) {
        int i = 0;
        if(entity.getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.PRIMORDIAL_HELMET.get())){
            i++;
        }
        if(entity.getItemBySlot(EquipmentSlot.CHEST).is(ACItemRegistry.PRIMORDIAL_TUNIC.get())){
            i++;
        }
        if(entity.getItemBySlot(EquipmentSlot.LEGS).is(ACItemRegistry.PRIMORDIAL_PANTS.get())){
            i++;
        }
        return i;
    }
}
