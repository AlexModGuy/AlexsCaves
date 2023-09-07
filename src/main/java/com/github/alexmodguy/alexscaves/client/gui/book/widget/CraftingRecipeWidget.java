package com.github.alexmodguy.alexscaves.client.gui.book.widget;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.gui.book.CaveBookScreen;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.item.ACItemstackRenderer;
import com.github.alexthe666.citadel.recipe.SpecialRecipeInGuideBook;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class CraftingRecipeWidget extends BookWidget {

    @Expose
    @SerializedName("recipe_id")
    private String recipeId;
    @Expose
    private boolean sepia;

    @Expose(serialize = false, deserialize = false)
    private Recipe recipe;

    private static final int GRID_TEXTURE_SIZE = 64;

    @Expose(serialize = false, deserialize = false)
    private boolean smelting = false;

    private static final ResourceLocation CRAFTING_GRID_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/gui/book/crafting_grid.png");
    private static final ResourceLocation SMELTING_GRID_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/gui/book/smelting_grid.png");

    public CraftingRecipeWidget(int displayPage, String recipeId, boolean sepia, int x, int y, float scale) {
        super(displayPage, Type.CRAFTING_RECIPE, x, y, scale);
        this.recipeId = recipeId;
        this.sepia = sepia;
    }

    public void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTicks, boolean onFlippingPage) {
        if (recipe == null && recipeId != null) {
            recipe = getRecipeByName(recipeId);
            if(recipe instanceof AbstractCookingRecipe){
                smelting = true;
            }
        }
        if(recipe != null){
            float itemScale = 16.0F;
            float playerTicks = Minecraft.getInstance().player.tickCount;
            VertexConsumer vertexconsumer = bufferSource.getBuffer(ACRenderTypes.getBookWidget(smelting ? SMELTING_GRID_TEXTURE : CRAFTING_GRID_TEXTURE, sepia));
            poseStack.pushPose();
            poseStack.translate(getX(), getY(), 0);
            poseStack.scale(getScale(), getScale(), 1);
            poseStack.pushPose();
            poseStack.scale(1.5F, 1.5F, 1);
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            float scaledU1 = 55 / (float)GRID_TEXTURE_SIZE;
            float scaledV1 = 37 / (float)GRID_TEXTURE_SIZE;
            float texWidth = 55 / 2F;
            float texHeight = 37 / 2F;
            vertexconsumer.vertex(matrix4f, -texWidth, -texHeight, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0, 0).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, texWidth, -texHeight, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(scaledU1, 0).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).uv(0, scaledV1).endVertex();
            vertexconsumer.vertex(matrix4f, texWidth, texHeight, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(scaledU1, scaledV1).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).uv(0, 0).endVertex();
            vertexconsumer.vertex(matrix4f, -texWidth, texHeight, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0, scaledV1).overlayCoords(NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, 1.0F, 0.0F).uv(scaledU1, 0).endVertex();
            poseStack.popPose();


            if(smelting){
                poseStack.pushPose();
                poseStack.translate(43, -15, 0);
                poseStack.scale(1.35F, 1.35F, 1);
                ItemWidget.renderItem(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()), poseStack, bufferSource, sepia, itemScale * 1.25F);
                poseStack.popPose();

                Ingredient ing = (Ingredient) recipe.getIngredients().get(0);
                ItemStack stack = ItemStack.EMPTY;
                if (!ing.isEmpty()) {
                    if (ing.getItems().length > 1) {
                        int currentIndex = (int) ((playerTicks / 20F) % ing.getItems().length);
                        stack = ing.getItems()[currentIndex];
                    } else {
                        stack = ing.getItems()[0];
                    }
                }

                poseStack.pushPose();
                poseStack.translate(-27.5F, -12.5F, 0);
                ItemWidget.renderItem(stack, poseStack, bufferSource, sepia, itemScale);
                poseStack.popPose();

            }else{
                poseStack.pushPose();
                poseStack.translate(57, 2, 0);
                poseStack.scale(1.35F, 1.35F, 1);
                ItemWidget.renderItem(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()), poseStack, bufferSource, sepia, itemScale * 1.25F);
                poseStack.popPose();

                NonNullList<Ingredient> ingredients = recipe instanceof SpecialRecipeInGuideBook ? ((SpecialRecipeInGuideBook)recipe).getDisplayIngredients() : recipe.getIngredients();
                NonNullList<ItemStack> displayedStacks = NonNullList.create();

                for (int i = 0; i < ingredients.size(); i++) {
                    Ingredient ing = ingredients.get(i);
                    ItemStack stack = ItemStack.EMPTY;
                    if (!ing.isEmpty()) {
                        if (ing.getItems().length > 1) {
                            int currentIndex = (int) ((playerTicks / 20F) % ing.getItems().length);
                            stack = ing.getItems()[currentIndex];
                        } else {
                            stack = ing.getItems()[0];
                        }
                    }
                    if (!stack.isEmpty()) {
                        poseStack.pushPose();
                        poseStack.translate(-33 + (int) (i % 3) * 18.75F, -18.5F + (int) (i / 3) * 19.5F, 0);
                        ItemWidget.renderItem(stack, poseStack, bufferSource, sepia, itemScale);
                        poseStack.popPose();
                    }
                    displayedStacks.add(i, stack);
                }
            }
            poseStack.popPose();
        }
    }

    private Recipe getRecipeByName(String registryName) {
        try {
            RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
            if (manager.byKey(new ResourceLocation(registryName)).isPresent()) {
                return manager.byKey(new ResourceLocation(registryName)).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
