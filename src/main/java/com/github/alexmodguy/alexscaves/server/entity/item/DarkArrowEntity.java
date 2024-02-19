package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.PlayMessages;

public class DarkArrowEntity extends AbstractArrow {

    private float fadeOut = 0;
    private float prevFadeOut = 0;
    private boolean startFading = false;
    private float arrowR = 0;
    private float prevArrowR = 0;
    private static final EntityDataAccessor<Float> SHADOW_ARROW_DAMAGE = SynchedEntityData.defineId(DarkArrowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> PERFECT_SHOT = SynchedEntityData.defineId(DarkArrowEntity.class, EntityDataSerializers.BOOLEAN);

    public DarkArrowEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public DarkArrowEntity(Level level, LivingEntity shooter) {
        super(ACEntityRegistry.DARK_ARROW.get(), shooter, level);
    }

    public DarkArrowEntity(Level level, double x, double y, double z) {
        super(ACEntityRegistry.DARK_ARROW.get(), x, y, z, level);
    }

    public DarkArrowEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.DARK_ARROW.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHADOW_ARROW_DAMAGE, 0.0F);
        this.entityData.define(PERFECT_SHOT, false);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void startFalling() {
        this.inGround = false;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevArrowR = this.arrowR;
        this.prevFadeOut = this.fadeOut;
        if (this.inGround) {
            this.startFading = true;
        }
        if (this.startFading) {
            this.noPhysics = true;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.7F));
            if (this.fadeOut++ > 5F) {
                this.discard();
            }
        }
        if(this.isPerfectShot() && this.arrowR < 1.0F){
            this.arrowR = Math.min(arrowR + 0.15F, 1.0F);
        }
    }

    protected float getWaterInertia() {
        return 0.9F;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        float damage = this.getShadowArrowDamage();
        if(this.isPerfectShot()){
            damage *= 2;
        }
        DamageSource damageSource = ACDamageTypes.causeDarkArrowDamage(entity.level().registryAccess(), owner);
        if ((owner == null || !entity.is(owner) && !entity.isAlliedTo(owner) && !owner.isAlliedTo(entity)) && !this.startFading) {
            if (entity.hurt(damageSource, damage)) {
                this.startFading = true;
            }
        }
    }

    public float getShadowArrowDamage() {
        return this.entityData.get(SHADOW_ARROW_DAMAGE);
    }

    public void setShadowArrowDamage(float f) {
        this.entityData.set(SHADOW_ARROW_DAMAGE, f);
    }

    public void setPerfectShot(boolean b) {
        this.entityData.set(PERFECT_SHOT, b);
    }

    public boolean isPerfectShot() {
       return this.entityData.get(PERFECT_SHOT);
    }

    public float getFadeOut(float partialTicks) {
        return prevFadeOut + (fadeOut - prevFadeOut) * partialTicks;
    }
    public float getArrowRed(float partialTicks) {
        return prevArrowR + (arrowR - prevArrowR) * partialTicks;
    }
}