package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RadioactiveItem extends Item {

    private final float randomChanceOfRadiation;

    public RadioactiveItem(Properties properties, float randomChanceOfRadiation) {
        super(properties);
        this.randomChanceOfRadiation = randomChanceOfRadiation;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        if (!level.isClientSide && entity instanceof LivingEntity living && !(living instanceof Player player && player.isCreative())) {
            float stackChance = stack.getCount() * randomChanceOfRadiation;
            float hazmatMultiplier = 1F - HazmatArmorItem.getWornAmount(living) / 4F;
            if (!living.hasEffect(ACEffectRegistry.IRRADIATED.get()) && level.random.nextFloat() < stackChance * hazmatMultiplier) {
                MobEffectInstance instance = new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 1800);
                living.addEffect(instance);
                AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(entity.getId(), entity.getId(), 0, instance.getDuration()));
            }
        }
    }
}
