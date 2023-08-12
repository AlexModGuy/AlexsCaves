package com.github.alexmodguy.alexscaves.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.gui.ACAdvancementTabs;
import com.github.alexmodguy.alexscaves.client.gui.NuclearFurnaceScreen;
import com.github.alexmodguy.alexscaves.client.gui.SpelunkeryTableScreen;
import com.github.alexmodguy.alexscaves.client.model.baked.BakedModelShadeLayerFullbright;
import com.github.alexmodguy.alexscaves.client.particle.*;
import com.github.alexmodguy.alexscaves.client.render.ACInternalShaders;
import com.github.alexmodguy.alexscaves.client.render.blockentity.*;
import com.github.alexmodguy.alexscaves.client.render.entity.*;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ClientLayerRegistry;
import com.github.alexmodguy.alexscaves.client.render.item.ACArmorRenderProperties;
import com.github.alexmodguy.alexscaves.client.render.item.ACItemRenderProperties;
import com.github.alexmodguy.alexscaves.client.render.item.RaygunRenderHelper;
import com.github.alexmodguy.alexscaves.client.shader.ACPostEffectRegistry;
import com.github.alexmodguy.alexscaves.client.sound.NuclearSirenSound;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.DinosaurEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.*;
import com.github.alexmodguy.alexscaves.server.inventory.ACMenuRegistry;
import com.github.alexmodguy.alexscaves.server.item.*;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACKeybindRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import com.github.alexmodguy.alexscaves.server.potion.DeepsightEffect;
import com.github.alexthe666.citadel.client.event.EventLivingRenderer;
import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import com.github.alexthe666.citadel.client.event.EventRenderSplashText;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

@Mod.EventBusSubscriber(modid = AlexsCaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {

    private static final List<String> FULLBRIGHTS = ImmutableList.of("alexscaves:ambersol#", "alexscaves:radrock_uranium_ore#", "alexscaves:acidic_radrock#", "alexscaves:uranium_rod#axis=x", "alexscaves:uranium_rod#axis=y", "alexscaves:uranium_rod#axis=z", "alexscaves:block_of_uranium#", "alexscaves:abyssal_altar#active=true", "alexscaves:abyssmarine_", "alexscaves:peering_coprolith#", "alexscaves:forsaken_idol#", "alexscaves:magnetic_light#");
    public static final ResourceLocation POTION_EFFECT_HUD_OVERLAYS = new ResourceLocation(AlexsCaves.MODID, "textures/misc/potion_effect_hud_overlays.png");
    public static final ResourceLocation DINOSAUR_HUD_OVERLAYS = new ResourceLocation(AlexsCaves.MODID, "textures/misc/dinosaur_hud_overlays.png");
    public static final ResourceLocation ARMOR_HUD_OVERLAYS = new ResourceLocation(AlexsCaves.MODID, "textures/misc/armor_hud_overlays.png");
    public static final ResourceLocation BOMB_FLASH = new ResourceLocation(AlexsCaves.MODID, "textures/misc/bomb_flash.png");
    public static final ResourceLocation WATCHER_EFFECT = new ResourceLocation(AlexsCaves.MODID, "textures/misc/watcher_effect.png");
    public static final ResourceLocation IRRADIATED_SHADER = new ResourceLocation(AlexsCaves.MODID, "shaders/post/irradiated.json");
    private static final ResourceLocation SUBMARINE_SHADER = new ResourceLocation(AlexsCaves.MODID, "shaders/post/submarine_light.json");
    public static final ResourceLocation HOLOGRAM_SHADER = new ResourceLocation(AlexsCaves.MODID, "shaders/post/hologram.json");
    private static final ResourceLocation WATCHER_SHADER = new ResourceLocation(AlexsCaves.MODID, "shaders/post/watcher_perspective.json");
    private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/particle/teletor_trail.png");
    protected final RandomSource random = RandomSource.create();
    private int lastTremorTick = -1;
    private int updateCounter = 0;
    private int playerHealth = 0;
    private long lastSystemTime;
    private float[] randomTremorOffsets = new float[3];
    private List<UUID> blockedEntityRenders = new ArrayList<>();
    private Map<ClientLevel, List<BlockPos>> blockedParticleLocations = new HashMap<>();
    private Map<LivingEntity, Vec3[]> darknessTrailPosMap = new HashMap<>();
    private Map<LivingEntity, Integer> darknessTrailPointerMap = new HashMap<>();
    public int renderNukeFlashFor = 0;
    private float prevNukeFlashAmount = 0;
    private float nukeFlashAmount = 0;
    private float prevPossessionStrengthAmount = 0;
    private float possessionStrengthAmount = 0;
    public boolean renderNukeSkyDark = false;

    public static NuclearSirenSound closestSirenSound = null;
    private final ACItemRenderProperties isterProperties = new ACItemRenderProperties();
    private final ACArmorRenderProperties armorProperties = new ACArmorRenderProperties();
    private boolean spelunkeryTutorialComplete;
    private boolean hasACSplashText = false;
    private CameraType lastPOV = CameraType.FIRST_PERSON;


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
        EntityRenderers.register(ACEntityRegistry.BOAT.get(), (context) -> {
            return new AlexsCavesBoatRenderer(context, false);
        });
        EntityRenderers.register(ACEntityRegistry.CHEST_BOAT.get(), (context) -> {
            return new AlexsCavesBoatRenderer(context, true);
        });
        BlockEntityRenderers.register(ACBlockEntityRegistry.MAGNET.get(), MagnetBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.TESLA_BULB.get(), TelsaBulbBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.HOLOGRAM_PROJECTOR.get(), HologramProjectorBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.QUARRY.get(), QuarryBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.AMBERSOL.get(), AmbersolBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.AMBER_MONOLITH.get(), AmberMonolithBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.NUCLEAR_FURNACE.get(), NuclearFurnaceBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.SIREN_LIGHT.get(), SirenLightBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.ABYSSAL_ALTAR.get(), AbyssalAltarBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.COPPER_VALVE.get(), CopperValveBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.BEHOLDER.get(), BeholderBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MOVING_METAL_BLOCK.get(), MovingMetalBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TELETOR.get(), TeletorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MAGNETIC_WEAPON.get(), MagneticWeaponRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MAGNETRON.get(), MagnetronRenderer::new);
        EntityRenderers.register(ACEntityRegistry.BOUNDROID.get(), BoundroidRenderer::new);
        EntityRenderers.register(ACEntityRegistry.BOUNDROID_WINCH.get(), BoundroidWinchRenderer::new);
        EntityRenderers.register(ACEntityRegistry.FERROUSLIME.get(), FerrouslimeRenderer::new);
        EntityRenderers.register(ACEntityRegistry.NOTOR.get(), NotorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.QUARRY_SMASHER.get(), QuarrySmasherRenderer::new);
        EntityRenderers.register(ACEntityRegistry.SEEKING_ARROW.get(), SeekingArrowRenderer::new);
        EntityRenderers.register(ACEntityRegistry.SUBTERRANODON.get(), SubterranodonRenderer::new);
        EntityRenderers.register(ACEntityRegistry.VALLUMRAPTOR.get(), VallumraptorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.GROTTOCERATOPS.get(), GrottoceratopsRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TRILOCARIS.get(), TrilocarisRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TREMORSAURUS.get(), TremorsaurusRenderer::new);
        EntityRenderers.register(ACEntityRegistry.RELICHEIRUS.get(), RelicheirusRenderer::new);
        EntityRenderers.register(ACEntityRegistry.FALLING_TREE_BLOCK.get(), FallingTreeBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.LIMESTONE_SPEAR.get(), LimestoneSpearRenderer::new);
        EntityRenderers.register(ACEntityRegistry.NUCLEAR_EXPLOSION.get(), EmptyRenderer::new);
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
        EntityRenderers.register(ACEntityRegistry.MINE_GUARDIAN.get(), MineGuardianRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MINE_GUARDIAN_ANCHOR.get(), MineGuardianAnchorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.DEPTH_CHARGE.get(), (context) -> {
            return new ThrownItemRenderer<>(context, 1.75F, true);
        });
        EntityRenderers.register(ACEntityRegistry.FLOATER.get(), FloaterRenderer::new);
        EntityRenderers.register(ACEntityRegistry.GUANO.get(), (context) -> {
            return new ThrownItemRenderer<>(context, 1.25F, false);
        });
        EntityRenderers.register(ACEntityRegistry.FALLING_GUANO.get(), FallingBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.GLOOMOTH.get(), GloomothRenderer::new);
        EntityRenderers.register(ACEntityRegistry.UNDERZEALOT.get(), UnderzealotRenderer::new);
        EntityRenderers.register(ACEntityRegistry.WATCHER.get(), WatcherRenderer::new);
        EntityRenderers.register(ACEntityRegistry.CORRODENT.get(), CorrodentRenderer::new);
        EntityRenderers.register(ACEntityRegistry.VESPER.get(), VesperRenderer::new);
        EntityRenderers.register(ACEntityRegistry.FORSAKEN.get(), ForsakenRenderer::new);
        EntityRenderers.register(ACEntityRegistry.BEHOLDER_EYE.get(), EmptyRenderer::new);
        Sheets.addWoodType(ACBlockRegistry.PEWEN_WOOD_TYPE);
        Sheets.addWoodType(ACBlockRegistry.THORNWOOD_WOOD_TYPE);
        ItemProperties.register(ACItemRegistry.CAVE_MAP.get(), new ResourceLocation("filled"), (stack, level, living, j) -> {
            return CaveMapItem.isFilled(stack) ? 1F : 0F;
        });
        ItemProperties.register(ACItemRegistry.CAVE_MAP.get(), new ResourceLocation("loading"), (stack, level, living, j) -> {
            return CaveMapItem.isLoading(stack) ? 1F : 0F;
        });
        ItemProperties.register(ACItemRegistry.HOLOCODER.get(), new ResourceLocation("bound"), (stack, level, living, j) -> {
            return HolocoderItem.isBound(stack) ? 1.0F : 0.0F;
        });
        ItemProperties.register(ACItemRegistry.DINOSAUR_NUGGET.get(), new ResourceLocation("nugget"), (stack, level, living, j) -> {
            return (stack.getCount() % 4) / 4F;
        });
        ItemProperties.register(ACItemRegistry.LIMESTONE_SPEAR.get(), new ResourceLocation("throwing"), (stack, level, living, j) -> {
            return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
        });
        ItemProperties.register(ACItemRegistry.REMOTE_DETONATOR.get(), new ResourceLocation("active"), (stack, level, living, j) -> {
            return RemoteDetonatorItem.isActive(stack) ? 1.0F : 0.0F;
        });
        ItemProperties.register(ACItemRegistry.MAGIC_CONCH.get(), new ResourceLocation("tooting"), (stack, level, living, j) -> {
            return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
        });
        ItemProperties.register(ACItemRegistry.ORTHOLANCE.get(), new ResourceLocation("charging"), (stack, level, living, j) -> {
            return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
        });
        ItemProperties.register(ACItemRegistry.TOTEM_OF_POSSESSION.get(), new ResourceLocation("totem"), (stack, level, living, j) -> {
            return TotemOfPossessionItem.isBound(stack) ? living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.5F : 0.0F;
        });
        blockedParticleLocations.clear();
        ACPostEffectRegistry.registerEffect(IRRADIATED_SHADER);
        ACPostEffectRegistry.registerEffect(HOLOGRAM_SHADER);
        MenuScreens.register(ACMenuRegistry.SPELUNKERY_TABLE_MENU.get(), SpelunkeryTableScreen::new);
        MenuScreens.register(ACMenuRegistry.NUCLEAR_FURNACE_MENU.get(), NuclearFurnaceScreen::new);
        hasACSplashText = random.nextInt(30) == 0;
    }

    public static void setupParticles(RegisterParticleProvidersEvent registry) {
        AlexsCaves.LOGGER.debug("Registered particle factories");
        registry.registerSpecial(ACParticleRegistry.SCARLET_MAGNETIC_ORBIT.get(), new MagneticOrbitParticle.ScarletFactory());
        registry.registerSpecial(ACParticleRegistry.AZURE_MAGNETIC_ORBIT.get(), new MagneticOrbitParticle.AzureFactory());
        registry.registerSpecial(ACParticleRegistry.SCARLET_MAGNETIC_FLOW.get(), new MagneticFlowParticle.ScarletFactory());
        registry.registerSpecial(ACParticleRegistry.AZURE_MAGNETIC_FLOW.get(), new MagneticFlowParticle.AzureFactory());
        registry.registerSpecial(ACParticleRegistry.TESLA_BULB_LIGHTNING.get(), new TeslaBulbLightningParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.MAGNET_LIGHTNING.get(), new MagnetLightningParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.GALENA_DEBRIS.get(), GalenaDebrisParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.MAGNETIC_CAVES_AMBIENT.get(), new MagneticCavesAmbientParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.FERROUSLIME.get(), FerrouslimeParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.QUARRY_BORDER_LIGHTING.get(), new QuarryBorderLightningParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.AZURE_SHIELD_LIGHTNING.get(), new ResistorShieldLightningParticle.AzureFactory());
        registry.registerSpecial(ACParticleRegistry.SCARLET_SHIELD_LIGHTNING.get(), new ResistorShieldLightningParticle.ScarletFactory());
        registry.registerSpriteSet(ACParticleRegistry.FLY.get(), FlyParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.WATER_TREMOR.get(), WaterTremorParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.AMBER_MONOLITH.get(), AmberMonolithParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.AMBER_EXPLOSION.get(), SmallExplosionParticle.AmberFactory::new);
        registry.registerSpecial(ACParticleRegistry.STUN_STAR.get(), new StunStarParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.ACID_BUBBLE.get(), AcidBubbleParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.BLACK_VENT_SMOKE.get(), VentSmokeParticle.BlackFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.WHITE_VENT_SMOKE.get(), VentSmokeParticle.WhiteFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.GREEN_VENT_SMOKE.get(), VentSmokeParticle.GreenFactory::new);
        registry.registerSpecial(ACParticleRegistry.MUSHROOM_CLOUD.get(), new MushroomCloudParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.MUSHROOM_CLOUD_SMOKE.get(), SmallExplosionParticle.NukeFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.MUSHROOM_CLOUD_EXPLOSION.get(), SmallExplosionParticle.NukeFactory::new);
        registry.registerSpecial(ACParticleRegistry.PROTON.get(), new ProtonParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.FALLOUT.get(), FalloutParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.GAMMAROACH.get(), GammaroachParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.HAZMAT_BREATHE.get(), HazmatBreatheParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.RADGILL_SPLASH.get(), RadgillSplashParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.ACID_DROP.get(), AcidDropParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.NUCLEAR_SIREN_SONAR.get(), NuclearSirenSonarParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.RAYGUN_EXPLOSION.get(), SmallExplosionParticle.RaygunFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.RAYGUN_BLAST.get(), RaygunBlastParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.TUBE_WORM.get(), new TubeWormParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.DEEP_ONE_MAGIC.get(), DeepOneMagicParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.WATER_FOAM.get(), WaterFoamParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.BIG_SPLASH.get(), new BigSplashParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.BIG_SPLASH_EFFECT.get(), BigSplashEffectParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.MINE_EXPLOSION.get(), SmallExplosionParticle.MineFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.BIO_POP.get(), BioPopParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.WATCHER_APPEARANCE.get(), new WatcherAppearanceParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.VOID_BEING_CLOUD.get(), new VoidBeingCloudParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.VOID_BEING_TENDRIL.get(), new VoidBeingTendrilParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.VOID_BEING_EYE.get(), new VoidBeingEyeParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.UNDERZEALOT_MAGIC.get(), UnderzealotMagicParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.UNDERZEALOT_EXPLOSION.get(), SmallExplosionParticle.UnderzealotFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.FALLING_GUANO.get(), FallingGuanoParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.MOTH_DUST.get(), MothDustParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.FORSAKEN_SPIT.get(), ForsakenSpitParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.FORSAKEN_SONAR.get(), ForsakenSonarParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.FORSAKEN_SONAR_LARGE.get(), ForsakenSonarParticle.LargeFactory::new);
    }

    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        AlexsCaves.LOGGER.info("loaded in item colorizer");
        event.register((stack, colorIn) -> colorIn != 1 ? -1 : CaveInfoItem.getBiomeColorOf(Minecraft.getInstance().level, stack), ACItemRegistry.CAVE_TABLET.get());
        event.register((stack, colorIn) -> colorIn != 1 ? -1 : CaveInfoItem.getBiomeColorOf(Minecraft.getInstance().level, stack), ACItemRegistry.CAVE_CODEX.get());
        event.register((stack, colorIn) -> colorIn != 0 ? -1 : GazingPearlItem.getPearlColor(stack), ACItemRegistry.GAZING_PEARL.get());
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
            if (!isFirstPersonPlayer(event.getEntity())) {
                MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(event.getEntity(), event.getRenderer(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
                event.setCanceled(true);
            }
            blockedEntityRenders.remove(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public void postRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = event.getEntity();
        float partialTick = event.getPartialTick();
        if (entity instanceof HeadRotationEntityAccessor magnetic) {
            magnetic.resetMagnetHeadRotation();
        }
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() || Minecraft.getInstance().cameraEntity == null || !entity.is(Minecraft.getInstance().cameraEntity)) {
            RaygunRenderHelper.renderRaysFor(entity, entity.getPosition(partialTick), event.getPoseStack(), event.getMultiBufferSource(), partialTick, false);
        }
        if(entity.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get()) && entity.isAlive()){
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

    @SubscribeEvent
    public void postRenderStage(RenderLevelStageEvent event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        boolean firstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            GameRenderer renderer = Minecraft.getInstance().gameRenderer;
            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            if (player instanceof LivingEntity living) {
                Vec3 cameraPos = event.getCamera().getPosition();
                RaygunRenderHelper.renderRaysFor(living, cameraPos, event.getPoseStack(), multibuffersource$buffersource, event.getPartialTick(), true);
            }
            if (firstPerson && player.isPassenger() && player.getVehicle() instanceof SubmarineEntity submarine && SubmarineRenderer.isFirstPersonFloodlightsMode(submarine)) {
                if (renderer.currentEffect() == null || !SUBMARINE_SHADER.toString().equals(renderer.currentEffect().getName())) {
                    renderer.loadEffect(SUBMARINE_SHADER);
                }
            } else if (renderer.currentEffect() != null && SUBMARINE_SHADER.toString().equals(renderer.currentEffect().getName())) {
                renderer.checkEntityPostEffect(null);
            }
            if (firstPerson && player instanceof PossessesCamera || player instanceof LivingEntity afflicted && afflicted.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get())) {
                if (renderer.currentEffect() == null || !WATCHER_SHADER.toString().equals(renderer.currentEffect().getName())) {
                    renderer.loadEffect(WATCHER_SHADER);
                }
            } else if (renderer.currentEffect() != null && WATCHER_SHADER.toString().equals(renderer.currentEffect().getName())) {
                renderer.checkEntityPostEffect(null);
            }
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            RenderSystem.runAsFancy(() -> HologramProjectorBlockRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            RenderSystem.runAsFancy(() -> CorrodentRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS && AlexsCaves.CLIENT_CONFIG.ambersolShines.get()) {
            RenderSystem.runAsFancy(() -> AmbersolBlockRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
    }

    @SubscribeEvent
    public void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        float partialTick = Minecraft.getInstance().getPartialTick();
        float tremorAmount = renderNukeSkyDark ? 1.5F : 0F;
        if (player instanceof PossessesCamera watcherEntity) {
            Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
            tremorAmount = watcherEntity.isPossessionBreakable() ? getPossessionStrengthAmount(partialTick) : 0F;
        }
        if (player != null && AlexsCaves.CLIENT_CONFIG.screenShaking.get()) {
            float shakeDistanceScale = 20F;
            double distance = Double.MAX_VALUE;
            if (tremorAmount == 0) {
                AABB aabb = player.getBoundingBox().inflate(shakeDistanceScale);
                for (Mob screenShaker : Minecraft.getInstance().level.getEntitiesOfClass(Mob.class, aabb, (mob -> mob instanceof ShakesScreen))) {
                    if (((ShakesScreen) screenShaker).canFeelShake(player) && screenShaker.distanceTo(player) < distance) {
                        distance = screenShaker.distanceTo(player);
                        tremorAmount = (1F - (float) (distance / shakeDistanceScale)) * Math.max(((ShakesScreen) screenShaker).getScreenShakeAmount(partialTick), 0F);
                    }
                }
            }
            if (tremorAmount > 0) {
                if (lastTremorTick != player.tickCount) {
                    RandomSource rng = player.level().random;
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
        if (player != null && player.isPassenger() && player.getVehicle() instanceof TremorsaurusEntity && event.getCamera().isDetached()) {
            event.getCamera().move(-event.getCamera().getMaxZoom(2F), 0, 0);
        }
        if (player != null && player instanceof LivingEntity livingEntity && livingEntity.hasEffect(ACEffectRegistry.STUNNED.get())) {
            event.setRoll((float) (Math.sin((player.tickCount + partialTick) * 0.2F) * 10F));
        }
        Direction dir = MagnetUtil.getEntityMagneticDirection(player);

    }

    @SubscribeEvent
    public void clientLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if(!entity.level().isClientSide){
            return;
        }
        if (entity.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get()) && entity.isAlive()) {
            int trailPointer = darknessTrailPointerMap.getOrDefault(entity, -1);
            Vec3 latest = entity.position();
            if (darknessTrailPosMap.get(entity) == null) {
                Vec3[] trailPositions = new Vec3[64];
                if (trailPointer == -1) {
                    Arrays.fill(trailPositions, latest);
                }
                darknessTrailPosMap.put(entity, trailPositions);
            }
            if (++trailPointer == darknessTrailPosMap.get(entity).length) {
                trailPointer = 0;
            }
            darknessTrailPointerMap.put(entity, trailPointer);
            Vec3[] vector3ds = darknessTrailPosMap.get(entity);
            vector3ds[trailPointer] = latest;
            darknessTrailPosMap.put(entity, vector3ds);
        } else if (darknessTrailPosMap.containsKey(entity)) {
            darknessTrailPosMap.remove(entity);
            darknessTrailPointerMap.remove(entity);
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
        if (player != null && player.getVehicle() instanceof DinosaurEntity dinosaur && dinosaur.hasRidingMeter() && event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
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
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof LimestoneSpearItem && player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
            float f7 = (float) (player.getItemInHand(InteractionHand.MAIN_HAND).getUseDuration() - ((float) player.getUseItemRemainingTicks() - f + 1.0F)) / 10.0F;
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                rightHandSpearUseProgress = Math.max(rightHandSpearUseProgress, f7);
            } else {
                leftHandSpearUseProgress = Math.max(leftHandSpearUseProgress, f7);
            }
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof LimestoneSpearItem && player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
            float f7 = (float) (player.getItemInHand(InteractionHand.OFF_HAND).getUseDuration() - ((float) player.getUseItemRemainingTicks() - f + 1.0F)) / 10.0F;
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
            event.getModel().leftArm.xRot = (float) Math.toRadians(45F) + useProgress * (float) Math.toRadians(115F);
            event.getModel().leftArm.yRot = useProgressMiddle * (float) Math.toRadians(25F);
            event.getModel().leftArm.zRot = useProgressMiddle * (float) Math.toRadians(25F);
            event.setResult(Event.Result.ALLOW);
        }
        if (rightHandSpearUseProgress > 0.0F) {
            float useProgress = Math.min(1F, rightHandSpearUseProgress);
            float useProgressMiddle = (float) Math.sin(useProgress * Math.PI);
            event.getModel().rightArm.xRot = (float) Math.toRadians(45F) + useProgress * (float) Math.toRadians(115F);
            event.getModel().rightArm.yRot = useProgressMiddle * -(float) Math.toRadians(25F);
            event.getModel().rightArm.zRot = useProgressMiddle * -(float) Math.toRadians(25F);
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
    }

    public static boolean isFirstPersonPlayer(Entity entity) {
        return entity.equals(Minecraft.getInstance().cameraEntity) && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    @SubscribeEvent
    public void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Player player = getClientSidePlayer();
        boolean flag = false;
        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id()) && player.getVehicle() instanceof DinosaurEntity dinosaur && dinosaur.hasRidingMeter()) {
            flag = true;
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            int forgeGuiY = Minecraft.getInstance().gui instanceof ForgeGui forgeGui ? Math.max(forgeGui.leftHeight, forgeGui.rightHeight) : 0;
            if (player.getArmorValue() > 0 && dinosaur instanceof SubterranodonEntity) {
                forgeGuiY += 25;
            }
            if (forgeGuiY < 53) {
                forgeGuiY = 53;
            }
            int j = screenWidth / 2 - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorX.get();
            int k = screenHeight - forgeGuiY - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorY.get();
            float f = dinosaur.getMeterAmount();
            float invProgress = 1 - f;
            int uvOffset = 0;
            if (dinosaur instanceof TremorsaurusEntity) {
                uvOffset = 62;
                k += 5;
            }
            event.getGuiGraphics().pose().pushPose();
            event.getGuiGraphics().blit(DINOSAUR_HUD_OVERLAYS, j, k, 50, 0, uvOffset + 32, 43, 31, 128, 128);
            event.getGuiGraphics().blit(DINOSAUR_HUD_OVERLAYS, j, k, 50, 0, uvOffset, 43, (int) Math.floor(31 * invProgress), 128, 128);
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
            int k = screenHeight - forgeGuiY - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorY.get() + 9 - (flag ? 21 : 0);
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
                    y += random.nextInt(2);
                }
                if (i == regen) {
                    y -= 2;
                }
                event.getGuiGraphics().blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU, heartV + 18, 9, 9, 32, 32);
                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                        event.getGuiGraphics().blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU, heartV, 9, 9, 32, 32);
                        absorbRemaining -= 1.0F;
                    } else {
                        event.getGuiGraphics().blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
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

    @SubscribeEvent
    public void fogRender(ViewportEvent.RenderFog event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        FluidState fluidstate = player.level().getFluidState(event.getCamera().getBlockPosition());
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
        if (event.getCamera().getFluidInCamera() == FogType.WATER) {
            int i = Minecraft.getInstance().options.biomeBlendRadius().get();
            float farness;
            if (i == 0) {
                farness = ACBiomeRegistry.getBiomeWaterFogFarness(player.level().getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeWaterFogFarness(player.level().getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
                });
                farness = (float) vec31.x;
            }
            if (Minecraft.getInstance().player.hasEffect(ACEffectRegistry.DEEPSIGHT.get())) {
                farness *= 1.0F + 1.5F * DeepsightEffect.getIntensity(Minecraft.getInstance().player, (float) event.getPartialTick());
            }
            if (farness != 1.0F) {
                event.setCanceled(true);
                event.setFarPlaneDistance(event.getFarPlaneDistance() * farness);
            }
        } else if (event.getMode() == FogRenderer.FogMode.FOG_TERRAIN) {
            int i = Minecraft.getInstance().options.biomeBlendRadius().get();
            float nearness;
            if (i == 0) {
                nearness = ACBiomeRegistry.getBiomeFogNearness(player.level().getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeFogNearness(player.level().getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
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
                override = ACBiomeRegistry.getBiomeSkyOverride(player.level().getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeSkyOverride(player.level().getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
                });
                override = (float) vec31.x;
            }
            if (override != 0.0F) {
                Vec3 vec3;
                if (i == 0) {
                    vec3 = ((ClientLevel) player.level()).effects().getBrightnessDependentFogColor(Vec3.fromRGB24(player.level().getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getFogColor()), override);
                } else {
                    vec3 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                        return ((ClientLevel) player.level()).effects().getBrightnessDependentFogColor(Vec3.fromRGB24(player.level().getBiomeManager().getNoiseBiomeAtPosition(x, y, z).value().getFogColor()), override);
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
                override = ACBiomeRegistry.getBiomeSkyOverride(player.level().getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeSkyOverride(player.level().getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
                });
                override = (float) vec31.x;
            }
            if (override != 0) {
                if (i == 0) {
                    vec3 = Vec3.fromRGB24(player.level().getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getWaterFogColor());
                } else {
                    vec3 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                        return Vec3.fromRGB24(player.level().getBiomeManager().getNoiseBiomeAtPosition(x, y, z).value().getWaterFogColor());
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
        if (AlexsCaves.CLIENT_CONFIG.emissiveBlockModels.get()) {
            long time = System.currentTimeMillis();
            for (ResourceLocation id : e.getModels().keySet()) {
                if (FULLBRIGHTS.stream().anyMatch(str -> id.toString().startsWith(str))) {
                    e.getModels().put(id, new BakedModelShadeLayerFullbright(e.getModels().get(id)));
                }
            }
            AlexsCaves.LOGGER.info("Loaded emissive block models in {} ms", System.currentTimeMillis() - time);

        }
    }

    private void registerShaders(final RegisterShadersEvent e) {
        try {
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(AlexsCaves.MODID, "rendertype_ferrouslime_gel"), DefaultVertexFormat.NEW_ENTITY), ACInternalShaders::setRenderTypeFerrouslimeGelShader);
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(AlexsCaves.MODID, "rendertype_hologram"), DefaultVertexFormat.POSITION_COLOR), ACInternalShaders::setRenderTypeHologramShader);
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(AlexsCaves.MODID, "rendertype_irradiated"), DefaultVertexFormat.POSITION_COLOR_TEX), ACInternalShaders::setRenderTypeIrradiatedShader);
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(AlexsCaves.MODID, "rendertype_bubbled"), DefaultVertexFormat.NEW_ENTITY), ACInternalShaders::setRenderTypeBubbledShader);
            AlexsCaves.LOGGER.info("registered internal shaders");
        } catch (IOException exception) {
            AlexsCaves.LOGGER.error("could not register internal shaders");
            exception.printStackTrace();
        }
    }

    private void registerKeybinds(RegisterKeyMappingsEvent e) {
        e.register(ACKeybindRegistry.KEY_SPECIAL_ABILITY);
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

    public float getPossessionStrengthAmount(float partialTicks) {
        return prevPossessionStrengthAmount + (possessionStrengthAmount - prevPossessionStrengthAmount) * partialTicks;
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
            float partialTicks = getPartialTicks();
            prevNukeFlashAmount = nukeFlashAmount;
            if (renderNukeFlashFor > 0) {
                if (nukeFlashAmount < 1F) {
                    nukeFlashAmount = Math.min(nukeFlashAmount + 0.4F, 1F);
                }
                renderNukeFlashFor--;
            } else if (nukeFlashAmount > 0F) {
                nukeFlashAmount = Math.max(nukeFlashAmount - 0.05F, 0F);
            }
            prevPossessionStrengthAmount = possessionStrengthAmount;
            if (Minecraft.getInstance().getCameraEntity() instanceof PossessesCamera watcherEntity) {
                if (watcherEntity.instant()) {
                    possessionStrengthAmount = watcherEntity.getPossessionStrength(partialTicks);
                } else {
                    if (possessionStrengthAmount < watcherEntity.getPossessionStrength(partialTicks)) {
                        possessionStrengthAmount = Math.min(possessionStrengthAmount + 0.2F, watcherEntity.getPossessionStrength(partialTicks));
                    } else {
                        possessionStrengthAmount = Math.max(possessionStrengthAmount - 0.2F, watcherEntity.getPossessionStrength(partialTicks));
                    }
                }
                if (watcherEntity instanceof BeholderEyeEntity beholderEye) {
                    beholderEye.setOldRots();
                    beholderEye.setEyeYRot(Minecraft.getInstance().player.getYHeadRot());
                    beholderEye.setEyeXRot(Minecraft.getInstance().player.getXRot());
                    if (isKeyDown(4)) {
                        resetRenderViewEntity(Minecraft.getInstance().player);
                    }
                }
            } else if (possessionStrengthAmount > 0F) {
                possessionStrengthAmount = Math.max(possessionStrengthAmount - 0.05F, 0F);
            }
            if (Minecraft.getInstance().screen instanceof AdvancementsScreen advancementsScreen && advancementsScreen.selectedTab != null && ACAdvancementTabs.isAlexsCavesWidget(advancementsScreen.selectedTab.getAdvancement())) {
                ACAdvancementTabs.tick();
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
        if (player.isPassenger() && player.getVehicle() instanceof SubmarineEntity && fogtype == FogType.WATER) {
            float f = (float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0D, (double) 0.85714287F);
            event.setFOV(event.getFOV() / f);
        }
    }


    @SubscribeEvent
    public void onSplashTextRender(EventRenderSplashText.Pre event) {
        if (hasACSplashText) {
            event.setResult(Event.Result.ALLOW);
            event.setSplashText("30k downloads max");
            event.setSplashTextColor(0X00B6D5);
        }
    }

    public boolean isKeyDown(int keyType) {
        if (keyType == -1) {
            return Minecraft.getInstance().options.keyLeft.isDown() || Minecraft.getInstance().options.keyRight.isDown() || Minecraft.getInstance().options.keyUp.isDown() || Minecraft.getInstance().options.keyDown.isDown() || Minecraft.getInstance().options.keyJump.isDown();
        }
        if (keyType == 0) {
            return Minecraft.getInstance().options.keyJump.isDown();
        }
        if (keyType == 1) {
            return Minecraft.getInstance().options.keySprint.isDown();
        }
        if (keyType == 2) {
            return ACKeybindRegistry.KEY_SPECIAL_ABILITY.isDown();
        }
        if (keyType == 3) {
            return Minecraft.getInstance().options.keyAttack.isDown();
        }
        if (keyType == 4) {
            return Minecraft.getInstance().options.keyShift.isDown();
        }
        return false;
    }

    @Override
    public Object getISTERProperties() {
        return isterProperties;
    }

    @Override
    public Object getArmorProperties() {
        return armorProperties;
    }

    public float getPartialTicks() {
        return Minecraft.getInstance().getPartialTick();
    }

    public void setSpelunkeryTutorialComplete(boolean completedTutorial) {
        this.spelunkeryTutorialComplete = completedTutorial;
    }

    public boolean isSpelunkeryTutorialComplete() {
        return this.spelunkeryTutorialComplete;
    }

    public void setRenderViewEntity(Player player, Entity entity) {
        if (player == Minecraft.getInstance().player && Minecraft.getInstance().getCameraEntity() == Minecraft.getInstance().player) {
            lastPOV = Minecraft.getInstance().options.getCameraType();
            Minecraft.getInstance().setCameraEntity(entity);
            Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
        }
        Minecraft.getInstance().levelRenderer.allChanged();
    }

    public void resetRenderViewEntity(Player player) {
        if (player == Minecraft.getInstance().player) {
            Minecraft.getInstance().level = (ClientLevel) Minecraft.getInstance().player.level();
            Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
            Minecraft.getInstance().options.setCameraType(lastPOV);
        }
        Minecraft.getInstance().levelRenderer.allChanged();
    }

    public void playWorldSound(@Nullable Object soundEmitter, byte type) {
        if (type == 0 && soundEmitter instanceof NuclearSirenBlockEntity nuclearSiren) {
            if (closestSirenSound == null || closestSirenSound.isStopped()) {
                closestSirenSound = new NuclearSirenSound(nuclearSiren);
            }
            if (closestSirenSound != null && !Minecraft.getInstance().getSoundManager().isActive(closestSirenSound)) {
                Minecraft.getInstance().getSoundManager().play(closestSirenSound);
            }
        }
    }

    public Vec3 getDarknessTrailPosFor(LivingEntity living, int pointer, float partialTick) {
        if (living.isRemoved()) {
            partialTick = 1.0F;
        }
        Vec3[] trailPositions = darknessTrailPosMap.get(living);
        if(trailPositions == null || !darknessTrailPointerMap.containsKey(living)){
            return living.position();
        }
        int trailPointer = darknessTrailPointerMap.get(living);
        int i = trailPointer - pointer & 63;
        int j = trailPointer - pointer - 1 & 63;
        Vec3 d0 = trailPositions[j];
        Vec3 d1 = trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }


    public int getPlayerTime() {
        return Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.tickCount;
    }

    public void preScreenRender(float partialTick) {
        float screenEffectIntensity = Minecraft.getInstance().options.screenEffectScale().get().floatValue();
        float watcherPossessionStrength = getPossessionStrengthAmount(partialTick);
        float nukeFlashAmount = getNukeFlashAmount(partialTick);
        if (nukeFlashAmount > 0 && (AlexsCaves.CLIENT_CONFIG.nuclearBombFlash.get())) {
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, nukeFlashAmount * screenEffectIntensity);
            RenderSystem.setShaderTexture(0, ClientProxy.BOMB_FLASH);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (watcherPossessionStrength > 0) {
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, watcherPossessionStrength * screenEffectIntensity);
            RenderSystem.setShaderTexture(0, ClientProxy.WATCHER_EFFECT);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public Vec3 getCameraRotation() {
        return Vec3.ZERO;
    }
}

