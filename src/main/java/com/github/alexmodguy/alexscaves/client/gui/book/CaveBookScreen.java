package com.github.alexmodguy.alexscaves.client.gui.book;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.CaveBookModel;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.CaveBookProgress;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaveBookScreen extends Screen {

    private static final CaveBookModel BOOK_MODEL = new CaveBookModel();
    private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/gui/book/cave_book_model.png");
    public static final float MOUSE_LEAN_THRESHOLD = 0.75F;
    public static final int PAGE_SIZE_IN_LINES = 15;

    public static final int TEXT_COLOR = 0X826A41;
    public static final int TEXT_LINK_COLOR = 0X111111;
    public static final int TEXT_LINK_HOVER_COLOR = 0X0094FF;
    public static final int TEXT_LINK_LOCKED_COLOR = 0XD3C9AB;
    private final CaveBookProgress caveBookProgress;
    public boolean unlockTooltip;
    private boolean incrementingPage;
    private boolean decrementingPage;
    private float prevFlipProgress;
    private float flipProgress;
    private float prevOpenBookProgress;
    private float openBookProgress;
    private int tickCount = 0;
    private float flipSpeed = 0.1F;
    private int lastTurnClickTimestamp = -1;
    private boolean hoveringPageLeft;
    private boolean hoveringPageRight;
    protected ResourceLocation prevEntryJSON = null;
    protected ResourceLocation currentEntryJSON;
    protected ResourceLocation nextEntryJSON;

    private BookEntry currentEntry;
    private BookEntry prevEntry;
    private BookEntry nextEntry;
    private int entryPageNumber = 0;
    private int lastEntryPageBeforeLinkClick = -1;
    private int closeBookForTicks;
    private PageRenderer prevLeftPageRenderer = new PageRenderer(-2);
    private PageRenderer prevRightPageRenderer = new PageRenderer(-1);
    private PageRenderer leftPageRenderer = new PageRenderer(0);
    private PageRenderer rightPageRenderer = new PageRenderer(1);
    private PageRenderer nextLeftPageRenderer = new PageRenderer(2);
    private PageRenderer nextRightPageRenderer = new PageRenderer(3);

    public CaveBookScreen(String openTo) {
        super(Component.translatable("item.alexscaves.cave_book"));
        caveBookProgress = CaveBookProgress.getCaveBookProgress(Minecraft.getInstance().player);
        currentEntryJSON = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, openTo);
        resetEntry();
    }

    public CaveBookScreen(){
        this("books/root.json");
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void init() {
    }

    @Override
    public void tick() {
        prevFlipProgress = flipProgress;
        prevOpenBookProgress = openBookProgress;
        tickCount++;
        if (flipProgress < 1F && (openBookProgress >= 1F) && (decrementingPage || incrementingPage)) {
            flipProgress = Math.min(1.0F, flipProgress + flipSpeed);
        } else {
            if (incrementingPage) {
                if(nextEntryJSON != null){
                    this.prevEntryJSON = this.currentEntryJSON;
                    this.currentEntryJSON = nextEntryJSON;
                    this.nextEntryJSON = null;
                    this.nextEntry = null;
                    resetEntry();
                }else{
                    entryPageNumber++;
                }
                incrementingPage = false;
                updatePageRenderers();
            }
            if (decrementingPage) {
                entryPageNumber--;
                if(entryPageNumber < 0 && prevEntry != null){
                    int i = lastEntryPageBeforeLinkClick == -1 ? 0 : lastEntryPageBeforeLinkClick;
                    lastEntryPageBeforeLinkClick = -1;
                    this.currentEntryJSON = prevEntryJSON;
                    resetEntry();

                    this.entryPageNumber = i;
                }
                decrementingPage = false;
                updatePageRenderers();
            }
            prevFlipProgress = flipProgress = 0;
        }
        if (isBookOpened()) {
            if(openBookProgress == 0F){
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ACSoundRegistry.CAVE_BOOK_OPEN.get(), 1.0F));
            }
            if(openBookProgress < 1F){
                openBookProgress += 0.1F;
            }
        }else{
            if(openBookProgress > 0F){
                openBookProgress = Math.max(openBookProgress - 0.15F, 0);
            }
            if(closeBookForTicks > 0){
                closeBookForTicks--;
            }
            if(closeBookForTicks == 0 && nextEntryJSON != null){
                this.prevEntryJSON = this.currentEntryJSON;
                this.currentEntryJSON = nextEntryJSON;
                this.nextEntryJSON = null;
                this.nextEntry = null;
                resetEntry();
            }
        }
    }

    public void resetEntry() {
        if (currentEntryJSON != null) {
            currentEntry = readBookEntry(currentEntryJSON);
            if(currentEntry != null){
                currentEntry.init(this);
            }
        }
        if(this.currentEntry != null && this.currentEntry.getParent() != null && !this.currentEntry.getParent().isEmpty()){
            this.prevEntryJSON = ResourceLocation.parse(getBookFileDirectory() + this.currentEntry.getParent());
        }else{
            this.prevEntryJSON = null;
        }
        if (prevEntryJSON != null) {
            prevEntry = readBookEntry(prevEntryJSON);
            if(prevEntry != null){
                prevEntry.init(this);
            }
        }
        if (nextEntryJSON != null) {
            nextEntry = readBookEntry(nextEntryJSON);
            if(nextEntry != null){
                nextEntry.init(this);
            }
        }
        if (currentEntry != null) {
            entryPageNumber = 0;
        }
        updatePageRenderers();
    }

    public void updatePageRenderers() {
        boolean flag = prevEntryJSON != null && entryPageNumber == 0;
        int pgOffsetReturningFromLink = lastEntryPageBeforeLinkClick != -1 && entryPageNumber == 0 ? lastEntryPageBeforeLinkClick : 0;
        leftPageRenderer.setEntryPageNumber(entryPageNumber);
        leftPageRenderer.setEntry(currentEntry);
        rightPageRenderer.setEntryPageNumber(entryPageNumber);
        rightPageRenderer.setEntry(currentEntry);
        prevLeftPageRenderer.setEntryPageNumber(entryPageNumber + pgOffsetReturningFromLink);
        prevLeftPageRenderer.setEntry(entryPageNumber == 0 && prevEntry != null ? prevEntry : currentEntry);
        prevRightPageRenderer.setEntryPageNumber(entryPageNumber + pgOffsetReturningFromLink);
        prevRightPageRenderer.setEntry(entryPageNumber == 0 && prevEntry != null ? prevEntry : currentEntry);
        nextLeftPageRenderer.setEntryPageNumber(entryPageNumber);
        nextLeftPageRenderer.setEntry(nextEntryJSON != null ? nextEntry : currentEntry);
        nextRightPageRenderer.setEntryPageNumber(entryPageNumber);
        nextRightPageRenderer.setEntry(nextEntryJSON != null ? nextEntry : currentEntry);
        if(nextEntryJSON != null){
            nextLeftPageRenderer.enteringNewPageFlag = true;
            nextRightPageRenderer.enteringNewPageFlag = true;
        }else{
            nextLeftPageRenderer.enteringNewPageFlag = false;
            nextRightPageRenderer.enteringNewPageFlag = false;
        }
        if(flag){
            prevLeftPageRenderer.leavingNewPageFlag = true;
            prevRightPageRenderer.leavingNewPageFlag = true;
        }else{
            prevLeftPageRenderer.leavingNewPageFlag = false;
            prevRightPageRenderer.leavingNewPageFlag = false;
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, -1000, -1072689136, -804253680);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundRendered(this, guiGraphics));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float fakePartialTickThatsZeroForSomeReason) {
        float partialTick = Minecraft.getInstance().getPartialTick();
        PoseStack poseStack = guiGraphics.pose();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        float ageInTicks = tickCount + partialTick;
        float openGuiAmount = Math.min(ageInTicks, 5F) / 5F;
        float invOpenGuiAmount = 1F - openGuiAmount;
        float openBookAmount = prevOpenBookProgress + (openBookProgress - prevOpenBookProgress) * partialTick;
        float bookScale = 221 - invOpenGuiAmount * 180F;
        float flip = prevFlipProgress + (flipProgress - prevFlipProgress) * partialTick;
        float pageAngle = (float) Math.PI * (incrementingPage ? 1F - flip : flip);
        float pageFlipBump = (float) Math.sin(flip * Math.PI);
        float pageUp = incrementingPage ? 1F - flip : 0.0F;
        int i = this.width / 2;
        int j = this.height / 2;
        float mouseLeanX = (mouseX - i) / (float) bookScale;
        float mouseLeanY = (mouseY - j) / (float) bookScale;
        this.hoveringPageLeft = mouseLeanX < -MOUSE_LEAN_THRESHOLD && canGoLeft();
        this.hoveringPageRight = mouseLeanX > MOUSE_LEAN_THRESHOLD && canGoRight();
        poseStack.pushPose();
        poseStack.translate(i + invOpenGuiAmount * i * 0.5F, j + 6 + 15 * pageFlipBump, 100.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90F));
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(50 * invOpenGuiAmount));
        poseStack.scale(bookScale, bookScale, bookScale);
        poseStack.pushPose();
        BOOK_MODEL.setupAnim(null, openBookAmount, pageAngle, pageUp, -20 * (openBookAmount) - 10 * pageFlipBump, 0);
        BOOK_MODEL.mouseOver(mouseLeanX, mouseLeanY, ageInTicks, flip, canGoLeft(), canGoRight());
        BOOK_MODEL.renderToBuffer(poseStack, bufferSource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(BOOK_TEXTURE)), 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        renderBookContents(poseStack, mouseX, mouseY, partialTick);
        guiGraphics.flush();
        poseStack.popPose();
        poseStack.popPose();
        if(currentEntry != null){
            currentEntry.mouseOver(this, entryPageNumber, mouseLeanX, mouseLeanY);
        }
        super.render(guiGraphics, mouseX, mouseY, fakePartialTickThatsZeroForSomeReason);
        this.renderBackground(guiGraphics);
        if(unlockTooltip){
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("book.alexscaves.page_locked_0").withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable("book.alexscaves.page_locked_1").withStyle(ChatFormatting.GRAY));
            guiGraphics.renderTooltip(this.font, list, Optional.empty(), mouseX - 5, mouseY - 5);
        }
    }

    private void renderBookContents(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        boolean left = hoveringPageLeft || decrementingPage;
        boolean right = hoveringPageRight || incrementingPage;
        float flip = prevFlipProgress + (flipProgress - prevFlipProgress) * partialTick;
        if (left) {
            renderForPageType(leftPageRenderer, 2, poseStack, mouseX, mouseY, partialTick);
        } else if (flip < 0.9F) {
            renderForPageType(leftPageRenderer, 0, poseStack, mouseX, mouseY, partialTick);
        }
        if (right) {
            renderForPageType(rightPageRenderer, 3, poseStack, mouseX, mouseY, partialTick);
        } else if (flip < 0.9F) {
            renderForPageType(rightPageRenderer, 1, poseStack, mouseX, mouseY, partialTick);
        }
        if (incrementingPage) {
            renderForPageType(nextLeftPageRenderer, 2, poseStack, mouseX, mouseY, partialTick);
            if(flip > 0.1F){
                renderForPageType(nextRightPageRenderer, 1, poseStack, mouseX, mouseY, partialTick);
            }
        }
        if (decrementingPage) {
            renderForPageType(prevRightPageRenderer, 3, poseStack, mouseX, mouseY, partialTick);
            if(flip > 0.1F) {
                renderForPageType(prevLeftPageRenderer, 0, poseStack, mouseX, mouseY, partialTick);
            }
        }
    }

    //"kind" - what kind of transform. 0 = left page, 1 = right page, 2 = right side of flipping page, 3 = left side of flipping page
    private void renderForPageType(PageRenderer contents, int kind, PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        poseStack.pushPose();
        BOOK_MODEL.translateToPage(poseStack, Math.min(kind, 2));
        switch (kind) {
            case 0:
                poseStack.translate(-0.1F, -0.1885F, -0.005F);
                break;
            case 1:
                poseStack.translate(-0.725F, -0.1885F, -0.005F);
                break;
            case 2:
                poseStack.translate(-0.0375F, -0.015F, -0.01F);
                break;
            case 3:
                poseStack.translate(-0.7125F, 0.0054F, -0.005F);
                break;
        }
        poseStack.translate(0.75F, 0, 0.4F);
        poseStack.scale(0.005F, 0.005F, 0.005F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
        if (kind == 3) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        }
        poseStack.scale(1F, 1F, 0.01F);
        contents.renderPage(this, poseStack, mouseX, mouseY, partialTick, kind >= 2);
        poseStack.popPose();
    }


    private boolean isBookOpened() {
        return tickCount >= 12 && currentEntry != null && closeBookForTicks <= 0;
    }

    public boolean canGoLeft() {
        return isBookOpened() && (entryPageNumber > 0 || prevEntryJSON != null);
    }

    public boolean canGoRight() {
        return isBookOpened() && entryPageNumber + 1 < currentEntry.getPageCount();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = this.width / 2;
        float distFromMiddleX = (float) ((mouseX - i) / (float) 220);
        boolean prev = super.mouseClicked(mouseX, mouseY, button);
        if (!prev) {
            if(currentEntry != null && currentEntry.consumeMouseClick(this)){
                return true;
            }else{
                if (tickCount - lastTurnClickTimestamp < 8) {
                    flipSpeed = 0.3F;
                } else {
                    flipSpeed = 0.1F;
                }
                lastTurnClickTimestamp = tickCount;
                if (!decrementingPage && !incrementingPage) {
                    if (distFromMiddleX < -MOUSE_LEAN_THRESHOLD && canGoLeft()) {
                        decrementingPage = true;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ACSoundRegistry.CAVE_BOOK_TURN.get(), 1.0F));
                        return true;
                    } else if (distFromMiddleX > MOUSE_LEAN_THRESHOLD && canGoRight()) {
                        incrementingPage = true;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ACSoundRegistry.CAVE_BOOK_TURN.get(), 1.0F));
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    protected BookEntry readBookEntry(ResourceLocation res) {
        Optional<Resource> resource = null;
        BookEntry page = null;
        try {
            resource = Minecraft.getInstance().getResourceManager().getResource(res);
            if (resource.isPresent()) {
                BufferedReader inputstream = resource.get().openAsReader();
                page = BookEntry.deserialize(inputstream);
            }
        } catch (IOException e1) {
            if(!(e1 instanceof AccessDeniedException)){
                e1.printStackTrace();
            }
        }
        return page;
    }

    public CaveBookProgress getCaveBookProgress() {
        return caveBookProgress;
    }

    public int getEntryPageNumber() {
        return entryPageNumber;
    }

    public static String getBookFileDirectory() {
        return AlexsCaves.MODID + ":books/";
    }

    public boolean attemptChangePage(ResourceLocation changePageTo, boolean goingForwards) {
        if(!currentEntryJSON.equals(changePageTo)){
            lastEntryPageBeforeLinkClick = this.entryPageNumber;
        }
        if(goingForwards){
            prevEntryJSON = currentEntryJSON;
        }
        nextEntryJSON = changePageTo;
        if(goingForwards){
            closeBookForTicks = 10;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ACSoundRegistry.CAVE_BOOK_CLOSE.get(), 1.0F));
        }
        return  true;
    }

    public static void fixLighting(){
        Vector3f light0 = new Vector3f(1, 1.0F, -1.0F);
        Vector3f light1 = new Vector3f(1, 1.0F, -1.0F);
        RenderSystem.setShaderLights(light0, light1);
    }

    public int getEntryVisiblity(String linkTo) {
        ResourceLocation resourceLocation = ResourceLocation.parse(CaveBookScreen.getBookFileDirectory() + linkTo);
        BookEntry dummyEntry = readBookEntry(resourceLocation);
        int visiblity = 0;
        if(dummyEntry != null){
            visiblity = dummyEntry.getVisibility(this);
        }
        if(visiblity != 2 && Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative()){
            visiblity = 0;
        }
        return visiblity;
    }
}
