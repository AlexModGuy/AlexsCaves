package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.LuxtructosaurusEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SauropodBaseEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.RandomSource;

import java.util.List;

public class LuxtructosaurusModel extends SauropodBaseModel<LuxtructosaurusEntity> {
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

    public LuxtructosaurusModel() {
        super();
        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(-24.0F, -33.0F, -39.0F);
        chest.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.0F, -0.7854F);
        cube_r1.setTextureOffset(0, 123).addBox(-6.0F, -23.0F, 14.5F, 11.0F, 38.0F, 11.0F, 0.0F, true);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(24.0F, -33.0F, -39.0F);
        chest.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 0.0F, 0.7854F);
        cube_r2.setTextureOffset(0, 123).addBox(-5.0F, -23.0F, 14.5F, 11.0F, 38.0F, 11.0F, 0.0F, false);

        cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(-24.0F, -33.0F, -39.0F);
        chest.addChild(cube_r3);
        setRotateAngle(cube_r3, 0.3927F, 0.0F, -0.7854F);
        cube_r3.setTextureOffset(146, 285).addBox(-7.0F, -40.0F, -6.5F, 13.0F, 51.0F, 13.0F, 0.0F, true);

        cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(24.0F, -33.0F, -39.0F);
        chest.addChild(cube_r4);
        setRotateAngle(cube_r4, 0.3927F, 0.0F, 0.7854F);
        cube_r4.setTextureOffset(146, 285).addBox(-6.0F, -40.0F, -6.5F, 13.0F, 51.0F, 13.0F, 0.0F, false);


        cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(8.9991F, 25.5F, -2.7496F);
        right_Hand.addChild(cube_r5);
        setRotateAngle(cube_r5, 0.0F, -0.3927F, 0.0F);
        cube_r5.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, true);

        cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(-15.0F, 25.5F, -2.75F);
        right_Hand.addChild(cube_r6);
        setRotateAngle(cube_r6, 0.0F, 0.3927F, 0.0F);
        cube_r6.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, true);

        cube_r7 = new AdvancedModelBox(this);
        cube_r7.setRotationPoint(-8.9991F, 25.5F, -2.7496F);
        left_Hand.addChild(cube_r7);
        setRotateAngle(cube_r7, 0.0F, 0.3927F, 0.0F);
        cube_r7.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, false);

        cube_r8 = new AdvancedModelBox(this);
        cube_r8.setRotationPoint(15.0F, 25.5F, -2.75F);
        left_Hand.addChild(cube_r8);
        setRotateAngle(cube_r8, 0.0F, -0.3927F, 0.0F);
        cube_r8.setTextureOffset(20, 236).addBox(0.0F, -27.5F, -10.0F, 0.0F, 47.0F, 10.0F, 0.0F, false);


        cube_r9 = new AdvancedModelBox(this);
        cube_r9.setRotationPoint(-7.0F, -2.0F, -69.0F);
        neck2.addChild(cube_r9);
        setRotateAngle(cube_r9, 0.0F, 0.0F, -0.7854F);
        cube_r9.setTextureOffset(230, 149).addBox(-4.0F, -26.0F, 2.0F, 8.0F, 30.0F, 8.0F, 0.0F, true);
        cube_r9.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 19.0F, 8.0F, 24.0F, 8.0F, 0.0F, true);
        cube_r9.setTextureOffset(139, 62).addBox(-4.0F, -13.0F, 36.0F, 8.0F, 17.0F, 8.0F, 0.0F, true);

        cube_r10 = new AdvancedModelBox(this);
        cube_r10.setRotationPoint(7.0F, -2.0F, -69.0F);
        neck2.addChild(cube_r10);
        setRotateAngle(cube_r10, 0.0F, 0.0F, 0.7854F);
        cube_r10.setTextureOffset(230, 149).addBox(-4.0F, -26.0F, 2.0F, 8.0F, 30.0F, 8.0F, 0.0F, false);
        cube_r10.setTextureOffset(227, 259).addBox(-4.0F, -20.0F, 19.0F, 8.0F, 24.0F, 8.0F, 0.0F, false);
        cube_r10.setTextureOffset(139, 62).addBox(-4.0F, -13.0F, 36.0F, 8.0F, 17.0F, 8.0F, 0.0F, false);
        this.updateDefaultPose();

    }

    @Override
    public void setupAnim(SauropodBaseEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.walk(jaw, 0.05F, 0.1F, true, 1F, -0.1F, ageInTicks, 1);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return getListOfAllParts();
    }

    public ImmutableList<AdvancedModelBox> getListOfAllParts() {
        return ImmutableList.of(root, body, chest, hips, tail, tail2, tail3, left_Leg, left_Foot, right_Leg, right_Foot, left_Arm, left_Hand, right_Arm, right_Hand, neck, neck2, head, jaw, dewlap, cube_r1, cube_r2, cube_r3, cube_r4, cube_r5, cube_r6, cube_r7, cube_r8, cube_r9, cube_r10);
    }

    public AdvancedModelBox getRandomModelPart(RandomSource randomSource) {
        List<AdvancedModelBox> list = getListOfAllParts();
        return list.get(randomSource.nextInt(list.size()));
    }
}
