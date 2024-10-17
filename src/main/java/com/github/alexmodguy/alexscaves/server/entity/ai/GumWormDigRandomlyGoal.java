package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GumWormDigRandomlyGoal extends Goal {

    private GumWormEntity entity;
    private double x;
    private double y;
    private double z;
    private boolean surface = false;

    public GumWormDigRandomlyGoal(GumWormEntity worm) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.entity = worm;
    }

    @Override
    public boolean canUse() {
        if (entity.isVehicle() || entity.isPassenger()) {
            return false;
        } else {
            surface = entity.getRandom().nextInt(2) == 0;
            Vec3 target = this.generateAnyPosition(true);
            if (target == null && !surface) {
                target = this.generateAnyPosition(false);
            }
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
        entity.getNavigation().moveTo(this.x, this.y, this.z, 1F);
    }

    public boolean canContinueToUse() {
        return !this.entity.getNavigation().isDone() && !this.entity.getNavigation().isStuck();
    }

    public void stop() {
        surface = false;
    }


    private Vec3 generateAnyPosition(boolean favoredBlocksOnly) {
        BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos checkBefore = new BlockPos.MutableBlockPos();
        Entity target = entity.getTarget();
        Entity center = target == null ? entity : target;
        for (int i = 0; i < 20; i++) {
            check.move(center.blockPosition());
            check.move(entity.getRandom().nextInt(64) - 32, entity.getRandom().nextInt(64) - 32, entity.getRandom().nextInt(64) - 32);
            checkBefore.set(check);
            if (check.getY() < entity.level().getMinBuildHeight() || !entity.level().isLoaded(check)) {
                break;
            }
            if (surface) {
                while (!entity.level().isEmptyBlock(check) && check.getY() < entity.level().getMaxBuildHeight()) {
                    checkBefore.set(check);
                    check.move(0, 1, 0);
                }
                if (entity.level().isEmptyBlock(check) && (!favoredBlocksOnly || entity.level().getBlockState(checkBefore).is(ACTagRegistry.GUM_WORM_FAVORED_DIGGING))) {
                    return Vec3.atCenterOf(check.immutable().below());
                }
            } else {
                while (entity.level().isEmptyBlock(check) && check.getY() > entity.level().getMinBuildHeight() - 1) {
                    checkBefore.set(check);
                    check.move(0, -1, 0);
                }
                while(check.getY() < entity.level().getMinBuildHeight() + 1){
                    check.move(0, 1, 0);
                }
                if (GumWormEntity.isSafeDig(entity.level(), check.immutable()) && (!favoredBlocksOnly || entity.level().getBlockState(checkBefore).is(ACTagRegistry.GUM_WORM_FAVORED_DIGGING))) {
                    return Vec3.atCenterOf(check.immutable());
                }
            }
        }
        return null;
    }


}

