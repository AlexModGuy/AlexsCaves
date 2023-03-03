package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.LanternfishEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class LanternfishModel extends AdvancedEntityModel<LanternfishEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox rightfin;
    private final AdvancedModelBox leftfin;

    public LanternfishModel() {
        texWidth = 16;
        texHeight = 16;
        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 22.0F, -1.5F);
        body.setTextureOffset(2, 9).addBox(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, 0.0F, false);
        body.setTextureOffset(8, 3).addBox(0.0F, 1.0F, -1.25F, 0.0F, 2.0F, 4.0F, 0.0F, false);
        body.setTextureOffset(7, -1).addBox(0.0F, -2.5F, -1.0F, 0.0F, 2.0F, 3.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, 0.0F, 2.5F);
        body.addChild(tail);
        tail.setTextureOffset(0, 3).addBox(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 3.0F, 0.0F, false);

        rightfin = new AdvancedModelBox(this);
        rightfin.setRotationPoint(1.0F, 0.5F, -0.5F);
        body.addChild(rightfin);
        rightfin.setTextureOffset(0, 0).addBox(0.0F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        leftfin = new AdvancedModelBox(this);
        leftfin.setRotationPoint(-1.0F, 0.5F, -0.5F);
        body.addChild(leftfin);
        leftfin.setTextureOffset(0, 0).addBox(-1.0F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, tail, rightfin, leftfin);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public void setupAnim(LanternfishEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();

        float swimSpeed = 0.9F;
        float swimDegree = 0.9F;
        float partialTick = ageInTicks - entity.tickCount;
        float landProgress = entity.getLandProgress(partialTick);
        float pitchAmount = entity.getFishPitch(partialTick) / 57.295776F;
        progressRotationPrev(body, landProgress, 0, 0, (float) Math.toRadians(-90), 1F);
        this.walk(body, 0.1F, 0.05F, false, 0F, 0F, ageInTicks, 1);
        this.bob(body, 0.1F, 0.3F, false, ageInTicks, 1);
        this.swing(body, swimSpeed, swimDegree * 0.4F, false, 3F, 0.0F, limbSwing, limbSwingAmount);
        this.swing(tail, swimSpeed, swimDegree * 0.8F, false, 2F, 0.0F, limbSwing, limbSwingAmount);
        this.swing(rightfin, swimSpeed, swimDegree * 1.1F, true, 0F, 0.4F, limbSwing, limbSwingAmount);
        this.swing(leftfin, swimSpeed, swimDegree * 1.1F, false, 0F, 0.4F, limbSwing, limbSwingAmount);
        body.rotateAngleX += pitchAmount;
    }
}