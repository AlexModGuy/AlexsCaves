package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class VoidBeingTendrilParticle extends Particle {

    private static final ResourceLocation TENDRIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/void_being_cloud_tendril.png");

    private Vec3 tipTarget;
    private double tipX;
    private double tipY;
    private double tipZ;
    private double prevTipX;
    private double prevTipY;
    private double prevTipZ;

    private float animationOffset = 0;
    private int targetId = -1;
    private float size;

    private int seekByTime = 0;
    private float cameraOffsetX = 0;
    private float cameraOffsetY = 0;

    private VoidBeingTendrilParticle(ClientLevel world, double x, double y, double z, double targetId, double seekByTime) {
        super(world, x, y, z);
        this.setSize(1, 1);
        this.gravity = 0.0F;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = 300;
        this.size = 1.0F;
        tipTarget = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).normalize().scale(12).add(x, y, z);
        this.targetId = (int) targetId;
        this.animationOffset = (float) (Math.PI * random.nextFloat());
        this.tipX = x;
        this.tipY = y;
        this.tipZ = z;
        this.cameraOffsetX = (random.nextFloat() - 0.5F) * 1.3F;
        this.cameraOffsetY = (random.nextFloat() - 0.5F) * 1.3F;
        this.seekByTime = (int) seekByTime;
    }

    public void tick() {
        super.tick();
        prevTipX = tipX;
        prevTipY = tipY;
        prevTipZ = tipZ;
        Entity entityTarget = null;
        if (targetId != -1 && age > this.seekByTime) {
            entityTarget = Minecraft.getInstance().level.getEntity(targetId);
            if (entityTarget == null) {
                targetId = -1;
                this.remove();
            } else {
                tipTarget = entityTarget.position().add(0, 0.25F, 0);
            }
        }
        if (tipTarget != null) {
            float ageSmooth = age * 0.3F;
            Vec3 tippening = tipTarget.subtract(tipX, tipY, tipZ);
            if (entityTarget == null) {
                tippening = tippening.add((float) Math.sin(ageSmooth + animationOffset), (float) Math.cos(ageSmooth - Math.PI / 2 + animationOffset), -(float) Math.cos(ageSmooth + animationOffset));
            }
            if (tippening.length() > 1F) {
                tippening = tippening.normalize();
            }
            tipX += tippening.x * 0.2;
            tipY += tippening.y * 0.2;
            tipZ += tippening.z * 0.2;
            if (entityTarget == null && random.nextFloat() < 0.1F) {
                tipTarget = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).normalize().scale(12).add(x, y, z);
            }
        }
        this.xd *= 0.97D;
        this.yd *= 0.97D;
        this.zd *= 0.97D;
    }


    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        double x = (float) (Mth.lerp((double) partialTick, this.xo, this.x));
        double y = (float) (Mth.lerp((double) partialTick, this.yo, this.y));
        double z = (float) (Mth.lerp((double) partialTick, this.zo, this.z));
        Vector3f cameraOffset = new Vector3f(cameraOffsetX, cameraOffsetY, -0.1F);
        Quaternionf quaternion = new Quaternionf(camera.rotation());
        cameraOffset.rotate(quaternion);
        float width = targetId == -1 ? 1.5F : 1.5F + (age / (float) lifetime);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(ACRenderTypes.itemEntityTranslucentCull(TENDRIL_TEXTURE));
        PoseStack posestack = new PoseStack();
        posestack.pushPose();
        posestack.translate(-cameraPos.x + x + cameraOffset.x, -cameraPos.y + y + cameraOffset.y + 0.2F * (float) Math.sin((age + partialTick + animationOffset) * 0.1F), -cameraPos.z + z + cameraOffset.z);
        int samples = 1;
        Vec3 drawFrom = new Vec3(0, 0, 0);
        Vec3 topAngleVec = new Vec3(0, width, 0);
        Vec3 bottomAngleVec = new Vec3(0, -width, 0);
        int j = getLightColor(partialTick);
        float sampleCount = 20;
        while (samples <= sampleCount) {
            float samplesScale = samples / sampleCount;
            float wiggleAmount = (float) Math.sin(samplesScale * Math.PI);
            Vec3 drawTo = getTendrilPosition(samplesScale, wiggleAmount, new Vec3(cameraOffset), partialTick);
            float u1 = (samples - 1) / sampleCount;
            float u2 = (samples) / sampleCount;
            float overallAlpha = VoidBeingCloudParticle.getAlphaFromAge(age, lifetime);
            float startA = Math.min(1F, samples / (sampleCount - 8)) * overallAlpha;
            float endA = Math.min(1F, (samples + 1) / (sampleCount - 8)) * overallAlpha;
            PoseStack.Pose posestack$pose = posestack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertexconsumer.vertex(matrix4f, (float) drawFrom.x + (float) bottomAngleVec.x, (float) drawFrom.y + (float) bottomAngleVec.y, (float) drawFrom.z + (float) bottomAngleVec.z).color(1F, 1F, 1F, startA).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) drawTo.x + (float) bottomAngleVec.x, (float) drawTo.y + (float) bottomAngleVec.y, (float) drawTo.z + (float) bottomAngleVec.z).color(1F, 1F, 1F, endA).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) drawTo.x + (float) topAngleVec.x, (float) drawTo.y + (float) topAngleVec.y, (float) drawTo.z + (float) topAngleVec.z).color(1F, 1F, 1F, endA).uv(u2, 0).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) drawFrom.x + (float) topAngleVec.x, (float) drawFrom.y + (float) topAngleVec.y, (float) drawFrom.z + (float) topAngleVec.z).color(1F, 1F, 1F, startA).uv(u1, 0).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            samples++;
            drawFrom = drawTo;
        }
        multibuffersource$buffersource.endBatch();
        posestack.popPose();
    }

    private Vec3 getTendrilPosition(float f, float wiggleAmount, Vec3 cameraOffset, float partialTick) {
        float x = (float) (Mth.lerp((double) partialTick, this.xo, this.x));
        float y = (float) (Mth.lerp((double) partialTick, this.yo, this.y));
        float z = (float) (Mth.lerp((double) partialTick, this.zo, this.z));
        float ageSmooth = (age + partialTick) * (targetId == -1 || age < 200 ? 0.04F : 1F);
        Vec3 wiggleVec = new Vec3((float) Math.sin(ageSmooth + f + animationOffset), (float) Math.cos(ageSmooth - Math.PI / 2 + f + animationOffset), -(float) Math.cos(ageSmooth + f + animationOffset)).scale(wiggleAmount * 0.2F);
        Vec3 lerpOf = new Vec3(tipX - prevTipX, tipY - prevTipY, tipZ - prevTipZ).scale(partialTick);
        Vec3 vec31 = new Vec3(prevTipX, prevTipY, prevTipZ).add(lerpOf).subtract(cameraOffset);
        return vec31.subtract(x, y, z).scale(f).add(wiggleVec);
    }

    private Quaternionf calcCameraAngle(Camera camera, float sampleScale) {
        float followCameraAmount = sampleScale * sampleScale;
        return new Quaternionf(camera.rotation()).rotateX(followCameraAmount * -0.017453292F * camera.getXRot()).rotateY(followCameraAmount * 0.017453292F * camera.getYRot());
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }


    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new VoidBeingTendrilParticle(worldIn, x, y, z, xSpeed, ySpeed);
        }
    }
}
