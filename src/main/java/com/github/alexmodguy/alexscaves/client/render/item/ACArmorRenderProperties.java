package com.github.alexmodguy.alexscaves.client.render.item;

import com.github.alexmodguy.alexscaves.client.model.layered.ACModelLayers;
import com.github.alexmodguy.alexscaves.client.model.layered.DivingArmorModel;
import com.github.alexmodguy.alexscaves.client.model.layered.HazmatArmorModel;
import com.github.alexmodguy.alexscaves.client.model.layered.PrimordialArmorModel;
import com.github.alexmodguy.alexscaves.server.item.DivingArmorItem;
import com.github.alexmodguy.alexscaves.server.item.HazmatArmorItem;
import com.github.alexmodguy.alexscaves.server.item.PrimordialArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ACArmorRenderProperties implements IClientItemExtensions {

    private static boolean init;
    public static PrimordialArmorModel PRIMORDIAL_ARMOR_MODEL;
    public static HazmatArmorModel HAZMAT_ARMOR_MODEL;
    public static DivingArmorModel DIVING_ARMOR_MODEL;

    public static void initializeModels() {
        init = true;
        PRIMORDIAL_ARMOR_MODEL = new PrimordialArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ACModelLayers.PRIMORDIAL_ARMOR));
        HAZMAT_ARMOR_MODEL = new HazmatArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ACModelLayers.HAZMAT_ARMOR));
        DIVING_ARMOR_MODEL = new DivingArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ACModelLayers.DIVING_ARMOR));
    }

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
        if (!init) {
            initializeModels();
        }
        if (itemStack.getItem() instanceof PrimordialArmorItem) {
            return PRIMORDIAL_ARMOR_MODEL.withAnimations(entityLiving);
        }
        if (itemStack.getItem() instanceof HazmatArmorItem) {
            return HAZMAT_ARMOR_MODEL.withAnimations(entityLiving);
        }
        if (itemStack.getItem() instanceof DivingArmorItem) {
            return DIVING_ARMOR_MODEL;
        }
        return _default;
    }
}
