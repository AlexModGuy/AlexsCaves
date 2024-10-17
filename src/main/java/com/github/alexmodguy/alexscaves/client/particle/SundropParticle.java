package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SundropParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final float initialSize;
    private BlockPos blockPos;
    protected SundropParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = spriteSet;
        this.setSpriteFromAge(this.sprites);
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.blockPos = BlockPos.containing(x, y, z);
        this.setSize(2.5F, 2.5F);
        this.initialSize = 1.1F + world.random.nextFloat() * 0.4F;
        this.quadSize = this.initialSize;
        this.lifetime = 80 + world.random.nextInt(40);
        this.friction = 0.99F;
        this.setAlpha(0.0F);
    }

    public void tick() {
        this.setSpriteFromAge(this.sprites);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float ageProgress = this.age / (float) lifetime;
        float flicker = 0.5F + (float) (Math.sin(age * 0.2F) + 1.0F) * 0.25F;
        float ageAlpha = (float) Math.sin(Mth.sqrt(ageProgress) * Math.PI);
        this.setAlpha(flicker * ageAlpha);
        this.quadSize = this.initialSize + flicker * 0.1F;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
        }
        if(!level.getBlockState(blockPos).is(ACBlockRegistry.SUNDROP.get())){
            this.remove();
        }
    }

    public void remove() {
        super.remove();
        ((ClientProxy) AlexsCaves.PROXY).removeParticleAt(this.blockPos);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SundropParticle particle = new SundropParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return particle;
        }
    }
}