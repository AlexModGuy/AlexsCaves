package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.WatcherEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class WatcherModel extends AdvancedEntityModel<WatcherEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox head;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox rwing;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox lwing;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox lhorn;
    private final AdvancedModelBox rhorn;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox lleg;
    private final ModelAnimator animator;

    public WatcherModel() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, -2.0F, 0.0F);

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 5.125F, -0.5F);
        root.addChild(body);
        body.setTextureOffset(67, 61).addBox(-4.0F, -5.125F, -2.5F, 8.0F, 12.0F, 5.0F, 0.0F, false);
        body.setTextureOffset(27, 22).addBox(-5.0F, -5.875F, -3.5F, 10.0F, 22.0F, 7.0F, 0.25F, false);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(-5.1667F, -4.4583F, 0.0F);
        body.addChild(larm);
        larm.setTextureOffset(16, 61).addBox(-4.3333F, -0.6667F, -3.0F, 4.0F, 5.0F, 6.0F, 0.25F, false);
        larm.setTextureOffset(56, 46).addBox(-3.3333F, 0.3333F, -2.5F, 3.0F, 15.0F, 5.0F, 0.24F, false);
        larm.setTextureOffset(36, 65).addBox(-3.3333F, 15.3333F, -2.5F, 3.0F, 4.0F, 5.0F, 0.0F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(5.1667F, -4.4583F, 0.0F);
        body.addChild(rarm);
        rarm.setTextureOffset(16, 61).addBox(0.3333F, -0.6667F, -3.0F, 4.0F, 5.0F, 6.0F, 0.25F, true);
        rarm.setTextureOffset(56, 46).addBox(0.3333F, 0.3333F, -2.5F, 3.0F, 15.0F, 5.0F, 0.24F, true);
        rarm.setTextureOffset(36, 65).addBox(0.3333F, 15.3333F, -2.5F, 3.0F, 4.0F, 5.0F, 0.0F, true);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -5.125F, 0.5F);
        body.addChild(head);
        head.setTextureOffset(72, 45).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 7.0F, 0.0F, false);
        head.setTextureOffset(34, 0).addBox(-5.0F, -10.0F, -4.0F, 10.0F, 9.0F, 7.0F, 0.26F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, -10.25F, -4.25F);
        head.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.7854F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(54, 16).addBox(-2.0F, 0.25F, -7.25F, 4.0F, 4.0F, 7.0F, 0.25F, false);

        rwing = new AdvancedModelBox(this);
        rwing.setRotationPoint(0.0F, -5.0F, 3.0F);
        head.addChild(rwing);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        rwing.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 1.1781F, 0.0F);
        cube_r2.setTextureOffset(0, 18).addBox(0.0F, -9.0F, 0.0F, 0.0F, 17.0F, 11.0F, 0.0F, false);

        lwing = new AdvancedModelBox(this);
        lwing.setRotationPoint(0.0F, -5.0F, 3.0F);
        head.addChild(lwing);


        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
        lwing.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.0F, -1.1781F, 0.0F);
        cube_r3.setTextureOffset(0, 18).addBox(0.0F, -9.0F, 0.0F, 0.0F, 17.0F, 11.0F, 0.0F, true);

        lhorn = new AdvancedModelBox(this);
        lhorn.setRotationPoint(5.25F, -6.0F, 0.0F);
        head.addChild(lhorn);
        lhorn.setTextureOffset(0, 61).addBox(0.0F, -11.0F, 0.0F, 8.0F, 15.0F, 0.0F, 0.0F, true);

        rhorn = new AdvancedModelBox(this);
        rhorn.setRotationPoint(-5.25F, -6.0F, 0.0F);
        head.addChild(rhorn);
        rhorn.setTextureOffset(0, 61).addBox(-8.0F, -11.0F, 0.0F, 8.0F, 15.0F, 0.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(2.5F, 11.5F, -0.5F);
        root.addChild(rleg);
        rleg.setTextureOffset(61, 27).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 15.0F, 3.0F, 0.0F, true);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(-2.5F, 11.5F, -0.5F);
        root.addChild(lleg);
        lleg.setTextureOffset(61, 27).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 15.0F, 3.0F, 0.0F, false);
        root.rotateAngleY = (float) Math.PI;
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public void setupAnim(WatcherEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float partialTick = ageInTicks - entity.tickCount;
        float runProgress = entity.getRunAmount(partialTick);
        float shadeAmount = entity.getShadeAmount(partialTick);
        float groundAmount = 1F - shadeAmount;
        float walkAmount = limbSwingAmount * (1 - runProgress);
        float runAmount = limbSwingAmount * runProgress;
        float walkSpeed = 0.5F;
        float walkDegree = 1F;
        float runSpeed = 0.5F;
        float runDegree = 1F;
        float twitchinessAmount = ACMath.smin((float) Math.sin(ageInTicks * 0.03F) + 0.5F, 0.0F, 0.3F);
        progressRotationPrev(body, walkAmount, (float) Math.toRadians(-5), 0, 0, 1F);
        progressRotationPrev(head, walkAmount, (float) Math.toRadians(5), 0, 0, 1F);
        progressRotationPrev(body, runAmount, (float) Math.toRadians(-15), 0, 0, 1F);
        progressRotationPrev(head, runAmount, (float) Math.toRadians(15), 0, 0, 1F);
        progressRotationPrev(rarm, runProgress, (float) Math.toRadians(75), (float) Math.toRadians(35), 0, 1F);
        progressRotationPrev(larm, runProgress, (float) Math.toRadians(75), (float) Math.toRadians(-35), 0, 1F);
        progressPositionPrev(body, runAmount * groundAmount, 0, -6.5F, 4, 1F);
        progressPositionPrev(head, runAmount * groundAmount, 0, 1.5F, 2, 1F);
        this.swing(lwing, 0.2F, 0.25F, false, 1, 0.1F, ageInTicks, 1);
        this.swing(rwing, 0.2F, 0.25F, true, 1, 0.1F, ageInTicks, 1);
        this.flap(head, 4, 0.1F, false, 0, 0F, ageInTicks, twitchinessAmount);
        this.flap(larm, 0.1F, 0.1F, false, -0.5F, 0.1F, ageInTicks, 1);
        this.flap(rarm, 0.1F, 0.1F, true, -0.5F, 0.1F, ageInTicks, 1);
        this.swing(larm, 0.1F, 0.1F, true, 2, 0F, ageInTicks, 1);
        this.swing(rarm, 0.1F, 0.1F, false, 2, 0F, ageInTicks, 1);
        this.walk(lleg, walkSpeed, walkDegree * 0.5F, false, 1, -0.1F, limbSwing, walkAmount * groundAmount);
        this.walk(rleg, walkSpeed, walkDegree * 0.5F, true, 1, 0.1F, limbSwing, walkAmount * groundAmount);
        this.walk(body, walkSpeed * 0.5F, walkDegree * 0.1F, false, -2, 0.0F, limbSwing, walkAmount);
        this.walk(head, walkSpeed * 0.5F, walkDegree * 0.1F, false, -1, 0.0F, limbSwing, walkAmount);
        this.walk(larm, walkSpeed * 0.5F, walkDegree * 0.25F, false, -0.5F, 0.1F, limbSwing, walkAmount);
        this.walk(rarm, walkSpeed * 0.5F, walkDegree * 0.25F, false, 0.5F, 0.1F, limbSwing, walkAmount);
        this.walk(head, runSpeed * 0.5F, runDegree * 0.1F, false, -1, 0.0F, limbSwing, runAmount);
        this.walk(lleg, runSpeed, runDegree * 0.5F, false, 4, -0.1F, limbSwing, runAmount * groundAmount);
        this.walk(rleg, runSpeed, runDegree * 0.5F, true, 4, 0.1F, limbSwing, runAmount * groundAmount);
        this.walk(rarm, runSpeed, runDegree * 0.15F, false, 2, 0.4F, limbSwing, runAmount);
        this.walk(larm, runSpeed, runDegree * 0.15F, true, 2, -0.4F, limbSwing, runAmount);
        this.swing(root, runSpeed, runDegree * 0.35F, true, 3, 0F, limbSwing, runAmount * groundAmount);
        this.swing(body, runSpeed, runDegree * 0.25F, false, 3, 0F, limbSwing, runAmount);
        this.larm.setScale(1F, 1F + runAmount * 0.4F, 1F);
        this.rarm.setScale(1F, 1F + runAmount * 0.4F, 1F);
        this.walk(lleg, walkSpeed, walkDegree * 0.3F, false, -1, -0.1F, limbSwing, limbSwingAmount * shadeAmount);
        this.walk(rleg, walkSpeed, walkDegree * 0.3F, false, -2, -0.1F, limbSwing, limbSwingAmount * shadeAmount);
        this.head.rotateAngleY += netHeadYaw / 57.295776F;
        this.head.rotateAngleX -= headPitch / 57.295776F;
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, cube_r1, cube_r2, cube_r3, body, larm, lleg, rarm, rleg, lhorn, rhorn, head, rwing, lwing);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(WatcherEntity.ANIMATION_ATTACK_0);
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float) Math.toRadians(30), 0);
        animator.rotate(rarm, (float) Math.toRadians(-35), (float) Math.toRadians(-60), (float) Math.toRadians(-25));
        animator.rotate(larm, (float) Math.toRadians(25), 0, (float) Math.toRadians(40));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float) Math.toRadians(-30), 0);
        animator.rotate(rarm, (float) Math.toRadians(25), 0, (float) Math.toRadians(-40));
        animator.rotate(larm, (float) Math.toRadians(-35), (float) Math.toRadians(60), (float) Math.toRadians(25));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(WatcherEntity.ANIMATION_ATTACK_1);
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -5);
        animator.rotate(body, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(-10), (float) Math.toRadians(75), (float) Math.toRadians(25));
        animator.rotate(larm, (float) Math.toRadians(-10), (float) Math.toRadians(-75), (float) Math.toRadians(-25));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(rarm, (float) Math.toRadians(-0), (float) Math.toRadians(-45), (float) Math.toRadians(25));
        animator.rotate(larm, (float) Math.toRadians(-0), (float) Math.toRadians(45), (float) Math.toRadians(-25));
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    public void positionForParticle(float partialTick, float v) {
        this.resetToDefaultPose();
    }
}
