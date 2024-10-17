package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class NucleeperModel extends AdvancedEntityModel<NucleeperEntity> {
    private final AdvancedModelBox base;
    private final AdvancedModelBox coreTop;
    private final AdvancedModelBox head;
    private final AdvancedModelBox rpupil;
    private final AdvancedModelBox lpupil;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox rfoot2;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox lfoot2;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox lfoot1;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rfoot;
    private final AdvancedModelBox coreBottom;

    public NucleeperModel(float f) {
        texWidth = 128;
        texHeight = 128;
        base = new AdvancedModelBox(this);
        base.setRotationPoint(0.0F, 15.5F, 0.0F);
        base.setTextureOffset(80, 21).addBox(-7.0F, -36.5F, -5.0F, 14.0F, 38.0F, 10.0F, f, false);
        coreTop = new AdvancedModelBox(this);
        coreTop.setRotationPoint(0.0F, -62.5F, 0.0F);
        base.addChild(coreTop);
        coreTop.setTextureOffset(0, 0).addBox(-8.0F, 3.0F, -8.0F, 16.0F, 8.0F, 16.0F, f, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -37.5F, 0.0F);
        base.addChild(head);
        head.setTextureOffset(0, 48).addBox(-7.0F, -14.0F, -7.0F, 14.0F, 14.0F, 14.0F, f, false);
        head.setTextureOffset(26, 86).addBox(-6.0F, -13.5F, -6.0F, 12.0F, 12.0F, 12.0F, f, false);

        rpupil = new AdvancedModelBox(this);
        rpupil.setRotationPoint(-3.5F, -9.0F, -5.6F);
        head.addChild(rpupil);
        rpupil.setTextureOffset(26, 86).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, f, false);

        lpupil = new AdvancedModelBox(this);
        lpupil.setRotationPoint(3.5F, -9.0F, -5.6F);
        head.addChild(lpupil);
        lpupil.setTextureOffset(26, 86).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, f, true);

        rleg2 = new AdvancedModelBox(this);
        rleg2.setRotationPoint(-7.0F, -1.0F, 3.5F);
        base.addChild(rleg2);
        rleg2.setTextureOffset(80, 69).addBox(-2.0F, -2.5F, -3.5F, 6.0F, 5.0F, 12.0F, f, false);

        rfoot2 = new AdvancedModelBox(this);
        rfoot2.setRotationPoint(1.0F, -2.5F, 4.5F);
        rleg2.addChild(rfoot2);
        rfoot2.setTextureOffset(0, 93).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 12.0F, 5.0F, f, false);
        rfoot2.setTextureOffset(1, 28).addBox(0.0F, 0.0F, 5.0F, 0.0F, 6.0F, 3.0F, f, false);

        lleg2 = new AdvancedModelBox(this);
        lleg2.setRotationPoint(7.0F, -1.0F, 3.5F);
        base.addChild(lleg2);
        lleg2.setTextureOffset(80, 69).addBox(-4.0F, -2.5F, -3.5F, 6.0F, 5.0F, 12.0F, f, true);

        lfoot2 = new AdvancedModelBox(this);
        lfoot2.setRotationPoint(-1.0F, -2.5F, 4.5F);
        lleg2.addChild(lfoot2);
        lfoot2.setTextureOffset(0, 93).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 12.0F, 5.0F, f, true);
        lfoot2.setTextureOffset(1, 28).addBox(0.0F, 0.0F, 5.0F, 0.0F, 6.0F, 3.0F, f, true);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(7.0F, -1.0F, -3.5F);
        base.addChild(lleg);
        lleg.setTextureOffset(44, 69).addBox(-4.0F, -2.5F, -8.5F, 6.0F, 5.0F, 12.0F, f, true);

        lfoot1 = new AdvancedModelBox(this);
        lfoot1.setRotationPoint(-1.0F, -2.5F, -4.5F);
        lleg.addChild(lfoot1);
        lfoot1.setTextureOffset(0, 76).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 12.0F, 5.0F, f, true);
        lfoot1.setTextureOffset(0, 46).addBox(0.0F, 0.0F, -8.0F, 0.0F, 6.0F, 5.0F, f, true);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-7.0F, -1.0F, -3.5F);
        base.addChild(rleg);
        rleg.setTextureOffset(44, 69).addBox(-2.0F, -2.5F, -8.5F, 6.0F, 5.0F, 12.0F, f, false);

        rfoot = new AdvancedModelBox(this);
        rfoot.setRotationPoint(1.0F, -2.5F, -4.5F);
        rleg.addChild(rfoot);
        rfoot.setTextureOffset(0, 76).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 12.0F, 5.0F, f, false);
        rfoot.setTextureOffset(0, 46).addBox(0.0F, 0.0F, -8.0F, 0.0F, 6.0F, 5.0F, f, false);

        coreBottom = new AdvancedModelBox(this);
        coreBottom.setRotationPoint(0.0F, -27.5F, 0.0F);
        base.addChild(coreBottom);
        coreBottom.setTextureOffset(0, 24).addBox(-8.0F, -11.0F, -8.0F, 16.0F, 8.0F, 16.0F, f, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(base);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(base, coreBottom, coreTop, head, rpupil, lpupil, lleg, lleg2, rleg, rleg2, rfoot, rfoot2, lfoot2, lfoot1);
    }


    @Override
    public void setupAnim(NucleeperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 0.8F;
        float walkDegree = 1.2F;
        float partialTick = ageInTicks - entity.tickCount;
        float closeProgress = entity.getCloseProgress(partialTick);
        float explodeProgress = entity.getExplodeProgress(partialTick);
        float stillProgress = 1 - limbSwingAmount;
        progressPositionPrev(head, closeProgress, 0, 8, 0, 1F);
        progressPositionPrev(coreTop, closeProgress, 0, 14, 0, 1F);
        progressPositionPrev(coreBottom, closeProgress, 0, 1, 0, 1F);
        progressRotationPrev(lleg, stillProgress, 0, (float) Math.toRadians(-25), 0, 1F);
        progressRotationPrev(lleg2, stillProgress, 0, (float) Math.toRadians(25), 0, 1F);
        progressRotationPrev(rleg, stillProgress, 0, (float) Math.toRadians(25), 0, 1F);
        progressRotationPrev(rleg2, stillProgress, 0, (float) Math.toRadians(-25), 0, 1F);
        this.base.setScale(1F - explodeProgress * 0.15F, 1F - explodeProgress * 0.65F, 1F - explodeProgress * 0.15F);
        this.base.scaleChildren = true;
        this.flap(base, walkSpeed, walkDegree * 0.1F, true, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(lleg, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(rleg, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(lleg2, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.flap(rleg2, walkSpeed, walkDegree * 0.1F, false, 1F, 0F, limbSwing, limbSwingAmount);

        float bodyBob = walkValue(limbSwing, limbSwingAmount, walkSpeed * 1.5F, 0.5F, 2.4F, true);
        this.base.rotationPointY += bodyBob;
        this.walk(lleg, walkSpeed, walkDegree * 0.3F, false, 1F, -0.2F, limbSwing, limbSwingAmount);
        this.walk(lfoot1, walkSpeed, walkDegree * 0.2F, false, 3F, 0.2F, limbSwing, limbSwingAmount);
        lleg.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 5F, true)) - bodyBob;
        lleg.rotationPointZ += walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 2.5F, true);

        this.walk(rleg, walkSpeed, walkDegree * 0.3F, true, 1F, 0.2F, limbSwing, limbSwingAmount);
        this.walk(rfoot, walkSpeed, walkDegree * 0.2F, true, 3F, -0.2F, limbSwing, limbSwingAmount);
        rleg.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 5F, false)) - bodyBob;
        rleg.rotationPointZ += walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 2.5F, false);
        this.walk(rleg2, walkSpeed, walkDegree * 0.3F, false, 0F, 0.2F, limbSwing, limbSwingAmount);
        this.walk(rfoot2, walkSpeed, walkDegree * 0.2F, false, 2F, -0.2F, limbSwing, limbSwingAmount);
        rleg2.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 5F, true)) - bodyBob;
        rleg2.rotationPointZ += walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 2.5F, true);
        this.walk(lleg2, walkSpeed, walkDegree * 0.3F, true, 0F, -0.2F, limbSwing, limbSwingAmount);
        this.walk(lfoot2, walkSpeed, walkDegree * 0.2F, true, 2F, 0.2F, limbSwing, limbSwingAmount);
        lleg2.rotationPointY += Math.min(0, walkValue(limbSwing, limbSwingAmount, walkSpeed, -0.5F, 5F, false)) - bodyBob;
        lleg2.rotationPointZ += walkValue(limbSwing, limbSwingAmount, walkSpeed, -1.5F, 2.5F, false);
        this.flap(base, 3F, 0.3F, true, 1F, 0F, ageInTicks, explodeProgress);

        Entity look = Minecraft.getInstance().getCameraEntity();
        if (look != null) {
            Vec3 vector3d = look.getEyePosition(0.0F);
            Vec3 vector3d1 = entity.getEyePosition(0.0F);
            double d0 = vector3d.y - vector3d1.y;
            float f1 = (float) Mth.clamp(-d0, -1.0F, 1.0F);
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = (new Vec3(vector3d1.x - vector3d.x, 0.0D, vector3d1.z - vector3d.z)).normalize().yRot(((float) Math.PI / 2F));
            double d1 = vector3d2.dot(vector3d3);
            double d2 = Mth.sqrt((float) Math.abs(d1)) * (float) Math.signum(d1);
            this.lpupil.rotationPointX += d2 - this.base.rotateAngleZ;
            this.lpupil.rotationPointY += f1;
            this.rpupil.rotationPointX += d2 - this.base.rotateAngleZ;
            this.rpupil.rotationPointY += f1;
        }
    }

    public Vec3 getSirenPosition(Vec3 offsetIn) {
        PoseStack armStack = new PoseStack();
        armStack.pushPose();
        base.translateAndRotate(armStack);
        head.translateAndRotate(armStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(armStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), -armOffsetVec.y(), armOffsetVec.z());
        armStack.popPose();
        return vec3.add(0, 1.5F, 0);
    }

    private float walkValue(float limbSwing, float limbSwingAmount, float speed, float offset, float degree, boolean inverse) {
        return (float) ((Math.cos(limbSwing * speed + offset) * degree * limbSwingAmount) * (inverse ? -1 : 1));
    }
}