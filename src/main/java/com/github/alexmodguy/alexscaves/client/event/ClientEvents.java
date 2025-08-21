package com.github.alexmodguy.alexscaves.client.event;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.gui.ACAdvancementTabs;
import com.github.alexmodguy.alexscaves.client.render.blockentity.AmbersolBlockRenderer;
import com.github.alexmodguy.alexscaves.client.render.blockentity.HologramProjectorBlockRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.CorrodentRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.LicowitchRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.SubmarineRenderer;
import com.github.alexmodguy.alexscaves.client.render.item.RaygunRenderHelper;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexmodguy.alexscaves.server.entity.util.*;
import com.github.alexmodguy.alexscaves.server.item.*;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSampler;
import com.github.alexmodguy.alexscaves.server.misc.ACVanillaMapUtil;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import com.github.alexmodguy.alexscaves.server.potion.DeepsightEffect;
import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import com.github.alexthe666.citadel.client.event.EventLivingRenderer;
import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import com.github.alexthe666.citadel.client.event.EventRenderSplashText;
import com.github.alexthe666.citadel.client.tick.ClientTickRateTracker;
import com.github.alexthe666.citadel.server.tick.TickRateTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.UUID;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class ClientEvents {

    private static final ResourceLocation POTION_EFFECT_HUD_OVERLAYS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/potion_effect_hud_overlays.png");
    private static final ResourceLocation BOSS_BAR_HUD_OVERLAYS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/boss_bar_hud_overlays.png");
    private static final ResourceLocation DINOSAUR_HUD_OVERLAYS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/dinosaur_hud_overlays.png");
    private static final ResourceLocation ARMOR_HUD_OVERLAYS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/armor_hud_overlays.png");
    private static final ResourceLocation SUBMARINE_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "shaders/post/submarine_light.json");
    private static final ResourceLocation WATCHER_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "shaders/post/watcher_perspective.json");
    private static final ResourceLocation SUGAR_RUSH_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "shaders/post/sugar_rush.json");
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/teletor_trail.png");

    private static float lastSampledFogNearness = 0.0F;
    private static float lastSampledWaterFogFarness = 0.0F;
    private static Vec3 lastSampledFogColor = Vec3.ZERO;
    private static Vec3 lastSampledWaterFogColor = Vec3.ZERO;

    public static PoseStack lastVanillaMapPoseStack;
    public static MultiBufferSource lastVanillaMapRenderBuffer;
    public static int lastVanillaMapRenderPackedLight;
    private static final RenderType UNDERGROUND_CABIN_MAP_ICONS = RenderType.text(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/underground_cabin_map_icons.png"));

    @SubscribeEvent
    public void setupEntityRotations(EventLivingRenderer.SetupRotations event) {
        if (event.getEntity() instanceof MagneticEntityAccessor magnetic) {
            float width = event.getEntity().getBbWidth();
            float height = event.getEntity().getBbHeight();
            float progress = magnetic.getAttachmentProgress(event.getPartialTicks());
            float prevProg = 1F - progress;
            float bodyRot = 180.0F - event.getBodyYRot();
            if (magnetic.getMagneticAttachmentFace().getAxis() != Direction.Axis.Y) {
                event.getPoseStack().mulPose(Axis.YN.rotationDegrees(bodyRot));
            }
            rotateForAngle(event.getEntity(), event.getPoseStack(), magnetic.getPrevMagneticAttachmentFace(), prevProg, width, height);
            rotateForAngle(event.getEntity(), event.getPoseStack(), magnetic.getMagneticAttachmentFace(), progress, width, height);
        }
    }

    @SubscribeEvent
    public void preRenderLiving(RenderLivingEvent.Pre event) {
        if (event.getEntity() instanceof HeadRotationEntityAccessor magnetic) {
            magnetic.setMagnetHeadRotation();
        }

        if (ClientProxy.blockedEntityRenders.contains(event.getEntity().getUUID())) {
            if (!AlexsCaves.PROXY.isFirstPersonPlayer(event.getEntity())) {
                MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(event.getEntity(), event.getRenderer(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
                event.setCanceled(true);
            }
            ClientProxy.blockedEntityRenders.remove(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public void postRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = event.getEntity();
        float partialTick = event.getPartialTick();
        if (entity instanceof HeadRotationEntityAccessor magnetic) {
            magnetic.resetMagnetHeadRotation();
        }
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            RaygunRenderHelper.renderRaysFor(entity, entity.getPosition(partialTick), event.getPoseStack(), event.getMultiBufferSource(), partialTick, false, 0);
        }
        if (entity.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get()) && entity.isAlive()) {
            Vec3 trailOffset = new Vec3(0, entity.getBbHeight() * 0.5F, 0);
            double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double y = Mth.lerp(partialTick, entity.yOld, entity.getY());
            double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
            int samples = 0;
            int sampleSize = 60;
            float trailHeight = entity.getBbHeight() * 0.8F;
            Vec3 topAngleVec = new Vec3(0, trailHeight, 0);
            Vec3 bottomAngleVec = new Vec3(0, -trailHeight, 0);
            Vec3 drawFrom = trailOffset;
            VertexConsumer vertexconsumer = event.getMultiBufferSource().getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));
            float trailA = DarknessIncarnateEffect.getIntensity(entity, partialTick, 20F);
            int packedLightIn = event.getPackedLight();
            while (samples < sampleSize) {
                Vec3 sample = AlexsCaves.PROXY.getDarknessTrailPosFor(entity, samples + 5, partialTick).subtract(x, y, z).add(trailOffset);
                float u1 = samples / (float) sampleSize;
                float u2 = u1 + 1 / (float) sampleSize;

                Vec3 draw1 = drawFrom;
                Vec3 draw2 = sample;

                PoseStack.Pose posestack$pose = event.getPoseStack().last();
                Matrix4f matrix4f = posestack$pose.pose();
                Matrix3f matrix3f = posestack$pose.normal();

                vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) bottomAngleVec.x, (float) draw1.y + (float) bottomAngleVec.y, (float) draw1.z + (float) bottomAngleVec.z).color(0, 0, 0, trailA).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) bottomAngleVec.x, (float) draw2.y + (float) bottomAngleVec.y, (float) draw2.z + (float) bottomAngleVec.z).color(0, 0, 0, trailA).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) topAngleVec.x, (float) draw2.y + (float) topAngleVec.y, (float) draw2.z + (float) topAngleVec.z).color(0, 0, 0, trailA).uv(u2, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) topAngleVec.x, (float) draw1.y + (float) topAngleVec.y, (float) draw1.z + (float) topAngleVec.z).color(0, 0, 0, trailA).uv(u1, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                samples++;
                drawFrom = sample;
            }
        }
    }

    private static void attemptLoadShader(ResourceLocation resourceLocation) {
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;
        if (ClientProxy.shaderLoadAttemptCooldown <= 0) {
            renderer.loadEffect(resourceLocation);
            if (!renderer.effectActive) {
                ClientProxy.shaderLoadAttemptCooldown = 12000;
                AlexsCaves.LOGGER.warn("Alex's Caves could not load the shader {}, will attempt to load shader in 30 seconds", resourceLocation);
            }
        }
    }

    @SubscribeEvent
    public void postRenderStage(RenderLevelStageEvent event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        boolean firstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            if (firstPerson && player instanceof LivingEntity living) {
                MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
                Vec3 cameraPos = event.getCamera().getPosition();
                RaygunRenderHelper.renderRaysFor(living, cameraPos, event.getPoseStack(), multibuffersource$buffersource, event.getPartialTick(), true, 2);
            }
            GameRenderer renderer = Minecraft.getInstance().gameRenderer;
            if (firstPerson && player.isPassenger() && player.getVehicle() instanceof SubmarineEntity submarine && SubmarineRenderer.isFirstPersonFloodlightsMode(submarine)) {
                if (renderer.currentEffect() == null || !SUBMARINE_SHADER.toString().equals(renderer.currentEffect().getName())) {
                    attemptLoadShader(SUBMARINE_SHADER);
                }
            } else if (renderer.currentEffect() != null && SUBMARINE_SHADER.toString().equals(renderer.currentEffect().getName())) {
                renderer.checkEntityPostEffect(null);
            }else if (firstPerson && player instanceof PossessesCamera || player instanceof LivingEntity afflicted && afflicted.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get())) {
                if (renderer.currentEffect() == null || !WATCHER_SHADER.toString().equals(renderer.currentEffect().getName())) {
                    attemptLoadShader(WATCHER_SHADER);
                }
            } else if (renderer.currentEffect() != null && WATCHER_SHADER.toString().equals(renderer.currentEffect().getName())) {
                renderer.checkEntityPostEffect(null);
            }else if (player instanceof LivingEntity afflicted && afflicted.hasEffect(ACEffectRegistry.SUGAR_RUSH.get()) && AlexsCaves.CLIENT_CONFIG.sugarRushSaturationEffect.get()) {
                if (renderer.currentEffect() == null || !SUGAR_RUSH_SHADER.toString().equals(renderer.currentEffect().getName())) {
                    attemptLoadShader(SUGAR_RUSH_SHADER);
                }
            } else if (renderer.currentEffect() != null && SUGAR_RUSH_SHADER.toString().equals(renderer.currentEffect().getName())) {
                renderer.checkEntityPostEffect(null);
            }
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            if (firstPerson && player instanceof LivingEntity living) {
                MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
                Vec3 cameraPos = event.getCamera().getPosition();
                RaygunRenderHelper.renderRaysFor(living, cameraPos, event.getPoseStack(), multibuffersource$buffersource, event.getPartialTick(), true, 1);
            }
            RenderSystem.runAsFancy(() -> HologramProjectorBlockRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            RenderSystem.runAsFancy(() -> CorrodentRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
            RenderSystem.runAsFancy(() -> LicowitchRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS && AlexsCaves.CLIENT_CONFIG.ambersolShines.get()) {
            RenderSystem.runAsFancy(() -> AmbersolBlockRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
    }

    @SubscribeEvent
    public void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        float partialTick = Minecraft.getInstance().getPartialTick();
        float tremorAmount = ClientProxy.renderNukeSkyDarkFor > 0 ? 1.5F : 0F;
        if (player instanceof PossessesCamera watcherEntity) {
            Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
            tremorAmount = watcherEntity.isPossessionBreakable() ? AlexsCaves.PROXY.getPossessionStrengthAmount(partialTick) : 0F;
        }
        if (player != null && AlexsCaves.CLIENT_CONFIG.screenShaking.get()) {
            double shakeDistanceScale = 64;
            double distance = Double.MAX_VALUE;
            if (tremorAmount == 0) {
                AABB aabb = player.getBoundingBox().inflate(shakeDistanceScale);
                for (Mob screenShaker : Minecraft.getInstance().level.getEntitiesOfClass(Mob.class, aabb, (mob -> mob instanceof ShakesScreen))) {
                    ShakesScreen shakesScreen = (ShakesScreen) screenShaker;
                    if (shakesScreen.canFeelShake(player) && screenShaker.distanceTo(player) < distance) {
                        distance = screenShaker.distanceTo(player);
                        tremorAmount = Math.min((1F - (float) Math.min(1, distance / shakesScreen.getShakeDistance())) * Math.max(shakesScreen.getScreenShakeAmount(partialTick), 0F), 2.0F);
                    }
                }
            }
            if (tremorAmount > 0) {
                if (ClientProxy.lastTremorTick != player.tickCount) {
                    RandomSource rng = player.level().random;
                    ClientProxy.randomTremorOffsets[0] = rng.nextFloat();
                    ClientProxy.randomTremorOffsets[1] = rng.nextFloat();
                    ClientProxy.randomTremorOffsets[2] = rng.nextFloat();
                    ClientProxy.lastTremorTick = player.tickCount;
                }
                double intensity = tremorAmount * Minecraft.getInstance().options.screenEffectScale().get();
                event.getCamera().move(ClientProxy.randomTremorOffsets[0] * 0.2F * intensity, ClientProxy.randomTremorOffsets[1] * 0.2F * intensity, ClientProxy.randomTremorOffsets[2] * 0.5F * intensity);
            }
        }
        if (player != null && player.isPassenger() && player.getVehicle() instanceof SubmarineEntity && event.getCamera().isDetached()) {
            event.getCamera().move(-event.getCamera().getMaxZoom(4F), 0, 0);
        }
        if (player != null && player.isPassenger() && player.getVehicle() instanceof TremorsaurusEntity && event.getCamera().isDetached()) {
            event.getCamera().move(-event.getCamera().getMaxZoom(2F), 0, 0);
        }
        if (player != null && player.isPassenger() && player.getVehicle() instanceof AtlatitanEntity && event.getCamera().isDetached()) {
            event.getCamera().move(-event.getCamera().getMaxZoom(4F), 0, 0);
        }
        if (player != null && player.isPassenger() && player.getVehicle() instanceof TremorzillaEntity && event.getCamera().isDetached()) {
            event.getCamera().move(-event.getCamera().getMaxZoom(10F), 0, 0);
        }
        if (player != null && player.isPassenger() && player.getVehicle() instanceof GumWormSegmentEntity && event.getCamera().isDetached()) {
            event.getCamera().move(-event.getCamera().getMaxZoom(12F), 0, 0);
        }
        if (player != null && player instanceof LivingEntity livingEntity && livingEntity.hasEffect(ACEffectRegistry.STUNNED.get())) {
            event.setRoll((float) (Math.sin((player.tickCount + partialTick) * 0.2F) * 10F));
        }
        Direction dir = MagnetUtil.getEntityMagneticDirection(player);

    }

    @SubscribeEvent
    public void clientLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide) {
            return;
        }
        if (entity.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get()) && entity.isAlive()) {
            int trailPointer = ClientProxy.darknessTrailPointerMap.getOrDefault(entity, -1);
            Vec3 latest = entity.position();
            if (ClientProxy.darknessTrailPosMap.get(entity) == null) {
                Vec3[] trailPositions = new Vec3[64];
                if (trailPointer == -1) {
                    Arrays.fill(trailPositions, latest);
                }
                ClientProxy.darknessTrailPosMap.put(entity, trailPositions);
            }
            if (++trailPointer == ClientProxy.darknessTrailPosMap.get(entity).length) {
                trailPointer = 0;
            }
            ClientProxy.darknessTrailPointerMap.put(entity, trailPointer);
            Vec3[] vector3ds = ClientProxy.darknessTrailPosMap.get(entity);
            vector3ds[trailPointer] = latest;
            ClientProxy.darknessTrailPosMap.put(entity, vector3ds);
        } else if (ClientProxy.darknessTrailPosMap.containsKey(entity)) {
            ClientProxy.darknessTrailPosMap.remove(entity);
            ClientProxy.darknessTrailPointerMap.remove(entity);
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (Minecraft.getInstance().getCameraEntity() instanceof PossessesCamera) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPreRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player instanceof PossessesCamera && (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id()) || event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id()) || event.getOverlay().id().equals(VanillaGuiOverlay.JUMP_BAR.id()) || event.getOverlay().id().equals(VanillaGuiOverlay.ITEM_NAME.id()))) {
            event.setCanceled(true);
        }
        if (player != null && player.getVehicle() instanceof RidingMeterMount dinosaur && dinosaur.hasRidingMeter() && event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPoseHand(EventPosePlayerHand event) {
        LivingEntity player = (LivingEntity) event.getEntityIn();
        float f = Minecraft.getInstance().getFrameTime();
        float rightHandResistorShieldUseProgress = 0.0F;
        float leftHandResistorShieldUseProgress = 0.0F;
        float rightHandGalenaGauntletUseProgress = 0.0F;
        float leftHandGalenaGauntletUseProgress = 0.0F;
        float rightHandSpearUseProgress = 0.0F;
        float leftHandSpearUseProgress = 0.0F;
        float rightHandRaygunUseProgress = 0.0F;
        float leftHandRaygunUseProgress = 0.0F;
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ResistorShieldItem) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                rightHandResistorShieldUseProgress = Math.max(rightHandResistorShieldUseProgress, ResistorShieldItem.getLerpedUseTime(player.getItemInHand(InteractionHand.MAIN_HAND), f));
            } else {
                leftHandResistorShieldUseProgress = Math.max(leftHandResistorShieldUseProgress, ResistorShieldItem.getLerpedUseTime(player.getItemInHand(InteractionHand.MAIN_HAND), f));
            }
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ResistorShieldItem) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                leftHandResistorShieldUseProgress = Math.max(leftHandResistorShieldUseProgress, ResistorShieldItem.getLerpedUseTime(player.getItemInHand(InteractionHand.OFF_HAND), f));
            } else {
                rightHandResistorShieldUseProgress = Math.max(rightHandResistorShieldUseProgress, ResistorShieldItem.getLerpedUseTime(player.getItemInHand(InteractionHand.OFF_HAND), f));
            }
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GalenaGauntletItem) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                rightHandGalenaGauntletUseProgress = Math.max(rightHandGalenaGauntletUseProgress, GalenaGauntletItem.getLerpedUseTime(player.getItemInHand(InteractionHand.MAIN_HAND), f));
            } else {
                leftHandGalenaGauntletUseProgress = Math.max(leftHandGalenaGauntletUseProgress, GalenaGauntletItem.getLerpedUseTime(player.getItemInHand(InteractionHand.MAIN_HAND), f));
            }
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GalenaGauntletItem) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                leftHandGalenaGauntletUseProgress = Math.max(leftHandGalenaGauntletUseProgress, GalenaGauntletItem.getLerpedUseTime(player.getItemInHand(InteractionHand.OFF_HAND), f));
            } else {
                rightHandGalenaGauntletUseProgress = Math.max(rightHandGalenaGauntletUseProgress, GalenaGauntletItem.getLerpedUseTime(player.getItemInHand(InteractionHand.OFF_HAND), f));
            }
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpearItem && player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
            float f7 = (player.getItemInHand(InteractionHand.MAIN_HAND).getUseDuration() - ((float) player.getUseItemRemainingTicks() - f + 1.0F)) / 10.0F;
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                rightHandSpearUseProgress = Math.max(rightHandSpearUseProgress, f7);
            } else {
                leftHandSpearUseProgress = Math.max(leftHandSpearUseProgress, f7);
            }
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof SpearItem && player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
            float f7 = (player.getItemInHand(InteractionHand.OFF_HAND).getUseDuration() - ((float) player.getUseItemRemainingTicks() - f + 1.0F)) / 10.0F;
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                leftHandSpearUseProgress = Math.max(leftHandSpearUseProgress, f7);
            } else {
                rightHandSpearUseProgress = Math.max(rightHandSpearUseProgress, f7);
            }
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RaygunItem) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                rightHandRaygunUseProgress = Math.max(rightHandRaygunUseProgress, RaygunItem.getLerpedUseTime(player.getItemInHand(InteractionHand.MAIN_HAND), f));
            } else {
                leftHandRaygunUseProgress = Math.max(leftHandRaygunUseProgress, RaygunItem.getLerpedUseTime(player.getItemInHand(InteractionHand.MAIN_HAND), f));
            }
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RaygunItem) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                leftHandRaygunUseProgress = Math.max(leftHandRaygunUseProgress, RaygunItem.getLerpedUseTime(player.getItemInHand(InteractionHand.OFF_HAND), f));
            } else {
                rightHandRaygunUseProgress = Math.max(rightHandRaygunUseProgress, RaygunItem.getLerpedUseTime(player.getItemInHand(InteractionHand.OFF_HAND), f));
            }
        }
        if (player.isPassenger() && player.getVehicle() instanceof SubterranodonEntity subterranodon) {
            float flight = subterranodon.getFlyProgress(f) - subterranodon.getHoverProgress(f);
            if (flight > 0.0F) {
                event.getModel().leftArm.xRot = -(float) Math.toRadians(180F) * flight;
                event.getModel().leftArm.zRot = (float) Math.toRadians(-10F) * flight;
                event.getModel().rightArm.xRot = -(float) Math.toRadians(180F) * flight;
                event.getModel().rightArm.zRot = (float) Math.toRadians(10F) * flight;
            }
            event.setResult(Event.Result.ALLOW);
        }
        if (leftHandResistorShieldUseProgress > 0.0F) {
            float useProgress = Math.min(10F, leftHandResistorShieldUseProgress) / 10F;
            float useProgressTurn = Math.min(useProgress * 4F, 1F);
            float useProgressUp = (float) Math.sin(useProgress * Math.PI);
            float armTilt = event.getModel().crouching ? 120F : 80F;
            event.getModel().leftArm.xRot = -(float) Math.toRadians(armTilt) - (float) Math.toRadians(80F) * useProgressUp;
            event.getModel().leftArm.yRot = (float) Math.toRadians(20F) * useProgressTurn;
            event.setResult(Event.Result.ALLOW);
        }
        if (rightHandResistorShieldUseProgress > 0.0F) {
            float useProgress = Math.min(10F, rightHandResistorShieldUseProgress) / 10F;
            float useProgressTurn = Math.min(useProgress * 4F, 1F);
            float useProgressUp = (float) Math.sin(useProgress * Math.PI);
            float armTilt = event.getModel().crouching ? 120F : 80F;
            event.getModel().rightArm.xRot = -(float) Math.toRadians(armTilt) - (float) Math.toRadians(80F) * useProgressUp;
            event.getModel().rightArm.yRot = -(float) Math.toRadians(20F) * useProgressTurn;
            event.setResult(Event.Result.ALLOW);
        }
        if (leftHandGalenaGauntletUseProgress > 0.0F) {
            float useProgress = Math.min(5F, leftHandGalenaGauntletUseProgress) / 5F;
            event.getModel().leftArm.xRot = (event.getModel().head.xRot - (float) Math.toRadians(80F)) * useProgress;
            event.getModel().leftArm.yRot = event.getModel().head.yRot * useProgress;
            event.setResult(Event.Result.ALLOW);
        }
        if (rightHandGalenaGauntletUseProgress > 0.0F) {
            float useProgress = Math.min(5F, rightHandGalenaGauntletUseProgress) / 5F;
            event.getModel().rightArm.xRot = (event.getModel().head.xRot - (float) Math.toRadians(80F)) * useProgress;
            event.getModel().rightArm.yRot = event.getModel().head.yRot * useProgress;
            event.setResult(Event.Result.ALLOW);
        }
        if (leftHandSpearUseProgress > 0.0F) {
            float useProgress = Math.min(1F, leftHandSpearUseProgress);
            float useProgressMiddle = (float) Math.sin(useProgress * Math.PI);
            event.getModel().leftArm.xRot = useProgress * ((float) Math.toRadians(-180F) + event.getModel().head.xRot);
            event.getModel().leftArm.yRot = useProgressMiddle * ((float) Math.toRadians(-25F) - event.getModel().head.yRot);
            event.getModel().leftArm.zRot = useProgress * (float) Math.toRadians(50F) - (float) Math.toRadians(25F);
            event.setResult(Event.Result.ALLOW);
        }
        if (rightHandSpearUseProgress > 0.0F) {
            float useProgress = Math.min(1F, rightHandSpearUseProgress);
            float useProgressMiddle = (float) Math.sin(useProgress * Math.PI);
            event.getModel().rightArm.xRot = useProgress * ((float) Math.toRadians(-180F) + event.getModel().head.xRot);
            event.getModel().rightArm.yRot = useProgressMiddle * ((float) Math.toRadians(25F) - event.getModel().head.yRot);
            event.getModel().rightArm.zRot = useProgress * -(float) Math.toRadians(50F) + (float) Math.toRadians(25F);
            event.setResult(Event.Result.ALLOW);
        }
        if (event.getEntityIn().getVehicle() instanceof NuclearBombEntity) {
            float ageInTicks = event.getEntityIn().tickCount + f;
            event.getModel().rightArm.xRot = (float) Math.toRadians(-170F);
            event.getModel().rightArm.yRot = (float) Math.toRadians(100F) + (float) Math.cos(ageInTicks * 0.35F) * (float) Math.toRadians(20F);
            event.getModel().rightArm.zRot = (float) Math.sin(ageInTicks * 0.35F) * (float) Math.toRadians(50F) - (float) Math.toRadians(70F);
            event.getModel().leftArm.yRot = (float) Math.toRadians(30F);
            event.setResult(Event.Result.ALLOW);
        }
        if (leftHandRaygunUseProgress > 0.0F) {
            float useProgress = Math.min(5F, leftHandRaygunUseProgress) / 5F;
            event.getModel().leftArm.xRot = (event.getModel().head.xRot - (float) Math.toRadians(80F)) * useProgress;
            event.getModel().leftArm.yRot = event.getModel().head.yRot * useProgress;
            event.getModel().leftArm.zRot = 0;
            event.setResult(Event.Result.ALLOW);
        }
        if (rightHandRaygunUseProgress > 0.0F) {
            float useProgress = Math.min(5F, rightHandRaygunUseProgress) / 5F;
            event.getModel().rightArm.xRot = (event.getModel().head.xRot - (float) Math.toRadians(80F)) * useProgress;
            event.getModel().rightArm.yRot = event.getModel().head.yRot * useProgress;
            event.getModel().rightArm.zRot = 0;
            event.setResult(Event.Result.ALLOW);
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShotGumItem && ShotGumItem.shouldBeHeldUpright(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                event.getModel().rightArm.xRot = (event.getModel().head.xRot - (float) Math.toRadians(70F));
                event.getModel().rightArm.yRot = event.getModel().head.yRot;
                event.getModel().rightArm.zRot = 0;
                event.getModel().leftArm.xRot = event.getModel().head.xRot - (float) Math.toRadians(70F);
                event.getModel().leftArm.yRot = event.getModel().head.yRot + (float) Math.toRadians(40F);
                event.getModel().leftArm.zRot = (float) Math.toRadians(20F);
            } else {
                event.getModel().leftArm.xRot = (event.getModel().head.xRot - (float) Math.toRadians(70F));
                event.getModel().leftArm.yRot = event.getModel().head.yRot;
                event.getModel().leftArm.zRot = 0;
                event.getModel().rightArm.xRot = event.getModel().head.xRot - (float) Math.toRadians(70F);
                event.getModel().rightArm.yRot = event.getModel().head.yRot + (float) Math.toRadians(-40F);
                event.getModel().rightArm.zRot = (float) Math.toRadians(-20F);
            }
            event.setResult(Event.Result.ALLOW);
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShotGumItem && ShotGumItem.shouldBeHeldUpright(player.getItemInHand(InteractionHand.OFF_HAND))) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                event.getModel().leftArm.xRot = (event.getModel().head.xRot - (float) Math.toRadians(70F));
                event.getModel().leftArm.yRot = event.getModel().head.yRot;
                event.getModel().leftArm.zRot = 0;
                event.getModel().rightArm.xRot = event.getModel().head.xRot - (float) Math.toRadians(70F);
                event.getModel().rightArm.yRot = event.getModel().head.yRot + (float) Math.toRadians(-40F);
                event.getModel().rightArm.zRot = (float) Math.toRadians(-20F);

            } else {
                event.getModel().rightArm.xRot = (event.getModel().head.xRot - (float) Math.toRadians(70F));
                event.getModel().rightArm.yRot = event.getModel().head.yRot;
                event.getModel().rightArm.zRot = 0;
                event.getModel().leftArm.xRot = event.getModel().head.xRot - (float) Math.toRadians(70F);
                event.getModel().leftArm.yRot = event.getModel().head.yRot + (float) Math.toRadians(40F);
                event.getModel().leftArm.zRot = (float) Math.toRadians(20F);
            }
            event.setResult(Event.Result.ALLOW);
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof CandyCaneHookItem && CandyCaneHookItem.isActive(player.getItemInHand(InteractionHand.MAIN_HAND)) && player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof CandyCaneHookItem && CandyCaneHookItem.isActive(player.getItemInHand(InteractionHand.OFF_HAND)) && player.getVehicle() instanceof GumWormSegmentEntity) {
            float rightWiggle = -Math.min(player.xxa, 0F) * (float) Math.sin(player.tickCount + AlexsCaves.PROXY.getPartialTicks()) * 25;
            float leftWiggle = Math.max(player.xxa, 0F) * (float) Math.sin(player.tickCount + AlexsCaves.PROXY.getPartialTicks()) * 25;
            event.getModel().rightArm.xRot = (float) Math.toRadians(-100F + rightWiggle);
            event.getModel().leftArm.xRot = (float) Math.toRadians(-100F + leftWiggle);
            event.getModel().rightArm.yRot = (float) Math.toRadians(20F);
            event.getModel().leftArm.yRot = (float) Math.toRadians(-20F);
            event.getModel().rightLeg.xRot = (float) Math.toRadians(-20F);
            event.getModel().leftLeg.xRot = (float) Math.toRadians(20F);
            event.setResult(Event.Result.ALLOW);
        }
        if (event.getResult() != Event.Result.ALLOW && player.hasEffect(ACEffectRegistry.SUGAR_RUSH.get()) && !AlexsCaves.PROXY.isFirstPersonPlayer(player)) {
            float speedModifier = 0.35F;
            if(AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get() && AlexsCaves.PROXY.isTickRateModificationActive(Minecraft.getInstance().level)){
                float tickRate = ClientTickRateTracker.getForClient(Minecraft.getInstance()).getClientTickRate() / 50.0F;
                speedModifier *= tickRate;
            }
            float deltaSpeed = 1.0F;
            float partialTicks = AlexsCaves.PROXY.getPartialTicks();
            float walkPos = player.walkAnimation.position(partialTicks);
            float walkSpeed = player.walkAnimation.speed(partialTicks);
            float headXRot = player.getViewXRot(partialTicks);
            float headYRot = Mth.lerp(partialTicks, player.yHeadRotO, player.yHeadRot) - Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot);
            event.getModel().rightArm.xRot = Mth.cos(walkPos * speedModifier + (float) Math.PI * 0.5F) * 2.0F * walkSpeed * 0.5F / deltaSpeed;
            event.getModel().leftArm.xRot = Mth.cos(walkPos * speedModifier) * 2.0F * walkSpeed * 0.5F / deltaSpeed;
            event.getModel().rightArm.zRot = (Mth.sin(walkPos * -speedModifier + (float) Math.PI * 0.5F) + 2.5F) * 1.5F * walkSpeed * 0.5F / deltaSpeed;
            event.getModel().leftArm.zRot = (Mth.sin(walkPos * -speedModifier) - 2.5F) * 1.5F * walkSpeed * 0.5F / deltaSpeed;
            event.getModel().head.xRot = headXRot * ((float) Math.PI / 180F) + Mth.cos(walkPos * speedModifier + (float) Math.PI) * 1.0F * walkSpeed * 0.5F / deltaSpeed;
            event.getModel().head.yRot = headYRot * ((float) Math.PI / 180F) + Mth.sin(walkPos * speedModifier + (float) Math.PI) * 1.0F * walkSpeed * 0.5F / deltaSpeed;
            event.getModel().leftLeg.xRot = Mth.cos(walkPos * speedModifier + (float) Math.PI) * 4.0F * walkSpeed * 0.5F / deltaSpeed;
            event.getModel().rightLeg.xRot = Mth.cos(walkPos * speedModifier) * 4.0F * walkSpeed * 0.5F / deltaSpeed;
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Player player = AlexsCaves.PROXY.getClientSidePlayer();
        int hudY = 0;
        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id()) && player.getVehicle() instanceof RidingMeterMount mount && mount.hasRidingMeter()) {
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            int forgeGuiY = Minecraft.getInstance().gui instanceof ForgeGui forgeGui ? Math.max(forgeGui.leftHeight, forgeGui.rightHeight) : 0;
            if (player.getArmorValue() > 0 && mount instanceof SubterranodonEntity) {
                forgeGuiY += 25;
            }
            if (forgeGuiY < 53) {
                forgeGuiY = 53;
            }
            int j = screenWidth / 2 - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorX.get();
            int k = screenHeight - forgeGuiY - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorY.get();
            float f = mount.getMeterAmount();
            float invProgress = 1 - f;
            int uOffset = 0;
            int vOffset = 0;
            int dinoHeight = 31;
            if (mount instanceof TremorsaurusEntity) {
                vOffset = 63;
                k += 5;
                hudY = 20;
            } else if (mount instanceof AtlatitanEntity) {
                vOffset = 126;
                dinoHeight = 32;
                k += 3;
                hudY = 40;
            } else if (mount instanceof TremorzillaEntity tremorzilla) {
                vOffset = 193;
                if (tremorzilla.isPowered() && !tremorzilla.isFiring() && tremorzilla.getSpikesDownAmount() > 0) {
                    if (tremorzilla.tickCount / 2 % 2 == 1) {
                        vOffset = 251;
                    }
                    invProgress = 1F;
                }
                dinoHeight = 29;
                k += 5;
                hudY = 20;
            } else if (mount instanceof CandicornEntity) {
                vOffset = 280;
                dinoHeight = 25;
                hudY = 40;
                k += 4;
            } else {
                hudY = 40;
            }
            event.getGuiGraphics().pose().pushPose();
            event.getGuiGraphics().blit(DINOSAUR_HUD_OVERLAYS, j, k, 50, uOffset, vOffset + dinoHeight, 43, dinoHeight, 128, 512);
            event.getGuiGraphics().blit(DINOSAUR_HUD_OVERLAYS, j, k, 50, uOffset, vOffset, 43, (int) Math.floor(dinoHeight * invProgress), 128, 512);
            event.getGuiGraphics().pose().popPose();
        }
        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id()) && DarknessArmorItem.hasMeter(player)) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            int forgeGuiY = Minecraft.getInstance().gui instanceof ForgeGui forgeGui ? Math.max(forgeGui.leftHeight, forgeGui.rightHeight) : 0;
            if (forgeGuiY < 53) {
                forgeGuiY = 53;
            }
            int j = screenWidth / 2 - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorX.get() + 13;
            int k = screenHeight - forgeGuiY - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorY.get() + 9 - hudY;
            float f = DarknessArmorItem.getMeterProgress(stack);
            float invProgress = 1 - f;
            int uvOffset = DarknessArmorItem.canChargeUp(stack) && f >= 1.0F ? 0 : 18;
            event.getGuiGraphics().pose().pushPose();
            event.getGuiGraphics().blit(ARMOR_HUD_OVERLAYS, j, k, 50, uvOffset, 19, 18, 19, 128, 128);
            event.getGuiGraphics().blit(ARMOR_HUD_OVERLAYS, j, k, 50, 0, 0, 18, (int) Math.floor(19 * invProgress), 128, 128);
            event.getGuiGraphics().pose().popPose();
        }
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id()) && Minecraft.getInstance().gameMode.canHurtPlayer() && Minecraft.getInstance().getCameraEntity() instanceof Player && player.hasEffect(ACEffectRegistry.IRRADIATED.get())) {
            int leftHeight = 39;
            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();
            int health = Mth.ceil(player.getHealth());
            int forgeGuiTick = Minecraft.getInstance().gui instanceof ForgeGui forgeGui ? forgeGui.getGuiTicks() : 0;
            AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            float healthMax = (float) attrMaxHealth.getValue();
            float absorb = Mth.ceil(player.getAbsorptionAmount());

            int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);

            ClientProxy.random.setSeed(forgeGuiTick * 312871L);
            int left = width / 2 - 91;
            int top = height - leftHeight;
            int regen = -1;
            if (player.hasEffect(MobEffects.REGENERATION)) {
                regen = forgeGuiTick % Mth.ceil(healthMax + 5.0F);
            }
            final int heartV = player.level().getLevelData().isHardcore() ? 9 : 0;
            int heartU = 0;
            float absorbRemaining = absorb;
            event.getGuiGraphics().pose().pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, POTION_EFFECT_HUD_OVERLAYS);
            for (int i = Mth.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                int row = Mth.ceil((float) (i + 1) / 10.0F) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;
                if (health <= 4) {
                    y += ClientProxy.random.nextInt(2);
                }
                if (i == regen) {
                    y -= 2;
                }
                event.getGuiGraphics().blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU, heartV + 18, 9, 9, 32, 32);
                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                        event.getGuiGraphics().blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                        absorbRemaining -= 1.0F;
                    } else {
                        event.getGuiGraphics().blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU, heartV, 9, 9, 32, 32);
                        absorbRemaining -= 2.0F;
                    }
                } else {
                    if (i * 2 + 1 < health) {
                        event.getGuiGraphics().blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU, heartV, 9, 9, 32, 32);
                    } else if (i * 2 + 1 == health) {
                        event.getGuiGraphics().blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                    }
                }
            }
            event.getGuiGraphics().pose().popPose();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderBossOverlay(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if (ClientProxy.bossBarRenderTypes.containsKey(event.getBossEvent().getId())) {
            int renderTypeFor = ClientProxy.bossBarRenderTypes.get(event.getBossEvent().getId());
            int i = event.getGuiGraphics().guiWidth();
            int j = event.getY();
            Component component = event.getBossEvent().getName();
            if (renderTypeFor == 0) {
                event.setCanceled(true);
                event.getGuiGraphics().blit(BOSS_BAR_HUD_OVERLAYS, event.getX(), event.getY(), 0, 0, 182, 15);
                int progressScaled = (int) (event.getBossEvent().getProgress() * 183.0F);
                event.getGuiGraphics().blit(BOSS_BAR_HUD_OVERLAYS, event.getX(), event.getY(), 0, 15, progressScaled, 15);
                int l = Minecraft.getInstance().font.width(component);
                int i1 = i / 2 - l / 2;
                int j1 = j - 9;
                PoseStack poseStack = event.getGuiGraphics().pose();
                poseStack.pushPose();
                poseStack.translate(i1, j1, 0);
                Minecraft.getInstance().font.drawInBatch8xOutline(component.getVisualOrderText(), 0.0F, 0.0F, 0XFF5100, 0X361515, poseStack.last().pose(), event.getGuiGraphics().bufferSource(), 240);
                poseStack.popPose();
                event.setIncrement(event.getIncrement() + 7);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void fogRender(ViewportEvent.RenderFog event) {
        if (event.isCanceled()) {
            //another mod has cancelled fog rendering.
            return;
        }
        //some mods incorrectly set the RenderSystem fog start and end directly, so this will have to do as a band-aid...
        float defaultFarPlaneDistance = RenderSystem.getShaderFogEnd();
        float defaultNearPlaneDistance = RenderSystem.getShaderFogStart();

        Entity player = Minecraft.getInstance().getCameraEntity();
        FluidState fluidstate = player.level().getFluidState(event.getCamera().getBlockPosition());
        BlockState blockState = player.level().getBlockState(event.getCamera().getBlockPosition());
        if (!fluidstate.isEmpty() && fluidstate.getType().getFluidType().equals(ACFluidRegistry.ACID_FLUID_TYPE.get())) {
            event.setCanceled(true);
            float farness = 10.0F;
            if (Minecraft.getInstance().player.hasEffect(ACEffectRegistry.DEEPSIGHT.get())) {
                farness *= 1.0F + 1.5F * DeepsightEffect.getIntensity(Minecraft.getInstance().player, (float) event.getPartialTick());
            }
            event.setFarPlaneDistance(farness);
            event.setNearPlaneDistance(0.0F);
            return;
        }
        if (!fluidstate.isEmpty() && fluidstate.getType().getFluidType().equals(ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get())) {
            event.setCanceled(true);
            float farness = 20.0F;
            float nearness = -8.0F;
            if (Minecraft.getInstance().player.hasEffect(ACEffectRegistry.DEEPSIGHT.get())) {
                float f = DeepsightEffect.getIntensity(Minecraft.getInstance().player, (float) event.getPartialTick());
                farness *= 1.0F + 1.5F * f;
                nearness *= 1.0F - f;
            }
            event.setFarPlaneDistance(farness);
            event.setNearPlaneDistance(nearness);
            return;
        }
        if (blockState.is(ACBlockRegistry.PRIMAL_MAGMA.get()) || blockState.is(ACBlockRegistry.FISSURE_PRIMAL_MAGMA.get())) {
            event.setCanceled(true);
            float farness = 2.0F;
            if (Minecraft.getInstance().player.hasEffect(ACEffectRegistry.DEEPSIGHT.get())) {
                farness *= 1.0F + 1.5F * DeepsightEffect.getIntensity(Minecraft.getInstance().player, (float) event.getPartialTick());
            }
            event.setFarPlaneDistance(farness);
            event.setNearPlaneDistance(0.0F);
            return;
        }
        if (event.getCamera().getFluidInCamera() == FogType.WATER && AlexsCaves.CLIENT_CONFIG.biomeWaterFogOverrides.get()) {
            float farness = lastSampledWaterFogFarness;
            if (Minecraft.getInstance().player.hasEffect(ACEffectRegistry.DEEPSIGHT.get())) {
                farness *= 1.0F + 1.5F * DeepsightEffect.getIntensity(Minecraft.getInstance().player, (float) event.getPartialTick());
            }
            if (farness != 1.0F) {
                event.setCanceled(true);
                event.setFarPlaneDistance(defaultFarPlaneDistance * farness);
            }
        } else if (event.getMode() == FogRenderer.FogMode.FOG_TERRAIN && AlexsCaves.CLIENT_CONFIG.biomeSkyFogOverrides.get()) {
            float nearness = lastSampledFogNearness;
            float primordialBossAmount = AlexsCaves.PROXY.getPrimordialBossActiveAmount((float) event.getPartialTick());
            boolean flag = Math.abs(nearness) - 1.0F < 0.01F;
            if (primordialBossAmount > 0.0F) {
                flag = true;
                nearness *= (1.0F - primordialBossAmount * 0.75F);
            }
            if (flag) {
                event.setCanceled(true);
                event.setNearPlaneDistance(defaultNearPlaneDistance * nearness);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void fogColor(ViewportEvent.ComputeFogColor event) {
        Entity player = Minecraft.getInstance().player;
        BlockState blockState = player.level().getBlockState(event.getCamera().getBlockPosition());
        if (blockState.is(ACBlockRegistry.PRIMAL_MAGMA.get()) || blockState.is(ACBlockRegistry.FISSURE_PRIMAL_MAGMA.get())) {
            event.setRed(1F);
            event.setGreen(0.4F);
            event.setBlue((float) (0));
        } else if (player.getEyeInFluidType() != null && player.getEyeInFluidType().equals(ACFluidRegistry.ACID_FLUID_TYPE.get())) {
            event.setRed((float) (0));
            event.setGreen((float) (1));
            event.setBlue((float) (0));
        } else if (player.getEyeInFluidType() != null && player.getEyeInFluidType().equals(ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get())) {
            event.setRed(0.6F);
            event.setGreen(0.1F);
            event.setBlue(0.85F);
        } else if (event.getCamera().getFluidInCamera() == FogType.NONE && AlexsCaves.CLIENT_CONFIG.biomeSkyFogOverrides.get()) {
            float override = ClientProxy.acSkyOverrideAmount;
            float setR = event.getRed();
            float setG = event.getGreen();
            float setB = event.getBlue();

            boolean flag = false;
            if (override != 0.0F) {
                flag = true;
                Vec3 vec3 = lastSampledFogColor;
                setR = (float) (vec3.x - setR) * override + setR;
                setG = (float) (vec3.y - setG) * override + setG;
                setB = (float) (vec3.z - setB) * override + setB;
            }
            float primordialBossAmount = AlexsCaves.PROXY.getPrimordialBossActiveAmount((float) event.getPartialTick());
            if (primordialBossAmount > 0.0F) {
                flag = true;
                setR = (0.8F - setR) * primordialBossAmount + setR;
                setG = (0.2F - setG) * primordialBossAmount + setG;
                setB = (0.15F - setB) * primordialBossAmount + setB;
            }
            if (flag) {
                event.setRed(setR);
                event.setGreen(setG);
                event.setBlue(setB);
            }
        } else if (event.getCamera().getFluidInCamera() == FogType.WATER && AlexsCaves.CLIENT_CONFIG.biomeWaterFogOverrides.get()) {
            int i = Minecraft.getInstance().options.biomeBlendRadius().get();
            float override = ClientProxy.acSkyOverrideAmount;
            if (override != 0) {
                Vec3 vec3 = lastSampledWaterFogColor;
                event.setRed((float) (event.getRed() + (vec3.x - event.getRed()) * override));
                event.setGreen((float) (event.getGreen() + (vec3.y - event.getGreen()) * override));
                event.setBlue((float) (event.getBlue() + (vec3.z - event.getBlue()) * override));
            }
        }
    }

    private void rotateForAngle(LivingEntity entity, PoseStack matrixStackIn, Direction rotate, float f, float width, float height) {
        boolean down = entity.zza < 0.0F;
        switch (rotate) {
            case DOWN:
                break;
            case UP:
                matrixStackIn.translate(0.0D, height * f, 0.0D);
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180.0F * f));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180.0F * f));
                break;
            case NORTH:
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -0.25f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
            case SOUTH:
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180 * f));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -0.25f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
            case WEST:
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90 * f));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -0.25f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
            case EAST:
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-90 * f));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -0.25f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
        }
    }

    private static float calculateBiomeAmbientLight(Entity player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return ACBiomeRegistry.getBiomeAmbientLight(player.level().getBiome(player.blockPosition()));
        } else {
            return BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeAmbientLight);
        }
    }

    private static Vec3 calculateBiomeLightColor(Entity player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return ACBiomeRegistry.getBiomeLightColorOverride(player.level().getBiome(player.blockPosition()));
        } else {
            return BiomeSampler.sampleBiomesVec3(player.level(), player.position(), ACBiomeRegistry::getBiomeLightColorOverride);
        }
    }

    private static float calculateBiomeFogNearness(Entity player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        float nearness;
        if (i == 0) {
            nearness = ACBiomeRegistry.getBiomeFogNearness(player.level().getBiome(player.blockPosition()));
        } else {
            nearness = BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeFogNearness);
        }
        return nearness;
    }

    private static float calculateBiomeWaterFogFarness(Entity player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        float farness;
        if (i == 0) {
            farness = ACBiomeRegistry.getBiomeWaterFogFarness(player.level().getBiome(player.blockPosition()));
        } else {
            farness = BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeWaterFogFarness);
        }
        return farness;
    }

    private static Vec3 calculateBiomeFogColor(Entity player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        Vec3 vec3;
        if (i == 0) {
            vec3 = ((ClientLevel) player.level()).effects().getBrightnessDependentFogColor(Vec3.fromRGB24(player.level().getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getFogColor()), 1.0F);
        } else {
            vec3 = ((ClientLevel) player.level()).effects().getBrightnessDependentFogColor(BiomeSampler.sampleBiomesVec3(player.level(), player.position(), biomeHolder -> Vec3.fromRGB24(biomeHolder.value().getFogColor())), 1.0F);
        }
        return vec3;
    }

    private Vec3 calculateBiomeWaterFogColor(Entity player) {
        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        Vec3 vec3;
        if (i == 0) {
            vec3 = ((ClientLevel) player.level()).effects().getBrightnessDependentFogColor(Vec3.fromRGB24(player.level().getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getWaterFogColor()), 1.0F);
        } else {
            vec3 = ((ClientLevel) player.level()).effects().getBrightnessDependentFogColor(BiomeSampler.sampleBiomesVec3(player.level(), player.position(), biomeHolder -> Vec3.fromRGB24(biomeHolder.value().getWaterFogColor())), 1.0F);
        }
        return vec3;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Entity cameraEntity = Minecraft.getInstance().cameraEntity;
            float partialTicks = AlexsCaves.PROXY.getPartialTicks();
            if (ClientProxy.shaderLoadAttemptCooldown > 0) {
                ClientProxy.shaderLoadAttemptCooldown--;
            }
            ClientProxy.prevPrimordialBossActiveAmount = ClientProxy.primordialBossActiveAmount;
            ClientProxy.prevNukeFlashAmount = ClientProxy.nukeFlashAmount;
            if (cameraEntity != null) {
                ClientProxy.acSkyOverrideAmount = ACBiomeRegistry.calculateBiomeSkyOverride(cameraEntity);
                if (ClientProxy.acSkyOverrideAmount > 0) {
                    ClientProxy.acSkyOverrideColor = BiomeSampler.sampleBiomesVec3(Minecraft.getInstance().level, Minecraft.getInstance().cameraEntity.position(), biomeHolder -> Vec3.fromRGB24(biomeHolder.value().getSkyColor()));
                }
                ClientProxy.lastBiomeLightColorPrev = ClientProxy.lastBiomeLightColor;
                ClientProxy.lastBiomeLightColor = calculateBiomeLightColor(cameraEntity);
                ClientProxy.lastBiomeAmbientLightAmountPrev = ClientProxy.lastBiomeAmbientLightAmount;
                ClientProxy.lastBiomeAmbientLightAmount = calculateBiomeAmbientLight(cameraEntity);
                lastSampledFogNearness = calculateBiomeFogNearness(cameraEntity);
                lastSampledWaterFogFarness = calculateBiomeWaterFogFarness(cameraEntity);
                if (cameraEntity.level() instanceof ClientLevel) { //fixes crash with beholder
                    lastSampledFogColor = calculateBiomeFogColor(cameraEntity);
                    lastSampledWaterFogColor = calculateBiomeWaterFogColor(cameraEntity);
                }
            }
            if (ClientProxy.renderNukeSkyDarkFor > 0) {
                ClientProxy.renderNukeSkyDarkFor--;
            }
            if (ClientProxy.muteNonNukeSoundsFor > 0) {
                ClientProxy.muteNonNukeSoundsFor--;
                if (ClientProxy.masterVolumeNukeModifier < 1.0F) {
                    ClientProxy.masterVolumeNukeModifier += 0.1F;
                }
            } else if (ClientProxy.masterVolumeNukeModifier > 0.0F) {
                ClientProxy.masterVolumeNukeModifier -= 0.1F;
            }
            if (ClientProxy.lastBossLevel != Minecraft.getInstance().level) {
                ClientProxy.primordialBossActive = false;
                ClientProxy.primordialBossActiveAmount = 0;
                ClientProxy.lastBossLevel = Minecraft.getInstance().level;
            }
            if (ClientProxy.primordialBossActive) {
                if (ClientProxy.primordialBossActiveAmount < 1.0F) {
                    ClientProxy.primordialBossActiveAmount += 0.025F;
                }
            } else {
                if (ClientProxy.primordialBossActiveAmount > 0.0F) {
                    ClientProxy.primordialBossActiveAmount -= 0.025F;
                }
            }
            if (ClientProxy.renderNukeFlashFor > 0) {
                if (ClientProxy.nukeFlashAmount < 1F) {
                    ClientProxy.nukeFlashAmount = Math.min(ClientProxy.nukeFlashAmount + 0.4F, 1F);
                }
                ClientProxy.renderNukeFlashFor--;
            } else if (ClientProxy.nukeFlashAmount > 0F) {
                ClientProxy.nukeFlashAmount = Math.max(ClientProxy.nukeFlashAmount - 0.05F, 0F);
            }
            ClientProxy.prevPossessionStrengthAmount = ClientProxy.possessionStrengthAmount;
            if (Minecraft.getInstance().getCameraEntity() instanceof PossessesCamera watcherEntity) {
                if (watcherEntity.instant()) {
                    ClientProxy.possessionStrengthAmount = watcherEntity.getPossessionStrength(partialTicks);
                } else {
                    if (ClientProxy.possessionStrengthAmount < watcherEntity.getPossessionStrength(partialTicks)) {
                        ClientProxy.possessionStrengthAmount = Math.min(ClientProxy.possessionStrengthAmount + 0.2F, watcherEntity.getPossessionStrength(partialTicks));
                    } else {
                        ClientProxy.possessionStrengthAmount = Math.max(ClientProxy.possessionStrengthAmount - 0.2F, watcherEntity.getPossessionStrength(partialTicks));
                    }
                }
                if (watcherEntity instanceof BeholderEyeEntity beholderEye) {
                    beholderEye.setOldRots();
                    beholderEye.setEyeYRot(Minecraft.getInstance().player.getYHeadRot());
                    beholderEye.setEyeXRot(Minecraft.getInstance().player.getXRot());
                    if (AlexsCaves.PROXY.isKeyDown(4)) {
                        AlexsCaves.PROXY.resetRenderViewEntity(Minecraft.getInstance().player);
                    }
                }
            } else if (ClientProxy.possessionStrengthAmount > 0F) {
                ClientProxy.possessionStrengthAmount = Math.max(ClientProxy.possessionStrengthAmount - 0.05F, 0F);
            }
            if (Minecraft.getInstance().screen instanceof AdvancementsScreen advancementsScreen && advancementsScreen.selectedTab != null && ACAdvancementTabs.isAlexsCavesWidget(advancementsScreen.selectedTab.getAdvancement())) {
                ACAdvancementTabs.tick();
            }
            if (ClientProxy.primordialBossActive && Minecraft.getInstance().level != null && !Minecraft.getInstance().isPaused()) {
                ClientLevel level = Minecraft.getInstance().level;
                BlockPos cameraBlockPos = Minecraft.getInstance().getCameraEntity().blockPosition();
                BlockPos.MutableBlockPos trySpawnParticleBlockPos = new BlockPos.MutableBlockPos();
                int dist = 16;
                for (int particles = 0; particles < 100; ++particles) {
                    int i = cameraBlockPos.getX() + level.random.nextInt(dist) - level.random.nextInt(dist);
                    int j = cameraBlockPos.getY() + level.random.nextInt(dist) - level.random.nextInt(dist);
                    int k = cameraBlockPos.getZ() + level.random.nextInt(dist) - level.random.nextInt(dist);
                    trySpawnParticleBlockPos.set(i, j, k);
                    BlockState blockstate = level.getBlockState(trySpawnParticleBlockPos);
                    if (!blockstate.isCollisionShapeFullBlock(level, trySpawnParticleBlockPos)) {
                        level.addParticle(ParticleTypes.ASH, (double) trySpawnParticleBlockPos.getX() + level.random.nextDouble(), (double) trySpawnParticleBlockPos.getY() + level.random.nextDouble(), (double) trySpawnParticleBlockPos.getZ() + level.random.nextDouble(), 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderBlockScreenEffect(RenderBlockScreenEffectEvent event) {
        Player player = event.getPlayer();
        if (player.isPassenger() && player.getVehicle() instanceof SubmarineEntity && event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.WATER) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onComputeFOV(ViewportEvent.ComputeFov event) {
        if (event.getCamera().getEntity() instanceof PossessesCamera) {
            event.setFOV(90);
        }
        Player player = Minecraft.getInstance().player;
        FogType fogtype = event.getCamera().getFluidInCamera();
        if (player != null && player.isPassenger() && player.getVehicle() instanceof SubmarineEntity && fogtype == FogType.WATER) {
            float f = (float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0D, 0.85714287F);
            event.setFOV(event.getFOV() / f);
        }
    }

    @SubscribeEvent
    public void onComputeFOVModifier(ComputeFovModifierEvent event) {
        ItemStack itemstack = event.getPlayer().getUseItem();
        if (event.getPlayer().isUsingItem()) {
            if (itemstack.is(ACItemRegistry.DREADBOW.get())) {
                int i = event.getPlayer().getTicksUsingItem();
                float f1 = (float) i / 20.0F;
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                } else {
                    f1 *= f1;
                }
                event.setNewFovModifier(event.getFovModifier() * (1.0F - f1 * 0.15F));
            }
        }
    }

    @SubscribeEvent
    public void onSplashTextRender(EventRenderSplashText.Pre event) {
        if (ClientProxy.hasACSplashText) {
            event.setResult(Event.Result.ALLOW);
            event.setSplashText("30k downloads max");
            event.setSplashTextColor(0X00B6D5);
        }
    }

    @SubscribeEvent
    public void outlineColor(EventGetOutlineColor event) {
        if (Minecraft.getInstance().player.getUseItem() != null && Minecraft.getInstance().player.getUseItem().is(ACItemRegistry.TOTEM_OF_POSSESSION.get())) {
            ItemStack stack = Minecraft.getInstance().player.getUseItem();
            UUID boundUUID = TotemOfPossessionItem.getBoundEntityUUID(stack);
            if (boundUUID != null && boundUUID.equals(event.getEntityIn().getUUID())) {
                event.setResult(Event.Result.ALLOW);
                event.setColor(0xFF0000);
            }
        }
        if (event.getEntityIn() instanceof ItemEntity item) {
            if (item.getItem().is(ACItemRegistry.TECTONIC_SHARD.get())) {
                event.setResult(Event.Result.ALLOW);
                event.setColor(0XFFDB00);
            }
            if (item.getItem().is(ACItemRegistry.SWEET_TOOTH.get())) {
                event.setResult(Event.Result.ALLOW);
                event.setColor(0XFF8ACD);
            }
        }
    }

    public static void renderVanillaMapDecoration(MapDecoration mapdecoration, int k) {
        if(mapdecoration.getType() == ACVanillaMapUtil.UNDERGROUND_CABIN_MAP_DECORATION){
            MultiBufferSource multiBufferSource = lastVanillaMapRenderBuffer == null ? Minecraft.getInstance().renderBuffers().bufferSource() : lastVanillaMapRenderBuffer;
            PoseStack poseStack = lastVanillaMapPoseStack == null ? new PoseStack() : lastVanillaMapPoseStack;
            poseStack.pushPose();
            poseStack.translate(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F, 0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F, -0.02F);
            poseStack.mulPose(Axis.ZP.rotationDegrees((float)(mapdecoration.getRot() * 360) / 16.0F));
            poseStack.scale(4.0F, 4.0F, 3.0F);
            poseStack.translate(-0.125F, 0.125F, 0.0F);
            byte b0 = ACVanillaMapUtil.getMapIconRenderOrdinal(mapdecoration.getType());
            float f1 = (float)(b0 % 16 + 0) / 16.0F;
            float f2 = (float)(b0 / 16 + 0) / 16.0F;
            float f3 = (float)(b0 % 16 + 1) / 16.0F;
            float f4 = (float)(b0 / 16 + 1) / 16.0F;
            Matrix4f matrix4f1 = poseStack.last().pose();
            float f5 = -0.001F;
            VertexConsumer vertexconsumer1 = multiBufferSource.getBuffer(UNDERGROUND_CABIN_MAP_ICONS);
            vertexconsumer1.vertex(matrix4f1, -1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f1, f2).uv2(lastVanillaMapRenderPackedLight).endVertex();
            vertexconsumer1.vertex(matrix4f1, 1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f3, f2).uv2(lastVanillaMapRenderPackedLight).endVertex();
            vertexconsumer1.vertex(matrix4f1, 1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f3, f4).uv2(lastVanillaMapRenderPackedLight).endVertex();
            vertexconsumer1.vertex(matrix4f1, -1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f1, f4).uv2(lastVanillaMapRenderPackedLight).endVertex();
            poseStack.popPose();
            if (mapdecoration.getName() != null) {
                Font font = Minecraft.getInstance().font;
                Component component = mapdecoration.getName();
                float f6 = (float)font.width(component);
                float f7 = Mth.clamp(25.0F / f6, 0.0F, 6.0F / 9.0F);
                poseStack.pushPose();
                poseStack.translate(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F - f6 * f7 / 2.0F, 0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F + 4.0F, -0.025F);
                poseStack.scale(f7, f7, 1.0F);
                poseStack.translate(0.0F, 0.0F, -0.1F);
                font.drawInBatch(component, 0.0F, 0.0F, -1, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, Integer.MIN_VALUE, lastVanillaMapRenderPackedLight);
                poseStack.popPose();
            }
        }
    }
}
