package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GummyBearModel;
import com.github.alexmodguy.alexscaves.client.model.GummyBearModel;
import com.github.alexmodguy.alexscaves.client.model.SweetishFishModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.GummyBearHeldMobLayer;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.LicowitchPossessionLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.GummyBearEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GummyBearEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SweetishFishEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class GummyBearRenderer extends MobRenderer<GummyBearEntity, GummyBearModel> implements CustomBookEntityRenderer {
    public static final GummyBearModel OUTSIDE_MODEL = new GummyBearModel(0.0F);
    private static final ResourceLocation TEXTURE_RED = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gummy_bear_red.png");
    private static final ResourceLocation TEXTURE_GREEN = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gummy_bear_green.png");
    private static final ResourceLocation TEXTURE_YELLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gummy_bear_yellow.png");
    private static final ResourceLocation TEXTURE_BLUE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gummy_bear_blue.png");
    private static final ResourceLocation TEXTURE_PINK = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gummy_bear_pink.png");
    private static final ResourceLocation TEXTURE_INNARDS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gummy_bear_innards.png");
    private boolean sepia = false;

    public GummyBearRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GummyBearModel(-1.8F), 0.85F);
        this.addLayer(new LayerOutside());
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new GummyBearHeldMobLayer(this));
        this.addLayer(new LicowitchPossessionLayer<>(this, new GummyBearModel(0.0F), this::getOutsideTextureLocation));
    }


    protected void scale(GummyBearEntity mob, PoseStack matrixStackIn, float partialTicks) {
        float r = mob.getStomachRed();
        float g = mob.getStomachGreen();
        float b = mob.getStomachBlue();
        float alpha = mob.getStomachAlpha(partialTicks);
        this.model.setColor(r, g, b, alpha);
    }

    public ResourceLocation getTextureLocation(GummyBearEntity entity) {
        return TEXTURE_INNARDS;
    }


    @Nullable
    protected RenderType getRenderType(GummyBearEntity gummyBearEntity, boolean notInvisible, boolean renderAsItemCull, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(gummyBearEntity);
        if (renderAsItemCull) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (notInvisible) {
            return sepia ? ACRenderTypes.getBookWidget(resourcelocation, true) : RenderType.entityTranslucent(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    public ResourceLocation getOutsideTextureLocation(GummyBearEntity entity) {
        switch (entity.getGummyColor()){
            case RED:
                return TEXTURE_RED;
            case GREEN:
                return TEXTURE_GREEN;
            case YELLOW:
                return TEXTURE_YELLOW;
            case BLUE:
                return TEXTURE_BLUE;
            case PINK:
                return TEXTURE_PINK;
        }
        return TEXTURE_RED;
    }

    @Override
    public void setSepiaFlag(boolean sepiaFlag) {
        this.sepia = sepiaFlag;
    }

    class LayerOutside extends RenderLayer<GummyBearEntity, GummyBearModel> {


        public LayerOutside() {
            super(GummyBearRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GummyBearEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entitylivingbaseIn.isInvisible()) {
                this.getParentModel().copyPropertiesTo(OUTSIDE_MODEL);
                OUTSIDE_MODEL.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                OUTSIDE_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(sepia ? ACRenderTypes.getBookWidget(getOutsideTextureLocation(entitylivingbaseIn), true) : RenderType.entityTranslucent(getOutsideTextureLocation(entitylivingbaseIn))), packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}

