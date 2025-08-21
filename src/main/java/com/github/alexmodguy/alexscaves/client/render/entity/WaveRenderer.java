package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.WaveModel;
import com.github.alexmodguy.alexscaves.server.entity.item.WaveEntity;
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

public class WaveRenderer extends EntityRenderer<WaveEntity> {

    private static final ResourceLocation TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/wave_0.png");
    private static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/wave_1.png");
    private static final ResourceLocation TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/wave_2.png");
    private static final ResourceLocation TEXTURE_3 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/wave_3.png");
    private static final ResourceLocation OVERLAY_TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/wave_overlay_0.png");
    private static final ResourceLocation OVERLAY_TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/wave_overlay_1.png");
    private static final ResourceLocation OVERLAY_TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/wave_overlay_2.png");
    private static final ResourceLocation OVERLAY_TEXTURE_3 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/wave_overlay_3.png");
    private static final WaveModel MODEL = new WaveModel();

    public WaveRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(WaveEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if(entityIn.isInvisible()){
            return;
        }
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, (double) 1.5F, 0.0D);
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) + 180.0F));
        float ageInTicks = entityIn.activeWaveTicks + partialTicks;
        float f = ageInTicks / 10F;
        matrixStackIn.translate(0.0D, -0.1F + (1 - f) * -1, -(double) 0.5);
        matrixStackIn.scale(1F, -(0.2F + f * 0.9F), 1F);
        MODEL.setupAnim(entityIn, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(getWaveTexture(entityIn.activeWaveTicks)));
        int waterColorAt = entityIn.level().getBiome(entityIn.blockPosition()).get().getWaterColor();
        float colorR = (waterColorAt >> 16 & 255) / 255F;
        float colorG = (waterColorAt >> 8 & 255) / 255F;
        float colorB = (waterColorAt & 255) / 255F;
        MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, colorR, colorG, colorB, 1.0F);
        VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(RenderType.entityTranslucent(getOverlayTexture(entityIn.activeWaveTicks)));
        MODEL.renderToBuffer(matrixStackIn, ivertexbuilder2, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public ResourceLocation getTextureLocation(WaveEntity entity) {
        return getWaveTexture(entity.activeWaveTicks);
    }

    private ResourceLocation getWaveTexture(int tickCount) {
        int j = tickCount % 12 / 3;
        switch (j) {
            case 0:
                return TEXTURE_0;
            case 1:
                return TEXTURE_1;
            case 2:
                return TEXTURE_2;
            default:
                return TEXTURE_3;
        }
    }

    private ResourceLocation getOverlayTexture(int tickCount) {
        int j = tickCount % 12 / 3;
        switch (j) {
            case 0:
                return OVERLAY_TEXTURE_0;
            case 1:
                return OVERLAY_TEXTURE_1;
            case 2:
                return OVERLAY_TEXTURE_2;
            default:
                return OVERLAY_TEXTURE_3;
        }
    }
}
