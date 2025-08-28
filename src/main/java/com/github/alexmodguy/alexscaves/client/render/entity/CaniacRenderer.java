package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.CaniacModel;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.LicowitchPossessionLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.CaniacEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CaniacRenderer extends MobRenderer<CaniacEntity, CaniacModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/caniac.png");

    public CaniacRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CaniacModel(), 0.65F);
        this.addLayer(new LicowitchPossessionLayer(this));
    }

    public ResourceLocation getTextureLocation(CaniacEntity entity) {
        return TEXTURE;
    }
}


