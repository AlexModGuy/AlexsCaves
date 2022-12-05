package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.client.model.GrottoceratopsModel;
import com.github.alexmodguy.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GrottoceratopsRenderer extends MobRenderer<GrottoceratopsEntity, GrottoceratopsModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/grottoceratops.png");

    public GrottoceratopsRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GrottoceratopsModel(), 1.1F);
    }

    protected void scale(GrottoceratopsEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(GrottoceratopsEntity entity) {
        return TEXTURE;
    }
}

