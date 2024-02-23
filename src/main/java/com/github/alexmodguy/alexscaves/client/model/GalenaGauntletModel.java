package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class GalenaGauntletModel extends AdvancedEntityModel<Entity> {

    private final AdvancedModelBox base;
    private final AdvancedModelBox fingers;
    private final AdvancedModelBox thumb;
    private final AdvancedModelBox finger;
    private final AdvancedModelBox finger3;
    private final AdvancedModelBox finger2;

    private final boolean left;

    public GalenaGauntletModel(boolean left) {
        texWidth = 128;
        texHeight = 128;
        this.left = left;

        base = new AdvancedModelBox(this);
        base.setRotationPoint(0.0F, 6.75F, -0.5F);
        base.setTextureOffset(27, 31).addBox(-4.5F, 0.25F, -4.5F, 9.0F, 11.0F, 9.0F, 0.01F, false);
        base.setTextureOffset(0, 0).addBox(-4.5F, 0.25F, -4.5F, 9.0F, 11.0F, 9.0F, 0.26F, false);

        fingers = new AdvancedModelBox(this);
        fingers.setRotationPoint(0.0F, 9.25F, 0.5F);
        base.addChild(fingers);


        thumb = new AdvancedModelBox(this);
        if (!left) {
            thumb.setRotationPoint(4.0F, 0.5F, 2.0F);
            setRotateAngle(thumb, 0.0F, 3.1416F, 0.0F);
            thumb.setTextureOffset(27, 0).addBox(-4.5F, -1.5F, -2.0F, 5.0F, 3.0F, 4.0F, 0.25F, false);
            thumb.setTextureOffset(36, 23).addBox(-4.5F, -1.5F, -2.0F, 5.0F, 3.0F, 4.0F, 0.0F, false);
        } else {
            thumb.setRotationPoint(-5.0F, 0.5F, 2.0F);
            thumb.setTextureOffset(27, 0).addBox(-4.5F, -1.5F, -2.0F, 5.0F, 3.0F, 4.0F, 0.25F, false);
            thumb.setTextureOffset(36, 23).addBox(-4.5F, -1.5F, -2.0F, 5.0F, 3.0F, 4.0F, 0.0F, false);
        }

        fingers.addChild(thumb);

        finger = new AdvancedModelBox(this);
        finger.setRotationPoint(-4.5F, 0.5F, -5.0F);
        fingers.addChild(finger);
        finger.setTextureOffset(30, 54).addBox(-2.0F, -1.75F, -6.0F, 4.0F, 3.0F, 6.0F, 0.25F, true);
        finger.setTextureOffset(56, 54).addBox(-2.0F, -1.75F, -6.0F, 4.0F, 3.0F, 6.0F, 0.0F, false);

        finger3 = new AdvancedModelBox(this);
        finger3.setRotationPoint(4.5F, 0.25F, -5.0F);
        fingers.addChild(finger3);
        finger3.setTextureOffset(30, 54).addBox(-2.0F, -1.5F, -6.0F, 4.0F, 3.0F, 6.0F, 0.25F, true);
        finger3.setTextureOffset(56, 54).addBox(-2.0F, -1.5F, -6.0F, 4.0F, 3.0F, 6.0F, 0.0F, true);

        finger2 = new AdvancedModelBox(this);
        finger2.setRotationPoint(0.0F, 0.25F, -5.0F);
        fingers.addChild(finger2);
        finger2.setTextureOffset(30, 54).addBox(-2.0F, -1.5F, -6.0F, 4.0F, 3.0F, 6.0F, 0.25F, true);
        finger2.setTextureOffset(56, 54).addBox(-2.0F, -1.5F, -6.0F, 4.0F, 3.0F, 6.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(Entity entity, float openAmount, float switchProgress, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float closeAmount = 1F - openAmount;
        float leftOff = left ? -1 : 1;
        if (left) {
            progressRotationPrev(base, closeAmount, 0, (float) Math.toRadians(-90), 0, 1F);
            progressRotationPrev(thumb, closeAmount, 0, 0, (float) Math.toRadians(-90), 1F);
        } else {
            progressRotationPrev(base, closeAmount, 0, (float) Math.toRadians(90), 0, 1F);
            progressRotationPrev(thumb, closeAmount, 0, 0, (float) Math.toRadians(90), 1F);
        }
        progressRotationPrev(finger, closeAmount, (float) Math.toRadians(180), 0, 0, 1F);
        progressRotationPrev(finger2, closeAmount, (float) Math.toRadians(180), 0, 0, 1F);
        progressRotationPrev(finger3, closeAmount, (float) Math.toRadians(180), 0, 0, 1F);
        progressPositionPrev(finger, closeAmount, 1, 2, 0, 1F);
        progressPositionPrev(finger2, closeAmount, 0, 3, -1, 1F);
        progressPositionPrev(finger3, closeAmount, -1, 2.5F, 0, 1F);
        progressPositionPrev(thumb, closeAmount, 0, -2.5F, 0, 1F);
        this.walk(finger, 0.1F, 0.3F, true, 3 * leftOff, -0.4F, ageInTicks, openAmount);
        this.swing(finger, 0.1F, 0.1F, left, 5 * leftOff, 0.1F * leftOff, ageInTicks, openAmount);
        this.walk(finger2, 0.1F, 0.3F, true, 2 * leftOff, -0.4F, ageInTicks, openAmount);
        this.walk(finger3, 0.1F, 0.3F, true, 1 * leftOff, -0.4F, ageInTicks, openAmount);
        this.swing(finger3, 0.1F, -0.1F, left, 2 * leftOff, -0.1F * leftOff, ageInTicks, openAmount);
        this.flap(thumb, 0.1F, 0.3F * leftOff, true, -1, -0.2F * leftOff, ageInTicks, openAmount);

    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(base, fingers, thumb, finger, finger2, finger3);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(base);
    }
}