package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class InkBombEntity extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Boolean> GLOWING_BOMB = SynchedEntityData.defineId(InkBombEntity.class, EntityDataSerializers.BOOLEAN);

    public InkBombEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public InkBombEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.INK_BOMB.get(), level);
    }

    public InkBombEntity(Level level, LivingEntity thrower) {
        super(ACEntityRegistry.INK_BOMB.get(), thrower, level);
    }

    public InkBombEntity(Level level, double x, double y, double z) {
        super(ACEntityRegistry.INK_BOMB.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GLOWING_BOMB, false);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setGlowingBomb(tag.getBoolean("GlowingBomb"));

    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("GlowingBomb", this.isGlowingBomb());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void handleEntityEvent(byte message) {
        if (message == 3) {
            double d0 = 0.08D;
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        hitResult.getEntity().hurt(damageSources().thrown(this, this.getOwner()), 0F);
        if (hitResult.getEntity() instanceof SubmarineEntity submarine) {
            submarine.setLightsOn(false);
            if (submarine.getFirstPassenger() instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
                if (isGlowingBomb()) {
                    player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300));
                }
                if (!player.isCreative()) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                    player.removeEffect(MobEffects.CONDUIT_POWER);
                }
            }
        }
        if (hitResult.getEntity() instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
            if (!(living instanceof Player player && player.isCreative())) {
                living.removeEffect(MobEffects.NIGHT_VISION);
                living.removeEffect(MobEffects.CONDUIT_POWER);
            }
        }
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY() + 0.2F, this.getZ());
            areaeffectcloud.setParticle(isGlowingBomb() ? ParticleTypes.GLOW_SQUID_INK : ParticleTypes.SQUID_INK);
            areaeffectcloud.setFixedColor(0);
            areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
            if (isGlowingBomb()) {
                areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300));
            }
            areaeffectcloud.setRadius(2F);
            areaeffectcloud.setDuration(60);
            areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());
            this.level().addFreshEntity(areaeffectcloud);
        }

    }

    public boolean isGlowingBomb() {
        return this.entityData.get(GLOWING_BOMB);
    }

    public void setGlowingBomb(boolean bool) {
        this.entityData.set(GLOWING_BOMB, bool);
    }

    protected Item getDefaultItem() {
        return isGlowingBomb() ? ACItemRegistry.GLOW_INK_BOMB.get() : ACItemRegistry.INK_BOMB.get();
    }
}