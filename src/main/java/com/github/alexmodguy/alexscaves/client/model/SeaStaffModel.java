package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class SeaStaffModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox bone;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox bClaw;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox sClaw;
    private final AdvancedModelBox cube_r4;

    public SeaStaffModel() {
        texWidth = 64;
        texHeight = 64;

        bone = new AdvancedModelBox(this);
        bone.setRotationPoint(0.0F, 15.1414F, 0.0F);
        bone.setTextureOffset(26, 0).addBox(-0.5F, -11.1414F, -0.5F, 1.0F, 19.0F, 1.0F, 0.0F, false);
        bone.setTextureOffset(0, 28).addBox(-1.0F, -16.1414F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, -16.6414F, 0.0F);
        bone.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.7854F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(0, 29).addBox(0.0F, -2.5F, -3.5F, 0.0F, 6.0F, 6.0F, 0.0F, false);
        cube_r1.setTextureOffset(0, 0).addBox(-2.0F, -1.5F, -2.5F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 7.8586F, 0.0F);
        bone.addChild(cube_r2);
        setRotateAngle(cube_r2, -0.7854F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(30, 0).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 3.0F, 3.0F, 0.0F, false);

        bClaw = new AdvancedModelBox(this);
        bClaw.setRotationPoint(0.0F, -17.3914F, -1.0F);
        bone.addChild(bClaw);


        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(0.0F, 0.75F, 1.0F);
        bClaw.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.7854F, 0.0F, 0.0F);
        cube_r3.setTextureOffset(2, 2).addBox(0.0F, -10.5F, -4.5F, 0.0F, 9.0F, 11.0F, 0.0F, false);
        cube_r3.setTextureOffset(22, 24).addBox(-1.0F, -8.5F, -1.5F, 2.0F, 7.0F, 4.0F, 0.0F, false);
        cube_r3.setTextureOffset(26, 16).addBox(-1.0F, -8.5F, 2.5F, 2.0F, 4.0F, 4.0F, 0.0F, false);

        sClaw = new AdvancedModelBox(this);
        sClaw.setRotationPoint(0.0F, -17.1627F, 1.3284F);
        bone.addChild(sClaw);


        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(0.0F, 0.5212F, -1.3284F);
        sClaw.addChild(cube_r4);
        setRotateAngle(cube_r4, 0.7854F, 0.0F, 0.0F);
        cube_r4.setTextureOffset(12, 0).addBox(-1.0F, -2.5F, 4.5F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        cube_r4.setTextureOffset(38, 31).addBox(0.0F, -2.5F, 1.5F, 0.0F, 5.0F, 6.0F, 0.0F, false);
        cube_r4.setTextureOffset(11, 3).addBox(-1.0F, -1.5F, 1.5F, 2.0F, 3.0F, 5.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(bone);
    }


    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(bone, cube_r1, cube_r2, cube_r3, cube_r4, sClaw, bClaw);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float explode, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();

    }
}
