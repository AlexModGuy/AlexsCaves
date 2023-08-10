package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.stream.Stream;

public class FloaterEntity extends Entity {

    public int timeOutOfWater = 0;

    public FloaterEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public FloaterEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.FLOATER.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        if (this.wasEyeInWater) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.2D, 0.0D));
            if (this.level().isClientSide) {
                Vec3 center = this.position();
                this.level().addParticle(ParticleTypes.CURRENT_DOWN, center.x, center.y, center.z, 0, 0, 0);
            }
        } else if (!level().isClientSide() && timeOutOfWater++ > 5) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));

    }

    public void handleEntityEvent(byte message) {
        if (message == 3) {
            for (int i = 0; i < 10 + random.nextInt(4); ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ACItemRegistry.FLOATER.get())), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), ((double) this.random.nextFloat() - 0.5D) * 0.3D, ((double) this.random.nextFloat() - 0.5D) * 0.3D, ((double) this.random.nextFloat() - 0.5D) * 0.3D);
            }
        } else {
            super.handleEntityEvent(message);
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public boolean shouldBeSaved() {
        return !this.isRemoved();
    }

    public boolean isAttackable() {
        return false;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ACItemRegistry.FLOATER.get());
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            double d0 = this.getY() + 0.7F + passenger.getMyRidingOffset();
            moveFunction.accept(passenger, this.getX(), d0, this.getZ());
            passenger.fallDistance = 0.0F;
        } else {
            super.positionRider(passenger, moveFunction);
        }
    }

    public boolean causeFallDamage(float f, float f1, DamageSource damageSource) {
        return false;
    }
}
