package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneMageEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;

public class DeepOneMageModel extends AdvancedEntityModel<DeepOneMageEntity> implements ArmedModel {
    private final AdvancedModelBox body;
    private final AdvancedModelBox eye;
    private final AdvancedModelBox smTentaclefront;
    private final AdvancedModelBox smTentacleleft;
    private final AdvancedModelBox smTentacleback;
    private final AdvancedModelBox smTentacleright;
    private final AdvancedModelBox bigTentaclerightFront;
    private final AdvancedModelBox bigTentaclebottom;
    private final AdvancedModelBox bigTentacleleftFront;
    private final AdvancedModelBox bigTentaclebottom2;
    private final AdvancedModelBox bigTentacleleftBack;
    private final AdvancedModelBox bigTentaclebottom3;
    private final AdvancedModelBox bigTentaclerightBack;
    private final AdvancedModelBox bigTentaclebottom4;
    private final AdvancedModelBox bigTentaclerightArm;
    private final AdvancedModelBox bTendrilRight;
    private final AdvancedModelBox bTendrilLeft;
    private final AdvancedModelBox fin;
    private final AdvancedModelBox bigTentacleleftArm;
    private final ModelAnimator animator;

    public DeepOneMageModel() {
        texWidth = 256;
        texHeight = 256;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 15.0F, 0.0F);
        body.setTextureOffset(0, 0).addBox(-10.5F, -19.0F, -5.5F, 21.0F, 15.0F, 21.0F, 0.0F, false);
        body.setTextureOffset(43, 45).addBox(-5.5F, -4.0F, -5.5F, 11.0F, 9.0F, 11.0F, 0.0F, false);
        body.setTextureOffset(0, 9).addBox(0.0F, -24.0F, -11.5F, 0.0F, 20.0F, 27.0F, 0.0F, false);

        eye = new AdvancedModelBox(this);
        eye.setRotationPoint(0.0F, -11.0F, 5.0F);
        body.addChild(eye);
        eye.setTextureOffset(105, 33).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.0F, false);

        smTentaclefront = new AdvancedModelBox(this);
        smTentaclefront.setRotationPoint(0.0F, 5.0F, -5.5F);
        body.addChild(smTentaclefront);
        smTentaclefront.setTextureOffset(26, 65).addBox(-5.5F, 0.0F, 0.0F, 11.0F, 10.0F, 0.0F, 0.0F, false);

        smTentacleleft = new AdvancedModelBox(this);
        smTentacleleft.setRotationPoint(5.5F, 5.0F, 0.0F);
        body.addChild(smTentacleleft);
        smTentacleleft.setTextureOffset(26, 54).addBox(0.0F, 0.0F, -5.5F, 0.0F, 10.0F, 11.0F, 0.0F, true);

        smTentacleback = new AdvancedModelBox(this);
        smTentacleback.setRotationPoint(0.0F, 5.0F, 5.5F);
        body.addChild(smTentacleback);
        smTentacleback.setTextureOffset(26, 65).addBox(-5.5F, 0.0F, 0.0F, 11.0F, 10.0F, 0.0F, 0.0F, false);

        smTentacleright = new AdvancedModelBox(this);
        smTentacleright.setRotationPoint(-5.5F, 5.0F, 0.0F);
        body.addChild(smTentacleright);
        smTentacleright.setTextureOffset(26, 54).addBox(0.0F, 0.0F, -5.5F, 0.0F, 10.0F, 11.0F, 0.0F, false);

        bigTentaclerightFront = new AdvancedModelBox(this);
        bigTentaclerightFront.setRotationPoint(-4.0F, 5.0F, -3.5F);
        body.addChild(bigTentaclerightFront);
        setRotateAngle(bigTentaclerightFront, 0.0F, 0.7854F, 0.0F);
        bigTentaclerightFront.setTextureOffset(10, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 11.0F, 0.0F, 0.0F, false);

        bigTentaclebottom = new AdvancedModelBox(this);
        bigTentaclebottom.setRotationPoint(0.0F, 11.0F, 0.0F);
        bigTentaclerightFront.addChild(bigTentaclebottom);
        bigTentaclebottom.setTextureOffset(0, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 11.0F, 0.0F, 0.0F, false);

        bigTentacleleftFront = new AdvancedModelBox(this);
        bigTentacleleftFront.setRotationPoint(4.0F, 5.0F, -3.5F);
        body.addChild(bigTentacleleftFront);
        setRotateAngle(bigTentacleleftFront, 0.0F, -0.7854F, 0.0F);
        bigTentacleleftFront.setTextureOffset(10, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 11.0F, 0.0F, 0.0F, true);

        bigTentaclebottom2 = new AdvancedModelBox(this);
        bigTentaclebottom2.setRotationPoint(0.0F, 11.0F, 0.0F);
        bigTentacleleftFront.addChild(bigTentaclebottom2);
        bigTentaclebottom2.setTextureOffset(0, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 11.0F, 0.0F, 0.0F, true);

        bigTentacleleftBack = new AdvancedModelBox(this);
        bigTentacleleftBack.setRotationPoint(4.0F, 5.0F, 4.0F);
        body.addChild(bigTentacleleftBack);
        setRotateAngle(bigTentacleleftBack, 0.0F, 0.7854F, 0.0F);
        bigTentacleleftBack.setTextureOffset(10, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 11.0F, 0.0F, 0.0F, true);

        bigTentaclebottom3 = new AdvancedModelBox(this);
        bigTentaclebottom3.setRotationPoint(0.0F, 11.0F, 0.0F);
        bigTentacleleftBack.addChild(bigTentaclebottom3);
        bigTentaclebottom3.setTextureOffset(0, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 11.0F, 0.0F, 0.0F, true);

        bigTentaclerightBack = new AdvancedModelBox(this);
        bigTentaclerightBack.setRotationPoint(-4.0F, 5.0F, 4.0F);
        body.addChild(bigTentaclerightBack);
        setRotateAngle(bigTentaclerightBack, 0.0F, -0.7854F, 0.0F);
        bigTentaclerightBack.setTextureOffset(10, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 11.0F, 0.0F, 0.0F, false);

        bigTentaclebottom4 = new AdvancedModelBox(this);
        bigTentaclebottom4.setRotationPoint(0.0F, 11.0F, 0.0F);
        bigTentaclerightBack.addChild(bigTentaclebottom4);
        bigTentaclebottom4.setTextureOffset(0, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 11.0F, 0.0F, 0.0F, false);

        bigTentaclerightArm = new AdvancedModelBox(this);
        bigTentaclerightArm.setRotationPoint(-2.5F, 3.0F, -0.5F);
        body.addChild(bigTentaclerightArm);
        bigTentaclerightArm.setTextureOffset(63, 0).addBox(-17.0F, -10.0F, 0.0F, 19.0F, 21.0F, 0.0F, 0.0F, false);

        bTendrilRight = new AdvancedModelBox(this);
        bTendrilRight.setRotationPoint(8.5F, -4.0F, 15.5F);
        body.addChild(bTendrilRight);
        bTendrilRight.setTextureOffset(48, 65).addBox(-4.0F, 0.0F, 0.0F, 6.0F, 27.0F, 0.0F, 0.0F, false);

        bTendrilLeft = new AdvancedModelBox(this);
        bTendrilLeft.setRotationPoint(-8.5F, -4.0F, 15.5F);
        body.addChild(bTendrilLeft);
        bTendrilLeft.setTextureOffset(48, 65).addBox(-2.0F, 0.0F, 0.0F, 6.0F, 27.0F, 0.0F, 0.0F, true);

        fin = new AdvancedModelBox(this);
        fin.setRotationPoint(0.0F, -21.5F, 15.5F);
        body.addChild(fin);
        fin.setTextureOffset(0, 43).addBox(0.0F, -2.5F, 0.0F, 0.0F, 20.0F, 13.0F, 0.0F, false);

        bigTentacleleftArm = new AdvancedModelBox(this);
        bigTentacleleftArm.setRotationPoint(2.5F, 3.0F, -0.5F);
        body.addChild(bigTentacleleftArm);
        bigTentacleleftArm.setTextureOffset(63, 0).addBox(-2.0F, -10.0F, 0.0F, 19.0F, 21.0F, 0.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, eye, smTentacleback, smTentaclefront, smTentacleleft, smTentacleright, bigTentaclebottom, bigTentaclebottom2, bigTentaclebottom3, bigTentaclebottom4, bigTentaclerightFront, bigTentaclerightBack, bigTentacleleftBack, bigTentacleleftFront, bigTentaclerightArm, bigTentacleleftArm, fin, bTendrilLeft, bTendrilRight);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(DeepOneMageEntity.ANIMATION_DISAPPEAR);
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float) Math.toRadians(5), (float) Math.toRadians(-15));
        animator.rotate(bigTentacleleftArm, 0, (float) Math.toRadians(15), (float) Math.toRadians(-25));
        animator.rotate(bigTentaclerightArm, 0, (float) Math.toRadians(-35), (float) Math.toRadians(75));
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.rotate(body, 0, (float) Math.toRadians(-5), (float) Math.toRadians(10));
        animator.rotate(bigTentacleleftArm, 0, (float) Math.toRadians(35), (float) Math.toRadians(-75));
        animator.rotate(bigTentaclerightArm, 0, (float) Math.toRadians(-15), (float) Math.toRadians(25));
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.rotate(body, 0, (float) Math.toRadians(5), (float) Math.toRadians(-15));
        animator.rotate(bigTentacleleftArm, 0, (float) Math.toRadians(15), (float) Math.toRadians(-25));
        animator.rotate(bigTentaclerightArm, 0, (float) Math.toRadians(-35), (float) Math.toRadians(75));
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.rotate(body, 0, (float) Math.toRadians(-5), (float) Math.toRadians(10));
        animator.rotate(bigTentacleleftArm, 0, (float) Math.toRadians(35), (float) Math.toRadians(-75));
        animator.rotate(bigTentaclerightArm, 0, (float) Math.toRadians(-15), (float) Math.toRadians(25));
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.rotate(body, (float) Math.toRadians(15), 0, 0);
        animator.rotate(bigTentacleleftArm, 0, (float) Math.toRadians(75), (float) Math.toRadians(-55));
        animator.rotate(bigTentaclerightArm, 0, (float) Math.toRadians(-75), (float) Math.toRadians(55));
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(DeepOneMageEntity.ANIMATION_ATTACK);
        animator.startKeyframe(10);
        animator.move(body, 0, -3, 0);
        animator.rotate(body, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(smTentacleback, (float) Math.toRadians(10), 0, 0);
        animator.rotate(smTentaclefront, (float) Math.toRadians(10), 0, 0);
        animator.rotate(smTentacleleft, (float) Math.toRadians(10), 0, 0);
        animator.rotate(smTentacleright, (float) Math.toRadians(10), 0, 0);
        animator.rotate(bTendrilLeft, (float) Math.toRadians(10), 0, 0);
        animator.rotate(bTendrilRight, (float) Math.toRadians(10), 0, 0);
        animator.rotate(bigTentacleleftArm, (float) Math.toRadians(-10), (float) Math.toRadians(-10), (float) Math.toRadians(-85));
        animator.rotate(bigTentaclerightArm, (float) Math.toRadians(-10), (float) Math.toRadians(10), (float) Math.toRadians(85));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(15), 0, 0);
        animator.rotate(bigTentacleleftArm, 0, (float) Math.toRadians(90), (float) Math.toRadians(-55));
        animator.rotate(bigTentaclerightArm, 0, (float) Math.toRadians(-90), (float) Math.toRadians(55));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(8);
        animator.setAnimation(DeepOneMageEntity.ANIMATION_SPIN);
        animator.startKeyframe(10);
        animator.rotate(body, (float) Math.toRadians(-5), (float) Math.toRadians(-20), 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float) Math.toRadians(70), 0);
        animatePose(0);
        animator.endKeyframe();
        animator.startKeyframe(50);
        animator.rotate(body, 0, (float) Math.toRadians(70 + 9 * 180), 0);
        animatePose(0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float) Math.toRadians(1780), 0);
        animator.endKeyframe();
        animator.setAnimation(DeepOneMageEntity.ANIMATION_TRADE);
        animator.startKeyframe(10);
        animator.rotate(body, (float) Math.toRadians(15), 0, 0);
        animatePose(1);
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.rotate(body, (float) Math.toRadians(5), 0, (float) Math.toRadians(5));
        animatePose(1);
        animator.endKeyframe();
        animator.startKeyframe(20);
        animator.rotate(body, (float) Math.toRadians(5), 0, (float) Math.toRadians(-5));
        animatePose(1);
        animator.endKeyframe();
        animator.startKeyframe(20);
        animator.rotate(body, (float) Math.toRadians(5), 0, (float) Math.toRadians(5));
        animatePose(1);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(-6), 0, 0);
        animator.rotate(bigTentacleleftArm, 0, (float) Math.toRadians(90), (float) Math.toRadians(-55));
        animator.rotate(bigTentaclerightArm, 0, (float) Math.toRadians(-90), (float) Math.toRadians(55));
        animator.endKeyframe();
        animator.resetKeyframe(10);
    }

    private void animatePose(int pose) {
        if (pose == 0) {
            animator.rotate(smTentacleback, (float) Math.toRadians(80), 0, 0);
            animator.rotate(smTentaclefront, (float) Math.toRadians(-80), 0, 0);
            animator.rotate(smTentacleleft, 0, 0, (float) Math.toRadians(-80));
            animator.rotate(smTentacleright, 0, 0, (float) Math.toRadians(80));
            animator.rotate(bigTentaclerightFront, (float) Math.toRadians(-80), 0, 0);
            animator.rotate(bigTentacleleftFront, (float) Math.toRadians(-80), 0, 0);
            animator.rotate(bigTentaclerightBack, (float) Math.toRadians(80), 0, 0);
            animator.rotate(bigTentacleleftBack, (float) Math.toRadians(80), 0, 0);
            animator.rotate(bTendrilRight, (float) Math.toRadians(30), 0, 0);
            animator.rotate(bTendrilLeft, (float) Math.toRadians(30), 0, 0);
            animator.rotate(bigTentacleleftArm, 0, 0, (float) Math.toRadians(15));
            animator.rotate(bigTentaclerightArm, 0, 0, (float) Math.toRadians(-15));
        } else if (pose == 1) {
            animator.move(bigTentacleleftArm, 0, 0, -3);
            animator.move(bigTentaclerightArm, 0, 0, -3);
            animator.rotate(bigTentacleleftArm, (float) Math.toRadians(50), (float) Math.toRadians(40), (float) Math.toRadians(65));
            animator.rotate(bigTentaclerightArm, (float) Math.toRadians(50), (float) Math.toRadians(-40), (float) Math.toRadians(-65));
        }
    }

    @Override
    public void setupAnim(DeepOneMageEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        limbSwing = ageInTicks;
        float walkSpeed = 0.5F;
        float walkDegree = 0.3F;
        float partialTicks = ageInTicks - entity.tickCount;
        float yBodyRot = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks;
        float bodyIdleBob = ACMath.walkValue(ageInTicks, 1F, 0.1F, 0F, 1F, false);
        float bodyWalkBob = ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 1F, 2F, false);
        float handsDownAmount = 1 - limbSwingAmount;
        float landProgress = 1 - entity.getSwimAmount(partialTicks);
        float fishPitchAmount = entity.getFishPitch(partialTicks) / 57.295776F;
        float clampedYaw = netHeadYaw / 57.295776F;
        float headPitchAmount = headPitch / 57.295776F;
        this.body.rotationPointY += bodyIdleBob + bodyWalkBob - landProgress * 16;
        this.eye.rotationPointY -= bodyIdleBob + bodyWalkBob;
        progressRotationPrev(body, limbSwingAmount, (float) Math.toRadians(20 + 20 * entity.getSwimAmount(partialTicks)), 0, 0, 1F);
        progressPositionPrev(bigTentacleleftArm, handsDownAmount, 2, 1, 0, 1F);
        progressRotationPrev(bigTentacleleftArm, handsDownAmount, 0, 0, (float) Math.toRadians(45), 1F);
        progressPositionPrev(bigTentaclerightArm, handsDownAmount, -2, 1, 0, 1F);
        progressRotationPrev(bigTentaclerightArm, handsDownAmount, 0, 0, (float) Math.toRadians(-45), 1F);
        if (entity.getAnimation() != DeepOneMageEntity.ANIMATION_TRADE) {
            this.flap(bigTentaclerightArm, 0.1F, 0.15F, false, 1F, -0.1F, ageInTicks, 1);
            this.swing(bigTentaclerightArm, 0.1F, 0.15F, false, 1F, -0.1F, ageInTicks, 1);
            this.flap(bigTentacleleftArm, 0.1F, 0.15F, true, 2F, 0F, ageInTicks, 1);
            this.swing(bigTentacleleftArm, 0.1F, 0.15F, false, 2F, 0F, ageInTicks, 1);
        }
        this.walk(body, 0.1F, 0.02F, false, 1F, 0F, ageInTicks, 1);
        this.walk(bTendrilLeft, 0.1F, 0.2F, false, 0F, 0.1F, ageInTicks, 1);
        this.walk(bTendrilRight, 0.1F, 0.2F, false, 0F, 0.1F, ageInTicks, 1);
        this.walk(smTentacleback, 0.1F, 0.1F, false, -2F, 0.1F, ageInTicks, 1);
        this.walk(smTentaclefront, 0.1F, 0.1F, true, -2F, 0.1F, ageInTicks, 1);
        this.flap(smTentacleleft, 0.1F, 0.1F, true, -2F, 0.1F, ageInTicks, 1);
        this.flap(smTentacleright, 0.1F, 0.1F, false, -2F, 0.1F, ageInTicks, 1);
        this.walk(bigTentacleleftFront, 0.1F, 0.15F, true, -3F, 0.1F, ageInTicks, 1);
        this.walk(bigTentaclebottom2, 0.1F, 0.1F, true, -4F, 0.1F, ageInTicks, 1);
        this.walk(bigTentaclerightFront, 0.1F, 0.1F, true, -3F, 0.1F, ageInTicks, 1);
        this.walk(bigTentaclebottom, 0.1F, 0.1F, true, -4F, 0.1F, ageInTicks, 1);
        this.walk(bigTentacleleftBack, 0.1F, 0.15F, false, -3F, 0.1F, ageInTicks, 1);
        this.walk(bigTentaclebottom4, 0.1F, 0.1F, false, -4F, 0.1F, ageInTicks, 1);
        this.walk(bigTentaclerightBack, 0.1F, 0.1F, false, -3F, 0.1F, ageInTicks, 1);
        this.walk(bigTentaclebottom3, 0.1F, 0.1F, false, -4F, 0.1F, ageInTicks, 1);

        this.walk(bigTentacleleftFront, walkSpeed, walkDegree, true, -3F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(bigTentaclebottom2, walkSpeed, walkDegree * 0.5F, true, -4F, 0F, limbSwing, limbSwingAmount);
        this.walk(bigTentaclerightFront, walkSpeed, walkDegree, true, -3F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(bigTentaclebottom, walkSpeed, walkDegree * 0.5F, true, -4F, 0F, limbSwing, limbSwingAmount);
        this.walk(bigTentacleleftBack, walkSpeed, walkDegree, false, -3F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(bigTentaclebottom4, walkSpeed, walkDegree * 0.5F, false, -4F, 0F, limbSwing, limbSwingAmount);
        this.walk(bigTentaclerightBack, walkSpeed, walkDegree, false, -3F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(bigTentaclebottom3, walkSpeed, walkDegree * 0.5F, false, -4F, 0F, limbSwing, limbSwingAmount);
        this.walk(smTentacleback, walkSpeed, walkDegree, false, -2F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(smTentaclefront, walkSpeed, walkDegree, true, -2F, 0.1F, limbSwing, limbSwingAmount);
        this.flap(smTentacleleft, walkSpeed, walkDegree, true, -2F, 0.1F, limbSwing, limbSwingAmount);
        this.flap(smTentacleright, walkSpeed, walkDegree, false, -2F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(bTendrilLeft, walkSpeed, walkDegree * 0.3F, true, 3F, -0.4F, limbSwing, limbSwingAmount);
        this.walk(bTendrilRight, walkSpeed, walkDegree * 0.3F, false, 3F, 0.4F, limbSwing, limbSwingAmount);
        this.swing(bigTentaclerightArm, walkSpeed, walkDegree * 0.3F, true, 5, -0.1F, limbSwing, limbSwingAmount);
        this.swing(bigTentacleleftArm, walkSpeed, walkDegree * 0.3F, false, 5, 0.1F, limbSwing, limbSwingAmount);

        this.body.rotateAngleX += fishPitchAmount;
        this.body.rotateAngleX += headPitchAmount;
        this.body.rotateAngleY += clampedYaw;
        Entity look = Minecraft.getInstance().getCameraEntity();
        if (look != null) {
            Vec3 cameraPosition = look.getEyePosition(partialTicks);
            Vec3 eyePosition = entity.getEyePosition(partialTicks);
            double d0 = eyePosition.x - cameraPosition.x;
            double d1 = eyePosition.y - cameraPosition.y;
            double d2 = eyePosition.z - cameraPosition.z;
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            this.eye.rotateAngleX = (float) Math.toRadians(Mth.wrapDegrees((float) ((Mth.atan2(d1, d3) * 180.0F / (float) Math.PI)))) + body.rotateAngleX;
            this.eye.rotateAngleY -= Math.toRadians(yBodyRot) + body.rotateAngleY;
            this.eye.rotateAngleY -= Math.toRadians(-(float) (Mth.atan2(d2, d0) * 57.2957763671875D) - 90);
            this.eye.rotateAngleZ -= body.rotateAngleZ;
        }

    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        body.translateAndRotate(poseStack);
        if (humanoidArm == HumanoidArm.RIGHT) {
            bigTentaclerightArm.translateAndRotate(poseStack);
            poseStack.translate(-1.0F, -0.6F, 0.1);
        } else {
            bigTentacleleftArm.translateAndRotate(poseStack);
            poseStack.translate(0.4F, -0.6F, -0.1);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
    }
}