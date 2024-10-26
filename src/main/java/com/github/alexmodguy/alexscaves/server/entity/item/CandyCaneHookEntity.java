package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.github.alexmodguy.alexscaves.server.item.CandyCaneHookItem;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class CandyCaneHookEntity extends ThrowableProjectile {

    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(CandyCaneHookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> REELING = SynchedEntityData.defineId(CandyCaneHookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> OFFHAND = SynchedEntityData.defineId(CandyCaneHookEntity.class, EntityDataSerializers.BOOLEAN);
    private static EntityDataAccessor<Optional<Vec3>> HOOKED_POSITION = SynchedEntityData.defineId(CandyCaneHookEntity.class, ACEntityDataRegistry.OPTIONAL_VEC_3.get());
    private static final EntityDataAccessor<Integer> HOOKED_ENTITY_ID = SynchedEntityData.defineId(CandyCaneHookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> HOOKED_ENTITY_UUID = SynchedEntityData.defineId(CandyCaneHookEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(CandyCaneHookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> RESISTS_GRAVITY = SynchedEntityData.defineId(CandyCaneHookEntity.class, EntityDataSerializers.BOOLEAN);

    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private boolean wasReeling = false;

    public CandyCaneHookEntity(EntityType type, Level level) {
        super(type, level);
    }

    public CandyCaneHookEntity(Player player, Level level, ItemStack itemstack, boolean offhand) {
        this(ACEntityRegistry.CANDY_CANE_HOOK.get(), level);
        this.setOwner(player);
        float f = player.getXRot();
        int i;
        if (offhand) {
            i = player.getMainArm() == HumanoidArm.RIGHT ? -1 : 1;
        } else {
            i = player.getMainArm() == HumanoidArm.LEFT ? -1 : 1;
        }
        float f1 = player.getYRot();
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        Vec3 armOffset = new Vec3(i * -0.45F, 0, 0.25F).yRot((float) -Math.toRadians(f1));
        double d0 = player.getX() - (double) f3 * 0.7D;
        double d1 = player.getEyeY();
        double d2 = player.getZ() - (double) f2 * 0.7D;
        this.moveTo(d0 + armOffset.x, d1, d2 + armOffset.z, f1, f);
        Vec3 vec3 = new Vec3(-f3, Mth.clamp(-(f5 / f4), -5.0F, 5.0F), -f2);
        double d3 = vec3.length();
        double launchDist = 0.5D + itemstack.getEnchantmentLevel(ACEnchantmentRegistry.FAR_FLUNG.get()) * 0.2D;
        vec3 = vec3.multiply(launchDist / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, launchDist / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, launchDist / d3 + 0.5D + this.random.nextGaussian() * 0.0045D);
        this.setDeltaMovement(vec3);
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, vec3.horizontalDistance()) * (double) (180F / (float) Math.PI)));
        this.setOffhand(offhand);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.setDamage(itemstack.getEnchantmentLevel(ACEnchantmentRegistry.SHARP_CANE.get()) * 3.0F);
        if(itemstack.getEnchantmentLevel(ACEnchantmentRegistry.STRAIGHT_HOOK.get()) > 0){
            this.entityData.set(RESISTS_GRAVITY, true);
        }
    }

    public CandyCaneHookEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(ACEntityRegistry.CANDY_CANE_HOOK.get(), world);
        this.setInvulnerable(true);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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

    protected void defineSynchedData() {
        this.getEntityData().define(OWNER_ID, -1);
        this.getEntityData().define(REELING, false);
        this.getEntityData().define(OFFHAND, false);
        this.getEntityData().define(HOOKED_POSITION, Optional.empty());
        this.getEntityData().define(HOOKED_ENTITY_ID, -1);
        this.getEntityData().define(HOOKED_ENTITY_UUID, Optional.empty());
        this.getEntityData().define(DAMAGE, 0.0F);
        this.getEntityData().define(RESISTS_GRAVITY, false);
    }

    public void tick() {
        super.tick();
        Entity owner = this.getOwner();
        Entity hooked = this.getHookedEntity();
        Player playerOwner = this.getPlayerOwner();
        if (!level().isClientSide) {
            this.entityData.set(OWNER_ID, owner == null ? -1 : owner.getId());
            this.entityData.set(HOOKED_ENTITY_ID, hooked == null ? -1 : hooked.getId());
            boolean reelingFromHook = isOwnerHoldingHook(false);
            if (!isReeling() && reelingFromHook || playerOwner != null && playerOwner.isShiftKeyDown()) {
                this.setReeling(true);
            }
            if(isReeling() && !reelingFromHook){
                this.setReeling(false);
            }
            if (this.isReeling() && tickCount > 5) {
                this.noPhysics = true;
                if (owner != null) {
                    Vec3 moveTo = owner.getEyePosition().subtract(this.position());
                    if (moveTo.length() > 1F) {
                        moveTo = moveTo.normalize();
                    } else if (moveTo.length() < 0.85F) {
                        this.discard();
                    }
                    this.setDeltaMovement(moveTo);

                } else {
                    this.discard();
                }
            } else if (hooked != null) {
                if(!hooked.isAlive()){
                    this.setReeling(true);
                }else if (hooked instanceof GumWormEntity gumWorm) {
                    int i = 0;
                    if (playerOwner != null) {
                        if (this.getHandLaunchedFrom() == InteractionHand.MAIN_HAND) {
                            i = playerOwner.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
                        } else if (this.getHandLaunchedFrom() == InteractionHand.OFF_HAND) {
                            i = playerOwner.getMainArm() == HumanoidArm.LEFT ? 1 : -1;
                        }
                    }
                     this.setHookedPosition(gumWorm.getHookPosition(i));
                } else {
                    this.setHookedPosition(hooked.position().add(0, hooked.getBbHeight() * 0.5F, 0));
                }
            } else {
                AABB scanForHookTo = this.getBoundingBox().inflate(1.0F);
                Entity favoredToHookTo = null;
                for (Entity entity : level().getEntities(this, scanForHookTo, Entity::isAlive)) {
                    Entity entity1 = entity instanceof GumWormSegmentEntity gumWormSegment ? gumWormSegment.getHeadEntity() : entity;
                    if (entity1 != null && !(entity1 instanceof CandyCaneHookEntity)  && !(entity1 instanceof Projectile) && (owner == null || (!entity1.is(owner)) && (favoredToHookTo == null || entity1.distanceTo(this) < favoredToHookTo.distanceTo(this) || entity1 instanceof GumWormEntity && !(favoredToHookTo instanceof GumWormEntity)))) {
                        favoredToHookTo = entity1;
                    }
                }
                if (favoredToHookTo != null) {
                    this.setHookedEntityUUID(favoredToHookTo.getUUID());
                    if(this.getDamage() != 0){
                        DamageSource damageSource = this.damageSources().thrown(this, owner);
                        favoredToHookTo.hurt(damageSource, this.getDamage());
                    }
                }
            }
            Vec3 hookedPosition = this.getHookedPosition();
            if (!this.isReeling() && hookedPosition != null) {
                this.setPos(hookedPosition);
            }
            if (hookedPosition == null && this.onGround()) {
                this.setHookedPosition(this.position());
            }
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
            this.reapplyPosition();
            this.setRot(this.getYRot(), this.getXRot());
        }
        if (hooked instanceof GumWormEntity gumWorm && playerOwner != null && !isReeling() && !playerOwner.isShiftKeyDown()) {
            if (gumWorm.getRidingSegment() instanceof GumWormSegmentEntity gumWormSegment && !playerOwner.isPassengerOfSameVehicle(gumWorm)) {
                Vec3 ridePosition = gumWormSegment.getRiderPosition(playerOwner);
                Vec3 moveVec = ridePosition.subtract(playerOwner.position());
                if(!playerOwner.isPassenger()){
                    gumWorm.onMounted();
                }
                if(!level().isClientSide && moveVec.length() < 4.0F){
                    playerOwner.startRiding(gumWormSegment);
                }else {
                    if (moveVec.length() >= 1.0F){
                        moveVec = moveVec.normalize();
                    }
                    playerOwner.setDeltaMovement(playerOwner.getDeltaMovement().scale(0.8F).add(moveVec));
                }
            }
            gumWorm.setHookId(this.isOffhand(), this.getId());
            Vec3 vec3 = gumWorm.position().add(0, 0.5F * gumWorm.getBbWidth(), 0).subtract(this.position());
            double d0 = vec3.horizontalDistance();
            float reelMultiX = isReeling() ? -1 : 1;
            float reelAddY = isReeling() ? 180 : 0;
            this.setXRot(lerpRotation(this.xRotO, reelMultiX * (float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI))));
            this.setYRot(lerpRotation(this.yRotO, reelAddY + (float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI))));
        }
        if (getHookedPosition() == null || this.isReeling()) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(onGround() ? 0.5F : 0.9F));
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        if (!wasReeling && isReeling()) {
            wasReeling = this.isReeling();
            if (isOwnerHoldingHook(true)) {
                fling();
            }
        }
    }

    public void fling() {
        Vec3 flingFrom = this.getHookedPosition();
        Entity hooked = this.getHookedEntity();
        Entity owner = this.getOwner();
        if (owner != null && flingFrom != null) {
            if (hooked == null) {
                Vec3 newDelta = flingFrom.subtract(owner.position()).scale(0.2F);
                if (newDelta.length() > 1.0F) {
                    newDelta = newDelta.scale(1.0F);
                }
                owner.setDeltaMovement(newDelta.add(0, 0.2, 0).add(owner.getDeltaMovement()));
            } else if (!(hooked instanceof GumWormEntity)) {
                Vec3 newDelta = owner.position().subtract(hooked.position()).scale(0.2F);
                if (newDelta.length() > 1.0F) {
                    newDelta = newDelta.scale(1.0F);
                }
                hooked.setDeltaMovement(newDelta.add(0, 0.2, 0).add(hooked.getDeltaMovement()));
            }
        }
    }

    @Override
    protected void updateRotation() {
        Vec3 vec3 = this.getDeltaMovement();
        if (vec3.length() > 0.1F) {
            double d0 = vec3.horizontalDistance();
            float reelMultiX = isReeling() ? -1 : 1;
            float reelAddY = isReeling() ? 180 : 0;
            this.setXRot(lerpRotation(this.xRotO, reelMultiX * (float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI))));
            this.setYRot(lerpRotation(this.yRotO, reelAddY + (float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI))));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        this.setDeltaMovement(Vec3.ZERO);
        this.setHookedPosition(entityHitResult.getLocation());
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof GumWormSegmentEntity gumWormSegment) {
            entity = gumWormSegment.getHeadEntity();
        }
        this.setHookedEntityUUID(entity.getUUID());
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        BlockState blockstate = this.level().getBlockState(blockHitResult.getBlockPos());
        blockstate.onProjectileHit(this.level(), blockstate, blockHitResult, this);
        if (!level().isClientSide) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setHookedPosition(blockHitResult.getLocation());
        }
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        postReel();
        super.remove(removalReason);
    }

    @Override
    protected float getGravity() {
        return isReeling() || this.entityData.get(RESISTS_GRAVITY) && this.getDeltaMovement().horizontalDistance() > 0.05F ? 0 : 0.08F;
    }

    public InteractionHand getHandLaunchedFrom() {
        boolean offhand = this.isOffhand();
        return offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    public boolean isOwnerHoldingHook(boolean ignoreReeling) {
        if (getOwner() instanceof Player player) {
            ItemStack itemStack = player.getItemInHand(getHandLaunchedFrom());
            if (itemStack.getItem() instanceof CandyCaneHookItem) {
                UUID hookUUID = CandyCaneHookItem.getLaunchedHookUUID(itemStack);
                return hookUUID != null && hookUUID.equals(this.getUUID()) && (ignoreReeling || CandyCaneHookItem.isReelingIn(itemStack));
            }
        }
        return true;
    }

    public void postReel() {
        if (getOwner() instanceof Player player) {
            ItemStack itemStack = player.getItemInHand(getHandLaunchedFrom());
            if (itemStack.getItem() instanceof CandyCaneHookItem) {
                CandyCaneHookItem.setLastLaunchedHookUUID(itemStack, null);
            }
        }
    }

    private boolean isOffhand() {
        return this.entityData.get(OFFHAND);
    }

    public void setOffhand(boolean offhand) {
        this.entityData.set(OFFHAND, offhand);
    }

    public boolean isReeling() {
        return this.entityData.get(REELING);
    }

    public void setReeling(boolean reeling) {
        this.entityData.set(REELING, reeling);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    @Override
    public Entity getOwner() {
        Entity prev = super.getOwner();
        if (this.entityData.get(OWNER_ID) != -1) {
            return level().getEntity(this.entityData.get(OWNER_ID));
        } else {
            return prev;
        }
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        this.entityData.set(OWNER_ID, owner == null ? -1 : owner.getId());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setReeling(tag.getBoolean("Reeling"));
        if(tag.contains("HookedEntityUUID")){
            this.setHookedEntityUUID(tag.getUUID("HookedEntityUUID"));
        }
        this.setOffhand(tag.getBoolean("Offhand"));
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Reeling", this.isReeling());
        if(this.getHookedEntityUUID() != null){
            tag.putUUID("HookedEntityUUID", this.getHookedEntityUUID());
        }
        tag.putBoolean("Offhand", this.isOffhand());
    }

    public Entity getHookedEntity() {
        if (!level().isClientSide) {
            final UUID id = getHookedEntityUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(HOOKED_ENTITY_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public void setHookedEntityId(int id) {
        this.entityData.set(HOOKED_ENTITY_ID, id);
    }

    @org.jetbrains.annotations.Nullable
    public Vec3 getHookedPosition() {
        return this.entityData.get(HOOKED_POSITION).orElse(null);
    }

    public void setHookedPosition(@org.jetbrains.annotations.Nullable Vec3 vec3) {
        this.entityData.set(HOOKED_POSITION, Optional.ofNullable(vec3));
    }

    @Nullable
    public UUID getHookedEntityUUID() {
        return this.entityData.get(HOOKED_ENTITY_UUID).orElse(null);
    }

    public void setHookedEntityUUID(@Nullable UUID uniqueId) {
        this.entityData.set(HOOKED_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }


    @Nullable
    public Player getPlayerOwner() {
        if (level().isClientSide && this.entityData.get(OWNER_ID) != -1) {
            Entity entity = level().getEntity(this.entityData.get(OWNER_ID));
            return entity instanceof Player ? (Player) entity : null;
        } else {
            Entity entity = this.getOwner();
            return entity instanceof Player ? (Player) entity : null;
        }
    }
}

