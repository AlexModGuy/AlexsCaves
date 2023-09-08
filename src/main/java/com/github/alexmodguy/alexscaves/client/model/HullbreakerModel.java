package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.HullbreakerEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class HullbreakerModel extends AdvancedEntityModel<HullbreakerEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox dorsal;
    private final AdvancedModelBox bottomFlipper;
    private final AdvancedModelBox lflipper;
    private final AdvancedModelBox rflipper;
    private final AdvancedModelBox body2;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox tail3;
    private final AdvancedModelBox dorsal2;
    private final AdvancedModelBox rbarb;
    private final AdvancedModelBox rbarb2;
    private final AdvancedModelBox rbarbLure;
    private final AdvancedModelBox lbarb;
    private final AdvancedModelBox lbarb2;
    private final AdvancedModelBox lbarbLure;
    private final AdvancedModelBox head;
    private final AdvancedModelBox teeth1;
    private final AdvancedModelBox teeth2;
    private final AdvancedModelBox teeth3;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox teeth4;
    private final AdvancedModelBox teeth5;
    private final ModelAnimator animator;

    public boolean straighten = false;

    public HullbreakerModel() {
        texWidth = 512;
        texHeight = 512;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 0.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 0.0F, -15.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-12.0F, -41.0F, -12.0F, 24.0F, 41.0F, 53.0F, 0.0F, false);

        dorsal = new AdvancedModelBox(this);
        dorsal.setRotationPoint(0.0F, -30.5F, -8.5F);
        body.addChild(dorsal);
        dorsal.setTextureOffset(0, 41).addBox(0.0F, -67.5F, -10.5F, 0.0F, 75.0F, 53.0F, 0.0F, false);

        bottomFlipper = new AdvancedModelBox(this);
        bottomFlipper.setRotationPoint(0.0F, -1.5F, 32.0F);
        body.addChild(bottomFlipper);
        bottomFlipper.setTextureOffset(122, 245).addBox(0.0F, -3.5F, -6.0F, 0.0F, 39.0F, 32.0F, 0.0F, false);

        lflipper = new AdvancedModelBox(this);
        lflipper.setRotationPoint(12.0F, -9.5F, -2.0F);
        body.addChild(lflipper);
        setRotateAngle(lflipper, 0.3927F, -0.7854F, 0.0F);
        lflipper.setTextureOffset(101, 0).addBox(0.0F, -31.5F, 0.0F, 47.0F, 41.0F, 0.0F, 0.0F, false);

        rflipper = new AdvancedModelBox(this);
        rflipper.setRotationPoint(-12.0F, -9.5F, -2.0F);
        body.addChild(rflipper);
        setRotateAngle(rflipper, 0.3927F, 0.7854F, 0.0F);
        rflipper.setTextureOffset(101, 0).addBox(-47.0F, -31.5F, 0.0F, 47.0F, 41.0F, 0.0F, 0.0F, true);

        body2 = new AdvancedModelBox(this);
        body2.setRotationPoint(0.0F, -17.0F, 40.0F);
        body.addChild(body2);
        body2.setTextureOffset(210, 106).addBox(-10.0F, -14.0F, -2.0F, 20.0F, 28.0F, 46.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, 1.5F, 43.75F);
        body2.addChild(tail);
        tail.setTextureOffset(0, 117).addBox(0.0F, -35.5F, 6.25F, 0.0F, 67.0F, 52.0F, 0.0F, false);
        tail.setTextureOffset(0, 236).addBox(-6.0F, -9.5F, -2.75F, 12.0F, 19.0F, 49.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setRotationPoint(0.0F, 0.0F, 58.25F);
        tail.addChild(tail2);
        tail2.setTextureOffset(106, 109).addBox(0.0F, -35.5F, 0.0F, 0.0F, 67.0F, 52.0F, 0.0F, false);

        tail3 = new AdvancedModelBox(this);
        tail3.setRotationPoint(0.0F, 0.0F, 52F);
        tail2.addChild(tail3);
        tail3.setTextureOffset(106, 42).addBox(0.0F, -35.5F, 0F, 0.0F, 67.0F, 52.0F, 0.0F, false);
        tail3.setTextureOffset(0, 0).addBox(-3.5F, -5.5F, 44F, 7.0F, 7.0F, 7.0F, 0.0F, false);

        dorsal2 = new AdvancedModelBox(this);
        dorsal2.setRotationPoint(0.0F, -14.5F, -3.5F);
        body2.addChild(dorsal2);
        dorsal2.setTextureOffset(104, 173).addBox(0.0F, -48.5F, -2.5F, 0.0F, 49.0F, 55.0F, 0.0F, false);

        rbarb = new AdvancedModelBox(this);
        rbarb.setRotationPoint(-9.0F, -0.5F, -3.0F);
        body.addChild(rbarb);
        rbarb.setTextureOffset(214, 208).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 17.0F, 2.0F, 0.0F, true);

        rbarb2 = new AdvancedModelBox(this);
        rbarb2.setRotationPoint(0.0F, 17.0F, 0.0F);
        rbarb.addChild(rbarb2);
        rbarb2.setTextureOffset(214, 208).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 17.0F, 2.0F, 0.0F, true);

        rbarbLure = new AdvancedModelBox(this);
        rbarbLure.setRotationPoint(0.0F, 17.0F, 0.0F);
        rbarb2.addChild(rbarbLure);
        rbarbLure.setTextureOffset(214, 208).addBox(0.0F, 0.5F, -3.0F, 0.0F, 24.0F, 32.0F, 0.0F, true);
        rbarbLure.setTextureOffset(30, 4).addBox(-2.5F, 21.5F, 3.0F, 5.0F, 5.0F, 5.0F, 0.0F, true);

        lbarb = new AdvancedModelBox(this);
        lbarb.setRotationPoint(9.0F, -0.5F, -3.0F);
        body.addChild(lbarb);
        lbarb.setTextureOffset(214, 208).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 17.0F, 2.0F, 0.0F, false);

        lbarb2 = new AdvancedModelBox(this);
        lbarb2.setRotationPoint(0.0F, 17.0F, 0.0F);
        lbarb.addChild(lbarb2);
        lbarb2.setTextureOffset(214, 208).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 17.0F, 2.0F, 0.0F, false);

        lbarbLure = new AdvancedModelBox(this);
        lbarbLure.setRotationPoint(0.0F, 17.0F, 0.0F);
        lbarb2.addChild(lbarbLure);
        lbarbLure.setTextureOffset(214, 208).addBox(0.0F, 0.5F, -3.0F, 0.0F, 24.0F, 32.0F, 0.0F, false);
        lbarbLure.setTextureOffset(30, 4).addBox(-2.5F, 21.5F, 3.0F, 5.0F, 5.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(1.0F, -27.0F, -10.0F);
        body.addChild(head);
        head.setTextureOffset(278, 255).addBox(-10.0F, 8.0F, -12.0F, 18.0F, 18.0F, 13.0F, -0.01F, false);
        head.setTextureOffset(154, 0).addBox(-11.0F, 0.0F, -52.0F, 20.0F, 26.0F, 53.0F, 0.0F, false);
        head.setTextureOffset(0, 304).addBox(-11.0F, 0.0F, -51.0F, 20.0F, 11.0F, 38.0F, 0.0F, false);

        teeth1 = new AdvancedModelBox(this);
        teeth1.setRotationPoint(-1.0F, 15.0F, -51.1464F);
        head.addChild(teeth1);
        setRotateAngle(teeth1, 0.0F, -0.7854F, 0.0F);
        teeth1.setTextureOffset(0, 304).addBox(-6.5F, -4.0F, -6.5F, 13.0F, 8.0F, 13.0F, 0.01F, false);

        teeth2 = new AdvancedModelBox(this);
        teeth2.setRotationPoint(-1.0F, 11.5F, -52.0F);
        head.addChild(teeth2);
        setRotateAngle(teeth2, 0.0F, 0.7854F, 0.0F);
        teeth2.setTextureOffset(278, 240).addBox(-39.0F, -11.44F, -7.0F, 32.0F, 5.0F, 10.0F, 0.07F, true);

        teeth3 = new AdvancedModelBox(this);
        teeth3.setRotationPoint(-1.0F, 11.5F, -52.0F);
        head.addChild(teeth3);
        setRotateAngle(teeth3, 0.0F, -0.7854F, 0.0F);
        teeth3.setTextureOffset(278, 240).addBox(7.0F, -11.44F, -7.0F, 32.0F, 5.0F, 10.0F, 0.07F, false);
        teeth3.setTextureOffset(247, 0).addBox(-7.0F, -11.44F, -7.0F, 14.0F, 36.0F, 14.0F, 0.07F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(-1.0F, 26.0F, -12.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(0, 0).addBox(-9.0F, -19.0F, -54.0F, 0.0F, 39.0F, 14.0F, 0.0F, true);
        jaw.setTextureOffset(284, 136).addBox(-9.0F, 7.0F, -54.0F, 18.0F, 0.0F, 14.0F, 0.0F, true);
        jaw.setTextureOffset(0, 0).addBox(9.0F, -19.0F, -54.0F, 0.0F, 39.0F, 14.0F, 0.0F, false);
        jaw.setTextureOffset(214, 180).addBox(-9.0F, 0.0F, -40.0F, 18.0F, 7.0F, 53.0F, 0.0F, false);

        teeth4 = new AdvancedModelBox(this);
        teeth4.setRotationPoint(9.0F, 0.0F, -13.5F);
        jaw.addChild(teeth4);
        setRotateAngle(teeth4, 0.0F, 0.0F, 0.3927F);
        teeth4.setTextureOffset(210, 20).addBox(0.0F, -26.0F, -33.5F, 0.0F, 26.0F, 60.0F, 0.0F, false);

        teeth5 = new AdvancedModelBox(this);
        teeth5.setRotationPoint(-9.0F, 0.0F, -13.5F);
        jaw.addChild(teeth5);
        setRotateAngle(teeth5, 0.0F, 0.0F, -0.3927F);
        teeth5.setTextureOffset(210, 20).addBox(0.0F, -26.0F, -33.5F, 0.0F, 26.0F, 60.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, body2, tail, head, tail, tail2, tail3, dorsal, dorsal2, jaw, rbarb, rbarb2, rbarbLure, lbarb, lbarb2, lbarbLure, teeth1, teeth2, teeth3, teeth4, teeth5, rflipper, lflipper);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(HullbreakerEntity.ANIMATION_PUZZLE);
        animator.startKeyframe(10);
        animator.move(head, -5, -5, 0);
        animator.rotate(head, 0, (float) Math.toRadians(-5), (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.startKeyframe(20);
        animator.move(head, 5, -5, 0);
        animator.rotate(head, 0, (float) Math.toRadians(5), (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.startKeyframe(20);
        animator.move(head, -5, -5, 0);
        animator.rotate(head, 0, (float) Math.toRadians(-5), (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(HullbreakerEntity.ANIMATION_BITE);
        animator.startKeyframe(10);
        animator.move(head, 0, -2, 10);
        animator.rotate(head, (float) Math.toRadians(-40), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(80), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -10);
        animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(HullbreakerEntity.ANIMATION_BASH);
        animator.startKeyframe(10);
        animator.move(head, 0, -11, -3);
        animator.rotate(body, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(body2, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(50), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, -5, -20);
        animator.move(head, 0, -9, -3);
        animator.rotate(head, (float) Math.toRadians(30), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(HullbreakerEntity.ANIMATION_DIE);
        animator.startKeyframe(10);
        animator.move(head, 0, -2, 10);
        animator.rotate(head, (float) Math.toRadians(-40), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(80), 0, 0);
        animator.rotate(body, 0, 0, (float) Math.toRadians(5));
        animator.endKeyframe();
        animator.startKeyframe(35);
        animator.move(head, 0, -2, 10);
        animator.rotate(head, (float) Math.toRadians(-30), 0, (float) Math.toRadians(-20));
        animator.rotate(jaw, (float) Math.toRadians(40), 0, 0);
        animator.rotate(body, 0, 0, (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    @Override
    public void setupAnim(HullbreakerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float partialTicks = ageInTicks - entity.tickCount;
        float bodyYRot = (entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks);
        float clampedYaw = netHeadYaw / 57.295776F;
        float fishPitchAmount = entity.getFishPitch(partialTicks) / 57.295776F;
        float headPitchAmount = headPitch / 57.295776F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.5F;
        float swimSpeed = 0.45F;
        float swimDegree = 0.4F;
        float landProgress = entity.getLandProgress(partialTicks);
        float stillAmount = (1f - limbSwingAmount) * (1f - landProgress);
        this.body.rotationPointY -= ACMath.walkValue(ageInTicks, 1, idleSpeed, 1.5F, 3F, true) * stillAmount;
        this.walk(jaw, idleSpeed, idleDegree * 0.15F, false, 2F, 0.2F, ageInTicks, stillAmount);
        this.flap(head, idleSpeed, idleDegree * 0.05F, false, 1F, 0F, ageInTicks, 1F);
        this.walk(dorsal, idleSpeed, idleDegree * 0.1F, false, 4F, -0.1F, ageInTicks, 1F);
        this.walk(dorsal2, idleSpeed, idleDegree * 0.1F, false, 3F, -0.1F, ageInTicks, 1F);
        this.walk(rbarb, idleSpeed, idleDegree * 0.2F, false, -1F, 0.2F, ageInTicks, 1F);
        this.walk(lbarb, idleSpeed, idleDegree * 0.2F, false, -1F, 0.2F, ageInTicks, 1F);
        this.walk(rbarb2, idleSpeed, idleDegree * 0.2F, false, -1F, 0.2F, ageInTicks, 1F);
        this.walk(lbarb2, idleSpeed, idleDegree * 0.2F, false, -1F, 0.2F, ageInTicks, 1F);
        this.swing(rbarb, idleSpeed, idleDegree * 0.2F, true, -1F, 0.2F, ageInTicks, stillAmount);
        this.swing(lbarb, idleSpeed, idleDegree * 0.2F, false, -1F, 0.2F, ageInTicks, stillAmount);
        this.walk(rflipper, idleSpeed * 2F, idleDegree * 0.4F, false, 0F, 0.0F, ageInTicks, stillAmount);
        this.swing(rflipper, idleSpeed * 2F, idleDegree * 0.8F, false, 2F, -0.25F, ageInTicks, stillAmount);
        this.flap(rflipper, idleSpeed * 2F, idleDegree * 0.5F, true, 0F, 0.2F, ageInTicks, stillAmount);
        this.walk(lflipper, idleSpeed * 2F, idleDegree * 0.4F, true, 2F, 0.0F, ageInTicks, stillAmount);
        this.swing(lflipper, idleSpeed * 2F, idleDegree * 0.8F, true, 4F, -0.25F, ageInTicks, stillAmount);
        this.flap(lflipper, idleSpeed * 2F, idleDegree * 0.5F, false, 2F, 0.2F, ageInTicks, stillAmount);

        this.body.rotationPointY -= ACMath.walkValue(limbSwingAmount, 1, swimSpeed, 1.5F, 6F, true);
        this.swing(body, swimSpeed, swimDegree * 0.05F, true, 1F, 0.0F, limbSwing, limbSwingAmount);
        this.flap(body, swimSpeed, swimDegree * 0.05F, true, -1F, 0.0F, limbSwing, limbSwingAmount);
        this.walk(head, swimSpeed, swimDegree * 0.1F, true, 2F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(jaw, swimSpeed, swimDegree * 0.1F, true, 3F, -0.1F, limbSwing, limbSwingAmount);
        this.walk(dorsal, swimSpeed, swimDegree * 0.2F, true, 2F, 0.4F, limbSwing, limbSwingAmount);
        this.walk(dorsal2, swimSpeed, swimDegree * 0.2F, true, 1F, 0.4F, limbSwing, limbSwingAmount);
        this.walk(rbarb, swimSpeed, swimDegree * 0.01F, false, -1F, 0.9F, limbSwing, limbSwingAmount);
        this.walk(lbarb, swimSpeed, swimDegree * 0.01F, false, -1F, 0.9F, limbSwing, limbSwingAmount);
        this.swing(rflipper, swimSpeed, swimDegree * 1.0F, false, -1F, 0.3F, limbSwing, limbSwingAmount);
        this.swing(lflipper, swimSpeed, swimDegree * 1.0F, false, -1F, -0.3F, limbSwing, limbSwingAmount);
        this.flap(rflipper, swimSpeed, swimDegree * 0.5F, true, -2.5F, 0.2F, limbSwing, limbSwingAmount);
        this.flap(lflipper, swimSpeed, swimDegree * 0.5F, false, -2.5F, 0.2F, limbSwing, limbSwingAmount);

        progressPositionPrev(root, landProgress, 0, 17, 0, 1F);
        progressPositionPrev(head, landProgress, 0, -5, 0, 1F);
        progressRotationPrev(head, landProgress, (float) Math.toRadians(-10), 0, (float) Math.toRadians(30), 1F);
        progressRotationPrev(body, landProgress, 0, 0, (float) Math.toRadians(-40), 1F);
        progressRotationPrev(body2, landProgress, 0, 0, (float) Math.toRadians(-30), 1F);
        progressRotationPrev(tail, landProgress, 0, (float) Math.toRadians(-10), (float) Math.toRadians(-20), 1F);
        progressRotationPrev(tail2, landProgress, 0, (float) Math.toRadians(5), 0, 1F);
        progressRotationPrev(rflipper, landProgress, 0, (float) Math.toRadians(40), 0, 1F);
        progressRotationPrev(lflipper, landProgress, 0, (float) Math.toRadians(-40), 0, 1F);
        progressRotationPrev(rbarb, landProgress, (float) Math.toRadians(20), 0, (float) Math.toRadians(-60), 1F);
        progressRotationPrev(lbarb, landProgress, 0, 0, (float) Math.toRadians(-40), 1F);

        this.body.rotateAngleX += fishPitchAmount * 0.75F;
        this.head.rotateAngleX += headPitchAmount;
        this.head.rotateAngleY += clampedYaw;
        if (!straighten) {
            body2.rotateAngleX += Math.toRadians(entity.tail1Part.calculateAnimationAngle(partialTicks, true));
            body2.rotateAngleY += Math.toRadians(entity.tail1Part.calculateAnimationAngle(partialTicks, false));
            tail.rotateAngleX += Math.toRadians(entity.tail2Part.calculateAnimationAngle(partialTicks, true));
            tail.rotateAngleY += Math.toRadians(entity.tail2Part.calculateAnimationAngle(partialTicks, false));
            float tail2XRot = (float) entity.tail3Part.calculateAnimationAngle(partialTicks, true);
            float tail3XRot = (float) entity.tail4Part.calculateAnimationAngle(partialTicks, true);
            tail2.rotateAngleX += Math.toRadians(tail2XRot);
            tail2.rotationPointZ -= 5 * Math.abs(tail2XRot / 20F);
            tail2.rotateAngleY += Math.toRadians(entity.tail3Part.calculateAnimationAngle(partialTicks, false));
            tail3.rotateAngleX += Math.toRadians(tail3XRot);
            tail3.rotationPointZ -= 5 * Math.abs(tail3XRot / 20F);
            tail3.rotateAngleY += Math.toRadians(entity.tail4Part.calculateAnimationAngle(partialTicks, false));
            lbarb.rotateAngleY += Math.toRadians(entity.getYawFromBuffer(2, partialTicks) - bodyYRot);
            rbarb.rotateAngleY += Math.toRadians(entity.getYawFromBuffer(2, partialTicks) - bodyYRot);
            lbarb2.rotateAngleY += Math.toRadians(entity.getYawFromBuffer(4, partialTicks) - bodyYRot);
            rbarb2.rotateAngleY += Math.toRadians(entity.getYawFromBuffer(4, partialTicks) - bodyYRot);
        }
    }
}