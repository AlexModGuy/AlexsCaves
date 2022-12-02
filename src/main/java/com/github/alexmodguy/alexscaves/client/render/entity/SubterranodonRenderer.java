package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.client.model.SubterranodonModel;
import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SubterranodonRenderer extends MobRenderer<SubterranodonEntity, SubterranodonModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/subterranodon.png");

    public SubterranodonRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SubterranodonModel(), 0.5F);
    }

    protected void scale(SubterranodonEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(SubterranodonEntity entity) {
        return TEXTURE;
    }
}

