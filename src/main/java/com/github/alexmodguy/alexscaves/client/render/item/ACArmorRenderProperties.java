package com.github.alexmodguy.alexscaves.client.render.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.layered.*;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.item.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ACArmorRenderProperties implements IClientItemExtensions {

    private static final ResourceLocation DARKNESS_ARMOR_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/darkness_armor_glow.png");
    private static boolean init;
    public static PrimordialArmorModel PRIMORDIAL_ARMOR_MODEL;
    public static HazmatArmorModel HAZMAT_ARMOR_MODEL;
    public static DivingArmorModel DIVING_ARMOR_MODEL;
    public static DarknessArmorModel DARKNESS_ARMOR_MODEL;
    public static RainbounceArmorModel RAINBOUNCE_ARMOR_MODEL;
    public static GingerbreadArmorModel GINGERBREAD_ARMOR_MODEL;


    public static void initializeModels() {
        init = true;
        PRIMORDIAL_ARMOR_MODEL = new PrimordialArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ACModelLayers.PRIMORDIAL_ARMOR));
        HAZMAT_ARMOR_MODEL = new HazmatArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ACModelLayers.HAZMAT_ARMOR));
        DIVING_ARMOR_MODEL = new DivingArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ACModelLayers.DIVING_ARMOR));
        DARKNESS_ARMOR_MODEL = new DarknessArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ACModelLayers.DARKNESS_ARMOR));
        RAINBOUNCE_ARMOR_MODEL = new RainbounceArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ACModelLayers.RAINBOUNCE_ARMOR));
        GINGERBREAD_ARMOR_MODEL = new GingerbreadArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ACModelLayers.GINGERBREAD_ARMOR));
    }

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
        if (!init) {
            initializeModels();
        }
        if (itemStack.getItem() instanceof PrimordialArmorItem) {
            return entityLiving == null ? PRIMORDIAL_ARMOR_MODEL : PRIMORDIAL_ARMOR_MODEL.withAnimations(entityLiving);
        }
        if (itemStack.getItem() instanceof HazmatArmorItem) {
            return entityLiving == null ? HAZMAT_ARMOR_MODEL : HAZMAT_ARMOR_MODEL.withAnimations(entityLiving);
        }
        if (itemStack.getItem() instanceof DivingArmorItem) {
            return DIVING_ARMOR_MODEL;
        }
        if (itemStack.getItem() instanceof DarknessArmorItem) {
            return entityLiving == null ? DARKNESS_ARMOR_MODEL : DARKNESS_ARMOR_MODEL.withAnimations(entityLiving);
        }
        if (itemStack.getItem() instanceof RainbounceBootsItem) {
            return RAINBOUNCE_ARMOR_MODEL;
        }
        if (itemStack.getItem() instanceof GingerbreadArmorItem) {
            return GINGERBREAD_ARMOR_MODEL;
        }
        return _default;
    }

    public static void renderCustomArmor(PoseStack poseStack, MultiBufferSource multiBufferSource, int light, ItemStack itemStack, ArmorItem armorItem, Model armorModel, boolean legs, ResourceLocation texture) {
        if(armorItem.getMaterial() == ACItemRegistry.DARKNESS_ARMOR_MATERIAL){
            VertexConsumer vertexconsumer1 = itemStack.hasFoil() ? VertexMultiConsumer.create(multiBufferSource.getBuffer(RenderType.entityGlintDirect()), multiBufferSource.getBuffer(RenderType.entityTranslucent(texture))) : multiBufferSource.getBuffer(RenderType.entityTranslucent(texture));
            armorModel.renderToBuffer(poseStack, vertexconsumer1, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            VertexConsumer vertexconsumer2 = multiBufferSource.getBuffer(ACRenderTypes.getEyesAlphaEnabled(DARKNESS_ARMOR_GLOW));
            armorModel.renderToBuffer(poseStack, vertexconsumer2, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }else if(armorItem.getMaterial() == ACItemRegistry.RAINBOUNCE_ARMOR_MATERIAL){
            VertexConsumer vertexconsumer1 = itemStack.hasFoil() ? VertexMultiConsumer.create(multiBufferSource.getBuffer(RenderType.entityGlintDirect()), multiBufferSource.getBuffer(ACRenderTypes.getTeslaBulb(texture))) : multiBufferSource.getBuffer(ACRenderTypes.getTeslaBulb(texture));
            armorModel.renderToBuffer(poseStack, vertexconsumer1, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
