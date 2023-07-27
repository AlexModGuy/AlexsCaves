package com.github.alexmodguy.alexscaves.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;


public class MagneticCavesAmbientParticle extends Particle {

    protected MagneticCavesAmbientParticle(ClientLevel clientLevel, double x, double y, double z) {
        super(clientLevel, x, y, z);
        this.lifetime = 1;
        this.gravity = 0;
    }

    public void tick() {
        Entity entity = Minecraft.getInstance().getCameraEntity();

        if (this.random.nextFloat() > 0.85F && (entity == null || entity.distanceToSqr(x, y, z) > 25)) {
            Vec3 offset = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).scale(30);
            Vec3 startPos = getStartPosition(level, random.nextBoolean(), x + offset.x, y + offset.y, z + offset.z);
            this.level.addParticle(ACParticleRegistry.MAGNET_LIGHTNING.get(), startPos.x, startPos.y, startPos.z, 0, 0, 0);
        } else {
            this.level.addParticle(ACParticleRegistry.GALENA_DEBRIS.get(), x, y, z, 0, 0, 0);
        }
        super.tick();
        this.remove();
    }

    @Override
    public void render(VertexConsumer p_107261_, Camera p_107262_, float p_107263_) {

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.NO_RENDER;
    }

    public static Vec3 getStartPosition(ClientLevel level, boolean goUp, double x, double y, double z) {
        BlockPos pos = BlockPos.containing(x, y, z);
        if (goUp) {
            while (pos.getY() < level.getMaxBuildHeight() && level.getBlockState(pos).isAir()) {
                pos = pos.above();
            }
        } else {
            while (pos.getY() > level.getMinBuildHeight() && level.getBlockState(pos).isAir()) {
                pos = pos.below();
            }
        }
        return Vec3.atBottomCenterOf(pos).add(0, goUp ? 0 : 1, 0);
    }


    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory() {
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new MagneticCavesAmbientParticle(worldIn, x, y, z);
        }
    }

}
