package com.github.alexmodguy.alexscaves.client.gui.book.widget;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class EntityBoxWidget extends EntityWidget {

    @Expose
    @SerializedName("box_width")
    private float boxWidth;
    @Expose
    @SerializedName("box_height")
    private float boxHeight;
    @Expose
    @SerializedName("box_scale")
    private float boxScale;
    @Expose
    @SerializedName("entity_x_offset")
    private float entityXOffset;
    @Expose
    @SerializedName("entity_y_offset")
    private float entityYOffset;
    @Expose
    @SerializedName("box_image")
    private String borderImage;

    @Expose(serialize = false, deserialize = false)
    private ResourceLocation borderTexture;

    private static final int BORDER_TEXTURE_SIZE = 64;
    private static final int PIX_WIDTH_CORNER = 10;
    private static final int PIX_WIDTH_LINE = 4;

    public EntityBoxWidget(int displayPage, String entityId, boolean sepia, float boxWidth, float boxHeight, float boxScale, float entityXOffset, float entityYOffset, String borderImage, String entityNBT, int x, int y, float scale) {
        super(displayPage, Type.ENTITY_BOX, entityId, sepia, entityNBT, x, y, scale);
        this.boxHeight = boxHeight;
        this.boxWidth = boxWidth;
        this.borderImage = borderImage;
        this.boxScale = boxScale;
        this.entityXOffset = entityXOffset;
        this.entityYOffset = entityYOffset;
    }

    public void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTicks, boolean onFlippingPage) {
        if (this.borderTexture == null) {
            this.borderTexture = ResourceLocation.parse(borderImage);
        }
        poseStack.pushPose();
        poseStack.translate(this.entityXOffset * getScale(), this.entityYOffset * getScale(), 0);
        super.render(poseStack, bufferSource, partialTicks, onFlippingPage);
        poseStack.popPose();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(ACRenderTypes.getBookWidget(this.borderTexture, this.isSepia()));

        float endUV = PIX_WIDTH_CORNER + PIX_WIDTH_LINE;
        float endSub = PIX_WIDTH_CORNER / 2F;
        float endUV1 = endUV + PIX_WIDTH_CORNER;

        Lighting.setupForFlatItems();
        poseStack.pushPose();
        poseStack.translate(0, 0, 2);
        poseStack.translate(getX() - boxWidth * 0.5F * boxScale, getY() - boxHeight * 0.5F * boxScale, -15);
        poseStack.scale(boxScale, boxScale, 1F);
        poseStack.pushPose();

        poseStack.pushPose();
        poseStack.translate(0, 0, 2);
        renderCorner(poseStack, vertexconsumer, 0, PIX_WIDTH_CORNER, 0, PIX_WIDTH_CORNER);
        poseStack.popPose();

        renderQuad(poseStack, vertexconsumer, boxWidth - endSub, PIX_WIDTH_CORNER / 2F, -PIX_WIDTH_CORNER / 2F, PIX_WIDTH_CORNER / 2F, PIX_WIDTH_CORNER, PIX_WIDTH_CORNER + PIX_WIDTH_LINE, 0, PIX_WIDTH_CORNER);

        poseStack.pushPose();
        poseStack.translate(boxWidth, 0, 2);
        renderCorner(poseStack, vertexconsumer, endUV, endUV1, 0, PIX_WIDTH_CORNER);
        poseStack.popPose();

        renderQuad(poseStack, vertexconsumer, PIX_WIDTH_CORNER / 2F, -PIX_WIDTH_CORNER / 2F, PIX_WIDTH_CORNER / 2F - 0.1F, boxHeight - endSub, 0, PIX_WIDTH_CORNER, PIX_WIDTH_CORNER, PIX_WIDTH_CORNER + PIX_WIDTH_LINE);

        poseStack.pushPose();
        poseStack.translate(0, boxHeight, 2);
        renderCorner(poseStack, vertexconsumer, 0, PIX_WIDTH_CORNER, endUV, endUV1);
        poseStack.popPose();

        renderQuad(poseStack, vertexconsumer, boxWidth - endSub, PIX_WIDTH_CORNER / 2F, boxHeight - PIX_WIDTH_CORNER / 2F, boxHeight + PIX_WIDTH_CORNER / 2F, PIX_WIDTH_CORNER, PIX_WIDTH_CORNER + PIX_WIDTH_LINE, PIX_WIDTH_LINE + PIX_WIDTH_CORNER, PIX_WIDTH_LINE + PIX_WIDTH_CORNER * 2);

        poseStack.pushPose();
        poseStack.translate(boxWidth, boxHeight, 2);
        renderCorner(poseStack, vertexconsumer, endUV, endUV1, endUV, endUV1);
        poseStack.popPose();

        renderQuad(poseStack, vertexconsumer, boxWidth + PIX_WIDTH_CORNER / 2F, boxWidth - PIX_WIDTH_CORNER / 2F, PIX_WIDTH_CORNER / 2F - 0.1F, boxHeight - endSub, PIX_WIDTH_LINE + PIX_WIDTH_CORNER, PIX_WIDTH_LINE + PIX_WIDTH_CORNER * 2, PIX_WIDTH_CORNER, PIX_WIDTH_CORNER + PIX_WIDTH_LINE);

        poseStack.popPose();
        poseStack.popPose();
    }

    private void renderCorner(PoseStack poseStack, VertexConsumer vertexconsumer, float u0, float u1, float v0, float v1){
        float texWidth = (u1 - u0) / 2F;
        float texHeight = (v1 - v0) / 2F;
        renderQuad(poseStack, vertexconsumer, texWidth, -texWidth, -texHeight, texHeight, u0, u1, v0, v1);
    }

    private void renderQuad(PoseStack poseStack, VertexConsumer vertexconsumer, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1){
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        float scaledU0 = u0 / (float)BORDER_TEXTURE_SIZE;
        float scaledU1 = u1 / (float)BORDER_TEXTURE_SIZE;
        float scaledV0 = v0 / (float)BORDER_TEXTURE_SIZE;
        float scaledV1 = v1 / (float)BORDER_TEXTURE_SIZE;

        vertexconsumer.vertex(matrix4f, x0, y0, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(scaledU1, scaledV0).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexconsumer.vertex(matrix4f, x1, y0, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(scaledU0, scaledV0).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).uv(scaledU0, scaledV1).endVertex();
        vertexconsumer.vertex(matrix4f, x1, y1, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(scaledU0, scaledV1).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).uv(scaledU0, scaledV0).endVertex();
        vertexconsumer.vertex(matrix4f, x0, y1, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(scaledU1, scaledV1).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).uv(scaledU1, scaledV0).endVertex();
    }
}
