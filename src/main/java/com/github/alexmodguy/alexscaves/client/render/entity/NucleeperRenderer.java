package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.NucleeperModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.NucleeperEnergySwirlLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
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

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class NucleeperRenderer extends MobRenderer<NucleeperEntity, NucleeperModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper.png");
    private static final ResourceLocation TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_glow.png");
    private static final ResourceLocation TEXTURE_GLASS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_glass.png");
    private static final ResourceLocation TEXTURE_BUTTONS_0 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_buttons_0.png");
    private static final ResourceLocation TEXTURE_BUTTONS_1 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_buttons_1.png");
    private static final ResourceLocation TEXTURE_BUTTONS_2 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_buttons_2.png");
    private static final ResourceLocation TEXTURE_EXPLODE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_explode.png");


    public NucleeperRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new NucleeperModel(0.0F), 0.8F);
        this.addLayer(new LayerGlow());
        this.addLayer(new NucleeperEnergySwirlLayer(this));
    }

    protected void scale(NucleeperEntity mob, PoseStack poseStack, float partialTicks) {
    }

    public ResourceLocation getTextureLocation(NucleeperEntity entity) {
        return TEXTURE;
    }

    private static void shineOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, Matrix3f p_114092_, float xOffset, float yOffset) {
        p_114220_.vertex(p_114221_, 0.0F, 0.0F, 0.0F).color(0, 255, 0, 230).uv(xOffset + 0.5F, yOffset).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, Matrix3f p_114092_, float p_114217_, float p_114218_, float xOffset, float yOffset) {
        p_114215_.vertex(p_114216_, -ACMath.HALF_SQRT_3 * p_114218_, p_114217_, 0).color(0, 255, 0, 0).uv(xOffset, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, Matrix3f p_114092_, float p_114226_, float p_114227_, float xOffset, float yOffset) {
        p_114224_.vertex(p_114225_, ACMath.HALF_SQRT_3 * p_114227_, p_114226_, 0).color(0, 255, 0, 0).uv(xOffset + 1, yOffset + 1).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }


    public void render(NucleeperEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        float closeProgress = entityIn.getCloseProgress(partialTicks);
        if (closeProgress > 0F && entityIn.isAlive() && !entityIn.isExploding()) {
            float sq = Mth.sqrt(closeProgress);
            float length = sq * 3;
            float width = (1F - sq) * 1.25F;
            Vec3 modelOffset = model.getSirenPosition(new Vec3(0, -0.5F, 0));
            poseStack.pushPose();
            poseStack.translate(modelOffset.x, modelOffset.y, modelOffset.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(entityIn.getSirenAngle(partialTicks)));
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZN.rotationDegrees(90));
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f1 = posestack$pose.pose();
            Matrix3f matrix3f1 = posestack$pose.normal();
            VertexConsumer lightConsumer = bufferIn.getBuffer(ACRenderTypes.getNucleeperLights());
            shineOriginVertex(lightConsumer, matrix4f1, matrix3f1, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            shineRightCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
            Matrix4f matrix4f2 = posestack$pose.pose();
            Matrix3f matrix3f2 = posestack$pose.normal();
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            shineOriginVertex(lightConsumer, matrix4f2, matrix3f2, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f2, matrix3f2, length, width, 0, 0);
            shineRightCornerVertex(lightConsumer, matrix4f2, matrix3f2, length, width, 0, 0);
            shineLeftCornerVertex(lightConsumer, matrix4f2, matrix3f2, length, width, 0, 0);
            poseStack.popPose();
            poseStack.popPose();
        }
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }

    class LayerGlow extends RenderLayer<NucleeperEntity, NucleeperModel> {

        public LayerGlow() {
            super(NucleeperRenderer.this);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, NucleeperEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float alpha = (float) (1F + Math.sin(ageInTicks * 0.3F)) * 0.25F + 0.5F;
            float explodeProgress = entitylivingbaseIn.getExplodeProgress(partialTicks);
            VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder1, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
            VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(ForgeRenderTypes.getUnlitTranslucent(TEXTURE_GLASS));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder2, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            ResourceLocation buttons;
            int buttonDiv = entitylivingbaseIn.tickCount / 5 % 6;
            if(entitylivingbaseIn.isCharged()){
                buttonDiv = entitylivingbaseIn.tickCount / 2 % 6;
            }
            if (buttonDiv < 2) {
                buttons = TEXTURE_BUTTONS_0;
            } else if (buttonDiv < 4) {
                buttons = TEXTURE_BUTTONS_1;
            } else {
                buttons = TEXTURE_BUTTONS_2;
            }
            VertexConsumer ivertexbuilder3 = bufferIn.getBuffer(RenderType.eyes(buttons));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder3, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            VertexConsumer ivertexbuilder4 = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_EXPLODE));
            this.getParentModel().renderToBuffer(poseStack, ivertexbuilder4, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, explodeProgress);

        }
    }
}


