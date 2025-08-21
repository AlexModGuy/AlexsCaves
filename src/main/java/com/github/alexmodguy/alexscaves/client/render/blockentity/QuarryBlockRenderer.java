package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.QuarryGrinderModel;
import com.github.alexmodguy.alexscaves.server.block.QuarryBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.QuarryBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class QuarryBlockRenderer<T extends QuarryBlockEntity> implements BlockEntityRenderer<T> {

    private static final QuarryGrinderModel GRINDER_MODEL = new QuarryGrinderModel();
    private static final ResourceLocation GRINDER_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/quarry_grinder.png");

    public QuarryBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T valve, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        BlockState state = valve.getBlockState();
        Direction dir = state.getValue(QuarryBlock.FACING);
        if (dir == Direction.NORTH) {
            poseStack.translate(0.5, 0.5F, -0.5F);
        } else if (dir == Direction.EAST) {
            poseStack.translate(1.5F, 0.5F, 0.5F);
        } else if (dir == Direction.SOUTH) {
            poseStack.translate(0.5, 0.5F, 1.5F);
        } else if (dir == Direction.WEST) {
            poseStack.translate(-0.5F, 0.5F, 0.5F);
        }
        poseStack.mulPose(dir.getOpposite().getRotation());
        float spin = valve.getGrindRotation(partialTicks);
        GRINDER_MODEL.setupAnim(null, spin, 0, 0, 0, 0);
        GRINDER_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(GRINDER_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1);
        poseStack.popPose();
    }
}