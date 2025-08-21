package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.BeholderModel;
import com.github.alexmodguy.alexscaves.server.block.blockentity.BeholderBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class BeholderBlockRenderer<T extends BeholderBlockEntity> implements BlockEntityRenderer<T> {

    private static final BeholderModel MODEL = new BeholderModel();
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/beholder.png");
    private static final ResourceLocation TEXTURE_EYE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/beholder_eye.png");

    protected final RandomSource random = RandomSource.create();

    public BeholderBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T beholder, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-180));
        MODEL.hideEye(beholder.isFirstPersonView(Minecraft.getInstance().cameraEntity));
        MODEL.setupAnim(null, beholder.getEyeXRot(partialTicks), beholder.getEyeYRot(partialTicks), beholder.age + partialTicks, 0, 0);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.eyes(TEXTURE_EYE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1);
        poseStack.popPose();

    }

    public int getViewDistance() {
        return 128;
    }
}