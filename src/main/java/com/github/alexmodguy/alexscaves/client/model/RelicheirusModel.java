package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.RelicheirusEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;

public class RelicheirusModel extends AdvancedEntityModel<RelicheirusEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox chest;
    private final AdvancedModelBox chestFeathers;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox larmFeathers;
    private final AdvancedModelBox lhand;
    private final AdvancedModelBox lhandClaw1;
    private final AdvancedModelBox lhandClaw2;
    private final AdvancedModelBox lhandClaw3;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox rarmFeathers;
    private final AdvancedModelBox rhand;
    private final AdvancedModelBox rhandClaw1;
    private final AdvancedModelBox rhandClaw2;
    private final AdvancedModelBox rhandClaw3;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox neckFeathers;
    private final AdvancedModelBox head;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox lwattle;
    private final AdvancedModelBox rwattle;
    private final AdvancedModelBox hips;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox lfoot;
    private final AdvancedModelBox claw1;
    private final AdvancedModelBox claw2;
    private final AdvancedModelBox claw3;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox rfoot;
    private final AdvancedModelBox rclaw1;
    private final AdvancedModelBox rclaw2;
    private final AdvancedModelBox rclaw3;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox tailFeathers;
    private final ModelAnimator animator;

    public RelicheirusModel() {
        texWidth = 256;
        texHeight = 256;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -15.0F, 0.0F);

        chest = new AdvancedModelBox(this);
        chest.setRotationPoint(0.0F, -10.5F, 3.0F);
        body.addChild(chest);
        chest.setTextureOffset(34, 98).addBox(-15.0F, 28.5F, -28.0F, 30.0F, 4.0F, 28.0F, 0.0F, false);
        chest.setTextureOffset(0, 0).addBox(-15.0F, -8.5F, -28.0F, 30.0F, 37.0F, 28.0F, 0.0F, false);
        chest.setTextureOffset(0, 96).addBox(-13.5F, -1.5F, -2.0F, 27.0F, 18.0F, 11.0F, 0.0F, false);

        chestFeathers = new AdvancedModelBox(this);
        chestFeathers.setRotationPoint(0.0F, -8.5F, -9.5F);
        chest.addChild(chestFeathers);
        chestFeathers.setTextureOffset(0, 113).addBox(0.0F, -6.0F, -18.5F, 0.0F, 14.0F, 33.0F, 0.0F, false);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(15.0F, 20.5F, -21.0F);
        chest.addChild(larm);
        larm.setTextureOffset(40, 160).addBox(-2.5F, 4.0F, 2.5F, 7.0F, 21.0F, 15.0F, 0.0F, false);
        larm.setTextureOffset(84, 170).addBox(-2.0F, 9.0F, 3.0F, 6.0F, 8.0F, 14.0F, 0.0F, false);
        larm.setTextureOffset(130, 138).addBox(-2.5F, -4.0F, -2.5F, 7.0F, 8.0F, 20.0F, 0.0F, false);

        larmFeathers = new AdvancedModelBox(this);
        larmFeathers.setRotationPoint(2.5F, 7.5F, 17.5F);
        larm.addChild(larmFeathers);
        larmFeathers.setTextureOffset(154, 221).addBox(0.0F, -11.5F, 0.0F, 0.0F, 23.0F, 6.0F, 0.0F, false);

        lhand = new AdvancedModelBox(this);
        lhand.setRotationPoint(0.0F, 17.5F, 10.0F);
        larm.addChild(lhand);
        lhand.setTextureOffset(84, 170).addBox(-2.0F, -0.5F, -7.0F, 6.0F, 8.0F, 14.0F, 0.0F, false);

        lhandClaw1 = new AdvancedModelBox(this);
        lhandClaw1.setRotationPoint(3.0F, 7.5F, -5.0F);
        lhand.addChild(lhandClaw1);
        lhandClaw1.setTextureOffset(151, 169).addBox(-12.0F, -4.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);
        lhandClaw1.setTextureOffset(0, 88).addBox(-12.0F, 0.0F, -2.0F, 13.0F, 4.0F, 4.0F, 0.0F, false);

        lhandClaw2 = new AdvancedModelBox(this);
        lhandClaw2.setRotationPoint(1.0F, 7.5F, 0.0F);
        lhand.addChild(lhandClaw2);
        lhandClaw2.setTextureOffset(151, 169).addBox(-10.0F, -4.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);
        lhandClaw2.setTextureOffset(0, 88).addBox(-10.0F, 0.0F, -2.0F, 13.0F, 4.0F, 4.0F, 0.0F, false);

        lhandClaw3 = new AdvancedModelBox(this);
        lhandClaw3.setRotationPoint(1.0F, 7.5F, 5.0F);
        lhand.addChild(lhandClaw3);
        lhandClaw3.setTextureOffset(151, 169).addBox(-10.0F, -4.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);
        lhandClaw3.setTextureOffset(0, 88).addBox(-10.0F, 0.0F, -2.0F, 13.0F, 4.0F, 4.0F, 0.0F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-15.0F, 20.5F, -21.5F);
        chest.addChild(rarm);
        rarm.setTextureOffset(84, 170).addBox(-4.0F, 9.0F, 3.5F, 6.0F, 8.0F, 14.0F, 0.0F, true);
        rarm.setTextureOffset(40, 160).addBox(-4.5F, 4.0F, 3.0F, 7.0F, 21.0F, 15.0F, 0.0F, true);
        rarm.setTextureOffset(130, 138).addBox(-4.5F, -4.0F, -2.0F, 7.0F, 8.0F, 20.0F, 0.0F, true);

        rarmFeathers = new AdvancedModelBox(this);
        rarmFeathers.setRotationPoint(-2.5F, 7.5F, 18.0F);
        rarm.addChild(rarmFeathers);
        rarmFeathers.setTextureOffset(154, 221).addBox(0.0F, -11.5F, 0.0F, 0.0F, 23.0F, 6.0F, 0.0F, true);

        rhand = new AdvancedModelBox(this);
        rhand.setRotationPoint(0.0F, 17.5F, 10.5F);
        rarm.addChild(rhand);
        rhand.setTextureOffset(84, 170).addBox(-4.0F, -0.5F, -7.0F, 6.0F, 8.0F, 14.0F, 0.0F, true);

        rhandClaw1 = new AdvancedModelBox(this);
        rhandClaw1.setRotationPoint(-3.0F, 7.5F, -5.0F);
        rhand.addChild(rhandClaw1);
        rhandClaw1.setTextureOffset(151, 169).addBox(11.0F, -4.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, true);
        rhandClaw1.setTextureOffset(0, 88).addBox(-1.0F, 0.0F, -2.0F, 13.0F, 4.0F, 4.0F, 0.0F, true);

        rhandClaw2 = new AdvancedModelBox(this);
        rhandClaw2.setRotationPoint(-1.0F, 7.5F, 0.0F);
        rhand.addChild(rhandClaw2);
        rhandClaw2.setTextureOffset(151, 169).addBox(9.0F, -4.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, true);
        rhandClaw2.setTextureOffset(0, 88).addBox(-3.0F, 0.0F, -2.0F, 13.0F, 4.0F, 4.0F, 0.0F, true);

        rhandClaw3 = new AdvancedModelBox(this);
        rhandClaw3.setRotationPoint(-1.0F, 7.5F, 5.0F);
        rhand.addChild(rhandClaw3);
        rhandClaw3.setTextureOffset(151, 169).addBox(9.0F, -4.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, true);
        rhandClaw3.setTextureOffset(0, 88).addBox(-3.0F, 0.0F, -2.0F, 13.0F, 4.0F, 4.0F, 0.0F, true);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(0.0F, 4.5F, -23.0F);
        chest.addChild(neck);
        neck.setTextureOffset(116, 0).addBox(-8.0F, -28.0F, -11.0F, 16.0F, 30.0F, 16.0F, 0.0F, false);
        neck.setTextureOffset(66, 138).addBox(-8.0F, 2.0F, -11.0F, 16.0F, 6.0F, 16.0F, 0.0F, false);

        neckFeathers = new AdvancedModelBox(this);
        neckFeathers.setRotationPoint(0.0F, -28.0F, -7.5F);
        neck.addChild(neckFeathers);
        neckFeathers.setTextureOffset(0, 49).addBox(0.0F, -3.0F, -9.5F, 0.0F, 22.0F, 17.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -25.0F, -1.0F);
        neck.addChild(head);
        head.setTextureOffset(0, 160).addBox(-5.0F, -27.0F, -5.0F, 10.0F, 31.0F, 10.0F, 0.0F, false);
        head.setTextureOffset(168, 46).addBox(-3.0F, -27.0F, -12.0F, 6.0F, 2.0F, 7.0F, 0.0F, false);
        head.setTextureOffset(168, 56).addBox(-4.5F, -27.0F, -17.0F, 9.0F, 4.0F, 5.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, -25.0F, -5.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(88, 14).addBox(-3.5F, 0.0F, -7.0F, 7.0F, 4.0F, 7.0F, 0.0F, false);
        jaw.setTextureOffset(0, 19).addBox(-4.5F, 2.0F, -12.0F, 9.0F, 2.0F, 5.0F, 0.0F, false);

        lwattle = new AdvancedModelBox(this);
        lwattle.setRotationPoint(3.0F, 4.0F, -1.0F);
        jaw.addChild(lwattle);
        lwattle.setTextureOffset(30, 164).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);

        rwattle = new AdvancedModelBox(this);
        rwattle.setRotationPoint(-3.0F, 4.0F, -1.0F);
        jaw.addChild(rwattle);
        rwattle.setTextureOffset(30, 164).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);

        hips = new AdvancedModelBox(this);
        hips.setRotationPoint(0.0F, -10.5F, 3.0F);
        body.addChild(hips);
        hips.setTextureOffset(97, 46).addBox(-13.0F, -0.75F, -5.0F, 26.0F, 29.0F, 19.0F, 0.0F, false);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(12.0F, 23.5F, 9.5F);
        hips.addChild(lleg);
        lleg.setTextureOffset(124, 166).addBox(-4.0F, -3.0F, -7.5F, 8.0F, 18.0F, 11.0F, 0.0F, false);

        lleg2 = new AdvancedModelBox(this);
        lleg2.setRotationPoint(-0.5F, 13.0F, 1.0F);
        lleg.addChild(lleg2);
        lleg2.setTextureOffset(0, 0).addBox(-2.5F, -2.0F, -2.5F, 5.0F, 12.0F, 7.0F, 0.0F, false);

        lfoot = new AdvancedModelBox(this);
        lfoot.setRotationPoint(0.0F, 9.5F, 1.0F);
        lleg2.addChild(lfoot);
        lfoot.setTextureOffset(88, 0).addBox(-3.5F, 0.5F, -7.5F, 7.0F, 3.0F, 11.0F, 0.0F, false);

        claw1 = new AdvancedModelBox(this);
        claw1.setRotationPoint(-2.5F, 0.5F, -7.5F);
        lfoot.addChild(claw1);
        claw1.setTextureOffset(88, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 3.0F, 3.0F, 0.0F, false);

        claw2 = new AdvancedModelBox(this);
        claw2.setRotationPoint(0.0F, 0.5F, -7.5F);
        lfoot.addChild(claw2);
        claw2.setTextureOffset(88, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 3.0F, 3.0F, 0.0F, false);

        claw3 = new AdvancedModelBox(this);
        claw3.setRotationPoint(2.5F, 0.5F, -7.5F);
        lfoot.addChild(claw3);
        claw3.setTextureOffset(88, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 3.0F, 3.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-12.0F, 23.5F, 9.5F);
        hips.addChild(rleg);
        rleg.setTextureOffset(124, 166).addBox(-4.0F, -3.0F, -7.5F, 8.0F, 18.0F, 11.0F, 0.0F, true);

        rleg2 = new AdvancedModelBox(this);
        rleg2.setRotationPoint(0.5F, 13.0F, 1.0F);
        rleg.addChild(rleg2);
        rleg2.setTextureOffset(0, 0).addBox(-2.5F, -2.0F, -2.5F, 5.0F, 12.0F, 7.0F, 0.0F, true);

        rfoot = new AdvancedModelBox(this);
        rfoot.setRotationPoint(0.0F, 9.5F, 1.0F);
        rleg2.addChild(rfoot);
        rfoot.setTextureOffset(88, 0).addBox(-3.5F, 0.5F, -7.5F, 7.0F, 3.0F, 11.0F, 0.0F, true);

        rclaw1 = new AdvancedModelBox(this);
        rclaw1.setRotationPoint(2.5F, 0.5F, -7.5F);
        rfoot.addChild(rclaw1);
        rclaw1.setTextureOffset(88, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 3.0F, 3.0F, 0.0F, true);

        rclaw2 = new AdvancedModelBox(this);
        rclaw2.setRotationPoint(0.0F, 0.5F, -7.5F);
        rfoot.addChild(rclaw2);
        rclaw2.setTextureOffset(88, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 3.0F, 3.0F, 0.0F, true);

        rclaw3 = new AdvancedModelBox(this);
        rclaw3.setRotationPoint(-2.5F, 0.5F, -7.5F);
        rfoot.addChild(rclaw3);
        rclaw3.setTextureOffset(88, 0).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 3.0F, 3.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, 10.5F, 13.0F);
        hips.addChild(tail);
        tail.setTextureOffset(165, 100).addBox(-8.0F, -5.0F, -2.0F, 16.0F, 16.0F, 22.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setRotationPoint(0.0F, 1.0F, 19.5F);
        tail.addChild(tail2);
        tail2.setTextureOffset(88, 225).addBox(-4.0F, -2.0F, -2.5F, 8.0F, 8.0F, 23.0F, 0.0F, false);

        tailFeathers = new AdvancedModelBox(this);
        tailFeathers.setRotationPoint(0.0F, 1.0F, 17.5F);
        tail2.addChild(tailFeathers);
        tailFeathers.setTextureOffset(127, 167).addBox(-12.0F, 0.0F, -14.0F, 24.0F, 18.0F, 38.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, chest, chestFeathers, tail, tail2, tailFeathers, neck, neckFeathers, head, jaw, hips, lwattle, rwattle, lleg, lleg2, rleg, rleg2, rfoot, lfoot, rarm, rarmFeathers, larm, larmFeathers, rhand, rhandClaw1, rhandClaw2, rhandClaw3, lhand, lhandClaw1, lhandClaw2, lhandClaw3, rclaw1, rclaw2, rclaw3, claw1, claw2, claw3);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(RelicheirusEntity.ANIMATION_SPEAK_1);
        animator.startKeyframe(5);
        animatePose(0);
        animator.rotate(neck, (float) Math.toRadians(5), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(5);
        animator.setAnimation(RelicheirusEntity.ANIMATION_SPEAK_2);
        animator.startKeyframe(5);
        animatePose(0);
        animator.rotate(neck, (float) Math.toRadians(5), (float) Math.toRadians(10), 0);
        animator.rotate(head, (float) Math.toRadians(-15), (float) Math.toRadians(10), 0);
        animator.endKeyframe();
        animator.startKeyframe(10);
        animatePose(0);
        animator.rotate(neck, (float) Math.toRadians(5), (float) Math.toRadians(-10), 0);
        animator.rotate(head, (float) Math.toRadians(-15), (float) Math.toRadians(-10), 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(RelicheirusEntity.ANIMATION_EAT_TREE);
        animator.startKeyframe(5);
        animatePose(1);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.rotate(neck, (float) Math.toRadians(10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rwattle, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(lwattle, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(lhand, 0, 0, (float) Math.toRadians(10));
        animator.rotate(rhand, 0, 0, (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.rotate(lhand, 0, 0, (float) Math.toRadians(10));
        animator.rotate(rhand, 0, 0, (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.rotate(neck, (float) Math.toRadians(10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rwattle, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(lwattle, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(lhand, 0, 0, (float) Math.toRadians(10));
        animator.rotate(rhand, 0, 0, (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.rotate(lhand, 0, 0, (float) Math.toRadians(10));
        animator.rotate(rhand, 0, 0, (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.rotate(neck, (float) Math.toRadians(10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rwattle, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(lwattle, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(lhand, 0, 0, (float) Math.toRadians(10));
        animator.rotate(rhand, 0, 0, (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(RelicheirusEntity.ANIMATION_EAT_TRILOCARIS);
        animator.startKeyframe(10);
        animatePose(2);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(15), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(10);
        animatePose(2);
        animator.rotate(neck, (float) Math.toRadians(35), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rwattle, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(lwattle, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(15);
        animatePose(1);
        animator.rotate(larm, (float) Math.toRadians(55), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(55), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rwattle, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(lwattle, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(RelicheirusEntity.ANIMATION_PUSH_TREE);
        animator.startKeyframe(15);
        animatePose(1);
        animator.rotate(lhand, 0, 0, (float) Math.toRadians(10));
        animator.rotate(rhand, 0, 0, (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.startKeyframe(10);
        animatePose(1);
        animator.rotate(neck, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(larm, 0, 0, (float) Math.toRadians(-10));
        animator.rotate(rarm, 0, 0, (float) Math.toRadians(10));
        animator.rotate(lhand, 0, 0, (float) Math.toRadians(-20));
        animator.rotate(rhand, 0, 0, (float) Math.toRadians(20));
        animator.move(body, 0, 0, 5);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animatePose(1);
        animator.rotate(neck, (float) Math.toRadians(5), 0, 0);
        animator.rotate(head, (float) Math.toRadians(5), 0, 0);
        animator.rotate(larm, 0, 0, (float) Math.toRadians(20));
        animator.rotate(rarm, 0, 0, (float) Math.toRadians(-20));
        animator.move(body, 0, 0, -5F);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(RelicheirusEntity.ANIMATION_SCRATCH_1);
        animator.startKeyframe(10);
        animatePose(1);
        animator.move(rarm, -2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(65), 0, (float) Math.toRadians(5));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(rarm, (float) Math.toRadians(-155), 0, (float) Math.toRadians(-15));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(rhandClaw1, 0, 0, (float) Math.toRadians(35));
        animator.rotate(rhandClaw2, 0, 0, (float) Math.toRadians(35));
        animator.rotate(rhandClaw3, 0, 0, (float) Math.toRadians(35));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.move(rarm, -2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(45), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(65), 0, (float) Math.toRadians(5));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(rarm, (float) Math.toRadians(-115), 0, (float) Math.toRadians(5));
        animator.rotate(rhand, (float) Math.toRadians(-10), (float) Math.toRadians(-30), (float) Math.toRadians(-15));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(rhandClaw1, 0, 0, (float) Math.toRadians(55));
        animator.rotate(rhandClaw2, 0, 0, (float) Math.toRadians(55));
        animator.rotate(rhandClaw3, 0, 0, (float) Math.toRadians(55));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.move(rarm, -2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(65), 0, (float) Math.toRadians(5));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(rarm, (float) Math.toRadians(-155), 0, (float) Math.toRadians(-15));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(rhandClaw1, 0, 0, (float) Math.toRadians(35));
        animator.rotate(rhandClaw2, 0, 0, (float) Math.toRadians(35));
        animator.rotate(rhandClaw3, 0, 0, (float) Math.toRadians(35));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.move(rarm, -2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(45), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(65), 0, (float) Math.toRadians(5));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(rarm, (float) Math.toRadians(-115), 0, (float) Math.toRadians(5));
        animator.rotate(rhand, (float) Math.toRadians(-10), (float) Math.toRadians(-30), (float) Math.toRadians(-15));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(rhandClaw1, 0, 0, (float) Math.toRadians(55));
        animator.rotate(rhandClaw2, 0, 0, (float) Math.toRadians(55));
        animator.rotate(rhandClaw3, 0, 0, (float) Math.toRadians(55));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.move(rarm, -2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(65), 0, (float) Math.toRadians(5));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(rarm, (float) Math.toRadians(-155), 0, (float) Math.toRadians(-15));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(rhandClaw1, 0, 0, (float) Math.toRadians(35));
        animator.rotate(rhandClaw2, 0, 0, (float) Math.toRadians(35));
        animator.rotate(rhandClaw3, 0, 0, (float) Math.toRadians(35));
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(RelicheirusEntity.ANIMATION_SCRATCH_2);
        animator.startKeyframe(10);
        animatePose(1);
        animator.move(larm, 2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(65), 0, (float) Math.toRadians(-5));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(larm, (float) Math.toRadians(-135), 0, (float) Math.toRadians(15));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(lhandClaw1, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(lhandClaw2, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(lhandClaw3, 0, 0, (float) Math.toRadians(-35));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.move(larm, 2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(45), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(65), 0, (float) Math.toRadians(-5));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(larm, (float) Math.toRadians(-115), 0, (float) Math.toRadians(-5));
        animator.rotate(lhand, (float) Math.toRadians(-10), (float) Math.toRadians(30), (float) Math.toRadians(15));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(lhandClaw1, 0, 0, (float) Math.toRadians(-55));
        animator.rotate(lhandClaw2, 0, 0, (float) Math.toRadians(-55));
        animator.rotate(lhandClaw3, 0, 0, (float) Math.toRadians(-55));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.move(larm, 2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(65), 0, (float) Math.toRadians(-5));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(larm, (float) Math.toRadians(-155), 0, (float) Math.toRadians(15));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(lhandClaw1, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(lhandClaw2, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(lhandClaw3, 0, 0, (float) Math.toRadians(-35));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.move(larm, 2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(45), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(65), 0, (float) Math.toRadians(-5));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(larm, (float) Math.toRadians(-115), 0, (float) Math.toRadians(-5));
        animator.rotate(lhand, (float) Math.toRadians(-10), (float) Math.toRadians(30), (float) Math.toRadians(15));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(lhandClaw1, 0, 0, (float) Math.toRadians(-55));
        animator.rotate(lhandClaw2, 0, 0, (float) Math.toRadians(-55));
        animator.rotate(lhandClaw3, 0, 0, (float) Math.toRadians(-55));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animatePose(1);
        animator.move(larm, 2, -4, 0F);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(65), 0, (float) Math.toRadians(-5));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(larm, (float) Math.toRadians(-155), 0, (float) Math.toRadians(15));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(lhandClaw1, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(lhandClaw2, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(lhandClaw3, 0, 0, (float) Math.toRadians(-35));
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(RelicheirusEntity.ANIMATION_MELEE_SLASH_1);
        animator.startKeyframe(7);
        animatePose(1);
        animator.rotate(body, 0, (float) Math.toRadians(15), 0);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(75), 0, (float) Math.toRadians(5));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(rarm, (float) Math.toRadians(15), (float) Math.toRadians(50), (float) Math.toRadians(75));
        animator.rotate(rhand, 0, 0, (float) Math.toRadians(35));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animatePose(1);
        animator.rotate(body, 0, (float) Math.toRadians(-15), 0);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(75), 0, (float) Math.toRadians(5));
        animator.rotate(lhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(35));
        animator.rotate(rarm, (float) Math.toRadians(-15), (float) Math.toRadians(-20), (float) Math.toRadians(15));
        animator.rotate(rhand, 0, 0, (float) Math.toRadians(35));
        animator.rotate(rhandClaw1, 0, 0, (float) Math.toRadians(35));
        animator.rotate(rhandClaw2, 0, 0, (float) Math.toRadians(35));
        animator.rotate(rhandClaw3, 0, 0, (float) Math.toRadians(35));
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(RelicheirusEntity.ANIMATION_MELEE_SLASH_2);
        animator.startKeyframe(7);
        animatePose(1);
        animator.rotate(body, 0, (float) Math.toRadians(-15), 0);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(75), 0, (float) Math.toRadians(-5));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(larm, (float) Math.toRadians(15), (float) Math.toRadians(-50), (float) Math.toRadians(-75));
        animator.rotate(lhand, 0, 0, (float) Math.toRadians(-35));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animatePose(1);
        animator.rotate(body, 0, (float) Math.toRadians(15), 0);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(75), 0, (float) Math.toRadians(-5));
        animator.rotate(rhand, (float) Math.toRadians(10), 0, (float) Math.toRadians(-35));
        animator.rotate(larm, (float) Math.toRadians(-15), (float) Math.toRadians(20), (float) Math.toRadians(-15));
        animator.rotate(lhand, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(lhandClaw1, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(lhandClaw2, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(lhandClaw3, 0, 0, (float) Math.toRadians(-35));
        animator.endKeyframe();
        animator.resetKeyframe(10);
    }

    private void animatePose(int pose) {
        switch (pose) {
            case 0:
                animator.move(lwattle, 0, 0, -1);
                animator.move(rwattle, 0, 0, -1);
                animator.rotate(lwattle, (float) Math.toRadians(-35), 0, 0);
                animator.rotate(rwattle, (float) Math.toRadians(-35), 0, 0);
                animator.rotate(jaw, (float) Math.toRadians(30), 0, 0);
                break;
            case 1:
                animator.move(body, 0, 1, 5);
                animator.move(rleg, 0, -6, -1);
                animator.move(lleg, 0, -6, -1);
                animator.rotate(body, (float) Math.toRadians(-35), 0, 0);
                animator.rotate(neck, (float) Math.toRadians(35), 0, 0);
                animator.rotate(lleg, (float) Math.toRadians(35), 0, 0);
                animator.rotate(rleg, (float) Math.toRadians(35), 0, 0);
                animator.rotate(tail, (float) Math.toRadians(15), 0, 0);
                animator.rotate(tail2, (float) Math.toRadians(15), 0, 0);
                animator.rotate(chest, (float) Math.toRadians(-10), 0, 0);
                animator.rotate(larm, (float) Math.toRadians(-55), 0, 0);
                animator.rotate(rarm, (float) Math.toRadians(-55), 0, 0);
                break;
            case 2:
                animator.move(body, 0, 7, -2);
                animator.move(rleg, 0, 2.5F, -1.5F);
                animator.move(lleg, 0, 2.5F, -1.5F);
                animator.move(rarm, 0, -4F, 1.5F);
                animator.move(larm, 0, -4F, 1.5F);
                animator.rotate(body, (float) Math.toRadians(35), 0, 0);
                animator.rotate(tail, (float) Math.toRadians(-25), 0, 0);
                animator.rotate(rleg, (float) Math.toRadians(-35), 0, 0);
                animator.rotate(lleg, (float) Math.toRadians(-35), 0, 0);
                animator.rotate(rarm, (float) Math.toRadians(-125), 0, 0);
                animator.rotate(larm, (float) Math.toRadians(-125), 0, 0);
                break;
        }
    }

    private void setupAnimForAnimation(RelicheirusEntity entity, Animation animation, float limbSwing, float bodyDown, float ageInTicks) {
        float partialTick = ageInTicks - entity.tickCount;
        if (animation == RelicheirusEntity.ANIMATION_SHAKE) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, animation, partialTick, 0);
            this.swing(neck, 0.5F, 0.2F, false, 1, 0F, ageInTicks, animationIntensity);
            this.swing(head, 0.5F, 0.2F, false, 0F, 0F, ageInTicks, animationIntensity);
            this.flap(head, 0.5F, 0.1F, false, 0F, 0F, ageInTicks, animationIntensity);
            this.flap(lwattle, 0.5F, 0.7F, false, -1, 0F, ageInTicks, animationIntensity);
            this.flap(rwattle, 0.5F, 0.7F, false, -1, 0F, ageInTicks, animationIntensity);
            this.walk(neck, 0.5F, 0.05F, false, 3F, 0.3F, ageInTicks, animationIntensity);
            this.walk(head, 0.5F, 0.05F, false, 2F, 0.1F, ageInTicks, animationIntensity);
        }
        if (animation == RelicheirusEntity.ANIMATION_EAT_TREE || animation == RelicheirusEntity.ANIMATION_EAT_TRILOCARIS) {
            float animationIntensity;
            if (animation == RelicheirusEntity.ANIMATION_EAT_TRILOCARIS) {
                animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 2, animation, partialTick, 15, 30);
            } else {
                animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, animation, partialTick, 0);
            }
            float peckY = (float) (entity.getPeckY() - (entity.getEyePosition(partialTick).y - bodyDown / 16));
            float peckYPixel = Mth.clamp(peckY, -2F, 2F) * animationIntensity;
            this.neck.rotateAngleX -= peckYPixel * 0.2F;
            this.head.rotateAngleX -= peckYPixel * 0.2F;
            this.neck.rotationPointY -= peckYPixel * 16 * 0.15F;
            this.neck.rotationPointZ += Math.abs(peckYPixel * 16 * 0.35F);
        }
    }

    @Override
    public void setupAnim(RelicheirusEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float partialTick = ageInTicks - entity.tickCount;
        float walkSpeed = 0.8F;
        float walkDegree = 2F;
        float raisedArmsAmount = entity.getRaiseArmsAmount(partialTick);
        float armsWalkAmount = 1F - raisedArmsAmount;
        float f = articulateLegs(entity.legSolver, armsWalkAmount, partialTick);
        if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            setupAnimForAnimation(entity, entity.getAnimation(), limbSwing, f, ageInTicks);
        }
        this.walk(neck, 0.1F, 0.03F, true, 0F, 0F, ageInTicks, 1);
        this.walk(head, 0.1F, 0.03F, false, -0.5F, 0.0F, ageInTicks, 1);
        this.flap(lwattle, 0.1F, 0.1F, false, -1F, 0.0F, ageInTicks, 1);
        this.flap(rwattle, 0.1F, 0.1F, false, -1F, 0.0F, ageInTicks, 1);
        this.swing(tail, 0.1F, 0.05F, true, 0F, 0F, ageInTicks, 1);
        this.swing(tail2, 0.1F, 0.1F, true, -1F, 0F, ageInTicks, 1);

        this.walk(neck, walkSpeed, walkDegree * 0.1F, false, 2F, 0.3F, limbSwing, limbSwingAmount);
        this.walk(head, walkSpeed, walkDegree * -0.1F, false, 1F, -0.2F, limbSwing, limbSwingAmount);
        this.swing(tail, walkSpeed, walkDegree * 0.2F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.swing(tail2, walkSpeed, walkDegree * 0.2F, false, 2F, 0F, limbSwing, limbSwingAmount);
        float bodyWalkBob = walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 4, false);
        this.body.rotationPointY += bodyWalkBob;
        this.lleg.rotationPointY -= bodyWalkBob;
        this.rleg.rotationPointY -= bodyWalkBob;
        this.larm.rotationPointY -= bodyWalkBob * armsWalkAmount;
        this.rarm.rotationPointY -= bodyWalkBob * armsWalkAmount;
        this.walk(lleg, walkSpeed, walkDegree * 0.4F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(lleg2, walkSpeed, walkDegree * 0.3F, false, 1.5F, -0.1F, limbSwing, limbSwingAmount);
        this.walk(lfoot, walkSpeed, walkDegree * 0.8F, false, -1.5F, 0.4F, limbSwing, limbSwingAmount);
        lleg.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, walkDegree * 10, true));
        this.walk(rleg, walkSpeed, walkDegree * 0.4F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(rleg2, walkSpeed, walkDegree * 0.3F, true, 1.5F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(rfoot, walkSpeed, walkDegree * 0.8F, true, -1.5F, -0.4F, limbSwing, limbSwingAmount);
        rleg.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, walkDegree * 10, false));
        this.walk(rarm, walkSpeed, walkDegree * 0.4F, false, 1F, -0.6F, limbSwing, limbSwingAmount * armsWalkAmount);
        this.walk(rhand, walkSpeed, walkDegree * 0.4F, false, 2.5F, 0.2F, limbSwing, limbSwingAmount * armsWalkAmount);
        rarm.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, walkDegree * 10, true)) * armsWalkAmount;
        rhand.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, 1.5F, walkDegree * 4, true)) * armsWalkAmount;
        this.walk(larm, walkSpeed, walkDegree * 0.4F, true, 1F, 0.6F, limbSwing, limbSwingAmount * armsWalkAmount);
        this.walk(lhand, walkSpeed, walkDegree * 0.4F, true, 2.5F, -0.2F, limbSwing, limbSwingAmount * armsWalkAmount);
        larm.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, walkDegree * 10, false)) * armsWalkAmount;
        lhand.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, 1.5F, walkDegree * 4, false)) * armsWalkAmount;
        float yawAmount = netHeadYaw / 57.295776F;
        float pitchAmount = headPitch / 57.295776F;
        this.neck.rotateAngleX += pitchAmount * 0.5F;
        this.head.rotateAngleX += pitchAmount * 0.5F;
        this.neck.rotateAngleY += yawAmount * 0.5F;
        this.head.rotateAngleY += yawAmount * 0.5F;
    }

    private float walkValue(float limbSwing, float limbSwingAmount, float speed, float offset, float degree, boolean inverse) {
        return (float) ((Math.cos(limbSwing * speed + offset) * degree * limbSwingAmount) * (inverse ? -1 : 1));
    }

    public void translateToMouth(PoseStack matrixStackIn) {
        body.translateAndRotate(matrixStackIn);
        chest.translateAndRotate(matrixStackIn);
        neck.translateAndRotate(matrixStackIn);
        head.translateAndRotate(matrixStackIn);
    }

    private float articulateLegs(LegSolverQuadruped legs, float armsWalkAmount, float partialTick) {
        float heightBackLeft = legs.backLeft.getHeight(partialTick);
        float heightBackRight = legs.backRight.getHeight(partialTick);
        float heightFrontLeft = legs.frontLeft.getHeight(partialTick);
        float heightFrontRight = legs.frontRight.getHeight(partialTick);
        float max = Math.max(Math.max(heightBackLeft, heightBackRight), armsWalkAmount * Math.max(heightFrontLeft, heightFrontRight)) * 0.8F;
        body.rotationPointY += max * 16;
        rarm.rotationPointY += (heightFrontRight - max) * armsWalkAmount * 16;
        larm.rotationPointY += (heightFrontLeft - max) * armsWalkAmount * 16;
        rleg.rotationPointY += (heightBackRight - max) * 16;
        lleg.rotationPointY += (heightBackLeft - max) * 16;
        return max * 16;
    }
}