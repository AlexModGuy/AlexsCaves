package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.TeslaBulbModel;
import com.github.alexmodguy.alexscaves.server.block.blockentity.AbyssalAltarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public class AbyssalAltarBlockRenderer<T extends AbyssalAltarBlockEntity> implements BlockEntityRenderer<T> {

    private static final TeslaBulbModel MODEL = new TeslaBulbModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/tesla_bulb.png");

    protected final RandomSource random = RandomSource.create();

    public AbyssalAltarBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T altar, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack itemStack = altar.getDisplayStack();
        if(!itemStack.isEmpty()){
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.02F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(altar.getItemAngle()));
            float slideBy = altar.getItem(0).isEmpty() ? 0.5F * (1F - altar.getSlideProgress(partialTicks)) : 0.5F * altar.getSlideProgress(partialTicks);
            poseStack.translate(0, 0, slideBy );
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BakedModel bakedModel = renderer.getModel(itemStack, altar.getLevel(), null, 0);
            renderer.render(itemStack, ItemTransforms.TransformType.FIXED, false, poseStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, bakedModel);
            poseStack.popPose();
        }

    }

    public int getViewDistance() {
        return 256;
    }
}