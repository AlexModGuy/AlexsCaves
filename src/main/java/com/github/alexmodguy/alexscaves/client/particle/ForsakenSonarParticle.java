package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.client.render.entity.ForsakenRenderer;
import com.github.alexmodguy.alexscaves.server.entity.living.ForsakenEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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

public class ForsakenSonarParticle extends TextureSheetParticle {

    private final int forsakenId;
    private float xRot;
    private float yRot;
    private float fadeR;
    private float fadeG;
    private float fadeB;
    private boolean massive;

    private boolean passedTarget;

    protected ForsakenSonarParticle(ClientLevel world, double x, double y, double z, int entityId, float xRot, float yRot, boolean massive) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.setSize(massive ? 2.99F : 0.9F, 0.99F);
        this.setColor(1F, 1F, 1F);
        this.forsakenId = entityId;
        this.massive = massive;
        this.lifetime = 15;
        setInMouthPos(1.0F);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.quadSize = massive ? 3F + world.random.nextFloat() * 0.5F : 1F + world.random.nextFloat() * 0.3F;
        this.friction = 1F;
        this.xRot = xRot;
        this.yRot = yRot;
        this.setFadeColor(0XE60000);
        angleTowardsTarget();
    }

    public void setFadeColor(int i) {
        this.fadeR = (float)((i & 16711680) >> 16) / 255.0F;
        this.fadeG = (float)((i & '\uff00') >> 8) / 255.0F;
        this.fadeB = (float)((i & 255) >> 0) / 255.0F;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float f = ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime;
        float f1 = this.age / (float)this.lifetime;
        float f2 = 1F - 0.1F * f1;
        friction = 1F - 0.65F * f1;
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - f * 2F);
        }
        angleTowardsTarget();
        this.rCol += (fadeR - this.rCol) * 0.1F;
        this.gCol += (fadeG - this.gCol) * 0.1F;
        this.bCol += (fadeB - this.bCol) * 0.1F;
        Vec3 motionVec = new Vec3(0, 0, massive ? -0.2F : 0.5F).xRot((float) Math.toRadians(xRot)).yRot(-(float) Math.toRadians(yRot));
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

    public void setInMouthPos(float partialTick){
        if(forsakenId != -1 && level.getEntity(forsakenId) instanceof ForsakenEntity entity){
            Vec3 mouthPos = ForsakenRenderer.getMouthPositionFor(forsakenId);
            if(mouthPos != null){
                Vec3 translate = mouthPos.add(new Vec3(0, this.massive ? 0.75F : 0F, 0F)).yRot((float) (Math.PI - entity.yBodyRot * ((float)Math.PI / 180F)));
                this.setPos(entity.getX() + translate.x, entity.getY() + translate.y, entity.getZ() + translate.z);
            }
            if(!this.massive){
                Entity target = entity.getSonarTarget();
                if(target == null){
                    lifetime = 40;
                }else{
                    lifetime = 15 + Math.min(15, (int) Math.ceil(entity.distanceTo(target) * 3));
                }
            }
        }
    }

    public void angleTowardsTarget(){
        if(!massive && forsakenId != -1 && !passedTarget && level.getEntity(forsakenId) instanceof ForsakenEntity forsakenEntity){
            Entity target = forsakenEntity.getSonarTarget();
            if(target != null){
                Vec3 vector3d1 = target.getEyePosition().subtract(x, y, z);
                if(vector3d1.length() < 2){
                    passedTarget = true;
                }
                this.yRot = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                this.xRot = (float) ((Mth.atan2(vector3d1.y, vector3d1.horizontalDistance()) * (double) (180F / (float) Math.PI)));
            }
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
        float f = massive ? 4.0F : 2.0F;
        float f1 = massive ? 1.0F : 1.5F;
        return this.quadSize * Mth.clamp(((float) this.age + scaleFactor) * f1 / (float) this.lifetime, 0.0F, 1.0F) * f;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        this.renderSignal(vertexConsumer, camera, partialTick, (quaternionf) -> {
            quaternionf.rotateY(-(float) Math.toRadians(yRot)).rotateX(-(float) Math.toRadians(xRot));
        });
        this.renderSignal(vertexConsumer, camera, partialTick, (quaternionf) -> {
            quaternionf.rotateY(-(float)Math.PI - (float) Math.toRadians(yRot)).rotateX((float) Math.toRadians(xRot));
        });
    }

    private void renderSignal(VertexConsumer consumer, Camera camera, float partialTicks, Consumer<Quaternionf> rots) {
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp((double)partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)partialTicks, this.zo, this.z) - vec3.z());
        Vector3f vector3f = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0.0F, vector3f.x(), vector3f.y(), vector3f.z());
        rots.accept(quaternionf);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
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
        consumer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }


    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ForsakenSonarParticle particle = new ForsakenSonarParticle(worldIn, x, y, z, (int) xSpeed, (float) ySpeed, (float) zSpeed, false);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LargeFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public LargeFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ForsakenSonarParticle particle = new ForsakenSonarParticle(worldIn, x, y, z, (int) xSpeed, (float) ySpeed, (float) zSpeed, true);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }


}
