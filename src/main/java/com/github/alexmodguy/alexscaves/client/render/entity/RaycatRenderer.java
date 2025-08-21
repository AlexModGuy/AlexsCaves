package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.RaycatModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RaycatRenderer extends MobRenderer<RaycatEntity, RaycatModel> implements CustomBookEntityRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raycat.png");
    private static final ResourceLocation TEXTURE_BODY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raycat_body.png");
    private static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raycat_eyes.png");

    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);
    private boolean sepia;

    public RaycatRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RaycatModel(), 0.4F);
        this.addLayer(new LayerGlow());
    }

    @Nullable
    protected RenderType getRenderType(RaycatEntity raycatEntity, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(raycatEntity);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return sepia ? null : AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get() ? ACRenderTypes.getRadiationGlow(resourcelocation) : ACRenderTypes.entityTranslucent(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    private static void shineOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, Matrix3f p_114092_, float xOffset, float yOffset) {
        p_114220_.vertex(p_114221_, 0.0F, 0.0F, 0.0F).color(0, 255, 0, 255).uv(xOffset + 0.5F, yOffset).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, Matrix3f p_114092_, float p_114217_, float p_114218_, float xOffset, float yOffset) {
        p_114215_.vertex(p_114216_, -HALF_SQRT_3 * p_114218_, p_114217_, 0).color(0, 255, 0, 0).uv(xOffset, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, Matrix3f p_114092_, float p_114226_, float p_114227_, float xOffset, float yOffset) {
        p_114224_.vertex(p_114225_, HALF_SQRT_3 * p_114227_, p_114226_, 0).color(0, 255, 0, 0).uv(xOffset + 1, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    public void render(RaycatEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        if(!sepia && AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()){
            PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
        }
        float absorbAmount = entityIn.getAbsorbAmount(partialTicks);
        Entity absorbTarget = entityIn.getAbsorbTarget();
        if (absorbAmount > 0F && entityIn.isAlive() && absorbTarget != null) {
            Vec3 to = absorbTarget.getPosition(partialTicks).add(0, absorbTarget.getBbHeight() * 0.5F, 0);
            Vec3 toTranslate = to.subtract(entityIn.getPosition(partialTicks).add(0, entityIn.getBbHeight() * 0.5F, 0));
            float yRot = ((float) Mth.atan2(toTranslate.x, toTranslate.z)) * 180.0F / (float) Math.PI;
            float xRot = -(float) (Mth.atan2(toTranslate.y, toTranslate.horizontalDistance()) * (double) (180F / (float) Math.PI));
            float length = (float) toTranslate.length() * absorbAmount;
            float width = absorbAmount * 0.8F;
            poseStack.pushPose();
            poseStack.translate(0, entityIn.getBbHeight() * 0.5F, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
            poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90));
            poseStack.pushPose();
            poseStack.mulPose(Axis.XN.rotationDegrees(90));
            poseStack.mulPose(Axis.ZN.rotationDegrees(90));
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f1 = posestack$pose.pose();
            Matrix3f matrix3f1 = posestack$pose.normal();
            VertexConsumer lightConsumer = bufferIn.getBuffer(ACRenderTypes.getNucleeperLights());
            shineOriginVertex(lightConsumer, matrix4f1, matrix3f1, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            shineRightCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            poseStack.popPose();
            poseStack.popPose();
        }
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }

    public ResourceLocation getTextureLocation(RaycatEntity entity) {
        return TEXTURE_BODY;
    }

    @Override
    public void setSepiaFlag(boolean sepiaFlag) {
        this.sepia = sepiaFlag;
    }

    class LayerGlow extends RenderLayer<RaycatEntity, RaycatModel> {

        public LayerGlow() {
            super(RaycatRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, RaycatEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(RaycatRenderer.this.sepia ? ACRenderTypes.getBookWidget(TEXTURE, true) : RenderType.entityCutoutNoCull(TEXTURE));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder1, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(RaycatRenderer.this.sepia ? ACRenderTypes.getBookWidget(TEXTURE_EYES, true) : ACRenderTypes.getEyesAlphaEnabled(TEXTURE_EYES));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder2, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}


