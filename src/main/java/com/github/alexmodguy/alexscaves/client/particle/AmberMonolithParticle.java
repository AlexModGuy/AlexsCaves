package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AmberMonolithParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private Vec3 target;

    private float initialDistance = 0;

    public AmberMonolithParticle(ClientLevel level, double x, double y, double z, double toX, double toY, double toZ, SpriteSet spriteSet) {
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
        this.quadSize *= 1.125F;
        this.lifetime = (int) (20.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.target = new Vec3(toX, toY, toZ);
        this.setSpriteFromAge(spriteSet);
        this.initialDistance = (float) target.subtract(x, y, z).length();
        this.rCol = 1.0F;
        this.gCol = 0.69F;
        this.bCol = 0.12F;

    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float partialTicks) {
        Vec3 to = this.target.subtract(x, y, z);
        float glowBy = (float) (to.length() / this.initialDistance);
        int i = super.getLightColor(partialTicks);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int) (glowBy * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.setAlpha(Mth.lerp(0.05F, this.alpha, 1.0F));
        Vec3 to = this.target.subtract(x, y, z);
        if (to.length() > 1F) {
            to = to.normalize();
        } else {
            this.remove();
        }
        this.xd += to.x * 0.05F;
        this.yd += to.y * 0.05F;
        this.zd += to.z * 0.05F;
        this.rCol = Math.min(1.0F, this.rCol + 0.03F);
        this.gCol = Math.min(1.0F, this.gCol + 0.03F);
        this.bCol = Math.min(1.0F, this.bCol + 0.03F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet p_107868_) {
            this.sprite = p_107868_;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            AmberMonolithParticle spellparticle = new AmberMonolithParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            return spellparticle;
        }
    }

}
