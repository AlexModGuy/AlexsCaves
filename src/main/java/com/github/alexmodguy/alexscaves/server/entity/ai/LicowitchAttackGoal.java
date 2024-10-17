package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.SpinningPeppermintEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SugarStaffHexEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.LicowitchEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessedByLicowitch;
import com.github.alexmodguy.alexscaves.server.message.WorldEventMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class LicowitchAttackGoal extends Goal {

    private final LicowitchEntity licowitch;

    private int peppermintCooldown = 0;
    private int hexCooldown = 0;
    private int potionCooldown = 0;

    private int enqueuedAttackType;

    private int checkReachCooldown = 0;
    private int duration = 0;

    public LicowitchAttackGoal(LicowitchEntity licowitch) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.licowitch = licowitch;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = licowitch.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        licowitch.updateFoldedArms = true;
        licowitch.updateHeldItems = true;
        peppermintCooldown = 0;
        hexCooldown = 0;
        potionCooldown = 0;
        checkReachCooldown = 0;
        duration = 0;
    }

    public void tick() {
        if (peppermintCooldown > 0) {
            peppermintCooldown--;
        }
        if (hexCooldown > 0) {
            hexCooldown--;
        }
        if (potionCooldown > 0) {
            potionCooldown--;
        }
        if (checkReachCooldown > 0) {
            checkReachCooldown--;
        }
        LivingEntity target = licowitch.getTarget();
        if(duration > 200 && checkReachCooldown == 0 && target != null){
            checkReachCooldown = 100 + licowitch.getRandom().nextInt(40);
            if(licowitch.canTeleport() && !licowitch.canReach(target.blockPosition())){
                licowitch.setTeleportingToPos(Vec3.atBottomCenterOf(target.blockPosition()));
                return;
            }
        }
        if (target != null && target.isAlive() && licowitch.getTeleportingToPos() == null) {
            double distance = licowitch.distanceTo(target);
            double distanceXZ = Mth.sqrt((float) licowitch.distanceToSqr(target.getX(), licowitch.getY(), target.getZ()));
            double attackDistance = licowitch.getBbWidth() + target.getBbWidth() + 5;
            licowitch.lookAt(target, 30.0F, 30.0F);
            if (licowitch.getAnimation() == LicowitchEntity.ANIMATION_EAT || enqueuedAttackType == 0 && distance < attackDistance - 1 || licowitch.getAnimation() == LicowitchEntity.ANIMATION_SPELL_1 && distanceXZ < 6.0F) {
                licowitch.getMoveControl().strafe(-4, 0.0F);
                licowitch.getNavigation().stop();
            } else if (distance > attackDistance || !this.licowitch.hasLineOfSight(target)) {
                this.licowitch.getNavigation().moveTo(target, 1.0D);
            }
            if (this.licowitch.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                if (enqueuedAttackType == 0) {
                    if (this.licowitch.hasLineOfSight(target)) {
                        if (peppermintCooldown <= 0 && licowitch.getRandom().nextBoolean()) {
                            enqueuedAttackType = 1;
                            peppermintCooldown = 80;
                        } else if (hexCooldown <= 0 && licowitch.getRandom().nextBoolean()) {
                            enqueuedAttackType = 2;
                            hexCooldown = 200;
                        } else if (potionCooldown <= 0 && distance < 10.0D && !target.hasEffect(MobEffects.HUNGER)) {
                            enqueuedAttackType = 3;
                            licowitch.updateHeldItems = false;
                            licowitch.setItemInHand(InteractionHand.MAIN_HAND, LicowitchEntity.hungerPotion.copy());
                            potionCooldown = 100;
                        }
                    }
                } else {
                    if (enqueuedAttackType == 1 && distance < 10.0D) {
                        this.licowitch.setAnimation(LicowitchEntity.ANIMATION_SPELL_0);
                    }
                    if ((enqueuedAttackType == 2 || enqueuedAttackType == 4) && distance < 7.0D) {
                        this.licowitch.setAnimation(LicowitchEntity.ANIMATION_SPELL_1);
                    }
                    if (enqueuedAttackType == 3 && distance < 10.0D) {
                        this.licowitch.setAnimation(licowitch.getMainArm() == HumanoidArm.RIGHT ? LicowitchEntity.ANIMATION_SWING_RIGHT : LicowitchEntity.ANIMATION_SWING_LEFT);
                    }
                }
            }
            if (enqueuedAttackType == 1 && licowitch.getAnimation() == LicowitchEntity.ANIMATION_SPELL_0 && licowitch.getAnimationTick() == 18) {
                peppermintAttack(target);
                enqueuedAttackType = 0;
            }
            if (enqueuedAttackType == 2 && licowitch.getAnimation() == LicowitchEntity.ANIMATION_SPELL_1 && licowitch.getAnimationTick() == 36) {
                hexAttack(target);
                enqueuedAttackType = 0;
            }
            if (enqueuedAttackType == 3 && (licowitch.getAnimation() == LicowitchEntity.ANIMATION_SWING_RIGHT || licowitch.getAnimation() == LicowitchEntity.ANIMATION_SWING_LEFT) && licowitch.getAnimationTick() == 6) {
                potionAttack(target);
                licowitch.updateHeldItems = true;
                enqueuedAttackType = 0;
            }
        }
        duration++;
    }

    private void potionAttack(LivingEntity target) {
        Vec3 vec3 = target.getDeltaMovement();
        double d0 = target.getX() + vec3.x - licowitch.getX();
        double d1 = target.getEyeY() - (double) 1.1F - licowitch.getY();
        double d2 = target.getZ() + vec3.z - licowitch.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        ThrownPotion thrownpotion = new ThrownPotion(licowitch.level(), licowitch);
        thrownpotion.setItem(LicowitchEntity.hungerPotion);
        thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
        thrownpotion.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
        if (!licowitch.isSilent()) {
            licowitch.level().playSound(null, licowitch.getX(), licowitch.getY(), licowitch.getZ(), SoundEvents.WITCH_THROW, licowitch.getSoundSource(), 1.0F, 0.8F + licowitch.getRandom().nextFloat() * 0.4F);
        }
        licowitch.level().addFreshEntity(thrownpotion);
    }

    private void hexAttack(LivingEntity target) {
        licowitch.lookAt(target, 180.0F, 30.0F);
        boolean summonSpace = licowitch.getPossessedUUIDs().size() < 3;
        boolean flag = false;
        if (summonSpace && licowitch.getRandom().nextBoolean()) {
            Vec3 summonSpot = findSummonSpot(target, 10);
            if (summonSpot != null) {
                Mob summoned = licowitch.createRandomPossessedMob();
                if (licowitch.level() instanceof ServerLevel serverLevel) {
                    summoned.finalizeSpawn(serverLevel, licowitch.level().getCurrentDifficultyAt(BlockPos.containing(summonSpot)), MobSpawnType.MOB_SUMMONED, null, null);
                }
                if (summoned instanceof PossessedByLicowitch possessed) {
                    possessed.setPossessedByLicowitch(licowitch);
                }
                summoned.setPos(summonSpot);
                licowitch.level().addFreshEntity(summoned);
                licowitch.addPossessedUUID(summoned.getUUID());
                AlexsCaves.sendMSGToAll(new WorldEventMessage(7, (int) summonSpot.x, (int) summonSpot.y, (int) summonSpot.z));
                flag = true;
                licowitch.level().playSound((Player) null, licowitch.blockPosition(), ACSoundRegistry.LICOWITCH_CAST_SUMMON.get(), SoundSource.HOSTILE, 0.3F, 0.9F + licowitch.level().random.nextFloat() * 0.2F);
            }
        }
        if (!flag) {
            Vec3 ground = ACMath.getGroundBelowPosition(licowitch.level(), licowitch.getEyePosition());
            Vec3 groundThere = ACMath.getGroundBelowPosition(target.level(), target.getEyePosition());
            SugarStaffHexEntity sugarStaffHexEntity = ACEntityRegistry.SUGAR_STAFF_HEX.get().create(licowitch.level());
            sugarStaffHexEntity.setOwner(licowitch);
            sugarStaffHexEntity.setPos(ground.x, groundThere.y, ground.z);
            Vec3 delta = groundThere.subtract(ground);
            sugarStaffHexEntity.setDeltaMovement(delta.multiply(0.25F, 0.0F, 0.25F));
            licowitch.level().addFreshEntity(sugarStaffHexEntity);
            licowitch.level().playSound((Player) null, licowitch.blockPosition(), ACSoundRegistry.LICOWITCH_CAST_HEX.get(), SoundSource.HOSTILE, 0.3F, 0.9F + licowitch.level().random.nextFloat() * 0.2F);
        }
    }

    private Vec3 findSummonSpot(LivingEntity target, int range) {
        for (int i = 0; i < 15; i++) {
            Vec3 heightAdjusted = target.position().add(target.getRandom().nextInt(range * 2) - range, target.getEyeHeight() + target.getRandom().nextInt(4), target.getRandom().nextInt(range * 2) - range);
            Vec3 ground = ACMath.getGroundBelowPosition(target.level(), heightAdjusted);
            BlockHitResult result = target.level().clip(new ClipContext(target.getEyePosition(), ground.add(0, 1, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target));
            if (result.getType() == HitResult.Type.MISS) {
                return ground;
            }
        }
        return null;
    }

    private void peppermintAttack(LivingEntity target) {
        boolean spinning = licowitch.getRandom().nextBoolean();
        int spawnIn = 3;
        Vec3 subtract = target.position().subtract(licowitch.position());
        float f = -((float) Mth.atan2(subtract.x, subtract.z)) * 180.0F / (float) Math.PI;
        for (int i = 0; i < spawnIn; i++) {
            SpinningPeppermintEntity spinningPeppermintEntity = ACEntityRegistry.SPINNING_PEPPERMINT.get().create(licowitch.level());
            spinningPeppermintEntity.setPos(licowitch.getStaffPosition());
            spinningPeppermintEntity.setStraight(!spinning);
            spinningPeppermintEntity.setYRot(180 + f + (i - 1) * 30);
            spinningPeppermintEntity.setSpinSpeed(spinning ? 12F : 8F);
            spinningPeppermintEntity.setSpinRadius(3.5F);
            spinningPeppermintEntity.setOwner(licowitch);
            spinningPeppermintEntity.setStartAngle(i * 360 / (float) spawnIn);
            licowitch.level().addFreshEntity(spinningPeppermintEntity);
        }
        licowitch.level().playSound((Player) null, licowitch.blockPosition(), ACSoundRegistry.LICOWITCH_CAST_PEPPERMINT.get(), SoundSource.HOSTILE, 0.3F, 0.9F + licowitch.level().random.nextFloat() * 0.2F);
    }
}