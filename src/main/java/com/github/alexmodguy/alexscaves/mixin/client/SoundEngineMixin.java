package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.sound.UnlimitedPitch;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Inject(method = "Lnet/minecraft/client/sounds/SoundEngine;calculatePitch(Lnet/minecraft/client/resources/sounds/SoundInstance;)F",
            at = @At("HEAD"),
            cancellable = true)
    private void ac_calculatePitch(SoundInstance soundInstance, CallbackInfoReturnable<Float> cir) {
        if (soundInstance instanceof UnlimitedPitch) {
            cir.setReturnValue(soundInstance.getPitch());
        }
    }
}
