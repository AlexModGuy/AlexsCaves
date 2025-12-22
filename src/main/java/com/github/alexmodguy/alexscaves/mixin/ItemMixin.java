package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.HazmatArmorItem;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {

  @Inject(method = "inventoryTick", at = @At("HEAD"))
  public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held, CallbackInfo info) {

    if (!stack.is(ACTagRegistry.ALL_RADIOACTIVE_ITEMS)) {
      return;
    }

    if (!level.isClientSide && entity instanceof LivingEntity living && !(living instanceof Player player && player.isCreative())) {

      float randomChanceOfRadiation = 0.0F;

      if (stack.is(ACTagRegistry.WEAK_RADIOACTIVE_ITEMS)) {
        randomChanceOfRadiation = 0.0005F;
      }

      if (stack.is(ACTagRegistry.RADIOACTIVE_ITEMS)) {
        randomChanceOfRadiation = 0.001F;
      }

      if (stack.is(ACTagRegistry.STRONG_RADIOACTIVE_ITEMS)) {
        randomChanceOfRadiation = 0.01F;
      }
      
      float stackChance = stack.getCount() * randomChanceOfRadiation;
      float hazmatMultiplier = 1F - HazmatArmorItem.getRadProtection(living) / 4F;

      if (!living.hasEffect(ACEffectRegistry.IRRADIATED.get()) && level.random.nextFloat() < stackChance * hazmatMultiplier) {
        MobEffectInstance instance = new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 1800);
        living.addEffect(instance);
        AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(entity.getId(), entity.getId(), 0, instance.getDuration()));
      }
    }
  }
}
