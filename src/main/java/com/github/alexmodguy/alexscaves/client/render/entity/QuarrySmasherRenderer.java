package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.BoundroidModel;
import com.github.alexmodguy.alexscaves.client.model.QuarrySmasherModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuarrySmasherRenderer extends EntityRenderer<QuarrySmasherEntity> {

    private static final QuarrySmasherModel QUARRY_SMASHER_MODEL = new QuarrySmasherModel();
    private static final BoundroidModel BOUNDROID_MODEL = new BoundroidModel();
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/quarry_smasher.png");
    private static final ResourceLocation TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/quarry_smasher_glow.png");
    private static final ResourceLocation TEXTURE_BOUNDROID = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/boundroid_quarry.png");
    private static final ResourceLocation TEXTURE_CHAIN = ResourceLocation.withDefaultNamespace("textures/block/chain.png");
    private static final Map<UUID, LightningRender> lightningRenderMap = new HashMap<>();
    private static final LightningBoltData.BoltRenderInfo LIGHTNING_BOLT_INFO = new LightningBoltData.BoltRenderInfo(0.0F, 0.01F, 0.3F, 0.6F, new Vector4f(0.71F, 0.76F, 0.95F, 0.3F), 0);


    public QuarrySmasherRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    public boolean shouldRender(QuarrySmasherEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            for (PartEntity part : entity.getParts()) {
                if (camera.isVisible(part.getBoundingBoxForCulling())) {
                    return true;
                }
            }
            return entity.lastMiningArea != null && camera.isVisible(entity.lastMiningArea);
        }
    }

    public void render(QuarrySmasherEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        float ageInTicks = entity.tickCount + partialTicks;
        float inactive = entity.getInactiveProgress(partialTicks);
        float headStill = entity.getHeadGroundProgress(partialTicks);

        poseStack.pushPose();
        poseStack.translate(0.0D, (double) 1.5F, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        QUARRY_SMASHER_MODEL.setupAnim(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
        VertexConsumer textureVertexConsumer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        QUARRY_SMASHER_MODEL.renderToBuffer(poseStack, textureVertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        VertexConsumer activeVertexConsumer = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
        QUARRY_SMASHER_MODEL.renderToBuffer(poseStack, activeVertexConsumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1F - inactive);
        poseStack.popPose();

        if (!entity.isInactive() && entity.lastMiningArea != null) {
            LightningRender lightningRender = getLightingRender(entity.getUUID());
            Vec3 position = entity.getPosition(partialTicks);

            poseStack.pushPose();
            poseStack.translate(-position.x, -position.y, -position.z);
            lightningRender.render(partialTicks, poseStack, bufferIn);
            poseStack.popPose();

            float armStickyOuty = 0.8F;
            Vec3 corner1 = new Vec3(entity.lastMiningArea.minX + 0.5F, entity.lastMiningArea.minY + 0.5F, entity.lastMiningArea.minZ + 0.5F);
            Vec3 corner2 = new Vec3(entity.lastMiningArea.maxX + 0.5F, entity.lastMiningArea.minY + 0.5F, entity.lastMiningArea.minZ + 0.5F);
            Vec3 corner3 = new Vec3(entity.lastMiningArea.minX + 0.5F, entity.lastMiningArea.minY + 0.5F, entity.lastMiningArea.maxZ + 0.5F);
            Vec3 corner4 = new Vec3(entity.lastMiningArea.maxX + 0.5F, entity.lastMiningArea.minY + 0.5F, entity.lastMiningArea.maxZ + 0.5F);

            int segCount = Mth.clamp((int) Math.ceil(0.3F * corner1.horizontalDistance()), 3, 30);
            LightningBoltData bolt1 = new LightningBoltData(LIGHTNING_BOLT_INFO, corner1, position.add(-0.7F, armStickyOuty, -0.7F), segCount)
                    .size(0.3F)
                    .lifespan(1)
                    .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
                    .fade(LightningBoltData.FadeFunction.NONE);
            LightningBoltData bolt2 = new LightningBoltData(LIGHTNING_BOLT_INFO, corner2, position.add(0.7F, armStickyOuty, -0.7F), segCount)
                    .size(0.3F)
                    .lifespan(1)
                    .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
                    .fade(LightningBoltData.FadeFunction.NONE);
            LightningBoltData bolt3 = new LightningBoltData(LIGHTNING_BOLT_INFO, corner3, position.add(-0.7F, armStickyOuty, 0.7F), segCount)
                    .size(0.3F)
                    .lifespan(1)
                    .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
                    .fade(LightningBoltData.FadeFunction.NONE);
            LightningBoltData bolt4 = new LightningBoltData(LIGHTNING_BOLT_INFO, corner4, position.add(0.7F, armStickyOuty, 0.7F), segCount)
                    .size(0.3F)
                    .lifespan(1)
                    .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
                    .fade(LightningBoltData.FadeFunction.NONE);
            if (!Minecraft.getInstance().isPaused()) {
                lightningRender.update(1, bolt1, partialTicks);
                lightningRender.update(2, bolt2, partialTicks);
                lightningRender.update(3, bolt3, partialTicks);
                lightningRender.update(4, bolt4, partialTicks);
            }
        }

        if (entity.headPart != null && entity.tickCount > 0) {
            Vec3 boundroidTop = entity.headPart.getPosition(partialTicks).subtract(entity.getPosition(partialTicks));

            poseStack.pushPose();
            poseStack.translate(boundroidTop.x, boundroidTop.y, boundroidTop.z);
            poseStack.translate(0.0D, (double) 1.5F, 0.0D);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            BOUNDROID_MODEL.animateForQuarry(ageInTicks, Math.max(headStill, inactive));
            VertexConsumer boundroidVertexConsumer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_BOUNDROID));
            BOUNDROID_MODEL.renderToBuffer(poseStack, boundroidVertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();

            Vec3 chainOffset = new Vec3(0, -0.75F, 0);
            Vec3 chainTo = entity.getPosition(partialTicks).add(chainOffset).subtract(entity.headPart.getPosition(partialTicks).add(0, -0.25F, 0));
            VertexConsumer chainBuffer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_CHAIN));
            poseStack.pushPose();
            poseStack.translate(boundroidTop.x - chainOffset.x, boundroidTop.y - chainOffset.y, boundroidTop.z - chainOffset.z);
            BoundroidWinchRenderer.renderChain(chainTo, poseStack, chainBuffer, packedLightIn, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }


        if (entity.isRemoved() && lightningRenderMap.containsKey(entity.getUUID())) {
            lightningRenderMap.remove(entity.getUUID());
        }

        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }


    private LightningRender getLightingRender(UUID uuid) {
        if (lightningRenderMap.get(uuid) == null) {
            lightningRenderMap.put(uuid, new LightningRender());
        }
        return lightningRenderMap.get(uuid);
    }

    public ResourceLocation getTextureLocation(QuarrySmasherEntity entity) {
        return TEXTURE;
    }
}
