package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.LicowitchEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessedByLicowitch;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class LicowitchPossessionLayer<T extends LivingEntity & PossessedByLicowitch, M extends AdvancedEntityModel<T>> extends RenderLayer<T, M> {

    private AdvancedEntityModel<T> replacementModel;
    private Function<T, ResourceLocation> replacementTexture;

    public LicowitchPossessionLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    public LicowitchPossessionLayer(RenderLayerParent<T, M> renderLayerParent, Function<T, ResourceLocation> replacementTexture) {
        super(renderLayerParent);
        this.replacementTexture = replacementTexture;
    }

    public LicowitchPossessionLayer(RenderLayerParent<T, M> renderLayerParent, AdvancedEntityModel<T> replacementModel, Function<T, ResourceLocation> replacementTexture) {
        super(renderLayerParent);
        this.replacementModel = replacementModel;
        this.replacementTexture = replacementTexture;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getPossessedByLicowitchId() != -1) {
            LicowitchEntity licowitch = entity.getPossessingLicowitch(entity.level());
            if (licowitch != null) {
                AdvancedEntityModel<T> model = getParentModel();
                ResourceLocation texture = getTextureLocation(entity);
                if (replacementTexture != null) {
                    texture = replacementTexture.apply(entity);
                }
                if (replacementModel != null) {
                    model = replacementModel;
                    this.getParentModel().copyPropertiesTo(model);
                    model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                }
                float possessAlpha = (float) (0.35F + (1F + Math.sin(ageInTicks * 0.2F)) * 0.15F);
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.PURPLE_WITCH_SHADER);
                model.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityTranslucent(texture)), packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 0.3F, 1.0F, possessAlpha);
                model.renderToBuffer(poseStack, bufferIn.getBuffer(ACRenderTypes.getPurpleWitch(texture)), packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0), 1F, 1F, 1F, 1.0F);
            }
        }
    }
}
