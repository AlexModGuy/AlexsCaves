package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.client.model.UnderzealotModel;
import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class UnderzealotRenderer extends MobRenderer<UnderzealotEntity, UnderzealotModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/underzealot.png");

    public UnderzealotRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new UnderzealotModel(), 0.25F);
    }

    public ResourceLocation getTextureLocation(UnderzealotEntity entity) {
        return TEXTURE;
    }
}


