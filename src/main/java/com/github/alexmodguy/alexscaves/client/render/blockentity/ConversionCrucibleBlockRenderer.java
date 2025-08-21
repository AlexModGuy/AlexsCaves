package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.ConversionCrucibleModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.entity.SugarStaffHexRenderer;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ConversionCrucibleBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.List;

public class ConversionCrucibleBlockRenderer<T extends ConversionCrucibleBlockEntity> implements BlockEntityRenderer<T> {

    private static final ConversionCrucibleModel MODEL = new ConversionCrucibleModel();
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/conversion_crucible.png");
    private static final ResourceLocation TEXTURE_OVERLAY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/conversion_crucible_active.png");
    private static final ResourceLocation TEXTURE_FLUID = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/conversion_crucible_fluid.png");
    private static final ResourceLocation TEXTURE_HEX = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sugar_staff_hex.png");

    protected final RandomSource random = RandomSource.create();

    public ConversionCrucibleBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {

    }

    @Override
    public void render(T crucible, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float conversionProgress = crucible.getConversionProgress(partialTicks);
        float splashProgress = crucible.getSplashProgress(partialTicks);
        float showItemProgress = crucible.getItemDisplayProgress(partialTicks) * (1F - splashProgress);
        float conversionProgressSq = conversionProgress * conversionProgress;
        float conversionProgressSqrt = (float) Math.sqrt(conversionProgress);
        float ageInTicks = crucible.tickCount + partialTicks;
        ItemStack displayStack = crucible.getDisplayItem();
        int intcolor = crucible.getConvertingToColor();
        float r = (float) ((intcolor & 16711680) >> 16) / 255.0F;
        float g = (float) ((intcolor & '\uff00') >> 8) / 255.0F;
        float b = (float) ((intcolor & 255) >> 0) / 255.0F;
        float bob1 = (float) (Math.sin(ageInTicks * 0.5F) * 0.25F) + 0.75F;
        float bob2 = ((float) (Math.sin(ageInTicks * 0.25F) * 0.25F) + 0.75F) * 0.4F;

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-180));
        MODEL.hideBeam(true);
        MODEL.hideSauce(true);
        MODEL.setupAnim(null, splashProgress, conversionProgress, ageInTicks, 0, 0);
        MODEL.setFilledLevel(crucible.getFilledLevel());
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1);
        if(conversionProgress > 0){
            poseStack.pushPose();
            poseStack.translate(0, 1.3F, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(ageInTicks * 15.0F));
            poseStack.scale(15 * conversionProgress, 15 * conversionProgress, 15 * conversionProgress);
            for(int i = 0; i < 5; i++){
                float f = (1F - i / 5F) * 0.5F;
                float bob3 = (float) (Math.sin(ageInTicks * 0.5F + i) * 0.005F);
                SugarStaffHexRenderer.renderHex(poseStack, bufferIn, ACRenderTypes.getVoidBeingCloud(TEXTURE_HEX), f * conversionProgressSqrt, r, g, b);
                poseStack.translate(0, -0.005F - bob3 * 0.03F, 0);
            }
            poseStack.popPose();
        }
        if(crucible.getFilledLevel() > 0){
            MODEL.hideBeam(true);
            MODEL.hideSauce(false);
            MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getVoidBeingCloud(TEXTURE_FLUID)), combinedLightIn, combinedOverlayIn, r, g, b, 1.0F);
        }
        MODEL.hideBeam(false);
        MODEL.hideSauce(true);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getVoidBeingCloud(TEXTURE_OVERLAY)), combinedLightIn, combinedOverlayIn, r, g, b, Math.max(splashProgress, bob1 * conversionProgress));
        poseStack.popPose();
        float cameraY = Minecraft.getInstance().getEntityRenderDispatcher().camera.getYRot();
        float lightLength = 1.25F + bob2;
        float lightWidth = 0.35F;
        Component text = crucible.getDisplayText();
        if(showItemProgress > 0.0F) {
            poseStack.translate(0.5F, 1.25F, 0.5F);
            if(!displayStack.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(-0.15F, bob2 - 0.15F, -0.15F);
                poseStack.scale(0.35F, 0.35F, 0.35F);
                poseStack.translate(0.5F, 0.0, 0.5F);
                poseStack.mulPose(Axis.YN.rotationDegrees(ageInTicks * 3.0F));
                poseStack.translate(-0.5F, 0.0, -0.5F);
                BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(displayStack, crucible.getLevel(), null, 0);
                RenderType rt = ACRenderTypes.getTeslaBulb(TextureAtlas.LOCATION_BLOCKS);
                renderModel(poseStack.last(), bufferIn.getBuffer(rt), showItemProgress * 0.35F, null, bakedmodel, r, g, b, 240, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, rt);
                poseStack.popPose();
            }
            if(text != null){
                poseStack.pushPose();
                poseStack.translate(0F, 0.45F + bob2, 0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180 - cameraY));
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.scale(0.02F, 0.02F, 0.02F);
                float f = (float)(-Minecraft.getInstance().font.width(text) / 2);
                Minecraft.getInstance().font.drawInBatch8xOutline(text.getVisualOrderText(), f, 0.0F, FastColor.ARGB32.color(Mth.clamp((int) (showItemProgress * 255), 4, 255), 255, 255, 255), FastColor.ARGB32.color((int) (Mth.clamp((int) (showItemProgress * 200), 4, 255)), (int) (r * 255), (int) (g * 255), (int) (b * 255)), poseStack.last().pose(), bufferIn, 240);
                poseStack.popPose();
            }
            poseStack.pushPose();
            poseStack.translate(0F, -1.1F, 0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180 - cameraY));
            PoseStack.Pose posestack$pose1 = poseStack.last();
            Matrix4f matrix4f2 = posestack$pose1.pose();
            Matrix3f matrix3f2 = posestack$pose1.normal();
            VertexConsumer lightConsumer = bufferIn.getBuffer(ACRenderTypes.getCrucibleItemBeam());
            shineOriginVertex(lightConsumer, matrix4f2, matrix3f2, 0, 0, r, g, b, 12 * showItemProgress);
            shineLeftCornerVertex(lightConsumer, matrix4f2, matrix3f2, lightLength, lightWidth, 0, 0, r, g, b, 0);
            shineRightCornerVertex(lightConsumer, matrix4f2, matrix3f2, lightLength, lightWidth, 0, 0, r, g, b, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f2, matrix3f2, lightLength, lightWidth, 0, 0, r, g, b, 0);
            poseStack.popPose();
        }
    }

    public static void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, float alpha, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_, ModelData modelData, net.minecraft.client.renderer.RenderType renderType) {
        RandomSource randomsource = RandomSource.create();
        long i = 42L;

        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, alpha, p_111071_.getQuads(p_111070_, direction, randomsource, modelData, renderType), p_111075_, p_111076_);
        }

        randomsource.setSeed(42L);
        renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, alpha, p_111071_.getQuads(p_111070_, (Direction) null, randomsource, modelData, renderType), p_111075_, p_111076_);
    }

    private static void renderQuadList(PoseStack.Pose p_111059_, VertexConsumer p_111060_, float p_111061_, float p_111062_, float p_111063_, float alpha, List<BakedQuad> p_111064_, int p_111065_, int p_111066_) {
        for (BakedQuad bakedquad : p_111064_) {
            float f;
            float f1;
            float f2;
            f = Mth.clamp(p_111061_, 0.0F, 1.0F);
            f1 = Mth.clamp(p_111062_, 0.0F, 1.0F);
            f2 = Mth.clamp(p_111063_, 0.0F, 1.0F);
            p_111060_.putBulkData(p_111059_, bakedquad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, f, f1, f2, alpha, new int[]{p_111065_, p_111065_, p_111065_, p_111065_}, p_111066_, false);
        }
    }


    private static void shineOriginVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float xOffset, float yOffset, float r, float g, float b, float a) {
        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(r, g, b, a).uv(xOffset + 0.5F, yOffset).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float length, float width, float xOffset, float yOffset, float r, float g, float b, float a) {
        vertexConsumer.vertex(matrix4f, -ACMath.HALF_SQRT_3 * width, length, 0).color(r, g, b, a).uv(xOffset, yOffset + 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float length, float width, float xOffset, float yOffset, float r, float g, float b, float a) {
        vertexConsumer.vertex(matrix4f, ACMath.HALF_SQRT_3 * width, length, 0).color(r, g, b, a).uv(xOffset + 1, yOffset + 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
    }

    public int getViewDistance() {
        return 128;
    }
}