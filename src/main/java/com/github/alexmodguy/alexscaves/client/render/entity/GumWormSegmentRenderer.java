package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GumWormSegmentModel;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GumWormSegmentRenderer extends EntityRenderer<GumWormSegmentEntity> {

    private static final ResourceLocation TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gum_worm_segment_0.png");
    private static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gum_worm_segment_1.png");
    private static final ResourceLocation TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gum_worm_segment_2.png");
    private static final ResourceLocation TEXTURE_CONNECTOR = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gum_worm_connection.png");
    private static final GumWormSegmentModel MODEL = new GumWormSegmentModel();

    public GumWormSegmentRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public boolean shouldRender(GumWormSegmentEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            Entity nextWorm = entity.getFrontEntity();
            if (nextWorm != null) {
                Vec3 vec3 = entity.position();
                Vec3 vec31 = nextWorm.position();
                return camera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
            }
            return false;
        }
    }

    public void render(GumWormSegmentEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        Entity frontAttachedEntity = entity.getFrontEntity();
        Entity backAttachedEntity = entity.getBackEntity();
        float yRotLerp = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRotLerp = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.pushPose();
        poseStack.translate(0.0D, (double) 1F, 0.0D);
        poseStack.mulPose(Axis.YN.rotationDegrees(yRotLerp));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRotLerp + 180));
        poseStack.translate(0.0D, (double) -0.5F, 0.0D);
        MODEL.setGumVisible(frontAttachedEntity != null, backAttachedEntity != null);
        MODEL.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTicks, 0.0F, 0.0F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        MODEL.renderToBuffer(poseStack, ivertexbuilder, packedLightIn, getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
        if(frontAttachedEntity != null){
            Vec3 centeredHeight = new Vec3(0, entity.getBbHeight() * 0.5F, 0);
            Vec3 from = new Vec3(0F, 0, -0.9F).xRot((float) Math.toRadians(180 - xRotLerp)).yRot(-(float) Math.toRadians(yRotLerp)).add(centeredHeight);
            Vec3 attachment = frontAttachedEntity.getPosition(partialTicks).add(0F, entity.getBbHeight() * 0.5F, 0);
            float zBack = 0;
            float yUp = 0;
            float theirYRotLerp = Mth.lerp(partialTicks, frontAttachedEntity.yRotO, frontAttachedEntity.getYRot());
            float theirXRotLerp = frontAttachedEntity.getViewXRot(partialTicks);
            if(frontAttachedEntity instanceof GumWormSegmentEntity){
                zBack = -0.9F;
            }else if(frontAttachedEntity instanceof GumWormEntity){
                zBack = -1.7F;
            }
            Vec3 to = attachment.add(new Vec3(0F, yUp, zBack).xRot(-(float) Math.toRadians(theirXRotLerp)).yRot(-(float) Math.toRadians(theirYRotLerp))).subtract(entity.getPosition(partialTicks));
            VertexConsumer gumConsumer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_CONNECTOR));
            poseStack.pushPose();
            poseStack.translate(from.x, from.y, from.z);
            renderGum(to.subtract(from), poseStack, gumConsumer, packedLightIn, getOverlayCoords(entity, 0.0F));
            poseStack.popPose();
        }
    }

    public static void renderGum(Vec3 to, PoseStack poseStack, VertexConsumer gumConsumer, int packedLightIn, int overlayCoords) {
        double d = to.horizontalDistance();
        float rotY = (float) (Mth.atan2(to.x, to.z) * (double) (180F / (float) Math.PI));
        float rotX = (float) (-(Mth.atan2(to.y, d) * (double) (180F / (float) Math.PI))) - 90.0F;
        float length = (float) to.length();
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
        poseStack.translate(0, -length, 0);
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        gumConsumer.vertex(matrix4f, 0, 0, -0.5F).color(255, 255, 255, 255).uv((float) 0, (float) 0).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        gumConsumer.vertex(matrix4f, 0, 0, 0.5F).color(255, 255, 255, 255).uv((float) 1, (float) 0).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        gumConsumer.vertex(matrix4f, 0, length, 0.5F).color(255, 255, 255, 255).uv((float) 1, (float)1).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        gumConsumer.vertex(matrix4f, 0, length, -0.5F).color(255, 255, 255, 255).uv((float) 0, (float)1).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        poseStack.popPose();
    }

    public static int getOverlayCoords(GumWormSegmentEntity segmentEntity, float f) {
        return OverlayTexture.pack(OverlayTexture.u(f), OverlayTexture.v(segmentEntity.renderHurtFlag));
    }

    public ResourceLocation getTextureLocation(GumWormSegmentEntity entity) {
        switch (entity.getIndex() % 3){
            case 1:
                return TEXTURE_1;
            case 2:
                return TEXTURE_2;
            default:
                return TEXTURE_0;
        }
    }
}
