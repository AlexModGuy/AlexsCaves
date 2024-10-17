package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class GumWormSegmentModel extends AdvancedEntityModel<GumWormSegmentEntity> {
    private final AdvancedModelBox main;
    private final AdvancedModelBox segment;
    private final AdvancedModelBox gum_back;
    private final AdvancedModelBox gum_front;

    public GumWormSegmentModel() {
        texWidth = 128;
        texHeight = 128;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);

        segment = new AdvancedModelBox(this);
        segment.setRotationPoint(0.0F, -16.0F, 0.0F);
        main.addChild(segment);
        segment.setTextureOffset(0, 0).addBox(-16.0F, -16.0F, -16.0F, 32.0F, 32.0F, 32.0F, 0.0F, false);

        gum_back = new AdvancedModelBox(this);
        gum_back.setRotationPoint(0.0F, 0.0F, 16.5F);
        segment.addChild(gum_back);
        gum_back.setTextureOffset(0, 64).addBox(-16.0F, -16.0F, 0.0F, 32.0F, 32.0F, 0.0F, 0.0F, false);

        gum_front = new AdvancedModelBox(this);
        gum_front.setRotationPoint(0.0F, 0.0F, -16.5F);
        segment.addChild(gum_front);
        gum_front.setTextureOffset(64, 64).addBox(-16.0F, -16.0F, 0.0F, 32.0F, 32.0F, 0.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, segment, gum_back, gum_front);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    public void setGumVisible(boolean front, boolean back){
        this.gum_front.showModel = front;
        this.gum_back.showModel = back;
    }

    @Override
    public void setupAnim(GumWormSegmentEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        this.segment.rotateAngleZ += (float) Math.toRadians(entity.getBodyZRot(partialTicks));

    }
}
