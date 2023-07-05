package com.github.alexmodguy.alexscaves.client.render.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.*;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem;
import com.github.alexmodguy.alexscaves.server.item.ResistorShieldItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ACItemstackRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation GALENA_GAUNTLET_TEXTURE = new ResourceLocation("alexscaves:textures/entity/galena_gauntlet.png");
    private static final ResourceLocation GALENA_GAUNTLET_RED_TEXTURE = new ResourceLocation("alexscaves:textures/entity/galena_gauntlet_red.png");
    private static final ResourceLocation GALENA_GAUNTLET_BLUE_TEXTURE = new ResourceLocation("alexscaves:textures/entity/galena_gauntlet_blue.png");
    private static final GalenaGauntletModel GALENA_GAUNTLET_RIGHT_MODEL = new GalenaGauntletModel(false);
    private static final GalenaGauntletModel GALENA_GAUNTLET_LEFT_MODEL = new GalenaGauntletModel(true);
    private static final ResourceLocation RESISTOR_SHIELD_TEXTURE = new ResourceLocation("alexscaves:textures/entity/resistor_shield.png");
    private static final ResourceLocation RESISTOR_SHIELD_RED_TEXTURE = new ResourceLocation("alexscaves:textures/entity/resistor_shield_red.png");
    private static final ResourceLocation RESISTOR_SHIELD_BLUE_TEXTURE = new ResourceLocation("alexscaves:textures/entity/resistor_shield_blue.png");
    private static final ResistorShieldModel RESISTOR_SHIELD_MODEL = new ResistorShieldModel();
    private static final ResourceLocation PRIMITIVE_CLUB_TEXTURE = new ResourceLocation("alexscaves:textures/entity/primitive_club.png");
    private static final PrimitiveClubModel PRIMITIVE_CLUB_MODEL = new PrimitiveClubModel();
    private static final ResourceLocation LIMESTONE_SPEAR_TEXTURE = new ResourceLocation("alexscaves:textures/entity/limestone_spear.png");
    private static final LimestoneSpearModel LIMESTONE_SPEAR_MODEL = new LimestoneSpearModel();
    private static final ResourceLocation SEA_STAFF_TEXTURE = new ResourceLocation("alexscaves:textures/entity/deep_one/sea_staff.png");
    private static final SeaStaffModel SEA_STAFF_MODEL = new SeaStaffModel();
    private static final ResourceLocation ORTHOLANCE_TEXTURE = new ResourceLocation("alexscaves:textures/entity/deep_one/ortholance.png");
    private static final OrtholanceModel ORTHOLANCE_MODEL = new OrtholanceModel();
    private static final ResourceLocation COPPER_VALVE_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/copper_valve.png");
    private static final CopperValveModel COPPER_VALVE_MODEL = new CopperValveModel();

    public ACItemstackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ClientLevel level = Minecraft.getInstance().level;
        float partialTick = Minecraft.getInstance().getPartialTick();
        boolean heldIn3d = transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        boolean left = transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        if (itemStackIn.is(ACItemRegistry.GALENA_GAUNTLET.get())) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, 0F, 0);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180));
            float openAmount = GalenaGauntletItem.getLerpedUseTime(itemStackIn, partialTick) / 5F;
            float closeAmount = 1F - openAmount;
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            if(left || transformType == ItemDisplayContext.GUI){
                GALENA_GAUNTLET_LEFT_MODEL.setupAnim(null, openAmount, 0, ageInTicks, 0, 0);
                GALENA_GAUNTLET_LEFT_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(GALENA_GAUNTLET_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                GALENA_GAUNTLET_LEFT_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_BLUE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, openAmount);
                GALENA_GAUNTLET_LEFT_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_RED_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, closeAmount);
            }else{
                GALENA_GAUNTLET_RIGHT_MODEL.setupAnim(null, openAmount, 0, ageInTicks, 0, 0);
                GALENA_GAUNTLET_RIGHT_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(GALENA_GAUNTLET_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                GALENA_GAUNTLET_RIGHT_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_BLUE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, openAmount);
                GALENA_GAUNTLET_RIGHT_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_RED_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, closeAmount);
            }
            matrixStackIn.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.RESISTOR_SHIELD.get())) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, 0.25F, 0.125F);
            float useTime = ResistorShieldItem.getLerpedUseTime(itemStackIn, partialTick);
            float useProgress = Math.min(10F, useTime) / 10F;
            float useProgressTurn = Math.min(useProgress * 4F, 1F);
            float useProgressUp = (float)Math.sin(useProgress * Math.PI);
            float switchProgress = ResistorShieldItem.getLerpedSwitchTime(itemStackIn, partialTick) / 5F;
            float leftOffset = left ? -1F : 1F;
            if(transformType.firstPerson()){
                matrixStackIn.translate(useProgressTurn * 0.2F * leftOffset, useProgressUp, 0);
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(useProgressTurn * -10));
            }else if (heldIn3d) {
                matrixStackIn.translate(useProgressTurn * 0.4F * leftOffset, useProgress * -0.1F, useProgressTurn * -0.2F);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(useProgressTurn * 10 * leftOffset));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(useProgressTurn * 80 * leftOffset));
            }
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
            RESISTOR_SHIELD_MODEL.setupAnim(null, useProgress, switchProgress, 0, 0, 0);
            RESISTOR_SHIELD_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(RESISTOR_SHIELD_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            RESISTOR_SHIELD_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(RESISTOR_SHIELD_RED_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, switchProgress);
            RESISTOR_SHIELD_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(RESISTOR_SHIELD_BLUE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F - switchProgress);
            matrixStackIn.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.PRIMITIVE_CLUB.get())) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.PRIMITIVE_CLUB_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
                matrixStackIn.translate(0, -1.15F, -0.1F);
                if(transformType.firstPerson()){
                    matrixStackIn.translate(0, 0.1F, 0);
                    matrixStackIn.scale(0.8F, 0.8F, 0.8F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(PRIMITIVE_CLUB_TEXTURE), false, itemStackIn.hasFoil());
                PRIMITIVE_CLUB_MODEL.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }

        if (itemStackIn.is(ACItemRegistry.LIMESTONE_SPEAR.get())) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.LIMESTONE_SPEAR_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
                matrixStackIn.translate(0, -0.85F, -0.1F);
                if(transformType.firstPerson()){
                    matrixStackIn.translate(0, 0.5F, 0F);
                    matrixStackIn.scale(0.75F, 0.75F, 0.75F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(LIMESTONE_SPEAR_TEXTURE), false, itemStackIn.hasFoil());
                LIMESTONE_SPEAR_MODEL.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }

        if (itemStackIn.is(ACItemRegistry.SEA_STAFF.get())) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.SEA_STAFF_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
                matrixStackIn.translate(0, -0.5F, 0);
                if(transformType.firstPerson()){
                    matrixStackIn.scale(0.6F, 0.6F, 0.6F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(SEA_STAFF_TEXTURE), false, itemStackIn.hasFoil());
                SEA_STAFF_MODEL.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }

        if (itemStackIn.is(ACItemRegistry.ORTHOLANCE.get())) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.ORTHOLANCE_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
                matrixStackIn.translate(0, -1.1F, 0);
                if(transformType.firstPerson()){
                    matrixStackIn.scale(0.6F, 1F, 0.6F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(ORTHOLANCE_TEXTURE), false, itemStackIn.hasFoil());
                ORTHOLANCE_MODEL.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }

        if (itemStackIn.is(ACBlockRegistry.COPPER_VALVE.get().asItem())) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
            COPPER_VALVE_MODEL.resetToDefaultPose();
            COPPER_VALVE_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(COPPER_VALVE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
    }
}
