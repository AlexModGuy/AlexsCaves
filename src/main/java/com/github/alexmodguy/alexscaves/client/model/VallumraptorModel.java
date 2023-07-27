package com.github.alexmodguy.alexscaves.client.model;

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
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

public class VallumraptorModel extends AdvancedEntityModel<VallumraptorEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox lfoot;
    private final AdvancedModelBox lclaw;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rfoot;
    private final AdvancedModelBox rclaw;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox lhand;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox rhand;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox headquill;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tailTip;
    private final AdvancedModelBox tailQuill;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox lquill;
    private final AdvancedModelBox rquill;
    private final ModelAnimator animator;

    private float alpha = 1.0F;

    public VallumraptorModel() {
        texWidth = 64;
        texHeight = 64;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 10.5F, -1.0F);
        body.setTextureOffset(0, 0).addBox(-3.5F, -3.5F, -6.0F, 7.0F, 7.0F, 12.0F, 0.0F, false);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(3.0F, 0.5F, 2.5F);
        body.addChild(lleg);
        lleg.setTextureOffset(34, 14).addBox(-1.5F, -2.0F, -3.5F, 4.0F, 8.0F, 5.0F, 0.0F, true);

        lleg2 = new AdvancedModelBox(this);
        lleg2.setRotationPoint(0.5F, 4.5F, 1.0F);
        lleg.addChild(lleg2);
        lleg2.setTextureOffset(0, 48).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 9.0F, 2.0F, 0.0F, true);

        lfoot = new AdvancedModelBox(this);
        lfoot.setRotationPoint(0.0F, 8.5F, 0.5F);
        lleg2.addChild(lfoot);
        lfoot.setTextureOffset(20, 0).addBox(-2.5F, 0.0F, -5.0F, 5.0F, 0.0F, 6.0F, 0.0F, true);

        lclaw = new AdvancedModelBox(this);
        lclaw.setRotationPoint(-2.0F, 0.0F, -3.0F);
        lfoot.addChild(lclaw);
        lclaw.setTextureOffset(21, 34).addBox(0.0F, -5.0F, -4.0F, 0.0F, 5.0F, 5.0F, 0.0F, true);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-3.0F, 0.5F, 2.5F);
        body.addChild(rleg);
        rleg.setTextureOffset(34, 14).addBox(-2.5F, -2.0F, -3.5F, 4.0F, 8.0F, 5.0F, 0.0F, false);

        rleg2 = new AdvancedModelBox(this);
        rleg2.setRotationPoint(-0.5F, 4.75F, 1.0F);
        rleg.addChild(rleg2);
        rleg2.setTextureOffset(0, 48).addBox(-1.0F, -0.75F, -0.5F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        rfoot = new AdvancedModelBox(this);
        rfoot.setRotationPoint(0.0F, 8.25F, 0.5F);
        rleg2.addChild(rfoot);
        rfoot.setTextureOffset(20, 0).addBox(-2.5F, 0.0F, -5.0F, 5.0F, 0.0F, 6.0F, 0.0F, false);

        rclaw = new AdvancedModelBox(this);
        rclaw.setRotationPoint(2.0F, 0.0F, -3.0F);
        rfoot.addChild(rclaw);
        rclaw.setTextureOffset(21, 34).addBox(0.0F, -5.0F, -4.0F, 0.0F, 5.0F, 5.0F, 0.0F, false);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(3.0F, 2.5F, -3.0F);
        body.addChild(larm);
        larm.setTextureOffset(8, 48).addBox(-0.5F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, true);
        larm.setTextureOffset(44, 0).addBox(-0.5F, 2.0F, -4.0F, 2.0F, 3.0F, 3.0F, 0.0F, true);

        lhand = new AdvancedModelBox(this);
        lhand.setRotationPoint(1.5F, 3.5F, -4.0F);
        larm.addChild(lhand);
        lhand.setTextureOffset(0, 28).addBox(-4.0F, -1.5F, -3.0F, 4.0F, 3.0F, 3.0F, 0.0F, true);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-3.0F, 2.5F, -3.0F);
        body.addChild(rarm);
        rarm.setTextureOffset(8, 48).addBox(-1.5F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
        rarm.setTextureOffset(44, 0).addBox(-1.5F, 2.0F, -4.0F, 2.0F, 3.0F, 3.0F, 0.0F, false);

        rhand = new AdvancedModelBox(this);
        rhand.setRotationPoint(-1.5F, 3.5F, -4.0F);
        rarm.addChild(rhand);
        rhand.setTextureOffset(0, 28).addBox(0.0F, -1.5F, -3.0F, 4.0F, 3.0F, 3.0F, 0.0F, false);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(0.0F, -1.5F, -5.0F);
        body.addChild(neck);
        neck.setTextureOffset(47, 22).addBox(-1.5F, -8.0F, -3.0F, 3.0F, 9.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -8.0F, -1.0F);
        neck.addChild(head);
        head.setTextureOffset(0, 0).addBox(-1.0F, -4.0F, -7.0F, 2.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(50, 8).addBox(-1.0F, -6.0F, -8.0F, 2.0F, 6.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(-9, 39).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 0.0F, 9.0F, 0.0F, false);
        head.setTextureOffset(46, 56).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(26, 54).addBox(-2.0F, 0.0F, -8.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 2.0F, -1.5F);
        head.addChild(jaw);
        jaw.setTextureOffset(30, 4).addBox(-1.5F, 0.0F, -6.0F, 3.0F, 2.0F, 6.0F, 0.0F, false);
        jaw.setTextureOffset(2, 57).addBox(-1.5F, -1.0F, -6.0F, 3.0F, 1.0F, 6.0F, 0.0F, false);

        headquill = new AdvancedModelBox(this);
        headquill.setRotationPoint(0.0F, -8.0F, 2.0F);
        neck.addChild(headquill);
        headquill.setTextureOffset(46, 27).addBox(0.0F, -5.0F, -5.0F, 0.0F, 14.0F, 9.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -2.25F, 5.0F);
        body.addChild(tail);
        tail.setTextureOffset(16, 19).addBox(-1.5F, -1.25F, 1.0F, 3.0F, 3.0F, 12.0F, 0.0F, false);

        tailTip = new AdvancedModelBox(this);
        tailTip.setRotationPoint(0.0F, 0.25F, 13.0F);
        tail.addChild(tailTip);
        tailTip.setTextureOffset(0, 5).addBox(0.0F, -7.5F, 0.0F, 0.0F, 9.0F, 14.0F, 0.0F, false);

        tailQuill = new AdvancedModelBox(this);
        tailQuill.setRotationPoint(0.0F, -1.25F, 7.0F);
        tail.addChild(tailQuill);
        tailQuill.setTextureOffset(18, 36).addBox(0.0F, -5.0F, -6.0F, 0.0F, 6.0F, 12.0F, 0.0F, false);

        lquill = new AdvancedModelBox(this);
        lquill.setRotationPoint(0.5F, 5.0F, -0.5F);
        larm.addChild(lquill);
        lquill.setTextureOffset(16, 47).addBox(0.0F, 0.0F, -3.5F, 0.0F, 3.0F, 7.0F, 0.0F, true);

        rquill = new AdvancedModelBox(this);
        rquill.setRotationPoint(-0.5F, 5.0F, -0.5F);
        rarm.addChild(rquill);
        rquill.setTextureOffset(16, 47).addBox(0.0F, 0.0F, -3.5F, 0.0F, 3.0F, 7.0F, 0.0F, true);

        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, tail, tailTip, lleg2, rleg2, lleg, rleg, neck, head, jaw, lhand, rhand, lfoot, lclaw, rfoot, rclaw, larm, rarm, headquill, tailQuill, lquill, rquill);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alphaIn) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alphaIn * this.alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alphaIn * this.alpha);
            });
            matrixStackIn.popPose();
        }
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(VallumraptorEntity.ANIMATION_CALL_1);
        animator.startKeyframe(5);
        animator.rotate(jaw, (float) Math.toRadians(30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);
        animator.setAnimation(VallumraptorEntity.ANIMATION_CALL_2);
        animator.startKeyframe(4);
        animatePose(0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(neck, 0, (float) Math.toRadians(20), 0);
        animatePose(0);
        animator.endKeyframe();
        animator.startKeyframe(8);
        animator.rotate(neck, 0, (float) Math.toRadians(-20), 0);
        animatePose(0);
        animator.endKeyframe();
        animator.resetKeyframe(4);
        animator.setAnimation(VallumraptorEntity.ANIMATION_SCRATCH_1);
        animator.startKeyframe(5);
        animatePose(1);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(1);
        animator.rotate(rarm, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(1);
        animator.rotate(rarm, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(1);
        animator.rotate(rarm, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(1);
        animator.rotate(rarm, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(1);
        animator.rotate(rarm, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(1);
        animator.rotate(rarm, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(VallumraptorEntity.ANIMATION_SCRATCH_2);
        animator.startKeyframe(5);
        animatePose(2);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(2);
        animator.rotate(larm, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(2);
        animator.rotate(larm, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(2);
        animator.rotate(larm, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(2);
        animator.rotate(larm, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(2);
        animator.rotate(larm, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(2);
        animatePose(2);
        animator.rotate(larm, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(VallumraptorEntity.ANIMATION_STARTLEAP);
        animator.startKeyframe(5);
        animator.move(head, 0, -0.5F, 1);
        animator.move(body, 0, 2.5F, 1);
        animator.move(lfoot, 0, -0.35F, 0);
        animator.move(rfoot, 0, -0.35F, 0);
        animator.move(lleg, 0, -0.35F, 0);
        animator.move(rleg, 0, -0.35F, 0);
        animator.rotate(body, (float) Math.toRadians(30), 0, 0);
        animator.rotate(lleg, (float) Math.toRadians(-20), (float) Math.toRadians(-30), 0);
        animator.rotate(rleg, (float) Math.toRadians(-20), (float) Math.toRadians(30), 0);
        animator.rotate(lleg2, (float) Math.toRadians(-40), 0, 0);
        animator.rotate(rleg2, (float) Math.toRadians(-40), 0, 0);
        animator.rotate(lfoot, (float) Math.toRadians(30), (float) Math.toRadians(5), (float) Math.toRadians(25));
        animator.rotate(rfoot, (float) Math.toRadians(30), (float) Math.toRadians(-5), (float) Math.toRadians(-25));
        animator.rotate(larm, (float) Math.toRadians(-40), (float) Math.toRadians(-20), (float) Math.toRadians(-50));
        animator.rotate(rarm, (float) Math.toRadians(-40), (float) Math.toRadians(20), (float) Math.toRadians(50));
        animator.rotate(head, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-30), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(10);
        animator.resetKeyframe(3);
        animator.setAnimation(VallumraptorEntity.ANIMATION_MELEE_BITE);
        animator.startKeyframe(3);
        animator.move(head, 0F, -1, 0);
        animator.rotate(neck, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(30), 0, (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(3);
        animator.move(head, 0F, -0.5F, 0.5F);
        animator.rotate(neck, (float) Math.toRadians(50), (float) Math.toRadians(10), 0);
        animator.rotate(head, (float) Math.toRadians(-58), 0, (float) Math.toRadians(-10));
        animator.rotate(jaw, (float) Math.toRadians(50), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(7);
        animator.setAnimation(VallumraptorEntity.ANIMATION_MELEE_SLASH_1);
        animator.startKeyframe(5);
        animatePose(3);
        animator.move(rhand, 0F, 0F, 1F);
        animator.rotate(larm, (float) Math.toRadians(10), 0, 0);
        animator.rotate(lhand, 0, (float) Math.toRadians(10), 0);
        animator.rotate(rarm, (float) Math.toRadians(-60), (float) Math.toRadians(60), 0);
        animator.rotate(rhand, 0, (float) Math.toRadians(40), 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float) Math.toRadians(-30), 0);
        animator.move(rhand, 0F, 0F, 1F);
        animator.rotate(larm, (float) Math.toRadians(10), 0, 0);
        animator.rotate(lhand, 0, (float) Math.toRadians(10), 0);
        animator.rotate(rarm, (float) Math.toRadians(-20), (float) Math.toRadians(-20), 0);
        animator.rotate(rhand, 0, (float) Math.toRadians(40), 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(VallumraptorEntity.ANIMATION_MELEE_SLASH_2);
        animator.startKeyframe(5);
        animatePose(3);
        animator.move(lhand, 0F, 0F, 1F);
        animator.rotate(rarm, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(rhand, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(larm, (float) Math.toRadians(-60), (float) Math.toRadians(-60), 0);
        animator.rotate(lhand, 0, (float) Math.toRadians(-40), 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float) Math.toRadians(30), 0);
        animator.move(lhand, 0F, 0F, 1F);
        animator.rotate(rarm, (float) Math.toRadians(10), 0, 0);
        animator.rotate(rhand, 0, (float) Math.toRadians(10), 0);
        animator.rotate(larm, (float) Math.toRadians(20), (float) Math.toRadians(20), 0);
        animator.rotate(lhand, 0, (float) Math.toRadians(-40), 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(VallumraptorEntity.ANIMATION_GRAB);
        animator.startKeyframe(5);
        animatePose(5);
        animator.rotate(head, 0, (float) Math.toRadians(-20), (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.setStaticKeyframe(4);
        animator.startKeyframe(5);
        animatePose(5);
        animator.rotate(head, 0, (float) Math.toRadians(20), (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.startKeyframe(3);
        animatePose(5);
        animator.rotate(rarm, (float) Math.toRadians(-40), (float) Math.toRadians(10), 0);
        animator.rotate(rhand, 0, (float) Math.toRadians(40), 0);
        animator.move(rarm, 0, 0, -3);
        animator.move(rhand, 0F, 0F, 1F);
        animator.rotate(larm, (float) Math.toRadians(-40), (float) Math.toRadians(-10), 0);
        animator.rotate(lhand, 0, (float) Math.toRadians(-40), 0);
        animator.move(larm, 0, 0, -3);
        animator.move(lhand, 0F, 0F, 1F);
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(5);

    }


    private void animatePose(int pose) {
        switch (pose) {
            case 0:
                animator.rotate(jaw, (float) Math.toRadians(65), 0, 0);
                animator.rotate(head, (float) Math.toRadians(-30), 0, 0);
                animator.rotate(neck, (float) Math.toRadians(-10), 0, 0);
                animator.rotate(body, (float) Math.toRadians(-10), 0, 0);
                animator.rotate(lleg, (float) Math.toRadians(10), 0, 0);
                animator.rotate(rleg, (float) Math.toRadians(10), 0, 0);
                animator.move(head, 0, -1, 1);
                animator.move(jaw, 0, 1, 0);
                animator.move(lleg, 0, -0.5F, 0);
                animator.move(rleg, 0, -0.5F, 0);
                break;
            case 1:
                animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
                animator.rotate(neck, (float) Math.toRadians(40), (float) Math.toRadians(10), 0);
                animator.rotate(larm, (float) Math.toRadians(20), 0, 0);
                animator.rotate(lhand, (float) Math.toRadians(20), 0, 0);
                animator.rotate(rarm, (float) Math.toRadians(-90), (float) Math.toRadians(30), 0);
                animator.rotate(rhand, (float) Math.toRadians(-10), (float) Math.toRadians(10), 0);
                animator.move(rarm, 0, -1, -1);
                animator.move(lhand, 0, 0, 1);
                animator.move(rhand, 0, 0, 1);
                break;
            case 2:
                animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
                animator.rotate(neck, (float) Math.toRadians(40), (float) Math.toRadians(-10), 0);
                animator.rotate(larm, (float) Math.toRadians(20), 0, 0);
                animator.rotate(rhand, (float) Math.toRadians(20), 0, 0);
                animator.rotate(larm, (float) Math.toRadians(-90), (float) Math.toRadians(-30), 0);
                animator.rotate(lhand, (float) Math.toRadians(-10), (float) Math.toRadians(-10), 0);
                animator.move(larm, 0, -1, -1);
                animator.move(rhand, 0, 0, 1);
                animator.move(lhand, 0, 0, 1);
                break;
            case 3:
                animator.rotate(body, 0, (float) Math.toRadians(10), (float) Math.toRadians(10));
                animator.rotate(lleg, 0, 0, (float) Math.toRadians(-10));
                animator.rotate(rleg, 0, 0, (float) Math.toRadians(-10));
                animator.move(rleg, 0, 0.25F, 0);
                animator.move(lleg, 0, -0.75F, 0);
                break;
            case 4:
                animator.rotate(body, 0, (float) Math.toRadians(-10), (float) Math.toRadians(-10));
                animator.rotate(lleg, 0, 0, (float) Math.toRadians(10));
                animator.rotate(rleg, 0, 0, (float) Math.toRadians(10));
                animator.move(lleg, 0, 0.25F, 0);
                animator.move(rleg, 0, -0.75F, 0);
                break;
            case 5:
                animator.rotate(head, (float) Math.toRadians(20), 0, 0);
                animator.rotate(neck, (float) Math.toRadians(-10), 0, 0);
                animator.rotate(body, (float) Math.toRadians(10), 0, 0);
                animator.rotate(lleg, (float) Math.toRadians(-10), 0, 0);
                animator.rotate(rleg, (float) Math.toRadians(-10), 0, 0);
                animator.move(head, 0, -1, 0);
                animator.move(body, 0, 0, 6);
                animator.move(lleg, 0, 0.35F, 0);
                animator.move(rleg, 0, 0.35F, 0);
                break;
        }
    }

    @Override
    public void setupAnim(VallumraptorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float walkSpeed = 0.5F;
        float walkDegree = 0.85F;
        float sprintSpeed = 0.5F;
        float sprintDegree = 0.5F;
        float partialTick = ageInTicks - entity.tickCount;
        float sprintProgress = entity.getRunProgress(partialTick);
        float walkProgress = sprintProgress - 1F;
        float jumpProgress = entity.getLeapProgress(partialTick);
        float walkAmount = limbSwingAmount * walkProgress * (1 - jumpProgress);
        float sprintAmount = limbSwingAmount * sprintProgress * (1 - jumpProgress);
        float stillAmount = 1 - limbSwingAmount;
        float relaxedAmount = entity.getRelaxedProgress(partialTick);
        float sitAmount = Math.max(entity.getSitProgress(partialTick), relaxedAmount);
        float puzzleRot = entity.getPuzzledHeadRot(partialTick);
        float buryEggsAmount = entity.getBuryEggsProgress(partialTick);
        float puzzleRotRad = (float) Math.toRadians(puzzleRot);
        float puzzleRotPoint = puzzleRot * 0.05F;
        float yaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTick;
        float tailYaw = Mth.wrapDegrees(entity.getTailYaw(partialTick) - yaw) / 57.295776F;
        float danceAmount = entity.getDanceProgress(partialTick);
        float danceSpeed = 0.5F;
        if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            setupAnimForAnimation(entity, entity.getAnimation(), limbSwing, limbSwingAmount, ageInTicks);
        }
        if (entity.getAnimation() != VallumraptorEntity.ANIMATION_CALL_2) {
            progressPositionPrev(head, walkAmount, 0, 1, -1, 1F);
            progressPositionPrev(head, sprintAmount, 0, -2, 2, 1F);
        }
        if (buryEggsAmount > 0.0F) {
            limbSwing = ageInTicks;
            walkAmount = buryEggsAmount * 0.5F;
            this.body.swing(0.25F, 0.4F, false, 0F, 0F, ageInTicks, buryEggsAmount);
            this.neck.swing(0.25F, 0.4F, true, -1F, 0F, ageInTicks, buryEggsAmount);
        }
        progressPositionPrev(body, jumpProgress, 0, 0, 2, 1F);
        progressPositionPrev(larm, jumpProgress, 0, 0, -2, 1F);
        progressPositionPrev(rarm, jumpProgress, 0, 0, -2, 1F);
        progressPositionPrev(neck, jumpProgress, 0, -1, 0, 1F);
        progressRotationPrev(body, jumpProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(tail, jumpProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(tailTip, jumpProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(rarm, jumpProgress, (float) Math.toRadians(-40), (float) Math.toRadians(50), (float) Math.toRadians(50), 1F);
        progressRotationPrev(larm, jumpProgress, (float) Math.toRadians(-40), (float) Math.toRadians(-50), (float) Math.toRadians(-50), 1F);
        progressRotationPrev(neck, jumpProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(jaw, jumpProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(rleg, jumpProgress, (float) Math.toRadians(-10), (float) Math.toRadians(10), (float) Math.toRadians(10), 1F);
        progressRotationPrev(lleg, jumpProgress, (float) Math.toRadians(-10), (float) Math.toRadians(-10), (float) Math.toRadians(-10), 1F);
        progressRotationPrev(rleg2, jumpProgress, (float) Math.toRadians(-30), 0, 0, 1F);
        progressRotationPrev(lleg2, jumpProgress, (float) Math.toRadians(-30), 0, 0, 1F);
        progressRotationPrev(rfoot, jumpProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(lfoot, jumpProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(rarm, danceAmount, (float) Math.toRadians(-50), (float) Math.toRadians(50), 0, 1F);
        progressRotationPrev(larm, danceAmount, (float) Math.toRadians(-50), (float) Math.toRadians(-50), 0, 1F);
        progressPositionPrev(body, sitAmount, 0, 6, -1F, 1F);
        progressPositionPrev(rarm, sitAmount, 0, -2, 1, 1F);
        progressPositionPrev(larm, sitAmount, 0, -2, 1, 1F);
        progressPositionPrev(rleg, sitAmount, 0, -1.5F, 5, 1F);
        progressPositionPrev(lleg, sitAmount, 0, -1.5F, 5, 1F);
        progressRotationPrev(rleg, sitAmount, (float) Math.toRadians(-20), (float) Math.toRadians(25), 0, 1F);
        progressRotationPrev(rleg2, sitAmount, (float) Math.toRadians(-50), 0, 0, 1F);
        progressRotationPrev(rfoot, sitAmount, (float) Math.toRadians(70), 0, 0, 1F);
        progressRotationPrev(lleg, sitAmount, (float) Math.toRadians(-20), (float) Math.toRadians(-25), 0, 1F);
        progressRotationPrev(lleg2, sitAmount, (float) Math.toRadians(-50), 0, 0, 1F);
        progressRotationPrev(lfoot, sitAmount, (float) Math.toRadians(70), 0, 0, 1F);
        progressRotationPrev(tail, sitAmount, (float) Math.toRadians(-10), 0, 0, 1F);
        progressPositionPrev(head, relaxedAmount, 1, -1, 3, 1F);
        progressRotationPrev(neck, relaxedAmount, (float) Math.toRadians(120), (float) Math.toRadians(30), 0, 1F);
        progressRotationPrev(head, relaxedAmount, (float) Math.toRadians(-120), (float) Math.toRadians(30), (float) Math.toRadians(-30), 1F);
        progressRotationPrev(tail, relaxedAmount, 0, (float) Math.toRadians(-30), 0, 1F);
        progressRotationPrev(tailTip, relaxedAmount, (float) Math.toRadians(-10), (float) Math.toRadians(-30), 0, 1F);

        this.swing(tail, 0.1F, 0.2F, false, 2F, 0F, ageInTicks, stillAmount);
        this.swing(tailTip, 0.1F, 0.2F, false, 1F, 0F, ageInTicks, stillAmount);
        this.walk(rarm, 0.1F, 0.1F, false, 3F, 0.1F, ageInTicks, 1);
        this.walk(larm, 0.1F, 0.1F, false, 3F, 0.1F, ageInTicks, 1);
        this.swing(rhand, 0.1F, 0.1F, true, 3F, 0.1F, ageInTicks, 1);
        this.swing(lhand, 0.1F, 0.1F, false, 3F, 0.1F, ageInTicks, 1);
        this.walk(neck, 0.1F, 0.05F, false, 4F, 0.1F, ageInTicks, 1);
        this.walk(head, 0.1F, 0.05F, true, 4F, 0.1F, ageInTicks, 1);

        this.walk(body, walkSpeed * 2F, walkDegree * 0.05F, false, -2, 0.0F, limbSwing, walkAmount);
        this.bob(body, -walkSpeed, walkDegree * -4, true, limbSwing, walkAmount);
        this.swing(tail, walkSpeed * 1, walkDegree * 0.5F, false, 2, 0.0F, limbSwing, walkAmount);
        this.swing(tailTip, walkSpeed * 1, walkDegree * 0.5F, false, 1, 0.0F, limbSwing, walkAmount);
        this.walk(lleg, walkSpeed, walkDegree * 1, true, 0, 0.2F, limbSwing, walkAmount);
        this.walk(lleg2, walkSpeed, walkDegree * 0.5F, true, -1F, -0.2F, limbSwing, walkAmount);
        this.swing(lleg2, walkSpeed, walkDegree * -0.5F, true, -1F, 0F, limbSwing, walkAmount);
        this.walk(lfoot, walkSpeed, walkDegree * -1F, true, -1.5F, 0.0F, limbSwing, walkAmount);
        this.swing(lfoot, walkSpeed, walkDegree * 0.5F, false, 0, 0F, limbSwing, walkAmount);
        lfoot.rotationPointY -= Math.abs((float) (Math.cos(limbSwing * walkSpeed - 1.5F) * walkDegree * 1.5F * walkAmount));
        this.walk(rleg, walkSpeed, walkDegree * 1, false, 0, 0.2F, limbSwing, walkAmount);
        this.walk(rleg2, walkSpeed, walkDegree * 0.5F, false, -1F, -0.2F, limbSwing, walkAmount);
        this.swing(rleg2, walkSpeed, walkDegree * -0.5F, false, -1F, 0F, limbSwing, walkAmount);
        this.walk(rfoot, walkSpeed, walkDegree * -1F, false, -1.5F, 0.0F, limbSwing, walkAmount);
        this.swing(rfoot, walkSpeed, walkDegree * 0.5F, false, 0, 0.0F, limbSwing, walkAmount);
        rfoot.rotationPointY -= Math.abs((float) (Math.cos(limbSwing * walkSpeed - 1.5F) * walkDegree * 1.5F * walkAmount));
        this.walk(neck, walkSpeed * 2F, walkDegree * 0.3F, false, -1F, -0.5F, limbSwing, walkAmount);
        this.walk(head, walkSpeed * 2F, walkDegree * 0.3F, true, -1F, -0.5F, limbSwing, walkAmount);
        this.walk(larm, walkSpeed * 2F, walkDegree * 0.3F, false, 1F, -0.1F, limbSwing, walkAmount);
        this.walk(rarm, walkSpeed * 2F, walkDegree * 0.3F, false, 1F, -0.1F, limbSwing, walkAmount);
        this.walk(body, sprintSpeed * 2F, sprintDegree * 0.05F, false, -2, 0.1F, limbSwing, sprintAmount);
        this.bob(body, 2 * sprintSpeed, sprintDegree * 3, false, limbSwing, sprintAmount);
        this.walk(lleg, sprintSpeed, sprintDegree * 1, true, 0, 0.2F, limbSwing, sprintAmount);
        this.walk(lleg2, sprintSpeed, sprintDegree * 0.9F, true, -1F, -0.2F, limbSwing, sprintAmount);
        this.swing(lleg2, sprintSpeed, sprintDegree * -0.5F, true, -1F, 0F, limbSwing, sprintAmount);
        this.walk(lfoot, sprintSpeed, sprintDegree * -1F, true, -1.5F, 0.0F, limbSwing, sprintAmount);
        this.swing(lfoot, sprintSpeed, sprintDegree * 1F, true, 0, 0F, limbSwing, sprintAmount);
        lfoot.rotationPointY -= Math.abs((float) (Math.cos(limbSwing * sprintSpeed - 1.5F) * sprintDegree * 2.5F * sprintAmount));
        this.walk(rleg, sprintSpeed, sprintDegree * 1, false, 0, 0.2F, limbSwing, sprintAmount);
        this.walk(rleg2, sprintSpeed, sprintDegree * 0.9F, false, -1F, -0.2F, limbSwing, sprintAmount);
        this.swing(rleg2, sprintSpeed, sprintDegree * -0.5F, false, -1F, 0F, limbSwing, sprintAmount);
        this.walk(rfoot, sprintSpeed, sprintDegree * -1F, false, -1.5F, 0.0F, limbSwing, sprintAmount);
        this.swing(rfoot, sprintSpeed, sprintDegree * -1F, false, 0, 0.0F, limbSwing, sprintAmount);
        rfoot.rotationPointY -= Math.abs((float) (Math.cos(limbSwing * sprintSpeed - 1.5F) * sprintDegree * 2.5F * sprintAmount));
        this.walk(tail, sprintSpeed * 2F, sprintDegree * 0.3F, false, 1F, -0.1F, limbSwing, sprintAmount);
        this.swing(tail, sprintSpeed * 1, sprintDegree * 0.5F, false, 2, 0.0F, limbSwing, sprintAmount);
        this.swing(tailTip, sprintSpeed * 1, sprintDegree * 0.5F, false, 1, 0.0F, limbSwing, sprintAmount);
        this.walk(neck, sprintSpeed * 1.5F, sprintDegree * 0.1F, false, -2, 1F, limbSwing, sprintAmount);
        this.walk(head, sprintSpeed * 1.5F, sprintDegree * -0.1F, false, -1.5F, -0.9F, limbSwing, sprintAmount);
        larm.rotationPointZ -= Math.abs((float) (Math.cos(limbSwing * sprintSpeed + 1.5F) * sprintDegree * 2 * sprintAmount)) + sprintAmount;
        this.walk(larm, sprintSpeed * 2F, sprintDegree * 0.3F, false, 1F, -0.1F, limbSwing, sprintAmount);
        this.swing(lhand, sprintSpeed * 2F, sprintDegree * 0.5F, false, 1F, 0.1F, limbSwing, sprintAmount);
        rarm.rotationPointZ -= Math.abs((float) (Math.cos(limbSwing * sprintSpeed + 1.5F) * sprintDegree * 2 * sprintAmount)) + sprintAmount;
        this.walk(rarm, sprintSpeed * 2F, sprintDegree * 0.3F, false, 1F, -0.1F, limbSwing, sprintAmount);
        this.swing(rhand, sprintSpeed * 2F, sprintDegree * 0.5F, true, 1F, 0.1F, limbSwing, sprintAmount);
        this.swing(neck, sprintSpeed * 1.5F, sprintDegree * 0.2F, false, 2F, 0, limbSwing, sprintAmount);
        this.swing(head, sprintSpeed * 1.5F, sprintDegree * -0.2F, false, 2F, 0F, limbSwing, sprintAmount);
        this.swing(body, sprintSpeed * 1, sprintDegree * 0.2F, false, 0, 0, limbSwing, sprintAmount);
        this.head.rotationPointX += Mth.clamp(puzzleRotPoint, -1.5F, 1.5F);
        this.head.rotateAngleZ += puzzleRotRad;
        this.neck.rotateAngleZ += puzzleRotRad * -0.3F;
        this.head.rotateAngleX += Math.abs(puzzleRotRad * 0.3F);
        this.neck.rotateAngleX -= Math.abs(puzzleRotRad * 0.3F);
        tail.rotateAngleY += tailYaw * 0.8F;
        tailTip.rotateAngleY += tailYaw * 0.2F;
        this.faceTarget(netHeadYaw, headPitch, 1, neck, head);
        this.swing(body, danceSpeed, 0.1F, false, 1, 0, ageInTicks, danceAmount);
        this.flap(rarm, danceSpeed, 0.5F, false, 0, 0, ageInTicks, danceAmount);
        this.flap(larm, danceSpeed, 0.5F, false, 0, 0, ageInTicks, danceAmount);
        this.walk(rhand, danceSpeed, 0.5F, false, 1, 0, ageInTicks, danceAmount);
        this.walk(lhand, danceSpeed, 0.5F, true, 1, 0, ageInTicks, danceAmount);
        this.walk(neck, danceSpeed, 0.25F, false, 1, 0, ageInTicks, danceAmount);
        this.walk(head, danceSpeed, 0.25F, false, 1, 0, ageInTicks, danceAmount);
        this.walk(tail, danceSpeed, 0.15F, false, 2, 0, ageInTicks, danceAmount);
        this.bob(rarm, danceSpeed, 1, false, ageInTicks, danceAmount);
        this.bob(larm, danceSpeed, 1, false, ageInTicks, danceAmount);
    }

    private void setupAnimForAnimation(VallumraptorEntity entity, Animation animation, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float partialTick = ageInTicks - entity.tickCount;
        if (animation == VallumraptorEntity.ANIMATION_SHAKE) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, animation, partialTick, 0);
            progressRotationPrev(neck, animationIntensity, (float) Math.toRadians(20), 0, 0, 1F);
            progressRotationPrev(head, animationIntensity, (float) Math.toRadians(-20), 0, 0, 1F);
            progressPositionPrev(head, animationIntensity, 0, -0.5F, 0, 1F);
            this.swing(body, 0.5F, 0.2F, false, 0F, 0F, ageInTicks, animationIntensity);
            this.flap(body, 0.5F, 0.2F, false, 0F, 0F, ageInTicks, animationIntensity);
            this.swing(rleg, 0.5F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.flap(rleg, 0.5F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            rleg.rotationPointY -= (float) (Math.cos(ageInTicks * 0.5F) * -0.5F * animationIntensity) + animationIntensity * 0.2F;
            lleg.rotationPointY -= (float) (Math.cos(ageInTicks * 0.5F) * 0.5F * animationIntensity) + animationIntensity * 0.2F;
            this.swing(lleg, 0.5F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.flap(lleg, 0.5F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.flap(neck, 0.5F, 0.5F, false, 1F, 0F, ageInTicks, animationIntensity);
            this.flap(headquill, 0.5F, 0.5F, false, -1F, 0F, ageInTicks, animationIntensity);
            this.swing(neck, 0.5F, 0.5F, false, 2F, 0F, ageInTicks, animationIntensity);
            this.flap(head, 0.5F, 0.15F, false, 2F, 0F, ageInTicks, animationIntensity);
            this.swing(head, 0.5F, 0.5F, false, 2F, 0F, ageInTicks, animationIntensity);
            this.swing(tail, 0.5F, 0.5F, true, 2F, 0F, ageInTicks, animationIntensity);
            this.flap(tail, 0.5F, 0.5F, true, 2F, 0F, ageInTicks, animationIntensity);
            this.flap(rarm, 0.5F, 0.5F, false, -1F, 0F, ageInTicks, animationIntensity);
            this.flap(larm, 0.5F, 0.5F, false, -1F, 0F, ageInTicks, animationIntensity);
        }
    }

    public void translateToHand(PoseStack matrixStackIn, boolean left) {
        body.translateAndRotate(matrixStackIn);
        if (left) {
            larm.translateAndRotate(matrixStackIn);
        } else {
            rarm.translateAndRotate(matrixStackIn);
        }
    }
}