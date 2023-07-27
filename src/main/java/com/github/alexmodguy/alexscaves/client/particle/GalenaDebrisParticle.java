package com.github.alexmodguy.alexscaves.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class GalenaDebrisParticle extends TextureSheetParticle {

    private boolean fromRoof;
    private float rotSpeed;
    private double hoverY;

    private int hoverTime = 0;

    public GalenaDebrisParticle(ClientLevel world, double x, double y, double z, boolean fromRoof, SpriteSet sprites) {
        super(world, x, y, z);
        this.pickSprite(sprites);
        this.fromRoof = fromRoof;
        this.gravity = fromRoof ? 0.001F : -0.001F;
        this.lifetime = 200 + random.nextInt(200);
        this.quadSize = 0.3F;
        this.rotSpeed = ((float) Math.random() - 0.5F) * 0.2F;
        this.roll = (float) Math.random() * ((float) Math.PI * 2F);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

    }

    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll += (float) Math.PI * this.rotSpeed * 2.0F;
        if (this.onGround) {
            this.oRoll = this.roll = 0.0F;
        }
        double targetY = Math.sin(age * 0.2F) * 0.01F;
        this.yd += targetY;
        int timeLeft = lifetime - age;
        if (timeLeft < 20) {
            this.quadSize = 0.3F * timeLeft / 20F;
        }
        age++;
        if (this.age >= this.lifetime || (this.yo == y) && age > 3) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.yd -= (double) this.gravity;
        }
    }

    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        super.render(consumer, camera, partialTick);
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
            boolean riseOrFall = worldIn.random.nextBoolean();
            Vec3 startPos = MagneticCavesAmbientParticle.getStartPosition(worldIn, riseOrFall, x, y, z);
            GalenaDebrisParticle particle = new GalenaDebrisParticle(worldIn, x, startPos.y, z, riseOrFall, spriteSet);
            return particle;
        }
    }

}