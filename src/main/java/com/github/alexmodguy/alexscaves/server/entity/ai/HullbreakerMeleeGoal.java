package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.HullbreakerEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class HullbreakerMeleeGoal extends Goal {

    private HullbreakerEntity hullbreaker;

    public HullbreakerMeleeGoal(HullbreakerEntity hullbreaker) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.hullbreaker = hullbreaker;
    }

    @Override
    public boolean canUse() {
        return hullbreaker.getTarget() != null && hullbreaker.getTarget().isAlive();
    }


    public void tick() {
        LivingEntity target = hullbreaker.getTarget();
        if (target != null) {
            double dist = hullbreaker.distanceTo(target);
            float f = hullbreaker.getBbWidth() + target.getBbWidth();
            if (dist < f + 7.0D) {
                if (hullbreaker.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    tryAnimation(hullbreaker.getRandom().nextBoolean() && hullbreaker.hasLineOfSight(target) ? HullbreakerEntity.ANIMATION_BITE : HullbreakerEntity.ANIMATION_BASH);
                }
            }
            if (dist > f + 2) {
                hullbreaker.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                hullbreaker.getNavigation().moveTo(target, 1.6D);
            }
            if (hullbreaker.getAnimation() == HullbreakerEntity.ANIMATION_BITE) {
                if (hullbreaker.getAnimationTick() > 10 && hullbreaker.getAnimationTick() <= 14) {
                    checkAndDealDamage(target, 1.0F);
                }
            }
            if (hullbreaker.getAnimation() == HullbreakerEntity.ANIMATION_BASH) {
                if (hullbreaker.getAnimationTick() > 10 && hullbreaker.getAnimationTick() <= 12) {
                    checkAndDealDamage(target, 1.5F);
                }
            }
            SubmarineEntity.alertSubmarineMountOf(target);
        }
    }

    public void start() {
        hullbreaker.setInterestLevel(6);
    }

    public void stop() {
        hullbreaker.setInterestLevel(0);
    }

    private void checkAndDealDamage(LivingEntity target, float multiplier) {
        if (hullbreaker.hasLineOfSight(target) && hullbreaker.distanceTo(target) < hullbreaker.getBbWidth() + target.getBbWidth() + 5.0D) {
            float f = (float) hullbreaker.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * multiplier;
            target.hurt(target.damageSources().mobAttack(hullbreaker), f);
            target.knockback(0.8D + 0.5D * multiplier, hullbreaker.getX() - target.getX(), hullbreaker.getZ() - target.getZ());
            Entity entity = target.getVehicle();
            if (entity != null) {
                entity.setDeltaMovement(target.getDeltaMovement());
                entity.hurt(target.damageSources().mobAttack(hullbreaker), f * 0.5F);
            }
        }
    }

    private boolean tryAnimation(Animation animation) {
        if (hullbreaker.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            hullbreaker.setAnimation(animation);
            if(hullbreaker.isInWaterOrBubble()){
                hullbreaker.playSound(ACSoundRegistry.HULLBREAKER_ATTACK.get());
            }
            return true;
        }
        return false;
    }
}
