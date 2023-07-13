package com.github.alexmodguy.alexscaves.client.model.layered;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class PrimordialArmorModel extends HumanoidModel {

    public ModelPart frontFlap;
    public ModelPart backFlap;

    public PrimordialArmorModel(ModelPart root) {
        super(root);
        this.frontFlap = root.getChild("body").getChild("frontFlap");
        this.backFlap = root.getChild("body").getChild("backFlap");
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");
        PartDefinition body = partdefinition.getChild("body");
        PartDefinition leftLeg = partdefinition.getChild("left_leg");
        PartDefinition rightLeg = partdefinition.getChild("right_leg");
        PartDefinition leftArm = partdefinition.getChild("left_arm");
        PartDefinition rightArm = partdefinition.getChild("right_arm");

        CubeDeformation deformationHelmet = deformation.extend(0.02F);
        CubeDeformation deformationTunic = deformation.extend(0.25F);
        CubeDeformation deformationPants = deformation.extend(0.1F);

        head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(100, 116).addBox(-6.0F, -13.0F, 2.0F, 12.0F, 10.0F, 2.0F, deformationHelmet)
                .texOffs(65, 117).addBox(-7.0F, -14.0F, 2.0F, 14.0F, 11.0F, 0.0F, deformationHelmet)
                .texOffs(114, 91).addBox(-2.0F, -8.0F, -7.0F, 4.0F, 4.0F, 3.0F, deformationHelmet)
                .texOffs(95, 85).addBox(-3.5F, -13.0F, -7.1F, 7.0F, 5.0F, 4.0F, deformationHelmet)
                .texOffs(120, 108).addBox(2.0F, -10.0F, -4.0F, 2.0F, 2.0F, 2.0F, deformationHelmet)
                .texOffs(120, 112).mirror().addBox(-4.0F, -10.0F, -4.0F, 2.0F, 2.0F, 2.0F, deformationHelmet).mirror(false), PartPose.ZERO);

        body.addOrReplaceChild("tunic", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformationTunic), PartPose.ZERO);

        rightArm.addOrReplaceChild("rightBand", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationTunic), PartPose.ZERO);

        leftArm.addOrReplaceChild("leftBand", CubeListBuilder.create().texOffs(48, 49).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationTunic), PartPose.ZERO);

        rightLeg.addOrReplaceChild("rightLegSpines", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationPants), PartPose.ZERO);

        leftLeg.addOrReplaceChild("leftLegSpines", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationPants), PartPose.ZERO);

        body.addOrReplaceChild("frontFlap", CubeListBuilder.create().texOffs(95, 104).addBox(-3.9F, -1.0F, 0.1F, 8.0F, 7.0F, 4.0F, deformationTunic), PartPose.offset(0.0F, 12.0F, -2.0F));
        body.addOrReplaceChild("backFlap", CubeListBuilder.create().texOffs(56, 104).addBox(-4.1F, -1.0F, -4.1F, 8.0F, 7.0F, 4.0F, deformationTunic), PartPose.offset(0.0F, 12.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public PrimordialArmorModel withAnimations(LivingEntity entity){
        float partialTick = Minecraft.getInstance().getFrameTime();
        float limbSwingAmount = entity.walkAnimation.speed(partialTick);
        float minLeg = Math.min(this.rightLeg.xRot, this.leftLeg.xRot);
        float maxLeg = Math.max(this.rightLeg.xRot, this.leftLeg.xRot);
        this.frontFlap.xRot = minLeg - (float)(limbSwingAmount * Math.toRadians(25));
        this.frontFlap.y = 12.0F - limbSwingAmount * 1.2F;
        this.backFlap.xRot = maxLeg + (float)(limbSwingAmount * Math.toRadians(25));
        this.backFlap.y = 12.0F - limbSwingAmount * 1.2F;
        return this;
    }

}
