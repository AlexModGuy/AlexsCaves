package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.DarkArrowEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class DarkArrowModel extends AdvancedEntityModel<DarkArrowEntity> {
    private final AdvancedModelBox main;
    private final AdvancedModelBox head;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox stick;
    private final AdvancedModelBox cube_r2;

    public DarkArrowModel() {
        texWidth = 32;
        texHeight = 32;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 21.5F, 0.0F);


        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 0.0F, -4.5F);
        main.addChild(head);
        head.setTextureOffset(0, -2).addBox(0.0F, -2.5F, -3.5F, 0.0F, 5.0F, 7.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 3.75F);
        head.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.0F, 1.5708F);
        cube_r1.setTextureOffset(0, -2).addBox(0.0F, -2.5F, -7.25F, 0.0F, 5.0F, 7.0F, 0.0F, false);

        stick = new AdvancedModelBox(this);
        stick.setRotationPoint(0.0F, 0.0F, -2.0F);
        main.addChild(stick);
        stick.setTextureOffset(2, 2).addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 10.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 1.25F);
        stick.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 0.0F, 1.5708F);
        cube_r2.setTextureOffset(2, 2).addBox(0.0F, -2.5F, -1.25F, 0.0F, 5.0F, 10.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    @Override
    public void setupAnim(DarkArrowEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float invFade = 1.0F - entity.getFadeOut(ageInTicks - entity.tickCount);
        float tickModifier = Math.min(ageInTicks / 10F, 1F) * 5F * invFade;
        this.stick.setScale(1.0F, 1.0F, 1.0F + tickModifier);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, head, cube_r1, cube_r2, stick);
    }
}
