package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.CitadelConstants;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements MagneticEntityAccessor {

    @Shadow
    @Final
    protected SynchedEntityData entityData;

    @Shadow
    protected abstract void playStepSound(BlockPos p_20135_, BlockState p_20136_);

    @Shadow
    private Level level;
    @Shadow
    private Vec3 position;

    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    public abstract void tick();

    @Shadow
    public abstract void refreshDimensions();

    @Shadow
    protected boolean wasTouchingWater;
    private static final EntityDataAccessor<Float> MAGNET_DELTA_X = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAGNET_DELTA_Y = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAGNET_DELTA_Z = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Direction> MAGNET_ATTACHMENT_DIRECTION = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.DIRECTION);
    private float attachChangeProgress = 0F;
    private float prevAttachChangeProgress = 0F;
    private Direction prevAttachDir = Direction.DOWN;
    private int jumpFlipCooldown = 0;

    private BlockPos lastStepPos;

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "Lnet/minecraft/world/entity/Entity;<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V")
    private void citadel_registerData(CallbackInfo ci) {
        entityData.define(MAGNET_DELTA_X, 0F);
        entityData.define(MAGNET_DELTA_Y, 0F);
        entityData.define(MAGNET_DELTA_Z, 0F);
        entityData.define(MAGNET_ATTACHMENT_DIRECTION, Direction.DOWN);
    }


    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;tick()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_tick(CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
        prevAttachChangeProgress = attachChangeProgress;
        if (this.prevAttachDir != this.getMagneticAttachmentFace()) {
            if (attachChangeProgress < 1.0F) {
                attachChangeProgress += 0.1F;
            } else if (attachChangeProgress >= 1.0F) {
                this.prevAttachDir = this.getMagneticAttachmentFace();
            }
        } else {
            this.attachChangeProgress = 1.0F;
        }

        if (MagnetUtil.isPulledByMagnets(thisEntity)) {
            MagnetUtil.tickMagnetism(thisEntity);
            if (this.jumpFlipCooldown > 0) {
                this.jumpFlipCooldown--;
            }
        } else {
            if (this.getMagneticAttachmentFace() != Direction.DOWN) {
                this.setMagneticAttachmentFace(Direction.DOWN);
                this.refreshDimensions();
            }
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor, CallbackInfo ci) {
        if (MAGNET_ATTACHMENT_DIRECTION.equals(entityDataAccessor)) {
            this.prevAttachChangeProgress = 0.0F;
            this.attachChangeProgress = 0.0F;
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;getEyePosition()Lnet/minecraft/world/phys/Vec3;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_getEyePosition(CallbackInfoReturnable<Vec3> cir) {
        if (getMagneticAttachmentFace() != Direction.DOWN) {
            cir.setReturnValue(MagnetUtil.getEyePositionForAttachment((Entity) (Object) this, getMagneticAttachmentFace(), 1.0F));
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_getEyePosition_lerp(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        if (getMagneticAttachmentFace() != Direction.DOWN && getMagneticAttachmentFace() != Direction.UP) {
            cir.setReturnValue(MagnetUtil.getEyePositionForAttachment((Entity) (Object) this, getMagneticAttachmentFace(), partialTick));
        }
    }

    @Redirect(method = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntityCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    public List<VoxelShape> ac_getEntityCollisions(Level instance, Entity entity, AABB aabb) {
        List<VoxelShape> list1 = instance.getEntityCollisions(entity, aabb);
        List<VoxelShape> list2 = MagnetUtil.getMovingBlockCollisions(entity, aabb);
        return ImmutableList.<VoxelShape>builder().addAll(list1).addAll(list2).build();
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;turn(DD)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_turn(double yBy, double xBy, CallbackInfo ci) {
        if (getMagneticAttachmentFace() != Direction.DOWN) {
            ci.cancel();
            MagnetUtil.turnEntityOnMagnet((Entity) (Object) this, xBy, yBy, getMagneticAttachmentFace());
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;makeBoundingBox()Lnet/minecraft/world/phys/AABB;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_makeBoundingBox(CallbackInfoReturnable<AABB> cir) {
        if (this.entityData.isDirty() && getMagneticAttachmentFace() != Direction.DOWN) {
            cir.setReturnValue(MagnetUtil.rotateBoundingBox(dimensions, getMagneticAttachmentFace(), position));
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;isInWater()Z"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_isInWater(CallbackInfoReturnable<Boolean> cir) {
        if((Object)this instanceof LivingEntity living && living.hasEffect(ACEffectRegistry.BUBBLED.get()) && (living.canBreatheUnderwater() || living.getMobType() == MobType.WATER) && !living.getType().is(ACTagRegistry.RESISTS_BUBBLED)){
            cir.setReturnValue(true);
        }
    }

    @Override
    public float getMagneticDeltaX() {
        return entityData.get(MAGNET_DELTA_X);
    }

    @Override
    public float getMagneticDeltaY() {
        return entityData.get(MAGNET_DELTA_Y);
    }

    @Override
    public float getMagneticDeltaZ() {
        return entityData.get(MAGNET_DELTA_Z);
    }

    @Override
    public Direction getMagneticAttachmentFace() {
        return entityData.get(MAGNET_ATTACHMENT_DIRECTION);
    }

    @Override
    public Direction getPrevMagneticAttachmentFace() {
        return prevAttachDir;
    }

    @Override
    public float getAttachmentProgress(float partialTicks) {
        return prevAttachChangeProgress + (attachChangeProgress - prevAttachChangeProgress) * partialTicks;
    }

    @Override
    public void setMagneticDeltaX(float f) {
        entityData.set(MAGNET_DELTA_X, f);
    }

    @Override
    public void setMagneticDeltaY(float f) {
        entityData.set(MAGNET_DELTA_Y, f);
    }

    @Override
    public void setMagneticDeltaZ(float f) {
        entityData.set(MAGNET_DELTA_Z, f);
    }

    @Override
    public void setMagneticAttachmentFace(Direction dir) {
        entityData.set(MAGNET_ATTACHMENT_DIRECTION, dir);
    }


    @Override
    public void postMagnetJump() {
        this.jumpFlipCooldown = 20;
    }

    @Override
    public boolean canChangeDirection() {
        return jumpFlipCooldown <= 0 && getAttachmentProgress(1.0F) == 1.0F;
    }

    @Override
    public void stepOnMagnetBlock(BlockPos pos) {
        if (lastStepPos == null || lastStepPos.distSqr(pos) > 2) {
            this.lastStepPos = pos;
            this.playStepSound(pos, level.getBlockState(pos));
        }
    }
}
