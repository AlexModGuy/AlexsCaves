package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class MushroomCloudModel extends AdvancedEntityModel {
    private final AdvancedModelBox mushroom_cloud;
    private final AdvancedModelBox lightBall;
    private final AdvancedModelBox lowerCloud;
    private final AdvancedModelBox lowerCloud_planes;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox plume;
    private final AdvancedModelBox cube_r5;
    private final AdvancedModelBox cube_r6;
    private final AdvancedModelBox plumeBlast;
    private final AdvancedModelBox plumeRing;
    private final AdvancedModelBox upperCloud;
    private final AdvancedModelBox upperCloud1;
    private final AdvancedModelBox upperCloud_planes;
    private final AdvancedModelBox cube_r7;
    private final AdvancedModelBox cube_r8;
    private final AdvancedModelBox cube_r9;
    private final AdvancedModelBox cube_r10;
    private final AdvancedModelBox rings;
    private final AdvancedModelBox upperRing;
    private final AdvancedModelBox lowerRing;

    public MushroomCloudModel() {
        texWidth = 512;
        texHeight = 512;

        mushroom_cloud = new AdvancedModelBox(this);
        mushroom_cloud.setRotationPoint(0.0F, 24.0F, 0.0F);


        lightBall = new AdvancedModelBox(this);
        lightBall.setRotationPoint(0.0F, -19.0F, 0.0F);
        mushroom_cloud.addChild(lightBall);
        lightBall.setTextureOffset(300, 0).addBox(-16.0F, -16.0F, -16.0F, 32.0F, 32.0F, 32.0F, 0.0F, false);

        lowerCloud = new AdvancedModelBox(this);
        lowerCloud.setRotationPoint(0.0F, 5.0F, 0.0F);
        lightBall.addChild(lowerCloud);
        lowerCloud.setTextureOffset(0, 0).addBox(-50.0F, -9.0F, -50.0F, 100.0F, 16.0F, 100.0F, 0.0F, false);

        lowerCloud_planes = new AdvancedModelBox(this);
        lowerCloud_planes.setRotationPoint(0.0F, 0.0F, 0.0F);
        lowerCloud.addChild(lowerCloud_planes);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(-16.0F, -20.0F, 16.0F);
        lowerCloud_planes.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.7854F, 0.0F);
        cube_r1.setTextureOffset(142, 342).addBox(-66.0F, -13.0F, 0.0F, 66.0F, 40.0F, 0.0F, 0.0F, true);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(16.0F, -20.0F, -16.0F);
        lowerCloud_planes.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 0.7854F, 0.0F);
        cube_r2.setTextureOffset(142, 342).addBox(0.0F, -13.0F, 0.0F, 66.0F, 40.0F, 0.0F, 0.0F, false);

        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(16.0F, -20.0F, 16.0F);
        lowerCloud_planes.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.0F, -0.7854F, 0.0F);
        cube_r3.setTextureOffset(142, 342).addBox(0.0F, -13.0F, 0.0F, 66.0F, 40.0F, 0.0F, 0.0F, false);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(-16.0F, -20.0F, -16.0F);
        lowerCloud_planes.addChild(cube_r4);
        setRotateAngle(cube_r4, 0.0F, -0.7854F, 0.0F);
        cube_r4.setTextureOffset(142, 342).addBox(-66.0F, -13.0F, 0.0F, 66.0F, 40.0F, 0.0F, 0.0F, true);

        plume = new AdvancedModelBox(this);
        plume.setRotationPoint(0.0F, 9, 0.0F);
        lightBall.addChild(plume);


        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(0.0F, -44.8333F, 0.0F);
        plume.addChild(cube_r5);
        setRotateAngle(cube_r5, 0.0F, -0.7854F, 0.0F);
        cube_r5.setTextureOffset(256, 220).addBox(-32.0F, -61.0F, 0.0F, 64.0F, 122.0F, 0.0F, 0.0F, false);

        cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(0.0F, -44.8333F, 0.0F);
        plume.addChild(cube_r6);
        setRotateAngle(cube_r6, 0.0F, 0.7854F, 0.0F);
        cube_r6.setTextureOffset(256, 220).addBox(-32.0F, -61.0F, 0.0F, 64.0F, 122.0F, 0.0F, 0.0F, false);

        plumeBlast = new AdvancedModelBox(this);
        plumeBlast.setRotationPoint(0.0F, -80.8333F, 0.0F);
        plume.addChild(plumeBlast);
        plumeBlast.setTextureOffset(240, 116).addBox(-18.0F, 0.0F, -18.0F, 36.0F, 29.0F, 36.0F, 0.0F, false);

        plumeRing = new AdvancedModelBox(this);
        plumeRing.setRotationPoint(0.0F, -35.8333F, 0.0F);
        plume.addChild(plumeRing);
        plumeRing.setTextureOffset(296, 440).addBox(-36.0F, 0.0F, -36.0F, 72.0F, 0.0F, 72.0F, 0.0F, false);

        upperCloud = new AdvancedModelBox(this);
        upperCloud.setRotationPoint(0.0F, -80.8333F, 0.0F);
        plume.addChild(upperCloud);
        upperCloud.setTextureOffset(0, 116).addBox(-40.0F, -24.0F, -40.0F, 80.0F, 24.0F, 80.0F, 0.0F, false);

        upperCloud1 = new AdvancedModelBox(this);
        upperCloud1.setRotationPoint(0.0F, -24.0F, 0.0F);
        upperCloud.addChild(upperCloud1);
        upperCloud1.setTextureOffset(0, 220).addBox(-32.0F, -15.0F, -32.0F, 64.0F, 15.0F, 64.0F, 0.0F, false);

        upperCloud_planes = new AdvancedModelBox(this);
        upperCloud_planes.setRotationPoint(0.0F, -12.0F, 0.0F);
        upperCloud.addChild(upperCloud_planes);


        cube_r7 = new AdvancedModelBox(this);
        cube_r7.setRotationPoint(16.0F, 112.0F, -16.0F);
        upperCloud_planes.addChild(cube_r7);
        setRotateAngle(cube_r7, 0.0F, 0.7854F, 0.0F);
        cube_r7.setTextureOffset(0, 299).addBox(-23.0F, -152.0F, 0.0F, 71.0F, 69.0F, 0.0F, 0.0F, false);

        cube_r8 = new AdvancedModelBox(this);
        cube_r8.setRotationPoint(16.0F, 112.0F, 16.0F);
        upperCloud_planes.addChild(cube_r8);
        setRotateAngle(cube_r8, 0.0F, -0.7854F, 0.0F);
        cube_r8.setTextureOffset(0, 299).addBox(-23.0F, -152.0F, 0.0F, 71.0F, 69.0F, 0.0F, 0.0F, false);

        cube_r9 = new AdvancedModelBox(this);
        cube_r9.setRotationPoint(-16.0F, 112.0F, 16.0F);
        upperCloud_planes.addChild(cube_r9);
        setRotateAngle(cube_r9, 0.0F, 0.7854F, 0.0F);
        cube_r9.setTextureOffset(0, 299).addBox(-48.0F, -152.0F, 0.0F, 71.0F, 69.0F, 0.0F, 0.0F, true);

        cube_r10 = new AdvancedModelBox(this);
        cube_r10.setRotationPoint(-16.0F, 112.0F, -16.0F);
        upperCloud_planes.addChild(cube_r10);
        setRotateAngle(cube_r10, 0.0F, -0.7854F, 0.0F);
        cube_r10.setTextureOffset(0, 299).addBox(-48.0F, -152.0F, 0.0F, 71.0F, 69.0F, 0.0F, 0.0F, true);

        rings = new AdvancedModelBox(this);
        rings.setRotationPoint(0.0F, -55.0F, 0.0F);
        upperCloud.addChild(rings);


        upperRing = new AdvancedModelBox(this);
        upperRing.setRotationPoint(0.0F, -4.0F, 0.0F);
        rings.addChild(upperRing);
        upperRing.setTextureOffset(296, 440).addBox(-36.0F, 0.0F, -36.0F, 72.0F, 0.0F, 72.0F, 0.0F, false);

        lowerRing = new AdvancedModelBox(this);
        lowerRing.setRotationPoint(0.0F, 4.0F, 0.0F);
        rings.addChild(lowerRing);
        lowerRing.setTextureOffset(296, 440).addBox(-36.0F, 0.0F, -36.0F, 72.0F, 0.0F, 72.0F, 0.0F, false);
        this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(mushroom_cloud);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(rings, mushroom_cloud, lowerCloud, lowerCloud_planes, lightBall, cube_r1, cube_r2, cube_r3, cube_r4, cube_r5, cube_r6, cube_r7, cube_r8, cube_r9, cube_r10, plume, plumeBlast, plumeRing, upperCloud, upperCloud_planes, upperCloud1, upperRing, lowerRing);
    }


    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
    }

    public void hideFireball(boolean fireball){
        this.plume.showModel = fireball;
        this.lowerCloud.showModel = fireball;
        this.upperCloud.showModel = fireball;
    }
    public void animateParticle(float age, float life, float partialTicks){
        this.resetToDefaultPose();
        float lerpedAge = age + partialTicks;
        float baseExpand1 = life + life * life;
        float upperExpand = offset(life, 0.3F, 0.4F, 1.5F);
        float plumeRingExpand = offset(life, 0.0F, 0.0F, 0.5F) * 45;
        float lowerRingExpand = offset(life, 0.0F, 0.1F, 0.65F) * 20;
        float upperRingExpand = offset(life, 0.0F, 0.2F, 0.65F) * 15;
        this.plume.scaleChildren = true;
        this.upperCloud.scaleChildren = true;
        this.lowerCloud.scaleChildren = true;
        this.plume.setScale(1, life * 1.5F, 1);
        this.upperCloud.setScale(upperExpand, 1, upperExpand);
        this.plumeRing.setScale(plumeRingExpand, 1, plumeRingExpand);
        this.lowerRing.setScale(lowerRingExpand, 1, lowerRingExpand);
        this.upperRing.setScale(upperRingExpand, 1, upperRingExpand);
        this.lowerCloud.setScale(baseExpand1, baseExpand1, baseExpand1);
        this.plumeRing.rotateAngleY += lerpedAge * 0.1;
        this.lowerRing.rotateAngleY -= lerpedAge * 0.2;
        this.upperRing.rotateAngleY += lerpedAge * 0.1;
    }

    public float offset(float life, float forwards, float min, float max){
        return Mth.clamp(life + forwards, min, max);
    }
}