package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.MagnetronEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;

public class MagnetronModel extends AdvancedEntityModel<MagnetronEntity> {
    private final AdvancedModelBox wheel;
    private final AdvancedModelBox headPivot;
    private final AdvancedModelBox head;
    private final AdvancedModelBox headExtra;

    public MagnetronModel() {
        texWidth = 128;
        texHeight = 128;

        wheel = new AdvancedModelBox(this);
        wheel.setRotationPoint(0.0F, 16.0F, 0.0F);
        wheel.setTextureOffset(28, 28).addBox(-3.0F, -8.0F, -8.0F, 6.0F, 4.0F, 16.0F, 0.0F, false);
        wheel.setTextureOffset(40, 60).addBox(-3.0F, -4.0F, -8.0F, 6.0F, 8.0F, 4.0F, 0.0F, false);
        wheel.setTextureOffset(56, 22).addBox(-3.0F, -4.0F, 4.0F, 6.0F, 8.0F, 4.0F, 0.0F, false);
        wheel.setTextureOffset(0, 44).addBox(-3.0F, 4.0F, -8.0F, 6.0F, 4.0F, 16.0F, 0.0F, false);

        headPivot = new AdvancedModelBox(this);
        headPivot.setRotationPoint(0.0F, 0.0F, -2.0F);

        head = new AdvancedModelBox(this);
        setRotationAngle(head, 0.3927F, 0.0F, 0.0F);
        headPivot.addChild(head);

        headExtra = new AdvancedModelBox(this);
        headExtra.setRotationPoint(0.0F, 2.0F, 0.0F);
        head.addChild(headExtra);
        setRotationAngle(headExtra, 0.0F, -0.7854F, 0.0F);
        headExtra.setTextureOffset(52, 48).addBox(-2.0F, -5.0F, 18.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        headExtra.setTextureOffset(0, 22).addBox(-8.0F, -5.0F, 8.0F, 6.0F, 6.0F, 16.0F, 0.0F, false);
        headExtra.setTextureOffset(28, 48).addBox(18.0F, -5.0F, -2.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        headExtra.setTextureOffset(48, 0).addBox(8.0F, -5.0F, -8.0F, 16.0F, 6.0F, 6.0F, 0.0F, false);
        headExtra.setTextureOffset(0, 0).addBox(-8.0F, -5.0F, -8.0F, 16.0F, 6.0F, 16.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(wheel, headPivot);
    }

    @Override
    public void setupAnim(MagnetronEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = ageInTicks - entity.tickCount;
        float formProgress = Math.min(1, entity.getFormProgress(partialTick) * 10);
        float rollProgress = 1F - formProgress;
        float rollLeanProgress = rollProgress * entity.getRollLeanProgress(partialTick);
        float timeRolling = (float)Math.toRadians(Mth.wrapDegrees(entity.getRollPosition(partialTick)));
        float bodyYaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTick;
        float wheelYaw = (entity.getWheelYaw(partialTick) - bodyYaw) * rollProgress + 90F * formProgress;

        progressRotationPrev(head, rollLeanProgress, (float) Math.toRadians(20), 0, 0, 1F);
        progressPositionPrev(headPivot, rollLeanProgress, 0, -2, -4, 1F);
        this.bob(headPivot, 0.15F, 1.5F, false, ageInTicks, 1);
        this.wheel.rotateAngleY = (float) Math.toRadians(wheelYaw);
        if(entity.isFormed()){
            float rollDeg = (float) Mth.wrapDegrees(Math.toDegrees(entity.clientRoll));
        }else{
            wheel.rotateAngleX = timeRolling * rollProgress;
            entity.clientRoll = wheel.rotateAngleX;
            this.bob(wheel, 1, 10, true, timeRolling, rollProgress);
            this.bob(headPivot, 1, 4, true, timeRolling, rollProgress);
        }
        if(entity.isAlive()){
            this.wheel.showModel = true;
        }else{
            this.wheel.showModel = false;
        }
        this.faceTarget(netHeadYaw, headPitch, 1, headPivot);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(wheel, headPivot, head, headExtra);
    }


    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}