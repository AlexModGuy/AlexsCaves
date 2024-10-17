package com.github.alexmodguy.alexscaves.client.model.layered;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class GingerbreadArmorModel extends HumanoidModel {

    public GingerbreadArmorModel(ModelPart root) {
        super(root);
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
        CubeDeformation deformationBody = deformation;
        CubeDeformation deformationPants = deformation.extend(-0.1F);

        head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 0)
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformationHelmet)
                .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformationHelmet.extend(0.5F))
                .texOffs(0, 64).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformationHelmet.extend(0.75F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(27, 101).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, deformationHelmet), PartPose.offsetAndRotation(-0.0855F, -11.0F, 4.4918F, 0.0F, -1.5708F, 0.0F));

        head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(27, 101).addBox(-2.4532F, -2.0F, 5.4613F, 3.0F, 4.0F, 4.0F, deformationHelmet), PartPose.offsetAndRotation(-7.5468F, -11.0F, -6.4613F, 0.0F, 1.5708F, 0.0F));

        head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 98).mirror().addBox(-8.0F, 0.0F, -6.0F, 8.0F, 3.0F, 11.0F, deformationHelmet).mirror(false), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 98).addBox(0.0F, 0.0F, -6.0F, 8.0F, 3.0F, 11.0F, deformationHelmet), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        body.addOrReplaceChild("shirt", CubeListBuilder.create().texOffs(16, 16)
                .texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformationBody.extend(0.25F))
                .texOffs(104, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformationBody.extend(0.6F))
                .texOffs(28, 93).addBox(-2.5F, 2.0F, -4.0F, 5.0F, 5.0F, 2.0F, deformationBody), PartPose.ZERO);

        rightArm.addOrReplaceChild("right_arm_gingerbread", CubeListBuilder.create().texOffs(40, 16)
                .texOffs(32, 64).mirror().addBox(-5.0F, -7.0F, -4.0F, 4.0F, 10.0F, 8.0F, deformationBody).mirror(false)
                .texOffs(0, 80).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationBody.extend(0.5F)).mirror(false)
                .texOffs(27, 101).mirror().addBox(-6.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, deformationBody).mirror(false)
                .texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationBody.extend(0.25F)), PartPose.ZERO);

        leftArm.addOrReplaceChild("left_arm_gingerbread", CubeListBuilder.create().texOffs(32, 48)
                .texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationBody.extend(0.25F))
                .texOffs(0, 80).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationBody.extend(0.5F))
                .texOffs(32, 64).addBox(1.0F, -7.0F, -4.0F, 4.0F, 10.0F, 8.0F, deformationBody)
                .texOffs(27, 101).addBox(3.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, deformationBody), PartPose.ZERO);

        rightLeg.addOrReplaceChild("right_leg_gingerbread", CubeListBuilder.create().texOffs(0, 16)
                .texOffs(35, 112).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationPants.extend(0.5F)).mirror(false)
                .texOffs(0, 112).mirror().addBox(-2.5F, 6.0F, -2.5F, 5.0F, 6.0F, 5.0F, deformationPants.extend(0.25F)).mirror(false)
                .texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationPants.extend(0.25F)), PartPose.ZERO);

        leftLeg.addOrReplaceChild("left_leg_gingerbread", CubeListBuilder.create().texOffs(16, 48)
                .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationPants.extend(0.25F))
                .texOffs(35, 112).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationPants.extend(0.5F))
                .texOffs(0, 112).addBox(-2.5F, 6.0F, -2.5F, 5.0F, 6.0F, 5.0F, deformationPants.extend(0.25F)), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

}
