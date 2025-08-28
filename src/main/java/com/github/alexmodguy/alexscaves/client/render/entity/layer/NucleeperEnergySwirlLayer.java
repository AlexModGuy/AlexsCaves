package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.NucleeperModel;
import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class NucleeperEnergySwirlLayer  extends EnergySwirlLayer<NucleeperEntity, NucleeperModel> {
    private static final ResourceLocation POWER_LOCATION = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_charged.png");
    private final NucleeperModel model = new NucleeperModel(1.0F);

    public NucleeperEnergySwirlLayer(RenderLayerParent<NucleeperEntity, NucleeperModel> renderer) {
        super(renderer);
    }

    protected float xOffset(float f) {
        return f * 0.01F;
    }

    protected ResourceLocation getTextureLocation() {
        return POWER_LOCATION;
    }

    protected EntityModel<NucleeperEntity> model() {
        return this.model;
    }
}
