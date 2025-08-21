package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.TremorsaurusModel;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.TremorsaurusHeldMobLayer;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.TremorsaurusRiderLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TremorsaurusRenderer extends MobRenderer<TremorsaurusEntity, TremorsaurusModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorsaurus.png");
    private static final ResourceLocation TEXTURE_PRINCESS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorsaurus_princess.png");
    private static final ResourceLocation TEXTURE_RETRO = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorsaurus_retro.png");
    private static final ResourceLocation TEXTURE_TECTONIC = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorsaurus_tectonic.png");

    public TremorsaurusRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new TremorsaurusModel(), 1.1F);
        this.addLayer(new TremorsaurusRiderLayer(this));
        this.addLayer(new TremorsaurusHeldMobLayer(this));
    }

    protected void scale(TremorsaurusEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(TremorsaurusEntity entity) {
        return entity.hasCustomName() && "princess".equalsIgnoreCase(entity.getName().getString()) ? TEXTURE_PRINCESS : entity.getAltSkin() == 1 ? TEXTURE_RETRO : entity.getAltSkin() == 2 ? TEXTURE_TECTONIC : TEXTURE;
    }


}

