package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GrottoceratopsMeleeGoal extends Goal {

    private GrottoceratopsEntity grottoceratops;
    private float startTailYaw = 0;

    public GrottoceratopsMeleeGoal(GrottoceratopsEntity grottoceratops) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.grottoceratops = grottoceratops;
    }

    @Override
    public boolean canUse() {
        return grottoceratops.getTarget() != null && grottoceratops.getTarget().isAlive();
    }

    public void stop() {
        startTailYaw = 0;
    }

    public void tick() {
        LivingEntity target = grottoceratops.getTarget();
        if (target != null) {
            double dist = grottoceratops.distanceTo(target);

            if (dist < grottoceratops.getBbWidth() + target.getBbWidth() + 3.0D) {
                if (grottoceratops.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    if (grottoceratops.getRandom().nextBoolean()) {
                        tryAnimation(GrottoceratopsEntity.ANIMATION_MELEE_RAM);
                    } else {
                        boolean left = grottoceratops.getRandom().nextBoolean();
                        startTailYaw = grottoceratops.getYRot() + (left ? 30 : -30);
                        tryAnimation(left ? GrottoceratopsEntity.ANIMATION_MELEE_TAIL_1 : GrottoceratopsEntity.ANIMATION_MELEE_TAIL_2);
                    }
                }
            }
            if (grottoceratops.getAnimation() == GrottoceratopsEntity.ANIMATION_MELEE_RAM) {
                if (grottoceratops.getAnimationTick() > 10 && grottoceratops.getAnimationTick() <= 12) {
                    checkAndDealDamage(target, 1.0F);
                }
            }
            if (grottoceratops.getAnimation() == GrottoceratopsEntity.ANIMATION_MELEE_TAIL_1 || grottoceratops.getAnimation() == GrottoceratopsEntity.ANIMATION_MELEE_TAIL_2) {
                if (grottoceratops.getAnimationTick() > 10 && grottoceratops.getAnimationTick() <= 12) {
                    checkAndDealDamage(target, 1.5F);
                }
            } else {
                grottoceratops.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                grottoceratops.getNavigation().moveTo(target, 1.35D);
            }
        }
    }

    private void checkAndDealDamage(LivingEntity target, float multiplier) {
        if (grottoceratops.hasLineOfSight(target) && grottoceratops.distanceTo(target) < grottoceratops.getBbWidth() + target.getBbWidth() + 1.0D) {
            grottoceratops.playSound(ACSoundRegistry.GROTTOCERATOPS_ATTACK.get());
            target.hurt(target.damageSources().mobAttack(grottoceratops), (float) grottoceratops.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * multiplier);
            target.knockback(0.8D + 0.5D * multiplier, grottoceratops.getX() - target.getX(), grottoceratops.getZ() - target.getZ());
        }
    }

    private boolean tryAnimation(Animation animation) {
        if (grottoceratops.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            grottoceratops.setAnimation(animation);
            return true;
        }
        return false;
    }
}
