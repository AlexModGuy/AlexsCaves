package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.FerrouslimeModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.FerrouslimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class FerrouslimeRenderer extends EntityRenderer<FerrouslimeEntity> implements CustomBookEntityRenderer {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/ferrouslime.png");
    private static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/ferrouslime_eyes.png");
    private static final ResourceLocation TEXTURE_GEL = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/ferrouslime_gel.png");
    public static final FerrouslimeModel FERROUSLIME_MODEL = new FerrouslimeModel();
    private boolean sepia = false;

    public FerrouslimeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    public void render(FerrouslimeEntity entity, float f1, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light) {
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        poseStack.pushPose();
        poseStack.translate(0, 0.2F + entity.getBbHeight() * 0.5F, 0);
        float ageInTicks = entity.tickCount + partialTicks;
        float orbitDist = entity.getSlimeSize(partialTicks) * 0.1F;
        for (int i = 1; i <= entity.getHeadCount(); i++) {
            Vec3 vec3 = entity.getHeadOffsetPos(i);
            if (i > entity.prevHeadCount) {
                vec3 = vec3.scale(entity.getMergeProgress(partialTicks));
            }
            poseStack.pushPose();
            poseStack.translate(vec3.x + orbitDist * Math.sin(i + ageInTicks * 0.05F), vec3.y + orbitDist * Math.sin(2 + i + ageInTicks * 0.1F), vec3.z + orbitDist * Math.cos(i + ageInTicks * 0.035F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180 - bodyYaw));
            renderHead(entity, partialTicks, poseStack, source, light);
            poseStack.popPose();
        }
        float gelSize = entity.getSlimeSize(partialTicks) - 0.2F;
        poseStack.pushPose();
        if(sepia || entity.isFakeEntity()){
            renderGel(entity, partialTicks, poseStack, source.getBuffer(ACRenderTypes.getBookWidget(TEXTURE_GEL, sepia)), gelSize, light);
        }else{
            renderGel(entity, partialTicks, poseStack, source.getBuffer(ACRenderTypes.getGel(TEXTURE_GEL)), gelSize, light);
            renderGelSpikes(entity, partialTicks, poseStack, source.getBuffer(ACRenderTypes.getGelTriangles(TEXTURE_GEL)), gelSize, light);
        }
        poseStack.popPose();
        poseStack.popPose();
        super.render(entity, f1, partialTicks, poseStack, source, light);
    }

    private void renderHead(FerrouslimeEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight) {
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot) - bodyYaw;
        float headPitch = Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        FERROUSLIME_MODEL.setupAnim(entity, 0, 0, entity.tickCount + partialTicks, headYaw, headPitch);
        VertexConsumer textureConsumer = source.getBuffer(sepia ? ACRenderTypes.getBookWidget(TEXTURE, true) : RenderType.entityCutoutNoCull(TEXTURE));
        FERROUSLIME_MODEL.renderToBuffer(poseStack, textureConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        VertexConsumer eyesConsumer = source.getBuffer(sepia ? ACRenderTypes.getBookWidget(TEXTURE_EYES, true) : RenderType.eyes(TEXTURE_EYES));
        FERROUSLIME_MODEL.renderToBuffer(poseStack, eyesConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }


    private void renderGelSpikes(FerrouslimeEntity entity, float partialTicks, PoseStack poseStack, VertexConsumer consumer, float size, int packedLight) {
        float length = (size + 1) * entity.getAttackProgress(partialTicks);
        if (length > 0) {
            for (int i = 0; i < 8; i++) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(i * 45));
                poseStack.mulPose(Axis.XP.rotationDegrees(20 * (float) Math.sin(i * 2.3F + (entity.tickCount + partialTicks) * 0.4F)));
                poseStack.mulPose(Axis.ZP.rotationDegrees(20 * (float) Math.sin(i * 2.3F + (entity.tickCount + partialTicks) * 0.4F)));
                Matrix4f cubeAt = poseStack.last().pose();
                Matrix3f matrix3f = poseStack.last().normal();
                this.spikeCubeFace(entity, cubeAt, matrix3f, consumer, size * 0.25F, length, size * 0.25F, packedLight);
                poseStack.popPose();
            }
        }
    }

    private void spikeCubeFace(FerrouslimeEntity entity, Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, float offset, float length, float width, int packedLightIn) {
        int overlayCoords = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        int hurtColor = entity.hurtTime > 0 || entity.deathTime > 0 ? 10 : 255;
        vertexConsumer.vertex(matrix4f, 0, 0, offset + length).color(255, hurtColor, hurtColor, 255).uv((float) 0, (float) 0).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, 0, width, offset).color(255, hurtColor, hurtColor, 255).uv((float) width, (float) length).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, 0, -width, offset).color(255, hurtColor, hurtColor, 255).uv((float) 0, (float) length).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

    }

    private void renderGel(FerrouslimeEntity entity, float partialTicks, PoseStack poseStack, VertexConsumer consumer, float size, int packedLight) {
        Matrix4f cubeAt = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        float cubeStart = size * -0.5F;
        float cubeEnd = size * 0.5F;
        float textureScale = cubeEnd - cubeStart;
        float spike = 1;
        this.renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeEnd, cubeStart, cubeEnd, cubeEnd, cubeEnd, cubeEnd, cubeEnd, textureScale);
        this.renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeEnd, cubeEnd, cubeStart, cubeStart, cubeStart, cubeStart, cubeStart, textureScale);
        this.renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeEnd, cubeEnd, cubeEnd, cubeStart, cubeStart, cubeEnd, cubeEnd, cubeStart, textureScale);
        this.renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeStart, cubeStart, cubeEnd, cubeStart, cubeEnd, cubeEnd, cubeStart, textureScale);
        this.renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeEnd, cubeStart, cubeStart, cubeStart, cubeStart, cubeEnd, cubeEnd, textureScale);
        this.renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeEnd, cubeEnd, cubeEnd, cubeEnd, cubeEnd, cubeStart, cubeStart, textureScale);
    }

    private void renderCubeFace(FerrouslimeEntity entity, Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, int packedLightIn, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float textureScale) {
        int overlayCoords = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        int hurtColor = entity.hurtTime > 0 || entity.deathTime > 0 ? 10 : 255;
        vertexConsumer.vertex(matrix4f, f1, f3, f5).color(255, hurtColor, hurtColor, 255).uv((float) 0, (float) textureScale).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, f2, f3, f6).color(255, hurtColor, hurtColor, 255).uv((float) textureScale, (float) textureScale).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, f2, f4, f7).color(255, hurtColor, hurtColor, 255).uv((float) textureScale, (float) 0).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, f1, f4, f8).color(255, hurtColor, hurtColor, 255).uv((float) 0, (float) 0).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(FerrouslimeEntity entity) {
        return TEXTURE;
    }

    @Override
    public void setSepiaFlag(boolean sepiaFlag) {
        sepia = sepiaFlag;
    }
}

