package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.entity.util.PossessesCamera;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    @Nullable
    public abstract Entity getCameraEntity();

    @Shadow @Nullable public LocalPlayer player;

    @Inject(method = "Lnet/minecraft/client/Minecraft;startAttack()Z",
            at = @At("HEAD"),
            cancellable = true)
    private void ac_startAttack(CallbackInfoReturnable<Boolean> cir) {
        if (getCameraEntity() instanceof PossessesCamera) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "Lnet/minecraft/client/Minecraft;getSituationalMusic()Lnet/minecraft/sounds/Music;",
            at = @At("HEAD"),
            cancellable = true)
    private void ac_getSituationalMusic(CallbackInfoReturnable<Music> cir) {
        if(this.player != null){
            Holder<Biome> holder = this.player.level().getBiome(this.player.blockPosition());
            if(holder.is(ACTagRegistry.OVERRIDE_ALL_VANILLA_MUSIC_IN)){
                cir.setReturnValue(holder.value().getBackgroundMusic().orElse(Musics.GAME));
            }
        }
    }
}
