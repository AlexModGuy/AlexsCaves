package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntity;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.Optional;
import java.util.UUID;

public class WaterBoltEntity extends Projectile {

    private static final EntityDataAccessor<Optional<UUID>> ARC_TOWARDS_ENTITY_UUID = SynchedEntityData.defineId(WaterBoltEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> BUBBLING = SynchedEntityData.defineId(WaterBoltEntity.class, EntityDataSerializers.BOOLEAN);
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private Vec3[] trailPositions = new Vec3[64];
    private int trailPointer = -1;

    private boolean spawnedSplash = false;

    private int dieIn = -1;

    public WaterBoltEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public WaterBoltEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.WATER_BOLT.get(), level);
    }

    public WaterBoltEntity(Level level, LivingEntity shooter) {
        this(ACEntityRegistry.WATER_BOLT.get(), level);
        float f = shooter instanceof Player ? 0.3F : 0.1F;
        this.setPos(shooter.getX(), shooter.getEyeY() - f, shooter.getZ());
        this.setOwner(shooter);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(BUBBLING, false);
        this.getEntityData().define(ARC_TOWARDS_ENTITY_UUID, Optional.empty());
    }

    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            Entity arcTowards = getArcingTowards();
            if (arcTowards != null && tickCount > 3 && dieIn == -1 && this.distanceTo(arcTowards) > 1.5F && tickCount < 20) {
                Vec3 arcVec = arcTowards.position().add(0, 0.3 * arcTowards.getBbHeight(), 0).subtract(this.position()).normalize();
                this.setDeltaMovement(this.getDeltaMovement().add(arcVec.scale(0.3F)));
            }
        } else {
            for (int j = 0; j < 3 + random.nextInt(2); ++j) {
                this.level().addParticle(this.isInWaterOrBubble() || isBubbling() ? ParticleTypes.BUBBLE_COLUMN_UP : ParticleTypes.FALLING_WATER, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, -0.1F, 0);
            }
        }
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        this.updateRotation();
        float f = 0.99F;
        float f1 = 0.06F;
        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir) && !this.isInWaterOrBubble()) {
            this.discard();
        } else {
            this.setDeltaMovement(vec3.scale((double) 0.9F));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double) -0.07F, 0.0D));
            }
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
                this.setPos(d0, d1, d2);
            }
        }
        Vec3 trailAt = this.position().add(0, this.getBbHeight() / 2F, 0);
        if (trailPointer == -1) {
            Vec3 backAt = trailAt;
            for (int i = 0; i < trailPositions.length; i++) {
                trailPositions[i] = backAt;
            }
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailAt;

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }
        if (dieIn > 0) {
            dieIn--;
            if (dieIn == 0) {
                this.discard();
            }
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        super.remove(removalReason);
        if (!spawnedSplash && level() instanceof ServerLevel serverLevel) {
            spawnedSplash = true;
            BlockPos pos = this.blockPosition().above();
            while (level().isEmptyBlock(pos) && pos.getY() > level().getMinBuildHeight()) {
                pos = pos.below();
            }
            serverLevel.sendParticles(ACParticleRegistry.BIG_SPLASH.get(), this.getX(), pos.getY() + 1.5F, this.getZ(), 0, 1.3F, 1, 0, 1.0D);
        }
    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
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


    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!this.level().isClientSide && !ownedBy(hitResult.getEntity())) {
            damageMobs();
            if (dieIn == -1) {
                dieIn = 5;
            }
        }
    }

    private void damageMobs() {
        Entity owner = this.getOwner();
        DamageSource source = damageSources().mobProjectile(this, (owner instanceof LivingEntity living1 ? living1 : null));
        AABB bashBox = this.getBoundingBox().inflate(2.0D, 2, 2.0D);
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
            if (!isAlliedTo(entity) && !(entity instanceof DeepOneBaseEntity)) {
                if (entity.hurt(source, 3.0F) && this.isBubbling()) {
                    entity.addEffect(new MobEffectInstance(ACEffectRegistry.BUBBLED.get(), 200));
                    if (!entity.level().isClientSide) {
                        AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntity(entity.getId(), this.getId(), 1, 200));
                    }
                }
            }
        }
    }

    public boolean isBubbling() {
        return this.entityData.get(BUBBLING);
    }

    public void setBubbling(boolean bool) {
        this.entityData.set(BUBBLING, bool);
    }

    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide) {
            damageMobs();
            if (dieIn == -1) {
                dieIn = 5;
            }
        }
    }

    public Entity getArcingTowards() {
        UUID id = this.entityData.get(ARC_TOWARDS_ENTITY_UUID).orElse(null);
        return id == null ? null : ((ServerLevel) level()).getEntity(id);
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setBubbling(tag.getBoolean("Bubbling"));

    }

    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("Bubbling", this.isBubbling());
    }

    public void setArcingTowards(@javax.annotation.Nullable UUID arcingTowards) {
        this.entityData.set(ARC_TOWARDS_ENTITY_UUID, Optional.ofNullable(arcingTowards));
    }

    public boolean hasTrail() {
        return trailPointer != -1;
    }
}
