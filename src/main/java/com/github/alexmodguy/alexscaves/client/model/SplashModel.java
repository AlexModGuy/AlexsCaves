package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class SplashModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox base;
    private final AdvancedModelBox highSplash;
    private final AdvancedModelBox ripple;

    public SplashModel() {
        texWidth = 128;
        texHeight = 128;

        base = new AdvancedModelBox(this);
        base.setRotationPoint(0.0F, 0.0F, 0.0F);
        base.setTextureOffset(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F, 0.0F, false);

        highSplash = new AdvancedModelBox(this);
        highSplash.setRotationPoint(0.0F, 0.0F, 0.0F);
        base.addChild(highSplash);
        highSplash.setTextureOffset(0, 52).addBox(-5.0F, -16.0F, -5.0F, 10.0F, 16.0F, 10.0F, 0.0F, false);

        ripple = new AdvancedModelBox(this);
        ripple.setRotationPoint(0.0F, -0.25F, 0.0F);
        base.addChild(ripple);
        ripple.setTextureOffset(0, 0).addBox(-14.0F, 0.0F, -14.0F, 28.0F, 0.0F, 28.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(base);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(base, ripple, highSplash);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float lifetime, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float progress = Math.min(ageInTicks / lifetime, 1F);
        this.base.setScale(1F + progress * 0.35F, (float) Math.sin(progress * Math.PI * 1F), 1F + progress * 0.35F);
        this.highSplash.setScale(1F, Math.max((float) Math.sin(progress * Math.PI * 1F - 0.5F) * 1.3F, 0F), 1F);
        this.ripple.setScale(1F + progress * progress, 1F, 1F + progress * progress);
    }
}