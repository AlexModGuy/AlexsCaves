package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.TephraModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.item.TephraEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TephraRenderer extends EntityRenderer<TephraEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tephra.png");
    private static final TephraModel MODEL = new TephraModel();
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/teletor_trail.png");

    public TephraRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(TephraEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        float scale = entityIn.getLerpedScale(partialTicks) == 0 ? 1.0F : entityIn.getLerpedScale(partialTicks);
        float glowAmount = scale / Math.max(entityIn.getMaxScale(), 1.0F);
        poseStack.pushPose();
        poseStack.translate(0, 0.25F, 0F);
        poseStack.scale(scale, scale, scale);
        MODEL.setupAnim(entityIn, 0.0F, 0.0F, entityIn.tickCount + partialTicks, 0.0F, 0.0F);
        VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        MODEL.renderToBuffer(poseStack, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE));
        MODEL.renderToBuffer(poseStack, ivertexbuilder2, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, glowAmount);
        poseStack.popPose();
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }
    public ResourceLocation getTextureLocation(TephraEntity entity) {
        return TEXTURE;
    }
}
