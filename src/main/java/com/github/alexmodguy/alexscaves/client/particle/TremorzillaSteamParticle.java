package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.client.render.entity.TremorzillaRenderer;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class TremorzillaSteamParticle  extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final int tremorzillaId;
    private final Vec3 inMouthOffset;

    protected TremorzillaSteamParticle(ClientLevel level, double x, double y, double z, int tremorzillaId, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.quadSize *= 1.0F + random.nextFloat() * 3.0F;
        this.lifetime = 40 + random.nextInt(10);
        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
        this.tremorzillaId = tremorzillaId;
        this.inMouthOffset = new Vec3(random.nextBoolean() ? 0.9F : -0.9F, 0.7F + random.nextFloat() * 0.3F, random.nextFloat() * 2F - 1.2F);
        Vec3 vec3 = getInMouthPos(1.0F);
        this.setPos(vec3.x, vec3.y, vec3.z);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.gravity = -0.05F - random.nextFloat() * 0.05F;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        float f = (float)this.age / this.lifetime;
        this.setAlpha((1F - f));
    }

    public Vec3 getInMouthPos(float partialTick) {
        if (tremorzillaId != -1 && level.getEntity(tremorzillaId) instanceof TremorzillaEntity entity) {
            Vec3 mouthPos = TremorzillaRenderer.getMouthPositionFor(tremorzillaId);
            if (mouthPos != null) {
                Vec3 translate = mouthPos.add(inMouthOffset).yRot((float) (Math.PI - entity.yBodyRot * ((float) Math.PI / 180F)));
                return new Vec3(entity.getX() + translate.x, entity.getY() + translate.y, entity.getZ() + translate.z);
            }
        }
        return Vec3.ZERO;
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
            TremorzillaSteamParticle particle = new TremorzillaSteamParticle(worldIn, x, y, z, (int) xSpeed, spriteSet);
            float color = 0.2F * worldIn.random.nextFloat() + 0.6F;
            particle.setColor(color, color, color);
            return particle;
        }
    }

}
