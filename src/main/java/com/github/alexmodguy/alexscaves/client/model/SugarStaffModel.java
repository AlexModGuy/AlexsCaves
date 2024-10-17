package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class SugarStaffModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox staff;
    private final AdvancedModelBox mint;

    public SugarStaffModel() {
        texWidth = 32;
        texHeight = 32;

        staff = new AdvancedModelBox(this);
        staff.setRotationPoint(0.0F, 17.0F, 0.0F);
        staff.setTextureOffset(14, 12).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 13.0F, 2.0F, 0.0F, false);
        staff.setTextureOffset(12, 0).addBox(-1.5F, 4.0F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        staff.setTextureOffset(0, 14).addBox(0.0F, -11.0F, -8.0F, 0.0F, 9.0F, 7.0F, 0.0F, true);
        staff.setTextureOffset(0, 5).addBox(0.0F, -11.0F, 1.0F, 0.0F, 9.0F, 7.0F, 0.0F, false);
        staff.setTextureOffset(18, 6).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.25F, false);

        mint = new AdvancedModelBox(this);
        mint.setRotationPoint(0.0F, -13.5F, 0.0F);
        staff.addChild(mint);
        mint.setTextureOffset(0, 0).addBox(-1.5F, -3.0F, -3.0F, 3.0F, 6.0F, 6.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(staff);
    }


    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(staff, mint);
    }

    @Override
    public void setupAnim(Entity entity, float unused1, float unused2, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        mint.bob(0.2F, 0.5F, false, ageInTicks, 1);
        mint.rotateAngleX += ageInTicks * 0.035F;
    }
}