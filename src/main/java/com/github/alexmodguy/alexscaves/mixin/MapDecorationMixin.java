package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACVanillaMapUtil;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MapDecoration.class)
public abstract class MapDecorationMixin {

    @Shadow public abstract MapDecoration.Type getType();

    @Inject(
            method = {"Lnet/minecraft/world/level/saveddata/maps/MapDecoration;render(I)Z"},
            remap = false, //FORGE METHOD
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_render(int index, CallbackInfoReturnable<Boolean> cir) {
        if(this.getType() == ACVanillaMapUtil.UNDERGROUND_CABIN_MAP_DECORATION){
            AlexsCaves.PROXY.renderVanillaMapDecoration((MapDecoration)(Object)this, index);
            cir.setReturnValue(true);
        }
    }
}
