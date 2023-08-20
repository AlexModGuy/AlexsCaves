package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.vehicle.Boat;

public class ThornwoodBoatModel extends ACBoatModel {
    private final AdvancedModelBox bottom;
    private final AdvancedModelBox left;
    private final AdvancedModelBox right;
    private final AdvancedModelBox paddle_right;
    private final AdvancedModelBox paddle_left;
    private final AdvancedModelBox front;
    private final AdvancedModelBox back;
    private final AdvancedModelBox cull;

    public ThornwoodBoatModel() {
        texWidth = 128;
        texHeight = 128;

        bottom = new AdvancedModelBox(this);
        bottom.setRotationPoint(0.0F, 22.5F, 0.0F);
        bottom.setTextureOffset(0, 34).addBox(-8.0F, -2.5F, -14.0F, 16.0F, 4.0F, 28.0F, 0.0F, false);

        left = new AdvancedModelBox(this);
        left.setRotationPoint(0.0F, 24.0F, 0.0F);
        left.setTextureOffset(60, 0).addBox(8.0F, -9.0F, -2.0F, 2.0F, 6.0F, 17.0F, 0.0F, true);
        left.setTextureOffset(38, 66).addBox(8.0F, -12.0F, -15.0F, 2.0F, 9.0F, 13.0F, 0.0F, false);

        right = new AdvancedModelBox(this);
        right.setRotationPoint(0.0F, 24.0F, 0.0F);
        right.setTextureOffset(60, 0).addBox(-10.0F, -9.0F, -2.0F, 2.0F, 6.0F, 17.0F, 0.0F, false);
        right.setTextureOffset(38, 66).addBox(-10.0F, -12.0F, -15.0F, 2.0F, 9.0F, 13.0F, 0.0F, true);

        paddle_right = new AdvancedModelBox(this);
        paddle_right.setRotationPoint(-8.75F, 14.0F, -0.505F);
        setRotateAngle(paddle_right, 0.0F, 0.0F, -0.5236F);
        paddle_right.setTextureOffset(81, 34).addBox(-18.25F, -8.0F, -0.505F, 15.0F, 10.0F, 0.0F, 0.0F, true);
        paddle_right.setTextureOffset(60, 23).addBox(-12.25F, -1.0F, -0.495F, 18.0F, 2.0F, 2.0F, 0.0F, true);

        paddle_left = new AdvancedModelBox(this);
        paddle_left.setRotationPoint(8.75F, 14.0F, -0.505F);
        setRotateAngle(paddle_left, 0.0F, 0.0F, 0.5236F);
        paddle_left.setTextureOffset(81, 34).addBox(3.25F, -8.0F, -0.505F, 15.0F, 10.0F, 0.0F, 0.0F, false);
        paddle_left.setTextureOffset(60, 23).addBox(-5.75F, -1.0F, -0.495F, 18.0F, 2.0F, 2.0F, 0.0F, false);

        front = new AdvancedModelBox(this);
        front.setRotationPoint(0.0F, 2.8F, -15.8F);
        front.setTextureOffset(0, 66).addBox(-8.0F, 5.2F, -1.2F, 16.0F, 13.0F, 3.0F, 0.0F, false);
        front.setTextureOffset(14, 34).addBox(-2.0F, 0.2F, -1.2F, 4.0F, 5.0F, 3.0F, 0.0F, false);
        front.setTextureOffset(0, 0).addBox(-2.0F, -5.8F, -4.2F, 4.0F, 6.0F, 6.0F, 0.0F, false);
        front.setTextureOffset(18, 12).addBox(-2.0F, -11.8F, -4.2F, 4.0F, 6.0F, 0.0F, 0.0F, false);
        front.setTextureOffset(20, 0).addBox(-2.0F, -5.8F, 1.8F, 4.0F, 6.0F, 6.0F, 0.0F, false);

        back = new AdvancedModelBox(this);
        back.setRotationPoint(0.0F, 12.6667F, 15.8333F);
        back.setTextureOffset(81, 0).addBox(-8.0F, 2.3333F, -1.8333F, 16.0F, 6.0F, 3.0F, 0.0F, false);
        back.setTextureOffset(0, 34).addBox(-2.0F, -2.6667F, -1.8333F, 4.0F, 5.0F, 3.0F, 0.0F, false);
        back.setTextureOffset(0, 12).addBox(-2.0F, -7.6667F, -1.8333F, 4.0F, 5.0F, 5.0F, 0.0F, false);

        cull = new AdvancedModelBox(this);
        cull.setRotationPoint(0.0F, 24.0F, 0.0F);
        cull.setTextureOffset(0, 94).addBox(-8.0F, -9.0F, -14.0F, 16.0F, 6.0F, 28.0F, 0.0F, false);
        this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(bottom, right, left, back, front, paddle_left, paddle_right);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(bottom, right, left, back, front, paddle_left, paddle_right, cull);
    }

    public AdvancedModelBox getWaterMask() {
        return cull;
    }



    @Override
    public void setupAnim(Boat entity, float partialTicks, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        setupPaddleAnims(entity, paddle_left, paddle_right, partialTicks);
    }
}
