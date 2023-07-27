package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.render.misc.CaveMapRenderer;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin {

    private static final ModelResourceLocation MAP_FRAME_LOCATION = ModelResourceLocation.vanilla("item_frame", "map=true");
    private static final ModelResourceLocation GLOW_MAP_FRAME_LOCATION = ModelResourceLocation.vanilla("glow_item_frame", "map=true");

    @Shadow
    protected abstract void renderNameTag(ItemFrame entity, Component tag, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight);

    @Shadow
    protected abstract boolean shouldShowName(ItemFrame entity);

    @Shadow
    public abstract Vec3 getRenderOffset(ItemFrame entity, float partialTicks);

    @Shadow
    @Final
    private BlockRenderDispatcher blockRenderer;

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_renderArmWithItem(ItemFrame entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        ItemStack itemstack = entity.getItem();
        if (itemstack.is(ACItemRegistry.CAVE_MAP.get()) && CaveMapItem.isFilled(itemstack)) {
            ci.cancel();
            var renderNameTagEvent = new net.minecraftforge.client.event.RenderNameTagEvent(entity, entity.getDisplayName(), (ItemFrameRenderer) (Object) this, poseStack, bufferSource, packedLight, partialTicks);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameTagEvent);
            if (renderNameTagEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameTagEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || shouldShowName(entity))) {
                renderNameTag(entity, renderNameTagEvent.getContent(), poseStack, bufferSource, packedLight);
            }
            poseStack.pushPose();
            Direction direction = entity.getDirection();
            Vec3 vec3 = this.getRenderOffset(entity, partialTicks);
            poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
            poseStack.translate((double) direction.getStepX() * 0.46875D, (double) direction.getStepY() * 0.46875D, (double) direction.getStepZ() * 0.46875D);
            poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));
            if (!entity.isInvisible()) {
                ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
                ModelResourceLocation modelresourcelocation = entity.getType() == EntityType.GLOW_ITEM_FRAME ? GLOW_MAP_FRAME_LOCATION : MAP_FRAME_LOCATION;
                poseStack.pushPose();
                poseStack.translate(-0.5F, -0.5F, -0.5F);
                this.blockRenderer.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), (BlockState) null, modelmanager.getModel(modelresourcelocation), 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
            int j = entity.getRotation() % 4 * 2;
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) j * 360.0F / 8.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            float scale = 1F / 128F;
            poseStack.scale(scale, scale, scale);
            CaveMapRenderer.getMapFor(itemstack, false).render(poseStack, bufferSource, itemstack, true, packedLight);
            poseStack.popPose();
        }
    }
}