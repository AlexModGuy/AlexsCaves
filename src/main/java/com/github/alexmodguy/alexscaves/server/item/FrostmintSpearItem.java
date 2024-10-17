package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.FrostmintSpearEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.FrostmintFreezableAccessor;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FrostmintSpearItem extends SpearItem {

    public FrostmintSpearItem(Properties properties) {
        super(properties, 5.0D);
    }

    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i1) {
        if (livingEntity instanceof Player player) {
            int i = this.getUseDuration(itemStack) - i1;
            float f = getPowerForTime(i);
            if (f > 0.1D) {
                FrostmintSpearEntity spearEntity = new FrostmintSpearEntity(level, player, itemStack);
                spearEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 2.5F, 1.0F);
                if (player.getAbilities().instabuild) {
                    spearEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                level.addFreshEntity(spearEntity);
                level.playSound(null, spearEntity, ACSoundRegistry.FROSTMINT_SPEAR_THROW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity hurtEntity, LivingEntity player) {
        hurtEntity.setTicksFrozen(hurtEntity.getTicksRequiredToFreeze() + 200);
        ((FrostmintFreezableAccessor)hurtEntity).setFrostmintFreezing(true);
        return super.hurtEnemy(stack, hurtEntity, player);
    }
}
