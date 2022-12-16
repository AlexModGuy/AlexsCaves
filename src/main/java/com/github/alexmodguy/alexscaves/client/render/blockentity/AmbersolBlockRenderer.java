package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.block.blockentity.AmbersolBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.*;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class AmbersolBlockRenderer<T extends AmbersolBlockEntity> implements BlockEntityRenderer<T> {

    private static final Map<BlockPos, AmbersolBlockEntity> allOnScreen = new HashMap<>();
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);
    private static final int SHINE_R = 215;
    private static final int SHINE_G = 89;
    private static final int SHINE_B = 32;

    private static final int SHINE_CENTER_R = 255;
    private static final int SHINE_CENTER_G = 254;
    private static final int SHINE_CENTER_B = 233;


    public AmbersolBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    public static void renderEntireBatch(LevelRenderer levelRenderer, PoseStack poseStack, int renderTick, Camera camera, float partialTick) {
        if (!allOnScreen.isEmpty()) {
            List<BlockPos> sortedPoses = new ArrayList<BlockPos>(allOnScreen.keySet());
            Collections.sort(sortedPoses, (blockPos1, blockPos2) -> sortBlockPos(camera, blockPos1, blockPos2));
            poseStack.pushPose();
            Vec3 cameraPos = camera.getPosition();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            for (BlockPos pos : sortedPoses) {
                Vec3 blockAt = Vec3.atCenterOf(pos);
                poseStack.pushPose();
                poseStack.translate(blockAt.x, blockAt.y, blockAt.z);
                renderAt(allOnScreen.get(pos), partialTick, poseStack, multibuffersource$buffersource);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
        allOnScreen.clear();
    }

    private static int sortBlockPos(Camera camera, BlockPos blockPos1, BlockPos blockPos2) {
        double d1 = camera.getPosition().distanceTo(Vec3.atCenterOf(blockPos1));
        double d2 = camera.getPosition().distanceTo(Vec3.atCenterOf(blockPos2));
        return Double.compare(d2, d1);
    }

    @Override
    public void render(T ambersol, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (!ambersol.isRemoved()) {
            allOnScreen.put(ambersol.getBlockPos(), ambersol);
        } else {
            allOnScreen.remove(ambersol.getBlockPos());
        }

    }

    private static void renderAt(AmbersolBlockEntity ambersol, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn) {
        float scale = 1.0F;
        float time = 0;
        if (Minecraft.getInstance().getCameraEntity() != null) {
            scale = ambersol.calculateShineScale(Minecraft.getInstance().getCameraEntity().getPosition(partialTicks));
            time = Minecraft.getInstance().getCameraEntity().tickCount + partialTicks;
        }
        if (scale > 0.0F) {
            Quaternionf camera = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
            float time1 = time * ambersol.getRotSpeed() * 0.3F;
            float time2 = time * 0.1F;
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(camera);
            VertexConsumer lightConsumer = bufferIn.getBuffer(ACRenderTypes.getAmbersolShine());
            int lights = ambersol.getLights();
            matrixStackIn.mulPose(Axis.ZN.rotationDegrees(ambersol.getRotOffset()));
            for (int i = 0; i < lights; i++) {
                float length = (float) (3F + Math.sin(time2 + i * 2)) * scale;
                float width = (float) (1F - 0.2F * Math.abs(Math.cos(time2 - i * Math.PI * 0.5F))) * scale;
                int j = 255;
                float u = 0;
                float v = 0;
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.ZN.rotationDegrees(time1 - (i / (float) lights * 360)));
                PoseStack.Pose posestack$pose = matrixStackIn.last();
                Matrix4f matrix4f = posestack$pose.pose();
                Matrix3f matrix3f = posestack$pose.normal();
                shineOriginVertex(lightConsumer, matrix4f, matrix3f, j, u, v);
                shineLeftCornerVertex(lightConsumer, matrix4f, matrix3f, length, width, u, v);
                shineRightCornerVertex(lightConsumer, matrix4f, matrix3f, length, width, u, v);
                shineLeftCornerVertex(lightConsumer, matrix4f, matrix3f, length, width, u, v);
                matrixStackIn.popPose();
            }
            //minecrafts janky render system wont do transparent blocks unless you render a selection box behind it.
            PoseStack.Pose posestack$pose = matrixStackIn.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            VertexConsumer lines = bufferIn.getBuffer(RenderType.lines());
            matrixStackIn.popPose();
        }
    }

    private static void shineOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, Matrix3f p_114092_, int p_114222_, float xOffset, float yOffset) {
        p_114220_.vertex(p_114221_, 0.0F, 0.0F, 0.0F).color(SHINE_CENTER_R, SHINE_CENTER_G, SHINE_CENTER_B, 230).uv(xOffset + 0.5F, yOffset).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, Matrix3f p_114092_, float p_114217_, float p_114218_, float xOffset, float yOffset) {
        p_114215_.vertex(p_114216_, -HALF_SQRT_3 * p_114218_, p_114217_, 0).color(SHINE_R, SHINE_G, SHINE_B, 0).uv(xOffset, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, Matrix3f p_114092_, float p_114226_, float p_114227_, float xOffset, float yOffset) {
        p_114224_.vertex(p_114225_, HALF_SQRT_3 * p_114227_, p_114226_, 0).color(SHINE_R, SHINE_G, SHINE_B, 0).uv(xOffset + 1, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    public int getViewDistance() {
        return 256;
    }
}
