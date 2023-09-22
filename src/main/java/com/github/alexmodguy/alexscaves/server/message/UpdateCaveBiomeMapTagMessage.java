package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.UpdatesStackTags;
import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateCaveBiomeMapTagMessage {

    private int entityId;
    private UUID caveBiomeMapUUID;
    private CompoundTag tag;

    public UpdateCaveBiomeMapTagMessage(int entityId, UUID caveBiomeMapUUID, CompoundTag tag) {
        this.entityId = entityId;
        this.caveBiomeMapUUID = caveBiomeMapUUID;
        this.tag = tag;
    }


    public static UpdateCaveBiomeMapTagMessage read(FriendlyByteBuf buf) {
        return new UpdateCaveBiomeMapTagMessage(buf.readInt(), buf.readUUID(), PacketBufferUtils.readTag(buf));
    }

    public static void write(UpdateCaveBiomeMapTagMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
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
            Entity holder = playerSided.level().getEntity(message.entityId);
            if (holder instanceof Player player) {
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
