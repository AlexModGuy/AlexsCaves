package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UnderwaterAmbientSoundHandler.class)
public class UnderwaterAmbientSoundHandlerMixin {

    @Shadow @Final private LocalPlayer player;

    @Inject(method = "Lnet/minecraft/client/resources/sounds/UnderwaterAmbientSoundHandler;tick()V",
            at = @At("HEAD"), cancellable = true)
    private void ac_tick(CallbackInfo ci) {
        if(player.isUnderWater() && player.level().getBiome(player.blockPosition()).is(ACTagRegistry.OVERRIDE_UNDERWATER_AMBIENCE_IN)) {
            ci.cancel();
        }
    }
}
