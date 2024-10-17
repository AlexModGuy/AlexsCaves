package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.living.CaramelCubeEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class CaramelCubeModel extends AdvancedEntityModel<CaramelCubeEntity> {

    private final AdvancedModelBox main;
    private final AdvancedModelBox caramel_Core;
    private final AdvancedModelBox Caramel_Layer;
    private final AdvancedModelBox left_Eye;
    private final AdvancedModelBox right_Eye;
    private final AdvancedModelBox wrapper;
    private final AdvancedModelBox left_wrapperTie;
    private final AdvancedModelBox right_wrapperTie;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;

    public CaramelCubeModel() {
        texWidth = 64;
        texHeight = 64;

        main = new AdvancedModelBox(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);

        caramel_Core = new AdvancedModelBox(this);
        caramel_Core.setRotationPoint(0.0F, -5.5F, 0.0F);
        main.addChild(caramel_Core);
        caramel_Core.setTextureOffset(32, 12).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        left_Eye = new AdvancedModelBox(this);
        left_Eye.setRotationPoint(3.0F, -0.5F, -4.0F);
        caramel_Core.addChild(left_Eye);
        left_Eye.setTextureOffset(60, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        right_Eye = new AdvancedModelBox(this);
        right_Eye.setRotationPoint(-2.0F, 0.5F, -4.0F);
        caramel_Core.addChild(right_Eye);
        right_Eye.setTextureOffset(60, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        Caramel_Layer = new AdvancedModelBox(this);
        Caramel_Layer.setRotationPoint(0.0F, 0.0F, 0.0F);
        caramel_Core.addChild(Caramel_Layer);
        Caramel_Layer.setTextureOffset(0, 20).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, 0.0F, false);

        wrapper = new AdvancedModelBox(this);
        wrapper.setRotationPoint(0.0F, 0.0F, 0.0F);
        caramel_Core.addChild(wrapper);
        wrapper.setTextureOffset(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, 0.5F, false);

        left_wrapperTie = new AdvancedModelBox(this);
        left_wrapperTie.setRotationPoint(5.0F, 0.0F, -0.5F);
        wrapper.addChild(left_wrapperTie);

        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(2.0F, 2.5F, 8.0F);
        left_wrapperTie.addChild(cube_r1);
        setRotateAngle(cube_r1, 1.5708F, -1.5708F, 0.0F);
        cube_r1.setTextureOffset(0, 30).addBox(-8.0F, -8.0F, -2.0F, 0.0F, 10.0F, 10.0F, 0.0F, false);

        right_wrapperTie = new AdvancedModelBox(this);
        right_wrapperTie.setRotationPoint(-5.0F, 0.0F, -0.5F);
        wrapper.addChild(right_wrapperTie);
        
        cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(-2.0F, 2.5F, 8.0F);
        right_wrapperTie.addChild(cube_r2);
        setRotateAngle(cube_r2, 1.5708F, 1.5708F, 0.0F);
        cube_r2.setTextureOffset(0, 30).addBox(8.0F, -8.0F, -2.0F, 0.0F, 10.0F, 10.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(main, caramel_Core, Caramel_Layer, left_Eye, right_Eye, wrapper, left_wrapperTie, right_wrapperTie, cube_r1, cube_r2);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(main);
    }

    @Override
    public void setupAnim(CaramelCubeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        Entity look = Minecraft.getInstance().getCameraEntity();
        float partialTick = ageInTicks - entity.tickCount;
        float jumpProgress = entity.getJumpProgress(partialTick);
        float squishProgress = entity.getSquishProgress(partialTick);
        float jiggleTime = entity.getJiggleTime(partialTick);
        float squishJiggle = jiggleTime * (float) (1D + Math.sin(jiggleTime * Math.PI * 2.0F)) * 0.5F;
        float xzScale = 1F - 0.15F * jumpProgress + 0.2F * squishProgress + squishJiggle;
        float yScale = 1F + jumpProgress - 0.35F * squishProgress * 0.03F - squishJiggle * 0.5F;
        float invXzScale = 1F / Math.max(xzScale, 0.001F);
        float invYScale = 1F / Math.max(yScale, 0.001F);
        this.caramel_Core.setScale(xzScale, yScale, xzScale);
        this.wrapper.setScale(1F, invYScale, 1F);
        this.left_wrapperTie.setScale(invXzScale, 1F, invXzScale);
        this.right_wrapperTie.setScale(invXzScale, 1F, invXzScale);
        this.wrapper.setScale(1F, invYScale, 1F);
        this.right_Eye.setScale(invXzScale, invYScale, invXzScale);
        this.left_Eye.setScale(invXzScale, invYScale, invXzScale);
        this.caramel_Core.setShouldScaleChildren(true);
        this.wrapper.setShouldScaleChildren(true);
        this.left_wrapperTie.setShouldScaleChildren(true);
        this.right_wrapperTie.setShouldScaleChildren(true);
        this.caramel_Core.rotationPointY += jumpProgress * -5F - squishJiggle;
        this.wrapper.rotationPointY += (yScale - 1F) * -2.5F;
        this.right_Eye.rotationPointY += (yScale - 1F) * -0.75F;
        this.left_Eye.rotationPointY += (yScale - 1F) * -0.75F;
        if (look != null) {
            Vec3 vector3d = look.getEyePosition(0.0F);
            Vec3 vector3d1 = entity.getEyePosition(0.0F);
            double d0 = vector3d.y - vector3d1.y;
            float f1 = (float) Mth.clamp(-d0 * 0.7F, -0.7F, 0.7F);
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = (new Vec3(vector3d1.x - vector3d.x, 0.0D, vector3d1.z - vector3d.z)).normalize().yRot(((float) Math.PI / 2F));
            double d1 = vector3d2.dot(vector3d3);
            double d2 = Mth.sqrt((float) Math.abs(d1)) * (float) Math.signum(d1);
            this.left_Eye.rotationPointX += d2 - this.caramel_Core.rotateAngleZ;
            this.left_Eye.rotationPointY += f1;
            this.right_Eye.rotationPointX += d2 - this.caramel_Core.rotateAngleZ;
            this.right_Eye.rotationPointY += f1;
        }
    }
}
