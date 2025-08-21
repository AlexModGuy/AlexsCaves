package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.ExtinctionSpearModel;
import com.github.alexmodguy.alexscaves.server.entity.item.ExtinctionSpearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeRenderTypes;

public class ExtinctionSpearRenderer extends EntityRenderer<ExtinctionSpearEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/extinction_spear.png");
    private static final ExtinctionSpearModel MODEL = new ExtinctionSpearModel();

    public ExtinctionSpearRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(ExtinctionSpearEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.translate(0.0D, (double) 0.25F, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 180F));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot()) + 90.0F));
        poseStack.translate(0.0D, (double) 0.25F, 0.0D);
        MODEL.setupAnim(entityIn, 0.0F, 0.0F, entityIn.tickCount + partialTicks, 0.0F, 0.0F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(ForgeRenderTypes.getUnlitTranslucent(getTextureLocation(entityIn)));
        MODEL.renderToBuffer(poseStack, ivertexbuilder, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }

    public ResourceLocation getTextureLocation(ExtinctionSpearEntity entity) {
        return TEXTURE;
    }
}
