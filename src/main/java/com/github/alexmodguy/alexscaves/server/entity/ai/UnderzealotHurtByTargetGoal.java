package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.ForsakenEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.WatcherEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;

public class UnderzealotHurtByTargetGoal extends HurtByTargetGoal {

    private UnderzealotEntity underzealot;

    public UnderzealotHurtByTargetGoal(UnderzealotEntity underzealot) {
        super(underzealot, UnderzealotEntity.class, WatcherEntity.class, ForsakenEntity.class);
        this.setAlertOthers();
        this.underzealot = underzealot;
    }

    protected boolean canAttack(@Nullable LivingEntity target, TargetingConditions targetingConditions) {
        return !underzealot.isTargetingBlocked() && super.canAttack(target, targetingConditions);
    }

    protected void alertOther(Mob mob, LivingEntity target) {
        if (mob instanceof UnderzealotEntity otherUnderzealot && otherUnderzealot.isTargetingBlocked()) {
            return;
        }
        super.alertOther(mob, target);
    }
}
