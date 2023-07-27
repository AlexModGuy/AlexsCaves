package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.util.FallingBlockEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexthe666.citadel.CitadelConstants;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity implements FallingBlockEntityAccessor {

    @Shadow
    public int time;
    private static final EntityDataAccessor<Integer> FALL_BLOCK_TIME = SynchedEntityData.defineId(FallingBlockEntity.class, EntityDataSerializers.INT);

    public FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "Lnet/minecraft/world/entity/item/FallingBlockEntity;defineSynchedData()V")
    private void citadel_registerData(CallbackInfo ci) {
        entityData.define(FALL_BLOCK_TIME, 0);
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/item/FallingBlockEntity;tick()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_tick(CallbackInfo ci) {
        if (!this.isNoGravity() && hasFallBlocking()) {
            time = 10;
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.04D, 0.0D));
        }
        int fallBlockTime = entityData.get(FALL_BLOCK_TIME);
        if (fallBlockTime > 0) {
            entityData.set(FALL_BLOCK_TIME, fallBlockTime - 1);
        }
        if (MagnetUtil.isPulledByMagnets(this)) {
            MagnetUtil.tickMagnetism(this);
            if (MagnetUtil.getEntityMagneticDelta(this) != Vec3.ZERO) {
                this.setFallBlockingTime();
            }
        }
    }

    public boolean hasFallBlocking() {
        return entityData.get(FALL_BLOCK_TIME) > 0;
    }

    public void setFallBlockingTime() {
        entityData.set(FALL_BLOCK_TIME, 10);
    }
}
