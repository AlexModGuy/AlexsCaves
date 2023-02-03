package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class NuclearBombEntity extends Entity {

    private static final EntityDataAccessor<Integer> TIME = SynchedEntityData.defineId(NuclearBombEntity.class, EntityDataSerializers.INT);
    public static final int MAX_TIME = 300;
    public NuclearBombEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public NuclearBombEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.NUCLEAR_BOMB.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.7, 0.7D));
        }
        int i = this.getTime() + 1;
        if (i > MAX_TIME) {
            this.discard();
            if (!this.level.isClientSide) {
                this.explode();
            }
        } else {
            this.setTime(i);
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level.isClientSide && MAX_TIME - i > 10 && random.nextFloat() < 0.3F) {
                Vec3 center = this.getEyePosition();
                level.addParticle(ACParticleRegistry.PROTON.get(), center.x, center.y, center.z, center.x, center.y, center.z);
            }
        }
    }

    private void explode() {
        NuclearExplosionEntity explosion = ACEntityRegistry.NUCLEAR_EXPLOSION.get().create(level);
        explosion.copyPosition(this);
        explosion.setSize(3F);
        level.addFreshEntity(explosion);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TIME, 0);
    }

    public int getTime() {
        return this.entityData.get(TIME);
    }

    public void setTime(int time) {
        this.entityData.set(TIME, time);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}
