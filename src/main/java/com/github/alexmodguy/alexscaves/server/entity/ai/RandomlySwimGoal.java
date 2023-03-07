package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RandomlySwimGoal extends Goal {

    private BlockPos goal = null;
    private Mob mob;
    private int range;

    private int chance;
    private int belowSeaLevel;

    private double speed;

    public RandomlySwimGoal(Mob mob, int chance, int range, int belowSeaLevel, double speed) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.mob = mob;
        this.chance = chance;
        this.range = range;
        this.belowSeaLevel = belowSeaLevel;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return mob.isInWaterOrBubble() && (target == null || !target.isAlive()) && (chance == 0 || mob.getRandom().nextInt(chance) == 0);
    }

    @Override
    public boolean canContinueToUse() {
        return goal != null && mob.distanceToSqr(Vec3.atCenterOf(goal)) > 30 && !mob.getNavigation().isDone();
    }

    public void start(){
        goal = findSwimToPos();
        mob.getNavigation().moveTo(goal.getX(), goal.getY(), goal.getZ(), speed);
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(mob.getX(), mob.getEyeY(), mob.getZ());
        return mob.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)).getType() != HitResult.Type.MISS;
    }

    public BlockPos findSwimToPos() {
        BlockPos around = mob.blockPosition();
        int surfaceY;
        BlockPos.MutableBlockPos move = new BlockPos.MutableBlockPos();
        move.set(mob.getX(), mob.getY(), mob.getZ());
        while (move.getY() < mob.level.getMaxBuildHeight() && mob.level.getFluidState(move).is(FluidTags.WATER)) {
            move.move(0, 5, 0);
        }
        surfaceY = move.getY();
        around = around.atY(Math.min(surfaceY - belowSeaLevel, around.getY()));
        for (int i = 0; i < 15; i++) {
            BlockPos blockPos = around.offset(mob.getRandom().nextInt(range) - range / 2, mob.getRandom().nextInt(range) - range / 2, mob.getRandom().nextInt(range) - range / 2);
            if (mob.level.getFluidState(blockPos).is(FluidTags.WATER) && !isTargetBlocked(Vec3.atCenterOf(blockPos)) && blockPos.getY() > mob.level.getMinBuildHeight() + 1) {
                return blockPos;
            }
        }
        return around;
    }
}