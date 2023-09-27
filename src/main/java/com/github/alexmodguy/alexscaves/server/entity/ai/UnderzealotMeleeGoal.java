package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class UnderzealotMeleeGoal extends Goal {

    private UnderzealotEntity entity;
    private boolean shouldBurrow = false;

    public UnderzealotMeleeGoal(UnderzealotEntity entity) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.getTarget() != null && entity.getTarget().isAlive();
    }


    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target != null) {
            double dist = entity.distanceTo(target);
            float f = entity.getBbWidth() + target.getBbWidth();
            if (shouldBurrow && entity.onGround()) {
                shouldBurrow = false;
                entity.setBuried(true);
                entity.reemergeAt(entity.findReemergePos(target.blockPosition(), 15), 20 + entity.getRandom().nextInt(60));
            } else if (!entity.isBuried()) {
                if (entity.isDiggingInProgress()) {
                    entity.getNavigation().stop();
                } else {
                    entity.getNavigation().moveTo(target, 1.3D);
                    if (dist < f + 1.0F) {
                        tryAnimation(entity.getRandom().nextBoolean() ? UnderzealotEntity.ANIMATION_ATTACK_0 : UnderzealotEntity.ANIMATION_ATTACK_1);
                    }
                    if ((entity.getAnimation() == UnderzealotEntity.ANIMATION_ATTACK_0 || entity.getAnimation() == UnderzealotEntity.ANIMATION_ATTACK_1) && entity.getAnimationTick() == 8) {
                        checkAndDealDamage(target, 1.0F);
                    }
                }
            }
        }
    }


    private void checkAndDealDamage(LivingEntity target, float multiplier) {
        if (entity.hasLineOfSight(target) && entity.distanceTo(target) < entity.getBbWidth() + target.getBbWidth() + 1.0D) {
            float f = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * multiplier;
            target.hurt(target.damageSources().mobAttack(entity), f);
            target.knockback(0.2D + 0.3D * multiplier, entity.getX() - target.getX(), entity.getZ() - target.getZ());
            shouldBurrow = entity.level().random.nextBoolean();
            Entity vehicle = target.getVehicle();
            if (vehicle != null) {
                vehicle.setDeltaMovement(target.getDeltaMovement());
                vehicle.hurt(target.damageSources().mobAttack(this.entity), f * 0.5F);
            }
        }
    }

    public void stop() {
        shouldBurrow = false;
    }

    private boolean tryAnimation(Animation animation) {
        if (entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            entity.setAnimation(animation);
            entity.playSound(ACSoundRegistry.UNDERZEALOT_ATTACK.get());
            return true;
        }
        return false;
    }
}
