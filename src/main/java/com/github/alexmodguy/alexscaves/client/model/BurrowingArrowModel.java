package com.github.alexmodguy.alexscaves.client.model;

import com.github.alexmodguy.alexscaves.server.entity.item.BurrowingArrowEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;

public class BurrowingArrowModel extends AdvancedEntityModel<BurrowingArrowEntity> {
	private final AdvancedModelBox main;
	private final AdvancedModelBox arrow;
	private final AdvancedModelBox cube_r1;
	private final AdvancedModelBox arrow_head;
	private final AdvancedModelBox ljaw;
	private final AdvancedModelBox cube_r2;
	private final AdvancedModelBox tjaw;
	private final AdvancedModelBox cube_r3;

	public BurrowingArrowModel() {
		texWidth = 32;
		texHeight = 32;

		main = new AdvancedModelBox(this);
		main.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		arrow = new AdvancedModelBox(this);
		arrow.setRotationPoint(0.0F, -2.5F, 2.0F);
		main.addChild(arrow);
		arrow.setTextureOffset(0, 5).addBox(0.0F, -2.5F, -6.0F, 0.0F, 5.0F, 12.0F, 0.0F, false);

		cube_r1 = new AdvancedModelBox(this);
		cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		arrow.addChild(cube_r1);
		setRotateAngle(cube_r1, 0.0F, 0.0F, 1.5708F);
		cube_r1.setTextureOffset(0, 0).addBox(0.0F, -2.5F, -6.0F, 0.0F, 5.0F, 12.0F, 0.0F, false);

		arrow_head = new AdvancedModelBox(this);
		arrow_head.setRotationPoint(0.0F, 0.0F, -6.0F);
		arrow.addChild(arrow_head);

		ljaw = new AdvancedModelBox(this);
		ljaw.setRotationPoint(0.0F, 0.0F, 0.0F);
		arrow_head.addChild(ljaw);

		cube_r2 = new AdvancedModelBox(this);
		cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
		ljaw.addChild(cube_r2);
		setRotateAngle(cube_r2, -0.6981F, 0.0F, 0.0F);
		cube_r2.setTextureOffset(0, 7).addBox(-2.0F, 0.0F, 0.5F, 4.0F, 3.0F, 2.0F, 0.0F, false);
		cube_r2.setTextureOffset(0, 0).addBox(-2.0F, 0.0F, -3.5F, 4.0F, 3.0F, 4.0F, 0.0F, false);

		tjaw = new AdvancedModelBox(this);
		tjaw.setRotationPoint(0.0F, 0.0F, 0.0F);
		arrow_head.addChild(tjaw);
		

		cube_r3 = new AdvancedModelBox(this);
		cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
		tjaw.addChild(cube_r3);
		setRotateAngle(cube_r3, 0.6981F, 0.0F, 0.0F);
		cube_r3.setTextureOffset(12, 3).addBox(-2.0F, -3.0F, -3.5F, 4.0F, 3.0F, 4.0F, 0.01F, false);
		cube_r3.setTextureOffset(0, 22).addBox(-2.0F, -3.0F, 0.5F, 4.0F, 3.0F, 2.0F, 0.01F, false);
		this.updateDefaultPose();
	}

	@Override
	public Iterable<BasicModelPart> parts() {
		return ImmutableList.of(main);
	}

	@Override
	public void setupAnim(BurrowingArrowEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
		float burrow = entity.getDiggingAmount(ageInTicks - entity.tickCount);
		this.main.rotationPointZ += Math.abs((float) (Math.cos(ageInTicks * 0.5F) * 3 * burrow));
		this.walk(tjaw, 1F, 0.6F, false, 1F, -0.6F, ageInTicks, burrow);
		this.walk(ljaw, 1F, 0.6F, true, 1F, -0.6F, ageInTicks, burrow);
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(main, arrow, cube_r1, cube_r2, cube_r3, tjaw, ljaw, arrow_head);
	}

}