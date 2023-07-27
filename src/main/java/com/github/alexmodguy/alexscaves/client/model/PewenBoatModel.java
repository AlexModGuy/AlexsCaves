package com.github.alexmodguy.alexscaves.client.model;// Made with Blockbench 4.7.4
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.vehicle.Boat;

public class PewenBoatModel extends ACBoatModel {
    private final AdvancedModelBox bottom;
    private final AdvancedModelBox right;
    private final AdvancedModelBox left;
    private final AdvancedModelBox back;
    private final AdvancedModelBox front;
    private final AdvancedModelBox paddle_left;
    private final AdvancedModelBox paddle_right;
    private final AdvancedModelBox water_mask;
    private final AdvancedModelBox bb_main;

    public PewenBoatModel() {
        texWidth = 128;
        texHeight = 128;

        bottom = new AdvancedModelBox(this);
        bottom.setRotationPoint(0.0F, 22.5F, 2.0F);
        bottom.setTextureOffset(0, 0).addBox(-10.0F, -1.5F, -17.0F, 20.0F, 3.0F, 32.0F, 0.0F, false);
        bottom.setTextureOffset(97, 65).addBox(6.0F, 5.5F, 2.0F, 2.0F, 2.0F, 2.0F, 0.0F, true);
        bottom.setTextureOffset(80, 49).addBox(1.0F, 1.5F, 2.0F, 7.0F, 4.0F, 8.0F, 0.0F, true);
        bottom.setTextureOffset(80, 49).addBox(-8.0F, 1.5F, 2.0F, 7.0F, 4.0F, 8.0F, 0.0F, false);
        bottom.setTextureOffset(97, 65).addBox(-8.0F, 5.5F, 2.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        right = new AdvancedModelBox(this);
        right.setRotationPoint(0.0F, 24.0F, 0.0F);
        right.setTextureOffset(68, 72).addBox(-10.0F, -16.0F, 3.0F, 2.0F, 13.0F, 12.0F, 0.0F, false);
        right.setTextureOffset(61, 45).addBox(-10.0F, -11.0F, -13.0F, 2.0F, 8.0F, 16.0F, 0.0F, true);

        left = new AdvancedModelBox(this);
        left.setRotationPoint(0.0F, 24.0F, 0.0F);
        left.setTextureOffset(0, 61).addBox(8.0F, -11.0F, -13.0F, 2.0F, 8.0F, 16.0F, 0.0F, false);
        left.setTextureOffset(0, 0).addBox(8.0F, -16.0F, 3.0F, 2.0F, 13.0F, 12.0F, 0.0F, false);

        back = new AdvancedModelBox(this);
        back.setRotationPoint(0.0F, 14.5F, 16.0F);
        back.setTextureOffset(36, 69).addBox(-10.0F, -6.5F, -1.0F, 20.0F, 13.0F, 2.0F, 0.0F, false);

        front = new AdvancedModelBox(this);
        front.setRotationPoint(0.0F, 24.0F, 0.0F);
        front.setTextureOffset(72, 0).addBox(-10.0F, -11.0F, -15.0F, 20.0F, 8.0F, 2.0F, 0.0F, false);

        paddle_left = new AdvancedModelBox(this);
        paddle_left.setRotationPoint(10.5F, 12.25F, 1.0F);
        setRotateAngle(paddle_left, 0.0F, 0.0F, 0.5236F);
        paddle_left.setTextureOffset(46, 100).addBox(-6.0F, -1.25F, -1.0F, 18.0F, 2.0F, 2.0F, 0.0F, false);
        paddle_left.setTextureOffset(72, 25).addBox(8.0F, -4.25F, -0.9F, 7.0F, 6.0F, 1.0F, 0.0F, true);

        paddle_right = new AdvancedModelBox(this);
        paddle_right.setRotationPoint(-10.5F, 12.25F, 1.0F);
        setRotateAngle(paddle_right, 0.0F, 0.0F, -0.5236F);
        paddle_right.setTextureOffset(46, 100).addBox(-12.0F, -1.25F, -1.0F, 18.0F, 2.0F, 2.0F, 0.0F, true);
        paddle_right.setTextureOffset(72, 25).addBox(-15.0F, -4.25F, -0.9F, 7.0F, 6.0F, 1.0F, 0.0F, false);

        water_mask = new AdvancedModelBox(this);
        water_mask.setRotationPoint(0.0F, 25.0F, 0.0F);
        water_mask.setTextureOffset(0, 0).addBox(-8.0F, -13.0F, -13.0F, 16.0F, 10.0F, 28.0F, 0.0F, false);

        bb_main = new AdvancedModelBox(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
        bb_main.setTextureOffset(0, 35).addBox(-10.0F, -19.0F, -15.0F, 20.0F, 8.0F, 18.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(bottom, right, left, back, front, paddle_left, paddle_right, bb_main);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(bottom, right, left, back, front, paddle_left, paddle_right, bb_main, water_mask);
    }

    public AdvancedModelBox getWaterMask() {
        return water_mask;
    }

    @Override
    public void setupAnim(Boat entity, float partialTicks, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        setupPaddleAnims(entity, paddle_left, paddle_right, partialTicks);
    }
}