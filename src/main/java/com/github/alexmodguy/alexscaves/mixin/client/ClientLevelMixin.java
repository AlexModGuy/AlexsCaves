package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {

    protected ClientLevelMixin(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    //use the "time of day" to get daytime independent sky of cave biomes.
    @Inject(method = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true)
    private void ac_getSkyColor_timeOfDay(Vec3 position, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        if (AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides.get()) {
            if (ClientProxy.acSkyOverrideAmount > 0.0F) {
                Vec3 prevVec3 = cir.getReturnValue();
                Vec3 sampledVec3 = ClientProxy.acSkyOverrideColor;
                sampledVec3 = ClientProxy.processSkyColor(sampledVec3, partialTick);
                cir.setReturnValue(prevVec3.add(sampledVec3.subtract(prevVec3).scale(ClientProxy.acSkyOverrideAmount)));
            }
        }
    }

    @Inject(method = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyDarken(F)F",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true)
    private void ac_getSkyDarken_timeOfDay(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides.get()) {
            float skyDarken = cir.getReturnValue();
            if (ClientProxy.acSkyOverrideAmount > 0.0F) {
                cir.setReturnValue(Math.max(skyDarken, ClientProxy.acSkyOverrideAmount));
            }
        }
    }
}
