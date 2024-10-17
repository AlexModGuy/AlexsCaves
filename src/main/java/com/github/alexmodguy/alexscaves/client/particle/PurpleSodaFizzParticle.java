package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PurpleSodaFizzParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected PurpleSodaFizzParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteSet, boolean spinning) {
        super(world, x, y, z);
        this.sprites = spriteSet;
        this.setSpriteFromAge(this.sprites);
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.friction = 0.99F;
        this.setSize(0.5F, 0.5F);
        this.quadSize = 0.2F + random.nextFloat() * 0.1F;
        this.lifetime = 4 + world.random.nextInt(2);
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
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
            return new PurpleSodaFizzParticle(worldIn, x, y, z, spriteSet, false);
        }
    }


}