package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;

public class DreadbowModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox main;
    private final AdvancedModelBox bow;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox tArm;
    private final AdvancedModelBox bArm;
    private final AdvancedModelBox bowstring;
    private final AdvancedModelBox bString;
    private final AdvancedModelBox tString;

    public DreadbowModel() {
        texWidth = 64;
        texHeight = 64;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);


        bow = new AdvancedModelBox(this);
        bow.setRotationPoint(0.0F, -16.0F, -6.5F);
        main.addChild(bow);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        bow.addChild(cube_r1);
        setRotateAngle(cube_r1, -0.7854F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(16, 0).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, 0.0F, false);

        tArm = new AdvancedModelBox(this);
        tArm.setRotationPoint(0.0F, 0.0F, 0.0F);
        bow.addChild(tArm);
        setRotateAngle(tArm, -0.7854F, 0.0F, 0.0F);
        tArm.setTextureOffset(0, 0).addBox(-0.5F, -18.0F, -8.0F, 1.0F, 18.0F, 14.0F, 0.0F, false);
        tArm.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -8.0F, 0.0F, 3.0F, 6.0F, 0.0F, false);

        bArm = new AdvancedModelBox(this);
        bArm.setRotationPoint(0.0F, 0.0F, 0.0F);
        bow.addChild(bArm);
        setRotateAngle(bArm, 0.7854F, 0.0F, 0.0F);
        bArm.setTextureOffset(0, 32).addBox(-0.5F, 0.0F, -8.0F, 1.0F, 18.0F, 14.0F, 0.0F, true);
        bArm.setTextureOffset(0, -3).addBox(0.0F, -3.0F, -8.0F, 0.0F, 3.0F, 6.0F, 0.0F, false);

        bowstring = new AdvancedModelBox(this);
        bowstring.setRotationPoint(0.0F, 8.0F, 3.0F);


        bString = new AdvancedModelBox(this);
        bString.setRotationPoint(0.0F, -0.5F, 0.0F);
        bowstring.addChild(bString);
        bString.setTextureOffset(30, 18).addBox(-0.01F, -0.25F, -0.5F, 0.0F, 14.0F, 1.0F, 0.0F, false);

        tString = new AdvancedModelBox(this);
        tString.setRotationPoint(0.0F, -0.25F, 0.0F);
        bowstring.addChild(tString);
        tString.setTextureOffset(30, 0).addBox(0.0F, -13.75F, -0.5F, 0.0F, 14.0F, 1.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(Entity entity, float pullAmount, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.bowstring.rotationPointZ += pullAmount * 9;
        this.tString.rotateAngleX += (float) Math.toRadians(pullAmount * 25);
        this.bString.rotateAngleX += (float) Math.toRadians(pullAmount * -25);
        this.tArm.rotateAngleX += (float) Math.toRadians(pullAmount * -20);
        this.bArm.rotateAngleX += (float) Math.toRadians(pullAmount * 20);
    }

    public void translateToBowString(PoseStack matrixStackIn) {
        bowstring.translateAndRotate(matrixStackIn);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, bowstring, bow, cube_r1, tArm, bArm, bString, tString);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main, bowstring);
    }
}
