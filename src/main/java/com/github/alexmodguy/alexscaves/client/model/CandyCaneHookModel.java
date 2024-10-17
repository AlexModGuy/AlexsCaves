package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class CandyCaneHookModel extends AdvancedEntityModel<CandyCaneHookEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox hook;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;

    public CandyCaneHookModel() {
        texWidth = 32;
        texHeight = 32;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        hook = new AdvancedModelBox(this);
        hook.setRotationPoint(0.0F, -4.0F, 5.0F);
        root.addChild(hook);
        hook.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        hook.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.0F, 0.7854F);
        cube_r1.setTextureOffset(0, 0).addBox(0.0F, -5.0F, -13.0F, 0.0F, 10.0F, 14.0F, 0.0F, false);

        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        hook.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 0.0F, -0.7854F);
        cube_r2.setTextureOffset(0, 0).addBox(0.0F, -5.0F, -13.0F, 0.0F, 10.0F, 14.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(CandyCaneHookEntity entity, float splashProgress, float conversionProgress, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, cube_r1, cube_r2, hook);
    }

}
