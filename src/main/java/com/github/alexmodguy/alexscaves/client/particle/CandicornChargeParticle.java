package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.client.render.entity.CandicornRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.ForsakenRenderer;
import com.github.alexmodguy.alexscaves.server.entity.living.CandicornEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.ForsakenEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class CandicornChargeParticle extends TextureSheetParticle {

    private final int candicornId;
    private float xRot;
    private float yRot;
    private float fadeR;
    private float fadeG;
    private float fadeB;

    private boolean passedTarget;

    protected CandicornChargeParticle(ClientLevel world, double x, double y, double z, int entityId, float xRot, float yRot) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.quadSize = 0.3F;
        this.setSize(5.0F, 5.0F);
        this.setColor(1F, 1F, 1F);
        this.candicornId = entityId;
        this.lifetime = 35;
        setOnHornPos();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.friction = 1F;
        this.xRot = xRot;
        this.yRot = yRot;
        this.hasPhysics = false;
    }

    public void setFadeColor(int i) {
        this.fadeR = (float) ((i & 16711680) >> 16) / 255.0F;
        this.fadeG = (float) ((i & '\uff00') >> 8) / 255.0F;
        this.fadeB = (float) ((i & 255) >> 0) / 255.0F;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float f = ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime;
        float f1 = this.age / (float) this.lifetime;
        float f2 = 1F - 0.1F * f1;
        friction = 1F - 0.65F * f1;
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - f * 2F);
        }
        this.rCol += (fadeR - this.rCol) * 0.1F;
        this.gCol += (fadeG - this.gCol) * 0.1F;
        this.bCol += (fadeB - this.bCol) * 0.1F;
        Vec3 motionVec = new Vec3(0, 0, -0.05F).xRot((float) Math.toRadians(xRot)).yRot(-(float) Math.toRadians(yRot));
        this.xd += motionVec.x * f2;
        this.yd += motionVec.y * f2;
        this.zd += motionVec.z * f2;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
        }
    }

    public void setOnHornPos() {
        if (candicornId != -1 && level.getEntity(candicornId) instanceof CandicornEntity entity) {
            Vec3 chargeFocalPoint = new Vec3(0F, entity.getEyeHeight() - 0.1F, entity.getBbWidth() + 0.8F).yRot((float) Math.toRadians(-entity.getChargeYaw()));
            this.setPos(entity.getX() + chargeFocalPoint.x, entity.getY() + chargeFocalPoint.y, entity.getZ() + chargeFocalPoint.z);
            this.setFadeColor(entity.getParticleColor());
        }else{
            this.setFadeColor(0XFFEF57);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    public float getQuadSize(float scaleFactor) {
        float f = 8.0F;
        return this.quadSize * Mth.clamp(((float) this.age + scaleFactor) * 2.0F / (float) this.lifetime, 0.0F, 1.0F) * f;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        this.renderSignal(vertexConsumer, camera, partialTick, (quaternionf) -> {
            quaternionf.rotateY(-(float) Math.toRadians(yRot)).rotateX(-(float) Math.toRadians(xRot));
        });
        this.renderSignal(vertexConsumer, camera, partialTick, (quaternionf) -> {
            quaternionf.rotateY(-(float) Math.PI - (float) Math.toRadians(yRot)).rotateX((float) Math.toRadians(xRot));
        });
    }

    private void renderSignal(VertexConsumer consumer, Camera camera, float partialTicks, Consumer<Quaternionf> rots) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTicks, this.zo, this.z) - vec3.z());
        Vector3f vector3f = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0.0F, vector3f.x(), vector3f.y(), vector3f.z());
        rots.accept(quaternionf);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f1 = avector3f[i];
            vector3f1.rotate(quaternionf);
            vector3f1.mul(f3);
            vector3f1.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(partialTicks);
        consumer.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }


    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CandicornChargeParticle particle = new CandicornChargeParticle(worldIn, x, y, z, (int) xSpeed, (float) ySpeed, (float) zSpeed);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }


}
