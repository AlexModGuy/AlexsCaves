package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.client.model.BrainiacModel;
import com.github.alexmodguy.alexscaves.client.render.entity.BrainiacRenderer;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.BrainiacEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class BrainiacBackBarrelLayer extends RenderLayer<BrainiacEntity, BrainiacModel> {

    public BrainiacBackBarrelLayer(BrainiacRenderer render) {
        super(render);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, BrainiacEntity brainiac, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (brainiac.hasBarrel()) {
            boolean hand = (brainiac.getAnimation() == BrainiacEntity.ANIMATION_THROW_BARREL || brainiac.getAnimation() == BrainiacEntity.ANIMATION_DRINK_BARREL) && brainiac.getAnimationTick() > 10;
            matrixStackIn.pushPose();
            getParentModel().translateToArmOrChest(matrixStackIn, hand);
            matrixStackIn.translate(-0.5F, -0.7F, 1.01F);
            if (hand) {
                matrixStackIn.translate(1.25F, 2.1F, -1.5F);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90));
            } else {
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90));
            }
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(ACBlockRegistry.WASTE_DRUM.get().defaultBlockState(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.popPose();
        }
    }
}
