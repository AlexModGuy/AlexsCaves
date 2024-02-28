package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.TephraExplosion;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;

public class ExtinctionSpearEntity extends AbstractArrow {
    private boolean dealtDamage;
    private ItemStack spearItem = new ItemStack(ACItemRegistry.EXTINCTION_SPEAR.get());
    private static final EntityDataAccessor<Boolean> WIGGLING = SynchedEntityData.defineId(ExtinctionSpearEntity.class, EntityDataSerializers.BOOLEAN);
    private int ticksWiggling = 0;
    public ExtinctionSpearEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public ExtinctionSpearEntity(Level level, LivingEntity shooter, ItemStack itemStack) {
        super(ACEntityRegistry.EXTINCTION_SPEAR.get(), shooter, level);
        this.spearItem = itemStack.copy();
    }

    public ExtinctionSpearEntity(Level level, double x, double y, double z) {
        super(ACEntityRegistry.EXTINCTION_SPEAR.get(), x, y, z, level);
    }

    public ExtinctionSpearEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.EXTINCTION_SPEAR.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WIGGLING, false);
    }

    protected ItemStack getPickupItem() {
        return spearItem;
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 vec3, Vec3 vec31) {
        return this.dealtDamage ? null : super.findHitEntity(vec3, vec31);
    }

    public void tick(){
        super.tick();
        Entity entity = this.getOwner();
        if ((this.inGround || this.isNoPhysics()) && entity != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            } else if(this.isWiggling()){
                if(ticksWiggling++ > 20){
                    this.setWiggling(false);
                    this.explode();
                }
            } else {
                Vec3 vec3 = entity.getEyePosition().subtract(this.position());
                if(!this.isNoPhysics()){
                    this.setDeltaMovement(Vec3.ZERO);
                }
                this.setNoPhysics(true);
                if (this.level().isClientSide) {
                    this.yOld = this.getY();
                }
                double d0 = 0.3D;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(d0)));
            }
        }
        if (this.level().isClientSide && !this.inGround) {
            Vec3 center = this.position().add(0, 0.25F, 0);
            Vec3 vec3 = this.getDeltaMovement().scale(-0.2F);
            this.level().addAlwaysVisibleParticle(ACParticleRegistry.TEPHRA_FLAME.get(), true, center.x, center.y, center.z, vec3.x, vec3.y, vec3.z);
        }
    }

    protected boolean tryPickup(Player player) {
        Entity entity = this.getOwner();
        if(entity == null || entity.equals(player)){
            return super.tryPickup(player);
        }else{
            return false;
        }
    }

    private void explode() {
        TephraExplosion explosion = new TephraExplosion(level(), this.getOwner(), this.getX(), this.getY(0.5), this.getZ(), 1.5F, Explosion.BlockInteraction.KEEP);
        explosion.explode();
        explosion.finalizeExplosion(true);
    }

    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.setWiggling(true);
    }

    private boolean isAcceptibleReturnOwner() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayer) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        float f = 10.0F;
        if (entity instanceof LivingEntity livingentity) {
            f += EnchantmentHelper.getDamageBonus(this.getPickupItem(), livingentity.getMobType());
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, (Entity) (entity1 == null ? this : entity1));
        this.dealtDamage = true;
        SoundEvent soundevent = ACSoundRegistry.EXTINCTION_SPEAR_HIT.get();
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            entity.setSecondsOnFire(5);
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity) entity;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity1);
                }

                this.doPostHurtEffects(livingentity1);
            }
            if(entity1 != null){
                DinosaurSpiritEntity dinosaurSpirit = ACEntityRegistry.DINOSAUR_SPIRIT.get().create(level());
                dinosaurSpirit.setPos(entity.getX(), entity.getY() + entity.getBbHeight(), entity.getZ());
                dinosaurSpirit.setDinosaurType(DinosaurSpiritEntity.DinosaurType.SUBTERRANODON);
                dinosaurSpirit.setPlayerUUID(entity1.getUUID());
                dinosaurSpirit.setAttackingEntityId(entity.getId());
                dinosaurSpirit.lookAt(EntityAnchorArgument.Anchor.EYES, entity1.getEyePosition());
                dinosaurSpirit.setEnchantmentLevel(spearItem.getEnchantmentLevel(ACEnchantmentRegistry.PLUMMETING_FLIGHT.get()));
                this.playSound(ACSoundRegistry.EXTINCTION_SPEAR_SUMMON.get(), 1.0F, 1.0F);
                level().addFreshEntity(dinosaurSpirit);
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        this.playSound(soundevent, f1, 1.0F);
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return ACSoundRegistry.LIMESTONE_SPEAR_HIT.get();
    }

    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    public boolean isWiggling() {
        return this.entityData.get(WIGGLING);
    }

    public void setWiggling(boolean wiggling) {
        this.entityData.set(WIGGLING, wiggling);
    }

}

