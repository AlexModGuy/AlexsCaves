package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.HullbreakerEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class HullbreakerInspectMobGoal extends Goal {
    private HullbreakerEntity entity;
    private Vec3 startCirclingAt;
    private LivingEntity inspectingTarget;
    private boolean clockwise;
    private int phaseTime;
    private int maxPhaseTime;
    private boolean staring = false;

    public HullbreakerInspectMobGoal(HullbreakerEntity hullbreaker) {
        this.entity = hullbreaker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        long worldTime = entity.level().getGameTime() % 10;
        if (entity.getRandom().nextInt(60) != 0 && worldTime != 0 && (target == null || !target.isAlive())) {
            return false;
        }
        AABB aabb = entity.getBoundingBox().inflate(80);
        List<LivingEntity> list = entity.level().getEntitiesOfClass(LivingEntity.class, aabb, HullbreakerEntity.GLOWING_TARGET);
        if (!list.isEmpty()) {
            LivingEntity closest = null;
            for (LivingEntity mob : list) {
                if ((closest == null || mob.distanceToSqr(entity) < closest.distanceToSqr(entity)) && entity.hasLineOfSight(mob) && !mob.is(entity)) {
                    closest = mob;
                }
            }
            inspectingTarget = closest;
            return inspectingTarget != null;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = entity.getTarget();
        return inspectingTarget != null && inspectingTarget.isAlive() && (target == null || !target.isAlive());
    }

    public void start() {
        staring = true;
        clockwise = entity.getRandom().nextBoolean();
        phaseTime = 0;
        maxPhaseTime = 60 + 40 * Math.min(0, 5 - entity.getInterestLevel());
    }

    public void tick() {
        double distance = entity.distanceTo(inspectingTarget);
        if (entity.getInterestLevel() >= 5 && HullbreakerEntity.GLOWING_TARGET.test(inspectingTarget)) {
            if (!(inspectingTarget instanceof Player) || !((Player) inspectingTarget).isCreative()) {
                entity.setTarget(inspectingTarget);
            }
            inspectingTarget = null;
            return;
        }
        if (entity.getRandom().nextInt(20) == 0 && !HullbreakerEntity.GLOWING_TARGET.test(inspectingTarget)) {
            inspectingTarget = null;
        } else {
            if (entity.getAnimation() == HullbreakerEntity.ANIMATION_PUZZLE && entity.getAnimationTick() > 50) {
                phaseTime = maxPhaseTime;
            }
            if (phaseTime++ > maxPhaseTime) {
                entity.setInterestLevel(entity.getInterestLevel() + 1);
                staring = entity.getRandom().nextBoolean() && !staring;
                phaseTime = 0;
                startCirclingAt = inspectingTarget.getEyePosition();
                maxPhaseTime = staring ? 120 : 120 + 80 * Math.min(0, 5 - entity.getInterestLevel());
            }
            if (staring) {
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, inspectingTarget.getEyePosition());
                if (isPreyWatching() && distance < 18) {
                    if (entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        entity.setAnimation(HullbreakerEntity.ANIMATION_PUZZLE);
                    }
                    entity.getNavigation().stop();
                } else if (entity.getNavigation().isDone()) {
                    Vec3 frontVision = inspectingTarget.getEyePosition().add(inspectingTarget.getLookAngle().scale(12.0F));
                    entity.getNavigation().moveTo(frontVision.x, frontVision.y, frontVision.z, 1.0F);
                }
            } else {
                if (startCirclingAt == null) {
                    startCirclingAt = inspectingTarget.getEyePosition();
                }
                Vec3 circle = orbitAroundPos(inspectingTarget.getEyePosition(), 12 + Math.min(0, 5 - entity.getInterestLevel()) * 3);
                entity.getNavigation().moveTo(circle.x, circle.y, circle.z, 1.4F);
                entity.setYHeadRot(entity.yBodyRot + (clockwise ? 30 : -30));

            }
            SubmarineEntity.alertSubmarineMountOf(inspectingTarget);
        }
    }

    public boolean isPreyWatching() {
        if (!(inspectingTarget instanceof Player)) {
            return true;
        }
        Entity lowestPrey = inspectingTarget.getRootVehicle();
        Vec3 vec3 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        Vec3 vec31 = new Vec3(lowestPrey.getX(), lowestPrey.getEyeY(), lowestPrey.getZ());
        if (vec31.distanceTo(vec3) > 128.0D) {
            return false;
        } else {
            return entity.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, lowestPrey)).getType() == HitResult.Type.MISS;
        }
    }


    public void stop() {
        LivingEntity target = entity.getTarget();
        if (target == null || !target.isAlive()) {
            entity.setInterestLevel(0);
        }
    }

    public Vec3 orbitAroundPos(Vec3 target, float circleDistance) {
        final float angle = 3 * (float) (Math.PI * (clockwise ? -phaseTime : phaseTime) / (float) maxPhaseTime);
        final double extraX = circleDistance * Mth.sin((angle));
        final double extraZ = circleDistance * Mth.cos(angle);
        return startCirclingAt.add(extraX, -1, extraZ);
    }
}
