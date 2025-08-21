package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.WatcherModel;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraftforge.client.ForgeRenderTypes;

public class WatcherAppearanceParticle extends Particle {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/watcher_appearance.png");
    private final WatcherModel model = new WatcherModel();

    private WatcherAppearanceParticle(ClientLevel lvl, double x, double y, double z) {
        super(lvl, x, y, z);
        this.setSize(12, 12);
        this.gravity = 0.0F;
        this.lifetime = 30;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        float fogBefore = RenderSystem.getShaderFogEnd();
        RenderSystem.setShaderFogEnd(40);
        float age = this.age + partialTick;
        float f = (age - 5) / (float) (this.lifetime - 5);
        float initalFlip = Math.min(f, 0.1F) / 0.1F;
        float scale = 1;
        PoseStack posestack = new PoseStack();
        posestack.mulPose(camera.rotation());
        posestack.translate(0.0D, 0F, -1.2F);
        posestack.mulPose(Axis.XP.rotationDegrees(0F));
        posestack.scale(-scale, -scale, scale);
        posestack.translate(0.0D, 0.5F, 2 + (1F - initalFlip));
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(TEXTURE));
        this.model.positionForParticle(partialTick, age);
        this.model.renderToBuffer(posestack, vertexconsumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, Mth.clamp(1 - f * f, 0F, 1F));
        multibuffersource$buffersource.endBatch();
        RenderSystem.setShaderFogEnd(fogBefore);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WatcherAppearanceParticle(worldIn, x, y, z);
        }
    }
}
