package com.github.alexmodguy.alexscaves.client.model;// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class SubterranodonModel extends AdvancedEntityModel<SubterranodonEntity> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox jaw;
    private final AdvancedModelBox lwing;
    private final AdvancedModelBox lhand;
    private final AdvancedModelBox lwingTip;
    private final AdvancedModelBox rwing;
    private final AdvancedModelBox rhand;
    private final AdvancedModelBox rwingTip;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox ltalon;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rtalon;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox tailTip;

    public SubterranodonModel() {
        texWidth = 256;
        texHeight = 256;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 19.0F, -1.0F);
        body.setTextureOffset(0, 46).addBox(-5.0F, -5.0F, -7.0F, 10.0F, 10.0F, 14.0F, 0.0F, false);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(0.0F, -4.0F, -6.5F);
        body.addChild(neck);
        neck.setTextureOffset(74, 27).addBox(-2.0F, -1.0F, -8.5F, 4.0F, 5.0F, 8.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 1.0F, -5.5F);
        neck.addChild(head);
        head.setTextureOffset(48, 46).addBox(0.0F, -15.0F, -13.0F, 0.0F, 12.0F, 19.0F, 0.0F, false);
        head.setTextureOffset(0, 79).addBox(-3.0F, -6.0F, -8.0F, 6.0F, 7.0F, 9.0F, 0.0F, false);
        head.setTextureOffset(74, 10).addBox(-1.0F, -3.0F, -22.0F, 2.0F, 3.0F, 14.0F, 0.0F, false);
        head.setTextureOffset(51, 78).addBox(-2.0F, -7.0F, -32.0F, 4.0F, 9.0F, 10.0F, 0.0F, false);

        jaw = new AdvancedModelBox(this);
        jaw.setRotationPoint(0.0F, -1.0F, -8.0F);
        head.addChild(jaw);
        jaw.setTextureOffset(0, 0).addBox(-1.5F, -3.0F, -23.5F, 3.0F, 8.0F, 9.0F, 0.0F, false);
        jaw.setTextureOffset(71, 62).addBox(-0.5F, -1.0F, -15.0F, 1.0F, 3.0F, 15.0F, 0.0F, false);

        lwing = new AdvancedModelBox(this);
        lwing.setRotationPoint(4.5F, -3.0F, -5.0F);
        body.addChild(lwing);
        lwing.setTextureOffset(12, 36).addBox(0.5F, 0.5F, 2.0F, 26.0F, 0.0F, 10.0F, 0.0F, false);
        lwing.setTextureOffset(72, 42).addBox(0.5F, -1.0F, -2.0F, 26.0F, 3.0F, 4.0F, 0.0F, false);

        lhand = new AdvancedModelBox(this);
        lhand.setRotationPoint(25.0F, 0.5F, -2.0F);
        lwing.addChild(lhand);
        lhand.setTextureOffset(30, 87).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);

        lwingTip = new AdvancedModelBox(this);
        lwingTip.setRotationPoint(26.5F, 0.5F, -1.0F);
        lwing.addChild(lwingTip);
        lwingTip.setTextureOffset(0, 0).addBox(0.0F, 0.0F, 1.0F, 26.0F, 0.0F, 24.0F, 0.0F, false);
        lwingTip.setTextureOffset(72, 49).addBox(0.0F, -1.0F, -1.0F, 26.0F, 2.0F, 2.0F, 0.0F, false);

        rwing = new AdvancedModelBox(this);
        rwing.setRotationPoint(-4.5F, -3.0F, -5.0F);
        body.addChild(rwing);
        rwing.setTextureOffset(12, 36).addBox(-26.5F, 0.5F, 2.0F, 26.0F, 0.0F, 10.0F, 0.0F, true);
        rwing.setTextureOffset(72, 42).addBox(-26.5F, -1.0F, -2.0F, 26.0F, 3.0F, 4.0F, 0.0F, true);

        rhand = new AdvancedModelBox(this);
        rhand.setRotationPoint(-25.0F, 2.0F, -2.0F);
        rwing.addChild(rhand);
        rhand.setTextureOffset(30, 87).addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 4.0F, 0.0F, false);

        rwingTip = new AdvancedModelBox(this);
        rwingTip.setRotationPoint(-26.5F, 0.5F, -1.0F);
        rwing.addChild(rwingTip);
        rwingTip.setTextureOffset(72, 49).addBox(-26.0F, -1.0F, -1.0F, 26.0F, 2.0F, 2.0F, 0.0F, true);
        rwingTip.setTextureOffset(0, 0).addBox(-26.0F, 0.0F, 1.0F, 26.0F, 0.0F, 24.0F, 0.0F, true);

        lleg = new AdvancedModelBox(this);
        lleg.setRotationPoint(2.0F, -2.5F, 6.5F);
        body.addChild(lleg);
        lleg.setTextureOffset(25, 70).addBox(-2.0F, 0.0F, -0.5F, 5.0F, 0.0F, 13.0F, 0.0F, false);

        ltalon = new AdvancedModelBox(this);
        ltalon.setRotationPoint(0.5F, 0.0F, 12.5F);
        lleg.addChild(ltalon);
        ltalon.setTextureOffset(98, 30).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 4.0F, 4.0F, 0.0F, false);

        rleg = new AdvancedModelBox(this);
        rleg.setRotationPoint(-2.0F, -2.5F, 6.5F);
        body.addChild(rleg);
        rleg.setTextureOffset(25, 70).addBox(-3.0F, 0.0F, -0.5F, 5.0F, 0.0F, 13.0F, 0.0F, true);

        rtalon = new AdvancedModelBox(this);
        rtalon.setRotationPoint(-0.5F, 0.0F, 12.5F);
        rleg.addChild(rtalon);
        rtalon.setTextureOffset(98, 30).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 4.0F, 4.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -3.5F, 6.5F);
        body.addChild(tail);
        tail.setTextureOffset(41, 46).addBox(-3.0F, 0.0F, -0.5F, 6.0F, 0.0F, 19.0F, 0.0F, false);

        tailTip = new AdvancedModelBox(this);
        tailTip.setRotationPoint(0.0F, 0.0F, 18.5F);
        tail.addChild(tailTip);
        tailTip.setTextureOffset(0, 51).addBox(0.0F, -5.0F, 0.0F, 0.0F, 9.0F, 19.0F, 0.0F, false);
        tailTip.setTextureOffset(29, 46).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 0.0F, 19.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public void setupAnim(SubterranodonEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 1F;
        float walkDegree = 1F;
        float partialTick = ageInTicks - entity.tickCount;
        float flyProgress = entity.getFlyProgress(partialTick);
        float buryEggsAmount = entity.getBuryEggsProgress(partialTick);
        float groundProgress = 1F - flyProgress;
        float flapAmount = flyProgress * entity.getFlapAmount(partialTick);
        float groundStill = groundProgress * (1F - limbSwingAmount);
        float groundMove = groundProgress * limbSwingAmount;
        float glide = flyProgress * (1 - flapAmount);
        float hoverProgress = flyProgress * entity.getHoverProgress(partialTick);
        float openMouthProgress = entity.getBiteProgress(partialTick);
        float sitProgress = entity.getSitProgress(partialTick) * groundProgress;
        float rollAmount = entity.getFlightRoll(partialTick) / 57.295776F * flyProgress;
        float pitchAmount = entity.getFlightPitch(partialTick) / 57.295776F * (flyProgress - hoverProgress);
        float yaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTick;
        float tailYaw = Mth.wrapDegrees(entity.getTailYaw(partialTick) - yaw) / 57.295776F;
        float danceAmount = entity.getDanceProgress(partialTick);
        float danceSpeed = 0.5F;
        progressPositionPrev(body, groundProgress, 0, -8, 2, 1F);
        progressPositionPrev(rwing, groundProgress, 3, -1, 1, 1F);
        progressPositionPrev(lwing, groundProgress, -3, -1, 1, 1F);
        progressRotationPrev(lleg, groundProgress, (float) Math.toRadians(-70), 0, 0, 1F);
        progressRotationPrev(rleg, groundProgress, (float) Math.toRadians(-70), 0, 0, 1F);
        progressRotationPrev(ltalon, groundProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(rtalon, groundProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(lwing, groundProgress, 0, (float) Math.toRadians(10), (float) Math.toRadians(35), 1F);
        progressRotationPrev(rwing, groundProgress, 0, (float) Math.toRadians(-10), (float) Math.toRadians(-35), 1F);
        progressRotationPrev(lwingTip, groundProgress, 0, (float) Math.toRadians(-10), (float) Math.toRadians(-130), 1F);
        progressRotationPrev(rwingTip, groundProgress, 0, (float) Math.toRadians(10), (float) Math.toRadians(130), 1F);
        progressRotationPrev(lhand, groundProgress, 0, (float) Math.toRadians(-10), (float) Math.toRadians(-35), 1F);
        progressRotationPrev(rhand, groundProgress, 0, (float) Math.toRadians(10), (float) Math.toRadians(35), 1F);
        progressRotationPrev(tail, groundProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(tailTip, groundProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(tail, groundStill, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(tailTip, groundStill, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(jaw, openMouthProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressPositionPrev(body, sitProgress, 0, -1, 0, 1F);
        progressPositionPrev(lleg, sitProgress, 0, -2, -3, 1F);
        progressPositionPrev(rleg, sitProgress, 0, -2, -3, 1F);
        progressPositionPrev(lwing, sitProgress, 1, 1, -1, 1F);
        progressPositionPrev(rwing, sitProgress, -1, 1, -1, 1F);
        progressRotationPrev(body, sitProgress, (float) Math.toRadians(-50), 0, 0, 1F);
        progressRotationPrev(neck, sitProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(head, sitProgress, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(tail, sitProgress, (float) Math.toRadians(55), 0, 0, 1F);
        progressRotationPrev(tailTip, sitProgress, (float) Math.toRadians(10), 0, 0, 1F);
        progressRotationPrev(lleg, sitProgress, (float) Math.toRadians(15), (float) Math.toRadians(15), 0, 1F);
        progressRotationPrev(rleg, sitProgress, (float) Math.toRadians(15), (float) Math.toRadians(-15), 0, 1F);
        progressRotationPrev(lwing, sitProgress, (float) Math.toRadians(40), (float) Math.toRadians(-20), (float) Math.toRadians(20), 1F);
        progressRotationPrev(rwing, sitProgress, (float) Math.toRadians(40), (float) Math.toRadians(20), (float) Math.toRadians(-20), 1F);
        progressRotationPrev(lhand, sitProgress, (float) Math.toRadians(20), 0, 0, 1F);
        progressRotationPrev(rhand, sitProgress, (float) Math.toRadians(20), 0, 0, 1F);
        animateFlight(ageInTicks, flyProgress, hoverProgress, glide, flapAmount, entity.isVehicle(), true);
        if (buryEggsAmount > 0.0F) {
            limbSwing = ageInTicks;
            groundMove = buryEggsAmount * 0.5F;
            this.body.swing(0.25F, 0.4F, false, 0F, 0F, ageInTicks, buryEggsAmount);
            this.neck.swing(0.25F, 0.4F, true, -1F, 0F, ageInTicks, buryEggsAmount);
        }
        this.swing(tail, 0.1F, 0.2F, false, 2F, 0F, ageInTicks, 1);
        this.swing(tailTip, 0.1F, 0.2F, false, 1F, 0F, ageInTicks, 1);
        this.swing(lwing, walkSpeed, walkDegree, false, -1F, 0.2F, limbSwing, groundMove);
        this.swing(rwing, walkSpeed, walkDegree, false, -1F, -0.2F, limbSwing, groundMove);
        this.flap(lwingTip, walkSpeed, walkDegree * 1, true, 1F, -0.2F, limbSwing, groundMove);
        this.flap(rwingTip, walkSpeed, walkDegree * 1, true, 1F, 0.2F, limbSwing, groundMove);
        this.walk(lhand, walkSpeed, walkDegree, true, 0F, -0.2F, limbSwing, groundMove);
        this.walk(rhand, walkSpeed, walkDegree, false, 0F, -0.2F, limbSwing, groundMove);
        this.swing(neck, walkSpeed, walkDegree * 0.5F, false, 1F, 0F, limbSwing, groundMove);
        this.swing(head, walkSpeed, walkDegree * 0.5F, true, 0.5F, 0F, limbSwing, groundMove);
        this.walk(lleg, walkSpeed, walkDegree * 0.6F, false, -2F, 0.3F, limbSwing, groundMove);
        this.walk(rleg, walkSpeed, walkDegree * 0.6F, true, -2F, -0.3F, limbSwing, groundMove);
        this.walk(ltalon, walkSpeed, walkDegree * 0.6F, false, -1F, -0.3F, limbSwing, groundMove);
        this.walk(rtalon, walkSpeed, walkDegree * 0.6F, true, -1F, 0.3F, limbSwing, groundMove);
        this.swing(tail, walkSpeed, walkDegree * 0.5F, true, 0.5F, 0F, limbSwing, groundMove);
        this.bob(body, walkSpeed * 2F, walkDegree * 3, false, limbSwing, groundMove);
        this.bob(neck, 0.1F, 0.5F, false, ageInTicks, 1);
        this.faceTarget(netHeadYaw, headPitch, 1, head, neck);
        this.swing(neck, danceSpeed, 0.5F, true, 0F, 0F, ageInTicks, danceAmount);
        this.swing(head, danceSpeed, 0.25F, true, 1F, 0F, ageInTicks, danceAmount);
        this.flap(head, danceSpeed, 0.25F, true, 1F, 0F, ageInTicks, danceAmount);
        this.walk(jaw, danceSpeed, 0.25F, false, 2F, 0.2F, ageInTicks, danceAmount);
        this.swing(body, danceSpeed, 0.1F, false, 1, 0, ageInTicks, danceAmount);
        body.rotateAngleX += pitchAmount;
        body.rotateAngleZ += rollAmount;
        tail.rotateAngleY += tailYaw * 0.8F;
        tailTip.rotateAngleY += tailYaw * 0.2F;
        lleg.rotateAngleY += tailYaw * flyProgress * 0.4F;
        rleg.rotateAngleY += tailYaw * flyProgress * 0.4F;
    }

    private void animateFlight(float ageInTicks, float flyProgress, float hoverProgress, float glide, float flapAmount, boolean carrying, boolean bob){
        progressPositionPrev(head, glide, 0, 3, 0, 1F);
        progressPositionPrev(neck, glide, 0, 1, 0, 1F);
        progressRotationPrev(body, hoverProgress, (float) Math.toRadians(-50), 0, 0, 1F);
        progressRotationPrev(neck, hoverProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(head, hoverProgress, (float) Math.toRadians(30), 0, 0, 1F);
        progressRotationPrev(lleg, hoverProgress, (float) Math.toRadians(-40), 0, 0, 1F);
        progressRotationPrev(rleg, hoverProgress, (float) Math.toRadians(-40), 0, 0, 1F);
        progressRotationPrev(ltalon, hoverProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressRotationPrev(rtalon, hoverProgress, (float) Math.toRadians(-20), 0, 0, 1F);
        progressPositionPrev(neck, glide, 0, 1, 0, 1F);
        this.walk(lleg, 0.3F, 0.2F, false, 1F, -0.1F, ageInTicks, flyProgress);
        this.walk(rleg, 0.3F, 0.2F, false, 1F, -0.1F, ageInTicks, flyProgress);
        this.flap(rwing, 0.5F, 1F, false, 1F, -0.2F, ageInTicks, flapAmount);
        this.flap(lwing, 0.5F, 1F, true, 1F, -0.2F, ageInTicks, flapAmount);
        this.flap(rwingTip, 0.5F, 0.5F, false, 0F, -0.2F, ageInTicks, flapAmount);
        this.flap(lwingTip, 0.5F, 0.5F, true, 0F, -0.2F, ageInTicks, flapAmount);
        float bodyFlightBob = bob ? ACMath.walkValue(ageInTicks, flapAmount, 0.5F, 0F, 4, false) : 0;
        this.body.rotationPointY -= bodyFlightBob;
        this.bob(neck, 0.5F, -1, false, ageInTicks, flapAmount);
        this.walk(neck, 0.5F, 0.1F, false, 1F, 0F, ageInTicks, flyProgress);
        if (carrying) {
            this.walk(lleg, 0.3F, 0.2F, true, 1F, -0.1F, ageInTicks, flyProgress);
            this.walk(rleg, 0.3F, 0.2F, true, 1F, -0.1F, ageInTicks, flyProgress);
            this.rleg.rotationPointY += bodyFlightBob * 0.25F;
            this.rleg.rotationPointZ += bodyFlightBob * 0.25F;
            this.lleg.rotationPointY += bodyFlightBob * 0.25F;
            this.lleg.rotationPointZ += bodyFlightBob * 0.25F;
        }
        this.walk(rwing, 2F, 0.05F, false, 2F, 0.1F, ageInTicks, glide);
        this.walk(lwing, 2F, 0.05F, false, 2F, 0.1F, ageInTicks, glide);
    }

    public Vec3 getLegPosition(boolean right, Vec3 offsetIn) {
        PoseStack translationStack = new PoseStack();
        translationStack.pushPose();
        body.translateAndRotate(translationStack);
        if (right) {
            rleg.translateAndRotate(translationStack);
        } else {
            lleg.translateAndRotate(translationStack);
        }
        Vector4f armOffsetVec = new Vector4f((float) offsetIn.x, (float) offsetIn.y, (float) offsetIn.z, 1.0F);
        armOffsetVec.mul(translationStack.last().pose());
        Vec3 vec3 = new Vec3(armOffsetVec.x(), armOffsetVec.y(), armOffsetVec.z());
        translationStack.popPose();
        return vec3;
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.5F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.35F, 0.35F, 0.35F);
            matrixStackIn.translate(0.0D, 2.75D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            head.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, tail, tailTip, lleg, rleg, ltalon, rtalon, neck, head, jaw, lwing, lwingTip, lhand, rwing, rwingTip, rhand);
    }

    public void animateSpirit(DinosaurSpiritEntity entityIn, float partialTicks) {
        this.resetToDefaultPose();
        float ageInTicks = entityIn.tickCount + partialTicks;
        float flyProgress = 1f;
        float flapAmount = flyProgress;
        float hoverProgress = 1F;
        this.body.rotationPointY -= 16;
        animateFlight(ageInTicks, flyProgress, hoverProgress, 0F, flapAmount, true, false);
    }
}