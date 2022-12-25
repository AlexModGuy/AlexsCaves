package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.FerrouslimeEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class FerrouslimeModel extends AdvancedEntityModel<FerrouslimeEntity> {

    private final AdvancedModelBox ferrouslime;

    public FerrouslimeModel() {
        texWidth = 32;
        texHeight = 32;
        ferrouslime = new AdvancedModelBox(this);
        ferrouslime.setRotationPoint(0.0F, 0.0F, 0.0F);
        ferrouslime.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        ferrouslime.setTextureOffset(0, 12).addBox(-3.5F, 0.0F, -3.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        ferrouslime.setTextureOffset(0, 12).addBox(1.5F, 0.0F, -3.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        ferrouslime.setTextureOffset(0, 12).addBox(-1.0F, -3.5F, -3.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(ferrouslime);
    }

    @Override
    public void setupAnim(FerrouslimeEntity ferrouslimeEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(ferrouslime);
    }

}
