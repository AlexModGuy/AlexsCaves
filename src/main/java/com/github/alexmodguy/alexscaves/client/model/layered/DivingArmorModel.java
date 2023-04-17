package com.github.alexmodguy.alexscaves.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class DivingArmorModel extends HumanoidModel {

	public DivingArmorModel(ModelPart root) {
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
		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, deformation), PartPose.offset(0.0F, 0.0F, 0.0F));

		head.addOrReplaceChild("helmet", CubeListBuilder.create()
		.texOffs(0, 32).addBox(4.0F, -5.0F, -3.0F, 2.0F, 4.0F, 4.0F, deformation)
		.texOffs(0, 32).mirror().addBox(-6.0F, -5.0F, -3.0F, 2.0F, 4.0F, 4.0F, deformation).mirror(false)
		.texOffs(24, 0).addBox(-3.0F, -7.01F, -5.0F, 6.0F, 6.0F, 2.0F, deformation)
		.texOffs(14, 37).addBox(-4.5F, -1.0F, -4.5F, 9.0F, 2.0F, 9.0F, deformation), PartPose.offset(0.0F, 0.0F, 0.0F));

		body.addOrReplaceChild("chestplate", CubeListBuilder.create()
		.texOffs(40, 4).addBox(-2.0F, 2.0F, 2.0F, 4.0F, 8.0F, 4.0F, deformation), PartPose.offset(0.0F, 0.0F, 0.0F));

		leftArm.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation.extend(0.35F)), PartPose.offset(1.0F, 0.0F, 0.0F));
		rightArm.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(48, 48).mirror().addBox(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation.extend(0.35F)), PartPose.offset(-1.0F, 0.0F, 0.0F));
		leftLeg.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(32, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation.extend(0.35F)), PartPose.offset(0.0F, 0.75F, 0.0F));
		rightLeg.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(32, 48).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation.extend(0.35F)), PartPose.offset(0.0F, 0.75F, 0.0F));
		body.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformation.extend(0.35F)), PartPose.ZERO);

		return LayerDefinition.create(meshdefinition, 64, 64);
	}
}