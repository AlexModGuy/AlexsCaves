package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class UnderzealotModel extends AdvancedEntityModel<UnderzealotEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox head;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox lleg;
    private final ModelAnimator animator;
    public boolean noBurrowing;

    public UnderzealotModel() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -8.0F, 1.0F);
        root.addChild(body);
        body.setTextureOffset(0, 37).addBox(-6.5F, -7.0F, -5.0F, 13.0F, 11.0F, 10.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-6.5F, -7.0F, -5.0F, 13.0F, 14.0F, 10.0F, 0.25F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-6.25F, -6.0F, -0.5F);
        body.addChild(rarm);
        rarm.setTextureOffset(34, 58).addBox(-2.75F, -1.0F, -2.5F, 2.0F, 9.0F, 5.0F, 0.0F, true);
        rarm.setTextureOffset(59, 62).addBox(-2.75F, 8.0F, -2.5F, 2.0F, 3.0F, 5.0F, 0.0F, true);
        rarm.setTextureOffset(20, 58).addBox(-2.75F, -1.0F, -2.5F, 2.0F, 9.0F, 5.0F, 0.25F, true);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(6.25F, -6.0F, -0.5F);
        body.addChild(larm);
        larm.setTextureOffset(34, 58).addBox(0.75F, -1.0F, -2.5F, 2.0F, 9.0F, 5.0F, 0.0F, false);
        larm.setTextureOffset(59, 62).addBox(0.75F, 8.0F, -2.5F, 2.0F, 3.0F, 5.0F, 0.0F, false);
        larm.setTextureOffset(20, 58).addBox(0.75F, -1.0F, -2.5F, 2.0F, 9.0F, 5.0F, 0.25F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -7.0F, -1.0F);
        body.addChild(head);
        head.setTextureOffset(42, 14).addBox(-6.5F, -7.25F, -4.0F, 13.0F, 7.0F, 10.0F, 0.24F, false);
        head.setTextureOffset(46, 31).addBox(-6.5F, -7.0F, -4.0F, 13.0F, 7.0F, 10.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(2.99F, -7.49F, 6.24F);
        head.addChild(cube_r1);
        setRotateAngle(cube_r1, -0.7854F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(46, 0).addBox(-6.5F, 0.25F, 0.25F, 7.0F, 4.0F, 7.0F, 0.25F, false);

        nose = new AdvancedModelBox(this);
        nose.setRotationPoint(0.0F, -5.0F, -3.75F);
        head.addChild(nose);
        nose.setTextureOffset(42, 31).addBox(-2.5F, -1.0F, -2.25F, 5.0F, 2.0F, 2.0F, 0.0F, false);
        nose.setTextureOffset(46, 48).addBox(-7.0F, -5.0F, -1.25F, 14.0F, 11.0F, 0.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-3.5F, -4.0F, 1.0F);
        root.addChild(rleg);
        rleg.setTextureOffset(74, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, true);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(3.5F, -4.0F, 1.0F);
        root.addChild(lleg);
        lleg.setTextureOffset(74, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, nose, cube_r1, rleg, lleg, rarm, larm);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(UnderzealotEntity.ANIMATION_ATTACK_0);
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(30), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(-100), 0, (float) Math.toRadians(-40));
        animator.rotate(larm, (float) Math.toRadians(-100), 0, (float) Math.toRadians(40));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(-145), (float) Math.toRadians(40), (float) Math.toRadians(10));
        animator.rotate(larm, (float) Math.toRadians(-145), (float) Math.toRadians(-40), (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(UnderzealotEntity.ANIMATION_ATTACK_1);
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(-10), (float) Math.toRadians(30), 0);
        animator.rotate(rarm, (float) Math.toRadians(-70), (float) Math.toRadians(70), (float) Math.toRadians(-10));
        animator.rotate(larm, (float) Math.toRadians(-90), (float) Math.toRadians(-30), (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(-10), (float) Math.toRadians(-60), 0);
        animator.rotate(rarm, (float) Math.toRadians(-90), (float) Math.toRadians(30), (float) Math.toRadians(10));
        animator.rotate(larm, (float) Math.toRadians(-70), (float) Math.toRadians(-20), (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(UnderzealotEntity.ANIMATION_BREAKTORCH);
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(10), (float) Math.toRadians(-20), 0);
        animator.rotate(rarm, (float) Math.toRadians(-30), (float) Math.toRadians(10), (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(-150), (float) Math.toRadians(10), (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.resetKeyframe(5);

    }


    @Override
    public void setupAnim(UnderzealotEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float partialTick = ageInTicks - entity.tickCount;
        float buriedProgress = noBurrowing ? 0 : entity.getBuriedProgress(partialTick);
        float carryingProgress = entity.getCarryingProgress(partialTick);
        float prayingProgress = entity.getPrayingProgress(partialTick);
        float buriedStrength = (float) Math.sin(buriedProgress * Math.PI);
        float walkSpeed = 0.5F;
        float walkDegree = 1F;
        float digSpeed = 0.9F;
        float digDegree = 1F;
        float praySpeed = 0.2F;
        float prayDegree = 1F;
        float armFreedom = 1F - carryingProgress;
        progressPositionPrev(root, buriedProgress, 0, 28, 0, 1F);
        progressPositionPrev(rarm, buriedProgress, -2, -1, 0, 1F);
        progressPositionPrev(larm, buriedProgress, 2, -1, 0, 1F);
        progressPositionPrev(rarm, carryingProgress, 0, -2, 0, 1F);
        progressPositionPrev(larm, carryingProgress, 0, -2, 0, 1F);
        progressRotationPrev(rarm, buriedProgress, 0, 0, (float) Math.toRadians(160), 1F);
        progressRotationPrev(larm, buriedProgress, 0, 0, (float) Math.toRadians(-160), 1F);
        progressRotationPrev(rarm, carryingProgress, (float) Math.toRadians(-180), 0, (float) Math.toRadians(20), 1F);
        progressRotationPrev(larm, carryingProgress, (float) Math.toRadians(-180), 0, (float) Math.toRadians(-20), 1F);
        this.walk(nose, 0.4F, 0.1F, false, 1, -0.05F, ageInTicks, 1);
        this.swing(nose, 0.3F, 0.1F, false, 3, 0F, ageInTicks, 1);
        this.flap(rarm, 0.1F, 0.05F, false, 4, 0.05F, ageInTicks, 1);
        this.flap(larm, 0.1F, 0.05F, true, 4, 0.05F, ageInTicks, 1);
        this.swing(body, walkSpeed, walkDegree * 0.1F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.swing(head, walkSpeed, walkDegree * 0.1F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(lleg, walkSpeed, walkDegree * 0.5F, false, 1, -0.2F, limbSwing, limbSwingAmount);
        this.walk(rleg, walkSpeed, walkDegree * 0.5F, true, 1, 0.2F, limbSwing, limbSwingAmount);
        lleg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 2, true));
        rleg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 2, false));
        this.flap(rarm, walkSpeed, walkDegree * 0.1F, false, 4, 0.25F, limbSwing, limbSwingAmount * armFreedom);
        this.flap(larm, walkSpeed, walkDegree * 0.1F, true, 4, 0.25F, limbSwing, limbSwingAmount * armFreedom);
        this.swing(root, digSpeed, digDegree * 0.1F, false, 2, 0F, ageInTicks, buriedStrength);
        this.walk(rarm, digSpeed, digDegree, false, 4, 0F, ageInTicks, buriedStrength);
        this.walk(larm, digSpeed, digDegree, true, 4, 0F, ageInTicks, buriedStrength);
        this.walk(head, digSpeed, digDegree * 0.1F, true, 4, 0.5F, ageInTicks, buriedStrength);
        progressPositionPrev(root, prayingProgress, 0, -2, 0, 1F);
        progressPositionPrev(body, prayingProgress, 0, 0, 2, 1F);
        progressRotationPrev(root, prayingProgress, (float) Math.toRadians(-30), 0, 0, 1F);
        progressPositionPrev(lleg, prayingProgress, 0, 4, 0, 1F);
        progressRotationPrev(lleg, prayingProgress, (float) Math.toRadians(120), 0, 0, 1F);
        progressPositionPrev(rleg, prayingProgress, 0, 4, 0, 1F);
        progressRotationPrev(rleg, prayingProgress, (float) Math.toRadians(120), 0, 0, 1F);
        progressRotationPrev(rarm, prayingProgress * armFreedom, (float) Math.toRadians(-160), (float) Math.toRadians(60), 0, 1F);
        progressRotationPrev(larm, prayingProgress * armFreedom, (float) Math.toRadians(-160), (float) Math.toRadians(-60), 0, 1F);
        float bodyForwards = -Math.max(0, ACMath.walkValue(ageInTicks, prayingProgress, praySpeed, 4F, 6, false));
        body.rotationPointY -= bodyForwards * 0.5F;
        body.rotationPointZ += bodyForwards;
        this.walk(body, praySpeed, prayDegree * 0.5F, false, 4, 0.4F, ageInTicks, prayingProgress);
        this.walk(head, praySpeed, prayDegree * 0.5F, false, 3, 0.1F, ageInTicks, prayingProgress);
        this.walk(larm, praySpeed, prayDegree, false, 4, 1F, ageInTicks, prayingProgress * armFreedom);
        this.swing(larm, praySpeed, prayDegree, false, 4, 0.2F, ageInTicks, prayingProgress * armFreedom);
        this.walk(rarm, praySpeed, prayDegree, false, 4, 1F, ageInTicks, prayingProgress * armFreedom);
        this.swing(rarm, praySpeed, prayDegree, true, 4, 0.2F, ageInTicks, prayingProgress * armFreedom);
        this.faceTarget(netHeadYaw, headPitch, 1, head);
    }

}
