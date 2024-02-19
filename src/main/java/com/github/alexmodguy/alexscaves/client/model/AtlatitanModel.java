package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.AtlatitanEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class AtlatitanModel extends SauropodBaseModel<AtlatitanEntity> {

    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;
    private final AdvancedModelBox cube_r5;
    private final AdvancedModelBox cube_r6;
    private final AdvancedModelBox cube_r7;
    private final AdvancedModelBox cube_r8;
    private final AdvancedModelBox cube_r9;
    private final AdvancedModelBox cube_r10;
    private final AdvancedModelBox cube_r11;
    private final AdvancedModelBox cube_r12;

    public AtlatitanModel() {
        super();

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(-24.0F, -33.0F, -39.0F);
        chest.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.0F, -0.7854F);
        cube_r1.setTextureOffset(0, 123).addBox(-6.0F, -23.0F, 14.5F, 11.0F, 38.0F, 11.0F, 0.0F, true);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(-24.0F, -33.0F, -2.0F);
        chest.addChild(cube_r2);
        setRotateAngle(cube_r2, -0.7854F, 0.0F, -0.7854F);
        cube_r2.setTextureOffset(0, 123).addBox(-6.0F, -15.0F, 0.5F, 11.0F, 38.0F, 11.0F, 0.0F, true);

        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(24.0F, -33.0F, -2.0F);
        chest.addChild(cube_r3);
        setRotateAngle(cube_r3, -0.7854F, 0.0F, 0.7854F);
        cube_r3.setTextureOffset(0, 123).addBox(-5.0F, -15.0F, 0.5F, 11.0F, 38.0F, 11.0F, 0.0F, false);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(24.0F, -33.0F, -39.0F);
        chest.addChild(cube_r4);
        setRotateAngle(cube_r4, 0.0F, 0.0F, 0.7854F);
        cube_r4.setTextureOffset(0, 123).addBox(-5.0F, -23.0F, 14.5F, 11.0F, 38.0F, 11.0F, 0.0F, false);

        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(-24.0F, -33.0F, -39.0F);
        chest.addChild(cube_r5);
        setRotateAngle(cube_r5, 0.3927F, 0.0F, -0.7854F);
        cube_r5.setTextureOffset(146, 285).addBox(-7.0F, -14.0F, -6.5F, 13.0F, 51.0F, 13.0F, 0.0F, true);

        cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(24.0F, -33.0F, -39.0F);
        chest.addChild(cube_r6);
        setRotateAngle(cube_r6, 0.3927F, 0.0F, 0.7854F);
        cube_r6.setTextureOffset(146, 285).addBox(-6.0F, -14.0F, -6.5F, 13.0F, 51.0F, 13.0F, 0.0F, false);

        cube_r7 = new AdvancedModelBox(this);
        cube_r7.setRotationPoint(8.9991F, 25.5F, -2.7496F);
        right_Hand.addChild(cube_r7);
        setRotateAngle(cube_r7, 0.0F, -0.3927F, 0.0F);
        cube_r7.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, true);

        cube_r8 = new AdvancedModelBox(this);
        cube_r8.setRotationPoint(-15.0F, 25.5F, -2.75F);
        right_Hand.addChild(cube_r8);
        setRotateAngle(cube_r8, 0.0F, 0.3927F, 0.0F);
        cube_r8.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, true);

        cube_r9 = new AdvancedModelBox(this);
        cube_r9.setRotationPoint(-8.9991F, 25.5F, -2.7496F);
        left_Hand.addChild(cube_r9);
        setRotateAngle(cube_r9, 0.0F, 0.3927F, 0.0F);
        cube_r9.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, false);

        cube_r10 = new AdvancedModelBox(this);
        cube_r10.setRotationPoint(15.0F, 25.5F, -2.75F);
        left_Hand.addChild(cube_r10);
        setRotateAngle(cube_r10, 0.0F, -0.3927F, 0.0F);
        cube_r10.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, false);

        cube_r11 = new AdvancedModelBox(this);
        cube_r11.setRotationPoint(-7.0F, -2.0F, -69.0F);
        neck2.addChild(cube_r11);
        setRotateAngle(cube_r11, 0.0F, 0.0F, -0.7854F);
        cube_r11.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 19.0F, 8.0F, 24.0F, 8.0F, 0.0F, true);
        cube_r11.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 36.0F, 8.0F, 24.0F, 8.0F, 0.0F, true);
        cube_r11.setTextureOffset(139, 62).addBox(-4.0F, -13.0F, 1.0F, 8.0F, 17.0F, 8.0F, 0.0F, true);
        cube_r11.setTextureOffset(139, 62).addBox(-4.0F, -8.0F, 51.0F, 8.0F, 17.0F, 8.0F, 0.0F, true);

        cube_r12 = new AdvancedModelBox(this);
        cube_r12.setRotationPoint(7.0F, -2.0F, -69.0F);
        neck2.addChild(cube_r12);
        setRotateAngle(cube_r12, 0.0F, 0.0F, 0.7854F);
        cube_r12.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 36.0F, 8.0F, 24.0F, 8.0F, 0.0F, false);
        cube_r12.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 19.0F, 8.0F, 24.0F, 8.0F, 0.0F, false);
        cube_r12.setTextureOffset(139, 62).addBox(-4.0F, -13.0F, 2.0F, 8.0F, 17.0F, 8.0F, 0.0F, false);
        cube_r12.setTextureOffset(139, 62).addBox(-4.0F, -10.0F, 51.0F, 8.0F, 17.0F, 8.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 2F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            head.setRotationPoint(0.8F, 3.0F, -75.0F);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.15F, 0.15F, 0.15F);
            matrixStackIn.translate(0.0D, 8.55F, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setRotationPoint(0.8F, 8.0F, -75.0F);
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, chest, hips, tail, tail2, tail3, left_Leg, left_Foot, right_Leg, right_Foot, left_Arm, left_Hand, right_Arm, right_Hand, neck, neck2, head, jaw, dewlap, cube_r1, cube_r2, cube_r3, cube_r4, cube_r5, cube_r6, cube_r7, cube_r8, cube_r9, cube_r10, cube_r11, cube_r12);
    }

    public Vec3 getRiderPosition(Vec3 offsetIn) {
        PoseStack translationStack = new PoseStack();
        translationStack.pushPose();
        root.translateAndRotate(translationStack);
        body.translateAndRotate(translationStack);
        chest.translateAndRotate(translationStack);
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(translationStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), armOffsetVec.y(), armOffsetVec.z());
        translationStack.popPose();
        return vec3;
    }
}
