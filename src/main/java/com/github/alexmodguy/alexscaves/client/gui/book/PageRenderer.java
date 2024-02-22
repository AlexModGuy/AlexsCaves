package com.github.alexmodguy.alexscaves.client.gui.book;

import com.github.alexmodguy.alexscaves.client.gui.book.widget.BookWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

public class PageRenderer {

    public boolean enteringNewPageFlag;
    public boolean leavingNewPageFlag;
    private int relativePageNumber;
    private int entryPageNumber;

    private BookEntry entry;


    public PageRenderer(int relativePageNumber) {
        this.relativePageNumber = relativePageNumber;
    }

    public void setEntryPageNumber(int entryPageNumber) {
        this.entryPageNumber = entryPageNumber;
    }

    public int getDisplayPageNumber() {
        int i = relativePageNumber;
        if (enteringNewPageFlag) {
            i -= 2;
        }
        if (leavingNewPageFlag) {
            i += 2;
        }
        return 1 + i + (entryPageNumber * 2);
    }

    public void setEntry(BookEntry entry) {
        this.entry = entry;
    }

    protected void renderPage(CaveBookScreen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTicks, boolean onFlippingPage) {
        int pgNumber = getDisplayPageNumber();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        if(entry != null){
            if(pgNumber == 1 && !entry.getTranslatableTitle().isEmpty()){
                Component title = Component.translatable(entry.getTranslatableTitle());
                poseStack.pushPose();
                int titleLength = Math.max(screen.getMinecraft().font.width(title), 1);
                float titleScale =  Math.min(135F / (float) titleLength, 2.5F);
                poseStack.translate(65, 7 - 5 * titleScale, 0);
                poseStack.scale(titleScale, titleScale, 1F);
                poseStack.translate(-titleLength / 2F, 0, 0);
                screen.getMinecraft().font.drawInBatch8xOutline(title.getVisualOrderText(), 0.0F, 0.0F, 0XFFE7BF, 0XAA977F, poseStack.last().pose(), bufferSource, 15728880);
                poseStack.popPose();
            }
            if (!entry.getEntryText().isEmpty()) {
                int startReadingAt = (pgNumber - 1) * CaveBookScreen.PAGE_SIZE_IN_LINES;
                printLinesFromEntry(screen.getMinecraft().font, poseStack, bufferSource, entry, startReadingAt);
            }
            int numberWidth = screen.getMinecraft().font.width(String.valueOf(pgNumber));
            poseStack.pushPose();
            poseStack.scale(0.75F, 0.75F, 0.75F);
            screen.getMinecraft().font.drawInBatch(String.valueOf(pgNumber), 86.0F - numberWidth * 0.5F, 210.0F, CaveBookScreen.TEXT_COLOR, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            poseStack.popPose();
            for (BookWidget widget : entry.getWidgets()) {
                if (widget.getDisplayPage() == pgNumber) {
                    widget.render(poseStack, bufferSource, partialTicks, onFlippingPage);
                }
            }
        }
    }

    private void printLinesFromEntry(Font font, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, BookEntry bookEntry, int startReadingAt) {
        if (startReadingAt >= 0) {
            for (int i = startReadingAt; i < startReadingAt + CaveBookScreen.PAGE_SIZE_IN_LINES; i++) {
                if (bookEntry.getEntryText().size() > i) {
                    String printLine = bookEntry.getEntryText().get(i);
                    font.drawInBatch(printLine, 0.0F, (i - startReadingAt) * 10, CaveBookScreen.TEXT_COLOR, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                    for (BookLink bookLink : bookEntry.getEntryLinks()) {
                        if (bookLink.getLineNumber() == i) {
                            int fontWidth = font.width(printLine.substring(0, bookLink.getCharacterStartsAt()));
                            Component component = Component.literal(bookLink.getDisplayText()).withStyle(ChatFormatting.UNDERLINE);
                            font.drawInBatch(component, fontWidth, (i - startReadingAt) * 10, bookLink.isEnabled() ? bookLink.isHovered() ? CaveBookScreen.TEXT_LINK_HOVER_COLOR : CaveBookScreen.TEXT_LINK_COLOR : CaveBookScreen.TEXT_LINK_LOCKED_COLOR, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                        }
                    }
                }
            }
        }
    }
}
