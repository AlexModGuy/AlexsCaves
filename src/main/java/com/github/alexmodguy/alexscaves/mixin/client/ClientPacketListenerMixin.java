package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACKeybindRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow
    private ClientLevel level;

    @Inject(
            method = {"Lnet/minecraft/client/multiplayer/ClientPacketListener;handleSetEntityPassengersPacket(Lnet/minecraft/network/protocol/game/ClientboundSetPassengersPacket;)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/GameNarrator;sayNow(Lnet/minecraft/network/chat/Component;)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void iws_handleSetEntityPassengersPacket(ClientboundSetPassengersPacket packet, CallbackInfo ci) {
        Entity entity = this.level.getEntity(packet.getVehicle());
        if (entity instanceof SubmarineEntity) {
            Component componentBoard = Component.translatable("entity.alexscaves.submarine.mount_message", Minecraft.getInstance().options.keyJump.getTranslatedKeyMessage(), Minecraft.getInstance().options.keySprint.getTranslatedKeyMessage(), ACKeybindRegistry.KEY_SPECIAL_ABILITY.getTranslatedKeyMessage(), Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage());
            Minecraft.getInstance().gui.setOverlayMessage(componentBoard, false);
            Minecraft.getInstance().getNarrator().sayNow(componentBoard);
        }
        if (entity instanceof SubterranodonEntity) {
            Component componentBoard = Component.translatable("entity.alexscaves.subterranodon.mount_message", Minecraft.getInstance().options.keyJump.getTranslatedKeyMessage(), Minecraft.getInstance().options.keySprint.getTranslatedKeyMessage(), Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage());
            Minecraft.getInstance().gui.setOverlayMessage(componentBoard, false);
            Minecraft.getInstance().getNarrator().sayNow(componentBoard);
        }
        if (entity instanceof TremorsaurusEntity) {
            Component componentBoard = Component.translatable("entity.alexscaves.tremorsaurus.mount_message", ACKeybindRegistry.KEY_SPECIAL_ABILITY.getTranslatedKeyMessage(), Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage());
            Minecraft.getInstance().gui.setOverlayMessage(componentBoard, false);
            Minecraft.getInstance().getNarrator().sayNow(componentBoard);
        }
    }
}
