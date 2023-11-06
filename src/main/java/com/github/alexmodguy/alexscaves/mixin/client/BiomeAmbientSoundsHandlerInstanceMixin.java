package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeAmbientSoundsHandler.LoopSoundInstance.class)
public abstract class BiomeAmbientSoundsHandlerInstanceMixin extends AbstractTickableSoundInstance {

    protected BiomeAmbientSoundsHandlerInstanceMixin(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource) {
        super(soundEvent, soundSource, randomSource);
    }

    @Shadow private int fade;

    @Shadow private int fadeDirection;

    @Inject(method = "Lnet/minecraft/client/resources/sounds/BiomeAmbientSoundsHandler$LoopSoundInstance;tick()V",
            at = @At("HEAD"), cancellable = true)
    private void ac_tick(CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if(player != null && !player.isUnderWater() && player.level().getBiome(player.blockPosition()).is(ACTagRegistry.ONLY_AMBIENT_LOOP_UNDERWATER)){
            volume = 0;
            ci.cancel();
        }
    }
}
