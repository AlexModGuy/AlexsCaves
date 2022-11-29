package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SubterranodonFlightGoal extends Goal {

    private SubterranodonEntity entity;
    private double x;
    private double y;
    private double z;
    private boolean isFlying;

    public SubterranodonFlightGoal(SubterranodonEntity subterranodon) {
        this.entity = subterranodon;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.isVehicle() || (entity.getTarget() != null && entity.getTarget().isAlive()) || entity.isPassenger()) {
            return false;
        } else {
            boolean flag = false;
            if (entity.isPackFollower()) {
                if (entity.getPackLeader().isFlying()) {
                    this.isFlying = true;
                    flag = true;
                }
            }
            if (!flag) {
                if (entity.getRandom().nextInt(45) != 0 && !entity.isFlying()) {
                    return false;
                }
                if (entity.isOnGround()) {
                    this.isFlying = this.entity.getRandom().nextInt(3) == 0;
                } else {
                    this.isFlying = this.entity.getRandom().nextInt(8) > 0 && entity.timeFlying < 200;
                }
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
    }

    public void tick() {
        if (isFlying) {
            if (entity.resetFlightAIFlag || entity.horizontalCollision && entity.timeFlying % 10 == 0 || entity.distanceToSqr(x, y, z) < 9F) {
                Vec3 target = this.getPosition();
                if (target != null) {
                    this.x = target.x;
                    this.y = target.y;
                    this.z = target.z;
                }
                entity.resetFlightAIFlag = false;
            }
            entity.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1F);
        } else {
            if(entity.isFlying() || entity.getPackLeader().landingFlag){
                entity.landingFlag = true;
            }
            entity.getNavigation().moveTo(this.x, this.y, this.z, 1F);
        }
        if (!isFlying && entity.isFlying() && entity.isOnGround()) {
            entity.setFlying(false);
        }
        if (entity.isFlying() && entity.isOnGround() && entity.timeFlying > 40) {
            entity.setFlying(false);
        }
    }

    @javax.annotation.Nullable
    protected Vec3 getPosition() {
        if (isOverWaterOrVoid()) {
            isFlying = true;
        }
        Vec3 vec3 = findOrFollowFlightPos();
        if (isFlying) {
            if (entity.timeFlying < 2000 ||  isLeaderStillGoing() || isOverWaterOrVoid()) {
                return vec3;
            } else {
                return groundPosition(vec3);
            }
        } else {

            return entity.isFlying() ? groundPosition(vec3).add(0, -1, 0) : LandRandomPos.getPos(entity, 10, 7);
        }
    }

    private Vec3 findFlightPos() {
        float maxRot = entity.horizontalCollision ? 360 : 90;
        float xRotOffset = (float) Math.toRadians(entity.getRandom().nextFloat() * (maxRot - (maxRot / 2)) * 0.5F);
        float yRotOffset = (float) Math.toRadians(entity.getRandom().nextFloat() * maxRot - (maxRot / 2));
        Vec3 lookVec = entity.getLookAngle().scale(15 + entity.getRandom().nextInt(15)).xRot(xRotOffset).yRot(yRotOffset);
        Vec3 targetVec = entity.position().add(lookVec);
        Vec3 heightAdjusted = targetVec;
        if(entity.level.canSeeSky(new BlockPos(heightAdjusted))){
            Vec3 ground = groundPosition(heightAdjusted);
            heightAdjusted = new Vec3(heightAdjusted.x, ground.y + 5 + entity.getRandom().nextInt(10), heightAdjusted.z);
        }else{
            Vec3 ground = groundPosition(heightAdjusted);
            BlockPos ceiling = new BlockPos(ground).above(2);
            while (ceiling.getY() < entity.level.getMaxBuildHeight() && !entity.level.getBlockState(ceiling).getMaterial().isSolidBlocking()) {
                ceiling = ceiling.above();
            }
            float randCeilVal = 0.5F + entity.getRandom().nextFloat() * 0.2F;
            heightAdjusted = new Vec3(heightAdjusted.x, ground.y + (ceiling.getY() - ground.y) * randCeilVal, heightAdjusted.z);
        }

        BlockHitResult result = entity.level.clip(new ClipContext(entity.getEyePosition(), heightAdjusted, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (result.getType() == HitResult.Type.MISS) {
            entity.lastFlightTargetPos = heightAdjusted;
            return heightAdjusted;
        } else {
            return result.getLocation();
        }
    }

    private Vec3 findOrFollowFlightPos() {
        SubterranodonEntity leader = entity.getPackLeader();
        if (leader == entity || leader.lastFlightTargetPos == null) {
            Vec3 randOffsetMove = new Vec3(entity.getRandom().nextFloat() - 0.5F, entity.getRandom().nextFloat() - 0.5F, entity.getRandom().nextFloat() - 0.5F).scale(2);
            return findFlightPos().add(randOffsetMove);
        } else {
            int index = getPackPosition(entity, 0);
            int halfIndex = (int) Math.ceil(index / 2F);
            float xOffset = 6F + entity.getRandom().nextFloat() * 2F;
            float zOffset = 4F + entity.getRandom().nextFloat() * 3F;
            Vec3 offset = new Vec3(((index % 2) - 0.5F) * xOffset * halfIndex, 0, halfIndex * zOffset).yRot((float) Math.toRadians(180 - leader.yBodyRot));
            return leader.lastFlightTargetPos.add(offset);
        }
    }


    private boolean isLeaderStillGoing() {
        return entity.isPackFollower() && entity.getPackLeader().isFlying();
    }

    private int getPackPosition(SubterranodonEntity subterranodon, int index) {
        if (index < 16 && subterranodon.getPriorPackMember() != null) {
            return getPackPosition(subterranodon.getPriorPackMember(), index + 1);
        }
        return index;
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
        if (isFlying) {
            return entity.isFlying() && entity.distanceToSqr(x, y, z) > 5F;
        } else {
            return (!entity.getNavigation().isDone()) && !entity.isVehicle();
        }
    }

    public void start() {
        if (isFlying) {
            entity.setFlying(true);
            entity.getMoveControl().setWantedPosition(x, y, z, entity.isPackFollower() ? 2 : 1F);
        } else {
            entity.getNavigation().moveTo(this.x, this.y, this.z, 1F);
        }
    }

    public void stop() {
        entity.getNavigation().stop();
        entity.landingFlag = false;
        x = 0;
        y = 0;
        z = 0;
        super.stop();
    }
}
