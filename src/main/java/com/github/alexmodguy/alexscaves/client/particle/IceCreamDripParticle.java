package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class IceCreamDripParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private int onGroundTime;
    private int iceCreamType;

    protected IceCreamDripParticle(ClientLevel level, double x, double y, double z, int iceCreamType, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.98F;
        this.gravity = 0;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize *= 1.2F + random.nextFloat();
        this.lifetime = 100 + random.nextInt(40);
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
        int iceCreamColor = getIceCreamColorFromIndex(iceCreamType);
        this.rCol = (float) ((iceCreamColor & 16711680) >> 16) / 255.0F;
        this.gCol = (float) ((iceCreamColor & '\uff00') >> 8) / 255.0F;
        this.bCol = (float) ((iceCreamColor & 255) >> 0) / 255.0F;
        this.iceCreamType = iceCreamType;
    }

    public static int getIceCreamColorFromIndex(int iceCreamType) {
        switch (iceCreamType) {
            case 0:
                return 0XFFFCE9;
            case 1:
                return 0X9F6D4B;
            case 2:
                return 0XFFD6D6;
        }
        return 0;
    }

    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float) this.age + f) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    public void tick() {
        if (this.age < this.lifetime * 0.25F) {
            this.gravity = 0;
        } else {
            this.gravity = 1F;
            if (onGround) {
                onGroundTime++;
            }
        }
        int sprite = this.onGround ? 1 : 0;
        this.setSprite(sprites.get(sprite, 1));
        if (onGroundTime > 5) {
            this.remove();
        }else if(onGroundTime == 1){
            for(int i = 0; i < 3 + random.nextInt(2); i++){
                this.level.addParticle(ACParticleRegistry.ICE_CREAM_SPLASH.get(), this.x, this.y + 0.05F, this.z, 0.1F * (random.nextFloat() - 0.5F), this.iceCreamType, 0.1F * (random.nextFloat() - 0.5F));
            }
        }
        super.tick();
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
            IceCreamDripParticle particle = new IceCreamDripParticle(worldIn, x, y, z, (int) xSpeed, spriteSet);
            return particle;
        }
    }
}
