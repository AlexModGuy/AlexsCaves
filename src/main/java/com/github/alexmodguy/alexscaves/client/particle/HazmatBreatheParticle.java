package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class HazmatBreatheParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    private float greenOff = 0;
    private float initialColor = 0;

    protected HazmatBreatheParticle(ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion, SpriteSet sprites, boolean blue) {
        super(level, x, y, z, xMotion, yMotion, zMotion);
        this.friction = 0.96F;
        this.gravity = -0.02F - 0.03F * level.random.nextFloat();
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd *= (double) 0.125F;
        this.yd *= (double) 0.125F;
        this.zd *= (double) 0.125F;
        float initialColor = level.random.nextFloat() * 0.25F;
        float greenAmount = level.random.nextFloat() * 0.5F + 0.5F;
        this.rCol = initialColor;
        this.gCol = greenAmount;
        this.bCol = blue ? greenAmount : initialColor;
        this.quadSize *= 0.75F + random.nextFloat() * 0.5F;
        this.lifetime = (int) ((double) 30 / ((double) level.random.nextFloat() * 0.8D + 0.2D));
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(sprites);
        greenOff = this.random.nextFloat();
        this.hasPhysics = false;
    }


    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float) this.age + f) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    public void tick() {
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }


    public int getLightColor(float partialTicks) {
        return 240;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            HazmatBreatheParticle particle = new HazmatBreatheParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false);
            return particle;
        }
    }

    public static class BlueFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public BlueFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            HazmatBreatheParticle particle = new HazmatBreatheParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true);
            return particle;
        }
    }
}
