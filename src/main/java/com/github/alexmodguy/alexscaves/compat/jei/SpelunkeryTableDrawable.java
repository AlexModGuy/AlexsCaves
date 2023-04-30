package com.github.alexmodguy.alexscaves.compat.jei;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class SpelunkeryTableDrawable implements IDrawable {

    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/gui/spelunkery_table_jei.png");
    private static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(AlexsCaves.MODID, "textures/gui/spelunkery_table_widgets.png");
    @Override
    public int getWidth() {
        return 136;
    }

    @Override
    public int getHeight() {
        return 27;
    }

    @Override
    public void draw(PoseStack poseStack, int xOffset, int yOffset) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = xOffset;
        int j = yOffset;
        GuiComponent.blit(poseStack, i, j, 0, 0, getWidth(), getHeight(), 256, 256);
        RenderSystem.setShaderTexture(0, TEXTURE_WIDGETS);
        int bulbs = Minecraft.getInstance().player.tickCount % 40 / 10;
        for (int bulb = 0; bulb < bulbs; bulb++) {
            GuiComponent.blit(poseStack, i + 56 + bulb * 15, j + 7, 0, 0, 13, 14);
        }
    }
}