package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolver;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class TremorsaurusModel extends AdvancedEntityModel<TremorsaurusEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox rfoot;
    private final AdvancedModelBox rclaw1;
    private final AdvancedModelBox rclaw2;
    private final AdvancedModelBox rclaw3;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox lfoot;
    private final AdvancedModelBox lclaw1;
    private final AdvancedModelBox lclaw2;
    private final AdvancedModelBox lclaw3;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox glasses;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tailTip;
    private final ModelAnimator animator;

    public TremorsaurusModel() {
        texWidth = 256;
        texHeight = 256;
        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -17.0F, 0.0F);
        body.setTextureOffset(0, 0).addBox(-13.0F, -15.0F, -20.0F, 26.0F, 30.0F, 39.0F, 0.0F, false);
        body.setTextureOffset(91, 0).addBox(-1.0F, -18.0F, -9.0F, 2.0F, 3.0F, 5.0F, 0.0F, false);
        body.setTextureOffset(91, 0).addBox(-1.0F, -18.0F, -18.0F, 2.0F, 3.0F, 5.0F, 0.0F, false);
        body.setTextureOffset(91, 0).addBox(-1.0F, -18.0F, -2.0F, 2.0F, 3.0F, 5.0F, 0.0F, false);
        body.setTextureOffset(91, 0).addBox(-1.0F, -18.0F, 5.0F, 2.0F, 3.0F, 5.0F, 0.0F, false);
        body.setTextureOffset(91, 0).addBox(-1.0F, -18.0F, 12.0F, 2.0F, 3.0F, 5.0F, 0.0F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-13.0F, 11.0F, -13.5F);
        body.addChild(rarm);
        setRotateAngle(rarm, 0.0F, 0.0F, -1.2654F);
        rarm.setTextureOffset(16, 24).addBox(-8.0F, 0.0F, -1.5F, 8.0F, 3.0F, 3.0F, 0.0F, true);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(13.0F, 11.0F, -13.5F);
        body.addChild(larm);
        setRotateAngle(larm, 0.0F, 0.0F, 1.2654F);
        larm.setTextureOffset(16, 24).addBox(0.0F, 0.0F, -1.5F, 8.0F, 3.0F, 3.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-11.5F, 2.0F, 6.5F);
        body.addChild(rleg);
        rleg.setTextureOffset(61, 106).addBox(-6.5F, -2.0F, -9.5F, 13.0F, 26.0F, 19.0F, 0.0F, true);

        rleg2 = new AdvancedModelBox(this);
        rleg2.setRotationPoint(0.0F, 21.5F, 7.0F);
        rleg.addChild(rleg2);
        rleg2.setTextureOffset(0, 0).addBox(-3.5F, -1.5F, -3.5F, 7.0F, 15.0F, 9.0F, 0.0F, true);

        rfoot = new AdvancedModelBox(this);
        rfoot.setRotationPoint(0.5F, 13.5F, 1.0F);
        rleg2.addChild(rfoot);
        rfoot.setTextureOffset(125, 127).addBox(-6.0F, 0.0F, -12.5F, 11.0F, 4.0F, 17.0F, 0.0F, true);

        rclaw1 = new AdvancedModelBox(this);
        rclaw1.setRotationPoint(4.0F, 0.0F, -12.5F);
        rfoot.addChild(rclaw1);
        rclaw1.setTextureOffset(91, 8).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 4.0F, 4.0F, 0.0F, true);

        rclaw2 = new AdvancedModelBox(this);
        rclaw2.setRotationPoint(-0.5F, 0.0F, -12.5F);
        rfoot.addChild(rclaw2);
        rclaw2.setTextureOffset(91, 8).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 4.0F, 4.0F, 0.0F, true);

        rclaw3 = new AdvancedModelBox(this);
        rclaw3.setRotationPoint(-5.0F, 0.0F, -12.5F);
        rfoot.addChild(rclaw3);
        rclaw3.setTextureOffset(91, 8).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 4.0F, 4.0F, 0.0F, true);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(11.5F, 2.0F, 6.5F);
        body.addChild(lleg);
        lleg.setTextureOffset(61, 106).addBox(-6.5F, -2.0F, -9.5F, 13.0F, 26.0F, 19.0F, 0.0F, false);

        lleg2 = new AdvancedModelBox(this);
        lleg2.setRotationPoint(0.0F, 21.5F, 7.0F);
        lleg.addChild(lleg2);
        lleg2.setTextureOffset(0, 0).addBox(-3.5F, -1.5F, -3.5F, 7.0F, 15.0F, 9.0F, 0.0F, false);

        lfoot = new AdvancedModelBox(this);
        lfoot.setRotationPoint(-0.5F, 13.5F, 1.0F);
        lleg2.addChild(lfoot);
        lfoot.setTextureOffset(125, 127).addBox(-5.0F, 0.0F, -12.5F, 11.0F, 4.0F, 17.0F, 0.0F, false);

        lclaw1 = new AdvancedModelBox(this);
        lclaw1.setRotationPoint(-4.0F, 0.0F, -12.5F);
        lfoot.addChild(lclaw1);
        lclaw1.setTextureOffset(91, 8).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 4.0F, 4.0F, 0.0F, false);

        lclaw2 = new AdvancedModelBox(this);
        lclaw2.setRotationPoint(0.5F, 0.0F, -12.5F);
        lfoot.addChild(lclaw2);
        lclaw2.setTextureOffset(91, 8).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 4.0F, 4.0F, 0.0F, false);

        lclaw3 = new AdvancedModelBox(this);
        lclaw3.setRotationPoint(5.0F, 0.0F, -12.5F);
        lfoot.addChild(lclaw3);
        lclaw3.setTextureOffset(91, 8).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 4.0F, 4.0F, 0.0F, false);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(0.0F, -5.0F, -14.0F);
        body.addChild(neck);
        setRotateAngle(neck, 0.7854F, 0.0F, 0.0F);
        neck.setTextureOffset(44, 111).addBox(-1.0F, -19.0F, 3.0F, 2.0F, 5.0F, 3.0F, 0.0F, false);
        neck.setTextureOffset(0, 69).addBox(-1.0F, -26.0F, 1.0F, 2.0F, 5.0F, 5.0F, 0.0F, false);
        neck.setTextureOffset(91, 0).addBox(-1.0F, -26.0F, -6.0F, 2.0F, 3.0F, 5.0F, 0.0F, false);
        neck.setTextureOffset(0, 111).addBox(-7.0F, -23.0F, -13.0F, 14.0F, 26.0F, 16.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -17.0F, -8.0F);
        neck.addChild(head);
        setRotateAngle(head, -0.5672F, 0.0F, 0.0F);
        head.setTextureOffset(91, 0).addBox(-7.5F, -6.1F, -21.0F, 15.0F, 9.0F, 21.0F, 0.0F, false);
        head.setTextureOffset(130, 59).addBox(-7.5F, -0.1F, -7.0F, 15.0F, 3.0F, 7.0F, 0.0F, false);
        head.setTextureOffset(0, 25).addBox(2.5F, -9.1F, -7.0F, 5.0F, 3.0F, 6.0F, 0.0F, false);
        head.setTextureOffset(0, 0).addBox(5.5F, -11.1F, -7.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(0, 0).addBox(-7.5F, -11.1F, -7.0F, 2.0F, 2.0F, 2.0F, 0.0F, true);
        head.setTextureOffset(0, 25).addBox(-7.5F, -9.1F, -7.0F, 5.0F, 3.0F, 6.0F, 0.0F, true);
        head.setTextureOffset(0, 153).addBox(-7.5F, 2.9F, -21.0F, 15.0F, 5.0F, 21.0F, 0.0F, false);

        glasses = new AdvancedModelBox(this);
        glasses.setRotationPoint(0.0F, -7.6F, -2.5F);
        head.addChild(glasses);
        glasses.setTextureOffset(91, 30).addBox(-8.0F, -1.5F, -5.0F, 16.0F, 3.0F, 6.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, -0.25F, -2.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(124, 73).addBox(-8.5F, 3.15F, -20.0F, 17.0F, 7.0F, 15.0F, 0.0F, false);
        jaw.setTextureOffset(125, 106).addBox(-8.5F, -3.85F, -20.0F, 17.0F, 7.0F, 14.0F, 0.0F, false);
        jaw.setTextureOffset(57, 70).addBox(-8.5F, 0.15F, -5.0F, 17.0F, 10.0F, 8.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -4.0F, 18.0F);
        body.addChild(tail);
        tail.setTextureOffset(135, 2).addBox(-8.0F, -9.0F, -3.0F, 16.0F, 18.0F, 28.0F, 0.0F, false);
        tail.setTextureOffset(91, 0).addBox(-1.0F, -12.0F, 3.0F, 2.0F, 3.0F, 5.0F, 0.0F, false);
        tail.setTextureOffset(91, 0).addBox(-1.0F, -12.0F, 10.0F, 2.0F, 3.0F, 5.0F, 0.0F, false);
        tail.setTextureOffset(0, 111).addBox(-1.0F, -11.0F, 17.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);

        tailTip = new AdvancedModelBox(this);
        tailTip.setRotationPoint(0.0F, -2.5F, 24.0F);
        tail.addChild(tailTip);
        tailTip.setTextureOffset(0, 1).addBox(0.0F, -5.5F, 20.0F, 0.0F, 1.0F, 3.0F, 0.0F, false);
        tailTip.setTextureOffset(0, 2).addBox(0.0F, -5.5F, 15.0F, 0.0F, 1.0F, 3.0F, 0.0F, false);
        tailTip.setTextureOffset(157, 64).addBox(-4.0F, -4.5F, -2.0F, 8.0F, 9.0F, 31.0F, 0.0F, false);
        tailTip.setTextureOffset(0, 111).addBox(-1.0F, -6.5F, 9.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
        tailTip.setTextureOffset(0, 111).addBox(-1.0F, -6.5F, 3.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.25F, 0.25F, 0.25F);
            matrixStackIn.translate(0.0D, 4.5F, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(TremorsaurusEntity.ANIMATION_SNIFF);
        animator.startKeyframe(5);
        animator.rotate(neck, (float) Math.toRadians(10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-15), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(neck, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(neck, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-15), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(neck, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(neck, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-15), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(TremorsaurusEntity.ANIMATION_SPEAK);
        animator.startKeyframe(3);
        animator.rotate(neck, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(7);
        animator.rotate(neck, (float) Math.toRadians(10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(30), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(TremorsaurusEntity.ANIMATION_ROAR);
        animator.startKeyframe(5);
        animator.move(lleg, 0, -1.5F, 0);
        animator.move(rleg, 0, -1.5F, 0);
        animator.rotate(body, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(lleg, (float) Math.toRadians(15), 0, 0);
        animator.rotate(rleg, (float) Math.toRadians(15), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(5), 0, 0);
        animator.rotate(head, (float) Math.toRadians(15), 0, 0);
        animator.rotate(tail, (float) Math.toRadians(5), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(10);
        animatePose(0);
        animator.rotate(neck, 0, (float) Math.toRadians(30), 0);
        animator.rotate(head, 0, (float) Math.toRadians(20), (float) Math.toRadians(-20));
        animator.rotate(jaw, (float) Math.toRadians(60), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(20);
        animatePose(0);
        animator.rotate(neck, 0, (float) Math.toRadians(-30), 0);
        animator.rotate(head, 0, (float) Math.toRadians(-20), (float) Math.toRadians(20));
        animator.rotate(jaw, (float) Math.toRadians(60), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(10);
        animator.resetKeyframe(10);
        animator.setAnimation(TremorsaurusEntity.ANIMATION_BITE);
        animator.startKeyframe(5);
        animator.rotate(neck, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(15), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(neck, 0, 1, -4);
        animator.move(head, 0, -3, 1);
        animator.rotate(neck, (float) Math.toRadians(35), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-45), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(70), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(TremorsaurusEntity.ANIMATION_SHAKE_PREY);
        animator.startKeyframe(5);
        animatePose(0);
        animator.rotate(jaw, (float) Math.toRadians(40), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(jaw, (float) Math.toRadians(40), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(25);
        animator.resetKeyframe(5);

    }

    private void animatePose(int pose) {
        switch (pose) {
            case 0:
                animator.move(body, 0, 2, -5);
                animator.move(neck, 0, 3, 0);
                animator.move(head, 0, 0, -5);
                animator.move(lleg, 0, 3, 3);
                animator.move(rleg, 0, 3, 3);
                animator.rotate(body, (float) Math.toRadians(30), 0, 0);
                animator.rotate(lleg, (float) Math.toRadians(-30), 0, 0);
                animator.rotate(rleg, (float) Math.toRadians(-30), 0, 0);
                animator.rotate(neck, (float) Math.toRadians(-25), 0, 0);
                animator.rotate(head, (float) Math.toRadians(-15), 0, 0);
                animator.rotate(tail, (float) Math.toRadians(-10), 0, 0);
                animator.rotate(tailTip, (float) Math.toRadians(-10), 0, 0);
                break;
        }
    }

    private void setupAnimForAnimation(TremorsaurusEntity entity, Animation animation, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float partialTick = ageInTicks - entity.tickCount;
        if (entity.getAnimation() == TremorsaurusEntity.ANIMATION_ROAR) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 1, animation, partialTick, 5, 40);
            this.head.swing(1F, 0.1F, false, -1F, 0F, ageInTicks, animationIntensity);
            this.jaw.walk(2F, 0.1F, false, 1F, 0F, ageInTicks, animationIntensity);
        }
        if (entity.getAnimation() == TremorsaurusEntity.ANIMATION_SHAKE_PREY) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 1, animation, partialTick, 5, 30);
            this.body.swing(0.6F, 0.8F, false, 0F, 0F, ageInTicks, animationIntensity);
            this.lleg.swing(0.6F, 0.8F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.rleg.swing(0.6F, 0.8F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.neck.swing(0.6F, 0.8F, false, -1F, 0F, ageInTicks, animationIntensity);
            this.head.swing(0.6F, 0.8F, false, -2F, 0F, ageInTicks, animationIntensity);
            this.tail.swing(0.6F, 0.8F, true, 2F, 0F, ageInTicks, animationIntensity);
            this.tailTip.swing(0.6F, 0.8F, true, 1F, 0F, ageInTicks, animationIntensity);
        }
    }

    @Override
    public void setupAnim(TremorsaurusEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            setupAnimForAnimation(entity, entity.getAnimation(), limbSwing, limbSwingAmount, ageInTicks);
        }
        float partialTicks = ageInTicks - entity.tickCount;
        float danceAmount = entity.getDanceProgress(partialTicks);
        float sitAmount = entity.getSitProgress(partialTicks);
        float buryEggsAmount = entity.getBuryEggsProgress(partialTicks);
        float danceSpeed = 0.5F;
        this.glasses.showModel = danceAmount > 0;
        float walkSpeed = 0.8F;
        float walkDegree = 1F;
        progressPositionPrev(body, sitAmount, 0, 18, -1F, 1F);
        progressPositionPrev(rleg, sitAmount, 0, -11, 12, 1F);
        progressPositionPrev(lleg, sitAmount, 0, -11, 12, 1F);
        progressRotationPrev(rleg, sitAmount, (float) Math.toRadians(-20), (float) Math.toRadians(15), 0, 1F);
        progressRotationPrev(rleg2, sitAmount, (float) Math.toRadians(-50), 0, 0, 1F);
        progressRotationPrev(rfoot, sitAmount, (float) Math.toRadians(70), 0, 0, 1F);
        progressRotationPrev(lleg, sitAmount, (float) Math.toRadians(-20), (float) Math.toRadians(-15), 0, 1F);
        progressRotationPrev(lleg2, sitAmount, (float) Math.toRadians(-50), 0, 0, 1F);
        progressRotationPrev(lfoot, sitAmount, (float) Math.toRadians(70), 0, 0, 1F);
        if (buryEggsAmount > 0.0F) {
            limbSwing = ageInTicks;
            limbSwingAmount = buryEggsAmount * 0.5F;
            this.body.swing(0.25F, 0.4F, false, 0F, 0F, ageInTicks, buryEggsAmount);
            this.neck.swing(0.25F, 0.4F, true, -1F, 0F, ageInTicks, buryEggsAmount);
        }
        float bodyIdleBob = walkValue(ageInTicks, 1, 0.1F, -1F, 1F, false);
        this.walk(neck, 0.1F, 0.03F, false, 1F, 0F, ageInTicks, 1);
        this.walk(head, 0.1F, 0.03F, true, 2F, 0F, ageInTicks, 1);
        this.walk(jaw, 0.1F, 0.03F, true, 3F, 0F, ageInTicks, 1);
        this.flap(rarm, 0.1F, 0.05F, false, 3F, -0.1F, ageInTicks, 1);
        this.flap(larm, 0.1F, 0.05F, true, 3F, -0.1F, ageInTicks, 1);
        this.swing(rarm, 0.1F, 0.1F, true, 2F, 0.0F, ageInTicks, 1);
        this.swing(larm, 0.1F, 0.1F, true, 2F, 0.0F, ageInTicks, 1);
        this.swing(tail, 0.1F, 0.15F, true, -1F, 0.0F, ageInTicks, 1);
        this.swing(tailTip, 0.1F, 0.15F, true, -2F, 0.0F, ageInTicks, 1);
        float bodyWalkBob = -Math.abs(walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 4, false));
        this.body.rotationPointY += bodyIdleBob + bodyWalkBob;
        this.lleg.rotationPointY -= bodyIdleBob + bodyWalkBob;
        this.rleg.rotationPointY -= bodyIdleBob + bodyWalkBob;
        this.swing(body, walkSpeed, walkDegree * 0.3F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.swing(tail, walkSpeed, walkDegree * 0.5F, false, -1F, 0F, limbSwing, limbSwingAmount);
        this.swing(tailTip, walkSpeed, walkDegree * 0.5F, false, -2F, 0F, limbSwing, limbSwingAmount);
        this.swing(body, walkSpeed, walkDegree * 0.3F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.swing(neck, walkSpeed, walkDegree * 0.3F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.swing(lleg, walkSpeed, walkDegree * 0.3F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.swing(rleg, walkSpeed, walkDegree * 0.3F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.flap(body, walkSpeed, walkDegree * 0.5F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(tail, walkSpeed, walkDegree * 0.5F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(lleg, walkSpeed, walkDegree * 0.5F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(rleg, walkSpeed, walkDegree * 0.5F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(neck, walkSpeed, walkDegree * 0.5F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(lleg, walkSpeed, walkDegree * 0.6F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(lleg2, walkSpeed, walkDegree * 0.6F, false, 1.5F, -0.1F, limbSwing, limbSwingAmount);
        this.walk(lfoot, walkSpeed, walkDegree * 1F, false, -1.5F, 0.35F, limbSwing, limbSwingAmount);
        lleg.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 30, true));
        lfoot.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, walkDegree * 2, false));
        this.walk(rleg, walkSpeed, walkDegree * 0.6F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(rleg2, walkSpeed, walkDegree * 0.6F, true, 1.5F, 0.1F, limbSwing, limbSwingAmount);
        this.walk(rfoot, walkSpeed, walkDegree * 1F, true, -1.5F, -0.35F, limbSwing, limbSwingAmount);
        rleg.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 30, false));
        rfoot.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, walkDegree * 2, true));
        this.flap(rarm, walkSpeed, walkDegree * 0.1F, false, -1.5F, 0.2F, limbSwing, limbSwingAmount);
        this.flap(larm, walkSpeed, walkDegree * 0.1F, true, -1.5F, 0.2F, limbSwing, limbSwingAmount);
        this.faceTarget(netHeadYaw, headPitch, 1, neck, head);
        articulateLegs(entity.legSolver, partialTicks);
        this.walk(neck, danceSpeed, 0.5F, false, 1F, 0.2F, ageInTicks, danceAmount);
        this.swing(neck, danceSpeed, 0.35F, false, 0F, 0F, ageInTicks, danceAmount);
        this.walk(head, danceSpeed, 0.5F, true, 1F, 0.2F, ageInTicks, danceAmount);
        this.swing(head, danceSpeed, 0.35F, true, 1, 0F, ageInTicks, danceAmount);
        this.swing(larm, danceSpeed, 0.6F, false, 1F, -0.3F, ageInTicks, danceAmount);
        this.swing(rarm, danceSpeed, 0.6F, false, 1F, -0.3F, ageInTicks, danceAmount);
        this.flap(larm, danceSpeed, 0.3F, false, 1F, -0.3F, ageInTicks, danceAmount);
        this.flap(rarm, danceSpeed, 0.3F, false, 1F, 0.3F, ageInTicks, danceAmount);
        this.swing(body, danceSpeed, 0.2F, false, -1F, 0F, ageInTicks, danceAmount);
    }

    private void articulateLegs(LegSolver legs, float partialTick) {
        float heightBackLeft = legs.legs[0].getHeight(partialTick);
        float heightBackRight = legs.legs[1].getHeight(partialTick);
        float max = (1F - ACMath.smin(1F - heightBackLeft, 1F - heightBackRight, 0.1F)) * 0.8F;
        body.rotationPointY += max * 16;
        rleg.rotationPointY += (heightBackRight - max) * 16;
        lleg.rotationPointY += (heightBackLeft - max) * 16;
    }

    private float walkValue(float limbSwing, float limbSwingAmount, float speed, float offset, float degree, boolean inverse) {
        return (float) ((Math.cos(limbSwing * speed + offset) * degree * limbSwingAmount) * (inverse ? -1 : 1));
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, neck, head, jaw, glasses, tail, tailTip, rarm, larm, rleg, rleg2, rfoot, rclaw1, rclaw2, rclaw3, lleg, lleg2, lfoot, lclaw1, lclaw2, lclaw3);
    }

    public void translateToMouth(PoseStack matrixStackIn) {
        body.translateAndRotate(matrixStackIn);
        neck.translateAndRotate(matrixStackIn);
        head.translateAndRotate(matrixStackIn);
    }

    public Vec3 getRiderPosition(Vec3 offsetIn) {
        PoseStack translationStack = new PoseStack();
        translationStack.pushPose();
        body.translateAndRotate(translationStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(translationStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), armOffsetVec.y(), armOffsetVec.z());
        translationStack.popPose();
        return vec3;
    }

    public void animateSpirit(DinosaurSpiritEntity entityIn, float partialTicks) {
        this.resetToDefaultPose();
        float abilityProgress = entityIn.getAbilityProgress(partialTicks);
        float middleProgress = (float) Math.sin(abilityProgress * Math.PI);
        progressRotationPrev(neck, middleProgress, (float) Math.toRadians(-20F), 0, 0, 1F);
        progressRotationPrev(head, middleProgress, (float) Math.toRadians(-70F), 0, 0, 1F);
        progressRotationPrev(jaw, middleProgress, (float) Math.toRadians(70F), 0, 0, 1F);
        progressPositionPrev(neck, abilityProgress, 0, -4, -9, 1F);
    }

    public void renderSpiritToBuffer(PoseStack poseStack, VertexConsumer ivertexbuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        poseStack.translate(0, 1.3F, 1);
        neck.render(poseStack, ivertexbuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        poseStack.popPose();
    }
}