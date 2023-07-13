package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateEffectVisualityEntity {

    private int entityID;
    private int fromEntityID;
    private int potionType;
    private int duration;

    private boolean remove;

    public UpdateEffectVisualityEntity(int entityID, int fromEntityID, int potionType, int duration) {
        this(entityID, fromEntityID, potionType, duration, false);
    }

    public UpdateEffectVisualityEntity(int entityID, int fromEntityID, int potionType, int duration, boolean remove) {
        this.entityID = entityID;
        this.fromEntityID = fromEntityID;
        this.potionType = potionType;
        this.duration = duration;
        this.remove = remove;
    }


    public static UpdateEffectVisualityEntity read(FriendlyByteBuf buf) {
        return new UpdateEffectVisualityEntity(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public static void write(UpdateEffectVisualityEntity message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityID);
        buf.writeInt(message.fromEntityID);
        buf.writeInt(message.potionType);
        buf.writeInt(message.duration);
        buf.writeBoolean(message.remove);
    }

    public static void handle(UpdateEffectVisualityEntity message, Supplier<NetworkEvent.Context> context) {
        context.get().setPacketHandled(true);
        Player playerSided = context.get().getSender();
        if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
            playerSided = AlexsCaves.PROXY.getClientSidePlayer();
        }
        if(playerSided != null){
            Entity entity = playerSided.level().getEntity(message.entityID);
            Entity senderEntity = playerSided.level().getEntity(message.fromEntityID);
            if(entity instanceof LivingEntity living && senderEntity != null && senderEntity.distanceTo(living) < 32){
                MobEffect mobEffect = null;
                switch (message.potionType){
                    case 0:
                        mobEffect = ACEffectRegistry.IRRADIATED.get();
                        break;
                    case 1:
                        mobEffect = ACEffectRegistry.BUBBLED.get();
                        break;
                    case 2:
                        mobEffect = ACEffectRegistry.MAGNETIZING.get();
                        break;
                    case 3:
                        mobEffect = ACEffectRegistry.STUNNED.get();
                        break;
                }
                if(mobEffect != null){
                    if(message.remove){
                        living.removeEffectNoUpdate(mobEffect);
                    }else{
                        living.addEffect(new MobEffectInstance(mobEffect, message.duration));
                    }
                }
            }
        }
    }

}
