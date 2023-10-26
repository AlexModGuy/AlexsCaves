package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.FlyingAnimal;

import java.util.EnumSet;

public class TremorsaurusMeleeGoal extends Goal {

    private TremorsaurusEntity tremorsaurus;

    public TremorsaurusMeleeGoal(TremorsaurusEntity tremorsaurus) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.tremorsaurus = tremorsaurus;
    }

    @Override
    public boolean canUse() {
        return tremorsaurus.getTarget() != null && tremorsaurus.getTarget().isAlive() && !tremorsaurus.isDancing();
    }

    public void start() {
        tremorsaurus.setRunning(!tremorsaurus.isVehicle());
    }

    public void stop() {
        tremorsaurus.setRunning(false);
    }

    public void tick() {
        LivingEntity target = tremorsaurus.getTarget();
        if (target != null) {
            boolean grab = isFlyingTarget();
            tremorsaurus.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            tremorsaurus.tryRoar();
            double dist = tremorsaurus.distanceTo(target);
            tremorsaurus.getNavigation().moveTo(target, 1.0D);

            if (dist < tremorsaurus.getBbWidth() + target.getBbWidth() + 1.0D) {
                if (tremorsaurus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    if ((tremorsaurus.getRandom().nextBoolean() || Math.max(target.getBbHeight(), target.getBbWidth()) >= 2.0F) && !grab || tremorsaurus.isBaby()) {
                        tryAnimation(TremorsaurusEntity.ANIMATION_BITE);
                    } else {
                        tryAnimation(TremorsaurusEntity.ANIMATION_SHAKE_PREY);
                    }
                }
            }
            if (tremorsaurus.getAnimation() == TremorsaurusEntity.ANIMATION_BITE) {
                if (tremorsaurus.getAnimationTick() > 10 && tremorsaurus.getAnimationTick() <= 12) {
                    checkAndDealDamage(target);
                }
            }
        }
    }

    private void checkAndDealDamage(LivingEntity target) {
        if (tremorsaurus.hasLineOfSight(target) && tremorsaurus.distanceTo(target) < tremorsaurus.getBbWidth() + target.getBbWidth() + 2.0D) {
            tremorsaurus.playSound(ACSoundRegistry.TREMORSAURUS_BITE.get());
            target.hurt(target.damageSources().mobAttack(tremorsaurus), (float) tremorsaurus.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            target.knockback(0.5D, tremorsaurus.getX() - target.getX(), tremorsaurus.getZ() - target.getZ());
        }
    }

    private boolean isFlyingTarget() {
        return tremorsaurus.getTarget() instanceof FlyingAnimal;
    }

    private boolean tryAnimation(Animation animation) {
        if (tremorsaurus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            tremorsaurus.setAnimation(animation);
            return true;
        }
        return false;
    }
}
