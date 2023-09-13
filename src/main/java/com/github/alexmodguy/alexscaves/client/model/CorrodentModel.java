package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.CorrodentEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class CorrodentModel extends AdvancedEntityModel<CorrodentEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox chest;
    private final AdvancedModelBox head;
    private final AdvancedModelBox rwhisker_S;
    private final AdvancedModelBox rwhisker;
    private final AdvancedModelBox lwhisker_S;
    private final AdvancedModelBox lwhisker;
    private final AdvancedModelBox snout;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rarmPivot;
    private final AdvancedModelBox larmPivot;
    private final AdvancedModelBox hipsPivot;
    private final AdvancedModelBox hips;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox rlegPivot;
    private final AdvancedModelBox llegPivot;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox lleg;

    private final ModelAnimator animator;

    public CorrodentModel() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        chest = new AdvancedModelBox(this);
        chest.setRotationPoint(0.0F, -7.0F, 5.0F);
        root.addChild(chest);
        chest.setTextureOffset(0, 46).addBox(-1.0F, -10.0F, -7.0F, 2.0F, 5.0F, 14.0F, 0.0F, false);
        chest.setTextureOffset(0, 23).addBox(-4.0F, -5.0F, -7.0F, 8.0F, 9.0F, 14.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 0.0F, -6.5F);
        chest.addChild(head);
        head.setTextureOffset(30, 28).addBox(-5.0F, -4.0F, -3.5F, 1.0F, 1.0F, 2.0F, 0.0F, true);
        head.setTextureOffset(50, 0).addBox(-5.0F, -3.0F, -7.5F, 10.0F, 6.0F, 7.0F, 0.0F, false);
        head.setTextureOffset(30, 28).addBox(4.0F, -4.0F, -3.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        rwhisker_S = new AdvancedModelBox(this);
        rwhisker_S.setRotationPoint(4.5F, -8.0F, -2.5F);
        head.addChild(rwhisker_S);
        rwhisker_S.setTextureOffset(0, 62).addBox(0.0F, -5.0F, -3.0F, 0.0F, 10.0F, 6.0F, 0.0F, false);

        rwhisker = new AdvancedModelBox(this);
        rwhisker.setRotationPoint(4.0F, -3.0F, -7.0F);
        head.addChild(rwhisker);
        rwhisker.setTextureOffset(0, 17).addBox(0.0F, -10.0F, -0.5F, 0.0F, 10.0F, 6.0F, 0.0F, false);

        lwhisker_S = new AdvancedModelBox(this);
        lwhisker_S.setRotationPoint(-4.5F, -8.0F, -2.5F);
        head.addChild(lwhisker_S);
        lwhisker_S.setTextureOffset(0, 62).addBox(0.0F, -5.0F, -3.0F, 0.0F, 10.0F, 6.0F, 0.0F, true);

        lwhisker = new AdvancedModelBox(this);
        lwhisker.setRotationPoint(-4.0F, -3.0F, -7.0F);
        head.addChild(lwhisker);
        lwhisker.setTextureOffset(0, 17).addBox(0.0F, -10.0F, -0.5F, 0.0F, 10.0F, 6.0F, 0.0F, true);

        snout = new AdvancedModelBox(this);
        snout.setRotationPoint(0.0F, -3.0F, -6.0F);
        head.addChild(snout);
        snout.setTextureOffset(30, 7).addBox(-2.0F, 3.0F, -9.5F, 4.0F, 7.0F, 0.0F, 0.0F, false);
        snout.setTextureOffset(48, 28).addBox(-3.0F, -2.0F, -9.5F, 6.0F, 5.0F, 10.0F, 0.0F, false);

        nose = new AdvancedModelBox(this);
        nose.setRotationPoint(0.0F, -1.5F, -9.25F);
        snout.addChild(nose);
        nose.setTextureOffset(0, 9).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 0.0F, -7.5F);
        head.addChild(jaw);
        jaw.setTextureOffset(53, 43).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 3.0F, 9.0F, -0.01F, false);
        jaw.setTextureOffset(32, 51).addBox(-3.0F, -2.99F, -8.0F, 6.0F, 3.0F, 9.0F, -0.01F, false);
        jaw.setScale(0.99F, 0.99F, 0.99F);

        rarmPivot = new AdvancedModelBox(this);
        rarmPivot.setRotationPoint(4.0F, 2.25F, -3.5F);
        chest.addChild(rarmPivot);

        rarm = new AdvancedModelBox(this);
        rarmPivot.addChild(rarm);
        rarm.setTextureOffset(0, 0).addBox(-1.0F, -1.25F, -1.5F, 3.0F, 6.0F, 3.0F, 0.0F, true);
        rarm.setTextureOffset(23, 0).addBox(-1.0F, 4.75F, -5.5F, 7.0F, 0.0F, 7.0F, 0.0F, true);

        larmPivot = new AdvancedModelBox(this);
        larmPivot.setRotationPoint(-4.0F, 2.25F, -3.5F);
        chest.addChild(larmPivot);

        larm = new AdvancedModelBox(this);
        larmPivot.addChild(larm);
        larm.setTextureOffset(0, 0).addBox(-2.0F, -1.25F, -1.5F, 3.0F, 6.0F, 3.0F, 0.0F, false);
        larm.setTextureOffset(23, 0).addBox(-6.0F, 4.75F, -5.5F, 7.0F, 0.0F, 7.0F, 0.0F, false);

        hipsPivot = new AdvancedModelBox(this);
        hipsPivot.setRotationPoint(0.0F, 0.0F, 7.0F);
        chest.addChild(hipsPivot);

        hips = new AdvancedModelBox(this);
        hipsPivot.addChild(hips);
        hips.setTextureOffset(30, 32).addBox(-1.0F, -10.0F, 0.0F, 2.0F, 5.0F, 14.0F, -0.01F, false);
        hips.setTextureOffset(0, 0).addBox(-4.0F, -5.0F, 0.0F, 8.0F, 9.0F, 14.0F, -0.01F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -5.0F, 14.0F);
        hips.addChild(tail);
        tail.setTextureOffset(70, 8).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 5.0F, 14.0F, 0.0F, false);

        rlegPivot = new AdvancedModelBox(this);
        rlegPivot.setRotationPoint(4.0F, 2.25F, 11.5F);
        hips.addChild(rlegPivot);

        rleg = new AdvancedModelBox(this);
        rlegPivot.addChild(rleg);
        rleg.setTextureOffset(0, 0).addBox(-1.0F, -1.25F, -1.5F, 3.0F, 6.0F, 3.0F, 0.0F, true);
        rleg.setTextureOffset(23, 0).addBox(-1.0F, 4.75F, -5.5F, 7.0F, 0.0F, 7.0F, 0.0F, true);

        llegPivot = new AdvancedModelBox(this);
        llegPivot.setRotationPoint(-4.0F, 2.25F, 11.5F);
        hips.addChild(llegPivot);

        lleg = new AdvancedModelBox(this);
        llegPivot.addChild(lleg);
        lleg.setTextureOffset(0, 0).addBox(-2.0F, -1.25F, -1.5F, 3.0F, 6.0F, 3.0F, 0.0F, false);
        lleg.setTextureOffset(23, 0).addBox(-6.0F, 4.75F, -5.5F, 7.0F, 0.0F, 7.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, rarmPivot, larmPivot, rarm, larm, chest, hips, hipsPivot, llegPivot, rlegPivot, lleg, rleg, head, snout, nose, tail, lwhisker, lwhisker_S, rwhisker, rwhisker_S, jaw);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(CorrodentEntity.ANIMATION_BITE);
        animator.startKeyframe(5);
        animator.move(head, 0, 0, 4);
        animator.move(chest, 0, 0, 4);
        animator.rotate(head, (float) Math.toRadians(-5), 0, (float) Math.toRadians(-5));
        animator.rotate(snout, (float) Math.toRadians(-35), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(45), 0, 0);
        animator.rotate(tail, (float) Math.toRadians(25), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(chest, 0, 0, -2);
        animator.move(jaw, 0, 0, 1);
        animator.rotate(snout, (float) Math.toRadians(5), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(tail, (float) Math.toRadians(45), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    @Override
    public void setupAnim(CorrodentEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float partialTicks = ageInTicks - entity.tickCount;
        float walkSpeed = 1F;
        float walkDegree = 1F;
        float digSpeed = 1F;
        float digDegree = 1F;
        float stillAmount = 1 - limbSwingAmount;
        float digAmount = entity.getDigAmount(partialTicks);
        float afraidAmount = entity.getAfraidAmount(partialTicks);
        float walkAmount = (1F - digAmount) * limbSwingAmount * (1 + afraidAmount);
        float digLimbAmount = limbSwingAmount * digAmount;
        float twitchinessAmount = ACMath.smin((float) Math.sin(ageInTicks * 0.1F) + 0.5F, 0.0F, 0.3F);
        float digPitch = entity.getDigPitch(partialTicks);
        progressRotationPrev(tail, stillAmount, (float) Math.toRadians(-20), 0, 0, 1F);
        progressPositionPrev(chest, afraidAmount, 0, -2.5F, 0, 1F);
        progressPositionPrev(hips, afraidAmount, 0, -2.45F, -3, 1F);
        progressPositionPrev(head, afraidAmount, 0, -1F, 0, 1F);
        progressRotationPrev(chest, afraidAmount, (float) Math.toRadians(40), 0, 0, 1F);
        progressRotationPrev(head, afraidAmount, (float) Math.toRadians(-40), 0, 0, 1F);
        progressRotationPrev(larm, afraidAmount, (float) Math.toRadians(-40), 0, 0, 1F);
        progressRotationPrev(rarm, afraidAmount, (float) Math.toRadians(-40), 0, 0, 1F);
        progressRotationPrev(hips, afraidAmount, (float) Math.toRadians(-80), 0, 0, 1F);
        progressRotationPrev(lleg, afraidAmount, (float) Math.toRadians(40), 0, 0, 1F);
        progressRotationPrev(rleg, afraidAmount, (float) Math.toRadians(40), 0, 0, 1F);
        progressRotationPrev(tail, afraidAmount, (float) Math.toRadians(70), 0, 0, 1F);
        progressRotationPrev(snout, afraidAmount, (float) Math.toRadians(-10), 0, 0, 1F);
        progressRotationPrev(jaw, afraidAmount, (float) Math.toRadians(40), 0, 0, 1F);
        progressRotationPrev(rarm, digAmount, (float) Math.toRadians(-35), (float) Math.toRadians(-45), (float) Math.toRadians(-45), 1F);
        progressRotationPrev(larm, digAmount, (float) Math.toRadians(-35), (float) Math.toRadians(45), (float) Math.toRadians(45), 1F);
        progressRotationPrev(rleg, digAmount, (float) Math.toRadians(-35), (float) Math.toRadians(-45), (float) Math.toRadians(-45), 1F);
        progressRotationPrev(lleg, digAmount, (float) Math.toRadians(-35), (float) Math.toRadians(45), (float) Math.toRadians(45), 1F);
        progressPositionPrev(rarm, digAmount, 0, -2F, 0, 1F);
        progressPositionPrev(larm, digAmount, 0, -2F, 0, 1F);
        progressPositionPrev(rleg, digAmount, 0, -2F, 0, 1F);
        progressPositionPrev(lleg, digAmount, 0, -2F, 0, 1F);
        this.walk(head, 0.1F, 0.05F, true, 1F, 0.1F, ageInTicks, 1F);
        this.swing(tail, 0.1F, 0.05F, true, 3F, 0F, ageInTicks, 1F);
        this.walk(snout, 1F, 0.1F, true, 1F, -0.3F, ageInTicks, twitchinessAmount);
        this.walk(jaw, 1F, 0.1F, true, 1F, -0.2F, ageInTicks, twitchinessAmount);
        this.walk(nose, 2.5F, 0.1F, true, 1F, -0.4F, ageInTicks, twitchinessAmount);
        this.flap(lwhisker, 1.5F, 0.3F, false, 1F, 1.0F, ageInTicks, twitchinessAmount);
        this.flap(rwhisker, 1.5F, 0.3F, true, 1F, 1.0F, ageInTicks, twitchinessAmount);
        this.flap(head, 0.3F, 0.3F, true, 1F, 0F, ageInTicks, afraidAmount);
        this.swing(head, 0.3F, 0.2F, true, 2F, 0F, ageInTicks, afraidAmount);
        this.flap(lwhisker, 0.15F, 0.4F, false, 1F, -1.0F, ageInTicks, afraidAmount);
        this.flap(rwhisker, 0.15F, 0.4F, true, 1F, -1.0F, ageInTicks, afraidAmount);

        this.walk(head, walkSpeed, walkDegree * 0.05F, true, 1F, 0.1F, limbSwing, walkAmount);
        this.flap(chest, walkSpeed, walkDegree * 0.1F, true, 1F, 0F, limbSwing, walkAmount);
        this.flap(hips, walkSpeed, walkDegree * 0.1F, true, 2F, 0F, limbSwing, walkAmount);
        this.flap(lleg, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, walkAmount);
        this.flap(rleg, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, walkAmount);
        this.flap(larm, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, walkAmount);
        this.flap(rarm, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, walkAmount);
        this.flap(head, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, walkAmount);
        this.swing(tail, walkSpeed, walkDegree * 0.4F, false, -1F, 0F, limbSwing, walkAmount);
        float bodyBob = ACMath.walkValue(limbSwing, walkAmount, walkSpeed * 1.5F, 0.5F, 1F, true);
        this.chest.rotationPointY += bodyBob;

        this.walk(rarm, walkSpeed, walkDegree, false, -1.5F, 0.0F, limbSwing, walkAmount);
        rarm.rotateAngleZ -= chest.rotateAngleZ * walkAmount;
        rarm.rotationPointY -= bodyBob - Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -1.5F, 4, true)) + walkAmount;
        this.walk(larm, walkSpeed, walkDegree, true, -1.5F, 0.0F, limbSwing, walkAmount);
        larm.rotateAngleZ -= chest.rotateAngleZ * walkAmount;
        larm.rotationPointY -= bodyBob - Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -1.5F, 4, false)) + walkAmount;
        this.walk(rleg, walkSpeed, walkDegree, true, -2.5F, 0.0F, limbSwing, walkAmount);
        rleg.rotateAngleZ -= (chest.rotateAngleZ + hips.rotateAngleZ) * walkAmount;
        rleg.rotationPointY -= bodyBob - Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -2.5F, 4, false)) + walkAmount;
        this.walk(lleg, walkSpeed, walkDegree, false, -2.5F, 0.0F, limbSwing, walkAmount);
        lleg.rotateAngleZ -= (chest.rotateAngleZ + hips.rotateAngleZ) * walkAmount;
        lleg.rotationPointY -= bodyBob - Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -2.5F, 4, true)) + walkAmount;

        this.swing(chest, digSpeed * 0.5F, digDegree * 0.1F, false, 2F, 0F, limbSwing, digLimbAmount);
        this.swing(hips, digSpeed * 0.5F, digDegree * 0.1F, false, 1F, 0F, limbSwing, digLimbAmount);
        this.swing(tail, digSpeed * 0.5F, digDegree * 0.1F, false, 0F, 0F, limbSwing, digLimbAmount);
        this.swing(rarmPivot, digSpeed, digDegree, false, 1F, -0.3F, limbSwing, digLimbAmount);
        this.walk(rarmPivot, digSpeed, digDegree * 0.5F, false, 2F, 0F, limbSwing, digLimbAmount);
        this.flap(rarmPivot, digSpeed, digDegree, false, 3F, 0F, limbSwing, digLimbAmount);
        this.swing(larmPivot, digSpeed, digDegree, false, 1F, 0.3F, limbSwing, digLimbAmount);
        this.walk(larmPivot, digSpeed, digDegree * 0.5F, true, 2F, 0F, limbSwing, digLimbAmount);
        this.flap(larmPivot, digSpeed, digDegree, false, 3F, 0F, limbSwing, digLimbAmount);
        this.swing(llegPivot, digSpeed, digDegree, false, -1F, 0.3F, limbSwing, digLimbAmount);
        this.walk(llegPivot, digSpeed, digDegree * 0.5F, false, -2F, 0F, limbSwing, digLimbAmount);
        this.flap(llegPivot, digSpeed, digDegree, false, -2F, 0F, limbSwing, digLimbAmount);
        this.swing(rlegPivot, digSpeed, digDegree, false, -1F, -0.3F, limbSwing, digLimbAmount);
        this.walk(rlegPivot, digSpeed, digDegree * 0.5F, true, -2F, 0F, limbSwing, digLimbAmount);
        this.flap(rlegPivot, digSpeed, digDegree, false, 3F, 0F, limbSwing, digLimbAmount);
        this.walk(head, digSpeed * 4F, digDegree * 0.15F, true, 4F, -0.05F, limbSwing, digLimbAmount);
        this.walk(jaw, digSpeed * 4F, digDegree * 0.15F, true, 4F, -0.25F, limbSwing, digLimbAmount);


        float headPitchAmount = headPitch / 57.295776F;
        this.head.rotateAngleX += headPitchAmount;
        double defaultX = Mth.wrapDegrees(digPitch);
        double defaultY = Mth.wrapDegrees(entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks);
        double tailX = (entity.getTrailTransformation(5, 0, partialTicks)) - defaultX;
        double tail1Y = (entity.getTrailTransformation(5, 1, partialTicks)) - defaultY;
        double tail2Y = (entity.getTrailTransformation(10, 1, partialTicks)) - defaultY;
        chest.rotateAngleX += Math.toRadians(digPitch);
        hipsPivot.rotateAngleY += Math.toRadians(Mth.wrapDegrees(tail1Y)) * (1F - afraidAmount);
        tail.rotateAngleY += Math.toRadians(Mth.wrapDegrees(tail2Y));
        hipsPivot.rotateAngleX += Math.toRadians(Mth.wrapDegrees(tailX)) * (1F - afraidAmount);
        this.head.rotateAngleY += Math.toRadians(Mth.approachDegrees((float) defaultY, (float) (netHeadYaw + defaultY), 20) - defaultY);


    }

}
