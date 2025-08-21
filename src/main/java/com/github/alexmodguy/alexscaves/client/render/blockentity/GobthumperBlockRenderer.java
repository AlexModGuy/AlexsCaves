package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GobthumperModel;
import com.github.alexmodguy.alexscaves.server.block.blockentity.GobthumperBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GobthumperBlockRenderer<T extends GobthumperBlockEntity> implements BlockEntityRenderer<T> {

    private static final GobthumperModel MODEL = new GobthumperModel();
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gobthumper.png");
    private static final ResourceLocation TEXTURE_JELLY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gobthumper_jelly.png");

    public GobthumperBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T gobthumper, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-180));
        MODEL.setupAnim(null, 0.0F, 0.0F, gobthumper.getThumpTime(partialTicks), 0, 0);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURE_JELLY)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1);
        poseStack.popPose();

    }

    public int getViewDistance() {
        return 128;
    }
}