package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class ShotGumModel extends AdvancedEntityModel {
    private final AdvancedModelBox shot_gum;
    private final AdvancedModelBox handle;
    private final AdvancedModelBox gum_layers;
    private final AdvancedModelBox gum_layer;
    private final AdvancedModelBox gum_layer2;
    private final AdvancedModelBox gum_layer3;
    private final AdvancedModelBox gum_layer4;
    private final AdvancedModelBox stock;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox barrels;
    private final AdvancedModelBox crank;

    public ShotGumModel() {
        texWidth = 128;
        texHeight = 128;

        shot_gum = new AdvancedModelBox(this);
        shot_gum.setRotationPoint(0.0F, 21.0F, 6.0F);
        shot_gum.setTextureOffset(32, 23).addBox(-4.0F, -9.0F, -5.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        shot_gum.setTextureOffset(0, 30).addBox(-4.0F, -9.0F, -5.0F, 8.0F, 8.0F, 8.0F, 0.25F, false);

        handle = new AdvancedModelBox(this);
        handle.setRotationPoint(0.0F, 0.5F, -1.0F);
        shot_gum.addChild(handle);
        handle.setTextureOffset(7, 40).addBox(0.0F, -1.5F, -3.0F, 0.0F, 3.0F, 6.0F, 0.0F, false);

        gum_layers = new AdvancedModelBox(this);
        gum_layers.setRotationPoint(0.0F, -1.0F, -2.0F);
        shot_gum.addChild(gum_layers);


        gum_layer = new AdvancedModelBox(this);
        gum_layer.setRotationPoint(0.0F, -4.0F, 1.0F);
        gum_layers.addChild(gum_layer);
        gum_layer.setTextureOffset(31, 7).addBox(-4.0F, -3.8F, -4.0F, 8.0F, 2.0F, 8.0F, -0.05F, false);

        gum_layer2 = new AdvancedModelBox(this);
        gum_layer2.setRotationPoint(0.0F, -4.0F, 1.0F);
        gum_layers.addChild(gum_layer2);
        gum_layer2.setTextureOffset(31, 9).addBox(-4.0F, -1.9F, -4.0F, 8.0F, 2.0F, 8.0F, -0.05F, false);

        gum_layer3 = new AdvancedModelBox(this);
        gum_layer3.setRotationPoint(0.0F, -4.0F, 1.0F);
        gum_layers.addChild(gum_layer3);
        gum_layer3.setTextureOffset(31, 11).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 2.0F, 8.0F, -0.05F, false);

        gum_layer4 = new AdvancedModelBox(this);
        gum_layer4.setRotationPoint(0.0F, -4.0F, 1.0F);
        gum_layers.addChild(gum_layer4);
        gum_layer4.setTextureOffset(31, 13).addBox(-4.0F, 1.9F, -4.0F, 8.0F, 2.0F, 8.0F, -0.05F, false);

        stock = new AdvancedModelBox(this);
        stock.setRotationPoint(0.0F, -2.75F, 3.0F);
        shot_gum.addChild(stock);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 3.0F, 0.0F);
        stock.addChild(cube_r1);
        setRotateAngle(cube_r1, -0.5236F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(0, 49).addBox(-2.0F, -7.0F, 1.0F, 4.0F, 3.0F, 7.0F, 0.0F, false);
        cube_r1.setTextureOffset(41, 44).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 4.0F, 11.0F, 0.0F, false);
        cube_r1.setTextureOffset(0, 59).addBox(-2.0F, -7.0F, 1.0F, 4.0F, 7.0F, 7.0F, 0.25F, false);

        barrels = new AdvancedModelBox(this);
        barrels.setRotationPoint(0.0F, -3.8333F, -5.0F);
        shot_gum.addChild(barrels);
        barrels.setTextureOffset(0, 0).addBox(-3.5F, -0.6667F, -12.0F, 7.0F, 3.0F, 12.0F, 0.25F, false);
        barrels.setTextureOffset(0, 0).addBox(-0.5F, -2.6667F, -12.0F, 1.0F, 2.0F, 0.0F, 0.0F, false);
        barrels.setTextureOffset(0, 15).addBox(-3.5F, -0.6667F, -12.0F, 7.0F, 3.0F, 12.0F, 0.0F, false);

        crank = new AdvancedModelBox(this);
        crank.setRotationPoint(4.0F, -5.0F, -1.0F);
        shot_gum.addChild(crank);
        crank.setTextureOffset(8, 0).addBox(0.0F, -2.0F, 0.0F, 2.0F, 4.0F, 0.0F, 0.0F, false);
        barrels.setShouldScaleChildren(true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(shot_gum);
    }

    @Override
    public void setupAnim(Entity entity, float shootProgress, float gumballsLeft, float crankAngle, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        gum_layer4.showModel = gumballsLeft > 0;
        gum_layer3.showModel = gumballsLeft > 1;
        gum_layer2.showModel = gumballsLeft > 2;
        gum_layer.showModel = gumballsLeft > 3;
        this.crank.rotateAngleX += (float)Math.toRadians(crankAngle);
        barrels.setScale(1F + shootProgress * 0.3F, 1F + shootProgress * 0.3F, 1F - shootProgress * 0.5F);
        progressRotationPrev(shot_gum, shootProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressPositionPrev(barrels, shootProgress, 0, -1, 1, 1F);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(shot_gum, handle, gum_layers, gum_layer, gum_layer2, gum_layer3, gum_layer4, stock, cube_r1, barrels, crank);
    }
}
