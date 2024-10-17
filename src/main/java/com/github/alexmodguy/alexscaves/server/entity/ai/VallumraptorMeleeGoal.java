package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class VallumraptorMeleeGoal extends Goal {

    private VallumraptorEntity raptor;

    public VallumraptorMeleeGoal(VallumraptorEntity raptor) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.raptor = raptor;
    }

    @Override
    public boolean canUse() {
        return raptor.getTarget() != null && raptor.getTarget().isAlive() && !raptor.isDancing() && !(raptor.getTarget() instanceof Player player && player.isCreative());
    }

    public void stop() {
        raptor.setRunning(false);
        raptor.setLeaping(false);
    }

    public void tick() {
        LivingEntity target = raptor.getTarget();
        raptor.setRunning(true);
        if (target != null) {
            double dist = raptor.distanceTo(target);
            if (raptor.isLeaping()) {
                checkAndDealDamage(target);
                if (raptor.onGround() || raptor.isInWaterOrBubble()) {
                    raptor.setLeaping(false);
                }
            } else if (raptor.getAnimation() == VallumraptorEntity.ANIMATION_STARTLEAP) {
                raptor.getNavigation().stop();
                raptor.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                if (raptor.getAnimationTick() > 15 && raptor.onGround()) {
                    raptor.setLeaping(true);
                    raptor.playSound(ACSoundRegistry.VALLUMRAPTOR_ATTACK.get());
                    Vec3 vector3d = raptor.getDeltaMovement();
                    Vec3 vector3d1 = new Vec3(target.getX() - raptor.getX(), 0.0D, target.getZ() - raptor.getZ());
                    if (vector3d1.lengthSqr() > 1.0E-7D) {
                        vector3d1 = vector3d1.normalize().scale(0.9D).add(vector3d.scale(0.5D));
                    }
                    raptor.setDeltaMovement(vector3d1.x, 0.6F, vector3d1.z);
                }
            } else {
                raptor.getNavigation().moveTo(target, 1.0D);
                if (dist < raptor.getBbWidth() + target.getBbWidth() + 1) {
                    tryAnimation(raptor.getRandom().nextBoolean() ? VallumraptorEntity.ANIMATION_MELEE_BITE : raptor.getRandom().nextBoolean() ? VallumraptorEntity.ANIMATION_MELEE_SLASH_2 : VallumraptorEntity.ANIMATION_MELEE_SLASH_1);
                    if (raptor.getAnimation() == VallumraptorEntity.ANIMATION_MELEE_BITE && raptor.getAnimationTick() > 5 && raptor.getAnimationTick() <= 8) {
                        checkAndDealDamage(target);
                    }
                    if ((raptor.getAnimation() == VallumraptorEntity.ANIMATION_MELEE_SLASH_1 || raptor.getAnimation() == VallumraptorEntity.ANIMATION_MELEE_SLASH_2) && raptor.getAnimationTick() > 7 && raptor.getAnimationTick() <= 10) {
                        checkAndDealDamage(target);
                    }
                } else {
                    int jumpChance = raptor.isTame() ? 5 : 10;
                    if (dist > 3.0F && dist < 7.0F && raptor.getRandom().nextInt(jumpChance) == 0) {
                        tryAnimation(VallumraptorEntity.ANIMATION_STARTLEAP);
                    }
                }
            }
        }
    }

    private void checkAndDealDamage(LivingEntity target) {
        if (raptor.hasLineOfSight(target) && raptor.distanceTo(target) < raptor.getBbWidth() + target.getBbWidth() + 1) {
            raptor.playSound(ACSoundRegistry.VALLUMRAPTOR_SCRATCH.get());
            target.hurt(target.damageSources().mobAttack(raptor), (float) raptor.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        }
    }

    private boolean tryAnimation(Animation animation) {
        if (raptor.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            raptor.setAnimation(animation);
            return true;
        }
        return false;
    }
}
