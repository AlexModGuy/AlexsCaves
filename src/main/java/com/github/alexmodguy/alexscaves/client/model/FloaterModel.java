package com.github.alexmodguy.alexscaves.client.model;


import com.github.alexmodguy.alexscaves.server.entity.item.FloaterEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class FloaterModel extends AdvancedEntityModel<FloaterEntity> {
	private final AdvancedModelBox root;
	private final AdvancedModelBox main;
	private final AdvancedModelBox propellor;
	private final AdvancedModelBox cube_r1;

	public FloaterModel() {
		texWidth = 64;
		texHeight = 64;

		root = new AdvancedModelBox(this);
		root.setRotationPoint(0.0F, 24.0F, 0.0F);

		main = new AdvancedModelBox(this);
		main.setRotationPoint(0.0F, -9.0F, 0.0F);
		root.addChild(main);
		main.setTextureOffset(0, 16).addBox(-6.0F, -5.0F, -6.0F, 12.0F, 10.0F, 12.0F, 0.0F, false);

		propellor = new AdvancedModelBox(this);
		propellor.setRotationPoint(0.0F, 5.0F, 0.0F);
		main.addChild(propellor);
		propellor.setTextureOffset(0, 0).addBox(-8.0F, 4.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);
		propellor.setTextureOffset(36, 18).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 10.0F, 0.0F, 0.0F, false);

		cube_r1 = new AdvancedModelBox(this);
		cube_r1.setRotationPoint(0.0F, 12.0F, 0.0F);
		propellor.addChild(cube_r1);
		setRotateAngle(cube_r1, 0.0F, -1.5708F, 0.0F);
		cube_r1.setTextureOffset(36, 18).addBox(-6.0F, -18.0F, 0.0F, 12.0F, 10.0F, 0.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(root);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(root, cube_r1, propellor);
	}

	@Override
	public void setupAnim(FloaterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		this.propellor.rotateAngleY += ageInTicks * 0.5F;
	}
}