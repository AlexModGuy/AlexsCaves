package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.server.entity.item.MovingMetalBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class MovingMetalBlockRenderer extends EntityRenderer<MovingMetalBlockEntity> {

    public MovingMetalBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    public void render(MovingMetalBlockEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
        for (MovingBlockData data : entity.getData()) {
            BlockState blockstate = data.getState();
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                stack.pushPose();
                stack.translate(-0.5D, -0.5D, -0.5D);
                stack.translate(data.getOffset().getX(), data.getOffset().getY(), data.getOffset().getZ());
                if (blockstate.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED && blockstate.hasProperty(HorizontalDirectionalBlock.FACING)) {
                    float f = blockstate.getValue(HorizontalDirectionalBlock.FACING).toYRot();
                    stack.translate(0.5D, 0.5D, 0.5D);
                    stack.mulPose(Axis.YP.rotationDegrees(-f));
                    stack.translate(-0.5D, -0.5D, -0.5D);
                }
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockstate, stack, source, i, OverlayTexture.NO_OVERLAY);
                stack.popPose();
            }
        }

        super.render(entity, f1, f2, stack, source, i);
    }


    public ResourceLocation getTextureLocation(MovingMetalBlockEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

