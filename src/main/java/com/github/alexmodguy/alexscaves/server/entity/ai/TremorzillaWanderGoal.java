package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class TremorzillaWanderGoal extends Goal {

    private TremorzillaEntity tremorzilla;
    private double x;
    private double y;
    private double z;
    private boolean tryLandTarget;

    public TremorzillaWanderGoal(TremorzillaEntity tremorzilla) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.tremorzilla = tremorzilla;
    }

    public boolean canUse() {
        if (this.tremorzilla.getRandom().nextInt(40) != 0 && !this.tremorzilla.isTremorzillaSwimming()) {
            return false;
        }
        if (this.tremorzilla.isTremorzillaSwimming()) {
            this.tryLandTarget = tremorzilla.timeSwimming > 300 || tremorzilla.getRandom().nextFloat() < 0.1F;
        } else {
            this.tryLandTarget = tremorzilla.getRandom().nextFloat() > 0.1F;
        }
        Vec3 target = this.getPosition();
        if (target == null) {
            return false;
        } else {
            this.x = target.x;
            this.y = target.y;
            this.z = target.z;
            return true;
        }
    }

    public boolean canContinueToUse() {
        return !tremorzilla.getNavigation().isDone() && tremorzilla.distanceToSqr(x, y, z) > 8;
    }

    public void start() {
        tremorzilla.getNavigation().moveTo(this.x, this.y, this.z, 1F);
    }

    public BlockPos findWaterBlock(int range) {
        BlockPos around = tremorzilla.blockPosition();
        int surfaceY;
        BlockPos.MutableBlockPos move = new BlockPos.MutableBlockPos();
        move.set(tremorzilla.getX(), tremorzilla.getY(), tremorzilla.getZ());
        while (move.getY() < tremorzilla.level().getMaxBuildHeight() && !tremorzilla.level().getFluidState(move).isEmpty()) {
            move.move(0, 1, 0);
        }
        surfaceY = move.getY();
        around = around.atY(Math.min(surfaceY - 1, around.getY()));
        for (int i = 0; i < 15; i++) {
            BlockPos blockPos = around.offset(tremorzilla.getRandom().nextInt(range) - range / 2, tremorzilla.getRandom().nextInt(range) - range / 2, tremorzilla.getRandom().nextInt(range) - range / 2);
            if (!tremorzilla.level().getFluidState(blockPos).isEmpty() && !isTargetBlocked(Vec3.atCenterOf(blockPos)) && blockPos.getY() > tremorzilla.level().getMinBuildHeight() + 1) {
                return blockPos;
            }
        }
        return around;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(tremorzilla.getX(), tremorzilla.getEyeY(), tremorzilla.getZ());
        return tremorzilla.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, tremorzilla)).getType() != HitResult.Type.MISS;
    }

    @Nullable
    protected Vec3 getPosition() {
        if (tryLandTarget) {
            Vec3 landTarget = LandRandomPos.getPos(tremorzilla, 30, 8);
            if(landTarget != null){
                return landTarget;
            }
        }
        BlockPos water = findWaterBlock(20);
        if(water != null){
            return Vec3.atCenterOf(water);
        }
        return null;
    }
}