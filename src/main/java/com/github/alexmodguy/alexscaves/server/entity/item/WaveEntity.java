package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.UUID;

public class WaveEntity extends Entity {

    private static final EntityDataAccessor<Boolean> SLAMMING = SynchedEntityData.defineId(WaveEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(WaveEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAITING_TICKS = SynchedEntityData.defineId(WaveEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(WaveEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WAVE_SCALE = SynchedEntityData.defineId(WaveEntity.class, EntityDataSerializers.FLOAT);
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    private float slamProgress;
    private float prevSlamProgress;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    public int activeWaveTicks;

    public WaveEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public float getStepHeight() {
        return 2F;
    }

    public WaveEntity(Level level, LivingEntity shooter) {
        this(ACEntityRegistry.WAVE.get(), level);
        this.setOwner(shooter);
    }

    public WaveEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.WAVE.get(), level);
    }

    public void setOwner(@Nullable LivingEntity living) {
        this.owner = living;
        this.ownerUUID = living == null ? null : living.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }

        return this.owner;
    }


    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(SLAMMING, false);
        this.getEntityData().define(LIFESPAN, 10);
        this.getEntityData().define(WAITING_TICKS, 0);
        this.getEntityData().define(Y_ROT, 0F);
        this.getEntityData().define(WAVE_SCALE, 1F);

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public float getSlamAmount(float partialTicks) {
        return (prevSlamProgress + (slamProgress - prevSlamProgress) * partialTicks) * 0.1F;
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
        if (tag.contains("Lifespan")) {
            this.setLifespan(tag.getInt("Lifespan"));
        }
    }

    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        compoundTag.putInt("Lifespan", this.getLifespan());
    }

    public float getYRot() {
        return this.entityData.get(Y_ROT);
    }

    public void setYRot(float f) {
        this.entityData.set(Y_ROT, f);
    }

    public int getLifespan() {
        return this.entityData.get(LIFESPAN);
    }

    public void setLifespan(int time) {
        this.entityData.set(LIFESPAN, time);
    }

    public int getWaitingTicks() {
        return this.entityData.get(WAITING_TICKS);
    }

    public void setWaitingTicks(int time) {
        this.entityData.set(WAITING_TICKS, time);
    }

    public boolean isSlamming() {
        return this.entityData.get(SLAMMING);
    }

    public void setSlamming(boolean bool) {
        this.entityData.set(SLAMMING, bool);
    }

    public float getWaveScale() {
        return this.entityData.get(WAVE_SCALE);
    }

    public void setWaveScale(float waveScale) {
        this.entityData.set(WAVE_SCALE, waveScale);
    }

    private void spawnParticleAt(float yOffset, float zOffset, float xOffset, ParticleOptions particleType) {
        Vec3 vec3 = new Vec3(xOffset, yOffset, zOffset).yRot((float) Math.toRadians(-this.getYRot()));
        this.level().addParticle(particleType, this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z, this.getDeltaMovement().x, 0.1F, this.getDeltaMovement().z);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {

    }

    public void tick() {
        super.tick();
        prevSlamProgress = slamProgress;
        if(this.getWaitingTicks() > 0){
            if(!level().isClientSide){
                this.setWaitingTicks(this.getWaitingTicks() - 1);
            }
            this.setInvisible(true);
            return;
        }else{
            if(this.isInvisible()){
                this.setInvisible(false);
            }
        }
        if (isSlamming() && slamProgress < 10.0F) {
            slamProgress += 1F;
        }
        if (isSlamming() && slamProgress == 10.0F) {
            this.discard();
        }
        if (!this.isNoGravity() && !this.isInWaterOrBubble()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double) -0.04F, 0.0D));
        }
        float f = Math.min(this.activeWaveTicks / 10F, 1F);
        Vec3 directionVec = new Vec3(0, 0, f * f * 0.2F).yRot((float) Math.toRadians(-this.getYRot()));
        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
            for(int particleCount = 0; particleCount < getWaveScale(); particleCount++){
                for (int i = 0; i <= 4; i++) {
                    float xOffset = (float) i / 4F - 0.5F + (random.nextFloat() - 0.5F) * 0.2F;
                    spawnParticleAt((0.2F + random.nextFloat() * 0.2F) * this.getWaveScale(), 1.2F, xOffset * 1.2F * this.getWaveScale(), ACParticleRegistry.WATER_FOAM.get());
                    spawnParticleAt((0.2F + random.nextFloat() * 0.2F) * this.getWaveScale(), -0.2F, xOffset * 1.4F * this.getWaveScale(), ParticleTypes.SPLASH);
                }
            }
        } else {
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
        }
        if (!level().isClientSide) {
            attackEntities(getSlamAmount(1.0F) * 2 + 1 + this.getWaveScale());
        }
        Vec3 vec3 = this.getDeltaMovement().scale(0.9F).add(directionVec);
        this.move(MoverType.SELF, vec3);
        this.setDeltaMovement(vec3.multiply((double) 0.99F, (double) 0.98F, (double) 0.99F));
        if (this.activeWaveTicks > getLifespan() || this.activeWaveTicks > 10 && this.getDeltaMovement().horizontalDistance() < 0.04) {
            this.setSlamming(true);
        }
        activeWaveTicks++;
    }

    private void attackEntities(float scale) {
        AABB bashBox = this.getBoundingBox().inflate(0.5f, 0.5f, 0.5f);
        DamageSource source = damageSources().mobProjectile(this, owner);
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
            if (!isAlliedTo(entity) && !(entity instanceof DeepOneBaseEntity) && (owner == null || !owner.equals(entity) && !owner.isAlliedTo(entity))) {
                entity.hurt(source, scale + 1.0F);
                this.setSlamming(true);
                entity.knockback(0.1D + 0.5D * scale, (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
            }
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (WAVE_SCALE.equals(dataAccessor)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getWaveScale());
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }
}
