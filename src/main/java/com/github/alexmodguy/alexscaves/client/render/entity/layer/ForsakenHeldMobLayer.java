package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.ForsakenModel;
import com.github.alexmodguy.alexscaves.client.render.entity.ForsakenRenderer;
import com.github.alexmodguy.alexscaves.server.entity.living.ForsakenEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ForsakenHeldMobLayer extends RenderLayer<ForsakenEntity, ForsakenModel> {

    public ForsakenHeldMobLayer(ForsakenRenderer render) {
        super(render);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ForsakenEntity forsaken, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Entity heldMob = forsaken.getHeldMob();
        if (heldMob != null) {
            AlexsCaves.PROXY.releaseRenderingEntity(heldMob.getUUID());
            float vehicleRot = forsaken.yBodyRotO + (forsaken.yBodyRot - forsaken.yBodyRotO) * partialTicks;
            float riderRot = 0;
            float animationIntensity = ACMath.cullAnimationTick(forsaken.getAnimationTick(), 1F, forsaken.getAnimation(), partialTicks, 25, 30) * 0.75F;
            boolean right = forsaken.getAnimation() == ForsakenEntity.ANIMATION_RIGHT_PICKUP;
            float rightAmount = right ? 1 : -1;
            if (heldMob instanceof LivingEntity living) {
                riderRot = living.yBodyRotO + (living.yBodyRot - living.yBodyRotO) * partialTicks;
            }
            matrixStackIn.pushPose();
            Vec3 offset;
            if (right) {
                offset = new Vec3(0.8F + animationIntensity, 0.8F - animationIntensity, 0.35F * heldMob.getBbHeight() - animationIntensity * 0.5F);
            } else {
                offset = new Vec3(-0.8F - animationIntensity, 0.8F - animationIntensity, 0.35F * heldMob.getBbHeight() - animationIntensity * 0.5F);
            }
            Vec3 handPosition = getParentModel().getHandPosition(right, offset);
            matrixStackIn.translate(handPosition.x, handPosition.y, handPosition.z);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(vehicleRot - riderRot));
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
