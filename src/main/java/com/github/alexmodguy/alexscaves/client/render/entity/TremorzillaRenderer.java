package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.TremorzillaBeamModel;
import com.github.alexmodguy.alexscaves.client.model.TremorzillaModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.TremorzillaRiderLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TremorzillaRenderer extends MobRenderer<TremorzillaEntity, TremorzillaModel> implements CustomBookEntityRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla.png");
    private static final ResourceLocation TEXTURE_RETRO = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_retro.png");
    private static final ResourceLocation TEXTURE_TECTONIC = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_tectonic.png");
    private static final ResourceLocation TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_glow.png");
    private static final ResourceLocation TEXTURE_RETRO_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_retro_glow.png");
    private static final ResourceLocation TEXTURE_TECTONIC_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_tectonic_glow.png");
    private static final ResourceLocation TEXTURE_GLOW_POWERED = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_glow_powered.png");
    private static final ResourceLocation TEXTURE_RETRO_GLOW_POWERED = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_retro_glow_powered.png");
    private static final ResourceLocation TEXTURE_TECTONIC_GLOW_POWERED = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_tectonic_glow_powered.png");
    private static final ResourceLocation TEXTURE_BEAM_INNER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_beam_inner.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_INNER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_retro_beam_inner.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_INNER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_tectonic_beam_inner.png");
    private static final ResourceLocation TEXTURE_BEAM_OUTER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_beam_outer.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_OUTER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_retro_beam_outer.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_OUTER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_tectonic_beam_outer.png");
    private static final ResourceLocation TEXTURE_BEAM_END_0 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_beam_end_0.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_END_0 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_retro_beam_end_0.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_END_0 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_tectonic_beam_end_0.png");
    private static final ResourceLocation TEXTURE_BEAM_END_1 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_beam_end_1.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_END_1 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_retro_beam_end_1.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_END_1 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_tectonic_beam_end_1.png");
    private static final ResourceLocation TEXTURE_BEAM_END_2 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_beam_end_2.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_END_2 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_retro_beam_end_2.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_END_2 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tremorzilla/tremorzilla_tectonic_beam_end_2.png");
    private static final HashMap<Integer, Vec3> mouthParticlePositions = new HashMap<>();
    private static final Vec3 MOUTH_TRANSFORM_POS = new Vec3(0, 1F, -1F);
    private static final TremorzillaBeamModel BEAM_END_MODEL = new TremorzillaBeamModel();
    private boolean sepia;
    public TremorzillaRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new TremorzillaModel(), 4.0F);
        this.addLayer(new LayerGlow());
        this.addLayer(new TremorzillaRiderLayer(this));
    }

    protected void scale(TremorzillaEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(TremorzillaEntity entity) {
        return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC : entity.getAltSkin() == 1 ? TEXTURE_RETRO : TEXTURE;
    }

    public void render(TremorzillaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight) {
        this.model.straighten = sepia;
        this.shadowRadius = 4.0F * (float)entity.getScale();
        super.render(entity, entityYaw, partialTicks, poseStack, source, packedLight);
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float beamProgress = entity.getBeamProgress(partialTicks);
        Vec3 beamEndVec = entity.getClientBeamEndPosition(partialTicks);
        if (beamProgress > 0.0F && entity.isAlive() && beamEndVec != null) {
            Vec3 modelOffset = getModel().getMouthPosition(new Vec3(0, 0.1F, 0F)).yRot((float) (Math.PI - bodyYaw * ((float) Math.PI / 180F)));
            float ageInTicks = entity.tickCount + partialTicks;
            float shakeByX = (float) Math.sin(ageInTicks * 4F) * 0.075F;
            float shakeByY = (float) Math.sin(ageInTicks * 4F + 1.2F) * 0.075F;
            float shakeByZ = (float) Math.sin(ageInTicks * 4F + 2.4F) * 0.075F;
            Vec3 rawBeamPosition = beamEndVec.subtract(entity.getPosition(partialTicks).add(modelOffset));
            float length = (float) rawBeamPosition.length();
            Vec3 vec3 = rawBeamPosition.normalize();
            float xRot = (float) Math.acos(vec3.y);
            float yRot = (float) Math.atan2(vec3.z, vec3.x);
            float width = beamProgress * 1.5F;
            poseStack.pushPose();
            poseStack.translate(modelOffset.x + shakeByX, modelOffset.y + shakeByY, modelOffset.z + shakeByZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(((Mth.PI / 2F) - yRot) * Mth.RAD_TO_DEG));
            poseStack.mulPose(Axis.XP.rotationDegrees((-(Mth.PI / 2F) + xRot) * Mth.RAD_TO_DEG));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            renderBeam(entity, poseStack, source, partialTicks, width, length, true, false);
            if(AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()){
                renderBeam(entity, poseStack, source, partialTicks, width, length, true, true);
            }
            renderBeam(entity, poseStack, source, partialTicks, width, length, false, false);
            poseStack.popPose();
        }
        mouthParticlePositions.put(entity.getId(), this.model.getMouthPosition(MOUTH_TRANSFORM_POS));
    }

    public static Vec3 getMouthPositionFor(int entityId) {
        return mouthParticlePositions.get(entityId);
    }

    @Nullable
    protected RenderType getRenderType(TremorzillaEntity mob, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(mob);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return sepia ? ACRenderTypes.getBookWidget(resourcelocation, true) : RenderType.entityTranslucent(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    private void renderBeam(TremorzillaEntity entity, PoseStack poseStack, MultiBufferSource source, float partialTicks, float width, float length, boolean inner, boolean glowSecondPass) {
        poseStack.pushPose();
        int vertices;
        VertexConsumer vertexconsumer;
        float speed;
        float startAlpha = 1.0F;
        float endAlpha = 1.0F;
        if (inner) {
            vertices = 4;
            ResourceLocation resourceLocation = entity.getAltSkin() == 2 ? TEXTURE_TECTONIC_BEAM_INNER : entity.getAltSkin() == 1 ? TEXTURE_RETRO_BEAM_INNER : TEXTURE_BEAM_INNER;
            if (AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get() && glowSecondPass) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                vertexconsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(resourceLocation, true));
                endAlpha = 0.5F;
            } else {
                vertexconsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(resourceLocation, false));
            }
            speed = 0.5F;
        } else {
            vertices = 8;
            ResourceLocation resourceLocation = entity.getAltSkin() == 2 ? TEXTURE_TECTONIC_BEAM_OUTER : entity.getAltSkin() == 1 ? TEXTURE_RETRO_BEAM_OUTER : TEXTURE_BEAM_OUTER;
            if (AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                vertexconsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(resourceLocation, true));
            } else {
                vertexconsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(resourceLocation, false));
            }
            width += 0.25F;
            speed = 1F;
            endAlpha = 0.0F;
        }

        float v = ((float) entity.tickCount + partialTicks) * -0.25F * speed;
        float v1 = v + length * (inner ? 0.5F : 0.15F);
        float f4 = -width;
        float f5 = 0;
        float f6 = 0.0F;
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        for (int j = 0; j <= vertices; ++j) {
            Matrix3f matrix3f = posestack$pose.normal();
            float f7 = Mth.cos((float) Math.PI + (float) j * ((float) Math.PI * 2F) / (float) vertices) * width;
            float f8 = Mth.sin((float) Math.PI + (float) j * ((float) Math.PI * 2F) / (float) vertices) * width;
            float f9 = (float) j + 1;
            vertexconsumer.vertex(matrix4f, f4 * 0.55F, f5 * 0.55F, 0.0F).color(1.0F, 1.0F, 1.0F, startAlpha).uv(f6, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, f4, f5, length).color(1.0F, 1.0F, 1.0F, endAlpha).uv(f6, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, f7, f8, length).color(1.0F, 1.0F, 1.0F, endAlpha).uv(f9, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, f7 * 0.55F, f8 * 0.55F, 0.0F).color(1.0F, 1.0F, 1.0F, startAlpha).uv(f9, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }
        if(inner){
            VertexConsumer endVertexConsumer;
            if (AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get() && glowSecondPass) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                endVertexConsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(getEndBeamTexture(entity), true));
            } else {
                endVertexConsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(getEndBeamTexture(entity), false));
            }
            poseStack.pushPose();
            poseStack.translate(0, 0, length - 1.5F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45));
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(width, width, width);
            BEAM_END_MODEL.resetToDefaultPose();
            BEAM_END_MODEL.renderToBuffer(poseStack, endVertexConsumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private ResourceLocation getEndBeamTexture(TremorzillaEntity entity) {
        int time = entity.tickCount / 2 % 3;
        switch (time){
            case 0:
                return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC_BEAM_END_0 : entity.getAltSkin() == 1 ? TEXTURE_RETRO_BEAM_END_0 : TEXTURE_BEAM_END_0;
            case 1:
                return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC_BEAM_END_1 : entity.getAltSkin() == 1 ? TEXTURE_RETRO_BEAM_END_1 : TEXTURE_BEAM_END_1;
            case 2:
                return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC_BEAM_END_2 : entity.getAltSkin() == 1 ? TEXTURE_RETRO_BEAM_END_2 : TEXTURE_BEAM_END_2;
        }
        return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC_BEAM_END_0 : entity.getAltSkin() == 1 ? TEXTURE_RETRO_BEAM_END_0 : TEXTURE_BEAM_END_0;
    }

    public boolean shouldRender(TremorzillaEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            for (PartEntity part : entity.getParts()) {
                if (camera.isVisible(part.getBoundingBoxForCulling())) {
                    return true;
                }
            }
            if(entity.isFiring()){
                Vec3 endBeam = entity.getClientBeamEndPosition(1.0F);
                if (endBeam != null) {
                    Vec3 vec3 = entity.getBeamShootFrom(1.0F);
                    return camera.isVisible(new AABB(endBeam.x, endBeam.y, endBeam.z, vec3.x, vec3.y, vec3.z));
                }
            }
            return false;
        }
    }

    @Override
    public void setSepiaFlag(boolean sepiaFlag) {
        this.sepia = sepiaFlag;
    }

    class LayerGlow extends RenderLayer<TremorzillaEntity, TremorzillaModel> {

        public LayerGlow() {
            super(TremorzillaRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, TremorzillaEntity tremorzilla, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float normalAlpha = ((float) (Math.sin(ageInTicks * 0.2F)) * 0.15F + 0.5F);
            float spikeDownAmount = tremorzilla.getClientSpikeDownAmount(partialTicks);
            VertexConsumer normalGlowConsumer = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(tremorzilla.isPowered() ? tremorzilla.getAltSkin() == 2 ? TEXTURE_TECTONIC_GLOW_POWERED : tremorzilla.getAltSkin() == 1 ? TEXTURE_RETRO_GLOW_POWERED : TEXTURE_GLOW_POWERED : tremorzilla.getAltSkin() == 2 ? TEXTURE_TECTONIC_GLOW : tremorzilla.getAltSkin() == 1 ? TEXTURE_RETRO_GLOW : TEXTURE_GLOW));
            this.getParentModel().renderToBuffer(matrixStackIn, normalGlowConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(tremorzilla, 0.0F), 1.0F, 1.0F, 1.0F, normalAlpha);
            if (spikeDownAmount > 0) {
                VertexConsumer spikeGlowConsumer;
                if (AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
                    PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                    spikeGlowConsumer = bufferIn.getBuffer(ACRenderTypes.getTremorzillaBeam(tremorzilla.getAltSkin() == 2 ? TEXTURE_TECTONIC_GLOW_POWERED : tremorzilla.getAltSkin() == 1 ? TEXTURE_RETRO_GLOW_POWERED : TEXTURE_GLOW_POWERED, true));
                } else {
                    spikeGlowConsumer = normalGlowConsumer;
                }
                this.getParentModel().showSpikesBasedOnProgress(spikeDownAmount, 0.0F);
                this.getParentModel().renderToBuffer(matrixStackIn, spikeGlowConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(tremorzilla, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                this.getParentModel().showAllSpikes();
            }
        }
    }
}

