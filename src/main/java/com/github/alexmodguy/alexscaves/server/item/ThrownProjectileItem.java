package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class ThrownProjectileItem extends Item {

    private final float throwAngle;
    private final float throwSpeed;
    private final float throwRandomness;

    private final Function<Player, ThrowableItemProjectile> projectileSupplier;

    public ThrownProjectileItem(Item.Properties properties, Function<Player, ThrowableItemProjectile> projectileSupplier, float throwAngle, float throwSpeed, float throwRandomness) {
        super(properties);
        this.throwAngle = throwAngle;
        this.throwSpeed = throwSpeed;
        this.throwRandomness = throwRandomness;
        this.projectileSupplier = projectileSupplier;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, (level.getRandom().nextFloat() * 0.7F + 0.25F) * 0.5F);
        if (!level.isClientSide) {
            ThrowableItemProjectile brick = projectileSupplier.apply(player);
            brick.setItem(itemstack);
            brick.shootFromRotation(player, player.getXRot(), player.getYRot(), throwAngle, throwSpeed, throwRandomness);
            level.addFreshEntity(brick);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}