package com.github.alexmodguy.alexscaves.client.render.item;

import com.github.alexmodguy.alexscaves.client.model.OrtholanceModel;
import com.github.alexmodguy.alexscaves.client.model.SeaStaffModel;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ACItemstackRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation SEA_STAFF_TEXTURE = new ResourceLocation("alexscaves:textures/entity/deep_one/sea_staff.png");
    private static final SeaStaffModel SEA_STAFF_MODEL = new SeaStaffModel();

    private static final ResourceLocation ORTHOLANCE_TEXTURE = new ResourceLocation("alexscaves:textures/entity/deep_one/ortholance.png");
    private static final OrtholanceModel ORTHOLANCE_MODEL = new OrtholanceModel();

    public ACItemstackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ClientLevel level = Minecraft.getInstance().level;
        if (itemStackIn.is(ACItemRegistry.SEA_STAFF.get())) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.SEA_STAFF_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
                matrixStackIn.translate(0, -0.5F, 0);
                if(transformType.firstPerson()){
                    matrixStackIn.scale(0.6F, 0.6F, 0.6F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(SEA_STAFF_TEXTURE), false, itemStackIn.hasFoil());
                SEA_STAFF_MODEL.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }

        if (itemStackIn.is(ACItemRegistry.ORTHOLANCE.get())) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.ORTHOLANCE_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180));
                matrixStackIn.translate(0, -1.1F, 0);
                if(transformType.firstPerson()){
                    matrixStackIn.scale(0.6F, 1F, 0.6F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(ORTHOLANCE_TEXTURE), false, itemStackIn.hasFoil());
                ORTHOLANCE_MODEL.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            } else {
                Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
            }
        }
    }
}
