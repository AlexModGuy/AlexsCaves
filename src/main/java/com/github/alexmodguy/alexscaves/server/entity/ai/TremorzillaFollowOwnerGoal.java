package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class TremorzillaFollowOwnerGoal extends Goal {


    public static final int TELEPORT_WHEN_DISTANCE_IS = 32;
    private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
    private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
    private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
    private final TremorzillaEntity tremorzilla;
    private LivingEntity owner;
    private final LevelReader level;
    private final double speedModifier;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;

    public TremorzillaFollowOwnerGoal(TremorzillaEntity tremorzilla, double speed, float minDist, float maxDist) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.tremorzilla = tremorzilla;
        this.level = tremorzilla.level();
        this.speedModifier = speed;
        this.startDistance = minDist;
        this.stopDistance = maxDist;
    }

    public boolean canUse() {
        LivingEntity livingentity = this.tremorzilla.getOwner();
        if(this.tremorzilla.getCommand() != 2){
            return false;
        }else if (livingentity == null) {
            return false;
        } else if (livingentity.isSpectator()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else if (this.tremorzilla.distanceToSqr(livingentity) < (double)(this.startDistance * this.startDistance)) {
            return false;
        } else if (this.isInCombat()) {
            return false;
        } else {
            this.owner = livingentity;
            return true;
        }
    }

    public boolean canContinueToUse() {
        if(this.tremorzilla.getCommand() != 2){
            return false;
        }else if (this.tremorzilla.getNavigation().isDone()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else if (this.isInCombat()) {
            return false;
        } else {
            return !(this.tremorzilla.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
        }
    }

    private boolean unableToMove() {
        return this.tremorzilla.isOrderedToSit() || this.tremorzilla.isPassenger() || this.tremorzilla.isLeashed();
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }

    public void stop() {
        this.owner = null;
        this.tremorzilla.getNavigation().stop();
    }

    public void tick() {
        this.tremorzilla.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tremorzilla.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.tremorzilla.distanceToSqr(this.owner) >= TELEPORT_WHEN_DISTANCE_IS * TELEPORT_WHEN_DISTANCE_IS && AlexsCaves.COMMON_CONFIG.devastatingTremorzillaBeam.get()) {
                this.teleportToOwner();
            } else {
                this.tremorzilla.getNavigation().moveTo(this.owner, this.speedModifier);
            }

        }
    }

    private void teleportToOwner() {
        BlockPos blockpos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-10, 10);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-10, 10);
            boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    private boolean isInCombat() {
        Entity owner = tremorzilla.getOwner();
        if (owner != null) {
            return tremorzilla.distanceTo(owner) < 50 && tremorzilla.getTarget() != null && tremorzilla.getTarget().isAlive();
        }
        return false;
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getX()) < 6.0D && Math.abs((double)z - this.owner.getZ()) < 6.0D) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.tremorzilla.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.tremorzilla.getYRot(), this.tremorzilla.getXRot());
            this.tremorzilla.getNavigation().stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos blockPos) {
        BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, blockPos.mutable());
        if (blockpathtypes != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockPos blockpos = blockPos.subtract(this.tremorzilla.blockPosition());
            return this.level.noCollision(this.tremorzilla, this.tremorzilla.getBoundingBox().move(blockpos));
        }
    }

    private int randomIntInclusive(int i, int j) {
        return this.tremorzilla.getRandom().nextInt(j - i + 1) + i;
    }
}
