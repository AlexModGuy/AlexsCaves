package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.DinosaurEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DinosaurTransformParticle extends AbstractTrailParticle {
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/trail.png");

    private int dinosaurId;
    private float lastDinosaurWidth;

    private float initialWidth;
    private float initialYRot;
    private float rotateByAge;
    private float initialOrbitHeight = -1;

    public DinosaurTransformParticle(ClientLevel world, double x, double y, double z, int dinosaurId) {
        super(world, x, y, z, 0, 0, 0);
        this.dinosaurId = dinosaurId;
        this.gravity = 0;
        this.lifetime = 20 + this.random.nextInt(20);
        initialYRot = random.nextFloat() * 360F;
        rotateByAge = (10 + random.nextFloat() * 20F) * (random.nextBoolean() ? -1F : 1F);
        Vec3 vec3 = getOrbitPosition();
        this.x = this.xo = vec3.x;
        this.y = this.yo = vec3.y;
        this.z = this.zo = vec3.z;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
    }

    public Vec3 getDinosaurPosition(){
        if(dinosaurId != -1 && level.getEntity(dinosaurId) instanceof DinosaurEntity dinosaur){
            lastDinosaurWidth = dinosaur.getBbWidth();
            if(initialOrbitHeight == -1){
                initialOrbitHeight = random.nextFloat() * dinosaur.getBbHeight();
                initialWidth = lastDinosaurWidth + random.nextFloat();
            }
            return dinosaur.position();
        }
        return new Vec3(this.x, this.y, this.z);
    }

    public Vec3 getOrbitPosition(){
        Vec3 dinoPos = getDinosaurPosition();
        Vec3 vec3 = new Vec3(0,  initialOrbitHeight, initialWidth).yRot((float)Math.toRadians(initialYRot + rotateByAge * age));
        return dinoPos.add(vec3);
    }

    public void tick() {
        super.tick();
        float fade = 1F - age / (float) lifetime;
        this.trailA = 1F * fade;
        Vec3 vec3 = getOrbitPosition();
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
    }

    public int sampleCount() {
        return 4;
    }

    public int sampleStep() {
        return 1;
    }

    @Override
    public float getTrailHeight() {
        return 0.5F;
    }

    public int getLightColor(float f) {
        return 240;
    }

    @Override
    public ResourceLocation getTrailTexture() {
        return TRAIL_TEXTURE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class AmberFactory implements ParticleProvider<SimpleParticleType> {

        public AmberFactory() {
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DinosaurTransformParticle particle = new DinosaurTransformParticle(worldIn, x, y, z, (int)xSpeed);
            particle.trailR = 1.0F;
            particle.trailG = 0.69F + worldIn.random.nextFloat() * 0.025F;
            particle.trailB = 0.11F + worldIn.random.nextFloat() * 0.025F;
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TectonicFactory implements ParticleProvider<SimpleParticleType> {

        public TectonicFactory() {
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DinosaurTransformParticle particle = new DinosaurTransformParticle(worldIn, x, y, z, (int)xSpeed);
            particle.trailR = 0.9F + worldIn.random.nextFloat() * 0.05F;
            particle.trailG = 0.1F;
            particle.trailB = 0.1F;
            return particle;
        }
    }
}
