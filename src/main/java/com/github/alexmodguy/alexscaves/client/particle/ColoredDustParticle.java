package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.client.render.misc.BlockColorFinder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredDustParticle extends TextureSheetParticle {

    private float initialAlpha = 0.5F;
    protected ColoredDustParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSize(0.5F, 0.5F);
        this.quadSize = 0.5F + world.random.nextFloat() * 0.2F;
        this.lifetime = 10 + world.random.nextInt(4);
        this.friction = 0.96F;
        this.setColor((float) xSpeed, (float) ySpeed, (float) zSpeed);
        this.initialAlpha = 0.0F;
        this.setAlpha(initialAlpha);
        this.xd = 0.05F * random.nextGaussian();
        this.yd = 0.15F * random.nextFloat();
        this.zd = 0.05F * random.nextGaussian();
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
        }
        float f = (float)this.age / this.lifetime;
        this.setAlpha(1F - f);
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
            ColoredDustParticle particle = new ColoredDustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }

    public static class SmallFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SmallFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ColoredDustParticle particle = new ColoredDustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(spriteSet);
            particle.quadSize = 0.2F + worldIn.random.nextFloat() * 0.15F;
            particle.lifetime = 15 + worldIn.random.nextInt(15);
            particle.xd = 0.0F;
            particle.yd = 0.1F * worldIn.random.nextFloat();
            particle.zd = 0.0F;
            particle.friction = 0.99F;
            return particle;
        }
    }
}
