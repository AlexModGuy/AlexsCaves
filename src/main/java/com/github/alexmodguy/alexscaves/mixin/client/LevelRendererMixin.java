package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow private ClientLevel level;
    @Final @Shadow private Minecraft minecraft;
    @Final @Shadow private VertexBuffer skyBuffer;
    @Shadow private int ticks;

    @Shadow
    protected abstract boolean doesMobEffectBlockSky(Camera camera);

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/math/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void ac_renderSky(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean b, Runnable runnable, CallbackInfo ci) {
        float override = calculateBiomeSkyOverride(camera.getEntity());
        if(override > 0.0F){
            renderCaveBiomeSky(poseStack, matrix4f, f, camera, b, runnable, override);
        }
        if(override >= 1.0F){
            ci.cancel();
        }
    }

    private static float calculateBiomeSkyOverride(Entity player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return ACBiomeRegistry.getBiomeSkyOverride(player.level.getBiome(player.blockPosition()));
        } else {
            Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                return new Vec3(ACBiomeRegistry.getBiomeSkyOverride(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
            });
            return (float) vec31.x;
        }
    }

    public void renderCaveBiomeSky(PoseStack poseStack, Matrix4f matrix4f1, float partialTick, Camera camera, boolean b, Runnable runnable, float progress) {
        if (level.effects().renderSky(level, ticks, partialTick, poseStack, camera, matrix4f1, b, runnable))
            return;
        runnable.run();
        if (!b) {
            FogType fogtype = camera.getFluidInCamera();
            if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !this.doesMobEffectBlockSky(camera)) {
                RenderSystem.disableTexture();
                Vec3 vec3 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), partialTick);
                float f = (float)vec3.x;
                float f1 = (float)vec3.y;
                float f2 = (float)vec3.z;
                FogRenderer.levelFogColor();
                BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
                RenderSystem.depthMask(false);
                RenderSystem.setShaderColor(f, f1, f2, progress);
                ShaderInstance shaderinstance = RenderSystem.getShader();
                this.skyBuffer.bind();
                this.skyBuffer.drawWithShader(poseStack.last().pose(), matrix4f1, shaderinstance);
                VertexBuffer.unbind();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableTexture();
                RenderSystem.setShaderColor(f, f1, f2, progress);
                RenderSystem.enableTexture();
                RenderSystem.depthMask(true);
            }
        }
    }


}
