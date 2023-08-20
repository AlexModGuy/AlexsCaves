package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.client.render.item.ACArmorRenderProperties;
import com.github.alexmodguy.alexscaves.server.item.CustomArmorPostRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    private ItemStack lastArmorItemStackRendered = ItemStack.EMPTY;

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V"},
            at = @At(value = "HEAD"),
            remap = true
    )
    private void ac_renderArmorPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, LivingEntity livingEntity, EquipmentSlot equipmentSlot, int i, HumanoidModel model, CallbackInfo ci) {
        lastArmorItemStackRendered = livingEntity.getItemBySlot(equipmentSlot);
    }
        @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/resources/ResourceLocation;)V"},
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false //FORGE METHOD
    )
    private void ac_renderModel_pre(PoseStack poseStack, MultiBufferSource multiBufferSource, int light, ArmorItem armorItem, Model armorModel, boolean legs, float r, float g, float b, ResourceLocation texture, CallbackInfo ci) {
        if(armorItem instanceof CustomArmorPostRender customArmorPostRender){
            ACArmorRenderProperties.onPreRenderArmor(poseStack, multiBufferSource, light, lastArmorItemStackRendered, armorItem, armorModel, legs, texture);
            if(customArmorPostRender.stopDefaultRendering()){
                ci.cancel();
            }
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/resources/ResourceLocation;)V"},
            at = @At(value = "TAIL"),
            cancellable = true,
            remap = false //FORGE METHOD
    )
    private void ac_renderModel_post(PoseStack poseStack, MultiBufferSource multiBufferSource, int light, ArmorItem armorItem, Model armorModel, boolean legs, float r, float g, float b, ResourceLocation texture, CallbackInfo ci) {
        if(armorItem instanceof CustomArmorPostRender){
            ACArmorRenderProperties.onPostRenderArmor(poseStack, multiBufferSource, light, lastArmorItemStackRendered, armorItem, armorModel, legs, texture);

        }
    }
}
