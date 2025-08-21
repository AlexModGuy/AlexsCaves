package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.*;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;


public class NotorRenderer extends MobRenderer<NotorEntity, NotorModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/notor.png");
    private static final ResourceLocation TEXTURE_GLOW = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/notor_glow.png");
    private static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/notor_eyes.png");
    private static final List<NotorEntity> allOnScreen = new ArrayList<>();

    public NotorRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new NotorModel(), 0.25F);
        this.addLayer(new NotorRenderer.LayerGlow());
    }

    public boolean shouldRender(NotorEntity entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        } else {
            if (entity.getBeamProgress(1.0F) > 0) {
                Vec3 vec3 = entity.position();
                Vec3 vec31 = entity.getBeamEndPosition(1.0F);
                return camera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
            }
            return false;
        }
    }

    protected void scale(NotorEntity mob, PoseStack matrixStackIn, float partialTicks) {
    }

    public void render(NotorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, source, packedLight);
        Vec3 renderAt = entity.getPosition(partialTicks);
        if (entity.isAlive()) {
            Entity hologramEntity = entity.getHologramEntity();
            boolean scanning = !entity.showingHologram();
            Vec3 hologramScanPos = entity.getBeamEndPosition(partialTicks);
            float beamProgress = entity.getBeamProgress(partialTicks);
            if (hologramEntity != null && entity.showingHologram()) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.HOLOGRAM_SHADER);
                poseStack.pushPose();
                poseStack.translate(hologramScanPos.x - renderAt.x, hologramScanPos.y - renderAt.y, hologramScanPos.z - renderAt.z);
                poseStack.scale(1F, entity.getHologramProgress(partialTicks), 1F);
                poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 3F));
                renderEntityInHologram(hologramEntity, 0, 0, 0, 0, partialTicks, poseStack, source, 240);
                poseStack.popPose();
            }
            if (hologramScanPos != null) {
                Vec3 eyeOffset = entity.getViewVector(1.0F).scale(0.1F);
                Vec3 modelOffset = model.getChainPosition(Vec3.ZERO).add(eyeOffset);
                Vec3 toTranslate = hologramScanPos.subtract(entity.getPosition(partialTicks).add(modelOffset));
                float yRot = ((float) Mth.atan2(toTranslate.x, toTranslate.z)) * 180.0F / (float) Math.PI;
                float xRot = -(float) (Mth.atan2(toTranslate.y, toTranslate.horizontalDistance()) * (double) (180F / (float) Math.PI));
                float length = ((float) toTranslate.length() - (scanning ? 0 : 0)) * beamProgress;
                float width = hologramEntity == null ? 1.3F : hologramEntity.getBbHeight() / 2F;
                poseStack.pushPose();
                poseStack.translate(modelOffset.x, modelOffset.y, modelOffset.z);
                poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90));
                poseStack.mulPose(Axis.ZN.rotationDegrees(xRot));
                poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                if (scanning) {
                    poseStack.mulPose(Axis.YN.rotationDegrees(90));
                }
                PoseStack.Pose posestack$pose = poseStack.last();
                Matrix4f matrix4f1 = posestack$pose.pose();
                Matrix3f matrix3f1 = posestack$pose.normal();
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.HOLOGRAM_SHADER);
                VertexConsumer lightConsumer = source.getBuffer(ACRenderTypes.getHologramLights());
                shineOriginVertex(lightConsumer, matrix4f1, matrix3f1, 0, 0);
                shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
                shineRightCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
                shineLeftCornerVertex(lightConsumer, matrix4f1, matrix3f1, length, width, 0, 0);
                poseStack.popPose();
            }
        }

    }

    public static <E extends Entity> void renderEntityInHologram(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        PostEffectRegistry.renderEffectForNextTick(ClientProxy.HOLOGRAM_SHADER);

        EntityRenderer<? super E> render = null;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            render = manager.getRenderer(entityIn);
            float animSpeed = 0;
            float animSpeedOld = 0;
            float animPos = 0;
            float xRot = entityIn.getXRot();
            float xRotOld = entityIn.xRotO;
            float yRot = entityIn.getYRot();
            float yRotOld = entityIn.yRotO;
            float yBodyRot = 0;
            float yBodyRotOld = 0;
            float headRot = 0;
            float headRotOld = 0;
            if (entityIn instanceof LivingEntity living) {
                headRot = living.yHeadRot;
                headRotOld = living.yHeadRotO;
                yBodyRot = living.yBodyRot;
                yBodyRotOld = living.yBodyRotO;
                living.yHeadRot = 0;
                living.yHeadRotO = 0;
                living.yBodyRot = 0;
                living.yBodyRotO = 0;
                entityIn.setXRot(0);
                entityIn.xRotO = 0;
                entityIn.setYRot(0);
                entityIn.yRotO = 0;
                if (render instanceof LivingEntityRenderer renderer && renderer.getModel() != null) {
                    EntityModel model = renderer.getModel();
                    ResourceLocation texture = render.getTextureLocation(entityIn);
                    if(entityIn instanceof DeepOneMageEntity){
                        texture = DeepOneMageRenderer.TEXTURE;
                    }else if(entityIn instanceof GummyBearEntity gummyBearEntity && renderer instanceof GummyBearRenderer gummyBearRenderer){
                        texture = gummyBearRenderer.getOutsideTextureLocation(gummyBearEntity);
                        model = GummyBearRenderer.OUTSIDE_MODEL;
                    }
                    VertexConsumer ivertexbuilder = bufferIn.getBuffer(ACRenderTypes.getHologram(texture));
                    matrixStack.pushPose();
                    boolean shouldSit = entityIn.isPassenger() && (entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit());
                    model.young = living.isBaby();
                    model.riding = shouldSit;
                    model.attackTime = living.getAttackAnim(partialTicks);
                    boolean prevCrouching = false;
                    if (model instanceof HumanoidModel<?> humanoidModel) {
                        prevCrouching = humanoidModel.crouching;
                        humanoidModel.crouching = false;
                    }
                    if(model instanceof UnderzealotModel underzealotModel){
                        underzealotModel.noBurrowing = true;
                    }
                    if(model instanceof HullbreakerModel hullbreakerModel){
                        hullbreakerModel.straighten = true;
                    }
                    if(model instanceof SauropodBaseModel sauropodBaseModel){
                        sauropodBaseModel.straighten = true;
                    }
                    if(model instanceof TremorzillaModel tremorzillaModel){
                        tremorzillaModel.straighten = true;
                    }
                    model.setupAnim(living, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);

                    if(model instanceof UnderzealotModel underzealotModel){
                        underzealotModel.noBurrowing = false;
                    }
                    if(model instanceof HullbreakerModel hullbreakerModel){
                        hullbreakerModel.straighten = false;
                    }
                    if(model instanceof SauropodBaseModel sauropodBaseModel){
                        sauropodBaseModel.straighten = false;
                    }
                    if(model instanceof TremorzillaModel tremorzillaModel){
                        tremorzillaModel.straighten = false;
                    }
                    if(model instanceof GummyBearModel gummyBearModel){
                        gummyBearModel.ignoreColor = true;
                    }
                    matrixStack.scale(-living.getScale(), -living.getScale(), living.getScale());
                    ((LivingEntityRendererAccessor)renderer).scaleForHologram(living, matrixStack, partialTicks);
                    if(entityIn instanceof CaramelCubeEntity caramelCubeEntity){
                        float scaleBy = caramelCubeEntity.getSlimeSize() == 2 ? 4 : caramelCubeEntity.getSlimeSize() == 1 ? 2 : 1;
                        matrixStack.translate(0, -scaleBy * 0.25F, 0);
                    }
                    model.renderToBuffer(matrixStack, ivertexbuilder, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    matrixStack.popPose();
                    if (model instanceof HumanoidModel<?> humanoidModel) {
                        humanoidModel.crouching = prevCrouching;
                    }
                    if(model instanceof GummyBearModel gummyBearModel){
                        gummyBearModel.ignoreColor = false;
                    }
                }else if(render instanceof FerrouslimeRenderer && living instanceof FerrouslimeEntity ferrouslime){
                    matrixStack.pushPose();
                    matrixStack.translate(0, -1, 0);
                    matrixStack.scale(-living.getScale(), -living.getScale(), living.getScale());
                    VertexConsumer ivertexbuilder = bufferIn.getBuffer(ACRenderTypes.getHologram(render.getTextureLocation(entityIn)));
                    FerrouslimeRenderer.FERROUSLIME_MODEL.setupAnim(ferrouslime, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
                    FerrouslimeRenderer.FERROUSLIME_MODEL.renderToBuffer(matrixStack, ivertexbuilder, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    matrixStack.popPose();
                }
                entityIn.setXRot(xRot);
                entityIn.xRotO = xRotOld;
                entityIn.setYRot(yRot);
                entityIn.yRotO = yRotOld;
                living.yHeadRot = headRot;
                living.yHeadRotO = headRotOld;
                living.yBodyRot = yBodyRot;
                living.yBodyRotO = yBodyRotOld;
            }
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
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

    private static void shineOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, Matrix3f p_114092_, float xOffset, float yOffset) {
        p_114220_.vertex(p_114221_, 0.0F, 0.0F, 0.0F).color(255, 255, 255, 230).uv(xOffset + 0.5F, yOffset).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, Matrix3f p_114092_, float p_114217_, float p_114218_, float xOffset, float yOffset) {
        p_114215_.vertex(p_114216_, -ACMath.HALF_SQRT_3 * p_114218_, p_114217_, 0).color(0, 0, 255, 0).uv(xOffset, yOffset + 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, Matrix3f p_114092_, float p_114226_, float p_114227_, float xOffset, float yOffset) {
        p_114224_.vertex(p_114225_, ACMath.HALF_SQRT_3 * p_114227_, p_114226_, 0).color(0, 0, 255, 0).uv(xOffset + 1, yOffset + 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }


    public ResourceLocation getTextureLocation(NotorEntity entity) {
        return TEXTURE;
    }

    class LayerGlow extends RenderLayer<NotorEntity, NotorModel> {

        public LayerGlow() {
            super(NotorRenderer.this);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, NotorEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(ACRenderTypes.getGhostly(TEXTURE_GLOW));
            float alpha = 1F;
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
            VertexConsumer ivertexbuilder2;
            if (entitylivingbaseIn.getBeamProgress(partialTicks) > 0) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.HOLOGRAM_SHADER);
                ivertexbuilder2 = bufferIn.getBuffer(ACRenderTypes.getHologram(TEXTURE_EYES));
            } else {
                ivertexbuilder2 = bufferIn.getBuffer(RenderType.eyes(TEXTURE_EYES));
            }
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder2, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1);

        }
    }
}


