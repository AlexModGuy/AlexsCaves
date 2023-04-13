package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WaterFoamParticle extends TextureSheetParticle {

    private float fadeR;
    private float fadeG;
    private float fadeB;

    protected WaterFoamParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.setSize(0.35F, 0.35F);
        this.setColor(1F, 1F, 1F);
        this.quadSize = 0.3F + world.random.nextFloat() * 0.3F;
        this.lifetime = (int) (Math.random() * 5.0D) + 4;
        this.setFadeColor(BiomeColors.getAverageWaterColor(level, BlockPos.containing(x, y, z)));
        this.friction = 0.9F;
    }


    public void setFadeColor(int p_107660_) {
        this.fadeR = (float)((p_107660_ & 16711680) >> 16) / 255.0F;
        this.fadeG = (float)((p_107660_ & '\uff00') >> 8) / 255.0F;
        this.fadeB = (float)((p_107660_ & 255) >> 0) / 255.0F;
    }


    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
        }
        this.rCol += (fadeR - this.rCol) * 0.25F;
        this.gCol += (fadeG - this.gCol) * 0.25F;
        this.bCol += (fadeB - this.bCol) * 0.25F;
        this.yd -= 0.1F;
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

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            WaterFoamParticle particle = new WaterFoamParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }


}
