package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.LevelHeightAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.ClientLevelData.class)
public abstract class ClientLevelDataMixin  {

    @Inject(method = "Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;getHorizonHeight(Lnet/minecraft/world/level/LevelHeightAccessor;)D",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true)
    private void ac_getSkyDarken_timeOfDay(LevelHeightAccessor heightAccessor, CallbackInfoReturnable<Double> cir) {
        if (AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides.get()) {
            cir.setReturnValue((double) -heightAccessor.getMaxBuildHeight());

        }
    }
}
