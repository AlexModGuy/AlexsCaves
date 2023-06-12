package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.WatcherEntity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DeepsightEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
        if(AlexsCaves.CLIENT_CONFIG.biomeAmbientLight.get()){
            float f = calculateBiomeAmbientLight(Minecraft.getInstance().player);
            if(Minecraft.getInstance().getCameraEntity() instanceof WatcherEntity){
                f = Math.max(f, 0.35F);
            }
            if(Minecraft.getInstance().player.hasEffect(ACEffectRegistry.DEEPSIGHT.get()) && Minecraft.getInstance().player.isUnderWater()){
                f = Math.min(1.0F, f + 0.05F * DeepsightEffect.getIntensity(Minecraft.getInstance().player, Minecraft.getInstance().getFrameTime()));
            }
            if(f != 0){
                cir.setReturnValue(f + cir.getReturnValue());
            }
        }
    }

    @Redirect(method = "Lnet/minecraft/client/renderer/LightTexture;updateLightTexture(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;adjustLightmapColors(Lnet/minecraft/client/multiplayer/ClientLevel;FFFFIILorg/joml/Vector3f;)V"))
    private void ac_adjustLightmapColors(DimensionSpecialEffects specialEffects, ClientLevel clientLevel, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f vector3f) {
        specialEffects.adjustLightmapColors(clientLevel, partialTicks, skyDarken, skyLight, blockLight, pixelX, pixelY, vector3f);
        if(AlexsCaves.CLIENT_CONFIG.biomeAmbientLightColoring.get()){
            if(!clientLevel.effects().forceBrightLightmap()){
                Vec3 in = new Vec3(vector3f);
                Vec3 to = calculateBiomeLightColor(Minecraft.getInstance().player);
                vector3f.set(to.x * in.x, to.y * in.y, to.z * in.z);
            }
        }
    }

    private static float calculateBiomeAmbientLight(Player player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return ACBiomeRegistry.getBiomeAmbientLight(player.level().getBiome(player.blockPosition()));
        } else {
            Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                return new Vec3(ACBiomeRegistry.getBiomeAmbientLight(player.level().getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
            });
            return (float) vec31.x;
        }
    }

    private static Vec3 calculateBiomeLightColor(Player player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return ACBiomeRegistry.getBiomeLightColorOverride(player.level().getBiome(player.blockPosition()));
        } else {
            Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                return ACBiomeRegistry.getBiomeLightColorOverride(player.level().getBiomeManager().getNoiseBiomeAtPosition(x, y, z));
            });
            return vec31;
        }
    }
}
