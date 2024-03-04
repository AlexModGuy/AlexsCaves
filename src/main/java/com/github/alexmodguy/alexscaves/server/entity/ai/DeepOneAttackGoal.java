package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class DeepOneAttackGoal extends Goal {

    private DeepOneBaseEntity deepOne;

    public DeepOneAttackGoal(DeepOneBaseEntity deepOne) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.deepOne = deepOne;
    }

    @Override
    public boolean canUse() {
        return deepOne.getTarget() != null && deepOne.getTarget().isAlive() && !deepOne.isTradingLocked();
    }

    @Override
    public void stop(){
        super.stop();
        deepOne.setSoundsAngry(false);
    }

    public void tick() {
        LivingEntity target = deepOne.getTarget();
        if (target != null) {
            deepOne.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 20.0F, (float) deepOne.getMaxHeadXRot());
            deepOne.startAttackBehavior(target);
            deepOne.setSoundsAngry(true);
            if(deepOne.distanceTo(target) <= 16){
                SubmarineEntity.alertSubmarineMountOf(target);
            }
        }
    }
}
