package com.github.alexmodguy.alexscaves.client.render.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.item.RaygunItem;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RaygunRenderHelper {

    private static final ResourceLocation RAYGUN_RAY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raygun/raygun_ray.png");
    private static final ResourceLocation RAYGUN_BLUE_RAY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raygun/raygun_blue_ray.png");
    private static void renderRay(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 vec3, float useAmount, float offset, boolean irradiated, boolean blue) {
        float f2 = -1.0F * (offset * 0.25F % 1.0F);
        poseStack.pushPose();
        float length = (float) (vec3.length());
        vec3 = vec3.normalize();
        float f5 = (float) Math.acos(vec3.y);
        float f6 = (float) Math.atan2(vec3.z, vec3.x);
        poseStack.mulPose(Axis.YP.rotationDegrees(((Mth.PI / 2F) - f6) * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.XP.rotationDegrees(f5 * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.YP.rotationDegrees(offset * 3.0F));
        float f8 = 1F;
        int j = (int) (f8 * 255.0F);
        int k = (int) (f8 * 255.0F);
        int l = (int) (f8 * 255.0F);
        float v = -1.0F + f2;
        float v1 = length * 1F + v;
        float endWidth = 1.3F;
        float startMiddle = 0;
        if(irradiated){
            PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
        }
        VertexConsumer ivertexbuilder = bufferSource.getBuffer(ACRenderTypes.getRaygunRay(blue ? RAYGUN_BLUE_RAY : RAYGUN_RAY, irradiated));
        PoseStack.Pose matrixstack$entry = poseStack.last();
        poseStack.pushPose();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();

        vertex(ivertexbuilder, matrix4f, matrix3f, startMiddle, 0.0F, 0, j, k, l, 0.5F, v);
        vertex(ivertexbuilder, matrix4f, matrix3f, -endWidth, length, 0, j, k, l, 0.0F, v1);
        vertex(ivertexbuilder, matrix4f, matrix3f, endWidth, length, 0, j, k, l, 1.0F, v1);

        vertex(ivertexbuilder, matrix4f, matrix3f, 0, 0.0F, startMiddle, j, k, l, 0.5F, v);
        vertex(ivertexbuilder, matrix4f, matrix3f, 0, length, endWidth, j, k, l, 1F, v1);
        vertex(ivertexbuilder, matrix4f, matrix3f, 0, length, -endWidth, j, k, l, 0F, v1);
        poseStack.popPose();
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float x, float y, float z, int p_229108_6_, int p_229108_7_, int p_229108_8_, float u, float v) {
        p_229108_0_.vertex(p_229108_1_, x, y, z).color(p_229108_6_, p_229108_7_, p_229108_8_, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public static void renderRaysFor(LivingEntity entity,  Vec3 rayFrom, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, boolean firstPerson, int firstPersonPass) {
        if (entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RaygunItem && entity.isUsingItem()) {
            ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
            float useRaygunAmount = RaygunItem.getUseTime(stack) / 5F;
            float ageInTicks = entity.tickCount + partialTick;
            Vec3 rayPosition = RaygunItem.getLerpedRayPosition(stack, partialTick);
            boolean blue = stack.getEnchantmentLevel(ACEnchantmentRegistry.GAMMA_RAY.get()) > 0;
            if (rayPosition != null && RaygunItem.getUseTime(stack) >= 5F) {
                Vec3 gunPos = getGunOffset(entity, partialTick, firstPerson, entity.getMainArm() == HumanoidArm.LEFT);
                Vec3 vec3 = rayPosition.subtract(rayFrom.add(gunPos));
                poseStack.pushPose();
                poseStack.translate(gunPos.x, gunPos.y, gunPos.z);
                if(firstPersonPass == 0 || firstPersonPass == 1){
                    RaygunRenderHelper.renderRay(poseStack, bufferSource, vec3, useRaygunAmount, ageInTicks, false, blue);
                }
                if((firstPersonPass == 0 || firstPersonPass == 2) && AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()){
                    RaygunRenderHelper.renderRay(poseStack, bufferSource, vec3, useRaygunAmount, ageInTicks, true, blue);
                }
                poseStack.popPose();
            }
        }
        if (entity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RaygunItem && entity.isUsingItem()) {
            ItemStack stack = entity.getItemInHand(InteractionHand.OFF_HAND);
            float useRaygunAmount = RaygunItem.getUseTime(stack) / 5F;
            float ageInTicks = entity.tickCount + partialTick;
            Vec3 rayPosition = RaygunItem.getLerpedRayPosition(stack, partialTick);
            boolean blue = stack.getEnchantmentLevel(ACEnchantmentRegistry.GAMMA_RAY.get()) > 0;
            if (rayPosition != null && RaygunItem.getUseTime(stack) >= 5F) {
                Vec3 gunPos = getGunOffset(entity, partialTick, firstPerson, entity.getMainArm() == HumanoidArm.RIGHT);
                Vec3 vec3 = rayPosition.subtract(rayFrom.add(gunPos));
                poseStack.pushPose();
                poseStack.translate(gunPos.x, gunPos.y, gunPos.z);
                if(firstPersonPass == 0 || firstPersonPass == 1){
                    RaygunRenderHelper.renderRay(poseStack, bufferSource, vec3, useRaygunAmount, ageInTicks, false, blue);
                }
                if(firstPersonPass == 0 || firstPersonPass == 2){
                    RaygunRenderHelper.renderRay(poseStack, bufferSource, vec3, useRaygunAmount, ageInTicks, true, blue);
                }
                poseStack.popPose();
            }
        }
    }

    private static Vec3 getGunOffset(LivingEntity entity, float partialTicks, boolean firstPerson, boolean left) {
        int i = left ? -1 : 1;
        if(firstPerson){
            double d7 = 1000.0D / (double) Minecraft.getInstance().getEntityRenderDispatcher().options.fov().get().intValue();
            Vec3 vec3 = Minecraft.getInstance().getEntityRenderDispatcher().camera.getNearPlane().getPointOnPlane((float)i * 0.35F, -0.25F);
            float f = entity.getAttackAnim(partialTicks);
            float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
            vec3 = vec3.scale(d7);
            vec3 = vec3.yRot(f1 * 0.5F);
            vec3 = vec3.xRot(-f1 * 0.7F);
            return vec3;
        }else{
            float yBodyRot = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
            Vec3 offset = new Vec3(entity.getBbWidth() * -0.5F * i, entity.getBbHeight() * 0.8F, 0).yRot((float) Math.toRadians(-yBodyRot));
            Vec3 armViewExtra = entity.getViewVector(partialTicks).normalize().scale(1.5F);
            return offset.add(armViewExtra);
        }
    }
}
