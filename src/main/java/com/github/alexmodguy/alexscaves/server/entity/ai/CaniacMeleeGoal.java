package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.CaniacEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.CaniacEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SauropodBaseEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CaniacMeleeGoal extends Goal {

    private CaniacEntity caniac;
    private float chaseTime = 0;

    public CaniacMeleeGoal(CaniacEntity caniac) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.caniac = caniac;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = caniac.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        caniac.setRunning(false);
        caniac.setArmSpinSpeed(0.0F);
        chaseTime = 0;
    }

    public void tick() {
        LivingEntity target = caniac.getTarget();
        if (target != null && target.isAlive()) {
            double distance = caniac.distanceTo(target);
            double attackDistance = caniac.getBbWidth() + target.getBbWidth();
            caniac.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            if(caniac.getAnimation() == CaniacEntity.ANIMATION_LUNGE){
                caniac.setArmSpinSpeed(0.0F);
                caniac.getNavigation().stop();
                if(caniac.getAnimationTick() > 15){
                    target.hasImpulse = true;
                    Vec3 delta = caniac.position().subtract(target.position());
                    if(distance < 10){
                        target.setDeltaMovement(target.getDeltaMovement().scale(0.3F).add(delta.scale(0.1)));
                        if(caniac.getAnimationTick() > 19 && caniac.getAnimationTick() <= 22 && caniac.hasLineOfSight(target) && distance < 3.5F){
                            target.hurt(caniac.damageSources().mobAttack(caniac), 3.0F);
                        }
                    }
                }else if(caniac.getAnimationTick() > 10 && caniac.getAnimationTick() <= 13){
                    caniac.hasImpulse = true;
                    Vec3 delta = target.position().subtract(caniac.position()).normalize();
                    caniac.setDeltaMovement(caniac.getDeltaMovement().add(delta.scale(1.3F).add(0, 0.35, 0)));
                }
            }else{
                chaseTime++;
                caniac.setArmSpinSpeed(Math.min(30F, chaseTime * 5));
                if(distance > attackDistance){
                    this.caniac.getNavigation().moveTo(target, 1.0D);
                    caniac.setRunning(true);
                    if(distance < 12 && distance > 4 && caniac.getAnimation() == IAnimatedEntity.NO_ANIMATION && caniac.getRandom().nextInt(15) == 0){
                        caniac.setAnimation(CaniacEntity.ANIMATION_LUNGE);
                    }
                }else {
                    caniac.setRunning(false);
                }
            }
        }
    }

}