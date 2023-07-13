package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class MobTargetUntamedGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final TamableAnimal tamableMob;

    public MobTargetUntamedGoal(TamableAnimal tamableAnimal, Class<T> clazz, int chance, boolean seeCheck, boolean reachCheck, @Nullable Predicate<LivingEntity> entityPredicate) {
        super(tamableAnimal, clazz, chance, seeCheck, reachCheck, entityPredicate);
        this.tamableMob = tamableAnimal;
    }

    public boolean canUse() {
        return !this.tamableMob.isTame() && super.canUse();
    }

    public boolean canContinueToUse() {
        return this.targetConditions != null ? this.targetConditions.test(this.mob, this.target) : super.canContinueToUse();
    }
}