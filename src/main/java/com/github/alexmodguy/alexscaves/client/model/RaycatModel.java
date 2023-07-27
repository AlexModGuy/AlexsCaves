package com.github.alexmodguy.alexscaves.client.model;// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class RaycatModel extends AdvancedEntityModel<RaycatEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail2;

    public RaycatModel() {
        texWidth = 64;
        texHeight = 64;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 17.5F, 0.0F);
        body.setTextureOffset(28, 44).addBox(-2.5F, -3.5F, -7.0F, 5.0F, 7.0F, 13.0F, 0.25F, false);
        body.setTextureOffset(0, 34).addBox(-2.5F, -3.5F, -7.0F, 5.0F, 7.0F, 13.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -3.0F, -7.0F);
        body.addChild(head);
        head.setTextureOffset(20, 19).addBox(-2.5F, -1.5F, -5.0F, 5.0F, 5.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(40, 20).addBox(-2.5F, -0.5F, -5.0F, 5.0F, 2.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(38, 16).addBox(0.0F, -0.5F, -5.0F, 0.0F, 2.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(21, 0).addBox(-1.5F, 1.5F, -7.0F, 3.0F, 3.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(42, 16).addBox(-0.5F, 1.5F, -7.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(44, 54).addBox(-2.5F, -1.5F, -5.0F, 5.0F, 5.0F, 5.0F, 0.25F, false);
        head.setTextureOffset(44, 54).addBox(-2.75F, -2.75F, -1.75F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(44, 54).addBox(1.75F, -2.75F, -1.75F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        head.setTextureOffset(56, 60).addBox(-1.5F, 1.5F, -7.0F, 3.0F, 3.0F, 2.0F, 0.25F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-2.0F, -2.0F, -5.0F);
        body.addChild(rarm);
        rarm.setTextureOffset(56, 51).addBox(-1.0F, -2.5F, -1.0F, 2.0F, 11.0F, 2.0F, 0.25F, true);
        rarm.setTextureOffset(0, 34).addBox(-1.0F, -2.5F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, true);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(2.0F, -2.0F, -5.0F);
        body.addChild(larm);
        larm.setTextureOffset(56, 51).addBox(-1.0F, -2.5F, -1.0F, 2.0F, 11.0F, 2.0F, 0.25F, false);
        larm.setTextureOffset(0, 34).addBox(-1.0F, -2.5F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-1.5F, 2.0F, 5.0F);
        body.addChild(rleg);
        rleg.setTextureOffset(34, 0).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);
        rleg.setTextureOffset(56, 58).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 4.0F, 2.0F, 0.25F, true);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(1.5F, 2.0F, 5.0F);
        body.addChild(lleg);
        lleg.setTextureOffset(56, 58).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 4.0F, 2.0F, 0.25F, false);
        lleg.setTextureOffset(34, 0).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -2.5F, 6.0F);
        body.addChild(tail);
        tail.setTextureOffset(48, 55).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);
        tail.setTextureOffset(22, 0).addBox(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setRotationPoint(0.0F, 0.0F, 8.0F);
        tail.addChild(tail2);
        tail2.setTextureOffset(48, 55).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);
        tail2.setTextureOffset(42, 0).addBox(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 0.0F, false);

        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, tail, tail2, head, lleg, rleg, rarm, larm);
    }

    @Override
    public void setupAnim(RaycatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 0.7F;
        float walkDegree = 1F;
        float partialTicks = ageInTicks - entity.tickCount;
        float layProgress = entity.getLayProgress(partialTicks);
        float sitProgress = entity.getSitProgress(partialTicks) * (1F - layProgress);
        progressRotationPrev(body, sitProgress, (float) Math.toRadians(-35), 0, 0, 1F);
        progressRotationPrev(rleg, sitProgress, (float) Math.toRadians(-45), 0, 0, 1F);
        progressRotationPrev(lleg, sitProgress, (float) Math.toRadians(-45), 0, 0, 1F);
        progressPositionPrev(rarm, sitProgress, 0, 4, -1, 1F);
        progressPositionPrev(larm, sitProgress, 0, 4, -1, 1F);
        progressRotationPrev(rarm, sitProgress, (float) Math.toRadians(35), 0, 0, 1F);
        progressRotationPrev(larm, sitProgress, (float) Math.toRadians(35), 0, 0, 1F);
        progressRotationPrev(tail, sitProgress, (float) Math.toRadians(35), 0, 0, 1F);
        progressPositionPrev(head, sitProgress, 0, -1, -2, 1F);
        progressRotationPrev(head, sitProgress, (float) Math.toRadians(35), 0, 0, 1F);
        progressPositionPrev(body, layProgress, 0, 4, -1, 1F);
        progressRotationPrev(body, layProgress, (float) Math.toRadians(-30), 0, (float) Math.toRadians(90), 1F);
        progressPositionPrev(head, layProgress, -2, 2, 0, 1F);
        progressRotationPrev(head, layProgress, (float) Math.toRadians(-10), 0, (float) Math.toRadians(-75), 1F);
        progressRotationPrev(rleg, layProgress, (float) Math.toRadians(-45), 0, (float) Math.toRadians(-40), 1F);
        progressRotationPrev(lleg, layProgress, (float) Math.toRadians(25), 0, 0, 1F);
        progressPositionPrev(rarm, layProgress, -1, 1, 0, 1F);
        progressRotationPrev(rarm, layProgress, (float) Math.toRadians(15), 0, (float) Math.toRadians(-25), 1F);
        progressRotationPrev(larm, layProgress, (float) Math.toRadians(-15), 0, 0, 1F);
        this.walk(tail, 0.1F, 0.05F, true, 0F, 0.15F, ageInTicks, 1);
        this.swing(tail, 0.1F, 0.2F, false, -1F, 0F, ageInTicks, 1);
        this.swing(tail2, 0.1F, 0.3F, false, -2.5F, 0F, ageInTicks, 1);
        this.walk(head, 0.1F, 0.05F, true, 0F, 0.0F, ageInTicks, 1);
        this.walk(tail, walkSpeed, walkDegree * 0.1F, true, 0F, -0.2F, limbSwing, limbSwingAmount);
        this.walk(tail2, walkSpeed, walkDegree * 0.2F, true, 0F, -0.2F, limbSwing, limbSwingAmount);
        this.walk(rleg, walkSpeed, walkDegree, false, 0F, 0.0F, limbSwing, limbSwingAmount);
        this.walk(lleg, walkSpeed, walkDegree, true, 0F, 0.0F, limbSwing, limbSwingAmount);
        this.walk(rarm, walkSpeed, walkDegree * 0.8F, true, 0F, 0.0F, limbSwing, limbSwingAmount);
        this.walk(larm, walkSpeed, walkDegree * 0.8F, false, 0F, 0.0F, limbSwing, limbSwingAmount);
        this.faceTarget(netHeadYaw, headPitch, 1, head);
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }
}