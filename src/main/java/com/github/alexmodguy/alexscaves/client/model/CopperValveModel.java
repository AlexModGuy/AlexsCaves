package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class CopperValveModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox wheel;
    private final AdvancedModelBox cube_r1;

    public CopperValveModel() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(8.0F, 24.0F, -8.0F);
        root.setTextureOffset(0, 18).addBox(-9.0F, -12.0F, 7.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);

        wheel = new AdvancedModelBox(this);
        wheel.setRotationPoint(-8.0F, -10.1683F, 8.0F);
        root.addChild(wheel);
        wheel.setTextureOffset(8, 26).addBox(-2.0F, -1.8417F, -2.0F, 4.0F, 2.0F, 4.0F, 0.0F, false);
        wheel.setTextureOffset(-11, 1).addBox(-7.0F, 0.1683F, -7.0F, 14.0F, 0.0F, 14.0F, 0.0F, false);
        wheel.setTextureOffset(32, 36).addBox(-5.0F, -0.8317F, -7.0F, 10.0F, 2.0F, 2.0F, 0.0F, true);
        wheel.setTextureOffset(32, 36).addBox(-5.0F, -0.8317F, 5.0F, 10.0F, 2.0F, 2.0F, 0.0F, true);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(8.0F, 10.1683F, -8.0F);
        wheel.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 1.5708F, 0.0F);
        cube_r1.setTextureOffset(0, 32).addBox(-15.0F, -11.0F, -15.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
        cube_r1.setTextureOffset(0, 32).addBox(-15.0F, -11.0F, -3.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float lifetime, float down, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.wheel.rotationPointY += 4 * down;
        this.wheel.rotateAngleY += Math.toRadians(down * 360F);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, wheel, cube_r1);
    }


}