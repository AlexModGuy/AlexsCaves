package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.client.model.misc.HideableModelBoxWithChildren;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.TremorzillaLegSolver;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class TremorzillaModel extends AdvancedEntityModel<TremorzillaEntity> {
    private final AdvancedModelBox root;
    private final HideableModelBoxWithChildren torso;
    private final AdvancedModelBox torsoSpikes;
    private final AdvancedModelBox torsoSpike1;
    private final AdvancedModelBox torsoSpike2;
    private final AdvancedModelBox torsoSpike3;
    private final AdvancedModelBox rightLeg;
    private final AdvancedModelBox leftLeg;
    private final HideableModelBoxWithChildren tail1;
    private final HideableModelBoxWithChildren tail1Spikes;
    private final HideableModelBoxWithChildren tail1Spike1;
    private final HideableModelBoxWithChildren tail1Spike2;
    private final HideableModelBoxWithChildren tail2;
    private final HideableModelBoxWithChildren tail2Spikes;
    private final HideableModelBoxWithChildren tail3;
    private final HideableModelBoxWithChildren tail3Spikes;
    private final AdvancedModelBox tail4;
    private final AdvancedModelBox chest;
    private final AdvancedModelBox cube_r6;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox neckSpikes;
    private final AdvancedModelBox head;
    private final AdvancedModelBox rightEar;
    private final AdvancedModelBox leftEar;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox leftThumb;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox rightThumb;
    private final AdvancedModelBox chestSpikes;
    private final AdvancedModelBox chestSpike1;
    private final AdvancedModelBox chestSpike2;
    private final AdvancedModelBox chestSpike3;
    private final AdvancedModelBox neckSlope;
    private final ModelAnimator animator;
    public boolean straighten;

    public TremorzillaModel() {
        texWidth = 512;
        texHeight = 512;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        torso = new HideableModelBoxWithChildren(this);
        torso.setRotationPoint(0.0F, -51.0F, 0.0F);
        root.addChild(torso);
        torso.setTextureOffset(0, 0).addBox(-26.0F, -31.0F, -28.0F, 52.0F, 62.0F, 56.0F, 0.0F, false);

        torsoSpikes = new AdvancedModelBox(this);
        torsoSpikes.setRotationPoint(0.0F, -28.0671F, 27.4673F);
        torso.addChild(torsoSpikes);


        torsoSpike1 = new AdvancedModelBox(this);
        torsoSpike1.setRotationPoint(0.0F, -15.9329F, -24.4673F);
        torsoSpikes.addChild(torsoSpike1);
        setRotateAngle(torsoSpike1, 0.2618F, 0.0F, 0.0F);
        torsoSpike1.setTextureOffset(284, 262).addBox(0.02F, -5.0F, 9.0F, 0.0F, 54.0F, 54.0F, 0.0F, false);

        torsoSpike2 = new AdvancedModelBox(this);
        torsoSpike2.setRotationPoint(0.0F, -15.9329F, -24.4673F);
        torsoSpikes.addChild(torsoSpike2);
        setRotateAngle(torsoSpike2, 0.3927F, -0.3054F, -0.3054F);
        torsoSpike2.setTextureOffset(128, 290).addBox(-10.0F, -5.0F, 9.0F, 0.0F, 54.0F, 54.0F, 0.0F, true);

        torsoSpike3 = new AdvancedModelBox(this);
        torsoSpike3.setRotationPoint(0.0F, -15.9329F, -24.4673F);
        torsoSpikes.addChild(torsoSpike3);
        setRotateAngle(torsoSpike3, 0.3927F, 0.3054F, 0.3054F);
        torsoSpike3.setTextureOffset(128, 290).addBox(10.0F, -5.0F, 9.0F, 0.0F, 54.0F, 54.0F, 0.0F, false);

        rightLeg = new AdvancedModelBox(this);
        rightLeg.setRotationPoint(-24.0F, -2.0F, -3.0F);
        torso.addChild(rightLeg);
        rightLeg.setTextureOffset(160, 24).addBox(-20.5F, 44.5F, -24.0F, 27.0F, 8.0F, 8.0F, 0.0F, true);
        rightLeg.setTextureOffset(139, 148).addBox(-20.5F, 44.5F, -24.5F, 27.0F, 8.0F, 8.0F, 0.5F, true);
        rightLeg.setTextureOffset(1, 344).addBox(-21.0F, -11.0F, -16.0F, 28.0F, 64.0F, 34.0F, 0.0F, true);

        leftLeg = new AdvancedModelBox(this);
        leftLeg.setRotationPoint(24.0F, -2.0F, -3.0F);
        torso.addChild(leftLeg);
        leftLeg.setTextureOffset(160, 24).addBox(-6.5F, 44.5F, -24.0F, 27.0F, 8.0F, 8.0F, 0.0F, false);
        leftLeg.setTextureOffset(139, 148).addBox(-6.5F, 44.5F, -24.5F, 27.0F, 8.0F, 8.0F, 0.5F, false);
        leftLeg.setTextureOffset(1, 344).addBox(-7.0F, -11.0F, -16.0F, 28.0F, 64.0F, 34.0F, 0.0F, false);

        tail1 = new HideableModelBoxWithChildren(this);
        tail1.setRotationPoint(-0.5F, 24.0F, 16.0F);
        torso.addChild(tail1);
        tail1.setTextureOffset(124, 164).addBox(-14.0F, -15.0F, -6.0F, 29.0F, 34.0F, 64.0F, 0.0F, false);

        tail1Spikes = new HideableModelBoxWithChildren(this);
        tail1Spikes.setRotationPoint(0.5F, -15.3829F, 26.0F);
        tail1.addChild(tail1Spikes);
        tail1Spikes.setTextureOffset(156, 238).addBox(0.0F, -39.6171F, -32.0F, 0.0F, 40.0F, 64.0F, 0.0F, false);

        tail1Spike1 = new HideableModelBoxWithChildren(this);
        tail1Spike1.setRotationPoint(6.0F, 0.3829F, 0.0F);
        tail1Spikes.addChild(tail1Spike1);
        setRotateAngle(tail1Spike1, 0.0F, 0.0F, 0.3054F);
        tail1Spike1.setTextureOffset(156, 198).addBox(0.0F, -40.0F, -32.0F, 0.0F, 40.0F, 64.0F, 0.0F, false);

        tail1Spike2 = new HideableModelBoxWithChildren(this);
        tail1Spike2.setRotationPoint(-6.0F, 0.3829F, 0.0F);
        tail1Spikes.addChild(tail1Spike2);
        setRotateAngle(tail1Spike2, 0.0F, 0.0F, -0.3054F);
        tail1Spike2.setTextureOffset(156, 198).addBox(0.0F, -40.0F, -32.0F, 0.0F, 40.0F, 64.0F, 0.0F, true);

        tail2 = new HideableModelBoxWithChildren(this);
        tail2.setRotationPoint(0.5F, -9.0F, 54.0F);
        tail1.addChild(tail2);
        tail2.setTextureOffset(140, 42).addBox(-9.0F, -4.01F, -7.99F, 18.0F, 28.0F, 76.0F, -0.01F, false);

        tail2Spikes = new HideableModelBoxWithChildren(this);
        tail2Spikes.setRotationPoint(-0.01F, -4.01F, 36.01F);
        tail2.addChild(tail2Spikes);
        tail2Spikes.setTextureOffset(0, 252).addBox(0.0F, -28.0F, -32.0F, 0.0F, 28.0F, 64.0F, -0.01F, false);

        tail3 = new HideableModelBoxWithChildren(this);
        tail3.setRotationPoint(0.0F, 10.0F, 66.0F);
        tail2.addChild(tail3);
        tail3.setTextureOffset(0, 228).addBox(-5.0F, -8.0F, -2.0F, 10.0F, 20.0F, 68.0F, 0.0F, false);

        tail3Spikes = new HideableModelBoxWithChildren(this);
        tail3Spikes.setRotationPoint(0.0F, -8.0F, 29.0F);
        tail3.addChild(tail3Spikes);
        tail3Spikes.setTextureOffset(261, 38).addBox(0.0F, -26.0F, -27.0F, 0.0F, 26.0F, 54.0F, 0.0F, true);

        tail4 = new AdvancedModelBox(this);
        tail4.setRotationPoint(0.0F, 2.0F, 66.0F);
        tail3.addChild(tail4);
        tail4.setTextureOffset(248, 146).addBox(-3.0F, -6.0F, -4.0F, 6.0F, 12.0F, 68.0F, 0.0F, false);

        chest = new AdvancedModelBox(this);
        chest.setRotationPoint(0.0F, -29.565F, 17.1508F);
        torso.addChild(chest);


        cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(0.0F, -14.6696F, -14.0691F);
        chest.addChild(cube_r6);
        setRotateAngle(cube_r6, 0.3927F, 0.0F, 0.0F);
        cube_r6.setTextureOffset(0, 118).addBox(-20.0F, -29.0F, -43.0F, 40.0F, 56.0F, 54.0F, 0.0F, false);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(1.5556F, -36.6696F, -20.1803F);
        chest.addChild(neck);
        neck.setTextureOffset(252, 0).addBox(-14.5556F, -50.0F, -33.8889F, 26.0F, 54.0F, 38.0F, 0.0F, false);

        neckSpikes = new AdvancedModelBox(this);
        neckSpikes.setRotationPoint(-1.5556F, -38.0F, -0.8889F);
        neck.addChild(neckSpikes);
        neckSpikes.setTextureOffset(0, 200).addBox(-0.02F, -22.0F, -7.0F, 0.0F, 48.0F, 28.0F, 0.0F, false);
        neckSpikes.setTextureOffset(248, 118).addBox(-7.0F, -20.0F, -9.0F, 0.0F, 48.0F, 28.0F, 0.0F, true);
        neckSpikes.setTextureOffset(248, 118).addBox(7.0F, -20.0F, -9.0F, 0.0F, 48.0F, 28.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(-1.5556F, -49.0F, -20.8889F);
        neck.addChild(head);
        head.setTextureOffset(335, 124).addBox(-13.5F, -4.0F, -36.0F, 27.0F, 14.0F, 36.0F, 0.0F, false);
        head.setTextureOffset(11, 443).addBox(-13.5F, 10.0F, -36.0F, 27.0F, 5.0F, 31.0F, 0.0F, false);
        head.setTextureOffset(4, 122).addBox(4.5F, -13.0F, -13.0F, 9.0F, 5.0F, 8.0F, 0.0F, false);
        head.setTextureOffset(4, 142).addBox(4.5F, -8.0F, -13.0F, 9.0F, 4.0F, 8.0F, 0.0F, false);
        head.setTextureOffset(4, 122).addBox(-13.5F, -13.0F, -13.0F, 9.0F, 5.0F, 8.0F, 0.0F, true);
        head.setTextureOffset(430, 52).addBox(-13.5F, 4.0F, -14.0F, 27.0F, 6.0F, 14.0F, 0.0F, false);
        head.setTextureOffset(4, 142).addBox(-13.5F, -8.0F, -13.0F, 9.0F, 4.0F, 8.0F, 0.0F, true);
        head.setTextureOffset(32, 14).addBox(-13.5F, -7.0F, 0.0F, 0.0F, 6.0F, 8.0F, 0.0F, true);

        rightEar = new AdvancedModelBox(this);
        rightEar.setRotationPoint(-13.5F, -4.0F, 0.0F);
        head.addChild(rightEar);


        leftEar = new AdvancedModelBox(this);
        leftEar.setRotationPoint(13.5F, -4.0F, 0.0F);
        head.addChild(leftEar);
        leftEar.setTextureOffset(32, 14).addBox(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 8.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 5.2738F, -2.0631F);
        head.addChild(jaw);
        jaw.setTextureOffset(383, 3).addBox(-15.0F, -1.2738F, -11.9369F, 30.0F, 20.0F, 15.0F, 0.0F, false);
        jaw.setTextureOffset(368, 346).addBox(-15.0F, -9.2738F, -35.9369F, 30.0F, 14.0F, 24.0F, 0.0F, false);
        jaw.setTextureOffset(372, 202).addBox(-15.0F, 4.7262F, -35.9369F, 30.0F, 14.0F, 24.0F, 0.0F, false);

        leftArm = new AdvancedModelBox(this);
        leftArm.setRotationPoint(17.5F, -19.6696F, -33.0691F);
        chest.addChild(leftArm);
        setRotateAngle(leftArm, 0.3927F, -0.3927F, 0.3927F);
        leftArm.setTextureOffset(312, 226).addBox(16.5F, -6.0F, -28.0F, 18.0F, 12.0F, 20.0F, 0.0F, false);
        leftArm.setTextureOffset(0, 0).addBox(16.5F, -6.0F, -38.0F, 18.0F, 12.0F, 10.0F, 0.0F, false);
        leftArm.setTextureOffset(160, 0).addBox(2.5F, -6.0F, -8.0F, 32.0F, 12.0F, 12.0F, 0.0F, false);

        leftThumb = new AdvancedModelBox(this);
        leftThumb.setRotationPoint(16.5F, 4.0F, -22.0F);
        leftArm.addChild(leftThumb);
        leftThumb.setTextureOffset(10, 34).addBox(-6.0F, -2.0F, -6.0F, 6.0F, 4.0F, 6.0F, 0.0F, false);
        leftThumb.setTextureOffset(0, 46).addBox(-6.0F, -2.0F, -12.0F, 6.0F, 4.0F, 6.0F, 0.0F, false);

        rightArm = new AdvancedModelBox(this);
        rightArm.setRotationPoint(-17.5F, -19.6696F, -33.0691F);
        chest.addChild(rightArm);
        setRotateAngle(rightArm, 0.3927F, 0.3927F, -0.3927F);
        rightArm.setTextureOffset(312, 226).addBox(-34.5F, -6.0F, -28.0F, 18.0F, 12.0F, 20.0F, 0.0F, true);
        rightArm.setTextureOffset(0, 0).addBox(-34.5F, -6.0F, -38.0F, 18.0F, 12.0F, 10.0F, 0.0F, true);
        rightArm.setTextureOffset(160, 0).addBox(-34.5F, -6.0F, -8.0F, 32.0F, 12.0F, 12.0F, 0.0F, true);

        rightThumb = new AdvancedModelBox(this);
        rightThumb.setRotationPoint(-16.5F, 4.0F, -22.0F);
        rightArm.addChild(rightThumb);
        rightThumb.setTextureOffset(10, 34).addBox(0.0F, -2.0F, -6.0F, 6.0F, 4.0F, 6.0F, 0.0F, true);
        rightThumb.setTextureOffset(0, 46).addBox(0.0F, -2.0F, -12.0F, 6.0F, 4.0F, 6.0F, 0.0F, true);

        chestSpikes = new AdvancedModelBox(this);
        chestSpikes.setRotationPoint(0.0F, -19.8909F, -9.5233F);
        chest.addChild(chestSpikes);
        
        chestSpike1 = new AdvancedModelBox(this);
        chestSpike1.setRotationPoint(0.0F, 5.4558F, -4.6274F);
        chestSpikes.addChild(chestSpike1);
        setRotateAngle(chestSpike1, 0.7854F, 0.0F, 0.0F);
        chestSpike1.setTextureOffset(128, 290).addBox(0.0F, -29.0F, 11.0F, 0.0F, 54.0F, 54.0F, 0.0F, false);

        chestSpike2 = new AdvancedModelBox(this);
        chestSpike2.setRotationPoint(10.0F, 0.0F, 0.0F);
        chestSpikes.addChild(chestSpike2);
        setRotateAngle(chestSpike2, 0.7854F, 0.2182F, 0.2618F);
        chestSpike2.setTextureOffset(236, 330).addBox(0.0F, -28.4142F, -0.1299F, 0.0F, 54.0F, 44.0F, 0.0F, false);

        chestSpike3 = new AdvancedModelBox(this);
        chestSpike3.setRotationPoint(-10.0F, 0.0F, 0.0F);
        chestSpikes.addChild(chestSpike3);
        setRotateAngle(chestSpike3, 0.7854F, -0.2182F, -0.2618F);
        chestSpike3.setTextureOffset(236, 330).addBox(0.0F, -28.4142F, -0.1299F, 0.0F, 54.0F, 44.0F, 0.0F, true);
        
        neckSlope = new AdvancedModelBox(this);
        neckSlope.setRotationPoint(-1.5556F, -50.0F, -24.8889F);
        neck.addChild(neckSlope);
        setRotateAngle(neckSlope, 1.0105F, 0.0F, 0.0F);
        neckSlope.setTextureOffset(426, 480).addBox(-13.0F, 0.0F, -17.0F, 26.0F, 15.0F, 17.0F, -0.01F, false);

        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.15F, 0.15F, 0.15F);
            matrixStackIn.translate(0.0D, 8.55F, 0D);
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

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(TremorzillaEntity.ANIMATION_SPEAK);
        animator.startKeyframe(5);
        animator.rotate(neck, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(8);
        animator.rotate(neck, (float) Math.toRadians(10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(40), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(5);
        animator.setAnimation(TremorzillaEntity.ANIMATION_ROAR_1);
        animator.startKeyframe(10);
        animator.rotate(neck, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(chest, (float) Math.toRadians(-5), 0, 0);
        animator.move(neck, 0, 10, 0);
        animator.move(head, 0, 1, 2);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(8);
        animator.move(neck, 0, -5, -5);
        animator.rotate(neck, (float) Math.toRadians(50), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-60), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(50), 0, 0);
        animator.rotate(chest, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(30);
        animator.resetKeyframe(10);
        animator.setAnimation(TremorzillaEntity.ANIMATION_ROAR_2);
        animator.startKeyframe(10);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(20), 0, 0);
        animator.rotate(chest, (float) Math.toRadians(10), 0, 0);
        animator.move(head, 0, -1, -2);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(13);
        animator.move(neck, 0, 10, -5);
        animator.move(head, 0, 1, 5);
        animator.rotate(neck, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(chest, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-80), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(60), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(25);
        animator.resetKeyframe(10);
        animator.setAnimation(TremorzillaEntity.ANIMATION_RIGHT_SCRATCH);
        animator.startKeyframe(10);
        animator.rotate(chest, (float) Math.toRadians(-15), (float) Math.toRadians(15), 0);
        animator.rotate(head, (float) Math.toRadians(15), (float) Math.toRadians(-15), 0);
        animator.rotate(rightArm, (float) Math.toRadians(-85), (float) Math.toRadians(40), 0);
        animator.rotate(leftArm, (float) Math.toRadians(10), (float) Math.toRadians(25), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animator.move(chest, -5, -10, 2);
        animator.move(rightArm, 0, 0, -8);
        animator.rotate(chest, (float) Math.toRadians(20), (float) Math.toRadians(-35), 0);
        animator.rotate(head, (float) Math.toRadians(-20), (float) Math.toRadians(35), 0);
        animator.rotate(rightArm, (float) Math.toRadians(-35), (float) Math.toRadians(-40), (float) Math.toRadians(-10));
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(10);
        animator.setAnimation(TremorzillaEntity.ANIMATION_LEFT_SCRATCH);
        animator.startKeyframe(10);
        animator.rotate(chest, (float) Math.toRadians(-15), (float) Math.toRadians(-15), 0);
        animator.rotate(head, (float) Math.toRadians(15), (float) Math.toRadians(15), 0);
        animator.rotate(leftArm, (float) Math.toRadians(-85), (float) Math.toRadians(-40), 0);
        animator.rotate(rightArm, (float) Math.toRadians(10), (float) Math.toRadians(-25), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animator.move(chest, 5, -10, 2);
        animator.move(leftArm, 0, 0, -8);
        animator.rotate(chest, (float) Math.toRadians(20), (float) Math.toRadians(35), 0);
        animator.rotate(head, (float) Math.toRadians(-20), (float) Math.toRadians(-35), 0);
        animator.rotate(leftArm, (float) Math.toRadians(-35), (float) Math.toRadians(40), (float) Math.toRadians(10));
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(10);
        animator.setAnimation(TremorzillaEntity.ANIMATION_RIGHT_STOMP);
        animator.startKeyframe(10);
        animator.move(torso, 0, -20, 2);
        animator.move(leftLeg, 0, 20, -2);
        animator.move(rightLeg, 0, -10, -10);
        animator.move(tail1, 0, 10, 2);
        animator.move(rightArm, 0, -8, 2);
        animator.move(leftArm, 0, -8, 2);
        animator.rotate(torso, (float) Math.toRadians(-15), (float) Math.toRadians(-15), 0);
        animator.rotate(chest, (float) Math.toRadians(5), (float) Math.toRadians(5), 0);
        animator.rotate(neck, (float) Math.toRadians(10), (float) Math.toRadians(10), 0);
        animator.rotate(leftLeg, (float) Math.toRadians(15), (float) Math.toRadians(15), (float) Math.toRadians(5));
        animator.rotate(rightLeg, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-5));
        animator.rotate(tail1, (float) Math.toRadians(10), (float) Math.toRadians(15), 0);
        animator.rotate(tail2, (float) Math.toRadians(5), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animator.move(torso, 0, 0, 10);
        animator.move(rightLeg, 0, -10, -30);
        animator.rotate(torso, (float) Math.toRadians(15), 0, 0);
        animator.rotate(chest, (float) Math.toRadians(5), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(5), 0, 0);
        animator.rotate(leftLeg, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(rightLeg, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(rightArm, (float) Math.toRadians(-35), 0, 0);
        animator.rotate(leftArm, (float) Math.toRadians(-35), 0, 0);
        animator.rotate(tail1, (float) Math.toRadians(-15), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(10);
        animator.setAnimation(TremorzillaEntity.ANIMATION_LEFT_STOMP);
        animator.startKeyframe(10);
        animator.move(torso, 0, -20, 2);
        animator.move(rightLeg, 0, 20, -2);
        animator.move(leftLeg, 0, -10, -10);
        animator.move(tail1, 0, 10, 2);
        animator.move(rightArm, 0, -8, 2);
        animator.move(leftArm, 0, -8, 2);
        animator.rotate(torso, (float) Math.toRadians(-15), (float) Math.toRadians(15), 0);
        animator.rotate(chest, (float) Math.toRadians(5), (float) Math.toRadians(-5), 0);
        animator.rotate(neck, (float) Math.toRadians(10), (float) Math.toRadians(-10), 0);
        animator.rotate(rightLeg, (float) Math.toRadians(15), (float) Math.toRadians(-15), (float) Math.toRadians(-5));
        animator.rotate(leftLeg, (float) Math.toRadians(-15), 0, (float) Math.toRadians(5));
        animator.rotate(tail1, (float) Math.toRadians(10), (float) Math.toRadians(-15), 0);
        animator.rotate(tail2, (float) Math.toRadians(5), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.startKeyframe(5);
        animator.move(torso, 0, 0, 10);
        animator.move(leftLeg, 0, -10, -30);
        animator.rotate(torso, (float) Math.toRadians(15), 0, 0);
        animator.rotate(chest, (float) Math.toRadians(5), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(5), 0, 0);
        animator.rotate(leftLeg, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(rightLeg, (float) Math.toRadians(-15), 0, 0);
        animator.rotate(rightArm, (float) Math.toRadians(-35), 0, 0);
        animator.rotate(leftArm, (float) Math.toRadians(-35), 0, 0);
        animator.rotate(tail1, (float) Math.toRadians(-15), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(10);
        animator.setAnimation(TremorzillaEntity.ANIMATION_BITE);
        animator.startKeyframe(5);
        animator.rotate(chest, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(-5), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(chest, 0, 0, -5);
        animator.move(neck, 0, 2, -5);
        animator.rotate(chest, (float) Math.toRadians(25), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(25), 0, (float) Math.toRadians(15));
        animator.rotate(head, (float) Math.toRadians(-55), 0, (float) Math.toRadians(-15));
        animator.rotate(jaw, (float) Math.toRadians(55), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(chest, 0, 0, -5);
        animator.move(neck, 0, 2, -5);
        animator.rotate(chest, (float) Math.toRadians(35), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(15), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-55), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(5), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(TremorzillaEntity.ANIMATION_PREPARE_BREATH);
        animator.startKeyframe(13);
        animator.move(neck, 0, 10, -5);
        animator.move(head, 0, 5, 8);
        animator.rotate(chest, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-70), 0, 0);
        animator.rotate(leftArm, (float) Math.toRadians(-35), (float) Math.toRadians(-40), (float) Math.toRadians(20));
        animator.rotate(rightArm, (float) Math.toRadians(-35), (float) Math.toRadians(40), (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(5);
        animator.setAnimation(TremorzillaEntity.ANIMATION_CHEW);
        animator.startKeyframe(6);
        animator.rotate(jaw, (float) Math.toRadians(25), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(neck, 0, 0, (float) Math.toRadians(5));
        animator.endKeyframe();
        animator.startKeyframe(6);
        animator.rotate(jaw, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(head, (float) Math.toRadians(5), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(6);
        animator.rotate(jaw, (float) Math.toRadians(25), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(neck, 0, 0, (float) Math.toRadians(-5));
        animator.endKeyframe();
        animator.startKeyframe(6);
        animator.rotate(jaw, (float) Math.toRadians(-5), 0, 0);
        animator.rotate(head, (float) Math.toRadians(5), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(6);
        animator.rotate(jaw, (float) Math.toRadians(25), 0, 0);
        animator.rotate(head, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(neck, 0, 0, (float) Math.toRadians(5));
        animator.endKeyframe();
        animator.resetKeyframe(5);

    }

    @Override
    public void setupAnim(TremorzillaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entity);
        if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            setupAnimForAnimation(entity, entity.getAnimation(), limbSwing, limbSwingAmount, ageInTicks);
        }
        float partialTicks = ageInTicks - entity.tickCount;
        float burnProgress = entity.getBeamProgress(partialTicks);
        float danceProgress = entity.getDanceProgress(partialTicks);
        float sitProgress = entity.getSitProgress(partialTicks) * (1F - danceProgress);
        float swimProgress = entity.getSwimAmount(partialTicks) * (1F - sitProgress);
        float groundProgress = 1F - swimProgress;
        float standProgress = 1F - sitProgress;
        float spikesDownProgress = entity.getClientSpikeDownAmount(partialTicks);
        float buryEggsAmount = entity.getBuryEggsProgress(partialTicks);
        float spikes1Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 0F);
        float spikes2Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 1F);
        float spikes3Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 2F);
        float spikes4Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 3F);
        float spikes5Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 4F);
        float spikes6Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 5F);
        float walkSpeed = 0.25F;
        float walkDegree = 2F;
        float swimSpeed = 0.25F;
        float swimDegree = 2F;
        float limbSwingAmountGround = limbSwingAmount * groundProgress;
        float limbSwingAmountSwim = limbSwingAmount * swimProgress;
        float headYawAmount = netHeadYaw / 57.295776F * (1F - burnProgress) * standProgress;
        float headPitchAmount = headPitch / 57.295776F * (1F - burnProgress) * standProgress;
        Vec3 burnPos = entity.getClientBeamEndPosition(partialTicks);
        articulateLegs(entity.legSolver, partialTicks, groundProgress * (1F - danceProgress) * standProgress);
        if (!straighten && !entity.isFakeEntity()) {
            positionTail(entity, partialTicks);
        }
        if (buryEggsAmount > 0.0F) {
            limbSwing = ageInTicks;
            limbSwingAmount = buryEggsAmount * 0.5F;
        }
        animateDancing(entity, danceProgress, ageInTicks);
        progressPositionPrev(torso, swimProgress, 0, 20, 0, 1F);
        progressPositionPrev(head, swimProgress, 0, 5, 5, 1F);
        progressPositionPrev(neck, swimProgress, 0, 3, -5, 1F);
        progressPositionPrev(leftLeg, swimProgress, 2, 15, 0, 1F);
        progressPositionPrev(rightLeg, swimProgress, -2, 15, 0, 1F);
        progressPositionPrev(neck, swimProgress, 0, 3, -5, 1F);
        progressRotationPrev(torso, swimProgress, (float) Math.toRadians(70), 0, 0, 1F);
        progressRotationPrev(tail1, swimProgress, (float) Math.toRadians(-70), 0, 0, 1F);
        progressRotationPrev(neck, swimProgress, (float) Math.toRadians(-10), 0, 0, 1F);
        progressRotationPrev(head, swimProgress, (float) Math.toRadians(-40), 0, 0, 1F);
        progressRotationPrev(rightArm, swimProgress, (float) Math.toRadians(-50), (float) Math.toRadians(-60), (float) Math.toRadians(-30), 1F);
        progressRotationPrev(leftArm, swimProgress, (float) Math.toRadians(-50), (float) Math.toRadians(60), (float) Math.toRadians(30), 1F);
        progressPositionPrev(neck, burnProgress, 0, -10, -8, 1F);
        progressPositionPrev(head, burnProgress, 0, 2.5F, 5, 1F);
        progressPositionPrev(chest, burnProgress, 0, -5, -5, 1F);
        progressRotationPrev(jaw, burnProgress, (float) Math.toRadians(70), 0, 0, 1F);
        progressRotationPrev(head, burnProgress, (float) Math.toRadians(-110), 0, 0, 1F);
        progressRotationPrev(neck, burnProgress, (float) Math.toRadians(50), 0, 0, 1F);
        progressRotationPrev(chest, burnProgress, (float) Math.toRadians(20), 0, 0, 1F);
        progressPositionPrev(tail3Spikes, spikes1Down, 0, 8, 0, 1F);
        progressPositionPrev(tail2Spikes, spikes2Down, 0, 16, 0, 1F);
        progressPositionPrev(tail1Spikes, spikes3Down, 0, 22, 0, 1F);
        progressPositionPrev(tail1Spike1, spikes3Down, -6, 0, 0, 1F);
        progressPositionPrev(tail1Spike2, spikes3Down, 6, 0, 0, 1F);
        progressPositionPrev(torsoSpikes, spikes4Down, 0, 10, -27, 1F);
        progressPositionPrev(torsoSpike2, spikes4Down, 12, 0, 0, 1F);
        progressPositionPrev(torsoSpike3, spikes4Down, -12, 0, 0, 1F);
        progressPositionPrev(chestSpikes, spikes5Down, 0, 20, -22, 1F);
        progressPositionPrev(chestSpike2, spikes5Down, -10, 0, 0, 1F);
        progressPositionPrev(chestSpike3, spikes5Down, 10, 0, 0, 1F);
        progressPositionPrev(neckSpikes, spikes6Down, 0, 4, -7, 1F);
        progressPositionPrev(leftArm, limbSwingAmountGround, 0, -3, -5, 1F);
        progressPositionPrev(rightArm, limbSwingAmountGround, 0, -3, -5, 1F);
        progressPositionPrev(torso, sitProgress, 0, 5, -5, 1F);
        progressPositionPrev(leftLeg, sitProgress, 0, 8, 10, 1F);
        progressPositionPrev(rightLeg, sitProgress, 0, 8, 10, 1F);
        progressPositionPrev(tail1, sitProgress, 0, 0, -20, 1F);
        progressPositionPrev(neck, sitProgress, 0, 5, -5, 1F);
        progressPositionPrev(head, sitProgress, 0, 0, 5, 1F);
        progressPositionPrev(torsoSpikes, sitProgress, 0, 9, -3, 1F);
        progressRotationPrev(torso, sitProgress, (float) Math.toRadians(80), 0, 0, 1F);
        progressRotationPrev(chest, sitProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(leftLeg, sitProgress, (float) Math.toRadians(-80), 0, 0, 1F);
        progressRotationPrev(rightLeg, sitProgress, (float) Math.toRadians(-80), 0, 0, 1F);
        progressRotationPrev(tail1, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-20), (float) Math.toRadians(-10), 1F);
        progressRotationPrev(neck, sitProgress, (float) Math.toRadians(-25), 0, (float) Math.toRadians(30), 1F);
        progressRotationPrev(head, sitProgress, (float) Math.toRadians(-40), (float) Math.toRadians(-20), (float) Math.toRadians(20), 1F);
        progressRotationPrev(rightArm, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(-50), (float) Math.toRadians(50), 1F);
        progressRotationPrev(leftArm, sitProgress, (float) Math.toRadians(-90), (float) Math.toRadians(50), (float) Math.toRadians(-20), 1F);
        progressRotationPrev(chestSpikes, sitProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(torsoSpikes, sitProgress, (float) Math.toRadians(-40), 0, 0, 1F);

        float bodyIdleBob = ACMath.walkValue(ageInTicks, 1, 0.025F, -1F, 3F, false) * standProgress;
        this.walk(neck, 0.025F, 0.03F, false, 1F, 0F, ageInTicks, 1);
        this.walk(head, 0.025F, 0.03F, true, 2F, -0.03F, ageInTicks, 1);
        this.walk(jaw, 0.025F, 0.03F, true, 3F, 0F, ageInTicks, 1);
        this.swing(tail1, 0.025F, 0.05F, true, -1F, 0F, ageInTicks, 1);
        this.swing(tail2, 0.025F, 0.05F, true, -2F, 0F, ageInTicks, 1);
        this.swing(tail3, 0.025F, 0.1F, true, -3F, 0F, ageInTicks, 1);
        this.flap(leftArm, 0.025F, 0.1F, true, 1, -0.1F, ageInTicks, 1);
        this.swing(leftArm, 0.025F, 0.1F, true, 2, -0.1F, ageInTicks, 1);
        this.flap(rightArm, 0.025F, 0.1F, false, 1, -0.1F, ageInTicks, 1);
        this.swing(rightArm, 0.025F, 0.1F, false, 2, -0.1F, ageInTicks, 1);
        this.walk(chest, 0.025F, 0.03F, true, -1F, -0.02F, ageInTicks, 1);
        float bodyWalkBob = -Math.abs(ACMath.walkValue(limbSwing, limbSwingAmountGround, walkSpeed, -1.5F, 6, false));
        this.torso.rotationPointY += bodyIdleBob + bodyWalkBob;
        this.leftLeg.rotationPointY -= (bodyIdleBob + bodyWalkBob) * groundProgress;
        this.rightLeg.rotationPointY -= (bodyIdleBob + bodyWalkBob) * groundProgress;
        this.tail1.rotationPointY -= (bodyIdleBob + bodyWalkBob) * groundProgress;

        this.walk(torso, walkSpeed, walkDegree * 0.05F, true, 1.5F, -0.2F, limbSwing, limbSwingAmountGround);
        this.walk(leftLeg, walkSpeed, walkDegree * 0.05F, false, 1.5F, -0.2F, limbSwing, limbSwingAmountGround);
        this.walk(rightLeg, walkSpeed, walkDegree * 0.05F, false, 1.5F, -0.2F, limbSwing, limbSwingAmountGround);
        this.walk(tail1, walkSpeed, walkDegree * 0.05F, false, 1.5F, -0.2F, limbSwing, limbSwingAmountGround);
        this.swing(tail1, walkSpeed, walkDegree * 0.05F, false, 2F, 0F, limbSwing, limbSwingAmountGround);
        this.swing(tail2, walkSpeed, walkDegree * 0.05F, false, 1F, 0F, limbSwing, limbSwingAmountGround);
        this.swing(tail3, walkSpeed, walkDegree * 0.05F, false, 0F, 0F, limbSwing, limbSwingAmountGround);
        this.swing(tail4, walkSpeed, walkDegree * 0.1F, false, -1F, 0F, limbSwing, limbSwingAmountGround);
        this.swing(leftArm, walkSpeed, walkDegree * 0.2F, false, -2F, 0.45F, limbSwing, limbSwingAmountGround);
        this.swing(rightArm, walkSpeed, walkDegree * 0.2F, false, -2F, -0.45F, limbSwing, limbSwingAmountGround);
        this.walk(neck, walkSpeed, walkDegree * 0.05F, true, 1F, -0.1F, limbSwing, limbSwingAmountGround);
        this.walk(head, walkSpeed, walkDegree * 0.05F, true, 2.5F, 0.2F, limbSwing, limbSwingAmountGround);
        this.walk(leftLeg, walkSpeed, walkDegree * 0.2F, false, 1.5F, 0F, limbSwing, limbSwingAmountGround);
        leftLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmountGround, walkSpeed, -0.5F, 20, true));
        leftLeg.rotationPointZ += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmountGround, walkSpeed, -1F, 5, true));
        this.walk(rightLeg, walkSpeed, walkDegree * 0.2F, true, 1.5F, 0F, limbSwing, limbSwingAmountGround);
        rightLeg.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmountGround, walkSpeed, -0.5F, 20, false));
        rightLeg.rotationPointZ += Math.min(0, ACMath.walkValue(limbSwing, limbSwingAmountGround, walkSpeed, -1F, 5, false));
        this.bob(root, swimSpeed, 4, false, limbSwing, limbSwingAmountSwim);
        this.swing(tail2, swimSpeed, 0.1F, true, -2F, 0F, limbSwing, limbSwingAmountSwim);
        this.swing(tail3, swimSpeed, 0.2F, true, -3F, 0F, limbSwing, limbSwingAmountSwim);
        this.swing(rightArm, swimSpeed, 0.5F, true, -1, -0.5F, limbSwing, limbSwingAmountSwim);
        this.flap(rightArm, swimSpeed, 0.2F, true, -2, -0.4F, limbSwing, limbSwingAmountSwim);
        this.swing(leftArm, swimSpeed, 0.5F, true, -1, 0.5F, limbSwing, limbSwingAmountSwim);
        this.flap(leftArm, swimSpeed, 0.2F, true, -2, 0.4F, limbSwing, limbSwingAmountSwim);
        this.walk(leftLeg, swimSpeed, 0.3F, true, -4, 0.1F, limbSwing, limbSwingAmountSwim);
        this.flap(leftLeg, swimSpeed, 0.1F, false, -4, -0.1F, limbSwing, limbSwingAmountSwim);
        this.walk(rightLeg, swimSpeed, 0.3F, false, -4, -0.1F, limbSwing, limbSwingAmountSwim);
        this.flap(rightLeg, swimSpeed, 0.1F, true, -4, -0.1F, limbSwing, limbSwingAmountSwim);
        this.swing(torso, swimSpeed, 0.05F, true, -3, 0F, limbSwing, limbSwingAmountSwim);
        this.flap(chest, swimSpeed, 0.1F, true, -3, 0F, limbSwing, limbSwingAmountSwim);
        this.flap(neck, swimSpeed, 0.2F, true, -3, 0F, limbSwing, limbSwingAmountSwim);
        this.flap(head, swimSpeed, 0.2F, false, -3, 0F, limbSwing, limbSwingAmountSwim);

        this.flap(chest, 1F, 0.03F, true, -1F, 0F, ageInTicks, burnProgress);
        this.flap(neck, 2F, 0.03F, true, -2F, 0F, ageInTicks, burnProgress);
        this.flap(head, 2F, 0.03F, true, -2F, 0F, ageInTicks, burnProgress);
        this.walk(jaw, 0.4F, 0.1F, true, -1F, 0F, ageInTicks, burnProgress);
        this.walk(head, 0.4F, 0.1F, false, 0F, 0F, ageInTicks, burnProgress);
        if (burnProgress > 0 && burnPos != null) {
            Vec3 vector3d = burnPos;
            Vec3 vector3d1 = entity.getBeamShootFrom(partialTicks);
            Vec3 normalized = burnPos.subtract(vector3d1).normalize();
            double d0 = Mth.clamp(normalized.y, -1F, 1F) * Math.PI / 2F;
            Vec3 vector3d2 = entity.getBodyRotViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = (new Vec3(vector3d.x - vector3d1.x, 0.0D, vector3d.z - vector3d1.z)).normalize().yRot(((float) Math.PI / 2F));
            double d1 = vector3d2.dot(vector3d3);
            float xRotBy =  (float) (d0 * burnProgress);
            this.neck.rotateAngleX -= xRotBy * 0.5F;
            this.head.rotateAngleX -= xRotBy * 0.5F;
            this.neck.rotateAngleY += d1 * burnProgress;
            if(xRotBy > 0.0F){
                this.neck.rotationPointY += 5 * xRotBy;
                this.neck.rotationPointZ += 5 * xRotBy;
                this.head.rotationPointZ += 5F * xRotBy;
            }
        }
        if(entity.isTremorzillaSwimming()){
        }else{
            this.neck.rotateAngleY += headYawAmount * 0.5F;
            this.head.rotateAngleY += headYawAmount * 0.5F;
        }
        this.neck.rotateAngleX += headPitchAmount * 0.5F;
        this.head.rotateAngleX += headPitchAmount * 0.5F;
    }

    public void showSpikesBasedOnProgress(float spikesDownProgress, float threshold) {
        boolean spikes1Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 0F) > threshold;
        boolean spikes2Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 1F) > threshold;
        boolean spikes3Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 2F) > threshold;
        boolean spikes4Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 3F) > threshold;
        boolean spikes5Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 4F) > threshold;
        boolean spikes6Down = TremorzillaEntity.calculateSpikesDownAmountAtIndex(spikesDownProgress, 6F, 5F) > threshold;
        this.tail3.showModel = spikes1Down;
        this.tail3Spikes.showModel = spikes1Down;
        this.tail2.showModel = spikes2Down;
        this.tail2Spikes.showModel = spikes2Down;
        this.tail1.showModel = spikes3Down;
        this.tail1Spikes.showModel = spikes3Down;
        this.tail1Spike1.showModel = spikes3Down;
        this.tail1Spike2.showModel = spikes3Down;
        this.torso.showModel = spikes4Down;
        this.torsoSpikes.showModel = spikes4Down;
        this.torsoSpike1.showModel = spikes4Down;
        this.torsoSpike2.showModel = spikes4Down;
        this.torsoSpike3.showModel = spikes4Down;
        this.chest.showModel = spikes5Down;
        this.neck.showModel = spikes6Down;
    }

    public void showAllSpikes() {
        this.tail3.showModel = true;
        this.tail3Spikes.showModel = true;
        this.tail2.showModel = true;
        this.tail2Spikes.showModel = true;
        this.tail1.showModel = true;
        this.tail1Spikes.showModel = true;
        this.tail1Spike1.showModel = true;
        this.tail1Spike2.showModel = true;
        this.torso.showModel = true;
        this.torsoSpikes.showModel = true;
        this.torsoSpike1.showModel = true;
        this.torsoSpike2.showModel = true;
        this.torsoSpike3.showModel = true;
        this.chest.showModel = true;
        this.neck.showModel = true;
    }

    private void positionTail(TremorzillaEntity entity, float partialTicks) {
        float tailPart1Pitch = (float) Math.toRadians(entity.tailPart1.calculateAnimationAngle(partialTicks, true));
        float tailPart2Pitch = (float) Math.toRadians(entity.tailPart2.calculateAnimationAngle(partialTicks, true));
        float f = entity.tailPart3.calculateAnimationAngle(partialTicks, true) + entity.tailPart4.calculateAnimationAngle(partialTicks, true);
        float tailPart3Pitch = (float) Math.toRadians(Mth.wrapDegrees(f) * 0.25F);
        float f1 = entity.tailPart4.calculateAnimationAngle(partialTicks, true) + entity.tailPart5.calculateAnimationAngle(partialTicks, true);
        float tailPart4Pitch = (float) Math.toRadians(Mth.wrapDegrees(f1) * 0.25F);
        float tailPart1Yaw = (float) Math.toRadians(entity.tailPart1.calculateAnimationAngle(partialTicks, false));
        float tailPart2Yaw = (float) Math.toRadians(entity.tailPart2.calculateAnimationAngle(partialTicks, false));
        float f2 = entity.tailPart3.calculateAnimationAngle(partialTicks, false) + entity.tailPart4.calculateAnimationAngle(partialTicks, false);
        float tailPart3Yaw = (float) Math.toRadians(Mth.wrapDegrees(f2) * 0.25F);
        float f3 = entity.tailPart4.calculateAnimationAngle(partialTicks, false) + entity.tailPart5.calculateAnimationAngle(partialTicks, false);
        float tailPart4Yaw = (float) Math.toRadians(Mth.wrapDegrees(f3) * 0.25F);
        tail1.rotateAngleY += tailPart1Yaw;
        tail1.rotateAngleX += tailPart1Pitch;
        tail2.rotateAngleY += tailPart2Yaw;
        tail2.rotateAngleX += tailPart2Pitch;
        tail3.rotateAngleY += tailPart3Yaw;
        tail3.rotateAngleX += tailPart3Pitch;
        tail4.rotateAngleY += tailPart4Yaw;
        tail4.rotateAngleX += tailPart4Pitch;
    }

    private void articulateLegs(TremorzillaLegSolver legs, float partialTick, float modifier) {
        float heightBackLeft = legs.legs[0].getHeight(partialTick);
        float heightBackRight = legs.legs[1].getHeight(partialTick);
        float max = (1F - ACMath.smin(1F - heightBackLeft, 1F - heightBackRight, 0.1F)) * 0.8F;
        root.rotationPointY += max * 16 * modifier;
        rightLeg.rotationPointY += (heightBackRight - max) * 16 * modifier;
        leftLeg.rotationPointY += (heightBackLeft - max) * 16 * modifier;
    }

    private void animateDancing(TremorzillaEntity entity, float danceAmount, float ageInTicks) {
        float danceSpeed = 0.2F;
        root.rotationPointY -= Math.abs(ACMath.walkValue(ageInTicks, danceAmount, danceSpeed, -1F, 50, true));
        this.swing(root, danceSpeed, 0.2F, true, 0.5F, 0F, ageInTicks, danceAmount);
        this.swing(neck, danceSpeed, 0.2F, false, 1F, 0F, ageInTicks, danceAmount);
        this.swing(tail1, danceSpeed, 0.2F, false, 1F, 0F, ageInTicks, danceAmount);
        this.swing(tail2, danceSpeed, 0.1F, false, 2F, 0F, ageInTicks, danceAmount);
        this.swing(tail4, danceSpeed, 0.1F, false, 3F, 0F, ageInTicks, danceAmount);
        this.walk(leftLeg, danceSpeed, 0.6F, false, 0.5F, 0F, ageInTicks, danceAmount);
        this.flap(leftLeg, danceSpeed, 0.1F, false, 2F, 0.1F, ageInTicks, danceAmount);
        this.walk(rightLeg, danceSpeed, 0.6F, true, 1.5F, 0F, ageInTicks, danceAmount);
        this.flap(rightLeg, danceSpeed, 0.1F, true, 2F, 0.1F, ageInTicks, danceAmount);
        this.walk(rightArm, danceSpeed, 0.75F, false, 1.5F, -0.5F, ageInTicks, danceAmount);
        this.swing(rightArm, danceSpeed, 0.75F, false, 1F, -0.4F, ageInTicks, danceAmount);
        this.walk(leftArm, danceSpeed, 0.75F, true, 1.5F, 0.5F, ageInTicks, danceAmount);
        this.swing(leftArm, danceSpeed, 0.75F, false, 1F, 0.4F, ageInTicks, danceAmount);

    }

    private void setupAnimForAnimation(TremorzillaEntity entity, Animation animation, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float partialTick = ageInTicks - entity.tickCount;
        if (entity.getAnimation() == TremorzillaEntity.ANIMATION_ROAR_1 || entity.getAnimation() == TremorzillaEntity.ANIMATION_ROAR_2) {
            float animationIntensity = 0;
            float neckSide = 1F;
            if (entity.getAnimation() == TremorzillaEntity.ANIMATION_ROAR_1) {
                animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 1, animation, partialTick, 10, 50);
            } else {
                neckSide = 0.2F;
                animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 1, animation, partialTick, 15, 50);
            }
            this.neck.swing(0.2F, neckSide * 0.7F, false, 0F, 0F, ageInTicks, animationIntensity);
            this.neck.walk(0.2F, neckSide * 0.3F, false, 1F, 0F, ageInTicks, animationIntensity);
            this.chest.swing(0.2F, 0.1F, false, 0F, 0F, ageInTicks, animationIntensity);
            this.chest.walk(0.2F, 0.05F, false, 1F, 0F, ageInTicks, animationIntensity);
            this.head.swing(0.2F, neckSide * 0.3F, false, 1F, 0F, ageInTicks, animationIntensity);
            this.head.swing(1F, 0.1F, false, -1F, 0F, ageInTicks, animationIntensity);
            this.head.walk(0.2F, 0.3F, false, 2F, -0.2F, ageInTicks, animationIntensity);
            this.jaw.walk(2F, 0.1F, false, 1F, 0F, ageInTicks, animationIntensity);
        }
    }

    public Vec3 getMouthPosition(Vec3 offsetIn) {
        PoseStack translationStack = new PoseStack();
        translationStack.pushPose();
        root.translateAndRotate(translationStack);
        torso.translateAndRotate(translationStack);
        chest.translateAndRotate(translationStack);
        neck.translateAndRotate(translationStack);
        head.translateAndRotate(translationStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(translationStack.last().pose());
        Vec3 vec3 = new Vec3(-armOffsetVec.x(), -armOffsetVec.y(), armOffsetVec.z());
        translationStack.popPose();
        return vec3.add(0, 1.25D, 0);
    }


    public void translateToNeck(PoseStack translationStack) {
        root.translateAndRotate(translationStack);
        torso.translateAndRotate(translationStack);
        chest.translateAndRotate(translationStack);
        neck.translateAndRotate(translationStack);
        head.translateAndRotate(translationStack);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, torso, torsoSpikes, rightLeg, leftLeg, chest, chestSpikes, neck, neckSlope, rightArm, leftArm, rightThumb, leftThumb, head, jaw, rightEar, leftEar, neckSpikes, tail1, tail1Spikes, tail2, tail2Spikes, tail3, tail3Spikes, tail4, torsoSpike1, torsoSpike2, torsoSpike3, tail1Spike1, tail1Spike2, cube_r6, chestSpike1, chestSpike2, chestSpike3);
    }

}
