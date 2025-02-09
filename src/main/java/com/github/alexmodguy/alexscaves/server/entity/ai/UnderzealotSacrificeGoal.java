package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.PackAnimal;
import com.github.alexmodguy.alexscaves.server.entity.util.UnderzealotSacrifice;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class UnderzealotSacrificeGoal extends Goal {

    private final UnderzealotEntity entity;
    private int executionCooldown = 10;
    private int attemptToFollowTicks = 0;
    private BlockPos center;

    public UnderzealotSacrificeGoal(UnderzealotEntity entity) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        if (entity.sacrificeCooldown > 0) {
            return false;
        }
        if (entity.isPackFollower()) {
            UnderzealotEntity leader = (UnderzealotEntity) entity.getPackLeader();
            if (leader.isCarrying() && leader.getLastSacrificePos() != null && leader.distanceToSqr(Vec3.atCenterOf(leader.getLastSacrificePos())) < 2.5F) {
                center = leader.getLastSacrificePos();
                return true;
            }
        } else if (entity.isCarrying()) {
            if (executionCooldown-- > 0) {
                return false;
            } else {
                executionCooldown = 20 + entity.getRandom().nextInt(100);
                BlockPos pos = null;
                if (entity.getLastSacrificePos() != null) {
                    if (isValidSacrificePos(entity.getLastSacrificePos())) {
                        executionCooldown = 10;
                        pos = entity.getLastSacrificePos();
                    } else {
                        entity.setLastSacrificePos(null);
                    }
                }
                if (pos == null) {
                    pos = findNearestSacrificePos();
                }
                if (pos != null) {
                    center = pos;
                    entity.setLastSacrificePos(center);
                    return true;
                }
            }
        }
        return false;
    }

    private BlockPos findNearestSacrificePos() {
        BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
        check.move(entity.blockPosition());
        check.move(0, -1, 0);
        if (isValidSacrificePos(check)) {
            return check.immutable();
        }
        for (int i = 0; i < 20; i++) {
            check.move(entity.blockPosition());
            check.move(entity.getRandom().nextInt(20) - 10, 5, entity.getRandom().nextInt(20) - 10);
            if (!entity.level().isLoaded(check)) {
                continue;
            }
            while (entity.level().isEmptyBlock(check) && check.getY() > entity.level().getMinBuildHeight()) {
                check.move(0, -1, 0);
            }
            if (isValidSacrificePos(check) && canReach(check)) {
                return check.immutable();
            }
        }
        return null;
    }

    public boolean canReach(BlockPos target) {
        Path path = this.entity.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        } else {
            Node node = path.getEndNode();
            if (node == null) {
                return false;
            } else {
                int i = node.x - target.getX();
                int j = node.y - target.getY();
                int k = node.z - target.getZ();
                return (double) (i * i + j * j + k * k) <= 3D;
            }
        }
    }

    private boolean isValidSacrificePos(BlockPos pos) {
        if (entity.level().isEmptyBlock(pos)) {
            return false;
        }
        BlockPos.MutableBlockPos aboveGround = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();
        int badSpots = 0;
        for (int i = -2; i <= -2; i++) {
            for (int j = -2; j <= -2; j++) {
                aboveGround.set(pos.getX() + i, pos.getY() + 1, pos.getZ() + j);
                below.set(pos.getX() + i, pos.getY(), pos.getZ() + j);
                if (entity.level().isEmptyBlock(below)) {
                    badSpots++;
                }
                if (badSpots > 5) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (center == null) {
            return false;
        }
        if (entity.sacrificeCooldown > 0) {
            return false;
        }
        if (entity.isPackFollower()) {
            UnderzealotEntity leader = (UnderzealotEntity) entity.getPackLeader();
            return leader != null && leader.isCarrying() && leader.distanceToSqr(Vec3.atCenterOf(center)) < 5F;
        } else {
            return entity.isCarrying();
        }
    }

    public void tick() {
        if (this.entity.isDiggingInProgress() || this.entity.isBuried()) {
            this.entity.setPraying(false);
            return;
        }
        if (center != null) {
            int worshippingTicks = this.entity.getWorshipTime();
            if (entity.isPackFollower()) {
                UnderzealotEntity leader = (UnderzealotEntity) entity.getPackLeader();
                float f = getPackPosition() / ((float) entity.getPackSize() - 1);
                Vec3 offset = new Vec3(2, 0, 0).yRot(f * ((float) Math.PI * 2F));
                Vec3 at = groundOf(Vec3.atCenterOf(center).add(offset));
                entity.getNavigation().moveTo(at.x, at.y, at.z, 1);
                if (entity.isPraying()) {
                    attemptToFollowTicks = 0;
                } else if ((entity.getNavigation().isStuck() || attemptToFollowTicks > 60) && this.entity.distanceToSqr(at) > 8) {
                    entity.setBuried(true);
                    entity.reemergeAt(BlockPos.containing(at).above(), 20 + entity.getRandom().nextInt(20));
                }
                if (leader != null && leader.sacrificeCooldown > 0) {
                    this.entity.sacrificeCooldown = leader.sacrificeCooldown;
                }
                if (leader != null && this.entity.distanceToSqr(at) < 4) {
                    entity.setPraying(true);
                    Vec3 slightOffset = at.subtract(entity.position());
                    if (slightOffset.length() > 1F) {
                        slightOffset = slightOffset.normalize();
                    }
                    this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(slightOffset.scale(0.04F)));
                    entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(center));
                    BlockPos pos = leader.getLastSacrificePos() == null ? leader.blockPosition().above(5) : leader.getLastSacrificePos().above(5);
                    this.entity.setParticlePos(pos);
                    entity.level().broadcastEntityEvent(entity, (byte) 77);
                    if (worshippingTicks % 10 == 0) {
                        this.entity.level().broadcastEntityEvent(entity, (byte) 61);
                    }
                    this.entity.setWorshipTime(worshippingTicks + 1);
                } else {
                    this.entity.setWorshipTime(0);
                    attemptToFollowTicks++;
                }
            } else {
                this.entity.setParticlePos(center.above(5));
                Vec3 at = Vec3.atCenterOf(center);
                if (this.entity.distanceToSqr(at) < 4) {
                    Vec3 slightOffset = at.subtract(entity.position());
                    if (slightOffset.length() > 1F) {
                        slightOffset = slightOffset.normalize();
                    }
                    this.entity.setWorshipTime(worshippingTicks + 1);
                    this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(slightOffset.scale(0.04F)));
                    if (worshippingTicks > UnderzealotEntity.MAX_WORSHIP_TIME - 300 && this.entity.cloudCooldown <= 0 && entity.isSurroundedByPrayers()) {
                        if (entity.getFirstPassenger() instanceof UnderzealotSacrifice underzealotSacrifice) {
                            underzealotSacrifice.triggerSacrificeIn(300);
                            entity.cloudCooldown = 400;
                            entity.level().broadcastEntityEvent(entity, (byte) 62);
                        }
                    }
                } else {
                    this.entity.setWorshipTime(0);
                }
                entity.getNavigation().moveTo(center.getX(), center.getY(), center.getZ(), 1);
            }
        }
    }

    private Vec3 groundOf(Vec3 in) {
        BlockPos origin = BlockPos.containing(in);
        BlockPos.MutableBlockPos blockPos = origin.mutable();
        while (!entity.level().isEmptyBlock(blockPos) && blockPos.getY() < entity.level().getMaxBuildHeight()) {
            blockPos.move(0, 1, 0);
        }
        while (entity.level().isEmptyBlock(blockPos.below()) && blockPos.getY() > entity.level().getMinBuildHeight()) {
            blockPos.move(0, -1, 0);
        }
        return new Vec3(in.x, blockPos.getY(), in.z);
    }

    private int getPackPosition() {
        PackAnimal leader = entity.getPackLeader();
        int i = 1;
        while (leader.getAfterPackMember() != null) {
            if (leader.getAfterPackMember() == entity) {
                return i;
            }
            leader = leader.getAfterPackMember();
            i++;
        }
        return i;
    }

    public void stop() {
        attemptToFollowTicks = 0;
        this.entity.setWorshipTime(0);
        if (this.entity.sacrificeCooldown == 0) {
            this.entity.sacrificeCooldown = 100;
        }
        this.entity.getNavigation().stop();
        this.entity.ejectPassengers();
        this.entity.setPraying(false);
        this.entity.setParticlePos(null);
    }
}
