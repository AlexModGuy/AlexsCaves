package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.item.KeybindUsingArmor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ArmorKeyMessage {

    public int equipmentSlot;
    public int playerId;
    public int type;

    public ArmorKeyMessage(int equipmentSlot, int playerId, int type) {
        this.equipmentSlot = equipmentSlot;
        this.playerId = playerId;
        this.type = type;
    }


    public ArmorKeyMessage() {
    }

    public static ArmorKeyMessage read(FriendlyByteBuf buf) {
        return new ArmorKeyMessage(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(ArmorKeyMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.equipmentSlot);
        buf.writeInt(message.playerId);
        buf.writeInt(message.type);
    }

    public static void handle(ArmorKeyMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Player playerSided = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                playerSided = AlexsCaves.PROXY.getClientSidePlayer();
            }
            if(playerSided != null){
                Entity keyPresser = playerSided.level().getEntity(message.playerId);
                EquipmentSlot equipmentSlot1 = EquipmentSlot.values()[Mth.clamp(message.equipmentSlot, 0, EquipmentSlot.values().length - 1)];
                if (keyPresser != null && keyPresser instanceof Player player) {
                    ItemStack stack = player.getItemBySlot(equipmentSlot1);
                    if(stack.getItem() instanceof KeybindUsingArmor armor){
                        armor.onKeyPacket(keyPresser, stack, message.type);
                    }
                }

            }
        });
        context.get().setPacketHandled(true);
    }
}