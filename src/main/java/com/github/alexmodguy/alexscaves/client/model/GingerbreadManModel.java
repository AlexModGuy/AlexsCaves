package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.GingerbreadManEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.world.entity.HumanoidArm;

public class GingerbreadManModel extends AdvancedEntityModel<GingerbreadManEntity> implements ArmedModel {

    private final AdvancedModelBox main;
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox left_Leg;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox right_Leg;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox right_Arm;
    private final AdvancedModelBox left_Arm;
    private ModelAnimator animator;

    public GingerbreadManModel() {
        texWidth = 32;
        texHeight = 32;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -6.5F, -1.0F);
        main.addChild(body);
        body.setTextureOffset(14, 14).addBox(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 2.0F, 0.26F, false);
        body.setTextureOffset(0, 16).addBox(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 2.0F, 0.01F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -2.5F, 0.0F);
        body.addChild(head);
        head.setTextureOffset(0, 8).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 2.0F, 0.02F, false);
        head.setTextureOffset(0, 23).addBox(3.0F, -5.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.02F, false);
        head.setTextureOffset(0, 23).addBox(-5.0F, -5.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.02F, true);
        head.setTextureOffset(13, 28).addBox(-5.0F, -5.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.02F, true);
        head.setTextureOffset(13, 28).addBox(3.0F, -5.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.02F, false);
        head.setTextureOffset(0, 0).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 2.0F, 0.27F, false);

        left_Leg = new AdvancedModelBox(this);
        left_Leg.setRotationPoint(2.0F, 1.0F, 0.0F);
        body.addChild(left_Leg);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(-1.0F, -1.0F, 0.0F);
        left_Leg.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.0F, -0.3927F);
        cube_r1.setTextureOffset(20, 21).addBox(-1.5F, 1.5F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);
        cube_r1.setTextureOffset(12, 21).addBox(-1.5F, 1.5F, -1.0F, 2.0F, 5.0F, 2.0F, 0.25F, false);

        right_Leg = new AdvancedModelBox(this);
        right_Leg.setRotationPoint(-2.0F, 1.0F, 0.0F);
        body.addChild(right_Leg);


        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(1.0F, -1.0F, 0.0F);
        right_Leg.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 0.0F, 0.3927F);
        cube_r2.setTextureOffset(20, 21).addBox(-0.5F, 1.5F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, true);
        cube_r2.setTextureOffset(12, 21).addBox(-0.5F, 1.5F, -1.0F, 2.0F, 5.0F, 2.0F, 0.25F, true);

        right_Arm = new AdvancedModelBox(this);
        right_Arm.setRotationPoint(-2.75F, -1.5F, 0.0F);
        body.addChild(right_Arm);
        right_Arm.setTextureOffset(14, 6).addBox(-3.75F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 0.25F, true);
        right_Arm.setTextureOffset(16, 0).addBox(-3.75F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 0.0F, true);

        left_Arm = new AdvancedModelBox(this);
        left_Arm.setRotationPoint(2.75F, -1.5F, 0.0F);
        body.addChild(left_Arm);
        left_Arm.setTextureOffset(14, 6).addBox(-0.25F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 0.25F, false);
        left_Arm.setTextureOffset(16, 0).addBox(-0.25F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, body, head, left_Arm, left_Leg, cube_r1, cube_r2, right_Arm, right_Leg);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(GingerbreadManEntity.ANIMATION_IDLE_WAVE_LEFT);
        animator.startKeyframe(10);
        animator.rotate(body, 0, (float) Math.toRadians(-20), 0);
        animator.rotate(head, (float) Math.toRadians(-20), (float) Math.toRadians(-40), 0);
        animator.rotate(right_Arm,  0,0, (float) Math.toRadians(-40));
        animator.endKeyframe();
        animator.startKeyframe(20);
        animator.rotate(body, 0, (float) Math.toRadians(20), 0);
        animator.rotate(head, (float) Math.toRadians(-20), (float) Math.toRadians(40), 0);
        animator.rotate(right_Arm,  0,0, (float) Math.toRadians(-40));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(GingerbreadManEntity.ANIMATION_IDLE_WAVE_RIGHT);
        animator.startKeyframe(10);
        animator.rotate(body, 0, (float) Math.toRadians(20), 0);
        animator.rotate(head, (float) Math.toRadians(-20), (float) Math.toRadians(40), 0);
        animator.rotate(left_Arm,  0,0, (float) Math.toRadians(40));
        animator.endKeyframe();
        animator.startKeyframe(20);
        animator.rotate(body, 0, (float) Math.toRadians(-20), 0);
        animator.rotate(head, (float) Math.toRadians(-20), (float) Math.toRadians(-40), 0);
        animator.rotate(left_Arm,  0,0, (float) Math.toRadians(40));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(GingerbreadManEntity.ANIMATION_IDLE_FALL_OVER);
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(-90), 0, 0);
        animator.rotate(left_Arm, 0, (float) Math.toRadians(30), (float) Math.toRadians(-30));
        animator.rotate(right_Arm, 0, (float) Math.toRadians(-30), (float) Math.toRadians(30));
        animator.move(body, 0, 5, 7);
        animator.endKeyframe();
        animator.setStaticKeyframe(25);
        animator.resetKeyframe(20);
        animator.setAnimation(GingerbreadManEntity.ANIMATION_IDLE_JUMP);
        animator.startKeyframe(5);
        animator.move(body, 0, 1, 0);
        animator.rotate(body, (float) Math.toRadians(10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(20), 0, 0);
        animator.rotate(right_Arm,  0,0, (float) Math.toRadians(-40));
        animator.rotate(left_Arm,  0,0, (float) Math.toRadians(40));
        animator.rotate(right_Leg,  0,0, (float) Math.toRadians(20));
        animator.rotate(left_Leg,  0,0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, -1, 0);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(right_Arm,  0,0, (float) Math.toRadians(40));
        animator.rotate(left_Arm,  0,0, (float) Math.toRadians(40));
        animator.rotate(right_Leg,  (float) Math.toRadians(-20),0, (float) Math.toRadians(-20));
        animator.rotate(left_Leg,  (float) Math.toRadians(20),0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);
        animator.setAnimation(GingerbreadManEntity.ANIMATION_SWING_RIGHT);
        animator.startKeyframe(5);
        animator.rotate(head, 0, (float) Math.toRadians(-20), 0);
        animator.rotate(body, 0, (float) Math.toRadians(30), 0);
        animator.rotate(left_Leg, 0, 0, (float) Math.toRadians(15));
        animator.rotate(right_Leg, 0, 0, (float) Math.toRadians(-15));
        animator.rotate(left_Arm, 0, 0,  (float) Math.toRadians(35));
        animator.rotate(right_Arm, (float) Math.toRadians(-25), (float) Math.toRadians(15),  (float) Math.toRadians(15));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(head, 0, (float) Math.toRadians(20), 0);
        animator.rotate(body, 0, (float) Math.toRadians(-30), 0);
        animator.rotate(left_Arm, 0, 0,  (float) Math.toRadians(35));
        animator.rotate(right_Arm, (float) Math.toRadians(35), (float) Math.toRadians(15),  (float) Math.toRadians(-45));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(5);
        animator.setAnimation(GingerbreadManEntity.ANIMATION_SWING_LEFT);
        animator.startKeyframe(5);
        animator.rotate(head, 0, (float) Math.toRadians(20), 0);
        animator.rotate(body, 0, (float) Math.toRadians(-30), 0);
        animator.rotate(left_Leg, 0, 0, (float) Math.toRadians(15));
        animator.rotate(right_Leg, 0, 0, (float) Math.toRadians(-15));
        animator.rotate(right_Arm, 0, 0,  (float) Math.toRadians(-35));
        animator.rotate(left_Arm, (float) Math.toRadians(-25), (float) Math.toRadians(-15),  (float) Math.toRadians(-15));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(head, 0, (float) Math.toRadians(-20), 0);
        animator.rotate(body, 0, (float) Math.toRadians(30), 0);
        animator.rotate(right_Arm, 0, 0,  (float) Math.toRadians(-35));
        animator.rotate(left_Arm, (float) Math.toRadians(35), (float) Math.toRadians(-15),  (float) Math.toRadians(45));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(5);
    }

    @Override
    public void setupAnim(GingerbreadManEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        this.left_Arm.showModel = !entity.hasLostLimb(true, true);
        this.right_Arm.showModel = !entity.hasLostLimb(false, true);
        this.left_Leg.showModel = !entity.hasLostLimb(true, false);
        this.right_Leg.showModel = !entity.hasLostLimb(false, false);
        float partialTicks = ageInTicks - entity.tickCount;
        float walkSpeed = 0.6F;
        float walkDegree = 1F;
        float sitProgress = entity.getSitProgress(partialTicks);
        float danceProgress = entity.getDanceProgress(partialTicks);
        float carryItemProgress = entity.getCarryItemProgress(partialTicks);
        float invCarryItemProgress = 1F - carryItemProgress;
        this.head.rotateAngleY += netHeadYaw / 57.295776F;
        this.head.rotateAngleX += headPitch / 57.295776F;
        if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            setupAnimForAnimation(entity, entity.getAnimation(), limbSwing, limbSwingAmount, ageInTicks);
        }
        this.flap(right_Arm, 0.1F, 0.1F, true, -1F, 0.0F, ageInTicks, 1F);
        this.flap(left_Arm, 0.1F, 0.1F, false, -1F, 0.0F, ageInTicks, 1F);
        progressRotationPrev(left_Leg, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(10), 0, 1F);
        progressRotationPrev(right_Leg, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-10), 0, 1F);
        if(!entity.hasBothLegs()){
            progressRotationPrev(left_Leg, 1F, 0,0,  (float) Math.toRadians(20), 1F);
            progressRotationPrev(right_Leg, 1F, 0,0,  (float) Math.toRadians(-20), 1F);

        }
        progressPositionPrev(left_Leg, sitProgress, 0, 1F, 1F, 1F);
        progressPositionPrev(right_Leg, sitProgress, 0, 1F, 1F, 1F);
        progressPositionPrev(body, sitProgress, 0, 4F, 0F, 1F);
        progressRotationPrev(head, carryItemProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(right_Arm, carryItemProgress, 0, (float) Math.toRadians(-100), 0, 1F);
        progressRotationPrev(left_Arm, carryItemProgress, 0, (float) Math.toRadians(100), 0, 1F);
        this.swing(body, walkSpeed, walkDegree * 0.2F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
        this.swing(head, walkSpeed, walkDegree * 0.4F, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
        this.walk(head, walkSpeed, walkDegree * 0.4F, true, 1.0F, 0.2F, limbSwing, limbSwingAmount);
        this.walk(left_Leg, walkSpeed, walkDegree, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
        this.flap(left_Leg, walkSpeed, walkDegree * 0.3F, true, 0.0F, -0.15F, limbSwing, limbSwingAmount);
        this.walk(right_Leg, walkSpeed, walkDegree, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
        this.flap(right_Leg, walkSpeed, walkDegree * 0.3F, false, 0.0F, -0.15F, limbSwing, limbSwingAmount);
        this.swing(left_Arm, walkSpeed, walkDegree, false, 1.0F, 0.0F, limbSwing, limbSwingAmount * invCarryItemProgress);
        this.swing(right_Arm, walkSpeed, walkDegree, false, 1.0F, 0.0F, limbSwing, limbSwingAmount * invCarryItemProgress);
        this.flap(left_Arm, walkSpeed, walkDegree, false, 1.0F, 0.0F, limbSwing, limbSwingAmount * invCarryItemProgress);
        this.flap(right_Arm, walkSpeed, walkDegree, false, 1.0F, 0.0F, limbSwing, limbSwingAmount * invCarryItemProgress);
        body.rotationPointY -= Math.abs((float) (Math.cos(limbSwing * walkSpeed - 0.5F) * walkDegree * 4F * limbSwingAmount));
        this.flap(right_Arm, 1.0F, 0.7F, false, -1F, 0.3F, ageInTicks, danceProgress);
        this.flap(left_Arm, 1.0F, 0.7F, true, -1F, 0.3F, ageInTicks, danceProgress);
        this.walk(head, 1.0F, 0.5F, true, -2F, 0.0F, ageInTicks, danceProgress);
        this.swing(head, 1.0F, 0.5F, true, -1F, 0.0F, ageInTicks, danceProgress);
        this.swing(body, 1.0F, 0.15F, true, -3F, 0.0F, ageInTicks, danceProgress);
    }

    private void setupAnimForAnimation(GingerbreadManEntity entity, Animation animation, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float partialTick = ageInTicks - entity.tickCount;
        if (animation == GingerbreadManEntity.ANIMATION_IDLE_WAVE_LEFT) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, animation, partialTick, 0, 30);
            this.flap(left_Arm, 1.25F, 0.5F, false, 1.0F, -0.5F, ageInTicks, animationIntensity);
        }
        if (animation == GingerbreadManEntity.ANIMATION_IDLE_WAVE_RIGHT) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, animation, partialTick, 0, 30);
            this.flap(right_Arm, 1.25F, 0.5F, false, 1.0F, 0.5F, ageInTicks, animationIntensity);
        }
        if (animation == GingerbreadManEntity.ANIMATION_IDLE_FALL_OVER) {
            float animationIntensity1 = ACMath.cullAnimationTick(entity.getAnimationTick(), 2, animation, partialTick, 5, 25);
            this.swing(left_Arm, 1.25F, 0.5F, false, 1.0F, -0.5F, ageInTicks, animationIntensity1);
            this.swing(right_Arm, 1.25F, 0.5F, false, 1.0F, 0.5F, ageInTicks, animationIntensity1);
            float animationIntensity2 = ACMath.cullAnimationTick(entity.getAnimationTick(), 4, animation, partialTick, 20, 30);
            this.walk(left_Leg, 0.7F, 0.6F, false, 1.0F, 0F, ageInTicks, animationIntensity2);
            this.walk(right_Leg, 0.7F, 0.6F, true, 1.0F, 0F, ageInTicks, animationIntensity2);
        }
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        main.translateAndRotate(poseStack);
        body.translateAndRotate(poseStack);
        if(humanoidArm == HumanoidArm.LEFT){
            left_Arm.translateAndRotate(poseStack);
        }else{
            right_Arm.translateAndRotate(poseStack);
        }
    }
}
