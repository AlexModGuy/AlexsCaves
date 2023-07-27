package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.ForsakenEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.EnumSet;
import java.util.List;

public class ForsakenAttackGoal extends Goal {

    private ForsakenEntity entity;
    private BlockPos jumpTarget = null;
    private boolean jumpEnqueued = false;
    private boolean sonicEnqueued = false;
    private int navigationCheckCooldown = 0;

    private int attemptSonicDamageIn = 0;

    public ForsakenAttackGoal(ForsakenEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        navigationCheckCooldown = 0;
        jumpEnqueued = false;
        sonicEnqueued = entity.getRandom().nextBoolean();
        attemptSonicDamageIn = 0;
    }

    @Override
    public void stop() {
        jumpEnqueued = false;
        sonicEnqueued = false;
        this.entity.setRunning(false);
    }

    @Override
    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target != null && target.isAlive()) {
            double distance = entity.distanceTo(target);
            double attackDistance = entity.getBbWidth() + target.getBbWidth();
            boolean inPursuit = !this.isMovementFrozen();
            if (attemptSonicDamageIn > 0) {
                attemptSonicDamageIn--;
                if (attemptSonicDamageIn == 0 && this.entity.hasLineOfSight(target)) {
                    target.hurt(ACDamageTypes.causeForsakenSonicBoomDamage(entity.level().registryAccess(), entity), entity.getSonicDamageAgainst(target));
                }
            }
            if (sonicEnqueued && this.entity.hasLineOfSight(target) && distance < 200F) {
                this.entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                entity.setSonarId(target.getId());
                this.entity.getNavigation().stop();
                tryAnimation(distance > 10 || entity.getRandom().nextFloat() < 0.4 ? ForsakenEntity.ANIMATION_SONIC_ATTACK : ForsakenEntity.ANIMATION_SONIC_BLAST);
                if (this.entity.getAnimation() == ForsakenEntity.ANIMATION_SONIC_ATTACK) {
                    inPursuit = false;
                    if (this.entity.getAnimationTick() >= 10 && this.entity.getAnimationTick() <= 30 && attemptSonicDamageIn <= 0) {
                        attemptSonicDamageIn = (int) Math.ceil(distance * 0.2F);
                    }
                    if (this.entity.getAnimationTick() > 30) {
                        sonicEnqueued = false;
                    }
                }
                if (this.entity.getAnimation() == ForsakenEntity.ANIMATION_SONIC_BLAST) {
                    inPursuit = false;
                    if (this.entity.getAnimationTick() >= 10 && this.entity.getAnimationTick() <= 30) {
                        if (this.entity.getAnimationTick() % 5 == 0) {
                            List<LivingEntity> list = this.entity.level().getEntitiesOfClass(LivingEntity.class, this.entity.getBoundingBox().inflate(10, 8, 10));
                            for (LivingEntity living : list) {
                                if (living != this.entity && !this.entity.isAlliedTo(living) && !living.isAlliedTo(entity) && living.distanceTo(entity) <= 10F && !living.getType().is(ACTagRegistry.FORSAKEN_IGNORES)) {
                                    living.hurt(ACDamageTypes.causeForsakenSonicBoomDamage(entity.level().registryAccess(), entity), entity.getSonicDamageAgainst(target) * 0.65F);
                                }
                            }
                        }
                    }
                    if (this.entity.getAnimationTick() > 40) {
                        sonicEnqueued = false;
                    }
                }
            } else if (jumpEnqueued) {
                if (jumpTarget == null) {
                    jumpTarget = findJumpTarget(target, distance > 20.0F);
                } else {
                    inPursuit = false;
                    if (entity.isLeaping()) {
                        Vec3 vec3 = this.entity.getDeltaMovement();
                        Vec3 vec31 = new Vec3(this.jumpTarget.getX() + 0.5F - this.entity.getX(), 0.0D, this.jumpTarget.getZ() + 0.5F - this.entity.getZ());
                        if (vec31.lengthSqr() > 1.0E-7D) {
                            vec31 = vec31.scale(0.155F).add(vec3.scale(0.2D));
                        }
                        this.entity.setDeltaMovement(vec31.x, 0.2F + (double) vec31.length() * 0.3F, vec31.z);
                        jumpEnqueued = false;
                        jumpTarget = null;
                    } else if (entity.onGround()) {
                        entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(jumpTarget));
                        tryAnimation(ForsakenEntity.ANIMATION_PREPARE_JUMP);
                    }
                }
            }
            if (inPursuit) {
                this.entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                this.entity.getNavigation().moveTo(target, 1.0D);
                if (distance < attackDistance + 1.0F && this.entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    float attackType = entity.getRandom().nextFloat();
                    if (attackType < 0.25F && target.getBbWidth() < 2.0F) {
                        tryAnimation(this.entity.getRandom().nextBoolean() ? ForsakenEntity.ANIMATION_LEFT_PICKUP : ForsakenEntity.ANIMATION_RIGHT_PICKUP);
                    } else if (attackType < 0.5F) {
                        tryAnimation(this.entity.getRandom().nextBoolean() ? ForsakenEntity.ANIMATION_LEFT_SLASH : ForsakenEntity.ANIMATION_RIGHT_SLASH);
                    } else if (attackType < 0.75F) {
                        tryAnimation(ForsakenEntity.ANIMATION_GROUND_SMASH);
                    } else {
                        tryAnimation(ForsakenEntity.ANIMATION_BITE);
                    }
                }
            } else {
                entity.getNavigation().stop();
            }
            if ((distance > 20.0F && this.entity.getRandom().nextFloat() < 0.01 || entity.hasSonicCharge()) && !this.sonicEnqueued) {
                this.sonicEnqueued = true;
                this.entity.setSonicCharge(false);
            }
            if (distance > 30.0F && this.entity.getRandom().nextFloat() < 0.05 && !this.jumpEnqueued) {
                this.startCleanJump();
            }
            if (distance < 64.0F && distance > attackDistance && inPursuit) {
                this.entity.setRunning(true);
            } else {
                this.entity.setRunning(false);
            }
            if ((entity.getAnimation() == ForsakenEntity.ANIMATION_RIGHT_PICKUP || entity.getAnimation() == ForsakenEntity.ANIMATION_LEFT_PICKUP) && entity.getHeldMobId() == target.getId() && entity.getAnimationTick() >= 30) {
                checkAndDealDamage(target, 1.2F, 5);
            }
            if (entity.getAnimation() == ForsakenEntity.ANIMATION_RIGHT_SLASH && entity.getAnimationTick() >= 15 && entity.getAnimationTick() <= 18) {
                float knockbackStrength = 0.5F;
                if (checkAndDealDamage(target, 1, 2)) {
                    knockbackStrength = 3F;
                }
                knockBackAngle(target, knockbackStrength, -90F);
            }
            if (entity.getAnimation() == ForsakenEntity.ANIMATION_LEFT_SLASH && entity.getAnimationTick() >= 15 && entity.getAnimationTick() <= 18) {
                float knockbackStrength = 0.5F;
                if (checkAndDealDamage(target, 1, 2)) {
                    knockbackStrength = 3F;
                }
                knockBackAngle(target, knockbackStrength, 90F);
            }
            if (entity.getAnimation() == ForsakenEntity.ANIMATION_GROUND_SMASH && entity.getAnimationTick() >= 10 && entity.getAnimationTick() <= 15) {
                Vec3 smashPos = entity.position().add(new Vec3(0, 0, 3.5F).yRot((float) -Math.toRadians(entity.yBodyRot)));
                List<LivingEntity> list = this.entity.level().getEntitiesOfClass(LivingEntity.class, new AABB(smashPos.x - 4, smashPos.y - 2, smashPos.z - 4, smashPos.x + 4, smashPos.y + 3, smashPos.z + 4));
                for (LivingEntity living : list) {
                    if (living != this.entity && !this.entity.isAlliedTo(living) && !living.isAlliedTo(entity) && living.distanceToSqr(smashPos) <= 16F && !living.getType().is(ACTagRegistry.FORSAKEN_IGNORES)) {
                        if (checkAndDealDamage(living, 0.8F, 3) && living.onGround()) {
                            living.setDeltaMovement(living.getDeltaMovement().add(0, 0.5, 0));
                        }
                    }
                }
            }
            if (entity.getAnimation() == ForsakenEntity.ANIMATION_BITE && entity.getAnimationTick() >= 5 && entity.getAnimationTick() <= 8) {
                float knockbackStrength = 0.0F;
                if (checkAndDealDamage(target, 0.75F, 1)) {
                    knockbackStrength = 0.5F;
                }
                knockBackAngle(target, knockbackStrength, 0F);
            }
            if (navigationCheckCooldown-- < 0 && this.entity.onGround()) {
                navigationCheckCooldown = 20 + entity.getRandom().nextInt(40);
                if (!canReach(target)) {
                    this.startCleanJump();
                }
            }
        }
    }

    private boolean canReach(LivingEntity target) {
        Path path = this.entity.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        } else {
            Node node = path.getEndNode();
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

    private boolean isMovementFrozen() {
        return entity.getAnimation() == ForsakenEntity.ANIMATION_LEFT_PICKUP || entity.getAnimation() == ForsakenEntity.ANIMATION_RIGHT_PICKUP;
    }

    private void startCleanJump() {
        jumpTarget = null;
        jumpEnqueued = true;
    }

    private boolean checkAndDealDamage(LivingEntity target, double multiplier, float extraRange) {
        if (entity.hasLineOfSight(target) && entity.distanceTo(target) < entity.getBbWidth() + target.getBbWidth() + extraRange) {
            boolean b = target.hurt(target.damageSources().mobAttack(entity), (float) (multiplier * entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            if (entity.getRandom().nextInt(2) == 0) {
                startCleanJump();
            }
            if (!sonicEnqueued && entity.getRandom().nextInt(5) == 0) {
                sonicEnqueued = true;
            }
            return b;
        }
        return false;
    }

    private void knockBackAngle(LivingEntity target, double strength, float angle) {
        float yRot = this.entity.yBodyRot + angle;
        target.knockback((double) strength, (double) Mth.sin(yRot * ((float) Math.PI / 180F)), (double) (-Mth.cos(yRot * ((float) Math.PI / 180F))));

    }

    private boolean tryAnimation(Animation animation) {
        if (entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            entity.setAnimation(animation);
            return true;
        }
        return false;
    }

    private BlockPos findJumpTarget(LivingEntity target, boolean far) {
        int lengthOfRadius = far ? entity.getRandom().nextInt(2) + 4 : entity.getRandom().nextInt(10) + 15;
        Vec3 offset = target.position().add(new Vec3(0, 0, lengthOfRadius).yRot((float) (Math.PI * 2F * entity.getRandom().nextFloat())));
        Vec3 vec3 = null;
        if (far) {
            BlockPos farPos = LandRandomPos.movePosUpOutOfSolid(entity, BlockPos.containing(offset));
            if (farPos != null) {
                vec3 = Vec3.atCenterOf(farPos);
            }
        } else {
            vec3 = LandRandomPos.getPosTowards(this.entity, 20, 10, offset);
        }
        if (vec3 != null) {
            BlockPos blockpos = BlockPos.containing(vec3);
            AABB aabb = this.entity.getBoundingBox().move(vec3.add(0.5F, 1, 0.5F).subtract(this.entity.position()));
            if (entity.level().getBlockState(blockpos.below()).isSolidRender(entity.level(), blockpos.below()) && entity.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(entity.level(), blockpos.mutable())) == 0.0F && entity.level().isUnobstructed(this.entity, Shapes.create(aabb))) {
                return blockpos;
            }
        }
        return null;
    }
}
