package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.util.HeadRotationEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements HeadRotationEntityAccessor {

    @Shadow
    public abstract float getYHeadRot();

    @Shadow
    public float yHeadRotO;

    @Shadow
    public abstract boolean hasEffect(MobEffect p_21024_);

    @Shadow
    @Final
    public WalkAnimationState walkAnimation;
    @Shadow public float yHeadRot;
    private float prevHeadYaw;
    private float prevHeadYaw0;
    private float prevHeadPitch;
    private float prevHeadPitch0;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;calculateEntityAnimation(Z)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_calculateEntityAnimation(boolean b, CallbackInfo ci) {
        if (MagnetUtil.isPulledByMagnets(this) && ((MagneticEntityAccessor) this).getMagneticAttachmentFace() != Direction.DOWN) {
            ci.cancel();
            float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
            float f2 = Math.min(f1 * 6, 1.0F);
            this.walkAnimation.update(f2, 0.4F);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;increaseAirSupply(I)I"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    protected void ac_increaseAirSupply(int air, CallbackInfoReturnable<Integer> cir) {
        if (this.hasEffect(ACEffectRegistry.BUBBLED.get())) {
            cir.setReturnValue(air);
        }
    }

    public void setMagnetHeadRotation() {
        prevHeadYaw = this.getYHeadRot();
        prevHeadYaw0 = this.yHeadRotO;
        prevHeadPitch = this.getXRot();
        prevHeadPitch0 = this.xRotO;
        MagnetUtil.rotateHead((LivingEntity) (Entity) this);
    }


    public void resetMagnetHeadRotation() {
        this.yHeadRot = prevHeadYaw;
        this.yHeadRotO = prevHeadYaw0;
        setXRot(prevHeadPitch);
        this.xRotO = prevHeadPitch0;
    }
}
