package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GossamerWormModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.GossamerWormEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;

public class GossamerWormRenderer extends MobRenderer<GossamerWormEntity, GossamerWormModel> implements CustomBookEntityRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gossamer_worm.png");

    private boolean sepia;

    public GossamerWormRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GossamerWormModel(), 0.9F);
    }

    public boolean shouldRender(GossamerWormEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            for (PartEntity part : entity.getParts()) {
                if (camera.isVisible(part.getBoundingBoxForCulling())) {
                    return true;
                }
            }
            return false;
        }
    }

    public void render(GossamerWormEntity entity, float f1, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light) {
        this.model.straighten = sepia;
        super.render(entity, f1, partialTicks, poseStack, source, light);
    }

    @Nullable
    protected RenderType getRenderType(GossamerWormEntity gossamerWorm, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(gossamerWorm);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return sepia ? ACRenderTypes.getBookWidget(resourcelocation, true) : ACRenderTypes.getGhostly(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    @Override
    public void setSepiaFlag(boolean sepiaFlag) {
        this.sepia = sepiaFlag;
    }

    public ResourceLocation getTextureLocation(GossamerWormEntity entity) {
        return TEXTURE;
    }
}


