package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.DeepOneMageModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneMageEntity;
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

public class DeepOneMageRenderer extends MobRenderer<DeepOneMageEntity, DeepOneMageModel> implements CustomBookEntityRenderer{
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_mage.png");
    private static final ResourceLocation TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_mage_glow.png");

    private boolean sepia;
    public DeepOneMageRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new DeepOneMageModel(), 0.45F);
        this.addLayer(new LayerGlow());
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
    }

    @Override
    protected void scale(DeepOneMageEntity mob, PoseStack matrixStackIn, float partialTicks) {
        if (mob.isSummoned()) {
            matrixStackIn.translate(0, (mob.getBbHeight() + 1F) * (1F - mob.getSummonProgress(partialTicks)), 0);
        }
    }

    public ResourceLocation getTextureLocation(DeepOneMageEntity entity) {
        return TEXTURE_GLOW;
    }

    @Nullable
    protected RenderType getRenderType(DeepOneMageEntity deepOneMageEntity, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(deepOneMageEntity);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return sepia ? ACRenderTypes.getBookWidget(resourcelocation, true) : ACRenderTypes.getTeslaBulb(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    @Override
    public void setSepiaFlag(boolean sepiaFlag) {
        this.sepia = sepiaFlag;
    }

    class LayerGlow extends RenderLayer<DeepOneMageEntity, DeepOneMageModel> {

        public LayerGlow() {
            super(DeepOneMageRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DeepOneMageEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entitylivingbaseIn.isInvisible()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(DeepOneMageRenderer.this.sepia ? ACRenderTypes.getBookWidget(TEXTURE, true) : ACRenderTypes.getGhostly(TEXTURE));
                float alpha = 1.0F;
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
            }
        }
    }
}


