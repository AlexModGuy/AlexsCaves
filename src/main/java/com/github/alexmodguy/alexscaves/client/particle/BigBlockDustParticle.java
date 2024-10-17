package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.client.render.misc.BlockColorFinder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.state.BlockState;

public class BigBlockDustParticle extends TextureSheetParticle {

    private float initialAlpha = 0.5F;
    protected BigBlockDustParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockState state) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSize(0.5F, 0.5F);
        this.quadSize = 1.5F + world.random.nextFloat() * 0.4F;
        this.lifetime = 10 + world.random.nextInt(4);
        this.friction = 0.96F;
        this.setColor(BlockColorFinder.getBlockColor(state, world, BlockPos.containing(x, y, z)));
        this.initialAlpha = 0.0F;
        this.setAlpha(initialAlpha);
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
        }
        float f = (float)this.age / this.lifetime;
        this.setAlpha(0.5F * (1F - f));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public float getQuadSize(float scaleFactor) {
        return super.getQuadSize(scaleFactor);
    }

    private void setColor(int intcolor) {
        float f = (float) ((intcolor & 16711680) >> 16) / 255.0F;
        float f1 = (float) ((intcolor & '\uff00') >> 8) / 255.0F;
        float f2 = (float) ((intcolor & 255) >> 0) / 255.0F;
        float f3 = random.nextFloat() * 0.3F + 0.7F;
        this.setColor(f * f3, f1 * f3, f2 * f3);
    }

    public static class Factory implements ParticleProvider<BlockParticleOption> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(BlockParticleOption typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BigBlockDustParticle particle = new BigBlockDustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getState());
            particle.pickSprite(spriteSet);
            return particle;
        }
    }
}
