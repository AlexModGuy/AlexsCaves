package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.SplashModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class BigSplashParticle extends Particle {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/splash.png");
    private static final ResourceLocation TEXTURE_OVERLAY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/splash_overlay.png");
    private static final SplashModel MODEL = new SplashModel();
    private float scale;
    private final int waterColor;

    protected BigSplashParticle(ClientLevel level, double x, double y, double z, float scale, int lifetime) {
        super(level, x, y, z);
        this.gravity = 0.0F;
        this.lifetime = lifetime;
        this.scale = scale;
        this.waterColor = BiomeColors.getAverageWaterColor(level, BlockPos.containing(x, y, z));
        this.setSize(3.0F, 3.0F);
    }

    public boolean shouldCull() {
        return false;
    }

    public void tick() {
        super.tick();
        int k = this.lifetime - this.age;
        if (k < 5) {
            float f = k / 5F;
            this.setAlpha(f);
        } else {
            for (int j = 0; j < scale * 2 + 1; j++) {
                Vec3 sputterFrom = new Vec3((level.random.nextFloat() - 0.5F) * 0.1F * scale, -0.25F, (level.random.nextFloat() - 0.5F) * 0.1F * scale).add(this.x, this.y, this.z);
                this.level.addParticle(ACParticleRegistry.BIG_SPLASH_EFFECT.get(), sputterFrom.x, sputterFrom.y, sputterFrom.z, (level.random.nextFloat() - 0.5F) * 0.2F, 0.3F + level.random.nextFloat() * 0.2F, (level.random.nextFloat() - 0.5F) * 0.2F);
            }

        }
    }

    public void remove() {
        super.remove();
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - vec3.z());
        int packedLight = getLightColor(partialTick);
        float colorR = (waterColor >> 16 & 255) / 255F;
        float colorG = (waterColor >> 8 & 255) / 255F;
        float colorB = (waterColor & 255) / 255F;
        PoseStack posestack = new PoseStack();
        posestack.pushPose();
        posestack.translate(f, f1 - 0.5F, f2);
        posestack.scale(-scale, -scale, scale);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        MODEL.setupAnim(null, 0, lifetime, age + partialTick, 0, 0);
        VertexConsumer baseConsumer = multibuffersource$buffersource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        MODEL.renderToBuffer(posestack, baseConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorR, colorG, colorB, alpha);
        VertexConsumer overlayconsumer = multibuffersource$buffersource.getBuffer(RenderType.entityTranslucent(TEXTURE_OVERLAY));
        MODEL.renderToBuffer(posestack, overlayconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
        multibuffersource$buffersource.endBatch();
        posestack.popPose();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            if (xSpeed == 0.0) {
                xSpeed = 1.0F;
            }
            int lifetime = 5 + (int) Math.round(ySpeed * 5);
            return new BigSplashParticle(worldIn, x, y, z, (float) Math.max(0.5F, xSpeed), lifetime);
        }
    }
}
