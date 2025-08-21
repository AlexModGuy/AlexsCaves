package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.CorrodentModel;
import com.github.alexmodguy.alexscaves.server.entity.living.CorrodentEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class CorrodentRenderer extends MobRenderer<CorrodentEntity, CorrodentModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/corrodent.png");
    private static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/corrodent_eyes.png");
    private static final Map<BlockPos, Integer> allDugBlocksOnScreen = new HashMap<>();

    public CorrodentRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CorrodentModel(), 0.5F);
        this.addLayer(new LayerGlow());
    }

    public ResourceLocation getTextureLocation(CorrodentEntity entity) {
        return TEXTURE;
    }

    public void render(CorrodentEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        double x = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
        double y = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
        double z = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
        float digAmount = entityIn.getDigAmount(partialTicks);
        if (digAmount > 0) {
            double digEffectDistance = 3;
            for (BlockPos mutableBlockPos : BlockPos.betweenClosed((int) Math.floor(x - digEffectDistance), (int) Math.floor(y - digEffectDistance), (int) Math.floor(z - digEffectDistance), (int) Math.floor(x + digEffectDistance), (int) Math.floor(y + digEffectDistance), (int) Math.floor(z + digEffectDistance))) {
                int amount = (int) (entityIn.getCorrosionAmount(mutableBlockPos) * digAmount);
                if (amount >= 0) {
                    allDugBlocksOnScreen.put(mutableBlockPos.immutable(), Math.max(allDugBlocksOnScreen.getOrDefault(mutableBlockPos, -1), amount));
                }
            }
        }
    }

    public static void renderEntireBatch(LevelRenderer levelRenderer, PoseStack poseStack, int renderTick, Camera camera, float partialTick) {
        if (!allDugBlocksOnScreen.isEmpty()) {
            poseStack.pushPose();
            Vec3 cameraPos = camera.getPosition();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().crumblingBufferSource();
            for (Map.Entry<BlockPos, Integer> posAndInt : allDugBlocksOnScreen.entrySet()) {
                int progress = posAndInt.getValue() - 1;
                if (progress >= 0 && progress < 10) {
                    poseStack.pushPose();
                    BlockPos pos = posAndInt.getKey();
                    poseStack.translate((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
                    PoseStack.Pose posestack$pose1 = poseStack.last();
                    VertexConsumer vertexconsumer1 = new SheetedDecalTextureGenerator(multibuffersource$buffersource.getBuffer(ModelBakery.DESTROY_TYPES.get(progress)), posestack$pose1.pose(), posestack$pose1.normal(), 1.0F);
                    net.minecraftforge.client.model.data.ModelData modelData = Minecraft.getInstance().level.getModelDataManager().getAt(pos);
                    Minecraft.getInstance().getBlockRenderer().renderBreakingTexture(Minecraft.getInstance().level.getBlockState(pos), pos, Minecraft.getInstance().level, poseStack, vertexconsumer1, modelData == null ? net.minecraftforge.client.model.data.ModelData.EMPTY : modelData);
                    poseStack.popPose();
                }
            }
            poseStack.popPose();
        }
        allDugBlocksOnScreen.clear();

    }

    class LayerGlow extends RenderLayer<CorrodentEntity, CorrodentModel> {

        public LayerGlow() {
            super(CorrodentRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CorrodentEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(TEXTURE_EYES));
            float alpha = 1.0F;
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
        }
    }
}


