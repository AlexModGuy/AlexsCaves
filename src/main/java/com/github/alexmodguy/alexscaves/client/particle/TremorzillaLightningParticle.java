package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.client.render.entity.TremorzillaRenderer;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class TremorzillaLightningParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final int tremorzillaId;
    private final Vec3 inMouthOffset;
    private final Vec3 inMouthOrigin;
    private final Vec3 beamPosOffset;

    protected TremorzillaLightningParticle(ClientLevel level, double x, double y, double z, int tremorzillaId, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.gravity = 0;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize *= 2.0F + random.nextFloat() * 2.0F;
        this.lifetime = 2;
        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
        this.tremorzillaId = tremorzillaId;
        this.inMouthOffset = new Vec3(random.nextBoolean() ? 0.8F : -0.8F, random.nextFloat() * 0.8F, random.nextFloat() * 0.8F - 0.2F);
        this.beamPosOffset = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).scale(6.0F);
        Vec3 vec3 = getInMouthPos(1.0F);
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
        this.inMouthOrigin = vec3;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.roll = (float) Math.toRadians(random.nextInt(3) * 90F);
        this.oRoll = roll;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        if (tremorzillaId != -1 && level.getEntity(tremorzillaId) instanceof TremorzillaEntity entity) {
            Vec3 beamPos = entity.getClientBeamEndPosition(1.0F);
            Vec3 inMouthPos = getInMouthPos(1.0F);
            if(entity.getBeamProgress(1.0F) > 0 && beamPos != null){
                Vec3 dist = beamPos.add(beamPosOffset).subtract(inMouthPos);
                int distInTicks = (int)Math.ceil(beamPos.length() * 0.005F);
                this.lifetime = Math.max(this.lifetime, distInTicks);
                float f = Mth.clamp(this.age / (float)lifetime, 0F, 1F);
                Vec3 setPosVec = inMouthPos.add(dist.scale(f));
                this.setPos(setPosVec.x, setPosVec.y, setPosVec.z);
            }else{
                this.remove();
            }
        }
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
            TremorzillaLightningParticle particle = new TremorzillaLightningParticle(worldIn, x, y, z, (int) xSpeed, spriteSet);
            return particle;
        }
    }

}
