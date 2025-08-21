package com.github.alexmodguy.alexscaves.client.render.item.tooltip;

import com.github.alexmodguy.alexscaves.server.item.tooltip.SackOfSatingTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ClientSackOfSatingTooltip implements ClientTooltipComponent {

    private static final ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");
    private final SackOfSatingTooltip tooltipComponent;

    public ClientSackOfSatingTooltip(SackOfSatingTooltip tooltipComponent) {
        this.tooltipComponent = tooltipComponent;
    }

    @Override
    public int getHeight() {
        return tooltipComponent.getHungerValue() == 0 ? 0 : 11;
    }

    @Override
    public int getWidth(Font font) {
        return isTruncated() ? font.width(getHungerValueMultiplierText()) + 9 : Mth.ceil(tooltipComponent.getHungerValue() / 2.0D) * 9;
    }

    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int hungerValue = tooltipComponent.getHungerValue();
        int shanks = (int) Math.ceil(hungerValue / 2.0D);
        if (isTruncated()) {
            guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 16, 27, 9, 9);
            guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 52, 27, 9, 9);
            font.drawInBatch(getHungerValueMultiplierText(), (float)x + 10, (float)y + 1, 0XA8A8A8, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        } else {
            for (int i = 0; i < shanks; i++) {
                boolean halfShank = i == 0 && hungerValue % 2 == 1;
                //background outline
                guiGraphics.blit(GUI_ICONS_LOCATION, x + i * 9, y, 16, 27, 9, 9);
                guiGraphics.blit(GUI_ICONS_LOCATION, x + i * 9, y, halfShank ? 61 : 52, 27, 9, 9);
            }
        }
    }

    private boolean isTruncated() {
        return tooltipComponent.getHungerValue() >= 30;
    }

    private String getHungerValueMultiplierText(){
        int hungerValue = tooltipComponent.getHungerValue();
        double d = (hungerValue / 2.0D);
        String drawText = "x";
        if(d % 1.0D == 0.0D){
            drawText += (int)d;
        }else{
            drawText += d;
        }
        return drawText;
    }
}