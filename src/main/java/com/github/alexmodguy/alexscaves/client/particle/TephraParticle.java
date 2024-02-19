package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TephraParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    private float prevAlpha = 0.0F;

    private boolean spinning;
    private float spinIncrement;

    protected TephraParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet, boolean spinning) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = spriteSet;
        this.setSpriteFromAge(this.sprites);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.setSize(0.5F, 0.5F);
        this.quadSize = 1.0F + world.random.nextFloat() * 0.5F;
        this.lifetime = 10 + world.random.nextInt(20);
        this.friction = 0.99F;
        this.spinning = spinning;
        if (spinning) {
            this.roll = (float) Math.toRadians(360F * random.nextFloat());
            this.oRoll = roll;
            spinIncrement = (random.nextBoolean() ? -1 : 1) * random.nextFloat() * 0.4F;
        }
    }

    public void tick() {
        this.setSpriteFromAge(this.sprites);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float ageProgress = this.age / (float) lifetime;
        float f = ageProgress - 0.5F;
        float f1 = 1.0F - f * 2F;
        if (ageProgress > 0.5F) {
            prevAlpha = alpha;
            this.setAlpha(prevAlpha + (f1 - prevAlpha) * AlexsCaves.PROXY.getPartialTicks());
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
        }
        if (spinning) {
            this.oRoll = roll;
            this.roll += f1 * spinIncrement;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TephraParticle particle = new TephraParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SmallFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SmallFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TephraParticle particle = new TephraParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false);
            particle.quadSize = 0.5F + worldIn.random.nextFloat() * 0.25F;
            particle.lifetime = 10 + worldIn.random.nextInt(10);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class FlameFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public FlameFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TephraParticle particle = new TephraParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true);
            particle.quadSize = 0.5F + worldIn.random.nextFloat() * 0.25F;
            particle.lifetime = 20 + worldIn.random.nextInt(10);
            return particle;
        }
    }
}