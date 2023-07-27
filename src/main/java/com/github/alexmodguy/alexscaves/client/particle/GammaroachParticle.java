package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;

public class GammaroachParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected GammaroachParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.setSize(0.5F, 0.5F);
        this.quadSize = 0.2F + world.random.nextFloat() * 0.4F;
        this.lifetime = 8 + world.random.nextInt(5);
        this.friction = 0.96F;
        float randCol = world.random.nextFloat() * 0.05F;
        this.sprites = sprites;
        this.setColor(randCol * 0.2F + 0.2F, randCol * 0.2F + 0.2F, randCol * 0.2F + 0.2F);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.setSpriteFromAge(this.sprites);
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float) this.age - (float) (this.lifetime / 4)) / (float) this.lifetime);
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            int color1 = 0X5CF600;
            float randCol = level.random.nextFloat() * 0.05F;
            Vec3 targetColor = new Vec3(Math.min(FastColor.ARGB32.red(color1) / 255F + randCol, 1), FastColor.ARGB32.green(color1) / 255F + randCol, FastColor.ARGB32.blue(color1) / 255F + randCol);
            this.rCol += (targetColor.x - rCol) * 0.1F;
            this.gCol += (targetColor.y - gCol) * 0.1F;
            this.bCol += (targetColor.z - bCol) * 0.1F;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public float getQuadSize(float scaleFactor) {
        return super.getQuadSize(scaleFactor);
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
            GammaroachParticle particle = new GammaroachParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }
}
