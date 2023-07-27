package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SubmarineModel extends AdvancedEntityModel<SubmarineEntity> {
    private final AdvancedModelBox hull;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox leftpropeller;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rightpropeller;
    private final AdvancedModelBox llever;
    private final AdvancedModelBox rlever;
    private final AdvancedModelBox watermask;
    private final AdvancedModelBox motor;
    private final AdvancedModelBox backpropeller;
    private final AdvancedModelBox periscope;
    private final AdvancedModelBox seat;

    public SubmarineModel() {
        texWidth = 512;
        texHeight = 512;

        hull = new AdvancedModelBox(this);
        hull.setRotationPoint(0.0F, -10.0F, 0.0F);
        hull.setTextureOffset(103, 0).addBox(-8.0F, -26.0F, 7.0F, 16.0F, 6.0F, 16.0F, 0.0F, false);
        hull.setTextureOffset(0, 66).addBox(-20.0F, -22.0F, -7.0F, 40.0F, 46.0F, 32.0F, 0.0F, false);
        hull.setTextureOffset(152, 161).addBox(13.0F, 8.0F, -30.0F, 7.0F, 16.0F, 23.0F, 0.0F, false);
        hull.setTextureOffset(92, 161).addBox(-20.0F, 8.0F, -30.0F, 7.0F, 16.0F, 23.0F, 0.0F, false);
        hull.setTextureOffset(154, 0).addBox(-18.0F, -20.0F, -35.0F, 36.0F, 28.0F, 28.0F, 0.0F, false);
        hull.setTextureOffset(112, 66).addBox(-20.0F, 8.0F, -37.0F, 40.0F, 16.0F, 7.0F, 0.0F, false);
        hull.setTextureOffset(251, 189).addBox(-6.0F, 8.0F, -30.0F, 12.0F, 16.0F, 6.0F, 0.0F, false);
        hull.setTextureOffset(0, 0).addBox(-13.0F, 15.0F, -30.0F, 26.0F, 15.0F, 51.0F, 0.0F, false);

        rarm = new AdvancedModelBox(this);
        rarm.setRotationPoint(22.5F, 25.5F, -7.5F);
        hull.addChild(rarm);
        rarm.setTextureOffset(255, 91).addBox(-9.5F, -9.5F, -43.5F, 19.0F, 19.0F, 51.0F, 0.0F, false);

        leftpropeller = new AdvancedModelBox(this);
        leftpropeller.setRotationPoint(0.0F, 0.0F, -43.5F);
        rarm.addChild(leftpropeller);
        leftpropeller.setTextureOffset(189, 165).addBox(-9.5F, -9.5F, -3.0F, 19.0F, 19.0F, 0.0F, 0.0F, false);
        leftpropeller.setTextureOffset(103, 22).addBox(-2.5F, -2.5F, -6.0F, 5.0F, 5.0F, 6.0F, 0.0F, false);

        larm = new AdvancedModelBox(this);
        larm.setRotationPoint(-22.5F, 25.5F, -25.5F);
        hull.addChild(larm);
        larm.setTextureOffset(255, 91).addBox(-9.5F, -9.5F, -25.5F, 19.0F, 19.0F, 51.0F, 0.0F, true);

        rightpropeller = new AdvancedModelBox(this);
        rightpropeller.setRotationPoint(0.0F, 0.0F, -25.5F);
        larm.addChild(rightpropeller);
        rightpropeller.setTextureOffset(189, 165).addBox(-9.5F, -9.5F, -3.0F, 19.0F, 19.0F, 0.0F, 0.0F, false);
        rightpropeller.setTextureOffset(103, 22).addBox(-2.5F, -2.5F, -6.0F, 5.0F, 5.0F, 6.0F, 0.0F, false);

        llever = new AdvancedModelBox(this);
        llever.setRotationPoint(-7.0F, 15.25F, -16.0F);
        hull.addChild(llever);
        llever.setTextureOffset(265, 72).addBox(-1.0F, -11.25F, -1.0F, 2.0F, 16.0F, 2.0F, 0.0F, true);

        rlever = new AdvancedModelBox(this);
        rlever.setRotationPoint(7.0F, 15.0F, -16.0F);
        hull.addChild(rlever);
        rlever.setTextureOffset(265, 72).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 16.0F, 2.0F, 0.0F, true);

        watermask = new AdvancedModelBox(this);
        watermask.setRotationPoint(0.0F, 5.0F, 0.0F);
        hull.addChild(watermask);
        watermask.setTextureOffset(0, 214).addBox(-18.0F, 0.0F, -35.0F, 36.0F, 52.0F, 28.0F, 0.0F, false);

        motor = new AdvancedModelBox(this);
        motor.setRotationPoint(0.0F, 1.0F, 25.0F);
        hull.addChild(motor);
        motor.setTextureOffset(0, 0).addBox(-9.0F, -8.0F, 0.0F, 18.0F, 18.0F, 7.0F, 0.0F, false);
        motor.setTextureOffset(153, 211).addBox(-14.0F, -13.0F, 7.0F, 28.0F, 28.0F, 18.0F, 0.0F, false);

        backpropeller = new AdvancedModelBox(this);
        backpropeller.setRotationPoint(0.0F, 1.0F, 7.0F);
        motor.addChild(backpropeller);
        backpropeller.setTextureOffset(0, 25).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 12.0F, 0.0F, false);
        backpropeller.setTextureOffset(0, 188).addBox(-13.0F, -13.0F, 9.0F, 26.0F, 26.0F, 0.0F, 0.0F, false);

        periscope = new AdvancedModelBox(this);
        periscope.setRotationPoint(0.0F, -22.0F, 3.0F);
        hull.addChild(periscope);
        periscope.setTextureOffset(0, 66).addBox(-3.0F, -20.0F, -3.0F, 6.0F, 20.0F, 6.0F, 0.0F, false);
        periscope.setTextureOffset(199, 59).addBox(-3.0F, -20.0F, -10.0F, 6.0F, 6.0F, 7.0F, 0.25F, false);
        periscope.setTextureOffset(103, 35).addBox(-3.0F, -20.0F, -10.0F, 6.0F, 6.0F, 7.0F, 0.0F, false);

        seat = new AdvancedModelBox(this);
        seat.setRotationPoint(0.0F, 13.5F, -11.0F);
        hull.addChild(seat);
        seat.setTextureOffset(165, 93).addBox(-4.0F, -1.5F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(hull);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(hull, rarm, larm, seat, leftpropeller, rightpropeller, llever, rlever, watermask, motor, backpropeller, periscope);
    }

    public AdvancedModelBox getWaterMask() {
        return watermask;
    }

    @Override
    public void setupAnim(SubmarineEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float leftPropellerRot = Mth.wrapDegrees(entity.getLeftPropellerRot(partialTicks));
        float rightPropellerRot = Mth.wrapDegrees(entity.getRightPropellerRot(partialTicks));
        float backPropellerRot = Mth.wrapDegrees(entity.getBackPropellerRot(partialTicks));
        float shake = Math.max(entity.shakeTime - partialTicks, 0) / 10F;
        Entity controllingPlayer = entity.getFirstPassenger();
        this.rightpropeller.rotateAngleZ += Math.toRadians(leftPropellerRot);
        this.leftpropeller.rotateAngleZ += Math.toRadians(rightPropellerRot);
        this.backpropeller.rotateAngleZ += Math.toRadians(backPropellerRot);
        if (controllingPlayer instanceof LivingEntity living) {
            float subYaw = 180 - entity.getViewYRot(partialTicks);
            float headYaw = 180 + (living.yHeadRotO + (living.getYHeadRot() - living.yHeadRotO) * partialTicks);
            this.periscope.rotateAngleY += Math.toRadians(subYaw + headYaw);
        }
        this.hull.rotateAngleX += Math.sin(ageInTicks * 0.7F + 1.0F) * shake * 0.05F;
        this.hull.rotateAngleZ += Math.sin(ageInTicks * 0.7F) * shake * 0.1F;
        if (entity.getDamageLevel() < 4) {
            this.rarm.showModel = true;
        } else {
            this.hull.rotateAngleZ += Math.toRadians(10);
            this.rarm.showModel = false;
        }
    }

    public void setupWaterMask(SubmarineEntity entity, float partialTicks) {
        float xRot = (float) Math.toRadians(-entity.getViewXRot(partialTicks));
        Vec3 vec3 = new Vec3(0F, entity.getWaterHeight(), 0F).xRot(xRot);
        /*float waterLevel = (float) vec3.y;
        if(waterLevel >= 1.2F && waterLevel < 3.0F){
            if(xRot < 0){
                waterLevel += (xRot / 60F) * 3F;
            }
            float f = (waterLevel - 1.2F) / (3.0F);
            watermask.showModel = true;
            watermask.setScale(1F, -f - 0.1F, 1F);
        }else{
            watermask.showModel = false;
        }*/
        watermask.rotateAngleX = hull.rotateAngleX;
        watermask.rotateAngleZ = hull.rotateAngleZ;
        watermask.rotationPointY -= 7;
        watermask.setScale(1F, 0.25F, 1F);

    }
}