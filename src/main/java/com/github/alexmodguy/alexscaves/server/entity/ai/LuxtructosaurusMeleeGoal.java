package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.LuxtructosaurusEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SauropodBaseEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.PathResult;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class LuxtructosaurusMeleeGoal extends Goal {

    private LuxtructosaurusEntity luxtructosaurus;
    private int navigationCheckCooldown = 0;
    private int flamesCooldown = 0;
    private int roarCooldown = 0;
    private int successfulJumpCooldown = 0;

    public LuxtructosaurusMeleeGoal(LuxtructosaurusEntity luxtructosaurus) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.luxtructosaurus = luxtructosaurus;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = luxtructosaurus.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        navigationCheckCooldown = 0;
        flamesCooldown = 0;
        roarCooldown = 0;
    }

    @Override
    public void stop() {
        luxtructosaurus.turningFast = false;
    }

    public void tick() {
        LivingEntity target = luxtructosaurus.getTarget();
        if (target != null && target.isAlive()) {
            double distance = luxtructosaurus.distanceTo(target);
            double attackDistance = luxtructosaurus.getBbWidth() + target.getBbWidth();
            boolean farFlag = false;
            if (this.luxtructosaurus.getAnimation() == SauropodBaseEntity.ANIMATION_SPEW_FLAMES || this.luxtructosaurus.getAnimation() == SauropodBaseEntity.ANIMATION_LEFT_KICK || this.luxtructosaurus.getAnimation() == SauropodBaseEntity.ANIMATION_RIGHT_KICK || this.luxtructosaurus.getAnimation() == SauropodBaseEntity.ANIMATION_LEFT_WHIP || this.luxtructosaurus.getAnimation() == SauropodBaseEntity.ANIMATION_RIGHT_WHIP) {
                luxtructosaurus.turningFast = true;
                Vec3 vec3 = target.position().subtract(luxtructosaurus.position());
                this.luxtructosaurus.yBodyRot = Mth.approachDegrees(luxtructosaurus.yBodyRot, -((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI), 15);
                this.luxtructosaurus.yBodyRotO = this.luxtructosaurus.yBodyRot;
                this.luxtructosaurus.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
            } else {
                luxtructosaurus.turningFast = false;
            }
            if (distance > attackDistance) {
                this.luxtructosaurus.getNavigation().moveTo(target, 1.0D);
            }
            if (this.luxtructosaurus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                if (distance < attackDistance + 4.0D) {
                    float random = this.luxtructosaurus.getRandom().nextFloat();
                    if (random < 0.33 && this.luxtructosaurus.onGround()) {
                        this.luxtructosaurus.setAnimation(LuxtructosaurusEntity.ANIMATION_STOMP);
                        this.luxtructosaurus.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_ATTACK_STOMP.get(), 3.0F, this.luxtructosaurus.getVoicePitch());
                    } else if (random < 0.66 && distance < attackDistance + 1.0D) {
                        this.luxtructosaurus.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_KICK.get(), 3.0F, this.luxtructosaurus.getVoicePitch());
                        this.luxtructosaurus.setAnimation(this.luxtructosaurus.getRandom().nextBoolean() ? LuxtructosaurusEntity.ANIMATION_LEFT_KICK : LuxtructosaurusEntity.ANIMATION_RIGHT_KICK);
                    } else {
                        this.luxtructosaurus.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_TAIL.get(), 3.0F, this.luxtructosaurus.getVoicePitch());
                        this.luxtructosaurus.setAnimation(this.luxtructosaurus.getRandom().nextBoolean() ? LuxtructosaurusEntity.ANIMATION_RIGHT_WHIP : LuxtructosaurusEntity.ANIMATION_LEFT_WHIP);
                    }
                } else {
                    farFlag = true;
                    if (luxtructosaurus.isEnraged() && flamesCooldown == 0) {
                        flamesCooldown = 200 + luxtructosaurus.getRandom().nextInt(300);
                        this.luxtructosaurus.setAnimation(SauropodBaseEntity.ANIMATION_SPEW_FLAMES);
                    }
                }
                int roarRandomChance = farFlag ? 90 : 150;
                if (luxtructosaurus.isEnraged()) {
                    roarRandomChance += 200;
                }
                if ((roarCooldown == 0 &&  this.luxtructosaurus.getRandom().nextInt(roarRandomChance) == 0 || !luxtructosaurus.isEnraged() && luxtructosaurus.getHealth() < luxtructosaurus.getMaxHealth() * 0.25F)) {
                    roarCooldown = 100 + luxtructosaurus.getRandom().nextInt(200);
                    this.luxtructosaurus.setAnimation(SauropodBaseEntity.ANIMATION_ROAR);
                    this.luxtructosaurus.enragedFor = 500 + luxtructosaurus.getRandom().nextInt(200);
                    this.luxtructosaurus.setEnraged(true);
                }
            }
            if (successfulJumpCooldown <= 0 && navigationCheckCooldown-- < 0 && (luxtructosaurus.onGround() || luxtructosaurus.isInLava())) {
                navigationCheckCooldown = 20 + luxtructosaurus.getRandom().nextInt(40);
                if (!canReach(target) && this.luxtructosaurus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    this.luxtructosaurus.setAnimation(LuxtructosaurusEntity.ANIMATION_JUMP);
                    this.luxtructosaurus.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_JUMP.get(), 5.0F, this.luxtructosaurus.getVoicePitch());
                    this.luxtructosaurus.jumpTarget = target.position();
                    successfulJumpCooldown = 100 + luxtructosaurus.getRandom().nextInt(200);
                }
            }
        }
        if (flamesCooldown > 0) {
            flamesCooldown--;
        }
        if (roarCooldown > 0) {
            roarCooldown--;
        }
        if (successfulJumpCooldown > 0) {
            successfulJumpCooldown--;
        }
    }

    private boolean canReach(LivingEntity target) {
        if (target.distanceTo(this.luxtructosaurus) > 50) {
            return false;
        } else {
            PathResult path = ((AdvancedPathNavigate) this.luxtructosaurus.getNavigation()).moveToLivingEntity(target, 1.0D);
            if (path == null || path.getPath() == null) {
                return false;
            } else {
                Node node = path.getPath().getEndNode();
                if (node == null) {
                    return false;
                } else {
                    int i = node.x - target.getBlockX();
                    int j = node.y - target.getBlockY();
                    int k = node.z - target.getBlockZ();
                    return (double) (i * i + j * j + k * k) <= 3D;
                }
            }
        }
    }

}