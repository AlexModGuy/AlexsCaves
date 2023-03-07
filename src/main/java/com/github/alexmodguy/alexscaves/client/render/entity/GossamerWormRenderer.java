package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.client.model.GossamerWormModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.GossamerWormEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;

public class GossamerWormRenderer extends MobRenderer<GossamerWormEntity, GossamerWormModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/gossamer_worm.png");

    public GossamerWormRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GossamerWormModel(), 0.9F);
    }

    public boolean shouldRender(GossamerWormEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            for(PartEntity part : entity.getParts()){
                if(camera.isVisible(part.getBoundingBoxForCulling())){
                    return true;
                }
            }
            return false;
        }
    }

    @Nullable
    protected RenderType getRenderType(GossamerWormEntity gossamerWorm, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(gossamerWorm);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return ACRenderTypes.getGhostly(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    public ResourceLocation getTextureLocation(GossamerWormEntity entity) {
        return TEXTURE;
    }
}


