package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WitchCookieParticle extends TextureSheetParticle {

    private SpriteSet spriteSet;

    private final int eatSpeed;

    protected WitchCookieParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.quadSize = 0.22F + world.random.nextFloat() * 0.05F;
        this.gravity = -0.02F - 0.03F * level.random.nextFloat();
        this.speedUpWhenYMotionIsBlocked = true;
        this.hasPhysics = true;
        this.xd = xSpeed + level.random.nextFloat() * 0.1F - 0.05F;
        this.yd = ySpeed + 0.025F + level.random.nextFloat() * 0.025F;
        this.zd = zSpeed + level.random.nextFloat() * 0.1F - 0.05F;
        this.spriteSet = spriteSet;
        this.friction = 0.95F;
        this.lifetime = 18 + world.random.nextInt(20);
        this.eatSpeed = Math.min(8 + world.random.nextInt(12), lifetime - 5);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        int ageAt = this.lifetime - this.eatSpeed;
        int sprite = this.age >= ageAt ? Math.min(8 * (this.age - ageAt) / this.eatSpeed, 8) : 0;
        this.setSprite(spriteSet.get(sprite, 8));
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
            if(age >= ageAt){
                this.xd *= 0.8D;
                this.yd *= 0.8D;
                this.zd *= 0.8D;
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            WitchCookieParticle particle = new WitchCookieParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setSprite(spriteSet.get(0, 1));
            return particle;
        }
    }
}

