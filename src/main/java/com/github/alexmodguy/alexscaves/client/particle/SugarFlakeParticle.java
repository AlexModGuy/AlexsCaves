package com.github.alexmodguy.alexscaves.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class SugarFlakeParticle extends TextureSheetParticle {

    private final float particleRandom;
    private final float maxLife;
    private final  SpriteSet sprites;
    public SugarFlakeParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z);
        this.sprites = sprites;
        this.maxLife = 50 + random.nextInt(50);
        this.lifetime = (int) maxLife;
        this.quadSize = 0.15F + random.nextFloat() * 0.15F;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.particleRandom = this.random.nextFloat();
        this.gravity = -0.01F * this.random.nextFloat();
        this.alpha = 0.0F;
        this.setSprite(sprites.get(this.lifetime, this.lifetime));
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }


    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        }
        if (!this.removed) {
            this.setSprite(sprites.get((int) (maxLife - this.lifetime), (int)this.maxLife));
            float f = (float)(maxLife - this.lifetime);
            float f1 = Math.min(f / maxLife, 1.0F);
            float worldRotation = level.getGameTime() / 3.0F % 360.0F;
            double d0 = Math.cos(Math.toRadians((double)(this.particleRandom * 30.0F + worldRotation))) * 2.0D * Math.pow((double)f1, 1.25D);
            double d1 = Math.sin(Math.toRadians((double)(this.particleRandom * 30.0F + worldRotation))) * 2.0D * Math.pow((double)f1, 1.25D);
            this.xd += d0 * (double)0.01F;
            this.zd += d1 * (double)0.01F;
            this.yd -= (double)this.gravity * 0.025F;
            this.move(this.xd, this.yd, this.zd);
            if (this.onGround || this.lifetime < maxLife - 1 && (this.xd == 0.0D || this.zd == 0.0D)) {
                this.remove();
            }

            if (!this.removed) {
                this.xd *= (double)this.friction;
                this.yd *= (double)this.friction;
                this.zd *= (double)this.friction;
            }
            this.alpha = (float) Math.sin((this.lifetime / maxLife) * Math.PI) * 0.7F;
        }
    }

    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        super.render(consumer, camera, partialTick);
    }


    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Vec3 startPos = MagneticCavesAmbientParticle.getStartPosition(worldIn, false, x, y, z).add(0, 0.15F + 0.5F * worldIn.random.nextFloat(), 0);
            SugarFlakeParticle particle = new SugarFlakeParticle(worldIn, x, startPos.y, z, spriteSet);
            return particle;
        }
    }

}