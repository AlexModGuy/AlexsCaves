package com.github.alexmodguy.alexscaves.server.entity.ai;


import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class SubterranodonJoinPackGoal extends Goal {
    public final SubterranodonEntity entity;
    private int distCheckCounter;

    public SubterranodonJoinPackGoal(SubterranodonEntity subterranodon) {
        this.entity = subterranodon;
    }

    public boolean canUse() {
        long worldTime = entity.level.getGameTime() % 10;
        if (worldTime != 0 && entity.getRandom().nextInt(reducedTickDelay(30)) != 0) {
            return false;
        }
        if (!this.entity.isPackFollower() && !this.entity.hasPackFollower()) {
            double dist = 30D;
            List<SubterranodonEntity> list = entity.level.getEntitiesOfClass(SubterranodonEntity.class, entity.getBoundingBox().inflate(dist, dist, dist));
            SubterranodonEntity closestTail = null;
            double d0 = Double.MAX_VALUE;
            for (SubterranodonEntity subterranodon : list) {
                if (!subterranodon.hasPackFollower() && subterranodon != entity) {
                    double d1 = this.entity.distanceToSqr(subterranodon);
                    if (!(d1 > d0)) {
                        d0 = d1;
                        closestTail = subterranodon;
                    }
                }
            }
            if (closestTail == null) {
                return false;
            } else if (d0 < 1.0D) {
                return false;
            } else if (!entity.isValidLeader(closestTail.getPackLeader())) {
                return false;
            } else {
                this.entity.joinPackOf(closestTail);
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        if (this.entity.isPackFollower() && this.entity.getPriorPackMember().isAlive() && entity.isValidLeader(entity.getPackLeader())) {
            double d0 = this.entity.distanceToSqr(this.entity.getPriorPackMember());
            if (d0 > 676.0D) {
                if (this.distCheckCounter == 0) {
                    return false;
                }
            }
            if (this.distCheckCounter > 0) {
                --this.distCheckCounter;
            }

            return true;
        } else {
            return false;
        }
    }

    public void stop() {
        this.entity.leavePack();
    }

    public void tick() {
    }
}