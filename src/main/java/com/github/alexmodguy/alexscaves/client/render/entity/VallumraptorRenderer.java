package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.VallumraptorModel;
import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class VallumraptorRenderer extends MobRenderer<VallumraptorEntity, VallumraptorModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/vallumraptor.png");
    private static final ResourceLocation TEXTURE_ELDER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/vallumraptor_elder.png");
    private static final ResourceLocation TEXTURE_ALAN = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/vallumraptor_alan.png");
    private static final ResourceLocation TEXTURE_ALAN_ELDER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/vallumraptor_alan_elder.png");
    private static final ResourceLocation TEXTURE_RETRO = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/vallumraptor_retro.png");
    private static final ResourceLocation TEXTURE_RETRO_ELDER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/vallumraptor_retro_elder.png");

    private static final ResourceLocation TEXTURE_TECTONIC = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/vallumraptor_tectonic.png");
    private static final ResourceLocation TEXTURE_TECTONIC_ELDER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/vallumraptor_tectonic_elder.png");

    public VallumraptorRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new VallumraptorModel(), 0.3F);
        this.addLayer(new ItemLayer());
    }

    protected void scale(VallumraptorEntity mob, PoseStack matrixStackIn, float partialTicks) {
        if (mob.isElder()) {
            matrixStackIn.scale(1.1F, 1.1F, 1.1F);
        }
        float alpha = 1.0F - 0.9F * mob.getHideProgress(partialTicks);
        this.model.setAlpha(alpha);
    }

    public ResourceLocation getTextureLocation(VallumraptorEntity entity) {
        if(entity.hasCustomName() && "alan".equalsIgnoreCase(entity.getName().getString())){
            return entity.isElder() ? TEXTURE_ALAN_ELDER : TEXTURE_ALAN;
        }else if(entity.getAltSkin() == 1){
            return entity.isElder() ? TEXTURE_RETRO_ELDER : TEXTURE_RETRO;
        }else if(entity.getAltSkin() == 2){
            return entity.isElder() ? TEXTURE_TECTONIC_ELDER : TEXTURE_TECTONIC;
        }else{
            return entity.isElder() ? TEXTURE_ELDER : TEXTURE;
        }
    }



    @Nullable
    protected RenderType getRenderType(VallumraptorEntity entity, boolean defColor, boolean invis, boolean v) {
        if (entity.getHideProgress(1.0F) > 0.0F) {
            ResourceLocation resourcelocation = this.getTextureLocation(entity);
            return RenderType.entityTranslucent(resourcelocation);
        } else {
            return super.getRenderType(entity, defColor, invis, v);
        }
    }

    class ItemLayer extends RenderLayer<VallumraptorEntity, VallumraptorModel> {

        public ItemLayer() {
            super(VallumraptorRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, VallumraptorEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = entitylivingbaseIn.getMainHandItem();
            if (!itemstack.isEmpty()) {
                boolean left = entitylivingbaseIn.isLeftHanded();
                matrixStackIn.pushPose();
                if (entitylivingbaseIn.isBaby()) {
                    matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                    matrixStackIn.translate(0.0D, 1.5D, 0D);
                }
                matrixStackIn.pushPose();
                getParentModel().translateToHand(matrixStackIn, left);
                if (entitylivingbaseIn.isBaby()) {
                    matrixStackIn.translate(0.0D, 0.1F, -0.6D);
                }
                matrixStackIn.translate(left ? -0.2F : 0.2F, 0.2F, -0.3F);
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(180));
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-10));
                ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
                renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
                matrixStackIn.popPose();
                matrixStackIn.popPose();
            }
        }
    }
}

