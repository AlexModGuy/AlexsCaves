package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GloomothModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.GloomothEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class GloomothRenderer extends MobRenderer<GloomothEntity, GloomothModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,  "textures/entity/gloomoth.png");
    private static final ResourceLocation TEXTURE_EYESPOTS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gloomoth_eyespots.png");

    public GloomothRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GloomothModel(), 0.35F);
        this.addLayer(new LayerGlow());
    }

    protected void scale(GloomothEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(GloomothEntity entity) {
        return TEXTURE;
    }

    class LayerGlow extends RenderLayer<GloomothEntity, GloomothModel> {

        public LayerGlow() {
            super(GloomothRenderer.this);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, GloomothEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder4 = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_EYESPOTS));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder4, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 0.33F);

        }
    }
}

