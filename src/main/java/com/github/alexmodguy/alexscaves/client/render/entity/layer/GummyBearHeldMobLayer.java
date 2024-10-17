package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.GummyBearModel;
import com.github.alexmodguy.alexscaves.client.render.entity.GummyBearRenderer;
import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GummyBearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;

public class GummyBearHeldMobLayer extends RenderLayer<GummyBearEntity, GummyBearModel> {

    public GummyBearHeldMobLayer(GummyBearRenderer render) {
        super(render);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GummyBearEntity bear, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Entity heldMob = bear.getHeldMob();
        if (heldMob != null) {
            float bodyYaw = heldMob.yRotO + (heldMob.getYRot() - heldMob.yRotO) * partialTicks;
            AlexsCaves.PROXY.releaseRenderingEntity(heldMob.getUUID());
            matrixStackIn.pushPose();
            getParentModel().translateToHand(HumanoidArm.RIGHT, matrixStackIn);
            matrixStackIn.translate(0.1F * bear.getScale(), 0.7F * bear.getScale(), -0.3F * bear.getScale());
            matrixStackIn.mulPose(Axis.XN.rotationDegrees(180F));
            matrixStackIn.mulPose(Axis.YN.rotationDegrees(-90F));
            matrixStackIn.mulPose(Axis.XN.rotationDegrees(-10F));
            if (!AlexsCaves.PROXY.isFirstPersonPlayer(heldMob)) {
                renderEntity(heldMob, 0, 0, 0, 0, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }
            matrixStackIn.popPose();
            AlexsCaves.PROXY.blockRenderingEntity(heldMob.getUUID());
        }
    }

    public <E extends Entity> void renderEntity(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        EntityRenderer<? super E> render = null;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            render = manager.getRenderer(entityIn);

            if (render != null) {
                try {
                    render.render(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
                } catch (Throwable throwable1) {
                    throw new ReportedException(CrashReport.forThrowable(throwable1, "Rendering entity in world"));
                }
            }
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.forThrowable(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
            entityIn.fillCrashReportCategory(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
            crashreportcategory1.setDetail("Assigned renderer", render);
            crashreportcategory1.setDetail("Rotation", Float.valueOf(yaw));
            crashreportcategory1.setDetail("Delta", Float.valueOf(partialTicks));
            throw new ReportedException(crashreport);
        }
    }
}
