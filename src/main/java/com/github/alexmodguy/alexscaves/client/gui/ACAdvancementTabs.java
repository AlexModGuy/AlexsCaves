package com.github.alexmodguy.alexscaves.client.gui;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.util.Locale;

public class ACAdvancementTabs {

    private static final float MAX_TRANSITION_TIME = 25F;
    private static Type hoverType = Type.DEFAULT;
    private static Type previousHoverType = Type.DEFAULT;
    private static float hoverChangeProgress = MAX_TRANSITION_TIME;
    private static float previousHoverChangeProgress = MAX_TRANSITION_TIME;
    private static int windowWidth;
    private static int windowHeight;

    private static boolean[][] foregroundBlocks;

    public static boolean isAlexsCavesWidget(Advancement root) {
        return root.getId().getNamespace().equals(AlexsCaves.MODID);
    }

    public static void renderTabBackground(GuiGraphics guiGraphics, int topX, int topY, DisplayInfo displayInfo, double scrollX, double scrollY) {
        float partialTick = Minecraft.getInstance().getPartialTick();
        float hoverProgress = getHoverChangeAmount(partialTick);
        float priorHoverProgress = 1F - hoverProgress;
        int fastColor = FastColor.ARGB32.lerp(hoverProgress, previousHoverType.backgroundColor, hoverType.backgroundColor);
        guiGraphics.fill(0, 0, windowWidth + 100, windowHeight, fastColor | -16777216);
        renderTabBackgroundForType(guiGraphics, topX, topY, partialTick, scrollX, scrollY, previousHoverType, priorHoverProgress);
        renderTabBackgroundForType(guiGraphics, topX, topY, partialTick, scrollX, scrollY, hoverType, hoverProgress);
    }

    private static void renderTabBackgroundForType(GuiGraphics guiGraphics, int topX, int topY, float partialTick, double scrollX, double scrollY, Type type, float alpha) {
        guiGraphics.pose().pushPose();
        if (type != Type.DEFAULT) {
            int i = (int) Math.round(scrollX);
            int j = (int) Math.round(scrollY);
            for (int parallaxX = -1; parallaxX <= (windowWidth + 128) / 128; parallaxX++) {
                for (int parallaxY = -1; parallaxY <= (windowWidth + 128) / 128; parallaxY++) {
                    ColorBlitHelper.blitWithColor(guiGraphics, type.background, parallaxX * 128 + i / 4, parallaxY * 128 + j / 4, 0.0F, 0.0F, 128, 128, 128, 128, 1F, 1F, 1F, alpha);
                    ColorBlitHelper.blitWithColor(guiGraphics, type.midground, parallaxX * 128 + i / 2 - 1, parallaxY * 128 + j / 2, 0.0F, 0.0F, 128, 128, 128, 128, 1F, 1F, 1F, alpha);
                }
            }
        }
        int i = Mth.floor(scrollX);
        int j = Mth.floor(scrollY);
        int scrollPixelOffsetX = i % 16;
        int scrollPixelOffsetY = j % 16;
        int blockCoordOffsetX = i / 16;
        int blockCoordOffsetY = j / 16;
        int screenWidthInBlocks = windowWidth / 16 + 6;
        int screenHeightInBlocks = windowHeight / 16 + 6;
        for (int relativeBlockX = -2; relativeBlockX <= screenWidthInBlocks; relativeBlockX++) {
            for (int relativeBlockY = -2; relativeBlockY <= screenHeightInBlocks; relativeBlockY++) {
                int blockX = (relativeBlockX - blockCoordOffsetX);
                int blockY = (relativeBlockY - blockCoordOffsetY);
                if (type != Type.DEFAULT && isBlockCarvedOut(blockX, blockY, type)) {
                    continue;
                }
                ColorBlitHelper.blitWithColor(guiGraphics, type.baseStone, 16 * relativeBlockX + scrollPixelOffsetX, 16 * relativeBlockY + scrollPixelOffsetY, 0.0F, 0.0F, 16, 16, 16, 16, 1F, 1F, 1F, alpha);
            }
        }

        guiGraphics.pose().popPose();
    }

    private static boolean isBlockCarvedOut(int blockX, int blockY, Type type) {
        int biomeTypeOffset = type.ordinal() * 120;
        float noise = ACMath.sampleNoise2D(blockX + biomeTypeOffset, blockY + biomeTypeOffset, 20);
        return noise < -0.25F || noise > 0.25F;
    }

    public static void tick() {
        previousHoverChangeProgress = hoverChangeProgress;
        if (previousHoverType != hoverType) {
            if (hoverChangeProgress < MAX_TRANSITION_TIME) {
                hoverChangeProgress += 1F;
            } else if (hoverChangeProgress > MAX_TRANSITION_TIME) {
                previousHoverType = hoverType;
            }
        } else {
            hoverChangeProgress = MAX_TRANSITION_TIME;
        }
    }

    private static float getHoverChangeAmount(float partialTick) {
        return (previousHoverChangeProgress + (hoverChangeProgress - previousHoverChangeProgress) * partialTick) / MAX_TRANSITION_TIME;
    }

    public static void setHoverType(Type type) {
        if (hoverChangeProgress >= MAX_TRANSITION_TIME && type != hoverType) {
            previousHoverChangeProgress = 0.0F;
            hoverChangeProgress = 0.0F;
            previousHoverType = hoverType;
            hoverType = type;
        }
    }

    public static void setDimensions(int width, int height) {
        windowWidth = width;
        windowHeight = height;
    }

    public enum Type {
        DEFAULT(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,"alexscaves/root"), 0, ResourceLocation.withDefaultNamespace("textures/block/stone.png")),
        MAGNETIC(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,"alexscaves/discover_magnetic_caves"), 0X060607, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/block/galena.png")),
        PRIMORDIAL(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,"alexscaves/discover_primordial_caves"), 0XF2D860, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/block/limestone.png")),
        TOXIC(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,"alexscaves/discover_toxic_caves"), 0X7EFF00, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/block/radrock.png")),
        ABYSSAL(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,"alexscaves/discover_abyssal_chasm"), 0X011437, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/block/abyssmarine.png")),
        FORLORN(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,"alexscaves/discover_forlorn_hollows"), 0X15110E, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/block/guanostone.png")),
        CANDY(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,"alexscaves/discover_candy_cavity"), 0XF795CA, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/block/block_of_chocolate.png"));

        ResourceLocation root;

        private final int backgroundColor;
        private final ResourceLocation baseStone;
        private final ResourceLocation midground;
        private final ResourceLocation background;


        Type(ResourceLocation root, int backgroundColor, ResourceLocation baseStone) {
            this.root = root;
            this.backgroundColor = backgroundColor;
            this.baseStone = baseStone;
            this.midground = generateTexture("midground");
            this.background = generateTexture("background");
        }

        private ResourceLocation generateTexture(String type) {
            return this == DEFAULT ? null : ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/advancement/" + this.name().toLowerCase(Locale.ROOT) + "_" + type + ".png");
        }

        private static Type getDirectType(Advancement advancement) {
            for (Type type : values()) {
                if (type.root.equals(advancement.getId())) {
                    return type;
                }
            }
            return DEFAULT;
        }

        public static Type forAdvancement(Advancement advancement) {
            Type direct = getDirectType(advancement);
            Advancement next = advancement;
            while (direct == DEFAULT && next.getParent() != null) {
                next = next.getParent();
                direct = getDirectType(next);
            }
            return direct;
        }

        public static boolean isTreeNodeUnlocked(AdvancementWidget advancementWidget) {
            if (advancementWidget.progress.isDone()) {
                return true;
            }
            Type direct = getDirectType(advancementWidget.advancement);
            AdvancementWidget next = advancementWidget;
            while (direct == DEFAULT && next.advancement.getParent() != null) {
                next = next.parent;
                direct = getDirectType(next.advancement);
            }
            return direct == DEFAULT || next.progress != null && next.progress.isDone();
        }
    }
}
