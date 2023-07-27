package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class RaygunModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox handle;
    private final AdvancedModelBox trigger;
    private final AdvancedModelBox main;
    private final AdvancedModelBox barrel;
    private final AdvancedModelBox nose;
    private final AdvancedModelBox ring;
    private final AdvancedModelBox ring2;
    private final AdvancedModelBox ring3;
    private final AdvancedModelBox ball;
    private final AdvancedModelBox grip;

    public RaygunModel() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        handle = new AdvancedModelBox(this);
        handle.setRotationPoint(0.0F, -3.0F, 6.5F);
        root.addChild(handle);
        handle.setTextureOffset(14, 0).addBox(-1.0F, -3.0F, -0.5F, 2.0F, 6.0F, 3.0F, 0.0F, false);
        handle.setTextureOffset(0, 0).addBox(-1.0F, -3.0F, -0.5F, 2.0F, 6.0F, 3.0F, 0.25F, false);

        trigger = new AdvancedModelBox(this);
        trigger.setRotationPoint(0.0F, -1.5F, -3.5F);
        handle.addChild(trigger);
        trigger.setTextureOffset(0, 8).addBox(0.0F, -2.5F, -1.0F, 0.0F, 5.0F, 4.0F, 0.0F, false);

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 3.0F, -2.5F);
        handle.addChild(main);
        main.setTextureOffset(18, 6).addBox(-3.0F, -12.0F, -2.0F, 6.0F, 6.0F, 6.0F, 0.25F, false);
        main.setTextureOffset(0, 0).addBox(0.0F, -15.0F, -8.0F, 0.0F, 5.0F, 18.0F, 0.0F, false);
        main.setTextureOffset(24, 23).addBox(-3.0F, -12.0F, -2.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

        barrel = new AdvancedModelBox(this);
        barrel.setRotationPoint(0.0F, -9.0F, -6.0F);
        main.addChild(barrel);
        barrel.setTextureOffset(20, 35).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);
        barrel.setTextureOffset(0, 35).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 6.0F, 0.25F, false);

        nose = new AdvancedModelBox(this);
        nose.setRotationPoint(0.0F, 9.0F, 6.0F);
        barrel.addChild(nose);
        nose.setTextureOffset(36, 14).addBox(-0.5F, -9.5F, -14.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);

        ring = new AdvancedModelBox(this);
        ring.setRotationPoint(0.0F, -9.0F, -14.0F);
        nose.addChild(ring);
        ring.setTextureOffset(42, 7).addBox(-3.5F, -3.5F, 1.0F, 7.0F, 7.0F, 0.0F, 0.0F, false);

        ring2 = new AdvancedModelBox(this);
        ring2.setRotationPoint(0.0F, -9.0F, -13.0F);
        nose.addChild(ring2);
        ring2.setTextureOffset(42, 7).addBox(-3.5F, -3.5F, 1.0F, 7.0F, 7.0F, 0.0F, 0.0F, false);

        ring3 = new AdvancedModelBox(this);
        ring3.setRotationPoint(0.0F, -9.0F, -12.0F);
        nose.addChild(ring3);
        ring3.setTextureOffset(42, 7).addBox(-3.5F, -3.5F, 1.0F, 7.0F, 7.0F, 0.0F, 0.0F, false);

        ball = new AdvancedModelBox(this);
        ball.setRotationPoint(0.0F, -9.0F, -16.0F);
        nose.addChild(ball);
        ball.setTextureOffset(8, 12).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        grip = new AdvancedModelBox(this);
        grip.setRotationPoint(0.0F, -3.0F, -8.0F);
        handle.addChild(grip);
        grip.setTextureOffset(48, 37).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 6.0F, 0.25F, false);
        grip.setTextureOffset(48, 27).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(Entity entity, float useAmount, float ageInTicks, float unused, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, handle, grip, ball, barrel, nose, trigger, main, ring, ring2, ring3);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }
}