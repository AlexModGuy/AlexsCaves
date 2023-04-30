package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.render.misc.CaveMapRenderer;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow
    private ItemStack offHandItem;
    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    protected abstract float calculateMapTilt(float partialTick);

    @Shadow
    protected abstract void renderMapHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, HumanoidArm arm);

    @Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float f1, float f2, HumanoidArm arm);

    @Shadow @Final private ItemRenderer itemRenderer;

    @Inject(
            method = {"Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_renderArmWithItem(AbstractClientPlayer scoping, float f1, float f2, InteractionHand hand, float f3, ItemStack stack, float f4, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, CallbackInfo ci) {
        if (!scoping.isScoping()) {
            boolean flag = hand == InteractionHand.MAIN_HAND;
            HumanoidArm humanoidarm = flag ? scoping.getMainArm() : scoping.getMainArm().getOpposite();
            if (stack.is(ACItemRegistry.CAVE_MAP.get()) && CaveMapItem.isFilled(stack)) {
                ci.cancel();
                poseStack.pushPose();
                if (flag && offHandItem.isEmpty()) {
                    this.renderTwoHandedCaveMap(poseStack, multiBufferSource, packedLight, f2, f4, f3);
                } else {
                    this.renderOneHandedCaveMap(poseStack, multiBufferSource, packedLight, f4, humanoidarm, f3, stack);
                }
                poseStack.popPose();
            }
        }
    }

    private void renderOneHandedCaveMap(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float animation1, HumanoidArm arm, float animation2, ItemStack stack) {
        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.translate(f * 0.125F, -0.125F, 0.0F);
        if (!Minecraft.getInstance().player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(f * 10.0F));
            this.renderPlayerArm(poseStack, bufferSource, packedLight, animation1, animation2, arm);
            poseStack.popPose();
        }

        poseStack.pushPose();
        poseStack.translate(f * 0.51F, -0.08F + animation1 * -1.2F, -0.75F);
        float f1 = Mth.sqrt(animation2);
        float f2 = Mth.sin(f1 * (float)Math.PI);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * Mth.sin(f1 * ((float)Math.PI * 2F));
        float f5 = -0.3F * Mth.sin(animation2 * (float)Math.PI);
        poseStack.translate(f * f3, f4 - 0.3F * f2, f5);
        poseStack.mulPose(Axis.XP.rotationDegrees(f2 * -45.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(f * f2 * -30.0F));
        this.renderCaveMap(poseStack, bufferSource, packedLight, stack);
        poseStack.popPose();
    }

    private void renderCaveMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int light, ItemStack map) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(0.38F, 0.38F, 0.38F);
        poseStack.translate(-0.5F, -0.5F, 0.0F);
        poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(CaveMapRenderer.MAP_BACKGROUND);
        Matrix4f matrix4f = poseStack.last().pose();
        vertexconsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
        vertexconsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
        CaveMapRenderer.getMapFor(map, true).render(poseStack, multiBufferSource, map, false, light);

    }

    private void renderTwoHandedCaveMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick, float animation1, float animation2) {
        float f = Mth.sqrt(animation2);
        float f1 = -0.2F * Mth.sin(animation2 * (float)Math.PI);
        float f2 = -0.4F * Mth.sin(f * (float)Math.PI);
        poseStack.translate(0.0F, -f1 / 2.0F, f2);
        float f3 = this.calculateMapTilt(partialTick);
        poseStack.translate(0.0F, 0.04F + animation1 * -1.2F + f3 * -0.5F, -0.72F);
        poseStack.mulPose(Axis.XP.rotationDegrees(f3 * -85.0F));
        if (!Minecraft.getInstance().player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            this.renderMapHand(poseStack, multiBufferSource, packedLight, HumanoidArm.RIGHT);
            this.renderMapHand(poseStack, multiBufferSource, packedLight, HumanoidArm.LEFT);
            poseStack.popPose();
        }

        float f4 = Mth.sin(f * (float)Math.PI);
        poseStack.mulPose(Axis.XP.rotationDegrees(f4 * 20.0F));
        poseStack.scale(2.0F, 2.0F, 2.0F);
        this.renderCaveMap(poseStack, multiBufferSource, packedLight, mainHandItem);
    }
}
