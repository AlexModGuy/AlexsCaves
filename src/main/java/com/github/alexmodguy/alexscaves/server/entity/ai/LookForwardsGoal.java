package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LookForwardsGoal extends Goal {

    private Mob mob;

    public LookForwardsGoal(Mob mob){
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    public void tick(){
        mob.setYHeadRot(mob.getYRot());
    }
}
