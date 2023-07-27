package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class TeslaBulbModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox bulb;
    private final AdvancedModelBox baseRing;
    private final AdvancedModelBox midRing;
    private final AdvancedModelBox topRing;
    private final AdvancedModelBox outer;
    private final AdvancedModelBox center;

    public TeslaBulbModel() {
        texWidth = 128;
        texHeight = 128;

        bulb = new AdvancedModelBox(this);
        bulb.setRotationPoint(0.0F, 20.0F, 0.0F);


        baseRing = new AdvancedModelBox(this);
        baseRing.setRotationPoint(0.0F, 4.0F, 0.0F);
        bulb.addChild(baseRing);
        baseRing.setTextureOffset(0, 32).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);

        midRing = new AdvancedModelBox(this);
        midRing.setRotationPoint(0.0F, -2.0F, 0.0F);
        baseRing.addChild(midRing);
        midRing.setTextureOffset(0, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);

        topRing = new AdvancedModelBox(this);
        topRing.setRotationPoint(0.0F, -2.0F, 0.0F);
        midRing.addChild(topRing);
        topRing.setTextureOffset(0, 16).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);

        outer = new AdvancedModelBox(this);
        outer.setRotationPoint(0.0F, -8.0F, 0.0F);
        topRing.addChild(outer);
        outer.setTextureOffset(40, 40).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        center = new AdvancedModelBox(this);
        center.setRotationPoint(0.0F, 0.0F, 0.0F);
        outer.addChild(center);
        center.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(bulb);
    }


    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(bulb, baseRing, center, outer, midRing, topRing);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float explode, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float intensity = 1F + explode;
        float zoom = (float) ((Math.sin(ageInTicks * 0.5F * intensity) + 1F) * 0.5F) * 0.1F + (float) Math.sin(explode * Math.PI);
        outer.setScale(1F + zoom, 1F + zoom, 1F + zoom);
        bulb.rotationPointY += -1F + Math.sin(ageInTicks * 0.045F * intensity) * 1F;
        baseRing.rotationPointY -= -1F + Math.sin(ageInTicks * 0.045F * intensity) * 1F;
        baseRing.rotationPointY += -2.5F + Math.sin(ageInTicks * 0.045F * intensity + 1F) * 0.5F;
        midRing.rotationPointY += Math.sin(ageInTicks * 0.045F * intensity + 2F) * 0.25F;
        topRing.rotationPointY -= Math.sin(ageInTicks * 0.045F * intensity + 3F) * 0.15F;
        bulb.rotationPointY += Math.sin(ageInTicks * 0.045F) * 1F;
        bulb.rotationPointY += Math.sin(ageInTicks * 0.045F) * 1F;
        center.rotateAngleX += ageInTicks * 0.1F * intensity;
        center.rotateAngleY += ageInTicks * 0.2F * intensity;
        outer.rotateAngleZ += ageInTicks * 0.05F * intensity;
        outer.rotateAngleX -= ageInTicks * 0.1F * intensity;
        outer.rotateAngleY -= ageInTicks * 0.2F * intensity;
        outer.rotateAngleZ -= ageInTicks * 0.05F * intensity;
        baseRing.rotateAngleY += ageInTicks * 0.1F * intensity;
        midRing.rotateAngleY += ageInTicks * 0.1F * intensity;
        topRing.rotateAngleY += ageInTicks * 0.1F * intensity;
    }
}
