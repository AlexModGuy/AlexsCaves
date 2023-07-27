package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BigSplashEffectParticle extends WaterDropParticle {

    public BigSplashEffectParticle(ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
        super(level, x, y, z);
        this.gravity = 0.04F;
        this.xd = xMotion;
        this.yd = yMotion;
        this.zd = zMotion;
        this.lifetime = (int) (15.0D / (Math.random() * 0.8D + 0.2D));

    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
            BigSplashEffectParticle splashparticle = new BigSplashEffectParticle(clientLevel, x, y, z, xMotion, yMotion, zMotion);
            splashparticle.pickSprite(this.sprite);
            return splashparticle;
        }
    }
}
