package com.github.alexmodguy.alexscaves.client.render.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.*;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.GalenaGauntletItem;
import com.github.alexmodguy.alexscaves.server.item.RaygunItem;
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
    private static final ResourceLocation SIREN_LIGHT_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/siren_light.png");
    private static final ResourceLocation SIREN_LIGHT_COLOR_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/siren_light_color.png");
    private static final SirenLightModel SIREN_LIGHT_MODEL = new SirenLightModel();
    private static final ResourceLocation RAYGUN_TEXTURE = new ResourceLocation("alexscaves:textures/entity/raygun.png");
    private static final ResourceLocation RAYGUN_ACTIVE_TEXTURE = new ResourceLocation("alexscaves:textures/entity/raygun_active.png");
    private static final RaygunModel RAYGUN_MODEL = new RaygunModel();
    private static final ResourceLocation SEA_STAFF_TEXTURE = new ResourceLocation("alexscaves:textures/entity/deep_one/sea_staff.png");
    private static final SeaStaffModel SEA_STAFF_MODEL = new SeaStaffModel();
    private static final ResourceLocation ORTHOLANCE_TEXTURE = new ResourceLocation("alexscaves:textures/entity/deep_one/ortholance.png");
    private static final OrtholanceModel ORTHOLANCE_MODEL = new OrtholanceModel();
    private static final ResourceLocation COPPER_VALVE_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/copper_valve.png");
    private static final CopperValveModel COPPER_VALVE_MODEL = new CopperValveModel();

    private static final BeholderModel BEHOLDER_MODEL = new BeholderModel();
    private static final ResourceLocation BEHOLDER_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/beholder.png");
    private static final ResourceLocation BEHOLDER_TEXTURE_EYE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/beholder_eye.png");


    public ACItemstackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ClientLevel level = Minecraft.getInstance().level;
        float partialTick = Minecraft.getInstance().getPartialTick();
        boolean heldIn3d = transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        boolean left = transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        if (itemStackIn.is(ACItemRegistry.GALENA_GAUNTLET.get())) {
            poseStack.pushPose();
            poseStack.translate(0, 0F, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.YP.rotationDegrees(-180));
            float openAmount = GalenaGauntletItem.getLerpedUseTime(itemStackIn, partialTick) / 5F;
            float closeAmount = 1F - openAmount;
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            if (left || transformType == ItemDisplayContext.GUI) {
                GALENA_GAUNTLET_LEFT_MODEL.setupAnim(null, openAmount, 0, ageInTicks, 0, 0);
                GALENA_GAUNTLET_LEFT_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(GALENA_GAUNTLET_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                GALENA_GAUNTLET_LEFT_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_BLUE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, openAmount);
                GALENA_GAUNTLET_LEFT_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_RED_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, closeAmount);
            } else {
                GALENA_GAUNTLET_RIGHT_MODEL.setupAnim(null, openAmount, 0, ageInTicks, 0, 0);
                GALENA_GAUNTLET_RIGHT_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(GALENA_GAUNTLET_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                GALENA_GAUNTLET_RIGHT_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_BLUE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, openAmount);
                GALENA_GAUNTLET_RIGHT_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_RED_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, closeAmount);
            }
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.RESISTOR_SHIELD.get())) {
            poseStack.pushPose();
            poseStack.translate(0, 0.25F, 0.125F);
            float useTime = ResistorShieldItem.getLerpedUseTime(itemStackIn, partialTick);
            float useProgress = Math.min(10F, useTime) / 10F;
            float useProgressTurn = Math.min(useProgress * 4F, 1F);
            float useProgressUp = (float) Math.sin(useProgress * Math.PI);
            float switchProgress = ResistorShieldItem.getLerpedSwitchTime(itemStackIn, partialTick) / 5F;
            float leftOffset = left ? -1F : 1F;
            if (transformType.firstPerson()) {
                poseStack.translate(useProgressTurn * 0.2F * leftOffset, useProgressUp, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(useProgressTurn * -10));
            } else if (heldIn3d) {
                poseStack.translate(useProgressTurn * 0.4F * leftOffset, useProgress * -0.1F, useProgressTurn * -0.2F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(useProgressTurn * 10 * leftOffset));
                poseStack.mulPose(Axis.YP.rotationDegrees(useProgressTurn * 80 * leftOffset));
            }
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            RESISTOR_SHIELD_MODEL.setupAnim(null, useProgress, switchProgress, 0, 0, 0);
            RESISTOR_SHIELD_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(RESISTOR_SHIELD_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            RESISTOR_SHIELD_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(RESISTOR_SHIELD_RED_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, switchProgress);
            RESISTOR_SHIELD_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(RESISTOR_SHIELD_BLUE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F - switchProgress);
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.PRIMITIVE_CLUB.get())) {
            poseStack.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.PRIMITIVE_CLUB_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -1.15F, -0.1F);
                if (transformType.firstPerson()) {
                    poseStack.translate(0, 0.1F, 0);
                    poseStack.scale(0.8F, 0.8F, 0.8F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(PRIMITIVE_CLUB_TEXTURE), false, itemStackIn.hasFoil());
                PRIMITIVE_CLUB_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, poseStack, bufferIn, level, 0);
            }
        }

        if (itemStackIn.is(ACItemRegistry.LIMESTONE_SPEAR.get())) {
            poseStack.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.LIMESTONE_SPEAR_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -0.85F, -0.1F);
                if (transformType.firstPerson()) {
                    poseStack.translate(0, 0.5F, 0F);
                    poseStack.scale(0.75F, 0.75F, 0.75F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(LIMESTONE_SPEAR_TEXTURE), false, itemStackIn.hasFoil());
                LIMESTONE_SPEAR_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, poseStack, bufferIn, level, 0);
            }
        }

        if (itemStackIn.is(ACBlockRegistry.SIREN_LIGHT.get().asItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            SIREN_LIGHT_MODEL.resetToDefaultPose();
            SIREN_LIGHT_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(SIREN_LIGHT_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            SIREN_LIGHT_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityTranslucent(SIREN_LIGHT_COLOR_TEXTURE)), combinedLightIn, combinedOverlayIn, 0.0F, 1.0F, 0.0F, 1.0F);
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.RAYGUN.get())) {
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            float useAmount = RaygunItem.getLerpedUseTime(itemStackIn, partialTick) / 5F;
            float pulseAlpha = useAmount * (0.25F + 0.25F * (float) (1F + Math.sin(ageInTicks * 0.8F)));
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.pushPose();
            poseStack.scale(0.9F, 0.9F, 0.9F);
            RAYGUN_MODEL.setupAnim(null, useAmount, ageInTicks,  0, 0, 0);
            RAYGUN_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(RAYGUN_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            RAYGUN_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(RAYGUN_ACTIVE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, pulseAlpha);
            poseStack.popPose();
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.SEA_STAFF.get())) {
            poseStack.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.SEA_STAFF_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -0.5F, 0);
                if (transformType.firstPerson()) {
                    poseStack.scale(0.6F, 0.6F, 0.6F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(SEA_STAFF_TEXTURE), false, itemStackIn.hasFoil());
                SEA_STAFF_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, poseStack, bufferIn, level, 0);
            }
        }

        if (itemStackIn.is(ACItemRegistry.ORTHOLANCE.get())) {
            poseStack.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.ORTHOLANCE_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -1.1F, 0);
                if (transformType.firstPerson()) {
                    poseStack.scale(0.6F, 1F, 0.6F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(ORTHOLANCE_TEXTURE), false, itemStackIn.hasFoil());
                ORTHOLANCE_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, poseStack, bufferIn, level, 0);
            }
        }

        if (itemStackIn.is(ACBlockRegistry.COPPER_VALVE.get().asItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            COPPER_VALVE_MODEL.resetToDefaultPose();
            COPPER_VALVE_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(COPPER_VALVE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }

        if (itemStackIn.is(ACBlockRegistry.BEHOLDER.get().asItem())) {
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            BEHOLDER_MODEL.setupAnim(null, 0.0F, 45F, ageInTicks, 0, 0);
            BEHOLDER_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(BEHOLDER_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            BEHOLDER_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.eyes(BEHOLDER_TEXTURE_EYE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }
}
