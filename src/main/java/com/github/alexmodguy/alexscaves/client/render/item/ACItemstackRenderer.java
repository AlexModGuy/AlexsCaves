package com.github.alexmodguy.alexscaves.client.render.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.gui.book.widget.ItemWidget;
import com.github.alexmodguy.alexscaves.client.model.*;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.misc.CaveMapRenderHelper;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.item.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeRenderTypes;

public class ACItemstackRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation GALENA_GAUNTLET_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/galena_gauntlet.png");
    private static final ResourceLocation GALENA_GAUNTLET_RED_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/galena_gauntlet_red.png");
    private static final ResourceLocation GALENA_GAUNTLET_BLUE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/galena_gauntlet_blue.png");
    private static final GalenaGauntletModel GALENA_GAUNTLET_RIGHT_MODEL = new GalenaGauntletModel(false);
    private static final GalenaGauntletModel GALENA_GAUNTLET_LEFT_MODEL = new GalenaGauntletModel(true);
    private static final ResourceLocation RESISTOR_SHIELD_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/resistor_shield.png");
    private static final ResourceLocation RESISTOR_SHIELD_RED_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/resistor_shield_red.png");
    private static final ResourceLocation RESISTOR_SHIELD_BLUE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/resistor_shield_blue.png");
    private static final ResistorShieldModel RESISTOR_SHIELD_MODEL = new ResistorShieldModel();
    private static final ResourceLocation PRIMITIVE_CLUB_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/primitive_club.png");
    private static final PrimitiveClubModel PRIMITIVE_CLUB_MODEL = new PrimitiveClubModel();
    private static final ResourceLocation LIMESTONE_SPEAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/limestone_spear.png");
    private static final LimestoneSpearModel LIMESTONE_SPEAR_MODEL = new LimestoneSpearModel();
    private static final ResourceLocation EXTINCTION_SPEAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/extinction_spear.png");
    private static final ExtinctionSpearModel EXTINCTION_SPEAR_MODEL = new ExtinctionSpearModel();
    private static final ResourceLocation SIREN_LIGHT_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/siren_light.png");
    private static final ResourceLocation SIREN_LIGHT_COLOR_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/siren_light_color.png");
    private static final SirenLightModel SIREN_LIGHT_MODEL = new SirenLightModel();
    private static final ResourceLocation RAYGUN_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raygun/raygun.png");
    private static final ResourceLocation RAYGUN_ACTIVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raygun/raygun_active.png");
    private static final ResourceLocation RAYGUN_BLUE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raygun/raygun_blue.png");
    private static final ResourceLocation RAYGUN_BLUE_ACTIVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raygun/raygun_blue_active.png");
    private static final RaygunModel RAYGUN_MODEL = new RaygunModel();
    private static final ResourceLocation SEA_STAFF_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/sea_staff.png");
    private static final SeaStaffModel SEA_STAFF_MODEL = new SeaStaffModel();
    private static final ResourceLocation ORTHOLANCE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/deep_one/ortholance.png");
    private static final OrtholanceModel ORTHOLANCE_MODEL = new OrtholanceModel();
    private static final ResourceLocation COPPER_VALVE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/copper_valve.png");
    private static final CopperValveModel COPPER_VALVE_MODEL = new CopperValveModel();
    private static final BeholderModel BEHOLDER_MODEL = new BeholderModel();
    private static final ResourceLocation BEHOLDER_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/beholder.png");
    private static final ResourceLocation BEHOLDER_TEXTURE_EYE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/beholder_eye.png");
    private static final ResourceLocation DREADBOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/dreadbow.png");
    private static final ResourceLocation DREADBOW_TEXTURE_EYE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/dreadbow_eye.png");
    private static final ResourceLocation DREADBOW_TEXTURE_EYE_PERFECT = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/dreadbow_eye_perfect.png");
    private static final DreadbowModel DREADBOW_MODEL = new DreadbowModel();
    private static final GobthumperModel GOBTHUMPER_MODEL = new GobthumperModel();
    private static final ResourceLocation GOBTHUMPER_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gobthumper.png");
    private static final ResourceLocation GOBTHUMPER_JELLY_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gobthumper_jelly.png");
    private static final ResourceLocation SHOT_GUM_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/shot_gum.png");
    private static final ResourceLocation SHOT_GUM_GLASS_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/shot_gum_glass.png");
    private static final ShotGumModel SHOT_GUM_MODEL = new ShotGumModel();
    private static final ResourceLocation SUGAR_STAFF_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sugar_staff.png");
    private static final SugarStaffModel SUGAR_STAFF_MODEL = new SugarStaffModel();
    private static final ResourceLocation FROSTMINT_SPEAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/frostmint_spear.png");
    private static final FrostmintSpearModel FROSTMINT_SPEAR_MODEL = new FrostmintSpearModel();

    public static boolean sepiaFlag = false;

    private Entity renderedDreadbowArrow = null;

    public ACItemstackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ClientLevel level = Minecraft.getInstance().level;
        float partialTick = Minecraft.getInstance().getPartialTick();
        boolean heldIn3d = transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        boolean left = transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        if (itemStackIn.is(ACItemRegistry.CAVE_MAP.get())) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.CAVE_MAP_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            boolean done = CaveMapItem.isFilled(itemStackIn) && !CaveMapItem.isLoading(itemStackIn);
            if(done){
                spriteItem = new ItemStack(ACItemRegistry.CAVE_MAP_FILLED_SPRITE.get());
            }else if(CaveMapItem.isLoading(itemStackIn)){
                spriteItem = new ItemStack(ACItemRegistry.CAVE_MAP_LOADING_SPRITE.get());
            }
            if (transformType.firstPerson() && done) {
                Player player = Minecraft.getInstance().player;
                ItemStack offhandHeldItem = player.getItemInHand(InteractionHand.OFF_HAND);
                boolean renderingSmallMap = !offhandHeldItem.isEmpty();
                boolean offhand = offhandHeldItem.equals(itemStackIn);
                poseStack.pushPose();
                if(renderingSmallMap){
                    poseStack.translate(left ? 0.5F : -0.5F, 0.35, 0);
                    CaveMapRenderHelper.renderOneHandedCaveMap(poseStack, bufferIn, combinedLightIn, 0, offhand ? player.getMainArm().getOpposite() : player.getMainArm(), 0, itemStackIn);
                }else{
                    poseStack.translate(left ? 0.55F : -0.55F, 0.525F, 0.75F);
                    CaveMapRenderHelper.renderTwoHandedCaveMap(poseStack, bufferIn, combinedLightIn, partialTick, 0, 0, itemStackIn);
                }
                poseStack.popPose();
            } else if(heldIn3d && AlexsCaves.CLIENT_CONFIG.caveMapsVisibleInThirdPerson.get() && done){
                poseStack.translate(left ? 0.15F : -0.15F, 0.25F, 0.05F);
                poseStack.scale(1.5F, 1.5F, 1.5F);
                CaveMapRenderHelper.renderCaveMap(poseStack, bufferIn, combinedLightIn, itemStackIn, true);
            }else{
                renderStaticItemSpriteWithLighting(spriteItem, transformType, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, level);
            }
        }

        if (itemStackIn.is(ACItemRegistry.GALENA_GAUNTLET.get())) {
            poseStack.pushPose();
            poseStack.translate(0, 0F, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.mulPose(Axis.YP.rotationDegrees(-180));
            float openAmount = GalenaGauntletItem.getLerpedUseTime(itemStackIn, partialTick) / 5F;
            float closeAmount = 1F - openAmount;
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            if (left || transformType == ItemDisplayContext.GUI) {
                GALENA_GAUNTLET_LEFT_MODEL.setupAnim(null, openAmount, 0, ageInTicks, 0, 0);
                GALENA_GAUNTLET_LEFT_MODEL.renderToBuffer(poseStack, getVertexConsumerFoil(bufferIn, RenderType.entityCutoutNoCull(GALENA_GAUNTLET_TEXTURE), GALENA_GAUNTLET_TEXTURE, itemStackIn.hasFoil()), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                GALENA_GAUNTLET_LEFT_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_BLUE_TEXTURE), GALENA_GAUNTLET_BLUE_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, openAmount);
                GALENA_GAUNTLET_LEFT_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_RED_TEXTURE), GALENA_GAUNTLET_RED_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, closeAmount);
            } else {
                GALENA_GAUNTLET_RIGHT_MODEL.setupAnim(null, openAmount, 0, ageInTicks, 0, 0);
                GALENA_GAUNTLET_RIGHT_MODEL.renderToBuffer(poseStack, getVertexConsumerFoil(bufferIn, RenderType.entityCutoutNoCull(GALENA_GAUNTLET_TEXTURE), GALENA_GAUNTLET_TEXTURE, itemStackIn.hasFoil()), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                GALENA_GAUNTLET_RIGHT_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_BLUE_TEXTURE), GALENA_GAUNTLET_BLUE_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, openAmount);
                GALENA_GAUNTLET_RIGHT_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, ACRenderTypes.getEyesAlphaEnabled(GALENA_GAUNTLET_RED_TEXTURE), GALENA_GAUNTLET_RED_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, closeAmount);
            }
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.RESISTOR_SHIELD.get())) {
            poseStack.pushPose();
            poseStack.translate(0, 0.25F, 0.125F);
            float useTime = ResistorShieldItem.getLerpedUseTime(itemStackIn, partialTick);
            float useProgress = Math.min(10F, useTime) / 10F;
            float useProgressTurn = Math.min(useProgress * 4F, 1F);
            float useProgressUp = (float) Math.sin(useProgress * Math.PI);
            float switchProgress = ResistorShieldItem.getLerpedSwitchTime(itemStackIn, partialTick) / 5F;
            float leftOffset = left ? -1F : 1F;
            if (transformType.firstPerson()) {
                poseStack.translate(useProgressTurn * 0.2F * leftOffset, useProgressUp, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(useProgressTurn * -10));
            } else if (heldIn3d) {
                poseStack.translate(useProgressTurn * 0.4F * leftOffset, useProgress * -0.1F, useProgressTurn * -0.2F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(useProgressTurn * 10 * leftOffset));
                poseStack.mulPose(Axis.YP.rotationDegrees(useProgressTurn * 80 * leftOffset));
            }
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            RESISTOR_SHIELD_MODEL.setupAnim(null, useProgress, switchProgress, 0, 0, 0);
            RESISTOR_SHIELD_MODEL.renderToBuffer(poseStack, getVertexConsumerFoil(bufferIn, RenderType.entityCutoutNoCull(RESISTOR_SHIELD_TEXTURE), RESISTOR_SHIELD_TEXTURE, itemStackIn.hasFoil()), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            RESISTOR_SHIELD_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, ACRenderTypes.getEyesAlphaEnabled(RESISTOR_SHIELD_RED_TEXTURE), RESISTOR_SHIELD_RED_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, switchProgress);
            RESISTOR_SHIELD_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, ACRenderTypes.getEyesAlphaEnabled(RESISTOR_SHIELD_BLUE_TEXTURE), RESISTOR_SHIELD_BLUE_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F - switchProgress);
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.PRIMITIVE_CLUB.get())) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.PRIMITIVE_CLUB_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -1.15F, -0.1F);
                if (transformType.firstPerson()) {
                    poseStack.translate(0, 0.1F, 0);
                    poseStack.scale(0.8F, 0.8F, 0.8F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(PRIMITIVE_CLUB_TEXTURE), false, itemStackIn.hasFoil());
                PRIMITIVE_CLUB_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                renderStaticItemSprite(spriteItem, transformType, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, level);
            }
        }

        if (itemStackIn.is(ACItemRegistry.LIMESTONE_SPEAR.get())) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.LIMESTONE_SPEAR_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -0.85F, -0.1F);
                if (transformType.firstPerson()) {
                    poseStack.translate(0, 0.5F, 0F);
                    poseStack.scale(0.75F, 0.75F, 0.75F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(LIMESTONE_SPEAR_TEXTURE), false, itemStackIn.hasFoil());
                LIMESTONE_SPEAR_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                renderStaticItemSprite(spriteItem, transformType, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, level);
            }
        }

        if (itemStackIn.is(ACItemRegistry.EXTINCTION_SPEAR.get())) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.EXTINCTION_SPEAR_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -0.85F, -0.1F);
                if (transformType.firstPerson()) {
                    poseStack.translate(0, 0.5F, 0F);
                    poseStack.scale(0.75F, 0.75F, 0.75F);
                }
                EXTINCTION_SPEAR_MODEL.resetToDefaultPose();
                VertexConsumer vertexconsumer1 = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(EXTINCTION_SPEAR_TEXTURE), false, itemStackIn.hasFoil());
                EXTINCTION_SPEAR_MODEL.renderToBuffer(poseStack, vertexconsumer1, 240, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                VertexConsumer vertexconsumer2 = ItemRenderer.getArmorFoilBuffer(bufferIn, ForgeRenderTypes.getUnlitTranslucent(EXTINCTION_SPEAR_TEXTURE), false, itemStackIn.hasFoil());
                EXTINCTION_SPEAR_MODEL.renderToBuffer(poseStack, vertexconsumer2, 240, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                renderStaticItemSprite(spriteItem, transformType, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, level);
            }
        }

        if (itemStackIn.is(ACBlockRegistry.SIREN_LIGHT.get().asItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            SIREN_LIGHT_MODEL.resetToDefaultPose();
            SIREN_LIGHT_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, RenderType.entityCutoutNoCull(SIREN_LIGHT_TEXTURE), SIREN_LIGHT_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            SIREN_LIGHT_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, RenderType.entityTranslucent(SIREN_LIGHT_COLOR_TEXTURE), SIREN_LIGHT_COLOR_TEXTURE), combinedLightIn, combinedOverlayIn, 0.0F, 1.0F, 0.0F, 1.0F);
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.RAYGUN.get())) {
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            float useAmount = RaygunItem.getLerpedUseTime(itemStackIn, partialTick) / 5F;
            float pulseAlpha = useAmount * (0.25F + 0.25F * (float) (1F + Math.sin(ageInTicks * 0.8F)));
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.pushPose();
            poseStack.scale(0.9F, 0.9F, 0.9F);
            RAYGUN_MODEL.setupAnim(null, useAmount, ageInTicks,  0, 0, 0);
            boolean gamma = itemStackIn.getEnchantmentLevel(ACEnchantmentRegistry.GAMMA_RAY.get())  > 0;
            ResourceLocation texture = gamma ? RAYGUN_BLUE_TEXTURE : RAYGUN_TEXTURE;
            ResourceLocation textureActive = gamma ? RAYGUN_BLUE_ACTIVE_TEXTURE : RAYGUN_ACTIVE_TEXTURE;
            RAYGUN_MODEL.renderToBuffer(poseStack, getVertexConsumerFoil(bufferIn, RenderType.entityCutoutNoCull(texture), texture, itemStackIn.hasFoil()), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            RAYGUN_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, ACRenderTypes.getEyesAlphaEnabled(textureActive), textureActive), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, pulseAlpha);
            poseStack.popPose();
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.SEA_STAFF.get())) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.SEA_STAFF_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -0.5F, 0);
                if (transformType.firstPerson()) {
                    poseStack.scale(0.6F, 0.6F, 0.6F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(SEA_STAFF_TEXTURE), false, itemStackIn.hasFoil());
                SEA_STAFF_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                renderStaticItemSprite(spriteItem, transformType, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, level);
            }
        }

        if (itemStackIn.is(ACItemRegistry.ORTHOLANCE.get())) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.ORTHOLANCE_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -1.1F, 0);
                if (transformType.firstPerson()) {
                    poseStack.scale(0.6F, 1F, 0.6F);
                }
                VertexConsumer vertexconsumer = getVertexConsumerFoil(bufferIn, RenderType.entityCutoutNoCull(ORTHOLANCE_TEXTURE), ORTHOLANCE_TEXTURE, itemStackIn.hasFoil());
                ORTHOLANCE_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                renderStaticItemSprite(spriteItem, transformType, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, level);
            }
        }

        if (itemStackIn.is(ACBlockRegistry.COPPER_VALVE.get().asItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            COPPER_VALVE_MODEL.resetToDefaultPose();
            COPPER_VALVE_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, RenderType.entityCutoutNoCull(COPPER_VALVE_TEXTURE), COPPER_VALVE_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }

        if (itemStackIn.is(ACBlockRegistry.BEHOLDER.get().asItem())) {
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            BEHOLDER_MODEL.setupAnim(null, 0.0F, 45F, ageInTicks, 0, 0);
            BEHOLDER_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, RenderType.entityCutoutNoCull(BEHOLDER_TEXTURE),BEHOLDER_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            BEHOLDER_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, RenderType.eyes(BEHOLDER_TEXTURE_EYE), BEHOLDER_TEXTURE_EYE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.DREADBOW.get())) {
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            float pullAmount = DreadbowItem.getPullingAmount(itemStackIn, partialTick);
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ItemStack spriteItem = new ItemStack(pullAmount >= 0.8F ? ACItemRegistry.DREADBOW_PULLING_2_SPRITE.get() : pullAmount >= 0.5F ? ACItemRegistry.DREADBOW_PULLING_1_SPRITE.get() : pullAmount > 0.0F ? ACItemRegistry.DREADBOW_PULLING_0_SPRITE.get() : ACItemRegistry.DREADBOW_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                if (transformType.firstPerson()) {
                    poseStack.translate(left ? -0.1F : 0.1F, 0.1F, -0.1F);
                    poseStack.scale(0.5F, 0.5F, 0.5F);
                    poseStack.mulPose(Axis.XP.rotationDegrees(15));
                }else{
                    poseStack.translate(left ? 0.1F : -0.1F, -0.45F, 0.35F - pullAmount * 0.3F);
                    poseStack.mulPose(Axis.YP.rotationDegrees(left ? 7 : -7));
                }
                EntityType type = DreadbowItem.getTypeOfArrow(itemStackIn);
                DREADBOW_MODEL.setupAnim(null, pullAmount, ageInTicks,  0, 0, 0);
                if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.isUsingItem() && pullAmount > 0 && type != null && Minecraft.getInstance().level != null){
                    if(renderedDreadbowArrow == null || renderedDreadbowArrow.getType() != type){
                        renderedDreadbowArrow = type.create(Minecraft.getInstance().level);
                    }
                    EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
                    poseStack.pushPose();
                    DREADBOW_MODEL.translateToBowString(poseStack);
                    poseStack.mulPose(Axis.XP.rotationDegrees(180));
                    poseStack.mulPose(Axis.YP.rotationDegrees(left ? 7 : -7));
                    poseStack.translate(0, 0.0F, 0.75F);
                    manager.render(renderedDreadbowArrow, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack, bufferIn, DreadbowItem.isConvertibleArrow (renderedDreadbowArrow) ? (int) (Math.round(240 * (1F - pullAmount))) : combinedLightIn);
                    poseStack.popPose();
                }
                VertexConsumer vertexconsumer = getVertexConsumerFoil(bufferIn, RenderType.entityCutoutNoCull(DREADBOW_TEXTURE), DREADBOW_TEXTURE, itemStackIn.hasFoil());
                DREADBOW_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                ResourceLocation eyeTexture = DreadbowItem.getPerfectShotTicks(itemStackIn) > 0 ? DREADBOW_TEXTURE_EYE_PERFECT : DREADBOW_TEXTURE_EYE;
                DREADBOW_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, RenderType.eyes(eyeTexture), eyeTexture), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                renderStaticItemSprite(spriteItem, transformType, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, level);
            }
        }

        if (itemStackIn.is(ACBlockRegistry.GOBTHUMPER.get().asItem())) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            GOBTHUMPER_MODEL.setupAnim(null, 0.0F, 0.0F, 0.0F, 0, 0);
            GOBTHUMPER_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, RenderType.entityCutoutNoCull(GOBTHUMPER_TEXTURE),GOBTHUMPER_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            GOBTHUMPER_MODEL.renderToBuffer(poseStack, getVertexConsumer(bufferIn, RenderType.entityTranslucent(GOBTHUMPER_JELLY_TEXTURE), GOBTHUMPER_JELLY_TEXTURE), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.SHOT_GUM.get())) {
            float shootProgress = ShotGumItem.getLerpedShootTime(itemStackIn, partialTick) / 5F;
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.pushPose();
            poseStack.scale(0.8F, 0.8F, 0.8F);

            SHOT_GUM_MODEL.setupAnim(null, shootProgress, ShotGumItem.getGumballsLeft(itemStackIn),  ShotGumItem.getLerpedCrankAngle(itemStackIn, partialTick), 0, 0);
            SHOT_GUM_MODEL.renderToBuffer(poseStack, getVertexConsumerFoil(bufferIn, RenderType.entityCutoutNoCull(SHOT_GUM_TEXTURE), SHOT_GUM_TEXTURE, itemStackIn.hasFoil()), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            SHOT_GUM_MODEL.renderToBuffer(poseStack, getVertexConsumerFoil(bufferIn, RenderType.entityTranslucent(SHOT_GUM_GLASS_TEXTURE), SHOT_GUM_GLASS_TEXTURE, itemStackIn.hasFoil()), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
            poseStack.popPose();
        }

        if (itemStackIn.is(ACItemRegistry.SUGAR_STAFF.get())) {
            float ageInTicks = Minecraft.getInstance().player == null ? 0F : Minecraft.getInstance().player.tickCount + partialTick;
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.SUGAR_STAFF_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -1.0F, 0);
                if (transformType.firstPerson()) {
                    poseStack.translate(0, 0.4F, 0);
                    poseStack.scale(0.6F, 0.6F, 0.6F);
                }
                VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(SUGAR_STAFF_TEXTURE), false, itemStackIn.hasFoil());
                SUGAR_STAFF_MODEL.setupAnim(null, 0.0F, 0.0F,  ageInTicks, 0, 0);
                SUGAR_STAFF_MODEL.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                renderStaticItemSprite(spriteItem, transformType, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, level);
            }
        }

        if (itemStackIn.is(ACItemRegistry.FROSTMINT_SPEAR.get())) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ItemStack spriteItem = new ItemStack(ACItemRegistry.FROSTMINT_SPEAR_SPRITE.get());
            spriteItem.setTag(itemStackIn.getTag());
            if (heldIn3d) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-180));
                poseStack.translate(0, -0.85F, -0.1F);
                if (transformType.firstPerson()) {
                    poseStack.translate(0, 0.5F, 0F);
                    poseStack.scale(0.75F, 0.75F, 0.75F);
                }
                FROSTMINT_SPEAR_MODEL.resetToDefaultPose();
                VertexConsumer vertexconsumer1 = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(FROSTMINT_SPEAR_TEXTURE), false, itemStackIn.hasFoil());
                FROSTMINT_SPEAR_MODEL.renderToBuffer(poseStack, vertexconsumer1, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            } else {
                renderStaticItemSprite(spriteItem, transformType, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, level);
            }
        }
    }

    private void renderStaticItemSprite(ItemStack spriteItem, ItemDisplayContext transformType, int combinedLightIn, int combinedOverlayIn, PoseStack poseStack, MultiBufferSource bufferIn, ClientLevel level) {
        if(sepiaFlag){
            BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(spriteItem, Minecraft.getInstance().level, null, 0);
            ItemWidget.renderSepiaItem(poseStack, bakedmodel, spriteItem, Minecraft.getInstance().renderBuffers().bufferSource());
        }else{
            Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, poseStack, bufferIn, level, 0);
        }
    }

    private void renderStaticItemSpriteWithLighting(ItemStack spriteItem, ItemDisplayContext transformType, int combinedLightIn, int combinedOverlayIn, PoseStack poseStack, MultiBufferSource bufferIn, ClientLevel level) {
        if(sepiaFlag){
            BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(spriteItem, Minecraft.getInstance().level, null, 0);
            ItemWidget.renderSepiaItem(poseStack, bakedmodel, spriteItem, Minecraft.getInstance().renderBuffers().bufferSource());
        }else{
            Minecraft.getInstance().getItemRenderer().renderStatic(spriteItem, transformType, transformType != ItemDisplayContext.GUI ? combinedLightIn : 240, combinedOverlayIn, poseStack, bufferIn, level, 0);
        }
    }

    private static VertexConsumer getVertexConsumerFoil(MultiBufferSource bufferIn, RenderType _default, ResourceLocation resourceLocation, boolean foil){
        return sepiaFlag ? bufferIn.getBuffer(ACRenderTypes.getBookWidget(resourceLocation, true)) : ItemRenderer.getFoilBuffer(bufferIn, _default, false, foil);
    }
    private static VertexConsumer getVertexConsumer(MultiBufferSource bufferIn, RenderType _default, ResourceLocation resourceLocation){
        return sepiaFlag ? bufferIn.getBuffer(ACRenderTypes.getBookWidget(resourceLocation, true)) : bufferIn.getBuffer(_default);
    }
}
