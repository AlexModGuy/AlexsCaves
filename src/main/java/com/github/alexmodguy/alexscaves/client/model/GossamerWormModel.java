package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.GossamerWormEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class GossamerWormModel extends AdvancedEntityModel<GossamerWormEntity> {
    private final AdvancedModelBox head;
    private final AdvancedModelBox antennae;
    private final AdvancedModelBox antennae2;
    private final AdvancedModelBox segment;
    private final AdvancedModelBox rflipperFront;
    private final AdvancedModelBox rflipperBack;
    private final AdvancedModelBox lflipperFront;
    private final AdvancedModelBox lflipperBack;
    private final AdvancedModelBox segment2;
    private final AdvancedModelBox rflipperFront2;
    private final AdvancedModelBox rflipperBack2;
    private final AdvancedModelBox lflipperFront2;
    private final AdvancedModelBox lflipperBack2;
    private final AdvancedModelBox segment3;
    private final AdvancedModelBox rflipperFront3;
    private final AdvancedModelBox rflipperBack3;
    private final AdvancedModelBox lflipperFront3;
    private final AdvancedModelBox lflipperBack3;
    private final AdvancedModelBox segment4;
    private final AdvancedModelBox rflipperFront4;
    private final AdvancedModelBox rflipperBack4;
    private final AdvancedModelBox lflipperFront4;
    private final AdvancedModelBox lflipperBack4;
    private final AdvancedModelBox segment5;
    private final AdvancedModelBox rflipperBack5;
    private final AdvancedModelBox lflipperBack5;
    private final AdvancedModelBox rflipperFront5;
    private final AdvancedModelBox lflipperFront5;
    private final AdvancedModelBox segment6;
    private final AdvancedModelBox rflipperBack6;
    private final AdvancedModelBox lflipperBack6;
    private final AdvancedModelBox rflipperFront6;
    private final AdvancedModelBox lflipperFront6;
    private final AdvancedModelBox segment7;
    private final AdvancedModelBox rflipperBack7;
    private final AdvancedModelBox lflipperBack7;
    private final AdvancedModelBox rflipperFront7;
    private final AdvancedModelBox lflipperFront7;
    private final AdvancedModelBox segment8;
    private final AdvancedModelBox segment9;
    public boolean straighten;

    public GossamerWormModel() {
        texWidth = 128;
        texHeight = 128;

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 23.0F, -5.75F);
        head.setTextureOffset(0, 0).addBox(-4.0F, -1.0F, -5.25F, 8.0F, 2.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(0, 44).addBox(-8.0F, 0.0F, -13.25F, 16.0F, 0.0F, 8.0F, 0.0F, false);

        antennae = new AdvancedModelBox(this);
        antennae.setRotationPoint(4.0F, 0.0F, -4.25F);
        head.addChild(antennae);
        antennae.setTextureOffset(0, 0).addBox(-2.0F, 0.0F, -1.0F, 21.0F, 0.0F, 30.0F, 0.0F, false);

        antennae2 = new AdvancedModelBox(this);
        antennae2.setRotationPoint(-4.0F, 0.0F, -4.25F);
        head.addChild(antennae2);
        antennae2.setTextureOffset(0, 0).addBox(-19.0F, 0.0F, -1.0F, 21.0F, 0.0F, 30.0F, 0.0F, true);

        segment = new AdvancedModelBox(this);
        segment.setRotationPoint(0.0F, 0.0F, -0.75F);
        head.addChild(segment);
        segment.setTextureOffset(58, 70).addBox(-2.0F, -1.0F, 0.5F, 4.0F, 2.0F, 14.0F, 0.0F, false);

        rflipperFront = new AdvancedModelBox(this);
        rflipperFront.setRotationPoint(2.0F, 0.0F, 4.5F);
        segment.addChild(rflipperFront);
        rflipperFront.setTextureOffset(74, 68).addBox(0.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        rflipperBack = new AdvancedModelBox(this);
        rflipperBack.setRotationPoint(2.0F, 0.0F, 11.5F);
        segment.addChild(rflipperBack);
        rflipperBack.setTextureOffset(68, 48).addBox(0.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperFront = new AdvancedModelBox(this);
        lflipperFront.setRotationPoint(-2.0F, 0.0F, 4.5F);
        segment.addChild(lflipperFront);
        lflipperFront.setTextureOffset(68, 42).addBox(-17.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperBack = new AdvancedModelBox(this);
        lflipperBack.setRotationPoint(-2.0F, 0.0F, 11.5F);
        segment.addChild(lflipperBack);
        lflipperBack.setTextureOffset(66, 24).addBox(-17.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        segment2 = new AdvancedModelBox(this);
        segment2.setRotationPoint(0.0F, 0.0F, 14.0F);
        segment.addChild(segment2);
        segment2.setTextureOffset(58, 70).addBox(-2.0F, -1.0F, 0.5F, 4.0F, 2.0F, 14.0F, 0.0F, false);

        rflipperFront2 = new AdvancedModelBox(this);
        rflipperFront2.setRotationPoint(2.0F, 0.0F, 4.5F);
        segment2.addChild(rflipperFront2);
        rflipperFront2.setTextureOffset(66, 18).addBox(0.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        rflipperBack2 = new AdvancedModelBox(this);
        rflipperBack2.setRotationPoint(2.0F, 0.0F, 11.5F);
        segment2.addChild(rflipperBack2);
        rflipperBack2.setTextureOffset(66, 18).addBox(0.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperFront2 = new AdvancedModelBox(this);
        lflipperFront2.setRotationPoint(-2.0F, 0.0F, 4.5F);
        segment2.addChild(lflipperFront2);
        lflipperFront2.setTextureOffset(66, 6).addBox(-17.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperBack2 = new AdvancedModelBox(this);
        lflipperBack2.setRotationPoint(-2.0F, 0.0F, 11.5F);
        segment2.addChild(lflipperBack2);
        lflipperBack2.setTextureOffset(66, 6).addBox(-17.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        segment3 = new AdvancedModelBox(this);
        segment3.setRotationPoint(0.0F, 0.0F, 14.0F);
        segment2.addChild(segment3);
        segment3.setTextureOffset(58, 70).addBox(-2.0F, -1.0F, 0.5F, 4.0F, 2.0F, 14.0F, 0.0F, false);

        rflipperFront3 = new AdvancedModelBox(this);
        rflipperFront3.setRotationPoint(2.0F, 0.0F, 4.5F);
        segment3.addChild(rflipperFront3);
        rflipperFront3.setTextureOffset(66, 18).addBox(0.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        rflipperBack3 = new AdvancedModelBox(this);
        rflipperBack3.setRotationPoint(2.0F, 0.0F, 11.5F);
        segment3.addChild(rflipperBack3);
        rflipperBack3.setTextureOffset(66, 18).addBox(0.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperFront3 = new AdvancedModelBox(this);
        lflipperFront3.setRotationPoint(-2.0F, 0.0F, 4.5F);
        segment3.addChild(lflipperFront3);
        lflipperFront3.setTextureOffset(66, 6).addBox(-17.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperBack3 = new AdvancedModelBox(this);
        lflipperBack3.setRotationPoint(-2.0F, 0.0F, 11.5F);
        segment3.addChild(lflipperBack3);
        lflipperBack3.setTextureOffset(66, 6).addBox(-17.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        segment4 = new AdvancedModelBox(this);
        segment4.setRotationPoint(0.0F, 0.0F, 14.0F);
        segment3.addChild(segment4);
        segment4.setTextureOffset(58, 70).addBox(-2.0F, -1.0F, 0.5F, 4.0F, 2.0F, 14.0F, 0.0F, false);

        rflipperFront4 = new AdvancedModelBox(this);
        rflipperFront4.setRotationPoint(2.0F, 0.0F, 4.5F);
        segment4.addChild(rflipperFront4);
        rflipperFront4.setTextureOffset(68, 48).addBox(0.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        rflipperBack4 = new AdvancedModelBox(this);
        rflipperBack4.setRotationPoint(2.0F, 0.0F, 11.5F);
        segment4.addChild(rflipperBack4);
        rflipperBack4.setTextureOffset(68, 48).addBox(0.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperFront4 = new AdvancedModelBox(this);
        lflipperFront4.setRotationPoint(-2.0F, 0.0F, 4.5F);
        segment4.addChild(lflipperFront4);
        lflipperFront4.setTextureOffset(66, 24).addBox(-17.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperBack4 = new AdvancedModelBox(this);
        lflipperBack4.setRotationPoint(-2.0F, 0.0F, 11.5F);
        segment4.addChild(lflipperBack4);
        lflipperBack4.setTextureOffset(66, 24).addBox(-17.0F, 0.0F, -3.0F, 17.0F, 0.0F, 6.0F, 0.0F, false);

        segment5 = new AdvancedModelBox(this);
        segment5.setRotationPoint(0.0F, 0.0F, 14.0F);
        segment4.addChild(segment5);
        segment5.setTextureOffset(88, 86).addBox(-1.0F, -0.5F, 0.5F, 2.0F, 1.0F, 14.0F, 0.0F, false);

        rflipperBack5 = new AdvancedModelBox(this);
        rflipperBack5.setRotationPoint(1.0F, 0.0F, 11.5F);
        segment5.addChild(rflipperBack5);
        rflipperBack5.setTextureOffset(74, 68).addBox(-0.25F, 0.0F, -3.0F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperBack5 = new AdvancedModelBox(this);
        lflipperBack5.setRotationPoint(-1.0F, 0.0F, 11.5F);
        segment5.addChild(lflipperBack5);
        lflipperBack5.setTextureOffset(69, 42).addBox(-15.75F, 0.0F, -3.0F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        rflipperFront5 = new AdvancedModelBox(this);
        rflipperFront5.setRotationPoint(1.0F, 0.0F, 4.5F);
        segment5.addChild(rflipperFront5);
        rflipperFront5.setTextureOffset(74, 68).addBox(-0.25F, 0.0F, -3.0F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperFront5 = new AdvancedModelBox(this);
        lflipperFront5.setRotationPoint(-1.0F, 0.0F, 4.5F);
        segment5.addChild(lflipperFront5);
        lflipperFront5.setTextureOffset(69, 42).addBox(-15.75F, 0.0F, -3.0F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        segment6 = new AdvancedModelBox(this);
        segment6.setRotationPoint(0.0F, 0.0F, 14.0F);
        segment5.addChild(segment6);
        segment6.setTextureOffset(88, 86).addBox(-1.0F, -0.5F, 0.5F, 2.0F, 1.0F, 14.0F, 0.0F, false);

        rflipperBack6 = new AdvancedModelBox(this);
        rflipperBack6.setRotationPoint(1.0F, 0.0F, 10.0F);
        segment6.addChild(rflipperBack6);
        rflipperBack6.setTextureOffset(88, 80).addBox(-0.25F, 0.0F, -1.5F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperBack6 = new AdvancedModelBox(this);
        lflipperBack6.setRotationPoint(-1.0F, 0.0F, 10.0F);
        segment6.addChild(lflipperBack6);
        lflipperBack6.setTextureOffset(64, 86).addBox(-15.75F, 0.0F, -1.5F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        rflipperFront6 = new AdvancedModelBox(this);
        rflipperFront6.setRotationPoint(1.0F, 0.0F, 3.0F);
        segment6.addChild(rflipperFront6);
        rflipperFront6.setTextureOffset(32, 86).addBox(-0.25F, 0.0F, -1.5F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperFront6 = new AdvancedModelBox(this);
        lflipperFront6.setRotationPoint(-1.0F, 0.0F, 3.5F);
        segment6.addChild(lflipperFront6);
        lflipperFront6.setTextureOffset(84, 54).addBox(-15.75F, 0.0F, -2.0F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        segment7 = new AdvancedModelBox(this);
        segment7.setRotationPoint(0.0F, 0.0F, 14.0F);
        segment6.addChild(segment7);
        segment7.setTextureOffset(88, 86).addBox(-1.0F, -0.5F, 0.5F, 2.0F, 1.0F, 14.0F, 0.0F, false);

        rflipperBack7 = new AdvancedModelBox(this);
        rflipperBack7.setRotationPoint(1.0F, 0.0F, 10.0F);
        segment7.addChild(rflipperBack7);
        rflipperBack7.setTextureOffset(0, 84).addBox(-0.25F, 0.0F, -1.5F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperBack7 = new AdvancedModelBox(this);
        lflipperBack7.setRotationPoint(-1.0F, 0.0F, 10.0F);
        segment7.addChild(lflipperBack7);
        lflipperBack7.setTextureOffset(82, 36).addBox(-15.75F, 0.0F, -1.5F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        rflipperFront7 = new AdvancedModelBox(this);
        rflipperFront7.setRotationPoint(1.0F, 0.0F, 3.5F);
        segment7.addChild(rflipperFront7);
        rflipperFront7.setTextureOffset(82, 30).addBox(-0.25F, 0.0F, -2.0F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        lflipperFront7 = new AdvancedModelBox(this);
        lflipperFront7.setRotationPoint(-1.0F, 0.0F, 3.5F);
        segment7.addChild(lflipperFront7);
        lflipperFront7.setTextureOffset(74, 74).addBox(-15.75F, 0.0F, -2.0F, 16.0F, 0.0F, 6.0F, 0.0F, false);

        segment8 = new AdvancedModelBox(this);
        segment8.setRotationPoint(0.0F, 0.0F, 14.0F);
        segment7.addChild(segment8);
        segment8.setTextureOffset(20, 30).addBox(-5.0F, 0.0F, 0.5F, 10.0F, 0.0F, 14.0F, 0.0F, false);

        segment9 = new AdvancedModelBox(this);
        segment9.setRotationPoint(0.0F, 0.0F, 14.0F);
        segment8.addChild(segment9);
        segment9.setTextureOffset(0, 30).addBox(-5.0F, 0.0F, 0.5F, 10.0F, 0.0F, 14.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(head, antennae, antennae2, segment, rflipperFront, rflipperBack, lflipperFront, lflipperBack, segment2, rflipperFront2, rflipperBack2, lflipperFront2, lflipperBack2, segment3, rflipperFront3, rflipperBack3, lflipperFront3, lflipperBack3, segment4, rflipperFront4, rflipperBack4, lflipperFront4, lflipperBack4, segment5, rflipperBack5, lflipperBack5, rflipperFront5, lflipperFront5, segment6, rflipperBack6, lflipperBack6, rflipperFront6, lflipperFront6, segment7, rflipperBack7, lflipperBack7, rflipperFront7, lflipperFront7, segment8, segment9);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(head);
    }

    @Override
    public void setupAnim(GossamerWormEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float swimSpeed = 0.5F;
        float swimDegree = 0.5F;
        float squishAmount = entity.getSquishProgress(partialTicks);
        float swimAmount = 1F - squishAmount;
        float clampedYaw = netHeadYaw / 57.295776F;
        float headPitchAmount = headPitch / 57.295776F;
        float fishPitch = entity.getFishPitch(partialTicks);
        this.walk(head, 0.1F, 0.15F, true, 0F, 0F, ageInTicks, 1);
        this.walk(segment, 0.1F, 0.15F, false, 0F, 0F, ageInTicks, 1);
        this.walk(segment2, 0.1F, 0.05F, false, 1F, 0F, ageInTicks, swimAmount);
        this.walk(segment3, 0.1F, 0.05F, true, 2F, 0F, ageInTicks, swimAmount);
        this.walk(segment4, 0.1F, 0.05F, false, 3F, 0F, ageInTicks, swimAmount);
        this.walk(segment5, 0.1F, 0.05F, true, 4F, 0F, ageInTicks, swimAmount);
        this.walk(segment6, 0.1F, 0.05F, false, 5F, 0F, ageInTicks, swimAmount);
        this.walk(segment7, 0.1F, 0.05F, true, 6F, 0F, ageInTicks, swimAmount);
        this.swing(antennae, 0.1F, 0.15F, true, 1F, -0.1F, ageInTicks, 1);
        this.swing(antennae2, 0.1F, 0.15F, false, 1F, -0.1F, ageInTicks, 1);
        this.walk(segment, 0.8F, 0.05F, false, 3, 0.05F, ageInTicks, squishAmount);
        this.walk(segment3, 0.8F, 0.15F, false, 2, 0.05F, ageInTicks, squishAmount);
        this.walk(segment5, 0.8F, 0.05F, false, 1, 0.05F, ageInTicks, squishAmount);
        this.walk(segment7, 0.8F, 0.05F, false, 0F, 0.05F, ageInTicks, squishAmount);

        this.swing(rflipperFront, swimSpeed, swimDegree, true, 0F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperFront, swimSpeed, swimDegree, true, 0F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperBack, swimSpeed, swimDegree, true, 1F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperBack, swimSpeed, swimDegree, true, 1F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperFront2, swimSpeed, swimDegree, true, 2F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperFront2, swimSpeed, swimDegree, true, 2F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperBack2, swimSpeed, swimDegree, true, 3F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperBack2, swimSpeed, swimDegree, true, 3F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperFront3, swimSpeed, swimDegree, true, 4F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperFront3, swimSpeed, swimDegree, true, 4F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperBack3, swimSpeed, swimDegree, true, 5F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperBack3, swimSpeed, swimDegree, true, 5F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperFront4, swimSpeed, swimDegree, true, 6F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperFront4, swimSpeed, swimDegree, true, 6F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperBack4, swimSpeed, swimDegree, true, 7F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperBack4, swimSpeed, swimDegree, true, 7F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperFront5, swimSpeed, swimDegree, true, 7F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperFront5, swimSpeed, swimDegree, true, 7F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperBack5, swimSpeed, swimDegree, true, 8F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperBack5, swimSpeed, swimDegree, true, 8F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperFront6, swimSpeed, swimDegree, true, 9F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperFront6, swimSpeed, swimDegree, true, 9F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperBack6, swimSpeed, swimDegree, true, 10F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperBack6, swimSpeed, swimDegree, true, 10F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperFront7, swimSpeed, swimDegree, true, 11F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperFront7, swimSpeed, swimDegree, true, 11F, 0F, ageInTicks, swimAmount);
        this.swing(rflipperBack7, swimSpeed, swimDegree, true, 12F, 0F, ageInTicks, swimAmount);
        this.swing(lflipperBack7, swimSpeed, swimDegree, true, 12F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperFront, swimSpeed, swimDegree * 0.5F, true, 0F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperFront, swimSpeed, swimDegree * 0.5F, true, 0F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperBack, swimSpeed, swimDegree * 0.5F, true, 1F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperBack, swimSpeed, swimDegree * 0.5F, true, 1F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperFront2, swimSpeed, swimDegree * 0.5F, true, 2F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperFront2, swimSpeed, swimDegree * 0.5F, true, 2F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperBack2, swimSpeed, swimDegree * 0.5F, true, 3F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperBack2, swimSpeed, swimDegree * 0.5F, true, 3F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperFront3, swimSpeed, swimDegree * 0.5F, true, 4F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperFront3, swimSpeed, swimDegree * 0.5F, true, 4F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperBack3, swimSpeed, swimDegree * 0.5F, true, 5F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperBack3, swimSpeed, swimDegree * 0.5F, true, 5F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperFront4, swimSpeed, swimDegree * 0.5F, true, 6F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperFront4, swimSpeed, swimDegree * 0.5F, true, 6F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperBack4, swimSpeed, swimDegree * 0.5F, true, 7F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperBack4, swimSpeed, swimDegree * 0.5F, true, 7F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperFront5, swimSpeed, swimDegree * 0.5F, true, 7F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperFront5, swimSpeed, swimDegree * 0.5F, true, 7F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperBack5, swimSpeed, swimDegree * 0.5F, true, 8F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperBack5, swimSpeed, swimDegree * 0.5F, true, 8F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperFront6, swimSpeed, swimDegree * 0.5F, true, 9F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperFront6, swimSpeed, swimDegree * 0.5F, true, 9F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperBack6, swimSpeed, swimDegree * 0.5F, true, 10F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperBack6, swimSpeed, swimDegree * 0.5F, true, 10F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperFront7, swimSpeed, swimDegree * 0.5F, true, 11F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperFront7, swimSpeed, swimDegree * 0.5F, true, 11F, 0F, ageInTicks, swimAmount);
        this.flap(rflipperBack7, swimSpeed, swimDegree * 0.5F, true, 12F, 0F, ageInTicks, swimAmount);
        this.flap(lflipperBack7, swimSpeed, swimDegree * 0.5F, true, 12F, 0F, ageInTicks, swimAmount);
        if (!straighten) {
            double defaultX = Mth.wrapDegrees(fishPitch);
            double defaultY = Mth.wrapDegrees(entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks);
            double tail1X = (entity.getTrailTransformation(10, 0, partialTicks)) - defaultX;
            double tail1Y = (entity.getTrailTransformation(10, 1, partialTicks)) - defaultY;
            double tail2X = (entity.getTrailTransformation(20, 0, partialTicks)) - defaultX - tail1X;
            double tail2Y = (entity.getTrailTransformation(20, 1, partialTicks)) - defaultY - tail1Y;
            double tail3X = (entity.getTrailTransformation(30, 0, partialTicks)) - defaultX - tail2X;
            double tail3Y = (entity.getTrailTransformation(30, 1, partialTicks)) - defaultY - tail2Y;
            double tail4X = (entity.getTrailTransformation(40, 0, partialTicks)) - defaultX - tail3X;
            double tail4Y = (entity.getTrailTransformation(40, 1, partialTicks)) - defaultY - tail3Y;
            double tail5X = (entity.getTrailTransformation(50, 0, partialTicks)) - defaultX - tail4X;
            double tail5Y = (entity.getTrailTransformation(50, 1, partialTicks)) - defaultY - tail4Y;

            head.rotateAngleX += Math.toRadians(fishPitch);
            segment2.rotateAngleY += Math.toRadians(Mth.wrapDegrees(tail1Y) * 1);
            segment3.rotateAngleY += Math.toRadians(Mth.wrapDegrees(tail2Y) * 0.35F);
            segment4.rotateAngleY += Math.toRadians(Mth.wrapDegrees(tail2Y) * 0.35F);
            segment5.rotateAngleY += Math.toRadians(Mth.wrapDegrees(tail3Y) * 0.4F);
            segment6.rotateAngleY += Math.toRadians(Mth.wrapDegrees(tail4Y) * 0.4F);
            segment7.rotateAngleY += Math.toRadians(Mth.wrapDegrees(tail5Y) * 0.4F);

            segment2.rotateAngleX += Math.toRadians(tail1X * 1);
            segment3.rotateAngleX += Math.toRadians(tail2X * 0.5F);
            segment4.rotateAngleX += Math.toRadians(tail2X * 0.5F);
            segment5.rotateAngleX += Math.toRadians(tail3X * 0.35F);
            segment6.rotateAngleX += Math.toRadians(tail4X * 0.25F);
            segment7.rotateAngleX += Math.toRadians(tail5X * 0.25F);
        }

    }

}
