package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.TeletorEntity;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MagneticWeaponEntity extends Entity {

    private static final EntityDataAccessor<ItemStack> ITEMSTACK = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Optional<UUID>> CONTROLLER_UUID = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> CONTROLLER_ID = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IDLING = SynchedEntityData.defineId(MagneticWeaponEntity.class, EntityDataSerializers.BOOLEAN);
    private float prevStrikeProgress;
    private float strikeProgress;

    private boolean comingBack = false;

    public MagneticWeaponEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public MagneticWeaponEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.MAGNETIC_WEAPON.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ITEMSTACK, new ItemStack(Items.IRON_SWORD));
        this.entityData.define(CONTROLLER_UUID, Optional.empty());
        this.entityData.define(CONTROLLER_ID, -1);
        this.entityData.define(TARGET_ID, -1);
        this.entityData.define(IDLING, true);
    }


    public void tick() {
        super.tick();
        prevStrikeProgress = strikeProgress;
        Entity controller = getController();
        Entity target = getTarget();
        if (!level.isClientSide) {
            if (this.comingBack || (target == null || !target.isAlive())) {
                this.noPhysics = true;
            } else {
                this.noPhysics = false;
            }
            if (controller == null && this.tickCount > 20) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
        if ((this.getTarget() == null || comingBack) && strikeProgress > 0) {
            strikeProgress = Math.max(0, strikeProgress - 0.1F);
        }
        if (controller instanceof TeletorEntity teletor) {
            this.entityData.set(CONTROLLER_ID, teletor.getId());
            teletor.setWeaponUUID(this.getUUID());
            if (!level.isClientSide) {
                Entity e = teletor.getTarget();
                this.entityData.set(TARGET_ID, e != null && e.isAlive() ? e.getId() : -1);
            }
            boolean attacking = !comingBack && target != null && target.isAlive();
            Vec3 vec3 = attacking ? target.getEyePosition() : teletor.getWeaponPosition();
            Vec3 want = vec3.subtract(this.position());
            if (target != null && !comingBack) {
                this.entityData.set(IDLING, false);
                if (want.length() < target.getBbWidth() + 1F) {
                    if (strikeProgress < 1F) {
                        strikeProgress = Math.max(0, strikeProgress + 0.35F);
                    } else {
                        target.hurt(DamageSource.mobAttack(teletor), (float) getDamageForItem(this.getItemStack()));
                        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(this.getItemStack()).entrySet()) {
                            entry.getKey().doPostHurt(teletor, target, entry.getValue());
                            entry.getKey().doPostAttack(teletor, target, entry.getValue());
                        }
                        teletor.doEnchantDamageEffects(teletor, target);
                        this.comingBack = true;
                    }
                } else if (want.length() > 32) {
                    this.comingBack = true;
                }
            }
            if (want.length() > 1F) {
                want = want.normalize();
            }
            if (this.distanceTo(controller) < 2.5F && this.getY() > controller.getY()) {
                this.entityData.set(IDLING, true);
                if (this.comingBack) {
                    this.comingBack = false;
                }
            }
            float targetXRot = (float) (-(Mth.atan2(want.y, want.horizontalDistance()) * (double) (180F / (float) Math.PI)));
            float targetYRot = (float) (Mth.atan2(want.x, want.z) * (double) (180F / (float) Math.PI)) - 90.0F;
            if (isIdling()) {
                targetXRot = this.getXRot();
                targetYRot = this.getYRot() + 5;
            }
            this.setXRot(Mth.approachDegrees(this.getXRot(), targetXRot, 5F));
            this.setYRot(Mth.approachDegrees(this.getYRot(), targetYRot, 5F));
            this.setDeltaMovement(this.getDeltaMovement().add(want.scale(0.1F)));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9F));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("WeaponStack")) {
            this.setItemStack(ItemStack.of(tag.getCompound("WeaponStack")));
        }
        if (tag.hasUUID("ControllerUUID")) {
            this.setControllerUUID(tag.getUUID("ControllerUUID"));
        }
    }

    public double getDamageForItem(ItemStack itemStack) {
        Multimap<Attribute, AttributeModifier> map = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        if (!map.isEmpty()) {
            double d = 0;
            for (AttributeModifier mod : map.get(Attributes.ATTACK_DAMAGE)) {
                d += mod.getAmount();
            }
            return d;
        }
        return 0;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (!this.getItemStack().isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            this.getItemStack().save(stackTag);
            tag.put("WeaponStack", stackTag);
        }
        if (this.getControllerUUID() != null) {
            tag.putUUID("ControllerUUID", this.getControllerUUID());
        }
    }

    public ItemStack getItemStack() {
        return this.entityData.get(ITEMSTACK);
    }

    public void setItemStack(ItemStack item) {
        this.entityData.set(ITEMSTACK, item);
    }

    public boolean isIdling() {
        return this.entityData.get(IDLING);
    }

    @Nullable
    public UUID getControllerUUID() {
        return this.entityData.get(CONTROLLER_UUID).orElse(null);
    }

    public void setControllerUUID(@Nullable UUID uniqueId) {
        this.entityData.set(CONTROLLER_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getController() {
        if (!level.isClientSide) {
            final UUID id = getControllerUUID();
            return id == null ? null : ((ServerLevel) level).getEntity(id);
        } else {
            int id = this.entityData.get(CONTROLLER_ID);
            return id == -1 ? null : level.getEntity(id);
        }
    }

    public Entity getTarget() {
        int id = this.entityData.get(TARGET_ID);
        return id == -1 ? null : level.getEntity(id);
    }

    public float getStrikeProgress(float partialTick) {
        return prevStrikeProgress + (strikeProgress - prevStrikeProgress) * partialTick;
    }

}
