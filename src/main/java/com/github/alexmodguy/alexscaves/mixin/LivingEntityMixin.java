package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.util.HeadRotationEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements HeadRotationEntityAccessor {

    @Shadow public abstract float getYHeadRot();

    @Shadow public float yHeadRotO;

    @Shadow public abstract boolean hasEffect(MobEffect p_21024_);

    private float prevHeadYaw;
    private float prevHeadYaw0;
    private float prevHeadPitch;
    private float prevHeadPitch0;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;calculateEntityAnimation(Lnet/minecraft/world/entity/LivingEntity;Z)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_calculateEntityAnimation(LivingEntity living, boolean b, CallbackInfo ci) {
        if(MagnetUtil.isPulledByMagnets(this) && ((MagneticEntityAccessor)this).getMagneticAttachmentFace() != Direction.DOWN){
            ci.cancel();
            living.animationSpeedOld = living.animationSpeed;
            double d0 = living.getX() - living.xo;
            double d1 = living.getY() - living.yo;
            double d2 = living.getZ() - living.zo;
            float f = (float)Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 6.0F;
            if (f > 1F) {
                f = 1F;
            }
            living.animationSpeed += (f - living.animationSpeed) * 0.4F;
            living.animationPosition += living.animationSpeed;
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;increaseAirSupply(I)I"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    protected void increaseAirSupply(int air, CallbackInfoReturnable<Integer> cir) {
        if(this.hasEffect(ACEffectRegistry.BUBBLED.get())) {
            cir.setReturnValue(air);
        }
    }

    public void setMagnetHeadRotation(){
        prevHeadYaw = this.getYHeadRot();
        prevHeadYaw0 = this.yHeadRotO;
        prevHeadPitch = this.getXRot();
        prevHeadPitch0 = this.xRotO;
        MagnetUtil.rotateHead((LivingEntity)(Entity)this);
    }


    public void resetMagnetHeadRotation(){
        setYHeadRot(prevHeadYaw);
        this.yHeadRotO = prevHeadYaw0;
        setXRot(prevHeadPitch);
        this.xRotO = prevHeadPitch0;
    }
}
