package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.LicowitchEntity;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SpinningPeppermintEntity extends Entity {

    private static EntityDataAccessor<Optional<Vec3>> SPIN_AROUND = SynchedEntityData.defineId(SpinningPeppermintEntity.class, ACEntityDataRegistry.OPTIONAL_VEC_3.get());
    private static final EntityDataAccessor<Float> SPIN_RADIUS = SynchedEntityData.defineId(SpinningPeppermintEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPIN_SPEED = SynchedEntityData.defineId(SpinningPeppermintEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_ANGLE = SynchedEntityData.defineId(SpinningPeppermintEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> STRAIGHT = SynchedEntityData.defineId(SpinningPeppermintEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(SpinningPeppermintEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SEEKING_ENTITY = SynchedEntityData.defineId(SpinningPeppermintEntity.class, EntityDataSerializers.INT);
    public ItemStack peppermintRenderStack = new ItemStack(ACBlockRegistry.SMALL_PEPPERMINT.get());
    private int despawnsIn = -1;
    private int prevDespawnsIn;
    @javax.annotation.Nullable
    private LivingEntity owner;
    @javax.annotation.Nullable
    private UUID ownerUUID;

    private float startAngle;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    private float spinAngle;

    public SpinningPeppermintEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public SpinningPeppermintEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.SPINNING_PEPPERMINT.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SPIN_AROUND, Optional.empty());
        this.entityData.define(SPIN_RADIUS, 1.0F);
        this.entityData.define(SPIN_SPEED, 1.0F);
        this.entityData.define(START_ANGLE, 0.0F);
        this.entityData.define(STRAIGHT, false);
        this.entityData.define(LIFESPAN, 200);
        this.entityData.define(SEEKING_ENTITY, -1);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setOwner(@javax.annotation.Nullable LivingEntity living) {
        this.owner = living;
        this.ownerUUID = living == null ? null : living.getUUID();
    }

    @javax.annotation.Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }

        return this.owner;
    }

    public void tick() {
        super.tick();
        if(despawnsIn == -1){
            despawnsIn = this.getLifespan();
        }
        prevDespawnsIn = despawnsIn;
        if (despawnsIn > 0) {
            despawnsIn--;
        } else if (!level().isClientSide) {
            this.discard();
        }
        this.setSpinRadius(3F);
        this.setSpinSpeed(7F);
        Vec3 encirclePos = this.getSpinAroundPosition();
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
        } else {
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
            Entity owner = getOwner();
            if (owner instanceof Mob mob) {
                LivingEntity target = mob.getTarget();
                if (target != null && encirclePos != null) {
                    Vec3 add = target.getEyePosition().subtract(encirclePos);
                    if (add.length() > 1.0F) {
                        add = add.normalize();
                    }
                    this.setSpinAroundPosition(encirclePos.add(add.scale(0.05F)));
                }
            }else if(owner instanceof Player player){
                Vec3 playerPos = player.position().add(0, player.getBbHeight() * 0.45F, 0);
                Entity seeking = this.getSeekingEntityId() == -1 ? null : level().getEntity(this.getSeekingEntityId());
                if(seeking != null){
                    Vec3 add = seeking.getEyePosition().subtract(this.position());
                    if (add.length() > 1.0F) {
                        add = add.normalize();
                    }
                    this.setSpinRadius(4.0F - 4.0F * Math.min(1F, tickCount / 30F));
                    this.setSpinAroundPosition(this.position().add(add));
                }else{
                    this.setSpinAroundPosition(playerPos);
                }
            }
        }
        if (this.isStraight()) {
            if (!level().isClientSide) {
                Vec3 vec3 = new Vec3(0, 0, -0.01F * this.getSpinSpeed()).yRot((float) -Math.toRadians(this.getYRot()));
                this.setDeltaMovement(this.getDeltaMovement().add(vec3));
                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.9F).add(0.0D, -0.08D, 0.0D));
                }
                if (this.verticalCollision) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.9F, 0).multiply(0.4D, 1.0D, 0.4D));
                }
                this.move(MoverType.SELF, getDeltaMovement());
            }
        } else {
            if (encirclePos == null) {
                this.setSpinAroundPosition(this.position());
            } else if (!level().isClientSide) {
                this.move(MoverType.SELF, getDeltaMovement());
                float f = Math.min(1.0F, tickCount / 30F);
                Vec3 angle = new Vec3(0, 0, f * this.getSpinRadius()).yRot((float) -Math.toRadians(this.getStartAngle() + spinAngle));
                Vec3 encircle = encirclePos.add(angle);
                Vec3 newDelta = encircle.subtract(this.position());
                this.setDeltaMovement(newDelta.scale(0.05 * getSpinSpeed()));
                spinAngle += getSpinSpeed();
            }
        }
        hurtEntities();
    }

    public void setDespawnsIn(int i) {
        this.despawnsIn = i;
    }

    public float getDespawnTime(float partialTicks) {
        return prevDespawnsIn + (despawnsIn - prevDespawnsIn) * partialTicks;
    }

    @Nullable
    public Vec3 getSpinAroundPosition() {
        return this.entityData.get(SPIN_AROUND).orElse(null);
    }

    public void setSpinAroundPosition(@Nullable Vec3 vec3) {
        this.entityData.set(SPIN_AROUND, Optional.ofNullable(vec3));
    }

    public float getSpinSpeed() {
        return this.entityData.get(SPIN_SPEED);
    }

    public void setSpinSpeed(float spinSpeed) {
        this.entityData.set(SPIN_SPEED, spinSpeed);
    }

    public float getSpinRadius() {
        return this.entityData.get(SPIN_RADIUS);
    }

    public void setSpinRadius(float spinRadius) {
        this.entityData.set(SPIN_RADIUS, spinRadius);
    }

    public boolean isStraight() {
        return this.entityData.get(STRAIGHT);
    }

    public void setStraight(boolean straight) {
        this.entityData.set(STRAIGHT, straight);
    }

    public float getStartAngle() {
        return this.entityData.get(START_ANGLE);
    }

    public void setStartAngle(float f) {
        this.entityData.set(START_ANGLE, f);
    }

    public void setLifespan(int lifespan) {
        this.entityData.set(LIFESPAN, lifespan);
    }

    public int getLifespan() {
        return this.entityData.get(LIFESPAN);
    }

    public void setSeekingEntityId(int seekingEntityId) {
        this.entityData.set(SEEKING_ENTITY, seekingEntityId);
    }

    public int getSeekingEntityId() {
        return this.entityData.get(SEEKING_ENTITY);
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setStraight(compoundTag.getBoolean("Straight"));
        if (compoundTag.contains("DespawnsIn")) {
            this.despawnsIn = compoundTag.getInt("DespawnsIn");
        }
        this.setLifespan(compoundTag.getInt("Lifespan"));
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        if (compoundTag.contains("AroundX") && compoundTag.contains("AroundY") && compoundTag.contains("AroundZ")) {
            this.setSpinAroundPosition(new Vec3(compoundTag.getDouble("AroundX"), compoundTag.getDouble("AroundZ"), compoundTag.getDouble("AroundZ")));
        }
        this.setSpinSpeed(compoundTag.getFloat("SpinSpeed"));
        this.setSpinRadius(compoundTag.getFloat("SpinRadius"));
        this.setStartAngle(compoundTag.getFloat("StartAngle"));
        this.spinAngle = compoundTag.getFloat("SpinAngle");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("DespawnsIn", this.despawnsIn);
        compoundTag.putBoolean("Straight", this.isStraight());
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        Vec3 vec3 = getSpinAroundPosition();
        if (vec3 != null) {
            compoundTag.putDouble("AroundX", vec3.x);
            compoundTag.putDouble("AroundY", vec3.y);
            compoundTag.putDouble("AroundZ", vec3.z);
        }
        compoundTag.putFloat("SpinSpeed", this.getSpinSpeed());
        compoundTag.putFloat("SpinRadius", this.getSpinRadius());
        compoundTag.putFloat("StartAngle", this.getStartAngle());
        compoundTag.putFloat("SpinAngle", this.spinAngle);
        compoundTag.putInt("Lifespan", this.getLifespan());
    }

    private void hurtEntities() {
        AABB bashBox = this.getBoundingBox();
        DamageSource source = damageSources().mobProjectile(this, owner);
        boolean flag = false;
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
            if (!isAlliedTo(entity) && (owner != null && !entity.is(owner) && (!(owner instanceof LicowitchEntity) && !entity.isAlliedTo(owner) || owner instanceof LicowitchEntity licowitch && !licowitch.isFriendlyFire(entity)))) {
                if (entity.hurt(source, 3.0F)) {
                    flag = true;
                    entity.knockback(0.3F, this.getX() - entity.getX(), this.getZ() - entity.getZ());
                }
            }
        }
        if(flag && this.getSeekingEntityId() != -1){
            this.discard();
        }
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
