package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.ThrownWasteDrumEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class ThrownWasteDrumEntityRenderer extends EntityRenderer<ThrownWasteDrumEntity> {

    public ThrownWasteDrumEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    public void render(ThrownWasteDrumEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int lightIn) {
        super.render(entity, entityYaw, partialTicks, poseStack, source, lightIn);
        float ageInTicks = entity.tickCount + partialTicks;
        float progress = (entity.getOnGroundFor() + partialTicks) / ThrownWasteDrumEntity.MAX_TIME;
        if(progress > 1.0F){
            return;
        }
        float expandScale = 1F + (float)Math.sin(progress * progress * Math.PI) * 0.5F;
        poseStack.pushPose();
        poseStack.scale(1F, 1 - progress * 0.03F, 1F);
        poseStack.pushPose();
        poseStack.scale(expandScale, expandScale - progress * 0.3F, expandScale);
        poseStack.translate(0D, 0.5D, 0D);
        if(entity.onGround()){
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }else{
            poseStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float)(ageInTicks * 25)));
        }
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        BlockState state = ACBlockRegistry.WASTE_DRUM.get().defaultBlockState();
        BakedModel bakedmodel = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        float f = 1.0F - progress * 0.5F;
        float f1 = 1.0F + progress;
        float f2 = 1.0F - progress;
        for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(state, RandomSource.create(42), ModelData.EMPTY)){
            NuclearBombRenderer.renderModel(poseStack.last(), source.getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), state, bakedmodel, f, f1, f2, lightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, rt);
        }
        poseStack.popPose();
        poseStack.popPose();
    }

    public ResourceLocation getTextureLocation(ThrownWasteDrumEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

