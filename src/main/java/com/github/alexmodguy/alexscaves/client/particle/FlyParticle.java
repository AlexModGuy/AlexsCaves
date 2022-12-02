package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlyParticle extends TextureSheetParticle {

    private SpriteSet spriteSet;
    private final double orbitX;
    private final double orbitY;
    private final double orbitZ;
    private boolean reverseOrbit;
    private float orbitSpeed = 1F;
    private Vec3 orbitOffset;

    protected FlyParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.quadSize *= 1F + world.random.nextFloat() * 0.3F;
        this.hasPhysics = true;
        this.orbitX = xSpeed;
        this.orbitY = ySpeed;
        this.orbitZ = zSpeed;
        this.spriteSet = spriteSet;
        this.lifetime = (int) (Math.random() * 10.0D) + 40;
        this.friction = 0.8F;
        this.orbitOffset = new Vec3((0.5F - random.nextFloat()) * 2.0F, 0, (0.5F - random.nextFloat()) * 2.0F);
        this.reverseOrbit = random.nextBoolean();
        this.orbitSpeed = 3 + random.nextFloat() * 3F;
    }

    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float) this.age + scaleFactor) / (float) this.lifetime * 16.0F, 0.0F, 1.0F);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        int sprite = this.age % 4 >= 2 ? 1 : 0;
        this.setSprite(spriteSet.get(sprite, 1));
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            Vec3 vec3 = getOrbitPosition(age);
            Vec3 movement = vec3.subtract(this.x, this.y, this.z).normalize().scale(0.1F);

            this.xd = movement.x + random.nextGaussian() * 0.015F;
            this.yd += movement.y + random.nextGaussian() * 0.015F;
            if (this.onGround) {
                yd += 0.3F;
            }
            this.zd += movement.z + random.nextGaussian() * 0.015F;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;

        }
    }

    private Vec3 getOrbitPosition(float angle) {
        Vec3 center = new Vec3(orbitX, orbitY, orbitZ);
        float rot = angle * (reverseOrbit ? -orbitSpeed : orbitSpeed) * (float) (Math.PI / 180F);
        return center.add(orbitOffset.yRot(rot));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FlyParticle heartparticle = new FlyParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            heartparticle.setSprite(spriteSet.get(0, 1));
            return heartparticle;
        }
    }
}
