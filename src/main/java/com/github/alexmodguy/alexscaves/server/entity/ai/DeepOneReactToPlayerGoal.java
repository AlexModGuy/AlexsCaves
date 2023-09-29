package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneMageEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.DeepOneReaction;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
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

    private int executionTime = 0;
    private Vec3 moveTarget = null;

    public DeepOneReactToPlayerGoal(DeepOneBaseEntity deepOne) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.deepOne = deepOne;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = deepOne.getTarget();
        long worldTime = deepOne.level().getGameTime() % 20;
        if (worldTime != 0 && deepOne.getRandom().nextInt(15) != 0 || (target != null && target.isAlive()) || deepOne.getCorneringPlayer() != null) {
            return false;
        }
        AABB aabb = deepOne.getBoundingBox().inflate(80);
        List<Player> list = deepOne.level().getEntitiesOfClass(Player.class, aabb);
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
            DeepOneReaction reaction1 = DeepOneReaction.fromReputation(highestReputation);
            return player != null && (reaction1 != DeepOneReaction.AGGRESSIVE || deepOne.isSummoned() || !player.isCreative()) && reaction1.validPlayer(deepOne, player);
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity attackTarget = deepOne.getTarget();
        return player != null && !player.isSpectator() && reaction != null && deepOne.getCorneringPlayer() == null && deepOne.distanceTo(player) >= reaction.getMinDistance() && deepOne.distanceTo(player) <= reaction.getMaxDistance() && (attackTarget == null || !attackTarget.isAlive()) && (reaction != DeepOneReaction.AGGRESSIVE || !player.isCreative() || deepOne.isSummoned()) && !deepOne.isTradingLocked() && reaction.validPlayer(deepOne, player);
    }


    @Override
    public void start() {
        chaseTime = 0;
        executionTime = 0;
        refreshReaction();
    }

    @Override
    public void stop() {
        chaseTime = 0;
        executionTime = 0;
        following = false;
        isBeingLookedAt = false;
        moveTarget = null;
        deepOne.setSoundsAngry(false);
    }

    private void refreshReaction() {
        if (player != null) {
            prevReaction = reaction;
            reaction = deepOne.getReactionTo(player);
            if (prevReaction != reaction) {
                deepOne.getNavigation().stop();
            }
            refreshReactionTime = 20 + deepOne.getRandom().nextInt(40);
        }
    }

    @Override
    public void tick() {
        executionTime++;
        if (refreshReactionTime-- < 0) {
            refreshReaction();
        }
        switch (reaction) {
            case STALKING:
                tickStalking();
                break;
            case AGGRESSIVE:
                if (!player.isCreative()) {
                    deepOne.setTarget(player);
                }
                break;
            case NEUTRAL:
                tickFollow(0.1F);
                break;
            case HELPFUL:
                deepOne.copyTarget(player);
                tickFollow(0.4F);
                break;
        }
        deepOne.setSoundsAngry(reaction == DeepOneReaction.AGGRESSIVE);
        if (!deepOne.getNavigation().isDone() && (moveTarget == null || moveTarget.y < deepOne.getY() + 2)) {
            deepOne.setDeepOneSwimming(!deepOne.onGround() && deepOne.isInWaterOrBubble());
        }
    }

    private void tickFollow(float propensity) {
        float f = 0.1F;
        if (player.getOffhandItem().is(ACTagRegistry.DEEP_ONE_BARTERS) || player.getMainHandItem().is(ACTagRegistry.DEEP_ONE_BARTERS)) {
            f = 0.2F;
        }
        if (deepOne.isSummoned()) {
            f = 1000;
        }
        double distance = deepOne.distanceTo(player);
        if (deepOne.getRandom().nextFloat() < propensity * f && friendlyLookAtTime <= 0) {
            friendlyLookAtTime = 10 + deepOne.getRandom().nextInt(20);
        }
        if (friendlyLookAtTime > 0) {
            deepOne.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float) this.deepOne.getMaxHeadXRot());
            friendlyLookAtTime--;
        }
        if (following) {
            if (distance < 4) {
                following = false;
                deepOne.getNavigation().stop();
            } else {
                deepOne.getNavigation().moveTo(player, 1.0F);
            }
        } else if (distance > 10) {
            if (deepOne.getRandom().nextFloat() < propensity * 0.2F) {
                following = true;
            }
        }
    }

    private void tickStalking() {
        double distance = deepOne.distanceTo(player);
        double distanceXZ = Mth.sqrt((float) deepOne.distanceToSqr(player.getX(), deepOne.getY(), player.getZ()));
        if (distance <= 8 && isBeingLookedAt) {
            chaseTime++;
        }
        if (distance > 40 && chaseTime > 0) {
            chaseTime = 0;
        }
        if (chaseTime >= (deepOne.getLastHurtByMob() == player ? 10 : 60)) {
            deepOne.setCorneredBy(player);
        } else {
            if (lookAtTime-- < 0) {
                boolean isLooking = isEntityLookingAt(player, deepOne, 1.2F);
                if (isLooking != isBeingLookedAt) {
                    deepOne.getNavigation().stop();
                    moveTarget = null;
                    if (executionTime > 20 && deepOne.distanceTo(player) < 20F) {
                        ACAdvancementTriggerRegistry.STALKED_BY_DEEP_ONE.triggerForEntity(player);
                    }
                }
                isBeingLookedAt = isLooking;
                lookAtTime = 5 + deepOne.getRandom().nextInt(5);
            }
            deepOne.setInvisible(false);
            if (isBeingLookedAt || deepOne.getRandom().nextInt(100) == 0) {
                int j = 0;
                while ((moveTarget == null || moveTarget.distanceTo(deepOne.position()) < 3) && j < 10) {
                    moveTarget = DefaultRandomPos.getPosAway(deepOne, 40, 15, player.position());
                    j++;
                }
            } else {
                int j = 0;
                while ((moveTarget == null || moveTarget.distanceTo(deepOne.position()) < 3) && j < 10) {
                    Vec3 vec3 = DefaultRandomPos.getPosTowards(deepOne, 15, 15, player.position(), (double) ((float) Math.PI / 2F));
                    if (vec3 != null) {
                        float mageUp = deepOne instanceof DeepOneMageEntity ? 1 + deepOne.getRandom().nextInt(2) : 0;
                        moveTarget = new Vec3(vec3.x, distanceXZ < 20 ? player.getY() + mageUp : vec3.y, vec3.z);
                    }
                    j++;
                }
                deepOne.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float) this.deepOne.getMaxHeadXRot());
                if (distance < 12) {
                    deepOne.getNavigation().stop();
                    if (deepOne.onGround()) {
                        deepOne.setDeepOneSwimming(false);
                    }
                }
            }
            if (moveTarget != null && moveTarget.distanceTo(deepOne.position()) > 3 && (isBeingLookedAt || distance >= 12)) {
                if (moveTarget.y > deepOne.getY() + 1) {
                    deepOne.setDeepOneSwimming(deepOne.isInWaterOrBubble());
                }
                float mageUp = deepOne instanceof DeepOneMageEntity ? 2 : 0;
                deepOne.getNavigation().moveTo(moveTarget.x, moveTarget.y + mageUp, moveTarget.z, isBeingLookedAt ? 2F : 1F);
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
