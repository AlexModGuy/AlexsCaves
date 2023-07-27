package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneKnightEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.world.entity.HumanoidArm;

public class DeepOneKnightModel extends AdvancedEntityModel<DeepOneKnightEntity> implements ArmedModel {
    private final AdvancedModelBox body;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox head;
    private final AdvancedModelBox cube_r5;
    private final AdvancedModelBox cube_r6;
    private final AdvancedModelBox cube_r7;
    private final AdvancedModelBox cube_r8;
    private final AdvancedModelBox cube_r9;
    private final AdvancedModelBox cube_r10;
    private final AdvancedModelBox cube_r11;
    private final AdvancedModelBox cube_r12;
    private final AdvancedModelBox cube_r13;
    private final AdvancedModelBox cube_r14;
    private final AdvancedModelBox cube_r15;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox lfin;
    private final AdvancedModelBox rfin;
    private final AdvancedModelBox lure;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox tail;
    private final ModelAnimator animator;

    public DeepOneKnightModel() {
        texWidth = 128;
        texHeight = 128;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 24.0F, 0.0F);
        body.setTextureOffset(86, 105).addBox(-6.5F, -20.0F, -4.0F, 13.0F, 11.0F, 8.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-9.5F, -36.0F, -6.0F, 19.0F, 18.0F, 14.0F, 0.0F, false);
        body.setTextureOffset(0, 82).addBox(-9.5F, -36.0F, -6.0F, 19.0F, 18.0F, 14.0F, 0.25F, false);
        body.setTextureOffset(50, 41).addBox(0.0F, -41.0F, 0.0F, 0.0F, 23.0F, 16.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(-9.5F, -27.0F, 8.0F);
        body.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.7854F, 0.0F);
        cube_r1.setTextureOffset(68, 105).addBox(-2.0F, -9.0F, 0.0F, 2.0F, 18.0F, 0.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(9.5F, -27.0F, 8.0F);
        body.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, -0.7854F, 0.0F);
        cube_r2.setTextureOffset(68, 105).addBox(0.0F, -9.0F, 0.0F, 2.0F, 18.0F, 0.0F, 0.0F, true);

        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(9.5F, -27.0F, -6.0F);
        body.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.0F, 0.7854F, 0.0F);
        cube_r3.setTextureOffset(68, 105).addBox(0.0F, -9.0F, 0.0F, 2.0F, 18.0F, 0.0F, 0.0F, true);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(-9.5F, -27.0F, -6.0F);
        body.addChild(cube_r4);
        setRotateAngle(cube_r4, 0.0F, -0.7854F, 0.0F);
        cube_r4.setTextureOffset(68, 105).addBox(-2.0F, -9.0F, 0.0F, 2.0F, 18.0F, 0.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -36.0F, -1.0F);
        body.addChild(head);
        head.setTextureOffset(0, 57).addBox(-6.5F, -2.0F, -12.0F, 13.0F, 13.0F, 12.0F, 0.0F, false);
        head.setTextureOffset(68, 88).addBox(-6.5F, -5.0F, -12.0F, 13.0F, 3.0F, 6.0F, 0.0F, false);
        head.setTextureOffset(98, 96).addBox(-6.5F, 0.0F, -12.0F, 13.0F, 6.0F, 2.0F, 0.25F, false);

        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(0.0F, 11.0F, -12.0F);
        head.addChild(cube_r5);
        setRotateAngle(cube_r5, -0.7854F, 0.0F, 0.0F);
        cube_r5.setTextureOffset(29, 123).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 2.0F, 0.0F, 0.0F, false);

        cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(0.0F, -2.0F, 0.0F);
        head.addChild(cube_r6);
        setRotateAngle(cube_r6, -0.7854F, 0.0F, 0.0F);
        cube_r6.setTextureOffset(29, 126).addBox(-6.5F, -2.0F, 0.0F, 13.0F, 2.0F, 0.0F, 0.0F, false);

        cube_r7 = new AdvancedModelBox(this);
        cube_r7.setRotationPoint(0.0F, -2.0F, -12.0F);
        head.addChild(cube_r7);
        setRotateAngle(cube_r7, 0.7854F, 0.0F, 0.0F);
        cube_r7.setTextureOffset(29, 126).addBox(-6.5F, -2.0F, 0.0F, 13.0F, 2.0F, 0.0F, 0.0F, false);

        cube_r8 = new AdvancedModelBox(this);
        cube_r8.setRotationPoint(-6.5F, 3.5F, 0.0F);
        head.addChild(cube_r8);
        setRotateAngle(cube_r8, 0.0F, 0.7854F, 0.0F);
        cube_r8.setTextureOffset(0, 113).addBox(-2.0F, -7.5F, 0.0F, 2.0F, 15.0F, 0.0F, 0.0F, false);

        cube_r9 = new AdvancedModelBox(this);
        cube_r9.setRotationPoint(6.5F, 3.5F, 0.0F);
        head.addChild(cube_r9);
        setRotateAngle(cube_r9, 0.0F, -0.7854F, 0.0F);
        cube_r9.setTextureOffset(68, 108).addBox(0.0F, -7.5F, 0.0F, 2.0F, 15.0F, 0.0F, 0.0F, true);

        cube_r10 = new AdvancedModelBox(this);
        cube_r10.setRotationPoint(6.5F, 3.5F, -12.0F);
        head.addChild(cube_r10);
        setRotateAngle(cube_r10, 0.0F, 0.7854F, 0.0F);
        cube_r10.setTextureOffset(68, 108).addBox(0.0F, -7.5F, 0.0F, 2.0F, 15.0F, 0.0F, 0.0F, true);

        cube_r11 = new AdvancedModelBox(this);
        cube_r11.setRotationPoint(-6.5F, 3.5F, -12.0F);
        head.addChild(cube_r11);
        setRotateAngle(cube_r11, 0.0F, -0.7854F, 0.0F);
        cube_r11.setTextureOffset(0, 113).addBox(-2.0F, -7.5F, 0.0F, 2.0F, 15.0F, 0.0F, 0.0F, false);

        cube_r12 = new AdvancedModelBox(this);
        cube_r12.setRotationPoint(-6.5F, 11.0F, -6.0F);
        head.addChild(cube_r12);
        setRotateAngle(cube_r12, 0.0F, 0.0F, 0.7854F);
        cube_r12.setTextureOffset(56, 111).addBox(0.0F, 0.0F, -6.0F, 0.0F, 2.0F, 12.0F, 0.0F, true);

        cube_r13 = new AdvancedModelBox(this);
        cube_r13.setRotationPoint(6.5F, 11.0F, -6.0F);
        head.addChild(cube_r13);
        setRotateAngle(cube_r13, 0.0F, 0.0F, -0.7854F);
        cube_r13.setTextureOffset(56, 111).addBox(0.0F, 0.0F, -6.0F, 0.0F, 2.0F, 12.0F, 0.0F, false);

        cube_r14 = new AdvancedModelBox(this);
        cube_r14.setRotationPoint(6.5F, -2.0F, -6.0F);
        head.addChild(cube_r14);
        setRotateAngle(cube_r14, 0.0F, 0.0F, 0.7854F);
        cube_r14.setTextureOffset(56, 114).addBox(0.0F, -2.0F, -6.0F, 0.0F, 2.0F, 12.0F, 0.0F, false);

        cube_r15 = new AdvancedModelBox(this);
        cube_r15.setRotationPoint(-6.5F, -2.0F, -6.0F);
        head.addChild(cube_r15);
        setRotateAngle(cube_r15, 0.0F, 0.0F, -0.7854F);
        cube_r15.setTextureOffset(56, 114).addBox(0.0F, -2.0F, -6.0F, 0.0F, 2.0F, 12.0F, 0.0F, true);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 1.75F, 2.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(30, 36).addBox(-6.5F, 0.25F, -15.0F, 13.0F, 9.0F, 12.0F, 0.25F, false);

        lfin = new AdvancedModelBox(this);
        lfin.setRotationPoint(6.5F, 0.5F, -7.0F);
        head.addChild(lfin);
        lfin.setTextureOffset(0, 0).addBox(0.0F, -4.5F, 0.0F, 7.0F, 11.0F, 0.0F, 0.0F, true);

        rfin = new AdvancedModelBox(this);
        rfin.setRotationPoint(-6.5F, 0.5F, -7.0F);
        head.addChild(rfin);
        rfin.setTextureOffset(0, 0).addBox(-7.0F, -4.5F, 0.0F, 7.0F, 11.0F, 0.0F, 0.0F, false);

        lure = new AdvancedModelBox(this);
        lure.setRotationPoint(-0.25F, -2.0F, -11.5F);
        head.addChild(lure);
        lure.setTextureOffset(81, 54).addBox(0.0F, -11.0F, -0.5F, 0.0F, 13.0F, 5.0F, 0.0F, false);
        lure.setTextureOffset(86, 19).addBox(-1.0F, -10.0F, 0.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(4.5F, -9.5F, -1.0F);
        body.addChild(lleg);
        lleg.setTextureOffset(58, 80).addBox(-2.0F, 0.5F, -1.0F, 4.0F, 9.0F, 4.0F, -0.001F, true);
        lleg.setTextureOffset(44, 0).addBox(-2.0F, 9.5F, -5.0F, 8.0F, 0.0F, 8.0F, -0.001F, true);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-4.5F, -9.5F, -1.0F);
        body.addChild(rleg);
        rleg.setTextureOffset(58, 80).addBox(-2.0F, 0.5F, -1.0F, 4.0F, 9.0F, 4.0F, -0.001F, false);
        rleg.setTextureOffset(44, 0).addBox(-6.0F, 9.5F, -5.0F, 8.0F, 0.0F, 8.0F, -0.001F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-9.0F, -27.0F, 4.5F);
        body.addChild(rarm);
        rarm.setTextureOffset(80, 42).addBox(-5.5F, 19.0F, -4.5F, 6.0F, 4.0F, 6.0F, 0.0F, false);
        rarm.setTextureOffset(104, 11).addBox(-5.5F, -2.0F, -4.5F, 6.0F, 21.0F, 6.0F, 0.25F, false);
        rarm.setTextureOffset(0, 42).addBox(-2.5F, 4.0F, 1.5F, 0.0F, 13.0F, 6.0F, 0.0F, true);
        rarm.setTextureOffset(74, 72).addBox(-8.5F, -4.0F, -5.5F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        rarm.setTextureOffset(68, 17).addBox(-5.5F, -2.0F, -4.5F, 6.0F, 21.0F, 6.0F, 0.0F, false);
        rarm.setTextureOffset(122, 98).addBox(-8.5F, 4.0F, -1.5F, 3.0F, 15.0F, 0.0F, 0.0F, false);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(9.0F, -27.0F, 4.5F);
        body.addChild(larm);
        larm.setTextureOffset(80, 42).addBox(-0.5F, 19.0F, -4.5F, 6.0F, 4.0F, 6.0F, 0.0F, true);
        larm.setTextureOffset(0, 42).addBox(2.5F, 4.0F, 1.5F, 0.0F, 13.0F, 6.0F, 0.0F, false);
        larm.setTextureOffset(74, 72).addBox(0.5F, -4.0F, -5.5F, 8.0F, 8.0F, 8.0F, 0.0F, true);
        larm.setTextureOffset(68, 17).addBox(-0.5F, -2.0F, -4.5F, 6.0F, 21.0F, 6.0F, 0.0F, true);
        larm.setTextureOffset(104, 11).addBox(-0.5F, -2.0F, -4.5F, 6.0F, 21.0F, 6.0F, 0.25F, true);
        larm.setTextureOffset(122, 98).addBox(5.5F, 4.0F, -1.5F, 3.0F, 15.0F, 0.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -11.0F, 2.5F);
        body.addChild(tail);
        tail.setTextureOffset(0, 11).addBox(0.0F, -7.0F, -4.5F, 0.0F, 16.0F, 21.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, tail, head, jaw, rarm, larm, rleg, lleg, lfin, rfin, lure, cube_r1, cube_r2, cube_r3, cube_r4, cube_r5, cube_r6, cube_r7, cube_r7, cube_r8, cube_r9, cube_r10, cube_r11, cube_r12, cube_r13, cube_r14, cube_r15);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(DeepOneKnightEntity.ANIMATION_THROW);
        animator.startKeyframe(10);
        animator.rotate(jaw, (float) Math.toRadians(30), 0, 0);
        animator.rotate(lure, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(15), 0);
        animator.rotate(head, (float) Math.toRadians(-15), (float) Math.toRadians(-15), 0);
        animator.rotate(rarm, (float) Math.toRadians(45), (float) Math.toRadians(-30), (float) Math.toRadians(100));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(jaw, (float) Math.toRadians(25), 0, 0);
        animator.rotate(lure, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(-30), 0);
        animator.rotate(head, (float) Math.toRadians(-15), (float) Math.toRadians(15), 0);
        animator.rotate(rarm, (float) Math.toRadians(-45), 0, (float) Math.toRadians(80));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(DeepOneKnightEntity.ANIMATION_SCRATCH);
        animator.startKeyframe(5);
        animator.rotate(jaw, (float) Math.toRadians(15), 0, 0);
        animator.rotate(lure, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(10), 0);
        animator.rotate(rarm, (float) Math.toRadians(-75), (float) Math.toRadians(30), (float) Math.toRadians(65));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(jaw, (float) Math.toRadians(15), 0, 0);
        animator.rotate(lure, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(-10), 0);
        animator.move(rarm, 0, 0, -3);
        animator.rotate(rarm, (float) Math.toRadians(-75), (float) Math.toRadians(-30), (float) Math.toRadians(25));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(jaw, (float) Math.toRadians(15), 0, 0);
        animator.rotate(lure, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(larm, (float) Math.toRadians(-75), (float) Math.toRadians(-30), (float) Math.toRadians(-65));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(jaw, (float) Math.toRadians(15), 0, 0);
        animator.rotate(lure, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(10), 0);
        animator.move(larm, 0, 0, -3);
        animator.rotate(larm, (float) Math.toRadians(-75), (float) Math.toRadians(30), (float) Math.toRadians(-25));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(DeepOneKnightEntity.ANIMATION_BITE);
        animator.startKeyframe(5);
        animator.rotate(lure, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(body, (float) Math.toRadians(10), 0, 0);
        animator.rotate(rleg, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(lleg, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(30), 0, (float) Math.toRadians(10));
        animator.rotate(larm, (float) Math.toRadians(30), 0, (float) Math.toRadians(-10));
        animator.rotate(head, (float) Math.toRadians(-30), 0, 0);
        animator.move(head, 0, -1, -2);
        animator.move(body, 0, 1, -1);
        animator.move(rleg, 0, -1.5F, -1);
        animator.move(lleg, 0, -1.5F, -1);
        animator.rotate(jaw, (float) Math.toRadians(55), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(3);
        animator.setAnimation(DeepOneKnightEntity.ANIMATION_TRADE);
        animator.startKeyframe(10);
        animator.move(head, 0, -1, -2);
        animator.rotate(head, (float) Math.toRadians(25), 0, (float) Math.toRadians(-10));
        animator.rotate(rarm, (float) Math.toRadians(-35), (float) Math.toRadians(-20), (float) Math.toRadians(-10));
        animator.rotate(larm, (float) Math.toRadians(-45), (float) Math.toRadians(20), (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.move(head, 0, -1, -2);
        animator.rotate(head, (float) Math.toRadians(15), 0, (float) Math.toRadians(10));
        animator.rotate(lure, (float) Math.toRadians(15), 0, (float) Math.toRadians(10));
        animator.rotate(rarm, (float) Math.toRadians(-45), (float) Math.toRadians(-20), (float) Math.toRadians(-10));
        animator.rotate(larm, (float) Math.toRadians(-45), (float) Math.toRadians(20), (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.startKeyframe(20);
        animator.move(head, 0, -1, -2);
        animator.rotate(head, (float) Math.toRadians(15), 0, (float) Math.toRadians(-10));
        animator.rotate(lure, (float) Math.toRadians(15), 0, (float) Math.toRadians(-10));
        animator.rotate(rarm, (float) Math.toRadians(-45), (float) Math.toRadians(-20), (float) Math.toRadians(-10));
        animator.rotate(larm, (float) Math.toRadians(-45), (float) Math.toRadians(20), (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(larm, (float) Math.toRadians(-100), (float) Math.toRadians(-10), (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.resetKeyframe(10);
    }


    @Override
    public void setupAnim(DeepOneKnightEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float partialTick = ageInTicks - entity.tickCount;
        float swim = entity.getSwimAmount(partialTick);
        float clampedYaw = netHeadYaw / 57.295776F;
        float fishPitchAmount = entity.getFishPitch(partialTick) / 57.295776F;
        float headPitchAmount = headPitch / 57.295776F;
        float walkSpeed = 1F;
        float walkDegree = 1F;
        float swimSpeed = 0.25F;
        float swimDegree = 0.5F;
        float walkAmount = Math.min(limbSwingAmount * 2F, 1F) * (1 - swim);
        float swimAmount = limbSwingAmount * swim;
        this.walk(head, 0.1F, 0.05F, false, 0F, 0.02F, ageInTicks, 1F);
        this.swing(tail, 0.1F, 0.05F, false, -2.5F, 0.0F, ageInTicks, 1F);
        this.walk(larm, 0.1F, 0.05F, true, -1F, 0F, ageInTicks, 1F);
        this.walk(rarm, 0.1F, 0.05F, false, -1F, 0F, ageInTicks, 1F);
        this.walk(lure, 0.1F, 0.2F, false, 1F, -0.2F, ageInTicks, 1F);
        progressRotationPrev(body, walkAmount, (float) Math.toRadians(15), 0, 0, 1F);
        progressRotationPrev(head, walkAmount, (float) Math.toRadians(-15), 0, 0, 1F);
        progressRotationPrev(lleg, walkAmount, (float) Math.toRadians(-15), 0, 0, 1F);
        progressRotationPrev(rleg, walkAmount, (float) Math.toRadians(-15), 0, 0, 1F);
        progressRotationPrev(body, swim, (float) Math.toRadians(80), 0, 0, 1F);
        progressRotationPrev(head, swim, (float) Math.toRadians(-70), 0, 0, 1F);
        progressRotationPrev(tail, swim, (float) Math.toRadians(-50), 0, 0, 1F);
        progressPositionPrev(body, swim, 0, -6, 25, 1F);
        progressPositionPrev(rarm, swim, 0, -4, -2, 1F);
        progressPositionPrev(larm, swim, 0, -4, -2, 1F);
        this.flap(body, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, walkAmount);
        this.flap(lleg, walkSpeed, walkDegree * 0.1F, true, 1F, 0F, limbSwing, walkAmount);
        this.flap(rleg, walkSpeed, walkDegree * 0.1F, true, 1F, 0F, limbSwing, walkAmount);
        this.flap(head, walkSpeed, walkDegree * 0.1F, true, 2F, 0F, limbSwing, walkAmount);
        this.flap(larm, walkSpeed, walkDegree * 0.1F, true, 0F, 0F, limbSwing, walkAmount);
        this.flap(rarm, walkSpeed, walkDegree * 0.1F, false, 0F, 0F, limbSwing, walkAmount);
        float bodyWalkBob = -Math.abs(ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -1.5F, 1, false));
        float bodySwimBob = -ACMath.walkValue(limbSwing, swimAmount, swimSpeed, 0, 3, false);
        this.body.rotationPointY += bodyWalkBob + bodySwimBob;
        this.lleg.rotationPointY -= bodyWalkBob;
        this.rleg.rotationPointY -= bodyWalkBob;
        lleg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -2.6F, 5, true)) - walkAmount * 2F;
        rleg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -2.6F, 5, false)) - walkAmount * 2F;
        this.walk(lleg, walkSpeed, walkDegree, false, -1, 0F, limbSwing, walkAmount);
        this.walk(rleg, walkSpeed, walkDegree, true, -1, 0F, limbSwing, walkAmount);
        this.walk(rarm, walkSpeed, walkDegree * 0.2F, true, -3, 0.4F, limbSwing, walkAmount);
        this.walk(larm, walkSpeed, walkDegree * 0.2F, false, -3, -0.4F, limbSwing, walkAmount);
        this.swing(tail, walkSpeed, walkDegree * 0.2F, false, -1, -0F, limbSwing, walkAmount);

        this.flap(body, swimSpeed, swimDegree * 1F, true, 0F, 0F, limbSwing, swimAmount);
        this.swing(head, swimSpeed, swimDegree * 1, false, 0.5F, 0F, limbSwing, swimAmount);

        this.flap(larm, swimSpeed, swimDegree * 2.15F, true, -1.5F, 1.0F, limbSwing, swimAmount);
        this.swing(larm, swimSpeed, swimDegree, true, -1.5F, 0, limbSwing, swimAmount);
        this.walk(larm, swimSpeed, swimDegree, true, -2F, -0.2F, limbSwing, swimAmount);
        this.flap(rarm, swimSpeed, swimDegree * 2.15F, false, -3F, 1.0F, limbSwing, swimAmount);
        this.swing(rarm, swimSpeed, swimDegree, false, -1.5F, 0, limbSwing, swimAmount);
        this.walk(rarm, swimSpeed, swimDegree, false, -4.5F, -0.2F, limbSwing, swimAmount);
        this.flap(tail, swimSpeed, swimDegree * 0.75F, false, -2, 0F, limbSwing, swimAmount);

        this.walk(lure, swimSpeed * 0.1F, swimDegree * 0.2F, true, 2F, 0.3F, limbSwing, swimAmount);
        this.walk(rleg, swimSpeed * 1.5F, swimDegree * 1F, true, 2F, 0.0F, limbSwing, swimAmount);
        this.walk(lleg, swimSpeed * 1.5F, swimDegree * 1F, false, 2F, 0.0F, limbSwing, swimAmount);
        if (entity.getAnimation() != entity.getTradingAnimation()) {
            this.body.rotateAngleX += fishPitchAmount;
            this.head.rotateAngleX += headPitchAmount;
        }
        this.head.rotateAngleY += clampedYaw;
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        body.translateAndRotate(poseStack);
        if (arm == HumanoidArm.RIGHT) {
            rarm.translateAndRotate(poseStack);
            poseStack.translate(-0.1F, 0.7F, -0.2F);
        } else {
            larm.translateAndRotate(poseStack);
            poseStack.translate(0.1F, 0.7F, -0.2F);
        }
    }
}
