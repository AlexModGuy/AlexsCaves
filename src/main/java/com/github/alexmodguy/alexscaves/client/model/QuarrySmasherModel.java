package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class QuarrySmasherModel extends AdvancedEntityModel<QuarrySmasherEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox arm2;
    private final AdvancedModelBox coil;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rarm;

    public QuarrySmasherModel() {
        texWidth = 128;
        texHeight = 128;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 18.0F, 0.0F);
        body.setTextureOffset(20, 41).addBox(4.0F, -1.5F, -3.0F, 2.0F, 7.0F, 6.0F, 0.0F, false);
        body.setTextureOffset(36, 41).addBox(-6.0F, -1.5F, -3.0F, 2.0F, 7.0F, 6.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-6.0F, -10.5F, -6.0F, 12.0F, 9.0F, 12.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, -6.0F, 0.0F);
        body.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.7854F, 0.0F, 1.5708F);
        cube_r1.setTextureOffset(54, 4).addBox(-1.5F, -17.5F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        cube_r1.setTextureOffset(48, 26).addBox(-0.5F, -13.5F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, -6.0F, 0.0F);
        body.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.7854F, 0.0F, -1.5708F);
        cube_r2.setTextureOffset(54, 4).addBox(-2.5F, -17.5F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, true);
        cube_r2.setTextureOffset(48, 26).addBox(-1.5F, -13.5F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, true);

        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(0.0F, -6.0F, 0.0F);
        body.addChild(cube_r3);
        setRotateAngle(cube_r3, -0.7854F, 0.0F, -1.5708F);
        cube_r3.setTextureOffset(54, 4).addBox(-2.5F, -17.5F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, true);
        cube_r3.setTextureOffset(48, 26).addBox(-1.5F, -13.5F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, true);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(0.0F, -6.0F, 0.0F);
        body.addChild(cube_r4);
        setRotateAngle(cube_r4, -0.7854F, 0.0F, 1.5708F);
        cube_r4.setTextureOffset(54, 4).addBox(-1.5F, -17.5F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        cube_r4.setTextureOffset(48, 26).addBox(-0.5F, -13.5F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        arm2 = new AdvancedModelBox(this);
        arm2.setRotationPoint(0.0F, -6.0F, 0.0F);
        body.addChild(arm2);
        arm2.setTextureOffset(0, 54).addBox(-20.0F, -9.5F, -4.0F, 8.0F, 13.0F, 8.0F, 0.0F, true);
        arm2.setTextureOffset(32, 58).addBox(-18.0F, -14.5F, -2.0F, 4.0F, 5.0F, 4.0F, 0.0F, true);
        arm2.setTextureOffset(42, 61).addBox(-32.0F, -11.5F, -4.0F, 14.0F, 0.0F, 6.0F, 0.0F, true);
        arm2.setTextureOffset(46, 48).addBox(-14.0F, -11.5F, -2.0F, 14.0F, 0.0F, 6.0F, 0.0F, true);
        arm2.setTextureOffset(36, 4).addBox(-12.0F, -4.5F, -2.0F, 6.0F, 5.0F, 3.0F, 0.0F, true);

        coil = new AdvancedModelBox(this);
        coil.setRotationPoint(0.0F, 2.5F, 0.0F);
        body.addChild(coil);
        coil.setTextureOffset(0, 21).addBox(-4.5F, -3.5F, -3.5F, 9.0F, 7.0F, 7.0F, 0.0F, false);
        coil.setTextureOffset(26, 29).addBox(-4.0F, -3.0F, -3.0F, 8.0F, 6.0F, 6.0F, 0.0F, false);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(5.0F, -7.5F, 5.0F);
        body.addChild(lleg);


        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(5.0F, -7.5F, -5.0F);
        body.addChild(larm);


        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-5.0F, -7.5F, 5.0F);
        body.addChild(rleg);


        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-5.0F, -7.5F, -5.0F);
        body.addChild(rarm);
        this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public void setupAnim(QuarrySmasherEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = ageInTicks - entity.tickCount;
        float chainLength = entity.getChainLength(partialTick);
        float inactiveProgress = entity.getInactiveProgress(partialTick);
        float activeProgress = 1F - inactiveProgress;
        float wiggleActive = entity.isBeingActivated() ? (float) Math.sin(activeProgress * Math.PI) : 0;
        float shake = Math.max(entity.shakeTime - partialTick, 0) / 10F;
        progressRotationPrev(body, inactiveProgress, (float) Math.toRadians(45), (float) Math.toRadians(45), 0F, 1F);
        this.flap(body, 1F, 0.5F, true, 0F, 0F, ageInTicks, wiggleActive);
        this.swing(body, 1F, 0.35F, false, 2F, 0F, ageInTicks, wiggleActive);
        this.body.rotateAngleX += Math.sin(ageInTicks * 0.7F + 1.0F) * shake * 0.05F;
        this.body.rotateAngleZ += Math.sin(ageInTicks * 0.7F) * shake * 0.1F;
        this.coil.rotateAngleX = (float) Math.toRadians(chainLength * 260);

    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, cube_r1, cube_r2, cube_r3, cube_r4, lleg, larm, rleg, rarm, arm2, coil);
    }

}