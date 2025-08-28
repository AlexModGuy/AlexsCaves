package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Optional;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RainbowParticle extends Particle {
    public static final ParticleGroup PARTICLE_GROUP = new ParticleGroup(100);
    private static final RenderType RAINBOW_RENDER_TYPE = ACRenderTypes.getTeslaBulb(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/rainbow.png"));
    public int rainbowVecCount = 64;
    public int fadeSpeed = 15;
    public int fillSpeed = 40;
    public Vec3 origin;

    public Vec3 target;

    public double totalDistance;

    public double angle;

    protected Vec3[] bakedRainbowVecs;
    public float alphaProgression;
    private float prevAlphaProgression;

    private float prevAlpha;

    public RainbowParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
        super(world, x, y, z, 0, 0, 0);
        this.origin = new Vec3(x, y, z);
        this.target = new Vec3(xd, yd, zd);
        this.totalDistance = origin.distanceTo(target);
        rainbowVecCount = 64;
        bakedRainbowVecs = new Vec3[rainbowVecCount];
        rebakeRainbowVecs(totalDistance);
        this.lifetime = (int) (fillSpeed + this.totalDistance * 4);
        this.gravity = 0;
        this.setSize(3.0F, 3.0F);
    }

    protected void rebakeRainbowVecs(double totalDistance) {
        Vec3 rotateZero = new Vec3(totalDistance, 0, 0);
        for (int i = 0; i < rainbowVecCount; i++) {
            float lifeAt = i / (float) rainbowVecCount;
            float ageJump = (float) Math.sin(lifeAt * Math.PI);
            bakedRainbowVecs[i] = rotateZero.scale(lifeAt).add(0, ageJump * Math.max(totalDistance, 1F), 0);
        }
        Vec3 vecForAngle = target.subtract(origin);
        this.angle = Math.atan2(vecForAngle.x, vecForAngle.z);
    }

    public boolean shouldCull() {
        return false;
    }

    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(RAINBOW_RENDER_TYPE);
        Vec3 cameraPos = camera.getPosition();
        PoseStack posestack = new PoseStack();
        posestack.pushPose();
        posestack.translate(origin.x - cameraPos.x, origin.y - cameraPos.y, origin.z - cameraPos.z);
        float f = processAngle((float) angle, partialTick, posestack);
        posestack.mulPose(Axis.YP.rotation(f  - Mth.HALF_PI));
        scaleRainbow(partialTick, posestack);
        int j = getLightColor(partialTick);
        int vertIndex = 0;
        float width = getRainbowWidth();
        float alphaLerped = prevAlpha + (alpha - prevAlpha) * partialTick;
        float alphaProgressionLerped = prevAlphaProgression + (alphaProgression - prevAlphaProgression) * partialTick;
        while (vertIndex < bakedRainbowVecs.length - 1) {
            posestack.pushPose();
            float u1 = vertIndex / (float) bakedRainbowVecs.length;
            float u2 = u1 + 1 / (float) bakedRainbowVecs.length;

            Vec3 draw1 = bakedRainbowVecs[vertIndex];
            Vec3 draw2 = bakedRainbowVecs[vertIndex + 1];
            PoseStack.Pose posestack$pose = posestack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            float alpha0 = calcAlphaForVertex(vertIndex, alphaProgressionLerped) * alphaLerped;
            float alpha1 = calcAlphaForVertex(vertIndex + 1, alphaProgressionLerped) * alphaLerped;
            vertexconsumer.vertex(matrix4f, (float) draw1.x, (float) draw1.y, (float) draw1.z + width).color(1F, 1F, 1F, alpha0).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x, (float) draw2.y, (float) draw1.z + width).color(1F, 1F, 1F, alpha1).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x, (float) draw2.y, (float) draw2.z - width).color(1F, 1F, 1F, alpha1).uv(u2, 0).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw1.x, (float) draw1.y, (float) draw2.z - width).color(1F, 1F, 1F, alpha0).uv(u1, 0).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertIndex++;
            posestack.popPose();
        }
        multibuffersource$buffersource.endBatch();
        posestack.popPose();

    }

    public void scaleRainbow(float partialTick, PoseStack posestack) {
    }

    protected float getRainbowWidth() {
        return 0.5F;
    }

    protected float processAngle(float angle, float partialTick, PoseStack posestack) {
        return angle;
    }

    private float calcAlphaForVertex(int vertIndex, float alphaIn) {
        return Mth.clamp(alphaIn - vertIndex, 0, 1F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public void tick() {
        super.tick();
        this.prevAlpha = alpha;
        this.prevAlphaProgression = alphaProgression;
        int left = lifetime - age;
        if (left <= fadeSpeed) {
            this.alpha = left / (float) fadeSpeed;
        } else {
            float ageClamp = Mth.clamp(age / ((float) lifetime - fillSpeed), 0, 1F);
            this.alphaProgression = ageClamp * rainbowVecCount;
        }

    }

    @Override
    public Optional<ParticleGroup> getParticleGroup() {
        return Optional.of(PARTICLE_GROUP);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory() {
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new RainbowParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
