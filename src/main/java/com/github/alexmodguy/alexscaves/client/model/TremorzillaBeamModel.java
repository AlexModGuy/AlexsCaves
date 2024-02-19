package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class TremorzillaBeamModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox base;
    private final AdvancedModelBox flame;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox flame2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox flame3;
    private final AdvancedModelBox cube_r5;
    private final AdvancedModelBox cube_r6;
    private final AdvancedModelBox flame4;
    private final AdvancedModelBox cube_r7;
    private final AdvancedModelBox cube_r8;

    public TremorzillaBeamModel() {
        texWidth = 128;
        texHeight = 128;


        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        base = new AdvancedModelBox(this);
        base.setRotationPoint(0.0F, 0.0F, 0.0F);
        root.addChild(base);
        base.setTextureOffset(0, 0).addBox(-11.0F, 0.0F, -11.0F, 22.0F, 0.0F, 22.0F, 0.0F, false);

        flame = new AdvancedModelBox(this);
        flame.setRotationPoint(0.0F, 0.0F, -11.0F);
        base.addChild(flame);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        flame.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.3927F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(3, 38).addBox(-12.0F, -24.0F, 0.0F, 24.0F, 24.0F, 0.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        flame.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.7854F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(0, 22).addBox(-15.0F, -16.0F, 0.0F, 30.0F, 16.0F, 0.0F, 0.0F, false);

        flame2 = new AdvancedModelBox(this);
        flame2.setRotationPoint(0.0F, 0.0F, 11.0F);
        base.addChild(flame2);


        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
        flame2.addChild(cube_r3);
        setRotateAngle(cube_r3, -0.3927F, 0.0F, 0.0F);
        cube_r3.setTextureOffset(3, 38).addBox(-12.0F, -24.0F, 0.0F, 24.0F, 24.0F, 0.0F, 0.0F, true);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
        flame2.addChild(cube_r4);
        setRotateAngle(cube_r4, -0.7854F, 0.0F, 0.0F);
        cube_r4.setTextureOffset(0, 22).addBox(-15.0F, -16.0F, 0.0F, 30.0F, 16.0F, 0.0F, 0.0F, true);

        flame3 = new AdvancedModelBox(this);
        flame3.setRotationPoint(11.0F, 0.0F, 0.0F);
        base.addChild(flame3);


        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
        flame3.addChild(cube_r5);
        setRotateAngle(cube_r5, 0.0F, 0.0F, 0.3927F);
        cube_r5.setTextureOffset(3, 14).addBox(0.0F, -24.0F, -13.0F, 0.0F, 24.0F, 24.0F, 0.0F, true);

        cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(0.0F, 0.0F, 0.0F);
        flame3.addChild(cube_r6);
        setRotateAngle(cube_r6, 0.0F, 0.0F, 0.7854F);
        cube_r6.setTextureOffset(0, -8).addBox(0.0F, -16.0F, -15.0F, 0.0F, 16.0F, 30.0F, 0.0F, true);

        flame4 = new AdvancedModelBox(this);
        flame4.setRotationPoint(-11.0F, 0.0F, 0.0F);
        base.addChild(flame4);


        cube_r7 = new AdvancedModelBox(this);
        cube_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
        flame4.addChild(cube_r7);
        setRotateAngle(cube_r7, 0.0F, 0.0F, -0.3927F);
        cube_r7.setTextureOffset(3, 14).addBox(0.0F, -24.0F, -12.0F, 0.0F, 24.0F, 24.0F, 0.0F, false);

        cube_r8 = new AdvancedModelBox(this);
        cube_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
        flame4.addChild(cube_r8);
        setRotateAngle(cube_r8, 0.0F, 0.0F, -0.7854F);
        cube_r8.setTextureOffset(0, -8).addBox(0.0F, -16.0F, -15.0F, 0.0F, 16.0F, 30.0F, 0.0F, false);
        this.updateDefaultPose();
    }
    
    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, cube_r1, cube_r2, cube_r3, cube_r4, cube_r5, cube_r6, cube_r7, cube_r8, base, flame, flame2, flame3, flame4);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(Entity entity, float v, float v1, float v2, float v3, float v4) {
        this.resetToDefaultPose();
    }
}
