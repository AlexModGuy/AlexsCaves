package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.BoundroidWinchEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class BoundroidWinchModel extends AdvancedEntityModel<BoundroidWinchEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox coil;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox rarm;

    public BoundroidWinchModel() {
        texWidth = 64;
        texHeight = 64;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 18.0F, 0.0F);
        body.setTextureOffset(20, 41).addBox(4.0F, -1.5F, -3.0F, 2.0F, 7.0F, 6.0F, 0.0F, false);
        body.setTextureOffset(36, 41).addBox(-6.0F, -1.5F, -3.0F, 2.0F, 7.0F, 6.0F, 0.0F, false);
        body.setTextureOffset(0, 0).addBox(-6.0F, -10.5F, -6.0F, 12.0F, 9.0F, 12.0F, 0.0F, false);

        coil = new AdvancedModelBox(this);
        coil.setRotationPoint(0.0F, 2.5F, 0.0F);
        body.addChild(coil);
        coil.setTextureOffset(0, 21).addBox(-4.5F, -3.5F, -3.5F, 9.0F, 7.0F, 7.0F, 0.0F, false);
        coil.setTextureOffset(26, 29).addBox(-4.0F, -3.0F, -3.0F, 8.0F, 6.0F, 6.0F, 0.0F, false);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(5.0F, -7.5F, 5.0F);
        body.addChild(lleg);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(1.5F, -4.0F, 1.5F);
        lleg.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, -1.5708F, 0.0F);
        cube_r1.setTextureOffset(0, 35).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 10.0F, 5.0F, 0.0F, false);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(5.0F, -7.5F, -5.0F);
        body.addChild(larm);
        larm.setTextureOffset(0, 35).addBox(-1.0F, -9.0F, -4.0F, 5.0F, 10.0F, 5.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-5.0F, -7.5F, 5.0F);
        body.addChild(rleg);


        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(-1.5F, -4.0F, 1.5F);
        rleg.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 1.5708F, 0.0F);
        cube_r2.setTextureOffset(0, 35).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 10.0F, 5.0F, 0.0F, true);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(-5.0F, -7.5F, -5.0F);
        body.addChild(rarm);
        rarm.setTextureOffset(0, 35).addBox(-4.0F, -9.0F, -4.0F, 5.0F, 10.0F, 5.0F, 0.0F, true);
        this.updateDefaultPose();
    }


    public Vec3 getChainPosition(Vec3 offsetIn) {
        PoseStack armStack = new PoseStack();
        armStack.pushPose();
        body.translateAndRotate(armStack);
        coil.translateAndRotate(armStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(armStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), -armOffsetVec.y(), armOffsetVec.z());
        armStack.popPose();
        return vec3.add(0, 1.5F, 0);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, coil, rarm, rleg, larm, lleg, cube_r1, cube_r2);
    }

    @Override
    public void setupAnim(BoundroidWinchEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = ageInTicks - entity.tickCount;
        float chainLength = entity.getChainLength(partialTick);
        float bodyYaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTick;
        float walkSpeed = 0.8F;
        float walkDegree = 1.3F;
        float moveSideways = (float) (entity.getX() - entity.xo) * 3.0F;
        float moveForwards = (float) (entity.getZ() - entity.zo) * 3.0F;
        float onGroundAmount = 1F - entity.getLatchProgress(partialTick);
        this.coil.rotateAngleX = (float) Math.toRadians(chainLength * 260);
        this.body.rotateAngleX += onGroundAmount * Math.PI;
        this.body.rotationPointY -= onGroundAmount * 11;
        this.walk(larm, walkSpeed, walkDegree, true, 1F, 0F, ageInTicks, moveForwards);
        this.walk(rarm, walkSpeed, walkDegree, false, 1F, 0F, ageInTicks, moveForwards);
        this.walk(lleg, walkSpeed, walkDegree, true, -1F, 0F, ageInTicks, moveForwards);
        this.walk(rleg, walkSpeed, walkDegree, false, -1F, 0F, ageInTicks, moveForwards);
        this.flap(larm, walkSpeed, walkDegree, true, 1F, 0F, ageInTicks, moveSideways);
        this.flap(rarm, walkSpeed, walkDegree, false, 1F, 0F, ageInTicks, moveSideways);
        this.flap(lleg, walkSpeed, walkDegree, true, -1F, 0F, ageInTicks, moveSideways);
        this.flap(rleg, walkSpeed, walkDegree, false, -1F, 0F, ageInTicks, moveSideways);
    }

}