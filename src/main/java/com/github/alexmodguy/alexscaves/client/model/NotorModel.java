package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.NotorEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class NotorModel extends AdvancedEntityModel<NotorEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox propeller;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox blades;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox lleg;

    public NotorModel() {
        texWidth = 64;
        texHeight = 64;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 20.0F, 0.0F);
        body.setTextureOffset(0, 4).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        propeller = new AdvancedModelBox(this);
        propeller.setRotationPoint(0.0F, -2.0F, 0.0F);
        body.addChild(propeller);
        propeller.setTextureOffset(0, 0).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 3.0F, 0.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, -1.0F, 0.0F);
        propeller.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, -1.5708F, 0.0F);
        cube_r1.setTextureOffset(0, 0).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 3.0F, 0.0F, 0.0F, false);

        blades = new AdvancedModelBox(this);
        blades.setRotationPoint(0.0F, -1.0F, 0.0F);
        propeller.addChild(blades);
        blades.setTextureOffset(0, 0).addBox(-9.0F, 0.0F, -2.0F, 18.0F, 0.0F, 4.0F, 0.0F, false);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(-1.5F, 2.0F, 2.0F);
        body.addChild(larm);
        larm.setTextureOffset(4, 12).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(1.5F, 2.0F, 2.0F);
        body.addChild(rarm);
        rarm.setTextureOffset(4, 12).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(1.5F, 2.0F, -2.0F);
        body.addChild(rleg);
        rleg.setTextureOffset(4, 12).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, false);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(-1.5F, 2.0F, -2.0F);
        body.addChild(lleg);
        lleg.setTextureOffset(4, 12).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, propeller, cube_r1, blades, rarm, larm, rleg, lleg);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public void setupAnim(NotorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = ageInTicks - entity.tickCount;
        float landProgress = entity.getGroundProgress(partialTick);
        float flyProgress = 1F - landProgress;
        float bodyYaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTick;
        float bodyIdleBob = Math.abs(ACMath.walkValue(ageInTicks, landProgress, 0.1F, 0F, 1F, false));
        float bodyFlyBob = ACMath.walkValue(ageInTicks, flyProgress, 0.2F, 1F, 1.2F, false) - 1.2F * flyProgress;

        this.body.rotationPointY += bodyIdleBob + bodyFlyBob;
        this.larm.rotationPointY -= bodyIdleBob * 0.8F;
        this.rarm.rotationPointY -= bodyIdleBob * 0.8F;
        this.lleg.rotationPointY -= bodyIdleBob * 0.8F;
        this.rleg.rotationPointY -= bodyIdleBob * 0.8F;
        this.flap(propeller, 0.2F, 0.1F, false, 1F, 0F, ageInTicks, landProgress);
        this.walk(larm, 0.2F, 0.3F, false, 0F, 0.2F, ageInTicks, landProgress);
        this.walk(rarm, 0.2F, 0.3F, false, 0F, 0.2F, ageInTicks, landProgress);
        this.walk(lleg, 0.2F, 0.3F, true, 0F, 0.2F, ageInTicks, landProgress);
        this.walk(rleg, 0.2F, 0.3F, true, 0F, 0.2F, ageInTicks, landProgress);
        this.walk(larm, 0.2F, 0.3F, false, 1F, 0.2F, ageInTicks, flyProgress);
        this.walk(rarm, 0.2F, 0.3F, false, 1F, 0.2F, ageInTicks, flyProgress);
        this.walk(lleg, 0.2F, 0.3F, false, 2F, 0.2F, ageInTicks, flyProgress);
        this.walk(rleg, 0.2F, 0.3F, false, 2F, 0.2F, ageInTicks, flyProgress);
        propeller.rotateAngleY += (float) Math.toRadians(entity.getPropellerAngle(partialTick) - (bodyYaw * flyProgress));
        float flyForwards = limbSwingAmount * flyProgress;
        this.walk(body, 0.2F, 0.1F, false, 2F, 0.2F, limbSwing, flyForwards);
        this.flap(body, 0.4F, 0.05F, false, -1F, 0F, ageInTicks, flyProgress);
        this.walk(propeller, 0.2F, 0.1F, true, 2F, 0.2F, limbSwing, flyForwards);
    }


    public Vec3 getChainPosition(Vec3 offsetIn){
        PoseStack armStack = new PoseStack();
        armStack.pushPose();
        body.translateAndRotate(armStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(armStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), -armOffsetVec.y(), armOffsetVec.z());
        armStack.popPose();
        return vec3.add(0, 1.5F, 0);
    }

}