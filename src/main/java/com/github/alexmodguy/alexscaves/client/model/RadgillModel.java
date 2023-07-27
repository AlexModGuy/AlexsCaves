package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.RadgillEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class RadgillModel extends AdvancedEntityModel<RadgillEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox dorsal2;
    private final AdvancedModelBox tailfin;
    private final AdvancedModelBox dorsal;
    private final AdvancedModelBox bottom_fin;
    private final AdvancedModelBox lfin;
    private final AdvancedModelBox rfin;
    private final AdvancedModelBox jaw;

    public RadgillModel() {
        texWidth = 64;
        texHeight = 64;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 19.0F, 0.0F);
        body.setTextureOffset(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 9.0F, 11.0F, 0.0F, false);
        body.setTextureOffset(38, 40).addBox(-1.5F, -6.0F, -5.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        body.setTextureOffset(26, 40).addBox(1.75F, -5.0F, -5.99F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        body.setTextureOffset(34, 17).addBox(-4.75F, -5.0F, -5.99F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        body.setTextureOffset(23, 0).addBox(-3.0F, -4.0F, -11.0F, 6.0F, 4.0F, 5.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -1.0F, 4.5F);
        body.addChild(tail);
        tail.setTextureOffset(18, 20).addBox(-2.0F, -3.0F, 0.5F, 4.0F, 6.0F, 8.0F, 0.0F, false);

        dorsal2 = new AdvancedModelBox(this);
        dorsal2.setRotationPoint(0.0F, -3.0F, 3.0F);
        tail.addChild(dorsal2);
        dorsal2.setTextureOffset(20, 28).addBox(0.0F, -6.0F, -0.5F, 0.0F, 6.0F, 6.0F, 0.0F, false);

        tailfin = new AdvancedModelBox(this);
        tailfin.setRotationPoint(0.0F, 0.0F, 8.5F);
        tail.addChild(tailfin);
        tailfin.setTextureOffset(5, 34).addBox(0.0F, -4.0F, -4.0F, 0.0F, 10.0F, 14.0F, 0.0F, false);

        dorsal = new AdvancedModelBox(this);
        dorsal.setRotationPoint(0.0F, -4.0F, -0.5F);
        body.addChild(dorsal);
        dorsal.setTextureOffset(32, 28).addBox(0.0F, -6.0F, -0.5F, 0.0F, 6.0F, 6.0F, 0.0F, false);

        bottom_fin = new AdvancedModelBox(this);
        bottom_fin.setRotationPoint(0.0F, 4.0F, 3.0F);
        body.addChild(bottom_fin);
        bottom_fin.setTextureOffset(0, 22).addBox(0.0F, -1.0F, -1.0F, 0.0F, 6.0F, 6.0F, 0.0F, false);

        lfin = new AdvancedModelBox(this);
        lfin.setRotationPoint(3.0F, 2.5F, -1.0F);
        body.addChild(lfin);
        setRotateAngle(lfin, 0.0F, -0.7854F, 0.0F);
        lfin.setTextureOffset(34, 9).addBox(0.0F, -2.5F, 0.0F, 6.0F, 8.0F, 0.0F, 0.0F, false);

        rfin = new AdvancedModelBox(this);
        rfin.setRotationPoint(-3.0F, 2.5F, -1.0F);
        body.addChild(rfin);
        setRotateAngle(rfin, 0.0F, 0.7854F, 0.0F);
        rfin.setTextureOffset(34, 9).addBox(-6.0F, -2.5F, 0.0F, 6.0F, 8.0F, 0.0F, 0.0F, true);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 5.0F, -6.0F);
        body.addChild(jaw);
        jaw.setTextureOffset(0, 34).addBox(-3.5F, -6.0F, -5.0F, 7.0F, 6.0F, 6.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, tail, dorsal, dorsal2, tailfin, jaw, bottom_fin, rfin, lfin);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public void setupAnim(RadgillEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float swimSpeed = 0.5F;
        float swimDegree = 0.5F;
        float partialTick = ageInTicks - entity.tickCount;
        float landProgress = entity.getLandProgress(partialTick);
        float pitchAmount = entity.getFishPitch(partialTick) / 57.295776F;
        progressRotationPrev(body, landProgress, 0, 0, (float) Math.toRadians(-90), 1F);
        this.walk(jaw, 0.1F, 0.2F, false, 1F, -0.2F, ageInTicks, 1);
        this.walk(body, 0.1F, 0.1F, false, 0F, 0F, ageInTicks, 1);
        this.swing(lfin, 0.1F, 0.2F, false, -1F, -0.2F, ageInTicks, 1);
        this.swing(rfin, 0.1F, 0.2F, true, -1F, -0.2F, ageInTicks, 1);
        this.bob(body, 0.1F, 1F, false, ageInTicks, 1);
        this.swing(body, swimSpeed, swimDegree * 0.2F, false, 3F, 0.0F, limbSwing, limbSwingAmount);
        this.swing(tail, swimSpeed, swimDegree, false, 2F, 0.0F, limbSwing, limbSwingAmount);
        this.swing(tailfin, swimSpeed, swimDegree, false, 1F, 0.0F, limbSwing, limbSwingAmount);
        this.swing(rfin, swimSpeed, swimDegree * 0.9F, true, 0F, 0.2F, limbSwing, limbSwingAmount);
        this.swing(lfin, swimSpeed, swimDegree * 0.9F, false, 0F, 0.2F, limbSwing, limbSwingAmount);
        body.rotateAngleX += pitchAmount;
    }
}
