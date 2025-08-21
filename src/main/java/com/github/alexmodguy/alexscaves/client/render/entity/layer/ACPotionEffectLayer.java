package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import com.github.alexmodguy.alexscaves.server.potion.IrradiatedEffect;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ACPotionEffectLayer extends RenderLayer {

    private static final ResourceLocation TEXTURE_BUBBLE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/bubble.png");
    private static final ResourceLocation TEXTURE_WATER = ResourceLocation.parse("textures/block/water_still.png");
    public static final ResourceLocation INSIDE_BUBBLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/inside_bubble.png");
    public static final ResourceLocation TEXTURE_DARKNESS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/darkness_incarnate.png");
    public static final ResourceLocation TEXTURE_SUGAR_RUSH = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sugar_rush.png");
    private RenderLayerParent parent;


    public ACPotionEffectLayer(RenderLayerParent parent) {
        super(parent);
        this.parent = parent;
    }

    public static void renderBubbledFirstPerson(PoseStack poseStack) {
        poseStack.pushPose();
        renderBubbledFluid(Minecraft.getInstance(), poseStack, TEXTURE_BUBBLE, false);
        renderBubbledFluid(Minecraft.getInstance(), poseStack, INSIDE_BUBBLE_TEXTURE, true);
        poseStack.popPose();
    }

    public static void renderBubbledFluid(Minecraft p110726, PoseStack poseStack, ResourceLocation texture, boolean translate) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        BlockPos blockpos = BlockPos.containing(p110726.player.getX(), p110726.player.getEyeY(), p110726.player.getZ());
        float f = LightTexture.getBrightness(p110726.player.level().dimensionType(), p110726.player.level().getMaxLocalRawBrightness(blockpos));
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(f, f, f, translate ? 0.3F : 1.0F);
        Matrix4f matrix4f = poseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        if (translate) {
            float f7 = -p110726.player.getYRot() / 64.0F;
            float f8 = p110726.player.getXRot() / 64.0F;
            bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).uv(4.0F + f7, 4.0F + f8).endVertex();
            bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).uv(0.0F + f7, 4.0F + f8).endVertex();
            bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).uv(0.0F + f7, 0.0F + f8).endVertex();
            bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).uv(4.0F + f7, 0.0F + f8).endVertex();
        } else {
            float min = -0.5F;
            float max = 1.5F;
            bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).uv(max, max).endVertex();
            bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).uv(min, max).endVertex();
            bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).uv(min, min).endVertex();
            bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).uv(max, min).endVertex();

        }
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof LivingEntity living) {
            if (living.hasEffect(ACEffectRegistry.IRRADIATED.get()) && AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                int level = living.getEffect(ACEffectRegistry.IRRADIATED.get()).getAmplifier() + 1;
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(level >= IrradiatedEffect.BLUE_LEVEL ? ACRenderTypes.getBlueRadiationGlow(getTextureLocation(entity)) : ACRenderTypes.getRadiationGlow(getTextureLocation(entity)));
                float alpha = level >= IrradiatedEffect.BLUE_LEVEL ? 0.9F : Math.min(level * 0.33F, 1F);
                poseStack.pushPose();
                this.getParentModel().renderToBuffer(poseStack, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords((LivingEntity) entity, 0), 1, 1F, 1, alpha);
                poseStack.popPose();
            }
            if (living.hasEffect(ACEffectRegistry.BUBBLED.get()) && living.isAlive()) {
                float bodyYaw = Mth.rotLerp(partialTicks, living.yBodyRotO, living.yBodyRot);
                poseStack.pushPose();
                float size = (float) Math.ceil(Math.max(living.getBbHeight(), living.getBbWidth()));
                poseStack.translate(0, 1.4 - size * 0.5F, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(180 - bodyYaw));
                poseStack.scale(1.1F, 1.1F, 1.1F);
                float waterAnimOffset = (float) (Math.round(ageInTicks * 0.4)) % 16.0F;
                renderBubble(living, partialTicks, poseStack, bufferIn.getBuffer(ACRenderTypes.getBubbledCull(TEXTURE_WATER)), size - 0.1F, packedLightIn, size * 0.5F, size * 0.5F * 0.0625F, -0.0625F * waterAnimOffset, true);
                renderBubble(living, partialTicks, poseStack, bufferIn.getBuffer(ACRenderTypes.getBubbledNoCull(TEXTURE_BUBBLE)), size, packedLightIn, 1, 1, 0, false);
                poseStack.popPose();
            }
            if (living.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get()) && AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get() && living.isAlive()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(ACRenderTypes.entityTranslucent(getTextureLocation(entity)));
                poseStack.pushPose();
                float alpha = DarknessIncarnateEffect.getIntensity(living, partialTicks, 25F);
                this.getParentModel().renderToBuffer(poseStack, ivertexbuilder, 0, LivingEntityRenderer.getOverlayCoords((LivingEntity) entity, 0), 0F, 0F, 0F, alpha);
                poseStack.popPose();
            }
            if (living.hasEffect(ACEffectRegistry.SUGAR_RUSH.get()) && getParentModel() instanceof HumanoidModel<?>) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_SUGAR_RUSH));
                this.getParentModel().renderToBuffer(poseStack, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords((LivingEntity) entity, 0), 1, 1F, 1, 1);
            }
        }
    }

    private static void renderBubble(LivingEntity entity, float partialTicks, PoseStack poseStack, VertexConsumer consumer, float size, int packedLight, float textureScaleXZ, float textureScaleY, float uvOffset, boolean water) {
        Matrix4f cubeAt = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        float cubeStart = size * -0.5F;
        float cubeEnd = size * 0.5F;
        renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeEnd, cubeStart, cubeEnd, cubeEnd, cubeEnd, cubeEnd, cubeEnd, textureScaleXZ, textureScaleY, uvOffset, water);
        renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeEnd, cubeEnd, cubeStart, cubeStart, cubeStart, cubeStart, cubeStart, textureScaleXZ, textureScaleY, uvOffset, water);
        renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeEnd, cubeEnd, cubeEnd, cubeStart, cubeStart, cubeEnd, cubeEnd, cubeStart, textureScaleXZ, textureScaleY, uvOffset, water);
        renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeStart, cubeStart, cubeEnd, cubeStart, cubeEnd, cubeEnd, cubeStart, textureScaleXZ, textureScaleY, uvOffset, water);
        renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeEnd, cubeStart, cubeStart, cubeStart, cubeStart, cubeEnd, cubeEnd, textureScaleXZ, textureScaleY, uvOffset, water);
        renderCubeFace(entity, cubeAt, matrix3f, consumer, packedLight, cubeStart, cubeEnd, cubeEnd, cubeEnd, cubeEnd, cubeEnd, cubeStart, cubeStart, textureScaleXZ, textureScaleY, uvOffset, water);
    }

    private static void renderCubeFace(LivingEntity entity, Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, int packedLightIn, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float textureScaleXZ, float textureScaleY, float uvOffset, boolean water) {
        int overlayCoords = OverlayTexture.NO_OVERLAY;
        int colorR = 255;
        int colorG = 255;
        int colorB = 255;
        int colorA = water ? 200 : 255;
        if (water) {
            int waterColorAt = entity.level().getBiome(entity.blockPosition()).get().getWaterColor();
            colorR = waterColorAt >> 16 & 255;
            colorG = waterColorAt >> 8 & 255;
            colorB = waterColorAt & 255;
        }
        vertexConsumer.vertex(matrix4f, f1, f3, f5).color(colorR, colorG, colorB, colorA).uv((float) 0, (float) textureScaleY + uvOffset).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, f2, f3, f6).color(colorR, colorG, colorB, colorA).uv((float) textureScaleXZ, (float) textureScaleY + uvOffset).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, f2, f4, f7).color(colorR, colorG, colorB, colorA).uv((float) textureScaleXZ, (float) uvOffset).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix4f, f1, f4, f8).color(colorR, colorG, colorB, colorA).uv((float) 0, (float) uvOffset).overlayCoords(overlayCoords).uv2(packedLightIn).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
    }
}
