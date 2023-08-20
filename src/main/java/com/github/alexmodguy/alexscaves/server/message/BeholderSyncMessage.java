package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class BeholderSyncMessage {

    public int beholderId;
    public boolean active;

    public BeholderSyncMessage(int beholderId, boolean active) {
        this.beholderId = beholderId;
        this.active = active;
    }


    public BeholderSyncMessage() {
    }

    public static BeholderSyncMessage read(FriendlyByteBuf buf) {
        return new BeholderSyncMessage(buf.readInt(), buf.readBoolean());
    }

    public static void write(BeholderSyncMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.beholderId);
        buf.writeBoolean(message.active);
    }

    public static void handle(BeholderSyncMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Player playerSided = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                playerSided = AlexsCaves.PROXY.getClientSidePlayer();
            }
            Level serverLevel = ServerLifecycleHooks.getCurrentServer().getLevel(playerSided.level().dimension());
            Entity watcher = serverLevel.getEntity(message.beholderId);
            if (watcher instanceof BeholderEyeEntity beholderEye) {
                Entity beholderEyePlayer = beholderEye.getUsingPlayer();
                beholderEye.hasTakenFullControlOfCamera = true;
                if(beholderEyePlayer != null && beholderEyePlayer instanceof Player && beholderEyePlayer.equals(playerSided)){
                    if(message.active){
                        AlexsCaves.PROXY.setRenderViewEntity(playerSided, beholderEye);
                    }else{
                        AlexsCaves.PROXY.resetRenderViewEntity(playerSided);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}