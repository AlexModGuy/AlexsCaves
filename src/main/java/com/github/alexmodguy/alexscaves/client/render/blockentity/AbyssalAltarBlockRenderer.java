package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.TeslaBulbModel;
import com.github.alexmodguy.alexscaves.server.block.blockentity.AbyssalAltarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class AbyssalAltarBlockRenderer<T extends AbyssalAltarBlockEntity> implements BlockEntityRenderer<T> {

    protected final RandomSource random = RandomSource.create();

    public AbyssalAltarBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T altar, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack itemStack = altar.getDisplayStack();
        if (!itemStack.isEmpty()) {
            int i = Item.getId(itemStack.getItem()) + itemStack.getDamageValue();
            this.random.setSeed((long) i);
            int j = this.getModelCount(itemStack);
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.02F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(altar.getItemAngle()));
            float slideBy = altar.getItem(0).isEmpty() ? 0.5F * (1F - altar.getSlideProgress(partialTicks)) : 0.5F * altar.getSlideProgress(partialTicks);
            poseStack.translate(0, 0, slideBy);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            BakedModel bakedModel = renderer.getModel(itemStack, altar.getLevel(), null, 0);

            boolean flag = bakedModel.isGui3d();
            if (!flag) {
                float f7 = -0.0F * (float) (j - 1) * 0.5F;
                float f8 = -0.0F * (float) (j - 1) * 0.5F;
                float f9 = -0.09375F * (float) (j - 1) * 0.5F;
                poseStack.translate((double) f7, (double) f8, (double) f9);
            }

            for (int k = 0; k < j; ++k) {
                poseStack.pushPose();
                if (k > 0) {
                    if (flag) {
                        float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.25F;
                        float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.25F;
                        float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.25F;
                        poseStack.translate(f11, f13, f10 - k * 0.1F);
                    } else {
                        float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        poseStack.translate(f12, f14, 0.0D);
                    }
                }

                renderer.render(itemStack, ItemDisplayContext.FIXED, false, poseStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, bakedModel);
                poseStack.popPose();
                if (!flag) {
                    poseStack.translate(0.0, 0.0, 0.09375F);
                }
            }


            poseStack.popPose();
        }

    }

    protected int getModelCount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }
        return i;
    }

    public int getViewDistance() {
        return 128;
    }
}