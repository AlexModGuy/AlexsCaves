package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.NuclearFurnaceModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.block.NuclearFurnaceBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class NuclearFurnaceBlockRenderer<T extends NuclearFurnaceBlockEntity> implements BlockEntityRenderer<T> {

    private static final NuclearFurnaceModel MODEL = new NuclearFurnaceModel();
    private static final ResourceLocation OFF_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_off.png");
    private static final ResourceLocation ON_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_on.png");
    private static final ResourceLocation SUBCRITICAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_subcritical.png");
    private static final ResourceLocation CRITICAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_critical.png");
    private static final ResourceLocation SUPERCRITICAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_supercritical.png");

    private static final ResourceLocation OFF_TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_off_glow.png");
    private static final ResourceLocation ON_TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_on_glow.png");
    private static final ResourceLocation SUBCRITICAL_TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_subcritical_glow.png");
    private static final ResourceLocation CRITICAL_TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_critical_glow.png");
    private static final ResourceLocation SUPERCRITICAL_TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nuclear_furnace/nuclear_furnace_supercritical_glow.png");

    public NuclearFurnaceBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T furnace, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        BlockState state = furnace.getBlockState();
        Direction dir = state.getValue(NuclearFurnaceBlock.FACING);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.translate(0.0F, -1.5F, 0.0F);
        MODEL.setupAnim(null, dir.toYRot() - 180, furnace.getCriticality(), furnace.age + partialTicks, furnace.getWasteScale(), 0);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(getRenderTypeFor(furnace, false)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(getRenderTypeFor(furnace, true)), 240, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1);
        poseStack.popPose();
    }

    private RenderType getRenderTypeFor(T furnace, boolean glow) {
        if (!furnace.isUndergoingFission() && furnace.getCriticality() <= 0) {
            return glow ? ACRenderTypes.getEyesAlphaEnabled(OFF_TEXTURE_GLOW) : RenderType.entityCutoutNoCull(OFF_TEXTURE);
        } else {
            if (furnace.getCriticality() == 1) {
                return glow ? ACRenderTypes.getEyesAlphaEnabled(SUBCRITICAL_TEXTURE_GLOW) : RenderType.entityCutoutNoCull(SUBCRITICAL_TEXTURE);
            } else if (furnace.getCriticality() == 2) {
                return glow ? ACRenderTypes.getEyesAlphaEnabled(CRITICAL_TEXTURE_GLOW) : RenderType.entityCutoutNoCull(CRITICAL_TEXTURE);
            } else if (furnace.getCriticality() >= 3) {
                return glow ? ACRenderTypes.getEyesAlphaEnabled(SUPERCRITICAL_TEXTURE_GLOW) : RenderType.entityCutoutNoCull(SUPERCRITICAL_TEXTURE);
            }
            return glow ? ACRenderTypes.getEyesAlphaEnabled(ON_TEXTURE_GLOW) : RenderType.entityCutoutNoCull(ON_TEXTURE);
        }
    }
}