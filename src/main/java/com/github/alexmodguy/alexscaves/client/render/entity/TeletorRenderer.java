package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.TeletorModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.TeletorEntity;
import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class TeletorRenderer extends MobRenderer<TeletorEntity, TeletorModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/teletor.png");
    private static final ResourceLocation TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/teletor_glow.png");
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/teletor_trail.png");

    private Map<UUID, LightningRender> lightningRenderMap = new HashMap<>();

    public TeletorRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new TeletorModel(), 0.5F);
        this.addLayer(new LayerGlow());
    }

    protected void scale(TeletorEntity mob, PoseStack matrixStackIn, float partialTicks) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }


    public boolean shouldRender(TeletorEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            Entity weapon = entity.getWeapon();
            if (weapon != null) {
                Vec3 vec3 = entity.position();
                Vec3 vec31 = weapon.position();
                return camera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));

            }

            return false;
        }
    }

    public ResourceLocation getTextureLocation(TeletorEntity entity) {
        return TEXTURE;
    }


    public void render(TeletorEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        double x = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
        double y = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
        double z = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
        float yaw = entityIn.yBodyRotO + (entityIn.yBodyRot - entityIn.yBodyRotO) * partialTicks;
        if (entityIn.hasTrail()) {
            poseStack.pushPose();
            poseStack.translate(-x, -y, -z);
            setupRotations(entityIn, poseStack, 0F, 180F, partialTicks);
            Vec3 headModelPos = getModel().translateToHead(new Vec3(0, -0.4F, 0), yaw).scale(-1);
            poseStack.translate(headModelPos.x, headModelPos.y, headModelPos.z);
            renderTrail(entityIn, 0, partialTicks, poseStack, bufferIn, 0.2F, 0.2F, 0.8F, 0.8F, 240);
            renderTrail(entityIn, 1, partialTicks, poseStack, bufferIn, 0.8F, 0.2F, 0.2F, 0.8F, 240);
            poseStack.popPose();
        }
        Entity weapon = entityIn.getWeapon();
        if (weapon != null && entityIn.isAlive() && weapon.isAlive()) {
            poseStack.pushPose();
            poseStack.translate(-x, -y, -z);
            setupRotations(entityIn, poseStack, 0F, 180F, partialTicks);
            Vec3 headModelPos = getModel().translateToHead(new Vec3(0, -0.4F, 0), yaw).scale(-1);
            Vec3 fromVec1 = entityIn.getHelmetPosition(0).add(headModelPos);
            Vec3 fromVec2 = entityIn.getHelmetPosition(1).add(headModelPos);
            Vec3 toVec = weapon.getPosition(partialTicks).add(0F, weapon.getBbHeight() * 0.5F - 0.1F + Math.sin((weapon.tickCount + partialTicks) * 0.1F) * 0.1F, 0F);

            int segCount = Mth.clamp((int) weapon.distanceTo(entityIn) + 2, 3, 30);
            float spreadFactor = Mth.clamp((10 - weapon.distanceTo(entityIn)) / 10F * 0.2F, 0.01F, 0.2F);

            LightningBoltData.BoltRenderInfo blueBoltData = new LightningBoltData.BoltRenderInfo(0.0F, spreadFactor, 0.0F, 0.0F, new Vector4f(0.2F, 0.2F, 0.8F, 0.8F), 0.1F);
            LightningBoltData.BoltRenderInfo redBoltData = new LightningBoltData.BoltRenderInfo(0.0F, spreadFactor, 0.0F, 0.0F, new Vector4f(0.8F, 0.2F, 0.2F, 0.8F), 0.1F);

            LightningBoltData bolt1 = new LightningBoltData(blueBoltData, fromVec1, toVec, segCount)
                    .size(0.1F)
                    .lifespan(1)
                    .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
                    .fade(LightningBoltData.FadeFunction.NONE);
            LightningBoltData bolt2 = new LightningBoltData(redBoltData, fromVec2, toVec, segCount)
                    .size(0.1F)
                    .lifespan(1)
                    .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
                    .fade(LightningBoltData.FadeFunction.NONE);
            LightningRender lightningRender = getLightingRender(entityIn.getUUID());
            lightningRender.update(entityIn, bolt1, partialTicks);
            lightningRender.update(weapon, bolt2, partialTicks);

            lightningRender.render(partialTicks, poseStack, bufferIn);
            poseStack.popPose();
        }
        if (!entityIn.isAlive() && lightningRenderMap.containsKey(entityIn.getUUID())) {
            lightningRenderMap.remove(entityIn.getUUID());
        }
    }

    private LightningRender getLightingRender(UUID uuid) {
        if (lightningRenderMap.get(uuid) == null) {
            lightningRenderMap.put(uuid, new LightningRender());
        }
        return lightningRenderMap.get(uuid);
    }

    private void renderTrail(TeletorEntity entityIn, int side, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, float trailR, float trailG, float trailB, float trailA, int packedLightIn) {
        int samples = 0;
        int sampleSize = 10;
        float trailHeight = 0.2F;
        float trailZRot = 0;
        Vec3 topAngleVec = new Vec3(0, trailHeight, 0).zRot(trailZRot);
        Vec3 bottomAngleVec = new Vec3(0, -trailHeight, 0).zRot(trailZRot);
        Vec3 drawFrom = entityIn.getTrailPosition(0, side, partialTicks);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));
        while (samples < sampleSize) {
            Vec3 sample = entityIn.getTrailPosition(samples + 2, side, partialTicks);
            float u1 = samples / (float) sampleSize;
            float u2 = u1 + 1 / (float) sampleSize;

            Vec3 draw1 = drawFrom;
            Vec3 draw2 = sample;

            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) bottomAngleVec.x, (float) draw1.y + (float) bottomAngleVec.y, (float) draw1.z + (float) bottomAngleVec.z).color(trailR, trailG, trailB, trailA).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) bottomAngleVec.x, (float) draw2.y + (float) bottomAngleVec.y, (float) draw2.z + (float) bottomAngleVec.z).color(trailR, trailG, trailB, trailA).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) topAngleVec.x, (float) draw2.y + (float) topAngleVec.y, (float) draw2.z + (float) topAngleVec.z).color(trailR, trailG, trailB, trailA).uv(u2, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) topAngleVec.x, (float) draw1.y + (float) topAngleVec.y, (float) draw1.z + (float) topAngleVec.z).color(trailR, trailG, trailB, trailA).uv(u1, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            samples++;
            drawFrom = sample;
        }
    }

    class LayerGlow extends RenderLayer<TeletorEntity, TeletorModel> {

        public LayerGlow() {
            super(TeletorRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, TeletorEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
            float alpha = (float) (1F + Math.sin(ageInTicks * 0.3F)) * 0.1F + 0.8F;
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);

        }
    }
}

