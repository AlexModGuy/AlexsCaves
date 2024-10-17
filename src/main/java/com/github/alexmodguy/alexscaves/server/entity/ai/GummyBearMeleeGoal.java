package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GummyBearEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SweetishFishEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GummyBearMeleeGoal extends Goal {

    private final GummyBearEntity gummyBear;

    public GummyBearMeleeGoal(GummyBearEntity gummyBear) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.gummyBear = gummyBear;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = gummyBear.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        gummyBear.setSprinting(false);
    }

    @Override
    public void start() {
    }

    public void tick() {
        LivingEntity target = gummyBear.getTarget();
        if (target != null && target.isAlive()) {
            double distance = gummyBear.distanceTo(target);
            double attackDistance = gummyBear.getBbWidth() + target.getBbWidth() + 1.0D;
            gummyBear.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            if(target instanceof SweetishFishEntity){
                if(distance >= 5.0F && gummyBear.getHeldMobId() == -1){
                    gummyBear.getNavigation().moveTo(target, 1.3D);
                }else{
                    gummyBear.getNavigation().stop();
                }
                if(distance < 5.0F && gummyBear.getAnimation() == IAnimatedEntity.NO_ANIMATION && gummyBear.getHeldMobId() == -1){
                    if(distance <= 1.0F){
                        gummyBear.setAnimation(GummyBearEntity.ANIMATION_EAT);
                    }else{
                        gummyBear.setAnimation(GummyBearEntity.ANIMATION_FISH);
                    }
                }
                if(gummyBear.getAnimation() == GummyBearEntity.ANIMATION_FISH){
                    if(gummyBear.getAnimationTick() == 16){
                        gummyBear.setDeltaMovement(gummyBear.getDeltaMovement().add(0, 0.3F, 0));
                    }
                    if(gummyBear.getAnimationTick() > 15 && gummyBear.getAnimationTick() <= 20){
                        Vec3 delta = target.position().subtract(gummyBear.position());
                        if(delta.length() > 1.0F){
                            delta = delta.normalize();
                        }
                        gummyBear.setDeltaMovement(gummyBear.getDeltaMovement().add(delta.scale(0.3F)));
                    }
                    if(gummyBear.getAnimationTick() > 16 && gummyBear.getAnimationTick() < 25){
                        if(distance < 1.5F){
                            gummyBear.setHeldMobId(target.getId());
                        }
                    }
                }
            }else{
                if (distance < attackDistance && gummyBear.hasLineOfSight(target)) {
                    if (gummyBear.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        gummyBear.setAnimation(gummyBear.getRandom().nextBoolean() ? GummyBearEntity.ANIMATION_MAUL : GummyBearEntity.ANIMATION_SWIPE);
                    }
                    if (gummyBear.getAnimation() == GummyBearEntity.ANIMATION_MAUL) {
                        if (gummyBear.getAnimationTick() > 5 && gummyBear.getAnimationTick() <= 7 || gummyBear.getAnimationTick() > 17 && gummyBear.getAnimationTick() <= 19) {
                            checkAndDealDamage(target);
                        }
                    }
                    if (gummyBear.getAnimation() == GummyBearEntity.ANIMATION_SWIPE) {
                        if (gummyBear.getAnimationTick() > 5 && gummyBear.getAnimationTick() <= 7 || gummyBear.getAnimationTick() > 15 && gummyBear.getAnimationTick() <= 17) {
                            checkAndDealDamage(target);
                        }
                    }
                } else {
                    gummyBear.getNavigation().moveTo(target, 1.3D);
                    gummyBear.setSprinting(true);
                }
            }
        }
    }


    private void checkAndDealDamage(LivingEntity target) {
        if (gummyBear.hasLineOfSight(target) && gummyBear.distanceTo(target) < gummyBear.getBbWidth() + target.getBbWidth() + 1) {
            target.hurt(target.damageSources().mobAttack(gummyBear), (float) gummyBear.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        }
    }
}