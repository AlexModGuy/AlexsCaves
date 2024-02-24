package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.enchantment.ACWeaponEnchantment;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {

    @Inject(
            method = {"Lnet/minecraft/world/level/storage/loot/functions/EnchantRandomlyFunction;enchantItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/item/ItemStack;"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void ac_enchantItem(ItemStack stack, Enchantment enchantment, RandomSource randomSource, CallbackInfoReturnable<ItemStack> cir) {
        if(enchantment instanceof ACWeaponEnchantment && !AlexsCaves.COMMON_CONFIG.enchantmentsInLoot.get()){
            Enchantment enchantment1 = enchantment;
            boolean flag = stack.is(Items.BOOK);
            List<Enchantment> list = BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isDiscoverable).filter((enchantment2) -> {
                return flag || enchantment2.canEnchant(stack);
            }).collect(Collectors.toList());
            int tries = 0;
            while(enchantment1 instanceof ACWeaponEnchantment && tries < 100){
                enchantment1 = Util.getRandom(list, randomSource);
                tries++;
            }
            cir.setReturnValue(enchantItemNormally(stack, enchantment1, randomSource));
        }
    }

    private static ItemStack enchantItemNormally(ItemStack itemStack, Enchantment enchantment, RandomSource randomSource) {
        int i = Mth.nextInt(randomSource, enchantment.getMinLevel(), enchantment.getMaxLevel());
        if (itemStack.is(Items.BOOK)) {
            itemStack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(enchantment, i));
        } else {
            itemStack.enchant(enchantment, i);
        }

        return itemStack;
    }
}
