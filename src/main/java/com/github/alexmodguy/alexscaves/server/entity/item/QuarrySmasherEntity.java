package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.QuarryBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.QuarryBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuarrySmasherEntity extends Entity {

    public final QuarrySmasherHeadEntity headPart;
    public final QuarrySmasherHeadEntity[] allParts;
    public AABB lastMiningArea = null;
    private static final EntityDataAccessor<Boolean> INACTIVE = SynchedEntityData.defineId(QuarrySmasherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> QUARRY_POS = SynchedEntityData.defineId(QuarrySmasherEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Optional<BlockPos>> TARGET_POS = SynchedEntityData.defineId(QuarrySmasherEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> SLAMMING = SynchedEntityData.defineId(QuarrySmasherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PULLING_ITEMS_FOR = SynchedEntityData.defineId(QuarrySmasherEntity.class, EntityDataSerializers.INT);

    private float inactiveProgress;
    private float prevInactiveProgress;
    private float headGroundProgress;
    private float prevHeadGroundProgress;

    private int damageSustained;
    private int quarryActivityTime = 0;

    private int blockBreakCooldown = 0;

    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    public int shakeTime = 0;

    public List<ItemEntity> pulledItems = new ArrayList<>();
    private boolean triggerAdvancement;

    public QuarrySmasherEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        headPart = new QuarrySmasherHeadEntity(this);
        allParts = new QuarrySmasherHeadEntity[]{headPart};
        headPart.copyPosition(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(QUARRY_POS, Optional.empty());
        this.entityData.define(TARGET_POS, Optional.empty());
        this.entityData.define(INACTIVE, true);
        this.entityData.define(SLAMMING, false);
        this.entityData.define(PULLING_ITEMS_FOR, 0);
    }

    public QuarrySmasherEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(ACEntityRegistry.QUARRY_SMASHER.get(), world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        tickMultipart();
        super.tick();
        prevInactiveProgress = inactiveProgress;
        prevHeadGroundProgress = headGroundProgress;
        if (this.isInactive() && inactiveProgress < 10.0F) {
            inactiveProgress++;
        }
        if (!this.isInactive() && inactiveProgress > 0.0F) {
            inactiveProgress--;
        }
        if (this.isSlamming() && headGroundProgress < 5.0F) {
            headGroundProgress++;
        }
        if (!this.isSlamming() && headGroundProgress > 0.0F) {
            headGroundProgress--;
        }
        if (damageSustained > 0 && tickCount % 500 == 0) {
            damageSustained--;
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
        } else if (triggerAdvancement && tickCount % 20 == 0) {
            boolean flag = false;
            double advancementRange = 20.0D;
            for (Player player : level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(advancementRange))) {
                if (player.distanceTo(this) < advancementRange) {
                    flag = true;
                    ACAdvancementTriggerRegistry.FINISHED_QUARRY.triggerForEntity(player);
                }
            }
            triggerAdvancement = !flag;
        }
        if (this.isInactive()) {
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.2D, 0.0D));
            }
        } else {
            BlockPos quarryPos = getQuarryPos();
            if (quarryActivityTime-- < 0) {
                quarryActivityTime = 20;
                if (quarryPos == null || !level().getBlockState(quarryPos).is(ACBlockRegistry.QUARRY.get())) {
                    setQuarryPos(null);
                    setInactive(true);
                } else if (level().getBlockEntity(quarryPos) instanceof QuarryBlockEntity quarryBlockEntity) {
                    lastMiningArea = quarryBlockEntity.getMiningBox();
                }
            }
            BlockPos targetPos = getTargetPos();
            if (!level().isClientSide) {
                if (targetPos == null) {
                    this.setTargetPos(findTarget());
                } else {
                    Vec3 dist = Vec3.upFromBottomCenterOf(quarryPos == null ? targetPos : targetPos.atY(quarryPos.getY()), 4).subtract(this.position());
                    if (dist.horizontalDistance() < 0.5F) {
                        if (blockBreakCooldown <= 0) {
                            this.setSlamming(true);
                            if (this.headPart.position().distanceTo(Vec3.atBottomCenterOf(targetPos)) < 1.1) {
                                this.setSlamming(false);
                                level().destroyBlock(targetPos, true);
                                this.setTargetPos(null);
                                blockBreakCooldown = 35;
                                this.setPullingItemsFor(20);
                            }
                        }
                    } else {
                        this.setDeltaMovement(this.getDeltaMovement().add(dist.normalize().scale(0.1F)));
                    }
                }
            }
        }
        if (this.pullingItemsFor() > 0) {
            int i = this.pullingItemsFor() - 1;
            this.setPullingItemsFor(i);
            BlockPos quarry = getQuarryPos();
            boolean flag = false;
            for (ItemEntity itemEntity : level().getEntitiesOfClass(ItemEntity.class, this.headPart.getBoundingBox().inflate(2, 4, 2))) {
                itemEntity.hasImpulse = true;
                if (!pulledItems.contains(itemEntity)) {
                    pulledItems.add(itemEntity);
                }
                flag = true;
            }
            for (ItemEntity itemEntity : pulledItems) {
                if (i == 0) {
                    if (quarry != null && level().getBlockState(quarry).is(ACBlockRegistry.QUARRY.get())) {
                        itemEntity.setPos(quarry.getX() + 0.5F, quarry.getY() + 1F, quarry.getZ() + 0.5F);
                        Direction facing = level().getBlockState(quarry).getValue(QuarryBlock.FACING);
                        itemEntity.setDefaultPickUpDelay();
                        itemEntity.setDeltaMovement(new Vec3(facing.getStepX() * 0.1F, 0.4F, facing.getStepZ() * 0.1F));
                    }
                } else {
                    itemEntity.setPos(this.headPart.position().subtract(0, 0.5, 0));
                    itemEntity.setDeltaMovement(Vec3.ZERO);
                }
            }
            if (flag && quarry != null) {
                if (level().getBlockEntity(quarry) instanceof QuarryBlockEntity quarryBlockEntity) {
                    quarryBlockEntity.spinFor = 13;
                }
            }
            if (i == 0) {
                pulledItems.clear();
            }
        }
        if (blockBreakCooldown > 0) {
            blockBreakCooldown--;
        }
        if (shakeTime > 0) {
            shakeTime--;
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.7F));

    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lSteps > 0) {
            this.lSteps = 0;
            this.absMoveTo(this.lx, this.ly, this.lz, (float) this.lyr, (float) this.lxr);
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

    public BlockPos findTarget() {
        BlockPos quarry = getQuarryPos();
        if (quarry != null && level().getBlockEntity(quarry) instanceof QuarryBlockEntity quarryBlockEntity && quarryBlockEntity.hasMiningArea()) {
            BlockPos pos = quarryBlockEntity.findMinableBlock(level(), quarry.getY() + 3).orElse(null);
            if (pos != null) {
                return pos;
            } else {
                triggerAdvancement = true;
            }
        }
        return null;
    }

    public void tickMultipart() {
        Vec3 headTarget = getHeadTargetPos();
        float fallSpeed = 0.05F;
        if (isInactive()) {
            fallSpeed = 0.5F;
        } else if (isSlamming()) {
            fallSpeed = getHeadGroundProgress(1.0F) * 0.3F;
        } else if (this.pullingItemsFor() > 5) {
            fallSpeed = 0.0F;
        }
        Vec3 moveHeadBy = headTarget.subtract(this.headPart.position()).scale(fallSpeed);
        if (tickCount > 1) {
            this.headPart.setOldPosAndRot();
            this.headPart.setPos(moveHeadBy.add(headPart.position()));
        } else {
            this.headPart.setPos(this.position());
            this.headPart.setOldPosAndRot();
        }
    }

    private Vec3 getHeadTargetPos() {
        if (this.isInactive()) {
            return this.position().add(0.75F, 0, -0.75F);
        } else if (this.isSlamming()) {
            BlockPos target = getTargetPos();
            if (target != null) {
                return Vec3.upFromBottomCenterOf(target, 1);
            }
        }
        return this.position().add(0, -1F + Math.sin(tickCount * 0.1) * 0.5F, 0);
    }

    public float getChainLength(float partialTick) {
        return headPart == null ? 0 : (float) headPart.getPosition(partialTick).subtract(this.getPosition(partialTick)).length();
    }

    public boolean isInactive() {
        return this.entityData.get(INACTIVE);
    }

    public void setInactive(boolean inactive) {
        this.entityData.set(INACTIVE, inactive);
    }

    public boolean isSlamming() {
        return this.entityData.get(SLAMMING);
    }

    public void setSlamming(boolean slamming) {
        this.entityData.set(SLAMMING, slamming);
    }

    public int pullingItemsFor() {
        return this.entityData.get(PULLING_ITEMS_FOR);
    }

    public void setPullingItemsFor(int pullingItemsFor) {
        this.entityData.set(PULLING_ITEMS_FOR, pullingItemsFor);
    }

    public float getInactiveProgress(float partialTick) {
        return (prevInactiveProgress + (inactiveProgress - prevInactiveProgress) * partialTick) * 0.1F;
    }

    public boolean isBeingActivated() {
        return inactiveProgress <= prevInactiveProgress;
    }

    public float getHeadGroundProgress(float partialTick) {
        return (prevHeadGroundProgress + (headGroundProgress - prevHeadGroundProgress) * partialTick) * 0.2F;
    }

    public void setQuarryPos(@javax.annotation.Nullable BlockPos pos) {
        this.getEntityData().set(QUARRY_POS, Optional.ofNullable(pos));
    }

    @javax.annotation.Nullable
    public BlockPos getQuarryPos() {
        return this.getEntityData().get(QUARRY_POS).orElse((BlockPos) null);
    }

    public void setTargetPos(@javax.annotation.Nullable BlockPos pos) {
        this.getEntityData().set(TARGET_POS, Optional.ofNullable(pos));
    }

    @javax.annotation.Nullable
    public BlockPos getTargetPos() {
        return this.getEntityData().get(TARGET_POS).orElse((BlockPos) null);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    public void handleEntityEvent(byte b) {
        if (b == 48) {
            shakeTime = 10;
        } else {
            super.handleEntityEvent(b);
        }
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public boolean isAttackable() {
        return !this.isRemoved();
    }

    public boolean shouldBeSaved() {
        return !this.isRemoved();
    }

    @Nullable
    public ItemStack getPickResult() {
        return new ItemStack(ACItemRegistry.QUARRY_SMASHER.get());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return allParts;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageValue) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        } else {
            damageSustained += damageValue;
            this.level().broadcastEntityEvent(this, (byte) 48);
            if (damageSustained >= 10) {
                this.playSound(SoundEvents.ITEM_BREAK);
                if (!this.isRemoved()) {
                    for (int i = 0; i < 1 + random.nextInt(1); i++) {
                        this.spawnAtLocation(ACItemRegistry.AZURE_NEODYMIUM_INGOT.get());
                    }
                    for (int i = 0; i < 1 + random.nextInt(1); i++) {
                        this.spawnAtLocation(ACItemRegistry.SCARLET_NEODYMIUM_INGOT.get());
                    }
                }
                this.remove(RemovalReason.KILLED);
            }
            return true;
        }
    }
}
