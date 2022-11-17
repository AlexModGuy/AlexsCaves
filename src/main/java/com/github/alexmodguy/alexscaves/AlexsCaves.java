package com.github.alexmodguy.alexscaves;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.MultipartEntityMessage;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.ACFeatureRegistry;
import com.github.alexmodguy.alexscaves.server.level.surface.ACSurfaceRules;
import com.github.alexmodguy.alexscaves.server.message.PlayerControllerJumpMessage;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

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

    public AlexsCaves() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PROXY);
        ACBlockRegistry.DEF_REG.register(modEventBus);
        ACBlockEntityRegistry.DEF_REG.register(modEventBus);
        ACItemRegistry.DEF_REG.register(modEventBus);
        ACParticleRegistry.DEF_REG.register(modEventBus);
        ACEntityRegistry.DEF_REG.register(modEventBus);
        ACPOIRegistry.DEF_REG.register(modEventBus);
        ACFeatureRegistry.DEF_REG.register(modEventBus);
        PROXY.init();
        ACBiomeRegistry.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        int packetsRegistered = 0;
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, PlayerControllerJumpMessage.class, PlayerControllerJumpMessage::write, PlayerControllerJumpMessage::read, PlayerControllerJumpMessage::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MultipartEntityMessage.class, MultipartEntityMessage::write, MultipartEntityMessage::read, MultipartEntityMessage::handle);
        ACSurfaceRules.init();
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

    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
        NETWORK_WRAPPER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
