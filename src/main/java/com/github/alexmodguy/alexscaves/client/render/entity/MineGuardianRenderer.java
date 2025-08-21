package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.MineGuardianModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.MineGuardianEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class MineGuardianRenderer extends MobRenderer<MineGuardianEntity, MineGuardianModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/mine_guardian.png");
    private static final ResourceLocation TEXTURE_SLEEPING = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/mine_guardian_sleeping.png");
    private static final ResourceLocation TEXTURE_EYE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/mine_guardian_eye.png");
    private static final ResourceLocation TEXTURE_EXPLODE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/mine_guardian_explode.png");


    public MineGuardianRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new MineGuardianModel(), 0.8F);
        this.addLayer(new LayerGlow());
    }

    protected void scale(MineGuardianEntity mob, PoseStack poseStack, float partialTicks) {
        poseStack.scale(1.5F, 1.5F, 1.5F);
    }

    public ResourceLocation getTextureLocation(MineGuardianEntity entity) {
        return entity.isEyeClosed() ? TEXTURE_SLEEPING : TEXTURE;
    }

    private static void shineOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, Matrix3f p_114092_, float xOffset, float yOffset) {
        p_114220_.vertex(p_114221_, 0.0F, 0.0F, 0.0F).color(230, 0, 0, 230).uv(xOffset + 0.5F, yOffset).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, Matrix3f p_114092_, float p_114217_, float p_114218_, float xOffset, float yOffset) {
        p_114215_.vertex(p_114216_, -ACMath.HALF_SQRT_3 * p_114218_, p_114217_, 0).color(255, 0, 0, 0).uv(xOffset, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, Matrix3f p_114092_, float p_114226_, float p_114227_, float xOffset, float yOffset) {
        p_114224_.vertex(p_114225_, ACMath.HALF_SQRT_3 * p_114227_, p_114226_, 0).color(255, 0, 0, 0).uv(xOffset + 1, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }


    public void render(MineGuardianEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        float bodyYaw = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float scanProgress = entityIn.getScanProgress(partialTicks);
        if (scanProgress > 0F && entityIn.isAlive() && !entityIn.isExploding()) {
            float ticks = entityIn.tickCount + partialTicks;
            float length = (float) (scanProgress * (4 + Math.sin(ticks * 0.2F + 2)));
            float width = scanProgress * scanProgress * 1F;
            float extraX = (float) (scanProgress * (Math.sin(ticks * 0.1F) * 3));
            poseStack.pushPose();
            poseStack.translate(0, 0.5F, 0);
            poseStack.mulPose(Axis.YN.rotationDegrees(bodyYaw));
            poseStack.translate(extraX * 0.5F / 16F, 0.25F, 0.75F);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (Math.sin(ticks * 0.1F) * 32 * scanProgress)));
            model.translateToEye(poseStack);
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.translate(0F, -0.5F, 0F);
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f1 = posestack$pose.pose();
            Matrix3f matrix3f1 = posestack$pose.normal();
            VertexConsumer lightConsumer = bufferIn.getBuffer(ACRenderTypes.getSubmarineLights());
            shineOriginVertex(lightConsumer, matrix4f1, matrix3f1, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            shineRightCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            poseStack.popPose();
            poseStack.popPose();
        }
    }

    class LayerGlow extends RenderLayer<MineGuardianEntity, MineGuardianModel> {

        public LayerGlow() {
            super(MineGuardianRenderer.this);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, MineGuardianEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float explodeProgress = entitylivingbaseIn.getExplodeProgress(partialTicks);
            if (!entitylivingbaseIn.isEyeClosed()) {
                VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(RenderType.eyes(TEXTURE_EYE));
                this.getParentModel().renderToBuffer(poseStack, ivertexbuilder1, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
            VertexConsumer ivertexbuilder4 = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_EXPLODE));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder4, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, explodeProgress);

        }
    }
}


