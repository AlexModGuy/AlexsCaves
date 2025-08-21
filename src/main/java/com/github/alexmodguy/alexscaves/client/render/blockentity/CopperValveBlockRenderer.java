package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.CopperValveModel;
import com.github.alexmodguy.alexscaves.server.block.CopperValveBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.CopperValveBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class CopperValveBlockRenderer<T extends CopperValveBlockEntity> implements BlockEntityRenderer<T> {

    private static final CopperValveModel MODEL = new CopperValveModel();
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/copper_valve.png");

    protected final RandomSource random = RandomSource.create();

    public CopperValveBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T valve, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        BlockState state = valve.getBlockState();
        Direction dir = state.getValue(CopperValveBlock.FACING);
        if (dir == Direction.UP) {
            poseStack.translate(0.5F, 1.5F, 0.5F);
        } else if (dir == Direction.DOWN) {
            poseStack.translate(0.5F, -0.5F, 0.5F);
        } else if (dir == Direction.NORTH) {
            poseStack.translate(0.5, 0.5F, -0.5F);
        } else if (dir == Direction.EAST) {
            poseStack.translate(1.5F, 0.5F, 0.5F);
        } else if (dir == Direction.SOUTH) {
            poseStack.translate(0.5, 0.5F, 1.5F);
        } else if (dir == Direction.WEST) {
            poseStack.translate(-0.5F, 0.5F, 0.5F);
        }
        poseStack.mulPose(dir.getOpposite().getRotation());

        MODEL.setupAnim(null, 0, 0, valve.getDownAmount(partialTicks), 0, 0);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1);
        poseStack.popPose();

    }

    public int getViewDistance() {
        return 256;
    }
}