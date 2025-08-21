package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GumbeeperModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.GumbeeperEnergySwirlLayer;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.LicowitchPossessionLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneMageEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumbeeperEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class GumbeeperRenderer extends MobRenderer<GumbeeperEntity, GumbeeperModel> implements CustomBookEntityRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumbeeper.png");
    private static final ResourceLocation TEXTURE_GLASS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumbeeper_glass.png");
    private static final ResourceLocation TEXTURE_EXPLODE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumbeeper_explode.png");
    private static final ResourceLocation TEXTURE_POSSESSED = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumbeeper_possessed.png");
    private boolean sepia = false;

    public GumbeeperRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GumbeeperModel(0.0F), 0.8F);
        this.addLayer(new LayerGlow());
        this.addLayer(new GumbeeperEnergySwirlLayer(this));
        this.addLayer(new LicowitchPossessionLayer<>(this, gumbeeperEntity -> TEXTURE_EXPLODE));
    }

    protected void scale(GumbeeperEntity mob, PoseStack poseStack, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(GumbeeperEntity entity) {
        return TEXTURE;
    }

    @Override
    public void setSepiaFlag(boolean sepiaFlag) {
        this.sepia = sepiaFlag;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(GumbeeperEntity deepOneMageEntity, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(deepOneMageEntity);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return sepia ? ACRenderTypes.getBookWidget(resourcelocation, true) : RenderType.entityCutoutNoCull(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    class LayerGlow extends RenderLayer<GumbeeperEntity, GumbeeperModel> {

        public LayerGlow() {
            super(GumbeeperRenderer.this);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, GumbeeperEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(sepia ? ACRenderTypes.getBookWidget(TEXTURE_GLASS, true) : RenderType.entityTranslucentCull(TEXTURE_GLASS));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder2, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            float explodeProgress = entitylivingbaseIn.getExplodeProgress(partialTicks);
            float alpha = (float)(Math.sin(ageInTicks * 1.2F) + 1F) * 0.5F * explodeProgress * 0.8F;
            VertexConsumer ivertexbuilder4 = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_EXPLODE));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder4, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);

        }
    }
}


