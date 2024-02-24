package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.block.blockentity.HologramProjectorBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.*;

import static com.github.alexmodguy.alexscaves.client.render.entity.NotorRenderer.renderEntityInHologram;

public class HologramProjectorBlockRenderer<T extends HologramProjectorBlockEntity> implements BlockEntityRenderer<T> {

    private static final Map<BlockPos, HologramProjectorBlockEntity> allOnScreen = new HashMap<>();
    private static final Map<UUID, PlayerInfo> playerInfo = new HashMap<>();

    private static PlayerModel playerModel = null;
    private static PlayerModel slimPlayerModel = null;

    public HologramProjectorBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    public static void renderEntireBatch(LevelRenderer levelRenderer, PoseStack poseStack, int renderTick, Camera camera, float partialTick) {
        if (!allOnScreen.isEmpty()) {
            List<BlockPos> sortedPoses = new ArrayList<BlockPos>(allOnScreen.keySet());
            Collections.sort(sortedPoses, (blockPos1, blockPos2) -> sortBlockPos(camera, blockPos1, blockPos2));
            poseStack.pushPose();
            Vec3 cameraPos = camera.getPosition();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            for (BlockPos pos : sortedPoses) {
                MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
                Vec3 blockAt = Vec3.atCenterOf(pos);
                poseStack.pushPose();
                poseStack.translate(blockAt.x, blockAt.y, blockAt.z);
                renderAt(allOnScreen.get(pos), partialTick, poseStack, multibuffersource$buffersource);
                poseStack.popPose();
                multibuffersource$buffersource.endBatch();
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
    public void render(T hologram, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (!hologram.isRemoved()) {
            allOnScreen.put(hologram.getBlockPos(), hologram);
        } else {
            allOnScreen.remove(hologram.getBlockPos());
        }
    }

    private static void renderAt(HologramProjectorBlockEntity projectorBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn) {
        PostEffectRegistry.renderEffectForNextTick(ClientProxy.HOLOGRAM_SHADER);
        Entity holoEntity = projectorBlockEntity.getDisplayEntity(Minecraft.getInstance().level);
        float amount = projectorBlockEntity.getSwitchAmount(partialTicks);
        float ticks = projectorBlockEntity.tickCount + partialTicks;
        float bob1 = (float) (Math.sin(ticks * 0.05F + amount) * 0.1F);
        float bob2 = (float) (Math.cos(ticks * 0.05F + amount) * 0.1F);
        float length = (1F + bob1) * amount;
        float width = ((holoEntity == null ? 0.8F : holoEntity.getBbWidth()) + bob2) * amount;
        if(holoEntity instanceof LivingEntity living){
            width *= living.getScale();
        }
        VertexConsumer lightConsumer = bufferIn.getBuffer(ACRenderTypes.getHologramLights());
        poseStack.pushPose();
        float padStart = 0.125F;
        float padEnd = 1.0F - padStart;
        poseStack.pushPose();
        poseStack.translate(-0.5F, -0.235F, -0.5F);
        float cameraY = Minecraft.getInstance().getEntityRenderDispatcher().camera.getYRot();
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f1 = posestack$pose.pose();
        Matrix3f matrix3f1 = posestack$pose.normal();
        lightConsumer.vertex(matrix4f1, padStart, 0.0F, padEnd).color(220, 220, 255, (int) (amount * 150)).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f1, 0.0F, 1.0F, 0.0F).endVertex();
        lightConsumer.vertex(matrix4f1, padEnd, 0.0F, padEnd).color(220, 220, 255, (int) (amount * 150)).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f1, 0.0F, 1.0F, 0.0F).endVertex();
        lightConsumer.vertex(matrix4f1, padEnd, 0.0F, padStart).color(220, 220, 255, (int) (amount * 150)).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f1, 0.0F, 1.0F, 0.0F).endVertex();
        lightConsumer.vertex(matrix4f1, padStart, 0.0F, padStart).color(220, 220, 255, (int) (amount * 150)).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f1, 0.0F, 1.0F, 0.0F).endVertex();
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.translate(0F, -0.2F, 0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - cameraY));
        PoseStack.Pose posestack$pose1 = poseStack.last();
        Matrix4f matrix4f2 = posestack$pose1.pose();
        Matrix3f matrix3f2 = posestack$pose1.normal();
        shineOriginVertex(lightConsumer, matrix4f2, matrix3f2, 0, 0);
        shineLeftCornerVertex(lightConsumer, matrix4f2, matrix3f2, length, width, 0, 0);
        shineRightCornerVertex(lightConsumer, matrix4f2, matrix3f2, length, width, 0, 0);
        shineLeftCornerVertex(lightConsumer, matrix4f2, matrix3f2, length, width, 0, 0);
        if (projectorBlockEntity.isPlayerRender()) {
            poseStack.pushPose();
            poseStack.scale(1, amount, 1);
            poseStack.translate(0, length + 1.5F, 0);
            poseStack.mulPose(Axis.YN.rotationDegrees(180 - cameraY + projectorBlockEntity.getRotation(partialTicks)));
            renderPlayerHologram(projectorBlockEntity.getLastPlayerUUID(), partialTicks, poseStack, bufferIn, 240);
            poseStack.popPose();
        } else if (holoEntity != null) {
            poseStack.pushPose();
            poseStack.scale(1, amount, 1);
            poseStack.translate(0, length + 1.5F, 0);
            poseStack.mulPose(Axis.YN.rotationDegrees(180 - cameraY + projectorBlockEntity.getRotation(partialTicks)));
            renderEntityInHologram(holoEntity, 0, 0, 0, 0, partialTicks, poseStack, bufferIn, 240);
            poseStack.popPose();
        }
        poseStack.popPose();
        poseStack.popPose();

    }

    private static PlayerInfo getPlayerInfo(UUID uuid) {
        if (!playerInfo.containsKey(uuid)) {
            playerInfo.put(uuid, Minecraft.getInstance().getConnection().getPlayerInfo(uuid));
        }
        return playerInfo.get(uuid);
    }

    private static String getPlayerModelName(PlayerInfo playerInfo, UUID uuid) {
        return playerInfo == null ? DefaultPlayerSkin.getSkinModelName(uuid) : playerInfo.getModelName();
    }


    private static ResourceLocation getPlayerSkinTextureLocation(PlayerInfo playerInfo, UUID uuid) {
        return playerInfo == null ? DefaultPlayerSkin.getDefaultSkin(uuid) : playerInfo.getSkinLocation();
    }

    private static void renderPlayerHologram(UUID lastPlayerUUID, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int i) {
        PostEffectRegistry.renderEffectForNextTick(ClientProxy.HOLOGRAM_SHADER);
        PlayerInfo playerInfo = getPlayerInfo(lastPlayerUUID);
        String modelName = getPlayerModelName(playerInfo, lastPlayerUUID);
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? extends Player> renderer = manager.getSkinMap().get(modelName);
        if(playerModel == null || slimPlayerModel == null){
            playerModel = new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
            slimPlayerModel = new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true);
        }
        PlayerModel model = modelName.equals("slim") ? slimPlayerModel : playerModel;
        model.young = false;
        if (renderer instanceof LivingEntityRenderer livingEntityRenderer) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(ACRenderTypes.getHologram(getPlayerSkinTextureLocation(playerInfo, lastPlayerUUID)));
            poseStack.pushPose();
            poseStack.scale(-1F, -1F, 1F);
            model.renderToBuffer(poseStack, ivertexbuilder, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }


    private static void shineOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, Matrix3f p_114092_, float xOffset, float yOffset) {
        p_114220_.vertex(p_114221_, 0.0F, 0.0F, 0.0F).color(255, 255, 255, 230).uv(xOffset + 0.5F, yOffset).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, Matrix3f p_114092_, float p_114217_, float p_114218_, float xOffset, float yOffset) {
        p_114215_.vertex(p_114216_, -ACMath.HALF_SQRT_3 * p_114218_, p_114217_, 0).color(0, 0, 255, 0).uv(xOffset, yOffset + 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, Matrix3f p_114092_, float p_114226_, float p_114227_, float xOffset, float yOffset) {
        p_114224_.vertex(p_114225_, ACMath.HALF_SQRT_3 * p_114227_, p_114226_, 0).color(0, 0, 255, 0).uv(xOffset + 1, yOffset + 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    public int getViewDistance() {
        return 128;
    }
}
