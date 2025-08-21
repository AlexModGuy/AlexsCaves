package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GammaroachModel;
import com.github.alexmodguy.alexscaves.server.entity.living.GammaroachEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class GammaroachRenderer extends MobRenderer<GammaroachEntity, GammaroachModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gammaroach.png");
    private static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gammaroach_eyes.png");

    public GammaroachRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GammaroachModel(), 0.5F);
        this.addLayer(new LayerGlow());
    }

    protected void scale(GammaroachEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    protected float getFlipDegrees(GammaroachEntity centipede) {
        return 180.0F;
    }

    public ResourceLocation getTextureLocation(GammaroachEntity entity) {
        return TEXTURE;
    }

    class LayerGlow extends RenderLayer<GammaroachEntity, GammaroachModel> {

        public LayerGlow() {
            super(GammaroachRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GammaroachEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(TEXTURE_EYES));
            float alpha = 1.0F;
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
        }
    }
}


