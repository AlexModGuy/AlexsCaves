package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @Inject(
            method = {"Lnet/minecraft/client/renderer/LightTexture;getBrightness(Lnet/minecraft/world/level/dimension/DimensionType;I)F"},
            remap = true,
            cancellable = true,
            at = @At(value = "TAIL")
    )
    private static void ac_getBrightness(DimensionType dimensionType, int lightTextureIndex, CallbackInfoReturnable<Float> cir) {
        float f = calculateBiomeAmbientLight(Minecraft.getInstance().player);
        if(f > 0){
            cir.setReturnValue(f + cir.getReturnValue());
        }
    }

    private static float calculateBiomeAmbientLight(Player player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return ACBiomeRegistry.getBiomeAmbientLight(player.level.getBiome(player.blockPosition()));
        } else {
            Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                return new Vec3(ACBiomeRegistry.getBiomeAmbientLight(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
            });
            return (float) vec31.x;
        }
    }
}
