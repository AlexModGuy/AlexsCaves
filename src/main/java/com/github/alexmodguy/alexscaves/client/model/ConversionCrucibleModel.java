package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ConversionCrucibleBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.DarkArrowEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class ConversionCrucibleModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox crucible;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox cube_r5;
    private final AdvancedModelBox cube_r6;
    private final AdvancedModelBox sauce;
    private final AdvancedModelBox beam;

    public ConversionCrucibleModel() {
        texWidth = 64;
        texHeight = 64;

        crucible = new AdvancedModelBox(this);
        crucible.setRotationPoint(0.0F, 24.0F, 0.0F);
        crucible.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);
        crucible.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);
        crucible.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, 8.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);
        crucible.setTextureOffset(52, 0).addBox(-8.0F, -16.0F, -3.0F, 3.0F, 16.0F, 0.0F, 0.0F, false);
        crucible.setTextureOffset(52, 0).addBox(-8.0F, -16.0F, 3.0F, 3.0F, 16.0F, 0.0F, 0.0F, false);
        crucible.setTextureOffset(52, 0).addBox(5.0F, -16.0F, -3.0F, 3.0F, 16.0F, 0.0F, 0.0F, false);
        crucible.setTextureOffset(52, 0).addBox(5.0F, -16.0F, 3.0F, 3.0F, 16.0F, 0.0F, 0.0F, false);
        crucible.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, 5.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);
        crucible.setTextureOffset(-16, 32).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);
        crucible.setTextureOffset(-16, 16).addBox(-8.0F, -2.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);
        crucible.setTextureOffset(-16, 48).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);
        crucible.setTextureOffset(52, -3).addBox(-3.0F, -16.0F, -8.0F, 0.0F, 16.0F, 3.0F, 0.0F, false);
        crucible.setTextureOffset(52, -3).addBox(3.0F, -16.0F, -8.0F, 0.0F, 16.0F, 3.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(-5.0F, -8.0F, -0.5F);
        crucible.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 1.5708F, 0.0F);
        cube_r1.setTextureOffset(0, 0).addBox(-8.5F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(-3.0F, -8.0F, -0.5F);
        crucible.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 1.5708F, 0.0F);
        cube_r2.setTextureOffset(52, 0).addBox(-8.5F, -8.0F, 0.0F, 3.0F, 16.0F, 0.0F, 0.0F, false);

        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(3.0F, -8.0F, -0.5F);
        crucible.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.0F, 1.5708F, 0.0F);
        cube_r3.setTextureOffset(52, 0).addBox(-8.5F, -8.0F, 0.0F, 3.0F, 16.0F, 0.0F, 0.0F, false);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(-8.0F, -8.0F, -0.5F);
        crucible.addChild(cube_r4);
        setRotateAngle(cube_r4, 0.0F, 1.5708F, 0.0F);
        cube_r4.setTextureOffset(0, 0).addBox(-8.5F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);

        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(5.0F, -8.0F, -0.5F);
        crucible.addChild(cube_r5);
        setRotateAngle(cube_r5, 0.0F, 1.5708F, 0.0F);
        cube_r5.setTextureOffset(0, 0).addBox(-8.5F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);

        cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(8.0F, -8.0F, -0.5F);
        crucible.addChild(cube_r6);
        setRotateAngle(cube_r6, 0.0F, 1.5708F, 0.0F);
        cube_r6.setTextureOffset(0, 0).addBox(-8.5F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);

        sauce = new AdvancedModelBox(this);
        sauce.setRotationPoint(6.0F, -8.0F, -6.0F);
        crucible.addChild(sauce);
        sauce.setTextureOffset(20, 50).addBox(-13.0F, -6.0F, -1.0F, 14.0F, 0.0F, 14.0F, 0.0F, false);

        beam = new AdvancedModelBox(this);
        beam.setRotationPoint(-0.0F, -14.0F, 0.0F);
        crucible.addChild(beam);
        beam.setTextureOffset(44, 22).addBox(5.0F, -18.0F, -5.0F, 0.0F, 18.0F, 10.0F, 0.0F, false);
        beam.setTextureOffset(44, 32).addBox(-5.0F, -18.0F, 5.0F, 10.0F, 18.0F, 0.0F, 0.0F, false);
        beam.setTextureOffset(44, 32).addBox(-5.0F, -18.0F, -5.0F, 10.0F, 18.0F, 0.0F, 0.0F, false);
        beam.setTextureOffset(44, 22).addBox(-5.0F, -18.0F, -5.0F, 0.0F, 18.0F, 10.0F, 0.0F, false);
        this.updateDefaultPose();
   }

    public void hideBeam(boolean hide){
        this.beam.showModel = !hide;
    }

    public void hideSauce(boolean hide){
        this.sauce.showModel = !hide;
    }

    @Override
    public void setupAnim(Entity entity, float splashProgress, float conversionProgress, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float f1 = (float)Math.sin(splashProgress * Math.PI * 3F + ageInTicks * 0.3F) * 0.15F + 0.85F;
        float f2 = (float)Math.cos(splashProgress * Math.PI * 3F + ageInTicks * 0.3F) * 0.15F + 0.85F;

        this.beam.setScale(0.99F * f2, 1.5F * f1 * splashProgress + 2.0F * conversionProgress, 0.99F * f2);
        this.crucible.walk(1.5F, 0.1F, true, 1.5F, 0.0F, ageInTicks, conversionProgress);
        this.crucible.flap(1.5F, 0.1F, false, 0F, 0.0F, ageInTicks, conversionProgress);
    }

    public void setFilledLevel(int filledLevel) {
        this.sauce.rotationPointY += ConversionCrucibleBlockEntity.MAX_FILL_AMOUNT - filledLevel;
        this.beam.rotationPointY += ConversionCrucibleBlockEntity.MAX_FILL_AMOUNT - filledLevel;
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(crucible);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(crucible, cube_r1, cube_r2, cube_r3, cube_r4, cube_r5, cube_r6, beam, sauce);
    }
}

