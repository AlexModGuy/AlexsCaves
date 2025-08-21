package com.github.alexmodguy.alexscaves.client.gui.book.widget;

import com.github.alexmodguy.alexscaves.client.gui.book.CaveBookScreen;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.item.ACItemstackRenderer;
import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWidget extends BookWidget {

    @Expose
    private String item;
    @Expose
    private String nbt;
    @Expose
    private boolean sepia;

    @Expose(serialize = false, deserialize = false)
    private ItemStack actualItem = ItemStack.EMPTY;

    private static final RenderType SEPIA_ITEM_RENDER_TYPE = ACRenderTypes.getBookWidget(TextureAtlas.LOCATION_BLOCKS, true);

    public ItemWidget(int displayPage, String item, String nbt, boolean sepia, int x, int y, float scale) {
        super(displayPage, Type.ITEM, x, y, scale);
        this.item = item;
        this.nbt = nbt;
        this.sepia = sepia;
    }

    public void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTicks, boolean onFlippingPage) {
        if (actualItem == null && item != null) {
            actualItem = new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(item)));
            if (nbt != null && !nbt.isEmpty()) {
                CompoundTag tag = null;
                try {
                    tag = TagParser.parseTag(nbt);
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
                actualItem.setTag(tag);
            }
        }
        float scale = 16.0F * getScale();

        poseStack.pushPose();
        poseStack.translate(getX(), getY(), 0);
        poseStack.translate(0, 0, 50);
        renderItem(actualItem, poseStack, bufferSource, sepia, scale);
        poseStack.popPose();

    }

    public static void renderItem(ItemStack itemStack, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, boolean sepia, float scale){
        if(itemStack == null){
            return;
        }
        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, Minecraft.getInstance().level, null, 0);
        poseStack.pushPose();
        try {
            poseStack.scale(scale, scale, scale);
            CaveBookScreen.fixLighting();
            if (!sepia) {
                poseStack.mulPose(Axis.YP.rotationDegrees(180F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
            } else {
                poseStack.mulPose(Axis.ZN.rotationDegrees(180F));
                poseStack.scale(-1F, 1F, 1F);
                ACItemstackRenderer.sepiaFlag = true;
            }
            if (sepia && !bakedmodel.isCustomRenderer()) {
                renderSepiaItem(poseStack, bakedmodel, itemStack, bufferSource);
            } else {
                Minecraft.getInstance().getItemRenderer().render(itemStack, ItemDisplayContext.GUI, false, poseStack, bufferSource, 240, OverlayTexture.NO_OVERLAY, bakedmodel);
            }
            if (sepia) {
                ACItemstackRenderer.sepiaFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        poseStack.popPose();
    }

    public static void renderSepiaItem(PoseStack poseStack, BakedModel bakedmodel, ItemStack itemStack, MultiBufferSource.BufferSource bufferSource){
        poseStack.pushPose();
        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(poseStack, bakedmodel, ItemDisplayContext.GUI, false);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(itemStack, false)) {
            renderModel(poseStack.last(), bufferSource.getBuffer(SEPIA_ITEM_RENDER_TYPE), 1.0F, null, bakedmodel, 1.0F, 1.0F, 1.0F, 240, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, rt);
        }
        poseStack.popPose();
    }
    private static void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, float alpha, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_, ModelData modelData, net.minecraft.client.renderer.RenderType renderType) {
        RandomSource randomsource = RandomSource.create();
        long i = 42L;

        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, alpha, p_111071_.getQuads(p_111070_, direction, randomsource, modelData, renderType), p_111075_, p_111076_);
        }

        randomsource.setSeed(42L);
        renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, alpha, p_111071_.getQuads(p_111070_, (Direction) null, randomsource, modelData, renderType), p_111075_, p_111076_);
    }

    private static void renderQuadList(PoseStack.Pose p_111059_, VertexConsumer p_111060_, float p_111061_, float p_111062_, float p_111063_, float alpha, List<BakedQuad> p_111064_, int p_111065_, int p_111066_) {
        for (BakedQuad bakedquad : p_111064_) {
            float f;
            float f1;
            float f2;
            f = Mth.clamp(p_111061_, 0.0F, 1.0F);
            f1 = Mth.clamp(p_111062_, 0.0F, 1.0F);
            f2 = Mth.clamp(p_111063_, 0.0F, 1.0F);
            p_111060_.putBulkData(p_111059_, bakedquad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, f, f1, f2, alpha, new int[]{p_111065_, p_111065_, p_111065_, p_111065_}, p_111066_, false);
        }

    }
}
