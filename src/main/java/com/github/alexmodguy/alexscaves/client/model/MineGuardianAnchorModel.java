package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.MineGuardianAnchorEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class MineGuardianAnchorModel extends AdvancedEntityModel<MineGuardianAnchorEntity> {
    private final AdvancedModelBox anchor;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;

    public MineGuardianAnchorModel() {
        texWidth = 64;
        texHeight = 64;

        anchor = new AdvancedModelBox(this);
        anchor.setRotationPoint(0.0F, -1.75F, 0.0F);
        anchor.setTextureOffset(0, 0).addBox(-6.0F, -4.0F, 0.0F, 12.0F, 14.0F, 0.0F, 0.0F, false);
        anchor.setTextureOffset(0, 14).addBox(-2.0F, 4.0F, -2.0F, 4.0F, 16.0F, 4.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 23.0F, 0.0F);
        anchor.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.0F, -0.7854F);
        cube_r1.setTextureOffset(24, 0).addBox(1.0F, -15.0F, -2.0F, 3.0F, 3.0F, 4.0F, 0.0F, true);
        cube_r1.setTextureOffset(16, 26).addBox(-2.0F, -15.0F, -2.0F, 3.0F, 11.0F, 4.0F, 0.0F, true);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 23.0F, 0.0F);
        anchor.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 0.0F, 0.7854F);
        cube_r2.setTextureOffset(24, 0).addBox(-4.0F, -15.0F, -2.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);
        cube_r2.setTextureOffset(16, 26).addBox(-1.0F, -15.0F, -2.0F, 3.0F, 11.0F, 4.0F, 0.0F, false);
        cube_r2.setTextureOffset(16, 14).addBox(-4.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(anchor);
    }

    @Override
    public void setupAnim(MineGuardianAnchorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(cube_r1, cube_r2, anchor);
    }

    public Vec3 getChainPosition(Vec3 offsetIn) {
        PoseStack armStack = new PoseStack();
        armStack.pushPose();
        anchor.translateAndRotate(armStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(armStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), -armOffsetVec.y(), armOffsetVec.z());
        armStack.popPose();
        return vec3.add(0, 0.6F, 0);
    }
}
