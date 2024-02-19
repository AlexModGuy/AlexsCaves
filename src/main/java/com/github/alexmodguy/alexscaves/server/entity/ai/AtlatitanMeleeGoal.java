package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.AtlatitanEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SauropodBaseEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AtlatitanMeleeGoal extends Goal {

    private AtlatitanEntity atlatitan;

    public AtlatitanMeleeGoal(AtlatitanEntity atlatitan) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.atlatitan = atlatitan;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = atlatitan.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        atlatitan.turningFast = false;
    }

    public void tick() {
        LivingEntity target = atlatitan.getTarget();
        if (target != null && target.isAlive()) {
            double distance = atlatitan.distanceTo(target);
            double attackDistance = atlatitan.getBbWidth() + target.getBbWidth();
            if(this.atlatitan.getAnimation() == SauropodBaseEntity.ANIMATION_LEFT_KICK || this.atlatitan.getAnimation() == SauropodBaseEntity.ANIMATION_RIGHT_KICK || this.atlatitan.getAnimation() == SauropodBaseEntity.ANIMATION_LEFT_WHIP || this.atlatitan.getAnimation() == SauropodBaseEntity.ANIMATION_RIGHT_WHIP){
                atlatitan.turningFast = true;
                Vec3 vec3 = target.position().subtract(atlatitan.position());
                this.atlatitan.yBodyRot = Mth.approachDegrees(atlatitan.yBodyRot, -((float)Mth.atan2(vec3.x, vec3.z)) * (180F / (float)Math.PI), 15);
                this.atlatitan.yBodyRotO = this.atlatitan.yBodyRot;
                this.atlatitan.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
            }else{
                atlatitan.turningFast = false;
            }
            if(distance > attackDistance){
                this.atlatitan.getNavigation().moveTo(target, 1.0D);
            }
            if(this.atlatitan.getAnimation() == IAnimatedEntity.NO_ANIMATION){
                if(distance < attackDistance + 4.0D){
                    float random = this.atlatitan.getRandom().nextFloat();
                    if(random < 0.5F && distance < attackDistance + 1.0D){
                        this.atlatitan.playSound(ACSoundRegistry.ATLATITAN_KICK.get(), 3.0F, atlatitan.getVoicePitch());
                        this.atlatitan.setAnimation(this.atlatitan.getRandom().nextBoolean() ? AtlatitanEntity.ANIMATION_LEFT_KICK : AtlatitanEntity.ANIMATION_RIGHT_KICK);
                    }else{
                        this.atlatitan.playSound(ACSoundRegistry.ATLATITAN_TAIL.get(), 3.0F, atlatitan.getVoicePitch());
                        this.atlatitan.setAnimation(this.atlatitan.getRandom().nextBoolean() ? AtlatitanEntity.ANIMATION_RIGHT_WHIP : AtlatitanEntity.ANIMATION_LEFT_WHIP);
                    }
                }
            }
        }
    }

}