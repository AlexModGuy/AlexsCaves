package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GammaroachEntity;
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

public class DeepOneModel extends AdvancedEntityModel<DeepOneEntity> implements ArmedModel {
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox head;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox headFins;
    private final ModelAnimator animator;

    public DeepOneModel() {
        texWidth = 128;
        texHeight = 128;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.1667F, 2.1667F, 1.5F);
        body.setTextureOffset(0, 12).addBox(0.3333F, -15.1667F, -2.5F, 0.0F, 19.0F, 11.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-6.6667F, -10.1667F, -6.5F, 13.0F, 14.0F, 9.0F, 0.0F, false);
        body.setTextureOffset(45, 15).addBox(-3.6667F, 3.8333F, -3.5F, 7.0F, 10.0F, 5.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(-0.1667F, 12.3333F, 1.5F);
        body.addChild(tail);
        tail.setTextureOffset(22, 29).addBox(0.0F, -5.5F, 0.0F, 0.0F, 12.0F, 12.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(-0.1667F, -10.1667F, -3.5F);
        body.addChild(head);
        head.setTextureOffset(70, 9).addBox(-3.5F, -6.0F, -8.0F, 7.0F, 5.0F, 6.0F, 0.0F, false);
        head.setTextureOffset(22, 23).addBox(-3.5F, -1.0F, -8.0F, 7.0F, 9.0F, 9.0F, 0.0F, false);
        head.setTextureOffset(18, 53).addBox(-3.5F, -6.0F, -2.0F, 7.0F, 5.0F, 3.0F, 0.0F, false);
        head.setTextureOffset(0, 0).addBox(0.5F, -3.0F, -4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(0, 0).addBox(-2.5F, -3.0F, -4.0F, 2.0F, 2.0F, 2.0F, 0.0F, true);

        headFins = new AdvancedModelBox(this);
        headFins.setRotationPoint(-3.0F, -1.0F, -2.0F);
        head.addChild(headFins);
        setRotateAngle(headFins, -0.7854F, 0.0F, 0.0F);
        headFins.setTextureOffset(69, 20).addBox(-5.5F, -4.0F, 0.0F, 5.0F, 10.0F, 0.0F, 0.0F, true);
        headFins.setTextureOffset(69, 20).addBox(6.5F, -4.0F, 0.0F, 5.0F, 10.0F, 0.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 3.0F, -0.5F);
        head.addChild(jaw);
        jaw.setTextureOffset(44, 0).addBox(-3.5F, -1.0F, -7.5F, 7.0F, 6.0F, 9.0F, 0.25F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-6.1667F, -4.1667F, -0.75F);
        body.addChild(rarm);
        rarm.setTextureOffset(0, 42).addBox(-3.5F, -2.0F, -3.75F, 4.0F, 19.0F, 5.0F, 0.0F, true);
        rarm.setTextureOffset(54, 30).addBox(-3.5F, 17.0F, -3.75F, 4.0F, 4.0F, 5.0F, 0.0F, true);
        rarm.setTextureOffset(0, 66).addBox(-3.5F, -2.0F, -3.75F, 4.0F, 19.0F, 5.0F, 0.25F, true);
        rarm.setTextureOffset(46, 47).addBox(-1.5F, -2.0F, 1.25F, 0.0F, 19.0F, 5.0F, 0.0F, true);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(5.8333F, -4.1667F, -2.25F);
        body.addChild(larm);
        larm.setTextureOffset(0, 42).addBox(-0.5F, -2.0F, -2.25F, 4.0F, 19.0F, 5.0F, 0.0F, false);
        larm.setTextureOffset(0, 66).addBox(-0.5F, -2.0F, -2.25F, 4.0F, 19.0F, 5.0F, 0.25F, false);
        larm.setTextureOffset(54, 30).addBox(-0.5F, 17.0F, -2.25F, 4.0F, 4.0F, 5.0F, 0.0F, false);
        larm.setTextureOffset(46, 47).addBox(1.5F, -2.0F, 2.75F, 0.0F, 19.0F, 5.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-2.1667F, 13.3333F, -1.5F);
        body.addChild(rleg);
        rleg.setTextureOffset(56, 52).addBox(-1.5F, 0.5F, -1.0F, 3.0F, 8.0F, 3.0F, 0.0F, true);
        rleg.setTextureOffset(65, 43).addBox(-1.5F, 0.5F, -1.0F, 3.0F, 8.0F, 3.0F, 0.25F, true);
        rleg.setTextureOffset(29, 0).addBox(-4.5F, 8.5F, -4.0F, 6.0F, 0.0F, 6.0F, 0.0F, true);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(1.8333F, 13.3333F, -1.5F);
        body.addChild(lleg);
        lleg.setTextureOffset(56, 52).addBox(-1.5F, 0.5F, -1.0F, 3.0F, 8.0F, 3.0F, 0.0F, false);
        lleg.setTextureOffset(65, 43).addBox(-1.5F, 0.5F, -1.0F, 3.0F, 8.0F, 3.0F, 0.25F, false);
        lleg.setTextureOffset(29, 0).addBox(-1.5F, 8.5F, -4.0F, 6.0F, 0.0F, 6.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(DeepOneEntity.ANIMATION_THROW);
        animator.startKeyframe(10);
        animator.rotate(jaw, (float) Math.toRadians(45), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(15), 0);
        animator.rotate(head, (float) Math.toRadians(-15), (float) Math.toRadians(-15), 0);
        animator.rotate(rarm, (float) Math.toRadians(-65), (float) Math.toRadians(110), (float) Math.toRadians(65));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(jaw, (float) Math.toRadians(35), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(-30), 0);
        animator.rotate(head, (float) Math.toRadians(-15), (float) Math.toRadians(15), 0);
        animator.rotate(rarm, (float) Math.toRadians(-75), (float) Math.toRadians(10), (float) Math.toRadians(65));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(DeepOneEntity.ANIMATION_SCRATCH);
        animator.startKeyframe(5);
        animator.rotate(jaw, (float) Math.toRadians(35), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(10), 0);
        animator.rotate(rarm, (float) Math.toRadians(-75), (float) Math.toRadians(30), (float) Math.toRadians(65));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(jaw, (float) Math.toRadians(35), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(rarm, (float) Math.toRadians(-75), (float) Math.toRadians(-30), (float) Math.toRadians(25));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(jaw, (float) Math.toRadians(35), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(larm, (float) Math.toRadians(-75), (float) Math.toRadians(-30), (float) Math.toRadians(-65));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(jaw, (float) Math.toRadians(35), 0, 0);
        animator.rotate(body, 0, (float) Math.toRadians(10), 0);
        animator.rotate(larm, (float) Math.toRadians(-75), (float) Math.toRadians(30), (float) Math.toRadians(-25));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(DeepOneEntity.ANIMATION_BITE);
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(10), 0, 0);
        animator.rotate(rleg, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(lleg, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(30), 0, (float) Math.toRadians(10));
        animator.rotate(larm, (float) Math.toRadians(30), 0, (float) Math.toRadians(-10));
        animator.rotate(head, (float) Math.toRadians(-30), 0, 0);
        animator.move(head, 0, -1, -3);
        animator.move(body, 0, 1, -1);
        animator.move(rleg, 0, -1.5F, -1);
        animator.move(lleg, 0, -1.5F, -1);
        animator.rotate(jaw, (float) Math.toRadians(65), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(3);
        animator.setAnimation(DeepOneEntity.ANIMATION_TRADE);
        animator.startKeyframe(10);
        animator.rotate(head, (float) Math.toRadians(25), 0, (float) Math.toRadians(-10));
        animator.rotate(rarm, (float) Math.toRadians(-25), (float) Math.toRadians(-15), (float) Math.toRadians(-10));
        animator.rotate(larm, (float) Math.toRadians(-25), (float) Math.toRadians(15), (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.rotate(head, (float) Math.toRadians(15), 0, (float) Math.toRadians(10));
        animator.rotate(rarm, (float) Math.toRadians(-25), (float) Math.toRadians(-15), (float) Math.toRadians(-10));
        animator.rotate(larm, (float) Math.toRadians(-25), (float) Math.toRadians(15), (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.startKeyframe(20);
        animator.rotate(head, (float) Math.toRadians(15), 0, (float) Math.toRadians(-10));
        animator.rotate(rarm, (float) Math.toRadians(-25), (float) Math.toRadians(-15), (float) Math.toRadians(-10));
        animator.rotate(larm, (float) Math.toRadians(-25), (float) Math.toRadians(15), (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(larm, (float) Math.toRadians(-100), (float) Math.toRadians(25), (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.resetKeyframe(10);
    }

    @Override
    public void setupAnim(DeepOneEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
        this.walk(head, 0.1F, 0.02F, false, 0F, 0.02F, ageInTicks, 1F);
        this.walk(tail, 0.1F, 0.05F, false, -1F, 0.05F, ageInTicks, 1F);
        this.walk(larm, 0.1F, 0.05F, true, -1F, 0.1F, ageInTicks, 1F);
        this.walk(rarm, 0.1F, 0.05F, false, -1F, -0.1F, ageInTicks, 1F);
        progressRotationPrev(body, walkAmount, (float) Math.toRadians(15), 0, 0, 1F);
        progressRotationPrev(head, walkAmount, (float) Math.toRadians(-15), 0, 0, 1F);
        progressRotationPrev(lleg, walkAmount, (float) Math.toRadians(-15), 0, 0, 1F);
        progressRotationPrev(rleg, walkAmount, (float) Math.toRadians(-15), 0, 0, 1F);
        progressRotationPrev(body, swim, (float) Math.toRadians(80), 0, 0, 1F);
        progressRotationPrev(head, swim, (float) Math.toRadians(-70), 0, 0, 1F);
        progressRotationPrev(tail, swim, (float) Math.toRadians(-50), 0, 0, 1F);
        progressPositionPrev(body, swim, 0,8, 0, 1F);
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
        lleg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -2.5F, 5, true)) - walkAmount * 1;
        rleg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -2.5F, 5, false)) - walkAmount * 1;
        this.walk(lleg, walkSpeed, walkDegree, false, -1, 0F, limbSwing, walkAmount);
        this.walk(rleg, walkSpeed, walkDegree, true, -1, 0F, limbSwing, walkAmount);
        this.walk(rarm, walkSpeed, walkDegree * 0.2F, true, -3, 0.4F, limbSwing, walkAmount);
        this.walk(larm, walkSpeed, walkDegree * 0.2F, false, -3, -0.4F, limbSwing, walkAmount);
        this.swing(tail, walkSpeed, walkDegree * 0.2F, false, -1, -0F, limbSwing, walkAmount);

        this.flap(body, swimSpeed, swimDegree * 1F, true, 0F, 0F, limbSwing, swimAmount);
        this.swing(head, swimSpeed, swimDegree * 1, false, 0.5F, 0F, limbSwing, swimAmount);

        this.flap(larm, swimSpeed, swimDegree * 2.75F, true, -0.5F, 1.5F, limbSwing, swimAmount);
        this.swing(larm, swimSpeed, swimDegree, true, -1.5F, 0, limbSwing, swimAmount);
        this.walk(larm, swimSpeed, swimDegree, true, -2F, -0.2F, limbSwing, swimAmount);
        this.flap(rarm, swimSpeed, swimDegree * 2.75F, false, -3F, 1.5F, limbSwing, swimAmount);
        this.swing(rarm, swimSpeed, swimDegree, false, -1.5F, 0, limbSwing, swimAmount);
        this.walk(rarm, swimSpeed, swimDegree, false, -4.5F, -0.2F, limbSwing, swimAmount);
        this.flap(tail, swimSpeed, swimDegree * 0.75F, false, -2, 0F, limbSwing, swimAmount);

        this.walk(rleg, swimSpeed * 1.5F, swimDegree * 1F, true, 2F, 0.0F, limbSwing, swimAmount);
        this.walk(lleg, swimSpeed * 1.5F, swimDegree * 1F, false, 2F, 0.0F, limbSwing, swimAmount);
        this.body.rotateAngleX += fishPitchAmount;
        this.head.rotateAngleX += headPitchAmount;
        this.head.rotateAngleY += clampedYaw;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, tail, head, jaw, rarm, larm, rleg, lleg, headFins);
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        body.translateAndRotate(poseStack);
        if(arm == HumanoidArm.RIGHT){
            rarm.translateAndRotate(poseStack);
        }else{
            larm.translateAndRotate(poseStack);
        }
        poseStack.translate(0, 0.65F, 0.1F);
    }
}