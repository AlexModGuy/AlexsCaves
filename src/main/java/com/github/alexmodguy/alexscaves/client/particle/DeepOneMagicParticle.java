package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DeepOneMagicParticle extends TextureSheetParticle {
    private static final RandomSource RANDOM = RandomSource.create();
    private final SpriteSet sprites;

    public DeepOneMagicParticle(ClientLevel level, double x, double y, double z, double xDelta, double yDelta, double zDelta, SpriteSet spriteSet) {
        super(level, x, y, z, 0.5D - RANDOM.nextDouble(), yDelta, 0.5D - RANDOM.nextDouble());
        this.friction = 0.96F;
        this.gravity = 0.1F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = spriteSet;
        this.yd *= (double) 0.2F;
        if (xDelta == 0.0D && zDelta == 0.0D) {
            this.xd *= (double) 0.1F;
            this.zd *= (double) 0.1F;
        }
        this.quadSize *= 1.15F;
        this.lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.setSpriteFromAge(spriteSet);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.setAlpha(Mth.lerp(0.05F, this.alpha, 1.0F));
    }



    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet p_107868_) {
            this.sprite = p_107868_;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DeepOneMagicParticle spellparticle = new DeepOneMagicParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            float f = worldIn.random.nextFloat() * 0.15F + 0.85F;
            spellparticle.setColor(0.1F * f, 0.9F * f, 1.0F * f);
            return spellparticle;
        }
    }

}
