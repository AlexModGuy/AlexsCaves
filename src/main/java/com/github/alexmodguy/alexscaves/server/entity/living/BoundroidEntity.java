package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ai.MobTarget3DGoal;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class BoundroidEntity extends Monster {

    private static final EntityDataAccessor<Optional<UUID>> WINCH_UUID = SynchedEntityData.defineId(BoundroidEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> WINCH_ID = SynchedEntityData.defineId(BoundroidEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SLAMMING = SynchedEntityData.defineId(BoundroidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SCARED = SynchedEntityData.defineId(BoundroidEntity.class, EntityDataSerializers.BOOLEAN);
    private float groundProgress;
    private float prevGroundProgress;
    public boolean draggedClimable = false;
    public boolean stopGravity = false;

    public int stopSlammingFor = 0;
    private int stayOnGroundFor = 0;

    private static final AttributeModifier REMOVED_GRAVITY_MODIFIER = new AttributeModifier(UUID.fromString("B5B6CF2A-2F7C-31EF-9022-7C3E7D5E6BBA"), "remove gravity reduction", -0.08, AttributeModifier.Operation.ADDITION);

    public BoundroidEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.ARMOR, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 20.0D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new MobTarget3DGoal(this, Player.class, true));
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WINCH_UUID, Optional.empty());
        this.entityData.define(WINCH_ID, -1);
        this.entityData.define(SCARED, false);
        this.entityData.define(SLAMMING, false);
    }

    @Nullable
    public UUID getWinchUUID() {
        return this.entityData.get(WINCH_UUID).orElse(null);
    }

    public void setWinchUUID(@Nullable UUID uniqueId) {
        this.entityData.set(WINCH_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getWinch() {
        if (!level().isClientSide) {
            UUID id = getWinchUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(WINCH_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public void tick() {
        super.tick();
        this.prevGroundProgress = groundProgress;
        this.yBodyRot = this.getYRot();
        if (this.onGround() && groundProgress < 5F) {
            groundProgress++;
        }
        if (!this.onGround() && groundProgress > 0F) {
            groundProgress--;
        }
        if (!level().isClientSide) {
            Entity winch = getWinch();
            if (winch == null) {
                LivingEntity created = createWinch();
                level().addFreshEntity(created);
                this.setWinchUUID(created.getUUID());
                this.entityData.set(WINCH_ID, created.getId());
            } else {
                if (winch instanceof BoundroidWinchEntity winchEntity) {
                    winchEntity.linkWithHead(this);
                }
            }
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                Vec3 distanceVec = target.position().subtract(this.position());
                if (distanceVec.horizontalDistance() < 1.2 && stopSlammingFor == 0) {
                    this.setSlamming(true);
                }
            }
            if (this.isSlamming()) {
                this.setDeltaMovement(new Vec3(this.getDeltaMovement().x, -1, this.getDeltaMovement().z));
                if (this.onGround()) {
                    this.setSlamming(false);
                    this.playSound(ACSoundRegistry.BOUNDROID_SLAM.get());
                    this.stayOnGroundFor = 10;
                    this.stopSlammingFor = 30 + random.nextInt(20);
                    this.level().broadcastEntityEvent(this, (byte) 45);
                    AABB bashBox = new AABB(-1.5F, -2F, -1.5F, 1.5F, 1F, 1.5F);
                    bashBox = bashBox.move(this.position());
                    for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
                        if (!isAlliedTo(entity) && !(entity instanceof BoundroidEntity) && !(entity instanceof BoundroidWinchEntity)) {
                            entity.hurt(damageSources().mobAttack(this), 5);
                        }
                    }
                }
            }
        }else if(isAlive()){
            AlexsCaves.PROXY.playWorldSound(this, (byte) 12);
        }
        if (stopSlammingFor > 0) {
            stopSlammingFor--;
        }
        if (stayOnGroundFor > 0) {
            stayOnGroundFor--;
        }
    }

    public void handleEntityEvent(byte b) {
        if (b == 45) {
            spawnGroundEffects();
        } else {
            super.handleEntityEvent(b);
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.remove(removalReason);
    }

    public void spawnGroundEffects() {
        float radius = 1.35F;
        for (int i = 0; i < 4; i++) {
            for (int i1 = 0; i1 < 10 + random.nextInt(10); i1++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float angle = (0.01745329251F * this.yBodyRot) + i1;
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * Mth.cos(angle);
                Vec3 center = this.position().add(new Vec3(0, 3, 0).yRot((float) Math.toRadians(-this.yBodyRot)));
                BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(level(), new Vec3(Mth.floor(center.x + extraX), Mth.floor(center.y + extraY) - 1, Mth.floor(center.z + extraZ))));
                BlockState state = this.level().getBlockState(ground);
                if (state.isSolid()) {
                    if (level().isClientSide) {
                        level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), true, center.x + extraX, ground.getY() + extraY, center.z + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    public boolean onClimbable() {
        return super.onClimbable() || horizontalCollision && draggedClimable;
    }

    public float getGroundProgress(float partialTicks) {
        return (prevGroundProgress + (groundProgress - prevGroundProgress) * partialTicks) * 0.2F;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL);
    }

    public boolean isScared() {
        return this.entityData.get(SCARED);
    }

    public void setScared(boolean scared) {
        this.entityData.set(SCARED, scared);
    }

    public boolean isSlamming() {
        return this.entityData.get(SLAMMING);
    }

    public boolean stopPullingUp() {
        return isSlamming() || stayOnGroundFor > 0;
    }

    public void setSlamming(boolean slamming) {
        this.entityData.set(SLAMMING, slamming);
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != ACEffectRegistry.MAGNETIZING.get();
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("WinchUUID")) {
            this.setWinchUUID(compound.getUUID("WinchUUID"));
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getWinchUUID() != null) {
            compound.putUUID("WinchUUID", this.getWinchUUID());
        }
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.BOUNDROID_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.BOUNDROID_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.BOUNDROID_DEATH.get();
    }

    private LivingEntity createWinch() {
        return new BoundroidWinchEntity(this);
    }

}
