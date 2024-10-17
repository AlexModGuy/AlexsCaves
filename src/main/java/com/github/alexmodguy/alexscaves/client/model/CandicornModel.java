package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.CandicornEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class CandicornModel extends AdvancedEntityModel<CandicornEntity> {

    private final AdvancedModelBox main;
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail1;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox saddle;
    private final AdvancedModelBox head;
    private final AdvancedModelBox saddle_head;
    private final AdvancedModelBox left_Rein;
    private final AdvancedModelBox right_Rein;
    private final AdvancedModelBox mane1;
    private final AdvancedModelBox mane2;
    private final AdvancedModelBox mane3;
    private final AdvancedModelBox left_headTuft;
    private final AdvancedModelBox right_headTuft;
    private final AdvancedModelBox left_Ear;
    private final AdvancedModelBox right_Ear;
    private final AdvancedModelBox horn;
    private final AdvancedModelBox left_frontLeg;
    private final AdvancedModelBox right_frontLeg;
    private final AdvancedModelBox right_backLeg;
    private final AdvancedModelBox left_backLeg;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox cube_r5;
    private final ModelAnimator animator;

    public CandicornModel() {
        texWidth = 128;
        texHeight = 128;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -20.75F, 0.0F);
        main.addChild(body);
        body.setTextureOffset(0, 39).addBox(-5.0F, -7.25F, -11.0F, 10.0F, 12.0F, 22.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-5.0F, -7.25F, -11.0F, 10.0F, 17.0F, 22.0F, 0.25F, false);

        tail1 = new AdvancedModelBox(this);
        tail1.setRotationPoint(0.0F, -6.75F, 10.5F);
        body.addChild(tail1);
        tail1.setTextureOffset(47, 56).addBox(-3.5F, -3.5F, 0.5F, 7.0F, 7.0F, 17.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setRotationPoint(0.0F, 0.0F, 17.0F);
        tail1.addChild(tail2);
        tail2.setTextureOffset(47, 22).addBox(-3.5F, -3.5F, 0.5F, 7.0F, 7.0F, 17.0F, 0.01F, false);

        saddle = new AdvancedModelBox(this);
        saddle.setRotationPoint(0.0F, -7.75F, 0.0F);
        body.addChild(saddle);
        saddle.setTextureOffset(42, 0).addBox(-5.0F, 0.5F, -5.0F, 10.0F, 11.0F, 10.0F, 0.5F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -6.4846F, -9.8478F);
        body.addChild(head);
        setRotateAngle(head, 0.3927F, 0.0F, 0.0F);
        head.setTextureOffset(24, 73).addBox(-3.0F, -16.0F, -4.0F, 6.0F, 6.0F, 8.0F, 0.0F, false);
        head.setTextureOffset(0, 73).addBox(-2.0F, -10.0F, -4.0F, 4.0F, 17.0F, 8.0F, 0.0F, false);
        head.setTextureOffset(0, 0).addBox(-2.0F, -16.0F, -11.0F, 4.0F, 6.0F, 7.0F, 0.0F, false);

        saddle_head = new AdvancedModelBox(this);
        saddle_head.setRotationPoint(0.0F, 2.0F, 0.0F);
        head.addChild(saddle_head);
        saddle_head.setTextureOffset(0, 0).addBox(-3.0F, -15.0F, -9.0F, 1.0F, 2.0F, 2.0F, 0.0F, true);
        saddle_head.setTextureOffset(0, 13).addBox(-2.0F, -18.0F, -7.0F, 4.0F, 6.0F, 3.0F, 0.25F, false);
        saddle_head.setTextureOffset(0, 0).addBox(2.0F, -15.0F, -9.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        saddle_head.setTextureOffset(100, 92).addBox(-3.0F, -18.0F, -4.0F, 6.0F, 6.0F, 8.0F, 0.25F, false);

        left_Rein = new AdvancedModelBox(this);
        left_Rein.setRotationPoint(3.0F, -14.0F, -8.0F);
        saddle_head.addChild(left_Rein);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        left_Rein.addChild(cube_r1);
        setRotateAngle(cube_r1, -0.3927F, 0.1309F, 0.0F);
        cube_r1.setTextureOffset(73, 105).addBox(0.025F, -2.5006F, 0.0015F, 0.0F, 5.0F, 18.0F, 0.0F, false);

        right_Rein = new AdvancedModelBox(this);
        right_Rein.setRotationPoint(-3.0F, -14.0F, -8.0F);
        saddle_head.addChild(right_Rein);


        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        right_Rein.addChild(cube_r2);
        setRotateAngle(cube_r2, -0.3927F, -0.1309F, 0.0F);
        cube_r2.setTextureOffset(73, 105).addBox(-0.025F, -2.5006F, 0.0015F, 0.0F, 5.0F, 18.0F, 0.0F, true);

        mane1 = new AdvancedModelBox(this);
        mane1.setRotationPoint(0.0F, -7.0F, 4.0F);
        head.addChild(mane1);
        mane1.setTextureOffset(0, 102).addBox(0.0F, -9.0F, 0.0F, 0.0F, 18.0F, 8.0F, 0.0F, false);

        mane2 = new AdvancedModelBox(this);
        mane2.setRotationPoint(0.0F, 0.0F, 8.0F);
        mane1.addChild(mane2);
        mane2.setTextureOffset(38, 102).addBox(0.0F, -9.0F, 0.0F, 0.0F, 18.0F, 8.0F, 0.0F, true);

        mane3 = new AdvancedModelBox(this);
        mane3.setRotationPoint(0.0F, 0.0F, 8.0F);
        mane2.addChild(mane3);
        mane3.setTextureOffset(20, 102).addBox(0.0F, -9.0F, 0.0F, 0.0F, 18.0F, 8.0F, 0.0F, false);

        left_headTuft = new AdvancedModelBox(this);
        left_headTuft.setRotationPoint(3.0F, -13.5F, 3.0F);
        head.addChild(left_headTuft);


        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
        left_headTuft.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.0F, -0.7854F, 0.0F);
        cube_r3.setTextureOffset(42, 0).addBox(0.0F, -2.5F, 0.0F, 5.0F, 8.0F, 0.0F, 0.0F, false);

        right_headTuft = new AdvancedModelBox(this);
        right_headTuft.setRotationPoint(-3.0F, -13.5F, 3.0F);
        head.addChild(right_headTuft);


        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
        right_headTuft.addChild(cube_r4);
        setRotateAngle(cube_r4, 0.0F, 0.7854F, 0.0F);
        cube_r4.setTextureOffset(42, 0).addBox(-5.0F, -2.5F, 0.0F, 5.0F, 8.0F, 0.0F, 0.0F, true);

        left_Ear = new AdvancedModelBox(this);
        left_Ear.setRotationPoint(2.0F, -16.0F, 3.5F);
        head.addChild(left_Ear);
        left_Ear.setTextureOffset(15, 0).addBox(-1.0F, -3.0F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, false);

        right_Ear = new AdvancedModelBox(this);
        right_Ear.setRotationPoint(-2.0F, -16.0F, 3.5F);
        head.addChild(right_Ear);
        right_Ear.setTextureOffset(15, 0).addBox(-1.0F, -3.0F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, true);

        horn = new AdvancedModelBox(this);
        horn.setRotationPoint(0.0F, -16.0F, -3.0F);
        head.addChild(horn);
        horn.setTextureOffset(52, 74).addBox(0.0F, -12.0F, -3.0F, 0.0F, 12.0F, 6.0F, 0.0F, false);

        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(0.0F, -6.0F, 0.0F);
        horn.addChild(cube_r5);
        setRotateAngle(cube_r5, 0.0F, 1.5708F, 0.0F);
        cube_r5.setTextureOffset(42, 40).addBox(0.0F, -6.0F, -3.0F, 0.0F, 12.0F, 6.0F, 0.0F, false);

        left_frontLeg = new AdvancedModelBox(this);
        left_frontLeg.setRotationPoint(3.5F, 4.75F, -9.0F);
        body.addChild(left_frontLeg);
        left_frontLeg.setTextureOffset(14, 9).addBox(1.5F, 6.0F, 2.0F, 0.0F, 8.0F, 4.0F, 0.0F, false);
        left_frontLeg.setTextureOffset(0, 39).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 16.0F, 4.0F, 0.0F, false);

        right_frontLeg = new AdvancedModelBox(this);
        right_frontLeg.setRotationPoint(-3.5F, 4.75F, -9.0F);
        body.addChild(right_frontLeg);
        right_frontLeg.setTextureOffset(14, 9).addBox(-1.5F, 6.0F, 2.0F, 0.0F, 8.0F, 4.0F, 0.0F, false);
        right_frontLeg.setTextureOffset(0, 39).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 16.0F, 4.0F, 0.0F, true);

        right_backLeg = new AdvancedModelBox(this);
        right_backLeg.setRotationPoint(-3.5F, 4.75F, 9.0F);
        body.addChild(right_backLeg);
        right_backLeg.setTextureOffset(14, 9).addBox(-1.5F, 6.0F, 2.0F, 0.0F, 8.0F, 4.0F, 0.0F, true);
        right_backLeg.setTextureOffset(0, 39).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 16.0F, 4.0F, 0.0F, true);

        left_backLeg = new AdvancedModelBox(this);
        left_backLeg.setRotationPoint(-3.5F, 4.75F, 9.0F);
        body.addChild(left_backLeg);
        left_backLeg.setTextureOffset(14, 9).addBox(8.5F, 6.0F, 2.0F, 0.0F, 8.0F, 4.0F, 0.0F, false);
        left_backLeg.setTextureOffset(0, 39).addBox(5.5F, 0.0F, -2.0F, 3.0F, 16.0F, 4.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, body, head, tail1, tail2, left_frontLeg, left_backLeg, left_Ear, right_frontLeg, right_backLeg, right_Ear, left_Rein, left_headTuft, right_Rein, right_headTuft, saddle, saddle_head, horn, mane1, mane2, mane3, cube_r1, cube_r2, cube_r3, cube_r4, cube_r5);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(CandicornEntity.ANIMATION_BUCK);
        animator.startKeyframe(5);
        poseBucking();
        animator.rotate(left_frontLeg, (float) Math.toRadians(-75), 0, 0);
        animator.rotate(right_frontLeg, (float) Math.toRadians(-25), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        poseBucking();
        animator.rotate(left_frontLeg, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(right_frontLeg, (float) Math.toRadians(-75), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        poseBucking();
        animator.rotate(left_frontLeg, (float) Math.toRadians(-75), 0, 0);
        animator.rotate(right_frontLeg, (float) Math.toRadians(-25), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        poseBucking();
        animator.rotate(left_frontLeg, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(right_frontLeg, (float) Math.toRadians(-75), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        poseBucking();
        animator.rotate(left_frontLeg, (float) Math.toRadians(-75), 0, 0);
        animator.rotate(right_frontLeg, (float) Math.toRadians(-25), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        poseBucking();
        animator.rotate(left_frontLeg, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(right_frontLeg, (float) Math.toRadians(-75), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(CandicornEntity.ANIMATION_TAIL_FLICK_1);
        animator.startKeyframe(3);
        animator.rotate(tail1, (float) Math.toRadians(25), (float) Math.toRadians(25), 0);
        animator.rotate(tail2, (float) Math.toRadians(-10), (float) Math.toRadians(45), 0);
        animator.endKeyframe();
        animator.startKeyframe(6);
        animator.rotate(tail1, (float) Math.toRadians(25), (float) Math.toRadians(-25), 0);
        animator.rotate(tail2, (float) Math.toRadians(-10), (float) Math.toRadians(-45), 0);
        animator.endKeyframe();
        animator.resetKeyframe(3);
        animator.setAnimation(CandicornEntity.ANIMATION_TAIL_FLICK_2);
        animator.startKeyframe(3);
        animator.rotate(tail1, (float) Math.toRadians(25), (float) Math.toRadians(-25), 0);
        animator.rotate(tail2, (float) Math.toRadians(-10), (float) Math.toRadians(-45), 0);
        animator.endKeyframe();
        animator.startKeyframe(6);
        animator.rotate(tail1, (float) Math.toRadians(25), (float) Math.toRadians(25), 0);
        animator.rotate(tail2, (float) Math.toRadians(-10), (float) Math.toRadians(45), 0);
        animator.endKeyframe();
        animator.resetKeyframe(3);
        animator.setAnimation(CandicornEntity.ANIMATION_NIBBLE_IDLE);
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(100), 0, 0);
        animator.rotate(mane1, (float) Math.toRadians(-40), 0, 0);
        animator.move(head, 0, 8, -4);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(90), 0, 0);
        animator.rotate(mane1, (float) Math.toRadians(-40), 0, 0);
        animator.move(head, 0, 7, -3);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(100), 0, 0);
        animator.rotate(mane1, (float) Math.toRadians(-40), 0, 0);
        animator.move(head, 0, 8, -4);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(90), 0, 0);
        animator.rotate(mane1, (float) Math.toRadians(-40), 0, 0);
        animator.move(head, 0, 7, -3);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(100), 0, 0);
        animator.rotate(mane1, (float) Math.toRadians(-40), 0, 0);
        animator.move(head, 0, 8, -4);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(90), 0, 0);
        animator.rotate(mane1, (float) Math.toRadians(-40), 0, 0);
        animator.move(head, 0, 7, -3);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(CandicornEntity.ANIMATION_STAB);
        animator.startKeyframe(5);
        poseBucking();
        animator.rotate(left_frontLeg, (float) Math.toRadians(35), 0, 0);
        animator.rotate(right_frontLeg, (float) Math.toRadians(35), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-35), 0, 0);
        animator.rotate(mane1, (float) Math.toRadians(35), 0, 0);
        animator.move(body, 0, 0, 8);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(5);
        animator.rotate(head, (float) Math.toRadians(75), 0, 0);
        animator.rotate(mane1, (float) Math.toRadians(-35), 0, 0);
        animator.move(body, 0, 0, -4);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(8);

    }

    private void poseBucking() {
        animator.rotate(body, (float) Math.toRadians(-35), 0, 0);
        animator.move(body, 0, -3, 3);
        animator.move(left_backLeg, 0, -1.5F, 0);
        animator.move(right_backLeg, 0, -1.5F, 0);
        animator.rotate(tail1, (float) Math.toRadians(40), 0, 0);
        animator.rotate(left_backLeg, (float) Math.toRadians(35), 0, 0);
        animator.rotate(right_backLeg, (float) Math.toRadians(35), 0, 0);
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.horn.showModel = !this.young;
        if (this.young) {
            float f = 1.35F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            right_backLeg.setScale(1F, 1.5F, 1F);
            left_backLeg.setScale(1F, 1.5F, 1F);
            right_frontLeg.setScale(1F, 1.5F, 1F);
            left_frontLeg.setScale(1F, 1.5F, 1F);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.45F, 0.45F, 0.45F);
            matrixStackIn.translate(0.0D, 1.25D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
            right_backLeg.setScale(1, 1, 1);
            left_backLeg.setScale(1, 1, 1);
            right_frontLeg.setScale(1, 1, 1);
            left_frontLeg.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }

    public void setupAnim(CandicornEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        boolean saddled = entity.isSaddled();
        float partialTicks = ageInTicks - entity.tickCount;
        float runProgress = entity.getRunProgress(partialTicks);
        float leapProgress = entity.getLeapProgress(partialTicks);
        float runAmount = (limbSwingAmount * runProgress) * (1F - leapProgress);
        float walkProgress = 1F - runProgress;
        float walkAmount = (limbSwingAmount * walkProgress) * (1F - leapProgress);
        float stillAmount = 1F - limbSwingAmount;
        float sitProgress = entity.getSitProgress(partialTicks);
        float chargeProgress = entity.getChargeProgress(partialTicks);
        float maneWrapDirection = entity.getManeAngle(partialTicks);
        float maneWrapProgress = entity.getVehicleProgress(partialTicks);
        float walkSpeed = 0.8F;
        float walkDegree = 1F;
        float runSpeed = 0.5F;
        float runDegree = 1F;
        float headYawAmount = netHeadYaw / 57.295776F;
        float headPitchAmount = headPitch / 57.295776F;
        float yaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks;
        float tailYaw = Mth.wrapDegrees(entity.getTailYaw(partialTicks) - yaw) / 57.295776F;
        float leapPitch = entity.getLeapPitch(partialTicks) / 57.295776F * leapProgress;
        this.saddle.showModel = saddled;
        this.saddle_head.showModel = saddled && entity.isVehicle();
        if(entity.isVehicle() && Minecraft.getInstance().player != null && Minecraft.getInstance().player.isPassengerOfSameVehicle(entity) && Minecraft.getInstance().options.getCameraType().isFirstPerson()){
            mane1.showModel = false;
        }else{
            this.mane1.showModel = !this.young;
        }
        this.horn.setScale(1F, 1F + chargeProgress * 0.35F, 1F);
        progressRotationPrev(tail1, stillAmount, (float) Math.toRadians(-65), 0, 0, 1F);
        progressRotationPrev(tail2, stillAmount, (float) Math.toRadians(35), 0, 0, 1F);
        progressPositionPrev(tail2, stillAmount, 0, 1, -2, 1F);
        progressRotationPrev(mane1, stillAmount, (float) Math.toRadians(-20), 0F, 0, 1F);
        progressPositionPrev(mane1, Math.max(stillAmount, runAmount), 0, -1, -3, 1F);
        progressRotationPrev(mane1, maneWrapProgress, (float) Math.toRadians(15), (float) Math.toRadians(-65 * maneWrapDirection), 0, 1F);
        progressRotationPrev(mane2, maneWrapProgress, 0, (float) Math.toRadians(35 * maneWrapDirection), 0, 1F);
        progressRotationPrev(mane3, maneWrapProgress, 0, (float) Math.toRadians(25 * maneWrapDirection), 0, 1F);
        progressRotationPrev(left_backLeg, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-10), 0, 1F);
        progressPositionPrev(left_backLeg, sitProgress, 1, 1, 0, 1F);
        progressRotationPrev(right_backLeg, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(10), 0, 1F);
        progressPositionPrev(right_backLeg, sitProgress, -1, 1, 0, 1F);
        progressRotationPrev(left_frontLeg, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-15), 0, 1F);
        progressPositionPrev(left_frontLeg, sitProgress, -1, 1, 5, 1F);
        progressRotationPrev(right_frontLeg, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(15), 0, 1F);
        progressPositionPrev(right_frontLeg, sitProgress, 1, 1, 5, 1F);
        progressPositionPrev(body, sitProgress, 0, 13, 5, 1F);
        progressRotationPrev(tail1, sitProgress, (float) Math.toRadians(20), (float) Math.toRadians(-55), 0, 1F);
        progressRotationPrev(tail2, sitProgress, (float) Math.toRadians(25), (float) Math.toRadians(-55), (float) Math.toRadians(-45), 1F);
        progressPositionPrev(tail2, sitProgress, 1, -1, -1, 1F);
        progressRotationPrev(head, chargeProgress, (float) Math.toRadians(50), 0, 0, 1F);
        progressRotationPrev(horn, chargeProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(mane1, chargeProgress, (float) Math.toRadians(-10), 0, 0, 1F);
        progressPositionPrev(horn, chargeProgress, 0, 0.5F, 0.5F, 1F);

        this.walk(head, 0.06F, 0.05F, true, 1F, -0.1F, ageInTicks, 1F);
        this.walk(mane1, 0.06F, 0.05F, false, 2F, -0.1F, ageInTicks, 1F);
        this.swing(mane1, 0.06F, 0.1F, false, 0F, 0.0F, ageInTicks, stillAmount);
        this.swing(mane2, 0.06F, 0.2F, false, -1F, 0.0F, ageInTicks, stillAmount);
        this.swing(mane3, 0.06F, 0.3F, false, -2F, 0.0F, ageInTicks, stillAmount);
        this.swing(tail1, 0.06F, 0.1F, false, 1F, 0.0F, ageInTicks, 1F);
        this.walk(tail2, 0.06F, 0.1F, false, 2F, 0.0F, ageInTicks, 1F);
        this.walk(left_backLeg, walkSpeed, walkDegree, false, 1F, 0F, limbSwing, walkAmount);
        left_backLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -0.5F, walkDegree * 5, true));
        this.walk(right_backLeg, walkSpeed, walkDegree, true, 1F, 0F, limbSwing, walkAmount);
        right_backLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -0.5F, walkDegree * 5, false));
        this.walk(left_frontLeg, walkSpeed, walkDegree, false, 2.5F, 0F, limbSwing, walkAmount);
        left_frontLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, 1.0F, walkDegree * 5, true));
        this.walk(right_frontLeg, walkSpeed, walkDegree, true, 2.5F, 0F, limbSwing, walkAmount);
        right_frontLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, 1.0F, walkDegree * 5, false));
        this.walk(tail1, walkSpeed, walkDegree * 0.2F, false, 2F, -0.3F, limbSwing, walkAmount);
        this.walk(tail2, walkSpeed, walkDegree * 0.2F, false, 1F, 0.1F, limbSwing, walkAmount);
        this.walk(head, walkSpeed, walkDegree * 0.1F, false, 2F, 0, limbSwing, walkAmount);
        this.walk(mane1, walkSpeed, walkDegree * 0.1F, false, 1F, -0.1F, limbSwing, walkAmount);
        this.swing(mane1, walkSpeed, walkDegree * 0.2F, false, 0F, 0.0F, limbSwing, walkAmount);
        this.swing(mane2, walkSpeed, walkDegree * 0.3F, false, -1F, 0.0F, limbSwing, walkAmount);
        this.swing(mane3, walkSpeed, walkDegree * 0.4F, false, -2F, 0.0F, limbSwing, walkAmount);
        float bodyWalkBob = Math.max(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed * 2F, -2F, 1, false));
        float bodyRunBob = Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, -3F, 5, true));
        this.body.rotationPointY += bodyWalkBob + bodyRunBob;
        this.left_frontLeg.rotationPointY -= bodyWalkBob;
        this.right_frontLeg.rotationPointY -= bodyWalkBob;
        this.left_backLeg.rotationPointY -= bodyWalkBob;
        this.right_backLeg.rotationPointY -= bodyWalkBob;
        this.walk(left_backLeg, runSpeed, runDegree, false, 3F, 0.2F, limbSwing, runAmount);
        left_backLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, 1.5F, runDegree * 5, true));
        this.walk(right_backLeg, runSpeed, runDegree, false, 4F, 0.2F, limbSwing, runAmount);
        right_backLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, 2.5F, runDegree * 5, true));
        this.walk(left_frontLeg, runSpeed, runDegree, false, 2F, -0.2F, limbSwing, runAmount);
        left_frontLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, 0.5F, runDegree * 5, true));
        this.walk(right_frontLeg, runSpeed, runDegree, false, 1F, -0.2F, limbSwing, runAmount);
        right_frontLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, -0.5F, runDegree * 5, true));
        this.walk(body, runSpeed, runDegree * 0.15F, false, 0F, 0F, limbSwing, runAmount);
        this.walk(left_backLeg, runSpeed, runDegree * 0.15F, true, 0F, 0F, limbSwing, runAmount);
        this.walk(right_backLeg, runSpeed, runDegree * 0.15F, true, 0F, 0F, limbSwing, runAmount);
        this.walk(left_frontLeg, runSpeed, runDegree * 0.15F, true, 0F, 0F, limbSwing, runAmount);
        this.walk(right_frontLeg, runSpeed, runDegree * 0.15F, true, 0F, 0F, limbSwing, runAmount);
        this.walk(tail1, runSpeed, runDegree * 0.35F, false, -1F, 0F, limbSwing, runAmount);
        this.walk(tail2, runSpeed, runDegree * 0.35F, false, -2F, 0F, limbSwing, runAmount);
        this.walk(head, runSpeed, runDegree * 0.15F, false, -1F, 0.2F, limbSwing, runAmount);
        this.walk(mane1, runSpeed, runDegree * 0.1F, false, -1F, -0.2F, limbSwing, runAmount);
        this.swing(mane1, runSpeed, runDegree * 0.2F, false, 5F, 0.0F, limbSwing, runAmount);
        this.swing(mane2, runSpeed, runDegree * 0.3F, false, 4F, 0.0F, limbSwing, runAmount);
        this.swing(mane3, runSpeed, runDegree * 0.4F, false, 3F, 0.0F, limbSwing, runAmount);
        this.head.rotateAngleY += headYawAmount * 0.5F;
        this.mane1.rotateAngleY += headYawAmount * -0.5F;
        this.head.rotateAngleX += headPitchAmount * 0.5F;
        this.walk(left_backLeg, 0.3F, 0.2F, false, 3F, 0.8F, ageInTicks, leapProgress);
        this.walk(right_backLeg, 0.3F, 0.2F, true, 2F, -0.8F, ageInTicks, leapProgress);
        this.walk(left_frontLeg, 0.3F, 0.2F, false, 1F, -0.8F, ageInTicks, leapProgress);
        this.walk(right_frontLeg, 0.3F, 0.2F, true, 0F, 0.8F, ageInTicks, leapProgress);
        tail1.rotateAngleY += tailYaw * 0.8F;
        body.rotateAngleX += leapPitch;
    }

    public void translateToSaddle(PoseStack translationStack) {
        main.translateAndRotate(translationStack);
        body.translateAndRotate(translationStack);
        saddle.translateAndRotate(translationStack);
    }


}
