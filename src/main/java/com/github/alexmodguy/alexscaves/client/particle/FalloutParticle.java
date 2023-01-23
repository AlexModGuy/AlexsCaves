package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class FalloutParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    private float greenOff = 0;
    private float initialColor = 0;

    protected FalloutParticle(ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.gravity = 0.05F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd *= (double) 0.1F;
        this.yd *= (double) -0.1F;
        this.zd *= (double) 0.1F;
        this.xd += xMotion;
        this.yd += yMotion;
        this.zd += zMotion;
        initialColor = level.random.nextFloat() * 0.5F;
        this.rCol = initialColor;
        this.gCol = initialColor;
        this.bCol = initialColor;
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
        float greenAmount = (float) (Math.sin(age * 0.1F + greenOff * Math.PI) + 1F) * 0.5F;
        float invGreen = 1 - greenAmount;
        this.rCol = initialColor * invGreen;
        this.gCol = initialColor * invGreen + greenAmount * 0.7F;
        this.bCol = initialColor * invGreen;
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }


    public int getLightColor(float partialTicks) {
        float greenAmount = (float) (Math.sin((age + partialTicks) * 0.1F + greenOff * Math.PI) + 1F) * 0.5F;
        greenAmount = Math.max(0.3F, greenAmount);
        int i = super.getLightColor(partialTicks);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int) (greenAmount * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FalloutParticle particle = new FalloutParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return particle;
        }
    }
}
