package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class UnderzealotProcessionGoal extends Goal {
    public final UnderzealotEntity entity;
    private double speedModifier;
    private int attemptToFollowTicks = 0;

    public UnderzealotProcessionGoal(UnderzealotEntity underzealot, double speed) {
        this.entity = underzealot;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        if (this.entity.getRandom().nextInt(20) == 0 && this.entity.isPackFollower() && this.entity.getPriorPackMember() != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        return this.entity.isPackFollower() && this.entity.getPriorPackMember() != null;
    }

    public void stop() {
        this.entity.getNavigation().stop();
        attemptToFollowTicks = 0;
    }

    public void tick() {
        if(this.entity.isDiggingInProgress() || this.entity.isBuried()){
            this.entity.setPraying(false);
            return;
        }
        if (this.entity.getPriorPackMember() != null) {
            UnderzealotEntity priorPackMember = (UnderzealotEntity) this.entity.getPriorPackMember();
            double distanceTo = (double)this.entity.distanceTo(priorPackMember);
            if(distanceTo > this.entity.getBbWidth() + 0.5F){
                Vec3 vec3;
                vec3 = (new Vec3(priorPackMember.getX() - this.entity.getX(), priorPackMember.getY() - this.entity.getY(), priorPackMember.getZ() - this.entity.getZ())).normalize().scale(Math.max(distanceTo - 1.0D, 0.0D));
                this.entity.getNavigation().moveTo(this.entity.getX() + vec3.x, this.entity.getY() + vec3.y, this.entity.getZ() + vec3.z, this.speedModifier);
            }
            if(distanceTo > 8){
                attemptToFollowTicks++;
                if((entity.getNavigation().isStuck() || attemptToFollowTicks > 60)){
                    entity.setBuried(true);
                    entity.reemergeAt(priorPackMember.blockPosition(), 20 + entity.getRandom().nextInt(20));
                }
            }else{
                attemptToFollowTicks = 0;
            }
        }
    }

}