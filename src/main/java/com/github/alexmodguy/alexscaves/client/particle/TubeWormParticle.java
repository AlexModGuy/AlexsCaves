package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.TubeWormModel;
import com.github.alexmodguy.alexscaves.server.block.TubeWormBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TubeWormParticle extends Particle {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/tube_worm.png");
    private static final TubeWormModel MODEL = new TubeWormModel();
    private BlockPos blockPos;
    private int checkScareCooldown;
    private float tuckAmount;
    private float prevTuckAmount;

    private float yRot;
    private float animationOffset;
    private boolean scared;

    protected TubeWormParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.gravity = 0.0F;
        this.lifetime = 90 + random.nextInt(50);
        this.setSize(0.6F, 1.5F);
        this.blockPos = BlockPos.containing(x, y, z);
        this.animationOffset = ACMath.sampleNoise3D((int) x, (int) y, (int) z, 6);
        this.checkScareCooldown = 5 + random.nextInt(10);
        this.prevTuckAmount = this.tuckAmount = 1;
        this.yRot = Direction.from2DDataValue(2 + random.nextInt(3)).toYRot();
    }

    public boolean shouldCull() {
        return false;
    }

    public void tick() {
        super.tick();
        prevTuckAmount = tuckAmount;
        if (checkScareCooldown-- <= 0) {
            this.checkScareCooldown = 10 + random.nextInt(10);
            Vec3 vec3 = Vec3.atCenterOf(blockPos);
            AABB scareBox = new AABB(vec3.add(-5, -1.5, -5), vec3.add(5, 3, 5));
            this.scared = !this.level.getEntitiesOfClass(LivingEntity.class, scareBox).isEmpty();
        }
        float targetTuckAmount;
        BlockState state = level.getBlockState(blockPos);
        if (scared || this.age >= this.lifetime - 10 || !state.getFluidState().is(FluidTags.WATER) || !level.getFluidState(blockPos.above()).is(FluidTags.WATER)) {
            targetTuckAmount = 1.0F;
        } else {
            targetTuckAmount = 0F;
        }
        if (tuckAmount < targetTuckAmount) {
            tuckAmount = Math.min(targetTuckAmount, tuckAmount + 0.1F);
        }
        if (tuckAmount > targetTuckAmount) {
            tuckAmount = Math.max(targetTuckAmount, tuckAmount - 0.1F);
        }
        if (!TubeWormBlock.canSupportWormAt(level, state, blockPos)) {
            this.remove();
        }
    }


    public void remove() {
        super.remove();
        ((ClientProxy) AlexsCaves.PROXY).removeParticleAt(this.blockPos);
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float scale = 1;
        float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - vec3.z());
        float lerpedTuck = prevTuckAmount + (tuckAmount - prevTuckAmount) * partialTick;
        PoseStack posestack = new PoseStack();
        posestack.pushPose();
        posestack.translate(f, f1 + 1, f2);
        posestack.scale(-scale, -scale, scale);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        MODEL.animateParticle(age, lerpedTuck, this.animationOffset, this.yRot, partialTick);
        VertexConsumer baseConsumer = multibuffersource$buffersource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        MODEL.renderToBuffer(posestack, baseConsumer, getLightColor(partialTick), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        multibuffersource$buffersource.endBatch();
        posestack.popPose();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new TubeWormParticle(worldIn, x, y, z);
        }
    }
}
