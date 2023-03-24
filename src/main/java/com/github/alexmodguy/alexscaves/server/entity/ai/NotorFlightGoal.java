package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.NotorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class NotorFlightGoal extends Goal {

    private NotorEntity entity;
    private double x;
    private double y;
    private double z;

    public NotorFlightGoal(NotorEntity notor) {
        this.entity = notor;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.isVehicle() || (entity.getTarget() != null && entity.getTarget().isAlive()) || entity.isPassenger()) {
            return false;
        } else {
            boolean flag = false;
            if (entity.isOnGround() && entity.getRandom().nextInt(45) != 0) {
                return false;
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

    public void tick() {
        if (entity.horizontalCollision || entity.verticalCollision && !entity.isOnGround() || entity.distanceToSqr(x, y, z) < 3F) {
            Vec3 target = this.generatePosition();
            if (target != null) {
                this.x = target.x;
                this.y = target.y;
                this.z = target.z;
            }
        }
        entity.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1F);
    }

    @javax.annotation.Nullable
    protected Vec3 generatePosition() {
        Vec3 vec3 = findFlightPos();
        if (entity.getRandom().nextInt(20) != 0 || isOverWaterOrVoid()) {
            return vec3;
        } else {
            return groundPosition(vec3);
        }
    }

    private Vec3 findFlightPos() {
        float maxRot = entity.horizontalCollision || entity.verticalCollision ? 360 : 40;
        float xRotOffset = (float) Math.toRadians(entity.getRandom().nextFloat() * (maxRot - (maxRot / 2)) * 0.5F);
        float yRotOffset = (float) Math.toRadians(entity.getRandom().nextFloat() * maxRot - (maxRot / 2));
        Vec3 lookVec = entity.getLookAngle().scale(6 + entity.getRandom().nextInt(6)).xRot(xRotOffset).yRot(yRotOffset);
        Vec3 targetVec = entity.position().add(lookVec);
        Vec3 heightAdjusted = targetVec;
        if(entity.level.canSeeSky(new BlockPos(heightAdjusted))){
            Vec3 ground = groundPosition(heightAdjusted);
            heightAdjusted = new Vec3(heightAdjusted.x, ground.y + 4 + entity.getRandom().nextInt(3), heightAdjusted.z);
        }else{
            Vec3 ground = groundPosition(heightAdjusted);
            BlockPos ceiling = new BlockPos(ground).above(2);
            while (ceiling.getY() < entity.level.getMaxBuildHeight() && !entity.level.getBlockState(ceiling).getMaterial().isSolidBlocking()) {
                ceiling = ceiling.above();
            }
            float randCeilVal = 0.3F + entity.getRandom().nextFloat() * 0.5F;
            heightAdjusted = new Vec3(heightAdjusted.x, ground.y + (ceiling.getY() - ground.y) * randCeilVal, heightAdjusted.z);
        }

        BlockHitResult result = entity.level.clip(new ClipContext(entity.getEyePosition(), heightAdjusted, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (result.getType() == HitResult.Type.MISS) {
            return heightAdjusted;
        } else {
            return result.getLocation();
        }
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = entity.blockPosition();
        while (position.getY() > entity.level.getMinBuildHeight() && entity.level.isEmptyBlock(position)) {
            position = position.below();
        }
        return !entity.level.getFluidState(position).isEmpty() || entity.level.getBlockState(position).is(Blocks.VINE) || position.getY() <= entity.level.getMinBuildHeight();
    }

    public Vec3 groundPosition(Vec3 airPosition) {
        BlockPos ground = new BlockPos(airPosition);
        while (ground.getY() > entity.level.getMinBuildHeight() && !entity.level.getBlockState(ground).getMaterial().isSolidBlocking()) {
            ground = ground.below();
        }
        return Vec3.atCenterOf(ground.below());
    }

    public boolean canContinueToUse() {
        return !entity.isOnGround() && entity.distanceToSqr(x, y, z) > 5F;

    }

    public void start() {
        entity.getMoveControl().setWantedPosition(x, y, z, 1F);
    }

    public void stop() {
        entity.getNavigation().stop();
        x = 0;
        y = 0;
        z = 0;
        super.stop();
    }
}