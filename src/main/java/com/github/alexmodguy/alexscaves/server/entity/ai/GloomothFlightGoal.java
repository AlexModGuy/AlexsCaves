package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GloomothEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GloomothFlightGoal extends Goal {

    private GloomothEntity entity;
    private double x;
    private double y;
    private double z;

    private int orbitAngleOffset = 0;
    private int orbitDistance = 1;

    public GloomothFlightGoal(GloomothEntity notor) {
        this.entity = notor;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.isVehicle() || (entity.getTarget() != null && entity.getTarget().isAlive()) || entity.isPassenger()) {
            return false;
        } else {
            if (entity.onGround() && !entity.isFlying() && entity.getRandom().nextInt(4) != 0) {
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
        if (entity.horizontalCollision || entity.verticalCollision && !entity.onGround() || entity.distanceToSqr(x, y, z) > 100F) {
            Vec3 target = this.generatePosition();
            if (target != null) {
                this.x = target.x;
                this.y = target.y;
                this.z = target.z;
            }
        }
        float speed = 1F;
        if (entity.lightPos != null) {
            entity.setFlying(true);
            speed = 1.1F;
        } else if (entity.onGround()) {
            entity.setFlying(false);
            speed = 1F;
        }
        entity.getNavigation().moveTo(this.x, this.y, this.z, speed);
    }

    @javax.annotation.Nullable
    protected Vec3 generatePosition() {
        if (entity.lightPos != null) {
            return findLightCirclePos(entity.lightPos);
        }
        Vec3 vec3 = findFlightPos();
        if (isOverWaterOrVoid()) {
            return vec3.add(0, entity.getRandom().nextFloat() * 8, 0);
        } else if (entity.getRandom().nextInt(20) != 0) {
            return vec3;
        } else {
            return groundPosition(vec3);
        }
    }

    private Vec3 findLightCirclePos(BlockPos lightPos) {
        Vec3 center = lightPos.getCenter();
        Vec3 offset = new Vec3(entity.getRandom().nextFloat() * 4 + 1F, entity.getRandom().nextFloat() * 2, 0).yRot((float) (Math.PI * 2 * entity.getRandom().nextFloat()));
        return center.add(offset);
    }

    private Vec3 findFlightPos() {
        Vec3 heightAdjusted = entity.position().add(entity.getRandom().nextInt(10) - 5, 0, entity.getRandom().nextInt(10) - 5);
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
        BlockPos ground = BlockPos.containing(airPosition);
        while (ground.getY() > entity.level().getMinBuildHeight() && !entity.level().getBlockState(ground).isSolid()) {
            ground = ground.below();
        }
        return Vec3.atCenterOf(ground.below());
    }

    public boolean canContinueToUse() {
        return entity.distanceToSqr(x, y, z) > 3F && !entity.getNavigation().isDone();

    }

    public void start() {
        entity.setFlying(true);
        entity.getNavigation().moveTo(x, y, z, 1F);
    }

    public void stop() {
        entity.getNavigation().stop();
        super.stop();
    }

}
