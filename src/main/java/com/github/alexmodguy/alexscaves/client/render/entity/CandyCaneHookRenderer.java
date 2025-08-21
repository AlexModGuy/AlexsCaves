package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.CandyCaneHookModel;
import com.github.alexmodguy.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.github.alexmodguy.alexscaves.server.item.CandyCaneHookItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class CandyCaneHookRenderer extends EntityRenderer<CandyCaneHookEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/candy_cane_hook.png");
    private static final CandyCaneHookModel MODEL = new CandyCaneHookModel();

    private static final float LICORICE_COLOR_1_R = 80 / 255F;
    private static final float LICORICE_COLOR_1_G = 0;
    private static final float LICORICE_COLOR_1_B = 104 / 255F;
    private static final float LICORICE_COLOR_2_R = 34 / 255F;
    private static final float LICORICE_COLOR_2_G = 0;
    private static final float LICORICE_COLOR_2_B = 45 / 255F;

    public CandyCaneHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public boolean shouldRender(CandyCaneHookEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            Entity owner = entity.getOwner();
            if (owner != null) {
                Vec3 vec31 = entity.getPosition(1.0F);
                Vec3 vec32 = owner.getPosition(1.0F);
                return camera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec32.x, vec32.y, vec32.z));
            }
            return false;
        }
    }

    public void render(CandyCaneHookEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int lighting) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        poseStack.translate(0.0D, 1.5F, -0.15D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        float ageInTicks = entity.tickCount + partialTicks;
        RenderType renderType = RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
        VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
        MODEL.setupAnim(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
        MODEL.renderToBuffer(poseStack, vertexconsumer, lighting, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        //fishng rod stuff
        Player player = entity.getPlayerOwner();
        if (player != null) {
            poseStack.pushPose();
            int i = 0;
            if (entity.getHandLaunchedFrom() == InteractionHand.MAIN_HAND) {
                i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            } else if (entity.getHandLaunchedFrom() == InteractionHand.OFF_HAND) {
                i = player.getMainArm() == HumanoidArm.LEFT ? 1 : -1;
            }
            float f = player.getAttackAnim(partialTicks);
            float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
            float f2 = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float) Math.PI / 180F);
            boolean wormRiding = player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof CandyCaneHookItem && CandyCaneHookItem.isActive(player.getItemInHand(InteractionHand.MAIN_HAND)) && player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof CandyCaneHookItem && CandyCaneHookItem.isActive(player.getItemInHand(InteractionHand.OFF_HAND)) && player.getVehicle() instanceof GumWormSegmentEntity;
            float rightWiggle = -Math.min(player.xxa, 0F) * (float) Math.sin(player.tickCount + AlexsCaves.PROXY.getPartialTicks());
            float leftWiggle = Math.max(player.xxa, 0F) * (float) Math.sin(player.tickCount + AlexsCaves.PROXY.getPartialTicks());
            float wiggle = i == 1 ? rightWiggle : i == -1 ? leftWiggle : 0;
            double d0 = Mth.sin(f2);
            double d1 = Mth.cos(f2);
            double d2 = (double) i * (wormRiding ? 0.45D : 0.35D);
            double d3 = 0.8D;
            double d4;
            double d5;
            double d6;
            float f3;
            if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && player == Minecraft.getInstance().player) {
                double d7 = 960.0D / (double) this.entityRenderDispatcher.options.fov().get().intValue();
                Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float) i * 0.7F, -0.5F);
                vec3 = vec3.scale(d7);
                vec3 = vec3.yRot(f1 * 0.5F);
                vec3 = vec3.xRot(-f1 * 0.7F);
                d4 = Mth.lerp(partialTicks, player.xo, player.getX()) + vec3.x;
                d5 = Mth.lerp(partialTicks, player.yo, player.getY()) + vec3.y;
                d6 = Mth.lerp(partialTicks, player.zo, player.getZ()) + vec3.z;
                f3 = player.getEyeHeight();
            } else {
                double yDown = wormRiding ? -0.2D + 0.15D * wiggle : 0.55D;
                d4 = Mth.lerp(partialTicks, player.xo, player.getX()) - d1 * d2 - d0 * 0.45D;
                d5 = player.yo + (double) player.getEyeHeight() + (player.getY() - player.yo) * (double) partialTicks - yDown;
                d6 = Mth.lerp(partialTicks, player.zo, player.getZ()) - d0 * d2 + d1 * 0.45D;
                f3 = player.isCrouching() ? -0.1875F : 0.0F;
            }

            double d9 = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double d10 = Mth.lerp(partialTicks, entity.yo, entity.getY()) + 0.25D;
            double d8 = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            float f4 = (float) (d4 - d9);
            float f5 = (float) (d5 - d10) + f3;
            float f6 = (float) (d6 - d8);
            Vec3 rotationalVec = new Vec3(0F, 0.15F, -0.45F).xRot((float) Math.toRadians(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()))).yRot((float) Math.toRadians(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
            renderLicoriceString(entity, new Vec3(f4, f5, f6), poseStack, bufferSource, rotationalVec);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, lighting);
    }


    public void renderLicoriceString(CandyCaneHookEntity from, Vec3 fromVec, PoseStack poseStack, MultiBufferSource bufferSource, Vec3 to) {
        poseStack.pushPose();
        double d3 = fromVec.x;
        double d4 = fromVec.y;
        double d5 = fromVec.z;
        poseStack.translate(d3, d4, d5);
        float f = (float) (to.x - d3);
        float f1 = (float) (to.y - d4);
        float f2 = (float) (to.z - d5);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix4f = poseStack.last().pose();
        BlockPos blockpos = BlockPos.containing(fromVec);
        BlockPos blockpos1 = BlockPos.containing(to);
        int i = from.level().getBrightness(LightLayer.BLOCK, blockpos);
        int j = from.level().getBrightness(LightLayer.BLOCK, blockpos1);
        int k = from.level().getBrightness(LightLayer.SKY, blockpos);
        int l = from.level().getBrightness(LightLayer.SKY, blockpos1);
        for (int i1 = 0; i1 <= 32; ++i1) {
            float width = 0.07F - (i1 / 32F) * 0.035F;
            addLicoriceStringVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.2F, width, width, width, i1);
        }
        poseStack.popPose();
    }

    private static void addLicoriceStringVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix4f, float x, float y, float z, int blockLightFrom, int blockLightTo, int skyLightFrom, int skyLightTo, float height, float yWidth, float xWidth, float zWidth, int vertIndex) {
        float f = (float) vertIndex / 32.0F;
        int i = (int) Mth.lerp(f, (float) blockLightFrom, (float) blockLightTo);
        int j = (int) Mth.lerp(f, (float) skyLightFrom, (float) skyLightTo);
        int k = LightTexture.pack(i, j);
        boolean colorScheme = vertIndex % 2 == 0;
        float f2 = colorScheme ? LICORICE_COLOR_1_R : LICORICE_COLOR_2_R;
        float f3 = colorScheme ? LICORICE_COLOR_1_G : LICORICE_COLOR_2_G;
        float f4 = colorScheme ? LICORICE_COLOR_1_B : LICORICE_COLOR_2_B;
        float f5 = x * f;
        float f6 = y < 0.0F ? y * f * f : y - y * (1.0F - f) * (1.0F - f);
        float f7 = z * f;
        vertexConsumer.vertex(matrix4f, f5 - xWidth, f6 + yWidth, f7 + zWidth).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
        vertexConsumer.vertex(matrix4f, f5 + xWidth, f6 + height - yWidth, f7 - zWidth).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(CandyCaneHookEntity entity) {
        return TEXTURE;
    }

}