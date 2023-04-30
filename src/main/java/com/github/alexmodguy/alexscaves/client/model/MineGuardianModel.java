package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.MineGuardianEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MineGuardianModel extends AdvancedEntityModel<MineGuardianEntity> {
    private final AdvancedModelBox head;
    private final AdvancedModelBox head_r1;
    private final AdvancedModelBox eye;
    private final AdvancedModelBox spikepart0;
    private final AdvancedModelBox spikepart1;
    private final AdvancedModelBox spikepart2;
    private final AdvancedModelBox spikepart3;
    private final AdvancedModelBox spikepart4;
    private final AdvancedModelBox spikepart5;
    private final AdvancedModelBox spikepart6;
    private final AdvancedModelBox spikepart7;
    private final AdvancedModelBox spikepart8;
    private final AdvancedModelBox spikepart9;
    private final AdvancedModelBox spikepart10;
    private final AdvancedModelBox spikepart11;

    private final AdvancedModelBox spike0;
    private final AdvancedModelBox spike1;
    private final AdvancedModelBox spike2;
    private final AdvancedModelBox spike3;
    private final AdvancedModelBox spike4;
    private final AdvancedModelBox spike5;
    private final AdvancedModelBox spike6;
    private final AdvancedModelBox spike7;
    private final AdvancedModelBox spike8;
    private final AdvancedModelBox spike9;
    private final AdvancedModelBox spike10;
    private final AdvancedModelBox spike11;

    public MineGuardianModel() {
        texWidth = 64;
        texHeight = 64;

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 24.0F, 0.0F);
        head.setTextureOffset(0, 0).addBox(-6.0F, -14.0F, -8.0F, 12.0F, 12.0F, 16.0F, 0.0F, false);
        head.setTextureOffset(0, 28).addBox(-8.0F, -14.0F, -6.0F, 2.0F, 12.0F, 12.0F, 0.0F, false);
        head.setTextureOffset(0, 28).addBox(6.0F, -14.0F, -6.0F, 2.0F, 12.0F, 12.0F, 0.0F, true);
        head.setTextureOffset(16, 40).addBox(-6.0F, -16.0F, -6.0F, 12.0F, 2.0F, 12.0F, 0.0F, true);

        head_r1 = new AdvancedModelBox(this);
        head_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(head_r1);
        setRotateAngle(head_r1, 0.0F, 0.0F, -3.1416F);
        head_r1.setTextureOffset(16, 38).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 2.0F, 0.0F, 0.0F, true);
        head_r1.setTextureOffset(16, 40).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 2.0F, 12.0F, 0.0F, true);

        eye = new AdvancedModelBox(this);
        eye.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(eye);
        eye.setTextureOffset(8, 0).addBox(-1.0F, 15.0F, -8.25F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        spikepart0 = new AdvancedModelBox(this);
        spikepart0.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart0);
        setRotateAngle(spikepart0, 0.0F, 0.0F, 0.7854F);

        spikepart1 = new AdvancedModelBox(this);
        spikepart1.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart1);
        setRotateAngle(spikepart1, 0.0F, 0.0F, -0.7854F);

        spikepart2 = new AdvancedModelBox(this);
        spikepart2.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart2);
        setRotateAngle(spikepart2, 0.7854F, 0.0F, 0.0F);

        spikepart3 = new AdvancedModelBox(this);
        spikepart3.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart3);
        setRotateAngle(spikepart3, -0.7854F, 0.0F, 0.0F);

        spikepart4 = new AdvancedModelBox(this);
        spikepart4.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart4);
        setRotateAngle(spikepart4, 0.0F, 0.0F, 2.3562F);

        spikepart5 = new AdvancedModelBox(this);
        spikepart5.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart5);
        setRotateAngle(spikepart5, 0.0F, 0.0F, -2.3562F);

        spikepart6 = new AdvancedModelBox(this);
        spikepart6.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart6);
        setRotateAngle(spikepart6, 2.3562F, 0.0F, 0.0F);

        spikepart7 = new AdvancedModelBox(this);
        spikepart7.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart7);
        setRotateAngle(spikepart7, -2.3562F, 0.0F, 0.0F);

        spikepart8 = new AdvancedModelBox(this);
        spikepart8.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart8);
        setRotateAngle(spikepart8, 1.5708F, -0.7854F, 0.0F);

        spikepart9 = new AdvancedModelBox(this);
        spikepart9.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart9);
        setRotateAngle(spikepart9, 1.5708F, 0.7854F, 0.0F);

        spikepart10 = new AdvancedModelBox(this);
        spikepart10.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart10);
        setRotateAngle(spikepart10, 1.5708F, -2.3562F, 0.0F);

        spikepart11 = new AdvancedModelBox(this);
        spikepart11.setRotationPoint(0.0F, -24.0F, 0.0F);
        head.addChild(spikepart11);
        setRotateAngle(spikepart11, 1.5708F, 2.3562F, 0.0F);

        spike0 = new AdvancedModelBox(this);
        spikepart0.addChild(spike0);
        spike0.setTextureOffset(0, 0).addBox(10.25F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike1 = new AdvancedModelBox(this);
        spikepart1.addChild(spike1);
        spike1.setTextureOffset(0, 0).addBox(-12.25F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike2 = new AdvancedModelBox(this);
        spikepart2.addChild(spike2);
        spike2.setTextureOffset(0, 0).addBox(-1.0F, -4.5F, -12.25F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike3 = new AdvancedModelBox(this);
        spikepart3.addChild(spike3);
        spike3.setTextureOffset(0, 0).addBox(-1.0F, -4.5F, 10.5F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike4 = new AdvancedModelBox(this);
        spikepart4.addChild(spike4);
        spike4.setTextureOffset(0, 0).addBox(10.25F, -27.5F, -1.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike5 = new AdvancedModelBox(this);
        spikepart5.addChild(spike5);
        spike5.setTextureOffset(0, 0).addBox(-12.25F, -27.5F, -1.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike6 = new AdvancedModelBox(this);
        spikepart6.addChild(spike6);
        spike6.setTextureOffset(0, 0).addBox(-1.0F, -28.5F, -12.25F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike7 = new AdvancedModelBox(this);
        spikepart7.addChild(spike7);
        spike7.setTextureOffset(0, 0).addBox(-1.0F, -27.5F, 10.25F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike8 = new AdvancedModelBox(this);
        spikepart8.addChild(spike8);
        spike8.setTextureOffset(0, 0).addBox(-1.0F, -17.5F, -17.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike9 = new AdvancedModelBox(this);
        spikepart9.addChild(spike9);
        spike9.setTextureOffset(0, 0).addBox(-1.0F, -17.5F, -17.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike10 = new AdvancedModelBox(this);
        spikepart10.addChild(spike10);
        spike10.setTextureOffset(0, 0).addBox(-1.0F, -17.5F, -17.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        spike11 = new AdvancedModelBox(this);
        spikepart11.addChild(spike11);
        spike11.setTextureOffset(0, 0).addBox(-1.0F, -17.5F, -17.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(head, head_r1, eye, spikepart0, spikepart1, spikepart2, spikepart3, spikepart4, spikepart5, spikepart6, spikepart7, spikepart8, spikepart9, spikepart10, spikepart11, spike0, spike1, spike2, spike3, spike4, spike5, spike6, spike7, spike8, spike9, spike10, spike11);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(head);
    }


    @Override
    public void setupAnim(MineGuardianEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float explodeProgress = entity.getExplodeProgress(partialTicks);
        float scanProgress = entity.getScanProgress(partialTicks);
        head.setScale(1F + explodeProgress * 0.15F, 1F + explodeProgress * 0.15F, 1F + explodeProgress * 0.15F);
        this.flap(head, 3F, 0.3F, true, 1F, 0F, ageInTicks, explodeProgress);
        spike0.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 0F, 1F, false) + 2F;
        spike1.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 1F, 1F, false) + 2F;
        spike2.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 2F, 1F, false) + 2F;
        spike3.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 3F, 1F, false) + 2F;
        spike4.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 4F, 1F, false) + 2F;
        spike5.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 5F, 1F, false) + 2F;
        spike6.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 6F, 1F, false) + 2F;
        spike7.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 7F, 1F, false) + 2F;
        spike8.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 8F, 1F, false) + 2F;
        spike9.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 9F, 1F, false) + 2F;
        spike10.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 10F, 1F, false) + 2F;
        spike11.rotationPointY += ACMath.walkValue(ageInTicks, 1.0F, 0.1F + explodeProgress, 11F, 1F, false) + 2F;
        Entity look = Minecraft.getInstance().getCameraEntity();
        if(look != null){
            Vec3 vector3d = look.getEyePosition(0.0F);
            Vec3 vector3d1 = entity.getEyePosition(0.0F);
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = (new Vec3(vector3d1.x - vector3d.x, 0.0D, vector3d1.z - vector3d.z)).normalize().yRot(((float) Math.PI / 2F));
            double d1 = vector3d2.dot(vector3d3);
            double d2 = Mth.sqrt((float) Math.abs(d1)) * (float) Math.signum(d1);
            this.eye.rotationPointX += (d2 * 2F - this.head.rotateAngleZ) * (1F - scanProgress);
        }
        this.eye.rotationPointX += scanProgress * (Math.sin(ageInTicks * 0.1F) * 3);
    }


    public void translateToEye(PoseStack stack){
        this.head.translateAndRotate(stack);
        this.eye.translateAndRotate(stack);
    }
}