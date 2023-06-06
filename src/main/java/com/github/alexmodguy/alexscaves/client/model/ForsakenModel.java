package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.ForsakenEntity;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class ForsakenModel extends AdvancedEntityModel<ForsakenEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox chest;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox lforeArm;
    private final AdvancedModelBox lhand;
    private final AdvancedModelBox lfinger;
    private final AdvancedModelBox lfinger2;
    private final AdvancedModelBox lfinger3;
    private final AdvancedModelBox lthumb;
    private final AdvancedModelBox lthumb2;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox rforeArm;
    private final AdvancedModelBox rhand;
    private final AdvancedModelBox rfinger;
    private final AdvancedModelBox rfinger2;
    private final AdvancedModelBox rfinger3;
    private final AdvancedModelBox rthumb;
    private final AdvancedModelBox rthumb2;
    private final AdvancedModelBox lupperArm;
    private final AdvancedModelBox lupperForeArm;
    private final AdvancedModelBox lupperHand;
    private final AdvancedModelBox lupperFinger;
    private final AdvancedModelBox lupperThumb;
    private final AdvancedModelBox lupperThumb2;
    private final AdvancedModelBox lupperFinger2;
    private final AdvancedModelBox lupperFinger3;
    private final AdvancedModelBox rupperArm;
    private final AdvancedModelBox rupperForeArm;
    private final AdvancedModelBox rupperHand;
    private final AdvancedModelBox rupperFinger;
    private final AdvancedModelBox rupperThumb;
    private final AdvancedModelBox rupperThumb2;
    private final AdvancedModelBox rupperFinger2;
    private final AdvancedModelBox rupperFinger3;
    private final AdvancedModelBox hips;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox lthigh;
    private final AdvancedModelBox lcalf;
    private final AdvancedModelBox lfoot;
    private final AdvancedModelBox ltoe;
    private final AdvancedModelBox ltoe2;
    private final AdvancedModelBox ltoe3;
    private final AdvancedModelBox rthigh;
    private final AdvancedModelBox rcalf;
    private final AdvancedModelBox rfoot;
    private final AdvancedModelBox rtoe;
    private final AdvancedModelBox rtoe2;
    private final AdvancedModelBox rtoe3;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox cube_r5;
    private final AdvancedModelBox cube_r6;
    private final AdvancedModelBox skull;
    private final AdvancedModelBox rBigear;
    private final AdvancedModelBox lBigear;
    private final AdvancedModelBox rear;
    private final AdvancedModelBox lear;
    private final AdvancedModelBox jaw;
    private final ModelAnimator animator;

    public ForsakenModel() {
        texWidth = 256;
        texHeight = 256;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        chest = new AdvancedModelBox(this);
        chest.setRotationPoint(0.0F, -40.5F, -3.5F);
        root.addChild(chest);
        setRotateAngle(chest, 0.3927F, 0.0F, 0.0F);
        chest.setTextureOffset(0, 0).addBox(-10.0F, -13.5F, -12.5F, 20.0F, 26.0F, 30.0F, 0.0F, false);
        chest.setTextureOffset(70, 26).addBox(-10.0F, 12.5F, -12.5F, 20.0F, 6.0F, 30.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, -13.5F, 8.5F);
        chest.addChild(cube_r1);
        setRotateAngle(cube_r1, -1.2217F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(154, 62).addBox(-10.0F, -10.0F, 0.0F, 20.0F, 10.0F, 0.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, -13.5F, -2.5F);
        chest.addChild(cube_r2);
        setRotateAngle(cube_r2, -1.2217F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(154, 62).addBox(-10.0F, -10.0F, 0.0F, 20.0F, 10.0F, 0.0F, 0.0F, false);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(9.75F, -48.5F, -8.0F);
        root.addChild(larm);
        larm.setTextureOffset(30, 162).addBox(-1.75F, -6.5F, -4.0F, 6.0F, 25.0F, 8.0F, 0.01F, false);

        lforeArm = new AdvancedModelBox(this);
        lforeArm.setRotationPoint(1.5F, 15.5F, -0.5F);
        larm.addChild(lforeArm);
        lforeArm.setTextureOffset(148, 147).addBox(-2.5F, 1.0F, -3.5F, 5.0F, 30.0F, 9.0F, 0.0F, false);

        lhand = new AdvancedModelBox(this);
        lhand.setRotationPoint(0.5F, 30.0F, -1.3333F);
        lforeArm.addChild(lhand);
        lhand.setTextureOffset(140, 24).addBox(-5.0F, 0.0F, -12.1667F, 9.0F, 3.0F, 14.0F, 0.0F, false);

        lfinger = new AdvancedModelBox(this);
        lfinger.setRotationPoint(-4.25F, 0.0F, -11.1667F);
        lhand.addChild(lfinger);
        lfinger.setTextureOffset(0, 56).addBox(-1.0F, -3.0F, -10.0F, 2.0F, 6.0F, 11.0F, 0.0F, true);

        lfinger2 = new AdvancedModelBox(this);
        lfinger2.setRotationPoint(3.75F, 0.0F, -11.1667F);
        lhand.addChild(lfinger2);
        lfinger2.setTextureOffset(0, 56).addBox(-1.0F, -3.0F, -10.0F, 2.0F, 6.0F, 11.0F, 0.0F, true);

        lfinger3 = new AdvancedModelBox(this);
        lfinger3.setRotationPoint(-0.25F, 0.0F, -11.1667F);
        lhand.addChild(lfinger3);
        lfinger3.setTextureOffset(0, 56).addBox(-1.0F, -3.0F, -10.0F, 2.0F, 6.0F, 11.0F, 0.0F, true);

        lthumb = new AdvancedModelBox(this);
        lthumb.setRotationPoint(-4.25F, 0.0F, 0.8333F);
        lhand.addChild(lthumb);
        lthumb.setTextureOffset(107, 68).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 9.0F, 0.0F, true);

        lthumb2 = new AdvancedModelBox(this);
        lthumb2.setRotationPoint(3.5F, 0.0F, 0.8333F);
        lhand.addChild(lthumb2);
        lthumb2.setTextureOffset(107, 68).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 9.0F, 0.0F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-9.75F, -48.5F, -8.0F);
        root.addChild(rarm);
        rarm.setTextureOffset(30, 162).addBox(-4.25F, -6.5F, -4.0F, 6.0F, 25.0F, 8.0F, 0.01F, true);

        rforeArm = new AdvancedModelBox(this);
        rforeArm.setRotationPoint(-1.5F, 15.5F, -0.5F);
        rarm.addChild(rforeArm);
        rforeArm.setTextureOffset(148, 147).addBox(-2.5F, 1.0F, -3.5F, 5.0F, 30.0F, 9.0F, 0.0F, true);

        rhand = new AdvancedModelBox(this);
        rhand.setRotationPoint(-0.5F, 30.0F, -1.3333F);
        rforeArm.addChild(rhand);
        rhand.setTextureOffset(140, 24).addBox(-4.0F, 0.0F, -12.1667F, 9.0F, 3.0F, 14.0F, 0.0F, true);

        rfinger = new AdvancedModelBox(this);
        rfinger.setRotationPoint(4.25F, 0.0F, -11.1667F);
        rhand.addChild(rfinger);
        rfinger.setTextureOffset(0, 56).addBox(-1.0F, -3.0F, -10.0F, 2.0F, 6.0F, 11.0F, 0.0F, false);

        rfinger2 = new AdvancedModelBox(this);
        rfinger2.setRotationPoint(-3.75F, 0.0F, -11.1667F);
        rhand.addChild(rfinger2);
        rfinger2.setTextureOffset(0, 56).addBox(-1.0F, -3.0F, -10.0F, 2.0F, 6.0F, 11.0F, 0.0F, false);

        rfinger3 = new AdvancedModelBox(this);
        rfinger3.setRotationPoint(0.25F, 0.0F, -11.1667F);
        rhand.addChild(rfinger3);
        rfinger3.setTextureOffset(0, 56).addBox(-1.0F, -3.0F, -10.0F, 2.0F, 6.0F, 11.0F, 0.0F, false);

        rthumb = new AdvancedModelBox(this);
        rthumb.setRotationPoint(4.25F, 0.0F, 0.8333F);
        rhand.addChild(rthumb);
        rthumb.setTextureOffset(107, 68).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 9.0F, 0.0F, false);

        rthumb2 = new AdvancedModelBox(this);
        rthumb2.setRotationPoint(-3.5F, 0.0F, 0.8333F);
        rhand.addChild(rthumb2);
        rthumb2.setTextureOffset(107, 68).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 9.0F, 0.0F, true);

        lupperArm = new AdvancedModelBox(this);
        lupperArm.setRotationPoint(9.75F, -54.5F, 6.0F);
        root.addChild(lupperArm);
        setRotateAngle(lupperArm, 0.0F, 0.0F, -1.5708F);
        lupperArm.setTextureOffset(30, 162).addBox(-0.75F, -6.5F, -4.0F, 6.0F, 27.0F, 8.0F, 0.01F, false);

        lupperForeArm = new AdvancedModelBox(this);
        lupperForeArm.setRotationPoint(2.5F, 17.5F, -0.5F);
        lupperArm.addChild(lupperForeArm);
        lupperForeArm.setTextureOffset(79, 127).addBox(-2.5F, -1.0F, -4.5F, 5.0F, 32.0F, 10.0F, 0.0F, false);

        lupperHand = new AdvancedModelBox(this);
        lupperHand.setRotationPoint(-1.75F, 29.5F, 0.5F);
        lupperForeArm.addChild(lupperHand);
        lupperHand.setTextureOffset(0, 136).addBox(-2.5F, -1.5F, -7.0F, 5.0F, 19.0F, 14.0F, 0.0F, false);

        lupperFinger = new AdvancedModelBox(this);
        lupperFinger.setRotationPoint(2.5F, 16.5F, -5.5F);
        lupperHand.addChild(lupperFinger);
        lupperFinger.setTextureOffset(0, 107).addBox(-2.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, false);
        lupperFinger.setTextureOffset(92, 62).addBox(-12.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, false);

        lupperThumb = new AdvancedModelBox(this);
        lupperThumb.setRotationPoint(2.5F, -0.5F, -6.5F);
        lupperHand.addChild(lupperThumb);
        setRotateAngle(lupperThumb, -1.5708F, 0.0F, 0.0F);
        lupperThumb.setTextureOffset(0, 107).addBox(-2.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, false);
        lupperThumb.setTextureOffset(92, 62).addBox(-12.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, false);

        lupperThumb2 = new AdvancedModelBox(this);
        lupperThumb2.setRotationPoint(2.5F, -0.5F, 6.5F);
        lupperHand.addChild(lupperThumb2);
        setRotateAngle(lupperThumb2, 1.5708F, 0.0F, 0.0F);
        lupperThumb2.setTextureOffset(0, 107).addBox(-2.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, false);
        lupperThumb2.setTextureOffset(92, 62).addBox(-12.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, false);

        lupperFinger2 = new AdvancedModelBox(this);
        lupperFinger2.setRotationPoint(2.5F, 16.5F, 0.0F);
        lupperHand.addChild(lupperFinger2);
        lupperFinger2.setTextureOffset(0, 107).addBox(-2.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, false);
        lupperFinger2.setTextureOffset(92, 62).addBox(-12.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, false);

        lupperFinger3 = new AdvancedModelBox(this);
        lupperFinger3.setRotationPoint(2.5F, 16.5F, 5.5F);
        lupperHand.addChild(lupperFinger3);
        lupperFinger3.setTextureOffset(0, 107).addBox(-2.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, false);
        lupperFinger3.setTextureOffset(92, 62).addBox(-12.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, false);

        rupperArm = new AdvancedModelBox(this);
        rupperArm.setRotationPoint(-9.75F, -54.5F, 6.0F);
        root.addChild(rupperArm);
        setRotateAngle(rupperArm, 0.0F, 0.0F, 1.5708F);
        rupperArm.setTextureOffset(30, 162).addBox(-5.25F, -6.5F, -4.0F, 6.0F, 27.0F, 8.0F, 0.01F, true);

        rupperForeArm = new AdvancedModelBox(this);
        rupperForeArm.setRotationPoint(-2.5F, 17.5F, -0.5F);
        rupperArm.addChild(rupperForeArm);
        rupperForeArm.setTextureOffset(79, 127).addBox(-2.5F, -1.0F, -4.5F, 5.0F, 32.0F, 10.0F, 0.0F, true);

        rupperHand = new AdvancedModelBox(this);
        rupperHand.setRotationPoint(1.75F, 29.5F, 0.5F);
        rupperForeArm.addChild(rupperHand);
        rupperHand.setTextureOffset(0, 136).addBox(-2.5F, -1.5F, -7.0F, 5.0F, 19.0F, 14.0F, 0.0F, true);

        rupperFinger = new AdvancedModelBox(this);
        rupperFinger.setRotationPoint(-2.5F, 16.5F, -5.5F);
        rupperHand.addChild(rupperFinger);
        rupperFinger.setTextureOffset(0, 107).addBox(-4.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, true);
        rupperFinger.setTextureOffset(92, 62).addBox(-4.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, true);

        rupperThumb = new AdvancedModelBox(this);
        rupperThumb.setRotationPoint(-2.5F, -0.5F, -6.5F);
        rupperHand.addChild(rupperThumb);
        setRotateAngle(rupperThumb, -1.5708F, 0.0F, 0.0F);
        rupperThumb.setTextureOffset(0, 107).addBox(-4.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, true);
        rupperThumb.setTextureOffset(92, 62).addBox(-4.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, true);

        rupperThumb2 = new AdvancedModelBox(this);
        rupperThumb2.setRotationPoint(-2.5F, -0.5F, 6.5F);
        rupperHand.addChild(rupperThumb2);
        setRotateAngle(rupperThumb2, 1.5708F, 0.0F, 0.0F);
        rupperThumb2.setTextureOffset(0, 107).addBox(-4.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, true);
        rupperThumb2.setTextureOffset(92, 62).addBox(-4.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, true);

        rupperFinger2 = new AdvancedModelBox(this);
        rupperFinger2.setRotationPoint(-2.5F, 16.5F, 0.0F);
        rupperHand.addChild(rupperFinger2);
        rupperFinger2.setTextureOffset(0, 107).addBox(-4.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, true);
        rupperFinger2.setTextureOffset(92, 62).addBox(-4.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, true);

        rupperFinger3 = new AdvancedModelBox(this);
        rupperFinger3.setRotationPoint(-2.5F, 16.5F, 5.5F);
        rupperHand.addChild(rupperFinger3);
        rupperFinger3.setTextureOffset(0, 107).addBox(-4.0F, -1.0F, -2.0F, 6.0F, 11.0F, 4.0F, 0.0F, true);
        rupperFinger3.setTextureOffset(92, 62).addBox(-4.0F, 10.0F, -1.0F, 16.0F, 4.0F, 2.0F, 0.0F, true);

        hips = new AdvancedModelBox(this);
        hips.setRotationPoint(0.0F, -49.0F, 8.0F);
        root.addChild(hips);


        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
        hips.addChild(cube_r3);
        setRotateAngle(cube_r3, -0.3927F, 0.0F, 0.0F);
        cube_r3.setTextureOffset(0, 56).addBox(-7.0F, -3.0F, -2.0F, 14.0F, 22.0F, 29.0F, 0.0F, false);

        lthigh = new AdvancedModelBox(this);
        lthigh.setRotationPoint(6.0F, 17.0F, 17.0F);
        hips.addChild(lthigh);
        setRotateAngle(lthigh, -0.7854F, -0.7854F, -0.3927F);
        lthigh.setTextureOffset(39, 127).addBox(-2.0F, -3.0F, -5.0F, 7.0F, 22.0F, 13.0F, 0.01F, false);

        lcalf = new AdvancedModelBox(this);
        lcalf.setRotationPoint(1.5F, 19.25F, 7.5F);
        lthigh.addChild(lcalf);
        lcalf.setTextureOffset(0, 107).addBox(-2.5F, -8.25F, -2.5F, 5.0F, 8.0F, 21.0F, 0.0F, false);

        lfoot = new AdvancedModelBox(this);
        lfoot.setRotationPoint(0.0F, -2.25F, 17.75F);
        lcalf.addChild(lfoot);
        setRotateAngle(lfoot, -0.3491F, -0.3927F, 0.0F);
        lfoot.setTextureOffset(31, 107).addBox(-4.5F, -2.0F, -1.0F, 9.0F, 13.0F, 3.0F, 0.0F, false);

        ltoe = new AdvancedModelBox(this);
        ltoe.setRotationPoint(-4.0F, 10.0F, -1.0F);
        lfoot.addChild(ltoe);
        ltoe.setTextureOffset(57, 57).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 5.0F, 0.0F, false);

        ltoe2 = new AdvancedModelBox(this);
        ltoe2.setRotationPoint(4.0F, 10.0F, -1.0F);
        lfoot.addChild(ltoe2);
        ltoe2.setTextureOffset(57, 57).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 5.0F, 0.0F, true);

        ltoe3 = new AdvancedModelBox(this);
        ltoe3.setRotationPoint(0.0F, 10.0F, -1.0F);
        lfoot.addChild(ltoe3);
        ltoe3.setTextureOffset(57, 57).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 5.0F, 0.0F, false);

        rthigh = new AdvancedModelBox(this);
        rthigh.setRotationPoint(-6.0F, 17.0F, 17.0F);
        hips.addChild(rthigh);
        setRotateAngle(rthigh, -0.7854F, 0.7854F, 0.3927F);
        rthigh.setTextureOffset(39, 127).addBox(-5.0F, -3.0F, -5.0F, 7.0F, 22.0F, 13.0F, 0.01F, true);

        rcalf = new AdvancedModelBox(this);
        rcalf.setRotationPoint(-1.5F, 19.25F, 7.5F);
        rthigh.addChild(rcalf);
        rcalf.setTextureOffset(0, 107).addBox(-2.5F, -8.25F, -2.5F, 5.0F, 8.0F, 21.0F, 0.0F, true);

        rfoot = new AdvancedModelBox(this);
        rfoot.setRotationPoint(0.0F, -2.25F, 17.75F);
        rcalf.addChild(rfoot);
        setRotateAngle(rfoot, -0.3927F, 0.3927F, 0.0F);
        rfoot.setTextureOffset(31, 107).addBox(-4.5F, -2.0F, -1.0F, 9.0F, 13.0F, 3.0F, 0.0F, true);

        rtoe = new AdvancedModelBox(this);
        rtoe.setRotationPoint(4.0F, 10.0F, -1.0F);
        rfoot.addChild(rtoe);
        rtoe.setTextureOffset(57, 57).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 5.0F, 0.0F, true);

        rtoe2 = new AdvancedModelBox(this);
        rtoe2.setRotationPoint(-4.0F, 10.0F, -1.0F);
        rfoot.addChild(rtoe2);
        rtoe2.setTextureOffset(57, 57).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 5.0F, 0.0F, false);

        rtoe3 = new AdvancedModelBox(this);
        rtoe3.setRotationPoint(0.0F, 10.0F, -1.0F);
        rfoot.addChild(rtoe3);
        rtoe3.setTextureOffset(57, 57).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 5.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, 11.0F, 22.4224F);
        hips.addChild(tail);
        tail.setTextureOffset(109, 132).addBox(-2.0F, -2.0F, -2.4224F, 4.0F, 4.0F, 20.0F, 0.01F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setRotationPoint(0.0F, 0.0F, 17.0F);
        tail.addChild(tail2);
        tail2.setTextureOffset(130, 0).addBox(-1.0F, -1.0F, 0.5776F, 2.0F, 2.0F, 20.0F, 0.01F, false);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(0.0F, -44.0F, -14.0F);
        root.addChild(neck);
        neck.setTextureOffset(62, 83).addBox(-5.0F, -13.0F, -23.0F, 10.0F, 20.0F, 24.0F, 0.0F, false);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(0.0F, -13.0F, -3.0F);
        neck.addChild(cube_r4);
        setRotateAngle(cube_r4, -1.2217F, 0.0F, 0.0F);
        cube_r4.setTextureOffset(92, 68).addBox(-5.0F, -8.0F, 0.0F, 10.0F, 8.0F, 0.0F, 0.0F, false);

        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(0.0F, -13.0F, -13.0F);
        neck.addChild(cube_r5);
        setRotateAngle(cube_r5, -1.2217F, 0.0F, 0.0F);
        cube_r5.setTextureOffset(106, 98).addBox(-5.0F, -8.0F, 0.0F, 10.0F, 8.0F, 0.0F, 0.0F, false);

        cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(0.0F, -13.0F, -23.0F);
        neck.addChild(cube_r6);
        setRotateAngle(cube_r6, -1.2217F, 0.0F, 0.0F);
        cube_r6.setTextureOffset(66, 127).addBox(-5.0F, -8.0F, 0.0F, 10.0F, 8.0F, 0.0F, 0.0F, false);

        skull = new AdvancedModelBox(this);
        skull.setRotationPoint(0.0F, -7.0F, -18.0F);
        neck.addChild(skull);
        skull.setTextureOffset(114, 190).addBox(-5.5F, 7.0F, -21.5F, 11.0F, 6.0F, 23.0F, 0.01F, false);
        skull.setTextureOffset(106, 62).addBox(-6.0F, -2.0F, -22.0F, 12.0F, 12.0F, 24.0F, 0.01F, false);
        skull.setTextureOffset(232, 0).addBox(-6.0F, -10.0F, -22.5F, 12.0F, 14.0F, 0.0F, 0.01F, false);

        rBigear = new AdvancedModelBox(this);
        rBigear.setRotationPoint(-4.5F, -1.0F, 3.0F);
        skull.addChild(rBigear);
        setRotateAngle(rBigear, 0.0F, 0.0F, -0.7418F);
        rBigear.setTextureOffset(0, 0).addBox(-10.5F, -15.0F, -1.0F, 10.0F, 19.0F, 2.0F, 0.0F, false);
        rBigear.setTextureOffset(158, 7).addBox(-6.5F, -26.0F, -1.0F, 6.0F, 11.0F, 2.0F, 0.0F, false);

        lBigear = new AdvancedModelBox(this);
        lBigear.setRotationPoint(4.5F, -1.0F, 3.0F);
        skull.addChild(lBigear);
        setRotateAngle(lBigear, 0.0F, 0.0F, 0.7418F);
        lBigear.setTextureOffset(0, 0).addBox(0.5F, -15.0F, -1.0F, 10.0F, 19.0F, 2.0F, 0.0F, true);
        lBigear.setTextureOffset(158, 7).addBox(0.5F, -26.0F, -1.0F, 6.0F, 11.0F, 2.0F, 0.0F, true);

        rear = new AdvancedModelBox(this);
        rear.setRotationPoint(-4.5F, 0.5F, 4.5F);
        skull.addChild(rear);
        setRotateAngle(rear, 0.0F, 0.2182F, -0.1745F);
        rear.setTextureOffset(137, 132).addBox(-18.5F, -0.5F, -1.5F, 18.0F, 9.0F, 2.0F, 0.0F, false);
        rear.setTextureOffset(0, 78).addBox(-28.5F, -0.5F, -1.5F, 10.0F, 5.0F, 2.0F, 0.0F, false);

        lear = new AdvancedModelBox(this);
        lear.setRotationPoint(4.5F, 0.5F, 4.5F);
        skull.addChild(lear);
        setRotateAngle(lear, 0.0F, -0.2182F, 0.1745F);
        lear.setTextureOffset(137, 132).addBox(0.5F, -0.5F, -1.5F, 18.0F, 9.0F, 2.0F, 0.0F, true);
        lear.setTextureOffset(0, 78).addBox(18.5F, -0.5F, -1.5F, 10.0F, 5.0F, 2.0F, 0.0F, true);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, 9.5F, 0.0F);
        skull.addChild(jaw);
        jaw.setTextureOffset(106, 103).addBox(-6.0F, 0.5F, -22.0F, 12.0F, 5.0F, 24.0F, 0.0F, false);
        jaw.setTextureOffset(57, 62).addBox(-5.0F, -4.5F, -21.0F, 10.0F, 5.0F, 15.0F, 0.0F, false);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public void setupAnim(ForsakenEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float earTwitch = ACMath.smin((float) Math.sin(ageInTicks * 0.1F) + 0.5F, 0.0F, 0.3F);
        float jumpProgress = entity.getLeapProgress(partialTicks);
        float groundProgress = 1F - jumpProgress;
        float leftArmHoldAmount = (1F - entity.getRaisedLeftArmAmount(partialTicks));
        float rightArmHoldAmount = (1F - entity.getRaisedRightArmAmount(partialTicks));
        float lFingerPronateAmount = Math.max(jumpProgress, leftArmHoldAmount);
        float rFingerPronateAmount = Math.max(jumpProgress, rightArmHoldAmount);
        float runProgress = entity.getRunProgress(partialTicks);
        float runAmount = limbSwingAmount * groundProgress * runProgress;
        float walkAmount = limbSwingAmount * groundProgress * (1 - runProgress);
        float walkSpeed = 0.35F;
        float walkDegree = 0.8F;
        float runSpeed = 0.5F;
        float runDegree = 0.65F;
        float articulateLegScale = 1F - jumpProgress;
        if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            articulateLegScale *= setupAnimForAnimation(entity, entity.getAnimation(), limbSwing, limbSwingAmount, ageInTicks);
        }
        articulateLegs(entity.legSolver, articulateLegScale, partialTicks);
        animate(entity);
        progressRotationPrev(tail, 1F - limbSwingAmount, (float) Math.toRadians(-30), 0, 0, 1F);
        progressRotationPrev(lupperArm, leftArmHoldAmount, (float) Math.toRadians(-60), (float) Math.toRadians(-140), (float) Math.toRadians(70), 1F);
        progressRotationPrev(lupperForeArm, leftArmHoldAmount, (float) Math.toRadians(-20), (float) Math.toRadians(30), (float) Math.toRadians(100), 1F);
        progressRotationPrev(lupperHand, leftArmHoldAmount, (float) Math.toRadians(30), (float) Math.toRadians(30), (float) Math.toRadians(30), 1F);
        progressRotationPrev(lupperThumb, lFingerPronateAmount, (float) Math.toRadians(60), (float) Math.toRadians(30), 0, 1F);
        progressRotationPrev(lupperFinger, lFingerPronateAmount, 0, 0, (float) Math.toRadians(50), 1F);
        progressRotationPrev(lupperFinger2, lFingerPronateAmount, 0, 0, (float) Math.toRadians(50), 1F);
        progressRotationPrev(lupperFinger3, lFingerPronateAmount, 0, 0, (float) Math.toRadians(50), 1F);
        progressRotationPrev(lupperThumb2, lFingerPronateAmount, (float) Math.toRadians(-60), (float) Math.toRadians(30), 0, 1F);
        progressRotationPrev(rupperArm, rightArmHoldAmount, (float) Math.toRadians(-60), (float) Math.toRadians(140), (float) Math.toRadians(-70), 1F);
        progressRotationPrev(rupperForeArm, rightArmHoldAmount, (float) Math.toRadians(-20), (float) Math.toRadians(-30), (float) Math.toRadians(-100), 1F);
        progressRotationPrev(rupperHand, rightArmHoldAmount, (float) Math.toRadians(30), (float) Math.toRadians(-30), (float) Math.toRadians(-30), 1F);
        progressRotationPrev(rupperThumb, rFingerPronateAmount, (float) Math.toRadians(60), (float) Math.toRadians(-30), 0, 1F);
        progressRotationPrev(rupperFinger, rFingerPronateAmount, 0, 0, (float) Math.toRadians(-50), 1F);
        progressRotationPrev(rupperFinger2, rFingerPronateAmount, 0, 0, (float) Math.toRadians(-50), 1F);
        progressRotationPrev(rupperFinger3, rFingerPronateAmount, 0, 0, (float) Math.toRadians(-50), 1F);
        progressRotationPrev(rupperThumb2, rFingerPronateAmount, (float) Math.toRadians(-60), (float) Math.toRadians(-30), 0, 1F);
        progressRotationPrev(lthigh, limbSwingAmount, 0, (float) Math.toRadians(30), 0, 1F);
        progressRotationPrev(rthigh, limbSwingAmount, 0, (float) Math.toRadians(-30), 0, 1F);
        progressPositionPrev(root, jumpProgress, 0, -10, 0, 1F);
        progressRotationPrev(root, jumpProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(larm, jumpProgress, (float) Math.toRadians(20), (float) Math.toRadians(-30), 0, 1F);
        progressRotationPrev(lforeArm, jumpProgress, (float) Math.toRadians(-70), 0, 0, 1F);
        progressRotationPrev(lhand, jumpProgress, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(rarm, jumpProgress, (float) Math.toRadians(20), (float) Math.toRadians(30), 0, 1F);
        progressRotationPrev(rforeArm, jumpProgress, (float) Math.toRadians(-70), 0, 0, 1F);
        progressRotationPrev(rhand, jumpProgress, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(lthigh, jumpProgress, (float) Math.toRadians(20), (float) Math.toRadians(30), (float) Math.toRadians(10), 1F);
        progressRotationPrev(lcalf, jumpProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(lfoot, jumpProgress, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(rthigh, jumpProgress, (float) Math.toRadians(20), (float) Math.toRadians(-30), (float) Math.toRadians(-10), 1F);
        progressRotationPrev(rcalf, jumpProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(rfoot, jumpProgress, (float) Math.toRadians(20), 0, 0, 1F);
        this.walk(neck, 0.1F, 0.03F, true, 0F, 0F, ageInTicks, 1);
        this.walk(skull, 0.1F, 0.03F, true, 1F, 0F, ageInTicks, 1);
        this.walk(jaw, 0.1F, 0.1F, true, 2F, -0.1F, ageInTicks, 1);
        this.bob(neck, walkSpeed, walkDegree * 2, false, limbSwing, walkAmount);
        this.walk(neck, walkSpeed, walkDegree * 0.2F, true, 2F, -0.1F, limbSwing, walkAmount);
        this.walk(skull, walkSpeed, walkDegree * 0.2F, false, 1F, -0.1F, limbSwing, walkAmount);

        this.walk(lBigear, 3, 0.1F, true, 2F, -0.1F, ageInTicks, earTwitch);
        this.walk(rBigear, 3, 0.1F, true, 2F, -0.1F, ageInTicks, earTwitch);
        this.flap(lear, 2, 0.08F, true, 1F, -0.1F, ageInTicks, earTwitch);
        this.flap(rear, 2, 0.08F, true, 1F, -0.1F, ageInTicks, earTwitch);
        this.swing(tail, 0.1F, 0.05F, true, 0F, 0F, ageInTicks, 1);
        this.swing(tail2, 0.1F, 0.05F, true, -1F, 0F, ageInTicks, 1);
        this.swing(lupperArm, 0.1F, 0.1F, true, 0F, 0.2F, ageInTicks, leftArmHoldAmount);
        this.swing(lupperForeArm, 0.1F, 0.1F, true, 1F, 0F, ageInTicks, leftArmHoldAmount);
        this.flap(lupperThumb, 0.1F, 0.2F, true, 0F, 0.2F, ageInTicks, leftArmHoldAmount);
        this.flap(lupperFinger, 0.1F, 0.2F, true, 1F, 0.2F, ageInTicks, leftArmHoldAmount);
        this.flap(lupperFinger2, 0.1F, 0.2F, true, 2F, 0.2F, ageInTicks, leftArmHoldAmount);
        this.flap(lupperFinger3, 0.1F, 0.2F, true, 3F, 0.2F, ageInTicks, leftArmHoldAmount);
        this.flap(lupperThumb2, 0.1F, 0.2F, true, 4F, 0.2F, ageInTicks, leftArmHoldAmount);
        this.swing(rupperArm, 0.1F, 0.1F, true, 0F, -0.2F, ageInTicks, rightArmHoldAmount);
        this.swing(rupperForeArm, 0.1F, 0.1F, true, 1F, 0F, ageInTicks, rightArmHoldAmount);
        this.flap(rupperThumb, 0.1F, 0.2F, true, 0F, -0.2F, ageInTicks, rightArmHoldAmount);
        this.flap(rupperFinger, 0.1F, 0.2F, true, 1F, -0.2F, ageInTicks, rightArmHoldAmount);
        this.flap(rupperFinger2, 0.1F, 0.2F, true, 2F, -0.2F, ageInTicks, rightArmHoldAmount);
        this.flap(rupperFinger3, 0.1F, 0.2F, true, 3F, -0.2F, ageInTicks, rightArmHoldAmount);
        this.flap(rupperThumb2, 0.1F, 0.2F, true, 4F, -0.2F, ageInTicks, rightArmHoldAmount);

        float bodyWalkBob = -Math.abs(ACMath.walkValue(limbSwing, walkAmount, walkSpeed, 0, 9, false));
        this.chest.rotationPointY += bodyWalkBob;
        this.hips.rotationPointY += bodyWalkBob;
        this.rupperArm.rotationPointY += bodyWalkBob;
        this.lupperArm.rotationPointY += bodyWalkBob;
        this.lthigh.rotationPointY -= bodyWalkBob;
        this.rthigh.rotationPointY -= bodyWalkBob;
        this.walk(tail, walkSpeed, walkDegree * 0.6F, false, -3, -0.3F, limbSwing, walkAmount);
        this.walk(tail2, walkSpeed, walkDegree * 0.6F, false, -4F, 0.3F, limbSwing, walkAmount);

        this.walk(larm, walkSpeed, walkDegree * 0.6F, false, 1F, 0F, limbSwing, walkAmount);
        this.walk(lforeArm, walkSpeed, walkDegree * 0.2F, false, 1.5F, -0.1F, limbSwing, walkAmount);
        this.walk(lhand, walkSpeed, walkDegree * 1F, false, -3, 0F, limbSwing, walkAmount);
        larm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -0.5F, 9, true));
        larm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -1, 4, false));
        lhand.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -1, 4, false));
        this.walk(lfinger, walkSpeed, walkDegree * 0.3F, true, -2F, 0.4F, limbSwing, walkAmount);
        this.walk(lfinger2, walkSpeed, walkDegree * 0.3F, true, -2F, 0.4F, limbSwing, walkAmount);
        this.walk(lfinger3, walkSpeed, walkDegree * 0.3F, true, -2F, 0.4F, limbSwing, walkAmount);
        this.walk(lthumb, walkSpeed, walkDegree * 2F, true, -4, 0F, limbSwing, walkAmount);
        this.walk(lthumb2, walkSpeed, walkDegree * 2F, true, -4, 0F, limbSwing, walkAmount);

        this.walk(rarm, walkSpeed, walkDegree * 0.6F, true, 1F, 0F, limbSwing, walkAmount);
        this.walk(rforeArm, walkSpeed, walkDegree * 0.2F, true, 1.5F, 0.1F, limbSwing, walkAmount);
        this.walk(rhand, walkSpeed, walkDegree * 1F, true, -3, 0F, limbSwing, walkAmount);
        rarm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -0.5F, 9, false));
        rarm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -1, 5, true));
        rhand.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -1, 5, true));
        this.walk(rfinger, walkSpeed, walkDegree * 0.3F, false, -2F, -0.4F, limbSwing, walkAmount);
        this.walk(rfinger2, walkSpeed, walkDegree * 0.3F, false, -2F, -0.4F, limbSwing, walkAmount);
        this.walk(rfinger3, walkSpeed, walkDegree * 0.3F, false, -2F, -0.4F, limbSwing, walkAmount);
        this.walk(rthumb, walkSpeed, walkDegree * 2F, false, -4, 0F, limbSwing, walkAmount);
        this.walk(rthumb2, walkSpeed, walkDegree * 2F, false, -4, 0F, limbSwing, walkAmount);

        this.walk(lthigh, walkSpeed, walkDegree * 0.6F, true, 1F, -0.3F, limbSwing, walkAmount);
        this.walk(lcalf, walkSpeed, walkDegree * 0.6F, true, 1.5F, 0.1F, limbSwing, walkAmount);
        this.walk(lfoot, walkSpeed, walkDegree * 1F, true, 2F, 0F, limbSwing, walkAmount);
        this.swing(lfoot, walkSpeed, walkDegree * 0.4F, true, 2F, -0.4F, limbSwing, walkAmount);
        lthigh.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -0.5F, 18, false));
        lfoot.rotationPointZ += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, 2F, 8, false));
        lfoot.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, 2F, 8, false));
        this.walk(ltoe, walkSpeed, walkDegree * 1.4F, false, 3, -0.4F, limbSwing, walkAmount);
        this.walk(ltoe2, walkSpeed, walkDegree * 1.4F, false, 3, -0.4F, limbSwing, walkAmount);
        this.walk(ltoe3, walkSpeed, walkDegree * 1.4F, false, 3, -0.4F, limbSwing, walkAmount);

        this.walk(rthigh, walkSpeed, walkDegree * 0.6F, false, 1F, 0.3F, limbSwing, walkAmount);
        this.walk(rcalf, walkSpeed, walkDegree * 0.6F, false, 1.5F, -0.1F, limbSwing, walkAmount);
        this.walk(rfoot, walkSpeed, walkDegree * 1F, false, 2F, 0F, limbSwing, walkAmount);
        this.swing(rfoot, walkSpeed, walkDegree * 0.4F, false, 2F, -0.4F, limbSwing, walkAmount);
        rthigh.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, -0.5F, 18, true));
        rfoot.rotationPointZ += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, 2F, 8, true));
        rfoot.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, walkAmount, walkSpeed, 2F, 8, true));
        this.walk(rtoe, walkSpeed, walkDegree * 1.4F, true, 3, -0.4F, limbSwing, walkAmount);
        this.walk(rtoe2, walkSpeed, walkDegree * 1.4F, true, 3, -0.4F, limbSwing, walkAmount);
        this.walk(rtoe3, walkSpeed, walkDegree * 1.4F, true, 3, -0.4F, limbSwing, walkAmount);

        this.walk(root, runSpeed, runDegree * 0.1F, false, 0F, 0F, limbSwing, runAmount);
        float bodyRunBob = -Math.abs(ACMath.walkValue(limbSwing, runAmount, runSpeed, 0, 7, false));
        this.chest.rotationPointY += bodyRunBob;
        this.root.rotationPointZ -= ACMath.walkValue(limbSwing, runAmount, runSpeed, 2F, 7, false) + 7 * runAmount;
        this.hips.rotationPointY += bodyRunBob;
        this.rupperArm.rotationPointY += bodyRunBob;
        this.lupperArm.rotationPointY += bodyRunBob;
        this.walk(neck, runSpeed, runDegree * 0.2F, false, 1, 0.1F, limbSwing, runAmount);
        this.walk(skull, runSpeed, runDegree * 0.2F, true, 2, -0.1F, limbSwing, runAmount);
        this.walk(tail, runSpeed, runDegree * 0.2F, false, 2, -0.3F, limbSwing, runAmount);
        this.walk(tail2, runSpeed, runDegree * 0.2F, false, 1F, 0.3F, limbSwing, runAmount);
        this.swing(tail, runSpeed, runDegree * 0.4F, false, -1, 0F, limbSwing, runAmount);
        this.swing(tail2, runSpeed, runDegree * 0.4F, false, 0, 0F, limbSwing, runAmount);
        this.swing(lupperArm, runSpeed, runDegree * 0.2F, false, -1, -0.3F, limbSwing, runAmount);
        this.flap(lupperForeArm, runSpeed, runDegree * 0.1F, false, -2, 0.25F, limbSwing, runAmount);
        this.swing(rupperArm, runSpeed, runDegree * 0.2F, false, -1, 0.3F, limbSwing, runAmount);
        this.flap(rupperForeArm, runSpeed, runDegree * 0.1F, false, -2, -0.25F, limbSwing, runAmount);


        this.walk(larm, runSpeed, runDegree * 0.6F, false, 1F, 0F, limbSwing, runAmount);
        this.swing(larm, runSpeed, runDegree * 0.3F, false, 2F, -0.2F, limbSwing, runAmount);
        this.walk(lforeArm, runSpeed, runDegree * 0.2F, false, 1.5F, -0.1F, limbSwing, runAmount);
        this.walk(lhand, runSpeed, runDegree * 1F, false, -3, 0F, limbSwing, runAmount);
        larm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, -0.5F, 8, true));
        larm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, -1, 4, false));
        lhand.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, -1, 4, false));
        this.walk(lfinger, runSpeed, runDegree * 0.3F, true, -2F, 0.4F, limbSwing, runAmount);
        this.walk(lfinger2, runSpeed, runDegree * 0.3F, true, -2F, 0.4F, limbSwing, runAmount);
        this.walk(lfinger3, runSpeed, runDegree * 0.3F, true, -2F, 0.4F, limbSwing, runAmount);
        this.walk(lthumb, runSpeed, runDegree * 2F, true, -4, 0F, limbSwing, runAmount);
        this.walk(lthumb2, runSpeed, runDegree * 2F, true, -4, 0F, limbSwing, runAmount);

        this.walk(rarm, runSpeed, runDegree * 0.6F, false, 1F, 0F, limbSwing, runAmount);
        this.swing(rarm, runSpeed, runDegree * 0.3F, true, 2F, -0.2F, limbSwing, runAmount);
        this.walk(rforeArm, runSpeed, runDegree * 0.2F, false, 1.5F, -0.1F, limbSwing, runAmount);
        this.walk(rhand, runSpeed, runDegree * 1F, false, -3, 0F, limbSwing, runAmount);
        rarm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, -0.5F, 8, true));
        rarm.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, -1, 4, false));
        rhand.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, -1, 4, false));
        this.walk(rfinger, runSpeed, runDegree * 0.3F, true, -2F, 0.4F, limbSwing, runAmount);
        this.walk(rfinger2, runSpeed, runDegree * 0.3F, true, -2F, 0.4F, limbSwing, runAmount);
        this.walk(rfinger3, runSpeed, runDegree * 0.3F, true, -2F, 0.4F, limbSwing, runAmount);
        this.walk(rthumb, runSpeed, runDegree * 2F, true, -4, 0F, limbSwing, runAmount);
        this.walk(rthumb2, runSpeed, runDegree * 2F, true, -4, 0F, limbSwing, runAmount);

        this.walk(lthigh, runSpeed, runDegree * 0.6F, true, 2F, -0.3F, limbSwing, runAmount);
        this.walk(lcalf, runSpeed, runDegree * 0.6F, true, 2.5F, 0.1F, limbSwing, runAmount);
        this.walk(lfoot, runSpeed, runDegree * 1F, true, 3F, 0F, limbSwing, runAmount);
        this.swing(lfoot, runSpeed, runDegree * 0.4F, true, 3F, -0.4F, limbSwing, runAmount);
        lthigh.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, 0.5F, 10, false));
        lfoot.rotationPointZ += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, 3F, 8, false));
        lfoot.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, 3F, 8, false));
        this.walk(ltoe, runSpeed, runDegree * 1.4F, false, 4F, -0.4F, limbSwing, runAmount);
        this.walk(ltoe2, runSpeed, runDegree * 1.4F, false, 4F, -0.4F, limbSwing, runAmount);
        this.walk(ltoe3, runSpeed, runDegree * 1.4F, false, 4F, -0.4F, limbSwing, runAmount);

        this.walk(rthigh, runSpeed, runDegree * 0.6F, true, 2F, -0.3F, limbSwing, runAmount);
        this.walk(rcalf, runSpeed, runDegree * 0.6F, true, 2.5F, 0.1F, limbSwing, runAmount);
        this.walk(rfoot, runSpeed, runDegree * 1F, true, 3F, 0F, limbSwing, runAmount);
        this.swing(rfoot, runSpeed, runDegree * 0.4F, true, 3F, -0.4F, limbSwing, runAmount);
        rthigh.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, 0.5F, 10, false));
        rfoot.rotationPointZ += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, 3F, 8, false));
        rfoot.rotationPointY += Math.min(0, ACMath.walkValue(limbSwing, runAmount, runSpeed, 3F, 8, false));
        this.walk(rtoe, runSpeed, runDegree * 1.4F, false, 4F, -0.4F, limbSwing, runAmount);
        this.walk(rtoe2, runSpeed, runDegree * 1.4F, false, 4F, -0.4F, limbSwing, runAmount);
        this.walk(rtoe3, runSpeed, runDegree * 1.4F, false, 4F, -0.4F, limbSwing, runAmount);
        this.root.rotateAngleX += jumpProgress * (entity.getLeapPitch(partialTicks) * 0.6F) / 57.295776F;
        this.faceTarget(netHeadYaw, headPitch, 1, neck, skull);
    }

    private float setupAnimForAnimation(ForsakenEntity entity, Animation animation, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float partialTick = ageInTicks - entity.tickCount;
        float rootScale = 1F;
        float legMoveAmount = 1F;
        if (animation == ForsakenEntity.ANIMATION_SUMMON) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 3, animation, partialTick, 0);
            float animationProgress = (entity.getAnimationTick() + partialTick) / (float) animation.getDuration();
            rootScale = 0.5F + 0.5F * animationProgress;
            this.root.rotationPointY += (1F - animationProgress) * 25;
            legMoveAmount = 1F - animationIntensity;
        }
        if (entity.getAnimation() == ForsakenEntity.ANIMATION_SONIC_ATTACK) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 1, animation, partialTick, 5, 30);
            this.jaw.walk(2F, 0.1F, false, 1F, 0F, ageInTicks, animationIntensity);
            this.lBigear.walk(2F, 0.2F, false, 3F, 0F, ageInTicks, animationIntensity);
            this.rBigear.walk(2F, 0.2F, false, 3F, 0F, ageInTicks, animationIntensity);
            this.lear.walk(2F, 0.3F, false, 3F, 1F, ageInTicks, animationIntensity);
            this.rear.walk(2F, 0.3F, false, 3F, 1F, ageInTicks, animationIntensity);
        }
        if (entity.getAnimation() == ForsakenEntity.ANIMATION_SONIC_BLAST) {
            float animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 1, animation, partialTick, 5, 30);
            this.neck.flap(0.5F, 0.5F, false, 1F, 0F, ageInTicks, animationIntensity);
            this.neck.swing(0.5F, 0.5F, false, 2F, 0F, ageInTicks, animationIntensity);
            this.jaw.walk(2F, 0.1F, false, 1F, 0F, ageInTicks, animationIntensity);
            this.lBigear.walk(2F, 0.2F, false, 3F, 0F, ageInTicks, animationIntensity);
            this.rBigear.walk(2F, 0.2F, false, 3F, 0F, ageInTicks, animationIntensity);
            this.lear.walk(2F, 0.3F, false, 3F, 1F, ageInTicks, animationIntensity);
            this.rear.walk(2F, 0.3F, false, 3F, 1F, ageInTicks, animationIntensity);
        }
        this.root.setScale(rootScale, rootScale, rootScale);
        this.root.scaleChildren = true;
        return legMoveAmount;
    }

    public Vec3 getMouthPosition(Vec3 offsetIn) {
        PoseStack translationStack = new PoseStack();
        translationStack.pushPose();
        root.translateAndRotate(translationStack);
        neck.translateAndRotate(translationStack);
        skull.translateAndRotate(translationStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(translationStack.last().pose());
        Vec3 vec3 = new Vec3(-armOffsetVec.x(), -armOffsetVec.y(), armOffsetVec.z());
        translationStack.popPose();
        return vec3.add(0, 1F, -1F);
    }

    public Vec3 getHandPosition(boolean right, Vec3 offsetIn) {
        PoseStack translationStack = new PoseStack();
        translationStack.pushPose();
        root.translateAndRotate(translationStack);
        if(right){
            rupperArm.translateAndRotate(translationStack);
            rupperForeArm.translateAndRotate(translationStack);
            rupperHand.translateAndRotate(translationStack);
        }else{
            lupperArm.translateAndRotate(translationStack);
            lupperForeArm.translateAndRotate(translationStack);
            lupperHand.translateAndRotate(translationStack);
        }
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(translationStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), armOffsetVec.y(), armOffsetVec.z());
        translationStack.popPose();
        return vec3;
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, chest, cube_r1, cube_r2, larm, lforeArm, lhand, lfinger, lfinger2, lfinger3, lthumb, lthumb2, rarm, rforeArm, rhand, rfinger, rfinger2, rfinger3, rthumb, rthumb2, lupperArm, lupperForeArm, lupperHand, lupperFinger, lupperThumb, lupperThumb2, lupperFinger2, lupperFinger3, rupperArm, rupperForeArm, rupperHand, rupperFinger, rupperThumb, rupperThumb2, rupperFinger2, rupperFinger3, hips, cube_r3, lthigh, lcalf, lfoot, ltoe, ltoe2, ltoe3, rthigh, rcalf, rfoot, rtoe, rtoe2, rtoe3, tail, tail2, neck, cube_r4, cube_r5, cube_r6, skull, rBigear, lBigear, rear, lear, jaw);
    }

    private void articulateLegs(LegSolverQuadruped legs, float multiplier, float partialTick) {
        float heightBackLeft = legs.backLeft.getHeight(partialTick) * multiplier;
        float heightBackRight = legs.backRight.getHeight(partialTick) * multiplier;
        float heightFrontLeft = legs.frontLeft.getHeight(partialTick) * multiplier;
        float heightFrontRight = legs.frontRight.getHeight(partialTick) * multiplier;
        float max = Math.max(Math.max(heightBackLeft, heightBackRight), Math.max(heightFrontLeft, heightFrontRight));
        root.rotationPointY += max * 16;
        rarm.rotationPointY += (heightFrontRight - max) * 16 + (1F - heightFrontRight) * 7;
        rarm.rotateAngleX += (1F - heightFrontRight) * Math.toRadians(30F);
        rforeArm.rotateAngleX += (1F - heightFrontRight) * Math.toRadians(-60F);
        rhand.rotateAngleX += (1F - heightFrontRight) * Math.toRadians(30F);

        larm.rotationPointY += (heightFrontLeft - max) * 16 + (1F - heightFrontLeft) * 7;
        larm.rotateAngleX += (1F - heightFrontLeft) * Math.toRadians(30F);
        lforeArm.rotateAngleX += (1F - heightFrontLeft) * Math.toRadians(-60F);
        lhand.rotateAngleX += (1F - heightFrontLeft) * Math.toRadians(30F);

        rthigh.rotationPointY += (heightBackRight - max) * 16;
        rthigh.rotateAngleX += (heightBackRight) * Math.toRadians(30F);
        rthigh.rotateAngleZ += (heightBackRight) * Math.toRadians(-10F);
        rcalf.rotateAngleX += (heightBackRight) * Math.toRadians(-60F);
        rcalf.rotationPointY -= heightBackRight * 2;
        rcalf.rotationPointZ -= heightBackRight * 10;
        rfoot.rotateAngleX += (heightBackRight) * Math.toRadians(15F);

        lthigh.rotationPointY += (heightBackLeft - max) * 16;
        lthigh.rotateAngleX += (heightBackLeft) * Math.toRadians(30F);
        lthigh.rotateAngleZ += (heightBackLeft) * Math.toRadians(10F);
        lcalf.rotateAngleX += (heightBackLeft) * Math.toRadians(-60F);
        lcalf.rotationPointY -= heightBackLeft * 2;
        lcalf.rotationPointZ -= heightBackLeft * 10;
        lfoot.rotateAngleX += (heightBackLeft) * Math.toRadians(15F);
    }

    public void animate(IAnimatedEntity entity) {
        animator.update(entity);
        animator.setAnimation(ForsakenEntity.ANIMATION_PREPARE_JUMP);
        animator.startKeyframe(5);
        animator.move(root, 0, 10, 0);
        animator.move(larm, 0, 1, 1);
        animator.move(rarm, 0, 1, 1);
        animator.rotate(neck, (float) Math.toRadians(20), 0, 0);
        animator.rotate(skull, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(40), (float) Math.toRadians(-20), 0);
        animator.rotate(lforeArm, (float) Math.toRadians(-50), 0, 0);
        animator.rotate(lhand, (float) Math.toRadians(10), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(40), (float) Math.toRadians(20), 0);
        animator.rotate(rforeArm, (float) Math.toRadians(-50), 0, 0);
        animator.rotate(rhand, (float) Math.toRadians(10), 0, 0);
        animator.rotate(lthigh, (float) Math.toRadians(-40), (float) Math.toRadians(10), 0);
        animator.rotate(lcalf, (float) Math.toRadians(20), 0, 0);
        animator.rotate(lfoot, (float) Math.toRadians(10), 0, 0);
        animator.rotate(rthigh, (float) Math.toRadians(-40), (float) Math.toRadians(-10), 0);
        animator.rotate(rcalf, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rfoot, (float) Math.toRadians(10), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);
        animator.endKeyframe();
        animator.setAnimation(ForsakenEntity.ANIMATION_BITE);
        animator.startKeyframe(4);
        animator.move(chest, 0, -5, 0);
        animator.move(neck, 0, 0, 3);
        animator.rotate(chest, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(-20), 0, (float) Math.toRadians(-10));
        animator.rotate(skull, (float) Math.toRadians(30), 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.move(neck, 0, 5, 0);
        animator.rotate(neck, (float) Math.toRadians(10), 0, 0);
        animator.rotate(skull, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(70), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(3);
        animator.move(neck, 0, 5, 0);
        animator.move(jaw, 0, -1, 1);
        animator.rotate(jaw, (float) Math.toRadians(-10), 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(4);
        animator.setAnimation(ForsakenEntity.ANIMATION_LEFT_SLASH);
        animator.startKeyframe(10);
        animator.rotate(root, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(neck, 0, (float) Math.toRadians(15), 0);
        animator.rotate(lupperArm, (float) Math.toRadians(20), (float) Math.toRadians(-40), (float) Math.toRadians(20));
        animator.rotate(lupperForeArm, (float) Math.toRadians(-20), (float) Math.toRadians(-20), (float) Math.toRadians(-20));
        animator.rotate(lupperHand, (float) Math.toRadians(-40), 0, (float) Math.toRadians(-10));
        animator.rotate(lupperThumb, (float) Math.toRadians(65), (float) Math.toRadians(-35), 0);
        animator.rotate(lupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger2, 0, 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(35));
        animator.rotate(lupperThumb2, (float) Math.toRadians(-65), (float) Math.toRadians(-35), 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(neck, 0, 3, 3);
        animator.move(lupperArm, 2, 7, -14);
        animator.rotate(root, 0, (float) Math.toRadians(10), 0);
        animator.rotate(neck, 0, (float) Math.toRadians(15), 0);
        animator.rotate(lupperArm, (float) Math.toRadians(-30), 0, (float) Math.toRadians(30));
        animator.rotate(lupperForeArm, (float) Math.toRadians(-50), 0, (float) Math.toRadians(30));
        animator.rotate(lupperHand, (float) Math.toRadians(-20), 0, (float) Math.toRadians(-30));
        animator.rotate(lupperThumb, (float) Math.toRadians(65), (float) Math.toRadians(-55), 0);
        animator.rotate(lupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(55));
        animator.rotate(lupperFinger2, 0, 0, (float) Math.toRadians(55));
        animator.rotate(lupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(55));
        animator.rotate(lupperThumb2, (float) Math.toRadians(-65), (float) Math.toRadians(-55), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(15);
        animator.setAnimation(ForsakenEntity.ANIMATION_RIGHT_SLASH);
        animator.startKeyframe(10);
        animator.rotate(root, 0, (float) Math.toRadians(10), 0);
        animator.rotate(neck, 0, (float) Math.toRadians(-15), 0);
        animator.rotate(rupperArm, (float) Math.toRadians(20), (float) Math.toRadians(40), (float) Math.toRadians(-20));
        animator.rotate(rupperForeArm, (float) Math.toRadians(-20), (float) Math.toRadians(20), (float) Math.toRadians(20));
        animator.rotate(rupperHand, (float) Math.toRadians(-40), 0, (float) Math.toRadians(10));
        animator.rotate(rupperThumb, (float) Math.toRadians(65), (float) Math.toRadians(35), 0);
        animator.rotate(rupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger2, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(-35));
        animator.rotate(rupperThumb2, (float) Math.toRadians(-65), (float) Math.toRadians(35), 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(neck, 0, 3, 3);
        animator.move(rupperArm, -2, 7, -14);
        animator.rotate(root, 0, (float) Math.toRadians(-10), 0);
        animator.rotate(neck, 0, (float) Math.toRadians(-15), 0);
        animator.rotate(rupperArm, (float) Math.toRadians(-30), 0, (float) Math.toRadians(-30));
        animator.rotate(rupperForeArm, (float) Math.toRadians(-50), 0, (float) Math.toRadians(-30));
        animator.rotate(rupperHand, (float) Math.toRadians(-20), 0, (float) Math.toRadians(30));
        animator.rotate(rupperThumb, (float) Math.toRadians(65), (float) Math.toRadians(55), 0);
        animator.rotate(rupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-55));
        animator.rotate(rupperFinger2, 0, 0, (float) Math.toRadians(-55));
        animator.rotate(rupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(-55));
        animator.rotate(rupperThumb2, (float) Math.toRadians(-65), (float) Math.toRadians(55), 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(3);
        animator.resetKeyframe(15);
        animator.setAnimation(ForsakenEntity.ANIMATION_GROUND_SMASH);
        animator.startKeyframe(10);
        animator.rotate(neck, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(skull, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rupperArm, (float) Math.toRadians(20), (float) Math.toRadians(40), (float) Math.toRadians(20));
        animator.rotate(rupperForeArm, 0, 0, (float) Math.toRadians(20));
        animator.rotate(rupperHand, 0, 0, (float) Math.toRadians(20));
        animator.rotate(lupperArm, (float) Math.toRadians(20), (float) Math.toRadians(-40), (float) Math.toRadians(-20));
        animator.rotate(lupperForeArm, 0, 0, (float) Math.toRadians(-20));
        animator.rotate(lupperHand, 0, 0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.rotate(neck, (float) Math.toRadians(-40), 0, 0);
        animator.rotate(skull, (float) Math.toRadians(40), 0, 0);
        animator.move(chest, 0, 3, 0);
        animator.move(skull, 0, -1, -1);
        animator.move(rupperArm, -2, 12, -20);
        animator.rotate(rupperArm, (float) Math.toRadians(-60), (float) Math.toRadians(-30), (float) Math.toRadians(-40));
        animator.rotate(rupperForeArm, 0, (float) Math.toRadians(-30), (float) Math.toRadians(-30));
        animator.rotate(rupperHand, (float) Math.toRadians(20), (float) Math.toRadians(-10), (float) Math.toRadians(60));
        animator.rotate(rupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(25));
        animator.rotate(rupperFinger2, 0, 0, (float) Math.toRadians(25));
        animator.rotate(rupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(15));
        animator.rotate(rupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(20));
        animator.rotate(rupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(-20));
        animator.move(lupperArm, 2, 12, -20);
        animator.rotate(lupperArm, (float) Math.toRadians(-60), (float) Math.toRadians(30), (float) Math.toRadians(40));
        animator.rotate(lupperForeArm, 0, (float) Math.toRadians(30), (float) Math.toRadians(30));
        animator.rotate(lupperHand, (float) Math.toRadians(20), (float) Math.toRadians(10), (float) Math.toRadians(-60));
        animator.rotate(lupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-25));
        animator.rotate(lupperFinger2, 0, 0, (float) Math.toRadians(-25));
        animator.rotate(lupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(-15));
        animator.rotate(lupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(-20));
        animator.rotate(lupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(13);
        animator.setAnimation(ForsakenEntity.ANIMATION_SONIC_ATTACK);
        animator.startKeyframe(5);
        animator.move(neck, 0, 0, 3);
        animator.rotate(chest, (float) Math.toRadians(10), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(skull, (float) Math.toRadians(-30), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(neck, 0, 5, -3);
        animator.move(skull, 0, -2, -1);
        animator.rotate(neck, (float) Math.toRadians(10), 0, 0);
        animator.rotate(skull, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(70), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(15);
        animator.resetKeyframe(10);
        animator.setAnimation(ForsakenEntity.ANIMATION_SONIC_BLAST);
        animator.startKeyframe(5);
        animator.move(neck, 0, 0, 3);
        animator.rotate(chest, (float) Math.toRadians(10), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(-20), 0, 0);
        animator.rotate(skull, (float) Math.toRadians(-30), 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(neck, 0, -2, 2);
        animator.rotate(chest, (float) Math.toRadians(10), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(skull, (float) Math.toRadians(-30), 0, 0);
        animator.rotate(jaw, (float) Math.toRadians(70), 0, 0);
        animator.endKeyframe();
        animator.setStaticKeyframe(25);
        animator.resetKeyframe(10);
        animator.setAnimation(ForsakenEntity.ANIMATION_LEFT_PICKUP);
        animator.startKeyframe(10);
        animator.move(lupperArm, 2, 12, -20);
        animator.rotate(lupperArm, (float) Math.toRadians(-60), (float) Math.toRadians(30), (float) Math.toRadians(20));
        animator.rotate(lupperForeArm, 0, (float) Math.toRadians(20), (float) Math.toRadians(-30));
        animator.rotate(lupperHand, (float) Math.toRadians(-0), (float) Math.toRadians(-70), (float) Math.toRadians(-60));
        animator.rotate(lupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-25));
        animator.rotate(lupperFinger2, 0, 0, (float) Math.toRadians(-25));
        animator.rotate(lupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(-15));
        animator.rotate(lupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(-20));
        animator.rotate(lupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(lupperArm, 2, 12, -20);
        animator.rotate(lupperArm, (float) Math.toRadians(-70), (float) Math.toRadians(30), (float) Math.toRadians(20));
        animator.rotate(lupperForeArm, 0, (float) Math.toRadians(10), (float) Math.toRadians(-10));
        animator.rotate(lupperHand, (float) Math.toRadians(90), (float) Math.toRadians(-150), (float) Math.toRadians(-130));
        animator.rotate(lupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger2, 0, 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(35));
        animator.move(lupperThumb, -2, 0, 0);
        animator.move(lupperThumb2, -4, 0, 0);
        animator.rotate(lupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(-20));
        animator.rotate(lupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(10);
        animator.rotate(neck, (float) Math.toRadians(-30), (float) Math.toRadians(-10), (float) Math.toRadians(-10));
        animator.rotate(skull, (float) Math.toRadians(-20), (float) Math.toRadians(-20), (float) Math.toRadians(-10));
        animator.rotate(lupperArm, (float) Math.toRadians(-30), (float) Math.toRadians(-30), (float) Math.toRadians(-20));
        animator.rotate(lupperForeArm, (float) Math.toRadians(-10), (float) Math.toRadians(-10), (float) Math.toRadians(-20));
        animator.rotate(lupperHand, (float) Math.toRadians(30), (float) Math.toRadians(-30), (float) Math.toRadians(30));
        animator.rotate(lupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger2, 0, 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(35));
        animator.move(lupperThumb, -2, 0, 0);
        animator.move(lupperThumb2, -4, 0, 0);
        animator.rotate(lupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(-20));
        animator.rotate(lupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(5);
        animator.move(skull, 0, 2, 0);
        animator.rotate(neck, (float) Math.toRadians(-30), (float) Math.toRadians(-20), (float) Math.toRadians(-10));
        animator.rotate(skull, (float) Math.toRadians(-20), (float) Math.toRadians(-40), (float) Math.toRadians(-10));
        animator.rotate(jaw, (float) Math.toRadians(70), 0, 0);
        animator.rotate(lupperArm, (float) Math.toRadians(-50), (float) Math.toRadians(-10), (float) Math.toRadians(-20));
        animator.rotate(lupperForeArm, (float) Math.toRadians(-20), (float) Math.toRadians(-10), (float) Math.toRadians(-20));
        animator.rotate(lupperHand, (float) Math.toRadians(20), (float) Math.toRadians(-30), (float) Math.toRadians(30));
        animator.rotate(lupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger2, 0, 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(35));
        animator.move(lupperThumb, -2, 0, 0);
        animator.move(lupperThumb2, -4, 0, 0);
        animator.rotate(lupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(-20));
        animator.rotate(lupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(neck, (float) Math.toRadians(-30), (float) Math.toRadians(-20), (float) Math.toRadians(-10));
        animator.rotate(skull, (float) Math.toRadians(-20), (float) Math.toRadians(-40), (float) Math.toRadians(-10));
        animator.rotate(jaw, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(lupperArm, (float) Math.toRadians(-50), (float) Math.toRadians(-10), (float) Math.toRadians(-20));
        animator.rotate(lupperForeArm, (float) Math.toRadians(-20), (float) Math.toRadians(-10), (float) Math.toRadians(-20));
        animator.rotate(lupperHand, (float) Math.toRadians(20), (float) Math.toRadians(-30), (float) Math.toRadians(30));
        animator.rotate(lupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger2, 0, 0, (float) Math.toRadians(35));
        animator.rotate(lupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(35));
        animator.move(lupperThumb, -2, 0, 0);
        animator.move(lupperThumb2, -4, 0, 0);
        animator.rotate(lupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(-20));
        animator.rotate(lupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(10);
        animator.setAnimation(ForsakenEntity.ANIMATION_RIGHT_PICKUP);
        animator.startKeyframe(10);
        animator.move(rupperArm, -2, 12, -20);
        animator.rotate(rupperArm, (float) Math.toRadians(-60), (float) Math.toRadians(-30), (float) Math.toRadians(-20));
        animator.rotate(rupperForeArm, 0, (float) Math.toRadians(-20), (float) Math.toRadians(30));
        animator.rotate(rupperHand, (float) Math.toRadians(-0), (float) Math.toRadians(70), (float) Math.toRadians(60));
        animator.rotate(rupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(25));
        animator.rotate(rupperFinger2, 0, 0, (float) Math.toRadians(25));
        animator.rotate(rupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(15));
        animator.rotate(rupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(20));
        animator.rotate(rupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(-20));
        animator.endKeyframe();
        animator.startKeyframe(5);
        animator.move(rupperArm, -2, 12, -20);
        animator.rotate(rupperArm, (float) Math.toRadians(-70), (float) Math.toRadians(-30), (float) Math.toRadians(-20));
        animator.rotate(rupperForeArm, 0, (float) Math.toRadians(-10), (float) Math.toRadians(10));
        animator.rotate(rupperHand, (float) Math.toRadians(90), (float) Math.toRadians(150), (float) Math.toRadians(130));
        animator.rotate(rupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger2, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(-35));
        animator.move(rupperThumb, 2, 0, 0);
        animator.move(rupperThumb2, 4, 0, 0);
        animator.rotate(rupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(20));
        animator.rotate(rupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(10);
        animator.rotate(neck, (float) Math.toRadians(-30), (float) Math.toRadians(10), (float) Math.toRadians(10));
        animator.rotate(skull, (float) Math.toRadians(-20), (float) Math.toRadians(20), (float) Math.toRadians(10));
        animator.rotate(rupperArm, (float) Math.toRadians(-30), (float) Math.toRadians(30), (float) Math.toRadians(20));
        animator.rotate(rupperForeArm, (float) Math.toRadians(-10), (float) Math.toRadians(10), (float) Math.toRadians(20));
        animator.rotate(rupperHand, (float) Math.toRadians(30), (float) Math.toRadians(30), (float) Math.toRadians(-30));
        animator.rotate(rupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger2, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(-35));
        animator.move(rupperThumb, 2, 0, 0);
        animator.move(rupperThumb2, 4, 0, 0);
        animator.rotate(rupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(20));
        animator.rotate(rupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.startKeyframe(5);
        animator.move(skull, 0, 2, 0);
        animator.rotate(neck, (float) Math.toRadians(-30), (float) Math.toRadians(20), (float) Math.toRadians(10));
        animator.rotate(skull, (float) Math.toRadians(-20), (float) Math.toRadians(40), (float) Math.toRadians(10));
        animator.rotate(jaw, (float) Math.toRadians(70), 0, 0);
        animator.rotate(rupperArm, (float) Math.toRadians(-50), (float) Math.toRadians(10), (float) Math.toRadians(20));
        animator.rotate(rupperForeArm, (float) Math.toRadians(-20), (float) Math.toRadians(10), (float) Math.toRadians(20));
        animator.rotate(rupperHand, (float) Math.toRadians(20), (float) Math.toRadians(30), (float) Math.toRadians(-30));
        animator.rotate(rupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger2, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(-35));
        animator.move(rupperThumb, 2, 0, 0);
        animator.move(rupperThumb2, 4, 0, 0);
        animator.rotate(rupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(20));
        animator.rotate(rupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.startKeyframe(2);
        animator.rotate(neck, (float) Math.toRadians(-30), (float) Math.toRadians(20), (float) Math.toRadians(10));
        animator.rotate(skull, (float) Math.toRadians(-20), (float) Math.toRadians(40), (float) Math.toRadians(10));
        animator.rotate(jaw, (float) Math.toRadians(-10), 0, 0);
        animator.rotate(rupperArm, (float) Math.toRadians(-50), (float) Math.toRadians(10), (float) Math.toRadians(20));
        animator.rotate(rupperForeArm, (float) Math.toRadians(-20), (float) Math.toRadians(10), (float) Math.toRadians(20));
        animator.rotate(rupperHand, (float) Math.toRadians(20), (float) Math.toRadians(30), (float) Math.toRadians(-30));
        animator.rotate(rupperFinger, (float) Math.toRadians(-15), 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger2, 0, 0, (float) Math.toRadians(-35));
        animator.rotate(rupperFinger3, (float) Math.toRadians(15), 0, (float) Math.toRadians(-35));
        animator.move(rupperThumb, 2, 0, 0);
        animator.move(rupperThumb2, 4, 0, 0);
        animator.rotate(rupperThumb, (float) Math.toRadians(65), 0, (float) Math.toRadians(20));
        animator.rotate(rupperThumb2, (float) Math.toRadians(-65), 0, (float) Math.toRadians(20));
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(10);
        animator.setAnimation(ForsakenEntity.ANIMATION_SUMMON);
        animator.startKeyframe(0);
        animator.move(root, 0, -70, 0);
        animator.rotate(root, (float) Math.toRadians(-180), 0, 0);
        animator.rotate(neck, (float) Math.toRadians(65), 0, 0);
        animator.rotate(skull, (float) Math.toRadians(35), 0, 0);
        animator.rotate(tail, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(tail2, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(larm, (float) Math.toRadians(-25), (float) Math.toRadians(-35), 0);
        animator.rotate(lforeArm, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(lhand, (float) Math.toRadians(125), 0, 0);
        animator.rotate(rarm, (float) Math.toRadians(-25), (float) Math.toRadians(35), 0);
        animator.rotate(rforeArm, (float) Math.toRadians(-25), 0, 0);
        animator.rotate(rhand, (float) Math.toRadians(125), 0, 0);
        animator.rotate(rthigh, (float) Math.toRadians(-65), (float) Math.toRadians(15), (float) Math.toRadians(-15));
        animator.rotate(rcalf, (float) Math.toRadians(25), 0, 0);
        animator.rotate(rfoot, (float) Math.toRadians(95), 0, 0);
        animator.rotate(lthigh, (float) Math.toRadians(-65), (float) Math.toRadians(-15), (float) Math.toRadians(15));
        animator.rotate(lcalf, (float) Math.toRadians(25), 0, 0);
        animator.rotate(lfoot, (float) Math.toRadians(95), 0, 0);
        animator.rotate(rupperArm, (float) Math.toRadians(20), 0, 0);
        animator.rotate(rupperForeArm, (float) Math.toRadians(-40), 0, 0);
        animator.rotate(rupperHand, (float) Math.toRadians(20), (float) Math.toRadians(10), (float) Math.toRadians(-30));
        animator.rotate(lupperArm, (float) Math.toRadians(20), 0, 0);
        animator.rotate(lupperForeArm, (float) Math.toRadians(-40), 0, 0);
        animator.rotate(lupperHand, (float) Math.toRadians(20), (float) Math.toRadians(-10), (float) Math.toRadians(30));
        animator.endKeyframe();
        animator.setStaticKeyframe(10);
        animator.resetKeyframe(40);
    }

}