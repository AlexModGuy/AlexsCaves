package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;

public class SmallExplosionParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected SmallExplosionParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, boolean shortLifespan, int color1) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.setSize(0.5F, 0.5F);
        this.quadSize = ( shortLifespan ? 1 : 0.8F) + world.random.nextFloat() * 0.3F;
        this.lifetime = shortLifespan ? 5 + world.random.nextInt(3) : 15 + world.random.nextInt(10);
        this.friction = 0.96F;
        float randCol = world.random.nextFloat() * 0.05F;
        this.sprites = sprites;
        this.setColor(Math.min(FastColor.ARGB32.red(color1) / 255F + randCol, 1), FastColor.ARGB32.green(color1) / 255F + randCol, FastColor.ARGB32.blue(color1) / 255F + randCol);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.setSpriteFromAge(this.sprites);
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.rCol = this.rCol * 0.95F;
            this.gCol = this.gCol * 0.95F;
            this.bCol = this.bCol * 0.95F;
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

    public float getQuadSize(float scaleFactor) {
        return super.getQuadSize(scaleFactor);
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    public static class NukeFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public NukeFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false, 0XFFB300);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }

    public static class MineFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public MineFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XFFB300);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }

    public static class UnderzealotFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public UnderzealotFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false, 0);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }

    public static class AmberFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public AmberFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false, 0XFFDA1E);
            particle.setSpriteFromAge(spriteSet);
            particle.scale(0.8F);
            return particle;
        }
    }
}
