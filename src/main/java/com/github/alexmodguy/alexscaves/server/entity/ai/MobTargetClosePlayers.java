package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class MobTargetClosePlayers extends NearestAttackableTargetGoal<Player> {

    private Mob mob;
    private float range;

    public MobTargetClosePlayers(Mob mob, float range) {
        super(mob, Player.class, 80, true, true, null);
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
        return 3.0D;
    }
}