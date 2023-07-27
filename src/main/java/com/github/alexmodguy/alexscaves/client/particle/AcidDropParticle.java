package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class AcidDropParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private int onGroundTime;

    protected AcidDropParticle(ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.gravity = 0;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize *= 1.2F + random.nextFloat();
        this.lifetime = 100 + random.nextInt(40);
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
    }

    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float) this.age + f) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    public void tick() {
        if (this.age < this.lifetime * 0.25F) {
            this.gravity = 0;
        } else {
            this.gravity = 1F;
            if (onGround) {
                onGroundTime++;
            }
        }
        int sprite = this.onGround ? 1 : 0;
        this.setSprite(sprites.get(sprite, 1));
        if (onGroundTime > 5) {
            this.remove();
            this.level.addParticle(ParticleTypes.SMOKE.getType(), x, y, z, 0, 0, 0);
        }
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
            AcidDropParticle particle = new AcidDropParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return particle;
        }
    }
}
