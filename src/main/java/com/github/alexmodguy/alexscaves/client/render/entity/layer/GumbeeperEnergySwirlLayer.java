package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GumbeeperModel;
import com.github.alexmodguy.alexscaves.server.entity.living.GumbeeperEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;

public class GumbeeperEnergySwirlLayer extends EnergySwirlLayer<GumbeeperEntity, GumbeeperModel> {
    private static final ResourceLocation POWER_LOCATION = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumbeeper_charged.png");
    private final GumbeeperModel model = new GumbeeperModel(1.0F);

    public GumbeeperEnergySwirlLayer(RenderLayerParent<GumbeeperEntity, GumbeeperModel> renderer) {
        super(renderer);
    }

    protected float xOffset(float f) {
        return f * 0.01F;
    }

    protected ResourceLocation getTextureLocation() {
        return POWER_LOCATION;
    }

    protected EntityModel<GumbeeperEntity> model() {
        return this.model;
    }
}
