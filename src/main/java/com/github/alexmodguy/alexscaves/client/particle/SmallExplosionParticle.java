package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexthe666.citadel.repack.jcodec.scale.ColorUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;

public class SmallExplosionParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private boolean hasFadeColor = false;
    private float fadeR;
    private float fadeG;
    private float fadeB;
    protected SmallExplosionParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, boolean shortLifespan, int color1) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.setSize(0.5F, 0.5F);
        this.quadSize = (shortLifespan ? 1 : 0.8F) + world.random.nextFloat() * 0.3F;
        this.lifetime = shortLifespan ? 5 + world.random.nextInt(3) : 15 + world.random.nextInt(10);
        this.friction = 0.96F;
        float randCol = world.random.nextFloat() * 0.05F;
        this.sprites = sprites;
        this.setColor(Math.min(FastColor.ARGB32.red(color1) / 255F + randCol, 1), Math.min(1F, FastColor.ARGB32.green(color1) / 255F + randCol), Math.min(1F, FastColor.ARGB32.blue(color1) / 255F + randCol));
    }

    public void setFadeColor(int i){
        hasFadeColor = true;
        this.fadeR = (float) ((i & 16711680) >> 16) / 255.0F;
        this.fadeG = (float) ((i & '\uff00') >> 8) / 255.0F;
        this.fadeB = (float) ((i & 255) >> 0) / 255.0F;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.setSpriteFromAge(this.sprites);
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            if(hasFadeColor){
                this.rCol += (fadeR - this.rCol) * 0.2F;
                this.gCol += (fadeG - this.gCol) * 0.2F;
                this.bCol += (fadeB - this.bCol) * 0.2F;
            }else{
                this.rCol = this.rCol * 0.95F;
                this.gCol = this.gCol * 0.95F;
                this.bCol = this.bCol * 0.95F;
            }
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public float getQuadSize(float scaleFactor) {
        return super.getQuadSize(scaleFactor);
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    public static class NukeFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public NukeFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false, 0XFFB300);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }

    public static class MineFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public MineFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XFFB300);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }

    public static class UnderzealotFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public UnderzealotFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false, 0);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }

    public static class RaygunFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public RaygunFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XEEEEEE);
            particle.setSpriteFromAge(spriteSet);
            particle.lifetime = 5 + worldIn.random.nextInt(3);
            particle.scale(0.6F + worldIn.random.nextFloat() * 0.3F);
            particle.setFadeColor(0X40EE40);
            return particle;
        }
    }

    public static class BlueRaygunFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public BlueRaygunFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XEEEEEE);
            particle.setSpriteFromAge(spriteSet);
            particle.lifetime = 5 + worldIn.random.nextInt(5);
            particle.scale(0.5F + worldIn.random.nextFloat() * 0.5F);
            particle.setFadeColor(0X40EEDA);
            return particle;
        }
    }

    public static class TremorzillaFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public TremorzillaFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XEEEEEE);
            particle.setSpriteFromAge(spriteSet);
            particle.lifetime = 9 + worldIn.random.nextInt(3);
            particle.scale(1.0F + worldIn.random.nextFloat() * 0.9F);
            particle.setFadeColor(0X9BFF3D);
            return particle;
        }
    }

    public static class TremorzillaRetroFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public TremorzillaRetroFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XEEEEEE);
            particle.setSpriteFromAge(spriteSet);
            particle.lifetime = 9 + worldIn.random.nextInt(3);
            particle.scale(1.0F + worldIn.random.nextFloat() * 0.9F);
            particle.setFadeColor(0XE06EFF);
            return particle;
        }
    }


    public static class TremorzillaTectonicFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public TremorzillaTectonicFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XEEEEEE);
            particle.setSpriteFromAge(spriteSet);
            particle.lifetime = 9 + worldIn.random.nextInt(3);
            particle.scale(1.0F + worldIn.random.nextFloat() * 0.9F);
            particle.setFadeColor(0XFFD631);
            return particle;
        }
    }

    public static class AmberFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public AmberFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, false, 0XFFDA1E);
            particle.setSpriteFromAge(spriteSet);
            particle.scale(0.8F);
            return particle;
        }
    }

    public static class TotemFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public TotemFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XFF0000);
            particle.setSpriteFromAge(spriteSet);
            particle.lifetime = 5 + worldIn.random.nextInt(3);
            particle.scale(1.2F + worldIn.random.nextFloat() * 0.3F);
            particle.setFadeColor(0);
            return particle;
        }
    }

    public static class PurpleWitchFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public PurpleWitchFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XFF69FF);
            particle.setSpriteFromAge(spriteSet);
            particle.scale(0.8F);
            particle.setFadeColor(0XFFFFFF);
            return particle;
        }
    }

    public static class ConversionCrucibleFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public ConversionCrucibleFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, 0.0D, 0.0D, 0.0D, spriteSet, true, FastColor.ARGB32.color(255, (int)(255F * xSpeed), (int)(255F * ySpeed), (int)(255F * zSpeed)));
            particle.setSpriteFromAge(spriteSet);
            particle.scale(0.8F);
            particle.setFadeColor(0XFFFFFF);
            return particle;
        }
    }

    public static class FrostmintFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public FrostmintFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SmallExplosionParticle particle = new SmallExplosionParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, true, 0XFFFFFF);
            particle.quadSize *= 1.6F;
            particle.setSpriteFromAge(spriteSet);
            particle.setFadeColor(0XE5F9FA);
            return particle;
        }
    }
}
