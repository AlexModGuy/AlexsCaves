package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.TripodfishModel;
import com.github.alexmodguy.alexscaves.server.entity.living.TripodfishEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TripodfishRenderer extends MobRenderer<TripodfishEntity, TripodfishModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tripodfish.png");

    public TripodfishRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new TripodfishModel(), 0.45F);
    }

    protected void scale(TripodfishEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(TripodfishEntity entity) {
        return TEXTURE;
    }
}

