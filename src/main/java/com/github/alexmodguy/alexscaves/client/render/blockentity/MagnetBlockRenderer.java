package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.server.block.MagnetBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.MagnetBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class MagnetBlockRenderer<T extends MagnetBlockEntity> implements BlockEntityRenderer<T> {


    public MagnetBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T magnet, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState state = magnet.getBlockState();
        float ageInTicks = (magnet.age + partialTicks) * 3F;
        if (state.getValue(MagnetBlock.POWERED)) {
            float twitch = 0.01F;
            BlockState copy = state.setValue(MagnetBlock.POWERED, false);
            stack.pushPose();
            stack.translate(Math.sin(ageInTicks) * twitch, Math.cos(ageInTicks - Math.PI / 2) * twitch, -Math.cos(ageInTicks) * twitch);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(copy, stack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY);
            stack.popPose();
        }
        if (magnet.getRangeVisuality(partialTicks) > 0.0F) {
            stack.pushPose();
            stack.translate(-magnet.getBlockPos().getX(), -magnet.getBlockPos().getY(), -magnet.getBlockPos().getZ());
            AABB aabb = magnet.getRangeBB(magnet.getEffectiveRange(), false).inflate(-0.001F);
            Player player = Minecraft.getInstance().player;
            float r = 1.0F;
            float g = 0.2F;
            float b = 0.2F;
            float advance = (float) (Math.sin(ageInTicks * 0.04F) * 0.5F + 0.5F);
            if (magnet.isAzure()) {
                r = 0.2F;
                g = 0.2F;
                b = 1.0F;
            }
            if (player != null) {
                AABB aabb2 = null;
                if (magnet.isExtenderItem(player.getMainHandItem()) || magnet.isExtenderItem(player.getOffhandItem())) {
                    aabb2 = magnet.getRangeBB(magnet.getEffectiveRange() + advance, false).inflate(-0.002F);
                }
                if (magnet.isRetracterItem(player.getMainHandItem()) || magnet.isRetracterItem(player.getOffhandItem())) {
                    aabb2 = magnet.getRangeBB(magnet.getEffectiveRange() - advance, false).inflate(-0.002F);
                }
                if (aabb2 != null && (magnet.canAddRange() || magnet.canRemoveRange())) {
                    LevelRenderer.renderLineBox(stack, bufferIn.getBuffer(RenderType.lines()), aabb2, r, g, b, 0.3F * magnet.getRangeVisuality(partialTicks));

                }
            }
            LevelRenderer.renderLineBox(stack, bufferIn.getBuffer(RenderType.lines()), aabb, r, g, b, 0.6F * magnet.getRangeVisuality(partialTicks));
            stack.popPose();
        }
    }


}
