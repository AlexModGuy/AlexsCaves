package com.github.alexmodguy.alexscaves.server.entity.util;

import com.github.alexmodguy.alexscaves.server.entity.living.MagnetronEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public enum MagnetronJoint {

    SHOULDER(new Vec3(1F, 0.5F, 0), false),
    ELBOW(new Vec3(1.5F, -0.5F, 0.2F), false),
    HAND(new Vec3(1.5F, -1.5F, 0.4F), false),
    KNEE(new Vec3(0.5F, -1.5, 0), true),
    FOOT(new Vec3(0.5F, -3F, 0), true);

    private Vec3 basePosition;
    private boolean leg;

    MagnetronJoint(Vec3 basePosition, boolean leg) {
        this.basePosition = basePosition;
        this.leg = leg;
    }

    public Vec3 getTargetPosition(MagnetronEntity entity, boolean left) {
        Vec3 base = this.basePosition;
        float poseProgress = entity.getAttackPoseProgress(1.0F);
        float priorPoseProgress = 1F - poseProgress;
        if (left) {
            base = new Vec3(-this.basePosition.x, this.basePosition.y, this.basePosition.z);
        }
        base = base.add(animateForPose(left, entity, entity.getPrevAttackPose(), priorPoseProgress));
        base = base.add(animateForPose(left, entity, entity.getAttackPose(), poseProgress));
        return base.yRot((float) Math.toRadians(-entity.yBodyRot));
    }

    public Vec3 animateForPose(boolean left, MagnetronEntity entity, MagnetronEntity.AttackPose pose, float progress) {
        Vec3 add = Vec3.ZERO;
        float walkSpeed = 0.2F;
        float walkDegree = 0.4F;
        if (pose == MagnetronEntity.AttackPose.NONE) {

            float limbSwing = 0.0F;
            float limbSwingAmount = 1F;
            if (entity.isAlive()) {
                limbSwingAmount = entity.walkAnimation.speed() * 2F;
                limbSwing = entity.walkAnimation.position() * 2F;
            }
            if (this == KNEE) {
                float up = calculateRotation(walkSpeed, walkDegree * 0.6F, left, 0, left ? -0.3F : 0.3F, limbSwing, limbSwingAmount);
                float forwards = calculateRotation(walkSpeed, walkDegree * 1.5F, left, 0, 0, limbSwing, limbSwingAmount);
                add = new Vec3(0, up, forwards);
            } else if (this == FOOT) {
                float up = calculateRotation(walkSpeed, walkDegree * 1, left, 1, 0, limbSwing, limbSwingAmount);
                float forwards = calculateRotation(walkSpeed, walkDegree * 2.5F, left, 0, left ? -0.2F : 0.2F, limbSwing, limbSwingAmount);
                add = new Vec3(0, up, forwards);
            } else if (this == SHOULDER) {
                float up = calculateRotation(walkSpeed, walkDegree * -0.2F, !left, 0, 0, limbSwing, limbSwingAmount);
                float forwards = calculateRotation(walkSpeed, walkDegree * 0.75F, !left, 0, 0, limbSwing, limbSwingAmount);
                add = new Vec3(0, up, forwards);
            } else if (this == ELBOW) {
                float up = calculateRotation(walkSpeed, walkDegree * -0.2F, !left, 0, 0, limbSwing, limbSwingAmount);
                float forwards = calculateRotation(walkSpeed, walkDegree * 1.5F, !left, -1, 0, limbSwing, limbSwingAmount);
                add = new Vec3(0, up, forwards);
            } else if (this == HAND) {
                float up = calculateRotation(walkSpeed, walkDegree * 0.2F, !left, 0, 0, limbSwing, limbSwingAmount);
                float forwards = calculateRotation(walkSpeed, walkDegree * 2.2F, !left, -1, 0, limbSwing, limbSwingAmount);
                add = new Vec3(0, up, forwards);
            }
        } else if (pose == MagnetronEntity.AttackPose.RIGHT_PUNCH) {
            if (this == SHOULDER) {
                if (!left) {
                    add = new Vec3(0, -0.5F, 0.5F);
                } else {
                    add = new Vec3(-0.15F, 0.0F, -0.5F);
                }
            } else if (this == ELBOW) {
                if (!left) {
                    add = new Vec3(-1F, -0.1F, 1.5F);
                } else {
                    add = new Vec3(-0.5F, 0.0F, -0.7F);
                }
            } else if (this == HAND) {
                if (!left) {
                    add = new Vec3(-2F, 0.5F, 3F);
                } else {
                    add = new Vec3(-1F, 0.0F, -0.9F);
                }
            }
        } else if (pose == MagnetronEntity.AttackPose.LEFT_PUNCH) {
            if (this == SHOULDER) {
                if (left) {
                    add = new Vec3(0, -0.5F, 0.5F);
                } else {
                    add = new Vec3(0.15F, 0.0F, -0.5F);
                }
            } else if (this == ELBOW) {
                if (left) {
                    add = new Vec3(1F, -0.1F, 1.5F);
                } else {
                    add = new Vec3(0.5F, 0.0F, -0.7F);
                }
            } else if (this == HAND) {
                if (left) {
                    add = new Vec3(2F, 0.5F, 3F);
                } else {
                    add = new Vec3(1F, 0.0F, -0.9F);
                }
            }
        } else if (pose == MagnetronEntity.AttackPose.SLAM) {
            float f = left ? 1F : -1F;
            float piHalf = (float) Math.PI * 0.5F;
            Vec3 up = Vec3.ZERO;
            if (this == SHOULDER) {
                up = new Vec3(0.3F * f, -0.5F, -1);
            } else if (this == ELBOW) {
                up = new Vec3(0.5F * f, -1, -1);
            } else if (this == HAND) {
                up = new Vec3(1F * f, -2, -1.5F);
            }
            add = up.xRot(-piHalf - (float) (progress * 2 * piHalf));
        }
        return add.scale(progress);
    }

    private float calculateRotation(float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
        float rotation = Mth.cos(f * speed + offset) * degree * f1 + weight * f1;
        return invert ? -rotation : rotation;
    }
}
