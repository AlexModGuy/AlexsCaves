package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BioPopParticle extends TextureSheetParticle {

    private SpriteSet spriteSet;

    protected BioPopParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.quadSize *= 0.75F + world.random.nextFloat() * 1.2F;
        this.gravity = -0.02F - 0.03F * level.random.nextFloat();
        this.speedUpWhenYMotionIsBlocked = true;
        this.hasPhysics = true;
        this.xd = xSpeed + level.random.nextFloat() * 0.1F - 0.05F;
        this.yd = ySpeed + 0.05F + level.random.nextFloat() * 0.05F;
        this.zd = zSpeed + level.random.nextFloat() * 0.1F - 0.05F;
        this.spriteSet = spriteSet;
        this.friction = 0.8F;
        this.lifetime = 6 + world.random.nextInt(12);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        int ageAt = this.lifetime - 6;
        int sprite = this.age >= ageAt ? Math.min(this.age - ageAt, 6) : 0;
        this.setSprite(spriteSet.get(sprite, 7));
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BioPopParticle particle = new BioPopParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setSprite(spriteSet.get(0, 1));
            return particle;
        }
    }
}

