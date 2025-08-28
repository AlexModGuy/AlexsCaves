package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.SweetishFishModel;
import com.github.alexmodguy.alexscaves.server.entity.living.SweetishFishEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SweetishFishRenderer extends MobRenderer<SweetishFishEntity, SweetishFishModel> {
    private static final ResourceLocation TEXTURE_RED = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sweetish_fish_red.png");
    private static final ResourceLocation TEXTURE_GREEN = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sweetish_fish_green.png");
    private static final ResourceLocation TEXTURE_YELLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sweetish_fish_yellow.png");
    private static final ResourceLocation TEXTURE_BLUE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sweetish_fish_blue.png");
    private static final ResourceLocation TEXTURE_PINK = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sweetish_fish_pink.png");

    public SweetishFishRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new SweetishFishModel(), 0.35F);
    }

    protected void scale(SweetishFishEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(SweetishFishEntity entity) {
        switch (entity.getGummyColor()){
            case RED:
                return TEXTURE_RED;
            case GREEN:
                return TEXTURE_GREEN;
            case YELLOW:
                return TEXTURE_YELLOW;
            case BLUE:
                return TEXTURE_BLUE;
            case PINK:
                return TEXTURE_PINK;
        }
        return TEXTURE_RED;
    }
}

