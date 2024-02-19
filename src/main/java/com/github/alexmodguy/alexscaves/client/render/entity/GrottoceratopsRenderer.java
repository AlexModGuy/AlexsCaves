package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.client.model.GrottoceratopsModel;
import com.github.alexmodguy.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GrottoceratopsRenderer extends MobRenderer<GrottoceratopsEntity, GrottoceratopsModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/grottoceratops.png");
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation("alexscaves:textures/entity/grottoceratops_baby.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation("alexscaves:textures/entity/grottoceratops_retro.png");
    private static final ResourceLocation TEXTURE_RETRO_BABY = new ResourceLocation("alexscaves:textures/entity/grottoceratops_retro_baby.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation("alexscaves:textures/entity/grottoceratops_tectonic.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BABY = new ResourceLocation("alexscaves:textures/entity/grottoceratops_tectonic_baby.png");

    public GrottoceratopsRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GrottoceratopsModel(), 1.1F);
    }

    protected void scale(GrottoceratopsEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(GrottoceratopsEntity entity) {
        return entity.getAltSkin() == 1 ? entity.isBaby() ? TEXTURE_RETRO_BABY : TEXTURE_RETRO : entity.getAltSkin() == 2 ? entity.isBaby() ? TEXTURE_TECTONIC_BABY : TEXTURE_TECTONIC : entity.isBaby() ? TEXTURE_BABY : TEXTURE;
    }
}

