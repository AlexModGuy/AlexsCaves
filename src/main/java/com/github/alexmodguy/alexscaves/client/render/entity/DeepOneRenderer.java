package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.DeepOneModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneEntity;
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

import javax.annotation.Nullable;

public class DeepOneRenderer extends MobRenderer<DeepOneEntity, DeepOneModel> implements CustomBookEntityRenderer{
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/deep_one.png");
    private static final ResourceLocation TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_glow.png");

    private boolean sepia;
    public DeepOneRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new DeepOneModel(), 0.45F);
        this.addLayer(new LayerGlow());
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
    }

    @Override
    protected void scale(DeepOneEntity mob, PoseStack matrixStackIn, float partialTicks) {
        if (mob.isSummoned()) {
            matrixStackIn.translate(0, (mob.getBbHeight() + 1F) * (1F - mob.getSummonProgress(partialTicks)), 0);
        }
    }

    @Nullable
    protected RenderType getRenderType(DeepOneEntity entity, boolean normal, boolean translucent, boolean outline) {
        return sepia ? ACRenderTypes.getBookWidget(TEXTURE, true) : super.getRenderType(entity, normal, translucent, outline);
    }

    @Override
    public void setSepiaFlag(boolean sepiaFlag) {
        this.sepia = sepiaFlag;
    }

    public ResourceLocation getTextureLocation(DeepOneEntity entity) {
        return TEXTURE;
    }

    class LayerGlow extends RenderLayer<DeepOneEntity, DeepOneModel> {

        public LayerGlow() {
            super(DeepOneRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DeepOneEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entitylivingbaseIn.isInvisible()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(sepia ? ACRenderTypes.getBookWidget(TEXTURE_GLOW, true) : ACRenderTypes.getGhostly(TEXTURE_GLOW));
                float alpha = 1.0F;
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
            }
        }
    }
}


