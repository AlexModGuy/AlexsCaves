package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TremorzillaAttackGoal  extends Goal {

    private TremorzillaEntity tremorzilla;
    private Vec3 lastNavToPos;

    public TremorzillaAttackGoal(TremorzillaEntity tremorzilla) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.tremorzilla = tremorzilla;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = tremorzilla.getTarget();
        return target != null && target.isAlive() && !tremorzilla.isDancing();
    }

    public void start() {
        lastNavToPos = null;
    }

    public void stop() {
    }

    public void tick() {
        LivingEntity target = tremorzilla.getTarget();
        if (target != null) {
            double dist = tremorzilla.distanceTo(target);
            float combinedDist = tremorzilla.getBbWidth() + target.getBbWidth();
            if(!tremorzilla.isFiring()){
                if(tremorzilla.isPowered() && !tremorzilla.wantsToUseBeamFromServer && tremorzilla.getRandom().nextInt(100) == 0 && !tremorzilla.isBaby() && !tremorzilla.isInSittingPose()){
                    tremorzilla.wantsToUseBeamFromServer = true;
                }
                if(!tremorzilla.wantsToUseBeamFromServer && tremorzilla.getAnimation() != TremorzillaEntity.ANIMATION_RIGHT_TAIL && tremorzilla.getAnimation() != TremorzillaEntity.ANIMATION_LEFT_TAIL && tremorzilla.getAnimation() != TremorzillaEntity.ANIMATION_LEFT_STOMP && tremorzilla.getAnimation() != TremorzillaEntity.ANIMATION_RIGHT_STOMP){
                    tremorzilla.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 1.0F, (float) tremorzilla.getMaxHeadXRot());
                }
                if(lastNavToPos == null || tremorzilla.getNavigation().isDone() && dist > combinedDist + 1.0D  || lastNavToPos.distanceTo(target.position()) > tremorzilla.getBbWidth() - 1.0D){
                    tremorzilla.getNavigation().moveTo(target, 1.0D);
                }
            }
            if (dist < combinedDist + 3.0D && !tremorzilla.isFiring()) {
                if (tremorzilla.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    float decision = tremorzilla.getRandom().nextFloat();
                    if(decision < 0.25){
                        tryAnimation(tremorzilla.getRandom().nextBoolean() ? TremorzillaEntity.ANIMATION_LEFT_SCRATCH : TremorzillaEntity.ANIMATION_RIGHT_SCRATCH);
                    }else if(decision < 0.5 && !tremorzilla.isSwimming() && !tremorzilla.isBaby()){
                        tryAnimation(tremorzilla.getRandom().nextBoolean() ? TremorzillaEntity.ANIMATION_LEFT_STOMP : TremorzillaEntity.ANIMATION_RIGHT_STOMP);
                    }else if(decision < 0.75 && !tremorzilla.isSwimming() && !tremorzilla.isBaby()){
                        tryAnimation(tremorzilla.getRandom().nextBoolean() ? TremorzillaEntity.ANIMATION_LEFT_TAIL : TremorzillaEntity.ANIMATION_RIGHT_TAIL);
                    }else{
                        tryAnimation(TremorzillaEntity.ANIMATION_BITE);
                    }
                }
            }
            if(!tremorzilla.wantsToUseBeamFromServer && !tremorzilla.isBaby()){
                tremorzilla.tryRoar();
            }
        }
    }

    private boolean tryAnimation(Animation animation) {
        if (tremorzilla.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            tremorzilla.setAnimation(animation);
            return true;
        }
        return false;
    }
}

