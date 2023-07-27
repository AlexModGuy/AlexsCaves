package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.CorrodentEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CorrodentDigRandomlyGoal extends Goal {
    private CorrodentEntity entity;
    private double x;
    private double y;
    private double z;
    private boolean surface = false;

    public CorrodentDigRandomlyGoal(CorrodentEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.isVehicle() || (entity.getTarget() != null && entity.getTarget().isAlive()) || entity.isPassenger() || !entity.isDigging() && !entity.onGround() && !entity.isInWall()) {
            return false;
        } else {
            if (!entity.isDigging() && !entity.isInWall() && entity.getRandom().nextInt(20) != 0) {
                return false;
            }
            if (entity.isDigging() && entity.timeDigging > 300) {
                surface = true;
            }
            Vec3 target = this.generatePosition();
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
        entity.setDigging(true);
        entity.getNavigation().moveTo(this.x, this.y, this.z, 1F);
    }

    public boolean canContinueToUse() {
        return !this.entity.getNavigation().isDone() && !this.entity.getNavigation().isStuck() && entity.isDigging();
    }

    public void tick() {
        if (surface && this.entity.distanceToSqr(this.x, this.y, this.z) < 4) {
            this.entity.setDigging(false);
        }
    }

    public void stop() {
        surface = false;
    }

    private Vec3 generatePosition() {
        BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 20; i++) {
            check.move(entity.blockPosition());
            check.move(entity.getRandom().nextInt(32) - 16, entity.getRandom().nextInt(32) - 16, entity.getRandom().nextInt(32) - 16);
            if (check.getY() < entity.level().getMinBuildHeight() || !entity.level().isLoaded(check)) {
                break;
            }
            if (surface) {
                while (!entity.level().isEmptyBlock(check) && check.getY() < entity.level().getMaxBuildHeight()) {
                    check.move(0, 1, 0);
                }
                if (entity.level().isEmptyBlock(check)) {
                    return check.immutable().getCenter();
                }
            } else {
                while (entity.level().isEmptyBlock(check) && check.getY() > entity.level().getMinBuildHeight() - 1) {
                    check.move(0, -1, 0);
                }
                if (CorrodentEntity.isSafeDig(entity.level(), check.immutable()) && entity.canReach(check)) {
                    return Vec3.atCenterOf(check.immutable());
                }

            }
        }
        return null;
    }

}
