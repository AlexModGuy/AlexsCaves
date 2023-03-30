package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.WaterBoltEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class WaterBoltModel extends AdvancedEntityModel<WaterBoltEntity> {
    private final AdvancedModelBox bb_main;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;

    public WaterBoltModel() {
        texWidth = 64;
        texHeight = 64;

        bb_main = new AdvancedModelBox(this);
        bb_main.setRotationPoint(0.0F, 0F, 0.0F);
        bb_main.setTextureOffset(0, 24).addBox(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 11.0F, 0.0F, false);
        bb_main.setTextureOffset(0, 0).addBox(0.0F, -3.0F, -5.0F, 0.0F, 6.0F, 18.0F, 0.0F, false);
        bb_main.setTextureOffset(38, -11).addBox(0.0F, 3, -8.0F, 0.0F, 6.0F, 11.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, -3.0F, 4.0F);
        bb_main.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.0F, 1.5708F);
        cube_r1.setTextureOffset(0, 0).addBox(3.0F, -3.0F, -9.0F, 0.0F, 6.0F, 18.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(3.0F, -0.0F, -2.5F);
        bb_main.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 0.0F, -1.5708F);
        cube_r2.setTextureOffset(38, -11).addBox(0.0F, 0.0F, -5.5F, 0.0F, 6.0F, 11.0F, 0.0F, true);

        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(-3.0F, -0.0F, -2.5F);
        bb_main.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.0F, 0.0F, 1.5708F);
        cube_r3.setTextureOffset(38, -11).addBox(0.0F, 0.0F, -5.5F, 0.0F, 6.0F, 11.0F, 0.0F, false);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(0.0F, -6.0F, -2.5F);
        bb_main.addChild(cube_r4);
        setRotateAngle(cube_r4, 3.1416F, 0.0F, 0.0F);
        cube_r4.setTextureOffset(27, -11).addBox(0.0F, -3.0F, -5.5F, 0.0F, 6.0F, 11.0F, 0.0F, false);
        bb_main.scaleChildren = true;
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(bb_main);
    }

    @Override
    public void setupAnim(WaterBoltEntity waterBoltEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float stretch = Math.min(20F, ageInTicks) / 20F;
        bb_main.setScale(1, 1, 1 + stretch);
        bb_main.rotationPointZ += stretch * 12;
        bb_main.rotateAngleZ += ageInTicks * 0.2F;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(bb_main, cube_r1, cube_r2, cube_r3, cube_r4);
    }


}
