package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VoidBeingEyeParticle extends Particle {

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/void_eye_0.png"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/void_eye_1.png"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/void_eye_2.png")
    };

    private int textureIndex = 0;
    private float prevCameraOffsetX = 0;
    private float prevCameraOffsetY = 0;
    private float cameraOffsetX = 0;
    private float cameraOffsetY = 0;
    private float animationOffset = 0;

    public VoidBeingEyeParticle(ClientLevel world, double x, double y, double z, float cameraOffsetX, float cameraOffsetY) {
        super(world, x, y, z);
        textureIndex = random.nextInt(2);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.animationOffset = (float) (random.nextFloat() * Math.PI);
        this.lifetime = 300;
        this.gravity = 0.0F;
        this.prevCameraOffsetX = cameraOffsetX;
        this.prevCameraOffsetY = cameraOffsetY;
        this.cameraOffsetX = cameraOffsetX;
        this.cameraOffsetY = cameraOffsetY;
    }

    public void tick() {
        super.tick();
        this.prevCameraOffsetX = cameraOffsetX;
        this.prevCameraOffsetY = cameraOffsetY;
        if (age > 200) {
            float offsetX = (0 - cameraOffsetX) / (float) (lifetime - 200);
            float offsetY = (-5 - cameraOffsetY) / (float) (lifetime - 200);
            this.cameraOffsetX += offsetX;
            this.cameraOffsetY += offsetY;
        }
        if (age > lifetime - 20) {
            this.cameraOffsetY += -0.25F;
        }

    }


    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        this.alpha = VoidBeingCloudParticle.getAlphaFromAge(age, lifetime);
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - vec3.z());
        Quaternionf quaternion;
        if (this.roll == 0.0F) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternionf(camera.rotation());
            float f3 = Mth.lerp(partialTick, this.oRoll, this.roll);
            quaternion.mul(Axis.ZP.rotation(f3));
        }
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer portalStatic = multibuffersource$buffersource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(TEXTURES[textureIndex]));
        PoseStack posestack = new PoseStack();
        PoseStack.Pose posestack$pose = posestack.last();
        Matrix3f matrix3f = posestack$pose.normal();

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.rotate(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, -0.05F), new Vector3f(-1.0F, 1.0F, -0.05F), new Vector3f(1.0F, 1.0F, -0.05F), new Vector3f(1.0F, -1.0F, -0.05F)};
        float f4 = 0.5F;
        float offsetX = prevCameraOffsetX + (cameraOffsetX - prevCameraOffsetX) * partialTick;
        float offsetY = prevCameraOffsetY + (cameraOffsetY - prevCameraOffsetY) * partialTick;
        float shakeX = 0.0F;
        float shakeY = 0.0F;
        if (age > 200) {
            shakeX = 0.3F * (float) Math.sin((age + partialTick + 3 * animationOffset) * 0.54F);
            shakeY = 0.3F * (float) -Math.sin((age + partialTick + 3 * animationOffset) * 0.54F + 2);
        }
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i].add(offsetX + shakeX, offsetY + shakeY + 0.2F * (float) Math.sin((age + partialTick + animationOffset) * 0.1F), 0);
            vector3f.rotate(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }
        float f7 = 0;
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        int j = 240;
        portalStatic.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f8, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f8, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

        multibuffersource$buffersource.endBatch();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new VoidBeingEyeParticle(worldIn, x, y, z, (float) xSpeed, (float) ySpeed);
        }
    }
}
