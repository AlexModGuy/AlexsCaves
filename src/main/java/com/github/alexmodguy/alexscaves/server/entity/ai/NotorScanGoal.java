package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.NotorEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class NotorScanGoal extends Goal {

    private NotorEntity notor;
    private LivingEntity scanTarget;
    private int scanTime = 0;

    public NotorScanGoal(NotorEntity notor) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.notor = notor;
    }

    private int getMaxScanTime() {
        return notor.level().getDifficulty() == Difficulty.PEACEFUL ? 40 : 100;
    }

    @Override
    public boolean canUse() {
        long worldTime = notor.level().getGameTime() % 10;
        if (notor.getRandom().nextInt(300) != 0 && worldTime != 0 || notor.getHologramUUID() != null || notor.stopScanningFor > 0) {
            return false;
        }
        AABB aabb = notor.getBoundingBox().inflate(25);
        List<LivingEntity> list = notor.level().getEntitiesOfClass(LivingEntity.class, aabb, NotorEntity.SCAN_TARGET);
        if (!list.isEmpty()) {
            LivingEntity closest = null;
            for (LivingEntity mob : list) {
                if (!(mob instanceof NotorEntity)) {
                    if ((closest == null || mob.distanceToSqr(notor) < closest.distanceToSqr(notor)) && notor.hasLineOfSight(mob) || (!(closest instanceof Player) && mob instanceof Player)) {
                        closest = mob;
                    }
                }
            }
            scanTarget = closest;
            return scanTarget != null;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return scanTarget != null && scanTarget.isAlive() && notor.hasLineOfSight(scanTarget) && scanTarget.distanceTo(notor) <= 40 && scanTime < getMaxScanTime() && notor.getHologramUUID() == null;
    }

    @Override
    public void start() {
        notor.getNavigation().stop();
        scanTime = 0;
        notor.setScanningId(-1);
    }

    @Override
    public void stop() {
        if (scanTime >= getMaxScanTime() && scanTarget != null && scanTarget.isAlive()) {
            notor.setHologramUUID(scanTarget.getUUID());
            notor.setShowingHologram(false);
            notor.stopScanningFor = notor.getRandom().nextInt(300) + 300;
        }
        notor.setScanningId(-1);
    }

    @Override
    public void tick() {
        double dist = scanTarget.distanceTo(notor);
        notor.lookAt(EntityAnchorArgument.Anchor.EYES, scanTarget.getEyePosition());
        if (dist > 8) {
            notor.getNavigation().moveTo(scanTarget.getX(), scanTarget.getY(1.0F) + 1, scanTarget.getZ(), 1.2F);
            if (dist > 15) {
                notor.setScanningId(-1);
            }
        } else {
            notor.getNavigation().stop();
            notor.setScanningId(scanTarget.getId());
            scanTime++;
        }
    }
}
