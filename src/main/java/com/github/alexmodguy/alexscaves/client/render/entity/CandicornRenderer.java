package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.CandicornModel;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.CandicornRiderLayer;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.LicowitchPossessionLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.CandicornEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CandicornRenderer extends MobRenderer<CandicornEntity, CandicornModel> {
    private static final ResourceLocation TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/candicorn_0.png");
    private static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/candicorn_1.png");
    private static final ResourceLocation TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/candicorn_2.png");
    private static final ResourceLocation TEXTURE_3 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/candicorn_3.png");
    private static final ResourceLocation TEXTURE_4 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/candicorn_4.png");

    public CandicornRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CandicornModel(), 0.8F);
        this.addLayer(new CandicornRiderLayer(this));
        this.addLayer(new LicowitchPossessionLayer(this));
    }

    public void render(CandicornEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, source, packedLight);
    }

    public ResourceLocation getTextureLocation(CandicornEntity entity) {
        switch (entity.getVariant()) {
            case 1:
                return TEXTURE_1;
            case 2:
                return TEXTURE_2;
            case 3:
                return TEXTURE_3;
            case 4:
                return TEXTURE_4;
            default:
                return TEXTURE_0;
        }
    }
}


