package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class PurpleSodaBubbleEmitterParticle extends NoRenderParticle {

    private float initalRot = 0;
    private float rotBy = 0;

    public PurpleSodaBubbleEmitterParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(world, x, y, z);
        this.lifetime = 120;
        this.rotBy = (25F + random.nextFloat() * 15F) * (random.nextBoolean() ? -1 : 1);
        this.initalRot = random.nextFloat() * 360;
    }

    public void tick() {
        super.tick();
        Vec3 randOffset = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5).scale(0.2F);
        Vec3 rotation = new Vec3(0, 0, 0.02F).yRot((float)Math.toRadians(initalRot + rotBy * age));
        Vec3 delta = rotation;
        this.level.addParticle(ACParticleRegistry.PURPLE_SODA_BUBBLE.get(), this.x + randOffset.x, this.y + randOffset.y, this.z + randOffset.z, delta.x, delta.y, delta.z);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new PurpleSodaBubbleEmitterParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        }
    }

}