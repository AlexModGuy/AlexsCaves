package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.BoundroidEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class BoundroidModel extends AdvancedEntityModel<BoundroidEntity> {
    private final AdvancedModelBox head;
    private final AdvancedModelBox bump1;
    private final AdvancedModelBox bump2;

    public BoundroidModel() {
        texWidth = 128;
        texHeight = 128;

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 13.0F, 0.0F);
        head.setTextureOffset(0, 27).addBox(-6.0F, 1.0F, -6.0F, 12.0F, 5.0F, 12.0F, 0.0F, false);
        head.setTextureOffset(0, 0).addBox(-11.0F, 6.0F, -11.0F, 22.0F, 5.0F, 22.0F, 0.0F, false);

        bump1 = new AdvancedModelBox(this);
        bump1.setRotationPoint(0.0F, -1.5F, 0.0F);
        head.addChild(bump1);
        setRotateAngle(bump1, 0.0F, -0.7854F, 0.0F);
        bump1.setTextureOffset(0, 0).addBox(-4.0F, -2.5F, 0.0F, 8.0F, 5.0F, 0.0F, 0.0F, false);

        bump2 = new AdvancedModelBox(this);
        bump2.setRotationPoint(0.0F, -1.5F, 0.0F);
        head.addChild(bump2);
        setRotateAngle(bump2, 0.0F, 0.7854F, 0.0F);
        bump2.setTextureOffset(0, 0).addBox(-4.0F, -2.5F, 0.0F, 8.0F, 5.0F, 0.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(head);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(head, bump2, bump1);
    }

    public void showChains() {
        bump1.showModel = true;
        bump2.showModel = true;
    }

    public void hideChains() {
        bump1.showModel = false;
        bump2.showModel = false;
    }

    @Override
    public void setupAnim(BoundroidEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float offGroundAmount = 1F - entity.getGroundProgress(ageInTicks - entity.tickCount);
        float yawAmount = netHeadYaw / 57.295776F;
        this.head.rotateAngleY += yawAmount;
        this.walk(head, 0.15F, 0.2F, false, -1, 0F, ageInTicks, offGroundAmount);
        this.flap(head, 0.15F, 0.2F, false, 1, 0F, ageInTicks, offGroundAmount);
    }

    public void animateForQuarry(float ageInTicks, float slamAmount) {
        this.resetToDefaultPose();
        float offGroundAmount = 1F - slamAmount;
        this.walk(head, 0.15F, 0.2F, false, -1, 0F, ageInTicks, offGroundAmount);
        this.flap(head, 0.15F, 0.2F, false, 1, 0F, ageInTicks, offGroundAmount);
    }
}