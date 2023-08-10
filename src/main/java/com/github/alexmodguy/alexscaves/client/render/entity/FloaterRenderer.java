package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.BoundroidModel;
import com.github.alexmodguy.alexscaves.client.model.FloaterModel;
import com.github.alexmodguy.alexscaves.client.model.QuarrySmasherModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.item.FloaterEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FloaterRenderer extends EntityRenderer<FloaterEntity> {

    private static final FloaterModel FLOATER_MODEL = new FloaterModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/floater.png");

    public FloaterRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }


    public void render(FloaterEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        float ageInTicks = entity.tickCount + partialTicks;

        poseStack.pushPose();
        poseStack.translate(0.0D, (double) 1.5F, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        FLOATER_MODEL.setupAnim(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
        VertexConsumer textureVertexConsumer = bufferIn.getBuffer(ACRenderTypes.getGhostly(TEXTURE));
        FLOATER_MODEL.renderToBuffer(poseStack, textureVertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }

    public ResourceLocation getTextureLocation(FloaterEntity entity) {
        return TEXTURE;
    }
}
