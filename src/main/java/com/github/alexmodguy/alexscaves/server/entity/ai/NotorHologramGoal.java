package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.NotorEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class NotorHologramGoal extends Goal {

    private NotorEntity notor;
    private int checkForMonsterTime = 0;
    private Mob monster;
    private Vec3 moveTarget = null;

    private int hologramTime = 0;

    private static final int MAX_HOLOGRAM_TIME = 100;

    public NotorHologramGoal(NotorEntity notor) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.notor = notor;
    }

    @Override
    public boolean canUse() {
        return notor.getHologramUUID() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && (this.monster == null || monster.isAlive() && monster.distanceTo(notor) < 40);
    }

    public void start() {
        checkForMonsterTime = 0;
        hologramTime = 0;
        monster = null;
    }


    public void stop() {
        notor.setScanningId(-1);
        notor.setHologramPos(null);
        notor.setShowingHologram(false);
        notor.stopScanningFor += notor.getRandom().nextInt(100) + 100;
        monster = null;
    }

    public void tick() {
        Entity hologram = notor.getHologramEntity();
        double holoHeight = hologram == null ? 1 : hologram.getBbHeight();
        if (checkForMonsterTime < 0) {
            checkForMonsterTime = 20 + notor.getRandom().nextInt(10);
            if (monster == null || !monster.isAlive()) {
                Predicate<Entity> monsterAway = (entity) -> entity instanceof Enemy && (hologram == null || !entity.equals(hologram)) && entity.distanceTo(notor) > 5 && !entity.isPassenger() && hasNoTarget(entity);
                List<Mob> list = notor.level().getEntitiesOfClass(Mob.class, notor.getBoundingBox().inflate(30, 12, 30), EntitySelector.NO_SPECTATORS.and(monsterAway));
                list.sort(Comparator.comparingDouble(notor::distanceToSqr));
                if (!list.isEmpty()) {
                    monster = list.get(0);
                }
            }
        } else {
            checkForMonsterTime--;
        }
        if (monster == null || !monster.isAlive() || !hasNoTarget(monster)) {
            if (hologram != null) {
                int j = 0;
                while ((moveTarget == null || moveTarget.distanceTo(notor.position()) < 4) && j < 10) {
                    moveTarget = DefaultRandomPos.getPosAway(notor, 40, 15, hologram.position());
                    j++;
                }
            }
            if (moveTarget != null && moveTarget.distanceTo(notor.position()) >= 4) {
                notor.getNavigation().moveTo(moveTarget.x, moveTarget.y, moveTarget.z, 1.2F);
            }
        } else {
            double distToMonster = monster.distanceTo(notor);
            double distMonsterToPlayer = monster.distanceTo(notor);
            if (hologramTime < MAX_HOLOGRAM_TIME) {
                if (distToMonster < 8 && notor.hasLineOfSight(monster)) {
                    notor.getNavigation().stop();
                    if (notor.getHologramPos() == null) {
                        BlockPos set = monster.blockPosition();
                        for (int i = 0; i < 15; i++) {
                            BlockPos holoPos = monster.blockPosition().offset(notor.getRandom().nextInt(10) - 5, (int) (monster.getBbHeight() + 3), notor.getRandom().nextInt(10) - 5);
                            while (notor.level().isEmptyBlock(holoPos) && holoPos.getY() > notor.level().getMinBuildHeight()) {
                                holoPos = holoPos.below();
                            }
                            holoPos = holoPos.above();
                            Vec3 holoVec = Vec3.atCenterOf(holoPos);
                            if (!isTargetBlocked(monster, holoVec) && !isTargetBlocked(notor, holoVec)) {
                                set = holoPos;
                                break;
                            }
                        }
                        notor.setHologramPos(set.above((int) holoHeight));
                    }
                    BlockPos gotten = notor.getHologramPos();
                    Vec3 stareAt = gotten == null ? notor.getEyePosition() : Vec3.atCenterOf(gotten).add(0, 0, 0);
                    monster.lookAt(EntityAnchorArgument.Anchor.EYES, stareAt);
                    notor.lookAt(EntityAnchorArgument.Anchor.EYES, stareAt);
                    monster.getNavigation().stop();
                    notor.setShowingHologram(true);
                    hologramTime++;
                } else {
                    notor.getNavigation().moveTo(monster.getX(), monster.getY(1.0F) + 1, monster.getZ(), 1.2F);
                }
            } else {
                notor.setShowingHologram(false);
                if (hologram instanceof Player player && !player.isCreative()) {
                    monster.getNavigation().moveTo(notor.getX(), notor.getY(), notor.getZ(), 1.2F);
                    notor.getNavigation().moveTo(player.getX(), player.getY(1.0F) + 2, player.getZ(), 1.2F);
                    monster.setTarget(player);
                    if (distMonsterToPlayer < Math.min(monster.getAttributeValue(Attributes.FOLLOW_RANGE) - 15, 10)) {
                        notor.setHologramUUID(null);
                    }
                } else {
                    notor.setHologramUUID(null);
                }
            }
        }
    }

    private boolean hasNoTarget(Entity entity) {
        if (entity instanceof Mob living) {
            LivingEntity target = living.getTarget();
            return target == null || !target.isAlive();
        }
        return true;
    }

    public boolean isTargetBlocked(Mob mob, Vec3 target) {
        Vec3 Vector3d = new Vec3(mob.getX(), mob.getEyeY(), mob.getZ());
        return mob.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)).getType() != HitResult.Type.MISS;
    }
}
