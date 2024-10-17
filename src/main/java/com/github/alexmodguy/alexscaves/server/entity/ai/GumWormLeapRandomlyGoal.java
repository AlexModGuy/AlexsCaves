package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GumWormLeapRandomlyGoal extends Goal {

    private final GumWormEntity entity;
    private double x;
    private double y;
    private double z;
    private boolean hasLept = false;

    private float leapRot;

    private int leapFor;
    private int maxLeapTime;

    private float leapHeight;
    private float leapRange;

    public GumWormLeapRandomlyGoal(GumWormEntity worm) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.entity = worm;
    }

    @Override
    public boolean canUse() {
        Entity attackTarget = entity.getTarget();
        if (!entity.isInWall() || entity.isLeaping() || entity.isRidingMode() || attackTarget != null && attackTarget.isAlive() || entity.getRandom().nextInt(1) != 0) {
            return false;
        } else {
            Vec3 target = this.findLeapFromPosition();
            if (target == null) {
                return false;
            } else {
                this.x = target.x;
                this.y = target.y;
                this.z = target.z;
                return true;
            }
        }
    }

    public void start() {
        hasLept = false;
        maxLeapTime = 15 + entity.getRandom().nextInt(10);
        leapFor = 0;
        leapHeight = 0.4F + entity.getRandom().nextFloat() * 0.25F;
        leapRange = 0.3F + entity.getRandom().nextFloat() * 0.2F;
    }


    public void stop() {
        entity.getNavigation().stop();
        Entity attackTarget = entity.getTarget();
        if(attackTarget == null || !attackTarget.isAlive()){
            entity.setLeaping(false);
        }
    }

    public boolean canContinueToUse() {
        if (hasLept) {
            return leapFor > 0 || !this.entity.isInWall();
        } else {
            return this.entity.getNavigation().isInProgress() && !this.entity.getNavigation().isStuck() && !hasLept && !this.entity.isRidingMode();
        }
    }

    public void tick() {
        if (hasLept) {
            entity.getNavigation().stop();
            entity.setLeaping(true);
            float forceDown = leapFor / (float)maxLeapTime < 0.5F ? -0.45F : 0;
            Vec3 leapDelta = new Vec3(0, Math.sin(leapFor / (float) maxLeapTime * Math.PI) * leapHeight + forceDown, leapRange).yRot((float) -Math.toRadians(leapRot));
            entity.setDeltaMovement(entity.getDeltaMovement().add(leapDelta));
            entity.setYRot(leapRot);
            if (leapFor > 0) {
                leapFor--;
            }
        } else {
            entity.getNavigation().moveTo(this.x, this.y, this.z, 1.3F);
            if (entity.distanceToSqr(this.x, this.y, this.z) < 18.0F) {
                hasLept = true;
                leapFor = maxLeapTime;
                leapRot = entity.getYRot();
                entity.getNavigation().stop();
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.4F, 0));
            }
        }
    }

    private Vec3 findLeapFromPosition() {
        BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos checkBefore = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 20; i++) {
            check.move(entity.blockPosition());
            check.move(entity.getRandom().nextInt(32) - 16, entity.getRandom().nextInt(32) - 16, entity.getRandom().nextInt(32) - 16);
            checkBefore.set(check);
            if (check.getY() < entity.level().getMinBuildHeight() || !entity.level().isLoaded(check)) {
                break;
            }
            while (entity.level().isEmptyBlock(check) && check.getY() > entity.level().getMinBuildHeight()) {
                checkBefore.set(check);
                check.move(0, -1, 0);
            }
            while (!entity.level().isEmptyBlock(check) && check.getY() < entity.level().getMaxBuildHeight()) {
                checkBefore.set(check);
                check.move(0, 1, 0);
            }
            while(check.getY() < entity.level().getMinBuildHeight() + 1){
                check.move(0, 1, 0);
            }
            if (entity.level().isEmptyBlock(check)) {
                return Vec3.atCenterOf(check.immutable().below());
            }
        }
        return null;
    }


}

