package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class NuclearFurnaceModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox main;
    private final AdvancedModelBox waste;

    public NuclearFurnaceModel() {
        texWidth = 256;
        texHeight = 256;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(-16.0F, 24.0F, 16.0F);


        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, -14.5714F, 0.0F);
        root.addChild(main);
        main.setTextureOffset(0, 41).addBox(-16.0F, -17.4286F, -16.0F, 32.0F, 32.0F, 9.0F, 0.0F, false);
        main.setTextureOffset(0, 0).addBox(-16.0F, -17.4286F, 7.0F, 32.0F, 32.0F, 9.0F, 0.0F, false);
        main.setTextureOffset(0, 82).addBox(7.0F, -17.4286F, -7.0F, 9.0F, 32.0F, 14.0F, 0.0F, false);
        main.setTextureOffset(68, 68).addBox(-16.0F, -17.4286F, -7.0F, 9.0F, 32.0F, 14.0F, 0.0F, false);
        main.setTextureOffset(82, 0).addBox(-7.0F, 0.5714F, -7.0F, 14.0F, 14.0F, 14.0F, 0.0F, false);
        main.setTextureOffset(82, 17).addBox(12.0F, -8.4286F, 5.0F, 4.0F, 15.0F, 4.0F, 0.0F, false);
        main.setTextureOffset(82, 17).addBox(-16.0F, -8.4286F, -9.0F, 4.0F, 15.0F, 4.0F, 0.0F, true);

        waste = new AdvancedModelBox(this);
        waste.setRotationPoint(0.0F, -0.9286F, 0.0F);
        main.addChild(waste);
        waste.setTextureOffset(98, 17).addBox(12.0F, -7.5F, 5.0F, 3.0F, 15.0F, 4.0F, 0.0F, true);
        waste.setTextureOffset(98, 17).addBox(-15.0F, -7.5F, -9.0F, 3.0F, 15.0F, 4.0F, 0.0F, false);
        this.updateDefaultPose();
  }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, main, waste);
    }

    public void setupAnim(Entity entity, float rot, float criticality, float ageInTicks, float waste, float unused){
        this.resetToDefaultPose();
        this.main.rotateAngleY = (float) Math.toRadians(rot);
        this.waste.rotationPointY += (1F - waste) * 8;
        this.waste.scaleY = waste;
        if(criticality >= 2){
            float f = criticality >= 3 ? 1F : 0.1F;
            this.main.walk(2F, 0.1F, false, 3F, 0, ageInTicks, f);
            this.main.swing(3F, 0.1F, false, 2F, 0, ageInTicks, f);
            this.main.flap(4F, 0.1F, false, 1F, 0, ageInTicks, f);

        }
    }

}
