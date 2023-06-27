package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class SeekingArrowEntity extends AbstractArrow {
    private int duration = 200;

    private static final EntityDataAccessor<Integer> ARC_TOWARDS_ENTITY_ID = SynchedEntityData.defineId(SeekingArrowEntity.class, EntityDataSerializers.INT);
    public SeekingArrowEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public SeekingArrowEntity(Level level, LivingEntity shooter) {
        super(ACEntityRegistry.SEEKING_ARROW.get(), shooter, level);
    }

    public SeekingArrowEntity(Level level, double x, double y, double z) {
        super(ACEntityRegistry.SEEKING_ARROW.get(), x, y, z, level);
    }

    public SeekingArrowEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.SEEKING_ARROW.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARC_TOWARDS_ENTITY_ID, -1);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        int id = this.getArcTowardsID();
        if(!inGround){
            if(id == -1){
                if(!level().isClientSide){
                    Entity closest = null;
                    float boxExpandBy = Math.min(10, 3 + (this.tickCount / 4));
                    for(Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(boxExpandBy), this::canHitEntity)){
                        if((closest == null || entity.distanceTo(this) < closest.distanceTo(this)) && !ownedBy(entity)){
                            closest = entity;
                        }
                    }
                    if(closest != null){
                        this.setArcTowardsID(closest.getId());
                    }
                }
            }else{
                Entity arcTowards = level().getEntity(id);
                if(arcTowards != null){
                    Vec3 arcVec = arcTowards.position().add(0, 0.65F * arcTowards.getBbHeight(), 0).subtract(this.position()).normalize();
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.3F).add(arcVec.scale(0.7F)));
                }
            }
        }
        if (this.level().isClientSide && !this.inGround) {
            Vec3 center = this.position().add(this.getDeltaMovement());
            Vec3 vec3 = center.add(new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F));
            this.level().addParticle(ACParticleRegistry.SCARLET_SHIELD_LIGHTNING.get(), center.x, center.y, center.z, vec3.x, vec3.y, vec3.z);
        }
    }

    protected ItemStack getPickupItem() {
        return new ItemStack(ACItemRegistry.SEEKING_ARROW.get());
    }

    private int getArcTowardsID(){
        return this.entityData.get(ARC_TOWARDS_ENTITY_ID);
    }

    private void setArcTowardsID(int id){
        this.entityData.set(ARC_TOWARDS_ENTITY_ID, id);
    }
}
