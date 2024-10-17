package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.MapInstance.class)
public class MapRendererMapInstanceMixin {

    @Inject(
            method = {"Lnet/minecraft/client/gui/MapRenderer$MapInstance;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ZI)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_render(PoseStack poseStack, MultiBufferSource multiBufferSource, boolean inFrame, int packedLighting, CallbackInfo ci) {
        ClientEvents.lastVanillaMapPoseStack = poseStack;
        ClientEvents.lastVanillaMapRenderBuffer = multiBufferSource;
        ClientEvents.lastVanillaMapRenderPackedLight = packedLighting;
    }
}
