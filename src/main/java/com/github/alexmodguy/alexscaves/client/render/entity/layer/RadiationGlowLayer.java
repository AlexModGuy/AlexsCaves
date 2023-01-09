package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class RadiationGlowLayer extends RenderLayer {

    private RenderLayerParent parent;

    public RadiationGlowLayer(RenderLayerParent parent) {
        super(parent);
        this.parent = parent;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entity instanceof LivingEntity living && living.hasEffect(ACEffectRegistry.IRRADIATED.get()) && AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
            ClientProxy.irradiatedOutlineFlag = true;
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(ACRenderTypes.getRadiationGlow(getTextureLocation(entity)));
            int level = living.getEffect(ACEffectRegistry.IRRADIATED.get()).getAmplifier() + 1;
            float alpha = Math.min(level * 0.33F, 1F);
            matrixStackIn.pushPose();
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords((LivingEntity)entity, 0), 1, 1F, 1, alpha);
            matrixStackIn.popPose();
        }
    }
}
