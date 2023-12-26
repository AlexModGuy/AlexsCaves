package com.github.alexmodguy.alexscaves.client.model.layered;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class HazmatArmorModel extends HumanoidModel {

    public ModelPart mask;
    public ModelPart breather1;
    public ModelPart breather2;

    public HazmatArmorModel(ModelPart root) {
        super(root);
        this.mask = root.getChild("head").getChild("mask");
        this.breather1 = this.mask.getChild("breather1");
        this.breather2 = this.mask.getChild("breather2");
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


        CubeDeformation deformationMask = deformation.extend(-0.1F);
        CubeDeformation deformationShoulder = deformation.extend(0.1F);
        CubeDeformation deformationMain = deformation.extend(0.25F);
        CubeDeformation deformationPants = deformation.extend(0.1F);

        PartDefinition mask = head.addOrReplaceChild("mask", CubeListBuilder.create().texOffs(116, 100).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 5.0F, 2.0F, deformationMask), PartPose.offset(0.0F, -1.5F, -4.0F));
        mask.addOrReplaceChild("breather1", CubeListBuilder.create().texOffs(78, 99).mirror().addBox(-1.5F, -1.0F, -4.0F, 3.0F, 3.0F, 5.0F, deformationMask).mirror(false), PartPose.offsetAndRotation(2.5F, 1.0F, 1.0F, 0.3927F, -0.7854F, 0.0F));
        mask.addOrReplaceChild("breather2", CubeListBuilder.create().texOffs(78, 99).addBox(-1.5F, -1.0F, -4.0F, 3.0F, 3.0F, 5.0F, deformationMask), PartPose.offsetAndRotation(-2.5F, 1.0F, 1.0F, 0.3927F, 0.7854F, 0.0F));

        body.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformationMain), PartPose.offset(0.0F, 24.0F, 0.0F));

        rightArm.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationMain), PartPose.offset(0.0F, 0.0F, 0.0F));
        rightArm.addOrReplaceChild("right_shoulder_guard", CubeListBuilder.create().texOffs(42, 116).mirror().addBox(-4.0F, -4.0F, -3.0F, 5.0F, 6.0F, 6.0F, deformationShoulder).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));


        leftArm.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 49).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationMain), PartPose.offset(0.0F, 0.0F, 0.0F));
        leftArm.addOrReplaceChild("left_shoulder_guard", CubeListBuilder.create().texOffs(42, 116).addBox(-1.0F, -4.0F, -3.0F, 5.0F, 6.0F, 6.0F, deformationShoulder), PartPose.offset(0.0F, 0.0F, 0.0F));

        rightLeg.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationPants), PartPose.offset(0.0F, 0.0F, 0.0F));

        leftLeg.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformationPants), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public HazmatArmorModel withAnimations(LivingEntity entity) {
        float partialTick = Minecraft.getInstance().getFrameTime();
        float f = entity.tickCount + partialTick;
        float fromRads = (float) Mth.wrapDegrees(Math.toDegrees(this.head.yRot - this.body.yRot));

        this.mask.yRot = (float) (-(Math.toRadians(fromRads)) * 0.3F);
        float breathScale1 = (float) (Math.sin(f * 0.05F) + 1f) * 0.1F + 1F;
        float breathScale2 = (float) (Math.cos(f * 0.05F) + 1f) * 0.25F + 0.75F;
        if (entity instanceof ArmorStand) {
            breathScale1 = 1.0F;
            breathScale2 = 1.0F;
        }
        this.breather1.xScale = breathScale1;
        this.breather1.yScale = breathScale1;
        this.breather1.zScale = breathScale2;
        this.breather2.xScale = breathScale1;
        this.breather2.yScale = breathScale1;
        this.breather2.zScale = breathScale2;
        return this;
    }

}