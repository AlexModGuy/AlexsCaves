package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.sound.NuclearExplosionSound;
import com.github.alexmodguy.alexscaves.client.sound.UnlimitedPitch;
import net.minecraft.client.Camera;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Shadow @Final private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Shadow protected abstract float calculateVolume(SoundInstance p_120328_);

    private float lastNukeSoundDampenBy = 0;

    @Inject(method = "Lnet/minecraft/client/sounds/SoundEngine;calculatePitch(Lnet/minecraft/client/resources/sounds/SoundInstance;)F",
            at = @At("HEAD"),
            cancellable = true)
    private void ac_calculatePitch(SoundInstance soundInstance, CallbackInfoReturnable<Float> cir) {
        if (soundInstance instanceof UnlimitedPitch) {
            cir.setReturnValue(soundInstance.getPitch());
        }
    }

    @Inject(method = "Lnet/minecraft/client/sounds/SoundEngine;calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F",
            at = @At("RETURN"),
            cancellable = true)
    private void ac_calculateVolume(SoundInstance soundInstance, CallbackInfoReturnable<Float> cir) {
        if(!(soundInstance instanceof NuclearExplosionSound) && ClientProxy.masterVolumeNukeModifier > 0){
            float f = Math.max(1.0F - ClientProxy.masterVolumeNukeModifier, 0.001F);
            cir.setReturnValue(cir.getReturnValue() * f);
        }
    }

    @Inject(method = "Lnet/minecraft/client/sounds/SoundEngine;updateSource(Lnet/minecraft/client/Camera;)V",
            at = @At("TAIL"))
    private void ac_updateSource(Camera camera, CallbackInfo ci) {
        if(lastNukeSoundDampenBy != ClientProxy.masterVolumeNukeModifier || ClientProxy.masterVolumeNukeModifier > 0){
            updateAllSoundVolumes();
        }
    }

    public void updateAllSoundVolumes(){
        this.instanceToChannel.forEach((soundInstance, channelHandle) -> {
            float f = this.calculateVolume(soundInstance);
            channelHandle.execute((sound) -> {
                if (f <= 0.0F) {
                    sound.stop();
                } else {
                    sound.setVolume(f);
                }
            });
        });
        lastNukeSoundDampenBy = ClientProxy.masterVolumeNukeModifier;
    }
}
