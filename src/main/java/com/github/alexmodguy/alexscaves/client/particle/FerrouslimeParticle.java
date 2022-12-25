package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FerrouslimeParticle extends TextureSheetParticle {

    private final int followEntityId;

    protected FerrouslimeParticle(ClientLevel world, double x, double y, double z, double followEntityId, SpriteSet spriteSet) {
        super(world, x, y, z, 0, 0, 0);
        this.quadSize *= 1.2F + world.random.nextFloat() * 0.5F;
        this.hasPhysics = true;
        this.followEntityId = (int) followEntityId;
        this.pickSprite(spriteSet);
        this.lifetime = (int) (Math.random() * 30.0D) + 40;
        this.rCol = 0.22F - (world.random.nextFloat() * 0.05F);
        this.gCol = 0.23F - (world.random.nextFloat() * 0.05F);
        this.bCol = 0.32F - (world.random.nextFloat() * 0.05F);
        this.friction = 0.8F;
        this.alpha = 0.5F + world.random.nextFloat() * 0.3F;
    }

    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float) this.age + scaleFactor) / (float) this.lifetime * 16.0F, 0.0F, 1.0F);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            Entity entity = level.getEntity(followEntityId);
            if(entity != null){
                Vec3 vec3 = entity.getEyePosition().add(entity.getDeltaMovement());
                Vec3 movement = vec3.subtract(this.x, this.y, this.z).normalize().scale(0.05F);
                this.xd = movement.x;
                this.yd += movement.y;
                this.zd += movement.z;
            }

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

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FerrouslimeParticle particle = new FerrouslimeParticle(worldIn, x, y, z, xSpeed, this.spriteSet);
            return particle;
        }
    }
}
