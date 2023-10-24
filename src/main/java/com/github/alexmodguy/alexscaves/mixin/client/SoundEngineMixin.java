package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.sound.NuclearExplosionSound;
import com.github.alexmodguy.alexscaves.client.sound.UnlimitedPitch;
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

import java.util.Iterator;
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
        if(!(soundInstance instanceof NuclearExplosionSound) && ClientProxy.masterVolumeNukeModifier > 0 && AlexsCaves.CLIENT_CONFIG.nuclearBombMufflesSounds.get()){
            float f = Math.max(1.0F - ClientProxy.masterVolumeNukeModifier, 0.01F);
            cir.setReturnValue(cir.getReturnValue() * f);
        }
    }

    @Inject(method = "Lnet/minecraft/client/sounds/SoundEngine;tickNonPaused()V",
            at = @At("TAIL"))
    private void ac_tickNonPaused(CallbackInfo ci) {
        if((lastNukeSoundDampenBy != ClientProxy.masterVolumeNukeModifier || ClientProxy.masterVolumeNukeModifier > 0) && AlexsCaves.CLIENT_CONFIG.nuclearBombMufflesSounds.get()){
            dampenSoundsFromNuke();
        }
    }

    public void dampenSoundsFromNuke(){
        Iterator<Map.Entry<SoundInstance, ChannelAccess.ChannelHandle>> iterator = this.instanceToChannel.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry = iterator.next();
            ChannelAccess.ChannelHandle channelHandle = entry.getValue();
            SoundInstance soundinstance = entry.getKey();
            float f = this.calculateVolume(soundinstance);
            channelHandle.execute((sound) -> {
                sound.setVolume(f);
            });
        }
        lastNukeSoundDampenBy = ClientProxy.masterVolumeNukeModifier;
    }
}
