package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateCaveBiomeMapTagMessage {

    private UUID userUUID;
    private UUID caveBiomeMapUUID;
    private CompoundTag tag;

    public UpdateCaveBiomeMapTagMessage(UUID userUUID, UUID caveBiomeMapUUID, CompoundTag tag) {
        this.userUUID = userUUID;
        this.caveBiomeMapUUID = caveBiomeMapUUID;
        this.tag = tag;
    }


    public static UpdateCaveBiomeMapTagMessage read(FriendlyByteBuf buf) {
        return new UpdateCaveBiomeMapTagMessage(buf.readUUID(), buf.readUUID(), PacketBufferUtils.readTag(buf));
    }

    public static void write(UpdateCaveBiomeMapTagMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.userUUID);
        buf.writeUUID(message.caveBiomeMapUUID);
        PacketBufferUtils.writeTag(buf, message.tag);
    }

    public static void handle(UpdateCaveBiomeMapTagMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().setPacketHandled(true);
        Player playerSided = context.get().getSender();
        if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            playerSided = AlexsCaves.PROXY.getClientSidePlayer();
        }
        if(playerSided != null){
            Player player = playerSided.level().getPlayerByUUID(message.userUUID);
            if (player != null) {
                ItemStack set = null;
                for(int i = 0; i < player.getInventory().items.size(); i++){
                    ItemStack itemStack = player.getInventory().items.get(i);
                    if(itemStack.is(ACItemRegistry.CAVE_MAP.get()) && itemStack.getTag() != null){
                        CompoundTag tag = itemStack.getOrCreateTag();
                        if(tag.contains("MapUUID") && message.caveBiomeMapUUID.equals(tag.getUUID("MapUUID"))){
                            set = itemStack;
                            break;
                        }
                    }
                }
                if(set != null){
                    set.setTag(message.tag);
                }
            }
        }
    }

}
