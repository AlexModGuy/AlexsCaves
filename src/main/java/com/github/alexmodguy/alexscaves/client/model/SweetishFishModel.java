package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.SweetishFishEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TripodfishEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class SweetishFishModel extends AdvancedEntityModel<SweetishFishEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox bodySeg1;
    private final AdvancedModelBox bodySeg2;
    private final AdvancedModelBox tail;

    public SweetishFishModel() {
        texWidth = 32;
        texHeight = 32;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, -2.0F);

        bodySeg1 = new AdvancedModelBox(this);
        bodySeg1.setRotationPoint(0.0F, -2.5F, 0.0F);
        root.addChild(bodySeg1);
        bodySeg1.setTextureOffset(0, 0).addBox(-1.5F, -2.5F, -6.0F, 3.0F, 5.0F, 6.0F, 0.0F, false);
        bodySeg1.setTextureOffset(0, 0).addBox(0.0F, -4.5F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, false);

        bodySeg2 = new AdvancedModelBox(this);
        bodySeg2.setRotationPoint(0.0F, 0.0F, 0.0F);
        bodySeg1.addChild(bodySeg2);
        bodySeg2.setTextureOffset(12, 6).addBox(0.0F, -4.5F, 0.0F, 0.0F, 2.0F, 5.0F, 0.0F, false);
        bodySeg2.setTextureOffset(0, 11).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 6.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, 0.0F, 6.0F);
        bodySeg2.addChild(tail);
        tail.setTextureOffset(0, 18).addBox(0.0F, -3.5F, 0.0F, 0.0F, 7.0F, 4.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, bodySeg1, bodySeg2, tail);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(SweetishFishEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float swimSpeed = 0.8F;
        float swimDegree = 0.85F;
        float flopSpeed = 0.7F;
        float flopDegree = 0.5F;
        float landProgress = entity.getLandProgress(partialTicks);
        float swimProgress = 1F - landProgress;
        float fishPitchAmount = entity.getFishPitch(partialTicks) / 57.295776F * swimProgress;
        progressRotationPrev(bodySeg1, landProgress, 0, 0, (float) Math.toRadians(-85), 1F);
        this.swing(bodySeg1, swimSpeed, 0.3F * swimDegree, false, 1F, 0F, limbSwing, swimProgress * limbSwingAmount);
        this.swing(bodySeg2, swimSpeed, 0.5F * swimDegree, false, -1F, 0F, limbSwing, swimProgress * limbSwingAmount);
        this.swing(tail, swimSpeed, swimDegree, false, -1.5F, 0F, limbSwing, swimProgress * limbSwingAmount);
        this.bob(bodySeg1, flopSpeed * 0.5F, flopDegree * 2F, true, ageInTicks, landProgress);
        this.swing(bodySeg1, flopSpeed, 0.3F * flopDegree, true, 1F, 0F, ageInTicks, landProgress);
        this.swing(bodySeg2, flopSpeed, 0.5F * flopDegree, false, -1F, 0F, ageInTicks, landProgress);
        this.swing(tail, flopSpeed, flopDegree, false, -3, 0F, ageInTicks, landProgress);
        this.bodySeg1.rotateAngleX += fishPitchAmount * 0.9F;
    }
}