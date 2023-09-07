package com.github.alexmodguy.alexscaves.client.gui.book.widget;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public abstract class BookWidget {

    @Expose
    @SerializedName("display_page")
    private int displayPage;
    @Expose
    private Type type;
    @Expose
    private int x;
    @Expose
    private int y;
    @Expose
    private float scale;

    public BookWidget(int displayPage, Type type, int x, int y, float scale) {
        this.displayPage = displayPage;
        this.type = type;
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public int getDisplayPage() {
        return displayPage;
    }

    public Type getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getScale() {
        return scale;
    }

    public abstract void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTicks, boolean onFlippingPage);

    public enum Type {

        @SerializedName("image")
        IMAGE(ImageWidget.class),
        @SerializedName("item")
        ITEM(ItemWidget.class),
        @SerializedName("entity")
        ENTITY(EntityWidget.class),
        @SerializedName("entity_box")
        ENTITY_BOX(EntityBoxWidget.class),
        @SerializedName("crafting_recipe")
        CRAFTING_RECIPE(CraftingRecipeWidget.class);
        private final Class<? extends BookWidget> widgetClass;

        Type(Class<? extends BookWidget> widgetClass) {
            this.widgetClass = widgetClass;
        }

        public Class<? extends BookWidget> getWidgetClass() {
            return widgetClass;
        }

    }
}
