package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.DeepOneReaction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class DeepOneReactToPlayerGoal extends Goal {
    private DeepOneBaseEntity deepOne;
    private Player player;

    private DeepOneReaction prevReaction;
    private DeepOneReaction reaction;
    private boolean following = false;
    private int refreshReactionTime = 0;
    private boolean isBeingLookedAt = false;
    private int lookAtTime = 0;
    private int chaseTime = 0;
    private int friendlyLookAtTime = 0;

    private Vec3 moveTarget = null;

    public DeepOneReactToPlayerGoal(DeepOneBaseEntity deepOne) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.deepOne = deepOne;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = deepOne.getTarget();
        long worldTime = deepOne.level.getGameTime() % 20;
        if (worldTime != 0 && deepOne.getRandom().nextInt(15) != 0 || (target != null && target.isAlive()) || deepOne.getCorneringPlayer() != null) {
            return false;
        }
        AABB aabb = deepOne.getBoundingBox().inflate(80);
        List<Player> list = deepOne.level.getEntitiesOfClass(Player.class, aabb);
        if (!list.isEmpty()) {
            Player closest = null;
            int highestReputation = Integer.MIN_VALUE;
            for (Player scanningPlayer : list) {
                if ((closest == null || scanningPlayer.distanceToSqr(deepOne) < closest.distanceToSqr(deepOne) || deepOne.getReputationOf(scanningPlayer.getUUID()) > highestReputation) && deepOne.hasLineOfSight(scanningPlayer)) {
                    closest = scanningPlayer;
                    highestReputation = deepOne.getReputationOf(scanningPlayer.getUUID());
                }
            }
            player = closest;
            return player != null && (DeepOneReaction.fromReputation(highestReputation) != DeepOneReaction.AGGRESSIVE || !player.isCreative());
        }
        LivingEntity attackTarget = deepOne.getTarget();
        return player != null && reaction != null && deepOne.getCorneringPlayer() == null && deepOne.distanceTo(player) >= reaction.getMinDistance() && deepOne.distanceTo(player) <= reaction.getMaxDistance() && (attackTarget == null || !attackTarget.isAlive()) && (reaction != DeepOneReaction.AGGRESSIVE || !player.isCreative());
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity attackTarget = deepOne.getTarget();
        return player != null && !player.isSpectator() && reaction != null && deepOne.getCorneringPlayer() == null && deepOne.distanceTo(player) >= reaction.getMinDistance() && deepOne.distanceTo(player) <= reaction.getMaxDistance() && (attackTarget == null || !attackTarget.isAlive()) && (reaction != DeepOneReaction.AGGRESSIVE || !player.isCreative());
    }


    @Override
    public void start(){
        chaseTime = 0;
        refreshReaction();
    }

    @Override
    public void stop(){
        chaseTime = 0;
        following = false;
        isBeingLookedAt = false;
        moveTarget = null;
    }

    private void refreshReaction(){
        if(player != null){
            prevReaction = reaction;
            reaction = deepOne.getReactionTo(player);
            if(prevReaction != reaction){
                deepOne.getNavigation().stop();
            }
            refreshReactionTime = 20 + deepOne.getRandom().nextInt(40);
        }
    }

    @Override
    public void tick() {
        if(refreshReactionTime-- < 0){
            refreshReaction();
        }
        switch (reaction){
            case STALKING:
                tickStalking();
                break;
            case AGGRESSIVE:
                if(!player.isCreative()){
                    deepOne.setTarget(player);
                }
                break;
            case NEUTRAL:
                tickFollow(0.2F);
                break;
            case HELPFUL:
                tickFollow(1.0F);
                break;
        }
        if(!deepOne.getNavigation().isDone() && (moveTarget == null || moveTarget.y  < deepOne.getY() + 2)){
            deepOne.setDeepOneSwimming(!deepOne.isOnGround() && deepOne.isInWaterOrBubble());
        }
    }

    private void tickFollow(float propensity) {
        double distance = deepOne.distanceTo(player);
        if(deepOne.getRandom().nextFloat() < propensity * 0.1F && friendlyLookAtTime <= 0){
            friendlyLookAtTime = 10 + deepOne.getRandom().nextInt(20);
        }
        if(friendlyLookAtTime > 0){
            deepOne.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float)this.deepOne.getMaxHeadXRot());
            friendlyLookAtTime--;
        }
        if(following){
            if(distance < 4){
                following = false;
                deepOne.getNavigation().stop();
            }else{
                deepOne.getNavigation().moveTo(player, 1.0F);
            }
        }else if(distance > 10){
            if(deepOne.getRandom().nextFloat() < propensity * 0.2F){
                following = true;
            }
        }
    }

    private void tickStalking() {
        double distance = deepOne.distanceTo(player);
        double distanceXZ = Mth.sqrt((float) deepOne.distanceToSqr(player.getX(), deepOne.getY(), player.getZ()));
        if(distance <= 8 && isBeingLookedAt){
            chaseTime++;
        }
        if(distance > 40 && chaseTime > 0){
            chaseTime = 0;
        }
        if(chaseTime >= (deepOne.getLastHurtByMob() == player ? 10 : 60)){
            deepOne.setCorneredBy(player);
        }else{
            if(lookAtTime-- < 0){
                boolean isLooking = isEntityLookingAt(player, deepOne, 1.2F);
                if(isLooking != isBeingLookedAt){
                    deepOne.getNavigation().stop();
                    moveTarget = null;
                }
                isBeingLookedAt = isLooking;
                lookAtTime = 5 + deepOne.getRandom().nextInt(5);
            }
            deepOne.setInvisible(false);
            if(isBeingLookedAt || deepOne.getRandom().nextInt(100) == 0){
                int j = 0;
                while((moveTarget == null  || moveTarget.distanceTo(deepOne.position()) < 3) && j < 10){
                    moveTarget = DefaultRandomPos.getPosAway(deepOne, 40, 15, player.position());
                    j++;
                }
            }else{
                int j = 0;
                while((moveTarget == null || moveTarget.distanceTo(deepOne.position()) < 3) && j < 10){
                    Vec3 vec3 = DefaultRandomPos.getPosTowards(deepOne, 15, 15, player.position(), (double)((float)Math.PI / 2F));
                    if(vec3 != null){
                        moveTarget = new Vec3(vec3.x, distanceXZ < 20 ? player.getY() : vec3.y, vec3.z);
                    }
                    j++;
                }
                deepOne.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float)this.deepOne.getMaxHeadXRot());
                if(distance < 12){
                    deepOne.getNavigation().stop();
                    if(deepOne.isOnGround()){
                        deepOne.setDeepOneSwimming(false);
                    }
                }
            }
            if (moveTarget != null && moveTarget.distanceTo(deepOne.position()) > 3 && (isBeingLookedAt || distance >= 12)) {
                if(moveTarget.y > deepOne.getY() + 1){
                    deepOne.setDeepOneSwimming(deepOne.isInWaterOrBubble());
                }
                deepOne.getNavigation().moveTo(moveTarget.x, moveTarget.y, moveTarget.z, isBeingLookedAt ? 2F : 1F);
            }
        }
    }

    private boolean isEntityLookingAt(LivingEntity looker, LivingEntity seen, double degree) {
        degree *= 1 + (looker.distanceTo(seen) * 0.1);
        Vec3 vec3 = looker.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(seen.getX() - looker.getX(), seen.getBoundingBox().minY + (double) seen.getEyeHeight() - (looker.getY() + (double) looker.getEyeHeight()), seen.getZ() - looker.getZ());
        double d0 = vec31.length();
        vec31 = vec31.normalize();
        double d1 = vec3.dot(vec31);
        return d1 > 1.0D - degree / d0 && looker.hasLineOfSight(seen);
    }
}
