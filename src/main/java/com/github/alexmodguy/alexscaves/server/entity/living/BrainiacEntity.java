package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.item.ThrownWasteDrumEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BrainiacEntity extends Monster implements IAnimatedEntity {

    private Animation currentAnimation;
    private int animationTick;
    public static final Animation ANIMATION_THROW_BARREL = Animation.create(30);
    public static final Animation ANIMATION_DRINK_BARREL = Animation.create(75);
    public static final Animation ANIMATION_BITE = Animation.create(25);
    public static final Animation ANIMATION_SMASH = Animation.create(20);

    private static final EntityDataAccessor<Boolean> HAS_BARREL = SynchedEntityData.defineId(BrainiacEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TONGUE_TARGET_ID = SynchedEntityData.defineId(BrainiacEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TONGUE_SHOOT_TICK = SynchedEntityData.defineId(BrainiacEntity.class, EntityDataSerializers.INT);
    private float prevRaiseArmsAmount = 0;
    private float raiseArmsAmount = 0;
    private float prevLeftArmAmount = 0;
    private float raiseLeftArmAmount = 0;
    private float prevShootTongueAmount = 0;
    private float shootTongueAmount = 0;
    private float prevLastTongueDistance = 0;
    private float lastTongueDistance = 0;

    public BrainiacEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_BARREL, true);
        this.entityData.define(TONGUE_TARGET_ID, -1);
        this.entityData.define(TONGUE_SHOOT_TICK, 0);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new PickupBarrelGoal());
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ARMOR, 8.0D).add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevRaiseArmsAmount = raiseArmsAmount;
        this.prevLeftArmAmount = raiseLeftArmAmount;
        this.prevShootTongueAmount = shootTongueAmount;
        this.prevLastTongueDistance = lastTongueDistance;
        if (this.getAnimation() == ANIMATION_SMASH && raiseArmsAmount < 5F) {
            raiseArmsAmount++;
        }
        if (this.getAnimation() != ANIMATION_SMASH && raiseArmsAmount > 0F) {
            raiseArmsAmount--;
        }
        if (this.raisingLeftArm() && raiseLeftArmAmount < 5F) {
            raiseLeftArmAmount++;
        }
        if (!this.raisingLeftArm() && raiseLeftArmAmount > 0F) {
            raiseLeftArmAmount--;
        }
        if (this.getLickTicks() > 0 && shootTongueAmount < 10F) {
            shootTongueAmount++;
        }
        if (this.getLickTicks() <= 0 && shootTongueAmount > 0F) {
            shootTongueAmount--;
        }
        if (!level().isClientSide && this.hasBarrel()) {
            if (this.getAnimation() == ANIMATION_DRINK_BARREL && this.getAnimationTick() >= 60) {
                this.setHasBarrel(false);
                this.heal(10);
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 0));
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 4));
            }
            if (this.getAnimation() == ANIMATION_THROW_BARREL && this.getAnimationTick() >= 15) {
                LivingEntity attackTarget = this.getTarget();
                if (attackTarget != null && attackTarget.isAlive()) {
                    this.setHasBarrel(false);
                    Vec3 hand = new Vec3(0.65F, -0.3F, 0.9F).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F));
                    Vec3 handOnBody = this.getEyePosition().add(hand);
                    ThrownWasteDrumEntity wasteDrumEntity = ACEntityRegistry.THROWN_WASTE_DRUM.get().create(level());
                    wasteDrumEntity.setPos(handOnBody);
                    Vec3 toss = attackTarget.getEyePosition().subtract(handOnBody).multiply(0.35F, 0, 0.35F).add(0, 0.4, 0);
                    wasteDrumEntity.setYRot(-((float) Mth.atan2(toss.x, toss.z)) * 180.0F / (float) Math.PI);
                    level().addFreshEntity(wasteDrumEntity);
                    wasteDrumEntity.setDeltaMovement(toss.normalize().scale(attackTarget.distanceTo(this) * 0.2F));
                }
            }
        }
        Entity tongueTarget = this.getTongueTarget();
        if (level().isClientSide) {
            if (tongueTarget != null && tongueTarget.isAlive()) {
                lastTongueDistance = this.distanceTo(tongueTarget) - 0.5F;
            }
        } else {
            LivingEntity attackTarget = this.getTarget();
            if (this.getLickTicks() > 0) {
                this.setLickTicks(this.getLickTicks() - 1);
                if (attackTarget != null && attackTarget.isAlive() && this.hasLineOfSight(attackTarget) && attackTarget.distanceTo(this) < 20) {
                    this.setTongueTargetId(attackTarget.getId());
                    this.lookAt(EntityAnchorArgument.Anchor.EYES, attackTarget.getEyePosition());
                } else {
                    this.setTongueTargetId(-1);
                }
            } else {
                this.setTongueTargetId(-1);
            }
        }
        if (tongueTarget instanceof LivingEntity living) {
            if (this.shootTongueAmount >= 5F) {
                postAttackEffect(living);
                tongueTarget.hurt(damageSources().mobAttack(this), 4);
                living.knockback(0.3D, living.getX() - this.getX(), tongueTarget.getZ() - this.getZ());
                this.setLickTicks(0);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_THROW_BARREL, ANIMATION_DRINK_BARREL, ANIMATION_SMASH, ANIMATION_BITE};
    }

    public float getRaiseArmsAmount(float partialTick) {
        return (prevRaiseArmsAmount + (raiseArmsAmount - prevRaiseArmsAmount) * partialTick) * 0.2F;
    }

    public boolean raisingLeftArm() {
        return this.getAnimation() == ANIMATION_DRINK_BARREL || this.getAnimation() == ANIMATION_BITE || this.getAnimation() == ANIMATION_THROW_BARREL;
    }

    public float getRaiseLeftArmAmount(float partialTick) {
        return (prevLeftArmAmount + (raiseLeftArmAmount - prevLeftArmAmount) * partialTick) * 0.2F;
    }

    public float getLastTongueDistance(float partialTick) {
        return (prevLastTongueDistance + (lastTongueDistance - prevLastTongueDistance) * partialTick);
    }

    public float getShootTongueAmount(float partialTick) {
        return (prevShootTongueAmount + (shootTongueAmount - prevShootTongueAmount) * partialTick) * 0.1F;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHasBarrel(compound.getBoolean("HasBarrel"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasBarrel", this.hasBarrel());
    }

    public Entity getTongueTarget() {
        int id = this.entityData.get(TONGUE_TARGET_ID);
        return id == -1 ? null : level().getEntity(id);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasBarrel() && shouldDropBlocks()) {
            this.spawnAtLocation(new ItemStack(ACBlockRegistry.WASTE_DRUM.get()));
        }
    }

    private boolean shouldDropBlocks() {
        DamageSource lastDamageSource = getLastDamageSource();
        if (lastDamageSource != null) {
            return lastDamageSource.getEntity() != null || lastDamageSource.getDirectEntity() != null;
        }
        return false;
    }

    public void postAttackEffect(LivingEntity entity) {
        if (entity != null && entity.isAlive()) {
            entity.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 400));
        }
    }

    public void setTongueTargetId(int id) {
        this.entityData.set(TONGUE_TARGET_ID, id);
    }

    public int getLickTicks() {
        return this.entityData.get(TONGUE_SHOOT_TICK);
    }

    public void setLickTicks(int ticks) {
        this.entityData.set(TONGUE_SHOOT_TICK, ticks);
    }

    public boolean hasBarrel() {
        return this.entityData.get(HAS_BARREL);
    }

    public void setHasBarrel(boolean barrel) {
        this.entityData.set(HAS_BARREL, barrel);
    }

    public float getStepHeight() {
        return 1.1F;
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.BRAINIAC_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.BRAINIAC_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.BRAINIAC_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ACSoundRegistry.BRAINIAC_STEP.get(), 1.0F, 1.0F);

    }

    private class MeleeGoal extends Goal {

        private int tongueCooldown = 0;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = BrainiacEntity.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = BrainiacEntity.this.getTarget();
            if (tongueCooldown > 0) {
                tongueCooldown--;
            }
            if (target != null && target.isAlive()) {
                double dist = BrainiacEntity.this.distanceTo(target);
                if (BrainiacEntity.this.getAnimation() == NO_ANIMATION) {
                    if (BrainiacEntity.this.getHealth() < BrainiacEntity.this.getMaxHealth() * 0.5F && BrainiacEntity.this.hasBarrel() && BrainiacEntity.this.random.nextInt(20) == 0) {
                        BrainiacEntity.this.setAnimation(ANIMATION_DRINK_BARREL);
                    }
                    BrainiacEntity.this.getNavigation().moveTo(target, 1.2D);
                    if (BrainiacEntity.this.hasLineOfSight(target)) {
                        if (BrainiacEntity.this.getHealth() < BrainiacEntity.this.getMaxHealth() * 0.75F && dist < 20 && BrainiacEntity.this.hasBarrel() && BrainiacEntity.this.random.nextInt(30) == 0) {
                            BrainiacEntity.this.setAnimation(ANIMATION_THROW_BARREL);
                            BrainiacEntity.this.playSound(ACSoundRegistry.BRAINIAC_THROW.get());
                        }
                        if (dist < BrainiacEntity.this.getBbWidth() + target.getBbWidth() + 3.5D) {
                            BrainiacEntity.this.setAnimation(BrainiacEntity.this.random.nextBoolean() ? ANIMATION_SMASH : ANIMATION_BITE);
                            BrainiacEntity.this.playSound(ACSoundRegistry.BRAINIAC_ATTACK.get());
                            return;
                        }
                        if (tongueCooldown == 0 && BrainiacEntity.this.random.nextInt(16) == 0 && dist < 25) {
                            BrainiacEntity.this.playSound(ACSoundRegistry.BRAINIAC_LICK.get());
                            BrainiacEntity.this.setLickTicks(20);
                            tongueCooldown = 15 + BrainiacEntity.this.random.nextInt(15);
                        }
                    } else {
                        BrainiacEntity.this.setLickTicks(0);
                    }
                }
                if (BrainiacEntity.this.getAnimation() == ANIMATION_SMASH) {
                    if (BrainiacEntity.this.getAnimationTick() >= 10 && BrainiacEntity.this.getAnimationTick() <= 15) {
                        checkAndDealDamage(target, 2);
                    }
                }
                if (BrainiacEntity.this.getAnimation() == ANIMATION_BITE) {
                    if (BrainiacEntity.this.getAnimationTick() >= 10 && BrainiacEntity.this.getAnimationTick() <= 15) {
                        checkAndDealDamage(target, 1);
                    }
                }
            }
        }

        private void checkAndDealDamage(LivingEntity target, float damageMultiplier) {
            if (BrainiacEntity.this.hasLineOfSight(target) && BrainiacEntity.this.distanceTo(target) < BrainiacEntity.this.getBbWidth() + target.getBbWidth() + 2.0D) {
                target.hurt(damageSources().mobAttack(BrainiacEntity.this), (float) BrainiacEntity.this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * damageMultiplier);
                BrainiacEntity.this.postAttackEffect(target);
                target.knockback(0.3D, BrainiacEntity.this.getX() - target.getX(), BrainiacEntity.this.getZ() - target.getZ());
            }
        }

    }

    private class PickupBarrelGoal extends MoveToBlockGoal {

        public PickupBarrelGoal() {
            super(BrainiacEntity.this, 1.0F, 20, 6);
        }

        protected int nextStartTick(PathfinderMob mob) {
            return reducedTickDelay(40 + BrainiacEntity.this.getRandom().nextInt(40));
        }

        protected BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        protected void moveMobToBlock() {
            BlockPos pos = getMoveToTarget();
            this.mob.getNavigation().moveTo((double) ((float) pos.getX()) + 0.5D, (double) (pos.getY() + 1), (double) ((float) pos.getZ()) + 0.5D, this.speedModifier);
        }

        @Override
        public boolean canUse() {
            if (BrainiacEntity.this.hasBarrel()) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !BrainiacEntity.this.hasBarrel();
        }

        public double acceptedDistance() {
            return BrainiacEntity.this.getBbWidth() + 1.0F;
        }

        @Override
        public void tick() {
            super.tick();
            if (blockPos != null) {
                BrainiacEntity.this.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(blockPos));
                if (this.isReachedTarget()) {
                    BrainiacEntity.this.getNavigation().stop();
                    if (BrainiacEntity.this.getAnimation() == NO_ANIMATION) {
                        BrainiacEntity.this.setAnimation(ANIMATION_BITE);
                    }
                    if (BrainiacEntity.this.getAnimation() == ANIMATION_BITE && BrainiacEntity.this.getAnimationTick() >= 10 && BrainiacEntity.this.getAnimationTick() <= 15) {
                        if (isValidTarget(level(), blockPos)) {
                            level().destroyBlock(blockPos, false);
                            BrainiacEntity.this.setHasBarrel(true);
                        }
                    }
                }

            }
        }


        public void stop() {
            super.stop();
            this.blockPos = BlockPos.ZERO;
        }


        @Override
        protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
            return pos != null && worldIn.getBlockState(pos).is(ACBlockRegistry.WASTE_DRUM.get());
        }
    }

}