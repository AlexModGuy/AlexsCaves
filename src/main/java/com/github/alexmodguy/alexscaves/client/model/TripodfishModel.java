package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.TripodfishEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class TripodfishModel extends AdvancedEntityModel<TripodfishEntity> {
    private final AdvancedModelBox mainBody;
    private final AdvancedModelBox dorsalfin;
    private final AdvancedModelBox bfin;
    private final AdvancedModelBox rpectoralFin;
    private final AdvancedModelBox lpectoralFin;
    private final AdvancedModelBox lpelvicFin;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox rpelvicFin;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox ttailFin;
    private final AdvancedModelBox btailFin;

    public TripodfishModel() {
        texWidth = 64;
        texHeight = 64;

        mainBody = new AdvancedModelBox(this);
        mainBody.setRotationPoint(0.0F, 22.0F, -2.25F);
        mainBody.setTextureOffset(0, 0).addBox(-1.5F, -3.0F, -3.75F, 3.0F, 5.0F, 11.0F, 0.0F, false);
        mainBody.setTextureOffset(26, 24).addBox(-1.5F, -1.0F, -7.75F, 3.0F, 3.0F, 4.0F, 0.0F, false);

        dorsalfin = new AdvancedModelBox(this);
        dorsalfin.setRotationPoint(0.0F, -3.0F, 1.25F);
        mainBody.addChild(dorsalfin);
        dorsalfin.setTextureOffset(0, 7).addBox(0.0F, -5.0F, -1.0F, 0.0F, 6.0F, 9.0F, 0.0F, false);

        bfin = new AdvancedModelBox(this);
        bfin.setRotationPoint(0F, 2.0F, 3.75F);
        mainBody.addChild(bfin);
        bfin.setTextureOffset(22, 10).addBox(0.0F, -1.0F, -1.5F, 0.0F, 5.0F, 6.0F, 0.0F, false);

        rpectoralFin = new AdvancedModelBox(this);
        rpectoralFin.setRotationPoint(-1.5F, -2.0F, -0.75F);
        mainBody.addChild(rpectoralFin);
        rpectoralFin.setTextureOffset(17, 0).addBox(-7.0F, -9.0F, 0.0F, 7.0F, 11.0F, 0.0F, 0.0F, true);

        lpectoralFin = new AdvancedModelBox(this);
        lpectoralFin.setRotationPoint(1.5F, -2.0F, -0.75F);
        mainBody.addChild(lpectoralFin);
        lpectoralFin.setTextureOffset(17, 0).addBox(0.0F, -9.0F, 0.0F, 7.0F, 11.0F, 0.0F, 0.0F, false);

        lpelvicFin = new AdvancedModelBox(this);
        lpelvicFin.setRotationPoint(1.5F, 2.0F, -0.25F);
        mainBody.addChild(lpelvicFin);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 1.25F);
        lpelvicFin.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.0F, -0.4363F);
        cube_r1.setTextureOffset(0, 18).addBox(0.0F, -2.0F, -1.75F, 0.0F, 19.0F, 4.0F, 0.0F, false);

        rpelvicFin = new AdvancedModelBox(this);
        rpelvicFin.setRotationPoint(-1.5F, 2.0F, -0.25F);
        mainBody.addChild(rpelvicFin);


        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 1.25F);
        rpelvicFin.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 0.0F, 0.4363F);
        cube_r2.setTextureOffset(0, 18).addBox(0.0F, -2.0F, -1.75F, 0.0F, 19.0F, 4.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -0.5F, 6.75F);
        mainBody.addChild(tail);
        tail.setTextureOffset(10, 16).addBox(-1.0F, -2.0F, 0.5F, 2.0F, 4.0F, 8.0F, 0.0F, false);

        ttailFin = new AdvancedModelBox(this);
        ttailFin.setRotationPoint(0.0F, -1.5F, 8.5F);
        tail.addChild(ttailFin);
        ttailFin.setTextureOffset(16, 24).addBox(0.0F, -10.0F, 0.0F, 0.0F, 12.0F, 4.0F, 0.0F, false);

        btailFin = new AdvancedModelBox(this);
        btailFin.setRotationPoint(0.0F, 1.5F, 8.5F);
        tail.addChild(btailFin);
        btailFin.setTextureOffset(8, 24).addBox(-0.01F, -1.0F, 0.0F, 0.0F, 17.0F, 4.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(mainBody, bfin, btailFin, tail, dorsalfin, ttailFin, rpectoralFin, lpectoralFin, lpelvicFin, rpelvicFin, cube_r1, cube_r2);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(mainBody);
    }

    @Override
    public void setupAnim(TripodfishEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float landProgress = entity.getLandProgress(partialTicks);
        float standProgress = entity.getStandProgress(partialTicks) * (1f - landProgress);
        float swimProgress = 1f - standProgress;
        float fishPitchAmount = entity.getFishPitch(partialTicks) / 57.295776F * swimProgress;
        float swimSpeed = 0.8F;
        float swimDegree = 0.6F;
        progressRotationPrev(mainBody, landProgress, 0, 0, (float) Math.toRadians(85), 1F);
        progressPositionPrev(mainBody, standProgress, 0, -15F, -1F, 1F);
        progressPositionPrev(mainBody, landProgress, 0, -1F, -1F, 1F);
        progressPositionPrev(rpelvicFin, swimProgress, 0, 0F, 1F, 1F);
        progressPositionPrev(lpelvicFin, swimProgress, 0, 0F, 1F, 1F);
        progressRotationPrev(rpelvicFin, swimProgress, (float) Math.toRadians(70), 0, (float) Math.toRadians(-30), 1F);
        progressRotationPrev(lpelvicFin, swimProgress, (float) Math.toRadians(70), 0, (float) Math.toRadians(30), 1F);
        progressRotationPrev(ttailFin, swimProgress, (float) Math.toRadians(-10), 0, 0, 1F);
        progressRotationPrev(btailFin, swimProgress, (float) Math.toRadians(40), 0, 0, 1F);
        progressRotationPrev(rpectoralFin, swimProgress, (float) Math.toRadians(-30), (float) Math.toRadians(-10), (float) Math.toRadians(-70), 1F);
        progressRotationPrev(lpectoralFin, swimProgress, (float) Math.toRadians(-30), (float) Math.toRadians(10), (float) Math.toRadians(70), 1F);
        this.walk(rpectoralFin, 0.15F, 0.3F, false, 1F, 0.1F, ageInTicks, swimProgress);
        this.walk(lpectoralFin, 0.15F, 0.3F, false, 1F, -0.1F, ageInTicks, swimProgress);
        this.bob(mainBody, 0.1F, 0.5F, false, ageInTicks, swimProgress);
        this.swing(mainBody, swimSpeed, swimDegree * 0.5F, false, 0F, 0F, limbSwing, limbSwingAmount * swimProgress);
        this.swing(tail, swimSpeed, swimDegree * 0.75F, false, -1F, 0F, limbSwing, limbSwingAmount * swimProgress);
        this.swing(ttailFin, swimSpeed, swimDegree * 0.75F, false, -2F, 0F, limbSwing, limbSwingAmount * swimProgress);
        this.swing(btailFin, swimSpeed, swimDegree * 0.75F, false, -2F, 0F, limbSwing, limbSwingAmount * swimProgress);
        this.walk(rpectoralFin, swimSpeed, swimDegree * 0.4F, false, 1F, -0.1F, limbSwing, limbSwingAmount * swimProgress);
        this.walk(lpectoralFin, swimSpeed, swimDegree * 0.4F, false, 1F, 0.1F, limbSwing, limbSwingAmount * swimProgress);
        this.walk(rpectoralFin, 0.05F, 0.1F, false, 1F, 0F, ageInTicks, standProgress);
        this.walk(lpectoralFin, 0.05F, 0.1F, false, 1F, 0F, ageInTicks, standProgress);
        this.walk(ttailFin, 0.05F, 0.1F, false, -1F, 0F, ageInTicks, standProgress);
        this.walk(dorsalfin, 0.05F, 0.1F, false, 0F, 0F, ageInTicks, standProgress);
        this.mainBody.rotateAngleX += fishPitchAmount * 0.9F;
    }
}
