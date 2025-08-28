package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.GumbeeperModel;
import com.github.alexmodguy.alexscaves.client.model.LicowitchModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.CorrodentEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumbeeperEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.LicowitchEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class LicowitchRenderer extends MobRenderer<LicowitchEntity, LicowitchModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/licowitch.png");

    private static final Set<LicowitchEntity> allTeleportingLicowitchOnScreen = new HashSet<>();

    private static final LicowitchModel TELEPORTING_MODEL = new LicowitchModel();

    public LicowitchRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new LicowitchModel(), 0.5F);
        this.addLayer(new ItemLayer(renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new TeleportingDoubleLayer());
    }

    public static void renderEntireBatch(LevelRenderer levelRenderer, PoseStack posestack, int renderTick, Camera camera, float partialTick) {
        for (LicowitchEntity licowitch : allTeleportingLicowitchOnScreen) {
            Vec3 to = licowitch.getTeleportingToPos();
            float progress = licowitch.getTeleportingProgress(partialTick);
            if (to != null && progress > 0) {
                Vec3 cameraPos = camera.getPosition();
                float scale = 0.9375F;
                float bodyYaw = licowitch.yBodyRotO + (licowitch.yBodyRot - licowitch.yBodyRotO) * partialTick;
                float headYaw = Mth.rotLerp(partialTick, licowitch.yHeadRotO, licowitch.yHeadRot);
                float netHeadYaw = headYaw - bodyYaw;
                float headPitch = Mth.rotLerp(partialTick, licowitch.xRotO, licowitch.getXRot());
                try {
                    posestack.pushPose();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                posestack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                posestack.translate(to.x, to.y + 1.5F, to.z);
                posestack.scale(-scale, -scale, scale);
                posestack.mulPose(Axis.YN.rotationDegrees(180 - bodyYaw));
                MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                VertexConsumer textureBuffer = bufferSource.getBuffer(RenderType.entityTranslucentCull(LicowitchRenderer.TEXTURE));
                TELEPORTING_MODEL.setupAnim(licowitch, licowitch.walkAnimation.position(partialTick), licowitch.walkAnimation.speed(partialTick), licowitch.tickCount + partialTick, netHeadYaw, headPitch);
                TELEPORTING_MODEL.renderToBuffer(posestack, textureBuffer, 240, LivingEntityRenderer.getOverlayCoords(licowitch, 0.0F), 1.0F, 1.0F - (1F - progress), 1.0F, progress);
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.PURPLE_WITCH_SHADER);
                VertexConsumer witchEffectBuffer = bufferSource.getBuffer(ACRenderTypes.getPurpleWitch(LicowitchRenderer.TEXTURE));
                TELEPORTING_MODEL.renderToBuffer(posestack, witchEffectBuffer, 240, LivingEntityRenderer.getOverlayCoords(licowitch, 0.0F), 1.0F, 0.0F, 1.0F, progress);
                posestack.popPose();
            }
        }
        allTeleportingLicowitchOnScreen.clear();
    }

    public void render(LicowitchEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        if (entityIn.getTeleportingToPos() != null) {
            allTeleportingLicowitchOnScreen.add(entityIn);
        }
    }

    protected void scale(LicowitchEntity entity, PoseStack poseStack, float partialTicks) {
        //witches in vanilla are scaled down for some reason
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    public boolean shouldRender(LicowitchEntity entity, Frustum camera, double x, double y, double z) {
        return super.shouldRender(entity, camera, x, y, z) || entity.getTeleportingToPos() != null;
    }

    public ResourceLocation getTextureLocation(LicowitchEntity entity) {
        return TEXTURE;
    }

    private class ItemLayer extends ItemInHandLayer<LicowitchEntity, LicowitchModel> {

        private final ItemInHandRenderer witchItemInHandRenderer;

        private ItemLayer(ItemInHandRenderer itemInHandRenderer) {
            super(LicowitchRenderer.this, itemInHandRenderer);
            this.witchItemInHandRenderer = itemInHandRenderer;
        }

        @Override
        protected void renderArmWithItem(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            if (!itemStack.isEmpty() && livingEntity instanceof LicowitchEntity licowitch) {
                float partialTicks = Minecraft.getInstance().getPartialTick();
                boolean crossedArms = licowitch.areArmsVisuallyCrossed(partialTicks);

                boolean staff = itemStack.is(ACItemRegistry.SUGAR_STAFF.get());
                poseStack.pushPose();
                if (crossedArms) {
                    this.getParentModel().translateToCrossedArms(humanoidArm, poseStack);
                } else {
                    this.getParentModel().translateToHand(humanoidArm, poseStack);
                }
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                boolean flag = humanoidArm == HumanoidArm.LEFT;
                if (crossedArms) {
                    poseStack.translate(0.0F, 0.125F, -0.125F);
                } else {
                    poseStack.translate((float) (flag ? 1 : -1) * 0.0325F, 0.125F, -0.125F);
                }
                if (staff) {
                    float forwardsBend = 0;
                    if (licowitch.getAnimation() == LicowitchEntity.ANIMATION_SPELL_0) {
                        forwardsBend = 0.66F * ACMath.cullAnimationTick(licowitch.getAnimationTick(), 1, LicowitchEntity.ANIMATION_SPELL_0, partialTicks, 18, 25);
                    }
                    if (licowitch.getAnimation() == LicowitchEntity.ANIMATION_SPELL_1) {
                        forwardsBend = 0.4F * ACMath.cullAnimationTick(licowitch.getAnimationTick(), 1, LicowitchEntity.ANIMATION_SPELL_1, partialTicks, 25, 27);
                    }
                    if (crossedArms) {
                        poseStack.translate(0.0F, 0, 0.1F);
                        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                        poseStack.mulPose(Axis.ZP.rotationDegrees(5F));
                        poseStack.mulPose(Axis.XP.rotationDegrees(flag ? 20.0F : -20.0F));
                        poseStack.translate(0.0F, 0, -0.05F);
                    } else {
                        poseStack.mulPose(Axis.XP.rotationDegrees(-2.5F));
                    }
                    poseStack.translate(0F, -0.25F * forwardsBend, 0F);
                    poseStack.mulPose(Axis.XP.rotationDegrees(forwardsBend * -90F));
                } else {
                    poseStack.mulPose(Axis.XP.rotationDegrees(10.0F));
                }
                if (licowitch.getAnimation() == LicowitchEntity.ANIMATION_EAT) {
                    float animationIntensity = ACMath.cullAnimationTick(licowitch.getAnimationTick(), 4, LicowitchEntity.ANIMATION_EAT, partialTicks, 0);
                    poseStack.mulPose(Axis.XP.rotationDegrees(animationIntensity * 30.0F));
                }
                this.witchItemInHandRenderer.renderItem(livingEntity, itemStack, displayContext, flag, poseStack, multiBufferSource, packedLight);
                poseStack.popPose();
            }
        }
    }

    protected void setupRotations(LicowitchEntity licowitch, PoseStack poseStack, float f1, float f2, float f3) {
        super.setupRotations(licowitch, poseStack, f1, f2, f3);

    }

    class TeleportingDoubleLayer extends RenderLayer<LicowitchEntity, LicowitchModel> {

        public TeleportingDoubleLayer() {
            super(LicowitchRenderer.this);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LicowitchEntity witch, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float progress = witch.getTeleportingProgress(partialTicks);
            if (progress > 0.0F) {
                Vec3 to = witch.getTeleportingToPos();
                if (to != null) {
                    Vec3 vec3 = to.subtract(witch.getPosition(partialTicks));
                    if (vec3.length() > 0.5F) {
                        PostEffectRegistry.renderEffectForNextTick(ClientProxy.PURPLE_WITCH_SHADER);
                        VertexConsumer textureBuffer2 = bufferIn.getBuffer(RenderType.entityTranslucentCull(LicowitchRenderer.this.getTextureLocation(witch)));
                        this.getParentModel().renderToBuffer(poseStack, textureBuffer2, packedLightIn, LivingEntityRenderer.getOverlayCoords(witch, 0.0F), 1.0F, 1.0F - progress, 1.0F, progress);
                        VertexConsumer witchEffectBuffer2 = bufferIn.getBuffer(ACRenderTypes.getPurpleWitch(LicowitchRenderer.this.getTextureLocation(witch)));
                        this.getParentModel().renderToBuffer(poseStack, witchEffectBuffer2, packedLightIn, LivingEntityRenderer.getOverlayCoords(witch, 0.0F), 1.0F, 0.0F, 1.0F, progress);
                    }
                }
            }

        }
    }
}


