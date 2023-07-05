package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class PrimitiveClubModel extends AdvancedEntityModel {
    private final AdvancedModelBox club;

    public PrimitiveClubModel() {
        texWidth = 32;
        texHeight = 32;

        club = new AdvancedModelBox(this);
        club.setRotationPoint(0.0F, 19.75F, 0.0F);
        club.setTextureOffset(0, 0).addBox(-3.0F, -18.75F, -3.0F, 6.0F, 15.0F, 6.0F, 0.0F, false);
        club.setTextureOffset(8, 21).addBox(-1.0F, -3.75F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
        club.setTextureOffset(0, 21).addBox(-1.0F, -3.75F, -1.0F, 2.0F, 8.0F, 2.0F, 0.25F, false);
        this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(club);
    }


    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(club);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float explode, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }

}