package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VentSmokeParticle extends TextureSheetParticle {

    private final boolean fullbright;

    protected VentSmokeParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, boolean fullbright) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.setSize(0.5F, 0.5F);
        this.quadSize = 0.8F + world.random.nextFloat() * 0.3F;
        this.lifetime = (int) (Math.random() * 20.0D) + 40;
        this.friction = 0.99F;
        this.fullbright = fullbright;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime);
        }
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
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float) this.age + scaleFactor) / (float) this.lifetime * 4.0F, 0.0F, 1.0F);
    }

    public int getLightColor(float partialTicks) {
        int i = super.getLightColor(partialTicks);
        if (fullbright) {
            return 240;
        } else {
            return i;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class BlackFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public BlackFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VentSmokeParticle particle = new VentSmokeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, false);
            particle.pickSprite(spriteSet);
            float randCol = worldIn.random.nextFloat() * 0.05F;
            particle.setColor(randCol + 0.1F, randCol + 0.1F, randCol + 0.1F);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class WhiteFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public WhiteFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VentSmokeParticle particle = new VentSmokeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, false);
            particle.pickSprite(spriteSet);
            float randCol = worldIn.random.nextFloat() * 0.05F;
            particle.setColor(randCol + 0.95F, randCol + 0.95F, randCol + 0.95F);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class GreenFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public GreenFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VentSmokeParticle particle = new VentSmokeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, true);
            particle.pickSprite(spriteSet);
            float randCol = worldIn.random.nextFloat() * 0.05F;
            particle.setColor(randCol + 0.05F, randCol + 0.95F, 0);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class RedFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public RedFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VentSmokeParticle particle = new VentSmokeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, true);
            particle.pickSprite(spriteSet);
            float randCol = worldIn.random.nextFloat() * 0.15F;
            particle.setColor(randCol + 0.85F, randCol + 0.55F, randCol + 0.35F);
            return particle;
        }
    }
}
