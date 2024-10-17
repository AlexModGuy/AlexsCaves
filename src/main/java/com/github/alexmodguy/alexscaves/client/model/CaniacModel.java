package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.CaniacEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.CorrodentEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class CaniacModel extends AdvancedEntityModel<CaniacEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox pelvis;
    private final AdvancedModelBox spine;
    private final AdvancedModelBox chest;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox right_Eye;
    private final AdvancedModelBox left_Eye;
    private final AdvancedModelBox left_Arm;
    private final AdvancedModelBox right_Arm;
    private final AdvancedModelBox left_Leg;
    private final AdvancedModelBox right_Leg;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final ModelAnimator animator;

    public CaniacModel() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -19.0F, -3.0F);
        root.addChild(body);


        pelvis = new AdvancedModelBox(this);
        pelvis.setRotationPoint(0.0F, 3.5F, 3.0F);
        body.addChild(pelvis);
        pelvis.setTextureOffset(12, 40).addBox(-5.0F, -1.5F, -2.0F, 10.0F, 3.0F, 4.0F, 0.0F, false);

        spine = new AdvancedModelBox(this);
        spine.setRotationPoint(0.0F, -1.5F, 2.0F);
        pelvis.addChild(spine);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        spine.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.3927F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(0, 6).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, false);

        chest = new AdvancedModelBox(this);
        chest.setRotationPoint(0.0F, -3.7F, -1.54F);
        spine.addChild(chest);


        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        chest.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.3927F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(0, 0).addBox(-8.0F, -11.9959F, -9.9898F, 16.0F, 12.0F, 10.0F, 0.0F, false);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(0.0F, -11.05F, -4.56F);
        chest.addChild(neck);
        neck.setTextureOffset(32, 0).addBox(-2.0F, 0.0F, -10.0F, 4.0F, 0.0F, 10.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 0.0F, -9.4F);
        neck.addChild(head);
        head.setTextureOffset(36, 22).addBox(-7.0F, -7.25F, -4.25F, 14.0F, 14.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(0, 22).addBox(-7.0F, -7.25F, -4.25F, 14.0F, 14.0F, 4.0F, 0.25F, false);

        right_Eye = new AdvancedModelBox(this);
        right_Eye.setRotationPoint(-2.5F, -0.75F, -4.3F);
        head.addChild(right_Eye);
        right_Eye.setTextureOffset(0, 78).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, true);

        left_Eye = new AdvancedModelBox(this);
        left_Eye.setRotationPoint(2.5F, -0.75F, -4.3F);
        head.addChild(left_Eye);
        left_Eye.setTextureOffset(0, 78).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, false);

        left_Arm = new AdvancedModelBox(this);
        left_Arm.setRotationPoint(7.75F, -8.8F, -6.96F);
        chest.addChild(left_Arm);
        left_Arm.setTextureOffset(20, 47).addBox(0.0F, 22.5F, 8.5F, 3.0F, 7.0F, 3.0F, 0.0F, false);
        left_Arm.setTextureOffset(33, 40).addBox(0.0F, 26.5F, 1.5F, 3.0F, 3.0F, 7.0F, 0.0F, false);
        left_Arm.setTextureOffset(0, 40).addBox(0.0F, -5.5F, -1.5F, 3.0F, 35.0F, 3.0F, 0.0F, false);

        right_Arm = new AdvancedModelBox(this);
        right_Arm.setRotationPoint(-7.75F, -8.8F, -6.96F);
        chest.addChild(right_Arm);
        right_Arm.setTextureOffset(20, 47).addBox(-3.0F, 22.5F, 8.5F, 3.0F, 7.0F, 3.0F, 0.0F, true);
        right_Arm.setTextureOffset(33, 40).addBox(-3.0F, 26.5F, 1.5F, 3.0F, 3.0F, 7.0F, 0.0F, true);
        right_Arm.setTextureOffset(0, 40).addBox(-3.0F, -5.5F, -1.5F, 3.0F, 35.0F, 3.0F, 0.0F, true);

        left_Leg = new AdvancedModelBox(this);
        left_Leg.setRotationPoint(3.0F, 1.5F, 0.0F);
        pelvis.addChild(left_Leg);
        left_Leg.setTextureOffset(12, 47).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F, false);
        left_Leg.setTextureOffset(32, 22).addBox(-1.0F, 12.0F, 1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        left_Leg.setTextureOffset(0, 0).addBox(-1.0F, 10.0F, 3.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        right_Leg = new AdvancedModelBox(this);
        right_Leg.setRotationPoint(-3.0F, 1.5F, 0.0F);
        pelvis.addChild(right_Leg);
        right_Leg.setTextureOffset(12, 47).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F, true);
        right_Leg.setTextureOffset(32, 22).addBox(-1.0F, 12.0F, 1.0F, 2.0F, 2.0F, 2.0F, 0.0F, true);
        right_Leg.setTextureOffset(0, 0).addBox(-1.0F, 10.0F, 3.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, pelvis, chest, spine, chest, neck, head, right_Eye, right_Arm, left_Eye, left_Arm, right_Leg, left_Leg, cube_r1, cube_r2);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(CaniacEntity.ANIMATION_LUNGE);
        animator.startKeyframe(10);
        animator.move(body, 0, 0, 4);
        animator.rotate(spine, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(left_Arm, (float) Math.toRadians(-185), (float) Math.toRadians(-25), (float) Math.toRadians(45));
        animator.rotate(right_Arm, (float) Math.toRadians(-185), (float) Math.toRadians(25), (float) Math.toRadians(-45));
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animator.move(body, 0, 4, -15);
        animator.move(left_Arm, 0, -3, -8);
        animator.move(right_Arm, 0, -3, -8);
        animator.rotate(body, (float) Math.toRadians(30), 0, 0);
        animator.rotate(left_Arm, (float) Math.toRadians(-110), (float) Math.toRadians(-15), (float) Math.toRadians(60));
        animator.rotate(right_Arm, (float) Math.toRadians(-110), (float) Math.toRadians(15), (float) Math.toRadians(-60));
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(10);
    }


    public void setupAnim(CaniacEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float walkSpeed = 0.7F;
        float walkDegree = 1.0F;
        float runSpeed = 0.5F;
        float runDegree = 1.0F;
        float partialTick = ageInTicks - entity.tickCount;
        float runProgress = entity.getRunProgress(partialTick);
        float walkProgress = 1F - runProgress;
        float walkAmount = limbSwingAmount * walkProgress;
        float runAmount = limbSwingAmount * runProgress;
        float leftArmAngle = (float) Math.toRadians(Mth.wrapDegrees(entity.getArmAngle(true, partialTick)));
        float rightArmAngle = (float) Math.toRadians(Mth.wrapDegrees(entity.getArmAngle(false, partialTick)));
        float headYawAmount = netHeadYaw / 57.295776F;
        float headPitchAmount = headPitch / 57.295776F;

        progressRotationPrev(spine, walkAmount, (float) Math.toRadians(-25), 0, 0, 1F);
        progressPositionPrev(left_Arm, walkAmount, 0, 4, 0, 1F);
        progressPositionPrev(right_Arm, walkAmount, 0, 4, 0, 1F);
        progressRotationPrev(spine, runAmount, (float) Math.toRadians(15), 0, 0, 1F);
        progressPositionPrev(body, runAmount, 0, 0, 4, 1F);

        this.walk(neck, 0.06F, 0.1F, false, 0F, 0.1F, ageInTicks, 1F);
        this.walk(head, 0.06F, 0.1F, true, 0F, 0.1F, ageInTicks, 1F);
        this.walk(chest, 0.06F, 0.05F, true, 1F, 0.05F, ageInTicks, 1F);
        this.walk(left_Arm, 0.06F, 0.05F, false, 1F, 0.05F, ageInTicks, 1F);
        this.walk(right_Arm, 0.06F, 0.05F, false, 1F, 0.05F, ageInTicks, 1F);

        this.walk(left_Leg, walkSpeed, walkDegree, false, 1F, 0F, limbSwing, walkAmount);
        this.walk(right_Leg, walkSpeed, walkDegree, true, 1F, 0F, limbSwing, walkAmount);
        this.walk(chest, walkSpeed, walkDegree * 0.1F, false, 2F, -0.1F, limbSwing, walkAmount);
        if(entity.getAnimation() != CaniacEntity.ANIMATION_LUNGE){
            this.walk(left_Arm, walkSpeed, walkDegree * 0.05F, false, 3F, 1, limbSwing, walkAmount);
            this.walk(right_Arm, walkSpeed, walkDegree * 0.05F, true, 3F, -1, limbSwing, walkAmount);
        }
        float bodyWalkBob = -Math.abs(ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -1.5F, 3, false));
        float runWalkBob = -Math.abs(ACMath.walkValue(limbSwing, runAmount, runSpeed, -1.5F, 3, false));
        this.body.rotationPointY += bodyWalkBob + runWalkBob;

        this.walk(left_Leg, runSpeed, runDegree, false, 1F, 0F, limbSwing, runAmount);
        this.walk(right_Leg, runSpeed, runDegree, true, 1F, 0F, limbSwing, runAmount);
        this.walk(chest, runSpeed, runDegree * 0.15F, false, 1F, -0.1F, limbSwing, runAmount);
        this.swing(spine, runSpeed, runDegree * 0.15F, false, 0, 0F, limbSwing, runAmount);
        this.swing(neck, runSpeed, runDegree * 0.15F, true, 0, 0F, limbSwing, runAmount);
        this.right_Arm.rotateAngleX += leftArmAngle;
        this.left_Arm.rotateAngleX += rightArmAngle;
        this.head.rotateAngleY += headYawAmount * 0.35F;
        this.neck.rotateAngleY += headYawAmount * 0.35F;
        this.head.rotateAngleX += headPitchAmount * 0.15F;
        this.neck.rotateAngleX += headPitchAmount * 0.15F;
        Entity look = Minecraft.getInstance().getCameraEntity();
        if (look != null) {
            Vec3 vector3d = look.getEyePosition(0.0F);
            Vec3 vector3d1 = entity.getEyePosition(0.0F);
            double d0 = vector3d.y - vector3d1.y;
            float f1 = (float) Mth.clamp(-d0, -1.0F, 1.0F);
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = (new Vec3(vector3d1.x - vector3d.x, 0.0D, vector3d1.z - vector3d.z)).normalize().yRot(((float) Math.PI / 2F));
            double d1 = vector3d2.dot(vector3d3);
            double d2 = Mth.sqrt((float) Math.abs(d1 * 2)) * (float) Math.signum(d1);
            this.left_Eye.rotationPointX += d2 - this.head.rotateAngleZ;
            this.left_Eye.rotationPointY += f1;
            this.right_Eye.rotationPointX += d2 - this.head.rotateAngleZ;
            this.right_Eye.rotationPointY += f1;
        }

    }
}
