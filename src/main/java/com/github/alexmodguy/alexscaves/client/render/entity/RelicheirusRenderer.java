package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.client.model.RelicheirusModel;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.RelicheirusHeldTrilocarisLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.RelicheirusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RelicheirusRenderer extends MobRenderer<RelicheirusEntity, RelicheirusModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/relicheirus.png");

    public RelicheirusRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RelicheirusModel(), 1.0F);
        this.addLayer(new RelicheirusHeldTrilocarisLayer(this));
    }

    protected void scale(RelicheirusEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(RelicheirusEntity entity) {
        return TEXTURE;
    }
}

