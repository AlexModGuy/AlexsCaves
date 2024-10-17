package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GingerbreadManEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.UUID;

public class GingerbreadManFleeGoal extends Goal {

    private GingerbreadManEntity gingerbreadMan;

    private Entity runFrom;
    private Vec3 runToTarget = null;

    public GingerbreadManFleeGoal(GingerbreadManEntity gingerbreadMan) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.gingerbreadMan = gingerbreadMan;
    }

    @Override
    public boolean canUse() {
        UUID uuid = gingerbreadMan.getFleeingFromUUID();
        if(gingerbreadMan.getFleeFor() > 0 && uuid != null && gingerbreadMan.level() instanceof ServerLevel serverLevel){
            runFrom = serverLevel.getEntity(uuid);
            return runFrom != null;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse(){
        return runFrom != null && runFrom.isAlive() && gingerbreadMan.getFleeFor() > 0;
    }

    public void tick(){
        gingerbreadMan.stopSittingForAWhile();
        double distance = gingerbreadMan.distanceTo(runFrom);
        if(runToTarget == null || gingerbreadMan.distanceToSqr(runToTarget) < 4.0F){
            runToTarget = null;
            int tries = 0;
            while(runToTarget == null && tries < 10){
                tries++;
                runToTarget = DefaultRandomPos.getPosAway(this.gingerbreadMan, 20, 7, this.runFrom.position());
            }
        }
        if(runToTarget != null){
            double speed = distance > 5 ? 1.0F : 1.5F;
            gingerbreadMan.getNavigation().moveTo(runToTarget.x, runToTarget.y, runToTarget.z, speed);
        }
    }
}
