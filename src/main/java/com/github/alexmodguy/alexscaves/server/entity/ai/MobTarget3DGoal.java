package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public class MobTarget3DGoal extends NearestAttackableTargetGoal {

    public MobTarget3DGoal(Mob mob, Class targetClass, boolean sight) {
        super(mob, targetClass, sight);
    }


    public MobTarget3DGoal(Mob mob, Class targetClass, boolean sight, int chance, Predicate<LivingEntity> predicate) {
        super(mob, targetClass, chance, sight, false, predicate);
    }

    @Override
    protected AABB getTargetSearchArea(double distance) {
        return this.mob.getBoundingBox().inflate(distance, distance, distance);
    }

}
