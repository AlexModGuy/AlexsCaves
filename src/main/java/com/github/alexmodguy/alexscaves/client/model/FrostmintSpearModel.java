package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.FrostmintSpearEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class FrostmintSpearModel extends AdvancedEntityModel<FrostmintSpearEntity> {
    private final AdvancedModelBox spear;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;

    public FrostmintSpearModel() {
        texWidth = 32;
        texHeight = 32;

        spear = new AdvancedModelBox(this);
        spear.setRotationPoint(0.0F, 15.0F, 0.0F);
        spear.setTextureOffset(0, 0).addBox(-0.5F, -20.0F, -0.5F, 1.0F, 29.0F, 1.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, -23.0F, 0.0F);
        spear.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.7854F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(7, 8).addBox(0.0F, -6.0F, -6.0F, 0.0F, 12.0F, 12.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, -18.0F, 0.0F);
        spear.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.7854F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(4, 1).addBox(0.0F, -3.0F, -6.0F, 0.0F, 9.0F, 9.0F, 0.0F, false);

        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(0.0F, 8.0F, 0.0F);
        spear.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.7854F, 0.0F, 0.0F);
        cube_r3.setTextureOffset(20, 1).addBox(0.0F, -3.0F, -2.0F, 0.0F, 5.0F, 5.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(spear);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(spear, cube_r1, cube_r2, cube_r3);
    }

    @Override
    public void setupAnim(FrostmintSpearEntity entity, float limbSwing, float explode, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }
}
