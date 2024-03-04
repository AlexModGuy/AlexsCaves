package com.github.alexmodguy.alexscaves.client.render.misc;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class CaveMapRenderHelper {

    public static void renderOneHandedCaveMap(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float animation1, HumanoidArm arm, float animation2, ItemStack caveMapItem) {
        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float scale = 2.0F;
        poseStack.translate(f * 0.125F, -0.125F, 0.0F);
        if (!Minecraft.getInstance().player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(f * 10.0F));
            poseStack.translate(0F, 0F, 0.5F);
            poseStack.scale(scale, scale, scale);
            renderPlayerArm(poseStack, bufferSource, packedLight, animation1, animation2, arm);
            poseStack.popPose();
        }
        poseStack.pushPose();
        poseStack.translate(f * 0.51F, -0.08F + animation1 * -1.2F, -0.75F);
        float f1 = Mth.sqrt(animation2);
        float f2 = Mth.sin(f1 * (float) Math.PI);
        float f3 = 0.25F;
        float f4 = 0.4F * Mth.sin(f1 * ((float) Math.PI * 2F));
        float f5 = -0.3F * Mth.sin(animation2 * (float) Math.PI);
        poseStack.translate(f * f3, f4 - 0.3F * f2, f5);
        poseStack.mulPose(Axis.XP.rotationDegrees(f2 * -45.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(f * f2 * -30.0F));
        poseStack.scale(scale, scale, scale);
        renderCaveMap(poseStack, bufferSource, packedLight, caveMapItem, false);
        poseStack.popPose();
    }

    public static void renderCaveMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int light, ItemStack caveMapItem, boolean showBackground) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(0.38F, 0.38F, 0.38F);
        poseStack.translate(-0.5F, -0.5F, 0.0F);
        poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(ACRenderTypes.getCaveMapBackground(CaveMapRenderer.MAP_BACKGROUND, showBackground));
        Matrix4f matrix4f = poseStack.last().pose();
        vertexconsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
        vertexconsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
        CaveMapRenderer.getMapFor(caveMapItem, true).render(poseStack, multiBufferSource, caveMapItem, false, light);

    }

    public static void renderTwoHandedCaveMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick, float animation1, float animation2, ItemStack caveMapItem) {
        float f = Mth.sqrt(animation2);
        float f1 = -0.2F * Mth.sin(animation2 * (float) Math.PI);
        float f2 = -0.4F * Mth.sin(f * (float) Math.PI);
        float xRot = Mth.lerp(partialTick, Minecraft.getInstance().player.xRotO, Minecraft.getInstance().player.getXRot());
        poseStack.translate(0.0F, -f1 / 2.0F, f2);
        float f3 = calculateMapTilt(xRot);
        poseStack.translate(0.0F, 0.04F + animation1 * -1.2F + f3 * -0.5F, -0.72F);
        poseStack.mulPose(Axis.XP.rotationDegrees(f3 * -85.0F));
        if (!Minecraft.getInstance().player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            renderMapHand(poseStack, multiBufferSource, packedLight, HumanoidArm.RIGHT);
            renderMapHand(poseStack, multiBufferSource, packedLight, HumanoidArm.LEFT);
            poseStack.popPose();
        }

        float f4 = Mth.sin(f * (float) Math.PI);
        poseStack.mulPose(Axis.XP.rotationDegrees(f4 * 20.0F));
        poseStack.scale(2.0F, 2.0F, 2.0F);
        renderCaveMap(poseStack, multiBufferSource, packedLight, caveMapItem, false);
    }

    private static void renderMapHand(PoseStack poseStack, MultiBufferSource bufferSource, int i, HumanoidArm humanoidArm) {
        RenderSystem.setShaderTexture(0, Minecraft.getInstance().player.getSkinTextureLocation());
        PlayerRenderer playerrenderer = (PlayerRenderer)Minecraft.getInstance().getEntityRenderDispatcher().<AbstractClientPlayer>getRenderer(Minecraft.getInstance().player);
        poseStack.pushPose();
        float f = humanoidArm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(92.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * -41.0F));
        poseStack.translate(f * 0.3F, -1.1F, 0.45F);
        if (humanoidArm == HumanoidArm.RIGHT) {
            playerrenderer.renderRightHand(poseStack, bufferSource, i, Minecraft.getInstance().player);
        } else {
            playerrenderer.renderLeftHand(poseStack, bufferSource, i, Minecraft.getInstance().player);
        }

        poseStack.popPose();
    }

    private static void renderPlayerArm(PoseStack poseStack, MultiBufferSource bufferSource, int i, float animation1, float animation2, HumanoidArm humanoidArm) {
        boolean flag = humanoidArm != HumanoidArm.LEFT;
        float f = flag ? 1.0F : -1.0F;
        float f1 = Mth.sqrt(animation2);
        float f2 = -0.3F * Mth.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * Mth.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * Mth.sin(animation2 * (float)Math.PI);
        poseStack.translate(f * (f2 + 0.64000005F), f3 + -0.6F + animation1 * -0.6F, f4 + -0.71999997F);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 45.0F));
        float f5 = Mth.sin(animation2 * animation2 * (float)Math.PI);
        float f6 = Mth.sin(f1 * (float)Math.PI);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * f6 * 70.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * f5 * -20.0F));
        AbstractClientPlayer abstractclientplayer = Minecraft.getInstance().player;
        RenderSystem.setShaderTexture(0, abstractclientplayer.getSkinTextureLocation());
        poseStack.translate(f * -1.0F, 3.6F, 3.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * 120.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(200.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(f * -135.0F));
        poseStack.translate(f * 5.6F, 0.0F, 0.0F);
        PlayerRenderer playerrenderer = (PlayerRenderer)Minecraft.getInstance().getEntityRenderDispatcher().<AbstractClientPlayer>getRenderer(abstractclientplayer);
        if (flag) {
            playerrenderer.renderRightHand(poseStack, bufferSource, i, abstractclientplayer);
        } else {
            playerrenderer.renderLeftHand(poseStack, bufferSource, i, abstractclientplayer);
        }

    }

    private static float calculateMapTilt(float pitch) {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        return -Mth.cos(f * (float)Math.PI) * 0.5F + 0.5F;
    }

}
