package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.entity.SubmarineRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ACPotionEffectLayer;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    private float darkenWorldAmount;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    public abstract void resetProjectionMatrix(Matrix4f p_253668_);

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
        ((ClientProxy) AlexsCaves.PROXY).preScreenRender(partialTick);
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
        if (player != null && player.isPassenger() && player.getVehicle() instanceof SubmarineEntity submarine && SubmarineRenderer.isFirstPersonFloodlightsMode(submarine)) {
            Vec3 offset = submarine.getPosition(partialTicks).subtract(player.getEyePosition(partialTicks));
            poseStack.pushPose();
            poseStack.translate(offset.x, offset.y, offset.z);
            SubmarineRenderer.renderSubFirstPerson(submarine, partialTicks, poseStack, renderBuffers.bufferSource());
            poseStack.popPose();
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V",
                    shift = At.Shift.AFTER
            )
    )
    public void ac_renderLevelAfterHand(float partialTicks, long time, PoseStack poseStack, CallbackInfo ci) {
        if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living && living.hasEffect(ACEffectRegistry.BUBBLED.get()) && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            ACPotionEffectLayer.renderBubbledFirstPerson(poseStack);
            multibuffersource$buffersource.endBatch();
        }
    }
}
