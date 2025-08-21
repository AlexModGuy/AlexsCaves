package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.MineGuardianAnchorModel;
import com.github.alexmodguy.alexscaves.server.entity.item.MineGuardianAnchorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MineGuardianAnchorRenderer extends EntityRenderer<MineGuardianAnchorEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/mine_guardian_anchor.png");
    private static final ResourceLocation TEXTURE_CHAIN = ResourceLocation.withDefaultNamespace("textures/block/chain.png");
    private static final MineGuardianAnchorModel MODEL = new MineGuardianAnchorModel();

    public MineGuardianAnchorRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public boolean shouldRender(MineGuardianAnchorEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            Entity guardian = entity.getGuardian();
            if (guardian != null) {
                Vec3 vec3 = entity.position();
                Vec3 vec31 = guardian.position();
                return camera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
            }
            return false;
        }
    }

    public void render(MineGuardianAnchorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.translate(0.0D, (double) 1.35F, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 180));
        MODEL.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        MODEL.renderToBuffer(poseStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();

        Vec3 translateFrom = entity.getPosition(partialTicks);
        Vec3 modelOffset = MODEL.getChainPosition(new Vec3(0, 0.1F, 0));
        Vec3 chainTo = entity.getChainFrom(partialTicks).add(modelOffset).subtract(translateFrom);
        Vec3 chainFrom = entity.getChainTo(partialTicks).subtract(translateFrom).subtract(chainTo);
        VertexConsumer chainBuffer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_CHAIN));
        poseStack.pushPose();
        poseStack.translate(chainTo.x, chainTo.y, chainTo.z);
        BoundroidWinchRenderer.renderChain(chainFrom, poseStack, chainBuffer, packedLightIn, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    public ResourceLocation getTextureLocation(MineGuardianAnchorEntity entity) {
        return TEXTURE;
    }
}
