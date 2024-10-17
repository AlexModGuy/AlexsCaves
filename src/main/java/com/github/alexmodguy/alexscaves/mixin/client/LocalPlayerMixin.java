package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    private boolean wasUnderAcid;
    private boolean wasUnderPurpleSoda;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "Lnet/minecraft/client/player/LocalPlayer;updateIsUnderwater()Z",
            at = @At("TAIL"))
    private void ac_updateIsUnderwater(CallbackInfoReturnable<Boolean> cir) {
        boolean underAcid = this.getEyeInFluidType().equals(ACFluidRegistry.ACID_FLUID_TYPE.get());
        boolean underPurpleSoda = this.getEyeInFluidType().equals(ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get());
        if(wasUnderAcid != underAcid){
            if(underAcid){
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ACSoundRegistry.ACID_SUBMERGE.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }else{
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ACSoundRegistry.ACID_UNSUBMERGE.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }
        }
        if(wasUnderPurpleSoda != underPurpleSoda){
            if(underPurpleSoda){
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ACSoundRegistry.PURPLE_SODA_SUBMERGE.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }else{
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ACSoundRegistry.PURPLE_SODA_UNSUBMERGE.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }
        }
        wasUnderAcid = underAcid;
        wasUnderPurpleSoda = underPurpleSoda;
    }
}
