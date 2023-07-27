package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class ACBoatChestModel extends AdvancedEntityModel {
    private final AdvancedModelBox chest;

    public ACBoatChestModel() {
        texWidth = 64;
        texHeight = 64;

        chest = new AdvancedModelBox(this);
        chest.setRotationPoint(2.0F, 24.0F, 0.0F);
        chest.setTextureOffset(0, 18).addBox(-8.0F, -7.0F, -6.0F, 12.0F, 7.0F, 12.0F, 0.0F, false);
        chest.setTextureOffset(0, 0).addBox(-8.0F, -12.0F, -6.0F, 12.0F, 5.0F, 12.0F, 0.0F, false);
        chest.setTextureOffset(0, 1).addBox(-3.0F, -9.0F, -7.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(chest);
    }

    @Override
    public void setupAnim(Entity entity, float v, float v1, float v2, float v3, float v4) {
        this.resetToDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(chest);
    }

}