package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.IOException;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Nullable
    private PostChain irradiatedEffect;
    @Shadow
    private ClientLevel level;
    @Final
    @Shadow
    private Minecraft minecraft;
    @Final
    @Shadow
    private VertexBuffer skyBuffer;
    @Shadow
    private int ticks;

    @Shadow
    protected abstract boolean doesMobEffectBlockSky(Camera camera);

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void ac_renderSky(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean b, Runnable runnable, CallbackInfo ci) {
        if (AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides.get()) {
            float override = calculateBiomeSkyOverride(camera.getEntity());
            if (override > 0.0F) {
                renderCaveBiomeSky(poseStack, matrix4f, f, camera, b, runnable, override);
            }
            if (override >= 1.0F) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;initOutline()V",
            at = @At("TAIL"))
    private void ac_initOutline(CallbackInfo ci) {
        if (this.irradiatedEffect != null) {
            this.irradiatedEffect.close();
        }

        ResourceLocation resourcelocation = new ResourceLocation(AlexsCaves.MODID, "shaders/post/irradiated.json");
        try {
            this.irradiatedEffect = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), resourcelocation);
            this.irradiatedEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
            ClientProxy.irradiatedTarget = this.irradiatedEffect.getTempTarget("final");
        } catch (IOException ioexception) {
            AlexsCaves.LOGGER.warn("Failed to load shader: {}", resourcelocation, ioexception);
            this.irradiatedEffect = null;
            ClientProxy.irradiatedTarget = null;
        } catch (JsonSyntaxException jsonsyntaxexception) {
            AlexsCaves.LOGGER.warn("Failed to parse shader: {}", resourcelocation, jsonsyntaxexception);
            this.irradiatedEffect = null;
            ClientProxy.irradiatedTarget = null;
        }
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;resize(II)V",
            at = @At("TAIL"))
    private void ac_resize(int x, int y, CallbackInfo ci) {
        if (this.irradiatedEffect != null) {
            this.irradiatedEffect.resize(x, y);
        }
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V",
            at = @At("TAIL"))
    private void ac_doEntityOutline(CallbackInfo ci) {
        if (ClientProxy.irradiatedTarget != null&& AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            ClientProxy.irradiatedTarget.blitToScreen(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), false);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }

    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;",
                    shift = At.Shift.BEFORE
            ))
    private void ac_renderLevel_clear(PoseStack poseStack, float f, long l, boolean b, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        if (ClientProxy.irradiatedTarget != null && AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
            ClientProxy.irradiatedTarget.clear(Minecraft.ON_OSX);
            ClientProxy.irradiatedTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
    }


    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
            at = @At(value = "TAIL"))
    private void ac_renderLevel_end(PoseStack poseStack, float f, long l, boolean b, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        if (ClientProxy.irradiatedOutlineFlag && irradiatedEffect != null && AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
            this.irradiatedEffect.process(f);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
        ClientProxy.irradiatedOutlineFlag = false;
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
                Vec3 vec3 = CubicSampler.gaussianSampleVec3(this.minecraft.player.position(), (x, y, z) -> {
                    return Vec3.fromRGB24((this.minecraft.player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z).value().getSkyColor()));
                });
                float f = (float) vec3.x;
                float f1 = (float) vec3.y;
                float f2 = (float) vec3.z;
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
