package com.github.alexmodguy.alexscaves.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SleepParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private float yRotAngle = 0;
    private float rotations;
    private static final Vec3 ROTATE_BY = new Vec3(1F, 0, 0);

    protected SleepParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = spriteSet;
        this.yRotAngle = (float) (Math.PI * 2 * random.nextFloat());
        this.rotations = random.nextFloat() * 5F + 5F;
        this.setSpriteFromAge(this.sprites);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.setSize(0.5F, 0.5F);
        this.quadSize = 0.1F + world.random.nextFloat() * 0.1F;
        this.lifetime = 10 + world.random.nextInt(20);
        this.friction = 0.95F;
    }

    public void tick() {
        super.tick();
        float ageLerp = age / (float)lifetime;
        this.quadSize = Math.max(0.0F, this.quadSize - 0.008F);
        Vec3 vec3 = ROTATE_BY.yRot(yRotAngle + this.rotations * ageLerp);
        this.xd += vec3.x * 0.05;
        this.zd += vec3.z * 0.05;
        this.setSpriteFromAge(this.sprites);
        this.xd *= 0.8D;
        this.zd *= 0.8D;
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
            SleepParticle particle = new SleepParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return particle;
        }
    }
}