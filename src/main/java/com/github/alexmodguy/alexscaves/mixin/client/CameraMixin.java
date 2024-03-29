package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    public abstract void move(double forwards, double up, double side);

    @Shadow
    protected abstract void setPosition(Vec3 vec3);

    @Shadow
    protected abstract double getMaxZoom(double p_90567_);

    @Shadow
    protected abstract void setRotation(float p_90573_, float p_90574_);

    @Shadow
    private float yRot;

    @Shadow
    private float xRot;

    @Shadow
    private boolean initialized;

    @Inject(
            method = {"Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_onSyncedDataUpdated(BlockGetter level, Entity entity, boolean detatched, boolean mirrored, float partialTicks, CallbackInfo ci) {
        Direction dir = MagnetUtil.getEntityMagneticDirection(entity);
        if (dir != Direction.DOWN && dir != Direction.UP) {
            this.setPosition(MagnetUtil.getEyePositionForAttachment(entity, dir, partialTicks));
            if (detatched) {
                if (mirrored) {
                    this.setRotation(this.yRot + 180.0F, -this.xRot);
                }
                this.move(-this.getMaxZoom(4.0D), 0.0D, 0.0D);
            }
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/Camera;getFluidInCamera()Lnet/minecraft/world/level/material/FogType;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_getFluidInCamera(CallbackInfoReturnable<FogType> cir) {
        if (initialized && Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(ACEffectRegistry.BUBBLED.get()) && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            cir.setReturnValue(FogType.WATER);
        }
    }
}
