package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class ResistorShieldModel extends AdvancedEntityModel<Entity> {

    private final AdvancedModelBox root;
    private final AdvancedModelBox base;
    private final AdvancedModelBox rotationBolt;

    public ResistorShieldModel() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 2.0F);
        setRotateAngle(root, 0.0F, 1.5708F, 0.0F);

        base = new AdvancedModelBox(this);
        base.setRotationPoint(4.0F, -20.0F, 0.0F);
        root.addChild(base);
        setRotateAngle(base, 0.0F, -0.0436F, 0.0F);
        base.setTextureOffset(16, 36).addBox(-9.0F, -3.0F, -1.0F, 6.0F, 6.0F, 2.0F, 0.0F, false);
        base.setTextureOffset(28, 28).addBox(-3.0F, -8.0F, -8.0F, 6.0F, 4.0F, 16.0F, 0.0F, false);
        base.setTextureOffset(40, 60).addBox(-3.0F, -4.0F, -8.0F, 6.0F, 8.0F, 4.0F, 0.0F, false);
        base.setTextureOffset(56, 22).addBox(-3.0F, -4.0F, 4.0F, 6.0F, 8.0F, 4.0F, 0.0F, false);
        base.setTextureOffset(16, 20).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 8.0F, 8.0F, 0.0F, false);
        base.setTextureOffset(0, 44).addBox(-3.0F, 4.0F, -8.0F, 6.0F, 4.0F, 16.0F, 0.0F, false);

        rotationBolt = new AdvancedModelBox(this);
        rotationBolt.setRotationPoint(1.0F, 0.0F, 0.0F);
        base.addChild(rotationBolt);
        rotationBolt.setTextureOffset(54, 66).addBox(-3.0F, 4.0F, -3.0F, 6.0F, 13.0F, 6.0F, 0.0F, false);
        rotationBolt.setTextureOffset(22, 66).addBox(-3.0F, -17.0F, -3.0F, 6.0F, 13.0F, 6.0F, 0.0F, false);
        rotationBolt.setTextureOffset(1, 0).addBox(-3.0F, -4.0F, -4.0F, 7.0F, 8.0F, 8.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(Entity entity, float useProgress, float switchProgress, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.rotationBolt.rotateAngleX = (float) Math.toRadians(360F * useProgress + switchProgress * 180);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, rotationBolt, base);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }
}