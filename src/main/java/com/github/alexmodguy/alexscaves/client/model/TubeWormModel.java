package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class TubeWormModel extends AdvancedEntityModel {
    private final AdvancedModelBox worm;

    public TubeWormModel() {
        texWidth = 64;
        texHeight = 64;

        worm = new AdvancedModelBox(this);
        worm.setRotationPoint(0.0F, 24.0F, 0.0F);
        worm.setTextureOffset(18, 23).addBox(-3.0F, -18.0F, -3.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
        worm.setTextureOffset(0, 0).addBox(-3.0F, -15.0F, -3.0F, 6.0F, 15.0F, 6.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(worm);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(worm);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }

    public void animateParticle(float age, float tuckAmount, float animationOffset, float yRot, float partialTicks) {
        this.resetToDefaultPose();
        float ageInTicks = age + partialTicks;
        float invTuckAmount = 1F - tuckAmount;
        float bob = (float) (1.0F + Math.sin(ageInTicks * 0.3F + animationOffset)) * 2;
        worm.rotationPointY -= (invTuckAmount * 10) - invTuckAmount * bob + 5;
        worm.rotateAngleY += Math.toRadians(yRot);
        this.walk(worm, 0.35F, 0.15F, false, animationOffset - 1F, 0.0F, ageInTicks, invTuckAmount);
        this.flap(worm, 0.35F, 0.2F, false, animationOffset + 2F, 0.0F, ageInTicks, invTuckAmount);
        this.worm.setScale(1F, Math.min(0.5F + invTuckAmount, 1F), 1F);
    }
}
