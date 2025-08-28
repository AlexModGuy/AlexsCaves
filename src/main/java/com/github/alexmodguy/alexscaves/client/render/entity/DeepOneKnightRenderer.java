package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.DeepOneKnightModel;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneKnightEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class DeepOneKnightRenderer extends MobRenderer<DeepOneKnightEntity, DeepOneKnightModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_knight.png");
    private static final ResourceLocation TEXTURE_NOON = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_knight_noon.png");
    private static final ResourceLocation TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_knight_glow.png");

    public DeepOneKnightRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new DeepOneKnightModel(), 0.45F);
        this.addLayer(new LayerGlow());
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
    }

    @Override
    protected void scale(DeepOneKnightEntity mob, PoseStack matrixStackIn, float partialTicks) {
        if (mob.isSummoned()) {
            matrixStackIn.translate(0, (mob.getBbHeight() + 1F) * (1F - mob.getSummonProgress(partialTicks)), 0);
        }
    }

    public ResourceLocation getTextureLocation(DeepOneKnightEntity entity) {
        return entity.isNoon() ? TEXTURE_NOON : TEXTURE;
    }

    class LayerGlow extends RenderLayer<DeepOneKnightEntity, DeepOneKnightModel> {

        public LayerGlow() {
            super(DeepOneKnightRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DeepOneKnightEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entitylivingbaseIn.isInvisible() && !entitylivingbaseIn.isNoon()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(TEXTURE_GLOW));
                float alpha = 1.0F;
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
            }
        }
    }
}


