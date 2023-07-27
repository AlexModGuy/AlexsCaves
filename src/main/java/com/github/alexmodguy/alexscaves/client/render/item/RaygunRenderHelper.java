package com.github.alexmodguy.alexscaves.client.render.item;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.item.RaygunItem;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RaygunRenderHelper {

    private static final ResourceLocation RAYGUN_RAY = new ResourceLocation("alexscaves:textures/entity/raygun_ray.png");
    private static void renderRay(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 vec3, float useAmount, float offset) {
        float f2 = -1.0F * (offset * 0.15F % 1.0F);
        poseStack.pushPose();
        float f4 = (float) (vec3.length());
        vec3 = vec3.normalize();
        float f5 = (float) Math.acos(vec3.y);
        float f6 = (float) Math.atan2(vec3.z, vec3.x);
        poseStack.mulPose(Axis.YP.rotationDegrees(((Mth.PI / 2F) - f6) * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.XP.rotationDegrees(f5 * Mth.RAD_TO_DEG));
        float f8 = 1F;
        int j = (int) (f8 * 255.0F);
        int k = (int) (f8 * 255.0F);
        int l = (int) (f8 * 255.0F);
        float f11 = Mth.cos(0 + 2.3561945F) * 0.8F;
        float f12 = Mth.sin(0 + 2.3561945F) * 0.8F;
        float f13 = Mth.cos(0 + ACMath.QUARTER_PI) * 0.8F;
        float f14 = Mth.sin(0 + ACMath.QUARTER_PI) * 0.8F;
        float f15 = Mth.cos(0 + 3.926991F) * 0.8F;
        float f16 = Mth.sin(0 + 3.926991F) * 0.8F;
        float f17 = Mth.cos(0 + 5.4977875F) * 0.8F;
        float f18 = Mth.sin(0 + 5.4977875F) * 0.8F;
        float f19 = Mth.cos(0 + Mth.PI) * 0.4F;
        float f20 = Mth.sin(0 + Mth.PI) * 0.4F;
        float f21 = Mth.cos(0 + 0.0F) * 0.4F;
        float f22 = Mth.sin(0 + 0.0F) * 0.4F;
        float f23 = Mth.cos(0 + (Mth.PI / 2F)) * 0.4F;
        float f24 = Mth.sin(0 + (Mth.PI / 2F)) * 0.4F;
        float f25 = Mth.cos(0 + (Mth.PI * 1.5F)) * 0.4F;
        float f26 = Mth.sin(0 + (Mth.PI * 1.5F)) * 0.4F;
        float f29 = -1.0F + f2;
        float f30 = f4 * 1F + f29;
        VertexConsumer ivertexbuilder = bufferSource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(RAYGUN_RAY));
        PoseStack.Pose matrixstack$entry = poseStack.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();

        vertex(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 1F, f30);
        vertex(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 1F, f29);
        vertex(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
        vertex(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);

        vertex(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 1F, f30);
        vertex(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 1F, f29);
        vertex(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
        vertex(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, int p_229108_6_, int p_229108_7_, int p_229108_8_, float u, float v) {
        p_229108_0_.vertex(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).color(p_229108_6_, p_229108_7_, p_229108_8_, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public static void renderRaysFor(LivingEntity entity,  Vec3 rayFrom, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, boolean firstPerson) {
        if (entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RaygunItem) {
            ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
            float useRaygunAmount = RaygunItem.getUseTime(stack) / 5F;
            float ageInTicks = entity.tickCount + partialTick;
            float up = firstPerson ? 0F : entity.getEyeHeight();
            Vec3 rayPosition = RaygunItem.getLerpedRayPosition(stack, partialTick);
            if (rayPosition != null) {
                Vec3 gunPos = getGunOffset(entity, partialTick, firstPerson, entity.getMainArm() == HumanoidArm.LEFT);
                Vec3 vec3 = rayPosition.subtract(rayFrom.add(gunPos));
                poseStack.pushPose();
                poseStack.translate(gunPos.x, gunPos.y, gunPos.z);
                RaygunRenderHelper.renderRay(poseStack, bufferSource, vec3, useRaygunAmount, ageInTicks);
                poseStack.popPose();
            }
        }
        if (entity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RaygunItem) {
            ItemStack stack = entity.getItemInHand(InteractionHand.OFF_HAND);
            float useRaygunAmount = RaygunItem.getUseTime(stack) / 5F;
            float ageInTicks = entity.tickCount + partialTick;
            Vec3 rayPosition = RaygunItem.getLerpedRayPosition(stack, partialTick);
            if (rayPosition != null) {
                Vec3 gunPos = getGunOffset(entity, partialTick, firstPerson, entity.getMainArm() == HumanoidArm.RIGHT);
                Vec3 vec3 = rayPosition.subtract(rayFrom.add(gunPos));
                poseStack.pushPose();
                poseStack.translate(gunPos.x, gunPos.y, gunPos.z);
                RaygunRenderHelper.renderRay(poseStack, bufferSource, vec3, useRaygunAmount, ageInTicks);
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
