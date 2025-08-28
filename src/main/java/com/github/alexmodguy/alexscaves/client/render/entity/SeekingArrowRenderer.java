package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.item.SeekingArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class SeekingArrowRenderer extends ArrowRenderer<SeekingArrowEntity> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/seeking_arrow.png");

    public SeekingArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(SeekingArrowEntity entity, BlockPos pos) {
        return 15;
    }


    public ResourceLocation getTextureLocation(SeekingArrowEntity entity) {
        return TEXTURE;
    }
}