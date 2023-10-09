package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WorldEventMessage  {

    public int messageId;
    public int blockX;
    public int blockY;
    public int blockZ;

    public WorldEventMessage(int messageId, int blockX, int blockY, int blockZ) {
        this.messageId = messageId;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
    }

    public WorldEventMessage(){}

    public static WorldEventMessage read(FriendlyByteBuf buf) {
        return new WorldEventMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(WorldEventMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.messageId);
        buf.writeInt(message.blockX);
        buf.writeInt(message.blockY);
        buf.writeInt(message.blockZ);
    }

    public static void handle(WorldEventMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Player playerSided = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                playerSided = AlexsCaves.PROXY.getClientSidePlayer();
            }
            if(playerSided.level() != null){
                BlockPos blockPos = new BlockPos(message.blockX, message.blockY, message.blockZ);
                AlexsCaves.PROXY.playWorldEvent(message.messageId, playerSided.level(), blockPos);
            }
        });
        context.get().setPacketHandled(true);
    }
}