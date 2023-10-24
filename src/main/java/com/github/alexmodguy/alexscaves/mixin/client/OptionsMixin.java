package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.entity.util.PossessesCamera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {

    @Shadow private CameraType cameraType;

    @Inject(method = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V",
            at = @At("TAIL"))
    private void ac_setCameraType(CallbackInfo ci) {
        if(Minecraft.getInstance().getCameraEntity() instanceof PossessesCamera){
            this.cameraType = CameraType.FIRST_PERSON;
        }
    }
}
