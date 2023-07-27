package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class VallumraptorWanderGoal extends RandomStrollGoal {

    private VallumraptorEntity raptor;

    public VallumraptorWanderGoal(VallumraptorEntity vallumraptor, double speed, int rate) {
        super(vallumraptor, speed, rate);
        this.raptor = vallumraptor;
    }

    @Nullable
    protected Vec3 getPosition() {
        if (raptor.isPackFollower()) {
            return DefaultRandomPos.getPosTowards(this.mob, 10, 7, ((Entity) raptor.getPackLeader()).position(), (double) ((float) Math.PI / 2F));
        } else {
            return DefaultRandomPos.getPos(this.mob, 16, 7);
        }
    }
}
