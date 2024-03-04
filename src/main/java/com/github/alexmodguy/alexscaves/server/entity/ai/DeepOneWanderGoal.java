package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneMageEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DeepOneWanderGoal extends Goal {
    private BlockPos goal = null;
    private DeepOneBaseEntity mob;
    private int chance;
    private double speed;

    private boolean groundTarget = false;

    public DeepOneWanderGoal(DeepOneBaseEntity mob, int chance, double speed) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.mob = mob;
        this.chance = chance;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return mob.isInWaterOrBubble() && (target == null || !target.isAlive()) && (chance == 0 || mob.getRandom().nextInt(chance) == 0) && !mob.isTradingLocked();
    }

    @Override
    public boolean canContinueToUse() {
        return goal != null && !mob.getNavigation().isDone() && mob.getRandom().nextInt(200) != 0 && !mob.isTradingLocked();
    }

    public void start() {
        groundTarget = mob.onGround() ? mob.getRandom().nextFloat() < 0.7F : mob.getRandom().nextFloat() < 0.2F;
        goal = findSwimToPos();
    }

    public void tick() {
        mob.getNavigation().moveTo(goal.getX(), goal.getY(), goal.getZ(), speed);
        if (groundTarget) {
            if (mob.onGround()) {
                mob.setDeepOneSwimming(false);
            } else if (mob.distanceToSqr(Vec3.atCenterOf(goal)) < 4) {
                mob.setDeltaMovement(mob.getDeltaMovement().scale(0.8).add(0, -0.1F, 0));
            }
        }
        if (!groundTarget) {
            mob.setDeepOneSwimming(true);
        }
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(mob.getX(), mob.getEyeY(), mob.getZ());
        return mob.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)).getType() != HitResult.Type.MISS;
    }

    public BlockPos findSwimToPos() {
        BlockPos around = mob.blockPosition();
        int surfaceY;
        BlockPos.MutableBlockPos move = new BlockPos.MutableBlockPos();
        move.set(mob.getX(), mob.getY(), mob.getZ());
        while (move.getY() < mob.level().getMaxBuildHeight() && mob.level().getFluidState(move).is(FluidTags.WATER)) {
            move.move(0, 5, 0);
        }
        surfaceY = move.getY();
        around = around.atY(Math.max(surfaceY - 40, around.getY()));
        int range = 18;

        for (int i = 0; i < 15; i++) {
            BlockPos blockPos = around.offset(mob.getRandom().nextInt(range) - range / 2, mob.getRandom().nextInt(range) - range / 2, mob.getRandom().nextInt(range) - range / 2);

            if (mob.level().getFluidState(blockPos).is(FluidTags.WATER) && !isTargetBlocked(Vec3.atCenterOf(blockPos)) && blockPos.getY() > mob.level().getMinBuildHeight() + 1) {
                if (groundTarget) {
                    while (groundTarget && mob.level().getFluidState(blockPos.below()).is(FluidTags.WATER) && blockPos.getY() > mob.level().getMinBuildHeight()) {
                        blockPos = blockPos.below();
                    }
                    if (mob instanceof DeepOneMageEntity) {
                        blockPos = blockPos.above(1 + mob.getRandom().nextInt(2));
                    }
                }
                return blockPos;
            }
        }
        return around;
    }
}