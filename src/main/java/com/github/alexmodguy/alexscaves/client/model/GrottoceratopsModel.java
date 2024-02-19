package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

public class GrottoceratopsModel extends AdvancedEntityModel<GrottoceratopsEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox bodySpikes;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox rfoot;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox lfoot;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tailSpike;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox tail2Spike;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox grassBunch;
    private final AdvancedModelBox grass;
    private final AdvancedModelBox grass2;
    private final AdvancedModelBox grassBunch2;
    private final AdvancedModelBox grass3;
    private final AdvancedModelBox grass4;
    private final AdvancedModelBox grass5;
    private final AdvancedModelBox grass6;

    private final ModelAnimator animator;

    public GrottoceratopsModel() {
        texWidth = 256;
        texHeight = 256;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -3.0F, 0.0F);
        body.setTextureOffset(0, 0).addBox(-11.0F, -14.0F, -17.5F, 22.0F, 30.0F, 35.0F, 0.0F, false);

        bodySpikes = new AdvancedModelBox(this);
        bodySpikes.setRotationPoint(0.0F, -2.5F, 0.0F);
        body.addChild(bodySpikes);
        bodySpikes.setTextureOffset(0, 22).addBox(0.0F, -15.5F, -21.5F, 0.0F, 31.0F, 43.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-11.0F, 5.0F, 9.5F);
        body.addChild(rleg);
        rleg.setTextureOffset(117, 17).addBox(-5.0F, -2.0F, -8.0F, 9.0F, 15.0F, 12.0F, 0.0F, true);

        rleg2 = new AdvancedModelBox(this);
        rleg2.setRotationPoint(-0.5F, 8.5F, 2.0F);
        rleg.addChild(rleg2);
        rleg2.setTextureOffset(126, 44).addBox(-3.0F, -1.5F, -4.0F, 6.0F, 13.0F, 8.0F, 0.0F, true);

        rfoot = new AdvancedModelBox(this);
        rfoot.setRotationPoint(0.0F, 11.5F, -0.5F);
        rleg2.addChild(rfoot);
        rfoot.setTextureOffset(114, 0).addBox(-4.0F, 0.0F, -6.5F, 8.0F, 2.0F, 11.0F, 0.0F, true);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(11.0F, 5.0F, 9.5F);
        body.addChild(lleg);
        lleg.setTextureOffset(117, 17).addBox(-4.0F, -2.0F, -8.0F, 9.0F, 15.0F, 12.0F, 0.0F, false);

        lleg2 = new AdvancedModelBox(this);
        lleg2.setRotationPoint(0.5F, 8.5F, 2.0F);
        lleg.addChild(lleg2);
        lleg2.setTextureOffset(126, 44).addBox(-3.0F, -1.5F, -4.0F, 6.0F, 13.0F, 8.0F, 0.0F, false);

        lfoot = new AdvancedModelBox(this);
        lfoot.setRotationPoint(0.5F, 20.0F, 1.5F);
        lleg.addChild(lfoot);
        lfoot.setTextureOffset(114, 0).addBox(-4.0F, 0.0F, -6.5F, 8.0F, 2.0F, 11.0F, 0.0F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-10.0F, 4.0F, -12.0F);
        body.addChild(rarm);
        rarm.setTextureOffset(0, 0).addBox(-4.0F, -2.0F, -4.5F, 6.0F, 25.0F, 9.0F, 0.0F, true);
        rarm.setTextureOffset(79, 7).addBox(-1.0F, 17.0F, -7.5F, 3.0F, 3.0F, 3.0F, 0.0F, true);
        rarm.setTextureOffset(105, 31).addBox(-1.0F, 20.0F, -7.5F, 3.0F, 2.0F, 2.0F, 0.0F, true);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(10.0F, 4.0F, -12.0F);
        body.addChild(larm);
        larm.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, -4.5F, 6.0F, 25.0F, 9.0F, 0.0F, false);
        larm.setTextureOffset(79, 7).addBox(-2.0F, 17.0F, -7.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        larm.setTextureOffset(105, 31).addBox(-2.0F, 20.0F, -7.5F, 3.0F, 2.0F, 2.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -1.0F, 16.5F);
        body.addChild(tail);
        tail.setTextureOffset(65, 75).addBox(-6.0F, -5.0F, -1.0F, 12.0F, 12.0F, 21.0F, 0.0F, false);

        tailSpike = new AdvancedModelBox(this);
        tailSpike.setRotationPoint(0.0F, -5.0F, 10.5F);
        tail.addChild(tailSpike);
        tailSpike.setTextureOffset(0, 98).addBox(0.0F, -4.0F, -9.5F, 0.0F, 12.0F, 19.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setRotationPoint(0.0F, 0.0F, 19.0F);
        tail.addChild(tail2);
        tail2.setTextureOffset(26, 129).addBox(-10.0F, -3.0F, 14.0F, 7.0F, 3.0F, 3.0F, 0.0F, true);
        tail2.setTextureOffset(95, 46).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 7.0F, 19.0F, 0.0F, false);
        tail2.setTextureOffset(26, 129).addBox(3.0F, -3.0F, 8.0F, 7.0F, 3.0F, 3.0F, 0.0F, false);
        tail2.setTextureOffset(26, 129).addBox(3.0F, -3.0F, 14.0F, 7.0F, 3.0F, 3.0F, 0.0F, false);
        tail2.setTextureOffset(26, 129).addBox(-10.0F, -3.0F, 8.0F, 7.0F, 3.0F, 3.0F, 0.0F, true);

        tail2Spike = new AdvancedModelBox(this);
        tail2Spike.setRotationPoint(0.0F, -3.0F, 9.5F);
        tail2.addChild(tail2Spike);
        tail2Spike.setTextureOffset(131, 83).addBox(0.0F, -4.0F, -8.5F, 0.0F, 8.0F, 17.0F, 0.0F, false);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(0.0F, 5.0F, -16.5F);
        body.addChild(neck);
        neck.setTextureOffset(100, 129).addBox(-5.0F, -7.0F, -16.0F, 10.0F, 14.0F, 20.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -6.0F, -14.0F);
        neck.addChild(head);
        head.setTextureOffset(110, 72).addBox(-8.0F, -17.0F, -17.0F, 16.0F, 12.0F, 8.0F, 0.0F, false);
        head.setTextureOffset(79, 29).addBox(-4.0F, 10.0F, -17.0F, 8.0F, 2.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(46, 108).addBox(-7.0F, -5.0F, -7.0F, 14.0F, 15.0F, 10.0F, 0.0F, false);
        head.setTextureOffset(94, 108).addBox(-11.0F, -17.0F, 0.0F, 22.0F, 18.0F, 3.0F, 0.0F, false);
        head.setTextureOffset(0, 96).addBox(-14.0F, -20.0F, 0.0F, 28.0F, 21.0F, 0.0F, 0.0F, false);
        head.setTextureOffset(79, 0).addBox(-7.0F, -9.0F, -7.0F, 3.0F, 4.0F, 3.0F, 0.0F, false);
        head.setTextureOffset(21, 0).addBox(4.0F, -9.0F, -7.0F, 3.0F, 4.0F, 3.0F, 0.0F, false);
        head.setTextureOffset(0, 129).addBox(-4.0F, -5.0F, -17.0F, 8.0F, 7.0F, 10.0F, 0.0F, false);
        head.setTextureOffset(36, 142).addBox(-4.0F, 2.0F, -17.0F, 8.0F, 8.0F, 4.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 2.0F, -6.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(65, 140).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 8.0F, 6.0F, 0.0F, false);

        grassBunch = new AdvancedModelBox(this);
        grassBunch.setRotationPoint(2.0F, 0.0F, -3.5F);
        jaw.addChild(grassBunch);


        grass = new AdvancedModelBox(this);
        grass.setRotationPoint(0.0F, 0.0F, 0.0F);
        grassBunch.addChild(grass);


        grass5 = new AdvancedModelBox(this);
        grass5.setRotationPoint(2.4246F, -0.4275F, 0.0F);
        grass.addChild(grass5);
        setRotateAngle(grass5, 0.0F, 0.0F, -0.3491F);
        grass5.setTextureOffset(24, 165).addBox(-2.4246F, -0.4275F, -2.5F, 5.0F, 0.0F, 5.0F, 0.0F, false);

        grass2 = new AdvancedModelBox(this);
        grass2.setRotationPoint(0.0F, 0.0F, 0.0F);
        grassBunch.addChild(grass2);
        grass2.setTextureOffset(1, 165).addBox(0.0F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, 0.0F, false);

        grassBunch2 = new AdvancedModelBox(this);
        grassBunch2.setRotationPoint(-2.0F, 0.0F, -3.5F);
        jaw.addChild(grassBunch2);


        grass3 = new AdvancedModelBox(this);
        grass3.setRotationPoint(0.0F, 0.0F, 0.0F);
        grassBunch2.addChild(grass3);

        grass6 = new AdvancedModelBox(this);
        grass6.setRotationPoint(-2.4246F, -0.4275F, 0.0F);
        grass3.addChild(grass6);
        setRotateAngle(grass6, 0.0F, 0.0F, 0.3491F);
        grass6.setTextureOffset(24, 165).addBox(-2.5754F, -0.4275F, -2.5F, 5.0F, 0.0F, 5.0F, 0.0F, true);

        grass4 = new AdvancedModelBox(this);
        grass4.setRotationPoint(0.0F, 0.0F, 0.0F);
        grassBunch2.addChild(grass4);
        grass4.setTextureOffset(1, 165).addBox(-5.0F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, bodySpikes, tail, tail2, tailSpike, tail2Spike, neck, head, jaw, larm, rarm, lleg, lleg2, lfoot, rleg, rleg2, rfoot, grassBunch, grassBunch2, grass, grass2, grass3, grass4, grass5, grass6);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(GrottoceratopsEntity.ANIMATION_SPEAK_1);
        animator.startKeyframe(5);
        animator.move(jaw, 0, 1, -1);
        animator.rotate(jaw, (float) Math.toRadians(30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(10), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(-10), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);
        animator.setAnimation(GrottoceratopsEntity.ANIMATION_SPEAK_2);
        animator.startKeyframe(5);
        animator.move(jaw, 0, 1, -0.5F);
        animator.move(neck, 0, 0, -4);
        animator.move(head, 0, 2, -2);
        animator.rotate(jaw, (float) Math.toRadians(30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-20), (float) Math.toRadians(-20), 0);
        animator.rotate(neck, (float) Math.toRadians(-20), (float) Math.toRadians(-10), 0);
        animator.endKeyframe();
        animator.startKeyframe(10);
        animator.move(jaw, 0, 1, -0.5F);
        animator.move(neck, 0, 0, -4);
        animator.move(head, 0, 2, -2);
        animator.rotate(jaw, (float) Math.toRadians(30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-20), (float) Math.toRadians(20), 0);
        animator.rotate(neck, (float) Math.toRadians(-20), (float) Math.toRadians(10), 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(GrottoceratopsEntity.ANIMATION_CHEW_FROM_GROUND);
        animator.startKeyframe(5);
        animator.move(neck, 0, 0, -4);
        animator.move(head, 0, 3, -2);
        animator.rotate(jaw, (float) Math.toRadians(30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(20), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(neck, 0, 0, -1);
        animator.move(head, 0, 3, -2);
        animator.rotate(head, (float) Math.toRadians(15), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(neck, 0, 0, -4);
        animator.move(head, 0, 3, -2);
        animator.rotate(jaw, (float) Math.toRadians(30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(20), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(20), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(GrottoceratopsEntity.ANIMATION_MELEE_RAM);
        animator.startKeyframe(5);
        animator.move(neck, 0, 0, 2);
        animator.move(head, 0, 3, -2);
        animator.rotate(head, (float) Math.toRadians(20), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(2);
        animator.move(neck, 0, 0, -4);
        animator.move(head, 0, 3, -2);
        animator.rotate(head, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(-25), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(5);
        animator.setAnimation(GrottoceratopsEntity.ANIMATION_MELEE_TAIL_1);
        animator.startKeyframe(5);
        animator.rotate(neck, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(tail, (float) Math.toRadians(20), (float) Math.toRadians(20), 0);
        animator.rotate(tail2, (float) Math.toRadians(20), (float) Math.toRadians(20), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(5);
        animator.rotate(neck, 0, (float) Math.toRadians(10), 0);
        animator.rotate(tail, (float) Math.toRadians(-20), (float) Math.toRadians(-50), 0);
        animator.rotate(tail2, (float) Math.toRadians(-20), (float) Math.toRadians(-20), (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(5);
        animator.setAnimation(GrottoceratopsEntity.ANIMATION_MELEE_TAIL_2);
        animator.startKeyframe(5);
        animator.rotate(neck, 0, (float) Math.toRadians(10), 0);
        animator.rotate(tail, (float) Math.toRadians(20), (float) Math.toRadians(-20), 0);
        animator.rotate(tail2, (float) Math.toRadians(20), (float) Math.toRadians(-20), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(5);
        animator.rotate(neck, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(tail, (float) Math.toRadians(-20), (float) Math.toRadians(50), 0);
        animator.rotate(tail2, (float) Math.toRadians(-20), (float) Math.toRadians(20), (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(5);
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);

            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }

    private void setupAnimForAnimation(GrottoceratopsEntity entity, Animation animation, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float partialTick = ageInTicks - entity.tickCount;
        boolean chewing = animation == GrottoceratopsEntity.ANIMATION_CHEW_FROM_GROUND || animation == GrottoceratopsEntity.ANIMATION_CHEW;
        if (chewing) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, animation, partialTick, animation == GrottoceratopsEntity.ANIMATION_CHEW_FROM_GROUND ? 15 : 0);
            float jawDown = Math.min(0, ACMath.walkValue(ageInTicks, animationIntensity, 0.4F, 2F, 1F, true));
            this.head.rotateAngleX += ACMath.walkValue(ageInTicks, animationIntensity, 0.4F, 2F, 0.05F, false);
            this.jaw.rotationPointX += ACMath.walkValue(ageInTicks, animationIntensity, 0.4F, 0.5F, 2F, true);
            this.jaw.rotationPointY -= jawDown;
            this.grassBunch.rotationPointY -= jawDown * 0.5F;
            this.grassBunch2.rotationPointY -= jawDown * 0.5F;
            this.grassBunch.rotateAngleZ -= ACMath.walkValue(ageInTicks, animationIntensity, 0.4F, 2F, 0.3F, true);
            this.grassBunch2.rotateAngleZ -= ACMath.walkValue(ageInTicks, animationIntensity, 0.4F, 2F, 0.3F, false);
        }
    }

    @Override
    public void setupAnim(GrottoceratopsEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        float walkSpeed = 0.5F;
        float walkDegree = 1F;
        float partialTick = ageInTicks - entity.tickCount;
        float buryEggsAmount = entity.getBuryEggsProgress(partialTick);
        float stillAmount = 1 - limbSwingAmount;
        float danceAmount = entity.getDanceProgress(partialTick);
        float danceSpeed = 0.5F;
        boolean showGrass = entity.getAnimation() == GrottoceratopsEntity.ANIMATION_CHEW_FROM_GROUND && entity.getAnimationTick() > 15 || entity.getAnimation() == GrottoceratopsEntity.ANIMATION_CHEW;
        if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            setupAnimForAnimation(entity, entity.getAnimation(), limbSwing, limbSwingAmount, ageInTicks);
        }
        float tailSwingYaw = Mth.wrapDegrees(entity.getTailSwingRot(partialTick)) / 57.295776F;
        this.body.rotateAngleY += tailSwingYaw;
        this.grassBunch.showModel = showGrass;
        this.grassBunch2.showModel = showGrass;
        if (buryEggsAmount > 0.0F) {
            limbSwing = ageInTicks;
            limbSwingAmount = buryEggsAmount * 0.5F;
            this.body.swing(0.25F, 0.4F, false, 0F, 0F, ageInTicks, buryEggsAmount);
            this.neck.swing(0.25F, 0.4F, true, -1F, 0F, ageInTicks, buryEggsAmount);
        }
        progressRotationPrev(tail, stillAmount, (float) Math.toRadians(-10), 0, 0, 1F);
        progressPositionPrev(neck, 1F, 0, 0, 5, 1F);
        this.walk(tail, 0.1F, 0.1F, false, 0F, 0F, ageInTicks, stillAmount);
        this.swing(tail, 0.1F, 0.2F, false, 2F, 0F, ageInTicks, stillAmount);
        this.swing(tail2, 0.1F, 0.1F, false, 1F, 0F, ageInTicks, stillAmount);
        this.walk(neck, 0.1F, 0.03F, false, 2F, 0F, ageInTicks, stillAmount);
        this.walk(head, 0.1F, 0.03F, true, 1F, 0F, ageInTicks, stillAmount);

        this.flap(body, walkSpeed, walkDegree * 0.1F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(lleg, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(rleg, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(larm, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(rarm, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(neck, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.swing(tail, walkSpeed, walkDegree * 0.4F, false, -1F, 0F, limbSwing, limbSwingAmount);
        this.swing(tail2, walkSpeed, walkDegree * 0.2F, false, -1F, 0F, limbSwing, limbSwingAmount);
        articulateLegs(entity.legSolver, partialTick);
        float bodyBob = ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed * 1.5F, 0.5F, 2.4F, true);
        this.body.rotationPointY += bodyBob;
        this.walk(larm, walkSpeed, walkDegree * 0.4F, true, 0F, 0F, limbSwing, limbSwingAmount);
        larm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 5F, false)) - bodyBob;
        larm.rotationPointZ += ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 1F, false);
        this.walk(rarm, walkSpeed, walkDegree * 0.4F, false, 0F, 0F, limbSwing, limbSwingAmount);
        rarm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 5F, true)) - bodyBob;
        rarm.rotationPointZ += ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 1F, true);
        this.walk(lleg, walkSpeed, walkDegree * 0.3F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(lfoot, walkSpeed, walkDegree * 0.2F, false, 3F, 0F, limbSwing, limbSwingAmount);
        lleg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 5F, true)) - bodyBob;
        lleg.rotationPointZ += ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 1F, true);
        this.walk(rleg, walkSpeed, walkDegree * 0.3F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(rfoot, walkSpeed, walkDegree * 0.2F, true, 3F, 0F, limbSwing, limbSwingAmount);
        rleg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 5F, false)) - bodyBob;
        rleg.rotationPointZ += ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 1F, false);
        if (entity.getAnimation() != GrottoceratopsEntity.ANIMATION_MELEE_TAIL_1 && entity.getAnimation() != GrottoceratopsEntity.ANIMATION_MELEE_TAIL_2) {
            float yawAmount = netHeadYaw / 57.295776F;
            float pitchAmount = headPitch / 57.295776F;
            this.neck.rotateAngleX += pitchAmount * 0.1F;
            this.head.rotateAngleX += pitchAmount * 0.2F;
            this.neck.rotateAngleY += yawAmount * 0.3F;
            this.head.rotateAngleY += yawAmount * 0.4F;
        }
        neck.rotationPointY += ACMath.walkValue(ageInTicks, danceAmount, danceSpeed * 1.5F, 1F, 1.5F, false);
        neck.rotationPointX += ACMath.walkValue(ageInTicks, danceAmount, danceSpeed * 1.5F, 0F, 3F, false);
        this.swing(body, danceSpeed, 0.1F, false, 1, 0, ageInTicks, danceAmount);
        this.swing(tail, danceSpeed, 0.5F, false, 1, 0, ageInTicks, danceAmount);
        this.swing(tail2, danceSpeed, 0.5F, false, 1, 0, ageInTicks, danceAmount);

    }

    private void articulateLegs(LegSolverQuadruped legs, float partialTick) {
        float heightBackLeft = legs.backLeft.getHeight(partialTick);
        float heightBackRight = legs.backRight.getHeight(partialTick);
        float heightFrontLeft = legs.frontLeft.getHeight(partialTick);
        float heightFrontRight = legs.frontRight.getHeight(partialTick);
        float max = Math.max(Math.max(heightBackLeft, heightBackRight), Math.max(heightFrontLeft, heightFrontRight)) * 0.8F;
        body.rotationPointY += max * 16;
        rarm.rotationPointY += (heightFrontRight - max) * 16;
        larm.rotationPointY += (heightFrontLeft - max) * 16;
        rleg.rotationPointY += (heightBackRight - max) * 16;
        lleg.rotationPointY += (heightBackLeft - max) * 16;

    }

    public void animateSpirit(DinosaurSpiritEntity entityIn, float partialTicks) {
        this.resetToDefaultPose();
    }

    public void renderSpiritToBuffer(PoseStack poseStack, VertexConsumer ivertexbuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        poseStack.translate(0, 1.3F, 1);
        head.render(poseStack, ivertexbuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        poseStack.popPose();
    }
}