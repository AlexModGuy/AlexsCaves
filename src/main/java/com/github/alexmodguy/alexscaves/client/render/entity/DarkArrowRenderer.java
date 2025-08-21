package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.DarkArrowModel;
import com.github.alexmodguy.alexscaves.server.entity.item.DarkArrowEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeRenderTypes;

public class DarkArrowRenderer extends EntityRenderer<DarkArrowEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/dark_arrow.png");
    private static final DarkArrowModel MODEL = new DarkArrowModel();

    public DarkArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(DarkArrowEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int lighting) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        poseStack.translate(0.0D, (double) 1.5F, -0.15D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        float f9 = (float) entity.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -Mth.sin(f9 * 3.0F) * f9;
            poseStack.mulPose(Axis.ZP.rotationDegrees(f10));
        }
        float ageInTicks = entity.tickCount + partialTicks;
        float invFade = 1.0F - entity.getFadeOut(ageInTicks - entity.tickCount);
        float alpha = Math.min(ageInTicks / 4F, 1F) * invFade;
        RenderType renderType = ForgeRenderTypes.getUnlitTranslucent(this.getTextureLocation(entity));
        VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
        MODEL.setupAnim(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
        MODEL.renderToBuffer(poseStack, vertexconsumer, 240, OverlayTexture.NO_OVERLAY, entity.getArrowRed(partialTicks), 0.0F, 0.0F, alpha);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, lighting);
    }

    @Override
    public ResourceLocation getTextureLocation(DarkArrowEntity entity) {
        return TEXTURE;
    }

}