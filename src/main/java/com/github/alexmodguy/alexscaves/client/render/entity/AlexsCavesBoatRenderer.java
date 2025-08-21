package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.ACBoatChestModel;
import com.github.alexmodguy.alexscaves.client.model.ACBoatModel;
import com.github.alexmodguy.alexscaves.client.model.PewenBoatModel;
import com.github.alexmodguy.alexscaves.client.model.ThornwoodBoatModel;
import com.github.alexmodguy.alexscaves.server.entity.util.AlexsCavesBoat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

import java.util.HashMap;

public class AlexsCavesBoatRenderer<T extends Boat & AlexsCavesBoat> extends EntityRenderer<T> {

    private final HashMap<AlexsCavesBoat.Type, ResourceLocation> textureMap = new HashMap<>();
    private final HashMap<AlexsCavesBoat.Type, ACBoatModel> modelMap = new HashMap<>();

    private static final ResourceLocation CHEST_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/boat/chest.png");
    private static final ACBoatChestModel CHEST_MODEL = new ACBoatChestModel();

    private final boolean isChest;

    public AlexsCavesBoatRenderer(EntityRendererProvider.Context context, boolean isChest) {
        super(context);
        for (AlexsCavesBoat.Type type : AlexsCavesBoat.Type.values()) {
            textureMap.put(type, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/boat/" + type.getName() + "_boat.png"));
        }
        modelMap.put(AlexsCavesBoat.Type.PEWEN, new PewenBoatModel());
        modelMap.put(AlexsCavesBoat.Type.THORNWOOD, new ThornwoodBoatModel());
        this.isChest = isChest;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        ACBoatModel model = modelMap.get(entity.getACBoatType());
        poseStack.pushPose();
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        float f = (float) entity.getHurtTime() - partialTicks;
        float f1 = entity.getDamage() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) entity.getHurtDir()));
        }

        float f2 = entity.getBubbleAngle(partialTicks);
        if (!Mth.equal(f2, 0.0F)) {
            poseStack.mulPose((new Quaternionf()).setAngleAxis(entity.getBubbleAngle(partialTicks) * ((float) Math.PI / 180F), 1.0F, 0.0F, 1.0F));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        if (isChest) {
            poseStack.pushPose();
            poseStack.translate(0.0F, -0.25F, 0.5F);
            CHEST_MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(CHEST_TEXTURE)), packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
        model.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        model.renderToBuffer(poseStack, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!entity.isUnderWater()) {
            VertexConsumer vertexconsumer1 = bufferIn.getBuffer(RenderType.waterMask());
            model.getWaterMask().render(poseStack, vertexconsumer1, packedLightIn, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return textureMap.get(entity.getACBoatType());
    }
}
