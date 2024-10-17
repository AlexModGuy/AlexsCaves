package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.CaramelCubeEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.LicowitchEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.UUID;

public class SugarStaffHexEntity extends Entity {

    private static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(SugarStaffHexEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> HEX_SCALE = SynchedEntityData.defineId(SugarStaffHexEntity.class, EntityDataSerializers.FLOAT);

    private int despawnsIn = -1;
    private int prevDespawnsIn;
    @javax.annotation.Nullable
    private LivingEntity owner;
    @javax.annotation.Nullable
    private UUID ownerUUID;

    private final float yRenderOffset = random.nextFloat() * 0.05F;

    public SugarStaffHexEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public SugarStaffHexEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.SUGAR_STAFF_HEX.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LIFESPAN, 100);
        this.entityData.define(HEX_SCALE, 1F);
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
        } else {
            this.discard();
        }
        if (level().isClientSide) {
            if(despawnsIn < 5){
                for(int i = 0; i < 8 + random.nextInt(8); i++){
                    this.level().addParticle(ACParticleRegistry.PURPLE_WITCH_EXPLOSION.get(), this.getRandomX(0.45F), this.getRandomY(), this.getRandomZ(0.45F), 0,0,0);
                }
            }else if(random.nextFloat() < 0.6F){
                Vec3 ambientParticlePos = new Vec3((random.nextFloat() * 4F - 2F) * getHexScale(), 0.1F, (random.nextFloat() * 4F - 2F)  * getHexScale());
                Vec3 vec3 = this.position().add(ambientParticlePos);
                Vec3 vec31 = this.position().add(ambientParticlePos.scale(1.5F).add(0, random.nextFloat(), 0));
                this.level().addParticle(ACParticleRegistry.PURPLE_WITCH_MAGIC.get(), vec3.x, vec3.y, vec3.z, vec31.x, vec31.y, vec31.z);
            }
        }
        hurtEntities(despawnsIn < 5);
        Vec3 vec3 = this.getDeltaMovement();
        this.noPhysics = true;
        this.move(MoverType.SELF, vec3);
        this.setDeltaMovement(vec3.multiply(0.7F, 0.8F, 0.7F));
    }

    public void setDespawnsIn(int i) {
        this.despawnsIn = i;
    }

    public float getDespawnTime(float partialTicks) {
        return prevDespawnsIn + (despawnsIn - prevDespawnsIn) * partialTicks;
    }

    public void setLifespan(int lifespan) {
        this.entityData.set(LIFESPAN, lifespan);
    }

    public int getLifespan() {
        return this.entityData.get(LIFESPAN);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("DespawnsIn")) {
            this.despawnsIn = compoundTag.getInt("DespawnsIn");
        }
        this.setLifespan(compoundTag.getInt("Lifespan"));
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
    }


    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("DespawnsIn", this.despawnsIn);
        compoundTag.putInt("Lifespan", this.getLifespan());
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
    }

    private void hurtEntities(boolean finalExplosion) {
        AABB bashBox = this.getBoundingBox();
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
            if (!isAlliedTo(entity) && entity.distanceTo(this) <= 4.0F && (owner != null && !entity.is(owner) && (!(owner instanceof LicowitchEntity) && !entity.isAlliedTo(owner) || owner instanceof LicowitchEntity licowitch && !licowitch.isFriendlyFire(entity)))) {
                entity.hurt(this.damageSources().indirectMagic(this, owner), finalExplosion ? 6.0F : 1.0F);
                if(finalExplosion){
                    entity.knockback(0.9F, this.getX() - entity.getX(), this.getZ() - entity.getZ());
                }
            }
        }
    }


    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (HEX_SCALE.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(entityDataAccessor);
    }

    public float getHexScale() {
        return this.entityData.get(HEX_SCALE);
    }

    public void setHexScale(float f) {
        this.entityData.set(HEX_SCALE, f);
    }


    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.getHexScale() * 4.0F, 0.25F);
    }
    public float getYRenderOffset() {
        return yRenderOffset;
    }
}
