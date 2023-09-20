package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.WatcherEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WatcherAttackGoal extends Goal {

    private WatcherEntity watcher;
    private int navigationCheckCooldown = 0;
    private int possessions = 0;
    private boolean canReachViaGround = false;
    private int retreatFor = 0;
    private Vec3 retreatTo = null;

    public WatcherAttackGoal(WatcherEntity watcher) {
        this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        this.watcher = watcher;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = watcher.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = watcher.getTarget();
        if (navigationCheckCooldown-- < 0) {
            calculateReach();
        }
        if (target != null && target.isAlive()) {
            double dist = watcher.distanceTo(target);
            watcher.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());

            if (retreatFor-- > 0) {
                if (retreatTo == null) {
                    retreatFor = 0;
                } else {
                    Vec3 retreatVec = retreatTo.subtract(watcher.position());
                    if (retreatVec.length() > 1) {
                        retreatVec = retreatVec.normalize();
                        watcher.setDeltaMovement(watcher.getDeltaMovement().add(retreatVec.scale(0.2F)));
                        watcher.setShadeMode(true);
                    } else {
                        retreatTo = null;
                    }
                }
            } else {
                if (!canReachViaGround && !watcher.isShadeMode() && watcher.onGround()) {
                    watcher.setShadeMode(true);
                }
                watcher.setShadeMode(!canReachViaGround);
                if (dist < 6 && watcher.hasLineOfSight(target) && watcher.onGround()) {
                    watcher.setShadeMode(false);
                }
                if (dist > target.getBbWidth() + watcher.getBbWidth() + 0.5F) {
                    watcher.getNavigation().moveTo(target, (watcher.isRunning() ? 1.3F : 1F) + Math.min(Math.log(possessions + 1), 1F) * 0.6F);
                } else {
                    if (watcher.hasLineOfSight(target)) {
                        if (watcher.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                            watcher.setAnimation(watcher.getRandom().nextBoolean() ? WatcherEntity.ANIMATION_ATTACK_0 : WatcherEntity.ANIMATION_ATTACK_1);
                            watcher.playSound(ACSoundRegistry.WATCHER_ATTACK.get());
                        } else if (watcher.getAnimationTick() == 8) {
                            target.hurt(target.damageSources().mobAttack(watcher), (float) watcher.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                            target.knockback(0.5D, watcher.getX() - target.getX(), watcher.getZ() - target.getZ());
                            retreatFor = 30 + watcher.getRandom().nextInt(30);
                            for (int i = 0; i < 15; i++) {
                                Vec3 vec3 = DefaultRandomPos.getPosAway(watcher, 30, 15, target.position());
                                if (vec3 != null) {
                                    retreatTo = vec3;
                                    break;
                                }
                            }
                        }
                    }
                    watcher.getNavigation().stop();
                }
            }
            if (possessions > 2 || dist < 20) {
                watcher.setRunning(true);
            }

            if (watcher.attemptPossession(target)) {
                if (watcher.getPossessedEntity() == null) {
                    possessions++;
                }
                watcher.setPossessedEntityUUID(target.getUUID());
            }
        }
    }

    public void start() {
        super.start();
        navigationCheckCooldown = 0;
        possessions = 0;
        retreatFor = 0;
        retreatTo = null;
    }

    public void calculateReach() {
        LivingEntity target = watcher.getTarget();
        if (target != null && target.isAlive()) {
            this.canReachViaGround = watcher.canReach(target, false);
            navigationCheckCooldown = 10 + watcher.getRandom().nextInt(40);
        }
    }

    public void stop() {
        super.stop();
        watcher.setShadeMode(false);
        watcher.setRunning(false);
        retreatFor = 0;
        retreatTo = null;
    }
}
