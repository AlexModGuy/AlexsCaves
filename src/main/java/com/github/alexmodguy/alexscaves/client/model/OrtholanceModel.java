package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class OrtholanceModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox bone;
    private final AdvancedModelBox handle;

    public OrtholanceModel() {
        texWidth = 64;
        texHeight = 64;

        bone = new AdvancedModelBox(this);
        bone.setRotationPoint(0.0F, 20.0F, 0.0F);
        bone.setTextureOffset(0, 0).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        bone.setTextureOffset(0, 0).addBox(-3.0F, -9.0F, -3.0F, 6.0F, 7.0F, 6.0F, 0.0F, false);
        bone.setTextureOffset(0, 13).addBox(-2.5F, -16.0F, -2.5F, 5.0F, 7.0F, 5.0F, 0.0F, false);
        bone.setTextureOffset(20, 9).addBox(-2.0F, -23.0F, -2.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);
        bone.setTextureOffset(20, 20).addBox(-1.0F, -30.0F, -1.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

        handle = new AdvancedModelBox(this);
        handle.setRotationPoint(0.0F, 3.0F, 0.0F);
        bone.addChild(handle);
        setRotateAngle(handle, -0.7854F, 0.0F, 0.0F);
        handle.setTextureOffset(18, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(bone);
    }


    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(bone, handle);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float explode, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();

    }

}
