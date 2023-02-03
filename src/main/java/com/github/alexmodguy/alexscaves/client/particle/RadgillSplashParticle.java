package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class RadgillSplashParticle extends TextureSheetParticle {

    private boolean fallLeft;
    public RadgillSplashParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(world, x, y, z);
        this.pickSprite(sprites);
        this.lifetime = 10 + random.nextInt(10);
        this.quadSize = 0.1F + random.nextFloat() * 0.3F;
        this.gravity = 0.1F;
        this.roll = 0;
        this.fallLeft = random.nextBoolean();
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

    }

    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        float downRollTarget = (float) Math.PI;
        if(fallLeft){
            if(yd < 0 && roll > -downRollTarget){
                roll = Math.min(roll - downRollTarget * 0.2F, -downRollTarget);
            }
        }else{
            if(yd < 0 && roll < downRollTarget){
                roll = Math.min(roll + downRollTarget * 0.2F, downRollTarget);
            }
        }
        this.roll += 0;
        age++;
        if (this.age >= this.lifetime || (this.yo == y) && age > 3) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.yd -= (double) this.gravity;
        }
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }


    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            RadgillSplashParticle particle = new RadgillSplashParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return particle;
        }
    }

}