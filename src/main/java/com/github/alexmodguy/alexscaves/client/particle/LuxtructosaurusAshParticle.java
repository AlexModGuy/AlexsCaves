package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.client.model.LuxtructosaurusModel;
import com.github.alexmodguy.alexscaves.client.render.entity.LuxtructosaurusRenderer;
import com.github.alexmodguy.alexscaves.server.entity.living.LuxtructosaurusEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.TabulaModelRenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LuxtructosaurusAshParticle extends TextureSheetParticle {
    private static final float ACCELERATION_SCALE = 0.0025F;
    private static final int INITIAL_LIFETIME = 300;
    private static final int CURVE_ENDPOINT_TIME = 300;
    private static final float FALL_ACC = 0.25F;
    private static final float WIND_BIG = 2.0F;

    private final SpriteSet sprites;
    private final int luxtructosaurusId;
    private final float particleRandom;

    protected LuxtructosaurusAshParticle(ClientLevel level, double x, double y, double z, int luxtructosaurusId, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.quadSize *= 3.0F + random.nextFloat() * 5.0F;
        this.lifetime = 300;
        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
        this.luxtructosaurusId = luxtructosaurusId;
        this.setInitialPos();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xd = 0.3F - random.nextFloat() * 0.1F;
        this.yd = 0.2F - random.nextFloat() * 0.1F;
        this.zd = 0.3F - random.nextFloat() * 0.1F;
        this.gravity = -0.005F - random.nextFloat() * 0.04F;
        this.particleRandom = this.random.nextFloat();
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        float ageSca = (float)this.age / this.lifetime;
        this.setAlpha((1F - ageSca));
        this.xd *= 1.01F;
        this.yd *= 1.01F;
        this.zd *= 1.01F;
        this.rCol = Mth.approach(this.rCol,0.2F, 0.025F);
        this.gCol = Mth.approach(this.gCol,0.2F, 0.025F);
        this.bCol = Mth.approach(this.bCol,0.2F, 0.025F);

        if (!this.removed) {
            float f = (float)(300 - this.lifetime);
            float f1 = Math.min(f / 300.0F, 1.0F);
            double d0 = Math.cos(Math.toRadians((double)(this.particleRandom * 60.0F))) * 5.0D * Math.pow((double)f1, 1.25D);
            double d1 = Math.sin(Math.toRadians((double)(this.particleRandom * 60.0F))) * 5.0D * Math.pow((double)f1, 1.25D);
            this.xd += d0 * (double)0.0025F;
            this.zd += d1 * (double)0.0025F;
            this.yd -= (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.onGround || this.lifetime < 299 && (this.xd == 0.0D || this.zd == 0.0D)) {
                this.remove();
            }

            if (!this.removed) {
                this.xd *= (double)this.friction;
                this.yd *= (double)this.friction;
                this.zd *= (double)this.friction;
            }
        }
    }

    public float getQuadSize(float partialTicks) {
        return this.quadSize * Mth.clamp(1F - ((float) this.age + partialTicks) / (float) this.lifetime, 0.0F, 1.0F);
    }

    public void setInitialPos() {
        if (luxtructosaurusId != -1 && level.getEntity(luxtructosaurusId) instanceof LuxtructosaurusEntity luxtructosaurus) {
            EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
            if(manager.getRenderer(luxtructosaurus) instanceof LuxtructosaurusRenderer luxtructosaurusRenderer){
                LuxtructosaurusModel model = luxtructosaurusRenderer.getModel();
                for(int attempts = 0; attempts < 5; attempts++){
                    AdvancedModelBox box = model.getRandomModelPart(level.getRandom());
                    TabulaModelRenderUtils.ModelBox randomBox = box.cubeList.size() > 0 ? box.cubeList.get(random.nextInt(box.cubeList.size())) : null;
                    if(randomBox != null){
                        float f = random.nextFloat();
                        float f1 = random.nextFloat();
                        float f2 = random.nextFloat();
                        float f3 = Mth.lerp(f, randomBox.posX1, randomBox.posX2) / 16.0F;
                        float f4 = Mth.lerp(f1, randomBox.posY1, randomBox.posY2) / 16.0F;
                        float f5 = Mth.lerp(f2, randomBox.posZ1, randomBox.posZ2) / 16.0F;
                        Vec3 innerOffset = new Vec3(f3, f4, f5);
                        Vec3 translate = translateAndRotate(box, innerOffset).yRot((float) (Math.PI - luxtructosaurus.yBodyRot * ((float) Math.PI / 180F)));
                        this.setPos(luxtructosaurus.getX() + translate.x, luxtructosaurus.getY() + translate.y, luxtructosaurus.getZ() + translate.z);
                        return;
                    }

                }
            }
            this.alpha = 1.0F;
            this.remove();
        }
    }

    private Vec3 translateAndRotate(AdvancedModelBox box, Vec3 offsetIn) {
        PoseStack translationStack = new PoseStack();
        translationStack.pushPose();
        List<AdvancedModelBox> flipMe = new ArrayList<>();
        while(box.getParent() != null){
            box = box.getParent();
            flipMe.add(box);
        }
        Collections.reverse(flipMe);
        for(AdvancedModelBox translateBy : flipMe){
            translateBy.translateAndRotate(translationStack);
        }
        translationStack.translate(offsetIn.x, offsetIn.y, offsetIn.z);
        Vector4f armOffsetVec = new Vector4f((float) 0, (float) 0, (float) 0, 1.0F);
        armOffsetVec.mul(translationStack.last().pose());
        Vec3 vec3 = new Vec3(-armOffsetVec.x(), -armOffsetVec.y(), armOffsetVec.z());
        translationStack.popPose();
        return vec3.add(0, 2, 0);
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
            LuxtructosaurusAshParticle particle = new LuxtructosaurusAshParticle(worldIn, x, y, z, (int) xSpeed, spriteSet);
            float hue = worldIn.random.nextFloat() * 0.1F;
            particle.setColor(0.9F + hue, 0.4F + hue, hue);
            return particle;
        }
    }

}
