package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SundropRainbowMessage {

    public int fromX;
    public int fromY;
    public int fromZ;
    public int toX;
    public int toY;
    public int toZ;

    public SundropRainbowMessage(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.fromZ = fromZ;
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
    }

    public SundropRainbowMessage(){}

    public static SundropRainbowMessage read(FriendlyByteBuf buf) {
        return new SundropRainbowMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(SundropRainbowMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.fromX);
        buf.writeInt(message.fromY);
        buf.writeInt(message.fromZ);
        buf.writeInt(message.toX);
        buf.writeInt(message.toY);
        buf.writeInt(message.toZ);
    }

    public static void handle(SundropRainbowMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Player playerSided = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                playerSided = AlexsCaves.PROXY.getClientSidePlayer();
            }
            if(playerSided.level() != null){
                BlockPos blockPos1 = new BlockPos(message.fromX, message.fromY, message.fromZ);
                BlockPos blockPos2 = new BlockPos(message.toX, message.toY, message.toZ);
                if(playerSided.level().hasChunkAt(blockPos1) && playerSided.level().getBlockState(blockPos1).is(ACBlockRegistry.SUNDROP.get())){
                    playerSided.level().addAlwaysVisibleParticle(ACParticleRegistry.RAINBOW.get(), true, blockPos1.getX() + 0.5F, blockPos1.getY() + 0.5F, blockPos1.getZ() + 0.5F, blockPos2.getX() + 0.5F, blockPos2.getY() + 0.5F, blockPos2.getZ() + 0.5F);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}