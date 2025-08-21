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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class PurpleWitchMagicParticle extends AbstractTrailParticle {

    private static final ResourceLocation CENTER_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/purple_witch_magic.png");
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/trail.png");

    private final double xTarget;
    private final double yTarget;
    private final double zTarget;

    protected PurpleWitchMagicParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.alpha = 1;
        this.hasPhysics = false;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.xTarget = xSpeed;
        this.yTarget = ySpeed;
        this.zTarget = zSpeed;
        this.lifetime = 100 + this.random.nextInt(20);
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        super.render(vertexConsumer, camera, partialTick);
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
        VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(ACRenderTypes.itemEntityTranslucentCull(getTexture()));

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.rotate(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = 0.3F;

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }
        float f7 = 0;
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        float alpha = this.getAlpha();
        int j = 240;
        PoseStack posestack = new PoseStack();
        PoseStack.Pose posestack$pose = posestack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        vertexconsumer.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).color(this.rCol, this.gCol, this.bCol, alpha).uv(f8, f6).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexconsumer.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).color(this.rCol, this.gCol, this.bCol, alpha).uv(f8, f5).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexconsumer.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).color(this.rCol, this.gCol, this.bCol, alpha).uv(f7, f5).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexconsumer.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).color(this.rCol, this.gCol, this.bCol, alpha).uv(f7, f6).overlayCoords(NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        multibuffersource$buffersource.endBatch();
    }

    protected VertexConsumer getVetrexConsumer(MultiBufferSource.BufferSource multibuffersource$buffersource) {
        return multibuffersource$buffersource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(getTrailTexture()));
    }

    public float getAlpha() {
        return Mth.clamp( 1F - age / (float) this.lifetime, 0.0F, 1.0F);
    }

    public ResourceLocation getTexture() {
        return CENTER_TEXTURE;
    }

    public void tick() {
        super.tick();
        Vec3 travelVec = new Vec3(xTarget - this.x, yTarget - this.y, zTarget - this.z);
        if(travelVec.length() > 1.0F){
            travelVec = travelVec.normalize();
            this.xd = this.xd * 0.5F + travelVec.x * 0.15F + random.nextGaussian() * 0.05F;
            this.yd = this.yd * 0.5F +  travelVec.y * 0.15F + random.nextGaussian() * 0.05F;
            this.zd = this.zd * 0.5F + travelVec.z * 0.15F + random.nextGaussian() * 0.05F;
        }else{
            this.xd *= 0.4;
            this.yd *= 0.4;
            this.zd *= 0.4;
            this.age = Math.min(this.age + 3, this.lifetime);
        }
        float fadeIn = 0.8F * Mth.clamp(age / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
        float fadeOut = Mth.clamp( 1F - age / (float) this.lifetime, 0.0F, 1.0F);
        this.trailA = fadeIn * fadeOut;
    }

    @Override
    public float getTrailHeight() {
        return 0.15F;
    }

    @Override
    public ResourceLocation getTrailTexture() {
        return TRAIL_TEXTURE;
    }

    @Override
    public int sampleCount() {
        return 5;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            PurpleWitchMagicParticle particle = new PurpleWitchMagicParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.trailR = 1.0F;
            particle.trailG = 0.0F;
            particle.trailB = 1.0F;
            return particle;
        }
    }
}
