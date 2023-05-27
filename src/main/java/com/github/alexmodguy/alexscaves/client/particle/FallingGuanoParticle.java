package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class FallingGuanoParticle extends TextureSheetParticle {

    private float alphaOff = 0;
    private float initialColor = 0;

    protected FallingGuanoParticle(ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.gravity = 0.03F + random.nextFloat() * 0.05F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd *= (double) 0.5F;
        this.yd *= (double) -0.1F;
        this.zd *= (double) 0.5F;
        this.xd += xMotion;
        this.yd += yMotion;
        this.zd += zMotion;
        initialColor = 0.8F + level.random.nextFloat() * 0.2F;
        this.rCol = initialColor * 0.42F;
        this.gCol = initialColor * 0.31F;
        this.bCol = initialColor * 0.23F;
        this.quadSize *= 0.75F + random.nextFloat() * 0.5F;
        this.lifetime = (int) ((double) 30 / ((double) level.random.nextFloat() * 0.8D + 0.2D));
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(sprites);
        alphaOff = this.random.nextFloat();
        this.hasPhysics = false;
    }


    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float) this.age + f) / (float) this.lifetime * 10.0F, 0.0F, 1.0F);
    }

    public void tick() {
        float alphaFadeOut = age > lifetime - 5F ? (lifetime - age) / 5F : 1F;
        float alphaAmount = (float) (Math.sin(age * 0.2F + alphaOff * Math.PI) + 1F) * 0.5F;
        this.alpha = (0.3F + alphaAmount * 0.7F) * alphaFadeOut;
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }


    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FallingGuanoParticle particle = new FallingGuanoParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return particle;
        }
    }
}
