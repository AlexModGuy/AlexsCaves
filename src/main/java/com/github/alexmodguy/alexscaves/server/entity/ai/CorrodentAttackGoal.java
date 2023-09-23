package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.CorrodentEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CorrodentAttackGoal extends Goal {
    private CorrodentEntity entity;
    private boolean burrowing = false;
    private int burrowCheckTime = 0;

    private int evadeFor = 0;

    public CorrodentAttackGoal(CorrodentEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));

    }

    @Override
    public boolean canUse() {
        return entity.getTarget() != null && entity.getTarget().isAlive() && entity.fleeLightFor <= 0;
    }

    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target != null) {
            double dist = entity.distanceTo(target);
            float f = entity.getBbWidth() + target.getBbWidth();
            if (burrowCheckTime++ > 40 && entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                burrowCheckTime = 0;
                if (!burrowing) {
                    if (entity.onGround() && dist > f && (!entity.canReach(target.blockPosition()) || dist > 20 || entity.getRandom().nextInt(20) == 0)) {
                        burrowing = true;
                        evadeFor = 60 + entity.getRandom().nextInt(40);
                    }
                } else {
                    if (dist < f + 1 || entity.getRandom().nextInt(10) == 0) {
                        burrowing = false;
                        evadeFor = 0;
                    }
                }
            }
            if (evadeFor > 0) {
                evadeFor--;
                burrowing = true;
                this.entity.setDigging(true);
                if (this.entity.getNavigation().isDone()) {
                    Vec3 vec3 = generateEvadePosition(target.blockPosition());
                    if (vec3 != null) {
                        this.entity.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0D);
                    }
                }
            } else if (burrowing) {
                if (this.entity.onGround()) {
                    this.entity.setDigging(true);
                }
                this.entity.getNavigation().moveTo(target, 2D);
                if (!this.entity.isInWall()) {
                    this.entity.setDigging(false);
                    this.burrowing = false;
                }
            } else {
                if (!this.entity.isInWall()) {
                    this.entity.setDigging(false);
                } else {
                    this.entity.setDigging(true);
                    this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(0, 0.1D, 0));
                }
                this.entity.getNavigation().moveTo(target, 1.5F);
            }
            if (dist < f + 1.0F) {
                tryAnimation(CorrodentEntity.ANIMATION_BITE);
            }
            if (entity.getAnimation() == CorrodentEntity.ANIMATION_BITE) {
                this.entity.setDigging(false);
                if (entity.getAnimationTick() == 8) {
                    checkAndDealDamage(target, 1.5F);
                    if (entity.getRandom().nextBoolean()) {
                        evadeFor = 60 + entity.getRandom().nextInt(40);
                    }
                }
            }
        }
    }


    private Vec3 generateEvadePosition(BlockPos around) {
        BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 10; i++) {
            check.move(around);
            check.move(entity.getRandom().nextInt(16) - 8, entity.getRandom().nextInt(16) - 8, entity.getRandom().nextInt(16) - 8);
            if (!entity.level().isLoaded(check) || check.getY() < entity.level().getMinBuildHeight()) {
                break;
            }
            while (entity.level().isEmptyBlock(check) && entity.level().isLoaded(check) && check.getY() > entity.level().getMinBuildHeight() - 1) {
                check.move(0, -1, 0);
            }
            if (CorrodentEntity.isSafeDig(entity.level(), check.immutable()) && entity.canReach(check)) {
                return Vec3.atCenterOf(check.immutable());
            }
        }
        return null;
    }


    private void checkAndDealDamage(LivingEntity target, float multiplier) {
        if (entity.hasLineOfSight(target) && entity.distanceTo(target) < entity.getBbWidth() + target.getBbWidth() + 1.0D) {
            float f = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * multiplier;
            target.hurt(target.damageSources().mobAttack(entity), f);
            target.knockback(0.2D + 0.3D * multiplier, entity.getX() - target.getX(), entity.getZ() - target.getZ());
            Entity entity = target.getVehicle();
            if (entity != null) {
                entity.setDeltaMovement(target.getDeltaMovement());
                entity.hurt(target.damageSources().mobAttack(this.entity), f * 0.5F);
            }
            burrowing = true;
        }
    }

    public void stop() {
        burrowing = false;
        burrowCheckTime = 0;
        evadeFor = 0;
    }

    private boolean tryAnimation(Animation animation) {
        if (entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            entity.setAnimation(animation);
            entity.playSound(ACSoundRegistry.CORRODENT_ATTACK.get());
            return true;
        }
        return false;
    }
}
