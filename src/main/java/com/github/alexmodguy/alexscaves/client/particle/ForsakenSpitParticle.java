package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.client.render.entity.ForsakenRenderer;
import com.github.alexmodguy.alexscaves.server.entity.living.ForsakenEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ForsakenSpitParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private int onGroundTime;
    private final int forsakenId;
    private Vec3 inMouthOffset;

    protected ForsakenSpitParticle(ClientLevel level, double x, double y, double z, int forsakenId, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.gravity = 0;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize *= 0.8F + random.nextFloat() * 0.5F;
        this.lifetime = 100 + random.nextInt(40);
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
        this.forsakenId = forsakenId;
        this.inMouthOffset = new Vec3(random.nextBoolean() ? 0.3F : -0.3F, -0.0F, random.nextFloat() * 0.7F - 0.2F);
        setInMouthPos(1.0F);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.alpha = 0.5F;
    }

    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float) this.age + f) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    public void tick() {
        super.tick();
        if (this.isInMouth()) {
            this.gravity = 0;
            this.xd = 0;
            this.yd = 0;
            this.zd = 0;
            this.setInMouthPos(1.0F);
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
        }
    }

    public boolean isInMouth() {
        return this.age < this.lifetime * 0.25F;
    }

    public void setInMouthPos(float partialTick) {
        if (forsakenId != -1 && level.getEntity(forsakenId) instanceof ForsakenEntity entity) {
            Vec3 mouthPos = ForsakenRenderer.getMouthPositionFor(forsakenId);
            if (mouthPos != null) {
                Vec3 translate = mouthPos.add(inMouthOffset).yRot((float) (Math.PI - entity.yBodyRot * ((float) Math.PI / 180F)));
                this.setPos(entity.getX() + translate.x, entity.getY() + translate.y, entity.getZ() + translate.z);
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }


    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ForsakenSpitParticle particle = new ForsakenSpitParticle(worldIn, x, y, z, (int) xSpeed, spriteSet);
            return particle;
        }
    }
}
