package com.github.alexmodguy.alexscaves.client.model;// Made with Blockbench 4.8.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.github.alexmodguy.alexscaves.client.gui.book.CaveBookScreen;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CaveBookModel extends AdvancedEntityModel {
    private final AdvancedModelBox root;
    private final AdvancedModelBox spine;
    private final AdvancedModelBox lcover;
    private final AdvancedModelBox lpageStack;
    private final AdvancedModelBox rcover;
    private final AdvancedModelBox rpageStack;
    private final AdvancedModelBox rpageOpen;
    private final AdvancedModelBox page_flip;

    public CaveBookModel() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        
        spine = new AdvancedModelBox(this);
        spine.setRotationPoint(0.0F, 0.0F, 0.0F);
        root.addChild(spine);
        spine.setTextureOffset(0, 49).addBox(-1.0F, -0.5F, -8.0F, 2.0F, 1.0F, 16.0F, 0.0F, false);

        lcover = new AdvancedModelBox(this);
        lcover.setRotationPoint(0.5F, 0.0F, 0.0F);
        root.addChild(lcover);
        lcover.setTextureOffset(0, 32).addBox(-0.5F, -1.0F, -8.0F, 13.0F, 1.0F, 16.0F, 0.0F, false);

        lpageStack = new AdvancedModelBox(this);
        lpageStack.setRotationPoint(0.5F, 0.0F, 0.0F);
        lcover.addChild(lpageStack);
        lpageStack.setTextureOffset(0, 16).addBox(-1.0F, -3.0F, -7.0F, 12.0F, 2.0F, 14.0F, 0.01F, false);
        lpageStack.setTextureOffset(-16, 0).addBox(-1.0F, -4.0F, -8.0F, 13.0F, 1.0F, 16.0F, 0.01F, false);

        rcover = new AdvancedModelBox(this);
        rcover.setRotationPoint(-0.5F, 0.0F, 0.0F);
        root.addChild(rcover);
        rcover.setTextureOffset(0, 32).addBox(-12.5F, -1.0F, -8.0F, 13.0F, 1.0F, 16.0F, 0.0F, true);

        rpageStack = new AdvancedModelBox(this);
        rpageStack.setRotationPoint(-0.5F, 0.0F, 0.0F);
        rcover.addChild(rpageStack);
        rpageStack.setTextureOffset(0, 16).addBox(-11.0F, -3.0F, -7.0F, 12.0F, 2.0F, 14.0F, 0.0F, true);
        rpageStack.setTextureOffset(-16, 0).addBox(-12.0F, -4.0F, -8.0F, 13.0F, 1.0F, 16.0F, 0.0F, true);

        rpageOpen = new AdvancedModelBox(this);
        rpageOpen.setRotationPoint(0.5F, -3.0F, 0.0F);
        rcover.addChild(rpageOpen);
        setRotateAngle(rpageOpen, 0.0F, 0.0F, 0.1309F);


        page_flip = new AdvancedModelBox(this);
        page_flip.setRotationPoint(0.0F, -3.0F, 0.0F);
        root.addChild(page_flip);
        setRotateAngle(page_flip, 0.0F, 0.0F, -1.5708F);
        page_flip.setTextureOffset(-16, 0).addBox(0.0F, -1.0F, -8.0F, 13.0F, 1.0F, 16.0F, 0.0F, false);  this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(Entity entity, float openAmount, float pageAngle, float pageUp, float bookRotateX, float bookRotateY) {
        this.resetToDefaultPose();
        float close = 1F - openAmount;
        progressRotationPrev(lcover, close, 0F, 0F, (float) Math.toRadians(-90F), 1F);
        progressRotationPrev(rcover, close, 0F, 0F, (float) Math.toRadians(90F), 1F);
        progressPositionPrev(lcover, close, 1.75F, 0, 0, 1F);
        progressPositionPrev(rcover, close, -1.75F, 0, 0, 1F);
        progressRotationPrev(root, close, 0F, 0, (float) Math.toRadians(-90F), 1F);
        this.root.rotateAngleX += (float) Math.toRadians(bookRotateX);
        this.root.rotateAngleZ += (float) Math.toRadians(bookRotateY);
        this.lcover.setScale(1.0F + close * 0.01F, 1.0F + close * 0.01F, 1.0F + close * 0.01F);
        if (openAmount < 1.0F) {
            this.page_flip.showModel = false;
        } else {
            this.page_flip.showModel = true;
            this.page_flip.rotateAngleZ = lcover.rotateAngleZ - pageAngle;
        }
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, rcover, lcover, rpageOpen, rpageStack, lpageStack, page_flip, spine);
    }

    public void mouseOver(float mouseLeanX, float mouseLeanY, float ageInTicks, float pageFlipAmount, boolean canGoLeft, boolean canGoRight) {
        float turnWobble = (float) ((Math.sin(ageInTicks * 0.2F) + 1F) * 0.5F) * (1F - pageFlipAmount);
        if (pageFlipAmount == 0) {
            if (mouseLeanX < -CaveBookScreen.MOUSE_LEAN_THRESHOLD  && canGoLeft) {
                float clamped = Mth.clamp((mouseLeanX + CaveBookScreen.MOUSE_LEAN_THRESHOLD) * 8, -1.0F, 0);
                this.page_flip.rotateAngleZ = lcover.rotateAngleZ + clamped * (float) Math.toRadians(turnWobble * 15F);
            }
            if (mouseLeanX > CaveBookScreen.MOUSE_LEAN_THRESHOLD && canGoRight) {
                float clamped = Mth.clamp((mouseLeanX - CaveBookScreen.MOUSE_LEAN_THRESHOLD) * 8, 0.0F, 1.0F);
                this.page_flip.rotateAngleZ = lcover.rotateAngleZ + (float) Math.PI + clamped * (float) Math.toRadians(turnWobble * 15F);
            }
        }
    }

    public void translateToPage(PoseStack poseStack, int kind) {
        root.translateAndRotate(poseStack);
        if (kind == 0) {
            lcover.translateAndRotate(poseStack);
            lpageStack.translateAndRotate(poseStack);
        } else if (kind == 1) {
            rcover.translateAndRotate(poseStack);
            rpageStack.translateAndRotate(poseStack);
        } else if (kind == 2) {
            page_flip.translateAndRotate(poseStack);
        }
    }
}