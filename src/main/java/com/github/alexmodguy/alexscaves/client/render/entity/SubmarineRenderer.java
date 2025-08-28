package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.SubmarineModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class SubmarineRenderer extends EntityRenderer<SubmarineEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine.png");
    private static final ResourceLocation TEXTURE_EXPOSED = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_exposed.png");
    private static final ResourceLocation TEXTURE_WEATHERED = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_weathered.png");
    private static final ResourceLocation TEXTURE_OXIDIZED = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_oxidized.png");
    private static final ResourceLocation TEXTURE_NEW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_new.png");
    private static final ResourceLocation TEXTURE_LOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_low.png");
    private static final ResourceLocation TEXTURE_MEDIUM = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_medium.png");
    private static final ResourceLocation TEXTURE_HIGH = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_high.png");
    private static final ResourceLocation TEXTURE_CRITICAL = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_critical.png");
    private static final ResourceLocation TEXTURE_BUTTONS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_buttons.png");
    private static final ResourceLocation TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/submarine/submarine_glow.png");
    private static SubmarineModel MODEL = new SubmarineModel();
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    public SubmarineRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.0F;
    }

    public void render(SubmarineEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int lightIn) {
        if (!isFirstPersonFloodlightsMode(entity)) {
            renderSubmarine(entity, partialTicks, poseStack, source, lightIn, true);
            super.render(entity, entityYaw, partialTicks, poseStack, source, lightIn);
        }
    }

    public static boolean isFirstPersonFloodlightsMode(SubmarineEntity entity) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        return player.isPassengerOfSameVehicle(entity) && Minecraft.getInstance().options.getCameraType().isFirstPerson() && entity.areLightsOn();
    }

    public static void renderSubFirstPerson(SubmarineEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource source) {
        renderSubmarine(entity, partialTicks, poseStack, source, LevelRenderer.getLightColor(entity.level(), entity.blockPosition()), false);
    }

    public static void renderSubmarine(SubmarineEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource source, int lightIn, boolean maskWater) {
        Player player = Minecraft.getInstance().player;
        float ageInTicks = entity.tickCount + partialTicks;
        float submarineYaw = entity.getViewYRot(partialTicks);
        float submarinePitch = entity.getViewXRot(partialTicks);
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - submarineYaw));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.XN.rotationDegrees(submarinePitch));
        poseStack.translate(0, 0, 0);
        if (entity.getWaterHeight() > 0 && entity.getWaterHeight() < 1.6F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (Math.sin(ageInTicks * 0.1F) * 0.5F)));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (Math.sin(ageInTicks * 0.1F + 1.3F) * 0.5F)));
        }
        poseStack.pushPose();
        MODEL.setupAnim(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
        for (Entity passenger : entity.getPassengers()) {
            if (passenger == player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                continue;
            }
            AlexsCaves.PROXY.releaseRenderingEntity(passenger.getUUID());
            poseStack.pushPose();
            poseStack.translate(0, 0.65F, -0.75F);
            poseStack.mulPose(Axis.XN.rotationDegrees(180F));
            poseStack.mulPose(Axis.YN.rotationDegrees(360 - submarineYaw));
            renderPassenger(passenger, 0, 0, 0, 0, partialTicks, poseStack, source, lightIn);
            poseStack.popPose();
            AlexsCaves.PROXY.blockRenderingEntity(passenger.getUUID());
        }
        VertexConsumer textureBuffer = source.getBuffer(RenderType.entityCutoutNoCull(getSubmarineBaseTexture(entity)));
        MODEL.renderToBuffer(poseStack, textureBuffer, lightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        VertexConsumer damageBuffer = source.getBuffer(RenderType.entityTranslucent(getSubmarineDamageTexture(entity)));
        MODEL.renderToBuffer(poseStack, damageBuffer, lightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (entity.getDamageLevel() <= 3) {
            VertexConsumer buttonsBuffer = source.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_BUTTONS));
            MODEL.renderToBuffer(poseStack, buttonsBuffer, lightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, entity.getSonarFlashAmount(partialTicks));
            if (entity.areLightsOn() && entity.isVehicle()) {
                VertexConsumer glowBuffer = source.getBuffer(RenderType.eyes(TEXTURE_GLOW));
                MODEL.renderToBuffer(poseStack, glowBuffer, lightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        if (maskWater) {
            VertexConsumer waterMask = source.getBuffer(ACRenderTypes.getSubmarineMask());
            MODEL.setupWaterMask(entity, partialTicks);
            MODEL.getWaterMask().render(poseStack, waterMask, lightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (!isFirstPersonFloodlightsMode(entity) && entity.areLightsOn() && entity.isVehicle()) {
            Entity first = entity.getFirstPassenger();
            float xRot = 0;
            float yRot = 0;
            if (first instanceof Player firstPlayer) {
                float headYaw = (firstPlayer.yHeadRotO + (firstPlayer.getYHeadRot() - firstPlayer.yHeadRotO) * partialTicks);
                xRot = firstPlayer.getViewXRot(partialTicks) + submarinePitch;
                yRot = Mth.approachDegrees(submarineYaw, headYaw, 60);
            }
            float length = 4.5F;
            float width = 0.45F;
            poseStack.pushPose();
            poseStack.translate(0, 0.75F, -2.4F);
            poseStack.mulPose(Axis.YN.rotationDegrees(submarineYaw));
            poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
            poseStack.mulPose(Axis.XN.rotationDegrees(90 - xRot));
            poseStack.scale(3, 1, 1);
            poseStack.translate(0, -1, 0F);
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f1 = posestack$pose.pose();
            Matrix3f matrix3f1 = posestack$pose.normal();
            VertexConsumer lightConsumer = source.getBuffer(ACRenderTypes.getSubmarineLights());
            shineOriginVertex(lightConsumer, matrix4f1, matrix3f1, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            shineRightCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            poseStack.popPose();
        }

        poseStack.popPose();
        poseStack.popPose();
    }

    private static ResourceLocation getSubmarineDamageTexture(SubmarineEntity entity) {
        switch (entity.getDamageLevel()) {
            case 0:
                return TEXTURE_NEW;
            case 1:
                return TEXTURE_LOW;
            case 2:
                return TEXTURE_MEDIUM;
            case 3:
                return TEXTURE_HIGH;
            case 4:
                return TEXTURE_CRITICAL;
        }
        return TEXTURE_NEW;
    }

    public static <E extends Entity> void renderPassenger(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        EntityRenderer<? super E> render = null;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            render = manager.getRenderer(entityIn);

            if (render != null) {
                try {
                    render.render(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
                } catch (Throwable throwable1) {
                    throw new ReportedException(CrashReport.forThrowable(throwable1, "Rendering entity in world"));
                }
            }
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.forThrowable(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
            entityIn.fillCrashReportCategory(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
            crashreportcategory1.setDetail("Assigned renderer", render);
            crashreportcategory1.setDetail("Rotation", Float.valueOf(yaw));
            crashreportcategory1.setDetail("Delta", Float.valueOf(partialTicks));
            throw new ReportedException(crashreport);
        }
    }

    private static void shineOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, Matrix3f p_114092_, float xOffset, float yOffset) {
        p_114220_.vertex(p_114221_, 0.0F, 0.0F, 0.0F).color(255, 255, 255, 255).uv(xOffset + 0.5F, yOffset).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, Matrix3f p_114092_, float p_114217_, float p_114218_, float xOffset, float yOffset) {
        p_114215_.vertex(p_114216_, -HALF_SQRT_3 * p_114218_, p_114217_, 0).color(200, 235, 255, 0).uv(xOffset, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, Matrix3f p_114092_, float p_114226_, float p_114227_, float xOffset, float yOffset) {
        p_114224_.vertex(p_114225_, HALF_SQRT_3 * p_114227_, p_114226_, 0).color(200, 235, 255, 0).uv(xOffset + 1, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static ResourceLocation getSubmarineBaseTexture(SubmarineEntity entity) {
        switch (entity.getOxidizationLevel()) {
            case 0:
                return TEXTURE;
            case 1:
                return TEXTURE_EXPOSED;
            case 2:
                return TEXTURE_WEATHERED;
            case 3:
                return TEXTURE_OXIDIZED;
        }
        return TEXTURE;
    }

    public ResourceLocation getTextureLocation(SubmarineEntity entity) {
        return TEXTURE;
    }
}

