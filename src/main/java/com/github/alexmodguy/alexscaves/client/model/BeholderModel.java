package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class BeholderModel extends AdvancedEntityModel {
	private final AdvancedModelBox main;
	private final AdvancedModelBox stand;
	private final AdvancedModelBox cube_r1;
	private final AdvancedModelBox eye;

	public BeholderModel() {
		texWidth = 64;
		texHeight = 64;

		main = new AdvancedModelBox(this);
		main.setRotationPoint(0.0F, 24.0F, 0.0F);

		stand = new AdvancedModelBox(this);
		stand.setRotationPoint(0.0F, 0.0F, 0.0F);
		main.addChild(stand);
		stand.setTextureOffset(20, 8).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
		stand.setTextureOffset(0, 0).addBox(0.0F, -16.0F, -8.0F, 0.0F, 14.0F, 16.0F, 0.0F, false);

		cube_r1 = new AdvancedModelBox(this);
		cube_r1.setRotationPoint(0.0F, -10.0F, 0.0F);
		stand.addChild(cube_r1);
		setRotateAngle(cube_r1, 0.0F, -1.5708F, 0.0F);
		cube_r1.setTextureOffset(0, 0).addBox(0.0F, -6.0F, -8.0F, 0.0F, 14.0F, 16.0F, 0.0F, false);

		eye = new AdvancedModelBox(this);
		eye.setRotationPoint(0.0F, -9.5F, 0.0F);
		stand.addChild(eye);
		eye.setTextureOffset(1, 1).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, 0.0F, false);
		this.updateDefaultPose();
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(main);
	}

	@Override
	public void setupAnim(Entity entity, float eyeXRot, float eyeYRot, float ageInTicks, float unused0, float unused1) {
		this.resetToDefaultPose();
		this.eye.rotationPointY += Math.sin(ageInTicks * 0.2F) * 0.5F;
		this.eye.rotateAngleY = (float) Math.toRadians(eyeYRot);
		this.eye.rotateAngleX = (float) Math.toRadians(eyeXRot);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(main, stand, cube_r1, eye);
	}

	public void hideEye(boolean firstPersonView) {
		this.eye.showModel = !firstPersonView;
	}
}