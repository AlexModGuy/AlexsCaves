package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SubterranodonFleeGoal extends Goal {

    private SubterranodonEntity subterranodon;

    public SubterranodonFleeGoal(SubterranodonEntity subterranodon) {
        this.subterranodon = subterranodon;
    }

    @Override
    public boolean canUse() {
        if (subterranodon.isFlying() || subterranodon.isDancing() || subterranodon.isVehicle() || subterranodon.isInSittingPose()) {
            return false;
        }
        long worldTime = subterranodon.level().getGameTime() % 10;
        if (subterranodon.getRandom().nextInt(10) != 0 && worldTime != 0) {
            return false;
        }
        AABB aabb = subterranodon.getBoundingBox().inflate(7);
        List<Entity> list = subterranodon.level().getEntitiesOfClass(Entity.class, aabb, (entity -> entity.getType().is(ACTagRegistry.SUBTERRANODON_FLEES)));
        return !list.isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        subterranodon.setFlying(true);
        subterranodon.setHovering(true);
    }
}
