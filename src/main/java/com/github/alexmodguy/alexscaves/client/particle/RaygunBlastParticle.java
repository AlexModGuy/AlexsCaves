package com.github.alexmodguy.alexscaves.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class RaygunBlastParticle extends TextureSheetParticle {

    private Direction direction;

    private float randomRot = 0;

    protected RaygunBlastParticle(ClientLevel world, double x, double y, double z, Direction direction) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.direction = direction;
        this.hasPhysics = false;
        this.setSize(1.0F, 1.0F);
        this.setColor(1F, 1F, 1F);
        this.lifetime = world.random.nextInt(20) + 20;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.randomRot = (float) (Math.PI * 2F * world.random.nextFloat());
        this.quadSize = 0.2F + world.random.nextFloat() * 0.4F;
        this.friction = 0F;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float f = ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime;
        float f1 = this.age / (float) this.lifetime;
        float f2 = 1F - 0.1F * f1;
        friction = 1F - 0.65F * f1;
        if (this.age > lifetime / 2) {
            this.setAlpha(1.0F - f * 2F);
        }
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        BlockPos connectedTo = BlockPos.containing(this.x + direction.getStepX() * -0.1F, this.y + direction.getStepY() * -0.1F, this.z + direction.getStepZ() * -0.1F);
        BlockState state = level.getBlockState(connectedTo);
        if (this.age++ >= this.lifetime || state.isAir() || !state.isFaceSturdy(level, connectedTo, direction.getOpposite())) {
            this.remove();
        }else if(random.nextFloat() < 0.5F && this.age < this.lifetime / 2){
            this.level.addParticle(ParticleTypes.SMOKE.getType(), x, y, z, 0, 0, 0);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        this.renderSignal(vertexConsumer, camera, partialTick, (quaternionf) -> {
            quaternionf.mul(direction.getRotation());
            quaternionf.rotateX(-(float) Math.PI * 0.5F);
        });
        this.renderSignal(vertexConsumer, camera, partialTick, (quaternionf) -> {
            quaternionf.mul(direction.getRotation());
            quaternionf.rotateX((float) Math.PI - (float) Math.PI * 0.5F);
        });
    }

    private void renderSignal(VertexConsumer consumer, Camera camera, float partialTicks, Consumer<Quaternionf> rots) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTicks, this.zo, this.z) - vec3.z());
        Vector3f vector3f = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0.0F, vector3f.x(), vector3f.y(), vector3f.z());
        rots.accept(quaternionf);
        quaternionf.rotateZ(randomRot);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f1 = avector3f[i];
            vector3f1.rotate(quaternionf);
            vector3f1.mul(f3);
            vector3f1.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(partialTicks);
        consumer.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }


    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Direction direction = Direction.from3DDataValue((int)xSpeed);
            RaygunBlastParticle particle = new RaygunBlastParticle(worldIn, x, y, z, direction);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TremorzillaFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public TremorzillaFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Direction direction = Direction.from3DDataValue((int)xSpeed);
            RaygunBlastParticle particle = new RaygunBlastParticle(worldIn, x, y, z, direction);
            particle.pickSprite(spriteSet);
            particle.quadSize = 1.0F + worldIn.random.nextFloat() * 0.5F;
            particle.lifetime = 60 + worldIn.random.nextInt(20);
            return particle;
        }
    }
}
