package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.CaniacEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumbeeperEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class GumbeeperModel extends AdvancedEntityModel<GumbeeperEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox coin_Wheel;
    private final AdvancedModelBox right_backLeg;
    private final AdvancedModelBox left_backLeg;
    private final AdvancedModelBox right_frontLeg;
    private final AdvancedModelBox left_frontLeg;
    private final AdvancedModelBox head;
    private final AdvancedModelBox gum_layers;
    private final AdvancedModelBox gum_layerFinal;
    private final AdvancedModelBox gum_layer6;
    private final AdvancedModelBox gum_layer5;
    private final AdvancedModelBox gum_layer4;
    private final AdvancedModelBox gum_layer3;
    private final AdvancedModelBox gum_layer2;
    private final AdvancedModelBox gum_layer;

    public GumbeeperModel(float f) {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(1.0F, -5.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(16, 49).addBox(-5.0F, -7.0F, -4.0F, 8.0F, 9.0F, 8.0F, f + 0.25F, false);
        body.setTextureOffset(40, 40).addBox(-5.0F, -7.0F, -4.0F, 8.0F, 9.0F, 8.0F, f, false);

        coin_Wheel = new AdvancedModelBox(this);
        coin_Wheel.setRotationPoint(-1.0F, -4.0F, -5.0F);
        body.addChild(coin_Wheel);
        coin_Wheel.setTextureOffset(0, 0).addBox(0.0F, -2.0F, -1.0F, 0.0F, 4.0F, 2.0F, f, false);

        right_backLeg = new AdvancedModelBox(this);
        right_backLeg.setRotationPoint(-4.0F, 1.0F, 3.0F);
        body.addChild(right_backLeg);
        right_backLeg.setTextureOffset(52, 26).addBox(-3.0F, -1.0F, -1.0F, 4.0F, 5.0F, 4.0F, f, true);

        left_backLeg = new AdvancedModelBox(this);
        left_backLeg.setRotationPoint(2.0F, 1.0F, 3.0F);
        body.addChild(left_backLeg);
        left_backLeg.setTextureOffset(52, 26).addBox(-1.0F, -1.0F, -1.0F, 4.0F, 5.0F, 4.0F, f, false);

        right_frontLeg = new AdvancedModelBox(this);
        right_frontLeg.setRotationPoint(-4.0F, 1.0F, -3.0F);
        body.addChild(right_frontLeg);
        right_frontLeg.setTextureOffset(36, 26).addBox(-3.0F, -1.0F, -3.0F, 4.0F, 5.0F, 4.0F, f, true);

        left_frontLeg = new AdvancedModelBox(this);
        left_frontLeg.setRotationPoint(2.0F, 1.0F, -3.0F);
        body.addChild(left_frontLeg);
        left_frontLeg.setTextureOffset(36, 26).addBox(-1.0F, -1.0F, -3.0F, 4.0F, 5.0F, 4.0F, f, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(-1.0F, -7.0F, 0.0F);
        body.addChild(head);
        head.setTextureOffset(0, 24).addBox(-6.0F, -14.0F, -6.0F, 12.0F, 12.0F, 12.0F, f, false);
        head.setTextureOffset(36, 12).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, f, false);
        head.setTextureOffset(36, 0).addBox(-4.0F, -17.0F, -4.0F, 8.0F, 3.0F, 8.0F, f, false);

        gum_layers = new AdvancedModelBox(this);
        gum_layers.setRotationPoint(-0.5F, -7.75F, -0.5F);
        head.addChild(gum_layers);


        gum_layerFinal = new AdvancedModelBox(this);
        gum_layerFinal.setRotationPoint(0.0F, 0.0F, 0.0F);
        gum_layers.addChild(gum_layerFinal);
        if(f <= 0.0F) {
            gum_layerFinal.setTextureOffset(2, 7).addBox(-5.0F, 0.25F, -5.0F, 11.0F, 6.0F, 11.0F, f, false);
        }
        gum_layer6 = new AdvancedModelBox(this);
        gum_layer6.setRotationPoint(0.0F, -3.0F, 0.0F);
        gum_layers.addChild(gum_layer6);
        if(f <= 0.0F) {
            gum_layer6.setTextureOffset(2, 6).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        gum_layer5 = new AdvancedModelBox(this);
        gum_layer5.setRotationPoint(0.0F, -4.0F, 0.0F);
        gum_layers.addChild(gum_layer5);
        if(f <= 0.0F) {
            gum_layer5.setTextureOffset(2, 5).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        gum_layer4 = new AdvancedModelBox(this);
        gum_layer4.setRotationPoint(0.0F, -5.0F, 0.0F);
        gum_layers.addChild(gum_layer4);
        if(f <= 0.0F){
            gum_layer4.setTextureOffset(2, 4).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }

        gum_layer3 = new AdvancedModelBox(this);
        gum_layer3.setRotationPoint(0.0F, -6.0F, 0.0F);
        gum_layers.addChild(gum_layer3);
        if(f <= 0.0F) {
            gum_layer3.setTextureOffset(2, 3).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        gum_layer2 = new AdvancedModelBox(this);
        gum_layer2.setRotationPoint(0.0F, -7.0F, 0.0F);
        gum_layers.addChild(gum_layer2);
        if(f <= 0.0F) {
            gum_layer2.setTextureOffset(2, 2).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        gum_layer = new AdvancedModelBox(this);
        gum_layer.setRotationPoint(0.0F, -8.0F, 0.0F);
        gum_layers.addChild(gum_layer);
        if(f <= 0.0F) {
            gum_layer.setTextureOffset(2, 1).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, gum_layer, gum_layers, gum_layer2, gum_layer3, gum_layer4, gum_layer5, gum_layer6, gum_layerFinal, coin_Wheel, left_backLeg, left_frontLeg, right_backLeg, right_frontLeg);
    }

    public void setupAnim(GumbeeperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 0.8F;
        float walkDegree = 1.2F;
        float partialTick = ageInTicks - entity.tickCount;
        float explodeProgress = entity.getExplodeProgress(partialTick);
        float dialRot = (float) Math.toRadians(entity.getDialRot(partialTick));
        float dialRotFinalStretch = Math.max(dialRot - 400, 0) / 50F;
        float shootProgress = entity.getShootProgress(partialTick);
        int gumballsLeft = entity.getGumballsLeft();
        gum_layer6.showModel = gumballsLeft > 0;
        gum_layer5.showModel = gumballsLeft > 1;
        gum_layer4.showModel = gumballsLeft > 2;
        gum_layer3.showModel = gumballsLeft > 3;
        gum_layer2.showModel = gumballsLeft > 4;
        gum_layer.showModel = gumballsLeft > 5;
        progressRotationPrev(body, shootProgress, (float) Math.toRadians(-15), 0F, 0F, 1F);
        progressRotationPrev(head, shootProgress, (float) Math.toRadians(-5), 0F, 0F, 1F);
        progressRotationPrev(left_backLeg, shootProgress, (float) Math.toRadians(15), 0F, 0F, 1F);
        progressRotationPrev(right_backLeg, shootProgress, (float) Math.toRadians(15), 0F, 0F, 1F);
        progressRotationPrev(left_frontLeg, shootProgress, (float) Math.toRadians(15), 0F, 0F, 1F);
        progressRotationPrev(right_frontLeg, shootProgress, (float) Math.toRadians(15), 0F, 0F, 1F);
        progressPositionPrev(body, shootProgress, 0, -1F, 0F, 1F);
        progressPositionPrev(head, shootProgress, 0, 1F, 0F, 1F);
        progressPositionPrev(right_frontLeg, shootProgress, 0, 2F, 0F, 1F);
        progressPositionPrev(left_frontLeg, shootProgress, 0, 2F, 0F, 1F);
        this.coin_Wheel.rotateAngleZ += dialRot;
        this.coin_Wheel.rotationPointZ += dialRotFinalStretch * 1.5F;
        this.body.setScale(1F + explodeProgress * 0.15F, 1F - explodeProgress * 0.2F, 1F + explodeProgress * 0.15F);
        this.body.scaleChildren = true;
        float bodyBob = ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed * 1.5F, 0.5F, 0.5F, true);
        this.body.rotationPointY += bodyBob;
        this.walk(right_frontLeg, walkSpeed, walkDegree * 0.4F, true, 2.5F, 0.2F, limbSwing, limbSwingAmount);
        right_frontLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 0, 4F, false)) - bodyBob;
        this.walk(left_frontLeg, walkSpeed, walkDegree * 0.4F, false, 2.5F, -0.2F, limbSwing, limbSwingAmount);
        left_frontLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 0, 4F, true)) - bodyBob;
        this.walk(right_backLeg, walkSpeed, walkDegree * 0.4F, false, 2.5F, 0.2F, limbSwing, limbSwingAmount);
        right_backLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 0, 4F, true)) - bodyBob;
        this.walk(left_backLeg, walkSpeed, walkDegree * 0.4F, true, 2.5F, -0.2F, limbSwing, limbSwingAmount);
        left_backLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 0, 4F, false)) - bodyBob;
        this.swing(body, 3F, 0.2F, true, 1F, 0F, ageInTicks, explodeProgress);
        this.swing(head, 1.3F, 0.1F, true, 1F, 0F, ageInTicks, shootProgress);
    }
}
