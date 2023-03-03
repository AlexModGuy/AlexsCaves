package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.entity.SubmarineRenderer;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    private float darkenWorldAmount;

    @Shadow @Final private RenderBuffers renderBuffers;

    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;tick()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_tick(CallbackInfo ci) {
        if (((ClientProxy) AlexsCaves.PROXY).renderNukeSkyDark && darkenWorldAmount < 1.0F) {
            darkenWorldAmount = Math.min(darkenWorldAmount + 0.3F, 1.0F);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Lighting;setupFor3DItems()V",
                    shift = At.Shift.AFTER
            )
    )
    public void ac_render(float partialTick, long nanos, boolean idk, CallbackInfo ci) {
        float f = ((ClientProxy) AlexsCaves.PROXY).getNukeFlashAmount(partialTick) * Minecraft.getInstance().options.screenEffectScale().get().floatValue();
        float f1 = Minecraft.getInstance().options.screenEffectScale().get().floatValue();
        if (f > 0 && AlexsCaves.CLIENT_CONFIG.nuclearBombFlash.get()) {
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f * f1);
            RenderSystem.setShaderTexture(0, ClientProxy.BOMB_FLASH);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }


    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V",
                    shift = At.Shift.BEFORE
            )
    )
    public void ac_renderLevel(float partialTicks, long time, PoseStack poseStack, CallbackInfo ci) {
        Entity player = Minecraft.getInstance().cameraEntity;
        if(player != null && player.isPassenger() && player.getVehicle() instanceof SubmarineEntity submarine && SubmarineRenderer.isFirstPersonFloodlightsMode(submarine)) {
            Vec3 offset = submarine.getPosition(partialTicks).subtract(player.getEyePosition(partialTicks));
            poseStack.pushPose();
            poseStack.translate(offset.x, offset.y, offset.z);
            SubmarineRenderer.renderSubFirstPerson(submarine, partialTicks, poseStack, renderBuffers.bufferSource());
            poseStack.popPose();
        }
    }
}
