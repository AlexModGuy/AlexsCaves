package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;

public abstract class AnimalFollowOwnerGoal extends FollowOwnerGoal {

    private TamableAnimal tameable;

    public AnimalFollowOwnerGoal(TamableAnimal tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
        super(tameable, speed, minDist, maxDist, teleportToLeaves);
        this.tameable = tameable;
    }

    public boolean canUse(){
        return super.canUse() && shouldFollow() && !isInCombat();
    }

    public boolean canContinueToUse(){
        return super.canContinueToUse() && shouldFollow() && !isInCombat();
    }

    public abstract boolean shouldFollow();

    private boolean isInCombat() {
        Entity owner = tameable.getOwner();
        if(owner != null){
            return tameable.distanceTo(owner) < 30 && tameable.getTarget() != null && tameable.getTarget().isAlive();
        }
        return false;
    }
}
