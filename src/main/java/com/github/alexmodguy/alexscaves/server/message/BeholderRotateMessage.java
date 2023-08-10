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

public class BeholderRotateMessage {

    public int beholderId;
    public float rotX;
    public float rotY;

    public BeholderRotateMessage(int beholderId, float rotX, float rotY) {
        this.beholderId = beholderId;
        this.rotX = rotX;
        this.rotY = rotY;
    }


    public BeholderRotateMessage() {
    }

    public static BeholderRotateMessage read(FriendlyByteBuf buf) {
        return new BeholderRotateMessage(buf.readInt(), buf.readFloat(), buf.readFloat());
    }

    public static void write(BeholderRotateMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.beholderId);
        buf.writeFloat(message.rotX);
        buf.writeFloat(message.rotY);
    }

    public static void handle(BeholderRotateMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Player playerSided = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                playerSided = AlexsCaves.PROXY.getClientSidePlayer();
            }
            Level serverLevel = ServerLifecycleHooks.getCurrentServer().getLevel(playerSided.level().dimension());
            Entity watcher = serverLevel.getEntity(message.beholderId);
            if (watcher instanceof BeholderEyeEntity beholderEye) {
            }
        });
        context.get().setPacketHandled(true);
    }
}