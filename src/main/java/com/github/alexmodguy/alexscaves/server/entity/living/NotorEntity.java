package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ai.NotorFlightGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.NotorHologramGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.NotorScanGoal;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class NotorEntity extends PathfinderMob {
    private float groundProgress;
    private float prevGroundProgress;
    private float beamProgress;
    private float prevBeamProgress;
    private float hologramProgress;
    private float prevHologramProgress;
    private float propellerRot;
    private float prevPropellerRot;
    public int stopScanningFor = 80 + random.nextInt(220);
    private static final EntityDataAccessor<Boolean> SHOWING_HOLOGRAM = SynchedEntityData.defineId(NotorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> HOLOGRAM_POS = SynchedEntityData.defineId(NotorEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> SCANNING_ID = SynchedEntityData.defineId(NotorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> HOLOGRAM_ENTITY_UUID = SynchedEntityData.defineId(NotorEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> HOLOGRAM_ENTITY_ID = SynchedEntityData.defineId(NotorEntity.class, EntityDataSerializers.INT);

    public static final Predicate<LivingEntity> SCAN_TARGET = (mob) -> {
        return mob.isAlive() && !mob.getType().is(ACTagRegistry.NOTOR_IGNORES) && !mob.isInvisible();
    };
    private int flyingSoundTimer;

    public NotorEntity(EntityType entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlightMoveHelper(this);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new FlyingPathNavigation(this, worldIn) {
            public boolean isStableDestination(BlockPos blockPos) {
                return this.level.getBlockState(blockPos).isAir();
            }
        };
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new NotorHologramGoal(this));
        this.goalSelector.addGoal(2, new NotorScanGoal(this));
        this.goalSelector.addGoal(3, new NotorFlightGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.15D).add(Attributes.MAX_HEALTH, 6.0D);
    }

    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        return levelReader.getBlockState(blockPos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(HOLOGRAM_POS, Optional.empty());
        this.entityData.define(SHOWING_HOLOGRAM, false);
        this.entityData.define(SCANNING_ID, -1);
        this.entityData.define(HOLOGRAM_ENTITY_UUID, Optional.empty());
        this.entityData.define(HOLOGRAM_ENTITY_ID, -1);
    }

    public static boolean checkNotorSpawnRules(EntityType<NotorEntity> notor, LevelAccessor level, MobSpawnType spawnType, BlockPos blockPos, RandomSource randomSource) {
        if (blockPos.getY() >= level.getSeaLevel()) {
            return false;
        } else {
            int i = level.getMaxLocalRawBrightness(blockPos);
            int j = 4;
            return i > randomSource.nextInt(j) ? false : checkMobSpawnRules(notor, level, spawnType, blockPos, randomSource);
        }
    }

    public void tick() {
        super.tick();
        Entity hologram = getHologramEntity();
        prevGroundProgress = groundProgress;
        prevBeamProgress = beamProgress;
        prevHologramProgress = hologramProgress;
        prevPropellerRot = propellerRot;
        if (this.onGround() && groundProgress < 5.0F) {
            groundProgress++;
        }
        if (!this.onGround() && groundProgress > 0.0F) {
            groundProgress--;
        }
        Entity scanningMob = this.getScanningMob();
        boolean hasHologram = hologram != null && this.showingHologram();
        boolean hasBeam = scanningMob != null || hasHologram;
        if (hasBeam && beamProgress < 5.0F) {
            if(beamProgress == 0.0F && (hasHologram || scanningMob != null && this.hasLineOfSight(scanningMob))){
                this.playSound(ACSoundRegistry.HOLOGRAM_STOP.get());
            }
            beamProgress++;
        }
        if(hasBeam && this.isAlive()){
            AlexsCaves.PROXY.playWorldSound(this, (byte) 2);
        }
        if (!hasBeam && beamProgress > 0.0F) {
            if(beamProgress == 5.0F){
              this.playSound(ACSoundRegistry.HOLOGRAM_STOP.get());
            }
            if (this.hologramProgress > 0.0F) {
                hologramProgress--;
            } else {
                beamProgress--;
            }
        }
        if (hasHologram && beamProgress >= 5.0F && hologramProgress < 5.0F) {
            hologramProgress++;
        }
        if (!hasHologram && hologramProgress > 0.0F) {
            hologramProgress--;
        }
        double speed = this.getDeltaMovement().horizontalDistance();
        if (!this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9F, 0.9F, 0.9F));
            propellerRot += Math.max(speed * 10, 3) * 20;
            if(flyingSoundTimer++ >= 10){
                if(!level().isClientSide && !this.isSilent()){
                    this.playSound(ACSoundRegistry.NOTOR_FLYING.get(), 0.4F, 1.0F);
                }
                flyingSoundTimer = 0;
            }
        } else if (Mth.wrapDegrees(propellerRot) != 0) {
            flyingSoundTimer = 0;
            propellerRot = Mth.approachDegrees(propellerRot, 0, 15);
        }
        if (!level().isClientSide) {
            if (hologram != null) {
                this.entityData.set(HOLOGRAM_ENTITY_ID, hologram.getId());
            } else {
                this.entityData.set(HOLOGRAM_ENTITY_ID, -1);
            }
            if (stopScanningFor > 0) {
                stopScanningFor--;
            }
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.remove(removalReason);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 6.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);

    }

    public float getGroundProgress(float partialTick) {
        return (prevGroundProgress + (groundProgress - prevGroundProgress) * partialTick) * 0.2F;
    }

    public float getBeamProgress(float partialTick) {
        return (prevBeamProgress + (beamProgress - prevBeamProgress) * partialTick) * 0.2F;
    }

    public float getPropellerAngle(float partialTick) {
        return prevPropellerRot + (propellerRot - prevPropellerRot) * partialTick;
    }

    public void setScanningId(int i) {
        this.entityData.set(SCANNING_ID, i);
    }

    public Entity getScanningMob() {
        int id = getScanningId();
        return id == -1 ? null : level().getEntity(id);
    }

    @javax.annotation.Nullable
    public UUID getHologramUUID() {
        return this.entityData.get(HOLOGRAM_ENTITY_UUID).orElse(null);
    }


    public void setHologramUUID(@javax.annotation.Nullable UUID hologram) {
        this.entityData.set(HOLOGRAM_ENTITY_UUID, Optional.ofNullable(hologram));
    }

    public Entity getHologramEntity() {
        if (!level().isClientSide) {
            UUID id = getHologramUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(HOLOGRAM_ENTITY_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public int getScanningId() {
        return this.entityData.get(SCANNING_ID);
    }

    public void setHologramPos(@javax.annotation.Nullable BlockPos pos) {
        this.getEntityData().set(HOLOGRAM_POS, Optional.ofNullable(pos));
    }


    public boolean showingHologram() {
        return this.entityData.get(SHOWING_HOLOGRAM);
    }

    public void setShowingHologram(boolean showingHologram) {
        this.entityData.set(SHOWING_HOLOGRAM, showingHologram);
    }

    @javax.annotation.Nullable
    public BlockPos getHologramPos() {
        return this.getEntityData().get(HOLOGRAM_POS).orElse((BlockPos) null);
    }

    public Vec3 getBeamEndPosition(float partialTicks) {
        Entity scanning = this.getScanningMob();
        if (scanning != null) {
            float f = (float) Math.abs(Math.sin((tickCount + partialTicks) * 0.1F));
            return scanning.getPosition(partialTicks).add(0, scanning.getBbHeight() * f, 0);
        } else {
            BlockPos pos = this.getEntityData().get(HOLOGRAM_POS).orElse((BlockPos) null);
            if (pos == null) {
                return this.getPosition(partialTicks).add(0, -3, 0);
            } else {
                return Vec3.atCenterOf(pos);
            }
        }
    }

    public float getHologramProgress(float partialTicks) {
        return (prevHologramProgress + (hologramProgress - prevHologramProgress) * partialTicks) * 0.2F;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("ShowingHologram", this.showingHologram());
        if (this.getHologramUUID() != null) {
            compound.putUUID("HologramUUID", this.getHologramUUID());
        }
        compound.putInt("StopScanningTime", stopScanningFor);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setShowingHologram(compound.getBoolean("ShowingHologram"));
        if (compound.hasUUID("HologramUUID")) {
            this.setHologramUUID(compound.getUUID("HologramUUID"));
        }
        stopScanningFor = compound.getInt("StopScanningTime");
    }

    public boolean removeWhenFarAway(double dist) {
        return !this.hasCustomName();
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != ACEffectRegistry.MAGNETIZING.get();
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.NOTOR_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.NOTOR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.NOTOR_DEATH.get();
    }


    class FlightMoveHelper extends MoveControl {
        private final NotorEntity parentEntity;

        public FlightMoveHelper(NotorEntity bird) {
            super(bird);
            this.parentEntity = bird;
        }

        public void tick() {
            boolean gravity = true;
            if (parentEntity.getScanningId() != -1 || parentEntity.getHologramEntity() != null) {
                gravity = false;
                float angle = (0.01745329251F * (parentEntity.yBodyRot + 90));
                float radius = (float) Math.sin(parentEntity.tickCount * 0.2F) * 2;
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraZ = radius * Mth.cos(angle);
                Vec3 strafPlus = new Vec3(extraX, 0, extraZ);
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(strafPlus.scale(0.01D)));

            }
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                final Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                final double d5 = vector3d.length();
                if (d5 < parentEntity.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    gravity = false;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d5)));
                    final Vec3 vector3d1 = parentEntity.getDeltaMovement();
                    float f = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * 180.0F / (float) Math.PI;
                    parentEntity.setYRot(Mth.approachDegrees(parentEntity.getYRot(), f, 20));
                    parentEntity.yBodyRot = parentEntity.getYRot();
                }
            }
            parentEntity.setNoGravity(!gravity);
        }
    }
}
