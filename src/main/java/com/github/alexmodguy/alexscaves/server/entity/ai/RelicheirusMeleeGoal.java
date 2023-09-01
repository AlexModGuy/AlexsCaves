package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.RelicheirusEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TrilocarisEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class RelicheirusMeleeGoal extends Goal {

    private RelicheirusEntity relicheirus;

    public RelicheirusMeleeGoal(RelicheirusEntity relicheirus) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.relicheirus = relicheirus;
    }

    @Override
    public boolean canUse() {
        return relicheirus.getTarget() != null && relicheirus.getTarget().isAlive();
    }

    public void tick() {
        LivingEntity target = relicheirus.getTarget();
        if (target != null) {
            relicheirus.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            double dist = relicheirus.distanceTo(target);
            relicheirus.getNavigation().moveTo(target, 1.0D);
            if (dist < relicheirus.getBbWidth() + target.getBbWidth() + 1.0D) {
                if (relicheirus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    if (target instanceof TrilocarisEntity) {
                        relicheirus.setPeckY(target.getBlockY());
                        tryAnimation(RelicheirusEntity.ANIMATION_EAT_TRILOCARIS);
                    } else {
                        tryAnimation(relicheirus.getRandom().nextBoolean() ? RelicheirusEntity.ANIMATION_MELEE_SLASH_1 : RelicheirusEntity.ANIMATION_MELEE_SLASH_2);
                    }
                }
            }
            if (relicheirus.getAnimation() == RelicheirusEntity.ANIMATION_MELEE_SLASH_1 || relicheirus.getAnimation() == RelicheirusEntity.ANIMATION_MELEE_SLASH_2) {
                if (relicheirus.getAnimationTick() > 7 && relicheirus.getAnimationTick() <= 10) {
                    checkAndDealDamage(target);
                }
            }
        }
    }

    private void checkAndDealDamage(LivingEntity target) {
        if (relicheirus.hasLineOfSight(target) && relicheirus.distanceTo(target) < relicheirus.getBbWidth() + target.getBbWidth() + 2.0D) {
            relicheirus.playSound(ACSoundRegistry.RELICHEIRUS_SCRATCH.get());
            target.hurt(target.damageSources().mobAttack(relicheirus), (float) relicheirus.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            target.knockback(0.5D, relicheirus.getX() - target.getX(), relicheirus.getZ() - target.getZ());
        }
    }

    private boolean tryAnimation(Animation animation) {
        if (relicheirus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            relicheirus.setAnimation(animation);
            return true;
        }
        return false;
    }
}
