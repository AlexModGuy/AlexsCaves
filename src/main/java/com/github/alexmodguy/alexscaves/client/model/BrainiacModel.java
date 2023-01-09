package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.BrainiacEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class BrainiacModel extends AdvancedEntityModel<BrainiacEntity> {
    private final AdvancedModelBox torso;
    private final AdvancedModelBox chest;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox handMaw;
    private final AdvancedModelBox handMaw3;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox head;
    private final AdvancedModelBox brain;
    private final AdvancedModelBox tongue;
    private final AdvancedModelBox tongue2;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox rleg;
    private final ModelAnimator animator;

    public BrainiacModel() {
        texWidth = 128;
        texHeight = 128;

        torso = new AdvancedModelBox(this);
        torso.setRotationPoint(0.0F, 3.0F, 0.0F);
        torso.setTextureOffset(0, 68).addBox(-5.5F, -4.0F, -4.0F, 11.0F, 14.0F, 8.0F, 0.0F, false);

        chest = new AdvancedModelBox(this);
        chest.setRotationPoint(0.0F, 0.0F, 4.0F);
        torso.addChild(chest);
        chest.setTextureOffset(0, 42).addBox(-8.5F, -14.0F, -11.0F, 17.0F, 14.0F, 12.0F, 0.0F, false);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(8.1667F, -8.1667F, -5.0833F);
        chest.addChild(larm);
        larm.setTextureOffset(0, 0).addBox(0.3333F, -1.8333F, -4.1667F, 8.0F, 23.0F, 9.0F, 0.0F, false);
        larm.setTextureOffset(70, 79).addBox(1.3333F, -2.8333F, -6.1667F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        larm.setTextureOffset(38, 79).addBox(1.3333F, 9.1667F, -2.1667F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        handMaw = new AdvancedModelBox(this);
        handMaw.setRotationPoint(8.3333F, 21.1667F, 0.3333F);
        larm.addChild(handMaw);
        handMaw.setTextureOffset(0, 90).addBox(-4.0F, 0.0F, -4.5F, 4.0F, 8.0F, 9.0F, 0.0F, false);

        handMaw3 = new AdvancedModelBox(this);
        handMaw3.setRotationPoint(0.3333F, 21.1667F, 0.3333F);
        larm.addChild(handMaw3);
        handMaw3.setTextureOffset(92, 0).addBox(0.0F, 0.0F, -4.5F, 4.0F, 8.0F, 9.0F, 0.0F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-7.25F, -12.25F, -6.0F);
        chest.addChild(rarm);
        rarm.setTextureOffset(56, 0).addBox(-8.25F, -3.75F, -3.25F, 9.0F, 37.0F, 9.0F, 0.0F, false);
        rarm.setTextureOffset(84, 38).addBox(-10.25F, 15.25F, -5.25F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -7.0F, -11.25F);
        chest.addChild(head);
        head.setTextureOffset(100, 54).addBox(-3.5F, -3.0F, -6.25F, 7.0F, 10.0F, 7.0F, 0.0F, false);

        brain = new AdvancedModelBox(this);
        brain.setRotationPoint(0.0F, 28.0F, 7.25F);
        head.addChild(brain);
        brain.setTextureOffset(46, 56).addBox(-6.5F, -41.0F, -15.0F, 13.0F, 11.0F, 12.0F, 0.0F, false);

        tongue = new AdvancedModelBox(this);
        tongue.setRotationPoint(0.0F, 3.5F, -1.75F);
        head.addChild(tongue);
        tongue.setTextureOffset(21, 0).addBox(-3.5F, 0.0F, -21.0F, 7.0F, 0.0F, 21.0F, 0.0F, false);

        tongue2 = new AdvancedModelBox(this);
        tongue2.setRotationPoint(0.0F, 0.0F, -21.0F);
        tongue.addChild(tongue2);
        tongue2.setTextureOffset(21, 20).addBox(-3.5F, 0.0F, -21.0F, 7.0F, 0.0F, 21.0F, 0.0F, false);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(3.5F, 9.5F, 0.0F);
        torso.addChild(lleg);
        lleg.setTextureOffset(26, 91).addBox(-2.0F, 0.5F, -2.0F, 4.0F, 11.0F, 4.0F, 0.0F, true);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-3.5F, 9.5F, 0.0F);
        torso.addChild(rleg);
        rleg.setTextureOffset(26, 91).addBox(-2.0F, 0.5F, -2.0F, 4.0F, 11.0F, 4.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(BrainiacEntity.ANIMATION_THROW_BARREL);
        animator.startKeyframe(10);
        animator.rotate(torso, 0, (float) Math.toRadians(20), 0);
        animator.rotate(larm, (float) Math.toRadians(-270), (float) Math.toRadians(-10), (float) Math.toRadians(-10));
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(20));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(40));
        animator.move(handMaw3, 0, -4, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animator.rotate(larm, (float) Math.toRadians(-80), 0, 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-90));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(90));
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(BrainiacEntity.ANIMATION_DRINK_BARREL);
        animator.startKeyframe(10);
        animator.rotate(torso, 0, (float) Math.toRadians(20), 0);
        animator.rotate(larm, (float) Math.toRadians(-270), (float) Math.toRadians(-10), (float) Math.toRadians(-10));
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(20));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(40));
        animator.move(handMaw3, 0, -4, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(10);
        animator.move(larm, 1, 6, 10);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(-80), (float) Math.toRadians(20), 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-90));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(90));
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.move(larm, 1, 5, 10);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(-70), (float) Math.toRadians(20), 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-90));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(90));
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.move(larm, 1, 6, 10);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(-80), (float) Math.toRadians(20), 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-90));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(90));
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.move(larm, 1, 5, 10);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(-70), (float) Math.toRadians(20), 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-90));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(90));
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.move(larm, 1, 6, 10);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(-80), (float) Math.toRadians(20), 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-90));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(90));
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.move(larm, 1, 5, 10);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(-70), (float) Math.toRadians(20), 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-90));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(90));
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.move(larm, 1, 6, 10);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(-80), (float) Math.toRadians(20), 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-90));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(90));
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(10);
        animator.setAnimation(BrainiacEntity.ANIMATION_BITE);
        animator.startKeyframe(10);
        animator.rotate(torso, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(chest, 0, (float) Math.toRadians(-10), 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(torso, 0, (float) Math.toRadians(10), 0);
        animator.rotate(chest, 0, (float) Math.toRadians(10), 0);
        animator.rotate(larm, (float) Math.toRadians(-80), (float) Math.toRadians(-20), 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-90));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(90));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.rotate(torso, 0, (float) Math.toRadians(10), 0);
        animator.rotate(chest, 0, (float) Math.toRadians(10), 0);
        animator.rotate(larm, (float) Math.toRadians(-70), (float) Math.toRadians(-20), 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(BrainiacEntity.ANIMATION_SMASH);
        animator.startKeyframe(10);
        animator.rotate(chest, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(-130), (float) Math.toRadians(20), 0);
        animator.rotate(larm, (float) Math.toRadians(-130), (float) Math.toRadians(-20), 0);
        animator.rotate(handMaw, 0, 0, (float) Math.toRadians(-20));
        animator.rotate(handMaw3, 0, 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(chest, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(-20), (float) Math.toRadians(-20), 0);
        animator.rotate(larm, (float) Math.toRadians(-20), (float) Math.toRadians(20), 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(torso);
    }

    public void translateToArmOrChest(PoseStack poseStack, boolean arm) {
        this.torso.translateAndRotate(poseStack);
        this.chest.translateAndRotate(poseStack);
        if (arm) {
            this.larm.translateAndRotate(poseStack);
        }
    }

    @Override
    public void setupAnim(BrainiacEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float walkSpeed = 0.5F;
        float walkDegree = 1F;
        float partialTick = ageInTicks - entity.tickCount;
        float tongueLaunch = entity.getShootTongueAmount(partialTick);
        float tongueLength = entity.getLastTongueDistance(partialTick) * tongueLaunch;
        float tongueWiggle = (float) Math.sin(tongueLaunch * Math.PI);
        Entity tongueTarget = entity.getTongueTarget();
        if (tongueTarget != null && tongueLaunch > 0) {
            Vec3 vector3d = tongueTarget.getEyePosition(partialTick);
            Vec3 vector3d1 = entity.getEyePosition(partialTick);
            double d0 = Mth.clamp((vector3d.y - vector3d1.y) * 0.1F, -1F, 1F) * Math.PI / 2F;
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = (new Vec3(vector3d.x - vector3d1.x, 0.0D, vector3d.z - vector3d1.z)).normalize().yRot(((float) Math.PI / 2F));
            double d1 = vector3d2.dot(vector3d3);
            this.tongue.rotateAngleX -= d0 * tongueLaunch;
            this.tongue.rotateAngleY += d1 * tongueLaunch;
        }
        if (tongueLength <= 0F) {
            this.tongue.showModel = false;
        } else {
            this.tongue.showModel = true;
        }
        float hunchAmount = limbSwingAmount * (1F - entity.getRaiseArmsAmount(partialTick));
        float hunchLeftAmount = hunchAmount * (1F - entity.getRaiseLeftArmAmount(partialTick));
        float zoomedHunch = Math.min(1F, hunchAmount * 3F);
        progressRotationPrev(chest, zoomedHunch, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(larm, zoomedHunch, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(rarm, zoomedHunch, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(head, zoomedHunch, (float) Math.toRadians(-20), 0, 0, 1F);
        progressPositionPrev(rarm, zoomedHunch, 0, -3F, 0, 1F);
        progressPositionPrev(larm, zoomedHunch, 0, -2.5F, 0, 1F);
        progressPositionPrev(head, zoomedHunch, 0, -2F, 0, 1F);
        this.tongue.setScale(1F, 1F, tongueLength);
        this.tongue.scaleChildren = true;
        this.brain.setScale((1F + (float) Math.sin(ageInTicks * 0.5F + 2F)) * 0.1F + 1F, 1F, (1F + (float) Math.sin(ageInTicks * 0.5F)) * 0.1F + 1F);
        this.flap(head, 0.15F, 0.1F, false, 2F, 0F, ageInTicks, 1);
        this.flap(head, 0.55F, 0.1F, false, 2F, 0F, ageInTicks, 0.5F + 0.5F * (float) Math.sin(ageInTicks * 0.5F));
        this.walk(tongue, 0.4F, 0.1F, false, 2F, 0F, ageInTicks, 1);
        this.walk(tongue2, 0.4F, 0.2F, false, 0F, 0F, ageInTicks, 1);
        this.swing(tongue, 1.5F, 0.4F, false, 0F, 0F, ageInTicks, tongueWiggle);
        this.swing(tongue2, 1.5F, 0.4F, false, -1F, 0F, ageInTicks, tongueWiggle);
        this.tongue.rotateAngleZ -= head.rotateAngleZ;
        float bodyBob = walkValue(limbSwing, limbSwingAmount, walkSpeed * 1.5F, 0.5F, 1F, true);
        this.torso.rotationPointY += bodyBob;
        this.flap(chest, walkSpeed, walkDegree * 0.2F, false, 2.5F, 0F, limbSwing, limbSwingAmount);
        this.flap(head, walkSpeed, walkDegree * -0.1F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.flap(torso, walkSpeed, walkDegree * 0.1F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.walk(rleg, walkSpeed, walkDegree, true, 0F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(lleg, walkSpeed, walkDegree, false, 0F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(rarm, walkSpeed, walkDegree * 1.3F, false, 1F, 0F, limbSwing, hunchAmount);
        this.walk(larm, walkSpeed, walkDegree * 1.3F, false, -1F, 0F, limbSwing, hunchLeftAmount);
        this.walk(torso, walkSpeed, walkDegree * 0.1F, false, -1F, 0F, limbSwing, hunchLeftAmount);
        rarm.rotationPointY += Math.min(0, walkValue(limbSwing, hunchAmount, walkSpeed, -0.75F, 1, false)) - bodyBob;
        rarm.rotationPointZ += walkValue(limbSwing, hunchAmount, walkSpeed, -0.75F, -3, true);
        larm.rotationPointY += Math.min(0, walkValue(limbSwing, hunchLeftAmount, walkSpeed, 0.75F, 1, false)) - bodyBob;
        larm.rotationPointZ += walkValue(limbSwing, hunchLeftAmount, walkSpeed, 0.75F, -3, true);
        rarm.rotateAngleZ -= torso.rotateAngleZ;
        larm.rotateAngleZ -= torso.rotateAngleZ;
        float yawAmount = netHeadYaw / 57.295776F;
        float pitchAmount = headPitch / 57.295776F;
        this.head.rotateAngleX += pitchAmount * 0.25F;
        this.head.rotateAngleY += yawAmount * 0.5F;

    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(torso, chest, lleg, rleg, rarm, larm, handMaw, handMaw3, head, brain, tongue, tongue2);
    }

    private float walkValue(float limbSwing, float limbSwingAmount, float speed, float offset, float degree, boolean inverse) {
        return (float) ((Math.cos(limbSwing * speed + offset) * degree * limbSwingAmount) * (inverse ? -1 : 1));
    }
}
