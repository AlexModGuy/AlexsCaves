package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.entity.util.MinecartAccessor;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RidingMinecartSoundInstance.class)
public abstract class RidingMinecartSoundInstanceMixin extends AbstractTickableSoundInstance {

    @Shadow
    @Final
    private AbstractMinecart minecart;

    protected RidingMinecartSoundInstanceMixin(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource) {
        super(soundEvent, soundSource, randomSource);
    }

    @Inject(
            method = {"Lnet/minecraft/client/resources/sounds/RidingMinecartSoundInstance;tick()V"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void ac_tick(CallbackInfo ci) {
        if (((MinecartAccessor) minecart).isOnMagLevRail()) {
            volume = 0.0F;
            ci.cancel();
        }
    }
}
