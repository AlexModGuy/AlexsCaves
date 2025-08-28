package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.item.GumballEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.DragonFireball;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GumballRenderer extends EntityRenderer<GumballEntity> {
    private static final ResourceLocation TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_0.png");
    private static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_1.png");
    private static final ResourceLocation TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_2.png");
    private static final ResourceLocation TEXTURE_3 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_3.png");
    private static final ResourceLocation TEXTURE_4 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_4.png");
    private static final ResourceLocation TEXTURE_5 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_5.png");
    private static final ResourceLocation TEXTURE_6 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_6.png");
    private static final ResourceLocation TEXTURE_7 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_7.png");
    private static final ResourceLocation TEXTURE_8 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_8.png");
    private static final ResourceLocation TEXTURE_9 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_9.png");
    private static final ResourceLocation TEXTURE_10 = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_10.png");
    private static final ResourceLocation TEXTURE_EXPLODING = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gumball/gumball_exploding.png");

    public GumballRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(GumballEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        float explodeAmount = entity.getExplodeProgress(partialTicks);
        float scale = entity.isExplosive() ? 0.5F + explodeAmount * 0.2F : 0.25F;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityCutout(getTextureLocation(entity)));
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1, 1F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1, 1F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0, 1F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0, 1F);
        if(entity.isExplosive()){
            float explodeColorChange = entity.getBounces() >= entity.getMaximumBounces() ? 1.0F - 0.5F * (1F + Mth.sin((entity.tickCount + partialTicks) * 0.9F)) : 0.0F;
            VertexConsumer vertexconsumer2 = multiBufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE_EXPLODING));
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1, explodeColorChange);
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1, explodeColorChange);
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0, explodeColorChange);
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0, explodeColorChange);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLight);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int p_253829_, float x, int y, int u, int v, float alpha) {
        vertexConsumer.vertex(matrix4f, x - 0.5F, (float)y - 0.25F, 0.0F).color(1F, 1F, 1F, alpha).uv((float)u, (float)v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_253829_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(GumballEntity gumballEntity) {
        switch (gumballEntity.getColor()){
            case 0:
                return TEXTURE_0;
            case 1:
                return TEXTURE_1;
            case 2:
                return TEXTURE_2;
            case 3:
                return TEXTURE_3;
            case 4:
                return TEXTURE_4;
            case 5:
                return TEXTURE_5;
            case 6:
                return TEXTURE_6;
            case 7:
                return TEXTURE_7;
            case 8:
                return TEXTURE_8;
            case 9:
                return TEXTURE_9;
            case 10:
                return TEXTURE_10;
            default:
                return TEXTURE_0;
        }
    }
}