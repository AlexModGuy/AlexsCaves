package com.github.alexmodguy.alexscaves.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.baked.BakedModelShadeLayerFullbright;
import com.github.alexmodguy.alexscaves.client.particle.*;
import com.github.alexmodguy.alexscaves.client.render.ACInternalShaders;
import com.github.alexmodguy.alexscaves.client.render.blockentity.*;
import com.github.alexmodguy.alexscaves.client.render.entity.*;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ClientLayerRegistry;
import com.github.alexmodguy.alexscaves.client.render.item.ACItemRenderProperties;
import com.github.alexmodguy.alexscaves.client.shader.ACPostEffectRegistry;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.HeadRotationEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACKeybindRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DeepsightEffect;
import com.github.alexthe666.citadel.client.event.EventLivingRenderer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;
import java.util.*;

@Mod.EventBusSubscriber(modid = AlexsCaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {

    private static final List<String> FULLBRIGHTS = ImmutableList.of("alexscaves:ambersol#", "alexscaves:radrock_uranium_ore#", "alexscaves:acidic_radrock#", "alexscaves:uranium_rod#axis=x", "alexscaves:uranium_rod#axis=y", "alexscaves:uranium_rod#axis=z", "alexscaves:block_of_uranium#", "alexscaves:abyssal_altar#active=true", "alexscaves:abyssmarine_");
    public static final ResourceLocation POTION_EFFECT_HUD_OVERLAYS = new ResourceLocation(AlexsCaves.MODID, "textures/misc/potion_effect_hud_overlays.png");
    public static final ResourceLocation BOMB_FLASH = new ResourceLocation(AlexsCaves.MODID, "textures/misc/bomb_flash.png");
    public static final ResourceLocation IRRADIATED_SHADER = new ResourceLocation(AlexsCaves.MODID, "shaders/post/irradiated.json");
    private static final ResourceLocation SUBMARINE_SHADER = new ResourceLocation("alexscaves:shaders/post/submarine_light.json");
    public static final ResourceLocation HOLOGRAM_SHADER = new ResourceLocation(AlexsCaves.MODID, "shaders/post/hologram.json");
    protected final RandomSource random = RandomSource.create();
    private int lastTremorTick = -1;
    private int updateCounter = 0;
    private int playerHealth = 0;
    private long lastSystemTime;
    private float[] randomTremorOffsets = new float[3];
    private List<UUID> blockedEntityRenders = new ArrayList<>();
    private Map<ClientLevel, List<BlockPos>> blockedParticleLocations = new HashMap<>();
    public int renderNukeFlashFor = 0;

    private float prevNukeFlashAmount = 0;
    private float nukeFlashAmount = 0;
    public boolean renderNukeSkyDark = false;
    private final ACItemRenderProperties isterProperties = new ACItemRenderProperties();

    public void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ClientProxy::setupParticles);
        bus.addListener(this::registerKeybinds);
    }

    public void clientInit() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ClientLayerRegistry::addLayers);
        bus.addListener(this::bakeModels);
        bus.addListener(this::registerShaders);
        BlockEntityRenderers.register(ACBlockEntityRegistry.MAGNET.get(), MagnetBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.TESLA_BULB.get(), TelsaBulbBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.AMBERSOL.get(), AmbersolBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.ABYSSAL_ALTAR.get(), AbyssalAltarBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.COPPER_VALVE.get(), CopperValveBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MOVING_METAL_BLOCK.get(), MovingMetalBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TELETOR.get(), TeletorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MAGNETIC_WEAPON.get(), MagneticWeaponRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MAGNETRON.get(), MagnetronRenderer::new);
        EntityRenderers.register(ACEntityRegistry.BOUNDROID.get(), BoundroidRenderer::new);
        EntityRenderers.register(ACEntityRegistry.BOUNDROID_WINCH.get(), BoundroidWinchRenderer::new);
        EntityRenderers.register(ACEntityRegistry.FERROUSLIME.get(), FerrouslimeRenderer::new);
        EntityRenderers.register(ACEntityRegistry.NOTOR.get(), NotorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.SUBTERRANODON.get(), SubterranodonRenderer::new);
        EntityRenderers.register(ACEntityRegistry.VALLUMRAPTOR.get(), VallumraptorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.GROTTOCERATOPS.get(), GrottoceratopsRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TRILOCARIS.get(), TrilocarisRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TREMORSAURUS.get(), TremorsaurusRenderer::new);
        EntityRenderers.register(ACEntityRegistry.RELICHEIRUS.get(), RelicheirusRenderer::new);
        EntityRenderers.register(ACEntityRegistry.FALLING_TREE_BLOCK.get(), FallingTreeBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.NUCLEAR_EXPLOSION.get(), NuclearExplosionRenderer::new);
        EntityRenderers.register(ACEntityRegistry.NUCLEAR_BOMB.get(), NuclearBombRenderer::new);
        EntityRenderers.register(ACEntityRegistry.NUCLEEPER.get(), NucleeperRenderer::new);
        EntityRenderers.register(ACEntityRegistry.RADGILL.get(), RadgillRenderer::new);
        EntityRenderers.register(ACEntityRegistry.BRAINIAC.get(), BrainiacRenderer::new);
        EntityRenderers.register(ACEntityRegistry.THROWN_WASTE_DRUM.get(), ThrownWasteDrumEntityRenderer::new);
        EntityRenderers.register(ACEntityRegistry.GAMMAROACH.get(), GammaroachRenderer::new);
        EntityRenderers.register(ACEntityRegistry.RAYCAT.get(), RaycatRenderer::new);
        EntityRenderers.register(ACEntityRegistry.CINDER_BRICK.get(), (context) -> {
            return new ThrownItemRenderer<>(context, 1.25F, false);
        });
        EntityRenderers.register(ACEntityRegistry.LANTERNFISH.get(), LanternfishRenderer::new);
        EntityRenderers.register(ACEntityRegistry.SEA_PIG.get(), SeaPigRenderer::new);
        EntityRenderers.register(ACEntityRegistry.SUBMARINE.get(), SubmarineRenderer::new);
        EntityRenderers.register(ACEntityRegistry.HULLBREAKER.get(), HullbreakerRenderer::new);
        EntityRenderers.register(ACEntityRegistry.GOSSAMER_WORM.get(), GossamerWormRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TRIPODFISH.get(), TripodfishRenderer::new);
        EntityRenderers.register(ACEntityRegistry.DEEP_ONE.get(), DeepOneRenderer::new);
        EntityRenderers.register(ACEntityRegistry.INK_BOMB.get(), (context) -> {
            return new ThrownItemRenderer<>(context, 1.25F, false);
        });
        EntityRenderers.register(ACEntityRegistry.DEEP_ONE_KNIGHT.get(), DeepOneKnightRenderer::new);
        EntityRenderers.register(ACEntityRegistry.DEEP_ONE_MAGE.get(), DeepOneMageRenderer::new);
        EntityRenderers.register(ACEntityRegistry.WATER_BOLT.get(), WaterBoltRenderer::new);
        EntityRenderers.register(ACEntityRegistry.WAVE.get(), WaveRenderer::new);
        Sheets.addWoodType(ACBlockRegistry.PEWEN_WOOD_TYPE);
        ItemProperties.register(ACItemRegistry.CAVE_MAP.get(), new ResourceLocation("filled"), (stack, level, living, j) -> {
            return CaveMapItem.isFilled(stack) ? 1F : 0F;
        });
        ItemProperties.register(ACItemRegistry.CAVE_MAP.get(), new ResourceLocation("loading"), (stack, level, living, j) -> {
            return CaveMapItem.isLoading(stack) ? 1F : 0F;
        });
        ItemProperties.register(ACItemRegistry.MAGIC_CONCH.get(), new ResourceLocation("tooting"), (stack, level, living, j) -> {
            return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
        });
        ItemProperties.register(ACItemRegistry.ORTHOLANCE.get(), new ResourceLocation("charging"), (stack, level, living, j) -> {
            return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
        });
        blockedParticleLocations.clear();
        ACPostEffectRegistry.registerEffect(IRRADIATED_SHADER);
        ACPostEffectRegistry.registerEffect(HOLOGRAM_SHADER);
    }

    public static void setupParticles(RegisterParticleProvidersEvent registry) {
        AlexsCaves.LOGGER.debug("Registered particle factories");
        registry.register(ACParticleRegistry.SCARLET_MAGNETIC_ORBIT.get(), new MagneticOrbitParticle.ScarletFactory());
        registry.register(ACParticleRegistry.AZURE_MAGNETIC_ORBIT.get(), new MagneticOrbitParticle.AzureFactory());
        registry.register(ACParticleRegistry.SCARLET_MAGNETIC_FLOW.get(), new MagneticFlowParticle.ScarletFactory());
        registry.register(ACParticleRegistry.AZURE_MAGNETIC_FLOW.get(), new MagneticFlowParticle.AzureFactory());
        registry.register(ACParticleRegistry.TESLA_BULB_LIGHTNING.get(), new TeslaBulbLightningParticle.Factory());
        registry.register(ACParticleRegistry.MAGNET_LIGHTNING.get(), new MagnetLightningParticle.Factory());
        registry.register(ACParticleRegistry.GALENA_DEBRIS.get(), GalenaDebrisParticle.Factory::new);
        registry.register(ACParticleRegistry.MAGNETIC_CAVES_AMBIENT.get(), new MagneticCavesAmbientParticle.Factory());
        registry.register(ACParticleRegistry.FERROUSLIME.get(), FerrouslimeParticle.Factory::new);
        registry.register(ACParticleRegistry.FLY.get(), FlyParticle.Factory::new);
        registry.register(ACParticleRegistry.WATER_TREMOR.get(), WaterTremorParticle.Factory::new);
        registry.register(ACParticleRegistry.ACID_BUBBLE.get(), AcidBubbleParticle.Factory::new);
        registry.register(ACParticleRegistry.BLACK_VENT_SMOKE.get(), VentSmokeParticle.BlackFactory::new);
        registry.register(ACParticleRegistry.WHITE_VENT_SMOKE.get(), VentSmokeParticle.WhiteFactory::new);
        registry.register(ACParticleRegistry.GREEN_VENT_SMOKE.get(), VentSmokeParticle.GreenFactory::new);
        registry.register(ACParticleRegistry.MUSHROOM_CLOUD.get(), new MushroomCloudParticle.Factory());
        registry.register(ACParticleRegistry.MUSHROOM_CLOUD_SMOKE.get(), MushroomCloudEffectParticle.Factory::new);
        registry.register(ACParticleRegistry.MUSHROOM_CLOUD_EXPLOSION.get(), MushroomCloudEffectParticle.Factory::new);
        registry.register(ACParticleRegistry.PROTON.get(), new ProtonParticle.Factory());
        registry.register(ACParticleRegistry.FALLOUT.get(), FalloutParticle.Factory::new);
        registry.register(ACParticleRegistry.GAMMAROACH.get(), GammaroachParticle.Factory::new);
        registry.register(ACParticleRegistry.RADGILL_SPLASH.get(), RadgillSplashParticle.Factory::new);
        registry.register(ACParticleRegistry.ACID_DROP.get(), AcidDropParticle.Factory::new);
        registry.register(ACParticleRegistry.TUBE_WORM.get(), new TubeWormParticle.Factory());
        registry.register(ACParticleRegistry.DEEP_ONE_MAGIC.get(), DeepOneMagicParticle.Factory::new);
        registry.register(ACParticleRegistry.WATER_FOAM.get(), WaterFoamParticle.Factory::new);
        registry.register(ACParticleRegistry.BIG_SPLASH.get(), new BigSplashParticle.Factory());
        registry.register(ACParticleRegistry.BIG_SPLASH_EFFECT.get(), BigSplashEffectParticle.Factory::new);
    }

    @SubscribeEvent
    public void setupEntityRotations(EventLivingRenderer.SetupRotations event) {
        if (event.getEntity() instanceof MagneticEntityAccessor magnetic) {
            float width = event.getEntity().getBbWidth();
            float height = event.getEntity().getBbHeight();
            float progress = magnetic.getAttachmentProgress(event.getPartialTicks());
            float prevProg = 1F - progress;
            float bodyRot = 180.0F - event.getBodyYRot();
            if (magnetic.getMagneticAttachmentFace().getAxis() != Direction.Axis.Y) {
                event.getPoseStack().mulPose(Axis.YN.rotationDegrees(1F * bodyRot));
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

        if (blockedEntityRenders.contains(event.getEntity().getUUID())) {
            MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(event.getEntity(), event.getRenderer(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
            event.setCanceled(true);
            blockedEntityRenders.remove(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public void postRenderLiving(RenderLivingEvent.Post event) {
        if (event.getEntity() instanceof HeadRotationEntityAccessor magnetic) {
            magnetic.resetMagnetHeadRotation();
        }
    }

    @SubscribeEvent
    public void postRenderStage(RenderLevelStageEvent event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            GameRenderer renderer = Minecraft.getInstance().gameRenderer;
            if (player.isPassenger() && player.getVehicle() instanceof SubmarineEntity submarine && SubmarineRenderer.isFirstPersonFloodlightsMode(submarine)) {
                if (renderer.currentEffect() == null || !SUBMARINE_SHADER.toString().equals(renderer.currentEffect().getName())) {
                    renderer.loadEffect(SUBMARINE_SHADER);
                }
            } else if (renderer.currentEffect() != null && SUBMARINE_SHADER.toString().equals(renderer.currentEffect().getName())) {
                renderer.checkEntityPostEffect(null);
            }
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS && AlexsCaves.CLIENT_CONFIG.ambersolShines.get()) {
            RenderSystem.runAsFancy(() -> AmbersolBlockRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            RenderSystem.runAsFancy(() -> NotorRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
    }

    @SubscribeEvent
    public void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player != null && AlexsCaves.CLIENT_CONFIG.screenShaking.get()) {
            float tremorAmount = renderNukeSkyDark ? 1.5F : 0F;
            float shakeDistanceScale = 20F;
            double distance = Double.MAX_VALUE;
            float partialTick = Minecraft.getInstance().getPartialTick();
            if (player.isOnGround() && tremorAmount == 0) {
                AABB aabb = player.getBoundingBox().inflate(shakeDistanceScale);
                for (TremorsaurusEntity tremorsaurus : Minecraft.getInstance().level.getEntitiesOfClass(TremorsaurusEntity.class, aabb)) {
                    if (tremorsaurus.distanceTo(player) < distance) {
                        distance = tremorsaurus.distanceTo(player);
                        tremorAmount = (1F - (float) (distance / shakeDistanceScale)) * Math.max(tremorsaurus.getScreenShakeAmount(partialTick), 0F);
                    }
                }
            }
            if (tremorAmount > 0) {
                if (lastTremorTick != player.tickCount) {
                    RandomSource rng = player.getLevel().random;
                    randomTremorOffsets[0] = rng.nextFloat();
                    randomTremorOffsets[1] = rng.nextFloat();
                    randomTremorOffsets[2] = rng.nextFloat();
                    lastTremorTick = player.tickCount;
                }
                double intensity = tremorAmount * Minecraft.getInstance().options.screenEffectScale().get();
                event.getCamera().move(randomTremorOffsets[0] * 0.2F * intensity, randomTremorOffsets[1] * 0.2F * intensity, randomTremorOffsets[2] * 0.5F * intensity);
            }
        }
        if (player != null && player.isPassenger() && player.getVehicle() instanceof SubmarineEntity && event.getCamera().isDetached()) {
            event.getCamera().move(-event.getCamera().getMaxZoom(4F), 0, 0);
        }
        Direction dir = MagnetUtil.getEntityMagneticDirection(player);

    }

    @SubscribeEvent
    public void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Player player = getClientSidePlayer();
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id()) && Minecraft.getInstance().gameMode.canHurtPlayer() && Minecraft.getInstance().getCameraEntity() instanceof Player && player.hasEffect(ACEffectRegistry.IRRADIATED.get())) {
            int leftHeight = 39;
            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();
            int health = Mth.ceil(player.getHealth());
            if (health < this.playerHealth && player.invulnerableTime > 0) {
                this.lastSystemTime = System.currentTimeMillis();
            } else if (health > this.playerHealth && player.invulnerableTime > 0) {
                this.lastSystemTime = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - this.lastSystemTime > 1000L) {
                this.playerHealth = health;
                this.lastSystemTime = System.currentTimeMillis();
            }

            this.playerHealth = health;
            AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            float healthMax = (float) attrMaxHealth.getValue();
            float absorb = Mth.ceil(player.getAbsorptionAmount());

            int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);

            this.random.setSeed((long) (updateCounter * 312871));

            int left = width / 2 - 91;
            int top = height - leftHeight;
            int regen = -1;
            if (player.hasEffect(MobEffects.REGENERATION)) {
                regen = updateCounter % 25;
            }
            final int heartV = player.level.getLevelData().isHardcore() ? 9 : 0;
            int heartU = 0;
            float absorbRemaining = absorb;
            event.getPoseStack().pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, POTION_EFFECT_HUD_OVERLAYS);
            for (int i = Mth.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                int row = Mth.ceil((float) (i + 1) / 10.0F) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;
                if (health <= 4) {
                    y += random.nextInt(2);
                }
                if (i == regen) {
                    y -= 2;
                }
                GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU, heartV + 18, 9, 9, 32, 32);
                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                        GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU, heartV, 9, 9, 32, 32);
                        absorbRemaining -= 1.0F;
                    } else {
                        GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                        absorbRemaining -= 2.0F;
                    }
                } else {
                    if (i * 2 + 1 < health) {
                        GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU, heartV, 9, 9, 32, 32);
                    } else if (i * 2 + 1 == health) {
                        GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                    }
                }
            }
            event.getPoseStack().popPose();
        }
    }

    @SubscribeEvent
    public void fogRender(ViewportEvent.RenderFog event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        FluidState fluidstate = player.level.getFluidState(event.getCamera().getBlockPosition());
        if (!fluidstate.isEmpty() && fluidstate.getType().getFluidType().equals(ACFluidRegistry.ACID_FLUID_TYPE.get())) {
            event.setCanceled(true);
            float farness = 10.0F;
            if(Minecraft.getInstance().player.hasEffect(ACEffectRegistry.DEEPSIGHT.get())){
                farness *= 1.0F + 1.5F * DeepsightEffect.getIntensity(Minecraft.getInstance().player, (float)event.getPartialTick());
            }
            event.setFarPlaneDistance(farness);
            event.setNearPlaneDistance(0.0F);
            return;
        }
        if (event.getCamera().getFluidInCamera() == FogType.WATER) {
            int i = Minecraft.getInstance().options.biomeBlendRadius().get();
            float farness;
            if (i == 0) {
                farness = ACBiomeRegistry.getBiomeWaterFogFarness(player.level.getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeWaterFogFarness(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
                });
                farness = (float) vec31.x;
            }
            if(Minecraft.getInstance().player.hasEffect(ACEffectRegistry.DEEPSIGHT.get())){
                farness *= 1.0F + 1.5F * DeepsightEffect.getIntensity(Minecraft.getInstance().player, (float)event.getPartialTick());
            }
            if (farness != 1.0F) {
                event.setCanceled(true);
                event.setFarPlaneDistance(event.getFarPlaneDistance() * farness);
            }
        } else if (event.getMode() == FogRenderer.FogMode.FOG_TERRAIN) {
            int i = Minecraft.getInstance().options.biomeBlendRadius().get();
            float nearness;
            if (i == 0) {
                nearness = ACBiomeRegistry.getBiomeFogNearness(player.level.getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeFogNearness(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
                });
                nearness = (float) vec31.x;
            }

            if (nearness != 1.0F) {
                event.setCanceled(true);
                event.setNearPlaneDistance(event.getNearPlaneDistance() * nearness);
            }
        }
    }

    @SubscribeEvent
    public void fogRender(ViewportEvent.ComputeFogColor event) {
        Entity player = Minecraft.getInstance().player;
        if (player.getEyeInFluidType() != null && player.getEyeInFluidType().equals(ACFluidRegistry.ACID_FLUID_TYPE.get())) {
            event.setRed((float) (0));
            event.setGreen((float) (1));
            event.setBlue((float) (0));
        } else if (event.getCamera().getFluidInCamera() == FogType.NONE) {
            int i = Minecraft.getInstance().options.biomeBlendRadius().get();
            float override;
            if (i == 0) {
                override = ACBiomeRegistry.getBiomeSkyOverride(player.level.getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeSkyOverride(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
                });
                override = (float) vec31.x;
            }
            if (override != 0.0F) {
                Vec3 vec3;
                if (i == 0) {
                    vec3 = ((ClientLevel) player.level).effects().getBrightnessDependentFogColor(Vec3.fromRGB24(player.level.getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getFogColor()), override);
                } else {
                    vec3 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                        return ((ClientLevel) player.level).effects().getBrightnessDependentFogColor(Vec3.fromRGB24(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z).value().getFogColor()), override);
                    });
                }
                event.setRed((float) (vec3.x));
                event.setGreen((float) (vec3.y));
                event.setBlue((float) (vec3.z));
            }
        } else if (event.getCamera().getFluidInCamera() == FogType.WATER) {
            int i = Minecraft.getInstance().options.biomeBlendRadius().get();
            Vec3 vec3;
            float override;
            if (i == 0) {
                override = ACBiomeRegistry.getBiomeSkyOverride(player.level.getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeSkyOverride(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
                });
                override = (float) vec31.x;
            }
            if (override != 0) {
                if (i == 0) {
                    vec3 = Vec3.fromRGB24(player.level.getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getWaterFogColor());
                } else {
                    vec3 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                        return Vec3.fromRGB24(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z).value().getWaterFogColor());
                    });
                }
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

    private void bakeModels(final ModelEvent.ModifyBakingResult e) {
        if(AlexsCaves.CLIENT_CONFIG.emissiveBlockModels.get()){
            long time = System.currentTimeMillis();
            for (ResourceLocation id : e.getModels().keySet()) {
                if(FULLBRIGHTS.stream().anyMatch(str -> id.toString().startsWith(str))){
                    e.getModels().put(id, new BakedModelShadeLayerFullbright(e.getModels().get(id)));
                }
            }
            AlexsCaves.LOGGER.info("Loaded emissive block models in {} ms", System.currentTimeMillis() - time);

        }
    }

    private void registerShaders(final RegisterShadersEvent e) {
        try {
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(AlexsCaves.MODID, "rendertype_ferrouslime_gel"), DefaultVertexFormat.NEW_ENTITY), ACInternalShaders::setRenderTypeFerrouslimeGelShader);
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(AlexsCaves.MODID, "rendertype_irradiated"), DefaultVertexFormat.POSITION_COLOR_TEX), ACInternalShaders::setRenderTypeIrradiatedShader);
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(AlexsCaves.MODID, "rendertype_bubbled"), DefaultVertexFormat.NEW_ENTITY), ACInternalShaders::setRenderTypeBubbledShader);
            AlexsCaves.LOGGER.info("registered internal shaders");
        } catch (IOException exception) {
            AlexsCaves.LOGGER.error("could not register internal shaders");
            exception.printStackTrace();
        }
    }

    private void registerKeybinds(RegisterKeyMappingsEvent e){
        e.register(ACKeybindRegistry.KEY_SUB_FLOODLIGHTS);
    }


    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }


    public void blockRenderingEntity(UUID id) {
        blockedEntityRenders.add(id);
    }

    public void releaseRenderingEntity(UUID id) {
        blockedEntityRenders.remove(id);
    }

    public void setVisualFlag(int flag) {
    }

    public float getNukeFlashAmount(float partialTicks) {
        return prevNukeFlashAmount + (nukeFlashAmount - prevNukeFlashAmount) * partialTicks;
    }

    public boolean checkIfParticleAt(SimpleParticleType simpleParticleType, BlockPos at) {
        if (!blockedParticleLocations.containsKey(Minecraft.getInstance().level)) {
            blockedParticleLocations.clear();
            blockedParticleLocations.put(Minecraft.getInstance().level, new ArrayList<>());
        }
        if (simpleParticleType == ACParticleRegistry.TUBE_WORM.get()) {
            List blocked = blockedParticleLocations.get(Minecraft.getInstance().level);
            if (blocked.contains(at)) {
                return false;
            } else {
                blocked.add(new BlockPos(at));
                return true;
            }
        }
        return true;
    }

    public void removeParticleAt(BlockPos at) {
        if (!blockedParticleLocations.containsKey(Minecraft.getInstance().level)) {
            blockedParticleLocations.clear();
            blockedParticleLocations.put(Minecraft.getInstance().level, new ArrayList<>());
        }
        blockedParticleLocations.get(Minecraft.getInstance().level).remove(at);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            prevNukeFlashAmount = nukeFlashAmount;
            if (renderNukeFlashFor > 0) {
                if (nukeFlashAmount < 1F) {
                    nukeFlashAmount = Math.min(nukeFlashAmount + 0.4F, 1F);
                }
                renderNukeFlashFor--;
            } else if (nukeFlashAmount > 0F) {
                nukeFlashAmount = Math.max(nukeFlashAmount - 0.05F, 0F);
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
        Player player = Minecraft.getInstance().player;
        FogType fogtype = event.getCamera().getFluidInCamera();
        if (player.isPassenger() && player.getVehicle() instanceof SubmarineEntity && fogtype == FogType.WATER) {
            float f = (float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0D, (double) 0.85714287F);
            event.setFOV(event.getFOV() / f);
        }
    }
    public boolean isKeyDown(int keyType) {
        if (keyType == 0) {
            return Minecraft.getInstance().options.keyJump.isDown();
        }
        if (keyType == 1) {
            return Minecraft.getInstance().options.keySprint.isDown();
        }
        if (keyType == 2) {
            return ACKeybindRegistry.KEY_SUB_FLOODLIGHTS.isDown();
        }
        return false;
    }

    @Override
    public Object getISTERProperties() {
        return isterProperties;
    }

    public float getPartialTicks() {
        return Minecraft.getInstance().getPartialTick();
    }
}

