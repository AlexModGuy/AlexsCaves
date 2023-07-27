package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.GammaroachEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class GammaroachModel extends AdvancedEntityModel<GammaroachEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox bodyThing2;
    private final AdvancedModelBox bodyThing;
    private final AdvancedModelBox ltail;
    private final AdvancedModelBox rtail;
    private final AdvancedModelBox mtail;
    private final AdvancedModelBox carapace;
    private final AdvancedModelBox head;
    private final AdvancedModelBox lantennae;
    private final AdvancedModelBox antenna1Thing;
    private final AdvancedModelBox rantennae;
    private final AdvancedModelBox atenna2Thing;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox cube_r5;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox rleg3;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox lleg3;
    private final ModelAnimator animator;

    public GammaroachModel() {
        texWidth = 128;
        texHeight = 128;
        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 14.5F, 0.0F);
        body.setTextureOffset(33, 22).addBox(0.0F, -6.5F, 2.0F, 0.0F, 5.0F, 11.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-5.0F, -2.5F, -6.0F, 10.0F, 5.0F, 19.0F, 0.0F, false);

        bodyThing2 = new AdvancedModelBox(this);
        bodyThing2.setRotationPoint(-5.0F, -2.5F, 7.5F);
        body.addChild(bodyThing2);
        setRotateAngle(bodyThing2, 0.0F, 0.0F, -0.7854F);
        bodyThing2.setTextureOffset(22, 36).addBox(0.0F, -4.0F, -5.5F, 0.0F, 5.0F, 11.0F, 0.0F, true);

        bodyThing = new AdvancedModelBox(this);
        bodyThing.setRotationPoint(5.0F, -2.5F, 7.5F);
        body.addChild(bodyThing);
        setRotateAngle(bodyThing, 0.0F, 0.0F, 0.7854F);
        bodyThing.setTextureOffset(22, 36).addBox(0.0F, -4.0F, -5.5F, 0.0F, 5.0F, 11.0F, 0.0F, false);

        ltail = new AdvancedModelBox(this);
        ltail.setRotationPoint(3.5F, 1.5F, 13.0F);
        body.addChild(ltail);
        ltail.setTextureOffset(39, 24).addBox(-3.5F, 0.0F, 0.0F, 8.0F, 0.0F, 4.0F, 0.0F, false);

        rtail = new AdvancedModelBox(this);
        rtail.setRotationPoint(-3.5F, 1.5F, 13.0F);
        body.addChild(rtail);
        rtail.setTextureOffset(39, 24).addBox(-4.5F, 0.0F, 0.0F, 8.0F, 0.0F, 4.0F, 0.0F, true);

        mtail = new AdvancedModelBox(this);
        mtail.setRotationPoint(0.0F, 0.5F, 13.0F);
        body.addChild(mtail);
        mtail.setTextureOffset(30, 7).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 0.0F, 9.0F, 0.0F, false);

        carapace = new AdvancedModelBox(this);
        carapace.setRotationPoint(0.0F, -2.5F, -6.0F);
        body.addChild(carapace);
        carapace.setTextureOffset(0, 41).addBox(0.0F, -4.0F, -2.5F, 0.0F, 5.0F, 11.0F, 0.0F, false);
        carapace.setTextureOffset(0, 33).addBox(-6.0F, -1.0F, -0.5F, 12.0F, 5.0F, 9.0F, 0.0F, false);
        carapace.setTextureOffset(6, 8).addBox(4.0F, -3.0F, -0.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        carapace.setTextureOffset(0, 6).addBox(-6.0F, -3.0F, -0.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 2.0F, 0.5F);
        carapace.addChild(head);
        head.setTextureOffset(42, 38).addBox(-3.5F, -1.5F, -4.0F, 7.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(0, 0).addBox(-3.0F, 0.0F, -3.5F, 6.0F, 3.0F, 3.0F, 0.0F, false);

        lantennae = new AdvancedModelBox(this);
        lantennae.setRotationPoint(2.5F, 0.75F, -3.0F);
        head.addChild(lantennae);


        antenna1Thing = new AdvancedModelBox(this);
        antenna1Thing.setRotationPoint(-0.5F, 0.0F, 0.0F);
        lantennae.addChild(antenna1Thing);
        setRotateAngle(antenna1Thing, 0.0F, 0.0F, -0.7854F);
        antenna1Thing.setTextureOffset(0, 24).addBox(-2.0F, 0.0F, -7.5F, 17.0F, 0.0F, 9.0F, 0.0F, false);

        rantennae = new AdvancedModelBox(this);
        rantennae.setRotationPoint(-2.5F, 0.75F, -3.0F);
        head.addChild(rantennae);


        atenna2Thing = new AdvancedModelBox(this);
        atenna2Thing.setRotationPoint(0.5F, 0.0F, 0.0F);
        rantennae.addChild(atenna2Thing);
        setRotateAngle(atenna2Thing, 0.0F, 0.0F, 0.7854F);
        atenna2Thing.setTextureOffset(0, 24).addBox(-15.0F, 0.0F, -7.5F, 17.0F, 0.0F, 9.0F, 0.0F, true);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-1.5F, 2.0F, -5.0F);
        body.addChild(rleg);

        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(0.0F, 0.5F, 0.0F);
        rleg.addChild(cube_r5);
        setRotateAngle(cube_r5, 0.0F, -0.7854F, 0.0F);
        cube_r5.setTextureOffset(58, 12).addBox(-11.5F, -1.0F, 0.0F, 12.0F, 8.0F, 0.0F, 0.0F, true);

        rleg2 = new AdvancedModelBox(this);
        rleg2.setRotationPoint(-1.5F, 2.0F, -1.5F);
        body.addChild(rleg2);
        rleg2.setTextureOffset(49, 0).addBox(-11.5F, -3.5F, 0.0F, 12.0F, 11.0F, 0.0F, 0.0F, true);

        rleg3 = new AdvancedModelBox(this);
        rleg3.setRotationPoint(-1.5F, 2.0F, 2.0F);
        body.addChild(rleg3);
        setRotateAngle(rleg3, 0.0F, 0.7854F, 0.0F);
        rleg3.setTextureOffset(0, 57).addBox(-20.5F, -3.5F, 0.0F, 21.0F, 11.0F, 0.0F, 0.0F, true);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(1.5F, 2.0F, -5.0F);
        body.addChild(lleg);
        setRotateAngle(lleg, 0.0F, 0.7854F, 0.0F);
        lleg.setTextureOffset(58, 12).addBox(-0.5F, -0.5F, 0.0F, 12.0F, 8.0F, 0.0F, 0.0F, false);

        lleg2 = new AdvancedModelBox(this);
        lleg2.setRotationPoint(1.5F, 2.0F, -1.5F);
        body.addChild(lleg2);
        lleg2.setTextureOffset(49, 0).addBox(-0.5F, -3.5F, 0.0F, 12.0F, 11.0F, 0.0F, 0.0F, false);

        lleg3 = new AdvancedModelBox(this);
        lleg3.setRotationPoint(1.5F, 2.0F, 2.0F);
        body.addChild(lleg3);
        setRotateAngle(lleg3, 0.0F, -0.7854F, 0.0F);
        lleg3.setTextureOffset(0, 57).addBox(-0.5F, -3.5F, 0.0F, 21.0F, 11.0F, 0.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, head, lantennae, rantennae, carapace, mtail, rtail, ltail, bodyThing2, bodyThing, antenna1Thing, atenna2Thing, cube_r5, rleg, rleg2, rleg3, lleg, lleg2, lleg3);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(GammaroachEntity.ANIMATION_SPRAY);
        animator.startKeyframe(10);
        animator.rotate(body, (float) Math.toRadians(30), 0, 0);
        animator.rotate(carapace, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(lleg, (float) Math.toRadians(-10), (float) Math.toRadians(-30), 0);
        animator.rotate(rleg, (float) Math.toRadians(-10), (float) Math.toRadians(30), 0);
        animator.rotate(lleg2, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(rleg2, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(lleg3, (float) Math.toRadians(-10), (float) Math.toRadians(40), (float) Math.toRadians(-20));
        animator.rotate(rleg3, (float) Math.toRadians(-10), (float) Math.toRadians(-40), (float) Math.toRadians(20));
        animator.move(lleg, 0, -2, 1);
        animator.move(rleg, 0, -2, 1);
        animator.endKeyframe();
        animator.setStaticKeyframe(20);
        animator.resetKeyframe(10);
        animator.endKeyframe();
        animator.setAnimation(GammaroachEntity.ANIMATION_RAM);
        animator.startKeyframe(5);
        animator.move(body, 0, 0, 6);
        animator.rotate(lleg, 0, (float) Math.toRadians(30), 0);
        animator.rotate(rleg, 0, (float) Math.toRadians(-30), 0);
        animator.rotate(lleg2, 0, (float) Math.toRadians(40), 0);
        animator.rotate(rleg2, 0, (float) Math.toRadians(-40), 0);
        animator.rotate(lleg3, 0, (float) Math.toRadians(50), 0);
        animator.rotate(rleg3, 0, (float) Math.toRadians(-50), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(3);
        animator.move(body, 0, 0, -9);
        animator.move(carapace, 0, 1, -2);
        animator.rotate(carapace, (float) Math.toRadians(20), 0, 0);
        animator.rotate(head, (float) Math.toRadians(20), 0, 0);
        animator.rotate(lleg, 0, (float) Math.toRadians(-30), 0);
        animator.rotate(rleg, 0, (float) Math.toRadians(30), 0);
        animator.rotate(lleg2, 0, (float) Math.toRadians(-40), 0);
        animator.rotate(rleg2, 0, (float) Math.toRadians(40), 0);
        animator.rotate(lleg3, 0, (float) Math.toRadians(-20), 0);
        animator.rotate(rleg3, 0, (float) Math.toRadians(20), 0);
        animator.rotate(rantennae, (float) Math.toRadians(-80), 0, 0);
        animator.rotate(lantennae, (float) Math.toRadians(-80), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(10);
    }

    private void setupAnimForAnimation(GammaroachEntity entity, Animation animation, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float partialTick = ageInTicks - entity.tickCount;
        if (animation == GammaroachEntity.ANIMATION_SPRAY) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, animation, partialTick, 10, 30);
            this.swing(body, 1F, 0.2F, false, 0F, 0F, ageInTicks, animationIntensity);
            this.walk(body, 1F, 0.2F, false, 1F, 0F, ageInTicks, animationIntensity);
            this.swing(lleg, 1F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.walk(lleg, 1F, 0.2F, true, 1F, 0F, ageInTicks, animationIntensity);
            this.swing(rleg, 1F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.walk(rleg, 1F, 0.2F, true, 1F, 0F, ageInTicks, animationIntensity);
            this.swing(lleg2, 1F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.walk(lleg2, 1F, 0.2F, true, 1F, 0F, ageInTicks, animationIntensity);
            this.swing(rleg2, 1F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.walk(rleg2, 1F, 0.2F, true, 1F, 0F, ageInTicks, animationIntensity);
            this.swing(lleg3, 1F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.walk(lleg3, 1F, 0.2F, true, 1F, 0F, ageInTicks, animationIntensity);
            this.swing(rleg3, 1F, 0.2F, true, 0F, 0F, ageInTicks, animationIntensity);
            this.walk(rleg3, 1F, 0.2F, true, 1F, 0F, ageInTicks, animationIntensity);
        }
    }

    @Override
    public void setupAnim(GammaroachEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float walkSpeed = 1F;
        float walkDegree = 0.9F;
        float partialTicks = ageInTicks - entity.tickCount;
        float deathAmount = (entity.deathTime + partialTicks) / 20F;
        progressRotationPrev(rantennae, limbSwingAmount, (float) Math.toRadians(-80), 0, (float) Math.toRadians(20), 1F);
        progressRotationPrev(lantennae, limbSwingAmount, (float) Math.toRadians(-80), 0, (float) Math.toRadians(-20), 1F);
        if (entity.deathTime > 0.0) {
            limbSwing = ageInTicks;
            limbSwingAmount = 1;
        }
        progressPositionPrev(body, deathAmount * deathAmount, 0, 16, 0, 1F);
        this.walk(head, 0.1F, 0.1F, true, 0F, -0.1F, ageInTicks, 1);
        this.walk(rantennae, 0.1F, 0.15F, true, 1F, -0.3F, ageInTicks, 1);
        this.walk(lantennae, 0.1F, 0.15F, true, 1F, -0.3F, ageInTicks, 1);
        this.walk(mtail, 0.1F, 0.05F, true, 2F, -0.05F, ageInTicks, 1);
        this.swing(rtail, 0.1F, 0.05F, true, 3F, -0.05F, ageInTicks, 1);
        this.swing(ltail, 0.1F, 0.05F, false, 3F, -0.05F, ageInTicks, 1);
        float offset = 0F;
        float offsetleft = 2F;
        float offsetUp = 0.3F;

        this.swing(rleg, walkSpeed, walkDegree * 1.2F, false, offset, -0.3F, limbSwing, limbSwingAmount);
        this.flap(rleg, walkSpeed, walkDegree * 0.3F, false, offset - 1.5F, offsetUp, limbSwing, limbSwingAmount);
        this.swing(lleg, walkSpeed, -walkDegree * 1.2F, false, offset + offsetleft, 0.3F, limbSwing, limbSwingAmount);
        this.flap(lleg, walkSpeed, walkDegree * 0.3F, false, offset + offsetleft + 1.5F, -offsetUp, limbSwing, limbSwingAmount);
        offset += 1.5F;
        this.swing(rleg2, walkSpeed, -walkDegree, false, offset + offsetleft, 0, limbSwing, limbSwingAmount);
        this.flap(rleg2, walkSpeed, walkDegree * 0.3F, false, offset + offsetleft - 1.5F, offsetUp, limbSwing, limbSwingAmount);
        this.swing(lleg2, walkSpeed, walkDegree, false, offset, 0F, limbSwing, limbSwingAmount);
        this.flap(lleg2, walkSpeed, walkDegree * 0.3F, false, offset - 1.5F, -offsetUp, limbSwing, limbSwingAmount);
        offset += 1.5F;
        this.swing(rleg3, walkSpeed, walkDegree * 0.5F, false, offset, -0.1F, limbSwing, limbSwingAmount);
        this.flap(rleg3, walkSpeed, walkDegree * 0.3F, false, offset - 1.5F, offsetUp, limbSwing, limbSwingAmount);
        this.swing(lleg3, walkSpeed, -walkDegree * 0.5F, false, offset + offsetleft, 0.1F, limbSwing, limbSwingAmount);
        this.flap(lleg3, walkSpeed, walkDegree * 0.3F, false, offset + offsetleft - 1.5F, -offsetUp, limbSwing, limbSwingAmount);

        this.swing(body, walkSpeed, walkDegree * 0.2F, false, 3, 0, limbSwing, limbSwingAmount);
        this.bob(body, walkSpeed, walkDegree * -3, true, limbSwing, limbSwingAmount);
        if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            setupAnimForAnimation(entity, entity.getAnimation(), limbSwing, limbSwingAmount, ageInTicks);
        }
    }
}