package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class QuarryGrinderModel extends AdvancedEntityModel {
    private final AdvancedModelBox spinner;

    public QuarryGrinderModel() {
        texWidth = 64;
        texHeight = 64;

        spinner = new AdvancedModelBox(this);
        spinner.setRotationPoint(0.0F, 16.0F, -1.0F);
        spinner.setTextureOffset(0, 16).addBox(-6.0F, -4.0F, -4.0F, 12.0F, 8.0F, 8.0F, 0.0F, false);
        spinner.setTextureOffset(0, 0).addBox(-6.0F, -4.0F, -4.0F, 12.0F, 8.0F, 8.0F, 0.5F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(spinner);
    }

    @Override
    public void setupAnim(Entity entity, float spin, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        spinner.rotateAngleX -= spin * 0.5F;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(spinner);
    }


}