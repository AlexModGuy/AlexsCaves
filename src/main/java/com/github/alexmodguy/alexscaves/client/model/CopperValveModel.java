package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class CopperValveModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox line;
    private final AdvancedModelBox wheel;

    public CopperValveModel() {
        texWidth = 32;
        texHeight = 32;

        line = new AdvancedModelBox(this);
        line.setRotationPoint(0.0F, 18.0F, 0.0F);
        line.setTextureOffset(0, 18).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);

        wheel = new AdvancedModelBox(this);
        wheel.setRotationPoint(0.0F, -4.0F, 0.0F);
        line.addChild(wheel);
        wheel.setTextureOffset(-16, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(line);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float lifetime, float down, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.wheel.rotationPointY += 4 * down;
        this.wheel.rotateAngleY += Math.toRadians(down * 360F);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(line, wheel);
    }


}