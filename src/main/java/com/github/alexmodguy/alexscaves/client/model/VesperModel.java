package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.VesperEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class VesperModel extends AdvancedEntityModel<VesperEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox torso;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox lfoot;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox rfoot;
    private final AdvancedModelBox head;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox cube_r5;
    private final AdvancedModelBox cube_r6;
    private final AdvancedModelBox cube_r7;
    private final AdvancedModelBox cube_r8;
    private final AdvancedModelBox cube_r9;
    private final AdvancedModelBox cube_r10;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox cube_r11;
    private final AdvancedModelBox cube_r12;
    private final AdvancedModelBox cube_r13;
    private final AdvancedModelBox cube_r14;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox lear;
    private final AdvancedModelBox rear;
    private final AdvancedModelBox lwing;
    private final AdvancedModelBox lfinger;
    private final AdvancedModelBox lwingTip;
    private final AdvancedModelBox rwing;
    private final AdvancedModelBox rfinger;
    private final AdvancedModelBox rwingTip;
    private final AdvancedModelBox tail;
    private final ModelAnimator animator;

    public VesperModel() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        torso = new AdvancedModelBox(this);
        torso.setRotationPoint(0.0F, -10.0F, 0.5F);
        root.addChild(torso);
        torso.setTextureOffset(30, 44).addBox(-3.5F, -4.0F, -2.5F, 7.0F, 8.0F, 5.0F, 0.0F, false);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(2.5F, -6.3431F, -2.0F);
        root.addChild(lleg);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.3431F, 0.0F);
        lleg.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.7854F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(68, 32).addBox(-2.0F, 0.0F, -4.0F, 3.0F, 4.0F, 4.0F, 0.0F, true);

        lfoot = new AdvancedModelBox(this);
        lfoot.setRotationPoint(0.0F, 6.0F, 0.0F);
        lleg.addChild(lfoot);
        lfoot.setTextureOffset(68, 26).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 1.0F, 5.0F, 0.0F, true);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-2.5F, -6.3431F, -2.0F);
        root.addChild(rleg);


        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.3431F, 0.0F);
        rleg.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.7854F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(68, 32).addBox(-1.0F, 0.0F, -4.0F, 3.0F, 4.0F, 4.0F, 0.0F, false);

        rfoot = new AdvancedModelBox(this);
        rfoot.setRotationPoint(0.0F, 6.0F, 0.0F);
        rleg.addChild(rfoot);
        rfoot.setTextureOffset(68, 26).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 1.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -12.5F, 1.0F);
        root.addChild(head);
        head.setTextureOffset(0, 0).addBox(-6.5F, -12.5F, -10.0F, 13.0F, 13.0F, 13.0F, 0.0F, false);
        head.setTextureOffset(0, 44).addBox(-6.5F, -12.5F, 3.0F, 13.0F, 13.0F, 2.0F, 0.0F, false);

        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(-6.5F, -6.0F, -10.0F);
        head.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.0F, 1.309F, 0.0F);
        cube_r3.setTextureOffset(32, 67).addBox(-5.0F, -6.5F, 0.0F, 5.0F, 13.0F, 0.0F, 0.0F, true);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(-6.5F, -6.0F, -3.0F);
        head.addChild(cube_r4);
        setRotateAngle(cube_r4, 0.0F, 1.309F, 0.0F);
        cube_r4.setTextureOffset(32, 67).addBox(-5.0F, -6.5F, 0.0F, 5.0F, 13.0F, 0.0F, 0.0F, true);

        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(0.0F, 0.5F, -3.0F);
        head.addChild(cube_r5);
        setRotateAngle(cube_r5, 1.309F, 0.0F, 0.0F);
        cube_r5.setTextureOffset(52, 17).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 5.0F, 0.0F, 0.0F, false);

        cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(0.0F, 0.5F, -10.0F);
        head.addChild(cube_r6);
        setRotateAngle(cube_r6, 1.309F, 0.0F, 0.0F);
        cube_r6.setTextureOffset(52, 17).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 5.0F, 0.0F, 0.0F, false);

        cube_r7 = new AdvancedModelBox(this);
        cube_r7.setRotationPoint(6.5F, -6.0F, -3.0F);
        head.addChild(cube_r7);
        setRotateAngle(cube_r7, 0.0F, -1.309F, 0.0F);
        cube_r7.setTextureOffset(32, 67).addBox(0.0F, -6.5F, 0.0F, 5.0F, 13.0F, 0.0F, 0.0F, false);

        cube_r8 = new AdvancedModelBox(this);
        cube_r8.setRotationPoint(6.5F, -6.0F, -10.0F);
        head.addChild(cube_r8);
        setRotateAngle(cube_r8, 0.0F, -1.309F, 0.0F);
        cube_r8.setTextureOffset(32, 67).addBox(0.0F, -6.5F, 0.0F, 5.0F, 13.0F, 0.0F, 0.0F, false);

        cube_r9 = new AdvancedModelBox(this);
        cube_r9.setRotationPoint(0.0F, -12.5F, -10.0F);
        head.addChild(cube_r9);
        setRotateAngle(cube_r9, -1.309F, 0.0F, 0.0F);
        cube_r9.setTextureOffset(26, 62).addBox(-6.5F, -5.0F, 0.0F, 13.0F, 5.0F, 0.0F, 0.0F, false);

        cube_r10 = new AdvancedModelBox(this);
        cube_r10.setRotationPoint(0.0F, -12.5F, -3.0F);
        head.addChild(cube_r10);
        setRotateAngle(cube_r10, -1.309F, 0.0F, 0.0F);
        cube_r10.setTextureOffset(26, 62).addBox(-6.5F, -5.0F, 0.0F, 13.0F, 5.0F, 0.0F, 0.0F, false);

        nose = new AdvancedModelBox(this);
        nose.setRotationPoint(0.0F, -4.0F, -9.5F);
        head.addChild(nose);
        nose.setTextureOffset(49, 52).addBox(-3.5F, -2.75F, -2.5F, 7.0F, 5.0F, 5.0F, 0.0F, false);
        nose.setTextureOffset(66, 62).addBox(-3.5F, -13.75F, -2.5F, 7.0F, 11.0F, 0.0F, 0.0F, false);
        nose.setTextureOffset(66, 8).addBox(-2.5F, 2.25F, -2.24F, 5.0F, 2.0F, 5.0F, 0.0F, false);

        cube_r11 = new AdvancedModelBox(this);
        cube_r11.setRotationPoint(-7.1955F, 4.25F, -4.0307F);
        nose.addChild(cube_r11);
        setRotateAngle(cube_r11, 0.0F, -0.3927F, 0.0F);
        cube_r11.setTextureOffset(57, 22).addBox(0.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, true);

        cube_r12 = new AdvancedModelBox(this);
        cube_r12.setRotationPoint(7.1955F, 4.25F, -4.0307F);
        nose.addChild(cube_r12);
        setRotateAngle(cube_r12, 0.0F, 0.3927F, 0.0F);
        cube_r12.setTextureOffset(57, 22).addBox(-6.0F, -2.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);

        cube_r13 = new AdvancedModelBox(this);
        cube_r13.setRotationPoint(3.5F, -5.75F, -2.5F);
        nose.addChild(cube_r13);
        setRotateAngle(cube_r13, 0.0F, 0.3927F, 0.0F);
        cube_r13.setTextureOffset(0, 64).addBox(0.0F, -8.0F, 0.0F, 7.0F, 16.0F, 0.0F, 0.0F, false);

        cube_r14 = new AdvancedModelBox(this);
        cube_r14.setRotationPoint(-3.5F, -5.75F, -2.5F);
        nose.addChild(cube_r14);
        setRotateAngle(cube_r14, 0.0F, -0.3927F, 0.0F);
        cube_r14.setTextureOffset(0, 64).addBox(-7.0F, -8.0F, 0.0F, 7.0F, 16.0F, 0.0F, 0.0F, true);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 2.25F, -0.245F);
        nose.addChild(jaw);
        jaw.setTextureOffset(14, 67).addBox(-3.0F, -1.0F, -2.005F, 6.0F, 3.0F, 3.0F, 0.0F, false);

        lear = new AdvancedModelBox(this);
        lear.setRotationPoint(5.5F, -9.5F, -10.0F);
        head.addChild(lear);
        lear.setTextureOffset(52, 62).addBox(-3.0F, -13.0F, -1.0F, 6.0F, 15.0F, 1.0F, 0.0F, true);

        rear = new AdvancedModelBox(this);
        rear.setRotationPoint(-5.5F, -9.5F, -10.0F);
        head.addChild(rear);
        rear.setTextureOffset(52, 62).addBox(-3.0F, -13.0F, -1.0F, 6.0F, 15.0F, 1.0F, 0.0F, false);

        lwing = new AdvancedModelBox(this);
        lwing.setRotationPoint(3.0F, -10.75F, 0.5F);
        root.addChild(lwing);
        lwing.setTextureOffset(0, 26).addBox(-2.5F, 0.75F, 0.0F, 25.0F, 18.0F, 0.0F, 0.0F, false);
        lwing.setTextureOffset(39, 4).addBox(0.5F, -1.25F, -1.0F, 17.0F, 2.0F, 2.0F, 0.0F, false);

        lfinger = new AdvancedModelBox(this);
        lfinger.setRotationPoint(16.5F, -0.75F, 0.0F);
        lwing.addChild(lfinger);
        lfinger.setTextureOffset(42, 67).addBox(-1.0F, -4.5F, -0.5F, 2.0F, 5.0F, 1.0F, 0.0F, false);
        lfinger.setTextureOffset(39, 8).addBox(-1.0F, -7.5F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, false);

        lwingTip = new AdvancedModelBox(this);
        lwingTip.setRotationPoint(17.5F, -0.25F, 0.005F);
        lwing.addChild(lwingTip);
        lwingTip.setTextureOffset(39, 0).addBox(0.0F, -1.0F, -1.005F, 17.0F, 2.0F, 2.0F, 0.0F, false);
        lwingTip.setTextureOffset(0, 80).addBox(-11.0F, -1.0F, 0.005F, 43.0F, 28.0F, 0.0F, 0.0F, false);
        lwingTip.setTextureOffset(0, 80).addBox(-11.0F, -1.0F, -0.015F, 43.0F, 28.0F, 0.0F, 0.0F, false);

        rwing = new AdvancedModelBox(this);
        rwing.setRotationPoint(-3.0F, -10.75F, 0.5F);
        root.addChild(rwing);
        rwing.setTextureOffset(0, 26).addBox(-22.5F, 0.75F, 0.0F, 25.0F, 18.0F, 0.0F, 0.0F, true);
        rwing.setTextureOffset(39, 4).addBox(-17.5F, -1.25F, -1.0F, 17.0F, 2.0F, 2.0F, 0.0F, true);

        rfinger = new AdvancedModelBox(this);
        rfinger.setRotationPoint(-16.5F, -0.75F, 0.0F);
        rwing.addChild(rfinger);
        rfinger.setTextureOffset(42, 67).addBox(-1.0F, -4.5F, -0.5F, 2.0F, 5.0F, 1.0F, 0.0F, true);
        rfinger.setTextureOffset(39, 8).addBox(-1.0F, -7.5F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, true);

        rwingTip = new AdvancedModelBox(this);
        rwingTip.setRotationPoint(-17.5F, -0.25F, 0.005F);
        rwing.addChild(rwingTip);
        rwingTip.setTextureOffset(39, 0).addBox(-17.0F, -1.0F, -1.005F, 17.0F, 2.0F, 2.0F, 0.0F, true);
        rwingTip.setTextureOffset(0, 80).addBox(-32.0F, -1.0F, 0.005F, 43.0F, 28.0F, 0.0F, 0.0F, true);
        rwingTip.setTextureOffset(0, 80).addBox(-32.0F, -1.0F, -0.015F, 43.0F, 28.0F, 0.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -6.0F, 3.0F);
        root.addChild(tail);
        tail.setTextureOffset(43, 8).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 0.0F, 9.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, head, nose, jaw, tail, torso, lleg, rleg, lfoot, rfoot, lear, rear, lwing, lwingTip, lfinger, rwing, rwingTip, rfinger, cube_r1, cube_r2, cube_r3, cube_r4, cube_r5, cube_r6, cube_r7, cube_r8, cube_r9, cube_r10, cube_r11, cube_r12, cube_r13, cube_r14);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(VesperEntity.ANIMATION_BITE);
        animator.startKeyframe(7);
        animator.move(head, 0, 0, 1);
        animator.move(jaw, 0, 1, 1);
        animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(nose, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(65), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.move(head, 0, 0, -1);
        animator.move(jaw, 0, 1, 1);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    @Override
    public void setupAnim(VesperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float partialTick = ageInTicks - entity.tickCount;
        float capturedProgress = entity.getCapturedProgress(partialTick);
        float sleepProgress = entity.getSleepProgress(partialTick);
        float foldWingsProgress = Math.max(capturedProgress, sleepProgress);
        float flyProgress = entity.getFlyProgress(partialTick);
        float groundProgress = (1F - Math.max(flyProgress, sleepProgress)) * (1F - capturedProgress);
        float groundMove = groundProgress * limbSwingAmount;
        float walkSpeed = 1F;
        float walkDegree = 1F;
        float rollAmount = entity.getFlightRoll(partialTick) / 57.295776F * flyProgress;
        float flightPitchAmount = entity.getFlightPitch(partialTick) / 57.295776F * flyProgress;
        progressPositionPrev(root, sleepProgress, 0, -24, 0, 1F);
        progressPositionPrev(head, sleepProgress, 0, -2, -1, 1F);
        progressPositionPrev(rwing, sleepProgress, 0, -3, 1, 1F);
        progressPositionPrev(lwing, sleepProgress, 0, -3, 1, 1F);
        progressPositionPrev(rleg, sleepProgress, 0, -1, 0, 1F);
        progressPositionPrev(lleg, sleepProgress, 0, -1, 0, 1F);
        progressRotationPrev(rwing, foldWingsProgress, (float) Math.toRadians(-35), (float) Math.toRadians(-30), (float) Math.toRadians(35), 1F);
        progressRotationPrev(rwingTip, foldWingsProgress, 0, (float) Math.toRadians(-135), 0, 1F);
        progressRotationPrev(lwing, foldWingsProgress, (float) Math.toRadians(-35), (float) Math.toRadians(30), (float) Math.toRadians(-35), 1F);
        progressRotationPrev(lwingTip, foldWingsProgress, 0, (float) Math.toRadians(145), 0, 1F);
        progressRotationPrev(lleg, sleepProgress, 0, (float) Math.toRadians(-45), 0, 1F);
        progressRotationPrev(rleg, sleepProgress, 0, (float) Math.toRadians(45), 0, 1F);
        progressRotationPrev(head, sleepProgress, (float) Math.toRadians(-65), 0, 0, 1F);
        progressRotationPrev(tail, sleepProgress, (float) Math.toRadians(-45), 0, 0, 1F);
        progressRotationPrev(root, sleepProgress, 0, 0, (float) Math.toRadians(180 * (entity.getId() % 2 == 0 ? -1 : 1)), 1F);
        progressPositionPrev(head, groundProgress, 0, 3, -1, 1F);
        progressPositionPrev(rwing, groundProgress, 0, -1, -1, 1F);
        progressPositionPrev(lwing, groundProgress, 0, -1, -1, 1F);
        progressPositionPrev(lleg, groundProgress, 0, 0, 3, 1F);
        progressPositionPrev(rleg, groundProgress, 0, 0, 3, 1F);
        progressRotationPrev(rwing, groundProgress, (float) Math.toRadians(90), 0, (float) Math.toRadians(-40), 1F);
        progressRotationPrev(rwingTip, groundProgress, 0, (float) Math.toRadians(30), (float) Math.toRadians(-70), 1F);
        progressRotationPrev(rfinger, groundProgress, 0, (float) Math.toRadians(-50), 0, 1F);
        progressRotationPrev(lwing, groundProgress, (float) Math.toRadians(90), 0, (float) Math.toRadians(40), 1F);
        progressRotationPrev(lwingTip, groundProgress, 0, (float) Math.toRadians(-30), (float) Math.toRadians(70), 1F);
        progressRotationPrev(lfinger, groundProgress, 0, (float) Math.toRadians(50), 0, 1F);
        progressPositionPrev(root, flyProgress, 0, -7, 7, 1F);
        progressPositionPrev(head, flyProgress, 0, -2, -4, 1F);
        progressPositionPrev(lleg, flyProgress, 0, -1, 2, 1F);
        progressPositionPrev(rleg, flyProgress, 0, -1, 2, 1F);
        progressRotationPrev(root, flyProgress, (float) Math.toRadians(90), 0, 0, 1F);
        progressRotationPrev(head, flyProgress, (float) Math.toRadians(-70), 0, 0, 1F);
        progressRotationPrev(tail, flyProgress, (float) Math.toRadians(-90), 0, 0, 1F);
        progressRotationPrev(lfoot, flyProgress, (float) Math.toRadians(70), 0, 0, 1F);
        progressRotationPrev(rfoot, flyProgress, (float) Math.toRadians(70), 0, 0, 1F);
        progressRotationPrev(rwing, flyProgress, 0, (float) Math.toRadians(30), 0, 1F);
        progressRotationPrev(rwingTip, flyProgress, 0, (float) Math.toRadians(-40), 0, 1F);
        progressRotationPrev(lwing, flyProgress, 0, (float) Math.toRadians(-30), 0, 1F);
        progressRotationPrev(lwingTip, flyProgress, 0, (float) Math.toRadians(40), 0, 1F);
        progressPositionPrev(root, capturedProgress, 0, -1, -7, 1F);
        progressRotationPrev(root, capturedProgress, (float) Math.toRadians(-40), 0, 0, 1F);
        progressRotationPrev(head, capturedProgress, (float) Math.toRadians(-40), 0, 0, 1F);
        progressRotationPrev(lleg, capturedProgress, (float) Math.toRadians(-30), 0, 0, 1F);
        progressRotationPrev(rleg, capturedProgress, (float) Math.toRadians(-30), 0, 0, 1F);
        this.lleg.setScale(1F, 1F + sleepProgress * 0.5F, 1F);
        this.lleg.scaleChildren = true;
        this.rleg.setScale(1F, 1F + sleepProgress * 0.5F, 1F);
        this.rleg.scaleChildren = true;
        float hopForwards = -Math.max(0, ACMath.walkValue(limbSwing, groundMove, walkSpeed, -4F, 8, false));
        this.walk(head, 0.2F, 0.03F, false, 2, 0F, ageInTicks, 1);
        this.walk(nose, 0.9F, 0.05F, false, 1, 0.02F, ageInTicks, 1);
        this.flap(lear, 0.1F, 0.05F, false, 4, 0.05F, ageInTicks, 1);
        this.flap(rear, 0.1F, 0.05F, true, 4, 0.05F, ageInTicks, 1);
        this.bob(head, 0.1F, 0.5F, false, ageInTicks, 1);
        this.swing(lwing, walkSpeed, walkDegree, false, 0F, 0.2F, limbSwing, groundMove);
        this.flap(lfinger, walkSpeed, walkDegree, false, 0F, 0.2F, limbSwing, groundMove);
        this.swing(rwing, walkSpeed, walkDegree, true, 0F, 0.2F, limbSwing, groundMove);
        this.flap(rfinger, walkSpeed, walkDegree, true, 0F, 0.2F, limbSwing, groundMove);
        this.walk(rleg, walkSpeed, walkDegree, true, -1.5F, 0.4F, limbSwing, groundMove);
        this.walk(rfoot, walkSpeed, walkDegree, false, -3.5F, 0.4F, limbSwing, groundMove);
        this.walk(lleg, walkSpeed, walkDegree, true, -1.5F, 0.4F, limbSwing, groundMove);
        this.walk(lfoot, walkSpeed, walkDegree, false, -3.5F, 0.4F, limbSwing, groundMove);
        this.root.rotationPointZ += hopForwards * 1.5F;
        this.root.rotationPointY += hopForwards;
        this.walk(lleg, 0.3F, 0.2F, false, 2, -0.1F, ageInTicks, flyProgress);
        this.walk(rleg, 0.3F, 0.2F, false, 2, -0.1F, ageInTicks, flyProgress);
        this.walk(head, 0.3F, 0.1F, false, 1F, 0F, ageInTicks, flyProgress);
        this.swing(rwing, 0.5F, 1F, false, 1F, -0.5F, ageInTicks, flyProgress);
        this.swing(lwing, 0.5F, 1F, true, 1F, -0.5F, ageInTicks, flyProgress);
        this.swing(rwingTip, 0.5F, 0.4F, false, 0F, 0F, ageInTicks, flyProgress);
        this.swing(lwingTip, 0.5F, 0.4F, true, 0F, 0F, ageInTicks, flyProgress);
        this.bob(root, 0.5F, 4, false, ageInTicks, flyProgress);
        this.bob(head, 0.5F, -1, false, ageInTicks, flyProgress);
        this.walk(lleg, 0.3F, 0.5F, true, 0F, -0.1F, ageInTicks, capturedProgress);
        this.walk(rleg, 0.3F, 0.5F, false, 0F, -0.1F, ageInTicks, capturedProgress);
        this.swing(head, 0.3F, 0.9F, false, 2F, 0F, ageInTicks, capturedProgress);
        float yawAmount = netHeadYaw / 57.295776F;
        float pitchAmount = headPitch / 57.295776F;
        this.head.rotateAngleX += pitchAmount;
        this.head.rotateAngleZ += yawAmount * flyProgress;
        this.head.rotateAngleY += yawAmount * (1F - flyProgress);
        root.rotateAngleX += flightPitchAmount;
        root.rotateAngleZ += rollAmount;
    }
}
