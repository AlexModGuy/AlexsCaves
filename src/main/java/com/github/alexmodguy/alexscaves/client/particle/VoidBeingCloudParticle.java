package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.util.UnderzealotSacrifice;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
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
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VoidBeingCloudParticle extends Particle {

    private final int textureSize;
    private static int currentlyUsedTextures = 0;
    private final DynamicTexture dynamicTexture;
    private final RenderType renderType;
    private boolean requiresUpload = true;
    private float size;
    private int id = 0;
    private int targetId;
    private int totalTendrils;

    private int idleSoundTime = 30;

    private boolean spawnedExtras = false;

    public VoidBeingCloudParticle(ClientLevel world, double x, double y, double z, int size, int target, int totalTendrils) {
        super(world, x, y, z);
        this.gravity = 0.0F;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = 300;
        this.size = size + 1;
        this.setSize(this.size, this.size);
        textureSize = 32 + (int) size * 32;
        dynamicTexture = new DynamicTexture(textureSize, textureSize, true);
        id = currentlyUsedTextures;
        ResourceLocation resourcelocation = Minecraft.getInstance().textureManager.register("alexscavesvoid_particle/void_cloud_" + id, dynamicTexture);
        currentlyUsedTextures++;
        this.renderType = ACRenderTypes.getVoidBeingCloud(resourcelocation);
        this.targetId = target;
        this.totalTendrils = totalTendrils;
    }

    public void tick() {
        if (this.age <= 0 && !spawnedExtras) {
            onSpawn();
            spawnedExtras = true;
        }
        super.tick();
        this.xd *= 0.97D;
        this.yd *= 0.97D;
        this.zd *= 0.97D;
        updateTexture();
        if(idleSoundTime-- <= 0){
            idleSoundTime = 80 + random.nextInt(60);
            this.level.playLocalSound(this.x, this.y, this.z, ACSoundRegistry.DARK_CLOUD_IDLE.get(), SoundSource.BLOCKS, 2.0F, 1.0F, false);
        }
        this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
        Entity entity = level.getEntity(this.targetId);
        if(entity == null || !(entity instanceof UnderzealotSacrifice)){
            this.level.playLocalSound(this.x, this.y, this.z, ACSoundRegistry.DARK_CLOUD_DISAPPEAR.get(), SoundSource.BLOCKS, 2.0F, 1.0F, false);
            this.remove();
        }
        if(age == this.lifetime - 10){
            this.level.playLocalSound(this.x, this.y, this.z, ACSoundRegistry.DARK_CLOUD_DISAPPEAR.get(), SoundSource.BLOCKS, 2.0F, 1.0F, false);
        }
    }

    private void onSpawn() {
        int circleOffset = random.nextInt(360);
        int eyes = 3 + random.nextInt(2);
        this.level.playLocalSound(this.x, this.y, this.z, ACSoundRegistry.DARK_CLOUD_APPEAR.get(), SoundSource.BLOCKS, 2.0F, 1.0F, false);
        for (int j = 0; j < eyes; j++) {
            Vec3 vec3 = new Vec3((0.5F + random.nextFloat() * 0.7F) * size * 1.1F, 0, 0).yRot((float) (circleOffset + (j / (float) eyes * 180) * (Math.PI / 180F)));
            this.level.addParticle(ACParticleRegistry.VOID_BEING_EYE.get(), this.x, this.y, this.z, vec3.x, vec3.z, 0);

        }
        for (int j = 0; j < totalTendrils; j++) {
            int timeBy = 200 / totalTendrils * (j + 1);
            this.level.addParticle(ACParticleRegistry.VOID_BEING_TENDRIL.get(), this.x, this.y, this.z, this.targetId, timeBy, 0);
        }
    }

    private void updateTexture() {
        int center = textureSize / 2;
        int black = 0;
        double alphaFadeOut = age > lifetime - 10 ? (lifetime - age) / 10F : 1F;
        double radiusSq = center * center * getAlphaFromAge(age, lifetime);
        for (int i = 0; i < textureSize; ++i) {
            for (int j = 0; j < textureSize; ++j) {
                double d0 = center - i;
                double d1 = center - j;
                double f1 = (ACMath.sampleNoise3D(i, age, j, 15) + 1F) / 2F;
                double d2 = (d0 * d0 + d1 * d1);
                double alpha = (1F - d2 / (radiusSq - f1 * f1 * radiusSq)) * alphaFadeOut;
                if (alpha < 0) {
                    this.dynamicTexture.getPixels().setPixelRGBA(j, i, 0);
                } else {
                    this.dynamicTexture.getPixels().setPixelRGBA(j, i, FastColor.ARGB32.color((int) Math.min(alpha * 255, 255), black, black, black));
                }
            }
        }
        this.dynamicTexture.upload();
    }

    public static float getAlphaFromAge(int age, int lifetime) {
        float alphaFadeIn = Math.min(20, age) / 20F;
        float alphaFadeOut = age > lifetime - 10 ? (lifetime - age) / 10F : 1F;
        return alphaFadeIn * alphaFadeOut;
    }

    public void remove() {
        this.removed = true;
        currentlyUsedTextures--;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        if (this.requiresUpload) {
            this.updateTexture();
            this.requiresUpload = false;
        }
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
        VertexConsumer vertexConsumer1 = multibuffersource$buffersource.getBuffer(renderType);
        PoseStack posestack = new PoseStack();
        PoseStack.Pose posestack$pose = posestack.last();
        Matrix3f matrix3f = posestack$pose.normal();
        float zFightFix = 0;
        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.rotate(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, zFightFix), new Vector3f(-1.0F, 1.0F, zFightFix), new Vector3f(1.0F, 1.0F, zFightFix), new Vector3f(1.0F, -1.0F, zFightFix)};
        float f4 = size;

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i].add(0, 0.2F * (float) Math.sin((age + partialTick) * 0.1F), 0);
            vector3f.rotate(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }
        float f7 = 0;
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        int j = 240;
        vertexConsumer1.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f8, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer1.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f8, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer1.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer1.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(f7, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

        multibuffersource$buffersource.endBatch();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new VoidBeingCloudParticle(worldIn, x, y, z, (int) xSpeed, (int) ySpeed, (int) zSpeed);
        }
    }
}
