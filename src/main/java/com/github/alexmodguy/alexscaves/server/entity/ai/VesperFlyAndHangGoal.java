package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.VesperEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class VesperFlyAndHangGoal extends Goal {

    private VesperEntity entity;
    private boolean wantsToHang = false;
    private double x;
    private double y;
    private double z;

    private int hangCheckIn = 0;

    public VesperFlyAndHangGoal(VesperEntity entity) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        if (entity.isVehicle() || (entity.getTarget() != null && entity.getTarget().isAlive()) || entity.isPassenger()) {
            return false;
        }
        if (entity.isHanging() || entity.groundedFor > 0) {
            return false;
        }
        if (!entity.isFlying() && entity.getRandom().nextInt(70) != 0) {
            return false;
        }
        wantsToHang = entity.timeFlying > 300;
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

    private Vec3 getPosition() {
        if (wantsToHang) {
            Vec3 hangPos = findHangFromPos();
            if (hangPos != null) {
                return hangPos;
            }
        }
        return findFlightPos();
    }

    public void start() {
        this.entity.setFlying(true);
        entity.setHanging(false);
        hangCheckIn = 0;
        entity.getNavigation().moveTo(this.x, this.y, this.z, 1F);
    }

    public void tick() {
        if (wantsToHang) {
            if (hangCheckIn-- < 0) {
                hangCheckIn = 5 + entity.getRandom().nextInt(5);
                if (!entity.isHanging() && entity.canHangFrom(entity.posAbove(), entity.level().getBlockState(entity.posAbove()))) {
                    entity.setHanging(true);
                    entity.setFlying(false);
                }
            }
        }
        if (entity.isFlying() && entity.onGround() && entity.timeFlying > 40) {
            entity.setFlying(false);
        }
    }

    public boolean canContinueToUse() {
        if (wantsToHang) {
            return !entity.getNavigation().isDone() && !entity.isHanging() && entity.groundedFor <= 0;
        } else {
            return entity.isFlying() && !entity.getNavigation().isDone() && entity.groundedFor <= 0;
        }
    }

    public void stop() {
        if (wantsToHang) {
            this.entity.getNavigation().stop();
        }
        wantsToHang = false;
    }

    private Vec3 findFlightPos() {
        int range = 13;

        Vec3 heightAdjusted = entity.position().add(entity.getRandom().nextInt(range * 2) - range, 0, entity.getRandom().nextInt(range * 2) - range);
        if (entity.level().canSeeSky(BlockPos.containing(heightAdjusted))) {
            Vec3 ground = groundPosition(heightAdjusted);
            heightAdjusted = new Vec3(heightAdjusted.x, ground.y + 4 + entity.getRandom().nextInt(3), heightAdjusted.z);
        } else {
            Vec3 ground = groundPosition(heightAdjusted);
            BlockPos ceiling = BlockPos.containing(ground).above(2);
            while (ceiling.getY() < entity.level().getMaxBuildHeight() && !entity.level().getBlockState(ceiling).isSolid()) {
                ceiling = ceiling.above();
            }
            float randCeilVal = 0.3F + entity.getRandom().nextFloat() * 0.5F;
            heightAdjusted = new Vec3(heightAdjusted.x, ground.y + (ceiling.getY() - ground.y) * randCeilVal, heightAdjusted.z);
        }

        BlockHitResult result = entity.level().clip(new ClipContext(entity.getEyePosition(), heightAdjusted, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (result.getType() == HitResult.Type.MISS) {
            return heightAdjusted;
        } else {
            return result.getLocation();
        }
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = entity.blockPosition();
        while (position.getY() > entity.level().getMinBuildHeight() && entity.level().isEmptyBlock(position)) {
            position = position.below();
        }
        return !entity.level().getFluidState(position).isEmpty() || entity.level().getBlockState(position).is(Blocks.VINE) || position.getY() <= entity.level().getMinBuildHeight();
    }

    public Vec3 groundPosition(Vec3 airPosition) {
        BlockPos.MutableBlockPos ground = new BlockPos.MutableBlockPos();
        ground.set(airPosition.x, airPosition.y, airPosition.z);
        boolean flag = false;
        while (ground.getY() < entity.level().getMaxBuildHeight() && !entity.level().getBlockState(ground).isSolid() && entity.level().getFluidState(ground).isEmpty()){
            ground.move(0, 1, 0);
            flag = true;
        }
        ground.move(0, -1, 0);
        while (ground.getY() > entity.level().getMinBuildHeight() && !entity.level().getBlockState(ground).isSolid() && entity.level().getFluidState(ground).isEmpty()) {
            ground.move(0, -1, 0);
        }
        return Vec3.atCenterOf(flag ? ground.above() : ground.below());
    }

    public Vec3 findHangFromPos() {
        BlockPos blockpos = null;
        Random random = new Random();
        int range = 14;
        for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.entity.blockPosition().offset(random.nextInt(range) - range / 2, 0, random.nextInt(range) - range / 2);
            if (!this.entity.level().isEmptyBlock(blockpos1) ||!this.entity.level().isLoaded(blockpos1)) {
                continue;
            }
            while (this.entity.level().isEmptyBlock(blockpos1) && blockpos1.getY() < this.entity.level().getMaxBuildHeight()) {
                blockpos1 = blockpos1.above();
            }
            if (blockpos1.getY() > entity.getY() - 1 && entity.canHangFrom(blockpos1, entity.level().getBlockState(blockpos1)) && hasLineOfToPos(blockpos1)) {
                blockpos = blockpos1;
            }
        }
        return blockpos == null ? null : Vec3.atCenterOf(blockpos);
    }

    public boolean hasLineOfToPos(BlockPos in) {
        HitResult raytraceresult = entity.level().clip(new ClipContext(entity.getEyePosition(1.0F), new Vec3(in.getX() + 0.5, in.getY() + 0.5, in.getZ() + 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (raytraceresult instanceof BlockHitResult) {
            BlockHitResult blockRayTraceResult = (BlockHitResult) raytraceresult;
            BlockPos pos = blockRayTraceResult.getBlockPos();
            return pos.equals(in) || entity.level().isEmptyBlock(pos);
        }
        return true;
    }
}
