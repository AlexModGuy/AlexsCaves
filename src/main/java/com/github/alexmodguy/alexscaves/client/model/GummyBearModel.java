package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.GummyBearEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.world.entity.HumanoidArm;

public class GummyBearModel extends AdvancedEntityModel<GummyBearEntity> implements ArmedModel {
    private final AdvancedModelBox main;
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox left_Ear;
    private final AdvancedModelBox right_Ear;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox left_Arm;
    private final AdvancedModelBox right_Arm;
    private final AdvancedModelBox right_Leg;
    private final AdvancedModelBox left_Leg;
    private float red = 1.0F;
    private float green = 1.0F;
    private float blue = 1.0F;
    private float alpha = 1.0F;
    public boolean ignoreColor;
    private final ModelAnimator animator;

    public GummyBearModel(float scale) {
        texWidth = 128;
        texHeight = 128;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -13.0F, 0.5F);
        main.addChild(body);
        body.setTextureOffset(0, 0).addBox(-8.0F, -7.0F, -10.5F, 16.0F, 14.0F, 21.0F, scale + 0.01F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 1.5F, -10.0F);
        body.addChild(head);
        head.setTextureOffset(0, 35).addBox(-6.0F, -4.5F, -7.5F, 12.0F, 9.0F, 7.0F, scale, false);

        nose = new AdvancedModelBox(this);
        nose.setRotationPoint(0.0F, 1.5F, -7.5F);
        head.addChild(nose);
        nose.setTextureOffset(0, 9).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 5.0F, 3.0F, scale, false);

        left_Ear = new AdvancedModelBox(this);
        left_Ear.setRotationPoint(3.5F, -4.49F, -4.5F);
        head.addChild(left_Ear);
        left_Ear.setTextureOffset(34, 47).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 2.0F, 4.0F, scale, false);

        right_Ear = new AdvancedModelBox(this);
        right_Ear.setRotationPoint(-3.5F, -4.49F, -4.5F);
        head.addChild(right_Ear);
        right_Ear.setTextureOffset(34, 47).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 2.0F, 4.0F, scale, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, 2.0F, 10.0F);
        body.addChild(tail);
        tail.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, 0.5F, 6.0F, 6.0F, 3.0F, scale, false);

        left_Arm = new AdvancedModelBox(this);
        left_Arm.setRotationPoint(5.0F, 6.5F, -7.5F);
        body.addChild(left_Arm);
        left_Arm.setTextureOffset(38, 35).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 6.0F, 6.0F, scale, false);

        right_Arm = new AdvancedModelBox(this);
        right_Arm.setRotationPoint(-5.0F, 6.5F, -7.5F);
        body.addChild(right_Arm);
        right_Arm.setTextureOffset(38, 35).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 6.0F, 6.0F, scale, true);

        right_Leg = new AdvancedModelBox(this);
        right_Leg.setRotationPoint(-5.0F, 6.5F, 7.5F);
        body.addChild(right_Leg);
        right_Leg.setTextureOffset(38, 35).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 6.0F, 6.0F, scale, true);

        left_Leg = new AdvancedModelBox(this);
        left_Leg.setRotationPoint(5.0F, 6.5F, 7.5F);
        body.addChild(left_Leg);
        left_Leg.setTextureOffset(38, 35).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 6.0F, 6.0F, scale, false);
        this.updateDefaultPose();
        this.animator = ModelAnimator.create();
    }


    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (alpha * this.alpha > 0.0F || ignoreColor) {
            float redIn = ignoreColor ? 1.0F : red * this.red;
            float greenIn = ignoreColor ? 1.0F : green * this.green;
            float blueIn = ignoreColor ? 1.0F : blue * this.blue;
            float alphaIn = ignoreColor ? 1.0F : alpha * this.alpha;
            if (this.young) {
                float f = 1.5F;
                head.setScale(f, f, f);
                head.setShouldScaleChildren(true);
                matrixStackIn.pushPose();
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                matrixStackIn.translate(0.0D, 1.5D, 0D);
                parts().forEach((part) -> {
                    part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, redIn, greenIn, blueIn, alphaIn);
                });
                matrixStackIn.popPose();
                head.setScale(1, 1, 1);
            } else {
                matrixStackIn.pushPose();
                parts().forEach((part) -> {
                    part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, redIn, greenIn, blueIn, alphaIn);
                });
                matrixStackIn.popPose();
            }
        }
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, head, body, nose, tail, left_Arm, right_Arm, left_Ear, right_Ear, right_Leg, left_Leg);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(GummyBearEntity.ANIMATION_FISH);
        animator.startKeyframe(15);
        animator.move(body, 0, 1, 4);
        animator.rotate(body, (float) Math.toRadians(10), 0, 0);
        animator.rotate(left_Leg, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(right_Leg, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(right_Arm, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(left_Arm, (float) Math.toRadians(-30), 0, 0);
        animator.move(right_Arm, 0, -2, -1);
        animator.move(left_Arm, 0, -2, -1);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -10);
        animator.move(head, 0, -2, 0);
        animator.rotate(body, (float) Math.toRadians(10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.rotate(left_Leg, (float) Math.toRadians(30), 0, 0);
        animator.rotate(right_Leg, (float) Math.toRadians(30), 0, 0);
        animator.rotate(right_Arm, (float) Math.toRadians(-50), (float) Math.toRadians(30), 0);
        animator.rotate(left_Arm, (float) Math.toRadians(-50), (float) Math.toRadians(-30), 0);
        animator.move(right_Arm, 0, 0, -2);
        animator.move(left_Arm, 0, 0, -2);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(head, 0, -2, 0);
        animator.rotate(head, (float) Math.toRadians(20), 0, 0);
        animator.rotate(right_Arm, (float) Math.toRadians(-30), (float) Math.toRadians(-10), 0);
        animator.rotate(left_Arm, (float) Math.toRadians(-30), (float) Math.toRadians(10), 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(GummyBearEntity.ANIMATION_EAT);
        animator.startKeyframe(5);
        animator.move(head, 0, 3, 1);
        animator.rotate(right_Arm, 0, 0, (float) Math.toRadians(-20));
        animator.rotate(left_Arm, 0, 0, (float) Math.toRadians(20));
        animator.move(right_Arm, -1, 0, 3);
        animator.move(left_Arm, 1, 0, 3);
        animator.endKeyframe();
        animator.setStaticKeyframe(30);
        animator.resetKeyframe(5);
        animator.setAnimation(GummyBearEntity.ANIMATION_BACKSCRATCH);
        animator.startKeyframe(5);
        animator.move(body, 0, 0, 3);
        animator.rotate(right_Arm, 0, 0, (float) Math.toRadians(20));
        animator.rotate(left_Arm, 0, 0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.setStaticKeyframe(80);
        animator.resetKeyframe(5);
        animator.setAnimation(GummyBearEntity.ANIMATION_MAUL);
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float) Math.toRadians(20), 0);
        animator.rotate(head, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(right_Arm, (float) Math.toRadians(-70), 0, (float) Math.toRadians(20));
        animator.rotate(left_Arm, (float) Math.toRadians(20), 0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(body, 0, (float) Math.toRadians(-20), 0);
        animator.rotate(head, 0, (float) Math.toRadians(10), 0);
        animator.rotate(right_Arm, (float) Math.toRadians(20), 0, (float) Math.toRadians(20));
        animator.rotate(left_Arm, (float) Math.toRadians(20), 0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.startKeyframe(5);
        animator.rotate(body, 0, (float) Math.toRadians(-20), 0);
        animator.rotate(head, 0, (float) Math.toRadians(10), 0);
        animator.rotate(right_Arm, (float) Math.toRadians(20), 0, (float) Math.toRadians(-20));
        animator.rotate(left_Arm, (float) Math.toRadians(-70), 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(body, 0, (float) Math.toRadians(20), 0);
        animator.rotate(head, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(right_Arm, (float) Math.toRadians(20), 0, (float) Math.toRadians(-20));
        animator.rotate(left_Arm, (float) Math.toRadians(20), 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.resetKeyframe(6);
        animator.setAnimation(GummyBearEntity.ANIMATION_SWIPE);
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -2);
        animator.move(right_Arm, 0, 0, -2);
        animator.rotate(body, (float) Math.toRadians(-10), (float) Math.toRadians(-10), 0);
        animator.rotate(right_Leg, (float) Math.toRadians(10), 0, 0);
        animator.rotate(left_Leg, (float) Math.toRadians(10), 0, 0);
        animator.rotate(right_Arm, (float) Math.toRadians(-60), 0, (float) Math.toRadians(-10));
        animator.rotate(left_Arm, (float) Math.toRadians(20), 0, (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -4);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -2);
        animator.move(right_Arm, 0, 0, -2);
        animator.rotate(body, (float) Math.toRadians(-10), (float) Math.toRadians(-10), 0);
        animator.rotate(right_Leg, (float) Math.toRadians(10), 0, 0);
        animator.rotate(left_Leg, (float) Math.toRadians(10), 0, 0);
        animator.rotate(left_Arm, (float) Math.toRadians(-60), 0, (float) Math.toRadians(10));
        animator.rotate(right_Arm, (float) Math.toRadians(20), 0, (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(body, 0, 0, -4);
        animator.endKeyframe();
        animator.resetKeyframe(5);

    }

    public void setupAnim(GummyBearEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        boolean november13th = entity.lookForTheGummyBearAlbumInStoresOnNovember13th;
        float partialTicks = ageInTicks - entity.tickCount;
        float walkSpeed = 0.5F;
        float walkDegree = 1F;
        float bounceSpeed = 1.0F;
        float bounceDegree = 1.0F;
        float headYawAmount = netHeadYaw / 57.295776F;
        float headPitchAmount = headPitch / 57.295776F;
        float sleepProgress = entity.getSleepProgress(partialTicks);
        float danceProgress = (1F - sleepProgress) * entity.getDanceProgress(partialTicks);
        float sitProgress = (1F - sleepProgress) * Math.max(entity.getSitProgress(partialTicks), danceProgress);
        float standProgress = (1F - sleepProgress) * (1F - sitProgress) * (november13th ? Math.min(1.0F, limbSwingAmount * entity.getStandProgress(partialTicks)) : entity.getStandProgress(partialTicks));
        float uprightProgress = Math.max(sitProgress, standProgress);
        float allFoursProgress = 1F - uprightProgress;
        progressRotationPrev(body, uprightProgress, (float) Math.toRadians(-90), 0, 0, 1F);
        progressRotationPrev(head, uprightProgress, (float) Math.toRadians(90), 0, 0, 1F);
        progressPositionPrev(head, uprightProgress, 0, -4, -4, 1F);
        progressPositionPrev(body, sitProgress, 0, 2.5F, 2.5F, 1F);
        progressPositionPrev(body, standProgress, 0, -2.8F, 0F, 1F);
        progressPositionPrev(left_Leg, standProgress, 0, -2.5F, 2, 1F);
        progressPositionPrev(right_Leg, standProgress, 0, -2.5F, 2, 1F);
        progressPositionPrev(tail, standProgress, 0, -2.5F, 0, 1F);
        if (november13th) {
            progressRotationPrev(left_Leg, standProgress, (float) Math.toRadians(90), 0, 0, 1F);
            progressRotationPrev(right_Leg, standProgress, (float) Math.toRadians(90), 0, 0, 1F);
            progressRotationPrev(right_Arm, standProgress, 0, 0, (float) Math.toRadians(60), 1F);
            progressRotationPrev(left_Arm, standProgress, 0, 0, (float) Math.toRadians(-60), 1F);
        } else {
            progressRotationPrev(body, standProgress, (float) Math.toRadians(10), 0, 0, 1F);
            progressRotationPrev(head, standProgress, (float) Math.toRadians(-10), 0, 0, 1F);
            progressRotationPrev(left_Leg, standProgress, (float) Math.toRadians(80), 0, 0, 1F);
            progressRotationPrev(right_Leg, standProgress, (float) Math.toRadians(80), 0, 0, 1F);
        }
        progressRotationPrev(right_Arm, sleepProgress, (float) Math.toRadians(-30), 0, (float) Math.toRadians(90), 1F);
        progressRotationPrev(left_Arm, sleepProgress, (float) Math.toRadians(-30), 0, (float) Math.toRadians(-90), 1F);
        progressRotationPrev(head, sleepProgress, (float) Math.toRadians(10), (float) Math.toRadians(-20), (float) Math.toRadians(10), 1F);
        progressRotationPrev(right_Leg, sleepProgress, (float) Math.toRadians(30), 0, (float) Math.toRadians(90), 1F);
        progressRotationPrev(left_Leg, sleepProgress, (float) Math.toRadians(30), 0, (float) Math.toRadians(-90), 1F);
        progressPositionPrev(body, sleepProgress, 0, 5F, 0, 1F);
        progressPositionPrev(right_Arm, sleepProgress, -1, -2F, 0, 1F);
        progressPositionPrev(left_Arm, sleepProgress, 1, -2F, 0, 1F);
        progressPositionPrev(head, sleepProgress, 0, -1F, 1, 1F);
        progressPositionPrev(right_Leg, sleepProgress, -1, -2F, 0, 1F);
        progressPositionPrev(left_Leg, sleepProgress, 1, -2F, 0, 1F);

        this.flap(tail, 0.06F, 0.1F, false, -1F, 0.0F, ageInTicks, 1F);
        this.swing(left_Ear, 0.06F, 0.1F, false, -1F, 0.0F, ageInTicks, 1F);
        this.swing(right_Ear, 0.06F, 0.1F, true, -1F, 0.0F, ageInTicks, 1F);
        this.bob(head, 0.06F, 0.2F, false, ageInTicks, 1F);
        this.flap(right_Leg, 0.06F, 0.2F, false, -1F, 0.2F, ageInTicks, sitProgress);
        this.flap(left_Leg, 0.06F, 0.2F, true, -1F, 0.2F, ageInTicks, sitProgress);
        this.walk(right_Arm, 0.06F, 0.1F, false, -2F, 0.2F, ageInTicks, sitProgress);
        this.walk(left_Arm, 0.06F, 0.1F, false, -2F, 0.2F, ageInTicks, sitProgress);
        this.walk(right_Arm, 0.06F, 0.1F, false, -2F, 0.4F, ageInTicks, standProgress);
        this.walk(left_Arm, 0.06F, 0.1F, false, -2F, 0.4F, ageInTicks, standProgress);
        this.flap(body, 0.3F, 0.1F, false, -2F, 0.0F, ageInTicks, danceProgress);
        this.bob(body, 0.3F, 7, true, ageInTicks, danceProgress);
        this.flap(head, 0.3F, 0.1F, false, -2F, 0.0F, ageInTicks, danceProgress);
        this.swing(head, 0.3F, 0.1F, false, -2F, 0.0F, ageInTicks, danceProgress);
        this.walk(left_Arm, 0.3F, 0.5F, true, -2F, 0.5F, ageInTicks, danceProgress);
        this.walk(right_Arm, 0.3F, 0.5F, false, -2F, -0.5F, ageInTicks, danceProgress);
        this.walk(left_Leg, 0.3F, 0.3F, true, -1.5F, -0.1F, ageInTicks, danceProgress);
        this.walk(right_Leg, 0.3F, 0.3F, true, -1.5F, -0.1F, ageInTicks, danceProgress);
        if (november13th) {
            this.walk(body, bounceSpeed, bounceDegree * 0.1F, true, 1F, -0.1F, limbSwing, limbSwingAmount);
            this.walk(left_Leg, bounceSpeed, bounceDegree, true, 1F, 0F, limbSwing, limbSwingAmount);
            this.walk(right_Leg, bounceSpeed, bounceDegree, true, 1F, 0F, limbSwing, limbSwingAmount);
            this.walk(right_Arm, bounceSpeed, bounceDegree, true, 2F, 0.3F, limbSwing, limbSwingAmount);
            this.walk(left_Arm, bounceSpeed, bounceDegree, true, 2F, 0.3F, limbSwing, limbSwingAmount);
            body.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, bounceSpeed, 0.4F, bounceDegree * 15, false));
            body.rotationPointZ += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, bounceSpeed, 0.8F, bounceDegree * 6, false));
        } else {
            this.flap(body, walkSpeed * 2F, walkDegree * 0.1F, false, 0F, 0.0F, limbSwing, limbSwingAmount);
            this.flap(head, walkSpeed * 2F, walkDegree * 0.1F, true, 1F, 0.0F, limbSwing, limbSwingAmount);
            this.walk(left_Leg, walkSpeed, walkDegree, false, 1F, 0F, limbSwing, limbSwingAmount);
            left_Leg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, walkDegree * 2, true));
            this.walk(right_Leg, walkSpeed, walkDegree, true, 1F, 0F, limbSwing, limbSwingAmount);
            right_Leg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, walkDegree * 2, false));
            this.walk(left_Arm, walkSpeed, walkDegree, false, 2.5F, 0F, limbSwing, limbSwingAmount * allFoursProgress);
            left_Arm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount * allFoursProgress, walkSpeed, 1.0F, walkDegree * 2, true));
            this.walk(right_Arm, walkSpeed, walkDegree, true, 2.5F, 0F, limbSwing, limbSwingAmount * allFoursProgress);
            right_Arm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount * allFoursProgress, walkSpeed, 1.0F, walkDegree * 2, false));
        }
        if (entity.getAnimation() == GummyBearEntity.ANIMATION_EAT) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 4, GummyBearEntity.ANIMATION_EAT, partialTicks, 0);
            this.walk(head, 0.5F, 0.3F, true, 0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(left_Arm, 0.5F, 0.3F, true, 1F, 0.2F, ageInTicks, animationIntensity);
            this.walk(right_Arm, 0.5F, 0.3F, true, 1F, 0.2F, ageInTicks, animationIntensity);
        }
        if (entity.getAnimation() == GummyBearEntity.ANIMATION_BACKSCRATCH) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 4, GummyBearEntity.ANIMATION_BACKSCRATCH, partialTicks, 0);
            float bodyScratchUp = animationIntensity * 2.0F - ACMath.walkValue(ageInTicks, animationIntensity, 0.45F, -0.5F, 2.0F, true);
            this.body.rotationPointY += bodyScratchUp;
            this.left_Leg.rotationPointZ -= bodyScratchUp;
            this.right_Leg.rotationPointZ -= bodyScratchUp;
            this.walk(body, 0.45F, 0.1F, true, 0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(left_Leg, 0.45F, 0.1F, false, 0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(right_Leg, 0.45F, 0.1F, false, 0F, 0.2F, ageInTicks, animationIntensity);
            this.walk(head, 0.45F, 0.1F, true, 1F, 0.1F, ageInTicks, animationIntensity);
            this.flap(right_Arm, 0.45F, 0.1F, true, 1F, -0.3F, ageInTicks, animationIntensity);
            this.flap(left_Arm, 0.45F, 0.1F, true, 1F, 0.3F, ageInTicks, animationIntensity);
        }
        this.head.rotateAngleY += headYawAmount * 0.65F;
        this.head.rotateAngleX += headPitchAmount * 0.75F;
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack matrixStackIn) {
        main.translateAndRotate(matrixStackIn);
        body.translateAndRotate(matrixStackIn);
        if(humanoidArm == HumanoidArm.RIGHT){
            right_Arm.translateAndRotate(matrixStackIn);
        }else{
            left_Arm.translateAndRotate(matrixStackIn);
        }
    }
}
