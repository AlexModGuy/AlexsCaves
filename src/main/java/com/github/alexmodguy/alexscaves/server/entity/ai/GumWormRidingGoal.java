package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GumWormRidingGoal extends Goal {

    private final GumWormEntity entity;
    private float leapRot;

    public GumWormRidingGoal(GumWormEntity worm) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.entity = worm;
    }

    @Override
    public boolean canUse() {
        return entity.isRidingMode();
    }

    public void stop() {
        entity.setLeaping(false);
        entity.setRidingLeapTime(0);
        entity.setBiting(false);
    }

    @Override
    public void tick() {
        Player ridingPlayer = entity.getRidingPlayer();
        if (ridingPlayer != null) {
            entity.getNavigation().stop();
            if (entity.getRidingLeapTime() > 0 && entity.isValidRider()) {
                float f = Math.max(1.0F, entity.getMaxRidingLeapTime());
                entity.setLeaping(true);
                float f1 = 1 - (entity.getRidingLeapTime() / f);
                Vec3 leapDelta = new Vec3(0, Math.sin(f1 * Math.PI * 1.5F) * 2.0F, 2.0F).yRot((float) -Math.toRadians(leapRot));
                this.entity.setDeltaMovement(leapDelta);
                this.entity.setYRot(leapRot);
                entity.setRidingLeapTime(entity.getRidingLeapTime() - 1);
            } else {
                entity.setLeaping(false);
                Vec3 forwardsVec = new Vec3(entity.isValidRider() ? ridingPlayer.xxa * 2.5F : 0.0F, 0, 10F).yRot((float) -Math.toRadians(entity.yBodyRot)).add(entity.position());
                this.entity.getMoveControl().setWantedPosition(forwardsVec.x, forwardsVec.y, forwardsVec.z, 3.0F);
                this.entity.setTargetDigPitch(this.entity.horizontalCollision ? -45.0F : 0.0F);
                leapRot = entity.getYRot();
            }
            if (entity.isMouthOpen()) {
                entity.attackAllAroundMouth((float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue(), 2.0F);
            }
            if(entity.isMouthForcedOpen()){
                entity.setBiting(true);
            }else{
                entity.setBiting(false);

            }
        }
    }
}
