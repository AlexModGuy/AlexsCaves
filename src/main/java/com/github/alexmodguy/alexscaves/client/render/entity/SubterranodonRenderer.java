package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.SubterranodonModel;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.SubterranodonRiderLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SubterranodonRenderer extends MobRenderer<SubterranodonEntity, SubterranodonModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/subterranodon.png");
    private static final ResourceLocation TEXTURE_RETRO = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/subterranodon_retro.png");
    private static final ResourceLocation TEXTURE_TECTONIC = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/subterranodon_tectonic.png");

    public SubterranodonRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SubterranodonModel(), 0.5F);
        this.addLayer(new SubterranodonRiderLayer(this));

    }

    public ResourceLocation getTextureLocation(SubterranodonEntity entity) {
        return entity.getAltSkin() == 1 ? TEXTURE_RETRO : entity.getAltSkin() == 2 ? TEXTURE_TECTONIC : TEXTURE;
    }
}

