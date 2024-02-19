package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateBossBarMessage {

    private UUID bossBar;
    private int renderType;

    public UpdateBossBarMessage(UUID bossBar, int renderType) {
        this.bossBar = bossBar;
        this.renderType = renderType;
    }


    public static UpdateBossBarMessage read(FriendlyByteBuf buf) {
        return new UpdateBossBarMessage(buf.readUUID(), buf.readInt());
    }

    public static void write(UpdateBossBarMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.bossBar);
        buf.writeInt(message.renderType);
    }

    public static void handle(UpdateBossBarMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().setPacketHandled(true);
        Player playerSided = context.get().getSender();
        if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            playerSided = AlexsCaves.PROXY.getClientSidePlayer();
        }
        if(message.renderType == -1){
            AlexsCaves.PROXY.removeBossBarRender(message.bossBar);
        }else{
            AlexsCaves.PROXY.setBossBarRender(message.bossBar, message.renderType);
        }
    }

}
