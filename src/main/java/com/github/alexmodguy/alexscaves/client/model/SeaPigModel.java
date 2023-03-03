package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.SeaPigEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;

public class SeaPigModel extends AdvancedEntityModel<SeaPigEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox rleg1;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox rleg3;
    private final AdvancedModelBox rleg4;
    private final AdvancedModelBox lleg1;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox lleg3;
    private final AdvancedModelBox lleg4;
    private final AdvancedModelBox head;
    private final AdvancedModelBox lfrontAntennae;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox rfrontAntennae;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox lbackAntennae;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox rbackAntennae;
    private final AdvancedModelBox cube_r4;

    public SeaPigModel() {
        texWidth = 48;
        texHeight = 48;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 20.0F, 0.5F);
        body.setTextureOffset(0, 0).addBox(-2.5F, -3.0F, -4.5F, 5.0F, 6.0F, 9.0F, 0.0F, false);
        body.setTextureOffset(0, 29).addBox(-2.5F, -2.0F, -4.5F, 5.0F, 3.0F, 9.0F, -0.25F, false);

        rleg1 = new AdvancedModelBox(this);
        rleg1.setRotationPoint(-2.0F, 2.75F, -3.5F);
        body.addChild(rleg1);
        rleg1.setTextureOffset(0, 0).addBox(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        rleg2 = new AdvancedModelBox(this);
        rleg2.setRotationPoint(-2.0F, 2.75F, -1.0F);
        body.addChild(rleg2);
        rleg2.setTextureOffset(0, 0).addBox(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        rleg3 = new AdvancedModelBox(this);
        rleg3.setRotationPoint(-2.0F, 2.75F, 1.5F);
        body.addChild(rleg3);
        rleg3.setTextureOffset(0, 0).addBox(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        rleg4 = new AdvancedModelBox(this);
        rleg4.setRotationPoint(-2.0F, 2.75F, 4.0F);
        body.addChild(rleg4);
        rleg4.setTextureOffset(0, 0).addBox(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        lleg1 = new AdvancedModelBox(this);
        lleg1.setRotationPoint(2.0F, 2.75F, -3.5F);
        body.addChild(lleg1);
        lleg1.setTextureOffset(0, 0).addBox(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, true);

        lleg2 = new AdvancedModelBox(this);
        lleg2.setRotationPoint(2.0F, 2.75F, -1.0F);
        body.addChild(lleg2);
        lleg2.setTextureOffset(0, 0).addBox(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, true);

        lleg3 = new AdvancedModelBox(this);
        lleg3.setRotationPoint(2.0F, 2.75F, 1.5F);
        body.addChild(lleg3);
        lleg3.setTextureOffset(0, 0).addBox(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, true);

        lleg4 = new AdvancedModelBox(this);
        lleg4.setRotationPoint(2.0F, 2.75F, 4.0F);
        body.addChild(lleg4);
        lleg4.setTextureOffset(0, 0).addBox(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, true);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -0.5F, -4.5F);
        body.addChild(head);
        head.setTextureOffset(16, 6).addBox(-2.0F, 3.0F, -2.0F, 4.0F, 0.0F, 3.0F, 0.0F, false);
        head.setTextureOffset(0, 15).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 3.0F, 0.0F, false);

        lfrontAntennae = new AdvancedModelBox(this);
        lfrontAntennae.setRotationPoint(1.75F, -2.5F, -4.5F);
        body.addChild(lfrontAntennae);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        lfrontAntennae.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.3927F, -0.3927F, 0.0F);
        cube_r1.setTextureOffset(0, 14).addBox(0.0F, -6.5F, -8.0F, 0.0F, 7.0F, 8.0F, 0.0F, false);

        rfrontAntennae = new AdvancedModelBox(this);
        rfrontAntennae.setRotationPoint(-1.75F, -2.5F, -4.5F);
        body.addChild(rfrontAntennae);


        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        rfrontAntennae.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.3927F, 0.3927F, 0.0F);
        cube_r2.setTextureOffset(0, 14).addBox(0.0F, -6.5F, -8.0F, 0.0F, 7.0F, 8.0F, 0.0F, true);

        lbackAntennae = new AdvancedModelBox(this);
        lbackAntennae.setRotationPoint(2.0F, -3.0F, 2.0F);
        body.addChild(lbackAntennae);


        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
        lbackAntennae.addChild(cube_r3);
        setRotateAngle(cube_r3, -0.3927F, 0.0F, 0.7854F);
        cube_r3.setTextureOffset(20, 9).addBox(0.0F, -10.0F, -0.5F, 0.0F, 11.0F, 6.0F, 0.0F, false);

        rbackAntennae = new AdvancedModelBox(this);
        rbackAntennae.setRotationPoint(-2.0F, -3.0F, 2.0F);
        body.addChild(rbackAntennae);


        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
        rbackAntennae.addChild(cube_r4);
        setRotateAngle(cube_r4, -0.3927F, 0.0F, -0.7854F);
        cube_r4.setTextureOffset(20, 9).addBox(0.0F, -10.0F, -0.5F, 0.0F, 11.0F, 6.0F, 0.0F, true);
        this.updateDefaultPose();
        this.body.scaleChildren = true;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, rleg1, rleg2, rleg3, rleg4, lleg1, lleg2, lleg3, lleg4, head, lfrontAntennae, lbackAntennae, rfrontAntennae, rbackAntennae, cube_r1, cube_r2, cube_r3, cube_r4);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    public void translateToBody(PoseStack poseStack) {
        this.body.translateAndRotate(poseStack);
    }


    @Override
    public void setupAnim(SeaPigEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float squish = entity.getSquishProgress(ageInTicks - entity.tickCount);
        float walkSpeed = 1.0F;
        float walkDegree = 1.3F;
        this.body.rotationPointY += squish * 3F;
        this.body.setScale(1F, (1F - squish * 0.7F), 1F);
        this.walk(rleg1, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.rleg1.rotationPointY -= ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 1.5F, 0.2F, true);
        this.walk(rleg2, walkSpeed, walkDegree, false, 2F, 0F, limbSwing, limbSwingAmount);
        this.rleg2.rotationPointY -= ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 3.5F, 0.2F, true);
        this.walk(rleg3, walkSpeed, walkDegree, false, 4F, 0F, limbSwing, limbSwingAmount);
        this.rleg3.rotationPointY -= ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 5.5F, 0.2F, true);
        this.walk(rleg4, walkSpeed, walkDegree, false, 6F, 0F, limbSwing, limbSwingAmount);
        this.rleg4.rotationPointY -= ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 7.5F, 0.2F, true);
        this.walk(lleg1, walkSpeed, walkDegree, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.lleg1.rotationPointY -= ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 1.5F, 0.2F, true);
        this.walk(lleg2, walkSpeed, walkDegree, false, 2F, 0F, limbSwing, limbSwingAmount);
        this.lleg2.rotationPointY -= ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 3.5F, 0.2F, true);
        this.walk(lleg3, walkSpeed, walkDegree, false, 4F, 0F, limbSwing, limbSwingAmount);
        this.lleg3.rotationPointY -= ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 5.5F, 0.2F, true);
        this.walk(lleg4, walkSpeed, walkDegree, false, 6F, 0F, limbSwing, limbSwingAmount);
        this.lleg4.rotationPointY -= ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 7.5F, 0.2F, true);
        this.walk(body, walkSpeed, walkDegree * 0.05F, false, -0.5F, 0F, limbSwing, limbSwingAmount);
        this.body.rotationPointY -= ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 0.5F, 0.2F, true);
        this.walk(rfrontAntennae, 0.1F, 0.1F, true, 2F, 0F, ageInTicks, 1F);
        this.walk(lfrontAntennae, 0.1F, 0.1F, false, 2F, 0F, ageInTicks, 1F);
        this.walk(rbackAntennae, 0.1F, 0.1F, false, 1F, 0F, ageInTicks, 1F);
        this.walk(lbackAntennae, 0.1F, 0.1F, true, 1F, 0F, ageInTicks, 1F);
        this.flap(head, 0.2F, 0.03F, true, 3F, 0F, ageInTicks, 1F);

    }
}