package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.CandicornEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CandicornMeleeGoal extends Goal {

    private CandicornEntity candicorn;

    private int chargeCooldown = 0;
    private int chargeTimeout = 0;
    private Vec3 startChargeTargetVec = Vec3.ZERO;
    private Vec3 startChargeFromVec = Vec3.ZERO;

    public CandicornMeleeGoal(CandicornEntity candicorn) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.candicorn = candicorn;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = candicorn.getTarget();
        return target != null && target.isAlive() && !candicorn.isBaby();
    }

    @Override
    public void stop() {
        candicorn.setRunning(false);
        candicorn.setCharging(false);
        chargeCooldown = 0;
        startChargeTargetVec = Vec3.ZERO;
        startChargeFromVec = Vec3.ZERO;
    }

    public void tick() {
        if(chargeCooldown > 0){
            chargeCooldown--;
        }
        if(chargeTimeout > 100){
            chargeTimeout = 0;
            candicorn.setCharging(false);
        }
        LivingEntity target = candicorn.getTarget();
        if (target != null && target.isAlive()) {
            double distance = candicorn.distanceTo(target);
            double chargeFromDistance = 15;
            double attackDistance = candicorn.getBbWidth() + target.getBbWidth() + 0.5F;
            candicorn.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            if (candicorn.getAnimation() == CandicornEntity.ANIMATION_STAB) {
                candicorn.getNavigation().stop();
                if(candicorn.getAnimationTick() > 8 && candicorn.getAnimationTick() <= 12){
                    if(target.hurt(target.damageSources().mobAttack(candicorn), (float) candicorn.getAttribute(Attributes.ATTACK_DAMAGE).getValue())){
                        target.knockback(0.6F, candicorn.getX() - target.getX(), candicorn.getZ() - target.getZ());
                    }
                }
            } else {
                if(candicorn.isCharging()){
                    chargeTimeout++;
                    candicorn.getNavigation().stop();
                    Vec3 sub = startChargeTargetVec.subtract(startChargeFromVec);
                    Vec3 delta = sub.normalize().scale(0.85);
                    candicorn.setDeltaMovement(candicorn.getDeltaMovement().scale(0.9).add(delta));
                    candicorn.setChargeYaw(Mth.wrapDegrees((float)(Mth.atan2(sub.z, sub.x) * (double)(180F / (float)Math.PI)) - 90.0F));
                    if((distance < attackDistance || candicorn.distanceToSqr(startChargeTargetVec) < attackDistance * attackDistance) && candicorn.getChargeProgress(1.0F) == 1.0F && candicorn.hasLineOfSight(target)){
                        chargeCooldown = 100;
                        candicorn.setCharging(false);
                    }
                    candicorn.setRunning(true);
                }else if(distance > attackDistance && distance < chargeFromDistance && chargeCooldown <= 0 && !candicorn.isCharging() && candicorn.hasLineOfSight(target)){
                    candicorn.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                    candicorn.setCharging(true);
                    startChargeTargetVec = target.position();
                    startChargeFromVec = candicorn.position();
                }else if (distance < attackDistance && candicorn.getAnimation() == IAnimatedEntity.NO_ANIMATION && candicorn.hasLineOfSight(target) && !candicorn.isCharging()) {
                    candicorn.setAnimation(CandicornEntity.ANIMATION_STAB);
                }else if (distance > attackDistance) {
                    this.candicorn.getNavigation().moveTo(target, 1.0D);
                    candicorn.setRunning(true);
                } else {
                    candicorn.setRunning(false);
                }
            }
        }
    }

}