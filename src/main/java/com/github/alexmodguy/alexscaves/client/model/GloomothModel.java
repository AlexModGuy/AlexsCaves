package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.GloomothEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class GloomothModel extends AdvancedEntityModel<GloomothEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox rantennae;
    private final AdvancedModelBox lantennae;
    private final AdvancedModelBox lWing;
    private final AdvancedModelBox lWing_b;
    private final AdvancedModelBox lWing_s;
    private final AdvancedModelBox rWing;
    private final AdvancedModelBox rWing_b;
    private final AdvancedModelBox rWing_s;
    private final AdvancedModelBox legs;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox rleg3;
    private final AdvancedModelBox lleg3;

    public GloomothModel() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 17.5F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(31, 25).addBox(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 7.0F, 0.0F, false);
        body.setTextureOffset(0, 32).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 7.0F, 5.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -1.0F, -5.5F);
        body.addChild(head);
        head.setTextureOffset(0, 0).addBox(-2.5F, -2.0F, -2.5F, 5.0F, 4.0F, 3.0F, 0.0F, false);

        rantennae = new AdvancedModelBox(this);
        rantennae.setRotationPoint(-1.0F, -2.0F, -2.5F);
        head.addChild(rantennae);
        rantennae.setTextureOffset(22, 38).addBox(-7.5F, -7.0F, 0.0F, 8.0F, 7.0F, 0.0F, 0.0F, false);

        lantennae = new AdvancedModelBox(this);
        lantennae.setRotationPoint(1.0F, -2.0F, -2.5F);
        head.addChild(lantennae);
        lantennae.setTextureOffset(22, 38).addBox(-0.5F, -7.0F, 0.0F, 8.0F, 7.0F, 0.0F, 0.0F, true);

        lWing = new AdvancedModelBox(this);
        lWing.setRotationPoint(2.5F, -2.0F, -2.0F);
        body.addChild(lWing);


        lWing_b = new AdvancedModelBox(this);
        lWing_b.setRotationPoint(0.0F, 0.0F, 0.0F);
        lWing.addChild(lWing_b);
        lWing_b.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -12.0F, 13.0F, 0.0F, 16.0F, 0.0F, false);

        lWing_s = new AdvancedModelBox(this);
        lWing_s.setRotationPoint(0.0F, 0.25F, 0.0F);
        lWing.addChild(lWing_s);
        lWing_s.setTextureOffset(0, 16).addBox(0.0F, 0.0F, -2.0F, 11.0F, 0.0F, 16.0F, 0.0F, false);

        rWing = new AdvancedModelBox(this);
        rWing.setRotationPoint(-2.5F, -2.0F, -2.0F);
        body.addChild(rWing);


        rWing_b = new AdvancedModelBox(this);
        rWing_b.setRotationPoint(0.0F, 0.0F, 0.0F);
        rWing.addChild(rWing_b);
        rWing_b.setTextureOffset(0, 0).addBox(-12.5F, 0.0F, -12.0F, 13.0F, 0.0F, 16.0F, 0.0F, true);

        rWing_s = new AdvancedModelBox(this);
        rWing_s.setRotationPoint(0.0F, 0.25F, 0.0F);
        rWing.addChild(rWing_s);
        rWing_s.setTextureOffset(0, 16).addBox(-11.0F, 0.0F, -2.0F, 11.0F, 0.0F, 16.0F, 0.0F, true);

        legs = new AdvancedModelBox(this);
        legs.setRotationPoint(0.0F, 2.5F, -2.0F);
        root.addChild(legs);


        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(1.0F, 0.0F, -2.0F);
        legs.addChild(lleg);
        lleg.setTextureOffset(2, 11).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, 0.0F, true);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-1.0F, 0.0F, -2.0F);
        legs.addChild(rleg);
        rleg.setTextureOffset(2, 11).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, 0.0F, false);

        lleg2 = new AdvancedModelBox(this);
        lleg2.setRotationPoint(1.0F, 0.0F, 0.0F);
        legs.addChild(lleg2);
        lleg2.setTextureOffset(2, 11).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, 0.0F, true);

        rleg2 = new AdvancedModelBox(this);
        rleg2.setRotationPoint(-1.0F, 0.0F, 0.0F);
        legs.addChild(rleg2);
        rleg2.setTextureOffset(2, 11).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, 0.0F, false);

        rleg3 = new AdvancedModelBox(this);
        rleg3.setRotationPoint(-1.0F, 0.0F, 2.0F);
        legs.addChild(rleg3);
        rleg3.setTextureOffset(2, 11).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, 0.0F, false);

        lleg3 = new AdvancedModelBox(this);
        lleg3.setRotationPoint(1.0F, 0.0F, 2.0F);
        legs.addChild(lleg3);
        lleg3.setTextureOffset(2, 11).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(GloomothEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 2F;
        float walkDegree = 0.7F;
        float flySpeed = 0.6F;
        float flyDegree = 0.9F;
        float partialTick = ageInTicks - entity.tickCount;
        float flyProgress = entity.getFlyProgress(partialTick);
        float flapAmount = flyProgress;
        float rollAmount = entity.getFlightRoll(partialTick) / 57.295776F * flyProgress;
        float pitchAmount = entity.getFlightPitch(partialTick) / 57.295776F * flyProgress;
        float groundedAmount = 1 - flyProgress;
        progressRotationPrev(lantennae, flyProgress, (float) Math.toRadians(40), 0, 0, 1F);
        progressRotationPrev(rantennae, flyProgress, (float) Math.toRadians(40), 0, 0, 1F);
        progressRotationPrev(body, flyProgress, (float) Math.toRadians(-10), 0, 0, 1F);
        progressRotationPrev(lleg, flyProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(lleg2, flyProgress, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(lleg3, flyProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(rleg, flyProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(rleg2, flyProgress, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(rleg3, flyProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressPositionPrev(legs, flyProgress, 0, -1, 0, 1F);
        this.walk(rantennae, 0.1F, 0.15F, true, 1F, -0.3F, ageInTicks, 1);
        this.walk(lantennae, 0.1F, 0.15F, true, 1F, -0.3F, ageInTicks, 1);
        this.flap(rantennae, 0.1F, 0.15F, false, 1F, -0.1F, ageInTicks, 1);
        this.flap(lantennae, 0.1F, 0.15F, true, 1F, -0.1F, ageInTicks, 1);
        this.walk(lleg, walkSpeed, walkDegree, false, 4F, 0.1F, limbSwing, limbSwingAmount * groundedAmount);
        this.walk(lleg2, walkSpeed, walkDegree, false, 2.5F, 0.1F, limbSwing, limbSwingAmount * groundedAmount);
        this.walk(lleg3, walkSpeed, walkDegree, false, 1F, 0.1F, limbSwing, limbSwingAmount * groundedAmount);
        this.walk(rleg, walkSpeed, walkDegree, true, 4F, -0.1F, limbSwing, limbSwingAmount * groundedAmount);
        this.walk(rleg2, walkSpeed, walkDegree, true, 2.5F, -0.1F, limbSwing, limbSwingAmount * groundedAmount);
        this.walk(rleg3, walkSpeed, walkDegree, true, 1F, -0.1F, limbSwing, limbSwingAmount * groundedAmount);
        this.flap(rWing, flySpeed * 1.5F, flyDegree, false, 1.5F, -0.1F, ageInTicks, flapAmount);
        this.flap(lWing, flySpeed * 1.5F, flyDegree, true, 1.5F, -0.1F, ageInTicks, flapAmount);
        this.flap(rWing_s, flySpeed * 1.5F, flyDegree * 0.45F, false, 2F, -0.1F, ageInTicks, flapAmount);
        this.flap(lWing_s, flySpeed * 1.5F, flyDegree * 0.45F, true, 2F, -0.1F, ageInTicks, flapAmount);
        this.bob(root, flySpeed, flyDegree * 2, false, ageInTicks, flyProgress);
        root.rotateAngleX += pitchAmount;
        root.rotateAngleZ += rollAmount;

    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, rantennae, lantennae, lWing, rWing, lWing_s, rWing_s, lWing_b, rWing_b, legs, rleg, rleg2, rleg3, lleg, lleg2, lleg3);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

}