package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.VesperEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.UnderzealotSacrifice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class UnderzealotCaptureSacrificeGoal extends Goal {
    private UnderzealotEntity entity;
    private LivingEntity sacrifice;

    private int validTimeCheck = 0;

    public UnderzealotCaptureSacrificeGoal(UnderzealotEntity underzealot) {
        this.entity = underzealot;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        long worldTime = entity.level().getGameTime() % 10;
        if (entity.isCarrying() || entity.isPackFollower() || entity.sacrificeCooldown > 0) {
            return false;
        }
        if ((worldTime != 0 || entity.getRandom().nextInt(3) != 0) && (target == null || !target.isAlive())) {
            return false;
        }
        AABB aabb = entity.getBoundingBox().inflate(20);
        List<LivingEntity> list = entity.level().getEntitiesOfClass(LivingEntity.class, aabb, this::isFirstValidSacrifice);
        if (!list.isEmpty()) {
            LivingEntity closest = null;
            for (LivingEntity mob : list) {
                if ((closest == null || mob.distanceToSqr(entity) < closest.distanceToSqr(entity)) && entity.hasLineOfSight(mob)) {
                    closest = mob;
                }
            }
            sacrifice = closest;
            return sacrifice != null;
        }
        return false;
    }

    private boolean isFirstValidSacrifice(LivingEntity entity) {
        return isValidSacrifice(entity) && (entity instanceof VesperEntity || entity.getRandom().nextInt(4) == 0);
    }

    private boolean isValidSacrifice(LivingEntity entity) {
        return entity instanceof UnderzealotSacrifice sacrifice && !entity.isPassenger() && sacrifice.isValidSacrifice(getDistanceToGround(entity));
    }

    private int getDistanceToGround(LivingEntity entity) {
        int downBy = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
        while (entity.level().isEmptyBlock(pos) && pos.getY() > entity.level().getMinBuildHeight()) {
            pos.move(0, -1, 0);
            downBy++;
        }
        return downBy;
    }

    @Override
    public boolean canContinueToUse() {
        return sacrifice != null && sacrifice.isAlive() && !sacrifice.isPassenger() && !entity.isPackFollower() && entity.distanceTo(sacrifice) < 32 && entity.sacrificeCooldown <= 0;
    }

    public void stop() {
        this.entity.getNavigation().stop();
    }

    public void start() {
        validTimeCheck = 0;
    }

    public void tick() {
        double distance = entity.distanceTo(sacrifice);
        entity.getNavigation().moveTo(sacrifice, 1.0F);
        if (distance < 1.4F) {
            sacrifice.startRiding(entity);
        }
        Vec3 sub = sacrifice.position().subtract(entity.position());
        if (!entity.isBuried() && sub.y > 0.5F && sub.horizontalDistance() < 2.0F && entity.onGround()) {
            entity.jumpFromGround();
        }
        validTimeCheck++;
        if (validTimeCheck % 100 == 0 && !isValidSacrifice(sacrifice)) {
            sacrifice = null;
        }
    }
}
