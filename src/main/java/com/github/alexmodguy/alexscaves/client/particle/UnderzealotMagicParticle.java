package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class UnderzealotMagicParticle extends TextureSheetParticle {
    private static final RandomSource RANDOM = RandomSource.create();
    private final SpriteSet sprites;
    private Vec3 target;

    public UnderzealotMagicParticle(ClientLevel level, double x, double y, double z, double toX, double toY, double toZ, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0);
        this.friction = 0.96F;
        this.gravity = 0.1F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = spriteSet;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yd = (double) 0.0F;
        this.xd = (double) 0.0F;
        this.zd = (double) 0.0F;
        this.quadSize *= 1.15F;
        this.lifetime = (int) (20.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.target = new Vec3(toX, toY, toZ);
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
        Vec3 to = this.target.subtract(x, y, z);
        if (to.length() > 1F) {
            to = to.normalize();
        }
        this.xd += to.x * 0.03F;
        this.yd += to.y * 0.03F;
        this.zd += to.z * 0.03F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet p_107868_) {
            this.sprite = p_107868_;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            UnderzealotMagicParticle spellparticle = new UnderzealotMagicParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            float f = worldIn.random.nextFloat() * 0.15F + 0.85F;
            spellparticle.setColor(0.1F * f, 0.1F * f, 0.1F * f);
            return spellparticle;
        }
    }

}
