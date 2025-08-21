package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.VesperModel;
import com.github.alexmodguy.alexscaves.server.entity.living.VesperEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class VesperRenderer extends MobRenderer<VesperEntity, VesperModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/vesper.png");

    public VesperRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new VesperModel(), 0.35F);
    }

    public ResourceLocation getTextureLocation(VesperEntity entity) {
        return TEXTURE;
    }
}


