package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.shader.ACPostEffectRegistry;
import com.github.alexmodguy.alexscaves.server.block.EnergizedGalenaBlock;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = LevelRenderer.class, priority = 800)
public abstract class LevelRendererMixin {

    @Shadow
    private ClientLevel level;
    @Final
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private VertexBuffer skyBuffer;
    @Shadow
    private int ticks;

    private int aclastCameraChunkX;
    private int aclastCameraChunkY;
    private int aclastCameraChunkZ;

    @Shadow
    protected abstract boolean doesMobEffectBlockSky(Camera camera);

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    @Nullable
    private ViewArea viewArea;

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;initOutline()V",
            at = @At("TAIL"))
    private void ac_initOutline(CallbackInfo ci) {
        ACPostEffectRegistry.onInitializeOutline();
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;resize(II)V",
            at = @At("TAIL"))
    private void ac_resize(int x, int y, CallbackInfo ci) {
        ACPostEffectRegistry.resize(x, y);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;",
                    shift = At.Shift.BEFORE
            ))
    private void ac_renderLevel_beforeEntities(PoseStack poseStack, float f, long l, boolean b, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        ACPostEffectRegistry.copyDepth(this.minecraft.getMainRenderTarget());
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/OutlineBufferSource;endOutlineBatch()V",
                    shift = At.Shift.BEFORE
            ))
    private void ac_renderLevel_process(PoseStack poseStack, float f, long l, boolean b, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        ACPostEffectRegistry.processEffects(this.minecraft.getMainRenderTarget(), f);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "TAIL"
            ))
    private void ac_renderLevel_end(PoseStack poseStack, float f, long l, boolean b, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        ACPostEffectRegistry.blitEffects();
    }

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V",
            at = @At(
                    value = "HEAD"
            ),
            allow = 1)
    private void ac_setupRender(Camera camera, Frustum frustum, boolean b1, boolean b2, CallbackInfo ci) {
        if (Minecraft.getInstance().cameraEntity != null && Minecraft.getInstance().cameraEntity != Minecraft.getInstance().player) { // fixes chunks being too far to load when not the player
            double d0 = Minecraft.getInstance().cameraEntity.getX();
            double d1 = Minecraft.getInstance().cameraEntity.getY();
            double d2 = Minecraft.getInstance().cameraEntity.getZ();
            int i = SectionPos.posToSectionCoord(d0);
            int j = SectionPos.posToSectionCoord(d1);
            int k = SectionPos.posToSectionCoord(d2);
            if (this.aclastCameraChunkX != i || this.aclastCameraChunkY != j || this.aclastCameraChunkZ != k) {
                this.aclastCameraChunkX = i;
                this.aclastCameraChunkY = j;
                this.aclastCameraChunkZ = k;
                viewArea.repositionCamera(d0, d2);
            }
        }
    }

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

    @Inject(method = "Lnet/minecraft/client/renderer/LevelRenderer;getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
            at = @At("HEAD"),
            cancellable = true)
    private static void ac_getLightColor(BlockAndTintGetter level, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (state.getBlock() instanceof EnergizedGalenaBlock) {
            int i = level.getBrightness(LightLayer.SKY, pos);
            int j = level.getBrightness(LightLayer.BLOCK, pos);
            int k = state.getLightEmission(level, pos) - 1;
            if (j < k) {
                j = k;
            }
            cir.setReturnValue(i << 20 | (j) << 4);
        }
    }

    private static float calculateBiomeSkyOverride(Entity player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return ACBiomeRegistry.getBiomeSkyOverride(player.level().getBiome(player.blockPosition()));
        } else {
            Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                return new Vec3(ACBiomeRegistry.getBiomeSkyOverride(player.level().getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
            });
            return (float) vec31.x;
        }
    }

    public void renderCaveBiomeSky(PoseStack poseStack, Matrix4f matrix4f1, float partialTick, Camera camera, boolean b, Runnable runnable, float progress) {
        if (level.effects().renderSky(level, ticks, partialTick, poseStack, camera, matrix4f1, b, runnable))
            return;
        runnable.run();
        if (!b) {
            //possibly reimplement if needed
        }
    }


}
