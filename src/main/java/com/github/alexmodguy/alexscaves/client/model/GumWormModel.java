package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.CaniacEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumbeeperEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class GumWormModel extends AdvancedEntityModel<GumWormEntity> {
    private final AdvancedModelBox main;
    private final AdvancedModelBox head;
    private final AdvancedModelBox bottom_Eye;
    private final AdvancedModelBox left_Eye;
    private final AdvancedModelBox right_Eye;
    private final AdvancedModelBox top_Eye;
    private final AdvancedModelBox gum_Strand1;
    private final AdvancedModelBox gum_Strand2;
    private final AdvancedModelBox gum_Strand3;
    private final AdvancedModelBox bottom_Jaw;
    private final AdvancedModelBox top_Jaw;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;

    public GumWormModel() {
        texWidth = 256;
        texHeight = 256;

        float hatLayer = 1.0F;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 24.0F, -6.0F);


        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -16.5F, 35.25F);
        main.addChild(head);
        head.setTextureOffset(116, 67).addBox(-16.5F, -16.5F, -22.25F, 33.0F, 33.0F, 22.0F, 0.0F, false);
        head.setTextureOffset(17, 220).addBox(-16.5F, -16.5F, -22.25F, 33.0F, 33.0F, 3.0F, hatLayer, false);

        bottom_Eye = new AdvancedModelBox(this);
        bottom_Eye.setRotationPoint(0.0F, 18.0F, -11.75F);
        head.addChild(bottom_Eye);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        bottom_Eye.addChild(cube_r1);
        setRotateAngle(cube_r1, 0.0F, 0.0F, -1.5708F);
        cube_r1.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, 0.0F, true);

        left_Eye = new AdvancedModelBox(this);
        left_Eye.setRotationPoint(18.0F, 0.0F, -10.75F);
        head.addChild(left_Eye);
        left_Eye.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, 0.0F, false);

        right_Eye = new AdvancedModelBox(this);
        right_Eye.setRotationPoint(-18.0F, 0.0F, -10.75F);
        head.addChild(right_Eye);
        right_Eye.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, 0.0F, true);

        top_Eye = new AdvancedModelBox(this);
        top_Eye.setRotationPoint(0.0F, -18.0F, -11.75F);
        head.addChild(top_Eye);


        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        top_Eye.addChild(cube_r2);
        setRotateAngle(cube_r2, 0.0F, 0.0F, 1.5708F);
        cube_r2.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, 0.0F, true);

        gum_Strand1 = new AdvancedModelBox(this);
        gum_Strand1.setRotationPoint(9.0F, 1.0F, -35.75F);
        head.addChild(gum_Strand1);
        gum_Strand1.setTextureOffset(218, 103).addBox(0.0F, -18.5F, -9.5F, 0.0F, 37.0F, 19.0F, 0.0F, false);

        gum_Strand2 = new AdvancedModelBox(this);
        gum_Strand2.setRotationPoint(-9.0F, 1.0F, -35.75F);
        head.addChild(gum_Strand2);
        gum_Strand2.setTextureOffset(218, 103).addBox(0.0F, -18.5F, -9.5F, 0.0F, 37.0F, 19.0F, 0.0F, false);

        gum_Strand3 = new AdvancedModelBox(this);
        gum_Strand3.setRotationPoint(0.0F, 1.0F, -35.75F);
        head.addChild(gum_Strand3);
        setRotateAngle(gum_Strand3, 0.0F, 0.7854F, 0.0F);
        gum_Strand3.setTextureOffset(218, 103).addBox(0.0F, -18.5F, -9.5F, 0.0F, 37.0F, 19.0F, 0.0F, true);

        bottom_Jaw = new AdvancedModelBox(this);
        bottom_Jaw.setRotationPoint(0.0F, 6.5F, -22.0F);
        head.addChild(bottom_Jaw);
        bottom_Jaw.setTextureOffset(102, 9).addBox(-16.5F, 0.0F, -36.25F, 33.0F, 10.0F, 36.0F, 0F, false);
        bottom_Jaw.setTextureOffset(0, 43).addBox(-16.5F, -10.0F, -36.25F, 33.0F, 10.0F, 36.0F, 0F, false);
        bottom_Jaw.setTextureOffset(118, 210).addBox(-16.5F, 0.0F, -36.25F, 33.0F, 10.0F, 36.0F, hatLayer + 0.01F, false);

        top_Jaw = new AdvancedModelBox(this);
        top_Jaw.setRotationPoint(0.0F, -6.5F, -22.25F);
        head.addChild(top_Jaw);
        top_Jaw.setTextureOffset(118, 165).addBox(-16.5F, -10.0F, -36.0F, 33.0F, 10.0F, 36.0F, hatLayer + 0.01F, false);
        top_Jaw.setTextureOffset(0, 144).addBox(-16.5F, 0.0F, -36.0F, 33.0F, 10.0F, 36.0F, 0F, false);
        top_Jaw.setTextureOffset(0, 89).addBox(-16.5F, -10.0F, -36.0F, 33.0F, 10.0F, 36.0F, 0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, head, bottom_Eye, top_Eye, left_Eye, right_Eye, top_Jaw, bottom_Jaw, gum_Strand1, gum_Strand2, gum_Strand3, cube_r1, cube_r2);
    }

    public void setupAnim(GumWormEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - entity.tickCount;
        float mouthOpenProgress = entity.getMouthOpenProgress(partialTicks);
        this.walk(bottom_Jaw, 0.2F, 0.1F, true, 1F, -0.8F, ageInTicks, mouthOpenProgress);
        this.walk(top_Jaw, 0.2F, 0.1F, false, 1F, -0.8F, ageInTicks, mouthOpenProgress);
        float gumStretchVertical = Math.max(ACMath.walkValue(ageInTicks, mouthOpenProgress, 0.2F, 1F, 0.15F, true) + mouthOpenProgress, 0F) + 0.45F;
        this.gum_Strand1.setScale(1F, gumStretchVertical, 1F - gumStretchVertical * 0.2F);
        this.gum_Strand2.setScale(1F, gumStretchVertical, 1F - gumStretchVertical * 0.2F);
        this.gum_Strand3.setScale(1F, gumStretchVertical, 1F - gumStretchVertical * 0.2F);
        this.head.rotateAngleZ += (float) Math.toRadians(entity.getBodyZRot(partialTicks));
    }

}
