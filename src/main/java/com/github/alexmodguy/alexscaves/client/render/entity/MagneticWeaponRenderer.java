package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MagneticWeaponRenderer extends EntityRenderer<MagneticWeaponEntity> {

    private Map<UUID, LightningRender> lightningRenderMap = new HashMap<>();

    public MagneticWeaponRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    public void render(MagneticWeaponEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int i) {
        ItemStack itemStack = entity.getItemStack();
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = renderer.getModel(itemStack, entity.level(), null, 0);
        float ageInTicks = entity.tickCount + partialTicks;
        float strikeProgress = entity.getStrikeProgress(partialTicks);
        poseStack.pushPose();
        poseStack.translate(0, 0.1F + Math.sin(ageInTicks * 0.1F) * 0.1F, 0);
        Entity controller = entity.getController();
        if (controller instanceof Player player) {
            double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
            double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
            double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
            Vec3 fromVec;
            if (controller == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                int leftAmount = 1;
                ItemStack itemstack = player.getMainHandItem();
                if (!itemstack.is(ACItemRegistry.GALENA_GAUNTLET.get())) {
                    leftAmount = -leftAmount;
                }
                double d7 = 1000.0D / (double) this.entityRenderDispatcher.options.fov().get().intValue();
                float f = player.getAttackAnim(partialTicks);
                float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
                Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float) leftAmount * 0.525F, -0.7F);
                vec3 = vec3.scale(d7);
                vec3 = vec3.yRot(f1 * 0.5F);
                vec3 = vec3.xRot(-f1 * 0.7F);
                float d4 = (float) (Mth.lerp((double) partialTicks, player.xo, player.getX()) + vec3.x);
                float d5 = (float) (Mth.lerp((double) partialTicks, player.yo, player.getY()) + vec3.y);
                float d6 = (float) (Mth.lerp((double) partialTicks, player.zo, player.getZ()) + vec3.z);
                float f3 = player.getEyeHeight();
                Vec3 behind = controller.getViewVector(partialTicks).normalize().scale(-0.2F);
                fromVec = new Vec3(d4, d5 + f3, d6).add(behind);
            } else {
                fromVec = entity.getControllerHandPos(player, partialTicks);
            }
            Vec3 toVec = entity.position().add(0, 0.24F, 0);
            poseStack.pushPose();
            poseStack.translate(-x, -y, -z);
            int segCount = Mth.clamp((int) entity.distanceTo(player) + 2, 3, 30);
            float spreadFactor = Mth.clamp((10 - entity.distanceTo(player)) / 10F * 0.2F, 0.01F, 0.2F);
            float returnProgress = entity.getReturnProgress(partialTicks);
            LightningBoltData.BoltRenderInfo boltData = new LightningBoltData.BoltRenderInfo(0.0F, spreadFactor, 0.0F, 0.0F, new Vector4f(0.1F + returnProgress * 0.9F, 0.1F, 0.9F - returnProgress * 0.8F, 0.8F), 0.1F);
            LightningBoltData bolt1 = new LightningBoltData(boltData, fromVec, toVec, segCount)
                    .size(0.1F)
                    .lifespan(1)
                    .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
                    .fade(LightningBoltData.FadeFunction.NONE);
            LightningRender lightningRender = getLightingRender(entity.getUUID());
            lightningRender.update(entity, bolt1, partialTicks);

            lightningRender.render(partialTicks, poseStack, source);

            poseStack.popPose();
        }
        poseStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 180.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        poseStack.translate(0, strikeProgress * 0.1F, strikeProgress * 0.2F);
        poseStack.mulPose(Axis.XN.rotationDegrees(strikeProgress * 90F));
        Minecraft.getInstance().getItemRenderer().render(itemStack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, source, i, OverlayTexture.NO_OVERLAY, bakedModel);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, source, i);
        if (entity.isRemoved() && lightningRenderMap.containsKey(entity.getUUID())) {
            lightningRenderMap.remove(entity.getUUID());
        }
    }

    private LightningRender getLightingRender(UUID uuid) {
        if (lightningRenderMap.get(uuid) == null) {
            lightningRenderMap.put(uuid, new LightningRender());
        }
        return lightningRenderMap.get(uuid);
    }

    public boolean shouldRender(MagneticWeaponEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            Entity controller = entity.getController();
            if (controller != null) {
                Vec3 vec3 = entity.position();
                Vec3 vec31 = controller.position();
                return camera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
            }
            return controller instanceof Player;
        }
    }


    public ResourceLocation getTextureLocation(MagneticWeaponEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

