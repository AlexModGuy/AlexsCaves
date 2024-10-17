package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class CaramelDropParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected CaramelDropParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.quadSize = 0.15F + world.random.nextFloat() * 0.05F;
        this.lifetime = 8 + world.random.nextInt(8);
        this.sprites = sprites;
        this.setSpriteFromAge(this.sprites);
        this.friction = 0.6F;
        this.yd = 0;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CaramelDropParticle particle = new CaramelDropParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return particle;
        }
    }
}
