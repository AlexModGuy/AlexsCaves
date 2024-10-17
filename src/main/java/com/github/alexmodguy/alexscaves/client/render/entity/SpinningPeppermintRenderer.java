package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.item.DesolateDaggerEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SpinningPeppermintEntity;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nullable;
import java.util.List;

public class SpinningPeppermintRenderer extends EntityRenderer<SpinningPeppermintEntity> {

    public SpinningPeppermintRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    public void render(SpinningPeppermintEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int lightIn) {
        super.render(entity, entityYaw, partialTicks, poseStack, source, lightIn);
        PostEffectRegistry.renderEffectForNextTick(ClientProxy.PURPLE_WITCH_SHADER);
        float ageInTicks = partialTicks + entity.tickCount;
        float despawnsIn = entity.getDespawnTime(partialTicks);
        float minAge = Math.min(1F, Math.min(ageInTicks, despawnsIn) / 10F);
        poseStack.pushPose();
        poseStack.scale(minAge, minAge, minAge);
        poseStack.translate(0.0D, 0.5D, 0.0D);
        if(entity.isStraight()){
            poseStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) + 90.0F));
            poseStack.mulPose(Axis.ZN.rotationDegrees((float) (Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 5F * Math.sin(ageInTicks * 0.2F))));
            poseStack.mulPose(Axis.ZP.rotationDegrees(ageInTicks * -4.0F * entity.getSpinSpeed()));
            poseStack.translate(0.0D, 0.0D, -0.25);
        }else{
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(ageInTicks * -4.0F * entity.getSpinSpeed()));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.sin(ageInTicks * 0.8F) * 8));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.cos(ageInTicks * 0.8F) * 8));
        }
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(entity.peppermintRenderStack, entity.level(), null, 0);
        int redOverlay = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(true));
        for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(entity.peppermintRenderStack, false)) {
            renderModel(poseStack.last(), source.getBuffer(Sheets.translucentItemSheet()), 1.0F, null, bakedmodel, 1.0F, minAge, 1.0F, lightIn, redOverlay, ModelData.EMPTY, rt);
        }
        RenderType purpleWitch = ACRenderTypes.getPurpleWitch(TextureAtlas.LOCATION_BLOCKS);
        renderModel(poseStack.last(), source.getBuffer(purpleWitch), 1.0F, null, bakedmodel, 1.0F, 1.0F, 1.0F, lightIn, redOverlay, ModelData.EMPTY, purpleWitch);
        poseStack.popPose();
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

    public ResourceLocation getTextureLocation(SpinningPeppermintEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

