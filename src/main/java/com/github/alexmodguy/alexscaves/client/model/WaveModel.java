package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.WaveEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class WaveModel extends AdvancedEntityModel<WaveEntity> {
    private final AdvancedModelBox main;
    private final AdvancedModelBox top;

    public WaveModel() {
        texWidth = 128;
        texHeight = 128;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);
        main.setTextureOffset(0, 28).addBox(-13.0F, -7.2919F, 0.9497F, 26.0F, 6.0F, 12.0F, -0.01F, false);

        top = new AdvancedModelBox(this);
        top.setRotationPoint(0.0F, -2.0F, -4.0F);
        main.addChild(top);
        setRotateAngle(top, 0.7854F, 0.0F, 0.0F);
        top.setTextureOffset(0, 0).addBox(-13.0F, -7.0F, 3.0F, 26.0F, 11.0F, 17.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    @Override
    public void setupAnim(WaveEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float f = (float) ((Math.sin(ageInTicks * 0.1F) + 1F) * 0.2F);
        float waveScale = entity.getWaveScale();
        float stretch = Math.min(40F, ageInTicks) / 40F;
        float slam = entity.getSlamAmount(ageInTicks - entity.activeWaveTicks) * 1.4F;
        progressRotationPrev(top, slam, (float) Math.toRadians(100), 0, 0, 1F);
        progressPositionPrev(top, slam, 0, -1, 13, 1F);
        this.top.rotateAngleX += f;
        this.top.rotationPointY += f * 8;
        this.top.rotationPointZ += f * 2;
        main.setScale(waveScale, waveScale, waveScale + stretch * 4F);
        top.setScale(waveScale, waveScale, waveScale + stretch * 1F);
        this.top.rotationPointY += stretch * 2;
        this.top.rotationPointZ += stretch * 2;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, top);
    }
}
