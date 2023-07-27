package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.TeletorEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class TeletorModel extends AdvancedEntityModel<TeletorEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rarmPivot;
    private final AdvancedModelBox larmPivot;
    private final AdvancedModelBox rlegcrossed;
    private final AdvancedModelBox llegcrossed;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox lleg;

    public TeletorModel() {
        texWidth = 128;
        texHeight = 128;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 24.0F, 0.0F);
        body.setTextureOffset(40, 37).addBox(-3.0F, -16.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -16.0F, 0.0F);
        body.addChild(head);
        head.setTextureOffset(0, 0).addBox(-6.0F, -18.0F, -6.0F, 12.0F, 17.0F, 12.0F, 0.0F, false);
        head.setTextureOffset(40, 29).addBox(6.0F, -9.0F, -2.0F, 9.0F, 4.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(20, 29).addBox(9.0F, -23.0F, -2.0F, 6.0F, 14.0F, 4.0F, 0.0F, false);
        head.setTextureOffset(40, 29).addBox(-15.0F, -9.0F, -2.0F, 9.0F, 4.0F, 4.0F, 0.0F, true);
        head.setTextureOffset(0, 29).addBox(-15.0F, -23.0F, -2.0F, 6.0F, 14.0F, 4.0F, 0.0F, false);

        rarmPivot = new AdvancedModelBox(this);
        rarmPivot.setRotationPoint(-3.0F, -16.0F, 0.0F);
        body.addChild(rarmPivot);

        rarm = new AdvancedModelBox(this);
        rarmPivot.addChild(rarm);
        rarm.setTextureOffset(34, 8).addBox(-9.0F, 0.0F, -1.0F, 9.0F, 0.0F, 2.0F, 0.0F, true);

        larmPivot = new AdvancedModelBox(this);
        larmPivot.setRotationPoint(3.0F, -16.0F, 0.0F);
        body.addChild(larmPivot);

        larm = new AdvancedModelBox(this);
        larmPivot.addChild(larm);
        larm.setTextureOffset(34, 8).addBox(0.0F, 0.0F, -1.0F, 9.0F, 0.0F, 2.0F, 0.0F, false);

        rlegcrossed = new AdvancedModelBox(this);
        rlegcrossed.setRotationPoint(-1.5F, -10.0F, 0.0F);
        body.addChild(rlegcrossed);
        rlegcrossed.setTextureOffset(0, 47).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 2.0F, 5.0F, 0.0F, true);

        llegcrossed = new AdvancedModelBox(this);
        llegcrossed.setRotationPoint(1.5F, -9.0F, -3.0F);
        body.addChild(llegcrossed);
        llegcrossed.setTextureOffset(0, 47).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 2.0F, 5.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-2.0F, -10.0F, 0.0F);
        body.addChild(rleg);
        rleg.setTextureOffset(4, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 0.0F, 0.0F, false);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(2.0F, -10.0F, 0.0F);
        body.addChild(lleg);
        lleg.setTextureOffset(4, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 0.0F, 0.0F, false);
        this.updateDefaultPose();

    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, head, larm, lleg, llegcrossed, rarm, rleg, rlegcrossed, rarmPivot, larmPivot);
    }

    @Override
    public void setupAnim(TeletorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        if (entity.areLegsCrossed(limbSwingAmount)) {
            this.lleg.showModel = false;
            this.rleg.showModel = false;
            this.llegcrossed.showModel = true;
            this.rlegcrossed.showModel = true;
        } else {
            this.lleg.showModel = true;
            this.rleg.showModel = true;
            this.llegcrossed.showModel = false;
            this.rlegcrossed.showModel = false;
        }
        float partialTick = ageInTicks - entity.tickCount;
        float controlProgress = entity.getControlProgress(partialTick);
        progressRotationPrev(body, limbSwingAmount, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(head, limbSwingAmount, (float) Math.toRadians(-10), 0, 0, 1F);
        progressRotationPrev(head, controlProgress * limbSwingAmount, (float) Math.toRadians(-10), 0, 0, 1F);
        progressRotationPrev(lleg, limbSwingAmount, (float) Math.toRadians(40), 0, 0, 1F);
        progressRotationPrev(rleg, limbSwingAmount, (float) Math.toRadians(40), 0, 0, 1F);
        progressRotationPrev(rarm, limbSwingAmount, (float) Math.toRadians(40), (float) Math.toRadians(40), 0, 1F);
        progressRotationPrev(larm, limbSwingAmount, (float) Math.toRadians(40), (float) Math.toRadians(-40), 0, 1F);
        progressRotationPrev(rarm, controlProgress * limbSwingAmount, (float) Math.toRadians(-60), (float) Math.toRadians(-40), (float) Math.toRadians(60), 1F);
        progressRotationPrev(larm, controlProgress * limbSwingAmount, (float) Math.toRadians(-60), (float) Math.toRadians(40), (float) Math.toRadians(-60), 1F);
        progressPositionPrev(body, limbSwingAmount, 0, 1, 2, 1F);
        progressPositionPrev(head, limbSwingAmount, 0, -1, -2, 1F);
        progressPositionPrev(rarm, limbSwingAmount, -1, 1, 0, 1F);
        progressPositionPrev(larm, limbSwingAmount, 1, 1, 0, 1F);
        progressPositionPrev(rarm, controlProgress, -1, 1, -2, 1F);
        progressPositionPrev(larm, controlProgress, 1, 1, -2, 1F);
        progressRotationPrev(rarm, controlProgress, (float) Math.toRadians(-10), (float) Math.toRadians(-90), 0, 1F);
        progressRotationPrev(larm, controlProgress, (float) Math.toRadians(-10), (float) Math.toRadians(90), 0, 1F);

        this.bob(body, 0.1F, 2, false, ageInTicks, 1);
        this.bob(head, 0.1F, 1, false, ageInTicks, 1);
        this.flap(larm, 0.1F, 0.2F, true, -1F, -0.4F, ageInTicks, 1);
        this.flap(rarm, 0.1F, 0.2F, false, -1F, -0.4F, ageInTicks, 1);
        this.swing(llegcrossed, 0.1F, 0.2F, false, -2F, 0F, ageInTicks, 1);
        this.swing(rlegcrossed, 0.1F, 0.2F, true, -2F, 0F, ageInTicks, 1);
        this.swing(rarm, 0.5F, 0.2F, false, 2F, 0F, ageInTicks, controlProgress);
        this.swing(larm, 0.5F, 0.2F, true, 2F, 0F, ageInTicks, controlProgress);
        rarm.rotationPointZ -= (float) (Math.sin(ageInTicks * 0.5F) * controlProgress);
        rarm.rotationPointX -= (float) (Math.sin(ageInTicks * 0.5F + 2F) * controlProgress * 0.5F);
        larm.rotationPointZ -= (float) (Math.sin(ageInTicks * 0.5F) * controlProgress);
        larm.rotationPointX += (float) (Math.sin(ageInTicks * 0.5F + 2F) * controlProgress * 0.5F);
        this.faceTarget(netHeadYaw, headPitch, 1, head);
        Entity look = entity.getWeapon();
        if (look != null) {
            Vec3 vector3d = look.getEyePosition(partialTick);
            Vec3 vector3d1 = entity.getEyePosition(partialTick);
            double d0 = Mth.clamp((vector3d.y - vector3d1.y) * 0.5F, -1F, 1F) * Math.PI / 2F;
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = (new Vec3(vector3d.x - vector3d1.x, 0.0D, vector3d.z - vector3d1.z)).normalize().yRot(((float) Math.PI / 2F));
            double d1 = vector3d2.dot(vector3d3);
            this.rarmPivot.rotateAngleX -= d0 * controlProgress;
            this.larmPivot.rotateAngleX -= d0 * controlProgress;
            this.rarmPivot.rotateAngleY += d1 * controlProgress;
            this.larmPivot.rotateAngleY += d1 * controlProgress;
            if (d0 > 0) {
                this.head.rotationPointY -= d0 * controlProgress * 5;
            }
        }
    }

    public Vec3 translateToHead(Vec3 in, float yawIn) {
        PoseStack modelTranslateStack = new PoseStack();
        modelTranslateStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawIn));
        modelTranslateStack.translate((double) (body.rotationPointX / 16.0F), (double) (body.rotationPointY / 16.0F), (double) (body.rotationPointZ / 16.0F));
        modelTranslateStack.mulPose(Axis.ZN.rotation(body.rotateAngleZ));
        modelTranslateStack.mulPose(Axis.YN.rotation(body.rotateAngleY));
        modelTranslateStack.mulPose(Axis.XN.rotation(body.rotateAngleX));
        modelTranslateStack.translate((double) (head.rotationPointX / 16.0F), (double) (head.rotationPointY / 16.0F), (double) (head.rotationPointZ / 16.0F));
        modelTranslateStack.mulPose(Axis.ZN.rotation(head.rotateAngleZ));
        modelTranslateStack.mulPose(Axis.YN.rotation(head.rotateAngleY));
        modelTranslateStack.mulPose(Axis.XN.rotation(head.rotateAngleX));

        Vector4f bodyOffsetVec = new Vector4f((float) in.x, (float) in.y, (float) in.z, 1.0F);
        bodyOffsetVec.mul(modelTranslateStack.last().pose());
        Vec3 offset = new Vec3(bodyOffsetVec.x(), bodyOffsetVec.y(), bodyOffsetVec.z());
        modelTranslateStack.popPose();
        return offset;
    }

}
