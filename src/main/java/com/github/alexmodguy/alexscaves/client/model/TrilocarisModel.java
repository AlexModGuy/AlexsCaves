package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.TrilocarisEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class TrilocarisModel extends AdvancedEntityModel<TrilocarisEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox legs3;
    private final AdvancedModelBox head;
    private final AdvancedModelBox rantennae;
    private final AdvancedModelBox lantennae;
    private final AdvancedModelBox lmandible;
    private final AdvancedModelBox rmandible;
    private final AdvancedModelBox legs;
    private final AdvancedModelBox legs2;
    private final AdvancedModelBox tailFlipper;
    private final AdvancedModelBox lflippers;
    private final AdvancedModelBox lflipper;
    private final AdvancedModelBox lflipper2;
    private final AdvancedModelBox lflipper3;
    private final AdvancedModelBox rflippers;
    private final AdvancedModelBox rflipper;
    private final AdvancedModelBox rflipper2;
    private final AdvancedModelBox rflipper3;
    private final AdvancedModelBox legs4;

    public TrilocarisModel() {
        texWidth = 64;
        texHeight = 64;
        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 20.0F, 0.0F);
        body.setTextureOffset(21, 18).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 2.0F, 5.0F, 0.0F, false);
        body.setTextureOffset(16, 8).addBox(0.0F, -2.0F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, false);

        legs3 = new AdvancedModelBox(this);
        legs3.setRotationPoint(0.0F, 1.0F, 1.0F);
        body.addChild(legs3);
        legs3.setTextureOffset(0, 5).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 0.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(head);
        head.setTextureOffset(0, 7).addBox(0.0F, -3.0F, -5.0F, 0.0F, 3.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(21, 9).addBox(-4.0F, -2.0F, -5.0F, 8.0F, 3.0F, 5.0F, 0.0F, false);

        rantennae = new AdvancedModelBox(this);
        rantennae.setRotationPoint(-1.5F, -2.0F, -5.0F);
        head.addChild(rantennae);
        rantennae.setTextureOffset(42, 5).addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 5.0F, 0.0F, false);

        lantennae = new AdvancedModelBox(this);
        lantennae.setRotationPoint(1.5F, -2.0F, -5.0F);
        head.addChild(lantennae);
        lantennae.setTextureOffset(42, 5).addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 5.0F, 0.0F, true);

        lmandible = new AdvancedModelBox(this);
        lmandible.setRotationPoint(3.0F, 0.5F, -5.0F);
        head.addChild(lmandible);
        lmandible.setTextureOffset(0, 22).addBox(-2.0F, -0.5F, -6.0F, 3.0F, 1.0F, 7.0F, 0.0F, false);

        rmandible = new AdvancedModelBox(this);
        rmandible.setRotationPoint(-3.0F, 0.5F, -5.0F);
        head.addChild(rmandible);
        rmandible.setTextureOffset(0, 22).addBox(-1.0F, -0.5F, -6.0F, 3.0F, 1.0F, 7.0F, 0.0F, true);

        legs = new AdvancedModelBox(this);
        legs.setRotationPoint(0.0F, 1.0F, -3.0F);
        head.addChild(legs);
        legs.setTextureOffset(0, 5).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 0.0F, 0.0F, false);

        legs2 = new AdvancedModelBox(this);
        legs2.setRotationPoint(0.0F, 1.0F, -1.0F);
        head.addChild(legs2);
        legs2.setTextureOffset(0, 5).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 0.0F, 0.0F, false);

        tailFlipper = new AdvancedModelBox(this);
        tailFlipper.setRotationPoint(0.0F, 0.0F, 5.0F);
        body.addChild(tailFlipper);
        tailFlipper.setTextureOffset(0, 0).addBox(-6.0F, 0.0F, 0.0F, 12.0F, 0.0F, 5.0F, 0.0F, false);

        lflippers = new AdvancedModelBox(this);
        lflippers.setRotationPoint(3.0F, 1.0F, 3.0F);
        body.addChild(lflippers);


        lflipper = new AdvancedModelBox(this);
        lflipper.setRotationPoint(0.0F, 0.0F, -2.0F);
        lflippers.addChild(lflipper);
        lflipper.setTextureOffset(0, 8).addBox(0.0F, 0.0F, -1.0F, 4.0F, 0.0F, 2.0F, 0.0F, false);

        lflipper2 = new AdvancedModelBox(this);
        lflipper2.setRotationPoint(0.0F, 0.0F, 0.0F);
        lflippers.addChild(lflipper2);
        lflipper2.setTextureOffset(0, 8).addBox(0.0F, 0.0F, -1.0F, 4.0F, 0.0F, 2.0F, 0.0F, false);

        lflipper3 = new AdvancedModelBox(this);
        lflipper3.setRotationPoint(0.0F, 0.0F, 2.0F);
        lflippers.addChild(lflipper3);
        lflipper3.setTextureOffset(0, 8).addBox(0.0F, 0.0F, -1.0F, 4.0F, 0.0F, 2.0F, 0.0F, false);

        rflippers = new AdvancedModelBox(this);
        rflippers.setRotationPoint(-3.0F, 1.0F, 3.0F);
        body.addChild(rflippers);


        rflipper = new AdvancedModelBox(this);
        rflipper.setRotationPoint(0.0F, 0.0F, -2.0F);
        rflippers.addChild(rflipper);
        rflipper.setTextureOffset(0, 8).addBox(-4.0F, 0.0F, -1.0F, 4.0F, 0.0F, 2.0F, 0.0F, true);

        rflipper2 = new AdvancedModelBox(this);
        rflipper2.setRotationPoint(0.0F, 0.0F, 0.0F);
        rflippers.addChild(rflipper2);
        rflipper2.setTextureOffset(0, 8).addBox(-4.0F, 0.0F, -1.0F, 4.0F, 0.0F, 2.0F, 0.0F, true);

        rflipper3 = new AdvancedModelBox(this);
        rflipper3.setRotationPoint(0.0F, 0.0F, 2.0F);
        rflippers.addChild(rflipper3);
        rflipper3.setTextureOffset(0, 8).addBox(-4.0F, 0.0F, -1.0F, 4.0F, 0.0F, 2.0F, 0.0F, true);

        legs4 = new AdvancedModelBox(this);
        legs4.setRotationPoint(0.0F, 1.0F, 3.0F);
        body.addChild(legs4);
        legs4.setTextureOffset(0, 5).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 0.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, head, tailFlipper, legs, legs2, legs3, legs4, rflipper, rflipper2, rflipper3, rflippers, lflipper, lflipper2, lflipper3, lflippers, lantennae, rantennae, lmandible, rmandible);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public void setupAnim(TrilocarisEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 1F;
        float walkDegree = 1F;
        float partialTicks = ageInTicks - entity.tickCount;
        float groundProgress = entity.getGroundProgress(partialTicks);
        float swimProgress = 1F - groundProgress;
        float biteProgress = entity.getBiteProgress(partialTicks);
        float walkAmount = Math.min(limbSwingAmount * groundProgress * 10F, 1F);
        float swimAmount = limbSwingAmount * swimProgress;
        if (entity.deathTime > 0) {
            limbSwing = ageInTicks;
            limbSwingAmount = 1;
        }
        progressPositionPrev(lflippers, swimProgress, 0, 0, -1F, 1F);
        progressPositionPrev(rflippers, swimProgress, 0, 0, -1F, 1F);
        progressPositionPrev(legs, swimProgress, 0, -2F, 0F, 1F);
        progressPositionPrev(legs2, swimProgress, 0, -2F, 0F, 1F);
        progressPositionPrev(legs3, swimProgress, 0, -2F, 0F, 1F);
        progressPositionPrev(legs4, swimProgress, 0, -2F, 0F, 1F);
        progressRotationPrev(legs, swimProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(legs2, swimProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(legs3, swimProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(legs4, swimProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(rmandible, biteProgress, 0, (float) Math.toRadians(40), 0, 1F);
        progressRotationPrev(lmandible, biteProgress, 0, (float) Math.toRadians(-40), 0, 1F);
        this.walk(lantennae, 0.1F, 0.15F, true, 1F, -0.1F, ageInTicks, 1);
        this.walk(rantennae, walkSpeed, walkDegree * 0.1F, false, 1F, -0.7F, limbSwing, limbSwingAmount);
        this.walk(lantennae, walkSpeed, walkDegree * 0.1F, true, 1F, 0.7F, limbSwing, limbSwingAmount);
        this.walk(rantennae, 0.1F, 0.15F, false, 1F, 0.1F, ageInTicks, 1);
        this.swing(rmandible, 0.1F, 0.15F, true, 2F, 0.05F, ageInTicks, 1);
        this.swing(lmandible, 0.1F, 0.15F, false, 2F, 0.05F, ageInTicks, 1);
        float bodyBob = Math.abs((float) ((Math.cos(ageInTicks * 0.1F)))) * swimProgress;
        this.body.rotationPointY += bodyBob;
        this.legs.rotationPointY -= bodyBob * groundProgress;
        this.legs2.rotationPointY -= bodyBob * groundProgress;
        this.legs3.rotationPointY -= bodyBob * groundProgress;
        this.legs4.rotationPointY -= bodyBob * groundProgress;
        this.walk(legs, walkSpeed, walkDegree * 0.5F, true, 1F, -0.1F, limbSwing, walkAmount);
        this.walk(legs2, walkSpeed, walkDegree * 0.5F, true, 2F, -0.1F, limbSwing, walkAmount);
        this.walk(legs3, walkSpeed, walkDegree * 0.5F, true, 3F, -0.1F, limbSwing, walkAmount);
        this.walk(legs4, walkSpeed, walkDegree * 0.5F, true, 4F, -0.1F, limbSwing, walkAmount);
        this.walk(body, walkSpeed, walkDegree * 0.15F, false, 0F, 0F, limbSwing, swimAmount);
        this.flap(rflipper, walkSpeed, walkDegree, true, 3F, 0.05F, limbSwing, swimAmount);
        this.flap(rflipper2, walkSpeed, walkDegree, true, 2F, 0.05F, limbSwing, swimAmount);
        this.flap(rflipper3, walkSpeed, walkDegree, true, 1F, 0.05F, limbSwing, swimAmount);
        this.flap(lflipper, walkSpeed, walkDegree, false, 3F, 0.05F, limbSwing, swimAmount);
        this.flap(lflipper2, walkSpeed, walkDegree, false, 2F, 0.05F, limbSwing, swimAmount);
        this.flap(lflipper3, walkSpeed, walkDegree, false, 1F, 0.05F, limbSwing, swimAmount);
        this.walk(tailFlipper, walkSpeed, walkDegree, false, 5F, 0.05F, limbSwing, swimAmount);
    }
}