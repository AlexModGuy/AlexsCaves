package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.GummyBearEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.LicowitchEntity;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.IArmPoseTransformer;

public class LicowitchModel extends AdvancedEntityModel<LicowitchEntity> implements ArmedModel {
    
    private final AdvancedModelBox main;
    private final AdvancedModelBox body;
    private final AdvancedModelBox dress;
    private final AdvancedModelBox head;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox hat;
    private final AdvancedModelBox hat2;
    private final AdvancedModelBox hat3;
    private final AdvancedModelBox hat4;
    private final AdvancedModelBox arms;
    private final AdvancedModelBox armsCrossed;
    private final AdvancedModelBox left_Arm;
    private final AdvancedModelBox left_Hand;
    private final AdvancedModelBox right_Arm;
    private final AdvancedModelBox right_Hand;
    private final AdvancedModelBox right_Leg;
    private final AdvancedModelBox left_Leg;
    private final ModelAnimator animator;

    public LicowitchModel() {
        texWidth = 64;
        texHeight = 128;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -14.0F, 0.75F);
        main.addChild(body);
        body.setTextureOffset(16, 20).addBox(-4.0F, -10.0F, -3.75F, 8.0F, 12.0F, 6.0F, 0.0F, false);
        body.setTextureOffset(0, 38).addBox(-4.0F, -10.0F, -3.75F, 8.0F, 18.0F, 6.0F, 0.5F, false);

        dress = new AdvancedModelBox(this);
        dress.setRotationPoint(0.0F, -1.0F, -2.5F);
        body.addChild(dress);
        dress.setTextureOffset(9, 105).addBox(-8.0F, 0.0F, -1.5F, 16.0F, 12.0F, 11.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -10.0F, -0.75F);
        body.addChild(head);
        head.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);
        head.setTextureOffset(30, 61).addBox(-5.0F, -4.0F, -1.0F, 10.0F, 6.0F, 7.0F, 0.0F, false);

        nose = new AdvancedModelBox(this);
        nose.setRotationPoint(0.0F, -3.0F, -4.0F);
        head.addChild(nose);
        nose.setTextureOffset(24, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);
        nose.setTextureOffset(0, 0).addBox(0.0F, 2.0F, -2.75F, 1.0F, 1.0F, 1.0F, -0.25F, false);
        nose.setTextureOffset(0, 0).addBox(-1.25F, -0.25F, -2.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        hat = new AdvancedModelBox(this);
        hat.setRotationPoint(0.0F, -7.05F, 0.0F);
        head.addChild(hat);
        hat.setTextureOffset(0, 75).addBox(-8.0F, -3.0F, -8.0F, 16.0F, 2.0F, 16.0F, 0.0F, false);

        hat2 = new AdvancedModelBox(this);
        hat2.setRotationPoint(0.3571F, -3.0609F, 0.7114F);
        hat.addChild(hat2);
        setRotateAngle(hat2, -0.0524F, 0.0F, 0.0262F);
        hat2.setTextureOffset(0, 63).addBox(-3.5524F, -4.4966F, -3.6046F, 7.0F, 5.0F, 7.0F, 0.0F, false);
        hat2.setTextureOffset(0, 93).addBox(-3.5524F, -4.4966F, -3.6046F, 7.0F, 5.0F, 7.0F, 0.25F, false);

        hat3 = new AdvancedModelBox(this);
        hat3.setRotationPoint(0.4651F, -4.1322F, 0.9235F);
        hat2.addChild(hat3);
        setRotateAngle(hat3, -0.1047F, 0.0F, 0.0524F);
        hat3.setTextureOffset(0, 75).addBox(-2.1047F, -4.4863F, -2.2088F, 4.0F, 5.0F, 4.0F, 0.0F, false);

        hat4 = new AdvancedModelBox(this);
        hat4.setRotationPoint(0.724F, -4.5446F, 1.4457F);
        hat3.addChild(hat4);
        setRotateAngle(hat4, -0.2094F, 0.0F, 0.1047F);
        hat4.setTextureOffset(0, 84).addBox(-0.6045F, -2.4728F, -0.7068F, 1.0F, 3.0F, 1.0F, 0.25F, false);

        arms = new AdvancedModelBox(this);
        arms.setRotationPoint(0.0F, -8.0F, -0.75F);
        body.addChild(arms);
        setRotateAngle(arms, -0.7854F, 0.0F, 0.0F);

        armsCrossed = new AdvancedModelBox(this);
        armsCrossed.setRotationPoint(0.0F, 4.0F, 0.0F);
        arms.addChild(armsCrossed);
        armsCrossed.setTextureOffset(40, 38).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F, false);

        left_Arm = new AdvancedModelBox(this);
        left_Arm.setRotationPoint(4.0F, 0.0F, 0.0F);
        arms.addChild(left_Arm);
        left_Arm.setTextureOffset(44, 22).addBox(0.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
        left_Arm.setTextureOffset(28, 42).addBox(0.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.5F, false);

        left_Hand = new AdvancedModelBox(this);
        left_Hand.setRotationPoint(2.0F, 8.0F, 0.0F);
        left_Arm.addChild(left_Hand);
        left_Hand.setTextureOffset(32, 10).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        right_Arm = new AdvancedModelBox(this);
        right_Arm.setRotationPoint(-4.0F, 0.0F, 0.0F);
        arms.addChild(right_Arm);
        right_Arm.setTextureOffset(44, 22).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, true);
        right_Arm.setTextureOffset(28, 42).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.5F, true);

        right_Hand = new AdvancedModelBox(this);
        right_Hand.setRotationPoint(-2.0F, 8.0F, 0.0F);
        right_Arm.addChild(right_Hand);
        right_Hand.setTextureOffset(32, 10).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, true);

        right_Leg = new AdvancedModelBox(this);
        right_Leg.setRotationPoint(-2.0F, 2.0F, -0.75F);
        body.addChild(right_Leg);
        right_Leg.setTextureOffset(48, 75).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, true);
        right_Leg.setTextureOffset(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        left_Leg = new AdvancedModelBox(this);
        left_Leg.setRotationPoint(2.0F, 2.0F, -0.75F);
        body.addChild(left_Leg);
        left_Leg.setTextureOffset(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        left_Leg.setTextureOffset(48, 75).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);
        this.updateDefaultPose();
        this.animator = ModelAnimator.create();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, body, left_Leg, right_Leg, dress, head, nose, hat, hat2, hat3, hat4, arms, armsCrossed, left_Arm, right_Arm, left_Hand, right_Hand);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    public void animate(LicowitchEntity entity) {
        boolean left = entity.getMainArm() == HumanoidArm.LEFT;
        animator.update(entity);
        animator.setAnimation(LicowitchEntity.ANIMATION_SWING_RIGHT);
        animator.startKeyframe(2);
        animator.rotate(right_Arm, (float) Math.toRadians(25), (float) Math.toRadians(-15), 0);
        animator.rotate(body, 0, (float) Math.toRadians(10), 0);
        animator.rotate(head, 0, (float) Math.toRadians(-10), 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(right_Arm, (float) Math.toRadians(-90), (float) Math.toRadians(15), (float) Math.toRadians(-25));
        animator.rotate(body, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(head, 0, (float) Math.toRadians(10), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(1);
        animator.resetKeyframe(3);
        animator.setAnimation(LicowitchEntity.ANIMATION_SWING_LEFT);
        animator.startKeyframe(2);
        animator.rotate(left_Arm, (float) Math.toRadians(25), (float) Math.toRadians(15), 0);
        animator.rotate(body, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(head, 0, (float) Math.toRadians(10), 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(left_Arm, (float) Math.toRadians(-90), (float) Math.toRadians(-15), (float) Math.toRadians(25));
        animator.rotate(body, 0, (float) Math.toRadians(10), 0);
        animator.rotate(head, 0, (float) Math.toRadians(-10), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(1);
        animator.resetKeyframe(3);
        animator.setAnimation(LicowitchEntity.ANIMATION_EAT);
        animator.startKeyframe(10);
        animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(nose, (float) Math.toRadians(-55), 0, 0);
        animator.move(nose, 0, 0, 2);
        animator.move(arms, 0, 2, -1);
        animator.endKeyframe();
        animator.setStaticKeyframe(80);
        animator.resetKeyframe(10);
        animator.setAnimation(LicowitchEntity.ANIMATION_SPELL_0);
        animator.startKeyframe(10);
        animator.rotate(head, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(right_Arm, (float) Math.toRadians(-20), (float) Math.toRadians(40), (float) Math.toRadians(40));
        animator.rotate(left_Arm, (float) Math.toRadians(-20), (float) Math.toRadians(-40), (float) Math.toRadians(-40));
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(right_Arm, (float) Math.toRadians(-90), (float) Math.toRadians(-10), (float) Math.toRadians(-35));
        animator.rotate(left_Arm, (float) Math.toRadians(-90), (float) Math.toRadians(10), (float) Math.toRadians(35));
        animator.endKeyframe();
        animator.startKeyframe(6);
        animator.rotate(body, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(right_Leg, (float) Math.toRadians(25), 0, 0);
        animator.rotate(left_Leg, (float) Math.toRadians(25), 0, 0);
        animator.rotate(dress, (float) Math.toRadians(25), 0, 0);
        animator.move(body, 0, 1, 1);
        animator.move(dress, 0, 0, -2);
        animator.rotate(right_Arm, (float) Math.toRadians(-50), (float) Math.toRadians(-10), (float) Math.toRadians(-35));
        animator.rotate(left_Arm, (float) Math.toRadians(-50), (float) Math.toRadians(10), (float) Math.toRadians(35));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(25), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-35), 0, 0);
        animator.rotate(right_Leg, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(left_Leg, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(dress, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(getArmCube(entity), (float) Math.toRadians(-100), (float) Math.toRadians(left ? -10 : 10), (float) Math.toRadians(left ? 35 : -35));
        animator.move(getArmCube(entity), 0, 0, -2);
        animator.endKeyframe();
        animator.setStaticKeyframe(10);
        animator.resetKeyframe(10);
        animator.setAnimation(LicowitchEntity.ANIMATION_SPELL_1);
        animator.startKeyframe(20);
        animator.rotate(head, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(right_Arm, (float) Math.toRadians(-20), (float) Math.toRadians(40), (float) Math.toRadians(40));
        animator.rotate(left_Arm, (float) Math.toRadians(-20), (float) Math.toRadians(-40), (float) Math.toRadians(-40));
        animator.move(body, 0, -2, 1);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(65), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-75), 0, 0);
        animator.rotate(right_Leg, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(left_Leg, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(dress, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(getArmCube(entity), (float) Math.toRadians(-115), (float) Math.toRadians(left ? -10 : 10), (float) Math.toRadians(left ? 35 : -35));
        animator.move(getArmCube(entity), 0, 0, -2);
        animator.move(body, 0, -2, 1);
        animator.endKeyframe();
        animator.setStaticKeyframe(10);
        animator.resetKeyframe(10);
    }

    @Override
    public void setupAnim(LicowitchEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float partialTicks = ageInTicks - entity.tickCount;
        float unfurlArmsProgress = entity.getArmsUncrossedProgress(partialTicks);
        float teleportingProgress = Math.min(entity.getTeleportingProgress(partialTicks) * 5, 1F) * unfurlArmsProgress;
        boolean crossedArms = entity.areArmsVisuallyCrossed(partialTicks);
        float walkSpeed = 0.5F;
        float walkDegree = 0.8F;
        progressPositionPrev(dress, limbSwingAmount, 0, -0.5F, -3, 1F);
        progressRotationPrev(dress, limbSwingAmount, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(left_Arm, unfurlArmsProgress, (float) Math.toRadians(40), (float) Math.toRadians(20), (float) Math.toRadians(-20), 1F);
        progressRotationPrev(right_Arm, unfurlArmsProgress, (float) Math.toRadians(40), (float) Math.toRadians(-20), (float) Math.toRadians(20), 1F);
        progressPositionPrev(left_Arm, teleportingProgress, 0, -2, 0, 1F);
        progressPositionPrev(right_Arm, teleportingProgress, 0, -2, 0, 1F);
        progressRotationPrev(head, teleportingProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(left_Arm, teleportingProgress, (float) Math.toRadians(-160), (float) Math.toRadians(-40), (float) Math.toRadians(40), 1F);
        progressRotationPrev(right_Arm, teleportingProgress, (float) Math.toRadians(-160), (float) Math.toRadians(40), (float) Math.toRadians(-40), 1F);
        this.flap(nose, 0.035F, 0.08F, false, -1F, 0.0F, ageInTicks, 1F);
        this.walk(nose, 0.035F, 0.08F, false, 0F, -0.08F, ageInTicks, 1F);
        this.flap(left_Arm, 0.1F, 0.1F, false, 0F, -0.15F, ageInTicks, unfurlArmsProgress);
        this.flap(right_Arm, 0.1F, 0.1F, true, 0F, -0.15F, ageInTicks, unfurlArmsProgress);

        this.armsCrossed.showModel = crossedArms;
        this.left_Hand.showModel = !crossedArms;
        this.right_Hand.showModel = !crossedArms;
        this.head.rotateAngleY += netHeadYaw * ((float)Math.PI / 180F);
        this.head.rotateAngleX += headPitch * ((float)Math.PI / 180F);

        this.walk(left_Leg, walkSpeed, walkDegree, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_Leg, walkSpeed, walkDegree, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_Arm, walkSpeed, walkDegree, true, 0F, 0.3F, limbSwing, limbSwingAmount * unfurlArmsProgress);
        this.walk(right_Arm, walkSpeed, walkDegree, false, 0F, -0.3F, limbSwing, limbSwingAmount * unfurlArmsProgress);
        this.swing(dress, walkSpeed, walkDegree * 0.1F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.swing(body, walkSpeed, walkDegree * 0.05F, true, 2F, 0F, limbSwing, limbSwingAmount);
        this.swing(head, walkSpeed, walkDegree * 0.05F, false, 1F, 0F, limbSwing, limbSwingAmount);

        this.walk(left_Arm, 0.5F, 0.5F, true, 0F, 0.0F, ageInTicks, teleportingProgress);
        this.walk(right_Arm, 0.5F, 0.5F, false, 0F, 0.0F, ageInTicks, teleportingProgress);
        this.body.rotationPointY += teleportingProgress * (-5 - ACMath.walkValue(ageInTicks, 1F, 0.25F, 0F, 4, false));

        float f = -Math.min(left_Leg.rotateAngleX, right_Leg.rotateAngleX);
        this.dress.rotationPointZ -= f * 1.5F;
        this.dress.setScale(1F, 1F, 1F + limbSwingAmount * 0.33F);
        float runWalkBob = -Math.abs(ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 1F, 1F, false));
        this.body.rotationPointY += runWalkBob;
        this.left_Leg.rotationPointY -= runWalkBob;
        this.right_Leg.rotationPointY -= runWalkBob;
        this.dress.rotationPointY -= runWalkBob;
        if (entity.getAnimation() == LicowitchEntity.ANIMATION_EAT) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 4, LicowitchEntity.ANIMATION_EAT, partialTicks, 0);
            this.walk(head, 0.75F, 0.1F, false, 0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(arms, 0.75F, 0.3F, true, 1F, 0.2F, ageInTicks, animationIntensity);
        }
        if (entity.getAnimation() == LicowitchEntity.ANIMATION_SPELL_0) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 2, LicowitchEntity.ANIMATION_SPELL_0, partialTicks, 18, 25);
            AdvancedModelBox arm = getArmCube(entity);
            arm.rotationPointZ += animationIntensity * Math.sin(ageInTicks * 2F);
            this.walk(arm, 1F, 0.1F, false, 0F, -0.1F, ageInTicks, animationIntensity);
        }
        if (entity.getAnimation() == LicowitchEntity.ANIMATION_SPELL_1) {
            float animationIntensity1 = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, LicowitchEntity.ANIMATION_SPELL_1, partialTicks, 0, 40);
            float animationIntensity2 = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, LicowitchEntity.ANIMATION_SPELL_1, partialTicks, 25, 25);
            this.walk(left_Leg, 0.3F, 0.2F, false, 0F, 0.0F, ageInTicks, animationIntensity1);
            this.walk(right_Leg, 0.3F, 0.2F, true, 0F, 0.0F, ageInTicks, animationIntensity1);
            this.walk(body, 0.2F, 0.1F, true, 1F, 0.0F, ageInTicks, animationIntensity1);
            this.bob(body, 0.4F, 2, false, ageInTicks, animationIntensity1);
            AdvancedModelBox arm = getArmCube(entity);
            arm.rotationPointZ += animationIntensity2 * Math.sin(ageInTicks * 2F);
            this.walk(arm, 1F, 0.1F, false, 0F, -0.1F, ageInTicks, animationIntensity2);
        }
    }


    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.main.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        this.arms.translateAndRotate(poseStack);
        if(humanoidArm == HumanoidArm.RIGHT){
            this.right_Arm.translateAndRotate(poseStack);
            this.right_Hand.translateAndRotate(poseStack);
        }else{
            this.left_Arm.translateAndRotate(poseStack);
            this.left_Hand.translateAndRotate(poseStack);
        }
    }

    public void translateToCrossedArms(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.main.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        this.arms.translateAndRotate(poseStack);
        this.armsCrossed.translateAndRotate(poseStack);
    }

    private AdvancedModelBox getArmCube(LivingEntity living){
        return living.getMainArm() == HumanoidArm.LEFT ? left_Arm : right_Arm;
    }
}
