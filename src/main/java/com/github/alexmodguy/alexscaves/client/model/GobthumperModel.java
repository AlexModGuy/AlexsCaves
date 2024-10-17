package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class GobthumperModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox main;
    private final AdvancedModelBox stick;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox upper_gel;
    private final AdvancedModelBox gumballs;

    public GobthumperModel() {
        texWidth = 32;
        texHeight = 32;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, -7.0F, 0.0F);
        root.addChild(main);


        stick = new AdvancedModelBox(this);
        stick.setRotationPoint(0.0F, 7.0F, 0.0F);
        main.addChild(stick);
        stick.setTextureOffset(16, 0).addBox(0.0F, -10.0F, -1.0F, 0.0F, 10.0F, 2.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, -6.0F, 0.0F);
        stick.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 1.5272F, 0.0F);
        cube_r1.setTextureOffset(16, 0).addBox(0.0F, -4.0F, -1.0F, 0.0F, 10.0F, 2.0F, 0.0F, false);

        upper_gel = new AdvancedModelBox(this);
        upper_gel.setRotationPoint(0.0F, -7.0F, 0.0F);
        stick.addChild(upper_gel);
        upper_gel.setTextureOffset(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

        gumballs = new AdvancedModelBox(this);
        gumballs.setRotationPoint(0.0F, -6.0F, 0.0F);
        stick.addChild(gumballs);
        gumballs.setTextureOffset(0, 12).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 8.0F, 4.0F, -0.25F, false);
        this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(Entity entity, float f, float f1, float ageInTicks, float unused0, float unused1) {
        this.resetToDefaultPose();
        float jiggle1 = (float)(Math.sin(ageInTicks * 0.7F) + 1F) * 0.5F;
        float jiggle2 = (float)(Math.cos(ageInTicks * 0.7F - 1F) + 1F) * 0.5F;
        this.upper_gel.setScale(1F + jiggle1, 1F + 0.25F * jiggle2, 1F + jiggle1);
        this.upper_gel.rotationPointY -= jiggle1;
        this.main.rotationPointY += jiggle2 * 1;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, main, stick, cube_r1, upper_gel, gumballs);
    }

}
