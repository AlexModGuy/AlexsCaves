package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.CaniacModel;
import com.github.alexmodguy.alexscaves.client.model.GingerbreadManModel;
import com.github.alexmodguy.alexscaves.client.model.GumbeeperModel;
import com.github.alexmodguy.alexscaves.client.model.LicowitchModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.LicowitchPossessionLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GingerbreadManRenderer extends MobRenderer<GingerbreadManEntity, GingerbreadManModel> {
    private static final ResourceLocation[] TEXTURES_FOR_VARIANT = new ResourceLocation[GingerbreadManEntity.MAX_VARIANTS + 1];
    private static final ResourceLocation TEXTURE_ALEX = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_alex.png");
    private static final ResourceLocation TEXTURE_CARRO = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_carro.png");
    private static final ResourceLocation TEXTURE_DENO = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_deno.png");
    private static final ResourceLocation TEXTURE_GATETOH = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_gatetoh.png");
    private static final ResourceLocation TEXTURE_HOLIDAY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_holiday.png");
    private static final ResourceLocation TEXTURE_PINKY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_pinky.png");
    private static final ResourceLocation TEXTURE_PLUMMET = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_plummet.png");
    private static final ResourceLocation TEXTURE_VAKY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_vaky.png");
    private static final ResourceLocation TEXTURE_BLANK = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_blank.png");
    private static final ResourceLocation TEXTURE_TEAM_OVERLAY = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_team_overlay.png");

    static {
        for (int i = 0; i <= GingerbreadManEntity.MAX_VARIANTS; i++) {
            TEXTURES_FOR_VARIANT[i] = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_" + i + ".png");
        }
    }

    public GingerbreadManRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GingerbreadManModel(), 0.25F);
        this.addLayer(new ItemLayer(renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new TeamOverlayLayer());
        this.addLayer(new LicowitchPossessionLayer<>(this));
    }

    public ResourceLocation getTextureLocation(GingerbreadManEntity entity) {
        if(entity.hasCustomName()){
            String name = entity.getName().getString().toLowerCase();
            if(name.contains("alex")){
                return TEXTURE_ALEX;
            }else if(name.contains("carro")){
                return TEXTURE_CARRO;
            }else if(name.contains("deno")){
                return TEXTURE_DENO;
            }else if(name.contains("gatetoh")){
                return TEXTURE_GATETOH;
            }else if(name.contains("holiday")){
                return TEXTURE_HOLIDAY;
            }else if(name.contains("pinky")){
                return TEXTURE_PINKY;
            }else if(name.contains("plummet")){
                return TEXTURE_PLUMMET;
            }else if(name.contains("vaky")){
                return TEXTURE_VAKY;
            }
        }
        return TEXTURES_FOR_VARIANT[Mth.clamp(entity.getVariant(), 0, GingerbreadManEntity.MAX_VARIANTS)];
    }


    protected void setupRotations(GingerbreadManEntity entity, PoseStack poseStack, float bob, float yawIn, float partialTicks) {
        if (this.isShaking(entity)) {
            yawIn += (float) (Math.cos((double) entity.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }
        if (!entity.hasPose(Pose.SLEEPING)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawIn));
        }
        if (entity.deathTime > 0) {
            float f = ((float) entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }
            poseStack.mulPose(Axis.XP.rotationDegrees(f * this.getFlipDegrees(entity)));
        } else if (entity.isAutoSpinAttack()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - entity.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(((float) entity.tickCount + partialTicks) * -75.0F));
        } else if (entity.hasPose(Pose.SLEEPING)) {
            Direction direction = entity.getBedOrientation();
            float f1 = direction != null ? sleepDirectionToRotation(direction) : yawIn;
            poseStack.mulPose(Axis.YP.rotationDegrees(f1));
            poseStack.mulPose(Axis.ZP.rotationDegrees(this.getFlipDegrees(entity)));
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
        } else if (isEntityUpsideDown(entity)) {
            poseStack.translate(0.0F, entity.getBbHeight() + 0.1F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    private static float sleepDirectionToRotation(Direction p_115329_) {
        switch (p_115329_) {
            case SOUTH:
                return 90.0F;
            case WEST:
                return 0.0F;
            case NORTH:
                return 270.0F;
            case EAST:
                return 180.0F;
            default:
                return 0.0F;
        }
    }

    class TeamOverlayLayer extends RenderLayer<GingerbreadManEntity, GingerbreadManModel> {

        public TeamOverlayLayer() {
            super(GingerbreadManRenderer.this);
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, GingerbreadManEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(entitylivingbaseIn.isOvenSpawned()){
                VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_TEAM_OVERLAY));
                int color = entitylivingbaseIn.getGingerbreadTeamColor();
                float r = (float) ((color & 16711680) >> 16) / 255.0F;
                float g = (float) ((color & '\uff00') >> 8) / 255.0F;
                float b = (float) ((color & 255) >> 0) / 255.0F;
                this.getParentModel().renderToBuffer(poseStack, ivertexbuilder2, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), r, g, b, 1.0F);
            }

        }
    }

    private class ItemLayer extends ItemInHandLayer<GingerbreadManEntity, GingerbreadManModel> {

        private final ItemInHandRenderer witchItemInHandRenderer;

        private ItemLayer(ItemInHandRenderer itemInHandRenderer) {
            super(GingerbreadManRenderer.this, itemInHandRenderer);
            this.witchItemInHandRenderer = itemInHandRenderer;
        }

        @Override
        protected void renderArmWithItem(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            if (!itemStack.isEmpty() && livingEntity instanceof GingerbreadManEntity gingerbreadMan) {
                float partialTicks = Minecraft.getInstance().getPartialTick();
                float carryItemProgress = gingerbreadMan.getCarryItemProgress(partialTicks);
                boolean flag = humanoidArm == HumanoidArm.LEFT;
                poseStack.pushPose();
                this.getParentModel().translateToHand(humanoidArm, poseStack);
                poseStack.translate(0.2F * (flag ? 1F : -1F), -0.05F + 0.1F * carryItemProgress, -0.15F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-160.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(20.0F * carryItemProgress * (flag ? 1F : -1F)));
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(carryItemProgress * 90F));
                poseStack.translate(0, 0, -0.1F * carryItemProgress);
                this.witchItemInHandRenderer.renderItem(livingEntity, itemStack, displayContext, flag, poseStack, multiBufferSource, packedLight);
                poseStack.popPose();
            }
        }
    }
}


