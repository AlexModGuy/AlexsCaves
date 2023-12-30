package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class MobTargetClosePlayers extends NearestAttackableTargetGoal<Player> {

    private Mob mob;
    private float range;

    public MobTargetClosePlayers(Mob mob, int chance, float range) {
        super(mob, Player.class, chance, true, true, null);
        this.mob = mob;
        this.range = range;
    }

    public boolean canUse() {
        if (mob.isBaby() || mob instanceof TamableAnimal && ((TamableAnimal) mob).isTame()) {
            return false;
        } else {
            return super.canUse();
        }
    }

    protected double getFollowDistance() {
        return this.range;
    }

    protected AABB getTargetSearchArea(double rangeIn) {
        return this.mob.getBoundingBox().inflate(this.range, this.range, this.range);
    }
}