package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.CaramelCubeModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.LicowitchPossessionLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.CaramelCubeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class CaramelCubeRenderer extends MobRenderer<CaramelCubeEntity, CaramelCubeModel> implements CustomBookEntityRenderer {
    private static final ResourceLocation TEXTURE_SMALL = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_small.png");
    private static final ResourceLocation TEXTURE_SMALL_OUTSIDE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_small_outside.png");
    private static final ResourceLocation TEXTURE_MEDIUM = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_medium.png");
    private static final ResourceLocation TEXTURE_MEDIUM_OUTSIDE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_medium_outside.png");
    private static final ResourceLocation TEXTURE_LARGE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_large.png");
    private static final ResourceLocation TEXTURE_LARGE_OUTSIDE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_large_outside.png");
    private boolean sepia = false;

    public CaramelCubeRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CaramelCubeModel(), 0.65F);
        this.addLayer(new LayerOutside());
        this.addLayer(new LicowitchPossessionLayer<>(this, this::getOutsideTextureLocation));
    }

    protected void scale(CaramelCubeEntity mob, PoseStack matrixStackIn, float partialTicks) {
        int size = mob.getSlimeSize();
        float scaleBy = size == 2 ? 4 : size == 1 ? 2 : 1;
        matrixStackIn.scale(scaleBy, scaleBy, scaleBy);
    }

    public ResourceLocation getTextureLocation(CaramelCubeEntity entity) {
        switch (entity.getSlimeSize()) {
            case 1:
                return TEXTURE_MEDIUM;
            case 2:
                return TEXTURE_LARGE;
            default:
                return TEXTURE_SMALL;
        }
    }

    public ResourceLocation getOutsideTextureLocation(CaramelCubeEntity entity) {
        switch (entity.getSlimeSize()) {
            case 1:
                return TEXTURE_MEDIUM_OUTSIDE;
            case 2:
                return TEXTURE_LARGE_OUTSIDE;
            default:
                return TEXTURE_SMALL_OUTSIDE;
        }
    }


    @Nullable
    @Override
    protected RenderType getRenderType(CaramelCubeEntity caramelCubeEntity, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(caramelCubeEntity);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return sepia ? ACRenderTypes.getBookWidget(resourcelocation, true) : RenderType.entityCutoutNoCull(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    @Override
    public void setSepiaFlag(boolean sepiaFlag) {
        this.sepia = sepiaFlag;
    }

    class LayerOutside extends RenderLayer<CaramelCubeEntity, CaramelCubeModel> {

        public LayerOutside() {
            super(CaramelCubeRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CaramelCubeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entitylivingbaseIn.isInvisible()) {
                this.getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(sepia ? ACRenderTypes.getBookWidget(getOutsideTextureLocation(entitylivingbaseIn), true) : RenderType.entityTranslucent(getOutsideTextureLocation(entitylivingbaseIn))), packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}


