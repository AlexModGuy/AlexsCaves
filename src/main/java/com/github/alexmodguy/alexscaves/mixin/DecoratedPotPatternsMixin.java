package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.misc.ACPotPatternRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotPatterns.class)
public class DecoratedPotPatternsMixin {

    @Inject(
            method = "getPatternFromItem(Lnet/minecraft/world/item/Item;)Lnet/minecraft/resources/ResourceKey;",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = true
    )
    private static void ac_getPatternFromItem(Item item, CallbackInfoReturnable<ResourceKey<DecoratedPotPattern>> cir) {
        ResourceKey<DecoratedPotPattern> pattern = ACPotPatternRegistry.getPatternForSherd(item);
        if (pattern != null) {
            cir.setReturnValue(pattern);
        }
    }
}
