package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.ForsakenModel;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ForsakenHeldMobLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.ForsakenEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class ForsakenRenderer extends MobRenderer<ForsakenEntity, ForsakenModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/forsaken.png");
    private static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/forsaken_eyes.png");

    private static final ResourceLocation TEXTURE_DARKNESS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/forsaken_darkness.png");

    private static final HashMap<Integer, Vec3> mouthParticlePositions = new HashMap<>();

    public ForsakenRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ForsakenModel(), 1.15F);
        this.addLayer(new LayerGlow());
        this.addLayer(new ForsakenHeldMobLayer(this));
    }

    public static void renderEntireBatch(LevelRenderer levelRenderer, PoseStack poseStack, int renderTick, Camera camera, float partialTick) {
        mouthParticlePositions.clear();
    }

    public void render(ForsakenEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, source, packedLight);
        mouthParticlePositions.put(entity.getId(), this.model.getMouthPosition(Vec3.ZERO));
    }

    public static Vec3 getMouthPositionFor(int entityId) {
        return mouthParticlePositions.get(entityId);
    }

    public ResourceLocation getTextureLocation(ForsakenEntity entity) {
        return TEXTURE;
    }

    class LayerGlow extends RenderLayer<ForsakenEntity, ForsakenModel> {

        public LayerGlow() {
            super(ForsakenRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ForsakenEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer darnkessConsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURE_DARKNESS));
            this.getParentModel().renderToBuffer(matrixStackIn, darnkessConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, entitylivingbaseIn.getDarknessAmount(partialTicks));
            VertexConsumer eyesConsumer = bufferIn.getBuffer(RenderType.eyes(TEXTURE_EYES));
            this.getParentModel().renderToBuffer(matrixStackIn, eyesConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}


