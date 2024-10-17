package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GingerbreadManEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GingerbreadManAttackGoal extends Goal {

    private final GingerbreadManEntity gingerbreadMan;

    public GingerbreadManAttackGoal(GingerbreadManEntity gingerbreadMan) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.gingerbreadMan = gingerbreadMan;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = gingerbreadMan.getTarget();
        return target != null && target.isAlive() && gingerbreadMan.getFleeFor() <= 0 && !gingerbreadMan.isMovementBlocked();
    }

    public void tick() {
        LivingEntity target = gingerbreadMan.getTarget();
        if (target != null && target.isAlive()) {
            double distance = gingerbreadMan.distanceTo(target);
            double attackDistance = gingerbreadMan.getBbWidth() + target.getBbWidth() + 1.0F;
            gingerbreadMan.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            if(distance < attackDistance && gingerbreadMan.hasLineOfSight(target)){
                if(gingerbreadMan.getAnimation() == gingerbreadMan.getAnimationForHand(false) && gingerbreadMan.getAnimationTick() == 8){
                    if(gingerbreadMan.doHurtTarget(target)){
                        gingerbreadMan.playSound(ACSoundRegistry.GINGERBREAD_MAN_ATTACK.get());
                        gingerbreadMan.fleeFromFor(target, gingerbreadMan.isOvenSpawned() ? 0 : 120 + gingerbreadMan.getRandom().nextInt(60));
                    }
                }
                if(gingerbreadMan.getAnimation() == IAnimatedEntity.NO_ANIMATION){
                    gingerbreadMan.setAnimation(gingerbreadMan.getAnimationForHand(false));
                }
            }else{
                gingerbreadMan.getNavigation().moveTo(target, 1.0D);
            }
        }
    }
}