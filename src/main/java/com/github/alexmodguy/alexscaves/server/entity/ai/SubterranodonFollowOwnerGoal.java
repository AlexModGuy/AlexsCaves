package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class SubterranodonFollowOwnerGoal extends Goal {
    private final SubterranodonEntity subterranodon;
    private LivingEntity owner;
    private final LevelReader world;
    private final double followSpeed;
    private final PathNavigation navigator;
    private int timeToRecalcPath;
    private final float maxDist;
    private final float minDist;
    private float oldWaterCost;
    private final boolean teleportToLeaves;

    public SubterranodonFollowOwnerGoal(SubterranodonEntity subterranodon, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
        this.subterranodon = subterranodon;
        this.world = subterranodon.level();
        this.followSpeed = speed;
        this.navigator = subterranodon.getNavigation();
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.teleportToLeaves = teleportToLeaves;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        LivingEntity livingentity = this.subterranodon.getOwner();
        if (livingentity == null) {
            return false;
        } else if (livingentity.isSpectator()) {
            return false;
        } else if (this.subterranodon.isOrderedToSit()) {
            return false;
        } else if (this.subterranodon.distanceToSqr(livingentity) < (double) (this.minDist * this.minDist) || isInCombat()) {
            return false;
        } else {
            this.owner = livingentity;
            return subterranodon.getCommand() == 2;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        if (this.subterranodon.isOrderedToSit() || isInCombat()) {
            return false;
        } else {
            return this.subterranodon.distanceToSqr(this.owner) > (double) (this.maxDist * this.maxDist);
        }
    }

    private boolean isInCombat() {
        Entity owner = subterranodon.getOwner();
        if (owner != null) {
            return subterranodon.distanceTo(owner) < 30 && subterranodon.getTarget() != null && subterranodon.getTarget().isAlive();
        }
        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.subterranodon.getPathfindingMalus(BlockPathTypes.WATER);
        this.subterranodon.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.owner = null;
        this.navigator.stop();
        this.subterranodon.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        this.subterranodon.getLookControl().setLookAt(this.owner, 10.0F, (float) this.subterranodon.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.subterranodon.isLeashed() && !this.subterranodon.isPassenger()) {
                if (this.subterranodon.distanceToSqr(this.owner) >= 144.0D) {
                    this.tryToTeleportNearEntity();
                }
                if (this.subterranodon.distanceTo(owner) > 5) {
                    if(!this.subterranodon.isFlying()){
                        subterranodon.setFlying(true);
                        subterranodon.setHovering(true);
                    }
                    this.subterranodon.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + owner.getBbHeight(), owner.getZ(), followSpeed);
                } else {
                    if (this.subterranodon.onGround()) {
                        this.subterranodon.setFlying(false);
                    }
                    this.subterranodon.getNavigation().moveTo(owner, followSpeed);
                }
            }
        }
    }

    private void tryToTeleportNearEntity() {
        BlockPos blockpos = this.owner.blockPosition();

        for (int i = 0; i < 10; ++i) {
            int j = this.getRandomNumber(-3, 3);
            int k = this.getRandomNumber(-1, 1);
            int l = this.getRandomNumber(-3, 3);
            boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    private boolean tryToTeleportToLocation(int x, int y, int z) {
        if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.subterranodon.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D, this.subterranodon.getYRot(), this.subterranodon.getXRot());
            this.navigator.stop();
            return true;
        }
    }

    private boolean isTeleportFriendlyBlock(BlockPos pos) {
        if (this.world.getBlockState(pos).isAir()) {
            BlockPos blockpos = pos.subtract(this.subterranodon.blockPosition());
            return this.world.noCollision(this.subterranodon, this.subterranodon.getBoundingBox().move(blockpos));
        }
        BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.world, pos.mutable());
        if (pathnodetype != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.world.getBlockState(pos.below());
            if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.subterranodon.blockPosition());
                return this.world.noCollision(this.subterranodon, this.subterranodon.getBoundingBox().move(blockpos));
            }
        }
    }

    private int getRandomNumber(int min, int max) {
        return this.subterranodon.getRandom().nextInt(max - min + 1) + min;
    }
}

