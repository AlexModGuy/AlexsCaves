package com.github.alexmodguy.alexscaves.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public class PlayerRainbowParticle extends RainbowParticle {

     private final int entityId;
    private int bakeRainbowIndex = 0;
    private boolean stopFlag = false;

    public PlayerRainbowParticle(ClientLevel world, double x, double y, double z, int entityId) {
        super(world, x, y, z, 0, 0, 0);
        this.entityId = entityId;
        this.target = calculatePlayerPos(1.0F);
        this.totalDistance = origin.distanceTo(target);
        rainbowVecCount = 30;
        this.fillSpeed = 30;
        this.fadeSpeed = 5;
        this.lifetime = rainbowVecCount;
        bakedRainbowVecs = new Vec3[rainbowVecCount];
        for(int i = 0; i < rainbowVecCount; i++){
            bakedRainbowVecs[i] = Vec3.ZERO;
        }
    }

    public Vec3 calculatePlayerPos(float partialTicks) {
        Entity entity = level.getEntity(entityId);
        if (entityId != -1 && entity != null) {
            if(entity.onGround() && age > 1){
                stopFlag = true;
            }
            return entity.getPosition(partialTicks).add(0, 0.1, 0);
        }
        return this.getPos();
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = Math.min(alpha, 0.75F);
        float ageClamp = Mth.clamp(age / ((float) lifetime - fillSpeed), 0, 1F);
        this.alphaProgression = ageClamp * Math.max(0, bakeRainbowIndex - 1);
        if(bakeRainbowIndex < rainbowVecCount && !stopFlag){
            this.target = calculatePlayerPos(1.0F);
            this.totalDistance = origin.distanceTo(target);
            double y = target.y - this.origin.y;
            double xz = target.subtract(origin).horizontalDistance();
            bakedRainbowVecs[bakeRainbowIndex] = new Vec3(xz, y, 0);
            bakeRainbowIndex++;
        }
    }


    @Override
    protected float processAngle(float angle, float partialTick, PoseStack posestack) {
        if(!stopFlag){
            Vec3 vec3 = calculatePlayerPos(partialTick);
            Vec3 vecForAngle = vec3.subtract(origin);
            this.angle = (float) Math.atan2(vecForAngle.x, vecForAngle.z);
        }
        return (float) this.angle;
    }

    @Override
    public void scaleRainbow(float partialTick, PoseStack posestack) {
    }

    protected float getRainbowWidth() {
        return 0.3F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory() {
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new PlayerRainbowParticle(worldIn, x, y, z, (int) xSpeed);
        }
    }
}
