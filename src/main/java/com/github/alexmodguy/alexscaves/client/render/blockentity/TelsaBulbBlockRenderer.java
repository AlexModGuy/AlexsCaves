package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.TeslaBulbModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.block.TeslaBulbBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.TeslaBulbBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class TelsaBulbBlockRenderer<T extends TeslaBulbBlockEntity> implements BlockEntityRenderer<T> {

    private static final TeslaBulbModel MODEL = new TeslaBulbModel();
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tesla_bulb.png");

    protected final RandomSource random = RandomSource.create();

    public TelsaBulbBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T teslaBulb, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        boolean down = teslaBulb.getBlockState().getValue(TeslaBulbBlock.DOWN);
        if (down) {
            poseStack.translate(0.5F, -0.51F, 0.5F);
        } else {
            poseStack.translate(0.5F, 1.51F, 0.5F);
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        }
        float r = 1F;
        float g = 1F;
        float b = 1F;
        MODEL.setupAnim(null, 0, teslaBulb.getExplodeProgress(partialTicks), teslaBulb.age + partialTicks, 0, 0);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getTeslaBulb(TEXTURE)), combinedLightIn, combinedOverlayIn, r, g, b, 1);


        poseStack.popPose();

    }

    public int getViewDistance() {
        return 256;
    }
}