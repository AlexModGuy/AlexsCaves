package com.github.alexmodguy.alexscaves;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.config.ACClientConfig;
import com.github.alexmodguy.alexscaves.client.model.layered.ACModelLayers;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.config.ACServerConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.event.CommonEvents;
import com.github.alexmodguy.alexscaves.server.inventory.ACMenuRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.carver.ACCarverRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.ACFeatureRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.ACStructureRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.ACStructurePieceRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.ACStructureProcessorRegistry;
import com.github.alexmodguy.alexscaves.server.level.surface.ACSurfaceRuleConditionRegistry;
import com.github.alexmodguy.alexscaves.server.level.surface.ACSurfaceRules;
import com.github.alexmodguy.alexscaves.server.message.*;
import com.github.alexmodguy.alexscaves.server.misc.*;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.recipe.ACRecipeRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod(AlexsCaves.MODID)
public class AlexsCaves {
    public static final String MODID = "alexscaves";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final ResourceLocation PACKET_NETWORK_NAME = new ResourceLocation("alexscaves:main_channel");
    public static final SimpleChannel NETWORK_WRAPPER = NetworkRegistry.ChannelBuilder
            .named(PACKET_NETWORK_NAME)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
    public static final ACServerConfig COMMON_CONFIG;
    private static final ForgeConfigSpec COMMON_CONFIG_SPEC;
    public static final ACClientConfig CLIENT_CONFIG;
    private static final ForgeConfigSpec CLIENT_CONFIG_SPEC;
    public static final List<String> MOD_GENERATION_CONFLICTS = new ArrayList<>();

    static {
        final Pair<ACServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(ACServerConfig::new);
        COMMON_CONFIG = serverPair.getLeft();
        COMMON_CONFIG_SPEC = serverPair.getRight();
        final Pair<ACClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(ACClientConfig::new);
        CLIENT_CONFIG = clientPair.getLeft();
        CLIENT_CONFIG_SPEC = clientPair.getRight();
    }

    public AlexsCaves() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG_SPEC, "alexscaves-general.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG_SPEC, "alexscaves-client.toml");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::loadConfig);
        modEventBus.addListener(this::reloadConfig);
        modEventBus.addListener(this::registerLayerDefinitions);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        ACBlockRegistry.DEF_REG.register(modEventBus);
        ACBlockEntityRegistry.DEF_REG.register(modEventBus);
        ACItemRegistry.DEF_REG.register(modEventBus);
        ACParticleRegistry.DEF_REG.register(modEventBus);
        ACEntityRegistry.DEF_REG.register(modEventBus);
        ACEntityDataRegistry.DEF_REG.register(modEventBus);
        ACPOIRegistry.DEF_REG.register(modEventBus);
        ACFeatureRegistry.DEF_REG.register(modEventBus);
        ACSurfaceRuleConditionRegistry.DEF_REG.register(modEventBus);
        ACCarverRegistry.DEF_REG.register(modEventBus);
        ACSoundRegistry.DEF_REG.register(modEventBus);
        ACStructureRegistry.DEF_REG.register(modEventBus);
        ACStructurePieceRegistry.DEF_REG.register(modEventBus);
        ACStructureProcessorRegistry.DEF_REG.register(modEventBus);
        ACEnchantmentRegistry.DEF_REG.register(modEventBus);
        ACEffectRegistry.DEF_REG.register(modEventBus);
        ACEffectRegistry.POTION_DEF_REG.register(modEventBus);
        ACMenuRegistry.DEF_REG.register(modEventBus);
        ACRecipeRegistry.DEF_REG.register(modEventBus);
        ACRecipeRegistry.TYPE_DEF_REG.register(modEventBus);
        ACFrogRegistry.DEF_REG.register(modEventBus);
        ACFluidRegistry.FLUID_TYPE_DEF_REG.register(modEventBus);
        ACFluidRegistry.FLUID_DEF_REG.register(modEventBus);
        ACLootTableRegistry.DEF_REG.register(modEventBus);
        ACCreativeTabRegistry.DEF_REG.register(modEventBus);
        ACPotPatternRegistry.DEF_REG.register(modEventBus);
        PROXY.commonInit();
        ACBiomeRegistry.init();
    }

    private void loadConfig(final ModConfigEvent.Loading event) {
        BiomeGenerationConfig.reloadConfig();
    }

    private void reloadConfig(final ModConfigEvent.Reloading event) {
        BiomeGenerationConfig.reloadConfig();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PROXY.initPathfinding();
        int packetsRegistered = 0;
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, SpelunkeryTableChangeMessage.class, SpelunkeryTableChangeMessage::write, SpelunkeryTableChangeMessage::read, SpelunkeryTableChangeMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, SpelunkeryTableCompleteTutorialMessage.class, SpelunkeryTableCompleteTutorialMessage::write, SpelunkeryTableCompleteTutorialMessage::read, SpelunkeryTableCompleteTutorialMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, PlayerJumpFromMagnetMessage.class, PlayerJumpFromMagnetMessage::write, PlayerJumpFromMagnetMessage::read, PlayerJumpFromMagnetMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MultipartEntityMessage.class, MultipartEntityMessage::write, MultipartEntityMessage::read, MultipartEntityMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MountedEntityKeyMessage.class, MountedEntityKeyMessage::write, MountedEntityKeyMessage::read, MountedEntityKeyMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, UpdateEffectVisualityEntityMessage.class, UpdateEffectVisualityEntityMessage::write, UpdateEffectVisualityEntityMessage::read, UpdateEffectVisualityEntityMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, PossessionKeyMessage.class, PossessionKeyMessage::write, PossessionKeyMessage::read, PossessionKeyMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, UpdateItemTagMessage.class, UpdateItemTagMessage::write, UpdateItemTagMessage::read, UpdateItemTagMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, BeholderSyncMessage.class, BeholderSyncMessage::write, BeholderSyncMessage::read, BeholderSyncMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, BeholderRotateMessage.class, BeholderRotateMessage::write, BeholderRotateMessage::read, BeholderRotateMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, ArmorKeyMessage.class, ArmorKeyMessage::write, ArmorKeyMessage::read, ArmorKeyMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, WorldEventMessage.class, WorldEventMessage::write, WorldEventMessage::read, WorldEventMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, UpdateCaveBiomeMapTagMessage.class, UpdateCaveBiomeMapTagMessage::write, UpdateCaveBiomeMapTagMessage::read, UpdateCaveBiomeMapTagMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, UpdateBossEruptionStatus.class, UpdateBossEruptionStatus::write, UpdateBossEruptionStatus::read, UpdateBossEruptionStatus::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, UpdateBossBarMessage.class, UpdateBossBarMessage::write, UpdateBossBarMessage::read, UpdateBossBarMessage::handle);
        event.enqueueWork(() -> {
            ACSurfaceRules.setup();
            ACPlayerCapes.setup();
            ACEffectRegistry.setup();
            ACBlockRegistry.setup();
            ACItemRegistry.setup();
            ACAdvancementTriggerRegistry.setup();
            ACPotPatternRegistry.expandVanillaDefinitions();
            ACBlockEntityRegistry.expandVanillaDefinitions();
        });
        readModIncompatibilities();
    }
    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.clientInit());
    }

    public static <MSG> void sendMSGToServer(MSG message) {
        NETWORK_WRAPPER.sendToServer(message);
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(ACFluidRegistry::postInit);
    }

    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
        NETWORK_WRAPPER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    private void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        ACModelLayers.register(event);
    }

    private void readModIncompatibilities() {
        BufferedReader urlContents = WebHelper.getURLContents("https://raw.githubusercontent.com/AlexModGuy/AlexsCaves/main/src/main/resources/assets/alexscaves/warning/mod_generation_conflicts.txt", "assets/alexscaves/warning/mod_generation_conflicts.txt");
        if (urlContents != null) {
            try {
                String line;
                while ((line = urlContents.readLine()) != null) {
                    MOD_GENERATION_CONFLICTS.add(line);
                }
            } catch (IOException e) {
                LOGGER.warn("Failed to load mod conflicts");
            }
        } else{
            LOGGER.warn("Failed to load mod conflicts");
        }
    }
}