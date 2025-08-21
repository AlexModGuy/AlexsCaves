package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.SauropodBaseEntity;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class StunStarParticle extends AbstractTrailParticle {

    private static final ResourceLocation CENTER_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/stun_star.png");
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/teletor_trail.png");

    private final int entityId;

    private final float offset;
    private boolean reverseOrbit;

    protected StunStarParticle(ClientLevel world, double x, double y, double z, int entityId, float offset) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.xd = 0;
        this.yd = 0;
        this.offset = offset;
        this.zd = 0;
        this.alpha = 1;
        this.entityId = entityId;
        this.hasPhysics = false;
        this.reverseOrbit = random.nextBoolean();
        this.lifetime = 30 + this.random.nextInt(5);
        Vec3 vec3 = getOrbitPos();
        this.setPos(vec3.x, vec3.y, vec3.z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        if (age < 2) {
            return;
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
        VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(ACRenderTypes.itemEntityTranslucentCull(CENTER_TEXTURE));

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
        float alpha = 1;
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
        super.render(vertexConsumer, camera, partialTick);
    }

    public void tick() {
        this.xd *= 0.9;
        this.yd *= 0.9;
        this.zd *= 0.9;
        this.oRoll = this.roll;
        this.roll = (float) ((float) Math.PI * Math.sin(age * 0.6F) * 0.3F);
        super.tick();
        this.trailA = 0.2F * Mth.clamp(age / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
        if (entityId != -1) {
            Vec3 orbit = getOrbitPos();
            this.setPos(orbit.x, orbit.y, orbit.z);
            Entity entity = level.getEntity(entityId);
            if (entity instanceof LivingEntity living && !living.hasEffect(ACEffectRegistry.STUNNED.get())) {
                this.remove();
            }
        }
    }

    public Vec3 getOrbitPos() {
        Entity entity = level.getEntity(entityId);
        if (entity != null) {
            float angle = this.age * 10 + offset;
            Vec3 eyes;
            Vec3 orbitOffset;
            if(entity instanceof SauropodBaseEntity sauropod){
                eyes = sauropod.headPart.position().add(0, 1, 0);
                orbitOffset = new Vec3(0, 0, 1.9F).yRot(angle * (reverseOrbit ? -1 : 1) * (float) (Math.PI / 180F));
            }else{
                eyes = entity.getEyePosition().add(entity.getViewVector(0.0F).scale(entity.getBbWidth() * 0.85F)).add(0, 0.5 + 0.12 * entity.getBbHeight(), 0);
                orbitOffset = new Vec3(0, 0, entity.getBbWidth() * 0.5F + 0.2F).yRot(angle * (reverseOrbit ? -1 : 1) * (float) (Math.PI / 180F));
            }
            return eyes.add(orbitOffset);
        }
        return Vec3.ZERO;
    }

    @Override
    public float getTrailHeight() {
        return 0.4F;
    }

    @Override
    public ResourceLocation getTrailTexture() {
        return TRAIL_TEXTURE;
    }


    @Override
    public int sampleCount() {
        return Math.min(10, lifetime - age);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            StunStarParticle particle = new StunStarParticle(worldIn, x, y, z, (int) xSpeed, (float) ySpeed);
            particle.trailR = 1.0F;
            particle.trailG = 1.0F;
            particle.trailB = 1.0F;
            return particle;
        }
    }
}
