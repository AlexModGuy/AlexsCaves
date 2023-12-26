package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.MineGuardianEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class MineGuardianAnchorEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> GUARDIAN_UUID = SynchedEntityData.defineId(MineGuardianAnchorEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> GUARDIAN_ID = SynchedEntityData.defineId(MineGuardianAnchorEntity.class, EntityDataSerializers.INT);

    public MineGuardianAnchorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public MineGuardianAnchorEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.MINE_GUARDIAN_ANCHOR.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    public MineGuardianAnchorEntity(MineGuardianEntity mineGuardianEntity) {
        this(ACEntityRegistry.MINE_GUARDIAN_ANCHOR.get(), mineGuardianEntity.level());
        this.setGuardianUUID(mineGuardianEntity.getUUID());
        this.setYRot(random.nextFloat() * 360);
        this.setPos(mineGuardianEntity.position().add(0, 0.5F, 0));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(GUARDIAN_UUID, Optional.empty());
        this.entityData.define(GUARDIAN_ID, -1);
    }


    @Override
    public void tick() {
        super.tick();
        Entity guardian = getGuardian();
        if (!this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement().scale(0.9F));
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.9F, 0.9F, 0.9F));
        if (!level().isClientSide) {
            if (guardian == null && this.tickCount > 20) {
                this.remove(RemovalReason.DISCARDED);
            }
            if (guardian instanceof MineGuardianEntity mineGuardian) {
                Entity attackTarget = mineGuardian.getTarget();
                boolean hasTarget = attackTarget != null && attackTarget.isAlive();
                this.entityData.set(GUARDIAN_ID, mineGuardian.getId());
                mineGuardian.setAnchorUUID(this.getUUID());
                double distance = this.distanceTo(mineGuardian);
                int i = mineGuardian.getMaxChainLength();
                double distanceGoal = (mineGuardian.isInWaterOrBubble() ? i + Math.sin(tickCount * 0.1F + i * 0.5F) * 0.25F : 5) + (hasTarget ? 5 : 0);
                double waterHeight = mineGuardian.getFluidTypeHeight(ForgeMod.WATER_TYPE.get());
                double waterUp = Math.min(waterHeight, 1F) * 0.005F;
                if (mineGuardian.isInWaterOrBubble() && !hasTarget) {
                    double f = this.getX() + (float) -Math.sin(tickCount * 0.025F + i) * 0.5F;
                    double f1 = this.getZ() + (float) Math.cos(tickCount * 0.025F + i) * 0.5F;
                    double f2 = this.getY() + distanceGoal;
                    Vec3 vec3 = new Vec3(f, f2, f1).subtract(guardian.position());
                    mineGuardian.setDeltaMovement(mineGuardian.getDeltaMovement().add(vec3.scale(waterUp)));
                }
                if (distance > distanceGoal) {
                    double disRem = Math.min(distance - distanceGoal, 1F) * 0.1F;
                    Vec3 moveTo = getChainFrom(1.0F).subtract(mineGuardian.position());
                    if (moveTo.length() > 1.0D) {
                        moveTo = moveTo.normalize();
                    }
                    mineGuardian.setDeltaMovement(mineGuardian.getDeltaMovement().multiply(hasTarget ? 1F : 0.8F, hasTarget ? 1F : 0.8F, hasTarget ? 1F : 0.8F).add(moveTo.scale(disRem)));
                }

            }
        }
    }

    public UUID getGuardianUUID() {
        return this.entityData.get(GUARDIAN_UUID).orElse(null);
    }

    public void setGuardianUUID(@Nullable UUID uniqueId) {
        this.entityData.set(GUARDIAN_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getGuardian() {
        if (!level().isClientSide) {
            final UUID id = getGuardianUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(GUARDIAN_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public void linkWithGuardian(Entity head) {
        this.setGuardianUUID(head.getUUID());
        this.entityData.set(GUARDIAN_ID, head.getId());
    }


    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("GuardianUUID")) {
            this.setGuardianUUID(compound.getUUID("GuardianUUID"));
        }
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        if (this.getGuardianUUID() != null) {
            compound.putUUID("GuardianUUID", this.getGuardianUUID());
        }
    }

    public Vec3 getChainTo(float partialTicks) {
        if (getGuardian() instanceof MineGuardianEntity mineGuardianEntity) {
            return mineGuardianEntity.getPosition(partialTicks);
        }
        return this.getPosition(partialTicks).add(0, 1.0, 0);
    }

    public Vec3 getChainFrom(float partialTicks) {
        return this.getPosition(partialTicks).add(0, 1.0, 0);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    public boolean canBeCollidedWith() {
        return !this.isRemoved();
    }

    public boolean isPushable() {
        return !this.isRemoved();
    }

    public boolean isAttackable() {
        return !this.isRemoved();
    }
}
